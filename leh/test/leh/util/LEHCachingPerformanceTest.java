package leh.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.person.Employee;
import leh.example.person.Person;
import leh.util.wrappers.LEHWrapper;

import org.junit.Before;
import org.junit.Test;

/*
 * Tests caching performance - disabled in CI and
 * NOT TECHNICALLY A UNIT TEST AT ALL by virtue
 * of it lacking asserts, testing course grained
 * APIs, etc.
 */
public class LEHCachingPerformanceTest {
	
	List<String> timings;
	
	@Before
	public void setUp(){
		timings = new ArrayList<String>();
	}
	
	/*
	 * last count: on an i5 4670K 16GB of DDR3 and SSD in Win 8 w/ JDK 1.7.0.45.
	 * 
 	 * 
	 * LEH Proxy Methods: took: 1561ms:
	 * class com.sun.proxy.$Proxy4.toString() took: 977ms 
	 * class com.sun.proxy.$Proxy4.equals(samePersonButDifferentInstance) took: 295ms
	 * class com.sun.proxy.$Proxy4.equals(instance1.getSpouse()) took: 23ms
	 * class com.sun.proxy.$Proxy4.hashCode() took: 259ms 
	 *  
	 * LEH Equivalent java.lang.Object methods: took: 1119ms 
	 * class leh.util.LEH.getToString(person1) took: 693ms 
	 * class leh.util.LEH.isEqual(instance1, instance2) took: 199ms 
	 * class leh.util.LEH.isEqual(instance1, instance1.getSpouse()) took: 10ms 
	 * class leh.util.LEH.getHashCode(instance1) took: 217ms
	 * 
	 * @throws Exception
	 */
	@Test 
	public void testCachingPerformance_1000times() throws Exception {
		executePubliclyExposedLEHMethods(1000);
	}

	private void executePubliclyExposedLEHMethods(final int runs) {
		time(1, "*Executing publicly exposed APIs " + runs + " times.", new Runnable(){
			@Override
			public void run() {
				final Employee instance1 = createEmployee();
				final Employee instance2 = createEmployee();
				time(1, "  =LEH Proxy Methods:", new Runnable(){
					@Override
					public void run() {
						executeObjectMethodsOnWrapper(runs, instance1, instance2);
					}
				});
				time(1, "  =LEH Equivalent java.lang.Object methods:", new Runnable() {
					@Override
					public void run() {
						executePubliclyExposedLEHMethods(runs, instance1, instance2);
					}
				});
			}
		});
		for(int i = timings.size() - 1; i > -1; i--){
			System.out.println(timings.get(i));
		}
	}

	private void executePubliclyExposedLEHMethods(int runs, final Employee instance1, final Employee instance2) {
		final LEH leh = LEH.getInstance();
		String testNamePrefix = "    -" + leh.getClass() + ".";
		time(runs, testNamePrefix + "getToString(person1)", new Runnable() {
			@Override
			public void run() {
				leh.getToString(instance1);
			}
		});
		time(runs, testNamePrefix + "isEqual(instance1, instance2)", new Runnable() {
			@Override
			public void run() {
				leh.isEqual(instance1, instance2);
			}
		});
		time(runs, testNamePrefix + "isEqual(instance1, instance1.getSpouse())", new Runnable() {
			@Override
			public void run() {
				leh.isEqual(instance1, instance1.getSpouse());
			}
		});
		time(runs, testNamePrefix + "getHashCode(instance1)", new Runnable() {
			@Override
			public void run() {
				leh.getHashCode(instance1);
			}
		});
	}

	private void executeObjectMethodsOnWrapper(int runs, final Employee instance1, final Employee instance2) {
		final LEHWrapper wrapper = LEHWrapper.getInstance();
		final Object person = wrapper.wrap(instance1);
		final Object samePersonDifferentInstance = wrapper.wrap(instance2);
		String testNamePrefix = "    -" + person.getClass();
		time(runs, testNamePrefix + ".toString()", new Runnable() {
			@Override
			public void run() {
				person.toString();
			}
		});
		time(runs, testNamePrefix + ".equals(samePersonButDifferentInstance)", new Runnable() {
			@Override
			public void run() {
				person.equals(samePersonDifferentInstance);
			}
		});
		time(runs, testNamePrefix + ".equals(instance1.getSpouse())", new Runnable() {
			@Override
			public void run() {
				person.equals(instance1.getSpouse());
			}
		});
		time(runs, testNamePrefix + ".hashCode()", new Runnable() {
			@Override
			public void run() {
				person.hashCode();
			}
		});
	}
	
	private Employee createEmployee() {
		final Employee person = new Employee();
		person.setSsn("123456789");
		Date birthDate = new Date();
		person.setBirthDate(birthDate);
		final Person spouse = new Person();
		Food food = new Food();
		food.setType(FoodType.PIZZA);
		spouse.setFavoriteFoods(Arrays.asList(food));
		spouse.setFirstName("Brian");
		person.setSpouse(spouse);
		return person;
	}

	// just private because there's really no good reason other "unit" tests
	// should be doing this... if this were appropriate for unit tests it would
	// be in a test util class.
	private void time(int runs, String testName, Runnable behavior) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			behavior.run();
		}
		long time = System.currentTimeMillis() - start;
		timings.add(String.format("%1$-" + 20 + "s", "took: " + time + "ms. for: ") + testName);
	}
	
}
