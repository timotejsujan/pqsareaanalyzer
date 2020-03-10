package internal;

import javafx.scene.control.TextArea;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Timotej Sujan
 */
public class CustomOutputStream extends OutputStream {
    private TextArea textArea;

    public CustomOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.appendText(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.positionCaret(textArea.getLength());
        // keeps the textArea up to date
        //textArea.update(textArea.getGraphics());
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }
}