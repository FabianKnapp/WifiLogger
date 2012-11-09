package com.android.wifilogger.UI;

import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;


public class AlertDialogCreator {
	
	
	private static TreeMap<String, Dialog> dialogMap = new TreeMap<String, Dialog>();
	
	public static Dialog buildDialogTwoButtons(Context ctx,String title,String tag,String msg, OnClickListener left_btn_listener, String left_btn_str,OnClickListener right_btn_listener, String right_btn_str){
		return buildDialog(ctx,title,tag,msg, left_btn_listener, left_btn_str,right_btn_listener, right_btn_str,null,"",null);
	}
	

	public static Dialog buildDialogThreeButtons(Context ctx,String title,String tag,String msg, OnClickListener left_btn_listener, String left_btn_str,OnClickListener right_btn_listener, String right_btn_str,OnClickListener middle_btn_listener, String middle_btn_str){
		return buildDialog(ctx,title,tag,msg, left_btn_listener, left_btn_str,right_btn_listener, right_btn_str,middle_btn_listener,middle_btn_str,null);
	}
	

	public static Dialog buildDialogOneButton(Context ctx,String title,String tag,String msg, OnClickListener btn_listener, String btn_str){
		return buildDialog(ctx,title,tag,msg, btn_listener, btn_str,null, "",null,"",null);
	}

	
	private static Dialog buildDialog(Context ctx,String title, String tag,String msg, OnClickListener paramOnClickListener1, String str1,OnClickListener paramOnClickListener2, String str2,OnClickListener paramOnClickListener3, String str3,OnKeyListener onKeyListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(msg)
				.setTitle(title)
				.setCancelable(false);
		if(onKeyListener != null){
			builder.setOnKeyListener(onKeyListener);
		}
		if(paramOnClickListener1 != null && str1 != ""){
			builder.setPositiveButton(str1,paramOnClickListener1);
		}
		if(paramOnClickListener2 != null && str2 != ""){
			builder.setNegativeButton(str2,paramOnClickListener2);
		}
		if(paramOnClickListener3 != null && str3 != ""){
			builder.setNeutralButton(str3,paramOnClickListener3);
		}
		Dialog dialog = new Dialog(ctx);
		dialog = builder.create();
		dialog.setTitle(title);
		dialogMap.put(tag, dialog);
		return dialog;
		
	}
	
	public static void dismissAndRemoveDialog(String tag) {
		if(dialogMap.containsKey(tag)) {
			(dialogMap.get(tag)).dismiss();
			dialogMap.remove(tag);
		}
	}
	
	public static void dismissDialog(String tag) {
		if(dialogMap.containsKey(tag)) {
			(dialogMap.get(tag)).dismiss();
			dialogMap.remove(tag);
		}
	}
	
	public static boolean isDialogCreated(String tag) {
		if(dialogMap.containsKey(tag))
			return true;
		else
			return false;
	}
	
	public static Dialog getDialog(String tag) {
		return dialogMap.get(tag);
	}
	
	
	public static Dialog getLatestDialog() {
		return dialogMap.lastEntry().getValue();
	}

}
