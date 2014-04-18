package com.lachlanhurst.client.serverCommunication;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.maps.client.geocode.StatusCodes;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.userInterface.UiStateManager.UserLoginListener;

/**
 * manages the communication with the server for all bikebingle
 * data.
 * @author lachlan
 *
 */
public class BinglesCommunicationManager 
{
	public static final String GET_BINGLES_URL = "getbingles/";
	public static final String GET_LATEST_BINGLES_URL = "getlatestbingles/";
	public static final String ADD_BINGLE_URL = "addbingle/";
	public static final String DELETE_BINGLE_URL = "deletebingle/";
	
	public static final String JSON_NAME_DESCRIPTION = "description";
	public static final String JSON_NAME_OCCURED_ON = "occuredOn";
	public static final String JSON_NAME_ENTERED_ON = "enteredOn";
	public static final String JSON_NAME_INJURY_INDEX = "injuryIndex";
	public static final String JSON_NAME_TYPE = "type";
	public static final String JSON_NAME_LINK = "link";
	public static final String JSON_NAME_ID = "id";
	
	public static final String JSON_NAME_POSITION = "position";
	public static final String JSON_NAME_POSITION_LATITUDE = "lat";
	public static final String JSON_NAME_POSITION_LONGITUDE = "lng";
	
	public static final String JSON_NAME_ENTERED_BY = "enteredBy";
	public static final String JSON_NAME_ENTERED_BY_NAME = "name";
	public static final String JSON_NAME_ENTERED_BY_EMAIL = "email";
	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final int MAX_RETRY_ATTEMPS = 3;
	
	protected LatLngBounds _bounds;
	protected int _retryCount = 0;
	
	protected boolean _isIserOnlyBingles = false;
	protected boolean _isLowResBingles = false;
	
	protected Request _lastGetBinglesRequest = null;
	
	public BinglesCommunicationManager()
	{
		
	}
	
	/**
	 * only request bingles for the currently logged in user.
	 * @param isForUserOnly
	 */
	public void setUserOnlyBingles(boolean isForUserOnly)
	{
		_isIserOnlyBingles = isForUserOnly;
	}
	
	/**
	 * returns a level of detail represenation of the bingles as the 
	 * user has zoomed out a long way
	 * @param isLowResBingles
	 */
	public void setLowResBingles(boolean isLowResBingles)
	{
		_isLowResBingles = isLowResBingles;
	}
	
	/**
	 * requests the latest 10 bingles that were added to the database
	 * @throws BinglesCommunicationException
	 */
	public void requestLatestBingles() throws BinglesCommunicationException
	{
		String url = getLatestBinglesRequestUrl();
		RequestBuilder builder;
		if (GWT.getHostPageBaseURL().indexOf(":8888") != -1)
			builder = new RequestBuilder(RequestBuilder.GET, "result.js");
		else
			builder = new RequestBuilder(RequestBuilder.GET, url);
		
		builder.setHeader("Content-Length", "0");
		
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
			}

