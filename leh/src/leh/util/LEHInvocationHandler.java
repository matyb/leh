package leh.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LEHInvocationHandler implements InvocationHandler {

	private LEH leh = LEH.getInstance();
	private Object wrappedInstance;
	
	public LEHInvocationHandler(Object wrappedInstance){
		this.wrappedInstance = wrappedInstance;
	}
	
	public Object getWrappedInstance(){
		return wrappedInstance;
	}
	
	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
		if("equals".equals(arg1.getName()) && (arg2 != null && arg2.length == 1)){
			return leh.isEqual(wrappedInstance, arg2[0]);
		}else if("hashCode".equals(arg1.getName()) && (arg2 == null || arg2.length == 0)){
			return leh.getHashCode(wrappedInstance);
		}else if("toString".equals(arg1.getName()) && (arg2 == null || arg2.length == 0)){
			return leh.getToString(wrappedInstance);
		}
		return arg1.invoke(wrappedInstance, arg2);
	}

}
