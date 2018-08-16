package com.mapfre.dgtp.gaiafrontend.core.beans.proxy;

import java.io.Serializable;

/**
 * Class representing a void result from a Proxied bean service
 * @author fmarmar
 *
 */
public final class VoidBeanProxyReturn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 780505805366037220L;
	
	public static final transient VoidBeanProxyReturn INSTANCE = new VoidBeanProxyReturn();
	
	private VoidBeanProxyReturn() { }
	
}
