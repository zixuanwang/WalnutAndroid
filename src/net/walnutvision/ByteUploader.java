package net.walnutvision;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class ByteUploader {
	private static final String SERVER = "http://walnutvision.net:8081/walnut/upload";
	public static String upload(ArrayList<NameValuePair> nameValuePairs){
		String jsonString = "";
		try{
			HttpClient httpclient=new DefaultHttpClient();
			HttpPost httppost=new HttpPost(SERVER);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			jsonString = EntityUtils.toString(entity);
		}catch(Exception e){
			Log.e("ByteUploader", e.toString());
		}
		return jsonString;
	}
}
