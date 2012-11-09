package com.android.wifilogger.db;

import android.database.Cursor;

import com.android.wifilogger.db.datasource.AbstractDataSource;
import com.android.wifilogger.db.datasource.StandardDataSource;
import com.android.wifilogger.db.datasource.WPA2DataSource;
import com.android.wifilogger.db.datasource.WPADataSource;
import com.android.wifilogger.db.datasource.WifiDataSource;
import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.Wifi;

public class MasterDatabaseQuery {

	
	public static boolean containsWifi(String bssid) {
		boolean isContained;
		Cursor cursor = SimpleDbHelper.getInstance().open().query(SqlStaticStrings.TABLE_WIFIS, WifiDataSource.allColumns, SqlStaticStrings.COLUMN_BSSID
				+ " LIKE " + "'" + bssid + "'", null, null, null, null);
		if(cursor == null || cursor.getColumnCount() == 0)
			isContained = false;
		else
			isContained = true;
		cursor.close();
		return isContained;
	}
	
	/*
	public static ArrayList<Entry> getEntriesForList(double lat, double lng, double radius) {
		ArrayList<Entry> list = new ArrayList<Entry>();
		
		WifiDataSource wifiDao = new WifiDataSource();
		Cursor cursor = wifiDao.getInRadius();
		
		if(cursor.moveToFirst()) {
			
		}
		
		return list;
	}*/
	
	public static boolean pushEntryToDb(Entry entry) {
		if(!MasterDatabaseQuery.containsWifi(entry.wifi.bssid) || isToUpdate(entry)) {
			putCompleteWifiEntry(entry);
			return true;
		} else {
			return false;
		}
	}
	
	public static Entry getCompleteEntry(String bssid) {
		EntryBuilder builder = new FromDbEntryBuilder(bssid);

		
		return builder.getEntry();

	}
	
	private static void putCompleteWifiEntry(Entry entry) {
		AbstractDataSource DAO;
		
		if(!entry.wps.bssid.equals("")) {
			DAO = new StandardDataSource(SqlStaticStrings.TABLE_WPS);
			DAO.createEntry(entry.wps);
		}
		if(!entry.wpa2.bssid.equals("")) {
			DAO = new WPA2DataSource();
			DAO.createEntry(entry.wpa2);
		}
		if(!entry.wpa.bssid.equals("")) {
			DAO = new WPADataSource();
			DAO.createEntry(entry.wpa);
		}
		if(!entry.wep.bssid.equals("")) {
			DAO = new StandardDataSource(SqlStaticStrings.TABLE_WEP);
			DAO.createEntry(entry.wep);
		}
		if(!entry.ess.bssid.equals("")) {
			DAO = new StandardDataSource(SqlStaticStrings.TABLE_ESS);
			DAO.createEntry(entry.ess);
		}
		
		if(!entry.wifi.bssid.equals("")) {
			DAO = new WifiDataSource();
			DAO.createEntry(entry.wifi);
		}
		
	}
	
	private static boolean isToUpdate(Entry entry) {
		AbstractDataSource wifiSource = new WifiDataSource();
		Wifi existing = (Wifi) wifiSource.query(entry.wifi.bssid);
		
		boolean toOld = true;
		boolean badLevel = true;
		
		if(!existing.bssid.equals("")) {
			toOld = existing.timestamp + 60000 < System.currentTimeMillis();
			badLevel = existing.level < entry.wifi.level;
		}
		if(toOld || badLevel) {
			return true;
		} else {
			return false;
		}
	}
	
	

	
}
