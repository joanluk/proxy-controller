package org.emaginalabs.beanproxy.core.beans.proxy;

/**
 * 
 * @author jose
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

	public Object[] toArray() {
		return new Object[] {object};
	}
	
}