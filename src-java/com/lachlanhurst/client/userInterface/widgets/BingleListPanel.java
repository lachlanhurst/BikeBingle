package com.lachlanhurst.client.userInterface.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.userInterface.ViewStacksPanel;
import com.lachlanhurst.client.userInterface.widgets.BinglePanel.BingleOperationRequestedListener;

/**
 * contains a list of BinglePanel's and allows the user to scroll through them
 * @author lachlan
 *
 */
public class BingleListPanel extends VerticalPanel 
{
	public static final int SPACING = 2;
	
	private int _height;
	private int _width;
	
	private Button _buttonMoveUp = null;
	private Button _buttonMoveDown = null;
	
	private ScrollPanel _scrollPanelList = null;
	private VerticalPanel _verticalPanelList = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	public BingleListPanel(int width, int height, BikeStackTypes bingleTypeManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_height = height;
		_width = width;
		initialise();
	}
	
	protected void initialise()
	{
		this.setSpacing(SPACING);
		
		//this.add(getButtonMoveUp());
		this.add(getScrollPanelList());
		//this.add(getButtonMoveDown());
		
	}
	
	protected Button getButtonMoveUp()
	{
		if (_buttonMoveUp == null)
		{
			_buttonMoveUp = new Button("Up");
			_buttonMoveUp.setStylePrimaryName("addStackPanel-Button");
			_buttonMoveUp.setWidth(_width + "px");
			
			_buttonMoveUp.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) {
					moveScrollPanel(-40);
					
				}
				
			});
		}
		return _buttonMoveUp;
	}
	
	protected Button getButtonMoveDown()
	{
		if (_buttonMoveDown == null)
		{
			_buttonMoveDown = new Button("Down");
			_buttonMoveDown.setStylePrimaryName("addStackPanel-Button");
			_buttonMoveDown.setWidth(_width + "px");
			_buttonMoveDown.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					moveScrollPanel(40);
					
				}
				
			});
		}
		return _buttonMoveDown;
	}
	
	protected void moveScrollPanel(int movement)
	{
		int currentPos = getScrollPanelList().getScrollPosition();
		int newPos = currentPos + movement;
		getScrollPanelList().setScrollPosition(newPos);
	}
	
	protected ScrollPanel getScrollPanelList()
	{
		if (_scrollPanelList == null)
		{
			_scrollPanelList = new ScrollPanel();
			_scrollPanelList.setWidth(_width + "px");
			_scrollPanelList.setHeight(_height - 40 + "px");
			_scrollPanelList.add(getVerticalPanelListing());
			//_scrollPanelList.addStyleName("noScroll");
			//_scrollPanelList.setStyleName("noScroll");
			//_scrollPanelList.setAlwaysShowScrollBars(false);
			
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
			BinglePanel bp = new BinglePanel(aBingle,_width-25+"px",true,_bingleTypeManager);
			bp.addBingleOperationRequestedListener(new BinglePanel.BingleOperationRequestedListener()
			{
				public void onBingleOperationRequested(int operationType,BikeStack bingle) {
					fireBingleOperationRequestedEvent(operationType, bingle);
				}
				
			});
			getVerticalPanelListing().add(bp);
		}
		
	}
	
	public void removeBinglePanel(int bingleId)
	{
		int toRemove = -1;
		int count = getVerticalPanelListing().getWidgetCount();
		for (int i = 0; i < count; i++)
		{
			BinglePanel bp = (BinglePanel)getVerticalPanelListing().getWidget(i);
			if (bp.getBingle().getId() == bingleId)
			{
				toRemove = i;
			}
		}
		if (toRemove != -1)
		{
			getVerticalPanelListing().remove(toRemove);	
		}
		
	}
	
	
	//EVENT CODE
	protected List _eventBingleOperationRequestedListeners = new ArrayList();
	
	protected void fireBingleOperationRequestedEvent(int operationType, BikeStack bingle)
    {
    	for(Iterator it = _eventBingleOperationRequestedListeners.iterator(); it.hasNext();)
        {
    		BingleOperationRequestedListener listener = (BingleOperationRequestedListener) it.next();
            listener.onBingleOperationRequested(operationType, bingle);
        }
    }
	
	public void addBingleOperationRequestedListener(BingleOperationRequestedListener listener)
    {
		_eventBingleOperationRequestedListeners.add(listener);
    }
}