			public void onResponseReceived(Request request, Response response) 
			{
				String resp = response.getText();
				if (response.getStatusCode() == StatusCodes.SUCCESS)
				{
					JSONValue jsonValue = JSONParser.parse(resp);
					
					List bingles;
					try {
						bingles = parseJsonToBingles(jsonValue);
						GWT.log(bingles.size() + " latest bike bingles returned from server", null);
						fireLatestBinglesReturnedEvent(bingles);
					} catch (BinglesCommunicationException e) {
						GWT.log("error parsing json", e);
					}
				}
				else
				{
					GWT.log("status code = " + response.getStatusCode(), null);	
					fireServerContactFailedEvent("Could not contact BikeBingle server: </br> " + 
							                     response.getStatusText() + "</br>"); 
				}
				
			}
		};

		// Send the request
		try {
			builder.sendRequest("", requestCallback);
		} catch (RequestException e) {
			throw new BinglesCommunicationException("Error occured while requesting BikeBingle data from server",e);
		}

		
	}
	
	/**
	 * generates a request and sends it to the server to return all bingles within
	 * the set bounds (via setBounds method).  Two events are fired when this method
	 * is called; a RequestMade event fired when the request is sent and a 
	 * BinglesReturned event when the server response comes back.  Note: the second 
	 * event will only be fired if there are bingles in the specified bounds and the
	 * request was successful.
	 */
	public void requestBingles() throws BinglesCommunicationException
	{
		String url = getRequestUrl();
		RequestBuilder builder;
		if (GWT.getHostPageBaseURL().indexOf(":8888") != -1)
			builder = new RequestBuilder(RequestBuilder.GET, "result.js");
		else
			builder = new RequestBuilder(RequestBuilder.GET, url);
		
		//set the content length lock!!!, and do the python side too
		builder.setHeader("Content-Length", "0");
		
		if (_lastGetBinglesRequest != null)
		{
			_lastGetBinglesRequest.cancel();
			_lastGetBinglesRequest = null;
		}
		
		// Create a callback object to handle the result
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
			}

			public void onResponseReceived(Request request, Response response) 
			{
				String resp = response.getText();
				if (response.getStatusCode() == StatusCodes.SUCCESS)
				{
					JSONValue jsonValue = JSONParser.parse(resp);
					
					List bingles;
					try {
						bingles = parseJsonToBingles(jsonValue);
						GWT.log(bingles.size() + " bike bingles returned from server", null);
						_lastGetBinglesRequest = null;
						fireBinglesReturnedEvent(bingles);
					} catch (BinglesCommunicationException e) {
						GWT.log("error parsing json", e);
					}
				}
				else
				{
					GWT.log("status code = " + response.getStatusCode(), null);	
					fireServerContactFailedEvent("Could not contact BikeBingle server: </br> " + 
							                     response.getStatusText() + "</br>"); 
				}
				
			}
		};

		// Send the request
		try {
			_lastGetBinglesRequest = builder.sendRequest("", requestCallback);
		} catch (RequestException e) {
			throw new BinglesCommunicationException("Error occured while requesting BikeBingle data from server",e);
		}
	}
	
	public static List parseJsonToBingles(JSONValue jsonVal) throws BinglesCommunicationException
	{
		
		List res = new ArrayList();
		JSONArray ar = jsonVal.isArray();
		if (ar == null)
		{
			throw new BinglesCommunicationException("invalid JSON data returned from server");
		}
		else
		{
			
			for (int i = 0; i < ar.size(); i++)
			{
				JSONValue val = ar.get(i);
				BikeStack bb = getBingleFromJsonValue(val);
				res.add(bb);
			}
		}
		
		return res;
	}
	
	private static BikeStack getBingleFromJsonValue(JSONValue val) throws BinglesCommunicationException
	{
		JSONObject jObj = val.isObject();
		if (jObj == null)
		{
			throw new BinglesCommunicationException("error parsing a JSON object returned from server");
		}
		
		String description = getString(jObj.get(JSON_NAME_DESCRIPTION));
		String[] user = getUser(jObj.get(JSON_NAME_ENTERED_BY));
		Date enteredOn = getDate(jObj.get(JSON_NAME_ENTERED_ON));
		int injuryIndex = getInt(jObj.get(JSON_NAME_INJURY_INDEX));
		int id = getInt(jObj.get(JSON_NAME_ID));
		String link = getString(jObj.get(JSON_NAME_LINK));
		Date occuredOn = getDate(jObj.get(JSON_NAME_OCCURED_ON));
		double[] position = getPosition(jObj.get(JSON_NAME_POSITION));
		int type = getInt(jObj.get(JSON_NAME_TYPE));
		
		BikeStack bb = new BikeStack();
		bb.setDescription(description);
		if (user != null)
		{
			bb.setUser(user[0]);
			bb.setEmail(user[1]);
		}
		
		bb.setEntryDate(enteredOn);
		bb.setInjurySeverity(injuryIndex);
		bb.setLocation(position[0], position[1]);
		bb.setOccuredDate(occuredOn);
		bb.setType(type);
		bb.setId(id);
		bb.setLink(link);
		
		return bb;
	}
	
	private static String getString(JSONValue val)
	{
		if (val == null)
		{
			return "";
		}
		return val.isString().stringValue();
	}
	
	private static int getInt(JSONValue val)
	{
		if (val == null)
		{
			return -1;
		}
		return (int)val.isNumber().doubleValue();
	}
	
	private static Date getDate(JSONValue val)
	{
		if (val == null)
		{
			return null;
		}
		String s = val.isString().stringValue();
		int index = s.lastIndexOf('.');
		if (index != -1)
		{
			s = s.substring(0,index);	
		}
		
		DateTimeFormat dtf = DateTimeFormat.getFormat(DATE_TIME_FORMAT);
		return dtf.parse(s);
	}
	
	private static JSONString getJsonDate(Date date)
	{
		DateTimeFormat dtf = DateTimeFormat.getFormat(DATE_TIME_FORMAT);
		return new JSONString(dtf.format(date));
	}
	
	/**
	 * gets the user details from this json value
	 * @param val 
	 * @return array[0] is the name, array[1] is the email address
	 */
	private static String[] getUser(JSONValue val)
	{
		if (val == null)
		{
			return null;
		}
		JSONObject obj = val.isObject();
		String email = obj.get(JSON_NAME_ENTERED_BY_EMAIL).isString().stringValue();
		String name = obj.get(JSON_NAME_ENTERED_BY_NAME).isString().stringValue();
		String[] res = {name,email}; 
		return res;
	}
	
	/**
	 * gets the position details from this value
	 * @param val
	 * @return array[0] is the latitude, array[1] is the longitude
	 */
	private static double[] getPosition(JSONValue val)
	{
		JSONObject obj = val.isObject();
		double latitude = obj.get(JSON_NAME_POSITION_LATITUDE).isNumber().doubleValue();
		double longitude = obj.get(JSON_NAME_POSITION_LONGITUDE).isNumber().doubleValue();
		double res[] = {latitude,longitude};
		return res;
		
	}

	/**
	 * fires off a request to the server to add the given bingle to the database.
	 * will fire an addbingleattempt event when the response comes back.
	 * @param bingle the bikestack to add to the remote database
	 */
	public void requestAddBingle(BikeStack bingle) throws BinglesCommunicationException
	{
		JSONObject jsonBingle = getBingleAsJsonObject(bingle);
		//GWT.log(jsonBingle.toString(), null);
		
		String url = getAddUrl();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String bingleAsString = jsonBingle.toString();
		builder.setHeader("Content-Length", Integer.toString(bingleAsString.length()));
		
		// Create a callback object to handle the result
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
				fireAddBingleAttemptEvent(false, "Bingle not added to database, error: " + exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) 
			{
				if (response.getStatusCode() == StatusCodes.SUCCESS)
				{
					String resp = response.getText();
					fireAddBingleAttemptEvent(true, "Bingle added to database");
				}
				else
				{
					fireAddBingleAttemptEvent(false, "Bingle not added to database: " + response.getStatusText());
				}
				
			}
		};

		// Send the request
		try {
			builder.sendRequest(bingleAsString, requestCallback);
		} catch (RequestException e) {
			throw new BinglesCommunicationException("Error occured while requesting BikeBingle data from server",e);
		}

		
	}
	
	protected JSONObject getBingleAsJsonObject(BikeStack bingle)
	{
		JSONObject jObj = new JSONObject();
		jObj.put(JSON_NAME_DESCRIPTION, getJsonString(bingle.getDescription()));
		
		//JSONObject user = new JSONObject();
		//user.put(JSON_NAME_ENTERED_BY_NAME, new JSONString(bingle.getUser()));
		//user.put(JSON_NAME_ENTERED_BY_EMAIL, new JSONString(bingle.getEmail()));
		//jObj.put(JSON_NAME_ENTERED_BY, user);
		
		jObj.put(JSON_NAME_OCCURED_ON, getJsonDate(bingle.getOccuredDate()));
		jObj.put(JSON_NAME_INJURY_INDEX, new JSONNumber(bingle.getInjurySeverity()));
		
		LatLng pt = bingle.getPosition();
		JSONObject pos = new JSONObject();
		pos.put(JSON_NAME_POSITION_LATITUDE, new JSONNumber(pt.getLatitude()));
		pos.put(JSON_NAME_POSITION_LONGITUDE, new JSONNumber(pt.getLongitude()));
		jObj.put(JSON_NAME_POSITION, pos);

		jObj.put(JSON_NAME_TYPE, new JSONNumber(bingle.getType()));
		jObj.put(JSON_NAME_LINK, getJsonString(bingle.getLink()));
		
		return jObj;
	}
	
	protected JSONString getJsonString(String val)
	{
		if (val == null)
		{
			return new JSONString("");
		}
		else
		{
			return new JSONString(val);
		}
	}
	
	/**
	 * gets the url that will request data from the server
	 * @return
	 * @throws BinglesCommunicationException 
	 */
	public String getRequestUrl() throws BinglesCommunicationException
	{
		String base = GWT.getModuleBaseURL();
		String getUrlBit = GET_BINGLES_URL;
		String requestComponent = "?" + getBoundsUrlBit();
		
		if (_isIserOnlyBingles)
		{
			requestComponent = requestComponent + "&isuseronly=true";
		}
		
		if (_isLowResBingles)
		{
			requestComponent = requestComponent + "&islowres=true";
		}
		
		return base + getUrlBit + requestComponent;
	}
	
	public String getLatestBinglesRequestUrl() throws BinglesCommunicationException
	{
		String base = GWT.getModuleBaseURL();
		String getUrlBit = GET_LATEST_BINGLES_URL;
		
		return base + getUrlBit;
	}
	
	/**
	 * gets the URL that will be used to add data to the server
	 * @return
	 */
	public String getAddUrl()
	{
		String base = GWT.getModuleBaseURL();
		String getUrlBit = ADD_BINGLE_URL;
		return base + getUrlBit;
	}
	
	public String getDeleteUrl()
	{
		String base = GWT.getModuleBaseURL();
		String getUrlBit = DELETE_BINGLE_URL;
		return base + getUrlBit;
	}
	
	
	/**
	 * sets the bounds for any request that is generated.  Only bingles that
	 * are located within these bounds are returned.
	 * @param bounds
	 */
	public void setBounds(LatLngBounds bounds)
	{
		_bounds = bounds;
	}
	
	/**
	 * builds up a string suitable for addinh into the URL as part of the request
	 * @return
	 */
	protected String getBoundsUrlBit() throws BinglesCommunicationException
	{
		if (_bounds == null)
		{
			throw new BinglesCommunicationException("no bounds were specified, could not generate request URL");
		}
		
		String res = "neLatitude=" + _bounds.getNorthEast().getLatitude() +
		             "&neLongitude=" + _bounds.getNorthEast().getLongitude() +
		             "&swLatitude=" + _bounds.getSouthWest().getLatitude() +
		             "&swLongitude=" + _bounds.getSouthWest().getLongitude();
		return res;
	}
	
	public void requestDeleteBingle(int id) throws BinglesCommunicationException
	{
		String url = getDeleteUrl();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String bingleIdAsString = Integer.toString(id);
		builder.setHeader("Content-Length", Integer.toString(bingleIdAsString.length()));
		
		final int anId = id;
		
		// Create a callback object to handle the result
		RequestCallback requestCallback = new RequestCallback() 
		{
			public void onError(Request request, Throwable exception) 
			{
				GWT.log("error happened", null);
				fireDeleteBingleAttemptEvent(false, "Bingle not deleted from database, error: " + exception.getMessage(),anId);
			}

			public void onResponseReceived(Request request, Response response) 
			{
				if (response.getStatusCode() == StatusCodes.SUCCESS)
				{
					String resp = response.getText();
					if (resp.trim().compareTo("true") == 0)
					{
						fireDeleteBingleAttemptEvent(true, "Bingle deleted from database", anId);
					}
					else
					{
						fireDeleteBingleAttemptEvent(false, "Bingle not deleted from database", anId);
					}
				}
				else
				{
					fireDeleteBingleAttemptEvent(false, "Bingle not deleted from database: " + response.getStatusText(), anId);
				}
				
			}
		};

		// Send the request
		try {
			builder.sendRequest(bingleIdAsString, requestCallback);
		} catch (RequestException e) {
			throw new BinglesCommunicationException("Error occured while requesting BikeBingle deletion from server",e);
		}
	}
	
	public void requestDeletedBingle(BikeStack bingle) throws BinglesCommunicationException
	{
		requestDeleteBingle(bingle.getId());
	}
	
	
	//EVENT CODE
	private List _eventListenersRequestMade = new ArrayList();
	public interface RequestMadeListener extends java.util.EventListener
	{
	    void onRequestMade(String requestDescription);
	}
	
	public void addRequestMadeListener(RequestMadeListener listener)
    {
		_eventListenersRequestMade.add(listener);
    }

    public void removeStackAddedListener(RequestMadeListener listener)
    {
    	_eventListenersRequestMade.remove(listener);
    }

    protected void fireRequestMadeEvent(String requestDescription)
    {
    	for(Iterator it = _eventListenersRequestMade.iterator(); it.hasNext();)
        {
    		RequestMadeListener listener = (RequestMadeListener) it.next();
            listener.onRequestMade(requestDescription);
        }
    }

    //------------------------------
    private List _eventListenersLatestBinglesReturned = new ArrayList();
	public interface LatestBinglesReturnedListener extends java.util.EventListener
	{
	    void onBinglesReturned(List bingles);
	}
	
	public void addLatestBinglesReturnedListener(LatestBinglesReturnedListener listener)
    {
		_eventListenersLatestBinglesReturned.add(listener);
    }

    public void removeLatestBinglesReturnedListener(LatestBinglesReturnedListener listener)
    {
    	_eventListenersLatestBinglesReturned.remove(listener);
    }

    protected void fireLatestBinglesReturnedEvent(List bingles)
    {
    	for(Iterator it = _eventListenersLatestBinglesReturned.iterator(); it.hasNext();)
        {
    		LatestBinglesReturnedListener listener = (LatestBinglesReturnedListener) it.next();
            listener.onBinglesReturned(bingles);
        }
    }

    
    
    
	private List _eventListenersBinglesReturned = new ArrayList();
	public interface BinglesReturnedListener extends java.util.EventListener
	{
	    void onBinglesReturned(List bingles);
	}
	
	public void addBinglesReturnedListener(BinglesReturnedListener listener)
    {
		_eventListenersBinglesReturned.add(listener);
    }

    public void removeStackAddedListener(BinglesReturnedListener listener)
    {
    	_eventListenersBinglesReturned.remove(listener);
    }

    protected void fireBinglesReturnedEvent(List bingles)
    {
    	for(Iterator it = _eventListenersBinglesReturned.iterator(); it.hasNext();)
        {
    		BinglesReturnedListener listener = (BinglesReturnedListener) it.next();
            listener.onBinglesReturned(bingles);
        }
    }

	private List _eventListenersAddBingleAttempt = new ArrayList();
	public interface AddBingleAttemptListener extends java.util.EventListener
	{
	    void onAddBingleAttempt(boolean success, String message);
	}
	
	public void addAddBingleAttemptListener(AddBingleAttemptListener listener)
    {
		_eventListenersAddBingleAttempt.add(listener);
    }

    public void removeAddBingleAttemptListener(AddBingleAttemptListener listener)
    {
    	_eventListenersAddBingleAttempt.remove(listener);
    }

    protected void fireAddBingleAttemptEvent(boolean success, String message)
    {
    	for(Iterator it = _eventListenersAddBingleAttempt.iterator(); it.hasNext();)
        {
    		AddBingleAttemptListener listener = (AddBingleAttemptListener) it.next();
            listener.onAddBingleAttempt(success, message);
        }
    }

    private List _eventListenersDeleteBingleAttempt = new ArrayList();
	public interface DeleteBingleAttemptListener extends java.util.EventListener
	{
	    void onDeleteBingleAttempt(boolean success, String message, int bingleId);
	}
	
	public void addDeleteBingleAttemptListener(DeleteBingleAttemptListener listener)
    {
		_eventListenersDeleteBingleAttempt.add(listener);
    }

    public void removeStackAddedListener(DeleteBingleAttemptListener listener)
    {
    	_eventListenersDeleteBingleAttempt.remove(listener);
    }

    protected void fireDeleteBingleAttemptEvent(boolean success, String message, int bingleId)
    {
    	for(Iterator it = _eventListenersDeleteBingleAttempt.iterator(); it.hasNext();)
        {
    		DeleteBingleAttemptListener listener = (DeleteBingleAttemptListener) it.next();
            listener.onDeleteBingleAttempt(success, message, bingleId);
        }
    }

    
    private List _eventListenersServerContactFailed = new ArrayList();
	public interface ServerContactFailedListener extends java.util.EventListener
	{
	    void onServerContactFailed(String message);
	}
	
	public void addServerContactFailedListener(ServerContactFailedListener listener)
    {
		_eventListenersServerContactFailed.add(listener);
    }

    public void removeStackAddedListener(ServerContactFailedListener listener)
    {
    	_eventListenersServerContactFailed.remove(listener);
    }

    protected void fireServerContactFailedEvent(String message)
    {
    	for(Iterator it = _eventListenersServerContactFailed.iterator(); it.hasNext();)
        {
    		ServerContactFailedListener listener = (ServerContactFailedListener) it.next();
            listener.onServerContactFailed(message);
        }
    }
	
}
