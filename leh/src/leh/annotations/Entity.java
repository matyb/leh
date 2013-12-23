package leh.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * An annotation used to indicate to reflective equality and hashcode
 * implementations how to make assumptions about the annotated class's
 * fields.
 * 
 * When annotated with this type a class is assumed to contain only fields
 * relevant to a logical equality test by default. Fields annotated with @Identity
 * specify fields containing values to be omitted in a logical equality
 * test. @Identity attributes indicate how to identify this sibling of a
 * located possibly unequal instance to compare against. The absence of any @Identity
 * annotated fields in classes annotated by this type indicates that the
 * equals and hashcode implementations are symmetric in interpretation.
 */
public @interface Entity {
	
}
