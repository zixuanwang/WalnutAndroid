package net.walnutvision;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Category {
	public int id;
	public int level;
	public String description;
	public ArrayList<Category> subcategories = new ArrayList<Category>();

	public Category() {

	}

	public void loadCategory(JSONObject jsonObject) {
		try {
			id = jsonObject.getInt("id");
			level = jsonObject.getInt("level");
			description = jsonObject.getString("description");
			if (jsonObject.has("subcategory")) {
				JSONArray subcategoryArray = jsonObject
						.getJSONArray("subcategory");
				for (int i = 0; i < subcategoryArray.length(); ++i) {
					JSONObject subcategoryObject = subcategoryArray
							.getJSONObject(i);
					Category subcategory = new Category();
					subcategory.loadCategory(subcategoryObject);
					subcategories.add(subcategory);
				}
			}
		} catch (Exception e) {

		}
	}

	public ArrayList<Category> getSubcategory() {
		return subcategories;
	}
}
