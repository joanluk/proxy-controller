package org.emaginalabs.beanproxy.core.context.events;

import org.emaginalabs.beanproxy.core.utils.lang.AuditToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author jose
 *
 */
public abstract class ApplicationAuditEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6212076183719151885L;
	
	public ApplicationAuditEvent(Object source) {
		super(source);
	}

	public abstract boolean isErrorEvent();
	
	public abstract Throwable getFailureCause();
	
	public abstract String getEventName();

	public String getDescription() {
		ReflectionToStringBuilder toStrBuilder = new ReflectionToStringBuilder(this, AuditToStringStyle.INSTANCE);
		toStrBuilder.setAppendTransients(false);
		toStrBuilder.setExcludeFieldNames(new String[] {"failureCause"});
		return toStrBuilder.toString();
	}
	
	@Override
	public String toString() {
		return getEventName() + " - " + getDescription();
	}
	
}
