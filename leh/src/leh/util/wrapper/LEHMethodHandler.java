package leh.util.wrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Unwraps Wrapped LEH Proxy instances to get the core unwrapped instance for
 * reflective access.
 */
public abstract class LEHMethodHandler extends MethodHandler{

	private static final long serialVersionUID = 221057597747859063L;

	public LEHMethodHandler(String name, Class<?>[] argumentTypes) {
		super(name, argumentTypes);
	}

	/**
	 * Unwraps instance and arguments before passing to handler implementation
	 * in invokeOnUnwrappedLEHProxyInstances(...
	 * 
	 * @param instance
	 * @param args
	 */
	@Override
	public Object invoke(Object instance, String methodName, Object... args) {
		return invokeOnUnwrappedLEHProxyInstances(unwrapLEHProxy(instance), unwrapLEHProxies(args));
	}
	
	/**
	 * Actual MethodHandler implementation, invoked by invoke(Object, Object[])
	 * after unwrapping any LEH Proxy instances in the invocation chain.
	 * 
	 * @param instance
	 * @param args
	 * @return
	 */
	public abstract Object invokeOnUnwrappedLEHProxyInstances(Object instance, Object... args);
	
	/**
	 * Returns the passed in array instance unless it is non null and has a
	 * length > 0. In the event the args array contains something, each element
	 * is checked to see if it is an LEH Proxy instance, if so its wrapped
	 * instance is returned until a non-LEH Proxy instance is discovered.
	 * 
	 * @param args
	 * @return
	 */
	private Object[] unwrapLEHProxies(Object[] args) {
		if(args != null && args.length > 0){
			Object[] unwrapped = new Object[args.length];
			for(int i = 0; i < args.length; i++){
				unwrapped[i] = unwrapLEHProxy(args[i]);
			}
			args = unwrapped;
		}
		return args;
	}
	
	/**
	 * Returns the passed in Object instance unless it is an LEH Proxy instance.
	 * If the instance is an LEH Proxy the wrapped instance is evaluated
	 * recursively until a non-LEH Proxy instance is discovered and returned.
	 * 
	 * @param args
	 * @return
	 */
	public static Object unwrapLEHProxy(Object instance){
		Class<?> instanceClass = instance == null ? int.class : instance.getClass();
		if(Proxy.isProxyClass(instanceClass)){
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
			if(invocationHandler instanceof LEHInvocationHandler){
				return unwrapLEHProxy(((LEHInvocationHandler)invocationHandler).getWrappedInstance());
			}
		}
		return instance;
	}
	
}
