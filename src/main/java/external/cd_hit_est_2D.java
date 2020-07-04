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

    private String inputPathPqs = "";
    private String inputPath2 = "";
    private String inputPath2Pqs = "";
    private String parameters = "";
    private final String nameConst = "_cdhit2";
    public Process p;

    public boolean is_valid(){
        return !inputPathPqs.isEmpty() && !inputPath2.isEmpty();
    }

    public void start() throws IOException {
        String[] paramsInit = {program_path, "-i", input_path, "-i2", inputPath2, "-o", output_path + "/" + output_name + nameConst};
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

        print_status("the process has ended");
    }

    public void setInputPathPqs(String inputPathPqs) {
        this.inputPathPqs = inputPathPqs;
    }

    public void setInputPath2Pqs(String inputPath2Pqs) {
        this.inputPath2Pqs = inputPath2Pqs;
    }

    public void setInputPath2(String inputPath2) {
        this.inputPath2 = inputPath2;
    }

    public void setParams(String params) {
        parameters = params;
    }
}
