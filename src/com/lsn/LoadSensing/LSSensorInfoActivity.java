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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import greendroid.app.GDActivity;

public class LSSensorInfoActivity extends GDActivity {

	private String sensorSerial = null;
	private LSSensor sensorBundle = null;
	private LSSensor sensorObj = null;
	private static HashMap<String,Bitmap> hashImages = new HashMap<String,Bitmap>();
	private Bitmap imgSensor;
	private ArrayList<LSSensor>  m_sensors = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.act_sensorinfo);

		Bundle bundle = getIntent().getExtras();


		if (bundle != null)
		{
			sensorSerial = bundle.getString("SENSOR_SERIAL");
			sensorBundle = bundle.getParcelable("SENSOR_OBJ");
		}

		m_sensors = new ArrayList<LSSensor>();
		JSONObject jsonData = null;
		JSONArray jArray = null;

		if (sensorSerial != null)
		{
			// Server Request Ini
			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			params.put("serialNumber", sensorSerial);
			jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getSensorInfo.php",params);
		}

		if (sensorBundle != null)
		{

			// Server Request Ini
			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			params.put("sensor", sensorBundle.getSensorName());
			jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getSensorInfo.php",params);

		}

		if (jArray != null)
		{
			try {
				for (int i = 0; i<jArray.length(); i++)
				{

					jsonData = jArray.getJSONObject(i);

					sensorObj = new LSSensor();
					sensorObj.setSensorId(jsonData.getString("sensor"));
					sensorObj.setSensorSerial(jsonData.getString("serialNumber"));
					sensorObj.setSensorName(jsonData.getString("sensorName"));
					sensorObj.setSensorMeasure(jsonData.getString("measure"), jsonData.getString("measureUnit"));
					sensorObj.setSensorMaxLoad(jsonData.getString("MaxLoad"), jsonData.getString("MaxLoadUnit"));
					sensorObj.setSensorSensitivity(jsonData.getString("Sensivity"), jsonData.getString("SensivityUnit"));
					sensorObj.setSensorOffset(jsonData.getString("offset"), jsonData.getString("offsetUnit"));
					sensorObj.setSensorAlarmAt(jsonData.getString("AlarmAt"), jsonData.getString("AlarmAtUnit"));
					sensorObj.setSensorLastTare(jsonData.getString("LastTare"));
					sensorObj.setSensorChannel(jsonData.getString("canal"));
					sensorObj.setSensorType(jsonData.getString("tipus"));
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
					sensorObj.setSensorImage(imgSensor);
					sensorObj.setSensorDesc(jsonData.getString("Descripcio"));
					sensorObj.setSensorSituation(jsonData.getString("Poblacio"));
					sensorObj.setSensorNetwork(jsonData.getString("Nom"));
					m_sensors.add(sensorObj);
				}

				// Server Request End  

			} catch (JSONException e) {

				e.printStackTrace();
				CustomToast.showCustomToast(this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
			} catch (MalformedURLException e) {

				e.printStackTrace();
				CustomToast.showCustomToast(this,R.string.msg_ProcessError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
			}
		}
		else
		{
			CustomToast.showCustomToast(this,R.string.msg_CommError,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}

		if (sensorObj != null)
		{
			TextView txtNetName = (TextView) findViewById(R.id.netName);
			txtNetName.setText(sensorObj.getSensorNetwork());
			TextView txtSensorName = (TextView) findViewById(R.id.sensorName);
			txtSensorName.setText(sensorObj.getSensorName());
			TextView txtSensorSituation = (TextView) findViewById(R.id.sensorSituation);
			txtSensorSituation.setText(sensorObj.getSensorSituation());
			ImageView imageBitmap = (ImageView)findViewById(R.id.imageBitmap);
			imageBitmap.setImageBitmap(sensorObj.getSensorImage());
			TextView txtSensorType = (TextView) findViewById(R.id.sensorType);
			txtSensorType.setText(sensorObj.getSensorType());
			TextView txtSensorChannel = (TextView) findViewById(R.id.sensorChannel);
			txtSensorChannel.setText(sensorObj.getSensorChannel());
			TextView txtSensorDesc = (TextView) findViewById(R.id.sensorDescription);
			txtSensorDesc.setText(sensorObj.getSensorDesc());

			String value = null;
			String unit = null;

			value = sensorObj.getSensorMeasure().toString();
			unit = sensorObj.getSensorMeasureUnit();
			if ((unit != null)&&(unit != "null"))
			{
				value += " " + unit;
			}
			TextView txtSensorMeasure = (TextView) findViewById(R.id.sensorMeasure);
			txtSensorMeasure.setText(value);

			value = sensorObj.getSensorMaxLoad().toString();
			unit = sensorObj.getSensorMaxLoadUnit();
			if ((unit != null)&&(unit != "null"))
			{
				value += " " + unit;
			}
			TextView txtSensorMaxLoad = (TextView) findViewById(R.id.sensorMaxLoad);
			txtSensorMaxLoad.setText(value);

			value = sensorObj.getSensorSensitivity().toString();
			unit = sensorObj.getSensorSensitivityUnit();
			if ((unit != null)&&(unit != "null"))
			{
				value += " " + unit;
			}        
			TextView txtSensorSensitivity = (TextView) findViewById(R.id.sensorSensitivity);
			txtSensorSensitivity.setText(value);

			value = sensorObj.getSensorOffset().toString();
			unit = sensorObj.getSensorOffsetUnit();
			if ((unit != null)&&(unit != "null"))
			{
				value += " " + unit;
			}
			TextView txtSensorOffset = (TextView) findViewById(R.id.sensorOffset);
			txtSensorOffset.setText(value);

			value = sensorObj.getSensorAlarmAt().toString();
			unit = sensorObj.getSensorAlarmAtUnit();
			if ((unit != null)&&(unit != "null"))
			{
				value += " " + unit;
			}
			TextView txtSensorAlarmAt = (TextView) findViewById(R.id.sensorAlarmAt);
			txtSensorAlarmAt.setText(value);

			TextView txtSensorLastTare = (TextView) findViewById(R.id.sensorLastTare);
			txtSensorLastTare.setText(sensorObj.getSensorLastTare().toString());
			txtNetName.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {


				}

			});
		}
		else
		{
			CustomToast.showCustomToast(this,R.string.msg_SensorNotFound,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
		}



		Button loadChart = (Button)findViewById(R.id.btnLoadChart);
		loadChart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				if (sensorObj!=null)
				{
					RadioButton chartStrain = (RadioButton)findViewById(R.id.chartStrain);
					RadioButton chartPower = (RadioButton)findViewById(R.id.chartPower);
					RadioButton chartCounter = (RadioButton)findViewById(R.id.chartCounter);

					Integer chartType = 0;

					if (chartStrain.isChecked())
					{
						chartType = 0;
					}
					else if (chartPower.isChecked())
					{
						chartType = 1;
					}
					else if (chartCounter.isChecked())
					{
						chartType = 2;
					}

					Intent i = null;
					i = new Intent(LSSensorInfoActivity.this,LSSensorChartActivity.class);

					if (i!=null){
						Bundle bundle = new Bundle();

						bundle.putParcelable("SENSOR_OBJ", sensorObj);
						bundle.putInt("CHART_TYPE",chartType);
						i.putExtras(bundle);

						startActivity(i);
					}
				}
				else
				{
					CustomToast.showCustomToast(LSSensorInfoActivity.this,R.string.msg_SensorNotFound,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);
				}
			}
		});
	}
}