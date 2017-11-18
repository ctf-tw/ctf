if (FALSE) {
    someenv<-new.env()
    someenv[["1234"]]<-"Suspected because of X"
    print(toJSON(someenv, pretty=TRUE))
}

library(jsonlite)

json <-
'[
  {"Id" : "Mario", "Reason" : "Bad looking"},
  {"Id" : "Gloomy", "Reason" : "Just like that"},
  {"Id" : "Rosy", "Reason" : "Just like that"},
  {"Id" : "Peach", "Reason" : "Data science"}
]'

mydf <- fromJSON(json)
mydf$Ranking <- c(3, 1, 2, 4)
toJSON(mydf, pretty=TRUE)
