package org.chai.resolver.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResolverResultSet extends AbstractResolverResultSet{
	private String[] title = {""};
	private List<Map<String, String>> result = new ArrayList<>();

	public void setHeader(String[] header){
		title = header.clone();
	}

	@Override
	public void add(Map<String, String> column) {
		result.add(column);
	}

	public List<Map<String, String>> getContent() {
		return this.result;
	}

	public String[] getHeader() {
		return this.title;
	}

	public void setContents(List<Map<String, String>> contents) {
		this.result = contents;
	}
}
