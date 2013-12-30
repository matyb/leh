package leh.util.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leh.util.LEH;

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
	public final static MethodHandler EQUALS = new MethodHandler("equals", new Class[]{Object.class}) {
		@Override
		public Object invoke(Object instance, Object... args) {
			return LEH.getInstance().isEqual(instance, args[0], true);
		}
	};
	/**
	 * MethodHandler for interception of hashCode method when invoked on wrapped
	 * proxy instances.
	 * 
	 * @see wrap(Object instance, List<MethodHandler> methodHandlers)
	 */
	public final static MethodHandler HASHCODE = new MethodHandler("hashCode", new Class[0]) {
		@Override
		public Object invoke(Object instance, Object... args) {
			return LEH.getInstance().getHashCode(instance, true);
		}
	};
	/**
	 * MethodHandler for interception of toString method when invoked on wrapped
	 * proxy instances.
	 * 
	 * @see wrap(Object instance, List<MethodHandler> methodHandlers)
	 */
	public final static MethodHandler TOSTRING = new MethodHandler("toString", new Class[0]) {
		@Override
		public Object invoke(Object instance, Object... args) {
			return LEH.getInstance().getToString(instance, true);
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
