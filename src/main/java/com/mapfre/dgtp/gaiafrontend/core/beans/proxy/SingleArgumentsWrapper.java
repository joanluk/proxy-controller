package com.mapfre.dgtp.gaiafrontend.core.beans.proxy;

/**
 * 
 * @author fmarmar
 *
 */
public final class SingleArgumentsWrapper implements ArgumentsWrapper {

	private final Object object;
	
	public SingleArgumentsWrapper(Object obj) {
		this.object = obj;
	}
	
	public Object getOriginalObject() {
		return object;
	}
	
	@Override
	public Object[] toArray() {
		return new Object[] {object};
	}
	
}