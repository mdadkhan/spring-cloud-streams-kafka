package com.khan.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.khan.pojo.Employee;

import jakarta.annotation.PostConstruct;

@Service
public class SimpleEmpService {

	private List<Employee> empList = null;
	
	
	public List<Employee> getEmpList() {
		return empList;
	}


	public void setEmpList(List<Employee> empList) {
		this.empList = empList;
	}

	public Employee findEmployeeByEmpId(long empId ) {
		return empList.stream().filter(emp -> emp.getEmpId() == empId).findAny().orElse(null);
	}
	@PostConstruct
	public void populateEmployees() {
		
		empList = Arrays.asList(
				new Employee(11l, "Tom", "Hanks"),
				new Employee(12l, "Anthony", "Hopkins"),
				new Employee(13l, "Tom", "Cruise"),
				new Employee(14l, "Leonardo", "DiCaprio")
					);
	}
}
