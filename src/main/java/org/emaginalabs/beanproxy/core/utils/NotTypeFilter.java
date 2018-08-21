package org.emaginalabs.beanproxy.core.utils;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * 
 * @author jose
 *
 */
public class NotTypeFilter implements TypeFilter {
	
	private final TypeFilter filter;
	
	public NotTypeFilter(TypeFilter filter) {
		this.filter = filter;
	}
	

	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return !filter.match(metadataReader, metadataReaderFactory);
	}
		
}