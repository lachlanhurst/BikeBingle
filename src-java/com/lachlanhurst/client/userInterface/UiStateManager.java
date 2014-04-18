package com.lachlanhurst.client.userInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.maps.client.geocode.StatusCodes;
import com.google.gwt.user.client.Window;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.userInterface.AddStackPanel.StackAddedListener;

/**
 * class used to manage the state of the user interface
 * @author lachlan
 *
 */
public class UiStateManager 
{
	protected boolean _isUserLoggedIn = false;
	
	protected String _userName = null;
	protected String _userEmail = null;

	
	/**
	 * checks whether a user is currently logged in
	 * @return
	 */
	public boolean isUserLoggedIn() 
	{
		return _isUserLoggedIn;
	}
	
	/**
	 * fires off a request to the server for the users details, if a user is logged in
	 * the UserLoginListener list will be triggered sometime in the future (not immediately
	 * after).
	 */
	public void requestUserDetails() throws UiStateManagerException
	{
		try {
			setUserWithServerData();
		} catch (RequestException e) {
			throw new UiStateManagerException("Error obtaining user data from server",e);
		}

	}
	
	protected void setUserWithServerData() throws RequestException
	{
		String url = GWT.getModuleBaseURL() + "user/";
		
		RequestBuilder builder;
		if (GWT.getHostPageBaseURL().indexOf(":8888") != -1)
			builder = new RequestBuilder(RequestBuilder.GET, "user.js");
		else
			builder = new RequestBuilder(RequestBuilder.GET, url);
		
		// Create a callback object to handle the result
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
				_userName = "";
				_userEmail = "";
				_isUserLoggedIn = false; 
			}


			public void onResponseReceived(Request request, Response response) 
			{
				if (response.getStatusCode() == StatusCodes.SUCCESS)
				{
					GWT.log("got response: " + response.getText(), null);
					String resp = response.getText().trim();
					if (resp.length() == 0)
					{
						return;
					}
					String[] bits = resp.split(" ");
					if (bits.length == 1)
					{
						_userName = bits[0];
						_userEmail = bits[0];
					}
					else if (bits.length == 2)
					{
						_userName = bits[0];
						_userEmail = bits[1];	
					}
					_isUserLoggedIn = true;
					fireUserLoginEvent(_userName, _userEmail,true);
				}
				else
				{
					fireUserLoginEvent(_userName, _userEmail,false);
				}
				
			}
		};

		// Send the request
		builder.sendRequest("payload", requestCallback);
	}
	
	
	/**
	 * should eventually redirect to the app engine google account login.  Right
	 * now all it does is change a bool
	 */
	public void doUserLogin(String tabName) throws UiStateManagerException
	{
		
		String url;
		if (tabName != null) {
			url = GWT.getModuleBaseURL() + "login/?tabname=" + tabName;
		}
		else
		{
			url = GWT.getModuleBaseURL() + "login/";
		}
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		GWT.log("request url = " + builder.getUrl(), null);

		// Create a callback object to handle the result
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
				_userName = "";
				_userEmail = "";
				_isUserLoggedIn = false;
			}

			public void onResponseReceived(Request request, Response response) 
			{
				//GWT.log("got response: " + response.getText(), null);
				String resp = response.getText();
				
				if (resp.indexOf("isLoggedIn") != -1)
				{
					String[] bits = resp.split(" ");
					if (bits.length == 1)
					{
						_userName = bits[0];
						_userEmail = bits[0];
					}
					else if (bits.length == 2 || bits.length == 3)
					{
						_userName = bits[0];
						_userEmail = bits[1];	
					}
					_isUserLoggedIn = true;
					fireUserLoginEvent(_userName, _userEmail, true);
				}
				else if (resp.startsWith("http"))
				{
					redirect(response.getText());
				}
				else
				{
					redirect(GWT.getModuleBaseURL() + response.getText().substring(1));
				}
				
			}
		};

		try {
			// Send the request
			builder.sendRequest("payload", requestCallback);
		} catch (RequestException e) {
			throw new UiStateManagerException("Error occured while attempting user login",e);
		}

	}
	
	/**
	 * gets the user name that is currently logged in, the users prefered
	 * name in this case.
	 * @return
	 */
	public String getPreferredUserName()
	{
		//TODO hook this up to the server too
		return _userName;
	}
	
	/**
	 * gets the full google user name for this user
	 * @return
	 */
	public String getUserName()
	{
		//TODO hook this up to the server too
		return "lachlan.hurst@gmail.com";
	}
	
	/**
	 * redirects the current gwt app to the given url
	 * @param url
	 */
	native void redirect(String url)
	/*-{
	        $wnd.location.replace(url);

	}-*/;
	
	
	//EVENT CODE
	private List _eventListenersUserLogin = new ArrayList();
	
	public interface UserLoginListener extends java.util.EventListener
	{
	    void onUserLogin(String name, String email, boolean success);
	}
	
	public void addUserLoginListener(UserLoginListener listener)
    {
		_eventListenersUserLogin.add(listener);
    }

    public void removeStackAddedListener(UserLoginListener listener)
    {
    	_eventListenersUserLogin.remove(listener);
    }

    protected void fireUserLoginEvent(String name, String email, boolean success)
    {
    	for(Iterator it = _eventListenersUserLogin.iterator(); it.hasNext();)
        {
    		UserLoginListener listener = (UserLoginListener) it.next();
            listener.onUserLogin(name, email,success);
        }

    }

	
}
