package com.lachlanhurst.client.userInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapMouseMoveHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationException;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationManager;
import com.lachlanhurst.client.util.QuadTreeManager;
import com.lachlanhurst.client.util.RandomBikeStackGenerator;

/**
 * manages the map widget according to what the user selects in the 
 * interface and where the map is centered/zoomed on.
 * @author lachlan
 *
 */
public class MapManager 
{
	public static final int MODE_SETTING_VIEW = 24;
	public static final int MODE_SETTING_ADD = 25;
	public static final int MODE_SETTING_HELP = 26;
	public static final int MODE_SETTING_MY_BINGLES = 27;
	
	public static final int DISPLAY_SETTING_ICONS = 12;
	public static final int DISPLAY_SETTING_INTENSITY = 13;
	
	public static final int DISPLAY_STACKS_AS_ICONS = 45;
	public static final int DISPLAY_STACKS_AS_LARGE_ICONS = 46;
	public static final int DISPLAY_STACKS_AS_MARKERS = 47;
	
	public static final double MAP_BOUNDS_EXTENSION_FACTOR = 0.15;
	
	protected List _stacks;
	protected MapWidget _map;
	protected int _displaySetting = DISPLAY_SETTING_ICONS;
	protected int _modeSetting = MODE_SETTING_VIEW;
	
	protected LatLngBounds _bounds;
	protected LatLngBounds _boundsEnlarged;
	
	protected LatLng _pickedPoint;
	
	protected int _displayStacksAs = DISPLAY_STACKS_AS_ICONS;
	
	protected QuadTreeManager _quadTreeManager = null;
	protected BinglesCommunicationManager _binglesComm = null;
	
	protected MapClickHandlerPickLocation _locationPicker = null;
	protected Icon _pointSelectionIcon = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	protected int _popupStackId = -1;
	
	public MapManager(MapWidget map, BikeStackTypes bingleTypeManager, int initialPopupId)
	{
		_popupStackId = initialPopupId;
		_stacks = new ArrayList();
		_bingleTypeManager = bingleTypeManager;
		_map = map;
		_bounds = _map.getBounds();
		_boundsEnlarged = LatLngBounds.newInstance(); //getBoundsEnalarged(_bounds);
		
		_binglesComm = new BinglesCommunicationManager();
		_binglesComm.addBinglesReturnedListener(new BinglesCommunicationManager.BinglesReturnedListener(){

			public void onBinglesReturned(List bingles) 
			{
				_stacks.clear();
				_stacks.addAll(bingles);
				drawStacksOntoMap();
			}
			
		});
		
		_pointSelectionIcon = Icon.newInstance("selectionRing.png");
		Point anc = Point.newInstance(20, 20);
		_pointSelectionIcon.setIconAnchor(anc);
		
		_locationPicker = new MapClickHandlerPickLocation(this);
	}
	
	public MapManager(MapWidget map, LatLngBounds bounds)
	{
		_stacks = new ArrayList();
		_map = map;
		_bounds = bounds;
		
		_locationPicker = new MapClickHandlerPickLocation(this);
	}
	
	/**
	 * returns the communication manager so that requests can be sent to the
	 * server for various things.
	 * @return
	 */
	public BinglesCommunicationManager getBinglesCommunicator()
	{
		return _binglesComm;
	}
	
	public LatLngBounds getBoundsEnalarged(LatLngBounds bounds)
	{
		LatLng ne = bounds.getNorthEast();
		LatLng sw = bounds.getSouthWest();
		
		LatLng size = bounds.toSpan();
		double height = size.getLatitude();
		double width = size.getLongitude();
		
		//GWT.log("enlarging bounds w= " + Double.toString(width) + " h=" + Double.toString(height), null);
		
		double heightToAdd = height * MAP_BOUNDS_EXTENSION_FACTOR;
		double widthToAdd = width * MAP_BOUNDS_EXTENSION_FACTOR;
		
		double newNoth = ne.getLatitude()+heightToAdd;
		double newSouth = sw.getLatitude()-heightToAdd;
		
		if (newNoth > 90)
			newNoth = 90;
		if (newSouth < -90)
			newSouth = -90;
		
		double newEast = ne.getLongitude() +widthToAdd;
		double newWest = sw.getLongitude() -widthToAdd;
		
		if (newEast > 180)
			newEast = 180;
		if (newWest < -180)
			newWest = -180;
		
		/*if (bounds.isFullLongitude())
		{
			GWT.log("full lng", null);
		}
		
		LatLng enalrgedNe = LatLng.newInstance(newNoth, newEast);
		LatLng enalrgedSw = LatLng.newInstance(newSouth, newWest);
		
		GWT.log("west="+Double.toString(newWest)+"east="+Double.toString(newEast), null);
		
		LatLngBounds enlargedBounds = LatLngBounds.newInstance(enalrgedSw,enalrgedNe);
		return enlargedBounds;*/
		
		bounds.extend(LatLng.newInstance(newNoth, newEast));
		bounds.extend(LatLng.newInstance(newSouth, newWest));
		return bounds;
		
	}
	
	
	public void setMap(MapWidget map)
	{
		_map = map;
	}
	
