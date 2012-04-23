package com.practica.BroadCastClient;
 
import java.io.Serializable;


public class FriendMessage implements Serializable  {
	
	static final long serialVersionUID = 1 ;
	
	/* Message types */
	public static final int MSG_CHAT = 5;
	public static final int MSG_UPDATE_MY_LOCATION = 6;
	public static final int MSG_ADD_FRIEND = 7;
	public static final int MSG_REMOVE_FRIEND = 8;
	
	private int 	Type;
	private String	From;
	private String 	ChatMessage;
	private double	LocLatitude;
	private double	LocLongitude;
	private String	LocProvider;

	
	public FriendMessage() {
		this.setType(0);
		this.setFrom("None");
		this.setChatMessage("");
		this.setLatitude(0);
		this.setLongitude(0);
		this.setProvider(null);
	}	 
	
	
	public FriendMessage(int t, String f) {
		this.setType(t);
		this.setFrom(f);
		this.setChatMessage("");
		this.setLatitude(0);
		this.setLongitude(0);
		this.setProvider(null);
	}
	
	
	public FriendMessage(int t, String f, String chat, double lat, double lon, String pro) 
	{ 
		this.setType(t);
		this.setFrom(f);
		this.setChatMessage(chat);
		this.setLatitude(lat);
		this.setLongitude(lon);
		this.setProvider(pro);
	}
	
	public void setType (int t) { 
		Type=t;
	}
	
	public void setFrom(String f) { 
		From=f;
	}
	
	public void setChatMessage(String chat) { 
		ChatMessage=chat;
	}
	
	public void setLatitude(double lat) {
		LocLatitude=lat;
	}
	
	public void  setLongitude(double lon) {
		LocLongitude=lon;
	}
	
	public void setProvider(String pro) {
		LocProvider=pro;
	}
	
	
	public void setLocation(double lat, double lon, String pro) { 
		LocLatitude=lat;
		LocLongitude=lon;
		LocProvider=pro;
	}

	
	public int getType () { 
		return Type;
	}
	
	public String getFrom() { 
		return From;
	}
	
	public String getChatMessage() { 
		return ChatMessage;
	}
	
	public double getLatitude() {
		return LocLatitude;
	}
	
	public double getLongitude() {
		return LocLongitude;
	}
	
	public String getProvider() {
		return LocProvider;
	}
	
	public String toString() {
		return new String("Type: "+Type+" From: "+From+" Mes:"+ChatMessage+" Loc: "+LocLatitude+","+LocLongitude);
	}
}
