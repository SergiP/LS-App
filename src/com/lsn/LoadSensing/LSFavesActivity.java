//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio Gonz�lez D�ez        [sergio.gd@gmail.com]
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.lsn.LoadSensing.actionbar.ActionBarFragmentActivity;
import com.lsn.LoadSensing.fragments.faves.LSFavesImagesActivity;
import com.lsn.LoadSensing.fragments.faves.LSFavesNetworksActivity;
import com.lsn.LoadSensing.fragments.faves.LSFavesSensorsActivity;

/* GreenDroid -----
import greendroid.app.ActionBarActivity;
import greendroid.app.GDTabActivity;

public class LSFavesActivity extends GDTabActivity {
----------
 */
public class LSFavesActivity extends ActionBarFragmentActivity implements TabHost.OnTabChangeListener {
	
	private int currentTab = 0;
	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	private TabInfo mLastTab = null;
	
	private class TabInfo {
		private String tag;
		private Class<?> clss;
		private Bundle args;
		private Fragment fragment;
		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}
	
	class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Step 1: Inflate layout
		setContentView(R.layout.act_04_faves);
        // Step 2: Setup TabHost
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
        	mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        
        getActionBarHelper().changeIconHome();
        
        /* GreenDroid -----
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
		----------
		 */
		
        //Retrieve initial tab
		Bundle bundle = this.getIntent().getExtras();
		currentTab = bundle.getInt("par");
		
		mTabHost.setCurrentTab(currentTab);
	}

	public void onAppUrlClicked(View v) {
		final Uri appUri = Uri.parse(getString(R.string.app_url));
		startActivity(new Intent(Intent.ACTION_VIEW, appUri));
	}

	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
		super.onSaveInstanceState(outState);
	}

	/**
	 * Step 2: Setup TabHost
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		LSFavesActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(getString(R.string.tab_fav_networks)), ( tabInfo = new TabInfo("Tab1", LSFavesNetworksActivity.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		LSFavesActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(getString(R.string.tab_fav_sensors)), ( tabInfo = new TabInfo("Tab2", LSFavesSensorsActivity.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		LSFavesActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator(getString(R.string.tab_fav_images)), ( tabInfo = new TabInfo("Tab3", LSFavesImagesActivity.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		
		// Default to first tab
		this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}
	
	/**
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(LSFavesActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();
		
		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state.  If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}
		
		tabHost.addTab(tabSpec);
	}
	
	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}
			
			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}	
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_overflow, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSFavesActivity.this,LSHomeActivity.class);
				break;
		case R.id.menu_config:
			i = new Intent(LSFavesActivity.this,LSConfigActivity.class);
			break; 
		case R.id.menu_info:
			i = new Intent(LSFavesActivity.this,LSInfoActivity.class);
			break;
		}	
		
		if (i != null) {
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}
}
