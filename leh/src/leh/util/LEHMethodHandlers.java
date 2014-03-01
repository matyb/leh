package leh.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leh.util.LEH.LEHInstance;
import leh.util.wrapper.LEHMethodHandler;
import leh.util.wrapper.MethodHandler;

/**
 * The constants for MethodHandler implementations LEH supports.
 */
public class LEHMethodHandlers extends ArrayList<MethodHandler>{

	private static final long serialVersionUID = 787650288465749449L;
	
	/**
	 * MethodHandler for interception of equals method when invoked on wrapped
	 * proxy instances.
	 * 
	 * @see wrap(Object instance, List<MethodHandler> methodHandlers)
	 */
	final static MethodHandler EQUALS = new LEHMethodHandler("equals", new Class[]{Object.class}) {
		private static final long serialVersionUID = -8471547281850970478L;
		@Override
		public Object invokeOnUnwrappedLEHProxyInstances(Object instance, Object... args) {
			return ((LEHInstance)LEH.getInstance()).isEqual(instance, args[0], true);
		}
	};
	/**
	 * MethodHandler for interception of hashCode method when invoked on wrapped
	 * proxy instances.
	 * 
	 * @see wrap(Object instance, List<MethodHandler> methodHandlers)
	 */
	final static MethodHandler HASHCODE = new LEHMethodHandler("hashCode", new Class[0]) {
		private static final long serialVersionUID = -6797737598996956214L;
		@Override
		public Object invokeOnUnwrappedLEHProxyInstances(Object instance, Object... args) {
			return ((LEHInstance)LEH.getInstance()).getHashCode(instance, true);
		}
	};
	/**
	 * MethodHandler for interception of toString method when invoked on wrapped
	 * proxy instances.
	 * 
	 * @see wrap(Object instance, List<MethodHandler> methodHandlers)
	 */
	final static MethodHandler TOSTRING = new LEHMethodHandler("toString", new Class[0]) {
		private static final long serialVersionUID = 5812630837074351932L;
		@Override
		public Object invokeOnUnwrappedLEHProxyInstances(Object instance, Object... args) {
			return ((LEHInstance)LEH.getInstance()).getToString(instance, true);
		}
	};
	
	/**
	 * All supported LEH methods, currently: equals, hashCode, toString.
	 */
	public final static List<MethodHandler> ALL_LEH_METHODS = Arrays.asList(EQUALS, HASHCODE, TOSTRING);
	
	/**
	 * Just equality and hashCode LEH MethodHandlers.
	 */
	public final static List<MethodHandler> EQUALS_HASHCODE_METHODS = Arrays.asList(EQUALS, HASHCODE);
	
	/**
	 * Just the toString LEH MethodHandler, in a List for convenience.
	 */
	public final static List<MethodHandler> TOSTRING_METHODS = Arrays.asList(TOSTRING);
	
	/**
	 * Restricted to prevent instantiation. Reference constants instead.
	 */
	private LEHMethodHandlers(){}
	
}
