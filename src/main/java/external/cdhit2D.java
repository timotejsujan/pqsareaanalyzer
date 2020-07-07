package external;

import javafx.scene.control.TextArea;

import java.io.*;

/**
 * @author Timotej Sujan
 */
public class cdhit2D extends base {

    public cdhit2D(TextArea outputArea) {
        super(outputArea);
    }

    public String input_path_pqs_1 = "";
    public String input_path_areas_2 = "";
    public String input_path_pqs_2 = "";
    private String parameters = "";
    public final String name_suffix = "_cdhit2";
    public Process p;
    public boolean keep_one_sized = false;

    public boolean is_valid() {
        return is_base_valid() && !input_path_pqs_2.isEmpty() && !input_path_pqs_1.isEmpty() && !input_path_areas_2.isEmpty();
    }

    public void start() throws Exception {
        String[] paramsInit = {program_path, "-i", input_path, "-i2", input_path_areas_2,
                "-o", output_path + "/" + output_name + name_suffix};
        String[] params = parameters.split(" ");
        int fal = paramsInit.length;        //determines length of firstArray
        int sal = params.length;   //determines length of secondArray
        String[] paramsAll = new String[fal + sal];  //resultant array of size first array and second array
        System.arraycopy(paramsInit, 0, paramsAll, 0, fal);
        System.arraycopy(params, 0, paramsAll, fal, sal);
        p = new ProcessBuilder(paramsAll).start();

        print_status(parameters);
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            if (Thread.interrupted()) return;
            print_status(line);
        }

        cluster_sort cs = new cluster_sort(this);
        cs.sort();

        File cdhitOutputClstr = new File(output_path + "/" + output_name + name_suffix + ".clstr");
        File cdhitOutput = new File(output_path + "/" + output_name + name_suffix);
        delete_file(cdhitOutputClstr);
        delete_file(cdhitOutput);
        cluster_transform ct = new cluster_transform(this, cs);
        ct.start();

        File cdhitOutputClstrSort = new File(cs.output_path + "/" + cs.output_name);
        delete_file(cdhitOutputClstrSort);

        print_status("the process has ended");
    }

    private void delete_file(File f) {
        try {
            f.delete();
        } catch (Exception e) {
            f.deleteOnExit();
        }
    }

    public void set_input_path_pqs_1(String inputPathPqs) {
        this.input_path_pqs_1 = inputPathPqs;
    }

    public void set_input_path_pqs_2(String inputPath2Pqs) {
        this.input_path_pqs_2 = inputPath2Pqs;
    }

    public void set_input_path_areas_2(String input_path_areas_2) {
        this.input_path_areas_2 = input_path_areas_2;
    }

    public void set_params(String params) {
        parameters = "-d 0 ";
        parameters += params;
    }

    public void set_keep_one_sized(boolean b){
        keep_one_sized = b;
    }
}
