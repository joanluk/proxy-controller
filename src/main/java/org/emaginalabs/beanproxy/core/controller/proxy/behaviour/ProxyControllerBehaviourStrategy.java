package org.emaginalabs.beanproxy.core.controller.proxy.behaviour;

import org.emaginalabs.beanproxy.core.beans.proxy.ArgumentsWrapper;
import org.emaginalabs.beanproxy.core.controller.proxy.BeanProxyController;

import javax.servlet.http.HttpServletRequest;

/**
 * Applications may define custom behavior for the input data or the request
 * into {@link BeanProxyController} functionality before executing service
 *
 * @author jose
 */
public interface ProxyControllerBehaviourStrategy {
    void addCustomBehaviour(ArgumentsWrapper inputData, HttpServletRequest httpRequest);
}
