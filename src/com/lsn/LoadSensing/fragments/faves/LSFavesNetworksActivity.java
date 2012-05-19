/*
 *    LS App - LoadSensing Application - https://github.com/SergiP/LS-App
 *    
 *    Copyright (C) 2011-2012
 *    Authors:
 *    	Sergio Gonz�lez D�ez        [sergio.gd@gmail.com]
 *    	Sergio Postigo Collado      [spostigoc@gmail.com]
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *    
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lsn.LoadSensing.fragments.faves;

import java.util.ArrayList;

import com.lsn.LoadSensing.LSNetInfoActivity;
import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSNetworkAdapter;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.Position;
import com.lsn.LoadSensing.ui.CustomToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LSFavesNetworksActivity extends ListFragment {

	private ProgressDialog       m_ProgressDialog = null;
	private ArrayList<LSNetwork> m_networks = null;
	private LSNetworkAdapter       m_adapter;
	private Runnable             viewNetworks;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		if (container == null) {      
            return null;
        }
		
		View v = inflater.inflate(R.layout.act_04_favesnetworks, container, false);
		
		m_networks = new ArrayList<LSNetwork>();
		
		this.m_adapter = new LSNetworkAdapter(getActivity(),R.layout.row_list_network,m_networks);
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
		m_ProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievNetworks), true);
		
		return v;
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
			CustomToast.showCustomToast(getActivity(),R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}
	};

	private void getNetworks() {

		try {
			m_networks = new ArrayList<LSNetwork>();
			LSNSQLiteHelper lsndbh = new LSNSQLiteHelper(getActivity(), "DBLSN", null, 1);
			SQLiteDatabase db = lsndbh.getReadableDatabase();

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
			getActivity().runOnUiThread(returnErr);
		}
		getActivity().runOnUiThread(returnRes);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Intent i = null;
		i = new Intent(getActivity(), LSNetInfoActivity.class);

		if (i != null) {
			Bundle bundle = new Bundle();

			bundle.putParcelable("NETWORK_OBJ", m_networks.get(position)); 

			i.putExtras(bundle);
			startActivity(i);
		}
	}
}