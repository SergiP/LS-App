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
import android.widget.ImageView;
import android.widget.TextView;

import com.lsn.LoadSensing.LSSensorInfoActivity;
import com.lsn.LoadSensing.R;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.filter.FilterAdapter;


public class LSSensorAdapter extends FilterAdapter<LSSensor>{

	private ArrayList<LSSensor> items;
	Context mContext;

	public LSSensorAdapter(Context context, int textViewResourceId, ArrayList<LSSensor> items) {
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
			v = vi.inflate(R.layout.row_list_sensor,null);
			wrapper = new ViewWrapper();
			wrapper.imgSensor = (ImageView) v.findViewById(R.id.imageBitmap);
			wrapper.txtSenName = (TextView) v.findViewById(R.id.sensorName);
			wrapper.txtSenType = (TextView) v.findViewById(R.id.sensorType);
			wrapper.txtSenChannel = (TextView) v.findViewById(R.id.sensorChannel);
			wrapper.txtTextNetwork = (TextView) v.findViewById(R.id.textNetwork);
			wrapper.txtSenNetwork = (TextView) v.findViewById(R.id.strNetwork);
			v.setTag(wrapper);
		} else {
			wrapper = (ViewWrapper)v.getTag();
		}
		
		wrapper.imgSensor.setImageBitmap(getItem(position).getSensorImage());
		wrapper.txtSenName.setText(getItem(position).getSensorName());
		wrapper.txtSenType.setText(getItem(position).getSensorType());
		wrapper.txtSenChannel.setText(getItem(position).getSensorChannel());
		wrapper.txtTextNetwork.setVisibility(View.GONE);
		
		if (getItem(position).getSensorFaves()==1)
		{
			wrapper.txtTextNetwork.setVisibility(View.VISIBLE);
			wrapper.txtSenNetwork.setText(getItem(position).getSensorNetwork());
		}
		
		v.setOnClickListener(new View.OnClickListener() {  	  
            @Override  
            public void onClick(View v) {  
            	Intent i = new Intent(mContext, LSSensorInfoActivity.class);
                
            	if (i != null) {
            		Bundle bundle = new Bundle();
            		bundle.putParcelable("SENSOR_OBJ", getItem(position));
            		
            		i.putExtras(bundle);
            		mContext.startActivity(i);
            	}
            }  
        }); 
	
		return v;
	}

	public String getSensorName(int position){
		LSSensor net = items.get(position);
		return net.getSensorName();
	}
	
	private class ViewWrapper{  
		ImageView imgSensor;
		TextView txtSenName;
		TextView txtSenType;
		TextView txtSenChannel;
		TextView txtTextNetwork;
		TextView txtSenNetwork;
    } 
}