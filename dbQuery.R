
setUpConnection <- function(){
  drv <- dbDriver("PostgreSQL")
  con_pg <- dbConnect(drv, dbname = "ctf",
                      host = "localhost", port = 5432,
                      user = "ctf", password = "")
  
  return(con_pg)
}


getData <- function(tableName){
  query <- gsub("TABLENAME", tableName, "SELECT * FROM TABLENAME")
  con <- setUpConnection()
  data <- dbGetQuery(con, query)
  return(data)
}