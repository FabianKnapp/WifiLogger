package com.android.wifilogger.db.datasource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.android.wifilogger.MyApplication;
import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.StandardTableEntry;
import com.android.wifilogger.db.tables.Wifi;

public class WifiDataSource extends AbstractDataSource {

	private static final int MAX_WIFI_RESULTS_PER_QUERY = 3000;
	
	public static final String[] allColumns = { SqlStaticStrings.COLUMN_ID,
			SqlStaticStrings.COLUMN_BSSID, SqlStaticStrings.WIFIS_COLUMN_SSID,
			SqlStaticStrings.WIFIS_COLUMN_FREQ,
			SqlStaticStrings.WIFIS_COLUMN_LEVEL,
			SqlStaticStrings.WIFIS_COLUMN_LATITUDE,
			SqlStaticStrings.WIFIS_COLUMN_LONGITUDE,
			SqlStaticStrings.WIFIS_COLUMN_TIMESTAMP,

			SqlStaticStrings.WIFIS_COLUMN_ESS,
			SqlStaticStrings.WIFIS_COLUMN_WEP,
			SqlStaticStrings.WIFIS_COLUMN_WPA,
			SqlStaticStrings.WIFIS_COLUMN_WPA2,
			SqlStaticStrings.WIFIS_COLUMN_WPS,

			SqlStaticStrings.WIFIS_COLUMN_COSLAT,
			SqlStaticStrings.WIFIS_COLUMN_SINLAT,
			SqlStaticStrings.WIFIS_COLUMN_COSLNG,
			SqlStaticStrings.WIFIS_COLUMN_SINLNG };

	public Cursor getWifisCursor(boolean wep, boolean wpa2, boolean wpa,
			boolean wps, boolean ess) {
		String query = queryBuilder(wep, wpa2, wpa, wps, ess);
		Cursor cursor;

		cursor = SimpleDbHelper
				.getInstance()
				.open()
				.query(getTableName(), getAllCollumns(), query, null, null,
						null, null);

		return cursor;
	}

