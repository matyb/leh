package leh.util;

public interface LEHDelegate {
	/**
	 * To return true either of the statements:<BR> 
	 *     instance1 == instance2;<BR>
	 *     instance1.equals(instace2);<BR> 
	 * evaluate to true; or if both instances are of types that implement Entity, 
	 * than fields may be tested by reflection for equality. Values found to be 
	 * of types implementing Entity in reflectively testing for equality enter 
	 * the same test.
	 * 
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @param instance1
	 * @param instance2
	 * @return
	 */
	boolean isEqual(Object instance1, Object instance2);
	
	/**
	 * Reflectively access fields and accumulate hashcode values as implemented
	 * specifically, implied by Entity inheritance.
	 * 
	 * @param instance
	 * @return
	 */
	int getHashCode(Object instance);
	
	/**
	 * Reflectively access fields and accumulate toString values as implemented
	 * specifically, implied by Entity inheritance.
	 * 
	 * @param instance
	 * @return
	 */
	String getToString(Object instance);
}
