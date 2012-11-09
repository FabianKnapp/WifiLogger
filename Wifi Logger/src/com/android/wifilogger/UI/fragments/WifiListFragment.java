package com.android.wifilogger.UI.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.wifilogger.R;
import com.android.wifilogger.WifiListLogic;
import com.android.wifilogger.WifiListLogic.LocationSearch;
import com.android.wifilogger.UI.fragments.WifiListClickListener.WifiDetailsDialog;
import com.android.wifilogger.db.datasource.WifiDataSource;
import com.android.wifilogger.db.tables.Wifi;

public class WifiListFragment extends SherlockFragment implements
		LocationSearch, WifiDetailsDialog {
	private ListView wifiList;
	WifiListAdapter listAdapter;
	ListContent listContent;
	private MenuItem refreshItem;

	WifiListClickListener clickListener;

	private WifiListLogic wifiListLogic;

	private int loadingRequestCounter = 0;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View content = inflater.inflate(R.layout.activity_wifi, container,
				false);

		wifiList = (ListView) content.findViewById(R.id.list_wifi);
		return content;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ButtonClickedObserver ");
		}
		clickListener = new WifiListClickListener(
				(WifiDetailsDialog) this);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.e("OptionsMenu Activity", "created");
		inflater.inflate(R.menu.activity_wifi, menu);
		refreshItem = menu.findItem(R.id.menu_refresh_btn);
		refreshItem.setEnabled(true);
		refreshItem.setVisible(true);

	};

	private synchronized void showRefreshContent() {
		Log.e("showRefreshContent", "try");

		if (refreshItem != null) {
			Log.e("showRefreshContent", "executed " + loadingRequestCounter);
			refreshItem.setActionView(R.layout.indeterminate_progress_action);
			loadingRequestCounter++;
		}
	}

	private synchronized void dismissRefreshContent() {
		Log.e("dismissRefreshContent", "try  loadingCounter = " + loadingRequestCounter);
		if (refreshItem != null && --loadingRequestCounter <= 0) {
			Log.e("dismissRefreshContent", "executed loadingCounter = " + loadingRequestCounter);

			refreshItem.setActionView(null);
			loadingRequestCounter = 0;
		}
	}

	public void onStart() {
		super.onStart();
		wifiListLogic = new WifiListLogic();

	}

	@Override
	public void onResume() {
		super.onResume();

		startListUpdate();

		Log.e("WifiList onResume", "executed");
	};

	@Override
	public void onPause() {
		super.onPause();
		stopListUpdate();
		clickListener.stop();
		if (listContent != null)
			listContent.cancel(true);
		
		
		Log.e("WifiList onPause", "executed");

	};

	@Override
	public void onDetach() {
		super.onDetach();
		// clickListener.stopWifiClickListenerThread();

	};

	@Override
	public void onStop() {
		super.onStop();
		loadingRequestCounter = 1;
		dismissRefreshContent();
		// observer.onFinishUpdate();
	};

	public void startListUpdate() {
		stopListUpdate();
		wifiListLogic.addListener(this);
		showRefreshContent();
		// observer.onStartUpdate();
	}

	public void stopListUpdate() {
		if (wifiListLogic != null && wifiListLogic.isStarted()) {
			wifiListLogic.removeListener();
		}
	}

	private class ListContent extends AsyncTask<Location, Void, Void> {

		long startTime;
		long endTime;

		@Override
		protected void onPreExecute() {
			// observer.onStartUpdate();
			// showRefreshContent();

		};

		@Override
		protected Void doInBackground(Location... locs) {
			WifiDataSource wifiDao = new WifiDataSource();

			startTime = System.currentTimeMillis();
			ArrayList<Wifi> wifiList = wifiDao.getInRadius(locs[0]);

			Log.e("wifi elements", wifiList.size() + " ");

			listAdapter = new WifiListAdapter(wifiList, locs[0]);
			Log.e("inBackground", "executed");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// observer.onFinishUpdate();
			dismissRefreshContent();

			Log.e("onPostExecute", "executed");

			wifiList.setAdapter(listAdapter);

			wifiList.setOnItemClickListener(clickListener);
			endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			Log.e("Time for List Update", " " + diff);
			// progressBar.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			listAdapter = null;
			super.onCancelled();
		}

	}

	@Override
	public void onReferenceLocationReceived(Location location) {
		Log.e("Location Received","successful");
		stopListUpdate();
		Location[] locations = { location };
		if (listContent == null
				|| listContent.getStatus() != android.os.AsyncTask.Status.PENDING)
			listContent = new ListContent();
		listContent.execute(locations);

	}

	/*
	 * @Override public void onStartLoading() { observer.onStartUpdate(); }
	 * 
	 * @Override public void onFinishedLoading() { observer.onFinishUpdate(); }
	 */

	@Override
	public void onWifiDetailLoadingStarted() {
		showRefreshContent();
	}

	@Override
	public void onWifiDetailLoadingFinished(String infoString, Wifi wifi) {
		if (infoString != null) {

			WifiListItemDialogFragment detailDialogFrag;

			FragmentManager fm = getFragmentManager();

			// Check to see if we have retained the worker fragment.
			detailDialogFrag = (WifiListItemDialogFragment) fm
					.findFragmentByTag("wifiDetail");

			// If not retained (or first time running), we need to create it.
			if (detailDialogFrag == null) {
				detailDialogFrag = WifiListItemDialogFragment.newInstance(
						infoString, wifi.lat, wifi.lng);
				SherlockFragment targetFragment = (SherlockFragment) fm
						.findFragmentByTag("list");
				// Tell it who it is working with.
				detailDialogFrag.setTargetFragment(targetFragment, 0);
				fm.beginTransaction().add(detailDialogFrag, "wifiDetail")
						.commit();

			}
		}
		dismissRefreshContent();
	}
}
