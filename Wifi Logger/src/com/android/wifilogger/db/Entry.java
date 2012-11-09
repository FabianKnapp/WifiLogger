package com.android.wifilogger.db;

import java.util.HashMap;

import com.android.wifilogger.db.tables.StandardTableEntry;
import com.android.wifilogger.db.tables.WPA;
import com.android.wifilogger.db.tables.WPA2;
import com.android.wifilogger.db.tables.Wifi;

public class Entry {

	public HashMap<String, StandardTableEntry> entries;
	
	public WPA wpa;
	public WPA2 wpa2;
	public StandardTableEntry ess;
	public StandardTableEntry wps;
	public Wifi wifi;
	public StandardTableEntry wep;
	
	public double lat;
	public double lng;
	
	public Entry() {
		wpa = new WPA();
		wpa2 = new WPA2();
		ess = new StandardTableEntry();
		wps = new StandardTableEntry();
		wep = new StandardTableEntry();
		wifi = new Wifi();

		
		entries = new HashMap<String, StandardTableEntry>();
		entries.put("wpa", wpa2);
		entries.put("wep", wep);
		entries.put("wpa2", wpa2);
		entries.put("ess", ess);
		entries.put("wifi", wifi);
		entries.put("wps", wps);

		
	}
	
}
