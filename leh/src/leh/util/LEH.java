package leh.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import leh.annotations.Identity;
import leh.annotations.Transient;
import leh.util.wrappers.LEHMethodHandler;

/**
 * A utility class that operates on Entity instances or on the presumption of an
 * instance implementing Entity to assume how their equals, hashCode, and
 * toString methods should operate.
 */
public class LEH implements LEHDelegate {
	
	/**
	 * Private instance, for use internally.
	 */
	private static LEH instance = new LEH();
	
	/**
	 * State of private mutable instance is wrapped to prevent mutation when
	 * exposed externally.
	 */
	private static LEH immutableInstance = 
			new LEH(Collections.unmodifiableMap(instance.identities),
					Collections.unmodifiableMap(instance.equalsHashCodeFields));
	
	/**
	 * ToString adapter for Map.entrySet
	 */
	private final ToStringFunction mapToStringFunction = new ToStringFunction() {
		@Override
		public String toString(Object o, List<Object> evaluated) {
			Entry<?, ?> entry = (Entry<?, ?>)o;
			return getToString((Object)entry.getKey(), evaluated) + "=" + getToString(entry.getValue(), evaluated);
		}
	};
	
	/**
	 * ToString adapter for Object
	 */
	private final ToStringFunction iterableToStringFunction = new ToStringFunction() {
		@Override
		public String toString(Object o, List<Object> evaluated) {
			return getToString(o, evaluated);
		}
	};
	
	/**
	 * List of @Identity annotated fields as discovered via reflection, by
	 * class. Acts as local cache mitigating performance hit associated with
	 * repetitive reflective discovery.
	 */
	private final Map<Class<?>, List<Field>> identities;
	
	/**
	 * List of equality/hashcode eligible fields as discovered via reflection,
	 * by class*. Acts as local cache mitigating performance hit associated with
	 * repetitive reflective discovery.
	 * 
	 * @see leh.annotations.Identity for definition of equality/hashcode
	 *      eligibility.
	 */
	private final Map<Class<?>, List<Field>> equalsHashCodeFields;
	
	/**
	 * Constructor used internally for mutable reference to singleton instance.
	 */
	private LEH(){
		this(new ConcurrentHashMap<Class<?>, List<Field>>(),
			 new ConcurrentHashMap<Class<?>, List<Field>>());
	}
	
	/**
	 * Construct a new instance with the passed in identity and
	 * equality/hashcode fields by class. Used exclusively internally
	 * as references supplied to this constructor are mutated.
	 * 
	 * @param identities
	 * @param logicallyEqualFields
	 */
	private LEH(Map<Class<?>, List<Field>> identities,
			Map<Class<?>, List<Field>> logicallyEqualFields) {
		this.identities = identities;
		this.equalsHashCodeFields = logicallyEqualFields;
	}

	/**
	 * Returns a referentially transparent (immutable) version of the singleton.
	 * @return
	 */
	public static LEH getInstance(){
		return immutableInstance;
	}

	/**
	 * @see leh.util.LEHDelegate.isEqual(Object instance1, Object instance2)
	 */
	public boolean isEqual(Object instance1, Object instance2){
		return isEqual(instance1, instance2, isEntity(instance1));
	}
	
	/**
	 * To return true either of the statements: 
	 *   instance1 == instance2;
	 *   instance1.equals(instace2); 
	 * evaluate to true; or if isEntity is true, than fields may be 
	 * tested by reflection for equality. Values found to be of types implementing 
	 * Entity in reflectively testing for equality enter the same test.
	 * 
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @param instance1
	 * @param instance2
	 * @param isEntity
	 * @return
	 */
	boolean isEqual(Object instance1, Object instance2, boolean isEntity){
		return areValuesEqual(instance1, instance2, equalsHashCodeFields, new ArrayList<Object>(), isEntity);
	}
	
	/**
	 * To return true both instances must be of identical types that implement
	 * Entity, and fields annotated with @Identity must have equal values in
	 * both instances. Values found to be of types implementing Entity in 
	 * reflectively testing for identity enter the same test recursively.
	 * 
	 * @see leh.util.LEHAware
	 * @see leh.annotations.Identity
	 * @param instance1
	 * @param instance2
	 * @return
	 */
	public boolean isIdentity(Object instance1, Object instance2){
		return areValuesEqual(instance1, instance2, identities, new ArrayList<Object>(), isEntity(instance1));
	}

	private boolean areValuesEqual(Object instance1, Object instance2,
			Map<Class<?>, List<Field>> fields, List<Object> evaluated) {
		return areValuesEqual(instance1, instance2, fields, evaluated, isEntity(instance1));
	}
	
