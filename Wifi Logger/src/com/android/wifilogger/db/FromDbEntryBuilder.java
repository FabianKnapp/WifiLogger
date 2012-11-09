package com.android.wifilogger.db;

import com.android.wifilogger.db.datasource.AbstractDataSource;
import com.android.wifilogger.db.datasource.StandardDataSource;
import com.android.wifilogger.db.datasource.WPA2DataSource;
import com.android.wifilogger.db.datasource.WPADataSource;
import com.android.wifilogger.db.datasource.WifiDataSource;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.WPA;
import com.android.wifilogger.db.tables.WPA2;
import com.android.wifilogger.db.tables.Wifi;

public class FromDbEntryBuilder extends EntryBuilder {

	private String bssid = "";

	public FromDbEntryBuilder(String bssid) {
		this.bssid = bssid;
		entry = new Entry();
		buildWepEntry();
		buildWpa2Entry();
		buildWpaEntry();
		buildWpsEntry();
		buildEssEntry();
		buildWifiEntry();

	}

	@Override
	public void buildWepEntry() {
		AbstractDataSource DAO = new StandardDataSource(SqlStaticStrings.TABLE_WEP);
		entry.wep = DAO.query(bssid);
	}

	@Override
	public void buildWpaEntry() {
		WPADataSource DAO = new WPADataSource();
		entry.wpa = (WPA) DAO.query(bssid);
	}

	@Override
	public void buildWpa2Entry() {
		WPA2DataSource DAO = new WPA2DataSource();
		entry.wpa2 = (WPA2) DAO.query(bssid);
	}

	@Override
	public void buildWpsEntry() {
		AbstractDataSource DAO = new StandardDataSource(SqlStaticStrings.TABLE_WPS);
		entry.wps = DAO.query(bssid);
	}

	@Override
	public void buildWifiEntry() {
		AbstractDataSource DAO = new WifiDataSource();
		entry.wifi = (Wifi) DAO.query(bssid);
	}

	@Override
	public void buildEssEntry() {
		AbstractDataSource DAO = new StandardDataSource(SqlStaticStrings.TABLE_ESS);
		entry.ess = DAO.query(bssid);
	}

}
