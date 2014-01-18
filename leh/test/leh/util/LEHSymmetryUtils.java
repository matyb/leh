package leh.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

/**
 * Verifies that equals, hashCode and toString are implemented symmetrically.
 * Once exception - toString() contains @Identity fields, whereas equals and hashcode
 * do not consider them by default, and so a varargs toString argument is available
 * to specify when toString values differ.
 */
public class LEHSymmetryUtils {

	/**
	 * Both the LEH singleton instance and Wrapper proxy are tested
	 * for parity and cohesion. Testing for additional LEHDelegates
	 * can be accomplished by adding them to this list.
	 */
	private static final List<LEHDelegate> lehInstances = Arrays.asList(
			LEH.getInstance(), LEHWrapperDelegate.getInstance());
	
	/**
	 * Verifies that equals, hashCode and toString are implemented
	 * symmetrically. Once exception - toString() contains @Identity fields,
	 * whereas equals and hashcode do not consider them by default, and so a
	 * varargs toString argument is available to specify when toString values
	 * differ.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param isEqual
	 * @param toStrings
	 *            Only necessary when they differ, should only be because of
	 *            identity fields contain differing values.
	 * @throws Exception
	 */
	public static void verify(Object instance1, Object instance2,
			boolean isEqual, String...toStrings) throws Exception {
		for(LEHDelegate delegate : lehInstances){
			verify(instance1, instance2, isEqual, delegate, toStrings);
		}
	}
	
	/**
	 * Verifies that equals, hashCode and toString are implemented
	 * symmetrically. Once exception - toString() contains @Identity fields,
	 * whereas equals and hashcode do not consider them by default, and so a
	 * varargs toString argument is available to specify when toString values
	 * differ.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param isEqual
	 * @param delegate
	 * @param toStrings
	 *            Only necessary when they differ, should only be because of
	 *            identity fields contain differing values.
	 * @throws Exception
	 */
	public static void verify(Object instance1, Object instance2,
			boolean isEqual, LEHDelegate delegate, String...toStrings) throws Exception {
		verifyMatchingEqualityHashCodeToString(instance1, instance2, isEqual, delegate, toStrings);
		verifyToString(instance1, instance2, isEqual, delegate, toStrings);
		// symmetry
		assertEquals(
				"Order of arguments is significant, equals methods should be symmetric.",
				delegate.isEqual(instance1, instance2), delegate.isEqual(instance2, instance1));
		verifyNull(instance1, instance2, delegate);
		verifyReflexion(instance1, instance2, delegate);
	}

	/**
	 * Ensure toString on wrapped instance and LEH.getToString return the
	 * expected string.
	 * 
	 * @param instance1
	 * @param toString
	 */
	public static void veryifyToString(Object instance1, String toString) {
		assertEquals(toString, LEH.getInstance().getToString(instance1));
		assertEquals(toString, LEHWrapperDelegate.getInstance().getToString(instance1));
	}
	
	/**
	 * Ensure toString is equal when equals/hashcode indicate equality, except
	 * for cases of identity which are ignored in equals/hashcode but in
	 * toString.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param isEqual
	 * @param delegate
	 * @param toStrings
	 */
	private static void verifyToString(Object instance1, Object instance2,
			boolean isEqual, LEHDelegate delegate, String... toStrings) {
		if(toStrings != null && toStrings.length == 2){
			assertEquals(toStrings[0], delegate.getToString(instance1));
			assertEquals(toStrings[1], delegate.getToString(instance2));
		}else{
			assertEquals(
					"Expected the strings " + instance1 + " and " + instance2 + (isEqual ? "" : " NOT") + " to be equal.", 
					isEqual, delegate.getToString(instance2).equals(delegate.getToString(instance1)));
		}
	}

	private static void verifyReflexion(Object instance1, Object instance2,
			LEHDelegate delegate) {
		assertTrue(
				"Somehow \""+instance1+"\" was not equal to itself.",
				delegate.isEqual(instance1, instance1));
		assertTrue(
				"Somehow \""+instance2+"\" was not equal to itself.",
				delegate.isEqual(instance2, instance2));
	}

	private static void verifyNull(Object instance1, Object instance2,
			LEHDelegate delegate) {
		assertFalse(
				"Somehow \""+instance1+"\" was equal to null.",
				delegate.isEqual(instance1, null));
		assertFalse(
				"Somehow \""+instance2+"\" was equal to null.",
				delegate.isEqual(instance2, null));
		assertFalse(
				"Somehow \""+instance1+"\" was equal to null.",
				delegate.isEqual(null, instance1));
		assertFalse(
				"Somehow \""+instance2+"\" was equal to null.",
				delegate.isEqual(null, instance2));
	}

	private static void verifyMatchingEqualityHashCodeToString(
			Object instance1, Object instance2, boolean isEqual,
			LEHDelegate delegate, String... toStrings) {
		// matches test expectation
		assertEquals(
				"Expected " + instance1 + " and " + instance2 + (isEqual ? "" : " NOT") + " to be equal.", 
				isEqual, delegate.isEqual(instance2, instance1));
		assertEquals(
				"Expected " + instance1 + " and " + instance2 + (isEqual ? "" : " NOT") + " to hash the same.", 
				isEqual, delegate.getHashCode(instance1) == delegate.getHashCode(instance2));
	}
	
	
	
}
