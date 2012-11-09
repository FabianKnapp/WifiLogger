package com.android.wifilogger.UI.fragments;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.wifilogger.MyApplication;
import com.android.wifilogger.R;
import com.android.wifilogger.Connectivity.DisableAirplaneCommander;
import com.android.wifilogger.Connectivity.EnableGpsCommander;
import com.android.wifilogger.Connectivity.EnableWifiCommander;
import com.android.wifilogger.Connectivity.GoOnlineCommander;
/*
 *
onCreate
onActivityCreated
onCreateDialog
onCreateView
onStart

onCancel
onDismissDialog
onStop
onDismissView

 *
 */
public class OfflineDialog extends SherlockDialogFragment {

	private OfflineDialogListener listener;
	private Context ctx;
	OnClickListener goOnline;
	OnClickListener cancel;
	private String failureString = "";
	
	

	/*
	 static OfflineDialog newInstance(int num) {
		 OfflineDialog f = new OfflineDialog();

         // Supply num input as an argument.
         Bundle args = new Bundle();
         args.putInt("num", num);
         f.setArguments(args);

         return f;
     }
     */
	
	
    
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OfflineDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OfflineDialogListener ");
		}
		//ctx = (Context) activity;
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final ArrayList<GoOnlineCommander> commander = new ArrayList<GoOnlineCommander>();
		final boolean airplaneFailure = MyApplication.isAirplaneMode();
		final boolean wifiFailure = !MyApplication.isWifiEnabled();
		final boolean gpsFailure = !MyApplication.isGpsEnabled();

		if(airplaneFailure) {
			failureString = MyApplication.getAppContext().getResources().getString(R.string.dialog_airplane_off);
			commander.add(new DisableAirplaneCommander());
			commander.add(new EnableWifiCommander());
			commander.add(new EnableGpsCommander(getActivity()));
		}
		else if(wifiFailure && gpsFailure) {
			failureString = MyApplication.getAppContext().getResources().getString(R.string.dialog_wifi_gps);
			commander.add(new EnableWifiCommander());
			commander.add(new EnableGpsCommander(getActivity()));
		} else if(wifiFailure) {
			failureString = MyApplication.getAppContext().getResources().getString(R.string.dialog_wifi);
			commander.add(new EnableWifiCommander());
		} else if(gpsFailure) {
			failureString = MyApplication.getAppContext().getResources().getString(R.string.dialog_gps);
			commander.add(new EnableGpsCommander(getActivity()));
		} else {
			failureString = "This message is a bug.";
			dismiss();
		}

		
		goOnline = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				Iterator<GoOnlineCommander> commandIt = commander.iterator();
				
				while(commandIt.hasNext())
					commandIt.next().goOnline();
				
				((OfflineDialogListener) getActivity()).onUserClick(true);
			}
		};
		
		cancel = new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((OfflineDialogListener) getActivity()).onUserClick(false);

			}
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(failureString)
               .setPositiveButton("Enable it", goOnline)
               .setNegativeButton("Cancel", cancel);
        // Create the AlertDialog object and return it
        return builder.create();

		
	};
	
	
	
	
	public interface OfflineDialogListener {
		public void onUserClick(boolean userDecision);
		
	}
	
	
	
	
}
