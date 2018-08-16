package com.mapfre.dgtp.gaiafrontend.core.context.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * 
 * @author fmarmar
 *
 */
public class StaticIdBeanLocator extends IdBeanLocator implements InitializingBean {

	private List<String> beanIds;
	

	public void afterPropertiesSet() throws Exception {
		if (beanIds == null) {
			beanIds = Collections.emptyList();
		}
	}

	@Override
	public Object getBean(String beanId) throws NoSuchBeanDefinitionException, BeansException {
		
		if (beanIds.contains(beanId)) {
			return super.getBean(beanId);
		} else {
			throw new NoSuchBeanDefinitionException(beanId);
		}
		
	}
	
	@Override
	public Collection<String> getAvailableIds() {
		return beanIds;
	}

	public void setBeanIds(List<String> beanIds) {
		this.beanIds = beanIds;
	}

}
