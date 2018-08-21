package org.emaginalabs.beanproxy.core.web.bind.annotation;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormModelAttribute {

}
