package leh.util.wrapper;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import leh.util.LEHAware;
import leh.util.LEHMethodHandlers;

/**
 * Wraps objects in a proxy with optional interfaces and MethodHandlers. 
 * The summation of all abstract methods from supplied interfaces must 
 * be implemented in supplied MethodHandler instances.
 * 
 * Proxy instances resulting from wrap invocations will by default 
 * intercept any supplied calls to equals, hashCode, and toString
 * unless 
 */
public class LEHWrapper {

	/**
	 * Singleton instance.
	 */
	private static LEHWrapper instance = new LEHWrapper();
	
	/**
	 * List of method handlers that LEH supports (equals, hashCode, toString).
	 */
	private final List<MethodHandler> methodHandlers;	
	
	/**
	 * Returns singleton instance of LEHWrapper.
	 * @return
	 */
	public static LEHWrapper getInstance(){
		return instance;
	}
	
	/**
	 * Syntactic sugar for wrap method. Merely invokes wrap on the provided
	 * instance with the singleton instance backing this class.
	 * 
	 * @param object
	 * @return
	 */
	public static LEHAware getInstance(Object object) {
		return instance.wrap(object);
	}
	
	/**
	 * Syntactic sugar for wrap method. Merely invokes wrap on the provided
	 * instances with the singleton instance backing this class.
	 * 
	 * @param object
	 * @return
	 */
	public static List<LEHAware> getInstance(Collection<Object> objects) {
		return instance.wrap(objects);
	}
	
	/**
	 * Produces a new LEHWrapper aware of the provided method handlers as
	 * opposed to the default equals, hashcode, and tostring methods.
	 * 
	 * @param methodHandlers
	 * @return
	 */
	public static LEHWrapper getInstance(MethodHandler... methodHandlers) {
		return new LEHWrapper(Arrays.asList(methodHandlers));
	}
	
	/**
	 * Returns a proxy wrapping the passed in instance that implements
	 * equals/hashcode/toString via Entity with LEH.
	 * 
	 * @see leh.util.LEHAware
	 * @param instance
	 * @return
	 */
	public LEHAware wrap(Object instance) {
		return wrap(instance, methodHandlers);
	}
	
	/**
	 * Returns a proxy wrapping the passed in instance that implements any of
	 * the supplied equals/hashcode/toString handlers via Entity with LEH.
	 * 
	 * @see leh.util.LEHAware
	 * @param instance
	 * @return
	 */
	public LEHAware wrap(Object instance, List<MethodHandler> handlers) {
		return (LEHAware)wrap(instance, handlers, LEHAware.class, new Class[0]);
	}
	
	/**
	 * Returns a list of proxies wrapping the passed in instances. Each
	 * implements equals/hashcode/toString via Entity with LEH.
	 * 
	 * @see leh.util.LEHAware
	 * @param instances
	 * @return
	 */
	public List<LEHAware> wrap(Collection<Object> instances) {
		return wrap(instances, methodHandlers);
	}
	
	/**
	 * Returns a list of proxies wrapping the passed in instances. Each
	 * implements any of the supplied equals/hashcode/toString handlers via
	 * Entity with LEH.
	 * 
	 * @see leh.util.LEHAware
	 * @param instances
	 * @return
	 */
	public List<LEHAware> wrap(Collection<Object> instances, List<MethodHandler> handlers) {
		List<LEHAware> entities = new ArrayList<LEHAware>(instances.size());
		for(Object instance : instances){
			entities.add(wrap(instance, handlers));
		}
		return entities;
	}
	
	/**
	 * Returns a proxy wrapping the passed in instance that implements
	 * equals/hashcode/toString via Entity with LEH as well as any
	 * supplied interfaces. Instance supplied must implement any
	 * supplied interfaces if they are cast to that type or do not
	 * have a concrete implementation of any specified methods nor
	 * a handler associated for the call.
	 * 
	 * @see leh.util.LEHAware
	 * @param instance
	 * @param ifaces
	 * @return
	 */
	public LEHAware wrap(Object instance, Class<?>...ifaces) {
		return (LEHAware)wrap(instance, methodHandlers, LEHAware.class, ifaces);
	}
	
