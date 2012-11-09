package com.android.wifilogger;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;

import com.android.wifilogger.Connectivity.WifiScanScheduler;
import com.android.wifilogger.Connectivity.WifiScanScheduler.WifiResultListener;
import com.android.wifilogger.db.datasource.WifiDataSource;
import com.android.wifilogger.db.tables.Wifi;

public class WifiListLogic implements WifiResultListener {

	private boolean hasUserstaticLocation;
	private boolean useUserstaticLocation;

	private boolean wifiFixReceived = false;

	private boolean killed = false;
	private boolean started = false;
	private double userSelectedLat;
	private double userSelectedLng;
	private LocationSearch listener;
	private Location staticLocation;
	private Handler wifiScanResultHandler;
	private final static int MAX_SCAN_TIME = 10000;
	WifiWaiterThread wifiWaiterThread;

	private ArrayList<ScanResult> scanResult;

	public WifiListLogic() {

		scanResult = new ArrayList<ScanResult>();

		WifiScanScheduler.getInstance().addListener(this);
		
		wifiScanResultHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (scanResult != null && scanResult.size() > 0) {
					WifiDataSource wifiDAO = new WifiDataSource();
					Wifi tempWifi;
					Iterator<ScanResult> itResult = scanResult.iterator();
					while (itResult.hasNext() && !killed) {
						tempWifi = (Wifi) wifiDAO.query(itResult.next().BSSID);
					//	if (!tempWifi.bssid.equals("")) {
							if (tempWifi.ssid.equals("Absteige")) {

							Location loc = new Location("");

							loc.setLatitude(tempWifi.lat);
							loc.setLongitude(tempWifi.lng);
							
							listener.onReferenceLocationReceived(loc);
							return;
						}
					}
				}
				listener.onReferenceLocationReceived(staticLocation);
			}
		};
	}

	public boolean isStarted() {
		return started;
	}

	public void addListener(LocationSearch locListener) {
		listener = locListener;
		start();
	}

	public void removeListener() {
		stop();
		listener = null;
	}

	private void start() {
		started = true;
		killed = false;
		
		SharedPreferences prefs = MyApplication.getPrivateSharedPreferences();
		staticLocation = MyApplication.staticStartLocation;
		hasUserstaticLocation = prefs.getBoolean(
				MyApplication.PREFS_HAS_USER_SELECTED_LOCATION, false);
		useUserstaticLocation = prefs.getBoolean(
				MyApplication.PREFS_USE_USER_SELECTED_LOCATION, false);
		Long userSelectedLatLong = prefs.getLong(
				MyApplication.PREFS_USER_SELECTED_LOCATION_LAT,
				Double.doubleToLongBits(staticLocation.getLatitude()));
		Long userSelectedLngLong = prefs.getLong(
				MyApplication.PREFS_USER_SELECTED_LOCATION_LAT,
				Double.doubleToLongBits(staticLocation.getLongitude()));

		userSelectedLat = Double.longBitsToDouble(userSelectedLatLong);
		userSelectedLng = Double.longBitsToDouble(userSelectedLngLong);

		staticLocation = new Location("");
		staticLocation.setLatitude(userSelectedLat);
		staticLocation.setLongitude(userSelectedLng);
		
		logic();
	}

	public void logic() {
		if (!useUserstaticLocation && MyApplication.isFixUpToDate()) {
			returnLocation(MyApplication.lastLocationFix);
			return;
		} else if (hasUserstaticLocation) {
			listener.onReferenceLocationReceived(staticLocation);
			return;
		} else {
			actualLocationDetermination();
		}
	}
	
	private void actualLocationDetermination() {
		if (MyApplication.isFixUpToDate()) {
			returnLocation(MyApplication.lastLocationFix);
			return;
		} else {
			actualLocationDeterminationWithWifi();
			
		}
	}
	
	private void actualLocationDeterminationWithWifi() {
		if (wifiFixReceived)
			wifiScanResultHandler.sendEmptyMessage(0);
		else if(WifiScanScheduler.getInstance().isScanResultUpToDate()) {
			scanResult = WifiScanScheduler.getInstance().getLastScanResults();
			wifiScanResultHandler.sendEmptyMessage(0);
		} else {
			
			waitingForWifiScanResult();
		}
	}
	
	private void waitingForWifiScanResult() {
		wifiWaiterThread = new WifiWaiterThread();
		wifiWaiterThread.start();
	}
	
	private class WifiWaiterThread extends Thread {
		
		@Override
		public void run() {

			try {
				Thread.sleep(MAX_SCAN_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!killed)
				wifiScanResultHandler.sendEmptyMessage(0);
			
			super.run();
		}
	}

	private void stop() {
		killed = true;
		WifiScanScheduler.getInstance().removeListener(this);
		interruptWifiWaiterThread();
		wifiScanResultHandler.removeCallbacksAndMessages(null);
	}

	private void interruptWifiWaiterThread() {
		if (wifiWaiterThread != null && wifiWaiterThread.isAlive()) 
			wifiWaiterThread.interrupt();
	}

	private void returnLocation(Location location) {
		if (!killed)
			listener.onReferenceLocationReceived(location);
	}

	public interface LocationSearch {
		public void onReferenceLocationReceived(Location location);
	}

	@Override
	public void onNewWifisFound(ArrayList<ScanResult> scanResult) {
		if(scanResult != null && scanResult.size() > 0) {
			wifiFixReceived = true;
			this.scanResult = scanResult;
			interruptWifiWaiterThread();
		}
	}
}
