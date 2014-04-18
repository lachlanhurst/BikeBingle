package com.lachlanhurst.client.userInterface.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
 * provides other features such as allowing the user to pan to the bingle
 * and to remove the bingle (provided they entered it).
 * @author lachlan
 *
 */
public class BinglePanel extends DecoratorPanel 
{
	public static final int BINGLE_OPERATION_GOTO = 200;
	public static final int BINGLE_OPERATION_OPEN_RELATED_URL = 300;
	public static final int BINGLE_OPERATION_DELETE = 400;
	
	private static final String MESSAGE_NO_DESCRIPTION = "No description provided";
	
	protected BikeStack _bingle = null;
	protected boolean _isShowingRemove;
	private static final int SPACING = 2;
	
	protected VerticalPanel _mainPanel = null;
	protected HorizontalPanel _topPanel = null;
	protected VerticalPanel _topPanelLabels = null;
	
	protected Label _descriptionLabel = null;
	
	protected HorizontalPanel _operationsPanel = null;
	protected Button _gotoButton = null;
	protected Button _openRelatedUrlButton = null;
	protected Button _deleteButton = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	protected InjurySeverityPanel _injurySeverityPanel = null;
	
	protected String _width;
	
	public BinglePanel(BikeStack aBingle, String width, boolean showRemove, BikeStackTypes bingleTypeManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_bingle = aBingle;
		_isShowingRemove = showRemove;
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
	
	private VerticalPanel getPanelMain()
	{
		if (_mainPanel == null)
		{
			_mainPanel = new VerticalPanel();
			_mainPanel.setSpacing(SPACING);
			_mainPanel.add(getPanelTop());
			_mainPanel.add(getPanelInjurySeverity());
			_mainPanel.add(getLabelDescription());
			
			//TODO add panel with the 
			//- pan to bingle point
			//- open url
			//- delete
			_mainPanel.add(getPanelOperationButtons());
		}
		return _mainPanel;
	}
	
	private HorizontalPanel getPanelTop()
	{
		if (_topPanel == null)
		{
			_topPanel = new HorizontalPanel();
			_topPanel.setSpacing(SPACING);
			
			_topPanel.add(_bingleTypeManager.getImageById(_bingle.getType()));
			_topPanel.add(getPanelTopLabels());
		}
		return _topPanel;
	}
	
	private VerticalPanel getPanelTopLabels()
	{
		if (_topPanelLabels == null)
		{
			_topPanelLabels = new VerticalPanel();
			_topPanelLabels.setSpacing(SPACING);
			Label heading = new Label(_bingleTypeManager.getTypeNameById(_bingle.getType()));
			heading.setStylePrimaryName("infoWindow-Heading");
			_topPanelLabels.add(heading);
			Label occuredOnLable = new Label("Occured on: " + getDateAsString(_bingle.getOccuredDate()));
			_topPanelLabels.add(occuredOnLable);
			Label addedOnLabel = new Label("Added on: " + getDateAsString(_bingle.getEntryDate()));
			_topPanelLabels.add(addedOnLabel);
		}
		return _topPanelLabels;
	}
	
	protected String getDateAsString(Date d)
	{
		if (d == null)
		{
			return "n/a";
		}
		DateTimeFormat df = DateTimeFormat.getShortDateFormat();
		return df.format(d);
	}
	
	protected InjurySeverityPanel getPanelInjurySeverity()
	{
		if (_injurySeverityPanel == null)
		{
			String ws = _width.substring(0,_width.length()-2);
			int w = Integer.parseInt(ws);
			_injurySeverityPanel = new InjurySeverityPanel(_bingle.getInjurySeverity(),w-10);
		}
		return _injurySeverityPanel;
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
		}
		return _descriptionLabel;
	}
	
	protected HorizontalPanel getPanelOperationButtons()
	{
		if (_operationsPanel == null)
		{
			_operationsPanel = new HorizontalPanel();
			_operationsPanel.setSpacing(SPACING);
			
			_operationsPanel.add(getButtonGoto());
			if (isRelatedUrlAvaliable())
			{
				_operationsPanel.add(getButtonOpenUrl());
			}
			_operationsPanel.add(getButtonDelete());
		}
		return _operationsPanel;
	}

	protected Button getButtonGoto()
	{
		if (_gotoButton == null)
		{
			_gotoButton = new Button("Go to");
			_gotoButton.setTitle("Pan map to location of accident");
			_gotoButton.setStylePrimaryName("addStackPanel-Button");
			_gotoButton.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					fireBingleOperationRequestedEvent(BINGLE_OPERATION_GOTO, _bingle);
				}
				
			});
		}
		return _gotoButton;
	}
	
	protected Button getButtonOpenUrl()
	{
		if (_openRelatedUrlButton == null)
		{
			_openRelatedUrlButton = new Button("Open link");
			_openRelatedUrlButton.setTitle("Opens link to another website related to accident");
			_openRelatedUrlButton.setStylePrimaryName("addStackPanel-Button");
			_openRelatedUrlButton.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					fireBingleOperationRequestedEvent(BINGLE_OPERATION_OPEN_RELATED_URL, _bingle);
				}
				
			});
		}
		return _openRelatedUrlButton;
	}
	
	protected Button getButtonDelete()
	{
		if (_deleteButton == null)
		{
			_deleteButton = new Button("Delete");
			_deleteButton.setTitle("Permanently deletes this BikeBingle from the database");
			_deleteButton.setStylePrimaryName("addStackPanel-Button");
			_deleteButton.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					fireBingleOperationRequestedEvent(BINGLE_OPERATION_DELETE, _bingle);
				}
				
			});
		}
		return _deleteButton;
	}
	
	
	protected boolean isRelatedUrlAvaliable()
	{
		String link = _bingle.getLink(); 
		if (link == null)
		{
			return false;
		}
		link = link.trim();
		if (link.length() == 0)
		{
			return false;
		}
		else if (link.indexOf('.') == -1)
		{
			return false;
		}
		else if (link.indexOf('>') != -1)
		{
			return false;
		}
		else if (link.indexOf('<') != -1)
		{
			return false;
		}
		else
		{
			return true;
		}
			
	}

	//EVENT CODE
	private List _eventBingleOperationRequestedListeners    = new ArrayList();
	
	public interface BingleOperationRequestedListener extends java.util.EventListener
	{
	    void onBingleOperationRequested(int operationType, BikeStack bingle);
	}
	
	public void addBingleOperationRequestedListener(BingleOperationRequestedListener listener)
    {
		_eventBingleOperationRequestedListeners.add(listener);
    }

    public void removeBingleOperationRequestedListener(BingleOperationRequestedListener listener)
    {
    	_eventBingleOperationRequestedListeners.remove(listener);
    }

    protected void fireBingleOperationRequestedEvent(int operationType, BikeStack bingle)
    {
    	for(Iterator it = _eventBingleOperationRequestedListeners.iterator(); it.hasNext();)
        {
    		BingleOperationRequestedListener listener = (BingleOperationRequestedListener) it.next();
            listener.onBingleOperationRequested(operationType, bingle);
        }

    }

}
