package com.emenu.models;

public class MenuItem {
	private long id;
	private String name;
	private boolean isSpecial;

	public MenuItem(long id, String name) {
		this.id = id;
		this.name = name;
		this.isSpecial = false;
	}

	public MenuItem(long id, String name, boolean isSpecial) {
		this.id = id;
		this.name = name;
		this.isSpecial = isSpecial;
	}

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

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	@Override
	public String toString() {
		return "MenuItem [id=" + id + ", name=" + name + ", isSpecial=" + isSpecial + "]";
	}

}
