package com.mapfre.dgtp.gaiafrontend.core.controller.proxy.behaviour;

import com.mapfre.dgtp.gaiafrontend.core.beans.proxy.ArgumentsWrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * Standard null implementation for bean proxy controller
 * 
 * @author fernando.rodriguez
 * 
 */
public class NullProxyControllerBehaviourStrategy implements ProxyControllerBehaviourStrategy {


	public void addCustomBehaviour(ArgumentsWrapper inputData, HttpServletRequest httpRequest) {
	}

}
