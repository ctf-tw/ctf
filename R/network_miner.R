#!/usr/bin/env Rscript
args <- commandArgs(trailingOnly=TRUE)

# query the database for given file and user
data <- ...
final_nodes <- c()

# convert it to a graph object
g <- graph_from_edgelist(as.matrix(unique(data[,c(1,2)])), directed=FALSE)

# decompose the graph object to subgraphs
subgraphs <- decompose.graph(g)

j <- 1
for(subgraph in subgraphs){
  print((100*j)/length(subgraphs))
  edgelist <- get.edgelist(subgraph)
  nodes <- unique(c(edgelist[,1], edgelist[,2]))
  
  # calculate various node metrics and combine them
  btwn <- betweenness(subgraph, directed=FALSE)
  nodes <- cbind.data.frame(node=nodes, betweenness=as.numeric(unname(btwn)))
  degree <- degree(subgraph)
  nodes <- cbind.data.frame(nodes, degree)
  closeness <- closeness(subgraph)
  nodes <- cbind.data.frame(nodes, closeness)
  node_weight <- sum(btwn) / (sum(btwn) - btwn)
  nodes <- cbind.data.frame(nodes, node_weight)
  
  # calculate various graph metrics and combine them
  density <- edge_density(subgraph)
  n_edges <- length(E(subgraph))
  n_nodes <- nrow(nodes)
  secrecy <- sum(degree**2)
  avg_path_length <- average.path.length(subgraph, directed=FALSE, unconnected=TRUE)
  shortest_paths <- shortest.paths(subgraph, v=V(subgraph), algorithm = "dijkstra")
  efficiency <- sum(upper.tri(shortest_paths))
  performance <- secrecy * efficiency
  
  # calculate the importance of a link
  performances <- c()
  for(i in nodes[,1]){
    edges_matrix <- get.edgelist(subgraph)
    if(ncol(as.matrix(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),])) == 1){
      g <- graph_from_edgelist(t(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),]))
    } else {
      g <- graph_from_edgelist(as.matrix(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),]))
    }
    secrecy <- sum(degree**2)
    shortest_paths <- shortest.paths(subgraph, v=V(subgraph), algorithm = "dijkstra")
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
  
  final_nodes <- rbind(final_nodes, nodes)
  j <- j+1
}

# add labels
final_nodes <- merge(final_nodes, label=data$label)
final_nodes$label <- as.factor(final_nodes$label)

# train a grid of models for 5min and predict using the best one
predictors <- colnames(final_nodes[,-which(colnames(final_nodes) %in% c("label","id_user"))])
auto_models <- h2o.automl(training_frame=as.h2o(final_nodes), 
                          x=predictors, 
                          max_runtime_secs = 300,
                          y="label")

# use the best model for predicting
preds <- h2o.predict(auto_models@leader, newdata=as.h2o(final_nodes)) %>% as.data.frame()

# combine the predictions
final_nodes <- cbind.data.frame(final_nodes, prediction=preds$predict, prob=preds$p1)

# return the TOP 10 cases that have not been marked as suspicious, yet have a high probability of being
head(as.character(final_nodes[order(final_nodes$prob, decreasing = TRUE), ]$id_user, 10))
