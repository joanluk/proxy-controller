package org.emaginalabs.beanproxy.core.utils;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * 
 * 
 * @author jose
 *
 */
public class LogicalOperationTypeFilter implements TypeFilter {
	
	public enum Operation {
		AND,
		OR,
		XOR
	}
	
	private final TypeFilter filter1;
	
	private final TypeFilter filter2;
	
	private final Operation operation;
	
	public LogicalOperationTypeFilter(TypeFilter filter1, Operation operation, TypeFilter filter2) {
		this.filter1 = filter1;
		this.filter2 = filter2;
		this.operation = operation;
	}

	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		
		switch (operation) {
			case AND:
				return filter1.match(metadataReader, metadataReaderFactory) && filter2.match(metadataReader, metadataReaderFactory);
			case OR:
				return filter1.match(metadataReader, metadataReaderFactory) || filter2.match(metadataReader, metadataReaderFactory);
			case XOR:
				return filter1.match(metadataReader, metadataReaderFactory) ^ filter2.match(metadataReader, metadataReaderFactory);
			default:
				return false;
		}
		
		
	}
		
}