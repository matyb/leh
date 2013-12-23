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

import leh.annotations.Entity;
import leh.annotations.Identity;
import leh.annotations.Transient;

public class LogicalEqualsHashCode {

	/**
	 * Private instance, for use internally.
	 */
	private static LogicalEqualsHashCode instance = new LogicalEqualsHashCode();
	
	/**
	 * State of private mutable instance is wrapped to prevent mutation when
	 * exposed externally.
	 */
	private static LogicalEqualsHashCode immutableInstance = new LogicalEqualsHashCode(Collections.unmodifiableMap(instance.identities), 
																					   Collections.unmodifiableMap(instance.equalsHashCodeFields));
	
	/**
	 * List of @Identity annotated fields as discovered via reflection, by
	 * class. Acts as local cache mitigating performance hit associated with
	 * repetitive reflective discovery.
	 */
	private final Map<Class<?>, List<Field>> identities;
	
	/**
	 * List of @Identity annotated fields as discovered via reflection, by
	 * class. Acts as local cache mitigating performance hit associated with
	 * repetitive reflective discovery.
	 */
	private final Map<Class<?>, List<Field>> equalsHashCodeFields;
	
	/**
	 * Constructor used internally for mutable reference to singleton instance.
	 */
	private LogicalEqualsHashCode(){
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
	public LogicalEqualsHashCode(Map<Class<?>, List<Field>> identities,
								 Map<Class<?>, List<Field>> logicallyEqualFields) {
		this.identities = identities;
		this.equalsHashCodeFields = logicallyEqualFields;
	}

	/**
	 * Returns a referentially transparent (immutable) version of the singleton.
	 * @return
	 */
	public static LogicalEqualsHashCode getInstance(){
		return immutableInstance;
	}
	
	/**
	 * To return true the statements: 
	 *   instance1 == instance2;
	 *   instance1.equals(instace2); 
	 * evaluate to true; or if both instances are of
	 * types that are annotated with @Entity, than fields may be tested by
	 * reflection for equality. Values found to be of types annotated with @Entity
	 * in reflectively testing for equality enter the same test.
	 * 
	 * @see leh.annotations.Entity
	 * @see leh.annotations.Identity
	 * @param instance1
	 * @param instance2
	 * @return
	 */
	public boolean isEqual(Object instance1, Object instance2){
		return doFieldsMatch(instance1, instance2, equalsHashCodeFields);
	}
	
	/**
	 * To return true both instances must be of identical types that are annotated with
	 * @Entity, and fields annotated with @Identity must have equal values in
	 * both instances. Values found to be of types annotated with @Entity
	 * in reflectively testing for identity enter the same test recursively.
	 * 
	 * @see leh.annotations.Entity
	 * @see leh.annotations.Identity
	 * @param instance1
	 * @param instance2
	 * @return
	 */
	public boolean isIdentity(Object instance1, Object instance2){
		return doFieldsMatch(instance1, instance2, identities);
	}

	/**
	 * Returns true when the values in both instances for each field found in
	 * supplied mappings are equal.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param fields
	 * @return
	 */
	private boolean doFieldsMatch(Object instance1, Object instance2, Map<Class<?>, List<Field>> fields) {
		if(instance1 == instance2){
			return true;  
		}
		if(instance1 == null || instance2 == null){
			return false;
		}
		if(instance1 instanceof Iterable<?> && instance2 instanceof Iterable<?>){
			return getIterableEquals(instance1, instance2, fields);
		}
		if(instance1 instanceof Map && instance2 instanceof Map){ 
			return getMapEquals(instance1, instance2, fields);
		}
		if(isEntity(instance1)){
			if(instance1.getClass() == instance2.getClass()){
				for(Field f : getFields(fields, instance1.getClass())){
					if(!isEqual(getValue(f, instance1), getValue(f, instance2))){
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

	/**
	 * Used in place of Map equality tests in map access. Map equality tests invoke equals, which may not
	 * represent logical equality. 
	 * 
	 * TODO: Make performant. Performance is lousy on large maps (O^N).
	 * 
	 * @param instance1
	 * @param instance2
	 * @param fields
	 * @return
	 */
	private boolean getMapEquals(Object instance1, Object instance2, Map<Class<?>, List<Field>> fields) {
		if(((Map<?,?>)instance1).size() != ((Map<?,?>)instance2).size()){
			return false;
		}
		for(Entry<?, ?> o1 : ((Map<?,?>)instance1).entrySet()){
			boolean foundValue = false;
			Object value1 = o1.getValue(), value2 = null;
			for(Entry<?, ?> o2 : ((Map<?,?>)instance2).entrySet()){
				if(doFieldsMatch(o1.getKey(), o2.getKey(), fields)){
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
	 * TODO: Make performant. Performance is lousy on large collections (O^N).
	 * 
	 * @param instance1
	 * @param instance2
	 * @param fields
	 * @return
	 */
	private boolean getIterableEquals(Object instance1, Object instance2, Map<Class<?>, List<Field>> fields) {
		if((instance1 instanceof Collection && instance2 instanceof Collection && 
				((Collection<?>)instance1).size() != ((Collection<?>)instance2).size()) ||
			instance1 instanceof Map && instance2 instanceof Map && 
				((Map<?,?>)instance1).size() != ((Map<?,?>)instance2).size()){
			return false;
		}
		for(Object o1 : (Iterable<?>)instance1){
			boolean foundEqual = false;
			for(Object o2 : (Iterable<?>)instance2){
				if(doFieldsMatch(o1, o2, fields)){
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
	 * Reflectively access fields and accumulate hashcode values as implemented
	 * specifically, implied by @Entity annotation, or assumed via inheritance.
	 * 
	 * @param o
	 * @return
	 */
	public int getHashCode(Object instance){
		if(isEntity(instance)){
			int hashCode = 0;
			for(Field f : getFields(equalsHashCodeFields, instance.getClass())){
				Object value = getValue(f, instance);
				if(value instanceof Iterable<?>){
					for(Object o : (Iterable<?>)value){
						hashCode += getHashCode(o);
					}
				}else if(value instanceof Map){
					for(Entry<?,?> o : ((Map<?,?>)value).entrySet()){
						hashCode += getHashCode(o.getKey()) + getHashCode(o.getValue());
					}
				} else{
					hashCode += getHashCode(value);
				}
			}
			return hashCode;
		}
		return instance == null ? 0 : instance.hashCode();
	}
	
	/**
	 * Reflectively access fields and accumulate toString values as implemented
	 * specifically, implied by @Entity annotation, or assumed via inheritance.
	 * 
	 * @param o
	 * @return
	 */
	public String getToString(Object instance){
		if(isEntity(instance)){
			String toString = instance.getClass().getSimpleName() + "=[";
			String seperator = ", ";
			List<Field> fields = getFields(identities, instance.getClass());
			String idsString = getToString("ids={", instance, seperator, fields, "}");
			toString += idsString;
			fields = getFields(equalsHashCodeFields, instance.getClass());
			if(fields.size() > 0){
				if(idsString.length() > 0){
					toString += seperator;
				}
				toString += getToString(instance, seperator, fields);
			}
			return toString + "]";
		}
		return String.valueOf(instance);
	}

	/**
	 * Reflect over the supplied fields and accumulate a string representing the state found in the instance's values.
	 * @param instance
	 * @param seperator
	 * @param fields
	 * @return
	 */
	private String getToString(Object instance, String seperator, List<Field> fields) {
		Iterator<Entry<String, String>> valueByFieldNameIterator = map(instance, fields, seperator).entrySet().iterator();
		String toString = "";
		if(valueByFieldNameIterator.hasNext()){
			while(valueByFieldNameIterator.hasNext()){
				Entry<String, String> valueByFieldName = valueByFieldNameIterator.next();
				toString += valueByFieldName.getKey() + "=" + valueByFieldName.getValue();
				if(valueByFieldNameIterator.hasNext()){
					toString += seperator;
				}
			}
		}
		return toString;
	}
	
	private String getToString(String prepend, Object instance, String seperator, List<Field> fields, String append) {
		String toString = getToString(instance, seperator, fields);
		return toString.length() > 0 ? prepend + toString + append : toString;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> map(Object instance, List<Field> fields, String seperator) {
		Map<String, String> map = fields.size() > 0 ? new LinkedHashMap<String, String>() : Collections.<String, String>emptyMap();
		for(Field field : fields){
			Object value = getValue(field, instance);
			if(value != null){
				if(value instanceof Iterable<?>){
					if(value instanceof List<?> && ((List<?>)value).size() > 0){
						map.put(field.getName(), getToCollectionString("[", (List<?>)value, "]", seperator));
					}else if(value instanceof Set && ((Set<?>)value).size() > 0){
						map.put(field.getName(), getToCollectionString("#{", (Set<?>)value, "}", seperator));
					}else {
						if(value instanceof Collection<?> && ((Collection<?>)value).size() > 0){
							map.put(field.getName(), getToCollectionString("(", (Iterable<?>)value, ")", seperator));
						}else{
							map.put(field.getName(), getToCollectionString("(", (Iterable<?>)value, ")", seperator));
						}
					}
				}else if(value instanceof Map<?,?> && ((Map<?,?>) value).size() > 0){
					map.put(field.getName(), getToCollectionString("{", (Map<Object, Object>)value, "}", seperator));
				}else{
					map.put(field.getName(), getToString(value));
				}
			}
		}
		return map;
	}

	private String getToCollectionString(String prepend, Iterable<?> value, String append, String seperator) {
		Iterator<?> valueIterator = value.iterator();
		String toString = "";
		if(valueIterator.hasNext()){
			toString = prepend;
			while(valueIterator.hasNext()){
				toString += getToString(valueIterator.next());
				if(valueIterator.hasNext()){
					toString += seperator;
				}
			}
			toString += append;
		}
		return toString;
	}
	
	private String getToCollectionString(String prepend, Map<Object, Object> valueMap, String append, String seperator) {
		Iterator<Entry<Object, Object>> values = valueMap.entrySet().iterator();
		String toString = "";
		if(values.hasNext()){
			toString = prepend;
			while(values.hasNext()){
				Entry<Object, Object> entry = values.next();
				toString += getToString(entry.getKey()) + "=" + getToString(entry.getValue());
				if(values.hasNext()){
					toString += seperator;
				}
			}
			toString += append;
		}
		return toString;
	}

	private Object getValue(Field field, Object instance) {
		if(field == null || instance == null){
			return null;
		}
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

	private List<Field> getFields(Map<Class<?>, List<Field>> map, Class<?> instanceClass) {
		List<Field> fields = map.get(instanceClass);
		if(fields == null){
			readFields(instanceClass);
			fields = getFields(map, instanceClass);
		}
		return fields;
	}
	
	private void readFields(Class<?> instanceClass) {
		List<Field> equalsFields = new ArrayList<Field>();
		List<Field> identityFields = new ArrayList<Field>();
		Class<?> lClass = instanceClass;
		while(lClass != null){
			if(lClass.isAnnotationPresent(Entity.class)){
				for(Field f : lClass.getDeclaredFields()){
					Identity identity = f.getAnnotation(Identity.class);
					if(!f.isAnnotationPresent(Transient.class) && (identity == null || identity.value())){
						equalsFields.add(f);
					}else{
						identityFields.add(f);
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
	 * @param instance
	 * @return
	 */
	public boolean isEntity(Object instance) {
		if(instance == null){
			return false;
		}
		Class<?> instanceClass = instance instanceof Class ? (Class<?>)instance : instance.getClass();
		return instanceClass.isAnnotationPresent(Entity.class) ? true : isEntity(instanceClass.getSuperclass());
	}
	
}
