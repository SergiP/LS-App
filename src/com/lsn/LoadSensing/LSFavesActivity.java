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

import greendroid.app.ActionBarActivity;
import greendroid.app.GDTabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lsn.LoadSensing.faves.LSFavesImagesActivity;
import com.lsn.LoadSensing.faves.LSFavesNetworksActivity;
import com.lsn.LoadSensing.faves.LSFavesSensorsActivity;

public class LSFavesActivity extends GDTabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_04_faves);

		//Setting Faves Networks Tab
		final String listText =  getString(R.string.tab_fav_networks);
		final Intent listIntent = new Intent(this, LSFavesNetworksActivity.class);
		listIntent.putExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.GONE);
		addTab(listText, listText, listIntent);

		//Setting Faves Sensors Tab
		final String sensorText =  getString(R.string.tab_fav_sensors);
		final Intent sensorIntent = new Intent(this, LSFavesSensorsActivity.class);
		sensorIntent.putExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.GONE);
		addTab(sensorText, sensorText, sensorIntent);

		//Setting Faves Images Tab
		final String imageText =  getString(R.string.tab_fav_images);
		final Intent imageIntent = new Intent(this, LSFavesImagesActivity.class);
		imageIntent.putExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.GONE);
		addTab(imageText, imageText, imageIntent);

		//Retrieve initial tab
		Bundle bundle = this.getIntent().getExtras();
		int i = bundle.getInt("par");

		this.getTabHost().setCurrentTab(i);

	}
}
