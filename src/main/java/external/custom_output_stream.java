package external;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Timotej Sujan
 */
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