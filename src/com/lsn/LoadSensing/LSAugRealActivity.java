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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.element.Position;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

public class LSAugRealActivity extends Activity {

	private LocationManager locManager;
	private LocationListener locationListenerGPS;
	private LocationListener locationListenerNetwork;
	private GetLocation getLocation;
	private Position curPosition;

	private boolean gpsStatus;
	private boolean netStatus;
	static boolean fromMixare = false;

	JSONObject mixareInfo;
	JSONArray jArray;
	JSONArray arrayObj;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
        String arrayJSON = intent.getStringExtra("ARRAY");

        try {
			arrayObj = new JSONArray(arrayJSON);
		} catch (JSONException e) {
			CustomToast.showCustomToast(LSAugRealActivity.this, R.string.msg_CommError,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}
	
	@Override
	protected void onPause() {

		if (gpsStatus || netStatus) {
			if (gpsStatus) {
				locManager.removeUpdates(locationListenerGPS);
			}

			if (netStatus) {
				locManager.removeUpdates(locationListenerNetwork);
			}

			getLocation.cancel(true);
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {

		if (gpsStatus || netStatus) {
			if (gpsStatus) {
				locManager.removeUpdates(locationListenerGPS);
			}

			if (netStatus) {
				locManager.removeUpdates(locationListenerNetwork);
			}

			getLocation.cancel(true);
		}
		super.onBackPressed();
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (locManager == null) {
			// Obtain reference to LocationManager
			locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		// Check status of location services
		checkGPSStatus();
		checkNETStatus();

		if (!gpsStatus && !netStatus) {
			// Show error message if there are no active services
			CustomToast.showCustomToast(this, R.string.msg_NOLocServ,
					CustomToast.IMG_ERROR, CustomToast.LENGTH_LONG);
		} else {
			// Obtain location using Async Task
			getLocation = new GetLocation();
			getLocation.execute();
			curPosition = getLocation.getPosition();

			if (!fromMixare) {
				boolean fileGenerated = generateJSONFile();
				if (fileGenerated) {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.parse("file://"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath() + File.separator
							+ "LSN/LSApp_mixare.json"),
							"application/mixare-lsn-json");

					fromMixare = true;
					startActivity(i);
				}
			} else {
				fromMixare = false;
				onBackPressed();
			}

		}

	}

	protected boolean generateJSONFile() {

		boolean retStatus = false;

		mixareInfo = new JSONObject();
		try {
			if (jArray != null) {

				mixareInfo.put("results", arrayObj);
				mixareInfo.put("num_results", new Integer(6));
				mixareInfo.put("status", "OK");

				boolean result = saveJSONFile(mixareInfo.toString());
				if (!result) {
					CustomToast.showCustomToast(this,
							R.string.msg_ErrorSavingFile,
							CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
				}
				retStatus = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			CustomToast.showCustomToast(LSAugRealActivity.this, R.string.msg_CommError,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}

		return retStatus;
	}

	protected boolean saveJSONFile(String info) {

		boolean retStatus = false;

		if (LSFunctions.checkSDCard(this)) {
			String folder = "LSN";
			String filename = "LSN/LSApp_mixare.json";

			File LSNFolder = new File(
					Environment.getExternalStorageDirectory(), folder);

			if (!LSNFolder.exists()) {
				LSNFolder.mkdir();
			}

			File file = new File(Environment.getExternalStorageDirectory(),
					filename);
			FileOutputStream fos;
			byte[] data = info.getBytes();

			try {
				fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close();
				retStatus = true;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return retStatus;
	}

	private void checkNETStatus() {

		try {
			gpsStatus = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

	}

	private void checkGPSStatus() {

		try {
			netStatus = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
	}

	public class GetLocation extends AsyncTask<Void, Position, Void> {
		private boolean running = true;

		@Override
		protected Void doInBackground(Void... arg0) {

			while (running) {
				getCurrentLocation();
				publishProgress(curPosition);
				SystemClock.sleep(5000);
			}
			return null;
		}

		@Override
		protected void onCancelled() {

			running = false;
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {

			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					15000, 0, locationListenerGPS);
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					15000, 0, locationListenerNetwork);
		}

		@Override
		protected void onPreExecute() {

			curPosition = new Position();

			super.onPreExecute();
			locationListenerGPS = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					curPosition = new Position(location);
				}

				@Override
				public void onProviderDisabled(String provider) {
				}

				@Override
				public void onProviderEnabled(String provider) {
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

			};

			locationListenerNetwork = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {

					curPosition = new Position(location);
				}

				@Override
				public void onProviderDisabled(String provider) {
				}

				@Override
				public void onProviderEnabled(String provider) {
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}
			};

			if (gpsStatus)
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						0, 0, locationListenerGPS);
			if (netStatus)
				locManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListenerNetwork);

		}

		@Override
		protected void onProgressUpdate(Position... values) {

			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					15000, 0, locationListenerGPS);
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					15000, 0, locationListenerNetwork);
		}

		private void getCurrentLocation() {
			locManager.removeUpdates(locationListenerGPS);
			locManager.removeUpdates(locationListenerNetwork);

			Location gpsLocation = null;
			Location netLocation = null;

			if (gpsStatus)
				gpsLocation = locManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (netStatus)
				netLocation = locManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (gpsLocation != null && netLocation != null) {
				if (gpsLocation.getTime() > netLocation.getTime()) {
					curPosition.setPosition(gpsLocation);
				} else {
					curPosition.setPosition(netLocation);
				}
			} else if (gpsLocation != null) {
				curPosition.setPosition(gpsLocation);
			} else if (netLocation != null) {
				curPosition.setPosition(netLocation);
			} else {
				return;
			}
		}

		public Position getPosition() {
			return curPosition;
		}

	}
}
