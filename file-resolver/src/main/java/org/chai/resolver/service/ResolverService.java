package org.chai.resolver.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface ResolverService {

	void resolveOrder(Boolean isYesterday) throws Exception;
}
