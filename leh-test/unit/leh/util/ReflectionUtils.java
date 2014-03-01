package leh.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {

	public static <T> T createAnonymous(T instance, Object parent) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		@SuppressWarnings("unchecked")
		Constructor<T> cons = (Constructor<T>) instance.getClass().getDeclaredConstructor(parent.getClass());
		boolean wasAccessible = cons.isAccessible();
		try{
			cons.setAccessible(true);
			return cons.newInstance(parent);
		}finally{
			cons.setAccessible(wasAccessible);
		}
	}
	
}
