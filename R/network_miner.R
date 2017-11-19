#!/usr/bin/env Rscript
args <- commandArgs(trailingOnly=TRUE)
source("dbQuery.R")
source("r_helpers.R")

# load the feature descriptions
features <- fread("~/Desktop/ctf/R/features.txt", sep=";") %>% as.data.frame()

# query the database for given file and user
data <- getData(args[1])
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
  link_importance <- c()
  
  # calculate various node metrics and combine them
  btwn <- as.numeric(unname(betweenness(subgraph, directed=FALSE)))
  nodes <- cbind.data.frame(node=nodes, betweenness=btwn, stringsAsFactors=FALSE)
  degree <- degree(subgraph)
  nodes <- cbind.data.frame(nodes, degree, stringsAsFactors=FALSE)
  closeness <- closeness(subgraph)
  nodes <- cbind.data.frame(nodes, closeness, stringsAsFactors=FALSE)
  node_weight <- ifelse(btwn == 0, 0, sum(btwn) / (sum(btwn) - btwn))
  nodes <- cbind.data.frame(nodes, node_weight, stringsAsFactors=FALSE)
  
  # calculate various graph metrics and combine them
  density <- edge_density(subgraph)
  n_edges <- length(E(subgraph))
  n_nodes <- nrow(nodes)
  avg_path_length <- average.path.length(subgraph, directed=FALSE, unconnected=TRUE)
  
  # calculate the importance of a link
  performances <- c()
  for(i in nodes[,1]){
    edges_matrix <- get.edgelist(subgraph)
    if(ncol(as.matrix(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),])) == 1){
      g <- graph_from_edgelist(t(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),]), directed = FALSE)
    } else {
      g <- graph_from_edgelist(as.matrix(edges_matrix[-c(which(edges_matrix[,1] == i), which(edges_matrix[,2] == i)),]), directed = FALSE)
    }
    degree <- degree(g)
    secrecy <- sum(degree**2)
    shortest_paths <- shortest.paths(g, v=V(g), algorithm = "dijkstra")
    efficiency <- sum(upper.tri(shortest_paths))
    performance <- secrecy * efficiency
    performances <- c(performances, performance)
  }
  
  secrecy <- sum(degree**2)
  efficiency <- sum(upper.tri(shortest_paths))
  performance <- secrecy * efficiency
  link_importance <- c(link_importance, ((performance - performances) * node_weight))
  nodes <- cbind.data.frame(nodes, link_importance)
  
  shortest_paths <- shortest.paths(subgraph, v=V(subgraph), algorithm = "dijkstra")
  nodes <- cbind.data.frame(nodes,
                            graph_density=rep(density, nrow(nodes)),
                            n_edges=rep(n_edges, nrow(nodes)),
                            n_nodes=rep(n_nodes, nrow(nodes)),
                            graph_secrecy=rep(secrecy, nrow(nodes)),
                            graph_avg_path_length=rep(avg_path_length, nrow(nodes)),
                            avg_shortest_path_length=rep(mean(shortest_paths), nrow(nodes)),
                            graph_efficiency=rep(efficiency, nrow(nodes)),
                            graph_performance=rep(performance, nrow(nodes)), 
                            stringsAsFactors=FALSE)
  
  final_nodes <- rbind(final_nodes, nodes)
  j <- j+1
}

# add labels
names(final_nodes)[1] <- "id_user"
final_nodes <- unique(merge(final_nodes, data[,c(which(colnames(data) %in% c("id_user","label")))]))
final_nodes$label <- as.factor(final_nodes$label)

# train a grid of models for 5min and predict using the best one
h2o.init()
predictors <- colnames(final_nodes[,-which(colnames(final_nodes) %in% c("label","id_user"))])
auto_models <- h2o.automl(training_frame=as.h2o(final_nodes), 
                          x=predictors, 
                          max_runtime_secs = 30,
                          y="label")

# use the best model for predicting
preds <- h2o.predict(auto_models@leader, newdata=as.h2o(final_nodes))

train_surrogate.hex <- h2o.cbind(as.h2o(final_nodes), preds$predict)
train_surrogate <- as.data.frame(train_surrogate.hex)
score <- cbind.data.frame(final_nodes, p=as.data.frame(preds$p1), predict = as.data.frame(preds$predict))
score <- score[score$predict == 1 & score$label == 0,]
score <- head(score[order(score$p1, decreasing = TRUE),], 5)

# specify surrogate params
hyper_params_glm <- list(alpha = c(0.0, 0.2, 0.4, 0.6, 0.8, 1.0))

# define search criterion
search_criteria <- list(strategy = "RandomDiscrete",
                        max_models = 10,
                        stopping_metric = "AUTO",
                        stopping_tolerance = 0.001,
                        stopping_rounds = 5,
                        seed = 1)
nfolds <- 5

surrogate_grid <- h2o.grid(algorithm="glm",
                           grid_id="glm_surrogate_grid",
                           x=predictors,
                           y="predict",
                           family="binomial",
                           training_frame = train_surrogate.hex,
                           nfolds=nfolds,
                           hyper_params = hyper_params_glm,
                           search_criteria = search_criteria,
                           seed = 1)

grid <- h2o.getGrid("glm_surrogate_grid",sort_by="AUC",decreasing=TRUE)
surrogate_model <- h2o.getModel(grid@model_ids[[1]])

# use surrogate model for feature explanation
weights <- surrogate_model@model$coefficients[-1]
intercept <- surrogate_model@model$coefficients[1]
weights <- weights[names(weights) %in% predictors]
weight_times_value <- t(t(score[,which(colnames(score) %in% names(weights))]) * weights)

# find most important features
nfeatures <- 4 # number of features to describe
descriptions <- c()
explained_feature_importance <- c()

for(row in seq(1,nrow(weight_times_value))){
  
  e_power <- weight_times_value[row, ] + intercept
  partial_prob <- exp(e_power) / (1+exp(e_power))
  
  # sort the odds
  important_features <- head(sort(partial_prob, decreasing = TRUE), nfeatures)
  important_feature_values <- score[row, which(names(score) %in% names(important_features))]
  
  # make sure the order of these vectors are the same
  important_features <- important_features[order(names(important_features))]
  important_feature_values <- important_feature_values[order(names(important_feature_values))]
  
  description <- c()
  for(i in seq(1, nfeatures)){
      description <- c(description, 
                       gsub("VALUE", ifelse(names(important_feature_values[i]) == "reconstruction_error", 
                                            round(important_feature_values[i],5), 
                                            round(important_feature_values[i],2)), 
                       as.character(features[features$feature == names(important_features)[i], ]$description)))
  }
  
  descriptions <- c(descriptions, sprintf("This network has %s.",
                                  paste(description,collapse=", ")))
}

# save the results to the DB
saveResults(score$id_user, descriptions)
