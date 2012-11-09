package com.android.wifilogger;

import com.android.wifilogger.R;

import android.graphics.drawable.Drawable;

public class BigTextItemization extends TextItemization {

	public BigTextItemization(Drawable marker) {
		super(marker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDrawableRes() {
		// TODO Auto-generated method stub
		return R.drawable.markerbig;
	}

	@Override
	public int getTextSize() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return "100";
	}

}
