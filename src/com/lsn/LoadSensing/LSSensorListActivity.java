/*
 *    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
 *    
 *    Copyright (C) 2011-2012
 *    Authors:
 *    	Sergio González Díez        [sergio.gd@gmail.com]
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lsn.LoadSensing.actionbar.ActionBarListActivity;
import com.lsn.LoadSensing.adapter.LSSensorAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LSSensorListActivity extends ActionBarListActivity {

	private ProgressDialog       			m_ProgressDialog = null;
	private ArrayList<LSSensor>  			m_sensors = null;
	private LSSensorAdapter      			m_adapter;
	private Runnable             			viewSensors;
	private LSNetwork 						networkObj;
	private static HashMap<String,Bitmap>	hashImages = new HashMap<String,Bitmap>();
	private Bitmap 							imgSensor;
	private Integer 						errMessage;
	private EditText 						filterText = null;
	private int								filter = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sensorlist);
		
		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();
		
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {		
			
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		} 
		
		m_sensors = new ArrayList<LSSensor>();
		this.m_adapter = new LSSensorAdapter(this,R.layout.row_list_sensor,m_sensors);
		setListAdapter(this.m_adapter);
		
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);
		
		TextView txtNetName = (TextView)findViewById(R.id.netName);
		txtNetName.setText(networkObj.getNetworkName());

		viewSensors = new Runnable() {
			@Override
			public void run() {
				
				getSensors();
			}
		};
		Thread thread = new Thread(null,viewSensors,"ViewSensors");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievSensors), true);
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {
		
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	
	    	m_adapter.getFilter().filter(s);
	    }

		@Override
		public void afterTextChanged(Editable s) {	
			
		}
	};

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			
			if(m_sensors != null && m_sensors.size() > 0){
				
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_sensors.size();i++)
					
					m_adapter.add(m_sensors.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private Runnable returnErr = new Runnable() {
		
		@Override
		public void run() {
			
			CustomToast.showCustomToast(LSSensorListActivity.this,errMessage,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}
	};

	private void getSensors() {
		
		try{
			
			m_sensors = new ArrayList<LSSensor>();

			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			params.put("IdXarxa", networkObj.getNetworkId());
			JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistaSensors.php",params);

			if (jArray != null) {
				
				for (int i = 0; i<jArray.length(); i++) {
					
					JSONObject jsonData = jArray.getJSONObject(i);
					LSSensor s1 = new LSSensor();
					s1.setSensorId(jsonData.getString("id"));
					s1.setSensorName(jsonData.getString("sensor"));
					s1.setSensorChannel(jsonData.getString("canal"));
					s1.setSensorType(jsonData.getString("tipus"));
					String image = jsonData.getString("imatge");
					if (hashImages.containsKey(image)) {
						
						imgSensor = hashImages.get(image);
					} else {
						
						imgSensor = LSFunctions.getRemoteImage(new URL("http://viuterrassa.com/Android/Imatges/"+image));
						hashImages.put(image, imgSensor);
					}
					s1.setSensorImage(imgSensor);
					s1.setSensorDesc(jsonData.getString("Descripcio"));
					s1.setSensorSituation(jsonData.getString("Poblacio"));
					s1.setSensorNetwork(jsonData.getString("Nom"));
					s1.setSensorImageName(image);
					m_sensors.add(s1);
				}
			} else {
				errMessage = R.string.msg_CommError;
				runOnUiThread(returnErr); 
			}

			Log.i("ARRAY SENSORS", ""+ m_sensors.size());
		} catch (Exception e) { 
			
			Log.e("BACKGROUND_PROC", e.getMessage());
			errMessage = R.string.msg_ProcessError;
			runOnUiThread(returnErr); 
		}
		runOnUiThread(returnRes);		
	}

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_search_ov_help, menu);
        
		getActionBarHelper().optionsMenuHelp(menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent i = null;
		
		switch (item.getItemId()) {
		
		case android.R.id.home:
			
			i = new Intent(LSSensorListActivity.this, LSNetInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("NETWORK_OBJ", networkObj);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtras(bundle);
			break;
			
		case R.id.menu_search:
			
			filter++;
			InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			
			if (filter % 2 == 0) {
				filterText.setVisibility(View.VISIBLE);
				filterText.requestFocus();
				inputMgr.toggleSoftInput(0, 0);
			} else {
				
				filterText.setVisibility(View.INVISIBLE);
				filterText.setText("");
				inputMgr.hideSoftInputFromWindow(filterText.getWindowToken(), 0);
			}
			break; 
			
		case R.id.menu_help:
			
			// TODO
			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break; 
			
		}	
		
		if (i != null) {
			
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}
}