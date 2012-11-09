package com.android.wifilogger.db;


public abstract class EntryBuilder {
	
	protected Entry entry;
	
	public Entry getEntry() {
		return entry;
	}
	
	
	public abstract void buildWepEntry();
	public abstract void buildWpaEntry();
	public abstract void buildWpa2Entry();
	public abstract void buildWpsEntry();
	public abstract void buildWifiEntry();
	public abstract void buildEssEntry();
	
}
