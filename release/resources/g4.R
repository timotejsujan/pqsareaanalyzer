library(BiocManager)
library(pqsfinder)

options(echo=TRUE) # if you want see commands in output file
args <- commandArgs(trailingOnly = TRUE)
print(args)
seq <- readDNAStringSet(args[1], "fasta")
file.create(args[2]);
for (i in 1:length(seq)) {
  # following lines cannot be changed
  # id = 3A892
pqs <- pqsfinder(seq[[i]], strand="*", overlapping=FALSE, min_score=52)
  # --------------------------------
  if (length(pqs@elementMetadata@listData[["score"]]) > 0) {
    cat(paste0(";segment number: ",i),file=args[2],sep="\n",append=TRUE)
    dss <- as(pqs, "DNAStringSet")
    writeXStringSet(dss, file = args[2], format = "fasta", append=TRUE)
  }
}
