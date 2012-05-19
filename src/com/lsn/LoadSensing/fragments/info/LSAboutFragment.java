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

import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/* GreenDroid -----
import greendroid.app.GDActivity;

public class LSAboutActivity extends GDActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.about);  
	}
----------
 */

public class LSAboutFragment extends Fragment {

	/** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    			
    	if (container == null) {      
            return null;
        }
    	
    	View v = inflater.inflate(R.layout.about, container, false);
        
    	// Allow link in TextView of caption
    	final TextView aboutCaption = (TextView) v.findViewById(R.id.about_caption);
    	aboutCaption.setMovementMethod(LinkMovementMethod.getInstance());
    	
    	// Allow link in TextView of collaboration
    	final TextView aboutColaboration = (TextView) v.findViewById(R.id.about_colaboration);
    	aboutColaboration.setMovementMethod(LinkMovementMethod.getInstance());

    	// Allow link in TextView of libraries
    	final TextView aboutLibrary1 = (TextView) v.findViewById(R.id.about_library1);
    	aboutLibrary1.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutLibrary2 = (TextView) v.findViewById(R.id.about_library2);
    	aboutLibrary2.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutLibrary3 = (TextView) v.findViewById(R.id.about_library3);
    	aboutLibrary3.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutLibrary4 = (TextView) v.findViewById(R.id.about_library4);
    	aboutLibrary4.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutLibrary5 = (TextView) v.findViewById(R.id.about_library5);
    	aboutLibrary5.setMovementMethod(LinkMovementMethod.getInstance());

    	// Allow link in TextView of image resources
    	final TextView aboutImgLink1 = (TextView) v.findViewById(R.id.about_ImgLink1);
    	aboutImgLink1.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutImgLink2 = (TextView) v.findViewById(R.id.about_ImgLink2);
    	aboutImgLink2.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutImgLink3 = (TextView) v.findViewById(R.id.about_ImgLink3);
    	aboutImgLink3.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutImgLink4 = (TextView) v.findViewById(R.id.about_ImgLink4);
    	aboutImgLink4.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutImgLink5 = (TextView) v.findViewById(R.id.about_ImgLink5);
    	aboutImgLink5.setMovementMethod(LinkMovementMethod.getInstance());

    	final TextView aboutImgLink6 = (TextView) v.findViewById(R.id.about_ImgLink6);
    	aboutImgLink6.setMovementMethod(LinkMovementMethod.getInstance());

        return v;
    }
}
