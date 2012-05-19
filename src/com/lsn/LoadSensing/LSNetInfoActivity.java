/*
 *    LS App - LoadSensing Application - https://github.com/SergiP/LS-App
 *    
 *    Copyright (C) 2011-2012
 *    Authors:
 *    	Sergio Gonz�lez D�ez        [sergio.gd@gmail.com]
 *    	Sergio Postigo Collado      [spostigoc@gmail.com]
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *    
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lsn.LoadSensing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LSNetInfoActivity extends ActionBarActivity {

	private LSNetwork 	networkObj = null;

	private String		networkId = null;
	private int 		cont = 0;
	private boolean 	star;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_netinfo);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			networkObj = bundle.getParcelable("NETWORK_OBJ");
			
			// Get intent (string) to AugReal
			networkId = bundle.getString("NETID");
		}
		
		if (networkId != null) {
			ProgressDialog progressDialog = new ProgressDialog(
					LSNetInfoActivity.this);
			progressDialog.setTitle(getString(R.string.msg_PleaseWait));
			progressDialog.setMessage(getString(R.string.msg_retrievData));
			progressDialog.setCancelable(true);

			// Get Network and Image
			GetNetworkData getData = new GetNetworkData(LSNetInfoActivity.this,
					progressDialog);
			getData.execute();
			
		} else {
			networkInFaves();
			networkData();
		}

		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();
		
		Button btnSensors = (Button) findViewById(R.id.btnLoadSensors);
		btnSensors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (networkObj != null) {
					Intent i = null;

					i = new Intent(LSNetInfoActivity.this,
							LSSensorListActivity.class);

					if (i != null) {
						Bundle bundle = new Bundle();
						bundle.putParcelable("NETWORK_OBJ", networkObj);

						i.putExtras(bundle);

						startActivity(i);
					}
				} else {
					CustomToast.showCustomToast(LSNetInfoActivity.this,
							R.string.msg_NetworkNotFound,
							CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
				}
			}
		});

		Button btnImages = (Button) findViewById(R.id.btnLoadImages);
		btnImages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (networkObj != null) {
					Intent i = null;
					i = new Intent(LSNetInfoActivity.this,
							LSNetImagesActivity.class);

					if (i != null) {
						Bundle bundle = new Bundle();
						bundle.putParcelable("NETWORK_OBJ", networkObj);
						i.putExtras(bundle);

						startActivity(i);
					}
				} else {
					CustomToast.showCustomToast(LSNetInfoActivity.this,
							R.string.msg_NetworkNotFound,
							CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
				}
			}
		});
	}
	
	private void networkInFaves() {
		
		star = getActionBarHelper().starNetwork(networkObj.getNetworkId());

		if (star) { // network are in faves
			getActionBarHelper().setFavesActionItem(star);
			cont = cont + 1;
		} else {
			getActionBarHelper().setFavesActionItem(star);
		}
	}
	
	private void networkData() {
		
		if (networkObj != null) {
			TextView txtNetName = (TextView) findViewById(R.id.netName);
			txtNetName.setText(networkObj.getNetworkName());
			TextView txtNetSituation = (TextView) findViewById(R.id.netSituation);
			txtNetSituation.setText(networkObj.getNetworkSituation());
			TextView txtNetPosLatitude = (TextView) findViewById(R.id.netPosLatitude);
			txtNetPosLatitude.setText(networkObj.getNetworkPosition()
					.getLatitude().toString());
			TextView txtNetPosLongitude = (TextView) findViewById(R.id.netPosLongitude);
			txtNetPosLongitude.setText(networkObj.getNetworkPosition()
					.getLongitude().toString());
			TextView txtNetPosAltitude = (TextView) findViewById(R.id.netPosAltitude);
			txtNetPosAltitude.setText(networkObj.getNetworkPosition()
					.getAltitude().toString());
			TextView txtNetNumSensors = (TextView) findViewById(R.id.netNumSensors);
			txtNetNumSensors.setText(networkObj.getNetworkNumSensors()
					.toString());
		} else {
			CustomToast.showCustomToast(this, R.string.msg_NetworkNotFound,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_star, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i = null;

		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSNetInfoActivity.this, LSNetListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			break;

		case R.id.menu_star:
			cont = cont + 1;
			if (cont % 2 == 0) {
				getActionBarHelper().setFavesActionItem(true);
				delToFaves();
			} else {
				getActionBarHelper().setFavesActionItem(false);
				insertToFaves();
			}
			break;
		}

		if (i != null) {
			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
	}

	private void insertToFaves() {

		LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
		SQLiteDatabase db = lsndbh.getWritableDatabase();
		SQLiteDatabase db1 = lsndbh.getReadableDatabase();

		if (db != null) {
			Cursor c = db1.rawQuery("SELECT * FROM Network WHERE idNetwork = '"
					+ networkObj.getNetworkId() + "';", null);
			if (c.getCount() == 0) {
				db.execSQL("INSERT INTO Network (name,poblacio,idNetwork,sensors,lat,lon) VALUES ('"
						+ networkObj.getNetworkName()
						+ "','"
						+ networkObj.getNetworkSituation()
						+ "','"
						+ networkObj.getNetworkId()
						+ "',"
						+ networkObj.getNetworkNumSensors()
						+ ",'"
						+ networkObj.getNetworkPosition().getLatitude()
						+ "','"
						+ networkObj.getNetworkPosition().getLongitude()
						+ "');");
				CustomToast.showCustomToast(this, R.string.message_add_network,
						CustomToast.IMG_CORRECT, CustomToast.LENGTH_SHORT);
			} else {
				CustomToast.showCustomToast(this,
						R.string.message_error_network,
						CustomToast.IMG_EXCLAMATION, CustomToast.LENGTH_SHORT);
			}
			db.close();
			c.close();
		}
	}

	private void delToFaves() {

		LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
		SQLiteDatabase db = lsndbh.getWritableDatabase();

		if (db != null) {
			db.execSQL("DELETE FROM Network WHERE idNetwork ='"
					+ networkObj.getNetworkId() + "'");
			CustomToast.showCustomToast(this, R.string.message_del_network,
					CustomToast.IMG_CORRECT, CustomToast.LENGTH_SHORT);
			db.close();
		}
	}
	
	public class GetNetworkData extends AsyncTask<String, Void, String> {

		private Activity activity;
		private ProgressDialog progressDialog;
		private String messageReturn = null;

		public GetNetworkData(Activity activity, ProgressDialog progressDialog) {

			this.progressDialog = progressDialog;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {

			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			boolean trobat = false;
			try {
				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				JSONArray jArray = LSFunctions.urlRequestJSONArray(
						"http://viuterrassa.com/Android/getLlistatXarxes.php",
						params);

				if (jArray != null) {
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject jsonData = jArray.getJSONObject(i);
						if ((networkId.equals(jsonData.getString("IdXarxa")) && !trobat)) {

							LSNetwork o1 = new LSNetwork();
							o1.setNetworkName(jsonData.getString("Nom"));
							o1.setNetworkPosition(jsonData.getString("Lat"),
									jsonData.getString("Lon"));
							o1.setNetworkNumSensors(jsonData
									.getString("Sensors"));
							o1.setNetworkId(jsonData.getString("IdXarxa"));
							o1.setNetworkSituation(jsonData
									.getString("Poblacio"));

							networkObj = o1;

							trobat = true;
						}
					}
				} else {
					messageReturn = getString(R.string.msg_CommError);
				}
			} catch (Exception e) {
				Log.e("BACKGROUND_PROC",
						"Exception GetNetworkData" + e.getMessage());
				messageReturn = getString(R.string.msg_ProcessError);
			}

			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {

			progressDialog.dismiss();
			((LSNetInfoActivity) activity).showError(pMessageReturn);

			networkInFaves();
			networkData();
		}
	}
	
	public void showError(String result) {

		if (result != null) {
			CustomToast.showCustomToast(LSNetInfoActivity.this, result,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}
}