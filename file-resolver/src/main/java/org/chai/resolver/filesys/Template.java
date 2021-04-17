package org.chai.resolver.filesys;

import org.chai.resolver.ExcelFileContent;

import java.io.File;
import java.nio.file.Path;

final public class Template extends FileSystem{
    private String name;
    private String[] headers;
    private static final String TEMPLATE_FOLDER_NAME = "output";
    private String templateDir;
    private Path templatePath;

    @Override
    void init() {
        templateDir = concatDir(TEMPLATE_FOLDER_NAME);
        templatePath = concatPath(TEMPLATE_FOLDER_NAME);
    }

    @Override
    void createDirectories() {
        createFilesSystem(templatePath);
    }

    @Override
    File[] readFiles() {
        return new File(templateDir).listFiles();
    }

    @Override
    public ExcelFileContent readFileContent(File file) {
        return new ExcelFileContent();
    }

    public String getTemplateDir() {
        return templateDir;
    }

    public Path getTemplatePath() {
        return templatePath;
    }
}
