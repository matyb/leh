package leh.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * An annotation used to indicate to reflective equality and hashcode
 * implementations what fields comprise an object's identity. Fields annotated with
 * this type will be assumed by default not to participate in equality tests. 
 * @Identity(true) may be used instead to indicate the annotated attribute
 * can be used to find the identity, but is not to be considered part of an 
 * equality test. 
 */
public @interface Identity {

	/**
	 * Does this field participate in equality tests? Default is false.
	 * 
	 * @return boolean indication as to whether the field is considered part of
	 *         a logical equality test.
	 */
	boolean value() default false;
	
}
