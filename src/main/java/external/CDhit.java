package external;

import internal.ClusterSort;
import internal.ClusterTransform;
import internal.CustomOutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


/**
 * @author Timotej Sujan
 */
public class CDhit {

    public CDhit(TextArea outputArea) {
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
    }

    public PrintStream printStream;
    private String cdhitPath = "cd-hit-v4.8.1-2019-0228/cd-hit-est";
    private String inputPath = "";
    private String inputPathPqs = "";
    private String outputPath = "";
    private String outputName = "";
    private String parameters = "";
    private final String nameConst = "_cdhit";
    public Process p;

    public void printStatus(String status) {
        Platform.runLater(() -> printStream.println(status));
    }

    public void start() throws IOException {
        String[] paramsInit = {cdhitPath, "-i", inputPath, "-o", outputPath + "/" + outputName + nameConst};
        String[] params = parameters.split(" ");
        int fal = paramsInit.length;        //determines length of firstArray
        int sal = params.length;   //determines length of secondArray
        String[] paramsAll = new String[fal + sal];  //resultant array of size first array and second array
        System.arraycopy(paramsInit, 0, paramsAll, 0, fal);
        System.arraycopy(params, 0, paramsAll, fal, sal);
        if (!parameters.equals("")) {
            p = new ProcessBuilder(paramsAll).start();
        } else {
            p = new ProcessBuilder(cdhitPath, "-i", inputPath, "-o", outputPath + "/" + outputName + nameConst).start();
        }
        printStatus(parameters);
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            if (Thread.interrupted()) return;
            printStatus(line);
        }

        ClusterSort cs = new ClusterSort();
        cs.inputPath = outputPath+"/"+outputName+nameConst+".clstr";
        cs.outputName = outputName +nameConst+ "_sort.clstr";
        cs.outputPath = outputPath;
        cs.clusterSort();

        File cdhitOutputClstr = new File(outputPath+"/"+outputName+nameConst+".clstr");
        File cdhitOutput = new File(outputPath+"/"+outputName+nameConst);
        deleteFile(cdhitOutputClstr);
        deleteFile(cdhitOutput);

        ClusterTransform ct = new ClusterTransform();
        ct.clstr_file = new File(cs.outputPath +"/"+cs.outputName);
        ct.fasta_file = new File(inputPath);
        ct.pqs_file = new File(inputPathPqs);
        ct.outputPath = outputPath;
        ct.outputName = outputName+".clstr";
        ct.getClstrFileWithSequences();

        File cdhitOutputClstrSort = new File(cs.outputPath +"/"+cs.outputName);
        deleteFile(cdhitOutputClstrSort);

        if (Thread.interrupted()) return;
        printStatus("the process has ended");
    }

    private void deleteFile(File f){
        try {
            f.delete();
        }catch (Exception e){
            f.deleteOnExit();
        }
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

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}

