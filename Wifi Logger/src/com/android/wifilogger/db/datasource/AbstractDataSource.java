package com.android.wifilogger.db.datasource;

import android.database.Cursor;

import com.android.wifilogger.db.helper.SimpleDbHelper;
import com.android.wifilogger.db.helper.SqlStaticStrings;
import com.android.wifilogger.db.tables.StandardTableEntry;

public abstract class AbstractDataSource {

	
	public boolean isInTable(StandardTableEntry table) {
		StandardTableEntry quer = query(table.bssid);
		if(quer.bssid.equals(""))
			return false;
		else
			return true;
	}
	

	
	public StandardTableEntry query(String bssid) {
		Cursor cursor = SimpleDbHelper
				.getInstance()
				.open()
				.query(getTableName(),
						getAllCollumns(),
						SqlStaticStrings.COLUMN_BSSID + " LIKE " + "'" + bssid
								+ "'", null, null, null, null);
		cursor.moveToFirst();
		StandardTableEntry table = cursorTo(cursor);
		cursor.close();
		return table;
	}
	
	public void delete(StandardTableEntry table) {
		long id = table.id;
		SimpleDbHelper
				.getInstance()
				.open()
				.delete(getTableName(),
						SqlStaticStrings.COLUMN_ID + " = " + id, null);
	}

	public void delete(String bssid) {
		SimpleDbHelper
				.getInstance()
				.open()
				.delete(getTableName(),
						SqlStaticStrings.COLUMN_BSSID + " LIKE " + "'" + bssid
								+ "'", null);
	}
	
	public Cursor getAll() {
		Cursor cursor = SimpleDbHelper.getInstance().open()
				.query(getTableName(), getAllCollumns(), null, null, null, null, null);
		return cursor;
	}
	
	public abstract StandardTableEntry cursorTo(Cursor cursor);
	public abstract String[] getAllCollumns();
	public abstract String getTableName();

	public abstract void createEntry(StandardTableEntry table);

}
