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
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.adapter.LSNetworkAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.ui.CustomToast;
import com.lsn.LoadSensing.element.Position;
import com.lsn.LoadSensing.func.LSFunctions;
import com.readystatesoftware.mapviewballoons.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import greendroid.app.GDListActivity;

public class LSNetCloserActivity extends GDListActivity{


	private LocationManager  locManager;
	private LocationListener locationListenerGPS;
	private LocationListener locationListenerNetwork;
	private GetLocation      getLocation;
	private UpdateNetworks   updateNetworks;
	private boolean gpsStatus;
	private boolean netStatus;

	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSNetwork> m_networks = null;
	private LSNetworkAdapter     m_adapter;
	private Runnable             viewNetworks;

	private Position currentPosition;
	private String typeUnit = null;
	private Integer maxDistance = 0;
	private Integer waitTime = 0;

	private static boolean running;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_06_netcloser);

		m_networks = new ArrayList<LSNetwork>();
		this.m_adapter = new LSNetworkAdapter(this,R.layout.row_list_network,m_networks);
		setListAdapter(this.m_adapter);

		//Allow the execution of async tasks
		running= true;
		//Retrieve configuration preferences
		retrievePreferences();

		if (locManager == null)
		{
			//Obtain reference to LocationManager
			locManager = 
					(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		}
		//Check status of location services
		checkGPSStatus();
		checkNETStatus();


		if (!gpsStatus && !netStatus)
		{
			//Show error message if there are no active services
			CustomToast.showCustomToast(this,
					R.string.msg_NOLocServ,
					CustomToast.IMG_ERROR,
					CustomToast.LENGTH_LONG);

			TextView txtLocation = (TextView)findViewById(R.id.txtLocation);
			txtLocation.setBackgroundColor(Color.RED);
			txtLocation.setText(R.string.msg_NOLocServ);
			
			String strDisplayInfoFormat = getResources().getString(R.string.strDisplayInfo);
			
			String strRadius = maxDistance + " " + typeUnit;
			String strDisplayInfo = String.format(strDisplayInfoFormat, strRadius, m_networks.size());  
			TextView txtDisplayInfo = (TextView)findViewById(R.id.txtDisplayInfo);
			txtDisplayInfo.setText(strDisplayInfo);
		}
		else
		{
			//Obtain location using Async Task
			getLocation = new GetLocation();
			getLocation.execute();

			//Update list using Async Task
			updateNetworks = new UpdateNetworks();
			updateNetworks.execute();

			//Feed list with closer networks
			viewNetworks = new Runnable()
			{

				@Override
				public void run() {

					getNetworks();
				}
			};


			Thread thread = new Thread(null,viewNetworks,"ViewNetworks");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievNetworks), true);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//Allow the execution of async tasks
		running= true;
		//Retrieve configuration preferences
		retrievePreferences();
	}    

	private void retrievePreferences()
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		typeUnit=settings.getString("netcloserunit", "km");
		maxDistance=settings.getInt("netcloserdist", 10);
		waitTime=settings.getInt("netclosertime",10);

	}

	private void checkNETStatus() {

		try
		{
			gpsStatus = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception ex) {}

	}


	private void checkGPSStatus() {

		try
		{
			netStatus = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch(Exception ex) {}
	}


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

			String strDisplayInfoFormat = getResources().getString(R.string.strDisplayInfo);

			String strRadius = maxDistance + " " + typeUnit;
			String strDisplayInfo = String.format(strDisplayInfoFormat, strRadius, m_networks.size());  
			TextView txtDisplayInfo = (TextView)findViewById(R.id.txtDisplayInfo);
			txtDisplayInfo.setText(strDisplayInfo);
		}
	};

	private Runnable returnErr = new Runnable() {

		@Override
		public void run() {

			TextView txtLocation = (TextView)findViewById(R.id.txtLocation);
			txtLocation.setBackgroundColor(Color.RED);
			txtLocation.setText(R.string.msg_NOLocServ);

		}
	};


	private void getNetworks() {

		try{
			m_networks = new ArrayList<LSNetwork>();

			// Server Request Ini
			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistatXarxes.php",params);

			if (jArray != null)
			{
				for (int i = 0; i<jArray.length(); i++)
				{
					JSONObject jsonData = jArray.getJSONObject(i);
					LSNetwork network = new LSNetwork();
					network.setNetworkName(jsonData.getString("Nom"));
					network.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
					network.setNetworkNumSensors(jsonData.getString("Sensors"));
					network.setNetworkId(jsonData.getString("IdXarxa"));
					network.setNetworkSituation(jsonData.getString("Poblacio"));

					if (currentPosition!=null)
					{
						//Check distance unit configured
						if (typeUnit.equals("mi")) //miles
						{
							//Check if current network is closer than maxDistance configured in miles
							if (network.getNetworkPosition().milesDistanceTo(currentPosition) < maxDistance)
							{
								m_networks.add(network);
							}
						}
						else //kilometers or not configured
						{
							//Check if current network is closer than maxDistance configured in kilometers
							if ((network.getNetworkPosition().metersDistanceTo(currentPosition)/1000) < maxDistance)
							{
								m_networks.add(network);
							}
						}
					}
					else
					{
						runOnUiThread(returnErr); 
					}
				}

				// Server Request End  
			}
			else
			{
				runOnUiThread(returnErr); 
			}
			//          LSNetwork o1 = new LSNetwork();
			//          o1.setNetworkName("Network 1");
			//          o1.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o1.setNetworkNumSensors(3);
			//          LSNetwork o2 = new LSNetwork();
			//          o2.setNetworkName("Network 2");
			//          o2.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o2.setNetworkNumSensors(2);
			//          LSNetwork o3 = new LSNetwork();
			//          o3.setNetworkName("Network 3");
			//          o3.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o3.setNetworkNumSensors(4);
			//          LSNetwork o4 = new LSNetwork();
			//          o4.setNetworkName("Network 4");
			//          o4.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o4.setNetworkNumSensors(5);
			//          LSNetwork o5 = new LSNetwork();
			//          o5.setNetworkName("Network 5");
			//          o5.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o5.setNetworkNumSensors(6);
			//          LSNetwork o6 = new LSNetwork();
			//          o6.setNetworkName("Network 6");
			//          o6.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o6.setNetworkNumSensors(7);
			//          LSNetwork o7 = new LSNetwork();
			//          o7.setNetworkName("Network 7");
			//          o7.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o7.setNetworkNumSensors(5);
			//          LSNetwork o8 = new LSNetwork();
			//          o8.setNetworkName("Network 8");
			//          o8.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o8.setNetworkNumSensors(6);
			//          LSNetwork o9 = new LSNetwork();
			//          o9.setNetworkName("Network 9");
			//          o9.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o9.setNetworkNumSensors(4);
			//          LSNetwork o10 = new LSNetwork();
			//          o10.setNetworkName("Network 10");
			//          o10.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//          o10.setNetworkNumSensors(3);
			//          
			//          m_networks.add(o1);
			//          m_networks.add(o2);
			//          m_networks.add(o3);
			//          m_networks.add(o4);
			//          m_networks.add(o5);
			//          m_networks.add(o6);
			//          m_networks.add(o7);
			//          m_networks.add(o8);
			//          m_networks.add(o9);
			//          m_networks.add(o10);
			//
			//          Thread.sleep(500);
			Log.i("ARRAY", ""+ m_networks.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());

			runOnUiThread(returnErr);
		}
		runOnUiThread(returnRes);		
	}

	@Override
	public void onBackPressed() {

		//Disable execution of Async Tasks
		running= false;

		//Disable updates of location when user goes out of the activity
		if (gpsStatus || netStatus)
		{
			if (gpsStatus) {
				locManager.removeUpdates(locationListenerGPS);
			}

			if (netStatus) {
				locManager.removeUpdates(locationListenerNetwork);
			}

			Log.i("INFO", "Cancel Async Task");
			getLocation.cancel(true);
			updateNetworks.cancel(true);
		}
		super.onBackPressed();
	}


	@Override
	protected void onPause() {
		
		//Disable execution of Async Tasks
		running= false;

		//Disable updates of location when user goes out of the activity
		if (gpsStatus || netStatus)
		{
			if (gpsStatus) {
				locManager.removeUpdates(locationListenerGPS);
			}

			if (netStatus) {
				locManager.removeUpdates(locationListenerNetwork);
			}

			Log.i("INFO", "Cancel Async Task");
			getLocation.cancel(true);
			updateNetworks.cancel(true);
		}
		super.onPause();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent i = null;
		i = new Intent(LSNetCloserActivity.this,LSNetInfoActivity.class);

		if (i!=null){
			Bundle bundle = new Bundle();

			bundle.putParcelable("NETWORK_OBJ", m_networks.get(position));
			i.putExtras(bundle);

			startActivity(i);
		}
	}

	public class GetLocation extends AsyncTask<Void,Position,Void>
	{
		private Position curPosition;
		//private boolean running = true;
		private boolean displayInfo = true;

		@Override
		protected Void doInBackground(Void... arg0) {

			while (running)
			{
				getCurrentLocation();
				publishProgress(curPosition);
				//SystemClock.sleep(5000);
			}
			return null;
		}


		@Override
		protected void onCancelled() {

			running=false;
			super.onCancelled();
		}


		@Override
		protected void onPostExecute(Void result) {

			locManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 15000, 0, locationListenerGPS);
			locManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 15000, 0, locationListenerNetwork);

			TextView txtLocation = (TextView)findViewById(R.id.txtLocation);

			String strYourLocationFormat = getResources().getString(R.string.strYourLocation);
			String strYourLocation = String.format(strYourLocationFormat, curPosition.getLatitudeStr(), curPosition.getLongitudeStr());  

			txtLocation.setBackgroundColor(Color.parseColor("#75b0e4"));   
			txtLocation.setText(strYourLocation);
		}


		@Override
		protected void onPreExecute() {

			curPosition = new Position();

			super.onPreExecute();
			locationListenerGPS = new LocationListener()
			{

				@Override
				public void onLocationChanged(Location location) {
					curPosition = new Position(location);
				}

				@Override
				public void onProviderDisabled(String provider) {}

				@Override
				public void onProviderEnabled(String provider) {}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {}

			};

			locationListenerNetwork = new LocationListener()
			{
				@Override
				public void onLocationChanged(Location location) {

					curPosition = new Position(location);
				}

				@Override
				public void onProviderDisabled(String provider) {}

				@Override
				public void onProviderEnabled(String provider) {}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {}
			};

			if (gpsStatus) locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
			if (netStatus) locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);

		}


		@Override
		protected void onProgressUpdate(Position... values) {



			locManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 15000, 0, locationListenerGPS);
			locManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 15000, 0, locationListenerNetwork);


			TextView txtLocation = (TextView)findViewById(R.id.txtLocation);

			String strYourLocationFormat = getResources().getString(R.string.strYourLocation);
			String strYourLocation = String.format(strYourLocationFormat, curPosition.getLatitudeStr(), curPosition.getLongitudeStr());  
			txtLocation.setBackgroundColor(Color.parseColor("#75b0e4"));
			txtLocation.setText(strYourLocation);

			if (displayInfo)
			{
				String strDisplayInfoFormat = getResources().getString(R.string.strDisplayInfo);

				String strRadius = maxDistance + " " + typeUnit;
				String strDisplayInfo = String.format(strDisplayInfoFormat, strRadius, m_networks.size());  
				TextView txtDisplayInfo = (TextView)findViewById(R.id.txtDisplayInfo);
				txtDisplayInfo.setText(strDisplayInfo);
				displayInfo = false;
			}
			currentPosition = curPosition;
		}

		public void getCurrentLocation()
		{
			locManager.removeUpdates(locationListenerGPS);
			locManager.removeUpdates(locationListenerNetwork);

			Location gpsLocation=null;
			Location netLocation=null;

			if (gpsStatus) gpsLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (netStatus) netLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (gpsLocation!=null && netLocation != null)
			{
				if (gpsLocation.getTime() > netLocation.getTime())
				{
					curPosition.setPosition(gpsLocation);
				}
				else
				{
					curPosition.setPosition(netLocation);
				}
			}
			else if (gpsLocation !=  null)
			{
				curPosition.setPosition(gpsLocation);			
			}
			else if (netLocation !=  null)
			{
				curPosition.setPosition(netLocation);			
			}
			else
			{
				return;
			}
		}
	}

	public class UpdateNetworks extends AsyncTask<Void,ArrayList<LSNetwork>,Void>
	{

		private Position lastPosition;
		//private ArrayList<LSNetwork> m_networks = null;

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... arg0) {

			while (running)
			{
				SystemClock.sleep(waitTime*1000);
				m_networks = new ArrayList<LSNetwork>();

				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistatXarxes.php",params);
				Log.i("INFO", "Call to server");

				if (jArray != null)
				{
					JSONObject jsonData;

					try {
						for (int i = 0; i<jArray.length(); i++)
						{
							jsonData = jArray.getJSONObject(i);
							LSNetwork network = new LSNetwork();
							network.setNetworkName(jsonData.getString("Nom"));
							network.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
							network.setNetworkNumSensors(jsonData.getString("Sensors"));
							network.setNetworkId(jsonData.getString("IdXarxa"));
							network.setNetworkSituation(jsonData.getString("Poblacio"));

							//Check distance unit configured
							if (typeUnit.equals("mi")) //miles
							{
								//Check if current network is closer than maxDistance configured in miles
								if (network.getNetworkPosition().milesDistanceTo(currentPosition) < maxDistance)
								{
									m_networks.add(network);
								}
							}
							else //kilometers or not configured
							{
								//Check if current network is closer than maxDistance configured in kilometers
								if ((network.getNetworkPosition().metersDistanceTo(currentPosition)/1000) < maxDistance)
								{
									m_networks.add(network);
								}
							}
						}
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
				publishProgress(m_networks);

			}
			return null;
		}


		@Override
		protected void onCancelled() {

			Log.i("INFO", "Async Task cancelled");
			running=false;
			super.onCancelled();
		}


		@Override
		protected void onPostExecute(Void result) {

		}


		@Override
		protected void onPreExecute() {


		}


		@Override
		protected void onProgressUpdate(ArrayList<LSNetwork>... values) {

			if (lastPosition != currentPosition)
			{
				m_adapter.clear();
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_networks.size();i++)
					m_adapter.add(m_networks.get(i));

				m_adapter.notifyDataSetChanged();

				String strDisplayInfoFormat = getResources().getString(R.string.strDisplayInfo);

				String strRadius = maxDistance + " " + typeUnit;
				String strDisplayInfo = String.format(strDisplayInfoFormat, strRadius, m_networks.size());  
				TextView txtDisplayInfo = (TextView)findViewById(R.id.txtDisplayInfo);
				txtDisplayInfo.setText(strDisplayInfo);
			}
		}

	}

}
