package model;

import controller.Main;
import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author Timotej Sujan
 */
public class blast_local extends base{

    private String parameters = "";
    private String database_path = "";

    public Process p;

    public blast_local(TextArea outputArea) {
        super(outputArea);
    }

    public void start() throws Exception {

        String[] paramsInit = {program_path, "-query", input_path, "-db", database_path, "-out",
                Paths.get(output_path, output_name+".txt").toString()};
        if (!parameters.isEmpty()){

            String[] params = parameters.split(" ");
            int fal = paramsInit.length;        //determines length of firstArray
            int sal = params.length;   //determines length of secondArray
            String[] paramsAll = new String[fal + sal];  //resultant array of size first array and second array
            System.arraycopy(paramsInit, 0, paramsAll, 0, fal);
            System.arraycopy(params, 0, paramsAll, fal, sal);
            p = new ProcessBuilder(paramsAll).start();
        } else {
            p = new ProcessBuilder(paramsInit).start();
        }

        print_status(parameters);

        SequenceInputStream s = new SequenceInputStream(p.getErrorStream(), p.getInputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(s));
        String line;

        while (!Thread.currentThread().isInterrupted() && ((line = input.readLine()) != null)){
            print_status(line);
        }

        input.close();

        delete_file(new File(input_path));

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

    public void set_output_dir(String s) throws IOException {
        output_path = s;
        Main.config.output_dir = s;
        Main.save_config();
    }

    public void set_output_name(String s) {
        output_name = s;
    }

    public void set_program_path(String s) {
        program_path = s;
    }

    public void set_database_path(String s) {
        database_path = s.substring(0, s.lastIndexOf("."));
    }

    public void set_parameters(String s) {
        parameters = s;
    }

}
