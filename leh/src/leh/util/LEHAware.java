package leh.util;

/**
 * A marker interface used to indicate to reflective equality and hashcode
 * implementations how to make assumptions about the annotated class's fields.
 * 
 * When implementing this type a class is assumed to contain only fields
 * relevant to a logical equality test by default when it is evaluated within
 * leh.util.LEH methods.
 * 
 * Fields annotated with @Identity specify fields comprising an object's
 * identity (primary keys) and are by default ignored in equality and hashcode
 * tests within leh.util.LEH methods.
 * 
 * @Identity attributes indicate how to identify a sibling of an evaluated
 * possibly !java.lang.Object.equal instance to compare against (ie wouldn't be
 * equal by standard java equals implementation's definition, but may be equal
 * if identity fields were ignored) when discovered in executing LEH methods.
 * 
 * @Transient attributes indicate a field is not significant to an LEHAware
 * instance's equality or hashcode. Examples include fields that are not
 * persisted to a database, or vary with time but not a notion of an object's
 * equality or hashcode. Does not imply identity.
 * 
 * @see leh.util.LEH
 * @see leh.util.annotations.Identity
 * @see leh.util.annotations.Transient
 */
public interface LEHAware {
	
}
