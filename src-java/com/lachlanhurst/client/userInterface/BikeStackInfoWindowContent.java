package com.lachlanhurst.client.userInterface;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.userInterface.widgets.InjurySeverityPanel;

/**
 * content for the little popup windows that appear when you click on a stack
 * in the map window.
 * @author lachlan
 *
 */
public class BikeStackInfoWindowContent extends VerticalPanel 
{
	protected BikeStack _bikeStack = null;
	private static final int SPACING = 2;
	private BikeStackTypes _bingleTypeManager = null;
	
	public BikeStackInfoWindowContent(BikeStack bikeStack, BikeStackTypes bingleTypeManager)
	{
		_bikeStack = bikeStack;
		_bingleTypeManager = bingleTypeManager;
		initialise();
	}
	
	protected void initialise()
	{
		this.setSpacing(SPACING);
		this.setWidth("220px");
		//add the heading (stack type) and the icon
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		hp.setSpacing(SPACING);
		
		hp.add(_bingleTypeManager.getImageById(_bikeStack.getType()));
		Label heading = new Label(_bingleTypeManager.getTypeNameById(_bikeStack.getType()));
		heading.setStylePrimaryName("infoWindow-Heading");
		hp.add(heading);
		this.add(hp);
		
		if (_bikeStack.getType() != -2)
		{
			addAddedBy();
			addOccuredOn();
			addAddedOn();
			addInjurySeverity();
			addDescription();
			addRelatedUrl();
			addBingleLink();
		}
		else
		{
			GWT.log("added low res", null);
			addLowResNotification();
		}
		
	}
	
	protected void addInjurySeverity()
	{
		InjurySeverityPanel isp = new InjurySeverityPanel(_bikeStack.getInjurySeverity(),200);
		this.add(isp);
	}
	
	protected void addAddedBy()
	{
		if (_bikeStack.getUser() == null)
		{
			return;
		}
		if (_bikeStack.getUser().length() == 0)
		{
			return;
		}
		Label addedBy = new Label("Added by: " + _bikeStack.getUser());
		this.add(addedBy);
	}
	
	protected void addOccuredOn()
	{
		if (_bikeStack.getOccuredDate() != null)
		{
			Label occuredOnLable = new Label("Occured on: " + getDateAsString(_bikeStack.getOccuredDate()));
			this.add(occuredOnLable);	
		}
		
	}
	
	protected void addAddedOn()
	{
		if (_bikeStack.getEntryDate() != null)
		{
			Label addedOnLabel = new Label("Added on: " + getDateAsString(_bikeStack.getEntryDate()));
			this.add(addedOnLabel);	
		}
		
	}
	
	protected String getDateAsString(Date d)
	{
		DateTimeFormat df = DateTimeFormat.getShortDateFormat();
		return df.format(d);
	}
	
	protected void addDescription()
	{
		if (_bikeStack.getDescription() != null)
		{
			Label description = new Label(_bikeStack.getDescription());
			description.setWidth("200px");
			description.setStylePrimaryName("infoWindow-Description");
			this.add(description);	
		}
		
	}

	protected void addLowResNotification()
	{

		Label description = new Label("Zoom in to view individual bingles");
		description.setWidth("200px");
		description.setStylePrimaryName("infoWindow-Description");
		this.add(description);	

	}
	
	
	protected void addRelatedUrl()
	{
		String link = _bikeStack.getLink(); 
		if (link == null)
		{
			return;
		}
		link = link.trim();
		if (link.length() == 0)
		{
			
		}
		else if (link.indexOf('.') == -1)
		{
			
		}
		else
		{
			link = link.replaceAll("<", "_");
			link = link.replaceAll(">", "_");
			
			String linkString = "<a href=\"" +  link + "\">" + "Related link" + "</a>";
			HTML linkHtml = new HTML(linkString);
			this.add(linkHtml);
			/*Button b = new Button("Open related URL");
			b.setStylePrimaryName("addStackPanel-Button");
			b.setTitle("Opens a popup with the related URL");
			b.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					Window.open(_link,"BikeBingle related page",null);
				}
				
			});
			this.add(b);*/
		}
			
	}
	
	public static final String BIKEBINGLE_LINK_TOOLTIP = "Copy this link to reference this specific bike bingle";
	protected void addBingleLink()
	{
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		hp.setSpacing(2);
		Label bll = new Label("BikeBingle link");
		bll.setTitle(BIKEBINGLE_LINK_TOOLTIP);
		hp.add(bll);
		
		TextBox linkTb = new TextBox();
		linkTb.setText(_bikeStack.getQueryUrl());
		linkTb.setWidth("100px");
		linkTb.setTitle(BIKEBINGLE_LINK_TOOLTIP);
		linkTb.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender) 
			{
				TextBox tb = (TextBox)sender;
				tb.setSelectionRange(0, tb.getText().length());
			}
		});
		hp.add(linkTb);
		this.add(hp);
	}
	
}
