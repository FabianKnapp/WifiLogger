package com.android.wifilogger;

import java.util.ArrayList;

import com.android.wifilogger.db.tables.Wifi;

public class GridField {

	
	public ArrayList<Wifi> wifis;
	
	public GridField() {
		wifis = new ArrayList<Wifi>();
	}
	
	public GridField(ArrayList<Wifi> wifis) {
		this.wifis = wifis;
	}
}