	public ArrayList<Wifi> getWifiList(boolean wep, boolean wpa2, boolean wpa,
			boolean wps, boolean ess) {

		ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
		Cursor cursor = getWifisCursor(wep, wpa2, wpa, wps, ess);

		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				wifiList.add((Wifi) cursorTo(cursor));
				cursor.moveToNext();
			}
		}

		return wifiList;
	}

	public ArrayList<Wifi> getWifiList() {

		SharedPreferences prefs = MyApplication.getPrivateSharedPreferences();
		boolean wep = prefs.getBoolean(MyApplication.PREFS_SHOW_WEP, true);
		boolean wps = prefs.getBoolean(MyApplication.PREFS_SHOW_WPS, true);
		boolean wpa = prefs.getBoolean(MyApplication.PREFS_SHOW_WPA, true);
		boolean wpa2 = prefs.getBoolean(MyApplication.PREFS_SHOW_WPA2, true);
		boolean ess = prefs.getBoolean(MyApplication.PREFS_SHOW_ESS, true);

		return getWifiList(wep, wpa2, wpa, wps, ess);
	}

	private String queryBuilder() {
		SharedPreferences prefs = MyApplication.getPrivateSharedPreferences();
		boolean wep = prefs.getBoolean(MyApplication.PREFS_SHOW_WEP, true);
		boolean wps = prefs.getBoolean(MyApplication.PREFS_SHOW_WPS, true);
		boolean wpa = prefs.getBoolean(MyApplication.PREFS_SHOW_WPA, true);
		boolean wpa2 = prefs.getBoolean(MyApplication.PREFS_SHOW_WPA2, true);
		boolean ess = prefs.getBoolean(MyApplication.PREFS_SHOW_ESS, true);
		
		return queryBuilder(wep, wpa2, wpa2, wps, ess);
	}
	
	private String queryBuilder(boolean wep, boolean wpa2, boolean wpa,
			boolean wps, boolean ess) {
		StringBuilder queryString = new StringBuilder("");

		if (wep) {
			queryString.append(SqlStaticStrings.WIFIS_COLUMN_WEP);
			queryString.append("=1 ");
		}
		if (wpa2) {
			queryString.append(SqlStaticStrings.WIFIS_COLUMN_WPA2);
			queryString.append("=1 ");
		}
		if (wpa) {
			queryString.append(SqlStaticStrings.WIFIS_COLUMN_WPA);
			queryString.append("=1 ");
		}
		if (wps) {
			queryString.append(SqlStaticStrings.WIFIS_COLUMN_WPS);
			queryString.append("=1 ");
		}
		if (ess) {
			queryString.append(SqlStaticStrings.WIFIS_COLUMN_ESS);
			queryString.append("=1");
		}
		String query = queryString.toString();
		query = query.trim().replaceAll(" ", " OR ");

		if (query.equals(""))
			query = SqlStaticStrings.WIFIS_COLUMN_WEP + "=2";

		Log.e("Where QUERY", query);
		return query;
	}

	public Cursor getInRadius(double lat, double lng, double radius,
			boolean wep, boolean wpa2, boolean wpa, boolean wps, boolean ess) {
		LinkedList<Wifi> list = new LinkedList<Wifi>();
		String query = queryBuilder(wep, wpa2, wpa, wps, ess);

		double partialRadius = Math.abs(MyApplication
				.convertKmToPartialDistance(radius));

		String[] col = new String[getAllCollumns().length + 1];

		for (int i = 0; i < allColumns.length; i++) {
			col[i] = allColumns[i];
		}

		col[col.length - 1] = SqlStaticStrings.buildDistanceQuery(lat, lng)
				+ " AS distance";

		Cursor cursor = SimpleDbHelper
				.getInstance()
				.open()
				.query(getTableName(), col,
						"(" + query + ") AND (distance > " + String.valueOf(partialRadius) + ")", null,
						null, null, "distance DESC");

		return cursor;
	}
	

	
	public ArrayList<Wifi> getInRadius(Location location) {
		
		
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			SharedPreferences settings = MyApplication.getPrivateSharedPreferences();
			double radius = settings.getLong(MyApplication.PREFS_RADIUS, 50);
			boolean wep = settings.getBoolean(MyApplication.PREFS_SHOW_WEP, true);
			boolean wps = settings.getBoolean(MyApplication.PREFS_SHOW_WPS, true);
			boolean wpa = settings.getBoolean(MyApplication.PREFS_SHOW_WPA, true);
			boolean wpa2 = settings.getBoolean(MyApplication.PREFS_SHOW_WPA2, true);
			boolean ess = settings.getBoolean(MyApplication.PREFS_SHOW_ESS, true);
			
			Cursor cursor = getInRadius(lat, lng, radius, wep, wpa2, wpa, wps, ess);
			
			
			

		
		return cursorToList(cursor);
	}
	
	public ArrayList<Wifi> getBetween(double startLat, double startLng, double endLat, double endLng) {
		String query = queryBuilder();
		
		Cursor cursor = SimpleDbHelper
				.getInstance()
				.open()
				.query(getTableName(), allColumns,
						"(" + query + ") " +
					"AND (" + SqlStaticStrings.WIFIS_COLUMN_LATITUDE + " > " + startLat + ") " +
					"AND (" + SqlStaticStrings.WIFIS_COLUMN_LATITUDE + " < " + endLat + ") " +
					"AND (" + SqlStaticStrings.WIFIS_COLUMN_LONGITUDE + " > " + startLng + ") " +
					"AND (" + SqlStaticStrings.WIFIS_COLUMN_LONGITUDE + " < " + endLng + ")"
					, null, null, null, null);

		Log.e("Elements between: " + cursor.getCount(),"lat: " + startLat + " - " + endLat + " lng: " + startLng + " - " + endLng);
		return cursorToList(cursor);
	}
	
	public ArrayList<Wifi> cursorToList(Cursor cursor) {
		ArrayList<Wifi> wifiList = new ArrayList<Wifi>();
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast() && wifiList.size() <= MAX_WIFI_RESULTS_PER_QUERY) {
				wifiList.add((Wifi) cursorTo(cursor));
				cursor.moveToNext();
			}
		}
		
		return wifiList;
	}

	@Override
	public StandardTableEntry cursorTo(Cursor cursor) {
		Wifi wifi = new Wifi();

		if (cursor.getCount() > 0) {
			wifi.id = cursor.getLong(0);
			wifi.bssid = cursor.getString(1);
			wifi.ssid = cursor.getString(2);
			wifi.freq = cursor.getLong(3);
			wifi.level = cursor.getLong(4);
			wifi.lat = cursor.getDouble(5);
			wifi.lng = cursor.getDouble(6);
			wifi.timestamp = cursor.getLong(7);

			wifi.ess = cursor.getLong(8);
			wifi.wep = cursor.getLong(9);
			wifi.wpa = cursor.getLong(10);
			wifi.wpa2 = cursor.getLong(11);
			wifi.wps = cursor.getLong(12);
		}

		return wifi;
	}

	@Override
	public String[] getAllCollumns() {
		return allColumns;
	}

	@Override
	public void createEntry(StandardTableEntry table) {

		Wifi wifis = (Wifi) table;
		ContentValues values = new ContentValues();
		values.put(SqlStaticStrings.COLUMN_BSSID, wifis.bssid);
		values.put(SqlStaticStrings.WIFIS_COLUMN_FREQ, wifis.freq);
		values.put(SqlStaticStrings.WIFIS_COLUMN_LATITUDE, wifis.lat);
		values.put(SqlStaticStrings.WIFIS_COLUMN_LEVEL, wifis.level);
		values.put(SqlStaticStrings.WIFIS_COLUMN_LONGITUDE, wifis.lng);

		values.put(SqlStaticStrings.WIFIS_COLUMN_ESS, wifis.ess);
		values.put(SqlStaticStrings.WIFIS_COLUMN_WPA, wifis.wpa);
		values.put(SqlStaticStrings.WIFIS_COLUMN_WPA2, wifis.wpa2);
		values.put(SqlStaticStrings.WIFIS_COLUMN_WEP, wifis.wep);
		values.put(SqlStaticStrings.WIFIS_COLUMN_WPS, wifis.wps);

		values.put(SqlStaticStrings.WIFIS_COLUMN_COSLAT,
				Math.cos(MyApplication.deg2rad(wifis.lat)));
		values.put(SqlStaticStrings.WIFIS_COLUMN_COSLNG,
				Math.cos(MyApplication.deg2rad(wifis.lng)));
		values.put(SqlStaticStrings.WIFIS_COLUMN_SINLAT,
				Math.sin(MyApplication.deg2rad(wifis.lat)));
		values.put(SqlStaticStrings.WIFIS_COLUMN_SINLNG,
				Math.sin(MyApplication.deg2rad(wifis.lng)));

		values.put(SqlStaticStrings.WIFIS_COLUMN_SSID, wifis.ssid);
		values.put(SqlStaticStrings.WIFIS_COLUMN_TIMESTAMP, wifis.timestamp);

		if (isInTable(wifis))
			SimpleDbHelper
					.getInstance()
					.open()
					.update(getTableName(),
							values,
							SqlStaticStrings.COLUMN_BSSID + " LIKE " + "'"
									+ wifis.bssid + "'", null);
		else
			SimpleDbHelper.getInstance().open()
					.insert(getTableName(), null, values);

	}

	@Override
	public String getTableName() {
		return SqlStaticStrings.TABLE_WIFIS;
	}

}