	private boolean areValuesEqual(Object instance1, Object instance2,
			Map<Class<?>, List<Field>> fields, List<Object> evaluated, boolean isEntity) {
		if(instance1 == instance2){
			return true;  
		}
		if(instance1 == null || instance2 == null){
			return false;
		}
		if(instance1 instanceof Iterable<?> && instance2 instanceof Iterable<?>){
			return getIterableEquals(instance1, instance2, fields, evaluated);
		}
		if(instance1 instanceof Map && instance2 instanceof Map){ 
			return getMapEquals(instance1, instance2, fields, evaluated);
		}
		if(isEntity){
			if(isAlreadyEvaluated(instance1, evaluated)){
				return instance1 == instance2 || 
						getHashCode(instance1) == getHashCode(instance2);
			}
			evaluated.add(instance1);
			Class<?> class1 = resolveClass(instance1);
			Class<?> class2 = resolveClass(instance2);
			if(class1 == class2){
				for(Field f : getFields(fields, class1, isEntity)){
					if(!areValuesEqual(getValue(f, instance1), 
									   getValue(f, instance2), 
									   fields, 
									   evaluated)){
						return false;
					}
				}
				return true;
			}
			return false;
		}else if(instance1.equals(instance2)){
			return true;
		}
		return false;
	}

	private boolean isAlreadyEvaluated(Object instance1, List<Object> evaluated) {
		for(Object o : evaluated){
			if(instance1 == o){
				return true;
			}
		}
		return false;
	}

	public Map<String, Object> getIdentity(Object instance) {
		return getValueByFieldName(instance, identities);
	}

	private Map<String, Object> getValueByFieldName(Object instance, 
			Map<Class<?>, List<Field>> fields) {
		return getValueByFieldName(instance,
				getFields(fields, instance.getClass(), isEntity(instance)));
	}

	/**
	 * Returns the passed in instance's class if it is not a proxy class. In the
	 * event the argument is of a proxy type, the wrapped type is returned
	 * instead.
	 * 
	 * @param instance
	 * @return
	 */
	private Class<?> resolveClass(Object instance) {
		return resolveInstance(instance).getClass();
	}

	/**
	 * Returns the passed in instance if it is not of a proxy type. In the event
	 * the argument is of a proxy type, the wrapped type is returned until
	 * unwrapping yields a wrapped type that has no LEHInvocationHandler.
	 * 
	 * Peels wrapper types off instance until underlying wrapped instance is
	 * found and returned. Supports recursive discovery enabling multiple layers
	 * of proxys to be unwrapped.
	 * 
	 * @param instance
	 * @return
	 */
	private Object resolveInstance(Object instance) {
		return LEHMethodHandler.unwrapLEHProxy(instance);
	}

	/**
	 * Used in place of Map equality tests in map access. Map equality tests invoke equals, which may not
	 * represent logical equality. 
	 * 
	 * TODO: Make performant. Performance is lousy on large maps.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param fields
	 * @return
	 */
	private boolean getMapEquals(Object instance1, Object instance2, Map<Class<?>, List<Field>> fields, List<Object> evaluated) {
		if(((Map<?,?>)instance1).size() != ((Map<?,?>)instance2).size()){
			return false;
		}
		for(Entry<?, ?> o1 : ((Map<?,?>)instance1).entrySet()){
			boolean foundValue = false;
			Object value1 = o1.getValue(), value2 = null;
			for(Entry<?, ?> o2 : ((Map<?,?>)instance2).entrySet()){
				if(areValuesEqual(o1.getKey(), o2.getKey(), fields, evaluated)){
					foundValue = true;
					value2 = o2.getValue();
					break;
				}
			}
			if(!foundValue || !isEqual(value1, value2)){
				return false;
			};
		}
		return true;
	}

	/**
	 * Used in place of Iterable equality tests in collections access. Iterable equality tests invoke equals, which may not
	 * represent logical equality. 
	 * 
	 * TODO: Make performant. Performance is lousy on large collections.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param fields
	 * @return
	 */
	private boolean getIterableEquals(Object instance1, Object instance2, 
			Map<Class<?>, List<Field>> fields, List<Object> evaluated) {
		if((instance1 instanceof Collection && instance2 instanceof Collection && 
				((Collection<?>)instance1).size() != ((Collection<?>)instance2).size()) ||
			instance1 instanceof Map && instance2 instanceof Map && 
				((Map<?,?>)instance1).size() != ((Map<?,?>)instance2).size()){
			return false;
		}
		for(Object o1 : (Iterable<?>)instance1){
			boolean foundEqual = false;
			for(Object o2 : (Iterable<?>)instance2){
				if(areValuesEqual(o1, o2, fields, evaluated)){
					foundEqual = true;
					break;
				}
			}
			if(!foundEqual){
				return false;
			};
		}
		return true;
	}

