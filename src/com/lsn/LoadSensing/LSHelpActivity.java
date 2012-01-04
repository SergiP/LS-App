package com.lsn.LoadSensing;


import com.lsn.LoadSensing.help.LSHelpListNetActivity;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

public class LSHelpActivity extends GDActivity { 

	private final int BACK = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.help_menu);

		initActionBar();

		findViewById(R.id.funcNetList).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(LSHelpActivity.this, LSHelpListNetActivity.class );
				startActivity(i);

			}

		});

		findViewById(R.id.funcNetMaps).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcQRCode).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcFaves).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcAugReal).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcNetCloser).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,R.string.msg_UnderDevelopment,CustomToast.IMG_EXCLAMATION,CustomToast.LENGTH_SHORT);
			}

		});

	}

	private void initActionBar() {

		addActionBarItem(getActionBar()
				.newActionBarItem(NormalActionBarItem.class)
				.setDrawable(R.drawable.gd_action_bar_back)
				.setContentDescription("Back"), BACK);
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {

		switch (item.getItemId()) {

		case BACK:
			onBackPressed();

			break;

		default:
			return super.onHandleActionBarItemClick(item, position);
		}

		return true;
	} 

}