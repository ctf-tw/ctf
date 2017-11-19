setUpConnection <- function(){
  drv <- dbDriver("PostgreSQL")
  con_pg <- dbConnect(drv, dbname = "ctf",
                      host = "localhost", port = 5432,
                      user = "ctf", password = "")
  
  return(con_pg)
}


getData <- function(tableName){
  query <- gsub("TABLENAME", tableName, "SELECT * FROM TABLENAME;")
  con <- setUpConnection()
  data <- dbGetQuery(con, query)
  dbDisconnect(con)
  return(data)
}


saveResults <- function(users, descriptions){
  con <- setUpConnection()
  dbGetQuery(con, "CREATE TABLE IF NOT EXISTS suspected_reason (
             user_id INT,
             description TEXT);")
  
  for(i in seq(1,length(users))){
    query <- "INSERT INTO suspected_reason
              SELECT
              ID_USER,
              \'DESCRIPTION\';"
    query <- gsub("ID_USER", users[i], query)
    query <- gsub("DESCRIPTION", descriptions[i], query)
    dbGetQuery(con, query)
  }
  dbDisconnect(con)
}