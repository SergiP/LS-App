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

import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class LSHomeActivity extends GDActivity {

	private final int MORE = 0;
	private final int HELP = 1;
	private QuickActionWidget quickActions;
	private String typeMaps = null;
	public static String idSession;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.dshb_home);

		//Set type of ActionBar to Normal
		getActionBar().setType(ActionBar.Type.Normal);
		//Set ActionBar title
		//Not used setTitle function because of an issue with x-large devices with this activity
		TextView tv = (TextView) findViewById(R.id.gd_action_bar_title);
		tv.setText(R.string.act_lbl_homHome);

		//Change image and action of Home Button of Action Bar	
		ImageButton	mHomeButton = (ImageButton) findViewById(R.id.gd_action_bar_home_item);

		mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.gd_action_bar_exit));
		mHomeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showLogOutDialog();
			}
		});

		//Initialize ActionBar and QuickActionBar
		initActionBar();
		initQuickActionBar();

		//Retrieve intent information
		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			idSession = bundle.getString("SESSION");
			CustomToast.showCustomToast(this,this.getString(R.string.msg_Welcome) +" " + bundle.getString("USER")+" " + idSession,CustomToast.IMG_CORRECT,CustomToast.LENGTH_LONG);
		}       

		//Set click Listener on dashboard buttons
		DashboardClickListener dbClickListener = new DashboardClickListener();
		findViewById(R.id.dsh_btn_netList).setOnClickListener(dbClickListener);
		findViewById(R.id.dsh_btn_netMaps).setOnClickListener(dbClickListener);
		findViewById(R.id.dsh_btn_QRCode).setOnClickListener(dbClickListener);
		findViewById(R.id.dsh_btn_Faves).setOnClickListener(dbClickListener);
		findViewById(R.id.dsh_btn_AR).setOnClickListener(dbClickListener);
		findViewById(R.id.dsh_btn_netCloser).setOnClickListener(dbClickListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		typeMaps=settings.getString("maps", "google");
	}    

	@Override
	public void onBackPressed() {

		showLogOutDialog();
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		Intent i = null;

		switch (item.getItemId()) {

		case MORE:
			quickActions.show(item.getItemView());
			break;
		case HELP:

			i = new Intent(LSHomeActivity.this,LSHelpActivity.class);
			break;

		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		if (i!=null){
			startActivity(i);
		}
		return true;
	}

	private void initActionBar() {

		//Define ActionBar items
		addActionBarItem(Type.Add,MORE);
		addActionBarItem(Type.Help,HELP);
	}

	private void initQuickActionBar()
	{
		//Define Quick Actions
		quickActions = new QuickActionBar(this); 
		quickActions.addQuickAction(new QuickAction(this,android.R.drawable.ic_menu_preferences,getString(R.string.abtxtConfiguration)));
		quickActions.addQuickAction(new QuickAction(this,android.R.drawable.ic_menu_info_details,getString(R.string.abtxtInformation)));
		quickActions.setOnQuickActionClickListener(new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget widget, int position) {

				Intent i = null;

				switch (position)
				{
				case 0:
					i = new Intent(LSHomeActivity.this,LSConfigActivity.class);
					break;
				case 1:
					i = new Intent(LSHomeActivity.this,LSInfoActivity.class);
					break;
				}

				if (i!=null){
					startActivity(i);
				}
			}
		});
	}



	private class DashboardClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = null;

			switch (v.getId()) {
			case R.id.dsh_btn_netList:
				i = new Intent(LSHomeActivity.this,LSNetListActivity.class);
				break;
			case R.id.dsh_btn_netMaps:
				if (typeMaps.equals("google"))
				{
					i = new Intent(LSHomeActivity.this,LSNetMapsActivity.class);
				}
				else   
				{
					i = new Intent(LSHomeActivity.this,LSNetMapsForgeActivity.class);
				}
				break;
			case R.id.dsh_btn_QRCode:
				i = new Intent(LSHomeActivity.this,LSQRCodeActivity.class);
				break;
			case R.id.dsh_btn_Faves:
				Bundle bundle = new Bundle();
				bundle.putInt("par", 0);

				i = new Intent(LSHomeActivity.this,LSFavesActivity.class);
				i.putExtras(bundle);
				break;
			case R.id.dsh_btn_AR:
				i = new Intent(LSHomeActivity.this,LSAugRealActivity.class);
				break;
			case R.id.dsh_btn_netCloser:
				i = new Intent(LSHomeActivity.this,LSNetCloserActivity.class);
				break;
			default:
				break;
			}
			if (i!=null){
				Bundle bundle = new Bundle();
				bundle.putString("SESSION", idSession);
				i.putExtras(bundle);
				startActivity(i);
			}
		}
	}

	private void showLogOutDialog() {

		AlertDialog.Builder dialogLogOut = new AlertDialog.Builder(this);
		dialogLogOut.setMessage(R.string.dialogLogOut)
		.setPositiveButton(R.string.txtYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Go to Login Activity erasing all other activities with FLAG_ACTIVITY_CLEAR_TOP
				Intent i = new Intent(LSHomeActivity.this,LSLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		})
		.setNegativeButton(R.string.txtNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertLogOut = dialogLogOut.create();
		alertLogOut.setTitle(R.string.txtExit);
		alertLogOut.show();
		//		CustomDialog.Builder dialogLogOut = new CustomDialog.Builder(this);
		//		dialogLogOut.setTitle(R.string.txtExit)
		//			.setMessage(R.string.dialogLogOut)
		//			.setPositiveButton(R.string.txtYes, new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int id) {
		//					// Go to Login Activity erasing all other activities with FLAG_ACTIVITY_CLEAR_TOP
		//					Intent i = new Intent(LSHomeActivity.this,LSLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//					startActivity(i);
		//				}
		//			})
		//			.setNegativeButton(R.string.txtNo, new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int id) {
		//					dialog.cancel();
		//				}
		//			});
		//		Dialog alertLogOut = dialogLogOut.create();
		//		alertLogOut.show();
	}

}