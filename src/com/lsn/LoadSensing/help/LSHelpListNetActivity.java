package com.lsn.LoadSensing.help;

import com.lsn.LoadSensing.R;

import android.os.Bundle;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;

public class LSHelpListNetActivity extends GDActivity {

	private final int BACK = 0;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.help_list_net);
        
        initActionBar();
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
