package org.chai.resolver.dao;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chai.resolver.entity.ResolverResultSet;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public interface FileDao {

	ResolverResultSet readInputFile(File inputFile);

	void writeOutputFile(Path outputPath, HSSFWorkbook workbook);

	void moveFile(Path source, Path destination, String fileName);

}
