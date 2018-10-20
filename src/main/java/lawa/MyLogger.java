package lawa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyLogger implements Logger {
    private final File logFile;
    private String fileName;
    private int pageNumber;

    private boolean hasBeenChanged;

    MyLogger(File logFile) {

        this.logFile = logFile;
        this.logFile.delete();
    }

    public void setFile(File file) {
        if (this.fileName == null || !this.fileName.equals(file.getName()))
        {
            hasBeenChanged = true;
            this.fileName = file.getName();
        }
    }

    public void setPageNumber(int pageNumber) {
        if (this.pageNumber != pageNumber) {
            hasBeenChanged = true;
            this.pageNumber = pageNumber;
        }
    }

    @Override
    public void log(String msg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter((logFile),true))) {
            if (hasBeenChanged) {
                writer.write(fileName + " (" + pageNumber + "):");
                writer.newLine();
                this.hasBeenChanged = false;
            }

            writer.write(msg);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
