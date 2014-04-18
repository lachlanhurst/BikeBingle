package com.lachlanhurst.client.userInterface;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;

public class MapClickHandlerPickLocation implements MapClickHandler 
{
	protected MapManager _mapManager = null;
	
	
	public MapClickHandlerPickLocation(MapManager mapManager)
	{
		_mapManager = mapManager;
	}
	
	public void onClick(MapClickEvent e) 
	{
		MapWidget sender = e.getSender();
        Overlay overlay = e.getOverlay();
        LatLng point = e.getLatLng();

        sender.clearOverlays();
        
        MarkerOptions options = MarkerOptions.newInstance();
        options.setDraggable(false);
        Marker m = new Marker(point,options);
        sender.addOverlay(m);
        _mapManager.setPickedLocation(point);
        /*if (overlay != null && overlay instanceof Marker) 
        {
          sender.removeOverlay(overlay);
        } else {
          sender.addOverlay(new Marker(point));
        }*/

	}

}
