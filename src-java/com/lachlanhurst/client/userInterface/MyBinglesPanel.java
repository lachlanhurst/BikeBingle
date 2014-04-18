package com.lachlanhurst.client.userInterface;

import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationException;
import com.lachlanhurst.client.serverCommunication.BinglesCommunicationManager;
import com.lachlanhurst.client.userInterface.widgets.BingleListPanel;
import com.lachlanhurst.client.userInterface.widgets.BinglePanel;
import com.lachlanhurst.client.util.DialogGenerator;

/**
 * the user interface for the mybingles tab.  Allows the user to view,
 * and remove their own bingles from the database
 * @author lachlan
 *
 */
public class MyBinglesPanel extends VerticalPanel 
{
	protected UiStateManager _uiStateManager;
	protected MapManager _mapManager;

	protected BikeStackTypes _bingleTypeManager = null;
	
	protected int _width;
	protected int _height;
	
	protected Label _labelNotLoggedIn = null;
	protected Button _buttonLogin = null;
	
	protected BingleListPanel _bingleList = null;
	
	public MyBinglesPanel(UiStateManager uiStateManager, MapManager mapManager, String width, int height, BikeStackTypes bingleTypeManager)
	{
		_bingleTypeManager = bingleTypeManager;
		_uiStateManager = uiStateManager;
		_mapManager = mapManager;
		_width = Integer.parseInt(width.substring(0, width.length() - 2)) + 7;
		_height = height;
		this.setWidth(width);
		
		initialise();
		
		_uiStateManager.addUserLoginListener(new UiStateManager.UserLoginListener()
		{
			public void onUserLogin(String name, String email, boolean isSuccess) 
			{
				if (isSuccess)
				{
					setUserIsLoggedIn();
				}
				else
				{
					Exception ex = new Exception("Error contacting BikeBingle Server, please retry login");
					handleException(ex);
				}
			}
		});
		
		if (_uiStateManager.isUserLoggedIn())
		{
			this.getLabelNotLoggedIn().setVisible(false);
			this.getButtonLogin().setVisible(false);
		}
		
		_mapManager.getBinglesCommunicator().addBinglesReturnedListener(new BinglesCommunicationManager.BinglesReturnedListener()
		{

			public void onBinglesReturned(List bingles) 
			{
				if (_mapManager.getMode() == MapManager.MODE_SETTING_MY_BINGLES)
				{
					getPanelBingleList().setBingles(bingles);
				}
				
			}
			
		});
		
		_mapManager.getBinglesCommunicator().addDeleteBingleAttemptListener(new BinglesCommunicationManager.DeleteBingleAttemptListener()
		{
			public void onDeleteBingleAttempt(boolean success, String message, int id) 
			{
				if (!success)
				{
					final DialogBox dialogBox = DialogGenerator.createErrorDialogBox(message);
				    dialogBox.setAnimationEnabled(true);
		            dialogBox.center();
		            dialogBox.show();
				}
				else
				{
					getPanelBingleList().removeBinglePanel(id);
					_mapManager.ensureServerUpdate();
					_mapManager.updateAll();
				}
			}
			
		});
	}
	
	protected void initialise()
	{
		this.setSpacing(1);
		this.add(getLabelNotLoggedIn());
		this.add(getButtonLogin());
		this.add(getPanelBingleList());
	}
	
	protected Label getLabelNotLoggedIn()
	{
		if (_labelNotLoggedIn == null)
		{
			_labelNotLoggedIn = new Label();
			_labelNotLoggedIn.setText("You must be logged in to access MyBingles");
		}
		return _labelNotLoggedIn;
	}
	
	protected Button getButtonLogin()
	{
		if (_buttonLogin == null)
		{
			_buttonLogin = new Button("Click to login");
			_buttonLogin.setStylePrimaryName("addStackPanel-Button");
			_buttonLogin.setWidth(_width + "px");
			_buttonLogin.addClickListener(new ClickListener()
			{
				public void onClick(Widget sender) 
				{
					try 
					{
						_uiStateManager.doUserLogin("mybingles");
					} catch (UiStateManagerException e) {
						handleException(e);
					}
				}
				
			});
		}
		return _buttonLogin;
	}
	
	/**
	 * sets the user interface to the state that is to be displayed when a user is logged in.
	 */
	protected void setUserIsLoggedIn()
	{
		if (_uiStateManager.isUserLoggedIn())
		{
			getButtonLogin().setVisible(false);
			getLabelNotLoggedIn().setVisible(false);
			getPanelBingleList().setVisible(true);
		}
		
	}
	
	private void handleException(Exception ex)
	{
		final DialogBox dialogBox = DialogGenerator.createExceptionDialogBox(ex);
	    dialogBox.setAnimationEnabled(true);
        dialogBox.center();
        dialogBox.show();
	}
	
	protected BingleListPanel getPanelBingleList()
	{
		if (_bingleList == null)
		{
			_bingleList = new BingleListPanel(_width,_height - 2,_bingleTypeManager);
			_bingleList.setVisible(false);
			_bingleList.addBingleOperationRequestedListener(new BinglePanel.BingleOperationRequestedListener()
			{
				public void onBingleOperationRequested(int operationType, BikeStack bingle) 
				{
					processBinglePanel(operationType, bingle);
				}
				
			});
		}
		return _bingleList;
	}
	
	protected void processBinglePanel(int operationType, BikeStack bingle)
	{
		if (operationType == BinglePanel.BINGLE_OPERATION_DELETE)
		{
			processDelete(bingle);
		}
		else if (operationType == BinglePanel.BINGLE_OPERATION_GOTO)
		{
			processGoto(bingle);
		}
		else if (operationType == BinglePanel.BINGLE_OPERATION_OPEN_RELATED_URL)
		{
			processOpenRelated(bingle);
		}
		else
		{
			throw new RuntimeException("Unexpected binglepanel operation received");
		}
	}
	
	protected void processGoto(BikeStack bingle)
	{
		LatLng pt = bingle.getPosition();
		_mapManager.showPointSelection(pt);
		_mapManager.panTo(pt);
	}
	
	protected void processDelete(BikeStack bingle)
	{
		boolean doDelete = Window.confirm("Bingle will be deleted from the database permanentally. Continue?");
		if (doDelete)
		{
			try {
				_mapManager.getBinglesCommunicator().requestDeleteBingle(bingle.getId());
				
			} catch (BinglesCommunicationException e) {
				handleException(e);
			}
		}
		
	}
	
	protected void processOpenRelated(BikeStack bingle)
	{
		Window.open(bingle.getLink(),"BikeBingle related page",null);
	}
}