	/**
	 * sets the current display mode of the map, this changes how the overlays
	 * are drawn.  Will also cause the map to update its display.
	 * @param displaySetting
	 */
	public void setDisplayMode(int displaySetting)
	{
		if (_displaySetting != displaySetting)
		{
			_displaySetting = displaySetting;
			updateDisplayOnly();
		}
		
	}
	
	public void ensureServerUpdate()
	{
		_boundsEnlarged = LatLngBounds.newInstance();
	}
	
	/**
	 * if the id of any of the bingles matches this id, the the info window for this bingle
	 * will be displayed.
	 * @param id
	 */
	public void setPopupBingleId(int id)
	{
		_popupStackId = id;
	}
	
	public void setMode(int mode)
	{
		if (mode != _modeSetting)
		{
			_modeSetting = mode;
			if (_modeSetting == MapManager.MODE_SETTING_VIEW)
			{
				_boundsEnlarged = LatLngBounds.newInstance();
				_map.removeMapClickHandler(_locationPicker);
				updateAll();
			}
			else if (_modeSetting == MapManager.MODE_SETTING_ADD)
			{
				_map.clearOverlays();
				drawPickedPointOntoMap();
				_map.addMapClickHandler(_locationPicker);
			}	
			else if (_modeSetting == MapManager.MODE_SETTING_MY_BINGLES)
			{
				//TODO 
				_boundsEnlarged = LatLngBounds.newInstance();
				_map.removeMapClickHandler(_locationPicker);
				updateAll();
			}
			else
			{
				//TODO - change this later
				_map.removeMapClickHandler(_locationPicker);
				_map.clearOverlays();
			}
			
			GWT.log("map manager mode changed to " + Integer.toString(mode), null);
		}
		
	}
	
	/**
	 * pans to the given location, then updates the map display
	 * @param center
	 */
	public void panTo(LatLng center)
	{
		_map.panTo(center);
		updateAll();
	}
	
	public void showPointSelection(LatLng pt)
	{
		MarkerOptions mo = MarkerOptions.newInstance(_pointSelectionIcon);
		final Marker m = new Marker(pt,mo);
		
		mo.setClickable(true);
		mo.setDraggable(false);
		
		_map.addOverlay(m);
		
		Timer t = new Timer(){
			public void run() {
				_map.removeOverlay(m);
			}
		};
		t.schedule(4000);
	}
	
	/**
	 * sets the location the user has picked on the map, also fires an event
	 * off to any added StackLocationPickedListeners. 
	 * @param pickedLocation
	 */
	void setPickedLocation(LatLng pickedLocation)
	{
		_pickedPoint = pickedLocation;
		if (_pickedPoint == null)
			_map.clearOverlays();
		fireStackLocationPickedEvent(pickedLocation);
	}
	
	/**
	 * gets the location picked by the user as their stack position.  Returns
	 * null if no point is picked.
	 * @return
	 */
	public LatLng getPickedLocation()
	{
		return _pickedPoint;
	}
	
