package external;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.PrintStream;

/**
 * @author Timotej Sujan
 */
public class base {
    public PrintStream print_stream;
    protected String input_path = "";
    protected String output_path = "";
    protected String output_name = "";
    protected String program_path = "";

    public base(TextArea oa){
        print_stream = new PrintStream(new custom_output_stream(oa));
    }

    public void print_status(String status) {
        Platform.runLater(() -> print_stream.println(status));
    }

    public void set_input_path(String s) {
        input_path = s;
    }

    public void set_output_path(String s) {
        output_path = s;
    }

    public void set_output_name(String s) {
        output_name = s;
    }

    public void set_program_path(String s) {
        program_path = s;
    }

    public Boolean is_base_valid(){
        return print_stream != null
                && !input_path.isEmpty()
                && !output_path.isEmpty()
                && !output_name.isEmpty()
                && !program_path.isEmpty();
    }
}