	/**
	 * Returns a proxy wrapping the passed in instance that implements any of
	 * the supplied equals/hashcode/toString handlers via Entity with LEH as
	 * well as any supplied interfaces. Instance supplied must implement any
	 * supplied interfaces if they are cast to that type or do not
	 * have a concrete implementation of any specified methods nor
	 * a handler associated for the call.
	 * 
	 * Same as Object, List<MethodHandler>, Class<?>... method but casts to the first provided interface class 
	 * 
	 * @param instance
	 * @param handlers
	 * @param ifaces
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T wrap(Object instance, List<MethodHandler> handlers, Class<T> referencedInterfaceType, Class<?>... ifaces) {
		Set<Class<?>> interfaces = new HashSet<Class<?>>(Arrays.asList(ifaces));
		interfaces.add(LEHAware.class);
		interfaces.add(referencedInterfaceType);
		Class<?> clazz = instance.getClass();
		while(clazz != null){
			interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
			clazz = clazz.getSuperclass();
		}
		if(ifaces.length != interfaces.size()){
			ifaces = interfaces.toArray(new Class[interfaces.size()]);
		}
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), 
				ifaces, new LEHInvocationHandler(instance, handlers));
	}

	/**
	 * Returns a list of proxies wrapping the passed in instances. Each
	 * implements equals/hashcode/toString via Entity with LEH as well as any
	 * supplied interfaces. Instance supplied must implement any
	 * supplied interfaces if they are cast to that type or do not
	 * have a concrete implementation of any specified methods nor
	 * a handler associated for the call.
	 * 
	 * @see leh.util.LEHAware
	 * @param instance
	 * @param ifaces
	 * @return
	 */
	public List<LEHAware> wrap(Collection<Object> instances, Class<?>...ifaces) {
		return wrap(instances, methodHandlers, ifaces);
	}
	
	/**
	 * Returns a list of proxies wrapping the passed in instances. Each
	 * implements any of equals/hashcode/toString per supplied handlers via
	 * Entity with LEH as well as any supplied interfaces. Instances 
	 * supplied must implement any supplied interfaces if they are cast to 
	 * that type or do not have a concrete implementation of any specified 
	 * methods nor a handler associated for the call.
	 * 
	 * @param instances
	 * @param handlers
	 * @param ifaces
	 * @return
	 */
	public List<LEHAware> wrap(Collection<Object> instances, List<MethodHandler> handlers, Class<?>...ifaces) {
		List<LEHAware> entities = new ArrayList<LEHAware>(instances.size());
		for(Object instance : instances){
			entities.add(wrap(instance, handlers, LEHAware.class, ifaces));
		}
		return entities;
	}
	
	/**
	 * Returns a list of proxies wrapping the passed in instances. Each
	 * implements any of equals/hashcode/toString per supplied handlers via
	 * Entity with LEH as well as any supplied interfaces. Instances 
	 * supplied must implement any supplied interfaces if they are cast to 
	 * that type or do not have a concrete implementation of any 
	 * specified methods nor a handler associated for the call.
	 * 
	 * @param instances
	 * @param handlers
	 * @param ifaces
	 * @return
	 */
	public <T> List<T> wrap(Collection<Object> instances, List<MethodHandler> handlers, Class<T> referencedInterfaceType, Class<?>...ifaces) {
		List<T> entities = new ArrayList<T>(instances.size());
		for(Object instance : instances){
			entities.add(wrap(instance, handlers, referencedInterfaceType, ifaces));
		}
		return entities;
	}

	/**
	 * Made inaccessible, this is a singleton or produced by factory.
	 * @param methodHandlers
	 */
	private LEHWrapper(List<MethodHandler> methodHandlers) {
		this.methodHandlers = methodHandlers;
	}
	
	/**
	 * Made inaccessible, solely used for singleton.
	 */
	private LEHWrapper(){
		this(LEHMethodHandlers.ALL_LEH_METHODS);
	}
	
}
