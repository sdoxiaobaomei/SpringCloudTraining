package org.chai.resolver.dao;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chai.resolver.entity.ResolverResultSet;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public interface FileDao {

	ResolverResultSet readInputFile(File inputFile);

	void writeOutputFile(Path outputPath, HSSFWorkbook workbook);

	void moveFile(Path source, Path destination, String fileName);

}
