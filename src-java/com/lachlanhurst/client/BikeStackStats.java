package com.lachlanhurst.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.lachlanhurst.client.userInterface.MainPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BikeStackStats implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		//make a new main panel, the entry point to the app
		MainPanel mp = new MainPanel();

		// Add the bike stack stats to the RootPanel
		RootPanel.get().add(mp);


	}
}
