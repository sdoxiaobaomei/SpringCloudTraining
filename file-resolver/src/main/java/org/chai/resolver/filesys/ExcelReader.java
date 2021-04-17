package org.chai.resolver.filesys;

import org.chai.resolver.FileContent;

import java.io.File;

public class ExcelReader {
    private final FileSystem fileSys;
    private File[] files;

    public ExcelReader(FileSystem fileSys) {
        this.fileSys = fileSys;
        this.files = fileSys.readFiles();
    }

    public FileContent read() {
        for (File file : files) {
            fileSys.readFileContent(file);
        }
        return null;
    }
}
