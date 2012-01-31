package net.walnutvision;

import org.json.JSONObject;

public class Material {
	public int id;
	public String materialname;
	public Material(){
		
	}
	public void loadMaterial(JSONObject jsonObject) {
		try {
			id = jsonObject.getInt("id");
			materialname = jsonObject.getString("materialname");
		} catch (Exception e) {

		}
	}
}
