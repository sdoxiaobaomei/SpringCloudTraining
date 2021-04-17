package org.chai.resolver.filesys;

import org.chai.resolver.FileContent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

final public class Output extends FileSystem {
    private static final String OUTPUT_FOLDER_NAME = "output";
    private String outputDir;
    private Path outputPath;

    @Override
    void init() {
        outputDir = concatDir(OUTPUT_FOLDER_NAME);
        outputPath = concatPath(OUTPUT_FOLDER_NAME);
    }

    @Override
    void createDirectories() {
        createFilesSystem(outputPath);
    }

    @Override
    File[] readFiles() {
        return new File(outputDir).listFiles();
    }

    public String getOutputDir() {
        return outputDir;
    }

    public Path getOutputPath() {
        return outputPath;
    }
}
