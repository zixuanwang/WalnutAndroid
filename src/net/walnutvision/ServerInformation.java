package net.walnutvision;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class ServerInformation {
	private static final String CATEGORY_PAGE = "http://zixuanpc.stanford.edu/index.php/android/category";
	private static final String COLOR_PAGE = "http://zixuanpc.stanford.edu/index.php/android/color";
	private static final String MATERIAL_PAGE = "http://zixuanpc.stanford.edu/index.php/android/material";
	private static final String TAG = "SERVER_INFO";
	private static ArrayList<Category> mCategoryArray = new ArrayList<Category>();
	private static ArrayList<Color> mColorArray = new ArrayList<Color>();
	private static ArrayList<Material> mMaterialArray = new ArrayList<Material>();

	public static String loadJSON(String url) {
		String jsonString = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			jsonString = EntityUtils.toString(httpEntity);
			Log.i(TAG, jsonString);
		} catch (Exception e) {

		}
		return jsonString;
	}

	public static void loadCategory() {
		try {
			String jsonString = loadJSON(CATEGORY_PAGE);
			JSONArray categoryArray = new JSONArray(jsonString);
			for (int i = 0; i < categoryArray.length(); ++i) {
				JSONObject categoryObject = categoryArray.getJSONObject(i);
				Category category = new Category();
				category.loadCategory(categoryObject);
				mCategoryArray.add(category);
			}
		} catch (Exception e) {

		}
	}

	public static void loadColor() {
		try {
			String jsonString = loadJSON(COLOR_PAGE);
			JSONArray colorArray = new JSONArray(jsonString);
			for (int i = 0; i < colorArray.length(); ++i) {
				JSONObject colorObject = colorArray.getJSONObject(i);
				Color color = new Color();
				color.loadColor(colorObject);
				mColorArray.add(color);
			}
		} catch (Exception e) {

		}
	}

	public static void loadMaterial() {
		try {
			String jsonString = loadJSON(MATERIAL_PAGE);
			JSONArray materialArray = new JSONArray(jsonString);
			for (int i = 0; i < materialArray.length(); ++i) {
				JSONObject materialObject = materialArray.getJSONObject(i);
				Material material = new Material();
				material.loadMaterial(materialObject);
				mMaterialArray.add(material);
			}
		} catch (Exception e) {

		}
	}

	public static ArrayList<Category> getCategoryArray() {
		return mCategoryArray;
	}

	public static ArrayList<Color> getColorArray() {
		return mColorArray;
	}

	public static ArrayList<Material> getMaterialArray() {
		return mMaterialArray;
	}

	public static ArrayList<HashMap<String, String>> loadCatetory() {
		ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(CATEGORY_PAGE);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String jsonString = EntityUtils.toString(httpEntity);
			Log.i(TAG, jsonString);
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); ++i) {
				HashMap<String, String> map = new HashMap<String, String>();
				String id = jsonArray.getJSONObject(i).getString("id");
				String description = jsonArray.getJSONObject(i).getString(
						"description");
				map.put("id", id);
				map.put("description", description);
				categoryList.add(map);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return categoryList;
	}
}
