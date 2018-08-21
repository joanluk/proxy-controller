package org.emaginalabs.beanproxy.core.utils;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Type filter that perfoms the AND logical operation over the given filters
 * 
 * @author jose
 *
 */
public class AndTypeFilter implements TypeFilter {
	
	private Collection<TypeFilter> filters;
	
	public AndTypeFilter() {
		filters = new ArrayList<TypeFilter>();
	}
	
	public AndTypeFilter(Collection<TypeFilter> filters) {
		this.filters = filters;
	}

	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		
		for (TypeFilter tf : filters) {
			if (!tf.match(metadataReader, metadataReaderFactory)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void addFilter(TypeFilter filter) {
		filters.add(filter);
	}
	
	public void addFilters(Collection<TypeFilter> filters) {
		this.filters.addAll(filters);
	}
	
	public void reset() {
		filters.clear();
	}
	
}