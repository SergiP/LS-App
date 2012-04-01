/*
 *    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
 *    
 *    Copyright (C) 2011-2012
 *    Authors:
 *    	Sergio González Díez        [sergio.gd@gmail.com]
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.element.LSImage;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.element.LSSensor;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

public class LSBigImageActivity extends ActionBarActivity implements
		OnGestureListener {

	private LSImage 						imageObj;
	private LSNetwork 						networkObj;
	private LSSensor 						sensorObj = null;

	private GestureDetector 				gestureScanner;

	private boolean 						moveLeft;
	private boolean 						moveRight;
	private String 							idImage = null;
	private String 							nameNetwork = null;
	private Integer 						position;

	private ImageButton[] 					imageButtonArray;
	private Bitmap 							imgNetwork;

	private JSONArray 						jArray = null;

	private RelativeLayout 					rl;
	private RelativeLayout.LayoutParams 	params1;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_bigimage);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {

			// Get bundle to LSFavesImagesActivity
			idImage = bundle.getString("IMAGE_ID");
			nameNetwork = bundle.getString("NETWORK_NAME");

			// Get bundle to LSNetImages
			position = bundle.getInt("POSITION");
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		}

		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();

		// if before activity -> LSFavesImagesActivity
		if (idImage != null && nameNetwork != null) {

			ProgressDialog progressDialog = new ProgressDialog(
					LSBigImageActivity.this);
			progressDialog.setTitle(getString(R.string.msg_PleaseWait));
			progressDialog.setMessage(getString(R.string.msg_retrievData));
			progressDialog.setCancelable(true);

			// Get Network and Image
			GetDataTask getData = new GetDataTask(LSBigImageActivity.this,
					progressDialog);
			getData.execute();

			// Get Sensors
			BigImageTask bigImageTask = new BigImageTask(
					LSBigImageActivity.this, progressDialog);
			bigImageTask.execute();

		} else { // if Before activity -> LSNetImages

			gestureScanner = new GestureDetector(this);
			updateTitle();
			updateImage();

			TextView txtNetName = (TextView) findViewById(R.id.netName);
			txtNetName.setText(imageObj.getImageNetwork());
		}

		rl = (RelativeLayout) findViewById(R.id.relative);
		params1 = new RelativeLayout.LayoutParams(15, 15);

	}

	public void showError(String result) {

		if (result != null) {

			CustomToast.showCustomToast(LSBigImageActivity.this, result,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {

		if (gestureScanner != null) {

			return gestureScanner.onTouchEvent(me);
		} else

			return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (moveLeft) {

			if (position < LSNetImagesActivity.m_images.size() - 1) {

				++position;
				delSensors();
				updateTitle();
				updateImage();
				return true;
			}
		}

		if (moveRight) {

			if (position > 0) {

				--position;
				delSensors();
				updateTitle();
				updateImage();
			}
		}

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		if (distanceX > 0) { // Scroll left

			moveLeft = true;
			moveRight = false;
		} else { // Scroll right

			moveLeft = false;
			moveRight = true;
		}

		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	private void updateTitle() {

		String strTitleFormat = getResources().getString(
				R.string.act_lbl_BigImage);
		String strTitle = String.format(strTitleFormat, position + 1,
				LSNetImagesActivity.m_images.size());

		this.setTitle(strTitle);
	}

	private void updateImage() {

		imageObj = LSNetImagesActivity.m_images.get(position);
		ImageView imgNetwork = (ImageView) findViewById(R.id.imageView);

		imgNetwork.setImageBitmap(imageObj.getImageBitmap());

		idImage = imageObj.getImageId();

		ProgressDialog progressDialog = new ProgressDialog(
				LSBigImageActivity.this);
		progressDialog.setTitle(getString(R.string.msg_PleaseWait));
		progressDialog.setMessage(getString(R.string.msg_retrievData));
		progressDialog.setCancelable(true);

		BigImageTask bigImageTask = new BigImageTask(LSBigImageActivity.this,
				progressDialog);
		bigImageTask.execute();
	}

	public void delSensors() {

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative);

		for (ImageButton imgButton : imageButtonArray) {

			rl.removeView(imgButton);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_null, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i = null;

		switch (item.getItemId()) {

		case android.R.id.home:

			i = new Intent(LSBigImageActivity.this, LSNetImagesActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("NETWORK_OBJ", networkObj);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtras(bundle);
			break;

		}

		if (i != null) {

			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
	}

	public class BigImageTask extends AsyncTask<String, Void, String> {

		private Activity activity;
		private ProgressDialog progressDialog;
		private String messageReturn = null;

		public BigImageTask(Activity activity, ProgressDialog progressDialog) {

			this.progressDialog = progressDialog;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {

			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			try {

				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				params.put("IdImatge", idImage);
				jArray = LSFunctions
						.urlRequestJSONArray(
								"http://viuterrassa.com/Android/getLlistaSensorsImatges.php",
								params);
			} catch (Exception e) {

				Log.e("BACKGROUND_PROC",
						"Exception BigImageTask" + e.getMessage());
				messageReturn = getString(R.string.msg_ProcessError);
			}

			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {

			progressDialog.dismiss();
			((LSBigImageActivity) activity).showError(pMessageReturn);

			getSensors();
		}
	}

	public void getSensors() {

		if (jArray != null) {

			try {

				imageButtonArray = new ImageButton[jArray.length()];

				for (int i = 0; i < jArray.length(); i++) {

					JSONObject jsonData;
					jsonData = jArray.getJSONObject(i);

					String x = jsonData.getString("x");
					String y = jsonData.getString("y");

					sensorObj = new LSSensor();
					sensorObj.setSensorId(jsonData.getString("id"));
					sensorObj.setSensorName(jsonData.getString("sensor"));

					// s1.setSensorChannel(jsonData.getString("canal"));
					// s1.setSensorType(jsonData.getString("tipus"));
					// s1.setSensorImageName(jsonData.getString("imatge"));
					// s1.setSensorDesc(jsonData.getString("Descripcio"));
					// s1.setSensorSituation(jsonData.getString("Poblacio"));
					// s1.setSensorNetwork(jsonData.getString("Nom"));

					params1 = new RelativeLayout.LayoutParams(15, 15);

					if (!x.equals("null")) {

						params1.leftMargin = Integer.parseInt(x) + 5;
					}
					if (!y.equals("null")) {

						params1.topMargin = Integer.parseInt(y) + 60;
					}

					imageButtonArray[i] = new ImageButton(this);
					imageButtonArray[i].setImageResource(R.drawable.loc_icon);

					imageButtonArray[i]
							.setOnClickListener(new OnClickListener() {

								public void onClick(View v) {

									Intent i = null;
									i = new Intent(LSBigImageActivity.this,
											LSSensorInfoActivity.class);

									if (i != null) {

										Bundle bundle = new Bundle();

										bundle.putParcelable("SENSOR_OBJ",
												sensorObj);
										bundle.putParcelable("NETWORK_OBJ",
												networkObj);
										i.putExtras(bundle);

										startActivity(i);
									}
								}
							});

					rl.addView(imageButtonArray[i], params1);
				}
			} catch (JSONException e) {

				Log.e("BACKGROUND_PROC", "JSONException" + e.getMessage());
				CustomToast.showCustomToast(this, R.string.msg_ProcessError,
						CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
			}
		} else {

			CustomToast.showCustomToast(this, R.string.msg_CommError,
					CustomToast.IMG_AWARE, CustomToast.LENGTH_SHORT);
		}
	}

	public class GetDataTask extends AsyncTask<String, Void, String> {

		private Activity activity;
		private ProgressDialog progressDialog;
		private String messageReturn = null;

		public GetDataTask(Activity activity, ProgressDialog progressDialog) {

			this.progressDialog = progressDialog;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {

			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			boolean trobat = false;
			try {

				// Server Request Ini
				Map<String, String> params = new HashMap<String, String>();
				params.put("session", LSHomeActivity.idSession);
				JSONArray jArray = LSFunctions.urlRequestJSONArray(
						"http://viuterrassa.com/Android/getLlistatXarxes.php",
						params);

				if (jArray != null) {

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject jsonData = jArray.getJSONObject(i);
						if ((nameNetwork.equals(jsonData.getString("Nom")) && !trobat)) {

							LSNetwork o1 = new LSNetwork();
							o1.setNetworkName(jsonData.getString("Nom"));
							o1.setNetworkPosition(jsonData.getString("Lat"),
									jsonData.getString("Lon"));
							o1.setNetworkNumSensors(jsonData
									.getString("Sensors"));
							o1.setNetworkId(jsonData.getString("IdXarxa"));
							o1.setNetworkSituation(jsonData
									.getString("Poblacio"));

							networkObj = o1;

							trobat = true;
						}
					}
				} else {

					messageReturn = getString(R.string.msg_CommError);
				}
			} catch (Exception e) {

				Log.e("BACKGROUND_PROC",
						"Exception GetDataTask" + e.getMessage());
				messageReturn = getString(R.string.msg_ProcessError);
			}

			if (trobat) {

				try {

					// Server Request Ini
					Map<String, String> params = new HashMap<String, String>();
					params.put("session", LSHomeActivity.idSession);
					params.put("IdXarxa", networkObj.getNetworkId());
					JSONArray jArray = LSFunctions
							.urlRequestJSONArray(
									"http://viuterrassa.com/Android/getLlistaImatges.php",
									params);

					trobat = false;
					String image = null;
					if (jArray != null) {

						for (int i = 0; i < jArray.length(); i++) {

							JSONObject jsonData = jArray.getJSONObject(i);
							if ((idImage.equals(jsonData.getString("IdImatge")) && !trobat)) {

								image = jsonData.getString("imatge");

								trobat = true;
							}
						}
					} else {

						messageReturn = getString(R.string.msg_CommError);
					}

					if (trobat) {
						imgNetwork = LSFunctions.getRemoteImage(new URL(
								"http://viuterrassa.com/Android/Imatges/"
										+ image));

					}
				} catch (Exception e) {

					Log.e("BACKGROUND_PROC",
							"Exception GetDataTask" + e.getMessage());
					messageReturn = getString(R.string.msg_ProcessError);
				}
			}

			return messageReturn;
		}

		@Override
		protected void onPostExecute(String pMessageReturn) {

			progressDialog.dismiss();
			((LSBigImageActivity) activity).showError(pMessageReturn);

			getImage();
		}
	}

	private void getImage() {

		TextView txtNetName = (TextView) findViewById(R.id.netName);
		txtNetName.setText(networkObj.getNetworkName());

		this.setTitle(networkObj.getNetworkName());

		ImageView imgNetwork1 = (ImageView) findViewById(R.id.imageView);
		imgNetwork1.setImageBitmap(imgNetwork);
	}
}