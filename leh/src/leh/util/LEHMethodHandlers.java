package leh.util;

import java.util.ArrayList;

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
		Object invoke(Object instance, Object... args) {
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
		Object invoke(Object instance, Object... args) {
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
		Object invoke(Object instance, Object... args) {
			return LEH.getInstance().getToString(instance, true);
		}
	};
	
	/**
	 * List of default LEH MethodHandler instances (equals, hashCode, toString). Can
	 * be modified to augment LEH method handling for wrapping instances with extra
	 * functionality external to LEH.
	 */
	public LEHMethodHandlers() {
		add(EQUALS);
		add(HASHCODE);
		add(TOSTRING);
	}
	
}
