package com.mapfre.dgtp.gaiafrontend.core.controller.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.mapfre.dgtp.gaia.commons.util.GaiaReflectionUtils;
import com.mapfre.dgtp.gaiafrontend.core.beans.proxy.AbstractArgumentsWrapper;
import com.mapfre.dgtp.gaiafrontend.core.beans.proxy.ArgumentsWrapper;
import com.mapfre.dgtp.gaiafrontend.core.beans.proxy.VoidBeanProxyReturn;
import com.mapfre.dgtp.gaiafrontend.core.context.beans.BeanProxyLocator;
import com.mapfre.dgtp.gaiafrontend.core.controller.proxy.behaviour.NullProxyControllerBehaviourStrategy;
import com.mapfre.dgtp.gaiafrontend.core.controller.proxy.behaviour.ProxyControllerBehaviourStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Controller able to execute any public method of a bean provided by a
 * {@link BeanProxyLocator}.
 *
 * @author fmarmar
 */
public class BeanProxyController extends AbstractBeanProxyController {

    private static final Logger LOG = LoggerFactory.getLogger(BeanProxyController.class);

    private String requestPreffix;

    protected BeanProxyLocator beanLocator;

    @Autowired
    protected BeanProxyBinder proxyBinder;

    @Autowired
    protected Validator validator;

    @Autowired(required = false)
    protected ProxyControllerBehaviourStrategy proxyControllerBehaviour = new NullProxyControllerBehaviourStrategy();

    @ResponseBody
    @RequestMapping(value = "/{serviceName}/{methodName}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object invokeService(@PathVariable("serviceName") String serviceName, @PathVariable("methodName") String methodName, @RequestBody JsonNode frontArgs, HttpServletRequest httpRequest) throws Exception {

        Object service = getService(serviceName);
        Method method = getMethod(service, methodName);
        Method binderMethod = getMethod(service, serviceName, methodName);
        ArgumentsWrapper inputData = proxyBinder.bind(binderMethod, frontArgs);
        proxyControllerBehaviour.addCustomBehaviour(inputData, httpRequest);
        validate(inputData, buildValidationName(serviceName, methodName));

        return execute(service, method, inputData.toArray());

    }

    protected Method getMethod(Object service, String serviceName, String methodName) {

        Class<?> iface = getInterfaceFromArray(service.getClass().getInterfaces(), serviceName);

        Method method;

        if (iface == null) {
            method = getMethod(service, methodName);
        } else {
            method = getMethod(iface, methodName);
        }

        return method;
    }

    private Class<?> getInterfaceFromArray(Class<?>[] interfaces, String serviceName) {

        Assert.notNull(interfaces, "interfaces classes is null");

        for (Class<?> iface : interfaces) {
            if (serviceName.equals(iface.getSimpleName())) {
                return iface;
            }
        }

        return null;
    }

    private String buildValidationName(String serviceName, String methodName) {
        return serviceName + '.' + methodName;
    }

    private void validate(ArgumentsWrapper inputData, String validationName) {

        if (inputData instanceof AbstractArgumentsWrapper) {

            BindingResult result = new BeanPropertyBindingResult(inputData, validationName);
            validator.validate(inputData, result);

            if (result.hasErrors()) {
                throw new RuntimeException("Error validation: " + result.getModel().toString());
            }

        }

    }

    protected Object execute(Object service, Method method, Object[] arguments) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing method " + method.getName() + " on bean " + service + " with arguments " + Arrays.toString(arguments));
        }

        Object result;

        result = GaiaReflectionUtils.invokeMethod(method, service, arguments);

        if (Void.TYPE.equals(method.getReturnType())) {
            result = VoidBeanProxyReturn.INSTANCE;
        }

        return result;

    }

    public String getRequestPreffix() {
        return requestPreffix;
    }

    public void setRequestPreffix(String requestPreffix) {
        if (requestPreffix.charAt(0) == '/') {
            this.requestPreffix = requestPreffix;
        } else {
            this.requestPreffix = "/" + requestPreffix;
        }
    }

    @Override
    protected BeanProxyLocator getBeanLocator() {
        return beanLocator;
    }

    public void setBeanLocator(BeanProxyLocator beanLocator) {
        this.beanLocator = beanLocator;
    }

    @Override
    protected BeanProxyBinder getProxyBinder() {
        return proxyBinder;
    }

    public void setProxyBinder(BeanProxyBinder proxyBinder) {
        this.proxyBinder = proxyBinder;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

}
