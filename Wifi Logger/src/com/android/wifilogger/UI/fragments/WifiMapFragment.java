package com.android.wifilogger.UI.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.http.impl.client.TunnelRefusedException;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.OnZoomChangeListener;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.wifilogger.Grid;
import com.android.wifilogger.GridField;
import com.android.wifilogger.MyApplication;
import com.android.wifilogger.R;
import com.android.wifilogger.WifiGrid;
import com.android.wifilogger.UI.MainTabActivity;
import com.android.wifilogger.db.datasource.WifiDataSource;
import com.android.wifilogger.db.tables.Wifi;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class WifiMapFragment extends SherlockFragment {

    Itemization itemizedOverlay;
    List<Overlay> mapOverlays;
    Drawable drawable;
    List<Wifi> wifiList;
    private Handler myHandler;
    private AddMarker addMarker;
		
		public WifiMapFragment() {
			
			myHandler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
			        //MainTabActivity.Exchanger.mMapView.invalidate();
				}
			};
		}
		


		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
			// The Activity created the MapView for us, so we can do some init stuff.
			MainTabActivity.Exchanger.mMapView.setClickable(true);
			MainTabActivity.Exchanger.mMapView.setBuiltInZoomControls(true); // If you want.
			/* MainTabActivity.Exchanger.mMapView
			 * If you're getting Exceptions saying that the MapView already has
			 * a parent, uncomment the next lines of code, but I think that it
			 * won't be necessary. In other cases it was, but in this case I
			 * don't this should happen.
			 */
			
			 final ViewGroup parent = (ViewGroup) MainTabActivity.Exchanger.mMapView.getParent();
			  if (parent != null) parent.removeView(MainTabActivity.Exchanger.mMapView);
			 
		   /* 
		        GeoPoint point = new GeoPoint(49873224, 8651499);
		        OverlayItem overlayitem = new OverlayItem(point, "", "");
		        itemizedOverlay.addOverlay(overlayitem);
		        
		        GeoPoint point2 = new GeoPoint(35410000, 139460000);
		        OverlayItem overlayitem2 = new OverlayItem(point2, "", "");
		        itemizedOverlay.addOverlay(overlayitem2);
*/
		        
		        
		      /*
		        new Thread(new Runnable() {
					
					@Override
					public void run() {
				        addMarkers();
				        myHandler.sendEmptyMessage(0);
					}
				}).start();*/
		        
		        addMarker = new AddMarker();
		        
		        addMarker.execute();
				
			return MainTabActivity.Exchanger.mMapView;
		}
		

		
	
		
		
		
		private class AddMarker extends AsyncTask<Void, List<Wifi>, Collection<OverlayItem>> {
			
			
			Grid wifiGrid;
			
			

			
			@Override
			protected void onPreExecute() {
				MainTabActivity.Exchanger.mMapView.removeAllViews();
				super.onPreExecute();
			}
			
		
			@Override
			protected Collection<OverlayItem> doInBackground(Void... params) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 mapOverlays = MainTabActivity.Exchanger.mMapView.getOverlays();
				 mapOverlays.clear();
			     drawable = MyApplication.getAppContext().getResources().getDrawable(R.drawable.androidmarker);
			     itemizedOverlay = new BigTextItemization(drawable);
			     mapOverlays.add(itemizedOverlay);

				
				GeoPoint center = MainTabActivity.Exchanger.mMapView.getMapCenter();
				int latSpan = MainTabActivity.Exchanger.mMapView.getLatitudeSpan();
				int lngSpan = MainTabActivity.Exchanger.mMapView.getLongitudeSpan();

				Log.e("latSpan", latSpan + "");
				Log.e("lngSpan", lngSpan + "");

		        OverlayItem overlayitem;
		        Collection<OverlayItem> markerItems = new ArrayList<OverlayItem>();


				wifiGrid = new WifiGrid((center.getLatitudeE6()-latSpan/2)/1e6, (center.getLongitudeE6()-lngSpan/2)/1e6, (center.getLatitudeE6()+latSpan/2)/1e6, (center.getLongitudeE6()+lngSpan/2)/1e6, 5, 5);
				GridField gridField;
				for(int x = 0; x < wifiGrid.getWidth(); x++) {
					for(int y = 0; y < wifiGrid.getHeight(); y++) {
						gridField = wifiGrid.getField(x, y);
						GeoPoint point = getMiddlePoint(gridField.wifis);
						
						if(point != null && point.getLatitudeE6() != 0 && point.getLatitudeE6() != 0) {
							overlayitem = new OverlayItem(point, "", "");
							markerItems.add(overlayitem);
						}
					}
				}
				Log.e("marker Items", markerItems.size() + " elements");

				
				return markerItems;
			}
			
			private GeoPoint getMiddlePoint(ArrayList<Wifi> wifis) {
				if(wifis != null && wifis.size() > 0) {
					int lng = 0;
					int lat = 0;
					
					int size = wifis.size();
					Iterator<Wifi> itWifi = wifis.iterator();
					while(itWifi.hasNext()) {
						Wifi tempWifi = itWifi.next();
						lng += (int)(tempWifi.lng*(1000000/size));
						lat += (int)(tempWifi.lat*(1000000/size));
					}
					

					return new GeoPoint(lat,lng);
				} else
					return null;
			}
			
			
			@Override
			protected void onPostExecute(Collection<OverlayItem> result) {
				Log.e("Result Elements on Map", result.size() + "");
				itemizedOverlay.addOverlays(result);
				MainTabActivity.Exchanger.mMapView.invalidate();
				super.onPostExecute(result);
			}

		}
		
		
	
}