	/**
	 * @see leh.util.LEHDelegate.getHashCode()
	 */
	public int getHashCode(Object instance){
		return getHashCode(instance, isEntity(instance));
	}
	
	/**
	 * Reflectively access fields and accumulate hashcode values as implemented
	 * specifically, implied by Entity inheritance.
	 * 
	 * @param instance
	 * @param evaluated
	 * @return
	 */
	private int getHashCode(Object instance, List<Object> evaluated){
		return getHashCode(instance, evaluated, isEntity(instance));
	}
	
	/**
	 * Reflectively access fields and accumulate hashcode values as implemented
	 * specifically, whether to treat instance as an entity is passed explicitly
	 * via the isEntity argument.
	 * 
	 * @param instance
	 * @param isEntity 
	 * @return
	 */
	int getHashCode(Object instance, boolean isEntity){
		return getHashCode(instance, new ArrayList<Object>(), isEntity);
	}
	
	/**
	 * getHashCode without Recurring indefinitely in the case of circular references.
	 * @param instance
	 * @param arrayList memory
	 * @return
	 */
	private int getHashCode(Object instance, List<Object> evaluated, boolean isEntity) {
		if(instance != null){
			if(isAlreadyEvaluated(instance, evaluated)){
				return resolveClass(instance).hashCode();
			}
			evaluated.add(instance);
			if(isEntity){
				instance = resolveInstance(instance);
				Class<? extends Object> instanceClass = instance.getClass();
				int hashCode = instanceClass.hashCode();
				Map<String, Object> valuesByFieldName = getValueByFieldName(instance, getFields(equalsHashCodeFields, resolveClass(instance), isEntity));
				for(Entry<String, Object> fieldNameAndValue : valuesByFieldName.entrySet()){
					Object value = fieldNameAndValue.getValue();
					int tempHashCode = 0;
					if(value instanceof Iterable<?>){
						for(Object o : (Iterable<?>)value){
							tempHashCode = getHashCode(o, evaluated);
						}
					}else if(value instanceof Map){
						for(Entry<?,?> o : ((Map<?,?>)value).entrySet()){
							tempHashCode = getHashCode(o.getKey(), evaluated) + getHashCode(o.getValue(), evaluated);
						}
					} else if(value != null){
						tempHashCode = getHashCode(value, evaluated);
					}
					if(value != null){
						hashCode += tempHashCode + fieldNameAndValue.getKey().hashCode();
					}
				}
				return hashCode;
			}
		}
		return instance == null ? 0 : instance.hashCode();
	}

	/**
	 * @see leh.util.LEHDelegate.getToString()
	 */
	public String getToString(Object instance){
		return getToString(instance, isEntity(instance), new ArrayList<Object>());
	}
	
	/**
	 * Reflectively access fields and accumulate toString values as implemented
	 * specifically, or implied by the presumption of the instance implementing
	 * entity as indicated by the isEntity argument..
	 * 
	 * Assumption of entity type may be passed explicitly if an object is to be
	 * treated as an entity.
	 * 
	 * @param o
	 * @return
	 */
	String getToString(Object instance, boolean isEntity){
		return getToString(instance, isEntity, new ArrayList<Object>());
	}

	/**
	 * Reflectively create logical toString implementation, but memorizes
	 * evaluated instances to prevent circular references from recurring
	 * indefinitely.
	 * 
	 * @param instance2
	 * @param arrayList
	 * @return
	 */
	private String getToString(Object instance, List<Object> evaluated) {
		return getToString(instance, isEntity(instance), evaluated);
	}
	
	/**
	 * Reflectively create logical toString implementation, but memorizes
	 * evaluated instances to prevent circular references from recurring
	 * indefinitely.
	 * 
	 * @param instance2
	 * @param arrayList
	 * @return
	 */
	private String getToString(Object instance, boolean isEntity, List<Object> evaluated) {
		String toString;
		if(isEntity){
			if(isAlreadyEvaluated(instance, evaluated)){
				toString = "parentReference#"+getHashCode(resolveInstance(instance), isEntity);
			}else{
				evaluated.add(instance);
				Class<?> instanceClass = resolveClass(instance);
				Class<?> tempClass = instanceClass;
				while(tempClass.isAnonymousClass()){
					if(tempClass.getInterfaces() != null && tempClass.getInterfaces().length > 0){
						tempClass = tempClass.getInterfaces()[0];
					}else{
						tempClass = tempClass.getSuperclass();
					}
				}
				toString = tempClass.getSimpleName() + (tempClass == instanceClass ? "" : ("$1"))  + "=[";
				String seperator = ", ";
				List<Field> idFields = getFields(identities, instanceClass, isEntity);
				String idsString = getToString("ids={", instance, seperator, idFields, "}", evaluated);
				toString += idsString;
				List<Field> equalsFields = getFields(equalsHashCodeFields, instanceClass, isEntity);
				List<Field> fields = new ArrayList<Field>(equalsFields);
				fields.removeAll(idFields);
				if(fields.size() > 0){
					if(idsString.length() > 0){
						toString += seperator;
					}
					toString += getToString(instance, seperator, fields, evaluated);
				}
				toString += "]";
			}
		}else{
			toString = String.valueOf(instance); 
		}
		return toString;
	}

