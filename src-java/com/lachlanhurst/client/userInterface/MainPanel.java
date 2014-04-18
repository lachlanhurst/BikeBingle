package com.lachlanhurst.client.userInterface;

import java.util.Calendar;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationException;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationManager;
import com.lachlanhurst.client.userInterface.AddStackPanel.StackAddedListener;
import com.lachlanhurst.client.userInterface.widgets.LatestBingleListPanel;
import com.lachlanhurst.client.util.DialogGenerator;

/**
 * main panel for the interface
 * @author lachlan
 *
 */
public class MainPanel extends VerticalPanel 
{
	public static final int SPACING = 2;
	
	public static final String WIDTH_TAB_PANEL = "260px";
	
	protected Image _mainTitle;
	
	protected SimplePanel _panelMainTitle;
	protected HorizontalPanel _panelMainWork;
	protected DecoratedTabPanel _tabPanelMainOptions;
	protected SimplePanel _tabPanelBackground;
	protected DecoratorPanel _panelMap;
	
	protected MapWidget _map;
	protected MapManager _mapManager;
	protected UiStateManager _uiStateManager;
	
	protected AddStackPanel _panelAddStack = null;
	
	protected DecoratedTabPanel _tabPanelViewOptions = null;
	protected VerticalPanel _panelLatestBingles = null;
	protected ViewStacksPanel _panelViewStacks = null;
	protected MyBinglesPanel _panelMyBingles = null;
	protected HelpPanel _panelHelp = null;
	
	protected BikeStackTypes _bingleTypeManager = null;
	
	public MainPanel()
	{
		_mainTitle = null;
		_panelMainTitle = null;
		_tabPanelMainOptions = null;
		_panelMap = null;
		_panelMainWork = null;
		_map = null;
		_uiStateManager = new UiStateManager();
		
		_bingleTypeManager = new BikeStackTypes();
		
		initialise();
		
		//_mapManager.updateAll();
		_mapManager.getBinglesCommunicator().addServerContactFailedListener(new BinglesCommunicationManager.ServerContactFailedListener(){
			public void onServerContactFailed(String message) {
				final DialogBox dialogBox = DialogGenerator.createErrorDialogBox(message);
			    dialogBox.setAnimationEnabled(true);
		        dialogBox.center();
		        dialogBox.show();
				
			}
			
		});
		
		/*if (TemporaryWarningDialogBox.isToBeShown())
		{
			final TemporaryWarningDialogBox db = new TemporaryWarningDialogBox();
			this.add(db);
			db.setAnimationEnabled(true);
	        db.center();
	        db.show();
		}*/
		
	}
	
	protected void initialise()
	{
		this.setWidth("100%");
		this.setHeight("100%");
		this.setSpacing(SPACING);
	    
	    this.add(getPanelMainTitle());
	    this.add(getMainWorkPanel());
	    
	    this.ensureDebugId("cwVerticalPanel");

	    try {
			_uiStateManager.requestUserDetails();
		} catch (UiStateManagerException e) {
			handleException(e);
		}
		try {
			//Window.alert("get latest req");
			_mapManager.getBinglesCommunicator().requestLatestBingles();
		} catch (BinglesCommunicationException e) {
			// TODO Auto-generated catch block
			Window.alert("bingles error " + e.getMessage());
			GWT.log("error when getting latest bingles", e);
		}
	}
	
