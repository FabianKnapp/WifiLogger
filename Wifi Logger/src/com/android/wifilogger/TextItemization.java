package com.android.wifilogger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public abstract class TextItemization extends Itemization{

	public TextItemization(Drawable marker) {
		super(marker);
	}
	
	@Override	
	public Drawable getDrawable() {
		int textSize = getTextSize();
		String text = getText();
		Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG);
		paint.setColor(getTextColor());
		paint.setTextSize(textSize);
		paint.setUnderlineText(underlineText());
		float textW = paint.measureText(text);
		
		 BitmapFactory.Options ops = new BitmapFactory.Options();
	     ops.inMutable = true;
	     //ops.inJustDecodeBounds = true;
	    Bitmap bitmap2 = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), getDrawableRes(), ops);
	    Bitmap bitmap = Bitmap.createScaledBitmap(bitmap2, 75, 75, true);
		Canvas canvas = new Canvas(bitmap);
		
		canvas.drawText(text, (canvas.getWidth() - textW) / 2, (canvas.getHeight() / 2) + textSize/2, paint);
		
		Drawable d = new BitmapDrawable(MyApplication.getAppContext().getResources(),bitmap);
		Log.e("Drawable",d + "");
		return d;
	};
	
	public boolean underlineText() {
		return false;
	}
	
	public int getTextColor() {
		return Color.CYAN;
	}
	
	public int getTextSize() {
		return 20;
	}
	
	public abstract int getDrawableRes();
	
	public abstract String getText();

}
