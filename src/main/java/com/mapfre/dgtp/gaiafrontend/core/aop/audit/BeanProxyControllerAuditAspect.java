package com.mapfre.dgtp.gaiafrontend.core.aop.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapfre.dgtp.gaiafrontend.core.context.events.BeanProxyInvocationEvent;
import com.mapfre.dgtp.gaiafrontend.core.context.events.BeanProxyInvocationExceptionEvent;
import com.mapfre.dgtp.gaiafrontend.core.context.events.BeanProxyInvocationResultEvent;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * 
 * @author fmarmar
 *
 */
@Aspect
public class BeanProxyControllerAuditAspect implements ApplicationEventPublisherAware {
	
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Setter
	@Autowired(required = false)
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Pointcut("execution (public Object com.mapfre.dgtp.gaiafrontend.core.controller.proxy.BeanProxyController.invokeService(..))")
	public void beanProxyInvocation() { }
	
	@Around("beanProxyInvocation()")
	//CHECKSTYLE.OFF: IllegalThrows - Comply Aspect method
	public Object publishInvocationEvent(ProceedingJoinPoint pjp) throws Throwable {
	//CHECKSTYLE.ON: IllegalThrows
		
		Object retValue = null;
		BeanProxyControllerInvocationInfo info = getProxyInvocationInfo(pjp.getArgs());
				
		try {
			auditBeforeInvocation(pjp.getTarget(), info);
			retValue = pjp.proceed();
			auditAfterInvocation(pjp.getTarget(), info, retValue);
			return retValue;
		} catch (Throwable th) {
			auditAfterException(pjp.getTarget(), info, th);
			throw th;
		}
		
	}

	
	private BeanProxyControllerInvocationInfo getProxyInvocationInfo(Object[] args) {
		
		String inputData = null;
		try {
			inputData = objectMapper.writeValueAsString(args[2]);
		} catch (Throwable th) {
			inputData = "Error getting inputData: " + th.getMessage();
		}
		
		return new BeanProxyControllerInvocationInfo((String) args[0], (String) args[1], inputData);
	}
	

	private void auditBeforeInvocation(Object source, BeanProxyControllerInvocationInfo info) {
		applicationEventPublisher.publishEvent(new BeanProxyInvocationEvent(source, info.getServiceName(), info.getMethodName(), info.getInputData()));
	}
	
	private void auditAfterInvocation(Object source, BeanProxyControllerInvocationInfo info, Object retValue) {
		String outputData = null;
		try {
			outputData = objectMapper.writeValueAsString(retValue);
		} catch (Throwable th) {
			outputData = "Error getting outputData: " + th.getMessage();
		}
		applicationEventPublisher.publishEvent(new BeanProxyInvocationResultEvent(source, info.getServiceName(), info.getMethodName(), info.getInputData(), outputData));
	}

	
	private void auditAfterException(Object source, BeanProxyControllerInvocationInfo info, Throwable th) {
		applicationEventPublisher.publishEvent(new BeanProxyInvocationExceptionEvent(source, info.getServiceName(), info.getMethodName(), info.getInputData(), th));
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	@Getter
	private static class BeanProxyControllerInvocationInfo {
		
		private final String serviceName;
		
		private final String methodName;
		
		private final String inputData;
		
		public BeanProxyControllerInvocationInfo(String serviceName, String methodName, String inputData) {
			this.serviceName = serviceName;
			this.methodName = methodName;
			this.inputData = inputData;
		}
		
	}
	
}
