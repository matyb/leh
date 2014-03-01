package leh.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Date;

import leh.example.SelfReferencingExample;
import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.food.FoodInventory;
import leh.example.person.Employee;
import leh.example.person.Person;

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
	
	@Test
	public void testAllNull() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		LEHAssertions.verify(person1, person2, true);
	}
	
	@Test
	public void testSubclass() throws Exception {
		Person person1 = new Employee();
		Person person2 = new Employee();
		LEHAssertions.verify(person1, person2, true);
	}
	
	@Test
	public void testDifferentSubclasses() throws Exception {
		Person person1 = new Person();
		Person person2 = new Employee();
		LEHAssertions.verify(person1, person2, false);
	}
	
	@Test
	public void testIgnoresFieldsWhereIdentityAnnotationIsPresentAndValueIsDefault() throws Exception {
		Employee employee1 = new Employee();
		employee1.setEmployeeId("1");
		Employee employee2 = new Employee();
		employee2.setEmployeeId("2");
		// they are equal, but their id is different, id is part of identity
		LEHAssertions.verify(
				employee1, employee2, true,
				"Employee=[ids={employeeId=1}, salary=0, reportees={}, gender=UNKNOWN, netWorth=0]",
				"Employee=[ids={employeeId=2}, salary=0, reportees={}, gender=UNKNOWN, netWorth=0]");
	}
	
	@Test
	public void testWithSameListElement() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		Food food1 = new Food();
		Food food2 = new Food();
		food1.setType(FoodType.PIZZA);
		food2.setType(FoodType.PIZZA);
		person1.setFavoriteFoods(Arrays.asList(food1));
		person2.setFavoriteFoods(Arrays.asList(food2));
		LEHAssertions.verify(person1, person2, true);
	}
	
	@Test
	public void testWithReferenceToOtherEqualEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		LEHAssertions.verify(person1, person2, true);
	}

	@Test
	public void testWithMapEntry() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		manager.addReportee(manager, employee1);
		manager.addReportee(manager, employee2);
		LEHAssertions.verify(employee1, employee2, true);
	}
	
	@Test
	public void testWithMapEntryNotEqual() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee2);
		LEHAssertions.verify(employee1, employee2, false);
	}
	
	@Test
	public void testWithMapEntryNotEqualCompliment() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee1);
		LEHAssertions.verify(employee1, employee2, false);
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
		LEHAssertions.verify(person1, person2, false);
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
		LEHAssertions.verify(person1, person2, false);
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
		LEHAssertions.verify(person1, person2, false);
	}
	
	@Test
	public void testNotEqualWithReferenceUnequalEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		person2.getSpouse().setBirthDate(new Date());
		LEHAssertions.verify(person1, person2, false);
	}
	
	@Test
	public void testEmptyEntityToString() throws Exception {
		Person person = new Person();
		person.setSsn("123456789");
		assertEquals("Person=[ids={ssn=123456789}, gender=UNKNOWN, netWorth=0]", LEH.getInstance().getToString(person).toString());
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
					+"spouse=Person=[firstName=Brian, favoriteFoods=[Food=[ids={type=PIZZA}, calories=0, sodium=0]], gender=UNKNOWN, netWorth=0]]", 
					LEH.getInstance().getToString(person).toString());
	}
	
	@Test 
	public void testCircularReferenceToString() throws Exception {
		Employee employee = new Employee();
		employee.setFirstName("employee");
		Employee manager = new Employee();
		manager.setFirstName("manager");
		manager.addReportee(manager, employee);
		int mgrHashCode = LEH.getInstance().getHashCode(manager).hashCode();
		LEHAssertions.verify(employee, manager, false, 
				"Employee=[salary=0, manager=Employee=[salary=0, reportees={parentReference#"+mgrHashCode+"=["
						+ employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0], "
						+ "reportees={}, firstName=employee, gender=UNKNOWN, netWorth=0]",
				"Employee=[salary=0, reportees={parentReference#"+mgrHashCode+"=["
						+ employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0]");
	}
	
	// Test for infinite recursion when reused by overriding equals, hashcode, toString
	
	@Test
	public void testReusingLehOverridingEquals() throws Exception {
		assertEquals(new FoodInventory(), new FoodInventory());
		LEHAssertions.verify(new FoodInventory(), new FoodInventory(), true);
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentClass() throws Exception {
		FoodInventory first = new FoodInventory();
		FoodInventory second = new FoodInventory(){/*anonymous inner class is a different class*/};
		assertNotEquals(first, second);
		LEHAssertions.verify(first, second, false);
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentValues() throws Exception {
		FoodInventory foodInventory1 = new FoodInventory();
		foodInventory1.setCalories(0);
		FoodInventory foodInventory2 = new FoodInventory();
		foodInventory2.setCalories(100);
		assertNotEquals(foodInventory1, foodInventory2);
		LEHAssertions.verify(foodInventory1, foodInventory2, false);
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
	public void testMultipleLayersOfWrappedProxyInstance() throws Exception {
		// not a smart thing to do, but doesn't hurt the behavior
		LEHAssertions.verify(LEH.getInstance(LEH.getInstance(new Person())), 
								LEH.getInstance(LEH.getInstance(new Person())), true);
	}
	
	@Test
	public void testToStringOnAnonymousInnerClass() throws Exception {
		LEHAware meh = new LEHAware(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		assertEquals("LEHAware$1=[name=Meh, age=22]", LEH.getInstance(meh).toString());
	}
	
	@Test
	public void testSelfReference() throws Exception {
		SelfReferencingExample sre1 = new SelfReferencingExample();
		sre1.instance = sre1;
		SelfReferencingExample sre2 = new SelfReferencingExample();
		sre2.instance = sre2;
		LEHAssertions.verify(sre1, sre2, true);
	}
	
	@Test
	public void testSelfReferenceToString() throws Exception {
		SelfReferencingExample sre = new SelfReferencingExample();
		sre.instance = sre;
		int hashCode = LEH.getInstance().getHashCode(sre).hashCode();
		assertEquals("SelfReferencingExample=[ids={instance=parentReference#" + hashCode + "}]", 
					 LEH.getInstance().getToString(sre).toString());
	}
	
	@Test
	public void testCrossReference() throws Exception {
		SelfReferencingExample sre1 = new SelfReferencingExample();
		SelfReferencingExample sre2 = new SelfReferencingExample();
		sre2.instance = sre1;
		sre1.instance = sre2;
		LEHAssertions.verify(sre1, sre2, true);
	}
	
	@Test
	public void testEqualsHashCodeAnonymousInnerClassWrapped() throws Exception {
		Object meh = new Object(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		Wrapper meh2 = LEH.getInstance(ReflectionUtils.createAnonymous(meh, this));
		meh = LEH.getInstance(meh);
		assertEquals(meh, meh2);
		assertEquals(meh.hashCode(), meh2.hashCode());
	}
	
}
