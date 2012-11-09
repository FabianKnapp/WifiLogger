package com.android.wifilogger;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.android.wifilogger.Connectivity.WifiBroadcastReceiver;
import com.android.wifilogger.Service.WifiLoggerService;

/**
 * 
 * Global static configuration.
 * 
 * @author Fabian Knapp for apprausch GmbH fabian@apprausch.de
 * 
 */
public class MyApplication extends Application {

	public static final int GPS_UPDATE_PERIOD = 0;

	public static final int USAGE_UPDATE_PERIOD = 2000;

	public static final long FIX_IS_GOOD = 120000;

	private static SharedPreferences prefs;

	public static float DISTANCE = 500;

	public static int destroyCounter = 0, createCounter = 0;

	private static Context context;

	private static ActivityManager activityManager;

	private static PackageManager packageManager;

	private static WindowManager windowManager;

	private static WifiManager wifiManager;

	private static LocationManager locationManager;

	private static TelephonyManager telephonyManager;

	private static AudioManager audioManager;

	private static LayoutInflater layoutInflater;

	private static ConnectivityManager connectivityManager;

	private static SensorManager sensorManager;

	private static Vibrator vibrator;

	private static Intent serviceIntent;

	private static BluetoothAdapter bluetoothAdapter;
	
	private WifiBroadcastReceiver wifiBroadcastReceiver;

	public static final String PREFS = "com.android.wifilogger.prefs";

	public static final String PREFS_SHOW_WEP = "com.android.wifilogger.prefs.wep";

	public static final String PREFS_SHOW_WPA = "com.android.wifilogger.prefs.wpa";

	public static final String PREFS_SHOW_WPA2 = "com.android.wifilogger.prefs.wpa2";

	public static final String PREFS_SHOW_ESS = "com.android.wifilogger.prefs.ess";

	public static final String PREFS_SHOW_WPS = "com.android.wifilogger.prefs.wps";

	public static final String PREFS_RADIUS = "com.android.wifilogger.prefs.radius";

	public static final String PREFS_USE_USER_SELECTED_LOCATION = "com.android.wifilogger.prefs.userselectedloc";
	
	public static final String PREFS_USER_SELECTED_LOCATION_LAT = "com.android.wifilogger.prefs.userselectedloclat";
	
	public static final String PREFS_USER_SELECTED_LOCATION_LNG = "com.android.wifilogger.prefs.userselectedloclng";

	public static final String PREFS_HAS_USER_SELECTED_LOCATION = "com.android.wifilogger.prefs.hasuserselectedloc";
	
	

	public static Location staticStartLocation;

	public static Location lastLocationFix;

