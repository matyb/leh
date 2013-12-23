package leh.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.person.Employee;
import leh.example.person.Person;

import org.junit.Ignore;
import org.junit.Test;


public class LogicalEqualHashCodeTest {

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
	public void testEqualAllNull() throws Exception {
		Person person1 = new Person();
		Person person2 = new Person();
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
	}

	private void assertEqualsAndHashCodeSymmetry(Object instance1, Object instance2, boolean isEqual) {
		LogicalEqualsHashCode leh = LogicalEqualsHashCode.getInstance();
		assertEquals("Expected " + instance1 + " and " + instance2 + (isEqual ? "" : "NOT") +" to be equal." , isEqual, leh.isEqual(instance1, instance2));
		assertEquals("Expected " + instance1 + " and " + instance2 + (isEqual ? "" : "NOT") +" to hash the same.", isEqual, leh.getHashCode(instance1) == leh.getHashCode(instance2));
	}
	
	@Test
	public void testEqualSubclass() throws Exception {
		Person person1 = new Employee();
		Person person2 = new Employee();
		assertEqualsAndHashCodeSymmetry(person1, person2, true);
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
		manager.addEmployee(manager, employee1);
		manager.addEmployee(manager, employee2);
		assertEqualsAndHashCodeSymmetry(employee1, employee2, true);
	}
	
	@Test
	public void testNotEqualWithMapEntry() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addEmployee(manager, employee2);
		assertEqualsAndHashCodeSymmetry(employee1, employee2, false);
	}
	
	@Test
	public void testNotEqualWithMapEntryCompliment() throws Exception {
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		Employee manager = new Employee();
		employee1.addEmployee(manager, employee1);
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
		assertEquals("Person=[ids={ssn=123456789}, gender=UNKNOWN, netWorth=0]", LogicalEqualsHashCode.getInstance().getToString(person));
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
					LogicalEqualsHashCode.getInstance().getToString(person));
	}
	
	@Test @Ignore
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
		LogicalEqualsHashCode leh = LogicalEqualsHashCode.getInstance();
		int runs = 100000;
		List<String> accumulator = new ArrayList<String>(runs);
		for(int i = 0; i < runs; i++){
			accumulator.add(leh.getToString(leh));
		}
	}
	
}
