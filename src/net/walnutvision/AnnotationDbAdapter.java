package net.walnutvision;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AnnotationDbAdapter {
	public static final String KEY_ROWID="_id";
    public static final String KEY_IMAGE_PATH="image_path";
    public static final String KEY_X="x";
    public static final String KEY_Y="y";
    public static final String KEY_WIDTH="width";
    public static final String KEY_HEIGHT="height";
    public static final String KEY_LABEL="label";
    private static final String TAG = "AnnotationDbAdapter";
    private static final String DATABASE_NAME = "imageweb_annotation";
    private static final String DATABASE_TABLE = "annotation";
    private static final int DATABASE_VERSION = 2;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    
    private static final String DATABASE_CREATE =
        "create table if not exists annotation (_id integer primary key autoincrement, "
                + "image_path text not null, x real not null, y real not null, "
                + "width real not null, height real not null, label text not null, time integer default current_timestamp);";
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);	
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS annotation");
            onCreate(db);
        }
    }
    public AnnotationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    public AnnotationDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long addAnnotation(String imagePath, float x, float y, float width, float height, String label){
    	String validImagePath=new File(imagePath).getName();
    	validImagePath=validImagePath.replaceAll("\\D", "");
    	ContentValues initialValues=new ContentValues();
    	initialValues.put(KEY_IMAGE_PATH, validImagePath);
    	initialValues.put(KEY_X, x);
    	initialValues.put(KEY_Y, y);
    	initialValues.put(KEY_WIDTH, width);
    	initialValues.put(KEY_HEIGHT, height);
    	initialValues.put(KEY_LABEL, label);
    	return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Cursor fetchAnnotations(String imagePath) throws SQLiteException{
    	String validImagePath=new File(imagePath).getName();
    	validImagePath=validImagePath.replaceAll("\\D", "");
    	Cursor cursor=mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_IMAGE_PATH,
    			KEY_X, KEY_Y, KEY_WIDTH, KEY_HEIGHT, KEY_LABEL}, 
    			KEY_IMAGE_PATH+"="+validImagePath, null, null, null, "time desc");
    	if(cursor!=null){
    		cursor.moveToFirst();
    	}
    	return cursor;
    }
    
    public boolean deleteAnnotation(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteImage(String imagePath){
    	return mDb.delete(DATABASE_TABLE, KEY_IMAGE_PATH+"="+imagePath, null)>0;
    }
}