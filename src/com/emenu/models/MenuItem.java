package com.emenu.models;

public class MenuItem {
	private long id;
	private String name;
	private String menuNumber;

	public MenuItem(long id, String name, String menuNumber) {
		this.id = id;
		this.name = name;
		this.menuNumber = menuNumber;
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

	public String getMenuNumber() {
		return menuNumber;
	}

	public void setMenuNumber(String menuNumber) {
		this.menuNumber = menuNumber;
	}

	@Override
	public String toString() {
		return "MenuItem [id=" + id + ", name=" + name + ", menuNumber=" + menuNumber + "]";
	}

}
