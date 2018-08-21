package org.emaginalabs.beanproxy.core.web.bind.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author jose
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArgumentsWrapperBean {
	
	/**
	 * 
	 * @return
	 */
	String className();
	
	/**
	 * 
	 * @return
	 */
	String method();

}