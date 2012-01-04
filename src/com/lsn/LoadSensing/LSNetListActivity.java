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
import org.json.JSONObject;

import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSNetworkAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
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
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class LSNetListActivity extends GDListActivity {

	private final int OPTIONS = 0;
	private final int HELP = 1;
	private QuickActionWidget quickActions;

	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSNetwork> m_networks = null;
	private LSNetworkAdapter     m_adapter;
	private Runnable             viewNetworks;
	private Integer  errMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_01_netlist);

		initActionBar();
		initQuickActionBar();

		m_networks = new ArrayList<LSNetwork>();
		this.m_adapter = new LSNetworkAdapter(this,R.layout.row_list_network,m_networks);
		setListAdapter(this.m_adapter);

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

		registerForContextMenu(getListView());
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
		}
	};

	private Runnable returnErr = new Runnable() {

		@Override
		public void run() {

			CustomToast.showCustomToast(LSNetListActivity.this,errMessage,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);

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
			Log.i("ARRAY", ""+ m_networks.size());
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
		i = new Intent(LSNetListActivity.this,LSNetInfoActivity.class);

		if (i!=null){
			Bundle bundle = new Bundle();

			bundle.putParcelable("NETWORK_OBJ", m_networks.get(position));

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

				CustomToast.showCustomToast(LSNetListActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
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
			LSNetwork net1 = new LSNetwork();
			net1 = m_networks.get(info.position);
			if (db != null) {
				Cursor c = db1.rawQuery(
						"SELECT * FROM Network WHERE idNetwork = '"
								+ net1.getNetworkId() + "';", null);
				if (c.getCount() == 0) {
					db.execSQL("INSERT INTO Network (name,poblacio,idNetwork,sensors,lat,lon) VALUES ('"
							+ net1.getNetworkName()
							+ "','"
							+ net1.getNetworkSituation()
							+ "','"
							+ net1.getNetworkId()
							+ "',"
							+ net1.getNetworkNumSensors()
							+ ",'"
							+ net1.getNetworkPosition().getLatitude()
							+ "','"
							+ net1.getNetworkPosition().getLongitude() + "');");
					CustomToast.showCustomToast(this,
							R.string.message_add_network,
							CustomToast.IMG_CORRECT, CustomToast.LENGTH_SHORT);
				} else
					CustomToast.showCustomToast(this,
							R.string.message_error_network,
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
