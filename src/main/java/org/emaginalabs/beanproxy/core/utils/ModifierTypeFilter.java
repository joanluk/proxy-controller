package org.emaginalabs.beanproxy.core.utils;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Type Filter based in the declaring modifier
 * @author jose
 *
 */
public class ModifierTypeFilter implements TypeFilter {
	
	public enum Type {
		INTERFACE,
		ABSTRACT,
		CONCRETE,
		FINAL,
		INDEPENDENT
	};
	
	private Type type;
	
	public ModifierTypeFilter(Type type) {
		this.type = type;
	}

	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		
		switch (type) {
			case INTERFACE:
				return classMetadata.isInterface();
			case ABSTRACT:
				return classMetadata.isAbstract();
			case CONCRETE:
				return classMetadata.isConcrete();
			case FINAL:
				return classMetadata.isFinal();
			case INDEPENDENT:
				return classMetadata.isIndependent();
			default:
				return false;
		}
		
	}

}
