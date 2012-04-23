package com.practica.serviceclient;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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



public class ChatActivity extends Activity implements OnClickListener
{
	
	private ServiceClientActivity parent;
	private Button sendb,retb;
	private static TextView tchat;
	private EditText echat;
	private static ScrollView scroll;
	
	private static HashMap h;
	private static int maxcolor = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        

        sendb = (Button)findViewById(R.id.button_send1);
        retb = (Button)findViewById(R.id.button_ret);
        
        tchat = (TextView)findViewById(R.id.textchat);
        tchat.setText("", BufferType.SPANNABLE);
        
        echat = (EditText) findViewById(R.id.editchat);
        echat.setMaxLines(1);
        
        scroll = (ScrollView) findViewById(R.id.scroll);
        
        h = new HashMap<String, Integer>();

        sendb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String s = echat.getText().toString();
            	parent.sendChatText(s);
            	echat.setText("");

            }
          });
        
        retb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	parent.chat_active = false;
            	finish();
            }
          });
        
        parent.chat_active = true;
    }
    
    
	@Override
	public void onPause() {
		super.onPause();
		maxcolor = -1;
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


	public static void writeChat(String s, String user)
	{
		
		int color = Color.BLACK;
		if (!user.equals("LocalClient"))
		{
			if(h.containsKey(user))
			{
				color = (Integer) h.get(user);
			}
			
			else
			{
				maxcolor = (maxcolor + 1)%5;
				color = getnewcolor();
				h.put(user, color);
			}
			
			SpannableString text = new SpannableString(user+": "+s+"\n");   
			text.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
			
			tchat.append(text);
			
			int height = tchat.getHeight();
			if(height > scroll.getMeasuredHeight())
			{
				scroll.scrollTo(0, height-scroll.getMeasuredHeight());
			}
			
		}
	
	}
	
	public static void removeUser(String user)
	{
		if(h.containsKey(user))h.remove(user);
	}

	private static int getnewcolor()
	{
		int c;
		
		switch (maxcolor)
		{
			case 0:
				c = Color.BLUE;
				break;
			case 1:
				c = Color.RED;
				break;
			case 2:
				c = Color.GREEN;
				break;
			case 3:
				c = Color.BLACK;
				break;
			case 4:
				c = Color.YELLOW;
				break;
			default:
				c = Color.GRAY;
				break;
		}		
		return c;
	}
	
}
