#!/usr/bin/env Rscript
args <- commandArgs(trailingOnly=TRUE)
source("dbQuery.R")

# query the database for given file and user
data <- getData(args[1])

# convert it to a graph object
g <- graph_from_edgelist(as.matrix(data[,c(1,2)]))

nodes <- unique(c(data[,1], data[,2]))

# calculate various node metrics and combine them
btwn <- betweenness(g, directed=FALSE)
nodes <- cbind.data.frame(nodes, betweenness=as.numeric(unname(btwn)))
degree <- degree(g)
nodes <- cbind.data.frame(nodes, degree)
closeness <- closeness(g)
nodes <- cbind.data.frame(nodes, closeness)
node_weight <- sum(btwn) / (sum(btwn) - btwn)
nodes <- cbind.data.frame(nodes, node_weight)

# calculate various graph metrics and combine them
density <- edge_density(g)
n_edges <- length(E(g))
n_nodes <- nrow(nodes)
secrecy <- sum(degree**2)
avg_path_length <- average.path.length(g, directed=FALSE, unconnected=TRUE)
shortest_paths <- shortest.paths(g, v=V(g), algorithm = "dijkstra")
efficiency <- sum(upper.tri(shortest_paths))
performance <- secrecy * efficiency

# calculate the importance of a link
performances <- c()
for(i in nodes[,1]){
  edges_matrix <- as.matrix(data[,c(1,2)])
  g <- graph_from_edgelist(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),])
  secrecy <- sum(degree**2)
  shortest_paths <- shortest.paths(g, v=V(g), algorithm = "dijkstra")
  efficiency <- sum(upper.tri(shortest_paths))
  performance <- secrecy * efficiency
  performances <- c(performances, performance)
}

node_importance <- performance - performances
nodes <- cbind.data.frame(nodes, node_importance)
nodes <- cbind.data.frame(nodes, 
                          graph_density=rep(density, nrow(nodes)),
                          n_edges=rep(n_edges, nrow(nodes)),
                          n_nodes=rep(n_nodes, nrow(nodes)),
                          graph_secrecy=rep(secrecy, nrow(nodes)),
                          graph_avg_path_length=rep(avg_path_length, nrow(nodes)),
                          avg_shortest_path_length=rep(mean(shortest_paths), nrow(nodes)),
                          graph_efficiency=rep(efficiency, nrow(nodes)),
                          graph_performance=rep(performance, nrow(nodes)))

data[,c(1,2)]
