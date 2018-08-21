package org.emaginalabs.beanproxy.core.context.events;

/**
 * 
 * @author jose
 *
 */
public class BeanProxyInvocationEvent extends ApplicationAuditEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1968425401458271476L;
	
	private static final String EVENT_NAME = "BeanProxyInvocationEvent";

	private String serviceName;
	
	private String methodName;
	
	private String inputData;
	
	public BeanProxyInvocationEvent(Object source, String serviceName, String methodName, String inputData) {
		super(source);
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.inputData = inputData;
	}
	
	@Override
	public boolean isErrorEvent() {
		return false;
	}

	@Override
	public Throwable getFailureCause() {
		return null;
	}

	@Override
	public String getEventName() {
		return EVENT_NAME;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getInputData() {
		return inputData;
	}

}
