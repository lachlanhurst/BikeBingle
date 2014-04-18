package com.lachlanhurst.client.userInterface;

import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;

/**
 * sets the map managers mode according to which tab was selected
 * @author lachlan
 *
 */
public class TabListenerCanViewTab implements TabListener 
{
	protected UiStateManager _uiStateManager;
	
	public TabListenerCanViewTab(UiStateManager uiStateManager)
	{
		_uiStateManager = uiStateManager;
	}
	
	
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) 
	{
		if (tabIndex == TabIndexes.VIEW)
		{
			//if (_uiStateManager.)
			return true;
		}
		else
		{
			//if true isn't returned the tab cannot be selected
			return true;
		}
		
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) 
	{
		//dont do anything here, yet

	}

}
