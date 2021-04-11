package org.chai.resolver.filesys;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public String getInputDir() {
        return inputDir;
    }

    public Path getInputPath() {
        return inputPath;
    }

}
