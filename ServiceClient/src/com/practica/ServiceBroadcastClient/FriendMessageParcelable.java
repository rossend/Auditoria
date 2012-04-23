package com.practica.ServiceBroadcastClient;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class FriendMessageParcelable implements Parcelable {
	
	static final long serialVersionUID = 1 ;
		
	/* Message types */ 
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_MESSAGE = 3;
	public static final int MSG_RECEIVED_MESSAGE = 4;
	
	public static final int MSG_CHAT = 5;
	public static final int MSG_UPDATE_MY_LOCATION = 6;
	public static final int MSG_ADD_FRIEND = 7;
	public static final int MSG_REMOVE_FRIEND = 8;

	private int 		Type;
	private String		From; 
	private Location	Localization;
	private String 		ChatMessage;


	public FriendMessageParcelable() {
		this.setType(0);
		this.setFrom("None");
		this.setLocation(null);
		this.setChat("");
	}	 
		
	public FriendMessageParcelable(int t, String f, Location l, String c) {
		this.setType(t);
		this.setFrom(f);
		this.setLocation(l);
		this.setChat(c);
	}
	
    public FriendMessageParcelable(Parcel source){
        /*
         * Reconstruct from the Parcel
         */
        Log.v("FriendMessageParceable", "ParcelData(Parcel source): time to put back parcel data");
        Type = source.readInt();
        From = source.readString();
        Localization = source.readParcelable(Location.class.getClassLoader());
        ChatMessage = source.readString();
    }
    
    public int describeContents(){
    	return 0;
    }


	public void writeToParcel(Parcel dest, int flags) {
	      Log.v("FriendMessageParceable", "writeToParcel..."+ flags);
	      dest.writeInt(Type);
	      dest.writeString(From);
	      dest.writeParcelable(Localization,0);
	      dest.writeString(ChatMessage);
	}
	
	
	
    public static final Parcelable.Creator<FriendMessageParcelable> CREATOR
    = new Parcelable.Creator<FriendMessageParcelable>() {
    		public FriendMessageParcelable createFromParcel(Parcel in) {
    			return new FriendMessageParcelable(in);
    		}

    		public FriendMessageParcelable[] newArray(int size) {
    			return new FriendMessageParcelable[size];
    	}
    };

	
	public void setType (int t) { 
		Type=t;
	}
		
	public void setFrom(String f) { 
		From=f;
	}
		
	public void setLocation(Location loc) { 
		Localization=loc;
	} 
		
	public void setChat(String c) { 
		ChatMessage=c;
	}
	
	public int getType () { 
		return Type;
	}
		
	public String getFrom() { 
		return From;
	}
		
	public Location getLocation() { 
		return Localization;
	} 
	
	public String getChat() { 
		return ChatMessage;
	}
}
