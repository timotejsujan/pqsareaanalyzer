library(BiocManager)
library(pqsfinder)

split_path <- function(path) {
  setdiff(strsplit(path,"/|\\\\")[[1]], "")
}

options(echo = TRUE)
args <- commandArgs(trailingOnly = TRUE)
print(args)
file <- args[1]
files <- open_input_files(c(file, file))
file.create(args[2])
count <- 0
nrec <- 4000
skip <- 0
while (TRUE) {
  fai <- fasta.index(file, nrec=nrec, skip=skip, seqtype="DNA")
  skip <- skip + 4000
  if (length(fai) == 0L)
    break
  for (i in 1:nrow(fai)) {
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
