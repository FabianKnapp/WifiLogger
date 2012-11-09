package com.android.wifilogger;

import com.android.wifilogger.db.datasource.WifiDataSource;

public class WifiGrid extends Grid {

	public double startLng;
	public double startLat;
	public double endLng;
	public double endLat;
	
	public double gridFieldWidth;
	public double gridFieldHeight;

	
	
	public WifiGrid(double startLat, double startLng, double endLat, double endLng, int x, int y) {
		super(x, y);
		
		this.startLng = startLng;
		this.startLat = startLat;
		this.endLng = endLng;
		this.endLat = endLat;
		
		gridFieldWidth = (endLng - startLng) / x;
		gridFieldHeight = (endLat - startLat) / y;

		initGrid();

	}
	

	@Override
	public GridField getGridField(int x, int y) {
		
		WifiDataSource wifiDao = new WifiDataSource();
		double xStart = startLng + gridFieldWidth*x;
		double xEnd = startLng + gridFieldWidth*(x+1);
		
		double yStart = startLat + gridFieldHeight*y;
		double yEnd = startLat + gridFieldHeight*(y+1);
		
		
		return new GridField(wifiDao.getBetween(yStart, xStart, yEnd, xEnd));
	}
	
	
	
}
