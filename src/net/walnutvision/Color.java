package net.walnutvision;

import org.json.JSONObject;

public class Color {
	public int id;
	public String colorname;
	public Color(){
		
	}
	public void loadColor(JSONObject jsonObject) {
		try {
			id = jsonObject.getInt("id");
			colorname = jsonObject.getString("colorname");
		} catch (Exception e) {

		}
	}
}
