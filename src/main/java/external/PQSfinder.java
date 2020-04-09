package external;

import internal.CustomOutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.*;

/**
 * @author Timotej Sujan
 */
public class PQSfinder {

    private TextArea outputArea;
    public PrintStream printStream;
    public Process p;

    private String RscriptPath = "scripts/g4.R";
    private String inputPath;
    private String outputPath;
    private String outputName = "test.txt";
    private final String line_id_inRscript = "3A892";
    private final String line_start = "pqs <- pqsfinder(seq[[i]]";

    // PARAMETERS
    private String strand = ""; // Strand specification (+, - or *).
    private String overlapping = ""; // If true, than overlapping PQS will be reported.
    private String max_len = ""; // Maximal total length of PQS.
    // Minimal score of PQS to be reported. The default value 52 shows
    // the best balanced accuracy on human G4 sequencing data (Chambers et al. 2015).
    private String min_score = "";
    private String run_min_len = ""; // Minimal length of each PQS run (G-run).
    private String run_max_len = ""; // Maximal length of each PQS run.
    private String loop_min_len = ""; // Minimal length of each PQS inner loop.
    private String loop_max_len = ""; // Maximal length of each PQS inner loop.
    private String max_bulges = ""; // Maximal number of runs containing a bulge.
    private String max_mismatches = ""; // Maximal number of runs containing a mismatch.
    private String max_defects = ""; // Maximum number of defects in total (#bulges + #mismatches).

    public PQSfinder(TextArea outputArea) {
        this.outputArea = outputArea;
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
        //System.setOut(printStream);
        //System.setErr(printStream);
    }

    public void setOutputDir(String outputPath) {
        this.outputPath = outputPath;
    }

    public void printStatus(String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                printStream.println(status);
            }
        });
    }

    public void start() throws Exception {
        changeParameters();
        p = Runtime.getRuntime().exec(new String[] { "Rscript", RscriptPath, inputPath, outputPath+"/"+outputName+".txt"});


        String line = "";
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while((line = error.readLine()) != null){
            if (Thread.interrupted()) return;
            System.out.println(line);
            //printStatus(line);
        }

        while((line=input.readLine()) != null){
            if (Thread.interrupted()) return;
            System.out.println(line);
            //printStatus(line);
        }
        error.close();
        input.close();

        //while(p.isAlive()){
            //java.util.Date date = new java.util.Date();
            //printStatus(date.toString() + " the process is running");
            //Thread.sleep(5000);
        //}
        if (Thread.interrupted()) return;
        printStatus("the process has ended");

    }



    public void changeParameters() throws Exception {
        File f = new File(RscriptPath);
        int line_to_replace = findLineInFIle(f, line_id_inRscript);
        String extraParams = "";
        if (strand != null && !strand.isEmpty()) extraParams += ", strand=\""+strand+"\"";
        if (overlapping != null && !overlapping.isEmpty()) extraParams += ", overlapping="+overlapping;
        if (!max_len.isEmpty()) extraParams += ", max_len="+max_len;
        if (!min_score.isEmpty()) extraParams += ", min_score="+min_score;
        if (!run_min_len.isEmpty()) extraParams += ", run_min_len="+run_min_len;
        if (!run_max_len.isEmpty()) extraParams += ", run_max_len="+run_max_len;
        if (!loop_min_len.isEmpty()) extraParams += ", loop_min_len="+loop_min_len;
        if (!loop_max_len.isEmpty()) extraParams += ", loop_max_len="+loop_max_len;
        if (!max_bulges.isEmpty()) extraParams += ", max_bulges="+max_bulges;
        if (!max_mismatches.isEmpty()) extraParams += ", max_mismatches="+max_mismatches;
        if (!max_defects.isEmpty()) extraParams += ", max_defects="+max_defects;

        replaceStringInFile(f, line_to_replace, line_start+extraParams+ ")");

    }
    public void replaceStringInFile (File inFile, int lineno, String lineToBeInserted)
            throws Exception {
        // temp file
        File outFile = new File("$$$$$$$$.tmp");

        // input
        FileInputStream fis  = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader
                (new InputStreamReader(fis));

        // output
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter out = new PrintWriter(fos);

        String thisLine = "";
        int i =1;
        while ((thisLine = in.readLine()) != null) {
            if(i == lineno) {
                out.println(lineToBeInserted);
            } else {
                out.println(thisLine);
            }
            i++;
        }
        out.flush();
        out.close();
        in.close();
//todo: osetrit
        inFile.delete();
        outFile.renameTo(inFile);
    }

    private int findLineInFIle(File file, String to_find) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int counter = 0;
        while ((st = br.readLine()) != null) {
            counter++;
            if (st.contains(to_find)) {
                while (!(st = br.readLine()).startsWith(">")){
                    return counter + 1;
                }
                break;
            }
        }
        return 0;
    }
    // SETTERS
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public void setOverlapping(String overlapping) {
        this.overlapping = overlapping;
    }

    public void setMax_len(String max_len) {
        this.max_len = max_len;
    }

    public void setMin_score(String min_score) {
        this.min_score = min_score;
    }

    public void setRun_min_len(String run_min_len) {
        this.run_min_len = run_min_len;
    }

    public void setRun_max_len(String run_max_len) {
        this.run_max_len = run_max_len;
    }

    public void setLoop_min_len(String loop_min_len) {
        this.loop_min_len = loop_min_len;
    }

    public void setLoop_max_len(String loop_max_len) {
        this.loop_max_len = loop_max_len;
    }

    public void setMax_bulges(String max_bulges) {
        this.max_bulges = max_bulges;
    }

    public void setMax_mismatches(String max_mismatches) {
        this.max_mismatches = max_mismatches;
    }

    public void setMax_defects(String max_defects) {
        this.max_defects = max_defects;
    }
}
