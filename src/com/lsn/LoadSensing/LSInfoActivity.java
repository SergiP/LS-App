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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import greendroid.app.ActionBarActivity;
import greendroid.app.GDTabActivity;

public class LSInfoActivity extends GDTabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Setting Information Tab
		final String aboutText =  getString(R.string.infoTabAbout);
		final Intent aboutIntent = new Intent(this, LSAboutActivity.class);
		aboutIntent.putExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.GONE);
		addTab(aboutText, aboutText, aboutIntent);

		//Setting License Tab
		final String licenseText =  getString(R.string.infoTabLicense);
		final Intent licenseIntent = new Intent(this, LSLicenseActivity.class);
		licenseIntent.putExtra(ActionBarActivity.GD_ACTION_BAR_VISIBILITY, View.GONE);
		licenseIntent.putExtra(LSLicenseActivity.EXTRA_CONTENT_URL, "file:///android_asset/LICENSE.txt");
		addTab(licenseText, licenseText, licenseIntent);
	}

	@Override
	public int createLayout() {
		return R.layout.info;
	}

	public void onAppUrlClicked(View v) {
		final Uri appUri = Uri.parse(getString(R.string.app_url));
		startActivity(new Intent(Intent.ACTION_VIEW, appUri));
	}
}
