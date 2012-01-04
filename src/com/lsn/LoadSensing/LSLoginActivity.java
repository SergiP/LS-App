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
import com.lsn.LoadSensing.encript.LSSecurity;
import com.lsn.LoadSensing.func.LSFunctions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class LSLoginActivity extends Activity {
	private String strUser;
	private String strPass;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		prefs = getSharedPreferences("LSLogin",Context.MODE_PRIVATE);

		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		EditText edtLogin = (EditText)findViewById(R.id.edtLogin);
		EditText edtPassword = (EditText)findViewById(R.id.edtPassword);

		edtLogin.setText(LSSecurity.rot13Decode(prefs.getString("user", "")));
		edtPassword.setText(LSSecurity.rot13Decode(prefs.getString("pass", "")));

		btnLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				EditText edtLogin = (EditText)findViewById(R.id.edtLogin);
				EditText edtPassword = (EditText)findViewById(R.id.edtPassword);
				strUser = edtLogin.getText().toString();
				strPass = edtPassword.getText().toString();

				if (!LSFunctions.checkConnection(LSLoginActivity.this))
				{
					CustomToast.showCustomToast(LSLoginActivity.this,R.string.msg_NotConnected,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
				}
				else
				{
					Map<String, String> params = new HashMap<String, String>();
					params.put("user", strUser);
					params.put("pass", LSSecurity.encrypt(strPass,LSSecurity.Code_MD5));
					JSONObject response = LSFunctions.urlRequestJSONObject("http://viuterrassa.com/Android/login.php",params);
					if (response!=null)
					{
						Boolean loginValue = null;
						String sessionValue = null;
						try {
							loginValue = (Boolean) response.get("login");
							sessionValue = response.get("session").toString();

						} catch (JSONException e) {

							e.printStackTrace();
						}

						//if ((strUser.equals("sergio")) && (strPass.equals("sergio"))) {
						if (loginValue) {

							Intent intent = new Intent(LSLoginActivity.this,LSHomeActivity.class);

							Bundle bundle = new Bundle();
							bundle.putString("USER", strUser);
							bundle.putString("SESSION", sessionValue);
							intent.putExtras(bundle);

							SharedPreferences.Editor editor = prefs.edit();
							editor.putString("user", LSSecurity.rot13Encode(strUser));
							editor.putString("pass", LSSecurity.rot13Encode(strPass));
							editor.commit();

							startActivity(intent);
						}
						else // user doesn't exist
						{ 
							CustomToast.showCustomToast(LSLoginActivity.this,R.string.msg_BadLoginPass,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
							edtLogin.setText("");
							edtPassword.setText("");
						}
					}
					else
					{
						CustomToast.showCustomToast(LSLoginActivity.this,R.string.msg_CommError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
					}
				}
			}
		});
	}
}
