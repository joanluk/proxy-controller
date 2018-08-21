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
public @interface MultipleArgumentsWrapperBean {
	
	ArgumentsWrapperBean[] value();

}