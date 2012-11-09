package com.android.wifilogger.Connectivity;

import com.android.wifilogger.MyApplication;



public class DisableAirplaneCommander implements GoOnlineCommander {

		@Override
		public void goOnline() {
			MyApplication.disableAirplaneMode();
		}
		
	
}
