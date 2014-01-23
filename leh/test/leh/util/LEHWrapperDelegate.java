package leh.util;

import leh.util.wrapper.LEHWrapper;

/**
 * 
 */
public class LEHWrapperDelegate implements LEHDelegate {

	/**
	 * Local singleton instance.
	 */
	private static LEHDelegate instance = new LEHWrapperDelegate();
	
	private LEHWrapper wrapper = LEHWrapper.getInstance();
	
	/**
	 * Restricted to prevent instantiation, this is a singleton.
	 */
	private LEHWrapperDelegate(){}
	
   	
	public static LEHDelegate getInstance(){
		return instance;
	}
	
	public boolean isEqual(Object instance1, Object instance2) {
		instance1 = instance1 == null ? null : wrapper.wrap(instance1);
		instance2 = instance2 == null ? null : wrapper.wrap(instance2);
		return instance1 == null ? 
				instance2.equals(instance1) : instance1.equals(instance2);
	}
	
	public String getToString(Object instance) {
		return "" + wrapper.wrap(instance);
	}
	
	public int getHashCode(Object instance) {
		return wrapper.wrap(instance).hashCode();
	}
	
}
