//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio Gonz�lez D�ez        [sergio.gd@gmail.com]
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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/* GreenDroid -----
import greendroid.app.GDActivity;

//public class LSNetInfoActivity extends GDActivity {
----------
 */
public class LSNetInfoActivity extends ActionBarActivity {
	private String netID = null;
	private LSNetwork networkObj = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* GreenDroid -----
		//setActionBarContentView(R.layout.act_netinfo);
		----------
		 */
		setContentView(R.layout.act_netinfo);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			netID = bundle.getString("NETID");
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		}
		
		getActionBarHelper().changeIconHome();
		
		if ((netID != null) && (networkObj == null))
		{
			JSONObject jsonData = null;
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistatXarxes.php",params);

				if (jArray != null)
				{
					boolean trobat = false;
					for (int i = 0; i<jArray.length(); i++)
					{
						jsonData = jArray.getJSONObject(i);

						if ((jsonData.getString("IdXarxa").equals(netID) && !trobat)){
							LSNetwork network = new LSNetwork();			
							network.setNetworkName(jsonData.getString("Nom"));
							network.setNetworkPosition(jsonData.getString("Lat"),jsonData.getString("Lon"));
							network.setNetworkNumSensors(jsonData.getString("Sensors"));
							network.setNetworkId(jsonData.getString("IdXarxa"));
							network.setNetworkSituation(jsonData.getString("Poblacio"));

							trobat = true;
							networkObj = network;
						}
					}
				}
				else
				{
					CustomToast.showCustomToast(this,R.string.msg_CommError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				CustomToast.showCustomToast(this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
			}
		}

		if (networkObj!=null)
		{
			TextView txtNetName = (TextView) findViewById(R.id.netName);
			txtNetName.setText(networkObj.getNetworkName());
			
			TextView txtNetSituation = (TextView) findViewById(R.id.netSituation);
			txtNetSituation.setText(networkObj.getNetworkSituation());
			TextView txtNetPosLatitude = (TextView) findViewById(R.id.netPosLatitude);
			txtNetPosLatitude.setText(networkObj.getNetworkPosition().getLatitude().toString());
			TextView txtNetPosLongitude = (TextView) findViewById(R.id.netPosLongitude);
			txtNetPosLongitude.setText(networkObj.getNetworkPosition().getLongitude().toString());
			TextView txtNetPosAltitude = (TextView) findViewById(R.id.netPosAltitude);
			txtNetPosAltitude.setText(networkObj.getNetworkPosition().getAltitude().toString());
			TextView txtNetNumSensors = (TextView) findViewById(R.id.netNumSensors);
			txtNetNumSensors.setText(networkObj.getNetworkNumSensors().toString());
		}
		else
		{
			CustomToast.showCustomToast(this,R.string.msg_NetworkNotFound,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}

		Button btnSensors = (Button) findViewById(R.id.btnLoadSensors);
		btnSensors.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				if (networkObj!=null)
				{
					Intent i = null;
					i = new Intent(LSNetInfoActivity.this,LSSensorListActivity.class);

					if (i!=null){
						Bundle bundle = new Bundle();

						bundle.putString("SESSION", LSHomeActivity.idSession);
						bundle.putParcelable("NETWORK_OBJ", networkObj);
						i.putExtras(bundle);

						startActivity(i);
					}
				}
				else
				{
					CustomToast.showCustomToast(LSNetInfoActivity.this,R.string.msg_NetworkNotFound,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
				}
			}


		});

		Button btnImages = (Button) findViewById(R.id.btnLoadImages);
		btnImages.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				if (networkObj!=null)
				{
					Intent i = null;
					i = new Intent(LSNetInfoActivity.this,LSNetImagesActivity.class);

					if (i!=null){
						Bundle bundle = new Bundle();

						bundle.putString("SESSION", LSHomeActivity.idSession);
						bundle.putParcelable("NETWORK_OBJ", networkObj);
						i.putExtras(bundle);

						startActivity(i);
					}
				}
				else
				{
					CustomToast.showCustomToast(LSNetInfoActivity.this,R.string.msg_NetworkNotFound,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
				}
			}


		});
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_help, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSNetInfoActivity.this, LSNetListActivity.class);
			break;
		case R.id.menu_help:
			CustomToast.showCustomToast(this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			break; 
		case R.id.menu_config:
			i = new Intent(LSNetInfoActivity.this,LSConfigActivity.class);
			break; 
		case R.id.menu_info:
			i = new Intent(LSNetInfoActivity.this,LSInfoActivity.class);
			break;
		}	
		
		if (i != null) {
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}	
}