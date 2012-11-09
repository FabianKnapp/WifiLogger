package com.android.wifilogger.UI.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.wifilogger.Connectivity.GoOnlineCommander;


public class WifiListItemDialogFragment extends SherlockDialogFragment {

	OnClickListener negListener;
	OnClickListener posListener;
	String message = "";
	double lat, lng;

    public static WifiListItemDialogFragment newInstance(String msg, double lat, double lng) {
    	WifiListItemDialogFragment f = new WifiListItemDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("msg", msg);
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);

        f.setArguments(args);

        return f;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ArrayList<GoOnlineCommander> commander = new ArrayList<GoOnlineCommander>();
		message = getArguments().getString("msg");
		lat = getArguments().getDouble("lat");
		lng = getArguments().getDouble("lng");

		

		
		
		posListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				navToWifi();
			}
		};
		
		negListener = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
               .setPositiveButton("Navigate to", posListener)
               .setNegativeButton("Cancel", negListener);
        // Create the AlertDialog object and return it
        return builder.create();

		
	};
	
	private void navToWifi() {
		getSherlockActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + lat + "," + lng)));

	}

}
