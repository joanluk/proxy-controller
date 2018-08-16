package com.mapfre.dgtp.gaiafrontend.core.context.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * 
 * @author fmarmar
 *
 */
public class IdBeanLocator implements BeanProxyLocator, ApplicationContextAware {

	protected ApplicationContext appCtx;


	public Object getBean(String beanId) throws NoSuchBeanDefinitionException, BeansException {
		if (appCtx.containsBean(beanId)) {
			return appCtx.getBean(beanId);
		} else {
			throw new NoSuchBeanDefinitionException(beanId);
		}
	}
	

	public Collection<String> getAvailableIds() {
		
		List<String> ids = new ArrayList<String>();
		
		ApplicationContext searchAppCtx = appCtx;
		
		while (searchAppCtx != null) {
			ids.addAll(Arrays.asList(appCtx.getBeanDefinitionNames()));
			searchAppCtx = appCtx.getParent();
		}
		
		return ids;
	}

	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		this.appCtx = appCtx;
	}

}
