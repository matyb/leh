package leh.util;

public abstract class MethodHandler {

	private String name;
	private Class<?>[] argumentTypes;
	
	public MethodHandler(String name, Class<?>[] argumentTypes){
		this.name = name;
		this.argumentTypes = argumentTypes;
	}
	
	abstract Object invoke(Object instance, Object...args);
	
	public String getName() {
		return name;
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}
	
}
