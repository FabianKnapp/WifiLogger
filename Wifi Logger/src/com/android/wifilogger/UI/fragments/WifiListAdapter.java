package com.android.wifilogger.UI.fragments;

import java.util.ArrayList;

import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wifilogger.MyApplication;
import com.android.wifilogger.R;
import com.android.wifilogger.db.tables.Wifi;


public class WifiListAdapter extends BaseAdapter {

    private ArrayList<Wifi> entries;
    private Location location;
    private boolean withDistance;
    
    public static class ViewHolder{
        public TextView ssid;
        public TextView crypt;
        public TextView distance;

    }
    
    public WifiListAdapter(ArrayList<Wifi> entries, Location location) {
    	this.entries = entries;
    	this.location = location;
    	withDistance = true;
    }
   
    public int getCount() {
        return entries.size();
    }

    public Wifi getItem(int position) {
        return entries.get(position);
    }

    
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if(convertView == null){
            vi =  MyApplication.getLayoutInflater().inflate(R.layout.wifi_list_item, null);
            holder = new ViewHolder();
            holder.ssid = (TextView)vi.findViewById(R.id.wifi_list_ssid);
            holder.crypt = (TextView)vi.findViewById(R.id.wifi_list_crypt);
            holder.distance = (TextView)vi.findViewById(R.id.wifi_list_distance);

            vi.setTag(holder);
            
            
        }
        else{
            holder = (ViewHolder)vi.getTag();
        }
        Wifi wifi = entries.get(position);
        
        holder.ssid.setText(wifi.ssid);
        
        StringBuilder cryptString = new StringBuilder(20);
        
        if(wifi.wep == 1)
        	cryptString.append("WEP ");
        if(wifi.wpa == 1)
        	cryptString.append("WPA ");
        if(wifi.wpa2 == 1)
        	cryptString.append("WPA2 ");
        if(wifi.wps == 1)
        	cryptString.append("WPS ");
        if(wifi.ess == 1)
        	cryptString.append("ESS");
        
        
        String distanceString;
        if(withDistance) {
            float[] results = new float[1];
        	Location.distanceBetween(location.getLatitude(), location.getLongitude(), wifi.lat, wifi.lng, results);
        	distanceString = MyApplication.formatDistance(results[0]);
        } else
        	distanceString = "unknown";
        	
        holder.distance.setText(distanceString);

        holder.crypt.setText(cryptString.toString().trim());
        //holder.distance.setText(results[0] + "");


        holder.ssid.setTag(position);
        holder.crypt.setTag(position);
        holder.distance.setTag(position);


        return vi;
    }
    
}
