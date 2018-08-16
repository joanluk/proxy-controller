package com.mapfre.dgtp.gaiafrontend.core.controller.proxy.behaviour;

import com.mapfre.dgtp.gaiafrontend.core.beans.proxy.ArgumentsWrapper;
import com.mapfre.dgtp.gaiafrontend.core.controller.proxy.BeanProxyController;

import javax.servlet.http.HttpServletRequest;

/**
 * Applications may define custom behavior for the input data or the request
 * into {@link BeanProxyController} functionality before executing service
 * 
 * @author fernando.rodriguez
 * 
 */
public interface ProxyControllerBehaviourStrategy {
	public void addCustomBehaviour(ArgumentsWrapper inputData, HttpServletRequest httpRequest);
}
