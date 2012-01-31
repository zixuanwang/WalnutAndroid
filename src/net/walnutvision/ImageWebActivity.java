package net.walnutvision;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageWebActivity extends Activity {
	private static final String IMAGE_DIRECTORY = "/sdcard/DCIM/Camera";
	private ImageView mImageView;
	private Bitmap mBitmap;
	private Button mCaptureButton;
	private Button mGalleryButton;
	private String mCurrentImagePath = null;
	private static final int ACTIVITY_SELECT_CAMERA = 0;
	private static final int ACTIVITY_SELECT_IMAGE = 1;

	private static final int ACTIVITY_EDIT_IMAGE = 2;
	private static final String TAG = "MAIN_ACTIVITY";
	private static final int CAMERA_ID = Menu.FIRST;
	private static final int GALLERY_ID = Menu.FIRST + 1;
	private static final int QUERY_ID = Menu.FIRST + 2;
	private static final int EDIT_ID = Menu.FIRST + 3;
	private static final int SEND_ID = Menu.FIRST + 4;
	private static final int LABEL_ID = Menu.FIRST + 5;
	private static final int DIALOG_LABEL = 0;
	// private ImageEditView mImageView = null;
	private Connector mConnector = null;
	private RectF mEditingBox = null;
	private Handler mHandler = new Handler();
	private DisplayMetrics mDisplayMetric = new DisplayMetrics();

	public static String mUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mCaptureButton = (Button) findViewById(R.id.captureButton);
		mImageView = (ImageView) findViewById(R.id.homeView);
		mCaptureButton.setOnClickListener(mCaptureListener);
		mGalleryButton = (Button) findViewById(R.id.galleryButton);
		mGalleryButton.setOnClickListener(mGalleryListener);
	}

	private OnClickListener mCaptureListener = new OnClickListener() {
		public void onClick(View v) {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			long timeTaken = System.currentTimeMillis();
			mCurrentImagePath = IMAGE_DIRECTORY + "/"
					+ Utility.createName(timeTaken) + ".jpg";
			Log.i(TAG, mCurrentImagePath);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(mCurrentImagePath)));
			startActivityForResult(cameraIntent, ACTIVITY_SELECT_CAMERA);
		}
	};
	private OnClickListener mGalleryListener = new OnClickListener() {
		public void onClick(View v) {
			Intent galleryIntent = new Intent(Intent.ACTION_PICK,
					Images.Media.INTERNAL_CONTENT_URI);
			startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
		}
	};

	private void resizeCameraImage(String imagePath) {
		ContentValues values = new ContentValues();
		int degrees = Utility.getRotationFromImage(imagePath);
		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 4;
			mBitmap = BitmapFactory.decodeFile(imagePath, option);
			if (degrees != 0) {
				mBitmap = Utility.rotate(mBitmap, degrees);
			}
			FileOutputStream out = new FileOutputStream(imagePath);
			mBitmap.compress(CompressFormat.JPEG, 100, out);
		} catch (Exception e) {

		}
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(Images.Media.DATA, imagePath);
		getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_SELECT_CAMERA
				&& resultCode == Activity.RESULT_OK) {
			resizeCameraImage(mCurrentImagePath);
		}
		if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
			try {
				Uri currImageURI = data.getData();
				String[] proj = { Images.Media.DATA, Images.Media.ORIENTATION };
				Cursor cursor = managedQuery(currImageURI, proj, null, null,
						null);
				int columnIndex = cursor.getColumnIndex(proj[0]);
				cursor.moveToFirst();
				mCurrentImagePath = cursor.getString(columnIndex);

			} catch (Exception e) {

			}
		}
		SharedPreferences redirectData = getSharedPreferences("RedirectData", 0);
		SharedPreferences.Editor editor = redirectData.edit();
		editor.putString("imagePath", mCurrentImagePath);
		editor.commit();
		Intent intent = new Intent(ImageWebActivity.this,
				ImageViewActivity.class);
		startActivity(intent);
	}

	class ImageEditView extends ImageView {
		private int mAlpha = 0x66000000;
		private Bitmap mOriginalBitmap;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Paint mPaint;
		private Paint mLabelPaint;
		private float mStartX, mStartY;
		private boolean mIsDrawingBox = false;
		private Random rnd = null;

		public ImageEditView(Context c) {
			super(c);
			// mCanvas=new Canvas();
			mBitmap = Bitmap.createBitmap(mDisplayMetric.widthPixels,
					mDisplayMetric.heightPixels, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mPaint = new Paint();
			mLabelPaint = new Paint();
			mLabelPaint.setAntiAlias(true);
			mLabelPaint.setColor(0xccff0000);
			mLabelPaint.setTextSize(54);
			rnd = new Random();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}

		// @Override
		// protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// super.onSizeChanged(w, h, oldw, oldh);
		// }

		public void setImagePath(String imagePath) {
			mCanvas.drawColor(Color.BLACK);
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			mOriginalBitmap = bitmap;
			mCanvas.drawBitmap(bitmap, 0, 0, new Paint());
			mEditingBox = null;
			invalidate();
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private RectF computeBox(float sx, float sy, float x, float y) {
			RectF box = new RectF();
			box.top = Math.min(sy, y);
			box.bottom = Math.max(sy, y);
			box.left = Math.min(sx, x);
			box.right = Math.max(sx, x);
			return box;
		}

		// private void drawLabels() {
		// if (mEditingImagePath != null) {
		// Cursor cursor = mDbHelper.fetchAnnotations(mEditingImagePath);
		// int columnX = cursor.getColumnIndex(AnnotationDbAdapter.KEY_X);
		// int columnY = cursor.getColumnIndex(AnnotationDbAdapter.KEY_Y);
		// int columnWidth = cursor
		// .getColumnIndex(AnnotationDbAdapter.KEY_WIDTH);
		// int columnHeight = cursor
		// .getColumnIndex(AnnotationDbAdapter.KEY_HEIGHT);
		// int columnLabel = cursor
		// .getColumnIndex(AnnotationDbAdapter.KEY_LABEL);
		// for (int i = 0; i < cursor.getCount(); i++) {
		// float x = cursor.getFloat(columnX);
		// float y = cursor.getFloat(columnY);
		// float width = cursor.getFloat(columnWidth);
		// float height = cursor.getFloat(columnHeight);
		// String label = cursor.getString(columnLabel);
		// drawLabel(x, y, width, height, label);
		// cursor.moveToNext();
		// }
		// }
		// }

		// public void drawLabel(float x, float y, float width, float height,
		// String label) {
		// RectF rect = new RectF(x, y, x + width, y + height);
		// int color = rnd.nextInt() & 0x00ffffff;
		// color = color | mAlpha;
		// mPaint.setColor(color);
		// mCanvas.drawRect(rect, mPaint);
		// float cx = rect.centerX();
		// float cy = rect.centerY();
		// int labelLength = label.length();
		// mCanvas.drawText(label, cx - 5 * labelLength, cy, mLabelPaint);
		// }

		public void drawLabel(String label) {
			if (label == "") {
				Toast.makeText(ImageWebActivity.this,
						"No label is found for this image.", Toast.LENGTH_LONG)
						.show();
			} else {
				mCanvas.drawText(label, 20, 690, mLabelPaint);
				invalidate();
			}
		}

		private void touch_start(float x, float y) {
			if (mCurrentImagePath != null) {
				mIsDrawingBox = true;
				if (mEditingBox != null) {
					if (mEditingBox.contains(x, y)) {
						mIsDrawingBox = false;
					}
				}
				int color = rnd.nextInt() & 0x00ffffff;
				color = color | mAlpha;
				mPaint.setColor(color);
				if (mIsDrawingBox) {
					mX = x;
					mY = y;
					mStartX = x;
					mStartY = y;
				} else {
					showDialog(DIALOG_LABEL);
				}
			}
		}

		private void touch_move(float x, float y) {
			if (mIsDrawingBox) {
				float dx = Math.abs(x - mX);
				float dy = Math.abs(y - mY);
				if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
					// mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
					mX = x;
					mY = y;
					mEditingBox = computeBox(mStartX, mStartY, mX, mY);
					mCanvas.drawBitmap(mOriginalBitmap, 0, 0, mPaint);
					mCanvas.drawRect(mEditingBox, mPaint);
				}
			}
		}

		private void touch_up() {
			if (mIsDrawingBox) {
				mEditingBox = computeBox(mStartX, mStartY, mX, mY);
				mCanvas.drawBitmap(mOriginalBitmap, 0, 0, mPaint);
				mCanvas.drawRect(mEditingBox, mPaint);
				// mBoundingBox.add(mEditingBox);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LABEL:
			LayoutInflater factory = LayoutInflater.from(this);
			final View labelView = factory.inflate(R.layout.label_dialog, null);
			return new AlertDialog.Builder(this)
					.setTitle(R.string.label_dialog_title)
					.setView(labelView)
					.setPositiveButton(R.string.label_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									AlertDialog ad = (AlertDialog) dialog;
									EditText et = (EditText) ad
											.findViewById(R.id.label_edit);
									String label = et.getText().toString();
									mConnector.sendImage(mCurrentImagePath);
									mConnector.sendLabel(mCurrentImagePath,
											mEditingBox.left, mEditingBox.top,
											mEditingBox.width(),
											mEditingBox.height(), label);
									Toast.makeText(ImageWebActivity.this,
											label + " is tagged.",
											Toast.LENGTH_LONG).show();
								}
							})
					.setNeutralButton(R.string.label_dialog_delete,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							})
					.setNegativeButton(R.string.label_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
									/* User clicked cancel so do some stuff */
								}
							}).create();
		}
		return super.onCreateDialog(id);
	}
}