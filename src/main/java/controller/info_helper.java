package controller;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class info_helper {
    private ExecutorService executorService;
    BasicThreadFactory factory = new BasicThreadFactory.Builder()
            .namingPattern("info")
            .build();

    public void open_doc(String file_name) {
        executorService = Executors.newSingleThreadExecutor(factory);
        if (Desktop.isDesktopSupported()) {
            File f = new File(Paths.get(Main.jar_folder_path, "documentation", file_name).toString());
            executorService.execute(() -> {
                try {
                    Desktop.getDesktop().open(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
