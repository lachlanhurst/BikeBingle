package com.lachlanhurst.client.userInterface;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * sets the map managers mode according to which tab was selected
 * @author lachlan
 *
 */
public class TabListenerMapManagerModeSetter implements TabListener 
{
	protected MapManager _mapManager;
	protected SimplePanel _tabPanelBackGround;
	
	public TabListenerMapManagerModeSetter(MapManager mapManager, SimplePanel tabPanelBackground)
	{
		_mapManager = mapManager;
		_tabPanelBackGround = tabPanelBackground;
	}
	
	
	public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) 
	{
		//if true isn't returned the tab cannot be selected
		return true;
	}

	public void onTabSelected(SourcesTabEvents sender, int tabIndex) 
	{
		if (tabIndex == TabIndexes.VIEW)
		{
			_tabPanelBackGround.addStyleName("mainBackground");
			_mapManager.setMode(MapManager.MODE_SETTING_VIEW);
		}
		else if (tabIndex == TabIndexes.ADD)
		{
			_tabPanelBackGround.removeStyleName("mainBackground");
			_mapManager.setMode(MapManager.MODE_SETTING_ADD);
		}
		else if (tabIndex == TabIndexes.HELP)
		{
			_tabPanelBackGround.addStyleName("mainBackground");
			_mapManager.setMode(MapManager.MODE_SETTING_HELP);
		}
		else if (tabIndex == TabIndexes.MY_BINGLES)
		{
			_tabPanelBackGround.removeStyleName("mainBackground");
			_mapManager.setMode(MapManager.MODE_SETTING_MY_BINGLES);
		}
		else
		{
			throw new RuntimeException("didn't know about this tab");
		}

	}

}