	/**
	 * Reflect over the supplied fields and accumulate a string representing the
	 * state found in the instance's values.
	 * 
	 * @param instance
	 * @param seperator
	 * @param fields
	 * @return
	 */
	private String getToString(Object instance, String seperator, List<Field> fields, List<Object> evaluated) {
		Iterator<Entry<String, String>> valueByFieldNameIterator = map(instance, fields, seperator, evaluated).entrySet().iterator();
		String toString = "";
		while(valueByFieldNameIterator.hasNext()){
			Entry<String, String> valueByFieldName = valueByFieldNameIterator.next();
			toString += valueByFieldName.getKey() + "=" + valueByFieldName.getValue();
			if(valueByFieldNameIterator.hasNext()){
				toString += seperator;
			}
		}
		return toString;
	}
	
	/**
	 * Used to style strings with a prepend/append value, only if the resulting
	 * toString value is significant (ie > 0 chars). Values as discovered in
	 * reflective access for supplied fields are seperated by the supplied
	 * seperator.
	 * 
	 * @param prepend
	 * @param instance
	 * @param seperator
	 * @param fields
	 * @param append
	 * @param evaluated
	 * @return
	 */
	private String getToString(String prepend, Object instance, String seperator, List<Field> fields, String append, List<Object> evaluated) {
		String toString = getToString(instance, seperator, fields, evaluated);
		return toString.length() > 0 ? prepend + toString + append : toString;
	}
	
	/**
	 * Used to create expressive toString implementations styling differing
	 * collection and value types with appropriate prepended/appended values.
	 * 
	 * @param instance
	 * @param fields
	 * @param seperator
	 * @param evaluated
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> map(Object instance, List<Field> fields, String seperator, List<Object> evaluated) {
		Map<String, String> map = fields.size() > 0 ? new LinkedHashMap<String, String>() : Collections.<String, String>emptyMap();
		Map<String, Object> values = getValueByFieldName(instance, fields);
		for(Entry<String, Object> fieldNameAndValue : values.entrySet()){
			String fieldName = fieldNameAndValue.getKey();
			Object value = fieldNameAndValue.getValue();
			if(value != null){ 
				if(value instanceof Iterable<?>){
					if(value instanceof List<?> && ((List<?>)value).size() > 0){
						map.put(fieldName, getToCollectionString("[", (List<?>)value, "]", seperator, evaluated));
					}else if(value instanceof Set && ((Set<?>)value).size() > 0){
						map.put(fieldName, getToCollectionString("#{", (Set<?>)value, "}", seperator, evaluated));
					}else {
						map.put(fieldName, getToCollectionString("(", (Iterable<?>)value, ")", seperator, evaluated));
					}
				}else if(value instanceof Map<?,?> && ((Map<?,?>) value).size() > 0){
					map.put(fieldName, getToCollectionString("{", (Map<Object, Object>)value, "}", seperator, evaluated));
				}else{
					map.put(fieldName, getToString(value, evaluated));
				}
			}
		}
		return map;
	}

	/**
	 * Used to create expressive toString implementations styling differing
	 * iterable types with appropriate prepended/appended values.
	 * 
	 * @param prepend
	 * @param value
	 * @param append
	 * @param seperator
	 * @param evaluated
	 * @return
	 */
	private String getToCollectionString(String prepend, Iterable<?> value, String append, String seperator, List<Object> evaluated) {
		return iteratorToString(prepend, value, seperator, append, iterableToStringFunction, evaluated);
	}
	
	/**
	 * Used to create expressive toString implementations styling map types with
	 * appropriate prepended/appended values.
	 * 
	 * @param prepend
	 * @param valueMap
	 * @param append
	 * @param seperator
	 * @param evaluated
	 * @return
	 */
	private String getToCollectionString(String prepend, Map<Object, Object> valueMap, String append, String seperator, List<Object> evaluated) {
		return iteratorToString(prepend, valueMap.entrySet(), seperator, append, mapToStringFunction, evaluated);
	}
	
