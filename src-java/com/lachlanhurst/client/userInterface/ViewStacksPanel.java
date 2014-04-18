package com.lachlanhurst.client.userInterface;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * user interface panel for viewing stacks in the database.
 * @author lachlan
 *
 */
public class ViewStacksPanel extends VerticalPanel 
{
	public static final String RADIO_BUTTON_GROUP_NAME_DISPLAY = "VisualisationDisplay";
	public static final String RADIO_BUTTON_GROUP_MARKER_DISPLAY_AS = "MarkerDisplayType";
	
	protected UiStateManager _uiStateManager;
	protected MapManager _mapManager;
	private int _width;

	protected DecoratorPanel _panelVisualisationType = null;
	
	protected DecoratorPanel _panelIntensityOptions = null;
	protected DecoratorPanel _panelMarkerOptions = null;
	
	public ViewStacksPanel(UiStateManager uiStateManager, MapManager mapManager, String width)
	{
		_uiStateManager = uiStateManager;
		_mapManager = mapManager;
		_width = Integer.parseInt(width.substring(0, width.length() - 2)) + 7;
		this.setWidth(width);
		
		initialise();
	}
	
	protected void initialise()
	{
		this.setSpacing(1);
		
		this.add(getPanelVisualisationType());
		this.add(getPanelMarkerOptions());
		this.add(getPanelIntensityOptions());
	}

	protected DecoratorPanel getPanelVisualisationType()
	{
		if (_panelVisualisationType == null)
		{
			_panelVisualisationType = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth(_width + "px");
			Label visHeading = new Label("Display type");
			visHeading.setStylePrimaryName("addStackPanel-SubHeading");
			vp.add(visHeading);
			
			RadioButton markerDisplay = new RadioButton(RADIO_BUTTON_GROUP_NAME_DISPLAY,"Markers");
			markerDisplay.setChecked(true);
			markerDisplay.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					RadioButton rb = (RadioButton)sender;
					if (rb.isChecked())
					{
						_mapManager.setDisplayMode(MapManager.DISPLAY_SETTING_ICONS);
						getPanelIntensityOptions().setVisible(false);
						getPanelMarkerOptions().setVisible(true);
					}
				}
				
			});
			
			vp.add(markerDisplay);
			RadioButton intensityDisplay = new RadioButton(RADIO_BUTTON_GROUP_NAME_DISPLAY,"Hot Spots");
			intensityDisplay.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					RadioButton rb = (RadioButton)sender;
					if (rb.isChecked())
					{
						_mapManager.setDisplayMode(MapManager.DISPLAY_SETTING_INTENSITY);
						getPanelIntensityOptions().setVisible(true);
						getPanelMarkerOptions().setVisible(false);
					}
				}
				
			});
			
			vp.add(intensityDisplay);
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			
			_panelVisualisationType.add(vp);
		}
		return _panelVisualisationType;
	}
	
	protected DecoratorPanel getPanelIntensityOptions()
	{
		if (_panelIntensityOptions == null)
		{
			_panelIntensityOptions = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(1);
			vp.setWidth(_width + "px");
			Label heading = new Label("Hot Spot display");
			heading.setStylePrimaryName("addStackPanel-SubHeading");
			vp.add(heading);
			vp.setStylePrimaryName("addStackPanel-InnerPanel");
			_panelIntensityOptions.add(vp);
			
			PushButton increaseDivisions = new PushButton(new Image("list-add.png"));
			increaseDivisions.setStylePrimaryName("buttonNoBorder");
			increaseDivisions.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					_mapManager.increaseIntesityDivisions();
					
				}
			});
			HorizontalPanel incHp = new HorizontalPanel();
			incHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			incHp.add(increaseDivisions);
			Label inLble =new Label("Refine"); 
			incHp.add(inLble);
			vp.add(incHp);
			
			PushButton decreaseDivisions = new PushButton(new Image("list-remove.png"));
			decreaseDivisions.setStylePrimaryName("buttonNoBorder");
			decreaseDivisions.addClickListener(new ClickListener(){
				public void onClick(Widget sender) {
					_mapManager.decreaseIntesityDivisions();
					
				}
			});
			HorizontalPanel decHp = new HorizontalPanel();
			decHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			decHp.add(decreaseDivisions);
			decHp.add(new Label("Coarsen"));
			vp.add(decHp);
			
			_panelIntensityOptions.setVisible(false);
		}
		return _panelIntensityOptions;
	}
		
	
	protected DecoratorPanel getPanelMarkerOptions()
	{
		if (_panelMarkerOptions == null)
		{
			_panelMarkerOptions = new DecoratorPanel();
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(1);
			vp.setWidth(_width + "px");
			Label visHeading = new Label("Marker options");
			visHeading.setStylePrimaryName("addStackPanel-SubHeading");
			vp.add(visHeading);
			
			RadioButton iconDisplay = new RadioButton(RADIO_BUTTON_GROUP_MARKER_DISPLAY_AS,"Icons");
			iconDisplay.setChecked(true);
			iconDisplay.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					RadioButton rb = (RadioButton)sender;
					if (rb.isChecked())
					{
						_mapManager.setDisplayStacksAs(MapManager.DISPLAY_STACKS_AS_ICONS);
					}
				}
			});
			vp.add(iconDisplay);
			
			RadioButton largeIconDisplay = new RadioButton(RADIO_BUTTON_GROUP_MARKER_DISPLAY_AS,"Large Icons");
			largeIconDisplay.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					RadioButton rb = (RadioButton)sender;
					if (rb.isChecked())
					{
						_mapManager.setDisplayStacksAs(MapManager.DISPLAY_STACKS_AS_LARGE_ICONS);
					}
				}
			});
			vp.add(largeIconDisplay);
			
			RadioButton markerDisplay = new RadioButton(RADIO_BUTTON_GROUP_MARKER_DISPLAY_AS,"Simple markers");
			markerDisplay.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					RadioButton rb = (RadioButton)sender;
					if (rb.isChecked())
					{
						_mapManager.setDisplayStacksAs(MapManager.DISPLAY_STACKS_AS_MARKERS);
					}
				}
			});
			vp.add(markerDisplay);
			
			_panelMarkerOptions.add(vp);
		}
		return _panelMarkerOptions;
	}
	
}
