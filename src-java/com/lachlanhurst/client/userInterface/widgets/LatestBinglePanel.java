package com.lachlanhurst.client.userInterface.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.userInterface.MapManager.StackLocationPickedListener;

/**
 * class displays the given bike bingle given via the constructor.  Also
 * provides other features such as allowing the user to pan to the bingle.
 * @author lachlan
 *
 */
public class LatestBinglePanel extends DecoratorPanel 
{
	private static final String MESSAGE_NO_DESCRIPTION = "No description provided";
	
	protected BikeStack _bingle = null;
	private static final int SPACING = 2;
	
	protected HorizontalPanel _mainPanel = null;
	protected VerticalPanel _leftPanel = null;
	protected VerticalPanel _rightPanel = null;
	
	protected Label _descriptionLabel = null;
	protected Button _gotoButton = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	protected InjurySeverityPanel _injurySeverityPanel = null;
	
	protected String _width;
	
	public LatestBinglePanel(BikeStack aBingle, String width, BikeStackTypes bingleTypeManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_bingle = aBingle;
		_width = width;
		this.setWidth(width);
		
		initialise();
	}
	
	protected void initialise()
	{
		this.add(getPanelMain());
		
	}
	
	public BikeStack getBingle()
	{
		return _bingle;
	}
	
	private HorizontalPanel getPanelMain()
	{
		if (_mainPanel == null)
		{
			_mainPanel = new HorizontalPanel();
			_mainPanel.setSpacing(SPACING);
			_mainPanel.add(getPanelLeft());
			_mainPanel.add(getPanelRight());
			
			String ws = _width.substring(0,_width.length()-2);
			int w = Integer.parseInt(ws) - 8;
			GWT.log(Integer.toString(w),null);
			_mainPanel.setWidth(w + "px");
			_mainPanel.setStylePrimaryName("whiteBackground");
		}
		return _mainPanel;
	}
	
	private VerticalPanel getPanelLeft()
	{
		if (_leftPanel == null)
		{
			_leftPanel = new VerticalPanel();
			_leftPanel.setSpacing(SPACING);
			_leftPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
			_leftPanel.add(_bingleTypeManager.getImageById(_bingle.getType()));
			_leftPanel.add(getButtonGoto());
		}
		return _leftPanel;
	}
	
	private VerticalPanel getPanelRight()
	{
		if (_rightPanel == null)
		{
			_rightPanel = new VerticalPanel();
			_rightPanel.setSpacing(SPACING);
			Label heading = new Label(_bingleTypeManager.getTypeNameById(_bingle.getType()));
			heading.setStylePrimaryName("infoWindow-Heading");
			_rightPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
			_rightPanel.add(heading);
			_rightPanel.add(getLabelDescription());
		}
		return _rightPanel;
	}
	
	protected Label getLabelDescription()
	{
		if (_descriptionLabel == null)
		{
			if (_bingle.getDescription() == null)
			{
				_descriptionLabel = new Label(MESSAGE_NO_DESCRIPTION);
			}
			else if (_bingle.getDescription().trim().length() == 0)
			{
				_descriptionLabel = new Label(MESSAGE_NO_DESCRIPTION);
			}
			else
			{
				_descriptionLabel = new Label(_bingle.getDescription());
			}
			
			//_descriptionLabel.setWidth(width)
		}
		return _descriptionLabel;
	}
	
	protected Button getButtonGoto()
	{
		if (_gotoButton == null)
		{
			_gotoButton = new Button("Go to");
			_gotoButton.setTitle("Pan map to location of bingle");
			_gotoButton.setStylePrimaryName("addStackPanel-Button");
			_gotoButton.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					fireBingleGotoEvent(_bingle);
				}
				
			});
		}
		return _gotoButton;
	}
	

	//EVENT CODE
	private List _eventGotoBingleListeners    = new ArrayList();
	
	public interface BingleGotoListener extends java.util.EventListener
	{
	    void onGotoRequested(BikeStack bingle);
	}
	
	public void addBingleGotoListener(BingleGotoListener listener)
    {
		_eventGotoBingleListeners.add(listener);
    }

    public void removeBingleGotoListener(BingleGotoListener listener)
    {
    	_eventGotoBingleListeners.remove(listener);
    }

    protected void fireBingleGotoEvent(BikeStack bingle)
    {
    	for(Iterator it = _eventGotoBingleListeners.iterator(); it.hasNext();)
        {
    		BingleGotoListener listener = (BingleGotoListener) it.next();
            listener.onGotoRequested(bingle);
        }

    }

}
