package leh.util.wrappers;

/**
 * A method implementation used by LEHInvocationHandler. Maps the name and
 * argument types to a method. Provides plumbing to logic by which to override a
 * matching method invocation at runtime.
 */
public abstract class MethodHandler {

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
	 * @see leh.util.wrappers.LEHInvocationHandler
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
	public abstract Object invoke(Object instance, Object...args);
	
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
	
}
