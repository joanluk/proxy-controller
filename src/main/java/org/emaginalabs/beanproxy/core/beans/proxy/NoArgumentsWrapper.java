package org.emaginalabs.beanproxy.core.beans.proxy;

/**
 * 
 * @author jose
 *
 */
public final class NoArgumentsWrapper implements ArgumentsWrapper {
	
	private final Object[] arguments;
	
	public NoArgumentsWrapper(Object[] arguments) {
		if (arguments == null) {
			this.arguments = new Object[0];
		} else {
			this.arguments = arguments;
		}
	}


	public Object[] toArray() {
		return arguments;
	}

}
