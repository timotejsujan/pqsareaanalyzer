package external;

import javafx.scene.control.TextArea;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author Timotej Sujan
 */
public class cd_hit_est extends base {

    public cd_hit_est(TextArea outputArea) {
        super(outputArea);
    }

    private String inputPathPqs = "";
    private String parameters = "";
    private final String nameConst = "_cdhit";
    public Process p;

    public boolean is_valid(){
        return !inputPathPqs.isEmpty();
    }

    public void start() throws IOException {

        String[] paramsInit = {program_path, "-i", input_path, "-o", output_path + "/" + output_name + nameConst};
        String[] params = parameters.split(" ");
        int fal = paramsInit.length;        //determines length of firstArray
        int sal = params.length;   //determines length of secondArray
        String[] paramsAll = new String[fal + sal];  //resultant array of size first array and second array
        System.arraycopy(paramsInit, 0, paramsAll, 0, fal);
        System.arraycopy(params, 0, paramsAll, fal, sal);
        if (!parameters.equals("")) {
            p = new ProcessBuilder(paramsAll).start();
        } else {
            p = new ProcessBuilder(program_path, "-i", input_path, "-o", output_path + "/" + output_name + nameConst).start();
        }
        print_status(parameters);
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            if (Thread.interrupted()) return;
            print_status(line);
        }

        cluster_sort cs = new cluster_sort();
        cs.inputPath = output_path +"/"+ output_name +nameConst+".clstr";
        cs.outputName = output_name +nameConst+ "_sort.clstr";
        cs.outputPath = output_path;
        cs.sort();

        File cdhitOutputClstr = new File(output_path +"/"+ output_name +nameConst+".clstr");
        File cdhitOutput = new File(output_path +"/"+ output_name +nameConst);
        deleteFile(cdhitOutputClstr);
        deleteFile(cdhitOutput);

        cluster_transform ct = new cluster_transform();
        ct.clstr_file = new File(cs.outputPath +"/"+cs.outputName);
        ct.fasta_file = new File(input_path);
        ct.pqs_file = new File(inputPathPqs);
        ct.outputPath = output_path;
        ct.outputName = output_name +".clstr";
        ct.getClstrFileWithSequences();

        File cdhitOutputClstrSort = new File(cs.outputPath +"/"+cs.outputName);
        deleteFile(cdhitOutputClstrSort);

        if (Thread.interrupted()) return;
        print_status("the process has ended");
    }

    private void deleteFile(File f){
        try {
            f.delete();
        }catch (Exception e){
            f.deleteOnExit();
        }
    }

    public void setInputPathPqs(String s) {
        this.inputPathPqs = s;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}