	private void handleException(Exception ex)
	{
		final DialogBox dialogBox = DialogGenerator.createExceptionDialogBox(ex);
	    dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected SimplePanel getPanelMainTitle()
	{
		if (_panelMainTitle == null)
		{
			_panelMainTitle = new DecoratorPanel();
			
			//_panelMainTitle.setWidth("80%");
			HorizontalPanel hp = new HorizontalPanel();
			hp.setSpacing(2);
			hp.add(getMainTitle());
			hp.setHeight("100px");
			hp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			int w = RootPanel.get().getOffsetWidth() -35;
			hp.setWidth(Integer.toString(w) + "px");
			
			//RootPanel rp = RootPanel.get("googleAdsenseBlock1");
			//RootPanel.detachOnWindowClose(rp);
			//RootPanel.detachNow(rp);
			
			//Element e = Document.get().getElementById("googleAdsenseBlock1");
			//HTML rp = HTML.wrap(e);
			
			//Element e = Document.get().getElementById("googleAdsenseBlock1");
			//String addHtml = e.getInnerHTML();
			//GWT.log(addHtml, null);
			//Window.alert(Adsense.AD_1);
			//e.setInnerHTML("");
			
			//HTML rp = new HTML(addHtml);
			//HTML rp = new HTML("hello");
			//hp.add(rp);
			
			Frame f = new Frame("adsenseCode.html");
			f.setWidth("280px");
			f.setHeight("90px");
			//f.getElement().setAttribute("FRAMEBORDER", "0");
			//f.getElement().setPropertyInt("FRAMEBORDER", 0);
			f.getElement().setAttribute("frameBorder","0");
			f.getElement().setAttribute("scrolling","no");
			f.getElement().setAttribute("allowtransparency","true");
			f.setStylePrimaryName("borderlessFrame");
			GWT.log(f.getElement().toString(),null);
			hp.add(f);
			
			_panelMainTitle.addStyleName("titleBackground");
			_panelMainTitle.add(hp);
		}
		return _panelMainTitle;
	}
	
	protected Image getMainTitle()
	{
		if (_mainTitle == null)
		{
			_mainTitle = new Image("MainTitle.png");
			_mainTitle.ensureDebugId("mainTitleImage");
			_mainTitle.setTitle("Bike Stack Stats");
			_mainTitle.setHeight("96px");
			_mainTitle.setWidth("491");
			
		}
		return _mainTitle;
	}
	
	protected HorizontalPanel getMainWorkPanel()
	{
		if (_panelMainWork == null)
		{
			_panelMainWork = new HorizontalPanel();
			_panelMainWork.setSpacing(SPACING);
			_panelMainWork.add(getPanelMap());
			//_panelMainWork.add(getTabPanelMainOptions());
			_panelMainWork.add(getTabPanelBackGround());
		}
		return _panelMainWork;
	}
	
	protected SimplePanel getTabPanelBackGround()
	{
		if (_tabPanelBackground == null)
		{
			_tabPanelBackground = new SimplePanel();
			//int h = RootPanel.get().getOffsetHeight() - _mainTitle.getHeight() - 15;
			int h = getBottomPanelHeight();
			//Window.alert(Integer.toString(h));
			_tabPanelBackground.setSize("260px", Integer.toString(h) + "px");
			//_tabPanelBackground.setPixelSize(260, h);
			_tabPanelBackground.addStyleName("mainBackground");
			_tabPanelBackground.add(getTabPanelMainOptions());
		}
		return _tabPanelBackground;
	}
	
	protected DecoratedTabPanel getTabPanelMainOptions()
	{
		if (_tabPanelMainOptions == null)
		{
			_tabPanelMainOptions = new DecoratedTabPanel();
			int h = getBottomPanelHeight(); //RootPanel.get().getOffsetHeight() - _mainTitle.getHeight() - 15;
		    _tabPanelMainOptions.setSize("260px", Integer.toString(h) + "px");
			_tabPanelMainOptions.setAnimationEnabled(false);
			_tabPanelMainOptions.ensureDebugId("TabPanelMainOptions");
			
			_tabPanelMainOptions.addTabListener(new TabListenerMapManagerModeSetter(_mapManager,getTabPanelBackGround()));
			
			_tabPanelMainOptions.add(getTabPanelViewOptions(), "View",true);
			_tabPanelMainOptions.add(getPanelAddStack(), "Add");
			_tabPanelMainOptions.add(getPanelMyBingles(),"MyBingles");
			_tabPanelMainOptions.add(getPanelHelp(), "Help");
			
			String param = Window.Location.getParameter("tab");
			if (param != null)
			{
				GWT.log("param tab=" + param, null);
				if (param.compareToIgnoreCase("add") == 0)
				{
					_mapManager.setMode(MapManager.MODE_SETTING_ADD);
					_tabPanelMainOptions.selectTab(TabIndexes.ADD);
				}
				else if (param.compareToIgnoreCase("view") == 0)
				{
					_mapManager.setMode(MapManager.MODE_SETTING_VIEW);
					_tabPanelMainOptions.selectTab(TabIndexes.VIEW);
				}
				else if (param.compareToIgnoreCase("mybingles") == 0)
				{
					_mapManager.setMode(MapManager.MODE_SETTING_MY_BINGLES);
					_tabPanelMainOptions.selectTab(TabIndexes.MY_BINGLES);
				}
				else if (param.compareToIgnoreCase("help") == 0)
				{
					_mapManager.setMode(MapManager.MODE_SETTING_HELP);
					_tabPanelMainOptions.selectTab(TabIndexes.HELP);
				}
				else
				{
					_mapManager.setMode(MapManager.MODE_SETTING_VIEW);
					_tabPanelMainOptions.selectTab(TabIndexes.VIEW);	
				}
			}
			else
			{
				_tabPanelMainOptions.selectTab(0);
			}
			
		}
		return _tabPanelMainOptions;
	}
	
	protected DecoratedTabPanel getTabPanelViewOptions()
	{
		if (_tabPanelViewOptions == null)
		{
			_tabPanelViewOptions = new DecoratedTabPanel();
			int h = getBottomPanelHeight() - 15;
			_tabPanelViewOptions.setSize("260px", Integer.toString(h) + "px");
			_tabPanelViewOptions.setAnimationEnabled(false);
			
			_tabPanelViewOptions.add(getLatestBinglesPanel(), "Latest",true);
			_tabPanelViewOptions.add(getPanelViewStacks(), "View options",true);
			_tabPanelViewOptions.selectTab(0);
		}
		return _tabPanelViewOptions;
	}
			
	protected VerticalPanel getLatestBinglesPanel()
	{
		if (_panelLatestBingles == null)
		{
			_panelLatestBingles = new VerticalPanel();
			_panelLatestBingles.setSpacing(2);
			Label heading = new Label("Latest bingle feed");
			heading.setStylePrimaryName("infoWindow-Heading");
			_panelLatestBingles.add(heading);
			
			//_mapManager,WIDTH_TAB_PANEL,getBottomPanelHeight(),_bingleTypeManager
			
			LatestBingleListPanel listing;
			listing = new LatestBingleListPanel(250,
					                            getBottomPanelHeight()-70,
					                            _bingleTypeManager,
					                            _mapManager);
			_panelLatestBingles.add(listing);
		}
		return _panelLatestBingles;
	}
	
	protected DecoratorPanel getPanelMap()
	{
		if (_panelMap == null)
		{
			_panelMap = new DecoratorPanel();
			_panelMap.setWidget(getMap());
			
		}
		return _panelMap;
	}
	
	protected LatLng getLatLngFromUrl()
	{
		String lat = Window.Location.getParameter("lat");
		String lng = Window.Location.getParameter("lng");
		
		if ((lat == null) || (lng == null))
		{
			return null;
		}
		
		try {
			double latd = Double.parseDouble(lat);
			double lngd = Double.parseDouble(lng);
			return LatLng.newInstance(latd, lngd);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	protected MapWidget getMap()
	{
		if (_map == null)
		{
			LatLng urlLl = getLatLngFromUrl();
			if (urlLl != null)
			{
				_map = new MapWidget(urlLl,17);
			}
			else
			{
				String cookie = Cookies.getCookie(MapMoveCookieSaver.COOKIE_MAP_LAT_LONG);
				if (cookie == null)
				{
					LatLng melbourne = LatLng.newInstance(-37.778313,144.9646);
					_map = new MapWidget(melbourne,11);
					GWT.log("could not find map cookie so went to Melbourne", null);
				}
				else
				{
					_map = new MapWidget(MapMoveCookieSaver.getCenterFromCookie(cookie),
							             MapMoveCookieSaver.getZoomFromCookie(cookie));
					//System.out.println(cookie);
					GWT.log("got map cookie:" + cookie, null);
				}
			}
			
			
		    int h = getBottomPanelHeight();
		    int w = RootPanel.get().getOffsetWidth() - 260 -75;
		    _map.setSize(Integer.toString(w) + "px", Integer.toString(h) + "px");
		    
		    _map.addControl(new LargeMapControl());
		    _map.addControl(new MapTypeControl());
		    
		    
		    
		    String param = Window.Location.getParameter("bingleid");
		    if (param != null)
		    {
		    	try {
					int id = Integer.parseInt(param);
					_mapManager = new MapManager(_map,_bingleTypeManager,id);
				} catch (Exception e) {	}
		    }
		    else
		    {
		    	_mapManager = new MapManager(_map,_bingleTypeManager,-1);
		    }
		    
		    _map.addMapMoveEndHandler(new MapMoveCookieSaver());
		    _map.addMapMoveEndHandler(new MapMoveViewStacksUpdater(_mapManager));
		    
		    
		}
		return _map;
	}
	
	private int getBottomPanelHeight()
	{
		int h = Window.getClientHeight() -15 ;

		if (h < 500)
			h = 500;
		return h;
	}
	
	protected AddStackPanel getPanelAddStack()
	{
		if (_panelAddStack == null)
		{
			_panelAddStack = new AddStackPanel(_uiStateManager,_mapManager,WIDTH_TAB_PANEL,_bingleTypeManager);
			int h = getBottomPanelHeight(); //RootPanel.get().getOffsetHeight() - _mainTitle.getHeight() - 15;
			_panelAddStack.setHeight(Integer.toString(h) + "px");
			_panelAddStack.addStackAddedListener(new StackAddedListener()
			{

				public void onStackAdded(BikeStack addedStack) 
				{
					_mapManager.setMode(MapManager.MODE_SETTING_VIEW);
					getTabPanelMainOptions().selectTab(TabIndexes.VIEW);
					LatLng binglePt = addedStack.getPosition();
					_mapManager.showPointSelection(binglePt);
					_mapManager.panTo(binglePt);
					
					try {
						_mapManager.getBinglesCommunicator().requestLatestBingles();
					} catch (BinglesCommunicationException e) {
						// TODO Auto-generated catch block
						GWT.log("error when getting latest bingles", e);
					}
				}
				
			});
		}
		return _panelAddStack;
	}
	
	protected ViewStacksPanel getPanelViewStacks()
	{
		if (_panelViewStacks == null)
		{
			_panelViewStacks = new ViewStacksPanel(_uiStateManager,_mapManager,"245px");
			int h = getBottomPanelHeight(); //RootPanel.get().getOffsetHeight() - _mainTitle.getHeight() - 15;
			_panelViewStacks.setHeight(Integer.toString(h) + "px");
		}
		return _panelViewStacks;
	}
	
	protected MyBinglesPanel getPanelMyBingles()
	{
		if (_panelMyBingles == null)
		{
			_panelMyBingles = new MyBinglesPanel(_uiStateManager,_mapManager,WIDTH_TAB_PANEL,getBottomPanelHeight(),_bingleTypeManager);
		}
		return _panelMyBingles;
	}
	
	protected HelpPanel getPanelHelp()
	{
		if (_panelHelp == null)
		{
			_panelHelp = new HelpPanel(_uiStateManager,WIDTH_TAB_PANEL,getBottomPanelHeight());
		}
		return _panelHelp;
	}
}
