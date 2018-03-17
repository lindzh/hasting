package com.lindzh.hasting.rpc.serialize;

import java.io.Serializable;

public class HelloBean implements Serializable {

	private static final long serialVersionUID = -7651276822721819289L;

	private long id;
	private String name;
	private long lnt;
	private long lat;
	private float price;
	private String category;
	private String classic;
	private int age;
	private String desc;
	private long addTime;
	private long addUserId;
	private String estimate;
	private String lastName;
	private String firstName;
	private String fullName;
	private String country;
	private String provice;
	private String city;
	private long belongs;
	private double lastPrice;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLnt() {
		return lnt;
	}

	public void setLnt(long lnt) {
		this.lnt = lnt;
	}

	public long getLat() {
		return lat;
	}

	public void setLat(long lat) {
		this.lat = lat;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getClassic() {
		return classic;
	}

	public void setClassic(String classic) {
		this.classic = classic;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public long getAddUserId() {
		return addUserId;
	}

	public void setAddUserId(long addUserId) {
		this.addUserId = addUserId;
	}

	public String getEstimate() {
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvice() {
		return provice;
	}

	public void setProvice(String provice) {
		this.provice = provice;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public long getBelongs() {
		return belongs;
	}

	public void setBelongs(long belongs) {
		this.belongs = belongs;
	}

	public double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}

	@Override
	public String toString() {
		return "HelloBean [id=" + id + ", name=" + name + ", lnt=" + lnt
				+ ", lat=" + lat + ", price=" + price + ", category="
				+ category + ", classic=" + classic + ", age=" + age
				+ ", desc=" + desc + ", addTime=" + addTime + ", addUserId="
				+ addUserId + ", estimate=" + estimate + ", lastName="
				+ lastName + ", firstName=" + firstName + ", fullName="
				+ fullName + ", country=" + country + ", provice=" + provice
				+ ", city=" + city + ", belongs=" + belongs + ", lastPrice="
				+ lastPrice + "]";
	}

}
