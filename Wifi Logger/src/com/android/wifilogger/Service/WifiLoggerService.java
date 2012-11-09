package com.android.wifilogger.Service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.android.wifilogger.MyApplication;
import com.android.wifilogger.Connectivity.LocationProviderLogic;
import com.android.wifilogger.Connectivity.LocationProviderLogic.GpsChangeListener;
import com.android.wifilogger.Connectivity.WifiBroadcastReceiver;
import com.android.wifilogger.Connectivity.WifiBroadcastReceiver.WifiBroadcastReceived;
import com.android.wifilogger.Connectivity.WifiScanScheduler;
import com.android.wifilogger.Connectivity.WifiScanScheduler.WifiResultListener;
import com.android.wifilogger.db.Entry;
import com.android.wifilogger.db.EntryBuilder;
import com.android.wifilogger.db.MasterDatabaseQuery;
import com.android.wifilogger.db.ToDbEntryBuilder;


public class WifiLoggerService extends Service implements WifiResultListener, GpsChangeListener, WifiBroadcastReceived {


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if(MyApplication.isWifiEnabled()) {
			WifiScanScheduler.getInstance().addListener(this);
			LocationProviderLogic.getInstance().addListener(this);
		}
		
		WifiBroadcastReceiver.addListener(this);
	}
	

	

	

	public void onDestroy() {
		super.onDestroy();
		LocationProviderLogic.getInstance().removeListener(this);
		WifiScanScheduler.getInstance().removeListener(this);
		WifiBroadcastReceiver.removeListener(this);

	}

	@Override
	public void onNewWifisFound(ArrayList<ScanResult> scanResult) {
		final ArrayList<ScanResult> results = scanResult;
		
		new Thread(new Runnable() {

			public void run() {
				if (MyApplication.isFixUpToDate()) {
					pushEntriesToDb(results);
				}

			}
		}).start();		
	}


	private void pushEntriesToDb(ArrayList<ScanResult> scanResult) {
		EntryBuilder toDbEntryBuilder;
		ScanResult result;
		Entry entry;
		double lat = MyApplication.lastLocationFix.getLatitude();
		double lng = MyApplication.lastLocationFix.getLongitude();
		Log.e("push to DB with", "Lat: " + lat + " Lng: " + lng);
		
		for (int i = 0; i < scanResult.size(); i++) {
			result = scanResult.get(i);

			toDbEntryBuilder = new ToDbEntryBuilder(result, lat, lng);

			entry = toDbEntryBuilder.getEntry();

			MasterDatabaseQuery.pushEntryToDb(entry);
		}
	}

	@Override
	public void onProviderStateChanged(boolean enabled, String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER)) {
			if(enabled)
				WifiScanScheduler.getInstance().addListener(this);
			else
				WifiScanScheduler.getInstance().removeListener(this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onWifiStateChanged(int state) {
		if(state == WifiManager.WIFI_STATE_ENABLED) {
			WifiScanScheduler.getInstance().addListener(this);
			LocationProviderLogic.getInstance().addListener(this);
		} else if(state == WifiManager.WIFI_STATE_DISABLED) {
			WifiScanScheduler.getInstance().removeListener(this);
			LocationProviderLogic.getInstance().removeListener(this);
		}
			

	}

	
	@Override
	public void onWifiReceivedBroadcastResult() {
	// handled by WifiLogic	
	}



}