	/**
	 * Used to create expressive toString implementations styling iterable types with
	 * supplied prepended/appended values only if collection size > 0.
	 * 
	 * @param prepend
	 * @param collection
	 * @param seperator
	 * @param append
	 * @param function
	 * @param evaluated
	 * @return
	 */
	private String iteratorToString(String prepend, Iterable<?> collection, String seperator, String append, ToStringFunction function, List<Object> evaluated){
		Iterator<?> values = collection.iterator();
		String toString = "";
		if(values.hasNext()){
			toString = prepend;
			while(values.hasNext()){
				String string = function.toString(values.next(), evaluated);
				if(string != null){
					toString += string;
					if(values.hasNext()){
						toString += seperator;
					}
				}
			}
			toString += append;
		}
		return toString;
	}

	/**
	 * Map fields to instance returning a map of field names with values
	 * representing values discovered for fields in instance.
	 * 
	 * @param instance
	 * @param fields
	 * @return
	 */
	private Map<String, Object> getValueByFieldName(Object instance, List<Field> fields){
		Map<String, Object> valueByFieldName = new LinkedHashMap<String, Object>();
		for(Field field : fields){
			Object value = getValue(field, instance);
			valueByFieldName.put(field.getName(), value);
		}
		return valueByFieldName;
	}

	/**
	 * Reflectively access a field and ensure it is not mutated.
	 * 
	 * @param field
	 * @param instance
	 * @return
	 */
	private Object getValue(Field field, Object instance) {
		if(field == null || instance == null){
			return null;
		}
		instance = resolveInstance(instance);
		boolean wasAbleToSetAccessible = false;
		boolean wasAccessible = false;
		try {
			try {
				wasAccessible = field.isAccessible();
				field.setAccessible(true);
				wasAbleToSetAccessible = true;
			} catch (Throwable e) {
				return null;
			}
			return field.get(instance);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			if(wasAbleToSetAccessible){
				field.setAccessible(wasAccessible);
			}
		}
		return null;
	}
	
	/**
	 * Returns cached list of fields for the class of the instance supplied in
	 * the map supplied. If no cached fields are discovered in the map, then the
	 * class is read and the list of appropriate fields is resolved before
	 * returning the cached fields.
	 * 
	 * @param map
	 * @param instanceClass
	 * @param isEntity
	 * @return
	 */
	private List<Field> getFields(Map<Class<?>, List<Field>> map, Class<?> instanceClass, boolean isEntity) {
		List<Field> fields = map.get(instanceClass);
		if(fields == null){
			readFields(instanceClass, isEntity);
			fields = getFields(map, instanceClass, isEntity);
		}
		return fields;
	}
	
	/**
	 * Evaluates an Entity class for equals/hashcode eligible fields and
	 * identity fields and stores in cache within local singleton instances.
	 * 
	 * @param instanceClass
	 * @param isEntity
	 */
	private synchronized void readFields(Class<?> instanceClass, boolean isEntity) {
		List<Field> equalsFields = new ArrayList<Field>();
		List<Field> identityFields = new ArrayList<Field>();
		Class<?> lClass = instanceClass;
		while(lClass != null){
			if(isEntity){
				for(Field f : lClass.getDeclaredFields()){
					if(!f.isSynthetic()){
						Identity identity = f.getAnnotation(Identity.class);
						if(!f.isAnnotationPresent(Transient.class) && (identity == null || identity.value())){
							equalsFields.add(f);
						}
						if(identity != null){
							identityFields.add(f);
						}
					}
				}
			}
			lClass = lClass.getSuperclass();
		}
		instance.equalsHashCodeFields.put(instanceClass, equalsFields);
		instance.identities.put(instanceClass, identityFields);
	}

	/**
	 * Returns true if instance supplied as argument is of a type that
	 * implements Entity or is a class that is equal to or a subtype of Entity.
	 * 
	 * @param instance
	 * @return
	 */
	public boolean isEntity(Object instance) {
		if(instance == null){
			return false;
		}
		Class<?> c;
		if(!(instance instanceof Class<?>)){
			c = instance.getClass();
		}else{
			c = (Class<?>)instance;
		}
		return LEHAware.class.isAssignableFrom(c);
	}	
	
	/**
	 * Local function object allowing differing string coercion to take place in
	 * the same general control logic.
	 * 
	 */
	private interface ToStringFunction {
		String toString(Object o, List<Object> evaluated);
	}
	
}
