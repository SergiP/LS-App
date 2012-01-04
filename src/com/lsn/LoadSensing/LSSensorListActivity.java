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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;



import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSSensorAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import greendroid.app.GDListActivity;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.ActionBarItem;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

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
import android.widget.TextView;

public class LSSensorListActivity extends GDListActivity {

	private final int OPTIONS = 0;
	private final int HELP = 1;
	private QuickActionWidget quickActions;

	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSSensor>  m_sensors = null;
	private LSSensorAdapter      m_adapter;
	private Runnable             viewSensors;

	private LSNetwork networkObj;
	private static HashMap<String,Bitmap> hashImages = new HashMap<String,Bitmap>();
	private Bitmap imgSensor;
	private Integer  errMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sensorlist);

		initActionBar();
		initQuickActionBar();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			networkObj = new LSNetwork();
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		}  

		TextView txtNetName = (TextView)findViewById(R.id.netName);
		txtNetName.setText(networkObj.getNetworkName());

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

			CustomToast.showCustomToast(LSSensorListActivity.this,errMessage,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);

		}
	};


	private void getSensors() {

		try{
			m_sensors = new ArrayList<LSSensor>();

			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			params.put("IdXarxa", networkObj.getNetworkId());
			JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistaSensors.php",params);

			if (jArray != null)
			{
				for (int i = 0; i<jArray.length(); i++)
				{
					JSONObject jsonData = jArray.getJSONObject(i);
					LSSensor s1 = new LSSensor();
					s1.setSensorId(jsonData.getString("id"));
					s1.setSensorName(jsonData.getString("sensor"));
					s1.setSensorChannel(jsonData.getString("canal"));
					s1.setSensorType(jsonData.getString("tipus"));
					String image = jsonData.getString("imatge");
					if (hashImages.containsKey(image))
					{
						imgSensor = hashImages.get(image);
					}
					else
					{
						imgSensor = LSFunctions.getRemoteImage(new URL("http://viuterrassa.com/Android/Imatges/"+image));
						hashImages.put(image, imgSensor);
					}
					s1.setSensorImage(imgSensor);
					s1.setSensorDesc(jsonData.getString("Descripcio"));
					s1.setSensorSituation(jsonData.getString("Poblacio"));
					s1.setSensorNetwork(jsonData.getString("Nom"));
					s1.setSensorImageName(image);
					m_sensors.add(s1);
				}
			}
			else
			{
				errMessage = R.string.msg_CommError;
				runOnUiThread(returnErr); 
			}

			//LSNetwork o1 = new LSNetwork();
			//o1.setNetworkName("Network 1");
			//o1.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//o1.setNetworkNumSensors(3);
			//LSNetwork o2 = new LSNetwork();
			//o2.setNetworkName("Network 2");
			//o2.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//o2.setNetworkNumSensors(2);
			//LSNetwork o3 = new LSNetwork();
			//o3.setNetworkName("Network 3");
			//o3.setNetworkSituation("lat. XX.XX lon. YY.YY");
			//o3.setNetworkNumSensors(4);
			//m_networks.add(o1);
			//m_networks.add(o2);
			//m_networks.add(o3);
			//Thread.sleep(1000);
			Log.i("ARRAY", ""+ m_sensors.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());
			errMessage = R.string.msg_ProcessError;
			runOnUiThread(returnErr); 
		}
		runOnUiThread(returnRes);		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent i = null;
		i = new Intent(LSSensorListActivity.this,LSSensorInfoActivity.class);

		if (i!=null){
			Bundle bundle = new Bundle();

			bundle.putParcelable("SENSOR_OBJ", m_sensors.get(position));

			i.putExtras(bundle);

			startActivity(i);
		}

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
		quickActions.setOnQuickActionClickListener(new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {

				CustomToast.showCustomToast(LSSensorListActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(R.string.act_lbl_homFaves);
		inflater.inflate(R.menu.context_menu_add, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
		SQLiteDatabase db = lsndbh.getWritableDatabase();
		SQLiteDatabase db1 = lsndbh.getReadableDatabase();

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.add_faves:
			LSSensor sen1 = new LSSensor();
			sen1 = m_sensors.get(info.position);
			if (db != null) {
				Cursor c = db1.rawQuery(
						"SELECT * FROM Sensor WHERE idSensor = '"
								+ sen1.getSensorId() + "';", null);
				if (c.getCount() == 0) {
					db.execSQL("INSERT INTO Sensor (name,idSensor,idNetwork,type,description,channel,poblacio,image,faves) " +
							"VALUES ('"
							+ sen1.getSensorName()
							+ "','"
							+ sen1.getSensorId()
							+ "','"
							+ sen1.getSensorNetwork()
							+ "','"
							+ sen1.getSensorType()
							+ "','"
							+ sen1.getSensorDesc()
							+ "','"
							+ sen1.getSensorChannel()
							+ "','"
							+ sen1.getSensorSituation()
							+ "','"
							+ sen1.getSensorImageName() 
							+ "',"
							+ 1
							+ ");");

					CustomToast.showCustomToast(this,
							R.string.message_add_sensor,
							CustomToast.IMG_CORRECT, CustomToast.LENGTH_SHORT);
				} else
					CustomToast.showCustomToast(this,
							R.string.message_error_sensor,
							CustomToast.IMG_EXCLAMATION,
							CustomToast.LENGTH_SHORT);
				db.close();
			}

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

}
