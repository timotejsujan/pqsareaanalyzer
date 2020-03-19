library(BiocManager)
library(pqsfinder)
options(echo=TRUE) # if you want see commands in output file
args <- commandArgs(trailingOnly = TRUE)
print(args)
# "/Users/timotej.sujan/Desktop/lab/DATA/first.txt"
seq = readDNAStringSet(args[1], "fasta")
# file.create(args[2])
for (i in 1:length(seq)) {
  cat(paste0(">",names(seq)[i]),file=args[2],sep="\n",append=TRUE)
  # id = 3A892 - following line cannot be changed
  pqs <- pqsfinder(seq[[i]])
  # --------------------------------
  if (length(pqs@elementMetadata@listData[["score"]]) > 0) {
    cat(paste0(start(pqs),",", width(pqs),",",strand(pqs),",", score(pqs), ",",pqs),file=args[2],sep="\n",append=TRUE)
  }
}

