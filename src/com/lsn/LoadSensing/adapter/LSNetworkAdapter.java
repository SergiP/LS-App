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

package com.lsn.LoadSensing.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.element.LSNetwork;

public class LSNetworkAdapter extends ArrayAdapter<LSNetwork>{

	private ArrayList<LSNetwork> items;
	Context mContext;

	public LSNetworkAdapter(Context context, int textViewResourceId, ArrayList<LSNetwork> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_list_network,null);
		}

		LSNetwork o = items.get(position);
		if (o != null)
		{
			TextView txtNetName = (TextView) v.findViewById(R.id.networkName);
			TextView txtNetSituation = (TextView) v.findViewById(R.id.networkSituation);
			TextView txtNetSensors = (TextView) v.findViewById(R.id.networkSensors);

			txtNetName.setText(o.getNetworkName()); 
			txtNetSituation.setText(o.getNetworkSituation());
			txtNetSensors.setText(o.getNetworkNumSensors().toString());

		}
		return v;
	}

	public String getNetworkName(int position){

		LSNetwork net = items.get(position);
		return net.getNetworkName();
	}
}
