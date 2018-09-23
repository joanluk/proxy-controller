package org.emaginalabs.beanproxy.core.controller.proxy;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import org.emaginalabs.beanproxy.core.beans.proxy.*;
import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentIndex;
import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentsWrapperBean;
import org.emaginalabs.beanproxy.core.web.bind.annotation.MultipleArgumentsWrapperBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 
 * @author jose
 * 
 */
public class BeanProxyBinderTest {

	private BeanProxyBinder binder;

	@Before
	public void init() {
		binder = new BeanProxyBinder();
		binder.setBasePackage("org.emaginalabs.beanproxy.core.controller.proxy");
		binder.setObjectMapper(new ObjectMapper());
		binder.setResourcePattern("BeanProxyBinderTest$*.class");
	}

	@Test
	public void testAfterPropertiesSet() throws Exception {

		binder.afterPropertiesSet();

		Map<String, Class<?>> wrapperBeans = binder.getWrapperBeans();

		assertFalse(wrapperBeans.isEmpty());
		assertEquals(3, wrapperBeans.size());
		assertTrue(wrapperBeans.containsKey("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod1"));
		assertEquals(TestBean.class, wrapperBeans.get("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod1"));
		assertTrue(wrapperBeans.containsKey("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod7"));
		assertEquals(TestBean3.class, wrapperBeans.get("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod7"));
		assertTrue(wrapperBeans.containsKey("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod8"));
		assertEquals(TestBean3.class, wrapperBeans.get("org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest.testMethod8"));

	}

