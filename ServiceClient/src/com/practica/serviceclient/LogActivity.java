package com.practica.serviceclient;

import java.util.Calendar;
import java.util.HashMap;

import com.practica.ServiceBroadcastClient.FriendMessageParcelable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;


public class LogActivity extends Activity implements OnClickListener
{
	
	private ServiceClientActivity parent;
	private Button sendb,retb;
	private static TextView tlog;
	private EditText echat;
	private static ScrollView scroll;
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        
        retb = (Button)findViewById(R.id.button_ret);
        
        tlog = (TextView)findViewById(R.id.textlog);
 
        scroll = (ScrollView) findViewById(R.id.scrollog);

        
        retb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	parent.log_active = false;
            	finish();
            }
          });
        
        parent.log_active = true;
    }
    
    
	@Override
	public void onPause() {
		super.onPause();
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void writeLog(FriendMessageParcelable fmsg)
	{
			
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
		int min = c.get(Calendar.MINUTE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		
		String date = new String(day+"-"+month+"-"+year+"||"+hour+":"+min+":"+seconds);
		String loc = new String ("["+fmsg.getLocation().getLatitude()+","+fmsg.getLocation().getLatitude()+"]");
		//Log.e("ServiceBroadcastClientActivity", "handleMessage::Message received from service ("+fmsg.getType()+","+fmsg.getFrom()+","+fmsg.getLocation().toString()+")");
	
		String text = new String(date+">> Msg Type:"+fmsg.getType()+", From: "+fmsg.getFrom()+", Loc: "+loc+"\n");   

		
		tlog.append(text);
		
		int height = tlog.getHeight();
		if(height > scroll.getMeasuredHeight())
		{
			scroll.scrollTo(0, height-scroll.getMeasuredHeight());
		}
		
	}
	
}