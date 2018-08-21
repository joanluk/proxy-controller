package org.emaginalabs.beanproxy.core.utils.lang;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * @author fmarmar
 *
 */
public class AuditToStringStyle extends ToStringStyle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7358228114282590484L;
	
	public static final transient AuditToStringStyle INSTANCE = new AuditToStringStyle(); 
	
	private AuditToStringStyle() {
		super();
		setUseClassName(false);
		setUseIdentityHashCode(false);
	}
	
	@Override
	protected void appendContentStart(StringBuffer buffer) { }
	
	@Override
	protected void appendContentEnd(StringBuffer buffer) { }
	
	@Override
	protected void appendFieldStart(StringBuffer buffer, String fieldName) {
        super.appendFieldStart(buffer, fieldName);
        buffer.append(getContentStart());
    }
	
	@Override
	protected void appendFieldEnd(StringBuffer buffer, String fieldName) {
		buffer.append(getContentEnd());
        appendFieldSeparator(buffer);
    }
	
}