package com.android.wifilogger.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlWifiOpenHelper extends MultiThreadSQLiteOpenHelper{


	public SqlWifiOpenHelper(Context context) {
		super(context, SqlStaticStrings.DATABASE_NAME, null, SqlStaticStrings.DATABASE_VERSION);
	}
	


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SqlStaticStrings.TABLE_WIFIS_CREATE);
		db.execSQL(SqlStaticStrings.TABLE_WPA_CREATE);
		db.execSQL(SqlStaticStrings.TABLE_WPA2_CREATE);
		db.execSQL(SqlStaticStrings.TABLE_WEP_CREATE);
		db.execSQL(SqlStaticStrings.TABLE_WPS_CREATE);
		db.execSQL(SqlStaticStrings.TABLE_ESS_CREATE);

		Log.e("SQLITE WIFI LOGGER","WIFI TABLE CREATED!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(SqlWifiOpenHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + SqlStaticStrings.TABLE_WIFIS);
	        onCreate(db);		
	}

}
