package leh.util.wrapper;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A method implementation used by LEHInvocationHandler. Maps the name and
 * argument types to a method. Provides plumbing to logic by which to override a
 * matching method invocation at runtime.
 */
public abstract class MethodHandler implements Serializable {

	private static final long serialVersionUID = -5896198214733158641L;

	/**
	 * The name of the method to intercept calls to.
	 */
	private String name;
	
	/**
	 * The arguments of the method to intercept calls to. Differing counts or
	 * types will result in this handler not being invoked.
	 */
	private Class<?>[] argumentTypes;
	
	/**
	 * Constructs a new method to be called when a method by the supplied name
	 * and argument types is invoked.
	 * 
	 * @param name
	 * @param argumentTypes
	 * @see leh.util.wrapper.LEHInvocationHandler
	 */
	public MethodHandler(String name, Class<?>[] argumentTypes){
		this.name = name;
		this.argumentTypes = argumentTypes;
	}
	
	/**
	 * The logic that executes in place of the method to overridden.
	 * @param instance
	 * @param args
	 * @return
	 */
	public abstract Object invoke(Object instance, String methodName, Object...args);
	
	/**
	 * The name of the method to intercept calls to.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * The arguments of the method to intercept calls to. Differing counts or
	 * types will result in this handler not being invoked.
	 * @return
	 */
	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}
	
	@Override
	public String toString() {
		return getName() + Arrays.toString(getArgumentTypes());
	}
	
}