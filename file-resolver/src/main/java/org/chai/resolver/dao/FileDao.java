package org.chai.resolver.dao;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public interface FileDao {

	List<Map<String, String>> readInputFile(File inputFile);

	writeOutputFile()

}
