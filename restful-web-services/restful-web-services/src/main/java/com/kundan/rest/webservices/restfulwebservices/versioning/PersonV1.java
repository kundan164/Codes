package com.kundan.rest.webservices.restfulwebservices.versioning;

public class PersonV1 {
	private String name;

	public PersonV1(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("PersonV1 [name=%s]", name);
	}
	
	
	
}