	/**
	 * checks if the user has already selected a point
	 * @return
	 */
	public boolean isPointPicked()
	{
		if (_pickedPoint == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * gets the mode that the map manager has been set to. ie: MODE_SETTING_VIEW,
	 * MODE_SETTING_ADD for viewing and adding data respectively.
	 * @return
	 */
	public int getMode()
	{
		return _modeSetting;
	}
	
	private boolean isLowResTriggered()
	{
		if (_map.getZoomLevel() <= 8)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean updateListWithBounds()
	{
		//GWT.log("enlarged = " + _boundsEnlarged.toString(), null);
		//GWT.log("view = " + _bounds.toString(), null);
		if (_boundsEnlarged.containsBounds(_bounds))
		{
			//then no need to update
			//GWT.log("update skippied", null);
			return false;
		}
		else
		{
			//_stacks.clear();
			LatLngBounds enlargedBounds = this.getBoundsEnalarged(_bounds);
			//List stacksInBounds = getListOfStacksInBounds(enlargedBounds);
			
			_binglesComm.setLowResBingles(isLowResTriggered());

			_binglesComm.setBounds(enlargedBounds);
			try {
				if (_modeSetting == MODE_SETTING_VIEW)
				{
					_binglesComm.setUserOnlyBingles(false);
				}
				else if (_modeSetting == MODE_SETTING_MY_BINGLES)
				{
					_binglesComm.setUserOnlyBingles(true);
				}
				
				GWT.log(_binglesComm.getRequestUrl(), null);
				_binglesComm.requestBingles();
			} catch (BinglesCommunicationException e) {
				GWT.log("error getting bingle request url", e);
			}
			
			_boundsEnlarged = enlargedBounds;
			//_stacks.addAll(stacksInBounds);
			GWT.log("update done", null);
			return true;
		}
		
		
		
	}
	
	/**
	 * does a big update, gets list of stacks from server and draws them
	 * all onto the map.  Update is based on the current state, ie what
	 * display mode is set and what the current bounds are.
	 */
	public void updateAll()
	{
		_bounds = _map.getBounds();
		updateListWithBounds();
		
		/*boolean contentsChanged = updateListWithBounds();
		
		if (contentsChanged)
		{
			//then no need to update
			//GWT.log("update skippied", null);
			drawStacksOntoMap();
		}
		
		GWT.log("updated map display", null);*/
	}
	
	/**
	 * only updates the overlays present on the map, provided they are currently
	 * being drawn
	 */
	public void updateDisplayOnly()
	{
		drawStacksOntoMap();
	}
	
	protected List getListOfStacksInBounds(LatLngBounds bounds)
	{
		List res = new ArrayList();
		//at the moment this just generates a random set of bike stacks
		
		for (int i = 0; i < 100; i++)
		{
			res.add(RandomBikeStackGenerator.getRandomStack(i, bounds,false));
		}
		for (int i = 0; i < 40; i++)
		{
			res.add(RandomBikeStackGenerator.getRandomStack(i, bounds,true));
		}
		res.addAll(RandomBikeStackGenerator.getLineOfStacks(50, bounds));
		res.addAll(RandomBikeStackGenerator.getLineOfStacks(80, bounds));
		return res;
	}
	
	protected void drawPickedPointOntoMap()
	{
		if (_pickedPoint != null)
		{
			MarkerOptions options = MarkerOptions.newInstance();
			//options.setClickable(true);
			options.setDraggable(false);
			
			Marker aStackMarker = new Marker(_pickedPoint,options);
			_map.addOverlay(aStackMarker);
			_map.panTo(_pickedPoint);
		}
	}
	
	
	protected void drawStacksOntoMap()
	{
		if (_modeSetting == MODE_SETTING_VIEW)
		{
			_map.clearOverlays();
			if (_displaySetting == DISPLAY_SETTING_ICONS)
			{
				drawStacksOntoMapAsIcons();
			}
			else if (_displaySetting == DISPLAY_SETTING_INTENSITY)
			{
				drawStacksOntoMapAsIntensity();
			}
			else
			{
				throw new RuntimeException("other display setting not supported");
			}
		}
		else if (_modeSetting == MODE_SETTING_MY_BINGLES)
		{
			_map.clearOverlays();
			drawStacksOntoMapAsIcons();
			/*if (_displaySetting == DISPLAY_SETTING_ICONS)
			{
				drawStacksOntoMapAsIcons();
			}
			else
			{
				throw new RuntimeException("other display setting not supported");
			}*/
		}
		
		
	}
	
	public void setDisplayStacksAs(int displayStacksAs)
	{
		if (_displayStacksAs == displayStacksAs)
		{
			//dont redraw as its already set
			return;
		}
		_displayStacksAs = displayStacksAs;
		drawStacksOntoMap();
	}
	
	/**
	 * draws the current list of stack onto the map currently displayed.
	 */
	protected void drawStacksOntoMapAsIcons()
	{
		Iterator stacks = _stacks.iterator();
		while (stacks.hasNext())
		{
			final BikeStack aStack = (BikeStack)stacks.next();
			MarkerOptions options;
			if (_displayStacksAs == DISPLAY_STACKS_AS_ICONS)
			{
				options = MarkerOptions.newInstance(_bingleTypeManager.getIconById(aStack.getType(),true));
			}
			else if (_displayStacksAs == DISPLAY_STACKS_AS_LARGE_ICONS)
			{
				options = MarkerOptions.newInstance(_bingleTypeManager.getIconById(aStack.getType(),false));
			}
			else
			{
				options = MarkerOptions.newInstance();	
			}
			
			options.setClickable(true);
			options.setDraggable(false);
			
			final Marker aStackMarker = new Marker(aStack.getPosition(),options);
			MarkerClickHandler myMarkerClickHandler =  new MarkerClickHandler() {
				public void onClick(MarkerClickEvent event) 
				{
					InfoWindow info = _map.getInfoWindow();
					BikeStackInfoWindowContent cont = new BikeStackInfoWindowContent(aStack,_bingleTypeManager);
					InfoWindowContent content = new InfoWindowContent(cont);
					info.open(aStackMarker.getLatLng(),content);
				}
			};
			aStackMarker.addMarkerClickHandler(myMarkerClickHandler);
			
			_map.addOverlay(aStackMarker);
			
			if (aStack.getId() == _popupStackId && 
				_modeSetting == MODE_SETTING_VIEW && 
				_displaySetting == DISPLAY_SETTING_ICONS)
			{
				myMarkerClickHandler.onClick(null);
				panTo(aStack.getPosition());
				GWT.log("showing", null);
				_popupStackId = -1;
			}
		}

	}
	
	
	protected void drawStacksOntoMapAsIntensity()
	{
		if (_quadTreeManager == null)
		{
			_quadTreeManager = new QuadTreeManager(_boundsEnlarged);	
		}
		else
		{
			_quadTreeManager = new QuadTreeManager(_boundsEnlarged,_quadTreeManager.getMaxDepth());
		}
		
		GWT.log("drawn qt", null);
		Iterator stacks = _stacks.iterator();
		while (stacks.hasNext())
		{
			BikeStack aStack = (BikeStack)stacks.next();
			_quadTreeManager.addBikeStack(aStack);
		}
		_quadTreeManager.drawOnMap(_map);
	}
	
	/**
	 * increases the number of divisions displayed in the intensity overlay.
	 * will also update the display.
	 */
	public void increaseIntesityDivisions()
	{
		_quadTreeManager.increaseDepth();
		_map.clearOverlays();
		_quadTreeManager.drawOnMap(_map);
	}
	
	/**
	 * decreases the number of divisions displayed in the intensity overlay.
	 * will also update the display.
	 */
	public void decreaseIntesityDivisions()
	{
		_quadTreeManager.decreaseDepth();
		_map.clearOverlays();
		_quadTreeManager.drawOnMap(_map);
	}
	
	//EVENT CODE
	private List _eventListenersStackLocation    = new ArrayList();
	
	public interface StackLocationPickedListener extends java.util.EventListener
	{
	    void onStackLocationPicked(LatLng pickedLocation);
	}
	
	public void addStackLocationPickedListener(StackLocationPickedListener listener)
    {
		_eventListenersStackLocation.add(listener);
    }

    public void removeStackLocationPickedListener(StackLocationPickedListener listener)
    {
    	_eventListenersStackLocation.remove(listener);
    }

    protected void fireStackLocationPickedEvent(LatLng point)
    {
    	for(Iterator it = _eventListenersStackLocation.iterator(); it.hasNext();)
        {
    		StackLocationPickedListener listener = (StackLocationPickedListener) it.next();
            listener.onStackLocationPicked(point);
        }

    }
	
}
