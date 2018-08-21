package org.emaginalabs.beanproxy.core.context.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Collection;

/**
 * 
 * @author jose
 *
 */
public interface BeanProxyLocator {

	/**
	 * 
	 * @param beanId The id of the bean to get. The meaning depends of the implementation
	 * @return
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the specified id
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String beanId) throws NoSuchBeanDefinitionException, BeansException;
	
	/**
	 * 
	 * @return A list of all beanIds availables for this beanProxyLocator
	 */
	Collection<String> getAvailableIds();
	
}
