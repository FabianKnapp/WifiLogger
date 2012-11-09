package com.android.wifilogger.db.datasource;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.StandardTableEntry;
import com.android.wifilogger.db.tables.WPA;

public class WPADataSource extends AbstractDataSource {

	private static final String[] allColumns = { SqlStaticStrings.COLUMN_ID,
			SqlStaticStrings.COLUMN_BSSID, SqlStaticStrings.WPA_COLUMN_TKIP,
			SqlStaticStrings.WPA_COLUMN_CCMP};



	@Override
	public StandardTableEntry cursorTo(Cursor cursor) {
		WPA wpa = new WPA();
		if(cursor.getCount() > 0) {

		wpa.id = cursor.getLong(0);
		wpa.bssid = cursor.getString(1);
		wpa.tkip = cursor.getLong(2);
		wpa.ccmp = cursor.getLong(3);
		}
		return wpa;
	}

	@Override
	public String[] getAllCollumns() {
		// TODO Auto-generated method stub
		return allColumns;
	}

	@Override
	public void createEntry(StandardTableEntry table) {
		ContentValues values = new ContentValues();
		WPA wpa = (WPA) table;
		values.put(SqlStaticStrings.COLUMN_BSSID, wpa.bssid);
		values.put(SqlStaticStrings.WPA_COLUMN_TKIP, wpa.tkip);
		values.put(SqlStaticStrings.WPA_COLUMN_CCMP, wpa.ccmp);
		
		

		if(isInTable(wpa))
			SimpleDbHelper.getInstance().open().update(SqlStaticStrings.TABLE_WPA, values,  SqlStaticStrings.COLUMN_BSSID
						+ " LIKE " + "'" + wpa.bssid + "'", null);
		else	
			SimpleDbHelper.getInstance().open().insert(SqlStaticStrings.TABLE_WPA, null, values);			
	}

	@Override
	public String getTableName() {
		return SqlStaticStrings.TABLE_WPA;
	}
}
