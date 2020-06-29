package external;

import javafx.scene.control.TextArea;

import java.io.*;

/**
 * @author Timotej Sujan
 */
public class cd_hit_est_2D extends base {

    public cd_hit_est_2D(TextArea outputArea) {
        super(outputArea);
    }

    private TextArea outputArea;
    private String inputPath2 = "";
    private String inputPathPqs = "";
    private String params = "";
    private final String nameConst = "_cdhit2";

    public void start() throws IOException {
        if (!is_base_valid()) return;

        Process process = new ProcessBuilder(program_path, "-i", input_path, "-i2", inputPath2, "-o", output_path +"/"
                + output_name +nameConst + " "+params).start();
        print_status(params);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            print_status(line);
        }

        if (Thread.interrupted()) {
            print_status("Proces byl zastaven");
        }

        cluster_sort cs = new cluster_sort();
        cs.inputPath = output_path +"/"+ output_name +nameConst+".clstr";
        cs.outputName = output_name +nameConst+ "_sort.clstr";
        cs.outputPath = output_path;
        cs.sort();

        cluster_transform ct = new cluster_transform();
        ct.clstr_file = new File(cs.outputPath +"/"+cs.outputName);
        ct.fasta_file = new File(input_path);
        ct.pqs_file = new File(inputPathPqs);
        ct.outputPath = output_path;
        ct.outputName = output_name +".clstr";
        ct.getClstrFileWithSequences();
    }

    public void setInputPathPqs(String inputPathPqs) {
        this.inputPathPqs = inputPathPqs;
    }

    public void setInputPath2(String inputPath2) {
        this.inputPath2 = inputPath2;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
