package com.lachlanhurst.client.userInterface.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class InjurySeverityPanel extends HorizontalPanel 
{
	protected int _injurySeverity = 0;
	protected int _width = 200;
	
	public InjurySeverityPanel(int injurySeverity, int width)
	{
		_injurySeverity = injurySeverity;
		if (_injurySeverity < 0)
			_injurySeverity = 0;
		else if (_injurySeverity > 10)
			_injurySeverity = 10;
		
		_width = width;
		this.setWidth(Integer.toString(width) + "px");
		this.setSpacing(2);
		this.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		initialise();
	}
	
	protected void initialise()
	{
		Label l = new Label("Injury");
		l.setWidth("50px");
		this.add(l);
		this.add(getInjurySeverityPanel());
	}
	
	private int getAvailableProgressWidth()
	{
		return _width - 60;
	}
	
	protected HorizontalPanel getInjurySeverityPanel()
	{
		int outerW = getAvailableProgressWidth();
		
		double fract = ((double)_injurySeverity)/((double)10);
		int innerW = (int)(fract * outerW);
		
		HorizontalPanel outer = new HorizontalPanel();
		outer.addStyleName("injurySeverity-outer");
		outer.setWidth(Integer.toString(outerW) + "px");
		outer.setHeight("10px");
		outer.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		
		SimplePanel inner = new SimplePanel();
		inner.addStyleName("injurySeverity-inner");
		//inner.setStylePrimaryName("injurySeverity-inner");
		inner.setWidth(Integer.toString(innerW) + "px");
		inner.setHeight("10px");
		
		outer.add(inner);
		
		return outer;
	}
	
}
