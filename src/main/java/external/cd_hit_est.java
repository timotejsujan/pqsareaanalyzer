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

        cluster_sort cs = new cluster_sort(output_path +"/"+ output_name +nameConst+".clstr",
                output_path,output_name +nameConst+ "_sort.clstr");
        cs.sort();

        File cdhitOutputClstr = new File(output_path +"/"+ output_name +nameConst+".clstr");
        File cdhitOutput = new File(output_path +"/"+ output_name +nameConst);
        deleteFile(cdhitOutputClstr);
        deleteFile(cdhitOutput);

        cluster_transform ct = new cluster_transform(new File(cs.output_path +"/"+cs.output_name),
                new File(input_path), new File(inputPathPqs), output_path, output_name +".txt");
        ct.start();

        File cdhitOutputClstrSort = new File(cs.output_path +"/"+cs.output_name);
        deleteFile(cdhitOutputClstrSort);

        print_status("the process has ended");
    }

    private void deleteFile(File f){
        try {
            f.delete();
        }catch (Exception e){
            f.deleteOnExit();
        }
    }

    public void set_input_path_pqs(String s) {
        inputPathPqs = s;
    }

    public void set_params(String params) { parameters = params; }
}

