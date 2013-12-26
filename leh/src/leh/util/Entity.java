package leh.util;

/**
 * A marker interface used to indicate to reflective equality and hashcode
 * implementations how to make assumptions about the annotated class's
 * fields.
 * 
 * When implementing this type a class is assumed to contain only fields
 * relevant to a logical equality test by default. Fields annotated with @Identity
 * specify fields containing values to be omitted in a logical equality
 * test. @Identity attributes indicate how to identify this sibling of a
 * located possibly unequal instance to compare against. The absence of any @Identity
 * annotated fields in classes annotated by this type indicates that the
 * equals and hashcode implementations are symmetric in interpretation.
 */
public interface Entity {
	
}
