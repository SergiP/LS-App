/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsn.LoadSensing.actionbar;

import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * An abstract class that handles some common action bar-related functionality
 * in the app. This class provides functionality useful for both phones and
 * tablets, and does not require any Android 3.0-specific features, although it
 * uses them if available.
 * 
 * Two implementations of this class are {@link ActionBarHelperBase} for a
 * pre-Honeycomb version of the action bar, and {@link ActionBarHelperHoneycomb}
 * , which uses the built-in ActionBar features in Android 3.0 and later.
 */
public abstract class ActionBarHelper {
	protected Activity mActivity;
	protected boolean trobat;

	/**
	 * Factory method for creating {@link ActionBarHelper} objects for a given
	 * activity. Depending on which device the app is running, either a basic
	 * helper or Honeycomb-specific helper will be returned.
	 */
	public static ActionBarHelper createInstance(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return new ActionBarHelperICS(activity);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return new ActionBarHelperHoneycomb(activity);
		} else {
			return new ActionBarHelperBase(activity);
		}
	}

	protected ActionBarHelper(Activity activity) {
		mActivity = activity;
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onCreate(android.os.Bundle)}.
	 */
	public void onCreate(Bundle savedInstanceState) {
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onPostCreate(android.os.Bundle)}.
	 */
	public void onPostCreate(Bundle savedInstanceState) {
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * 
	 * NOTE: Setting the visibility of menu items in <em>menu</em> is not
	 * currently supported.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onTitleChanged(CharSequence, int)}.
	 */
	protected void onTitleChanged(CharSequence title, int color) {
	}

	/**
	 * Returns a {@link MenuInflater} for use when inflating menus. The
	 * implementation of this method in {@link ActionBarHelperBase} returns a
	 * wrapped menu inflater that can read action bar metadata from a menu
	 * resource pre-Honeycomb.
	 */
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return superMenuInflater;
	}

	public void changeIconHome() {
	}
	
	public void optionsMenuInfo(Menu menu) {
	}
	
	public void optionsMenuConfig(Menu menu) {
	}
	
	public void optionsMenuHelp(Menu menu) {
	}
	
	public void optionsMenuHome(Menu menu) {
	}

	public void setFavesActionItem(boolean faves) {
	}

	public boolean starNetwork(String networkId) {

		trobat = false;

		try {

			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(mActivity, "DBLSN",
					null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

			if (db != null) {

				Cursor c = db.rawQuery("SELECT * FROM Network", null);
				c.moveToFirst();
				if (c != null) {

					while (!c.isAfterLast() && !trobat) {

						if (c.getString(c.getColumnIndex("idNetwork")).equals(
								networkId)) {
							trobat = true;
						}

						c.move(1);
					}
				}
				c.close();
				db.close();
			}
		} catch (Exception e) {

			Log.e("BACKGROUND_PROC", "starNetwork()" + e.getMessage());
		}

		return trobat;
	}

	public boolean starSensor(String sensorId) {
		trobat = false;

		try {
			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(mActivity, "DBLSN",
					null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

			if (db != null) {

				Cursor c = db.rawQuery("SELECT * FROM Sensor", null);
				c.moveToFirst();
				if (c != null) {

					while (!c.isAfterLast() && !trobat) {

						if (c.getString(c.getColumnIndex("idSensor")).equals(
								sensorId)) {
							trobat = true;
						}

						c.move(1);
					}
				}
				c.close();
				db.close();
			}
		} catch (Exception e) {

			Log.e("BACKGROUND_PROC", "starSensor()" + e.getMessage());
		}

		return trobat;
	}

	public boolean starImage(String imageId) {
		trobat = false;

		try {
			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(mActivity, "DBLSN",
					null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

			if (db != null) {

				Cursor c = db.rawQuery("SELECT * FROM Image", null);
				c.moveToFirst();
				if (c != null) {

					while (!c.isAfterLast() && !trobat) {

						if (c.getString(c.getColumnIndex("idImage")).equals(
								imageId)) {
							trobat = true;
						}

						c.move(1);
					}
				}
				c.close();
				db.close();
			}
		} catch (Exception e) {

			Log.e("BACKGROUND_PROC", "starImage()" + e.getMessage());
		}

		return trobat;
	}
}
