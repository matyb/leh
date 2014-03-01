package leh.util;

import java.util.Map;

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
	
	public Object getToString(Object instance) {
		return wrapper.wrap(instance);
	}
	
	public Object getHashCode(Object instance) {
		return wrapper.wrap(instance);
	}


	public Object getEquals(Object instance1) {
		return wrapper.wrap(instance1);
	}
	
	public Map<String, Object> getIdentity(Object instance) {
		throw new RuntimeException("I'm untested!");
	}
	
}
