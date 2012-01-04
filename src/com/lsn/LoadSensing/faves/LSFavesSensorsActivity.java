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

package com.lsn.LoadSensing.faves;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.lsn.LoadSensing.LSFavesActivity;
import com.lsn.LoadSensing.LSSensorInfoActivity;
import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSSensorAdapter;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;

import greendroid.app.GDListActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class LSFavesSensorsActivity extends GDListActivity{

	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSSensor> m_sensors = null;
	private LSSensorAdapter       m_adapter;
	private Runnable             viewSensors;

	private Bitmap imgSensor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_04_favessensors);

		m_sensors = new ArrayList<LSSensor>();
		this.m_adapter = new LSSensorAdapter(this,R.layout.row_list_sensor,m_sensors);
		setListAdapter(this.m_adapter);

		viewSensors = new Runnable()
		{

			@Override
			public void run() {
				getSensors();
			}
		};
		Thread thread = new Thread(null,viewSensors,"ViewSensors");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievSensors), true);

		registerForContextMenu(getListView());
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if(m_sensors != null && m_sensors.size() > 0){
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_sensors.size();i++)
					m_adapter.add(m_sensors.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private Runnable returnErr = new Runnable() {

		@Override
		public void run() {

			CustomToast.showCustomToast(LSFavesSensorsActivity.this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);

		}
	};

	private void getSensors() {

		try {
			m_sensors = new ArrayList<LSSensor>();
			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

			Log.i("INFO", "Faves getSensors");
			if (db != null) {
				Cursor c = db.rawQuery("SELECT * FROM Sensor", null);
				c.moveToFirst();
				if (c != null) {
					while (!c.isAfterLast()) {
						String name = c.getString(c.getColumnIndex("name"));
						String idSensor = c.getString(c.getColumnIndex("idSensor"));
						String idNetwork = c.getString(c
								.getColumnIndex("idNetwork"));
						String type = c.getString(c.getColumnIndex("type"));
						String description = c.getString(c
								.getColumnIndex("description"));
						String channel = c.getString(c.getColumnIndex("channel"));
						String city = c.getString(c.getColumnIndex("poblacio"));
						String image = c.getString(c.getColumnIndex("image"));
						int faves = c.getInt(c.getColumnIndex("faves"));

						Log.i("INFO", "Obtaining image " + image);
						try {
							Log.i("INFO", image);
							imgSensor = LSFunctions.getRemoteImage(new URL("http://viuterrassa.com/Android/Imatges/"+image));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}

						LSSensor sensor = new LSSensor();
						sensor.setSensorName(name);
						sensor.setSensorId(idSensor);
						sensor.setSensorNetwork(idNetwork);
						sensor.setSensorType(type);
						sensor.setSensorDesc(description);
						sensor.setSensorChannel(channel);
						sensor.setSensorSituation(city);
						sensor.setSensorImage(imgSensor);
						sensor.setSensorImageName(image);
						sensor.setSensorFaves(faves);

						m_sensors.add(sensor);

						c.move(1);
					}
					Log.i("INFO", "Close cursor");
					c.close();
				}
				db.close();
				Log.i("ARRAY", "" + m_sensors.size());
			}	
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
			runOnUiThread(returnErr);
		}

		runOnUiThread(returnRes);
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent i = null;
		i = new Intent(this, LSSensorInfoActivity.class);

		if (i != null) {	
			Bundle bundle = new Bundle();

			bundle.putParcelable("SENSOR_OBJ", m_sensors.get(position));

			i.putExtras(bundle);

			startActivity(i);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(R.string.act_lbl_homFaves);
		inflater.inflate(R.menu.context_menu_del, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
		SQLiteDatabase db = lsndbh.getWritableDatabase();

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.del_faves:
			LSSensor sen1 = new LSSensor();
			sen1 = m_sensors.get(info.position);
			if (db != null) {
				db.execSQL("DELETE FROM Sensor WHERE idSensor ='"
						+ sen1.getSensorId() + "'");
				db.close();
				Bundle bundle = new Bundle();
				bundle.putInt("par", 1);
				Intent i = new Intent(this, LSFavesActivity.class);
				i.putExtras(bundle);
				startActivity(i);
				this.finish();
			}

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
