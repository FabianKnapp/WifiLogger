package com.android.wifilogger.db.datasource;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.StandardTableEntry;

public class StandardDataSource extends AbstractDataSource {
	private static final String[] allColumns = { SqlStaticStrings.COLUMN_ID,
			SqlStaticStrings.COLUMN_BSSID };

	private String tableName;
	
	
	public StandardDataSource(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public StandardTableEntry cursorTo(Cursor cursor) {
		StandardTableEntry wps = new StandardTableEntry();
		if (cursor.getCount() > 0) {

			wps.id = cursor.getLong(0);
			wps.bssid = cursor.getString(1);
		}
		return wps;
	}

	@Override
	public String[] getAllCollumns() {
		return allColumns;
	}

	public void createEntry(StandardTableEntry table) {
		ContentValues values = new ContentValues();
		values.put(SqlStaticStrings.COLUMN_BSSID, table.bssid);

		if (isInTable(table))
			SimpleDbHelper
					.getInstance()
					.open()
					.update(getTableName(),
							values,
							SqlStaticStrings.COLUMN_BSSID + " LIKE " + "'"
									+ table.bssid + "'", null);
		else
			SimpleDbHelper.getInstance().open().insert(getTableName(), null, values);

	}

	@Override
	public String getTableName() {
		return tableName;
	}
}
