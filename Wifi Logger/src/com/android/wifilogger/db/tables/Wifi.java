package com.android.wifilogger.db.tables;

public class Wifi extends StandardTableEntry{
	
	public String ssid = "";

	public double lng = 0;
	public double lat = 0;

	public long ess = 0;
	public long wpa = 0;
	public long wps = 0;
	public long wpa2 = 0;
	public long wep = 0;
	
	
	public long freq = 0;
	public long level = 0;

	public long timestamp = 0;

	
}
