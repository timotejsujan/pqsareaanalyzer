package model;

import javafx.scene.control.TextArea;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Timotej Sujan
 */
public class pqsfinder extends base {

    //private String outputName = "";
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

    public Process p;

    public pqsfinder(TextArea outputArea) {
        super(outputArea);
    }

    public void start() throws Exception {
        changeParameters();
        p = Runtime.getRuntime().exec(new String[]{"Rscript", program_path, input_path,
                        output_path + "/" +
                        output_name + ".txt"});

        String line;
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while (!Thread.interrupted() && (line = error.readLine()) != null) {
            System.out.println(line);
            //printStatus(line);
        }

        while (!Thread.interrupted() && (line = input.readLine()) != null) {
            System.out.println(line);
            //printStatus(line);
        }
        error.close();
        input.close();

        if (Thread.interrupted()) return;
        print_status("the process has ended");
    }


    private void changeParameters() throws Exception {
        String extraParams = "";
        if (strand != null && !strand.isEmpty()) extraParams += ", strand=\"" + strand + "\"";
        if (overlapping != null && !overlapping.isEmpty()) extraParams += ", overlapping=" + overlapping;
        if (!max_len.isEmpty()) extraParams += ", max_len=" + max_len;
        if (!min_score.isEmpty()) extraParams += ", min_score=" + min_score;
        if (!run_min_len.isEmpty()) extraParams += ", run_min_len=" + run_min_len;
        if (!run_max_len.isEmpty()) extraParams += ", run_max_len=" + run_max_len;
        if (!loop_min_len.isEmpty()) extraParams += ", loop_min_len=" + loop_min_len;
        if (!loop_max_len.isEmpty()) extraParams += ", loop_max_len=" + loop_max_len;
        if (!max_bulges.isEmpty()) extraParams += ", max_bulges=" + max_bulges;
        if (!max_mismatches.isEmpty()) extraParams += ", max_mismatches=" + max_mismatches;
        if (!max_defects.isEmpty()) extraParams += ", max_defects=" + max_defects;

        File f = new File(program_path);
        int line_to_replace = findLineInFIle(f, line_id_inRscript);
        replaceStringInFile(f, line_to_replace, line_start + extraParams + ")");

    }

    private void replaceStringInFile(File inFile, int lineno, String lineToBeInserted)
            throws Exception {
        // temp file
        File outFile = new File("$$$$$$$$.tmp");

        // input
        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader
                (new InputStreamReader(fis));

        // output
        FileOutputStream fos = new FileOutputStream(outFile);
        PrintWriter out = new PrintWriter(fos);

        String line;
        int counter = 1;
        while ((line = in.readLine()) != null) {
            if (counter == lineno) {
                out.println(lineToBeInserted);
            } else {
                out.println(line);
            }
            counter++;
        }
        out.flush();
        out.close();
        in.close();
//todo: osetrit
        inFile.delete();
        outFile.renameTo(inFile);
    }

    private int findLineInFIle(File f, String id) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        int counter = 0;
        while ((line = br.readLine()) != null) {
            counter++;
            if (line.contains(id)) {
                return counter + 1;
            }
        }
        return 0;
    }
    // SETTERS

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
