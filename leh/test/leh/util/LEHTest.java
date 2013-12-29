package leh.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.food.FoodInventory;
import leh.example.person.Employee;
import leh.example.person.Person;

import org.junit.Ignore;
import org.junit.Test;


public class LEHTest {

	@Test
	public void testClassesUnderTestDoNotImplementHashCodeNorEquals() throws Exception {
		assertNotEquals(new Person(), new Person());
		assertNotEquals(new Person().hashCode(), new Person().hashCode());
		assertNotEquals(new Employee(), new Employee());
		assertNotEquals(new Employee().hashCode(), new Employee().hashCode());
		assertNotEquals(new Food(), new Food());
		assertNotEquals(new Food().hashCode(), new Food().hashCode());
	}

	/**
	 * HashCode and Equals should be symmetric, this test accounts for
	 * maintaining symmetry between those methods conveniently. Also
	 * enforces java equals contract.
	 * 
	 * @param instance1
	 * @param instance2
	 * @param isEqual
	 */
	private void assertEqualsAndHashCodeSymmetry(Object instance1, Object instance2, boolean isEqual) {
		LEH leh = LEH.getInstance();
		// matches test expectation
		assertEquals(
				"Expected " + instance1 + " and " + instance2 + (isEqual ? "" : " NOT") + " to be equal.", 
				isEqual, leh.isEqual(instance2, instance1));
		assertEquals(
				"Expected " + instance1 + " and " + instance2 + (isEqual ? "" : " NOT") + " to hash the same.", 
				isEqual, leh.getHashCode(instance1) == leh.getHashCode(instance2));
		// symmetry
		assertEquals(
				"Order of arguments is significant, equals methods should be symmetric.",
				leh.isEqual(instance1, instance2), leh.isEqual(instance2, instance1));
		// null
		assertFalse(
				"Somehow \""+instance1+"\" was equal to null.",
				leh.isEqual(instance1, null));
		assertFalse(
				"Somehow \""+instance2+"\" was equal to null.",
				leh.isEqual(instance2, null));
		assertFalse(
				"Somehow \""+instance1+"\" was equal to null.",
				leh.isEqual(null, instance1));
		assertFalse(
				"Somehow \""+instance2+"\" was equal to null.",
				leh.isEqual(null, instance2));
		// reflexive
		assertTrue(
				"Somehow \""+instance1+"\" was not equal to itself.",
				leh.isEqual(instance1, instance1));
		assertTrue(
				"Somehow \""+instance2+"\" was not equal to itself.",
				leh.isEqual(instance2, instance2));
	}
	
