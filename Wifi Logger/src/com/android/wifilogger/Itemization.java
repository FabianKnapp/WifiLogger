package com.android.wifilogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.android.wifilogger.R;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public abstract class Itemization extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public Itemization(Drawable marker) {
		super(boundCenterBottom(marker));
		// super(defaultMarker);
		populate();

	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	public void addOverlays(Collection<OverlayItem> overlays) {
		if(overlays != null) {
			Iterator<OverlayItem> itOverlay = overlays.iterator();
			while(itOverlay.hasNext()) {
				OverlayItem overlay = itOverlay.next();
				overlay.setMarker(boundCenterBottom(getDrawable()));
				//overlay.setMarker(MyApplication.getAppContext().getResources().getDrawable(R.drawable.androidmarker));

			}
			mOverlays.addAll(overlays);
			setLastFocusedIndex(-1);
			populate();
		}
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		setLastFocusedIndex(-1);
		populate();
	}
	
	public void clearOverlays() {
		mOverlays.clear();
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public abstract Drawable getDrawable();
}
