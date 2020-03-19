package external;

import internal.ClusterSort;
import internal.ClusterTransform;
import internal.CustomOutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;


import java.io.*;

/**
 * @author Timotej Sujan
 */
public class CDhit {

    public CDhit(TextArea outputArea) {
        this.outputArea = outputArea;
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
        //System.setOut(printStream);
        //System.setErr(printStream);
    }

    private TextArea outputArea;
    private PrintStream printStream;
    private String cdhitPath = "cd-hit-v4.8.1-2019-0228/cd-hit-est";
    private String inputPath;
    private String inputPathPqs;
    private String outputPath;
    private String outputName = "test";
    private final String nameConst = "_cdhit";

    public void printStatus(String status) {
        Platform.runLater(() -> printStream.println(status));
    }

    public void start() throws IOException {
        Process process = new ProcessBuilder(cdhitPath, "-i", inputPath, "-o", outputPath+"/"+outputName+nameConst).start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            printStatus(line);
        }

        if (Thread.interrupted()) {
            printStatus("Proces byl zastaven");
        }

        ClusterSort cs = new ClusterSort();
        cs.inputPath = outputPath+"/"+outputName+nameConst+".clstr";
        cs.outputName = outputName +nameConst+ "_sort.clstr";
        cs.outputPath = outputPath;
        cs.clusterSort();

        ClusterTransform ct = new ClusterTransform();
        ct.clstr_file = new File(cs.outputPath +"/"+cs.outputName);
        ct.fasta_file = new File(inputPath);
        ct.pqs_file = new File(inputPathPqs);
        ct.outputPath = outputPath;
        ct.outputName = outputName+".clstr";
        ct.getClstrFileWithSequences();
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setInputPathPqs(String inputPathPqs) {
        this.inputPathPqs = inputPathPqs;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
}

