package org.emaginalabs.beanproxy.core.context.events;



/**
 * 
 * @author jose
 *
 */
public class BeanProxyInvocationExceptionEvent extends BeanProxyInvocationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6105511542697967052L;
	
	private static final String EVENT_NAME = "BeanProxyInvocationExceptionEvent";
	
	private Throwable failureCause;
		
	public BeanProxyInvocationExceptionEvent(Object source, String serviceName, String methodName, String inputData, Throwable failureCause) {
		super(source, serviceName, methodName, inputData);
		this.failureCause = failureCause;
	}
	
	@Override
	public boolean isErrorEvent() {
		return true;
	}

	@Override
	public Throwable getFailureCause() {
		return failureCause;
	}

	@Override
	public String getEventName() {
		return EVENT_NAME;
	}
	
}
