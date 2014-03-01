package leh.util;

import java.util.Map;

public interface LEHDelegate {
	
	/**
	 * Returns a Wrapper implementing equals, hashCode, and toString logically for the
	 * supplied instance.
	 * 
	 * @param instance
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @see leh.annotations.Transient
	 * @return
	 */
	Wrapper getInstance(Object instance);
	
	/**
	 * To return true either of the statements:<BR> 
	 *     instance == instance2;<BR>
	 *     instance.equals(instance2);<BR> 
	 *     
	 * evaluate to true; or if both instances are of types that implement LEHAware, 
	 * than fields may be tested by reflection for equality. Values found to be 
	 * of types implementing LEHAware in reflectively testing for equality enter 
	 * the same test.<BR><BR>
	 * 
	 * If instance1 is null false will always be returned from equals, otherwise invoke
	 * equals on the supplied argument.
	 * 
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @see leh.annotations.Transient
	 * @param instance
	 * @return
	 */
	Object getEquals(Object instance);
	
	/**
	 * Return an Object that implements hashCode such that when it is invoked it
	 * returns a logical hashCode for the provided instance if it implements LEHAware,
	 * the instances hashCode, or a default if it is null.
	 * 
	 * @param instance
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @see leh.annotations.Transient
	 * @return
	 */
	Object getHashCode(Object instance);
	
	/**
	 * Return an Object that implements toString such that when it is invoked it
	 * returns a logical toString value for the provided instance if it implements LEHAware,
	 * what is returned from the instance's toString() method, or a "null" if it is null.
	 * 
	 * @param instance
	 * @return
	 */
	Object getToString(Object instance);
	
	/**
	 * Return an Map<String, Object> of identity values (Wrapped by LEH) as discovered for
	 * @Identity annotated fields. 
	 * 
	 * @param instance
	 * @return
	 */
	Map<String, Object> getIdentity(Object instance);
	
}
