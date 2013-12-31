package leh.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import leh.example.SelfReferencingExample;
import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.food.FoodInventory;
import leh.example.person.Employee;
import leh.example.person.Person;
import leh.util.wrappers.LEHWrapper;

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
	
	@Test
	public void testAllNull() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		LEHSymmetryUtils.verify(person1, person2, true);
	}
	
	@Test
	public void testSubclass() throws Exception {
		Person person1 = new Employee();
		Person person2 = new Employee();
		LEHSymmetryUtils.verify(person1, person2, true);
	}
	
	@Test
	public void testDifferentSubclasses() throws Exception {
		Person person1 = new Person();
		Person person2 = new Employee();
		LEHSymmetryUtils.verify(person1, person2, false);
	}
	
	@Test
	public void testIgnoresFieldsWhereIdentityAnnotationIsPresentAndValueIsDefault() throws Exception {
		Employee employee1 = new Employee();
		employee1.setEmployeeId("1");
		Employee employee2 = new Employee();
		employee2.setEmployeeId("2");
		// they are equal, but their id is different, id is part of identity
		LEHSymmetryUtils.verify(
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
		LEHSymmetryUtils.verify(person1, person2, true);
	}
	
	@Test
	public void testWithReferenceToOtherEqualEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		LEHSymmetryUtils.verify(person1, person2, true);
	}

	@Test
	public void testWithMapEntry() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		manager.addReportee(manager, employee1);
		manager.addReportee(manager, employee2);
		LEHSymmetryUtils.verify(employee1, employee2, true);
	}
	
	@Test
	public void testWithMapEntryNotEqual() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee2);
		LEHSymmetryUtils.verify(employee1, employee2, false);
	}
	
	@Test
	public void testWithMapEntryNotEqualCompliment() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addReportee(manager, employee1);
		LEHSymmetryUtils.verify(employee1, employee2, false);
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
		LEHSymmetryUtils.verify(person1, person2, false);
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
		LEHSymmetryUtils.verify(person1, person2, false);
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
		LEHSymmetryUtils.verify(person1, person2, false);
	}
	
	@Test
	public void testNotEqualWithReferenceUnequalEntity() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		person1.setSpouse(new Person());
		person2.setSpouse(new Person());
		person2.getSpouse().setBirthDate(new Date());
		LEHSymmetryUtils.verify(person1, person2, false);
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
		LEHSymmetryUtils.verify(employee, manager, false, 
				"Employee=[salary=0, manager=Employee=[salary=0, reportees={this=["
						+ employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0], "
						+ "reportees={}, firstName=employee, gender=UNKNOWN, netWorth=0]",
				"Employee=[salary=0, reportees={this=["
						+ employee.toString()+"]}, firstName=manager, gender=UNKNOWN, netWorth=0]");
	}
	
	// Test for infinite recursion when reused by overriding equals, hashcode, toString
	
	@Test
	public void testReusingLehOverridingEquals() throws Exception {
		assertEquals(new FoodInventory(), new FoodInventory());
		LEHSymmetryUtils.verify(new FoodInventory(), new FoodInventory(), true);
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentClass() throws Exception {
		FoodInventory first = new FoodInventory();
		FoodInventory second = new FoodInventory(){/*anonymous inner class is a different class*/};
		assertNotEquals(first, second);
		LEHSymmetryUtils.verify(first, second, false);
	}
	
	@Test
	public void testReusingLehOverridingEqualsComplimentDifferentValues() throws Exception {
		FoodInventory foodInventory1 = new FoodInventory();
		foodInventory1.setCalories(0);
		FoodInventory foodInventory2 = new FoodInventory();
		foodInventory2.setCalories(100);
		assertNotEquals(foodInventory1, foodInventory2);
		LEHSymmetryUtils.verify(foodInventory1, foodInventory2, false);
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
		LEHWrapper wrapper = LEHWrapper.getInstance();
		// not a smart thing to do, but doesn't hurt the behavior
		LEHSymmetryUtils.verify(wrapper.wrap(wrapper.wrap(new Person())), 
									    wrapper.wrap(wrapper.wrap(new Person())), 
										true);
	}
	
	@Test
	public void testToStringOnAnonymousInnerClass() throws Exception {
		Entity meh = new Entity(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		LEHSymmetryUtils.veryifyToString(meh, "Entity$1=[name=Meh, age=22]");
	}
	
	@Test
	public void testSelfReference() throws Exception {
		SelfReferencingExample sre1 = new SelfReferencingExample();
		sre1.instance = sre1;
		SelfReferencingExample sre2 = new SelfReferencingExample();
		sre2.instance = sre2;
		LEHSymmetryUtils.verify(sre1, sre2, true);
	}
	
	@Test
	public void testSelfReferenceToString() throws Exception {
		SelfReferencingExample sre = new SelfReferencingExample();
		sre.instance = sre;
		LEHSymmetryUtils.veryifyToString(sre, "SelfReferencingExample=[instance=this]");
	}
	
	@Test
	public void testCrossReference() throws Exception {
		SelfReferencingExample sre1 = new SelfReferencingExample();
		SelfReferencingExample sre2 = new SelfReferencingExample();
		sre2.instance = sre1;
		sre1.instance = sre2;
		LEHSymmetryUtils.verify(sre1, sre2, true);
	}
	
	@Test
	public void testEqualsHashCodeAnonymousInnerClassWrapped() throws Exception {
		Entity meh = new Entity(){
			@SuppressWarnings("unused")
			private String name = "Meh";
			@SuppressWarnings("unused")
			private int age = 22;
		};
		LEHWrapper leh = LEHWrapper.getInstance();
		Entity meh2 = leh.wrap(ReflectionUtils.createAnonymous(meh, this));
		meh = leh.wrap(meh);
		assertEquals(meh, meh2);
		assertEquals(meh.hashCode(), meh2.hashCode());
	}
	
	/*
	 * last count:
	 * 
	 * to string took: 41ms
	 * simple equality test took: 27ms
     * hashcode test took: 340ms
     * wrapper to string took: 1011ms
     * wrapper simple equality test took: 21ms
     * wrapper hashcode test took: 5ms
	 * 
	 * @throws Exception
	 */
	@Test @Ignore("Rough approximation of performance")
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
		List<Object> accumulator = new ArrayList<Object>(runs);
		long start = System.currentTimeMillis();
		for(int i = 0; i < runs; i++){
			accumulator.add(leh.getToString(leh));
		}
		System.out.println("to string took: "
				+ (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		LEH instance = LEH.getInstance();
		for(int i = 0; i < runs; i++){
			accumulator.add(instance.isEqual(person, spouse));
		}
		System.out.println("simple equality test took: "
				+ (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		accumulator.clear();
		for(int i = 0; i < runs; i++){
			accumulator.add(instance.getHashCode(person));
		}
		System.out.println("hashcode test took: "
				+ (System.currentTimeMillis() - start) + "ms");
		Object wrapper = LEHWrapper.getInstance().wrap(person);
		accumulator.clear();
		start = System.currentTimeMillis();
		for(int i = 0; i < runs; i++){
			accumulator.add(wrapper.toString());
		}
		System.out.println("wrapper to string took: "
				+ (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		for(int i = 0; i < runs; i++){
			accumulator.add(wrapper.equals(spouse));
		}
		System.out.println("wrapper simple equality test took: "
				+ (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		accumulator.clear();
		for(int i = 0; i < runs; i++){
			accumulator.add(person.hashCode());
		}
		System.out.println("wrapper hashcode test took: "
				+ (System.currentTimeMillis() - start) + "ms");
	}
	
}
