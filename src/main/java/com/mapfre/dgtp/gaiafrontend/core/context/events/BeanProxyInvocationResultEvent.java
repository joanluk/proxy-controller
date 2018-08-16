package com.mapfre.dgtp.gaiafrontend.core.context.events;



/**
 * 
 * @author fmarmar
 *
 */
public class BeanProxyInvocationResultEvent extends BeanProxyInvocationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6105511542697967052L;
	
	private static final String EVENT_NAME = "BeanProxyInvocationResultEvent";
	
	private String outputData;
		
	public BeanProxyInvocationResultEvent(Object source, String serviceName, String methodName, String inputData, String outputData) {
		super(source, serviceName, methodName, inputData);
		this.outputData = outputData;
	}
	

	@Override
	public String getEventName() {
		return EVENT_NAME;
	}
	
	public String getOutputData() {
		return outputData;
	}

}
