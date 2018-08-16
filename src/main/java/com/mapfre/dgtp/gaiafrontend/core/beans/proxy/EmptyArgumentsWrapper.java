package com.mapfre.dgtp.gaiafrontend.core.beans.proxy;

/**
 * 
 * @author fmarmar
 *
 */
public class EmptyArgumentsWrapper implements ArgumentsWrapper {
	
	public static final ArgumentsWrapper INSTANCE = new EmptyArgumentsWrapper();

	private EmptyArgumentsWrapper() { }

	public Object[] toArray() {
		return new Object[0];
	}

}
