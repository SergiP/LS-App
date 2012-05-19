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

package com.lsn.LoadSensing.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lsn.LoadSensing.LSNetInfoActivity;
import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.filter.FilterAdapter;


public class LSNetworkAdapter extends FilterAdapter<LSNetwork>{
	
	private ArrayList<LSNetwork> items;
	Context mContext;

	public LSNetworkAdapter(Context context, int textViewResourceId, ArrayList<LSNetwork> items) {
		super(context, textViewResourceId, items);
		this.mContext = context;
		this.items = items;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		ViewWrapper wrapper= null;
		
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			v = vi.inflate(R.layout.row_list_network,null);
			wrapper = new ViewWrapper();
			wrapper.txtNetName=(TextView)v.findViewById(R.id.networkName);
			wrapper.txtNetSituation=(TextView)v.findViewById(R.id.networkSituation);
			wrapper.txtNetSensors=(TextView)v.findViewById(R.id.networkSensors);
			v.setTag(wrapper);
		} else {
			wrapper = (ViewWrapper)v.getTag();
		}
		
		wrapper.txtNetName.setText(getItem(position).getNetworkName());
		wrapper.txtNetSituation.setText(getItem(position).getNetworkSituation());
		wrapper.txtNetSensors.setText(String.valueOf(getItem(position).getNetworkNumSensors()));

		v.setOnClickListener(new View.OnClickListener() {  	  
            @Override  
            public void onClick(View v) {  
            	Intent i = new Intent(mContext, LSNetInfoActivity.class);
                
            	if (i != null) {
            		Bundle bundle = new Bundle();
            		bundle.putParcelable("NETWORK_OBJ", getItem(position));
            		
            		i.putExtras(bundle);
            		mContext.startActivity(i);
            	}
            }  
        });  
		return v;
	}
	
	public String getNetworkName(int position){
		LSNetwork net = items.get(position);
		return net.getNetworkName();
	}

	private class ViewWrapper{  
        TextView txtNetName;  
        TextView txtNetSituation;
        TextView txtNetSensors;
    } 
}
