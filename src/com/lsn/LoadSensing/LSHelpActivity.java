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

import com.lsn.LoadSensing.actionbar.ActionBarActivity;
import com.lsn.LoadSensing.help.LSHelpListNetActivity;
import com.lsn.LoadSensing.ui.CustomToast;
import com.readystatesoftware.mapviewballoons.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class LSHelpActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_menu);
		
		// Change home icon (<Icon)
		getActionBarHelper().changeIconHome();

		findViewById(R.id.funcNetList).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent i = new Intent(LSHelpActivity.this,
								LSHelpListNetActivity.class);
						startActivity(i);
					}
				});

		findViewById(R.id.funcNetMaps).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						CustomToast.showCustomToast(LSHelpActivity.this,
								R.string.msg_UnderDevelopment,
								CustomToast.IMG_EXCLAMATION,
								CustomToast.LENGTH_SHORT);
					}

				});

		findViewById(R.id.funcQRCode).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,
						R.string.msg_UnderDevelopment,
						CustomToast.IMG_EXCLAMATION, CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcFaves).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CustomToast.showCustomToast(LSHelpActivity.this,
						R.string.msg_UnderDevelopment,
						CustomToast.IMG_EXCLAMATION, CustomToast.LENGTH_SHORT);
			}

		});

		findViewById(R.id.funcAugReal).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						CustomToast.showCustomToast(LSHelpActivity.this,
								R.string.msg_UnderDevelopment,
								CustomToast.IMG_EXCLAMATION,
								CustomToast.LENGTH_SHORT);
					}

				});

		findViewById(R.id.funcNetCloser).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						CustomToast.showCustomToast(LSHelpActivity.this,
								R.string.msg_UnderDevelopment,
								CustomToast.IMG_EXCLAMATION,
								CustomToast.LENGTH_SHORT);
					}
				});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ab_item_null, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent i = null;

		switch (item.getItemId()) {

		case android.R.id.home:

			i = new Intent(LSHelpActivity.this, LSHomeActivity.class);
			break;

		}

		if (i != null) {

			startActivity(i);
		}

		return super.onOptionsItemSelected(item);
	}
}