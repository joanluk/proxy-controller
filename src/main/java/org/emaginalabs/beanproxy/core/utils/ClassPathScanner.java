package org.emaginalabs.beanproxy.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

/**
 * 
 * Scans the classpath from a base package. It then applies exclude and include filters to the resulting classes to find
 * candidates.
 * 
 * Based on {@link org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider} to obtain any
 * class (included abstract classes and interfaces).
 * 
 * 
 * @author jose
 *
 */
@Slf4j
public class ClassPathScanner {

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private ResourcePatternResolver resourcePatternResolver;
	private MetadataReaderFactory metadataReaderFactory;
	private String resourcePattern;

	private final List<TypeFilter> includeFilters;
	private final List<TypeFilter> excludeFilters;

	public ClassPathScanner() {
		resourcePatternResolver = new PathMatchingResourcePatternResolver();
		metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		resourcePattern = DEFAULT_RESOURCE_PATTERN;
		includeFilters = new LinkedList<TypeFilter>();
		excludeFilters = new LinkedList<TypeFilter>();
	}

	/**
	 * Set the ResourceLoader to use for resource locations. This will typically be a ResourcePatternResolver
	 * implementation. <br>
	 * Default is PathMatchingResourcePatternResolver, also capable of resource pattern resolving through the
	 * ResourcePatternResolver interface.
	 * 
	 * @param resourceLoader
	 * 
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
	}

	/**
	 * Set the resource pattern to use when scanning the classpath. This value will be appended to each base package
	 * name.
	 * 
	 * @param resourcePattern
	 * 
	 * @see #findCandidateComponents(String)
	 * @see #DEFAULT_RESOURCE_PATTERN
	 */
	public void setResourcePattern(String resourcePattern) {
		Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
		this.resourcePattern = resourcePattern;
	}

	/**
	 * Add a collection of include type filter to the <i>end</i> of the inclusion list.
	 * 
	 * @param includeFilters
	 */
	public void addIncludeFilters(Collection<TypeFilter> includeFilters) {
		this.includeFilters.addAll(includeFilters);
	}

	/**
	 * Add an include type filter to the <i>end</i> of the inclusion list.
	 * 
	 * @param includeFilter
	 */
	public void addIncludeFilter(TypeFilter includeFilter) {
		this.includeFilters.add(includeFilter);
	}

	/**
	 * Add a collection of exclude type filter to the <i>front</i> of the exclusion list.
	 * 
	 * @param excludeFilters
	 */
	public void addExcludeFilters(Collection<TypeFilter> excludeFilters) {
		this.excludeFilters.addAll(excludeFilters);
	}

	/**
	 * Add an exclude type filter to the <i>front</i> of the exclusion list.
	 * 
	 * @param excludeFilter
	 */
	public void addExcludeFilter(TypeFilter excludeFilter) {
		this.excludeFilters.add(0, excludeFilter);
	}

	/**
	 * Reset the configured type filters.
	 */
	public void resetFilters() {
		this.includeFilters.clear();
		this.excludeFilters.clear();
	}

	/**
	 * Scan the class path for candidate clasess.
	 * 
	 * @param basePackage
	 *            the package to check for classes
	 * @return a corresponding Set of autodetected MetadataReader classes info
	 */
	public Set<MetadataReader> findCandidateComponents(String basePackage) {
		Set<MetadataReader> candidates = new LinkedHashSet<MetadataReader>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ resolveBasePackage(basePackage) + '/' + this.resourcePattern;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			for (Resource resource : resources) {
				log.trace("Scanning {}", resource);
				if (resource.isReadable()) {
					processCandidateResourceComponent(candidates, resource);
				} else {
					log.trace("Ignored because not readable: {}", resource);
				}
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}

	private void processCandidateResourceComponent(Set<MetadataReader> candidates, Resource resource)
			throws BeanDefinitionStoreException {
		try {
			MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
			if (isCandidateComponent(metadataReader)) {
				log.debug("Identified candidate component class: {}", resource);
				candidates.add(metadataReader);
			} else {
				log.trace("Ignored because not matching any filter: {}", resource);
			}
		} catch (Exception ex) {
			throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
		}
	}

	/**
	 * Resolve the specified base package into a pattern specification for the package search path.
	 * <p>
	 * The default implementation converts a "."-based package path to a "/"-based resource path.
	 * 
	 * @param basePackage
	 *            the base package as specified by the user
	 * @return the pattern specification to be used for package searching
	 */
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(basePackage);
	}

	/**
	 * Determine whether the given class does not match any exclude filter and does match at least one include filter.
	 * 
	 * @param metadataReader
	 *            the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : this.excludeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return false;
			}
		}
		for (TypeFilter tf : this.includeFilters) {
			if (tf.match(metadataReader, this.metadataReaderFactory)) {
				return true;
			}
		}
		return this.includeFilters.isEmpty();
	}
}
