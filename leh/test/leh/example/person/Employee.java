package leh.example.person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leh.annotations.Entity;
import leh.annotations.Identity;
import leh.annotations.Transient;

@Entity
public class Employee extends Person {

	private long salary;
	@Transient
	private Employee manager;
	private Map<Employee, List<Employee>> reportees = new HashMap<Employee, List<Employee>>();
	
	@Identity
	private String employeeId;
	
	public long getSalary() {
		return salary;
	}
	public void setSalary(long salary) {
		this.salary = salary;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Employee getManager(){
		return manager;
	}
	public Map<Employee, List<Employee>> getReportees() {
		return reportees;
	}
	public void addEmployee(Employee manager, Employee employee){
		List<Employee> employees = reportees.get(manager);
		if(employees == null){
			employees = new ArrayList<Employee>();
			reportees.put(manager, employees);
		}
		employee.manager = manager;
		employees.add(employee);
	}
}
