package com.android.wifilogger.UI;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.wifilogger.MyApplication;
import com.android.wifilogger.R;
import com.android.wifilogger.Connectivity.LocationProviderLogic;
import com.android.wifilogger.Connectivity.LocationProviderLogic.GpsChangeListener;
import com.android.wifilogger.Connectivity.WifiBroadcastReceiver;
import com.android.wifilogger.Connectivity.WifiBroadcastReceiver.WifiBroadcastReceived;
import com.android.wifilogger.UI.OfflineDialog.OfflineDialogListener;
import com.android.wifilogger.UI.fragments.SettingsFragment;
import com.android.wifilogger.UI.fragments.WifiListFragment;
import com.android.wifilogger.UI.fragments.WifiMapFragment;
import com.google.android.maps.MapView;

public class MainTabActivity extends SherlockFragmentActivity implements
		 OfflineDialogListener, GpsChangeListener, WifiBroadcastReceived {

	private boolean dialogIsShowing = false;
	private boolean dialogShowed = false;
	public ActionBar actionBar;
	private ActionBar.Tab listTab;
	public MenuItem refreshItem;
	private SherlockFragment listFragment;
	private SherlockFragment mapFragment;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Exchanger.mMapView = new MapView(this, getResources().getString(R.string.map_key));

		actionBar = getSupportActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		setContentView(R.layout.main);
		
		ActionBar.Tab settingsTab = actionBar.newTab().setText("Settings")
				.setTag("settings");
		listTab = actionBar.newTab().setText("Wifi List")
				.setTag("list");
		ActionBar.Tab mapTab = actionBar.newTab().setText("Map").setTag("map");

		SherlockFragment settingsFragment = new SettingsFragment();
		listFragment = new WifiListFragment();
		mapFragment = new WifiMapFragment();

		settingsTab.setTabListener(new WifiTabListener(settingsFragment, this));
		listTab.setTabListener(new WifiTabListener(listFragment, this));
		mapTab.setTabListener(new WifiTabListener(mapFragment, this));

		actionBar.addTab(listTab,false);
		actionBar.addTab(mapTab,false);
		actionBar.addTab(settingsTab,false);
		actionBar.selectTab(listTab);

	}
	

	@Override
	protected void onResume() {
		super.onResume();
		LocationProviderLogic.getInstance().addListener(this);
		WifiBroadcastReceiver.addListener(this);
	};

	@Override
	protected void onPause() {
		super.onPause();
		LocationProviderLogic.getInstance().removeListener(this);
		WifiBroadcastReceiver.removeListener(this);
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		//refreshItem.setActionView(null);
	};

	private void showRefreshContent() {

		if (refreshItem != null)
			refreshItem.setActionView(R.layout.indeterminate_progress_action);
	}

	private void dismissRefreshContent() {
		if (refreshItem != null)
			refreshItem.setActionView(null);
	}

	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_wifi, menu);
		Log.e("OptionsMenu Activity", "created");



		
		return true;
	}
	*/

	public void showRefresh(boolean show) {
		if (!show) {
			refreshItem.setEnabled(false);
			refreshItem.setVisible(false);
		} else if (show) {
			refreshItem.setEnabled(true);
			refreshItem.setVisible(true);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh_btn:
			((WifiListFragment) listFragment).startListUpdate();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	@Override
	public void onStartUpdate() {
		showRefreshContent();
	}

	@Override
	public void onFinishUpdate() {
		dismissRefreshContent();
	}*/
	


	@Override
	public void onUserClick(boolean userDecision) {
		dialogShowed = true;
		dialogIsShowing = false;
	}


	@Override
	public void onProviderStateChanged(boolean enabled, String provider) {
		boolean gpsProvider = provider.equals(LocationManager.GPS_PROVIDER);
		
		if(gpsProvider && !enabled) {
			showOfflineDialog();
			Log.e("GPS offline request","incoming");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWifiStateChanged(int state) {
		if(state == WifiManager.WIFI_STATE_DISABLED) {
			showOfflineDialog();
			Log.e("WIfi offline request","incoming");
		}
	}

	@Override
	public void onWifiReceivedBroadcastResult() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  
	  savedInstanceState.putBoolean("dialogShowed", dialogShowed);
	  savedInstanceState.putBoolean("dialogIsShowing", dialogIsShowing);

	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	
	  dialogShowed = savedInstanceState.getBoolean("dialogShowed");
	  dialogIsShowing = savedInstanceState.getBoolean("dialogIsShowing");

	  
	}
	
    public synchronized void showOfflineDialog() {
        
    	
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
    	
    		 Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

    	    	if(!dialogShowed && !dialogIsShowing && prev == null && MyApplication.isAnyOffline()) {
    		        /*
    		        if (prev != null) {
    		            ft.remove(prev);
    		        }
    		        ft.addToBackStack(null);
    		        */
    		
    		        // Create and show the dialog.
    			        OfflineDialog newFragment = new OfflineDialog();
    			        newFragment.show(getSupportFragmentManager(), "dialog");
    			        dialogIsShowing = true;
    			        Log.e("offlinedialog", "created");
    		        
    	    	}
		
       
    }
	
    public static class Exchanger {
		// We will use this MapView always.
    	public static MapView mMapView;
    }
	
}
