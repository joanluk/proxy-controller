package com.mapfre.dgtp.gaiafrontend.core.web.bind.annotation;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArgumentIndex {
	
	/**
	 * 
	 * @return
	 */
	int value();

}