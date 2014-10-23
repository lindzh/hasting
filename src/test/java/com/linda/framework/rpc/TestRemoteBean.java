package com.linda.framework.rpc;

import java.io.Serializable;

public class TestRemoteBean implements Serializable {

	private static final long serialVersionUID = 2448105590901413899L;
	private String name;
	private String action;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "TestRemoteBean [name=" + name + ", action=" + action + ", age="
				+ age + "]";
	}

}
