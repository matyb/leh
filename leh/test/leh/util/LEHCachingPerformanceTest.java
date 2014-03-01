package leh.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import leh.example.food.Food;
import leh.example.food.Food.FoodType;
import leh.example.person.Employee;
import leh.example.person.Person;

import org.junit.Before;
import org.junit.Ignore;
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
	 * took: 425ms. for:   *Executing publicly exposed APIs 1000 times.
	 * took: 133ms. for:     =LEH Equivalent java.lang.Object methods:
	 * took: 27ms. for:        -class leh.util.LEH.getHashCode(instance1)
	 * took: 2ms. for:         -class leh.util.LEH.isEqual(instance1, instance1.getSpouse())
	 * took: 43ms. for:        -class leh.util.LEH.isEqual(instance1, instance2)
	 * took: 60ms. for:        -class leh.util.LEH.getToString(person1)
	 * took: 288ms. for:     =LEH Proxy Methods:
	 * took: 40ms. for:        -class com.sun.proxy.$Proxy5.hashCode()
	 * took: 3ms. for:         -class com.sun.proxy.$Proxy5.equals(instance1.getSpouse())
	 * took: 53ms. for:        -class com.sun.proxy.$Proxy5.equals(samePersonButDifferentInstance)
	 * took: 173ms. for:       -class com.sun.proxy.$Proxy5.toString()
	 * 
	 * @throws Exception
	 */
	@Test @Ignore
	public void testCachingPerformance_1000times() throws Exception {
		executePubliclyExposedLEHMethods(1000);
	}

	private void executePubliclyExposedLEHMethods(final int runs) {
		time(1, "*Executing publicly exposed APIs " + runs + " times.", new Runnable(){
			public void run() {
				final Employee instance1 = createEmployee();
				final Employee instance2 = createEmployee();
				time(1, "  =LEH Equivalent java.lang.Object methods:", new Runnable() {
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
		final LEHDelegate leh = LEH.getInstance();
		String testNamePrefix = "    -" + leh.getClass() + ".";
		time(runs, testNamePrefix + "getToString(person1)", new Runnable() {
			public void run() {
				leh.getToString(instance1).toString();
			}
		});
		time(runs, testNamePrefix + "isEqual(instance1, instance2)", new Runnable() {
			public void run() {
				leh.getEquals(instance1).equals(instance2);
			}
		});
		time(runs, testNamePrefix + "isEqual(instance1, instance1.getSpouse())", new Runnable() {
			public void run() {
				leh.getEquals(instance1).equals(instance1.getSpouse());
			}
		});
		time(runs, testNamePrefix + "getHashCode(instance1)", new Runnable() {
			public void run() {
				leh.getHashCode(instance1).hashCode();
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
