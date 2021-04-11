package org.chai.resolver.filesys;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public String getTemplateDir() {
        return templateDir;
    }

    public Path getTemplatePath() {
        return templatePath;
    }
}
