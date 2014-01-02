package leh.util.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leh.example.Meh;
import leh.util.ReflectionUtils;

import org.junit.Test;

public class LEHWrapperTest {
	
	@Test
	public void testWrapHandlerAssociation() throws Exception {
		Meh meh = new Meh(){
			@Override
			public Object meh(String arg) {
				fail();
				return null;
			}
			@Override
			public Object heh() {
				return "OK Bai";
			}
		};
		Meh wrappedMeh = LEHWrapper.getInstance().wrap(meh, Arrays.asList((MethodHandler)new MethodHandler("meh", new Class[]{String.class}){
			public Object invoke(Object instance, Object... args){
				return Arrays.asList(instance, Arrays.asList(args));
			}
		}), Meh.class);
		assertEquals(meh.heh(), wrappedMeh.heh());
		assertEquals(Arrays.asList(wrappedMeh, Arrays.asList("Hai Danny")), wrappedMeh.meh("Hai Danny"));
	}
	
	// Too like an integration test?
	@Test
	public void testImplementingInterfaceAtRuntimeViaWrapImplicitlyAddsEntity() throws Exception {
		final Object meh1 = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		List<MethodHandler> handlers = new ArrayList<MethodHandler>(LEHMethodHandlers.ALL_LEH_METHODS);
		handlers.add(new MethodHandler("meh", new Class[]{String.class}) {
			public Object invoke(Object instance, Object... args) {
				assertEquals(meh1, instance);
				return "Hai " + args[0];
			}
		});
		handlers.add(new MethodHandler("heh", new Class[0]) {
			public Object invoke(Object instance, Object... args) {
				assertEquals(meh1, instance);
				return "OK Bai";
			}
		});
		Meh actualMeh1 = LEHWrapper.getInstance().wrap(meh1, handlers, Meh.class);
		Object meh2 = ReflectionUtils.createAnonymous(meh1, this);
		Meh actualMeh2 = LEHWrapper.getInstance().wrap(meh2, handlers, Meh.class);
		// base object is not the same instance nor does it implement concrete equals/hashcode/tostring 
		assertNotEquals(meh1, meh2);
		assertNotEquals(meh1.hashCode(), meh2.hashCode());
		assertNotEquals(meh1.toString(), meh2.toString());
		assertNotSame(actualMeh1, actualMeh2);
		// wrapped instances are however equal in equality, hashcode and tostring
		assertEquals(actualMeh1, actualMeh2);
		assertEquals(actualMeh1.hashCode(), actualMeh2.hashCode());
		assertEquals(actualMeh1.toString(), actualMeh2.toString());
		// if a value changes...
		Field nameField = meh2.getClass().getDeclaredField("name");
		nameField.setAccessible(true);
		nameField.set(meh1, "Something New");
		// ... then equality, hashcode, and tostring are no longer the same 
		assertNotEquals(actualMeh1, actualMeh2);
		assertNotEquals(actualMeh1.hashCode(), actualMeh2.hashCode());
		assertNotEquals(actualMeh1.toString(), actualMeh2.toString());
	}
	
	@Test
	public void testEqualsAnonymousInnerClassWrappedDoesNotExplicitlyInheritFromEntity() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		Object meh2 = ReflectionUtils.createAnonymous(meh, this);
		LEHWrapper leh = LEHWrapper.getInstance();
		assertNotEquals(meh, meh2);
		meh = leh.wrap(meh);
		meh2 = leh.wrap(meh2);
		assertEquals(meh, meh2);
	}
	
	@Test
	public void testHashCodeAnonymousInnerClassWrappedDoesNotExplicitlyInheritFromEntity() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		Object meh2 = ReflectionUtils.createAnonymous(meh, this);
		LEHWrapper leh = LEHWrapper.getInstance();
		assertNotEquals(meh.hashCode(), meh2.hashCode());
		meh = leh.wrap(meh);
		meh2 = leh.wrap(meh2);
		assertEquals(meh.hashCode(), meh2.hashCode());
	}
	
	@Test
	public void testToStringOnAnonymousInnerClassWrappedDoesNotExplicitlyInheritFromEntity() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		meh = LEHWrapper.getInstance().wrap(meh);
		assertEquals("Object$1=[name=Meh, age=22]", meh.toString());
	}
	
	@Test
	public void testSelectiveOverrideJustEqualsHashCode() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
			@Override
			public String toString() {
				return "Not intercepted!";
			}
		};
		Object meh1 = LEHWrapper.getInstance().wrap(meh, LEHMethodHandlers.EQUALS_HASHCODE_METHODS);
		assertEquals("Not intercepted!", meh1.toString());
		Object meh2 = LEHWrapper.getInstance().wrap(ReflectionUtils.createAnonymous(meh, this), LEHMethodHandlers.EQUALS_HASHCODE_METHODS);
		assertEquals(meh1, meh2);
		assertEquals(meh1.hashCode(), meh2.hashCode());
	}
	
	@Test
	public void testSelectiveOverrideJustEqualsHashCodeEqualsSameStateWithDifferentOverrides() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
			@Override
			public String toString() {
				return "Not intercepted!";
			}
		};
		Object meh1 = LEHWrapper.getInstance().wrap(meh, LEHMethodHandlers.EQUALS_HASHCODE_METHODS);
		assertEquals(meh.toString(), meh1.toString());
		Object meh2 = LEHWrapper.getInstance().wrap(ReflectionUtils.createAnonymous(meh, this)); // implements all 3 methods
		assertEquals(meh1, meh2);
		assertEquals(meh2, meh1);
		assertEquals(meh1.hashCode(), meh2.hashCode());
	}
	
}
