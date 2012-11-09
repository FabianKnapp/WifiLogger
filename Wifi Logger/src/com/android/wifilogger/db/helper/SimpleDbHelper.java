package com.android.wifilogger.db.helper;

/*
 * Copyright (c) 2012 d4rxh4wx
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import android.database.sqlite.SQLiteDatabase;

import com.android.wifilogger.MyApplication;


/**
 * 
 * SimpleDbHelper asking for an opened or closed database 
 * 
 * @author d4rxh4wx
 *
 */
public class SimpleDbHelper {

	private final static String TAG = "MULTI-THREAD-DB-HELPER";

	private MultiThreadSQLiteOpenHelper dbHelper;

	public static SimpleDbHelper instance = null; 

	private SimpleDbHelper() {

	}
	
	public static SimpleDbHelper getInstance() {
		if(instance == null)
			instance = new SimpleDbHelper();
		return instance;
	}

	public SQLiteDatabase open() {
		synchronized(this) {
			if (dbHelper == null) {
				dbHelper = new SqlWifiOpenHelper(MyApplication.getAppContext());
			}
			return dbHelper.getWritableDatabase(); // getting a cached database
		}
	}

	public void close() {
		synchronized(this) {
			if (dbHelper != null) {
				// Ask for closing database
				if (dbHelper.closeIfNeeded()) {
					dbHelper = null; // database closed: free resource for GC
				}
			}
		}
	}
}
