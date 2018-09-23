/**
 * 
 */
package org.emaginalabs.beanproxy.core.controller.proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author francisco.aranda
 * 
 */
@Configuration
public class BeanProxyBinderSpringConfiguration {

	@Bean
	public BeanProxyBinder getBeanProxyBinder() {
		return new BeanProxyBinder();
	}
}
