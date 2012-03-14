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
import java.util.List;

import org.mapsforge.android.maps.GeoPoint;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.MapViewMode;
import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.OverlayItem;

import com.lsn.LoadSensing.actionbar.ActionBarMapOSMActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.mapsforge.LSNetworksOverlayForge;
import com.lsn.LoadSensing.ui.CustomToast;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class LSNetMapsForgeActivity extends ActionBarMapOSMActivity {

	private List<Overlay>			mapOverlays;
	private MapView					mapView;
	GeoPoint						point;
	private LSNetworksOverlayForge	itemizedOverlay;

	private ArrayList<LSNetwork> m_networks = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_02_netmapsforge);
		
		getActionBarHelper().changeIconHome();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			m_networks = bundle.getParcelableArrayList("NETWOKRS");
		}
		
		mapView = (MapView)findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.setMapViewMode(MapViewMode.MAPNIK_TILE_DOWNLOAD);

		mapOverlays = mapView.getOverlays();

		// create the default marker for overlay items
		Drawable itemDefaultMarker = getResources().getDrawable(R.drawable.marker);
		itemizedOverlay = new LSNetworksOverlayForge(itemDefaultMarker, mapView,m_networks);

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
		
		point = new GeoPoint((int)(40.416369*1E6),(int)(-3.702992*1E6));
		
		MapController mMapController = mapView.getController();
		mMapController.setCenter(point);
		mMapController.setZoom(5);
	}
	
	public void showError(String result) {
		if (result != null){
			CustomToast.showCustomToast(LSNetMapsForgeActivity.this, result,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_help, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSNetMapsForgeActivity.this, LSHomeActivity.class);
			break;
		case R.id.menu_help:
			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break; 
		case R.id.menu_config:
			i = new Intent(LSNetMapsForgeActivity.this,LSConfigActivity.class);
			break; 
		case R.id.menu_info:
			i = new Intent(LSNetMapsForgeActivity.this,LSInfoActivity.class);
			break;
		}	
		
		if (i != null) {
			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
		
	}
}