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

import greendroid.app.GDActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

public class LSLicenseActivity extends GDActivity {

	public static final String EXTRA_CONTENT_URL = "com.lsn.LoadSensing.extra.CONTENT_URL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String contentUrl = getIntent().getStringExtra(EXTRA_CONTENT_URL);
		if (!TextUtils.isEmpty(contentUrl)) {
			setActionBarContentView(R.layout.license);
			final WebView webView = (WebView) findViewById(R.id.license);
			webView.loadUrl(contentUrl);

		}
	}

}