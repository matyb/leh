package leh.util.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leh.example.food.FoodInventory;
import leh.example.person.Employee;
import leh.example.person.Person;
import leh.util.Entity;

import org.junit.Test;

import leh.example.Meh;

public class LEHWrapperTest {

	@Test
	public void testProxyLehOverridingEquals() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertEquals(leh.wrap(new Person()), leh.wrap(new Person()));
	}
	
	@Test
	public void testProxyLehOverridingEqualsHonorsOmissionsLikeIdentity() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		Person person1 = new Person();
		person1.setSsn("123");
		Person person2 = new Person();
		person2.setSsn("321");
		assertEquals(leh.wrap(person1), leh.wrap(person2));
	}
	
	@Test
	public void testProxyLehOverridingEqualsComplimentDifferentClass() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertNotEquals(
				leh.wrap(new Person()), 
				leh.wrap(new Person(){/*anonymous inner class is a different class*/}));
	}
	
	@Test
	public void testProxyLehOverridingEqualsComplimentDifferentValues() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		Person person1 = new Person();
		person1.setFirstName("Carl");
		Person person2 = new Person();
		person2.setFirstName("Winston");
		assertNotEquals(leh.wrap(person1), leh.wrap(person2));
	}
	
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
		Object meh2 = createAnonymous(meh1);
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
	public void testProxyLehOverridingHashCode() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertEquals(leh.wrap(new Person()).hashCode(), leh.wrap(new Person()).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingHashCodeComplimentDifferentClass() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertNotEquals(
				leh.wrap(new Person()).hashCode(), 
				leh.wrap(new FoodInventory(){/*anonymous inner class is a different class*/}).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingHashCodeComplimentDifferentValues() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		Person person1 = new Person();
		person1.setFirstName("Carl");
		Person person2 = new Person();
		person2.setFirstName("Winston");
		assertNotEquals(leh.wrap(person1).hashCode(), leh.wrap(person2).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingToString() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertEquals(leh.wrap(new Person()).toString(), leh.wrap(new Person()).toString());
	}
	
	@Test
	public void testProxyLehOverridingToStringComplimentDifferentClass() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		assertNotEquals(
				leh.wrap(new Person()).toString(), 
				leh.wrap(new FoodInventory(){/*anonymous inner class is a different class*/}).toString());
	}
	
	@Test
	public void testProxyLehOverridingToStringComplimentDifferentValues() throws Exception {
		LEHWrapper leh = LEHWrapper.getInstance();
		Person person1 = new Person();
		person1.setFirstName("Carl");
		Person person2 = new Person();
		person2.setFirstName("Winston");
		assertNotEquals(leh.wrap(person1).toString(), leh.wrap(person2).toString());
	}
	
	@Test 
	public void testProxyLehOverridingToStringCircularReference() throws Exception {
		Employee employee = new Employee();
		employee.setFirstName("employee");
		Employee manager = new Employee();
		manager.setFirstName("manager");
		manager.addReportee(manager, employee);
		assertEquals("Employee=[salary=0, manager=Employee=[salary=0, reportees={this=["+employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0], "
				   + "reportees={}, firstName=employee, gender=UNKNOWN, netWorth=0]", 
				LEHWrapper.getInstance().wrap(employee).toString());
	}
	
	@Test
	public void testToStringOnAnonymousInnerClassWrapped() throws Exception {
		Entity meh = new Entity(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		meh = LEHWrapper.getInstance().wrap(meh);
		assertEquals("Entity$1=[name=Meh, age=22, this$0=" + toString() + "]", meh.toString());
	}
	
	@Test
	public void testEqualsHashCodeAnonymousInnerClassWrapped() throws Exception {
		Entity meh = new Entity(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		Entity meh2 = createAnonymous(meh);
		assertNotEquals(meh, meh2);
		assertNotEquals(meh.hashCode(), meh2.hashCode());
		LEHWrapper leh = LEHWrapper.getInstance();
		meh = leh.wrap(meh);
		meh2 = leh.wrap(meh2);
		assertEquals(meh, meh2);
		assertEquals(meh.hashCode(), meh2.hashCode());
	}
	
	@Test
	public void testEqualsAnonymousInnerClassWrappedDoesNotExplicitlyInheritFromEntity() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		Object meh2 = createAnonymous(meh);
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
		Object meh2 = createAnonymous(meh);
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
		assertEquals("Object$1=[name=Meh, age=22, this$0=" + toString() + "]", meh.toString());
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
		Object meh2 = LEHWrapper.getInstance().wrap(createAnonymous(meh), LEHMethodHandlers.EQUALS_HASHCODE_METHODS);
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
		assertEquals("Not intercepted!", meh1.toString());
		Object meh2 = LEHWrapper.getInstance().wrap(createAnonymous(meh)); // implements all 3 methods
		assertEquals(meh1, meh2);
		assertEquals(meh2, meh1);
		assertEquals(meh1.hashCode(), meh2.hashCode());
	}
	
	private <T> T createAnonymous(T instance) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		@SuppressWarnings("unchecked")
		Constructor<T> cons = (Constructor<T>) instance.getClass().getDeclaredConstructor(getClass());
		boolean wasAccessible = cons.isAccessible();
		try{
			cons.setAccessible(true);
			return cons.newInstance(this);
		}finally{
			cons.setAccessible(wasAccessible);
		}
	}
	
}
