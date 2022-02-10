package org.chai.resolver.entity;

import java.util.Map;

public interface ResolverResultSetHandler {

	void add(Map<String, String> column);
	void addAll();

}
