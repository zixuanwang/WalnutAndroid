package net.walnutvision;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ProductInfoActivity extends Activity implements
		ColorPickerDialog.OnColorChangedListener {
	private Spinner mSpCategory;
	private Spinner mSpSubcategory;
	private Button mBtColor;
	private Spinner mSpMaterial;
	private ImageView mIvSmallProductImage;
	private String mCurrentImagePath;
	private Bitmap mBitmap;
	private ArrayList<Category> mCategoryArray;
	private ArrayList<Color> mColorArray;
	private ArrayList<Material> mMaterialArray;
	private int mColor = 0;
	private ColorPickerDialog mColorPickerDialog;

	private OnItemSelectedListener mCategorySelectedListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			showToast("Spinner1: position=" + position + " id=" + id);
			Category subcategory = mCategoryArray.get(position);
			bindCategory(mSpSubcategory, subcategory.getSubcategory());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			return;
		}
	};

	private OnClickListener mColorClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			new ColorPickerDialog(ProductInfoActivity.this,
					ProductInfoActivity.this, mColor).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productinfo);
		// get the category information
		ServerInformation.loadCategory();
		ServerInformation.loadMaterial();
		mCategoryArray = ServerInformation.getCategoryArray();
		mMaterialArray = ServerInformation.getMaterialArray();
		mSpCategory = (Spinner) findViewById(R.id.spCategory);
		mSpSubcategory = (Spinner) findViewById(R.id.spSubcategory);
		mBtColor = (Button) findViewById(R.id.btColor);
		mSpMaterial = (Spinner) findViewById(R.id.spMaterial);
		mSpCategory.setOnItemSelectedListener(mCategorySelectedListener);
		mBtColor.setOnClickListener(mColorClickListener);
		mIvSmallProductImage = (ImageView) findViewById(R.id.ivSmallProductImage);
		SharedPreferences redirectData = getSharedPreferences("RedirectData", 0);
		mCurrentImagePath = redirectData.getString("imagePath", null);
		mBitmap = BitmapFactory.decodeFile(mCurrentImagePath);
		mIvSmallProductImage.setImageBitmap(mBitmap);
		bindCategory(mSpCategory, mCategoryArray);
		bindMaterial(mSpMaterial, mMaterialArray);
	}

	private void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void bindCategory(Spinner spinner, ArrayList<Category> categoryArray) {
		ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < categoryArray.size(); ++i) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("description", categoryArray.get(i).description);
			categoryList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, categoryList,
				android.R.layout.simple_spinner_item,
				new String[] { "description" },
				new int[] { android.R.id.text1 });
		spinner.setAdapter(adapter);
	}

	private void bindColor(Spinner spinner, ArrayList<Color> colorArray) {

	}

	private void bindMaterial(Spinner spinner, ArrayList<Material> materialArray) {
		ArrayList<HashMap<String, String>> materialList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < materialArray.size(); ++i) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("materialname", materialArray.get(i).materialname);
			materialList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, materialList,
				android.R.layout.simple_spinner_item,
				new String[] { "materialname" },
				new int[] { android.R.id.text1 });
		spinner.setAdapter(adapter);
	}

	// private void showCategory() {
	// ArrayList<Category> categoryArray = ServerInformation
	// .getCategoryArray();
	// ArrayList<HashMap<String, String>> categoryList = new
	// ArrayList<HashMap<String, String>>();
	// for (int i = 0; i < categoryArray.size(); ++i) {
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put("description", categoryArray.get(i).description);
	// categoryList.add(map);
	// }
	// SimpleAdapter adapter = new SimpleAdapter(this, categoryList,
	// android.R.layout.simple_spinner_item,
	// new String[] { "description" },
	// new int[] { android.R.id.text1 });
	// mSpCategory.setAdapter(adapter);
	// }

	public void colorChanged(int color) {
		mColor = color;
		mBtColor.setBackgroundColor(color);
	}
}
