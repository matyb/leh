package leh.util.wrapper;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An InvocationHandler implementation that can intercept calls to mapped
 * methods supplied at construction time, or pass along the invocation to 
 * the wrapped instance if no such MethodHandler is found.
 */
public class LEHInvocationHandler implements InvocationHandler, Serializable {
	
	private static final long serialVersionUID = -529912911387072643L;

	/**
	 * The instance to dispatch calls to.
	 */
	private Object wrappedInstance;
	
	/**
	 * Handlers that may handle invocations on behalf of the wrapped instance,
	 * effectively overriding them on proxies utilizing this handler.
	 */
	private final Map<String, MethodHandler> handlers;
	
	/**
	 * Construct a new InvocationHandler that maps invocations to the supplied
	 * handlers, or passes along the invocation to the wrapped object if no
	 * handler is found.
	 * 
	 * @param wrappedInstance
	 * @param handlers
	 */
	public LEHInvocationHandler(Object wrappedInstance, List<MethodHandler> handlers){
		this.wrappedInstance = wrappedInstance;
		Map<String, MethodHandler> handlerByName = new HashMap<String, MethodHandler>();
		for(MethodHandler handler : handlers){
			handlerByName.put(handler.getName(), handler);
		}
		this.handlers = Collections.unmodifiableMap(handlerByName);
	}
	
	/**
	 * Return the instance this handler wraps access to.
	 * @return
	 */
	public Object getWrappedInstance(){
		return wrappedInstance;
	}
	
	/**
	 * Implementation of InvocationHandler.invoke(). Maps invocations to the
	 * supplied handlers, or passes along the invocation to the wrapped object
	 * if no handler is found.
	 */
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
		MethodHandler handler = handlers.get(arg1.getName());
		Object result;
		if (handler != null
				&& ((arg2 == null && handler.getArgumentTypes().length == 0) || 
					(arg2 != null && Arrays.equals(handler.getArgumentTypes(), arg1.getParameterTypes())))){
			result = handler.invoke(wrappedInstance, arg1.getName(), arg2);
		}else{
			result = arg1.invoke(wrappedInstance, arg2);
		}
		return result;
	}
	
}
