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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lsn.LoadSensing.actionbar.ActionBarListActivity;
import com.lsn.LoadSensing.adapter.LSNetworkAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class LSNetListActivity extends ActionBarListActivity {

	private ProgressDialog      	m_ProgressDialog = null;
	private ArrayList<LSNetwork>	m_networks = null;
	private LSNetworkAdapter    	m_adapter;
	private Runnable            	viewNetworks;
	private Integer  				errMessage;
	private EditText 				filterText = null;
	private int						filter = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_01_netlist);

		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();
		
		m_networks = new ArrayList<LSNetwork>();
		m_adapter = new LSNetworkAdapter(this,R.layout.row_list_network,m_networks);
		setListAdapter(this.m_adapter);
		
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);
		
		viewNetworks = new Runnable() {
			@Override
			public void run() {
				getNetworks();
			}
		};
		
		Thread thread = new Thread(null,viewNetworks,"ViewNetworks");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievNetworks), true);
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
			
			if(m_networks != null && m_networks.size() > 0){
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_networks.size();i++)
					m_adapter.add(m_networks.get(i));
			}
			
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private Runnable returnErr = new Runnable() {
		
		@Override
		public void run() {
			
			CustomToast.showCustomToast(LSNetListActivity.this,errMessage,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}
	};

	private void getNetworks() {
		
		try{
			m_networks = new ArrayList<LSNetwork>();
			
			// Server Request Ini
			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistatXarxes.php",params);

			if (jArray != null) {
				for (int i = 0; i<jArray.length(); i++) {
					JSONObject jsonData = jArray.getJSONObject(i);
					LSNetwork o1 = new LSNetwork();
					o1.setNetworkName(jsonData.getString("Nom"));
					o1.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
					o1.setNetworkNumSensors(jsonData.getString("Sensors"));
					o1.setNetworkId(jsonData.getString("IdXarxa"));
					o1.setNetworkSituation(jsonData.getString("Poblacio"));
					m_networks.add(o1);
				}
			} else {
				errMessage = R.string.msg_CommError;
				runOnUiThread(returnErr); 
			}
			
			Log.i("ARRAY NETWORKS", ""+ m_networks.size());
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
		menuInflater.inflate(R.menu.ab_item_search, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent i = null;
		
		switch (item.getItemId()) {
		
		case android.R.id.home:	
			i = new Intent(LSNetListActivity.this, LSHomeActivity.class);
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
		}	
		
		if (i != null) {
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}
}