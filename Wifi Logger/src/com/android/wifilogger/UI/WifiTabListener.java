package com.android.wifilogger.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.android.wifilogger.R;

public class WifiTabListener implements ActionBar.TabListener {
		public Fragment fragment;
		public MainTabActivity act;
		 
		public WifiTabListener(Fragment fragment, MainTabActivity act) {
		this.fragment = fragment;
		this.act = act;
		}
		 
		
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.root, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
		 
		
}
