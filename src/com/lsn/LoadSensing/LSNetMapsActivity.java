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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.lsn.LoadSensing.actionbar.ActionBarMapActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.map.LSNetworksOverlay;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LSNetMapsActivity extends ActionBarMapActivity {

	MapView				mapView;
	List<Overlay>		mapOverlays;
	Drawable			drawable;
	GeoPoint			point;
	LSNetworksOverlay	itemizedOverlay;
	boolean				modeStreeView;

	private ArrayList<LSNetwork> m_networks = null;

	@Override
	protected void onResume() {
		getActionBarHelper().changeIconHome();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_02_netmap);
		
		mapView = (MapView) findViewById(R.id.netmap);
		mapView.setBuiltInZoomControls(true);
		setStreetView();

		mapOverlays = mapView.getOverlays();
		
		// overlay
		drawable = getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new LSNetworksOverlay(drawable, mapView,m_networks);
		
		ProgressDialog progressDialog = new ProgressDialog(LSNetMapsActivity.this);
		progressDialog.setTitle(getString(R.string.msg_PleaseWait));
		progressDialog.setMessage(getString(R.string.msg_retrievNetworks));
		progressDialog.setCancelable(false);

		MapTask mapTask = new MapTask(LSNetMapsActivity.this,progressDialog);
		mapTask.execute();
					
		point = new GeoPoint((int)(40.416369*1E6),(int)(-3.702992*1E6));
		final MapController mc = mapView.getController();
		mc.animateTo(point);
		mc.setZoom(6);
	}
	
	public void showError(String result) {
		if (result != null){
			CustomToast.showCustomToast(LSNetMapsActivity.this, result,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}

	@SuppressWarnings("deprecation")
	private void setStreetView(){

		final MapView mapView = (MapView) findViewById(R.id.netmap);
		mapView.setSatellite(false);
		mapView.setStreetView(true);
		modeStreeView=true;
	}

	@SuppressWarnings("deprecation")
	private void setSatelliteView(){

		final MapView mapView = (MapView) findViewById(R.id.netmap);
		mapView.setSatellite(true);
		mapView.setStreetView(false);
		modeStreeView=false;
	}

	/**
	 * @return the modeStreeView
	 */
	public boolean isModeStreeView() {
		return modeStreeView;
	}
	/**
	 * @param modeStreeView the modeStreeView to set
	 */
	public void setModeStreeView(boolean modeStreeView) {
		this.modeStreeView = modeStreeView;
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_mapmode_help, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSNetMapsActivity.this, LSHomeActivity.class);
			break;
		case R.id.menu_mapmode:
			//Switch map mode
			if (isModeStreeView()) {
				setSatelliteView();
			}
			else {
				setStreetView();
			}
			break; 
		case R.id.menu_help:
			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break; 
		case R.id.menu_config:
			i = new Intent(LSNetMapsActivity.this,LSConfigActivity.class);
			break; 
		case R.id.menu_info:
			i = new Intent(LSNetMapsActivity.this,LSInfoActivity.class);
			break;
		}	
		
		if (i != null) {
			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
		
	}

	public class MapTask extends AsyncTask<String, Void, String> {
		private Activity activity;
		private ProgressDialog progressDialog;
		private String messageReturn = null;
		
		public MapTask(Activity activity, ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			JSONObject jsonData;
			m_networks = new ArrayList<LSNetwork>();
			try {
				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistatXarxes.php",params);
				if (jArray != null)
				{
					for (int i = 0; i<jArray.length(); i++)
					{
						jsonData = jArray.getJSONObject(i);
						LSNetwork network = new LSNetwork();
						network.setNetworkName(jsonData.getString("Nom"));
						network.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
						network.setNetworkNumSensors(jsonData.getString("Sensors"));
						network.setNetworkId(jsonData.getString("IdXarxa"));
						network.setNetworkSituation(jsonData.getString("Poblacio"));
						m_networks.add(network);
					}
				}
				else
				{
					messageReturn = getString(R.string.msg_CommError);
				}
			} catch (JSONException e) {
				messageReturn = getString(R.string.msg_ProcessError);
			}
			
			point = null;
			for (int i = 0; i< m_networks.size(); i++)
			{
				Integer intLat = (int) (m_networks.get(i).getNetworkPosition().getLatitude()*1e6);
				Integer intLon = (int) (m_networks.get(i).getNetworkPosition().getLongitude()*1e6);
				String strName = m_networks.get(i).getNetworkName();
				String strSituation = m_networks.get(i).getNetworkSituation();
				Integer strNumSensor = m_networks.get(i).getNetworkNumSensors();
				String strNetDescripFormat = getResources().getString(R.string.strNetDescrip);
				String strNetDescrip = String.format(strNetDescripFormat, strSituation, strNumSensor);

				point = new GeoPoint(intLat,intLon);
				OverlayItem overlayItem = new OverlayItem(point, strName, strNetDescrip);
				itemizedOverlay.addOverlay(overlayItem);

				mapOverlays.add(itemizedOverlay);
			}
			
			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {
			progressDialog.dismiss();
			((LSNetMapsActivity) activity).showError(pMessageReturn);
		}
	}
}