	@Test
	public void testBind() throws Exception {

		binder.afterPropertiesSet();
		Method aMethod;
		JsonNode data;
		ArgumentsWrapper result;
		Object[] arguments;

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod1", Integer.TYPE, String.class, Boolean.TYPE);
		data = buildFromJson("{\"a\":1,\"b\":\"kk\",\"c\":true}");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof TestBean);
		assertEquals(1, ((TestBean) result).getA());
		assertEquals("kk", ((TestBean) result).getB());
		assertTrue(((TestBean) result).isC());
		data = buildFromJson("[\"1\",\"kk\",\"true\"]");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof NoArgumentsWrapper);
		arguments = result.toArray();
		assertEquals(1, arguments[0]);
		assertEquals("kk", arguments[1]);
		assertTrue((Boolean) arguments[2]);
		assertArrayEquals(new Object[] { 1, "kk", true }, arguments);

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod2");
		data = buildFromJson("{}");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof EmptyArgumentsWrapper);
		assertTrue(result.toArray().length == 0);
		data = MissingNode.getInstance();
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof EmptyArgumentsWrapper);
		assertTrue(result.toArray().length == 0);
		data = buildFromJson("[]");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof NoArgumentsWrapper);
		assertTrue(result.toArray().length == 0);

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod3", String[].class);
		data = buildFromJson("[[\"kk\"]]");
		result = binder.bind(aMethod, data);
		assertTrue("Expected type is NoArgumentsWrapper but was " + result.getClass(), result instanceof NoArgumentsWrapper);
		assertArrayEquals(new Object[] { new String[] { "kk" } }, result.toArray());

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod4", String.class);
		data = buildFromJson("\"kk\"");
		result = binder.bind(aMethod, data);
		assertTrue("Expected type is SingleArgumentsWrapper but was " + result.getClass(), result instanceof SingleArgumentsWrapper);
		assertEquals("kk", ((SingleArgumentsWrapper) result).getOriginalObject());
		assertArrayEquals(new Object[] { "kk" }, result.toArray());
		data = buildFromJson("[\"kk\"]");
		result = binder.bind(aMethod, data);
		assertTrue("Expected type is NoArgumentsWrapper but was " + result.getClass(), result instanceof NoArgumentsWrapper);
		assertArrayEquals(new Object[] { "kk" }, result.toArray());

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod5", Integer.TYPE, String.class, Boolean.TYPE);
		data = buildFromJson("{\"a\":1,\"b\":\"kk\",\"c\":true}");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof GeneratedArgumentsWrapper);
		assertEquals(1, PropertyUtils.getProperty(result, "a"));
		assertEquals("kk", PropertyUtils.getProperty(result, "b"));
		assertTrue((Boolean) PropertyUtils.getProperty(result, "c"));
		assertArrayEquals(new Object[] { 1, "kk", true }, result.toArray());

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod6", TestBean2.class);
		data = buildFromJson("{\"a\":1,\"b\":\"kk\",\"c\":true}");
		result = binder.bind(aMethod, data);
		assertTrue("Expected type is SingleArgumentsWrapper but was " + result.getClass(), result instanceof SingleArgumentsWrapper);
		assertEquals(1, PropertyUtils.getProperty(((SingleArgumentsWrapper) result).getOriginalObject(), "a"));
		assertEquals("kk", PropertyUtils.getProperty(((SingleArgumentsWrapper) result).getOriginalObject(), "b"));
		assertTrue((Boolean) PropertyUtils.getProperty(((SingleArgumentsWrapper) result).getOriginalObject(), "c"));
		assertArrayEquals(new Object[] { ((SingleArgumentsWrapper) result).getOriginalObject() }, result.toArray());

		aMethod = ReflectionUtils.findMethod(TestInterface.class, "testMethod1", Integer.TYPE, String.class, Boolean.TYPE);
		data = buildFromJson("[1,\"kk\",true]");
		result = binder.bind(aMethod, data);
		assertTrue(result instanceof NoArgumentsWrapper);
		arguments = result.toArray();
		assertEquals(1, arguments[0]);
		assertEquals("kk", arguments[1]);
		assertTrue((Boolean) arguments[2]);
		assertArrayEquals(new Object[] { 1, "kk", true }, arguments);

	}

	@Test(expected = IllegalStateException.class)
	public void testBindError() throws Exception {

		Method aMethod;
		JsonNode data;

		aMethod = ReflectionUtils.findMethod(TestInterface.class, "testMethod1", Integer.TYPE, String.class, Boolean.TYPE);
		data = buildFromJson("{}");
		binder.bind(aMethod, data);

	}

	@Test
	public void testBindingInfo() throws Exception {

		binder.afterPropertiesSet();
		Method aMethod;
		Object result;

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod1", Integer.TYPE, String.class, Boolean.TYPE);
		result = binder.bindingInfo(aMethod);
		assertTrue(result instanceof TestBean);
		assertEquals(0, ((TestBean) result).getA());
		assertEquals(null, ((TestBean) result).getB());
		assertFalse(((TestBean) result).isC());

		aMethod = ReflectionUtils.findMethod(this.getClass(), "testMethod5", Integer.TYPE, String.class, Boolean.TYPE);
		result = binder.bindingInfo(aMethod);
		assertEquals(0, PropertyUtils.getProperty(result, "a"));
		assertEquals(null, PropertyUtils.getProperty(result, "b"));
		assertFalse((Boolean) PropertyUtils.getProperty(result, "c"));

		aMethod = ReflectionUtils.findMethod(TestInterface.class, "testMethod1", Integer.TYPE, String.class, Boolean.TYPE);
		result = binder.bindingInfo(aMethod);
		assertTrue(result instanceof Object[]);
		assertEquals(0, ((Object[]) result)[0]);
		assertEquals(null, ((Object[]) result)[1]);
		assertFalse((Boolean) ((Object[]) result)[2]);

	}

	private JsonNode buildFromJson(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, JsonNode.class);
	}

	public void testMethod1(int a, String b, boolean c) {
	}

	public void testMethod2() {
	}

	public void testMethod3(String[] names) {
	}

	public void testMethod4(String a) {
	}

	public void testMethod5(int a, String b, boolean c) {
	}

	public void testMethod6(TestBean2 testBean) {
	}

	public void testMethod7(int a, String b, boolean c) {
	}

	public void testMethod8(int a, String b, boolean c) {
	}

	@Getter
	@Setter
	@ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest", method = "testMethod1")
	public static class TestBean extends AbstractArgumentsWrapper {

		@ArgumentIndex(0)
		private int a;

		@ArgumentIndex(1)
		private String b;

		@ArgumentIndex(2)
		private boolean c;
	}

	@Getter
	@Setter
	public static class TestBean2 {

		private int a;

		private String b;

		private boolean c;
	}

	@Getter
	@Setter
	@MultipleArgumentsWrapperBean({ @ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest", method = "testMethod7"), @ArgumentsWrapperBean(className = "org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyBinderTest", method = "testMethod8") })
	public static class TestBean3 extends AbstractArgumentsWrapper {

		@ArgumentIndex(0)
		private int a;

		@ArgumentIndex(1)
		private String b;

		@ArgumentIndex(2)
		private boolean c;
	}

	public static interface TestInterface {

		void testMethod1(int a, String b, boolean c);

	}

}
