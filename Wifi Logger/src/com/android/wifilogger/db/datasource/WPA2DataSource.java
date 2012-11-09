package com.android.wifilogger.db.datasource;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.StandardTableEntry;
import com.android.wifilogger.db.tables.WPA2;

public class WPA2DataSource extends AbstractDataSource {

	private static final String[] allColumns = { SqlStaticStrings.COLUMN_ID,
			SqlStaticStrings.COLUMN_BSSID, SqlStaticStrings.WPA2_COLUMN_TKIP,
			SqlStaticStrings.WPA2_COLUMN_CCMP,
			SqlStaticStrings.WPA2_COLUMN_PREAUTH };



	@Override
	public StandardTableEntry cursorTo(Cursor cursor) {
		WPA2 wpa2 = new WPA2();
		if (cursor.getCount() > 0) {

			wpa2.id = cursor.getLong(0);
			wpa2.bssid = cursor.getString(1);
			wpa2.tkip = cursor.getLong(2);
			wpa2.ccmp = cursor.getLong(3);
			wpa2.preauth = cursor.getLong(4);
		}
		return wpa2;
	}

	@Override
	public String[] getAllCollumns() {
		return allColumns;
	}

	@Override
	public void createEntry(StandardTableEntry table) {
		WPA2 wpa2 = (WPA2) table;

		ContentValues values = new ContentValues();
		values.put(SqlStaticStrings.COLUMN_BSSID, wpa2.bssid);
		values.put(SqlStaticStrings.WPA2_COLUMN_TKIP, wpa2.tkip);
		values.put(SqlStaticStrings.WPA2_COLUMN_CCMP, wpa2.ccmp);
		values.put(SqlStaticStrings.WPA2_COLUMN_PREAUTH, wpa2.preauth);

		if (isInTable(wpa2))
			SimpleDbHelper
					.getInstance()
					.open()
					.update(SqlStaticStrings.TABLE_WPA2,
							values,
							SqlStaticStrings.COLUMN_BSSID + " LIKE " + "'"
									+ wpa2.bssid + "'", null);
		else
			SimpleDbHelper.getInstance().open()
					.insert(SqlStaticStrings.TABLE_WPA2, null, values);

	}

	@Override
	public String getTableName() {
		return SqlStaticStrings.TABLE_WPA2;
	}
}
