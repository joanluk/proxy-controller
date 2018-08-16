package com.mapfre.dgtp.gaiafrontend.core.beans.proxy;

/**
 * 
 * @author fmarmar
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
