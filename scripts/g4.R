library(BiocManager)
library(pqsfinder)
options(echo=TRUE) # if you want see commands in output file
args <- commandArgs(trailingOnly = TRUE)
print(args)
# args[1] <- "/Users/timotej.sujan/Desktop/g4seq/Ahalleri_264_v1.txt"
# args[2] <- "/Users/timotej.sujan/Desktop/g4seq/speedtest.txt"
seq = readDNAStringSet(args[1], "fasta")
# file.create(args[2])
# str <- c("")
for (i in 1:length(seq)) {
  # str <- paste0(str,"\n", ">",names(seq)[i])
  cat(paste0(">",names(seq)[i]),file=args[2],sep="\n",append=TRUE)
  # following lines cannot be changed
  # id = 3A892
pqs <- pqsfinder(seq[[i]], strand="*", overlapping=FALSE, min_score=20)
  # --------------------------------
  if (length(pqs@elementMetadata@listData[["score"]]) > 0) {
    # str <- paste0(str, "\n", start(pqs), ",", width(pqs),",",strand(pqs),",", score(pqs),pqs)
    cat(paste0(start(pqs),",", width(pqs),",",strand(pqs),",", score(pqs), ",",pqs),file=args[2],sep="\n",append=TRUE)
  }
}
# cat(str,file=args[2],append=TRUE)
