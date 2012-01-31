package net.walnutvision;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final String TAG = "LOGIN";
	private final String LOGIN_URL = GlocalConfig.SERVER_URL
			+ "user/clientLoginAuthenticate";
	private Button mLoginButton;
	private Button mRegisterButton;
	private EditText mUsernameBox;
	private EditText mPasswordBox;

	private OnClickListener mLoginListener = new OnClickListener() {
		public void onClick(View v) {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", mUsernameBox
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("password", mPasswordBox
					.getText().toString()));
			String jsonString = FormUploader.submit(LOGIN_URL, nameValuePairs);
			Log.i(TAG,jsonString);
			//paring data
			int userId=-1;
			String username="";
			String realname="";
			try{
				JSONObject jsonData=new JSONObject(jsonString);
				userId=jsonData.getInt("id");
				username=jsonData.getString("username");
				realname=jsonData.getString("realname");
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			if(userId==-1){
				Toast.makeText(LoginActivity.this, "用户名密码错误",
						Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(LoginActivity.this, "欢迎"+realname,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LoginActivity.this,
						ImageWebActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mLoginButton = (Button) findViewById(R.id.btLogin);
		mRegisterButton = (Button) findViewById(R.id.btRegister);
		mUsernameBox = (EditText) findViewById(R.id.etUsername);
		mPasswordBox = (EditText) findViewById(R.id.etPassword);
		mLoginButton.setOnClickListener(mLoginListener);
	}
}
