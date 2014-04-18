package com.lachlanhurst.client.userInterface.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationException;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationManager;
import com.lachlanhurst.client.userInterface.MapManager;
import com.lachlanhurst.client.userInterface.ViewStacksPanel;
import com.lachlanhurst.client.userInterface.widgets.BinglePanel.BingleOperationRequestedListener;

/**
 * contains a list of BinglePanel's and allows the user to scroll through them
 * @author lachlan
 *
 */
public class LatestBingleListPanel extends VerticalPanel 
{
	public static final int SPACING = 2;
	
	private int _height;
	private int _width;
	
	private ScrollPanel _scrollPanelList = null;
	private VerticalPanel _verticalPanelList = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	protected MapManager _mapManager = null;
	
	public LatestBingleListPanel(int width, int height, BikeStackTypes bingleTypeManager, MapManager mapManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_height = height;
		_width = width;
		_mapManager = mapManager;
		initialise();
		
		//Window.alert("latest bingles inited");
		
		_mapManager.getBinglesCommunicator().addLatestBinglesReturnedListener(new BinglesCommunicationManager.LatestBinglesReturnedListener(){
			public void onBinglesReturned(List bingles) 
			{
				//Window.alert("bingles returned = " + bingles.size());
				setBingles(bingles);
			}
		});
		
	}
	
	protected void initialise()
	{
		this.setSpacing(SPACING);
		
		/*Button testing = new Button("getLatest");
		testing.addClickListener(new ClickListener(){

			public void onClick(Widget sender) {
				try {
					_mapManager.getBinglesCommunicator().requestLatestBingles();
				} catch (BinglesCommunicationException e) {
					// TODO Auto-generated catch block
					Window.alert("bingles error " + e.getMessage());
					GWT.log("error when getting latest bingles", e);
				}
				
			}
			
		});
		this.add(testing);*/
		this.add(getScrollPanelList());
	
	}
	
	protected ScrollPanel getScrollPanelList()
	{
		if (_scrollPanelList == null)
		{
			_scrollPanelList = new ScrollPanel();
			_scrollPanelList.setWidth(_width + "px");
			_scrollPanelList.setHeight(_height - 40 + "px");
			_scrollPanelList.add(getVerticalPanelListing());
		}
		return _scrollPanelList;
	}
	
	protected VerticalPanel getVerticalPanelListing()
	{
		if (_verticalPanelList == null)
		{
			_verticalPanelList = new VerticalPanel();
			_verticalPanelList.setSpacing(SPACING);
		}
		return _verticalPanelList;
	}
	
	/**
	 * sets the bingles that are to be displayed in this list panel
	 * @param bingles
	 */
	public void setBingles(List bingles)
	{
		getVerticalPanelListing().clear();
		
		Iterator binglesIter = bingles.iterator();
		while (binglesIter.hasNext())
		{
			BikeStack aBingle = (BikeStack)binglesIter.next();
			LatestBinglePanel bp = new LatestBinglePanel(aBingle,_width-25+"px",_bingleTypeManager);
			bp.addBingleGotoListener(new LatestBinglePanel.BingleGotoListener()
			{
				public void onGotoRequested(BikeStack bingle) {
					gotoBingle(bingle);
				}
				
			});
			getVerticalPanelListing().add(bp);
		}
		
	}
	
	private void gotoBingle(BikeStack bingle)
	{
		LatLng pt = bingle.getPosition();
		_mapManager.panTo(pt);
		_mapManager.showPointSelection(pt);
	}
}
