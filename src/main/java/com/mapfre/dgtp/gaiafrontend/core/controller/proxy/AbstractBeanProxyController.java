package com.mapfre.dgtp.gaiafrontend.core.controller.proxy;

import com.mapfre.dgtp.gaia.commons.util.GaiaReflectionUtils;
import com.mapfre.dgtp.gaiafrontend.core.context.beans.BeanProxyLocator;
import com.mapfre.dgtp.gaiafrontend.core.web.handler.RequestPreffixMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Base class for a Bean Proxy Controllers implementations used by 
 * {@link com.mapfre.dgtp.gaiafrontend.core.controller.proxy.BeanProxyControllerDocumentation} to
 * create dinamyc documnentation
 * 
 * @author fmarmar
 *
 */

public abstract class AbstractBeanProxyController implements RequestPreffixMapping {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected Object getService(String serviceName) {
		try {
			return getBeanLocator().getBean(serviceName);
		} catch (NoSuchBeanDefinitionException noSuchBeanDefE) {
				throw new NoSuchBeanDefinitionException("Service " + serviceName + " not found");
		}
	}
	
	protected Method getMethod(Object service, String methodName) {
		
		//TODO mejorar, p.e si el bean implementa ApplicationContextAware se podria ejecutar el metodo setApplicationContext
		// lo cual no es del todo incorrecto dada la idea del beanProxyController pero habria que ver como eliminar esa posibilidad.
				

		return getMethod(service.getClass(), methodName);
	}
	
	protected Method getMethod(Class<?> serviceClass, String methodName) {
		
		Method method = GaiaReflectionUtils.findUniqueMethod(serviceClass, methodName);

		if (method == null) {
			log.debug("Method {} not found", methodName);
			throw new NoSuchBeanDefinitionException("Operation " + methodName + " not found");
		}

		if (!Modifier.isPublic(method.getModifiers())) {
			log.debug("Method {} is not public", methodName);
			throw new NoSuchBeanDefinitionException("Operation " + methodName + " is not available");
		}

		return method;
	}
	
	protected abstract BeanProxyLocator getBeanLocator();
	
	protected abstract BeanProxyBinder getProxyBinder();
	
}
