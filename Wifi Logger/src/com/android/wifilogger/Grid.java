package com.android.wifilogger;

import java.util.HashMap;

import com.android.wifilogger.db.tables.Wifi;

import android.graphics.Point;
import android.util.Log;

public abstract class Grid {

	private HashMap<HashPoint,GridField> gridField;
	
	private int height;
	private int width;
	

	


	public Grid(int x, int y) {
		this.width = x;
		this.height = y;
		
		gridField = new HashMap<Grid.HashPoint, GridField>();
	}
	
	public void initGrid() {
		HashPoint tempPoint;
		for(int i = 0; i < width; i++) {
			for(int k = 0; k < height; k++) {
				tempPoint = new HashPoint(i, k);
				gridField.put(tempPoint, getGridField(i,k));
			}
		}
	}
	
	public GridField getField(int x, int y) {
		GridField returnField = gridField.get(new HashPoint(x,y));
		return returnField;
	}
	


	
	
	private class HashPoint {
		int x;
		int y;
		
		public HashPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int hashCode() {
			return x + y*1447;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof HashPoint) {
				HashPoint hashPoint1 = (HashPoint) o;
				if(x == hashPoint1.x && y == hashPoint1.y)
					return true;
			}
			return false;
		}
	}
	
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
	
	public abstract GridField getGridField(int x, int y);
}
