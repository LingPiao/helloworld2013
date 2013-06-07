package com.emenu.dao;

import java.util.List;

import com.emenu.models.Dish;
import com.emenu.models.MenuItem;

public interface EMenuDao {

	List<MenuItem> loadMenus();

	List<MenuItem> loadMenuById(long id);

	List<Dish> loadDishes();

	List<Dish> loadDishes(long menuItemId);

	void saveMenu(MenuItem menuItem);

	boolean updateMenu(MenuItem menuItem);

	void saveDish(Dish dish);

	boolean updateDish(Dish dish);

	boolean removeMenus(List<Long> ids);

	boolean removeDishes(List<Long> ids);

	long getMaxId4Menu();

	long getMaxId4Dish();

}
