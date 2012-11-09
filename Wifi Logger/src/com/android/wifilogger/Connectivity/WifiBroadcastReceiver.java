package com.android.wifilogger.Connectivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.android.wifilogger.MyApplication;

public class WifiBroadcastReceiver extends BroadcastReceiver {
	
	private static List<WifiBroadcastReceived> wifiListener;
	private static WifiBroadcastReceiver ctx;
	private static int state = -1;
	
	public WifiBroadcastReceiver() {
		wifiListener = new CopyOnWriteArrayList<WifiBroadcastReceived>();
		ctx = this;
	}
	

	
	public static void addListener(WifiBroadcastReceived listener) {
		wifiListener.add(listener);
		if(wifiListener.size() <= 1)
			register();
		else if(state != -1) 
			listener.onWifiStateChanged(state); 		// send sticky!
		

	}
	
	public static void removeListener(WifiBroadcastReceived listener) {
		if(wifiListener.size() <= 1)
			unregister();
		wifiListener.remove(listener);
	}
	
	private static void register() {
		IntentFilter wifiFilter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		MyApplication.getAppContext().registerReceiver(ctx, wifiFilter);
	}
	
	private static void unregister() {
		MyApplication.getAppContext().unregisterReceiver(ctx);
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
				
			Iterator<WifiBroadcastReceived> itListener = wifiListener.iterator();
			if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				
				state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_DISABLED);
				while(itListener.hasNext())
					itListener.next().onWifiStateChanged(state);
				
			} else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				while(itListener.hasNext())
					itListener.next().onWifiReceivedBroadcastResult();
			}
		
	}
	
	

	
	public interface WifiBroadcastReceived {
		public void onWifiStateChanged(int state);
		
		public void onWifiReceivedBroadcastResult();
	}
}
