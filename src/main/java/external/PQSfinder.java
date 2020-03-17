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
    private PrintStream printStream;

    private String RscriptPath = "/Users/timotej.sujan/Desktop/g4.R";
    private String inputPath;
    private String outputPath;
    private String outputName = "test.txt";

    public PQSfinder(TextArea outputArea) {
        this.outputArea = outputArea;
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
        //System.setOut(printStream);
        //System.setErr(printStream);
    }

    public void setOutputPath(String outputPath) {
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

    public void start() throws IOException {
        Process p = Runtime.getRuntime().exec(new String[] { "Rscript", RscriptPath, inputPath, outputPath+"/"+outputName});

        String line = "";
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while((line = error.readLine()) != null && !Thread.interrupted()){
            printStatus(line);
        }
        error.close();

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while((line=input.readLine()) != null && !Thread.interrupted()){
            printStatus(line);
        }

        input.close();

        if (Thread.interrupted()) {
            printStatus("Proces byl zastaven");
        }

    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
}
