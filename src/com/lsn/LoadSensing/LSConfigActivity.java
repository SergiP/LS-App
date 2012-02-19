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

import com.lsn.LoadSensing.actionbar.ActionBarPreferenceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class LSConfigActivity extends ActionBarPreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		super.onCreate(savedInstanceState);
		
		getActionBarHelper().changeIconHome();

		addPreferencesFromResource(R.xml.config); 
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.ls_actionbar_null_menu, menu);
        
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		
		switch (item.getItemId()) {
		case android.R.id.home:
			i = new Intent(LSConfigActivity.this,LSHomeActivity.class);
			break;
		
		case R.id.menu_config:
			break; 
		case R.id.menu_info:
			i = new Intent(LSConfigActivity.this,LSInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putChar("ACTIVITY_BEFORE", 'C');
			i.putExtras(bundle);
			break;
		}	
		
		if (i != null) {
			startActivity(i);
		}
		
		return super.onOptionsItemSelected(item);
	}
}