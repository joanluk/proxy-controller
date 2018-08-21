package org.emaginalabs.beanproxy.core.beans.proxy;

/**
 * 
 * @author jose
 *
 */
public class EmptyArgumentsWrapper implements ArgumentsWrapper {
	
	public static final ArgumentsWrapper INSTANCE = new EmptyArgumentsWrapper();

	private EmptyArgumentsWrapper() { }

	public Object[] toArray() {
		return new Object[0];
	}

}
