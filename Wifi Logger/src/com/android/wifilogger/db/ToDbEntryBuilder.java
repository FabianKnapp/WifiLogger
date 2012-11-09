package com.android.wifilogger.db;

import android.location.Location;
import android.net.wifi.ScanResult;

public class ToDbEntryBuilder extends EntryBuilder {

	private String wpaString = "";
	private String wpa2String = "";
	private String wepString = "";
	private String essString = "";
	private String wpsString = "";

	private double lng = 0;
	private double lat = 0;
	private ScanResult scanResult;

	public ToDbEntryBuilder(ScanResult scanResult, Location loc) {
		this(scanResult, loc.getLatitude(), loc.getLongitude());
	}

	public ToDbEntryBuilder(ScanResult scanResult, double lat, double lng) {
		String cap = scanResult.capabilities;
		this.scanResult = scanResult;
		this.lat = lat;
		this.lng = lng;
		entry = new Entry();
		extractCapabilityInfo(cap);
	}

	private void extractCapabilityInfo(String cap) {
		String[] caps = cap.split("\\]\\[");
		for (int i = 0; i < caps.length; i++) {

			caps[i] = caps[i].replaceAll("\\[|\\]", "").trim().toUpperCase();

			if (caps[i].contains("WPA-PSK"))
				wpaString = caps[i];
			else if (caps[i].contains("WPA2-PSK"))
				wpa2String = caps[i];
			else if (caps[i].contains("WEP"))
				wepString = caps[i];
			else if (caps[i].contains("ESS"))
				essString = caps[i];
			else if (caps[i].contains("WPS"))
				wpsString = caps[i];
		}
		buildAllEntries();
	}

	private void buildAllEntries() {

		buildWifiEntry();

	}

	@Override
	public void buildWepEntry() {
		entry.wep.bssid = scanResult.BSSID;
	}

	@Override
	public void buildWpaEntry() {
		long tkip = 0;
		long ccmp = 0;

		if (wpaString.contains("TKIP"))
			tkip = 1;
		if (wpaString.contains("CCMP"))
			ccmp = 1;

		entry.wpa.bssid = scanResult.BSSID;
		entry.wpa.ccmp = ccmp;
		entry.wpa.tkip = tkip;
	}

	@Override
	public void buildWpa2Entry() {
		long tkip = 0;
		long ccmp = 0;
		long preauth = 0;

		if (wpa2String.contains("TKIP"))
			tkip = 1;
		if (wpa2String.contains("CCMP"))
			ccmp = 1;
		if (wpa2String.contains("PREAUTH"))
			preauth = 1;

		entry.wpa2.bssid = scanResult.BSSID;
		entry.wpa2.ccmp = ccmp;
		entry.wpa2.tkip = tkip;
		entry.wpa2.preauth = preauth;

	}

	@Override
	public void buildWpsEntry() {
		entry.wps.bssid = scanResult.BSSID;
	}

	@Override
	public void buildWifiEntry() {
		entry.wifi.bssid = scanResult.BSSID;
		entry.wifi.freq = scanResult.frequency;
		entry.wifi.lat = lat;
		entry.wifi.lng = lng;
		entry.wifi.level = scanResult.level;
		entry.wifi.ssid = scanResult.SSID;
		entry.wifi.timestamp = System.currentTimeMillis();

		if (!wpaString.equals("")) {
			buildWpaEntry();
			entry.wifi.wpa = 1;
		}

		if (!wpa2String.equals("")) {
			buildWpa2Entry();
			entry.wifi.wpa2 = 1;
		}

		if (!wepString.equals("")) {
			buildWepEntry();
			entry.wifi.wep = 1;
		}

		if (!wpsString.equals("")) {
			buildWpsEntry();
			entry.wifi.wps = 1;
		}

		if (!essString.equals("")) {
			buildEssEntry();
			entry.wifi.ess = 1;
		}

	}

	@Override
	public void buildEssEntry() {
		entry.ess.bssid = scanResult.BSSID;
	}

}
