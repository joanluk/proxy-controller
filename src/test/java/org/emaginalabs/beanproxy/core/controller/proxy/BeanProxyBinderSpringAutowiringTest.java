package org.emaginalabs.beanproxy.core.controller.proxy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jose
 * 
 * 
 * @see BeanProxyBinderSpringConfiguration for test configuration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BeanProxyBinderSpringConfiguration.class)
public class BeanProxyBinderSpringAutowiringTest {

	@Autowired
	private BeanProxyBinder binder;

	@Test
	public void testSpringInjection() {
		Assert.assertNotNull(binder);
		Assert.assertNotNull(binder.objectMapper);
	}

}
