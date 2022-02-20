package org.chai.resolver.service;

import org.chai.resolver.entity.vo.OrderResultVO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Service
public interface ResolverService {

	List<OrderResultVO> resolveOrder(Boolean isYesterday) throws Exception;

	InputStream getFileInputStreamByName(String filename) throws FileNotFoundException;
}
