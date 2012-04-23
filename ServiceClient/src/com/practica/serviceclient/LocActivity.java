package com.practica.serviceclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class LocActivity extends MapActivity {

	private ServiceClientActivity parent;
	private static MapView mMapView;
	
	private static Drawable marker,selfmarker;
	
	private static HashMap h;
	private static MyItemizedOverlay myItemizedOverlay;
	private static MyItemizedOverlay SelfOverlay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loc);

        //you could get a reference to the map view from the main.xml
        //layout like this...
        mMapView = (MapView)findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
        
        Button b = (Button)findViewById(R.id.button_ret);
        
        h = new HashMap<String, GeoPoint>();
        
        
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	parent.loc_active = false;
            	finish();
            }
          });
        
        
        
        marker=getResources().getDrawable(R.drawable.marker);
        selfmarker=getResources().getDrawable(R.drawable.selfmarker);
        
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        
        markerWidth = selfmarker.getIntrinsicWidth();
        markerHeight = selfmarker.getIntrinsicHeight();
        selfmarker.setBounds(0, markerHeight, markerWidth, 0);
    
        myItemizedOverlay = new MyItemizedOverlay(marker);
        mMapView.getOverlays().add(myItemizedOverlay);
        
        SelfOverlay = new MyItemizedOverlay(selfmarker);
        mMapView.getOverlays().add(SelfOverlay);
        
        parent.loc_active = true;
        
        
        
    }

    //you must provide an implementation for isRouteDisplayed()
    //when you extend MapActivity...
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    public static void updateMap(String user, Location l)
    {
    	int lat = (int)(l.getLatitude()*1000000);
    	int lon = (int)(l.getLongitude()*1000000);
    	GeoPoint gp = new GeoPoint(lat,lon);
    	h.put(user, gp);
    	
    	Collection c = h.values();
    	Iterator it = c.iterator();
    	
    	myItemizedOverlay.clearItems();
    	
        while(it.hasNext())
        {
            myItemizedOverlay.addItem((GeoPoint)it.next(), user+" Point", user+" Point");
        }
        
    }
    
    public static void updateSelf(Location l)
    {
    	int lat = (int)(l.getLatitude()*1000000);
    	int lon = (int)(l.getLongitude()*1000000);
    	GeoPoint gp = new GeoPoint(lat,lon);
    	
    	SelfOverlay.clearItems();
    	SelfOverlay.addItem(gp, "Me", "Me");
    }
    
    public static void removeUser(String user)
    {
    	if(h.containsKey(user))h.remove(user);
    	
    	Collection c = h.values();
    	Iterator it = c.iterator();
    	
    	myItemizedOverlay.clearItems();
    	
        while(it.hasNext())
        {
            myItemizedOverlay.addItem((GeoPoint)it.next(), user+" Point", user+" Point");
        }
    }

    
    public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
   	 
    	private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
    	 
	    	public MyItemizedOverlay(Drawable marker) {
		    	super(boundCenterBottom(marker));
		    	// TODO Auto-generated constructor stub
		    	 
		    	populate();
	    	}
    	 
	    	public void addItem(GeoPoint p, String title, String snippet){
		    	OverlayItem newItem = new OverlayItem(p, title, snippet);
		    	overlayItemList.add(newItem);
		    	   populate();
	    	}
    	 
	    	public void clearItems()
	    	{
	    		overlayItemList.clear();
	    		populate();
	    	}
	    	@Override
	    	protected OverlayItem createItem(int i) {
		    	// TODO Auto-generated method stub
		    	return overlayItemList.get(i);
	    	}
    	 
	    	@Override
	    	public int size() {
		    	// TODO Auto-generated method stub
		    	return overlayItemList.size();
	    	}
    	 
	    	@Override
	    	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		    	// TODO Auto-generated method stub
		    	super.draw(canvas, mapView, shadow);
		    	//boundCenterBottom(marker);
	    	}
    	 
    	}
    
    
}


