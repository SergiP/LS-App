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


import java.util.ArrayList;

import com.lsn.LoadSensing.LSFavesActivity;
import com.lsn.LoadSensing.LSNetInfoActivity;
import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSNetworkAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.Position;
import com.lsn.LoadSensing.ui.CustomToast;

import greendroid.app.GDListActivity;

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

public class LSFavesNetworksActivity extends GDListActivity{


	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSNetwork> m_networks = null;
	private LSNetworkAdapter       m_adapter;
	private Runnable             viewNetworks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_04_favesnetworks);

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

		//		ItemAdapter adapter = new ItemAdapter(this);
		//		adapter.add(createTextItem(0,"Xarxa 1"));
		//		adapter.add(createTextItem(1,"Xarxa 2"));
		//		adapter.add(createTextItem(2,"Xarxa 3"));
		//		adapter.add(createTextItem(3,"Xarxa 4"));
		//		adapter.add(createTextItem(4,"Xarxa 5"));
		//		setListAdapter(adapter);
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

			CustomToast.showCustomToast(LSFavesNetworksActivity.this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);

		}
	};

	private void getNetworks() {

		try {
			m_networks = new ArrayList<LSNetwork>();
			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(this, "DBLSN", null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

			Log.i("INFO", "Faves getNetwork");
			if (db != null) {
				Cursor c = db.rawQuery("SELECT * FROM Network",
						null);
				c.moveToFirst();
				if (c != null) {
					while (!c.isAfterLast()) {
						String name = c.getString(c.getColumnIndex("name"));
						String idNetwork = c.getString(c.getColumnIndex("idNetwork"));
						int sensors = c.getInt(c.getColumnIndex("sensors"));
						String lat = c.getString(c.getColumnIndex("lat"));
						String lon = c.getString(c.getColumnIndex("lon"));
						String city = c.getString(c.getColumnIndex("poblacio"));

						double latitude=Double.valueOf(lat).doubleValue();
						double longitude=Double.valueOf(lon).doubleValue();
						Position p = new Position(latitude,longitude);

						LSNetwork network = new LSNetwork();
						network.setNetworkName(name);
						network.setNetworkId(idNetwork);
						network.setNetworkNumSensors(sensors);
						network.setNetworkPosition(p);
						network.setNetworkSituation(city);

						m_networks.add(network);

						c.move(1);
					}
					Log.i("INFO", "Close cursor");
					c.close();
				}
				Log.i("ARRAY", "" + m_networks.size());
				db.close();
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
		i = new Intent(this, LSNetInfoActivity.class);

		if (i != null) {
			Bundle bundle = new Bundle();

			// bundle.putString("SESSION", idSession);
			bundle.putParcelable("NETWORK_OBJ", m_networks.get(position)); 

			i.putExtras(bundle);
			// i.putExtra("NETWORK_OBJ", m_networks.get(position));
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
			LSNetwork net1 = new LSNetwork();
			net1 = m_networks.get(info.position);
			if (db != null) {
				db.execSQL("DELETE FROM Network WHERE idNetwork ='"
						+ net1.getNetworkId() + "'");
				db.close();
				Bundle bundle = new Bundle();
				bundle.putInt("par", 0);
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
