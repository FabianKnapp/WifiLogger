package com.android.wifilogger.UI.fragments;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.wifilogger.db.datasource.WPA2DataSource;
import com.android.wifilogger.db.datasource.WPADataSource;
import com.android.wifilogger.db.tables.WPA;
import com.android.wifilogger.db.tables.WPA2;
import com.android.wifilogger.db.tables.Wifi;

public class WifiListClickListener implements OnItemClickListener {


	private UpdateThread updateThread;
	private WifiDetailsDialog loadListener;
	private boolean killed = false;
  
	public WifiListClickListener(WifiDetailsDialog loadListener) {
		this.loadListener = loadListener;
		
	}
	

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		
		final Wifi wifi = (Wifi) parent.getAdapter().getItem(position);
		
		Wifi[] wifis = {wifi};
		
		startUpdateThread(wifis);
		
	}
	
	private void startUpdateThread(Wifi[] wifis) {
		

			if(updateThread != null) {
				Status status = updateThread.getStatus();
				if(status == AsyncTask.Status.RUNNING) 
					stopWifiClickListenerThread();
			}
				updateThread = new UpdateThread();
				updateThread.execute(wifis);

	}
	
	private void stopWifiClickListenerThread() {
		if(updateThread != null && !updateThread.isCancelled())
			updateThread.cancel(true);
	}
	
	public void stop() {
		
		if(updateThread != null && updateThread.getStatus() != AsyncTask.Status.FINISHED) {
			updateThread.killed = true;
			updateThread.cancel(true);
		}
	}
	
	
	private String addNewLine() {
		return "\n";
	}
	
	private String buildSsidString(Wifi wifi) {
		return "SSID: " + wifi.ssid + "\n";
	}
	
	private String buildBssidString(Wifi wifi) {
		return "BSSID: " + wifi.bssid + "\n";
	}
	
	private String buildWpa2String(Wifi wifi) {
		String string = "";
		if(wifi.wpa2 == 1) {
			string += "WPA2";
			WPA2DataSource wpa2Dao = new WPA2DataSource();
			WPA2 wpa2 = (WPA2) wpa2Dao.query(wifi.bssid);
			boolean ccmp = wpa2.ccmp == 1;
			boolean tkip = wpa2.tkip == 1;
			boolean preauth = wpa2.preauth == 1;

			string += buildCcmpTkipString(ccmp, tkip);
			string += buildPreAuthString(preauth);
			string += addNewLine();
		}
		return string;
	}
	
	private String buildWpaString(Wifi wifi) {
		String string = "";
		if(wifi.wpa == 1) {
			string += "WPA";
			WPADataSource wpaDao = new WPADataSource();
			WPA wpa = (WPA) wpaDao.query(wifi.bssid);
			boolean ccmp = wpa.ccmp == 1;
			boolean tkip = wpa.tkip == 1;

			string += buildCcmpTkipString(ccmp, tkip);
			
			string += addNewLine();
		}
		return string;
	}
	
	private String buildWepString(Wifi wifi) {
		if(wifi.wep == 1)
			return "WEP\n";
		return "";
	}
	
	private String buildEssString(Wifi wifi) {
		if(wifi.ess == 1)
			return "ESS\n";
		return "";
	}
	
	private String buildWpsString(Wifi wifi) {
		if(wifi.wps == 1)
			return "WPS\n";
		return "";
	}
	
	private String buildLocationString(Wifi wifi) {
		String locString;
		locString = "Lat: " + String.valueOf(wifi.lat) + "\n";
		locString += "Lng: " + String.valueOf(wifi.lng) + "\n";
		
		return locString;
	}
	
	private String buildLevelString(Wifi wifi) {
		return "Level: " + String.valueOf(wifi.level) + "\n";
	}
	
	private String buildFreqString(Wifi wifi) {
		return "Frequency: " + String.valueOf(wifi.freq) + "\n";
	}
	
	private String buildCcmpTkipString(boolean ccmp, boolean tkip) {
		String string = "";
		if(ccmp && tkip) 
			string += " (ccmp+tkip)";
		else if(ccmp)
			string += " (ccmp)";
		else if(tkip)
			string += " (tkip)";
		
		return string;
	}
	
	private String buildPreAuthString(boolean preauth) {
		if(preauth)
			return " (Preauth)";
		return "";
	}

	private class UpdateThread extends AsyncTask<Wifi, Wifi, Wifi> {
		private String infoString;

		private boolean killed = false;
		
		@Override
		protected void onPreExecute() {
			loadListener.onWifiDetailLoadingStarted();
		};
		
		@Override
		protected Wifi doInBackground(Wifi... wifis) {
			Wifi wifi = wifis[0];
		
			infoString = buildSsidString(wifi)
					+ buildBssidString(wifi)
					+ buildLevelString(wifi)
					+ buildFreqString(wifi)
					+ addNewLine()
					+ buildWpa2String(wifi)
					+ buildWpaString(wifi)
					+ buildWepString(wifi)
					+ buildWpsString(wifi)
					+ buildEssString(wifi)
					+ addNewLine()
					+ buildLocationString(wifi);

			return wifi;
		}
		
		@Override
		protected void onPostExecute(Wifi result) {
			

			
			//AlertDialogCreator.showDialogTwoButtons(ctx, "Wifi details", infoString, left_btn_listener , "Navigate to", right_btn_listener, "Cancel");
			if(!killed)
				loadListener.onWifiDetailLoadingFinished(infoString, result);
	
			super.onPostExecute(result);
		}
		
		
		@Override
		protected void onCancelled() {
			loadListener.onWifiDetailLoadingFinished(null, null);
			super.onCancelled();
		}
		

	}
	
	public interface WifiDetailsDialog {
		public void onWifiDetailLoadingStarted();
		public void onWifiDetailLoadingFinished(String infoString, Wifi wifi);
	}

}
