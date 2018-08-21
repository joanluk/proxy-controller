package org.emaginalabs.beanproxy.core.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * 
 * @author jose
 *
 */
public class RequestPreffixMappingHandlerMapping extends RequestMappingHandlerMapping {

	private static final transient Logger LOG = LoggerFactory.getLogger(RequestPreffixMappingHandlerMapping.class);
	
	public RequestPreffixMappingHandlerMapping() {
		super();
		setOrder(0);
	}



	@Override
	protected boolean isHandler(Class<?> beanType) {
		return RequestPreffixMapping.class.isAssignableFrom(beanType);
	}

	@Override
	protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {

		String preffix = getPreffixFromHandler(handler);
		
		if (preffix == null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Rejected method '{}' from handler '{}'. No request preffix for handler", method.getName(), handler);
			}
		} else {
			super.registerHandlerMethod(handler, method, buildRequestMappingInfoForPreffix(preffix).combine(mapping));
		}

	}
	
	private String getPreffixFromHandler(Object handler) {
		
		String preffix = null;
		
		if (handler instanceof String) {
			handler = getApplicationContext().getBean((String) handler);
		}
		
		if (handler instanceof RequestPreffixMapping) {
			preffix = ((RequestPreffixMapping) handler).getRequestPreffix();
		}
		
		return !StringUtils.isEmpty(preffix) ? preffix : null;
		
	}
	
	private RequestMappingInfo buildRequestMappingInfoForPreffix(String preffix) {
		return new RequestMappingInfo(new PatternsRequestCondition(preffix), null, null, null, null, null, null);
	}

}
