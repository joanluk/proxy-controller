package org.emaginalabs.beanproxy.core.controller.proxy.behaviour;

import org.emaginalabs.beanproxy.core.beans.proxy.ArgumentsWrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * Standard null implementation for bean proxy controller
 * 
 * @author jose
 * 
 */
public class NullProxyControllerBehaviourStrategy implements ProxyControllerBehaviourStrategy {


	public void addCustomBehaviour(ArgumentsWrapper inputData, HttpServletRequest httpRequest) {
	}

}
