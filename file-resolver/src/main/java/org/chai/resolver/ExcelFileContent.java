package org.chai.resolver;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ExcelFileContent extends FileContent {
    private String filename;
    private String absolutePath;
    private List<String> headers;
    private List<Map<String, String>> content;
    private String templateName;

    public ExcelFileContent() {

    }

    public void setContent(List<Map<String, String>> content) {
        this.content = content;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
