package com.android.wifilogger.db.helper;

import com.android.wifilogger.MyApplication;

public class SqlStaticStrings {
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BSSID = "bssid";

	public static final String TABLE_WIFIS = "wifis";
	public static final String WIFIS_COLUMN_SSID = "ssid";
	public static final String WIFIS_COLUMN_FREQ = "freq";
	public static final String WIFIS_COLUMN_LEVEL = "level";
	public static final String WIFIS_COLUMN_LONGITUDE = "lng";
	public static final String WIFIS_COLUMN_LATITUDE = "lat";
	public static final String WIFIS_COLUMN_COSLAT = "coslat";
	public static final String WIFIS_COLUMN_SINLAT = "sinlat";
	public static final String WIFIS_COLUMN_COSLNG = "coslng";
	public static final String WIFIS_COLUMN_SINLNG = "sinlng";
	public static final String WIFIS_COLUMN_WEP = "wifiwep";
	public static final String WIFIS_COLUMN_WPA = "wifiwpa";
	public static final String WIFIS_COLUMN_WPA2 = "wifiwpatwo";
	public static final String WIFIS_COLUMN_WPS = "wifiwps";
	public static final String WIFIS_COLUMN_ESS = "wifiess";
	public static final String WIFIS_COLUMN_TIMESTAMP = "timestamp";
	
	public static final String TABLE_WPA = "wpa";
	public static final String WPA_COLUMN_TKIP = "tkip";
	public static final String WPA_COLUMN_CCMP = "ccmp";

	public static final String TABLE_WPA2 = "wpatwo";
	public static final String WPA2_COLUMN_TKIP = "tkip";
	public static final String WPA2_COLUMN_CCMP = "ccmp";
	public static final String WPA2_COLUMN_PREAUTH = "preauth";

	public static final String TABLE_WEP = "wep";
	
	public static final String TABLE_WPS = "wps";
	
	public static final String TABLE_ESS = "ess";




	public static final String DATABASE_NAME = "wifilogger.db";
	public static final int DATABASE_VERSION = 2;

	public static final String TABLE_WIFIS_CREATE = "create table "
		      + TABLE_WIFIS + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null, "
		      + WIFIS_COLUMN_SSID + " text not null, "
		      + WIFIS_COLUMN_FREQ + " integer not null, "
		      + WIFIS_COLUMN_LEVEL + " integer not null, "
		      + WIFIS_COLUMN_LONGITUDE + " real, "
		      + WIFIS_COLUMN_LATITUDE + " real, "
		      + WIFIS_COLUMN_TIMESTAMP + " integer, "
		 
		      + WIFIS_COLUMN_ESS + " integer not null, "
		      + WIFIS_COLUMN_WEP + " integer not null, "
		      + WIFIS_COLUMN_WPA + " integer not null, "
		      + WIFIS_COLUMN_WPA2 + " integer not null, "
		      + WIFIS_COLUMN_WPS + " integer not null, "

		      + WIFIS_COLUMN_COSLAT + " real, "
		      + WIFIS_COLUMN_SINLAT + " real, "
		      + WIFIS_COLUMN_COSLNG + " real, "
		      + WIFIS_COLUMN_SINLNG + " real"
		      + ");";
	
	public static final String TABLE_WPA_CREATE = "create table "
		      + TABLE_WPA + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null, "
		      + WPA_COLUMN_TKIP + " integer not null, "
		      + WPA_COLUMN_CCMP + " integer not null"
		      + ");";

	public static final String TABLE_WPA2_CREATE = "create table "
		      + TABLE_WPA2 + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null, "
		      + WPA2_COLUMN_TKIP + " integer not null, "
		      + WPA2_COLUMN_CCMP + " integer not null, "
		      + WPA2_COLUMN_PREAUTH + " integer not null"
		      + ");";
	
	public static final String TABLE_WEP_CREATE = "create table "
		      + TABLE_WEP + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null"
		      + ");";
	
	public static final String TABLE_WPS_CREATE = "create table "
		      + TABLE_WPS + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null"
		      + ");";
	
	public static final String TABLE_ESS_CREATE = "create table "
		      + TABLE_ESS + "("
		      + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_BSSID + " text not null"
		      + ");";
	
	public static String buildDistanceQuery(double latitude, double longitude) {
	    final double coslat = Math.cos(MyApplication.deg2rad(latitude));
	    final double sinlat = Math.sin(MyApplication.deg2rad(latitude));
	    final double coslng = Math.cos(MyApplication.deg2rad(longitude));
	    final double sinlng = Math.sin(MyApplication.deg2rad(longitude));
	    
	    return "(" + coslat + "*" + WIFIS_COLUMN_COSLAT
	            + "*(" + WIFIS_COLUMN_COSLNG + "*" + coslng
	            + "+" + WIFIS_COLUMN_SINLNG + "*" + sinlng
	            + ")+" + sinlat + "*" + WIFIS_COLUMN_SINLAT 
	            + ")";
	    
	}
	
	
}
