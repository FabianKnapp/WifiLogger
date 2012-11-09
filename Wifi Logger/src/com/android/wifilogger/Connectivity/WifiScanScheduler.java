package com.android.wifilogger.Connectivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.android.wifilogger.MyApplication;
import com.android.wifilogger.Connectivity.WifiBroadcastReceiver.WifiBroadcastReceived;
import com.android.wifilogger.Connectivity.WifiScanScheduler.WifiResultListener;

public class WifiScanScheduler implements WifiBroadcastReceived {

	private List<WifiResultListener> wifiResultListener;
	


	private boolean started = false;
	private boolean stopped = false;

	
	private boolean wifiIsOn = false;
	private boolean killRequested = false;
	private ScanTimer scanTimer;
	private ArrayList<ScanResult> lastScanResults;
	private long timeLastScanResult;
	
	private static final long SCAN_TIME_INTERVAL = 10000;

	private static final long SCAN_TIME_UP_TO_DATE = 60000;
	
	private static WifiScanScheduler instance;
	
	public static WifiScanScheduler getInstance() {
		if(instance == null) {
			instance = new WifiScanScheduler();
		}
		return instance;
	}
	

	private WifiScanScheduler() {
		wifiResultListener = new CopyOnWriteArrayList<WifiScanScheduler.WifiResultListener>();
		lastScanResults = new ArrayList<ScanResult>();
		timeLastScanResult = 0; 
	}
	
	public void addListener(WifiResultListener listener) {
		if(!wifiResultListener.contains(listener)) {
			wifiResultListener.add(listener);
			if(wifiResultListener.size() <= 1)
				start();
		}
	}
	
	public void removeListener(WifiResultListener listener) {
		wifiResultListener.remove(listener);
		if(wifiResultListener.size() <= 0)
			stop();
	}

	public void start() {
		if(!isStarted()) {
			started = true;
			killRequested = false;
			WifiBroadcastReceiver.addListener(this);
			wifiIsOn = MyApplication.isWifiEnabled();
			startScanTimer();
		}
	}

	public void stop() {
		if(!isStopped()) {
			stopped = true;
			killRequested = true;
			WifiBroadcastReceiver.removeListener(this);
		}
	}
	
	public boolean isStarted() {
		return started;
	}

	
	public boolean isStopped() {
		return stopped;
	}



	private void startScanTimer() {
		if (scanTimer == null || !scanTimer.isAlive()) {
			scanTimer = new ScanTimer();
			scanTimer.setPriority(Thread.MAX_PRIORITY);
			scanTimer.start();
		}
	}

	private class ScanTimer extends Thread {
		
		
		@Override
		public void run() {

			while (wifiIsOn && !killRequested) {

				MyApplication.getWifiManager().startScan();

				try {
					Thread.sleep(SCAN_TIME_INTERVAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.run();

		}
	}

	@Override
	public void onWifiStateChanged(int state) {
		if (state == WifiManager.WIFI_STATE_ENABLED) {
			wifiIsOn = true;
			startScanTimer();
			
		} else if (state == WifiManager.WIFI_STATE_DISABLED) {
			wifiIsOn = false;
		}		
	}

	@Override
	public void onWifiReceivedBroadcastResult() {
		submitWifiResults();		
	}

	private void submitWifiResults() {
		lastScanResults = new ArrayList<ScanResult>(MyApplication.getWifiManager().getScanResults());
		timeLastScanResult = System.currentTimeMillis();
		if (lastScanResults != null && wifiResultListener != null) {
			Iterator<WifiResultListener> itWifiListener = wifiResultListener.iterator();
			while(itWifiListener.hasNext())
				itWifiListener.next().onNewWifisFound(lastScanResults);
		}
	}
	
	public ArrayList<ScanResult> getLastScanResults() {
		return lastScanResults;
	}
	
	public boolean isScanResultUpToDate() {
		if(lastScanResults != null && System.currentTimeMillis() - timeLastScanResult < SCAN_TIME_UP_TO_DATE)
			return true;
		else
			return false;
	}
	
	public interface WifiResultListener {

		public void onNewWifisFound(ArrayList<ScanResult> scanResult);
	}
}
