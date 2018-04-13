package com.lindzh.hasting.spring.test;

import java.io.Serializable;

public class TestBean implements Serializable {
	
	private static final long serialVersionUID = -6778119358481557931L;
	private int limit;
	private int offset;
	private String order;
	private String message;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "TestBean [limit=" + limit + ", offset=" + offset + ", order="
				+ order + ", message=" + message + "]";
	}

}
