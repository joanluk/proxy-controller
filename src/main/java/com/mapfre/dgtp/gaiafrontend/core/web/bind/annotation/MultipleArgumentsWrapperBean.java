package com.mapfre.dgtp.gaiafrontend.core.web.bind.annotation;

import java.lang.annotation.*;

/**
 * 
 * @author fmarmar
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultipleArgumentsWrapperBean {
	
	ArgumentsWrapperBean[] value();

}