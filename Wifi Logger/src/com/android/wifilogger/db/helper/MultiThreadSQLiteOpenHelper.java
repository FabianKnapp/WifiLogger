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

import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MultiThreadSQLiteOpenHelper:<br>
 * enhanced SQLiteOpenHelper for android applications where several threads might access and close the same database<br>
 * <p>
 * With SQLiteOpenHelper, if one thread is closing database, then other threads will crash while accessing a closed database.<br>
 * With MultiThreadSQLiteOpenHelper, a thread does not close the database anymore, but asks for a close with the closeIfNeeded method.
 * It then verifies that each thread asked for closing before really closing the database.
 * 
 * @author d4rxh4wx
 *
 */
public abstract class MultiThreadSQLiteOpenHelper extends SQLiteOpenHelper {


	// Tells for each thread if it requires that database should be opened or not opened (closed)
	// Using WeakHashMap so that thread can be released by GC when needed (no strong references on threads)
	private WeakHashMap<Thread, Boolean> states = new WeakHashMap<Thread, Boolean>();

	public MultiThreadSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public SQLiteDatabase getWritableDatabase() {
		// synchronized because it may be accessible by multi threads (all dbHelper methods are synchronized)
		// and synchronized on the object because open/close are related on each other 
		synchronized(this) {
			Thread currentThread = Thread.currentThread();

			states.put(currentThread, true);  // this thread requires that this database should be opened


			return super.getWritableDatabase();
		}
	}

	/**
	 * Close database if all threads dont need the database anymore
	 * @return true if closed, false otherwise
	 */
	public boolean closeIfNeeded() {
		// synchronized because it may be accessible by multi threads (all dbHelper methods are synchronized)
		// and synchronized on the object because open/close are related on each other 
		synchronized(this) {
			Thread currentThread = Thread.currentThread();


			states.put(currentThread, false); // this thread requires that this database should be closed

			boolean mustBeClosed = true;

			// if all threads asked for closing database, then close it
			Boolean opened = null;
			Thread thread = null;
			for (Map.Entry<Thread, Boolean> entry : states.entrySet()) {
				thread = entry.getKey();
				opened = entry.getValue();
				if (thread != null && opened != null) {
					if (opened.booleanValue()) {
						// one thread still requires that database should be opened
						mustBeClosed = false;
					}
				}
			}


			if (mustBeClosed) {
				super.close();
			}

			return mustBeClosed;
		}
	}
}
