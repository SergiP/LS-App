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

package com.lsn.LoadSensing.map;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.lsn.LoadSensing.LSNetInfoActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class LSNetworksOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context c;
	private ArrayList<LSNetwork> m_networks;

	public LSNetworksOverlay(Drawable defaultMarker, MapView mapView,ArrayList<LSNetwork> networks) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		m_networks = networks;
	}

	public void addOverlay(OverlayItem overlay) {
		m_overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		
		Intent i = null;
		i = new Intent(c,LSNetInfoActivity.class);

		if (i!=null){
			Bundle bundle = new Bundle();

			//bundle.putString("SESSION", LSHomeActivity.idSession);
			bundle.putParcelable("NETWORK_OBJ", m_networks.get(index));

			i.putExtras(bundle);

			c.startActivity(i);
		}
		return true;
	}

}
