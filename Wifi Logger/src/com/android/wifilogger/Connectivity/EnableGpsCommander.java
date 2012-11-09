package com.android.wifilogger.Connectivity;

import android.content.Context;
import android.widget.Toast;

import com.android.wifilogger.MyApplication;


public class EnableGpsCommander implements GoOnlineCommander {

		private Context ctx;
		
		public EnableGpsCommander(Context ctx) {
			this.ctx = ctx;
		}
	
		@Override
		public void goOnline() {
			try {
				ctx.startActivity(MyApplication.getGpsSettingsIntent());
			} catch (Exception e) {
				Toast.makeText(MyApplication.getAppContext(), "GPS enable failed. Pls do it manually in your settings.", Toast.LENGTH_LONG).show();
			}
		}
}
