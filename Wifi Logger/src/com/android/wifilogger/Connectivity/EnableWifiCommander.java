package com.android.wifilogger.Connectivity;

import com.android.wifilogger.MyApplication;


public class EnableWifiCommander implements GoOnlineCommander {

		@Override
		public void goOnline() {
			MyApplication.getWifiManager().setWifiEnabled(true);
		}
		
	
}
