package com.mapfre.dgtp.gaiafrontend.core.beans.proxy;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author fmarmar
 *
 */
public abstract class GeneratedArgumentsWrapper implements ArgumentsWrapper {
	
	private static final ConcurrentMap<Class<?>, String[]> GENERATED_BEANS_PROPS_ORDER = new ConcurrentHashMap<Class<?>, String[]>();

	public Object[] toArray() {
		
		Class<?> clazz = this.getClass();
		
		if (GENERATED_BEANS_PROPS_ORDER.containsKey(clazz)) {
			return toArray(GENERATED_BEANS_PROPS_ORDER.get(clazz));
		} else {
			throw new IllegalStateException("No registered class: " + clazz);
		}
	}
	
	private Object[] toArray(String[] argsNames) {
		
		Object[] args = new Object[argsNames.length];
		
		try {
			int idx = 0;
			for (String argName : argsNames) {
				args[idx++] = PropertyUtils.getSimpleProperty(this,argName);
			}
		} catch (Throwable th) {
			throw new RuntimeException(th.getMessage(), th);
		}
		
		return args;
		
	}
	
	public static void registerGeneratedBean(Class<?> clazz, String[] argumentNames) {
		GENERATED_BEANS_PROPS_ORDER.putIfAbsent(clazz, argumentNames);
	}

}
