package leh.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LEHInvocationHandler implements InvocationHandler {
	
	private Object wrappedInstance;
	private final Map<String, MethodHandler> handlers;
	
	public LEHInvocationHandler(Object wrappedInstance, List<MethodHandler> handlers){
		this.wrappedInstance = wrappedInstance;
		Map<String, MethodHandler> handlerByName = new HashMap<String, MethodHandler>();
		for(MethodHandler handler : handlers){
			handlerByName.put(handler.getName(), handler);
		}
		this.handlers = Collections.unmodifiableMap(handlerByName);
	}
	
	public Object getWrappedInstance(){
		return wrappedInstance;
	}
	
	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
		MethodHandler handler = handlers.get(arg1.getName());
		Object result;
		if (handler != null
				&& ((arg2 == null && handler.getArgumentTypes().length == 0) || 
					(arg2 != null && Arrays.equals(handler.getArgumentTypes(), arg1.getParameterTypes())))){
			result = handler.invoke(wrappedInstance, arg2);
		}else{
			result = arg1.invoke(wrappedInstance, arg2);
		}
		return result;
	}
	
}
