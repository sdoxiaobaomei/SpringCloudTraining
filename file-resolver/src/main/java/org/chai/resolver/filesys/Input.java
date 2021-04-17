package org.chai.resolver.filesys;

import org.chai.resolver.ExcelFileContent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

final public class Input extends FileSystem {
    private static final String INPUT_FOLDER_NAME = "input";
    private String inputDir;
    private Path inputPath;

    @Override
    void init() {
        inputDir = concatDir(INPUT_FOLDER_NAME);
        inputPath = concatPath(INPUT_FOLDER_NAME);
    }

    @Override
    void createDirectories() {
        createFilesSystem(inputPath);
    }

    @Override
    public File[] readFiles() {
        return new File(inputDir).listFiles();
    }

    @Override
    public ExcelFileContent readFileContent(File file) {

        return new ExcelFileContent();
    }

    public String getInputDir() {
        return inputDir;
    }

    public Path getInputPath() {
        return inputPath;
    }

}
