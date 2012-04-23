package com.practica.serviceclient;


import java.util.HashMap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.practica.ServiceBroadcastClient.FriendMessageParcelable;


public class ServiceClientActivity extends Activity implements OnClickListener
{
	static String UserName = "Unknow";
	
	/** Messenger for communicating with service. */
    static Messenger mService = null;
    
    /** Flag indicating whether we have called bind on the service. */
    boolean mIsBound;
    	
	 /** Target we publish for clients to send messages to IncomingHandler. */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    private static ChatActivity chat;
    private static LogActivity log;
    private static LocActivity loc;
    public static boolean chat_active = false;
    public static boolean log_active = false;
    public static boolean loc_active = false;

    private TextView tstatus;
    private LinearLayout ly;
    private EditText ename;
    
    private static HashMap husers;
    
    private int nusers=0;
    private int nid=0;
    
    private LocationListener mloc;
    private static LocationManager milocManager;
    

	
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() 
    {
    	
    	public void onServiceConnected(ComponentName className,
                    IBinder service) 
    	{
    		// This is called when the connection with the service has been
    		// established, giving us the service object we can use to
    		// interact with the service.  We are communicating with our
    		// service through an IDL interface, so get a client-side
    		// representation of that from the raw service object.
    		Log.e("ServiceBroadcastClientActivity", "onServiceConnected::Serviced connected");
    		mService = new Messenger(service);

    		// We want to monitor the service for as long as we are
    		// connected to it.
    		register();

    		// As part of the sample, tell the user what happened.
    		Toast.makeText(ServiceClientActivity.this, R.string.ServiceConnected,
    				Toast.LENGTH_SHORT).show();
    	}

    	
    	public void onServiceDisconnected(ComponentName className) 
    	{
    		// This is called when the connection with the service has been
    		// unexpectedly disconnected -- that is, its process crashed.
    		mService = null;
    		
			tstatus.setText(getString(R.string.serno));
			tstatus.setTextColor(Color.RED);
    		// As part of the sample, tell the user what happened.
    		Toast.makeText(ServiceClientActivity.this, R.string.ServiceDisconnected,
    				Toast.LENGTH_SHORT).show();
    	}
    };    
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.menu);
        
        tstatus = (TextView) findViewById(R.id.cstatus);
        ly = (LinearLayout) findViewById(R.id.lfriends);
        
        ename = (EditText) findViewById(R.id.editname);
        ename.setMaxLines(1);
        
        husers = new HashMap<String, Integer>();
        
        nid++;
        nusers++;
              
        Button button = (Button)findViewById(R.id.button_change);
        
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	changeUserName( ename.getText().toString());		
            }
          });
        
        
        button = (Button)findViewById(R.id.button_exit);
        
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            }
          });
               
        milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(milocManager == null)Log.e("ServiceBroadcastClientActivity", "LOCATION FAIL");
        else Log.d("ServiceBroadcastClientActivity", "LOCATION OK");
        
        mloc = new MiLocationListener();

        milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mloc);
        
        
        //Check GPS listener
        TextView locte = (TextView) findViewById(R.id.loct);
        Location location = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null) location = milocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null){locte.setText(getString(R.string.disp));locte.setTextColor(Color.GREEN);}
        else {locte.setText(getString(R.string.nodisp));locte.setTextColor(Color.RED);}
        
        
        
        Log.e("ServiceBroadcastClientActivity", "onCreate::Activity Create.");
        
    }
    
    @Override
    public void onStart(){
        super.onStart();
        // Bind with the Broadcast Message Service.
        doBindService();
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed -> Disconnect from Broadcast service.
        doUnbindService();
    }
    

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.omenu, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        
            case R.id.ichat:    Intent intent = new Intent (ServiceClientActivity.this, ChatActivity.class);                          
            					startActivity(intent);
                                break;
                                
            case R.id.ilog:     Intent intent1 = new Intent (ServiceClientActivity.this, LogActivity.class);                          
								startActivity(intent1);
                                break;
                                
            case R.id.iloc:     Intent intent2 = new Intent (ServiceClientActivity.this, LocActivity.class);                          
								startActivity(intent2);
                                break;
        }
        return true;
    }
    
    private void changeUserName(String name)
    {
    	if(husers.containsKey(name))
    	{
    		Toast.makeText(this, getString(R.string.userexist), Toast.LENGTH_LONG).show();
    		return;
    	}
    	unregister();

    	UserName = name;

    	register();
    	Toast.makeText(this, getString(R.string.uchange),Toast.LENGTH_SHORT).show();
    }
    
    public static void sendChatText(String s)
    {
        if (mService != null) {
        	
        	Location location = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location == null)
			{
				location = new Location("Default");
				location.setLatitude(0.0d);
				location.setLongitude(0.0d);
			}
			
			FriendMessageParcelable fmsg = new FriendMessageParcelable(0,UserName,location,s);
			fmsg.setType(FriendMessageParcelable.MSG_CHAT);
			
			try {
		    	// Build android message.
				Bundle b = new Bundle();
				b.putParcelable("FriendMessageParcelable",fmsg);
				Message msg = Message.obtain(null, FriendMessageParcelable.MSG_SEND_MESSAGE, 0, 0);
				msg.setData(b);
				// Send Broadcast Message to the service.
				mService.send(msg);
				chat.writeChat(fmsg.getChat(), UserName);
				
				Log.e("ServiceBroadcastClientActivity", "onClick::Message send to service ("+fmsg.getType()+","+fmsg.getFrom()+","+fmsg.getLocation().toString()+")");
			} 
			catch (RemoteException e) {
				//Print("Error, Sending message!!!\n");
	        }
        }
    }
    
    private void addfriend(String friend)
    {
    	if(!friend.equals("LocalClient") && !husers.containsKey(friend))
    	{
    		Log.d("ServiceBroadcastClientActivity", "Addfriend - *"+friend+"*");
	    	CheckBox cb = new CheckBox(this);
	    	cb.setId(100+nid);
	    	husers.put(friend, 100+nid);
	    	
	    	nid++;
	    	nusers++;
	    	cb.setText(friend);
	    	cb.setTextColor(Color.BLACK);
	    	cb.setChecked(true);
	    	     
	    	ly.addView(cb);
    	}
    }

    private void removefriend(String friend)
    {
    	if(husers.containsKey(friend) && (UserName != friend))
    	{
    		int id = (Integer) husers.get(friend);
    		CheckBox cb = (CheckBox) findViewById(id);
    		ly.removeView(cb);
    		husers.remove(friend);
    		if(chat_active)chat.removeUser(friend);
    		if(log_active)loc.removeUser(friend);
    		nusers--;
    		
    	}
    }
    
    private boolean useractive(String user)
    {
    	int id = (Integer) husers.get(user);
    	CheckBox cb = (CheckBox) findViewById(id);
    	return cb.isChecked();
    }
    
    private void updateloc()
    {

    	Location location = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(location != null && loc_active)loc.updateSelf(location);
    	
        if (mService != null && location != null) {
        	

    		FriendMessageParcelable fmsg = new FriendMessageParcelable(0,UserName,location,"UpdateLoc");
    		fmsg.setType(FriendMessageParcelable.MSG_UPDATE_MY_LOCATION);
    		
    		try {
    	    	// Build android message.
    			Bundle b = new Bundle();
    			b.putParcelable("FriendMessageParcelable",fmsg);
    			Message msg = Message.obtain(null, FriendMessageParcelable.MSG_SEND_MESSAGE, 0, 0);
    			msg.setData(b);
    			// Send Broadcast Message to the service.
    			mService.send(msg);
    			
    			Log.e("ServiceBroadcastClientActivity", "onClick::Message send to service ("+fmsg.getType()+","+fmsg.getFrom()+","+fmsg.getLocation().toString()+")");
    		} 
    		catch (RemoteException e) {
    			//Print("Error, Sending message!!!\n");
            }
        }
    	
    }
    


 
    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case FriendMessageParcelable.MSG_RECEIVED_MESSAGE: 				
    				Bundle b = msg.getData();
    				b.setClassLoader(FriendMessageParcelable.class.getClassLoader());
    				FriendMessageParcelable fmsg  =  (FriendMessageParcelable) b.getParcelable("FriendMessageParcelable");
    				
    				Log.e("ServiceBroadcastClientActivity", "handleMessage::Message received from service ("+fmsg.getType()+","+fmsg.getFrom()+","+fmsg.getLocation().toString()+")");
    				
    				if(log_active)log.writeLog(fmsg);
    				
    				switch (fmsg.getType())
    				{
    				case FriendMessageParcelable.MSG_CHAT:
    					if(chat_active && useractive(fmsg.getFrom()))chat.writeChat(fmsg.getChat(), fmsg.getFrom());
    					addfriend(fmsg.getFrom());
    					break;
    					
    				case FriendMessageParcelable.MSG_ADD_FRIEND:
    					if(chat_active)chat.writeChat(fmsg.getChat(), fmsg.getFrom());
    					addfriend(fmsg.getFrom());
    					break;
    					
    				case FriendMessageParcelable.MSG_REMOVE_FRIEND:
    					if(chat_active)chat.writeChat(fmsg.getChat(), fmsg.getFrom());
    					removefriend(fmsg.getFrom());
    					break;
    					
    				case FriendMessageParcelable.MSG_UPDATE_MY_LOCATION:
    					if(loc_active)loc.updateMap(fmsg.getFrom(), fmsg.getLocation());
    					break;
    				}
    				
    				break;
    			default:
    				super.handleMessage(msg);
    		}
    	}
    }

    
    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
    	try {
    		bindService(new Intent("com.practica.ServiceBroadCastClient.BROADCAST_MESSAGE_SERVICE"), mConnection, Context.BIND_AUTO_CREATE);
    		updateServiceStatus();
    		
    	}
        catch (Exception e) {
        	Log.e("ServiceBroadcastClientActivity", "doBindService::Error binding -> ", e);
            e.printStackTrace(); 
        }
        mIsBound = true;
        updateServiceStatus();
    }

  
    private void updateServiceStatus() {
  	  String bindStatus = mConnection == null ? "unbound" : "bound";
  	  String statusText = "Service status: "+
  							bindStatus+"\n";
  	  
  	  if (mConnection != null)
  	  {
			tstatus.setText(getString(R.string.seryes));
			tstatus.setTextColor(Color.GREEN);
  	  }
  	  else
  	  {
			tstatus.setText(getString(R.string.serno));
			tstatus.setTextColor(Color.RED);
  	  }
  	  
  	}

    private void unregister()
    {
        if (mService != null) {
            try {
            	// Build UNREGISTER_CLIENT Message.
            	
            	Location location = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    			if(location == null)
    			{
    				location = new Location("OnClick");
    				location.setLatitude(0.0d);
    				location.setLongitude(0.0d);
    			}
    			FriendMessageParcelable fmsg = new FriendMessageParcelable(0,UserName,location,getString(R.string.exit));
    			Bundle b = new Bundle();
				b.putParcelable("FriendMessageParcelable",fmsg);
            	
				// Send UNREGISTER_CLIENT Message
            	Message msg = Message.obtain(null,
            			FriendMessageParcelable.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
				msg.setData(b);
                mService.send(msg);
				
            } catch (RemoteException e) {
                // There is nothing special we need to do if the service
                // has crashed.
            }
        }
    	
    }
    
    private void register()
    {
		try {
			if (mService != null) {
				// Test Message Creation.
				// Setting up test Location
	        	Location location = milocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location == null)
				{
					location = new Location("OnClick");
					location.setLatitude(0.0d);
					location.setLongitude(0.0d);
				}
				// Creating FriendMessageParcelable
				FriendMessageParcelable fmsg = new FriendMessageParcelable(0,UserName,location,getString(R.string.join));
				
				// Create android message.
				Message msg = Message.obtain(null,
						FriendMessageParcelable.MSG_REGISTER_CLIENT);
				// Create Bundle to insert own FriendMessageParcelable on the message
				Bundle b = new Bundle();
				b.putParcelable("FriendMessageParcelable",fmsg); // Insert message
				msg.setData(b); 				// set Bundle on android message.		
				msg.replyTo = mMessenger;		// set IncomingHandler for service replies. 
				mService.send(msg);				// Sent message to the Broadcast service.
				
				
				Log.e("ServiceBroadcastClientActivity", "onServiceConnected:: REGISTER_CLIENT Message send to service ("+fmsg.toString()+"\n");
			}
		} 
		catch (RemoteException e) {
			// In this case the service has crashed before we could even
			// do anything with it; we can count on soon being
			// disconnected (and then reconnected if it can be restarted)
			// so there is no need to do anything here.
		}
    }
    
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.

        	unregister();
        	
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

       
    
    private OnClickListener mConnectListener = new OnClickListener() {
        public void onClick(View v) {
       
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("com.practica.ServiceBroadCastClient.BROADCAST_MESSAGE_SERVICE");
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
    

    public class MiLocationListener implements LocationListener
    {
  	
    	@Override
    	public void onLocationChanged(Location loc) {
    		// TODO Auto-generated method stub
    		Log.d("ServiceBroadcastClientActivity","Location Updated");
    		
    		double lat1 = loc.getLatitude();
    		double lon1 = loc.getLongitude();
    		
    		
    		updateloc();
    		
    	}

    	@Override
    	public void onProviderDisabled(String provider) {
    		// TODO Auto-generated method stub
    		
    		Log.d("ServiceBroadcastClientActivity","Location Disabled");
    		
    	}

    	@Override
    	public void onProviderEnabled(String provider) {
    		// TODO Auto-generated method stub
    		Log.d("ServiceBroadcastClientActivity","Location Enabled");
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		// TODO Auto-generated method stub
    		Log.d("ServiceBroadcastClientActivity","Location Status");
    	}
    	

    }




   
}



