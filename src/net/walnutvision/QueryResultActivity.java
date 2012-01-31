package net.walnutvision;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class QueryResultActivity extends ListActivity {
	// private ListView mLvQueryResult;
	PhotoAdapter mAdapter;
	private ArrayList<String> mImageArray = new ArrayList<String>();
	private OnItemClickListener myClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(
					android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=37.424324,-122.156768&daddr=37.021657,-121.56363"));
			startActivity(intent);
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences redirectData = getSharedPreferences("RedirectData", 0);
		String jsonString = redirectData.getString("resultArray", null);
		Log.i("result:", jsonString);
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(jsonString);
			for (int i = 0; i < resultArray.length(); ++i) {
				mImageArray.add(resultArray.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mAdapter = new PhotoAdapter(this,mImageArray);
		setListAdapter(mAdapter);
	}

	public class PhotoAdapter extends BaseAdapter {

		private ArrayList<String> mURLArray=new ArrayList<String>();

		public PhotoAdapter(Context c, ArrayList<String> uRLArray) {
			mContext = c;
			mURLArray = uRLArray;
		}

		public int getCount() {
			return mURLArray.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Make an ImageView to show a photo
			ImageView i = new ImageView(mContext);
			try {
				Bitmap bmp = BitmapFactory.decodeStream(new java.net.URL(mURLArray.get(position))
						.openStream());
				i.setImageBitmap(bmp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			return i;
		}

		private Context mContext;

		public void clearPhotos() {
			mURLArray.clear();
			notifyDataSetChanged();
		}

	}

}
