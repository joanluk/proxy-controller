package org.emaginalabs.beanproxy.core.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Miscellaneous class utility methods
 * @author jose
 *
 */
public class ClassUtils extends org.springframework.util.ClassUtils {


	private ClassUtils() { }
	
	/**
	 * Returns the corresponding object class for the given annotation name
	 * 
	 * @param annotationName The name of the class representing the annotation
	 * @param classLoader The classloader to use. If null, the default one will be use
	 * @return The annotation class object
	 * @throws ClassNotFoundException If no class was found for the annotationName or the class found is not an annotation
	 * @throws LinkageError {@link Class#forName(String)}
	 */
	@SuppressWarnings("unchecked")
	public static Class<Annotation> annotationForName(String annotationName, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
		Class<?> annotation = forName(annotationName, classLoader);
		
		if (annotation.isAnnotation()) {
			return (Class<Annotation>) annotation;
		} else {
			throw new ClassNotFoundException(annotationName + " is not an annotation");
		}
	}
	
	/**
	 * Extension of the {@link Method#getDeclaringClass()} method to find the declaring class on proxied classes.<br>
	 * 
	 * In case the method has been obtained from a proxied class, it will search the method in the interfaces the proxied class
	 * implements returning the first one declaring the method.
	 * 
	 * @param method The method
	 * @return The declaring class for the given method
	 */
	public static Class<?> getDeclaringClass(Method method) {
		
		Class<?> declaringClass = method.getDeclaringClass();
		
		// XXX We should consider CGLIB proxies too
		
		if (Proxy.isProxyClass(declaringClass)) { // The method is declared in one interface
			return (getDeclaringClass(declaringClass.getInterfaces(), method));
		} else {
			return declaringClass;	
		}
		
	}
	
	/**
	 * 
	 * @param ifaces
	 * @param method
	 * @return
	 */
	private static Class<?> getDeclaringClass(Class<?>[] ifaces, Method method) {
		
		for (Class<?> iface : ifaces) {
			if (ReflectionUtils.findMethod(iface, method.getName(), method.getParameterTypes()) != null) {
				return iface;
			}
		}
		
		throw new IllegalStateException("Should never get here");
	}

	public static boolean isConcrete(Class<?> clazz) {
		return !clazz.isInterface() && !isAbstract(clazz);
	}
	
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	

}