	public static BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;

	}

	@Override
	public void onCreate() {

		super.onCreate();
		MyApplication.context = getApplicationContext();
		prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

		serviceIntent = new Intent(MyApplication.getAppContext(),
				WifiLoggerService.class);
		activityManager = (ActivityManager) MyApplication.getAppContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		packageManager = MyApplication.getAppContext().getPackageManager();
		windowManager = (WindowManager) MyApplication.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		wifiManager = (WifiManager) MyApplication.getAppContext()
				.getSystemService(Context.WIFI_SERVICE);
		locationManager = (LocationManager) MyApplication.getAppContext()
				.getSystemService(Context.LOCATION_SERVICE);
		telephonyManager = (TelephonyManager) MyApplication.getAppContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		layoutInflater = (LayoutInflater) MyApplication.getAppContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		connectivityManager = (ConnectivityManager) MyApplication
				.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		sensorManager = (SensorManager) MyApplication.getAppContext()
				.getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) MyApplication.getAppContext().getSystemService(
				VIBRATOR_SERVICE);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		audioManager = (AudioManager) MyApplication.getAppContext()
				.getSystemService(Context.AUDIO_SERVICE);
		
		staticStartLocation = new Location("");
		staticStartLocation.setLatitude(75);
		staticStartLocation.setLongitude(75);

		wifiBroadcastReceiver = new WifiBroadcastReceiver();
		startService();

	}

	public static boolean startService() {
		if (!isMyServiceRunning()) {
			context.startService(serviceIntent);
			return true;
		} else
			return false;
	}

	public static boolean stopService() {
		if (isMyServiceRunning()) {
			context.stopService(serviceIntent);
			return true;
		} else
			return false;
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	public static Vibrator getVibrator() {

		return vibrator;
	}

	public static ConnectivityManager getConnectivityManager() {
		return connectivityManager;
	}

	public static int calcDevicePixels(int deviceIndependentPixel) {
		return (int) (deviceIndependentPixel
				* MyApplication.getAppContext().getResources()
						.getDisplayMetrics().density + 0.5f);
	}

	public static Context getAppContext() {
		return context;
	}

	public static ActivityManager getActivityManager() {
		return activityManager;
	}

	public static PackageManager getPackManager() {
		return packageManager;
	}

	public static WindowManager getWindowManager() {
		return windowManager;
	}

	public static WifiManager getWifiManager() {
		return wifiManager;
	}

	public static LocationManager getLocationManager() {
		return locationManager;
	}

	public static TelephonyManager getTelephonyManager() {
		return telephonyManager;
	}

	public static LayoutInflater getLayoutInflater() {
		return layoutInflater;
	}

	public static SensorManager getSensorManager() {
		return sensorManager;
	}

	public static boolean getOnlineState() {
		ConnectivityManager myConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo myNetworkInfo = myConnectivityManager
				.getActiveNetworkInfo();
		if (myNetworkInfo != null && myNetworkInfo.isConnectedOrConnecting())
			return true;
		return false;

	}

	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double convertPartialDistanceToKm(double result) {
		return Math.acos(result) * 6371;
	}

	public static double convertKmToPartialDistance(double km) {
		return Math.cos(km / 6731);
	}

	public static SharedPreferences getPrivateSharedPreferences() {
		return prefs;
	}

	public static boolean isMyServiceRunning() {
		ActivityManager manager = getActivityManager();
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.android.wifilogger.Service.WifiLoggerService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWifiEnabled() {
		int wifiState = MyApplication.getWifiManager().getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_ENABLING
				|| wifiState == WifiManager.WIFI_STATE_ENABLED)
			return true;
		else
			return false;
	}

	public static boolean isGpsEnabled() {
		return MyApplication.getLocationManager().isProviderEnabled(
				LocationManager.GPS_PROVIDER);
	}

	public static boolean isNetworkLocationEnabled() {
		return MyApplication.getLocationManager().isProviderEnabled(
				LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isFixUpToDate() {
		if (lastLocationFix != null
				&& (isGpsEnabled() || (System.currentTimeMillis() - lastLocationFix.getTime()) < FIX_IS_GOOD))
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 * @return true if airplane mode disabled, false if airplane mode is already offline.
	 */
	public static boolean disableAirplaneMode() {
		if(Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0) {   
			Settings.System.putInt(
				     context.getContentResolver(),
				      Settings.System.AIRPLANE_MODE_ON, 0);
			
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			context.sendBroadcast(intent);
			return true;
		} else
			return false;
	}
	
	
	public static boolean isAirplaneMode() {
		if(Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0) {   
			return true;
		} else
			return false;
	}

	public static String formatDistance(float distance) {
		int i = 0;
		String[] appender = { "m", "km" };
		
		while (distance > 999 && i < 2) {
			distance /= 1000;
			i++;
		}
		if (i == 1) {
			distance *= 10;
			distance = (float) ((int) distance);
			distance /= 10;
		}
		if(i == 0)
			return String.valueOf((int) distance) + appender[i];
		else
			return String.valueOf(distance) + appender[i];
	}
	

	public static Intent getGpsSettingsIntent() {
		Intent in = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
				.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		return in;
	}
	
	public static boolean isAnyOffline() {
		return !isGpsEnabled() || !isWifiEnabled() || isAirplaneMode();
	}

}