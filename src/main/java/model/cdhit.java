package model;

import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Timotej Sujan
 */
public class cdhit extends base {

    public cdhit(TextArea outputArea) {
        super(outputArea);
    }

    public String input_path_pqs = "";
    private String parameters = "";
    public final String name_suffix = "_cdhit.txt";
    public Process p;
    public boolean keep_one_sized = false;

    public boolean is_valid() {
        return !input_path_pqs.isEmpty();
    }

    public void start() throws Exception {

        String[] paramsInit = {program_path, "-i", input_path, "-o",
                Paths.get(output_path, output_name + name_suffix).toString()};
        String[] params = parameters.split(" ");
        int fal = paramsInit.length;        //determines length of firstArray
        int sal = params.length;   //determines length of secondArray
        String[] paramsAll = new String[fal + sal];  //resultant array of size first array and second array
        System.arraycopy(paramsInit, 0, paramsAll, 0, fal);
        System.arraycopy(params, 0, paramsAll, fal, sal);
        p = new ProcessBuilder(paramsAll).start();

        print_status(parameters);

        SequenceInputStream s = new SequenceInputStream(p.getErrorStream(), p.getInputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(s));
        String line;

        while (!Thread.currentThread().isInterrupted() && ((line = input.readLine()) != null)){
            print_status(line);
        }

        input.close();

        if (Thread.currentThread().isInterrupted()){
            if (p.isAlive())
                p.destroy();
            return;
        }

        cluster_sort cs = new cluster_sort(this);
        cs.sort();

        File cdhitOutputClstr = new File(Paths.get(output_path, output_name + name_suffix + ".clstr").toString());
        File cdhitOutput = new File(Paths.get(output_path, output_name + name_suffix).toString());
        delete_file(cdhitOutputClstr);
        delete_file(cdhitOutput);

        cluster_transform ct = new cluster_transform(this, cs);
        ct.start();

        File cdhitOutputClstrSort = new File(Paths.get(cs.output_path, cs.output_name).toString());
        delete_file(cdhitOutputClstrSort);

        if (Thread.currentThread().isInterrupted()){
            if (p.isAlive())
                p.destroy();
        } else {
            print_status("the process has ended");
        }
    }

    private void delete_file(File f) {
        try {
            f.delete();
        } catch (Exception e) {
            f.deleteOnExit();
        }
    }

    public void set_input_path_pqs(String s) {
        input_path_pqs = s;
    }

    public void set_params(String params) {
        parameters = "-d 0 ";
        parameters += params;
    }

    public void set_keep_one_sized(boolean b) {
        keep_one_sized = b;
    }
}

