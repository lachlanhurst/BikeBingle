package com.lachlanhurst.client.userInterface;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Cookies;

/**
 * 
 * @author lachlan
 *
 */
public class MapMoveViewStacksUpdater implements MapMoveEndHandler 
{
	protected MapManager _mapManager;
	protected int _lastZoomLevel;
	
	public MapMoveViewStacksUpdater(MapManager mapManager)
	{
		_mapManager = mapManager;
		_lastZoomLevel = -1;
	}
	
	
	public void onMoveEnd(MapMoveEndEvent event) 
	{
		if (_mapManager.getMode() == MapManager.MODE_SETTING_VIEW)
		{
			int evtZoomLevel = event.getSender().getZoomLevel();
			if (_lastZoomLevel == -1)
			{
				_lastZoomLevel = evtZoomLevel;
			}
			else if (_lastZoomLevel != evtZoomLevel)
			{
				_mapManager.ensureServerUpdate();
				_lastZoomLevel = evtZoomLevel;
			}
			else
			{
				_lastZoomLevel = evtZoomLevel;
			}
			_mapManager.updateAll();
		}
		
	}
	

}
