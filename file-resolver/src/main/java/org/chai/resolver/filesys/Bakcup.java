package org.chai.resolver.filesys;

import java.nio.file.Path;

public final class Bakcup extends FileSystem{
    private static final String BACKUP_FOLDER_NAME = "backup";
    private String backupDir;
    private Path backupPath;

    @Override
    void init() {
        backupDir = concatDir(BACKUP_FOLDER_NAME);
        backupPath = concatPath(BACKUP_FOLDER_NAME);
    }

    @Override
    void createDirectories() {
        createFilesSystem(backupPath);
    }

    public String getBackupDir() {
        return backupDir;
    }

    public Path getBackupPath() {
        return backupPath;
    }
}
