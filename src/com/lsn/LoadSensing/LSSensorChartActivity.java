/*
 *    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
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

package com.lsn.LoadSensing;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LSSensorChartActivity extends ActionBarActivity {

	private String 							sensorSerial = null;
	private LSSensor 						sensorBundle = null;
	private Integer 						chartType = 0;
	private static HashMap<String,Double> 	hashValues = new HashMap<String,Double>();
	private String 							strNetwork;
	private String 							strSensor;
	private LSNetwork 						networkObj = null;
	private JSONObject 						jsonData = null;
	private JSONArray 						jArray = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_sensorchart);
		
		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			
			sensorSerial = bundle.getString("SENSOR_SERIAL");
			sensorBundle = bundle.getParcelable("SENSOR_OBJ");
			chartType = bundle.getInt("CHART_TYPE");
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		}
		
		ProgressDialog progressDialog = new ProgressDialog(LSSensorChartActivity.this);
		progressDialog.setTitle(getString(R.string.msg_PleaseWait));
		progressDialog.setMessage(getString(R.string.msg_retrievData));
		progressDialog.setCancelable(false);

		SensorCharTask sensorCharTask = new SensorCharTask(LSSensorChartActivity.this,progressDialog);
		sensorCharTask.execute();
	}
	
	public void showError(String result) {
		
		if (result != null) {
			
			CustomToast.showCustomToast(LSSensorChartActivity.this, result,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_null, menu);
		
		getActionBarHelper().optionsMenuHome(menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent i = null;
		
		switch (item.getItemId()) {
		
		case android.R.id.home:
			
			i = new Intent(LSSensorChartActivity.this, LSSensorInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("SENSOR_SERIAL", sensorSerial);
			bundle.putParcelable("SENSOR_OBJ", sensorBundle);
			bundle.putParcelable("NETWORK_OBJ", networkObj);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtras(bundle);
			break;
			
		case R.id.menu_home:
			
			i = new Intent(LSSensorChartActivity.this, LSHomeActivity.class);
			break;
		}	
		
		if (i != null) {
			
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}	
	
	public class SensorCharTask extends AsyncTask<String, Void, String> {
		
		private Activity activity;
		private ProgressDialog progressDialog;
		private String messageReturn = null;
		
		public SensorCharTask(Activity activity, ProgressDialog progressDialog) {
			
			this.progressDialog = progressDialog;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			
			if (sensorSerial != null) {
				
				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				params.put("serialNumber", sensorSerial);
				params.put("TipusGrafic", chartType.toString());
				jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getValorsGrafic.php",params);
			}

			if (sensorBundle != null) {
				
				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				params.put("sensor", sensorBundle.getSensorId());
				params.put("TipusGrafic", chartType.toString());
				jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getValorsGrafic.php",params);
			}

			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {
			
			progressDialog.dismiss();
			
			pMessageReturn = getChart();
			
			((LSSensorChartActivity) activity).showError(pMessageReturn);
		}
	}
	
	private String getChart() {
		
		String message = null;
		
		if (jArray != null) {
			
			try {
				
				jsonData = jArray.getJSONObject(0);
				strSensor = jsonData.getString("sensorName");
				strNetwork = jsonData.getString("Nom");
				jArray = jsonData.getJSONArray("ValorsGrafica");
			} catch (JSONException e1) {
				
				e1.printStackTrace();
				message= getString(R.string.msg_ProcessError);
			}

			int num = jArray.length();
			GraphViewData[] data = new GraphViewData[num];
			String[] strKeys = new String[num];
			try {
				
				for (int i = 0; i<jArray.length(); i++) {
					
					jsonData = jArray.getJSONObject(i);
					hashValues.put(jsonData.getString("date"), jsonData.getDouble("value"));
					data[i] = new GraphViewData(i+1,jsonData.getDouble("value"));

					Format formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date date = null;
					try {
						
						date = ((DateFormat) formatter).parse(jsonData.getString("date"));
					} catch (ParseException e) {

						e.printStackTrace();
					}

					formatter = new SimpleDateFormat("dd/MM");
					strKeys[i] = formatter.format(date);
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
				message= getString(R.string.msg_ProcessError);
			}
			
			TextView txtNetName = (TextView) findViewById(R.id.netName);
			txtNetName.setText(strNetwork);
			TextView txtSensorName = (TextView) findViewById(R.id.sensorName);
			txtSensorName.setText(strSensor);

			String strChartType = null;
			switch (chartType) {
			
			case 0:
				
				strChartType = getResources().getString(R.string.lblSensorChartStrain);
				break;
				
			case 1:
				
				strChartType = getResources().getString(R.string.lblSensorChartPower);
				break;
				
			case 2:
				
				strChartType = getResources().getString(R.string.lblSensorChartCounter);
				break;
				
			}

			TextView txtChartType = (TextView) findViewById(R.id.chartType);
			txtChartType.setText(strChartType);

			GraphView graphView = new BarGraphView(this, sensorBundle.getSensorName()) {
				
				@Override
				public void drawSeries(Canvas canvas, GraphViewData[] values, float graphwidth, float graphheight,
						float border, double minX, double minY, double diffX, double diffY,
						float horstart) {
					
					float colwidth = (graphwidth) / values.length;

					paint.setColor(Color.parseColor("#bcd9f2"));
					paint.setTextAlign(Align.CENTER);

					// draw data
					for (int i = 0; i < values.length; i++) {
						
						float valY = (float) (values[i].valueY - minY);
						float ratY = (float) (valY / diffY);
						float y = graphheight * ratY;
						canvas.drawRect((i * colwidth) + horstart, (border - y) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), graphheight + border - 1, paint);
					}
				}
			};

			graphView.addSeries(new GraphViewSeries(data));
			graphView.setViewPort(2, 40);
			graphView.setScrollable(false);
			graphView.setScalable(false);
			graphView.setHorizontalLabels(strKeys);
			graphView.setBackgroundColor(Color.parseColor("#287fcb"));

			LinearLayout chartLayout = (LinearLayout)findViewById(R.id.sensorChart);
			chartLayout.addView(graphView);
			
		} else { // jArray = null
			
			message= getString(R.string.msg_CommError);
		}
		
		return message;
	}
}