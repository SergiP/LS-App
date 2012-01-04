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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.lsn.LoadSensing.SQLite.LSNSQLiteHelper;
import com.lsn.LoadSensing.adapter.LSGalleryAdapter;
import com.lsn.LoadSensing.element.LSImage;
import com.lsn.LoadSensing.element.LSNetwork;
import com.lsn.LoadSensing.func.LSFunctions;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

public class LSNetImagesActivity extends GDActivity {

	private final int PHOTO = 0;
	private static final int CAMERA_PIC_REQUEST = 1; 

	private ProgressDialog       m_ProgressDialog = null;
	protected static ArrayList<LSImage>	 m_images = null;
	private LSGalleryAdapter     m_adapter;
	private Runnable             viewImages;
	private static HashMap<String,Bitmap> hashImages = new HashMap<String,Bitmap>();
	private Bitmap imgNetwork;
	private LSNetwork networkObj;
	private Integer  errMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.act_netimages);

		GridView imagegrid = (GridView) findViewById(R.id.gridView);


		initActionBar();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			networkObj = bundle.getParcelable("NETWORK_OBJ");
		}  

		m_images = new ArrayList<LSImage>();
		m_adapter = new LSGalleryAdapter(this,R.layout.image_gallery,m_images);
		imagegrid.setAdapter(m_adapter);

		TextView txtNetName = (TextView) findViewById(R.id.netName);
		txtNetName.setText(networkObj.getNetworkName());

		imagegrid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Intent i = null;
				i = new Intent(LSNetImagesActivity.this,LSBigImageActivity.class);

				if (i!=null){
					Bundle bundle = new Bundle();

					try
					{
						bundle.putInt("POSITION", position);
						i.putExtras(bundle);

						startActivity(i);
					}
					catch (Exception ex)
					{
						ex.getStackTrace();
					}
				}


			}
		});

		viewImages = new Runnable()
		{

			@Override
			public void run() {
				getImages();
			}
		};
		Thread thread = new Thread(null,viewImages,"ViewNetworks");
		thread.start();
		m_ProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_PleaseWait), getResources().getString(R.string.msg_retrievImages), true);

		registerForContextMenu(imagegrid);
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if(m_images != null && m_images.size() > 0){
				m_adapter.notifyDataSetChanged();
				for(int i=0;i<m_images.size();i++)
					m_adapter.add(m_images.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private Runnable returnErr = new Runnable() {

		@Override
		public void run() {

			CustomToast.showCustomToast(LSNetImagesActivity.this,errMessage,CustomToast.IMG_AWARE,CustomToast.LENGTH_SHORT);

		}
	};

	private void getImages() {

		try{
			m_images = new ArrayList<LSImage>();

			// Server Request Ini
			Map<String, String> params = new HashMap<String, String>();
			params.put("session", LSHomeActivity.idSession);
			params.put("IdXarxa", networkObj.getNetworkId());
			JSONArray jArray = LSFunctions.urlRequestJSONArray("http://viuterrassa.com/Android/getLlistaImatges.php",params);
			if (jArray != null)
			{
				for (int i = 0; i<jArray.length(); i++)
				{
					JSONObject jsonData = jArray.getJSONObject(i);
					LSImage img = new LSImage();
					img.setImageId(jsonData.getString("IdImatge"));
					String image = jsonData.getString("imatge");
					if (hashImages.containsKey(image))
					{
						imgNetwork = hashImages.get(image);
					}
					else
					{
						imgNetwork = LSFunctions.getRemoteImage(new URL("http://viuterrassa.com/Android/Imatges/"+image));
						hashImages.put(image, imgNetwork);
					}
					img.setImageBitmap(imgNetwork);
					img.setImageSituation(jsonData.getString("Poblacio"));
					img.setImageName(jsonData.getString("Nom"));
					img.setImageNetwork(jsonData.getString("Nom"));
					img.setImageNameFile(image);
					m_images.add(img);
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
			Log.i("ARRAY", ""+ m_images.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());
			errMessage = R.string.msg_ProcessError;
			runOnUiThread(returnErr); 
		}
		runOnUiThread(returnRes);		
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		switch (item.getItemId()) {

		case PHOTO:
			Intent photoCamera = null;
			photoCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			photoCamera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(getTempFile(this)));
			startActivityForResult(photoCamera,CAMERA_PIC_REQUEST);
			break;

		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	private File getTempFile(Context context){

		String folder   = "LSN";
		final File path = new File( Environment.getExternalStorageDirectory(), folder );
		if(!path.exists()){
			path.mkdir();
		}
		return new File(path, "image.tmp");
	}

	private void initActionBar() {

		//Define ActionBar items
		addActionBarItem(Type.TakePhoto,PHOTO);

	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  

		super.onActivityResult(requestCode, resultCode, data);  

		if (requestCode == CAMERA_PIC_REQUEST) {  
			if (resultCode == RESULT_OK)
			{
				final File file = getTempFile(this);

				try {
					Bitmap photoTaken = Media.getBitmap(getContentResolver(), Uri.fromFile(file) );

					LSImage img = new LSImage();
					img.setImageId("3");

					img.setImageBitmap(photoTaken);
					img.setImageSituation("Barcelona");
					img.setImageName("Test photo");
					img.setImageNetwork("Test Network");
					m_images.add(img);

					m_adapter.notifyDataSetChanged();
					m_adapter.clear();
					for(int i=0;i<m_images.size();i++)
						m_adapter.add(m_images.get(i));
					m_adapter.notifyDataSetChanged();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (resultCode == RESULT_CANCELED)
			{

				CustomToast.showCustomToast(this,
						R.string.msg_ActionCancelled,
						CustomToast.IMG_EXCLAMATION,
						CustomToast.LENGTH_LONG);
			}
		}  
	}  

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle(R.string.act_lbl_homFaves);
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
			LSImage ima1 = new LSImage();
			ima1 = m_images.get(info.position);
			if (db != null) {
				Cursor c = db1.rawQuery(
						"SELECT * FROM Image WHERE idImage = '"
								+ ima1.getImageId() + "';", null);
				if (c.getCount() == 0) {
					db.execSQL("INSERT INTO Image (name,idImage,idNetwork,poblacio,imageFile) " +
							"VALUES ('"
							+ ima1.getImageName()
							+ "','"
							+ ima1.getImageId()
							+ "','"
							+ ima1.getImageNetwork()
							+ "','"
							+ ima1.getImageSituation()
							+ "','"
							+ ima1.getImageNameFile()
							+ "');");

					CustomToast.showCustomToast(this,
							R.string.message_add_image,
							CustomToast.IMG_CORRECT, CustomToast.LENGTH_SHORT);
				} else
					CustomToast.showCustomToast(this,
							R.string.message_error_image,
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
