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
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.map.LSNetworksOverlay;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import greendroid.app.GDMapActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class LSNetMapsActivity extends GDMapActivity {

	MapView           mapView;
	List<Overlay>     mapOverlays;
	Drawable          drawable;
	Drawable          drawable2;
	LSNetworksOverlay itemizedOverlay;
	LSNetworksOverlay itemizedOverlay2;
	boolean           modeStreeView;

	private final int OPTIONS = 0;
	private final int HELP = 1;

	private QuickActionWidget quickActions;

	//private static String idSession;
	private ArrayList<LSNetwork> m_networks = null;


	@Override
	protected void onResume() {

		super.onResume();

	}

	@Override
	protected boolean isRouteDisplayed() {

		return false;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.act_02_netmap);

		initActionBar();
		initQuickActionBar();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			//idSession = bundle.getString("SESSION");
		}  
		m_networks = new ArrayList<LSNetwork>();


		final MapView mapView = (MapView) findViewById(R.id.netmap);
		mapView.setBuiltInZoomControls(true);
		setStreetView();

		JSONObject jsonData;
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
				CustomToast.showCustomToast(this,R.string.msg_CommError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
			}
		} catch (JSONException e) {

			e.printStackTrace();
			CustomToast.showCustomToast(this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}

		mapOverlays = mapView.getOverlays();

		// first overlay
		drawable = getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new LSNetworksOverlay(drawable, mapView,m_networks);

		GeoPoint point = null;
		for (int i = 0; i<m_networks.size(); i++)
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

		point = new GeoPoint((int)(40.416369*1E6),(int)(-3.702992*1E6));
		final MapController mc = mapView.getController();
		mc.animateTo(point);
		mc.setZoom(6);

	}

	private void setStreetView(){

		final MapView mapView = (MapView) findViewById(R.id.netmap);
		mapView.setSatellite(false);
		mapView.setStreetView(true);
		modeStreeView=true;
	}

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

	private void initActionBar() {

		addActionBarItem(Type.Add,OPTIONS);
		addActionBarItem(Type.Help,HELP);

	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		switch (item.getItemId()) {

		case OPTIONS:

			quickActions.show(item.getItemView());
			break;
		case HELP:

			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	} 

	private void initQuickActionBar()
	{
		quickActions = new QuickActionBar(this);
		quickActions.addQuickAction(new QuickAction(this,R.drawable.ic_menu_search,R.string.strSearch));
		quickActions.addQuickAction(new QuickAction(this,R.drawable.ic_menu_filter,R.string.strFilter));
		quickActions.addQuickAction(new QuickAction(this,android.R.drawable.ic_menu_mapmode,R.string.strMapMode));
		quickActions.setOnQuickActionClickListener(new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch(position) {

				case 0:
					CustomToast.showCustomToast(LSNetMapsActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
					break;
				case 1:
					CustomToast.showCustomToast(LSNetMapsActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
					break;
				case 2:
					//Switch map mode
					if (isModeStreeView()) {

						setSatelliteView();
					}
					else {
						setStreetView();
					}
					break;
				}
			}
		});
	}

}
