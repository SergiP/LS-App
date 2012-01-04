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

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBar.OnActionBarListener;
import greendroid.widget.ActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.OverlayItem;

import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.mapsforge.LSNetworksOverlayForge;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class LSNetMapsForgeActivity extends MapActivity {

	private ArrayList<LSNetwork> m_networks = null;
	private List<Overlay>     mapOverlays;
	private MapView mapView;
	private LSNetworksOverlayForge itemizedOverlay;

	private final int OPTIONS = 0;
	private final int HELP = 1;

	private QuickActionWidget quickActions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_02_netmapsforge);

		initActionBar();
		initQuickActionBar();

		ActionBar actBar = (ActionBar)findViewById(R.id.gd_action_bar);
		actBar.setTitle(getResources().getString(R.string.act_lbl_homNetMaps));
		actBar.setType(Type.Normal);

		actBar.setOnActionBarListener(new OnActionBarListener() {
			public void onActionBarItemClicked(int position) {
				if (position == OnActionBarListener.HOME_ITEM) {

					Intent i = new Intent(LSNetMapsForgeActivity.this,LSHomeActivity.class);
					startActivity(i);
				} else {
					ActionBar actBar = (ActionBar)findViewById(R.id.gd_action_bar);
					onHandleActionBarItemClick(actBar.getItem(position), position);
				}
			}
		});

		mapView = (MapView)findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);

		mapOverlays = mapView.getOverlays();
		m_networks = new ArrayList<LSNetwork>();
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
					LSNetwork o1 = new LSNetwork();
					o1.setNetworkName(jsonData.getString("Nom"));
					o1.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
					o1.setNetworkNumSensors(jsonData.getString("Sensors"));
					o1.setNetworkId(jsonData.getString("IdXarxa"));
					o1.setNetworkSituation(jsonData.getString("Poblacio"));
					m_networks.add(o1);
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

		// create the default marker for overlay items
		Drawable itemDefaultMarker = getResources().getDrawable(R.drawable.marker);

		itemizedOverlay = new LSNetworksOverlayForge(itemDefaultMarker, mapView,m_networks);

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
		MapController mMapController = mapView.getController();
		mMapController.setCenter(point);
		mMapController.setZoom(5);
	}

	private void initActionBar() {

		ActionBar actBar = (ActionBar)findViewById(R.id.gd_action_bar);
		actBar.addItem(ActionBarItem.Type.Add,OPTIONS);
		actBar.addItem(ActionBarItem.Type.Help,HELP);
	}


	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		switch (item.getItemId()) {

		case OPTIONS:

			quickActions.show(item.getItemView());
			break;
		case HELP:

			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break;
		}

		return true;
	} 


	private void initQuickActionBar()
	{
		quickActions = new QuickActionBar(this);
		quickActions.addQuickAction(new QuickAction(this,R.drawable.ic_menu_search,R.string.strSearch));
		quickActions.addQuickAction(new QuickAction(this,R.drawable.ic_menu_filter,R.string.strFilter));
		quickActions.setOnQuickActionClickListener(new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {
				switch(position) {

				case 0:
					CustomToast.showCustomToast(LSNetMapsForgeActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
					break;
				case 1:
					CustomToast.showCustomToast(LSNetMapsForgeActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
					break;
				}
			}
		});
	}
}
