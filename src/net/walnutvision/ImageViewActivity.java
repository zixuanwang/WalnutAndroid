package net.walnutvision;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {
	private String mCurrentImagePath = null;
	private ImageView mImageView;
	private Button mBtUpload;
	private Button mBtQuery;
	private Bitmap mBitmap;
	private static final int ACTIVITY_UPLOAD_INFO = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view);
		mImageView = (ImageView) findViewById(R.id.imageView);
		mBtUpload = (Button) findViewById(R.id.BtUpload);
		mBtUpload.setOnClickListener(mUploadListener);
		mBtQuery = (Button) findViewById(R.id.BtQuery);
		mBtQuery.setOnClickListener(mQueryListener);
		SharedPreferences redirectData = getSharedPreferences("RedirectData", 0);
		mCurrentImagePath = redirectData.getString("imagePath", null);
		mBitmap = BitmapFactory.decodeFile(mCurrentImagePath);
		mImageView.setImageBitmap(mBitmap);
	}

	private String uploadImage() {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
		byte[] ba = bao.toByteArray();
		String ba1 = Base64.encodeBytes(ba);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("image", ba1));
		return ByteUploader.upload(nameValuePairs);
	}

	private OnClickListener mQueryListener = new OnClickListener() {
		public void onClick(View v) {
			String jsonString = uploadImage();
			SharedPreferences redirectData = getSharedPreferences("RedirectData", 0);
			SharedPreferences.Editor editor = redirectData.edit();
			editor.putString("resultArray", jsonString);
			editor.commit();
			Intent intent = new Intent(ImageViewActivity.this,
					QueryResultActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
		}
	};

	private OnClickListener mUploadListener = new OnClickListener() {
		public void onClick(View v) {
			// Intent intent = new Intent(ImageViewActivity.this,
			// ProductInfoActivity.class);
			// startActivityForResult(intent, ACTIVITY_UPLOAD_INFO);
			// overridePendingTransition(R.anim.fade, R.anim.hold);
			uploadImage();
		}
	};
}
