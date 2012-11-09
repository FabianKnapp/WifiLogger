package com.android.wifilogger.Connectivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.wifilogger.MyApplication;

public class LocationProviderLogic {
	
	private boolean networkLocationListenerEnable = false;
	private boolean gpsLocationListenerEnable = false;
	private LocationListener networkListener;
	private LocationListener gpsListener;
	private boolean killRequested = false;
	private List<GpsChangeListener> registeredListener;
	
	private static LocationProviderLogic instance = null;
	
	private LocationProviderLogic() {
		setupNetworkListener();
		registeredListener = new CopyOnWriteArrayList<GpsChangeListener>();
	}
	
	public static LocationProviderLogic getInstance() {
		if(instance == null)
			instance = new LocationProviderLogic();
		return instance;
	}
	

	
	public void addListener(GpsChangeListener listener) {
		if(!registeredListener.contains(listener)) {
			registeredListener.add(listener);
			if(registeredListener.size() <= 1) 
				start();
			else
				listener.onProviderStateChanged(MyApplication.isGpsEnabled(), LocationManager.GPS_PROVIDER);
		}
	}
	
	
	public void removeListener(GpsChangeListener listener) {
		registeredListener.remove(listener);
		if(registeredListener.size() == 0) {
			stop();
			instance = null;
		}
	}
	
	
	private void setupNetworkListener() {
		networkListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {
				Iterator<GpsChangeListener> itListener = registeredListener.iterator();
				while(itListener.hasNext())
					itListener.next().onProviderStateChanged(false, provider);
			}

			public void onLocationChanged(Location location) {
				MyApplication.lastLocationFix = location;
				Iterator<GpsChangeListener> itListener = registeredListener.iterator();
				while(itListener.hasNext())
					itListener.next().onLocationChanged(location);
			}
		};
		
		setupGpsListener();
	}
	

	
	private void setupGpsListener() {
		gpsListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			public void onProviderEnabled(String provider) {
				Iterator<GpsChangeListener> itListener = registeredListener.iterator();
				while(itListener.hasNext())
					itListener.next().onProviderStateChanged(true, provider);
			}

			public void onProviderDisabled(String provider) {
				if(!killRequested)
					registerNetworkLocListener();
				Iterator<GpsChangeListener> itListener = registeredListener.iterator();
				while(itListener.hasNext())
					itListener.next().onProviderStateChanged(false, provider);
			}
			
			public void onLocationChanged(Location location) {
				unregisterNetworkLocListener();
				MyApplication.lastLocationFix = location;
				Iterator<GpsChangeListener> itListener = registeredListener.iterator();
				while(itListener.hasNext())
					itListener.next().onLocationChanged(location);
			}
		};
		
	}

	
	private void start() {
		registerNetworkLocListener();
		registerGpsLocListener();
	}
	
	private void registerNetworkLocListener() {
		if (!networkLocationListenerEnable) {
			networkLocationListenerEnable = true;
			MyApplication.getLocationManager().requestLocationUpdates(
					LocationManager.PASSIVE_PROVIDER, 15000, 175,
					networkListener);
			MyApplication.getLocationManager().requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 15000, 175,
					networkListener);
		}
	}
	
	

	private void registerGpsLocListener() {
		gpsLocationListenerEnable = true;
		MyApplication.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,
				10000, 50, gpsListener);
	}
	
	private void stop() {
		killRequested = true;
		unregisterGpsLocListener();
		unregisterNetworkLocListener();
	}



	private void unregisterNetworkLocListener() {
		if (networkLocationListenerEnable) {
			MyApplication.getLocationManager().removeUpdates(networkListener);
			networkLocationListenerEnable = false;
		}
	}

	private void unregisterGpsLocListener() {
		if (gpsLocationListenerEnable) {
			MyApplication.getLocationManager().removeUpdates(gpsListener);
			gpsLocationListenerEnable = false;
		}
	}

	public interface GpsChangeListener {
		public void onProviderStateChanged(boolean enabled, String provider);
		
		public void onLocationChanged(Location location);
	}
}
