package com.mapfre.dgtp.gaiafrontend.core.context.beans;

import com.mapfre.dgtp.gaia.commons.annotation.GaiaService;
import com.mapfre.dgtp.gaia.commons.type.filter.AndTypeFilter;
import com.mapfre.dgtp.gaia.commons.type.filter.ModifierTypeFilter;
import com.mapfre.dgtp.gaia.commons.type.filter.ModifierTypeFilter.Type;
import com.mapfre.dgtp.gaia.commons.util.ClassPathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.StandardClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * 
 * @author fmarmar
 *
 */
public class GaiaServiceInterfaceNameBeanLocator implements BeanProxyLocator, ApplicationContextAware {

	private static final transient Logger LOG = LoggerFactory.getLogger(GaiaServiceInterfaceNameBeanLocator.class);

	protected ApplicationContext appCtx;
	
	private String resourcePattern;

	private String basePackage;

	private Map<String, Object> serviceBeans;

	public GaiaServiceInterfaceNameBeanLocator() {
		this.basePackage = "";
		serviceBeans = Collections.emptyMap();
	}

	@PostConstruct
	public void init() {

		Set<MetadataReader> candidateClasses = findCandidatesClasses();
		serviceBeans = new HashMap<String, Object>(candidateClasses.size());

		Object bean;

		for (MetadataReader metadata : candidateClasses) {
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("Searching bean for class: {}", metadata.getClassMetadata().getClassName());
			}

			bean = searchBeanInContext(getClassObject(metadata));

			if (bean != null) {
				serviceBeans.put(getShortName(metadata), bean);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Mapping id '{}' to bean: {}", getShortName(metadata), bean);
				}
			}

		}
		
	}

	private Set<MetadataReader> findCandidatesClasses() {

		ClassPathScanner scanner = new ClassPathScanner();

		if (resourcePattern != null) {
			scanner.setResourcePattern(resourcePattern);
		}

		AndTypeFilter andFilter = new AndTypeFilter();
		andFilter.addFilter(new ModifierTypeFilter(Type.INTERFACE));
		andFilter.addFilter(new AnnotationTypeFilter(GaiaService.class));
		scanner.addIncludeFilter(andFilter);

		return scanner.findCandidateComponents(basePackage);
	}
	
	@SuppressWarnings("rawtypes")
	private Class getClassObject(MetadataReader metadata) {

		ClassMetadata classMetadata = metadata.getClassMetadata();

		if (classMetadata instanceof StandardClassMetadata) {
			return ((StandardClassMetadata) classMetadata).getIntrospectedClass();
		} else {
			try {
				return Class.forName(classMetadata.getClassName(), false, Thread.currentThread().getContextClassLoader());
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Error getting class object from classpath scanning", e);
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object searchBeanInContext(Class type) {

		Map<String, Object> beansOfType = BeanFactoryUtils.beansOfTypeIncludingAncestors(appCtx, type);

		if (beansOfType.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No bean of type {} found in context", type.getName());
			}
			return null;
		} else {
			if (beansOfType.size() == 1) {
				return beansOfType.values().iterator().next();
			} else {
				throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
			}
		}

	}

	private String getShortName(MetadataReader metadata) {
		String className = metadata.getClassMetadata().getClassName();
		return ClassUtils.getShortName(className);
	}

	public Object getBean(String beanId) throws NoSuchBeanDefinitionException, BeansException {

		if (serviceBeans.containsKey(beanId)) {
			return serviceBeans.get(beanId);
		} else {
			throw new NoSuchBeanDefinitionException(beanId, "No bean found for interface name " + beanId);
		}

	}
	

	public Collection<String> getAvailableIds() {
		return serviceBeans.keySet();
	}
	

	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		this.appCtx = appCtx;
	}

	public void setResourcePattern(String resourcePattern) {
		this.resourcePattern = resourcePattern;
	}

	public void setBasePackage(String basePackage) {
		Assert.notNull(basePackage);
		this.basePackage = basePackage;
	}

	public Map<String, Object> getAvailableServiceBeansMap() {
		return Collections.unmodifiableMap(serviceBeans);
	}

}
