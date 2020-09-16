library(BiocManager)
library(pqsfinder)

options(echo = TRUE)
args <- commandArgs(trailingOnly = TRUE)
print(args)
f <- args[1]
file.create(args[2])
count <- 0
nrec <- 1000000
skip <- 0
while (TRUE) {
  fai <- fasta.index(f, nrec=nrec, skip=skip, seqtype="DNA")
  skip <- skip + nrec
  print(skip)
  if (length(fai) == 0L)
    break
  for (i in seq_len(nrow(fai))) {
    count <- count + 1
    seq <- readDNAStringSet(fai[i, ])
    # following lines cannot be changed
    # id = 3A892
    pqs <- pqsfinder(seq[[1]], strand="*", overlapping=FALSE, min_score=40)
    # --------------------------------
    if (length(pqs@elementMetadata@listData[["score"]]) > 0) {
      cat(paste0(";segment number: ", count), file = args[2], sep = "\n", append = TRUE)
      dss <- as(pqs, "DNAStringSet")
      writeXStringSet(dss, file = args[2], format = "fasta", append = TRUE)
    }
  }
}
