package org.emaginalabs.beanproxy.core.controller.proxy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.emaginalabs.beanproxy.core.beans.proxy.AbstractArgumentsWrapper;
import org.emaginalabs.beanproxy.core.beans.proxy.VoidBeanProxyReturn;
import org.emaginalabs.beanproxy.core.context.beans.BeanProxyLocator;
import org.emaginalabs.beanproxy.core.context.beans.IdBeanLocator;
import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentIndex;
import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentsWrapperBean;
import org.emaginalabs.beanproxy.core.web.bind.annotation.MultipleArgumentsWrapperBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;



/**
 * 
 * @author jose
 * 
 */
public class BeanProxyControllerTest {

	private static final String VALID_SERVICE_NAME = "myService";

	private static final String INVALID_SERVICE_NAME = "anotherService";

	private BeanProxyController controller;

	private BeanProxyLocator beanLocator;

	private BeanProxyBinder proxyBinder;

	private Object serviceBean;

	private ObjectMapper objMapper;

	private Validator validator;

	private HttpServletRequest httpRequest;

	private HttpSession httpSession;

	@Before
	public void initTest() throws Exception {

		objMapper = new ObjectMapper();

		serviceBean = new MyService();

		proxyBinder = new BeanProxyBinder();
		proxyBinder.setBasePackage("org.emaginalabs.beanproxy.core.controller.proxy");
		proxyBinder.setObjectMapper(objMapper);
		proxyBinder.afterPropertiesSet();

		beanLocator = spy(new IdBeanLocator());
		doReturn(serviceBean).when(beanLocator).getBean(VALID_SERVICE_NAME);
		doThrow(new NoSuchBeanDefinitionException("")).when(beanLocator).getBean("anotherService");

		httpRequest = mock(HttpServletRequest.class);
		httpSession = mock(HttpSession.class);

		validator = mock(Validator.class);

		controller = new BeanProxyController();
		controller.setBeanLocator(beanLocator);
		controller.setProxyBinder(proxyBinder);
		controller.setValidator(validator);


	}

	@Test(expected = RuntimeException.class)
	public void testGetServiceNotFound() {
		controller.getService(INVALID_SERVICE_NAME);
	}

	@Test
	public void testGetService() {
		assertNotNull(controller.getService(VALID_SERVICE_NAME));
	}

	@Test(expected = RuntimeException.class)
	public void testGetMethodNotFound() {
		controller.getMethod(serviceBean, "nonExistent");
	}

	@Test(expected = RuntimeException.class)
	public void testGetMethodNotPublic() {
		controller.getMethod(serviceBean, "invalid");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMethodOverloaded() {
		controller.getMethod(serviceBean, "overload");
	}

	@Test
	public void testGetMethod() {
		assertNotNull(controller.getMethod(serviceBean, "empty"));
		assertNotNull(controller.getMethod(serviceBean, "operation1"));
		assertNotNull(controller.getMethod(serviceBean, "operation2"));
	}

	@Test
	public void testInvokeService() throws Exception {

		Object actual;

		actual = controller.invokeService(VALID_SERVICE_NAME, "empty", buildFromJson("{}"), httpRequest);
		assertNotNull(actual);
		assertEquals(VoidBeanProxyReturn.INSTANCE, actual);

		actual = controller.invokeService(VALID_SERVICE_NAME, "operation1", buildFromJson("{}"), httpRequest);
		assertNotNull(actual);
		assertEquals(MyService.RETURNED_OBJ, actual);

		actual = controller.invokeService(VALID_SERVICE_NAME, "operation2", buildFromJson("{\"a\":\"kk\",\"b\":5}"), httpRequest);
		assertNotNull(actual);
		assertEquals(MyService.RETURNED_OBJ, actual);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvokeServiceWrongNumberArgs() throws Exception {
		controller.invokeService(VALID_SERVICE_NAME, "operation3", buildFromJson("{}"), httpRequest);
	}

	@Test(expected = JsonMappingException.class)
	public void testInvokeServiceInvalidArgs() throws Exception {
		controller.invokeService(VALID_SERVICE_NAME, "operation2", buildFromJson("{\"b\":\"kk\",\"a\":5}"), httpRequest);
	}

	@Test(expected = IOException.class)
	public void testDeserializeEmptyBody() throws IOException {
		String inputJson = StringUtils.EMPTY;
		buildFromJson(inputJson);
	}

	@Test(expected = NullPointerException.class)
	public void testDeserializeNullBody() throws IOException {
		String inputJson = null;
		buildFromJson(inputJson);
	}

	private JsonNode buildFromJson(String json) throws IOException {
		return objMapper.readValue(json, JsonNode.class);
	}

	public static class MyService {

		public static final Object RETURNED_OBJ = new Object();

		public MyService() {
		}

		public void empty() {
		}

		protected void invalid() {
		}

		public Object operation1() {
			return RETURNED_OBJ;
		}

		public Object operation2(String a, int b) {
			return RETURNED_OBJ;
		}

		public Object operation3(String a, int b) {
			return RETURNED_OBJ;
		}

		public Object overload() {
			return RETURNED_OBJ;
		}

		public Object overload(String a, int b) {
			return RETURNED_OBJ;
		}

		// public Object cnxSMethod(CnxS cnx, int b) {
		// return RETURNED_OBJ;
		// }
	}

	@MultipleArgumentsWrapperBean({ @ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyControllerTest$MyService", method = "empty"), @ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyControllerTest$MyService", method = "operation1"), @ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyControllerTest$MyService", method = "operation3") })
	public static class EmptyArgumentsWrapperBean extends AbstractArgumentsWrapper {

	}

	@Getter
	@Setter
	@ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyControllerTest$MyService", method = "operation2")
	public static class MyServiceOperation2ArgumentsWrapperBean extends AbstractArgumentsWrapper {

		@ArgumentIndex(0)
		private String a;

		@ArgumentIndex(1)
		private int b;

	}

}
