//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio González Díez        [sergio.gd@gmail.com]
//        Sergio Postigo Collado      [spostigoc@gmail.com]
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.lsn.LoadSensing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.ui.CustomToast;
import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.encript.LSSecurity;
import com.lsn.LoadSensing.func.LSFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LSLoginActivity extends ActionBarActivity {

	private SharedPreferences prefs;
	private EditText edtLogin;
	private EditText edtPassword;
	private Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);
				
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtLogin = (EditText) findViewById(R.id.edtLogin);
		edtPassword = (EditText) findViewById(R.id.edtPassword);

		// get shared preferences
		prefs = getSharedPreferences("LSLogin", Context.MODE_PRIVATE);

		edtLogin.setText(LSSecurity.rot13Decode(prefs.getString("user", "")));
		edtPassword
				.setText(LSSecurity.rot13Decode(prefs.getString("pass", "")));

		btnLogin.setOnClickListener(loginOnClickListener);
	}

	protected OnClickListener loginOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			ProgressDialog progressDialog = new ProgressDialog(LSLoginActivity.this);
			progressDialog.setMessage(getString(R.string.msg_PleaseWait));
			progressDialog.setCancelable(false);

			LoginTask loginTask = new LoginTask(LSLoginActivity.this,progressDialog);
			loginTask.execute(edtLogin.getText().toString(), edtPassword
					.getText().toString());
		}
	};

	public void showLoginError(String result) {
		CustomToast.showCustomToast(LSLoginActivity.this, result,
				CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);

		edtLogin.setText("");
		edtPassword.setText("");
	}

	public void login(String pSessionValue) {
		Intent intent = new Intent(LSLoginActivity.this, LSHomeActivity.class);

		Bundle bundle = new Bundle();
		bundle.putString("USER", edtLogin.getText().toString());
		bundle.putString("SESSION", pSessionValue);
		intent.putExtras(bundle);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("user",
				LSSecurity.rot13Encode(edtLogin.getText().toString()));
		editor.putString("pass",
				LSSecurity.rot13Encode(edtPassword.getText().toString()));
		editor.commit();

		startActivity(intent);
	}

	public class LoginTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progressDialog;
		private Activity activity;
		private String messageReturn = null;
		private Boolean loginValue = false;

		public LoginTask(Activity activity, ProgressDialog progressDialog) {
			this.activity = activity;
			this.progressDialog = progressDialog;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				if (!LSFunctions.checkConnection(LSLoginActivity.this)) {
					messageReturn = getString(R.string.msg_NotConnected);
				} else {
					Map<String, String> params = new HashMap<String, String>();
					params.put("user", edtLogin.getText().toString());
					params.put("pass", LSSecurity.encrypt(edtPassword.getText()
							.toString(), LSSecurity.Code_MD5));

					JSONObject response = LSFunctions.urlRequestJSONObject(
							"http://viuterrassa.com/Android/login.php", params);
					
					if (response != null) {
						try {
							loginValue = (Boolean) response.get("login");
							if (loginValue) {
								messageReturn = response.get("session").toString();
							} else {
								messageReturn = getString(R.string.msg_BadLoginPass);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						messageReturn = getString(R.string.msg_CommError);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {
			progressDialog.dismiss();
			if (loginValue)
				((LSLoginActivity) activity).login(pMessageReturn);
			else
				((LSLoginActivity) activity).showLoginError(pMessageReturn);
		}
	}
}
