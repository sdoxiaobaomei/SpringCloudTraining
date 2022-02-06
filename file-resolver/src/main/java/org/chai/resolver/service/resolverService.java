package org.chai.resolver.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface resolverService {

	void resolveOrder(Boolean isYesterday, List<File> inputFileList);
}
