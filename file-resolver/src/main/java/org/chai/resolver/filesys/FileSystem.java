package org.chai.resolver.filesys;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileSystem {
    protected String rootDir = "C:/让菜菜更轻松";

    /**
     * 初始化
     */
    abstract void init();

    /**
     * 创建目录
     */
    abstract void createDirectories();

    public FileSystem() {
        initRootDir();
        init();
        createDirectories();
    }

    private void initRootDir() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            rootDir = URLDecoder.decode(path, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("不支持的Encoding：" + e.getLocalizedMessage());
        }
    }

    protected String concatDir (String folderName) {
        return rootDir + "\\" + folderName;
    }

    protected Path concatPath (String folderName) {
        return Paths.get(rootDir + "\\" + folderName);
    }

    protected void createFilesSystem(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.println("创建输出文件目录失败：" + e.getLocalizedMessage());
        }
    }
}