	@Test
	public void testEqualAllNull() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
	}
	
	@Test
	public void testEqualSubclass() throws Exception {
		Person person1 = new Employee();
		Person person2 = new Employee();
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
	}
	
	@Test
	public void testNotEqualSubclass() throws Exception {
		Person person1 = new Person();
		Person person2 = new Employee();
		assertEqualsAndHashCodeSymmetry(person1, person2, false);
	}
	
	@Test
	public void testEqualDespiteIdSubclass() throws Exception {
		Employee employee1 = new Employee();
		employee1.setEmployeeId("1");
		Employee employee2 = new Employee();
		employee2.setEmployeeId("2");
		// they are equal, but their id is different, id is part of identity
		assertEqualsAndHashCodeSymmetry(employee1, employee2, true);
	}
	
	@Test
	public void testEqualWithSameListElement() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		Food food1 = new Food();
		Food food2 = new Food();
		food1.setType(FoodType.PIZZA);
		food2.setType(FoodType.PIZZA);
		person1.setFavoriteFoods(Arrays.asList(food1));
		person2.setFavoriteFoods(Arrays.asList(food2));
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
	}
	
	@Test
	public void testEqualWithReferenceToOtherEqualEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
	}

	@Test
	public void testEqualWithMapEntry() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		manager.addReportee(manager, employee1);
		manager.addReportee(manager, employee2);
		assertEqualsAndHashCodeSymmetry(employee1, employee2, true);
	}
	
	@Test
	public void testNotEqualWithMapEntry() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee2);
		if(LEH.getInstance().getHashCode(employee1) == LEH.getInstance().getHashCode(employee2)){
			System.out.println();
		}
		assertEqualsAndHashCodeSymmetry(employee1, employee2, false);
	}
	
	@Test
	public void testNotEqualWithMapEntryCompliment() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee1);
		assertEqualsAndHashCodeSymmetry(employee1, employee2, false);
	}
	
	@Test
	public void testNotEqualWithListElement() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		Food food1 = new Food();
		Food food2 = new Food();
		food1.setType(FoodType.PIZZA);
		food2.setType(FoodType.TACO);
		person1.setFavoriteFoods(Arrays.asList(food1));
		person2.setFavoriteFoods(Arrays.asList(food2));
		assertEqualsAndHashCodeSymmetry(person1, person2, false);
	}
	
	@Test
	public void testNotEqualWithSameListElementDifferentCount() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		Food food1 = new Food();
		Food food2 = new Food();
		food1.setType(FoodType.PIZZA);
		food2.setType(FoodType.PIZZA);
		person1.setFavoriteFoods(Arrays.asList(food1));
		person2.setFavoriteFoods(Arrays.asList(food2, food1));
		assertEqualsAndHashCodeSymmetry(person1, person2, false);
	}
	
	@Test
	public void testNotEqualWithSameListElementDifferentCountCompliment() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		Food food1 = new Food();
		Food food2 = new Food();
		food1.setType(FoodType.PIZZA);
		food2.setType(FoodType.PIZZA);
		person1.setFavoriteFoods(Arrays.asList(food1));
		person2.setFavoriteFoods(Arrays.asList(food2, food1));
		assertEqualsAndHashCodeSymmetry(person1, person2, false);
	}
	
	@Test
	public void testNotEqualWithReferenceToOtherDifferentEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		person2.getSpouse().setBirthDate(new Date());
		assertEqualsAndHashCodeSymmetry(person1, person2, false);
	}
	
	@Test
	public void testEmptyEntityToString() throws Exception {
		Person person = new Person();
		person.setSsn("123456789");
		assertEquals("Person=[ids={ssn=123456789}, gender=UNKNOWN, netWorth=0]", LEH.getInstance().getToString(person));
	}
	
	@Test
	public void testPopulatedEntityToString() throws Exception {
		Employee person = new Employee();
		person.setSsn("123456789");
		Date birthDate = new Date();
		person.setBirthDate(birthDate);
		Person spouse = new Person();
		Food food = new Food();
		food.setType(FoodType.PIZZA);
		spouse.setFavoriteFoods(Arrays.asList(food));
		spouse.setFirstName("Brian");
		person.setSpouse(spouse);
		assertEquals("Employee=[ids={ssn=123456789}, salary=0, reportees={}, birthDate="+birthDate+", gender=UNKNOWN, netWorth=0, "
					+"spouse=Person=[firstName=Brian, favoriteFoods=[Food=[calories=0, sodium=0, type=PIZZA]], gender=UNKNOWN, netWorth=0]]", 
					LEH.getInstance().getToString(person));
	}
	
	@Test 
	public void testCircularReferenceToString() throws Exception {
		Employee employee = new Employee();
		employee.setFirstName("employee");
		Employee manager = new Employee();
		manager.setFirstName("manager");
		manager.addReportee(manager, employee);
		assertEquals("Employee=[salary=0, manager=Employee=[salary=0, reportees={this=["+employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0], "
				   + "reportees={}, firstName=employee, gender=UNKNOWN, netWorth=0]", 
				LEH.getInstance().getToString(employee));
	}

	// Test for infinite recursion when reused by overriding equals, hashcode, toString
	
	@Test
	public void testReusingLehOverridingEquals() throws Exception {
		assertEquals(new FoodInventory(), new FoodInventory());
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentClass() throws Exception {
		assertNotEquals(
				new FoodInventory(), 
				new FoodInventory(){/*anonymous inner class is a different class*/});
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentValues() throws Exception {
		FoodInventory foodInventory1 = new FoodInventory();
		foodInventory1.setCalories(0);
		FoodInventory foodInventory2 = new FoodInventory();
		foodInventory2.setCalories(100);
		assertNotEquals(foodInventory1, foodInventory2);
	}
	
	@Test
	public void testReusingLehOverridingHashCode() throws Exception {
		assertEquals(new FoodInventory().hashCode(), new FoodInventory().hashCode());
	}
	
	@Test
	public void testReusingLehOverridingHashCodeComplimentDifferentClass() throws Exception {
		assertNotEquals(
				new FoodInventory().hashCode(), 
				new FoodInventory(){/*anonymous inner class is a different class*/}.hashCode());
	}
	
	@Test
	public void testReusingLehOverridingHashCodeComplimentDifferentValues() throws Exception {
		FoodInventory foodInventory1 = new FoodInventory();
		foodInventory1.setCalories(0);
		FoodInventory foodInventory2 = new FoodInventory();
		foodInventory2.setCalories(100);
		assertNotEquals(foodInventory1.hashCode(), foodInventory2.hashCode());
	}
	
	@Test
	public void testReusingLehOverridingToString() throws Exception {
		assertEquals(new FoodInventory().toString(), new FoodInventory().toString());
	}
	
	@Test
	public void testReusingLehOverridingToStringComplimentDifferentClass() throws Exception {
		assertNotEquals(
				new FoodInventory().toString(), 
				new FoodInventory(){/*anonymous inner class is a different class*/}.toString());
	}
	
	@Test
	public void testReusingLehOverridingToStringComplimentDifferentValues() throws Exception {
		FoodInventory foodInventory1 = new FoodInventory();
		foodInventory1.setCalories(0);
		FoodInventory foodInventory2 = new FoodInventory();
		foodInventory2.setCalories(100);
		assertNotEquals(foodInventory1.toString(), foodInventory2.toString());
	}

	@Test
	public void testProxyLehOverridingEquals() throws Exception {
		LEH leh = LEH.getInstance();
		assertEquals(leh.wrap(new Person()), leh.wrap(new Person()));
	}
	
	@Test
	public void testProxyLehOverridingEqualsHonorsOmissionsLikeIdentity() throws Exception {
		LEH leh = LEH.getInstance();
		Person person1 = new Person();
		person1.setSsn("123");
		Person person2 = new Person();
		person2.setSsn("321");
		assertEquals(leh.wrap(person1), leh.wrap(person2));
	}
	
	@Test
	public void testProxyLehOverridingEqualsComplimentDifferentClass() throws Exception {
		LEH leh = LEH.getInstance();
		assertNotEquals(
				leh.wrap(new Person()), 
				leh.wrap(new Person(){/*anonymous inner class is a different class*/}));
	}
	
	@Test
	public void testProxyLehOverridingEqualsComplimentDifferentValues() throws Exception {
		LEH leh = LEH.getInstance();
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
		Meh wrappedMeh = LEH.getInstance().wrap(meh, Arrays.asList((MethodHandler)new MethodHandler("meh", new Class[]{String.class}){
			Object invoke(Object instance, Object... args){
				return Arrays.asList(instance, Arrays.asList(args));
			}
		}), Meh.class);
		assertEquals(meh.heh(), wrappedMeh.heh());
		assertEquals(Arrays.asList(wrappedMeh, Arrays.asList("Hai Danny")), wrappedMeh.meh("Hai Danny"));
	}
	
	interface Meh {
		Object meh(String arg);
		Object heh();
	}
	
	@Test
	public void testProxyLehOverridingHashCode() throws Exception {
		LEH leh = LEH.getInstance();
		assertEquals(leh.wrap(new Person()).hashCode(), leh.wrap(new Person()).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingHashCodeComplimentDifferentClass() throws Exception {
		LEH leh = LEH.getInstance();
		assertNotEquals(
				leh.wrap(new Person()).hashCode(), 
				leh.wrap(new FoodInventory(){/*anonymous inner class is a different class*/}).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingHashCodeComplimentDifferentValues() throws Exception {
		LEH leh = LEH.getInstance();
		Person person1 = new Person();
		person1.setFirstName("Carl");
		Person person2 = new Person();
		person2.setFirstName("Winston");
		assertNotEquals(leh.wrap(person1).hashCode(), leh.wrap(person2).hashCode());
	}
	
	@Test
	public void testProxyLehOverridingToString() throws Exception {
		LEH leh = LEH.getInstance();
		assertEquals(leh.wrap(new Person()).toString(), leh.wrap(new Person()).toString());
	}
	
	@Test
	public void testProxyLehOverridingToStringComplimentDifferentClass() throws Exception {
		LEH leh = LEH.getInstance();
		assertNotEquals(
				leh.wrap(new Person()).toString(), 
				leh.wrap(new FoodInventory(){/*anonymous inner class is a different class*/}).toString());
	}
	
	@Test
	public void testProxyLehOverridingToStringComplimentDifferentValues() throws Exception {
		LEH leh = LEH.getInstance();
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
				LEH.getInstance().wrap(employee).toString());
	}
	
	@Test @Ignore("Just for curiosity's sake, and to debug that caching is working")
	public void test100000Times() throws Exception {
		Employee person = new Employee();
		person.setSsn("123456789");
		Date birthDate = new Date();
		person.setBirthDate(birthDate);
		Person spouse = new Person();
		Food food = new Food();
		food.setType(FoodType.PIZZA);
		spouse.setFavoriteFoods(Arrays.asList(food));
		spouse.setFirstName("Brian");
		person.setSpouse(spouse);
		LEH leh = LEH.getInstance();
		int runs = 100000;
		List<String> accumulator = new ArrayList<String>(runs);
		for(int i = 0; i < runs; i++){
			accumulator.add(leh.getToString(leh));
		}
	}
	
}
