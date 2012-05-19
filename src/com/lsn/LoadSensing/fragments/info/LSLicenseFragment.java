/*
 *    LS App - LoadSensing Application - https://github.com/SergiP/LS-App
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

package com.lsn.LoadSensing.fragments.info;

import com.lsn.LoadSensing.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/* GreenDroid -----
import greendroid.app.GDActivity;

public class LSLicenseFragment extends GDActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final String contentUrl = getIntent().getStringExtra(EXTRA_CONTENT_URL);
		if (!TextUtils.isEmpty(contentUrl)) {
			setActionBarContentView(R.layout.license);
			final WebView webView = (WebView) findViewById(R.id.license);
			webView.loadUrl(contentUrl);
		}
----------
 */

public class LSLicenseFragment extends Fragment {

	/** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    			
    	if (container == null) {      
            return null;
        }
    	
    	WebView webView = (WebView) inflater.inflate(R.layout.license, container, false);
		webView.loadUrl("file:///android_asset/LICENSE.txt");

        return webView;
    }
}
