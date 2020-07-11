package model;

import controller.Main;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Timotej Sujan
 */
public class base {
    public PrintStream print_stream;
    public String input_path = "";
    public String output_path = "";
    public String output_name = "";
    public String program_path = "";

    public base() {
    }

    public base(TextArea oa) {
        print_stream = new PrintStream(new custom_output_stream(oa));
    }

    public void print_status(String status) {
        Platform.runLater(() -> print_stream.println(status));
    }

    public void set_input_path(String s) {
        input_path = s;
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

    public Boolean is_base_valid() {
        return print_stream != null
                && !input_path.isEmpty()
                && !output_path.isEmpty()
                && !output_name.isEmpty()
                && !program_path.isEmpty();
    }

    protected StringBuilder get_reverse_sequence(StringBuilder s) {
        s.reverse();
        String st = s.toString().toUpperCase();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < st.length(); i++) {
            switch (st.charAt(i)) {
                case 'A':
                    n.append('T');
                    break;
                case 'T':
                    n.append('A');
                    break;
                case 'C':
                    n.append('G');
                    break;
                case 'G':
                    n.append('C');
                    break;
                default:
                    n.append('N');
                    break;
            }
        }
        return n;
    }

    public class custom_output_stream extends OutputStream {
        private TextArea text_area;

        public custom_output_stream(TextArea ta) {
            text_area = ta;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text area
            text_area.appendText(String.valueOf((char) b));
            // scrolls the text area to the end of data
            text_area.positionCaret(text_area.getLength());
            // keeps the textArea up to date
            //textArea.update(textArea.getGraphics());
        }

        public void set_text_area(TextArea ta) {
            text_area = ta;
        }
    }
}
