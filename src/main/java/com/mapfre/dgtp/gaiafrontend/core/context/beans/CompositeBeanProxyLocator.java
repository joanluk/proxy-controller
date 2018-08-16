package com.mapfre.dgtp.gaiafrontend.core.context.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.OrderComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author fmarmar
 *
 */
public class CompositeBeanProxyLocator implements BeanProxyLocator, InitializingBean {

	private List<BeanProxyLocator> beanProxyLocators;
	
	private static final Logger LOG = LoggerFactory.getLogger(CompositeBeanProxyLocator.class);

	private boolean orderList;


	public void afterPropertiesSet() throws Exception {
		if (beanProxyLocators == null) {
			beanProxyLocators = Collections.emptyList();
		} else {
			if (orderList) {
				Collections.sort(beanProxyLocators, OrderComparator.INSTANCE);
			}
		}
	}

	public Object getBean(String beanId) throws NoSuchBeanDefinitionException, BeansException {

		for (BeanProxyLocator beanProxyLocator : beanProxyLocators) {
			try {
				return beanProxyLocator.getBean(beanId);
			} catch (NoSuchBeanDefinitionException noSuchBeanDefE) {
				//DoNothing
				LOG.debug("No bean definition for " + beanId, noSuchBeanDefE);
			}
		}

		throw new NoSuchBeanDefinitionException(beanId, "No bean found for id " + beanId);
	}


	public Collection<String> getAvailableIds() {
		List<String> availablesIds = new ArrayList<String>(20);

		for (BeanProxyLocator beanProxyLocator : beanProxyLocators) {
			availablesIds.addAll(beanProxyLocator.getAvailableIds());
		}

		return availablesIds;
	}

	public void setBeanProxyLocators(List<BeanProxyLocator> beanProxyLocators) {
		this.beanProxyLocators = beanProxyLocators;
	}

	public void setOrderList(boolean orderList) {
		this.orderList = orderList;
	}

}