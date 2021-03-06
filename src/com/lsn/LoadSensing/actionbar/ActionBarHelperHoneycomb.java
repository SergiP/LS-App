/* Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lsn.LoadSensing.actionbar;

import com.lsn.LoadSensing.R;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An extension of {@link ActionBarHelper} that provides Android 3.0-specific
 * functionality for Honeycomb tablets. It thus requires API level 11.
 */
public class ActionBarHelperHoneycomb extends ActionBarHelper {
	private Menu mOptionsMenu;

	protected ActionBarHelperHoneycomb(Activity activity) {
		super(activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		final MenuItem starButton1 = mOptionsMenu.findItem(R.id.menu_star);
		if (starButton1 != null) {
			if (trobat) {
				starButton1.setIcon(R.drawable.ic_action_star_on);
			} else {
				starButton1.setIcon(R.drawable.ic_action_star_off);
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Returns a {@link Context} suitable for inflating layouts for the action
	 * bar. The implementation for this method in {@link ActionBarHelperICS}
	 * asks the action bar for a themed context.
	 */
	protected Context getActionBarThemedContext() {
		return mActivity;
	}

	public void changeIconHome() {
		mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void setFavesActionItem(boolean faves) {

		if (mOptionsMenu == null) {
			return;
		}
		final MenuItem starButton1 = mOptionsMenu.findItem(R.id.menu_star);
		if (starButton1 != null) {

			starButton1.setIcon(faves ? R.drawable.ic_action_star_off
					: R.drawable.ic_action_star_on);
		}
	}
}
