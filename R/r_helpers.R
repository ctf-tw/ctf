## 1 ##
installPackages <- function(pkg){
  new.pkg <- pkg[!(pkg %in% installed.packages()[, "Package"])]
  if (length(new.pkg))
  install.packages(new.pkg, dependencies = TRUE, repos = "https://cran.rstudio.com/")
  sapply(pkg, require, character.only = TRUE)
}

installPackages(paste(readLines("r_dependencies")))
