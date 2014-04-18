package com.lachlanhurst.client.data;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.LatLng;

/**
 * represents a single bike stack with position (lat/lng), id, description, user
 * and so forth. 
 * @author lachlan
 *
 */
public class BikeStack 
{
	protected int _id;
	
	protected Date _occured;
	protected Date _entered;
	
	protected double _latitude;
	protected double _longitude;
	
	protected String _description;
	
	protected String _user;
	protected String _email;
	
	protected int _injuryIndex;
	protected int _type;
	
	protected String _link = null;
	
	public BikeStack()
	{
		_entered = new Date();
		_occured = null;
	}
	
	/**
	 * gets the unique id of this bike stack
	 * @return
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * sets the unique identifer number for this stack
	 * @param id
	 */
	public void setId(int id)
	{
		_id = id;
	}
	
	/**
	 * gets the date that the stack happened, as enetered by the user
	 * @return
	 */
	public Date getOccuredDate()
	{
		return _occured;
	}
	
	/**
	 * gets the date that this bike stack was added to the database
	 * @return
	 */
	public Date getEntryDate()
	{
		return _entered;
	}
	
	/**
	 * sets the time when this stack was added to the database
	 * @param entered
	 */
	public void setEntryDate(Date entered)
	{
		_entered = entered;
	}
	
	/**
	 * set the date when this stack happened
	 * @param occured
	 */
	public void setOccuredDate(Date occured)
	{
		_occured = occured;
	}
	
	/**
	 * gets the position of the stack
	 * @return
	 */
	public LatLng getPosition()
	{
		return LatLng.newInstance(_latitude, _longitude);
	}
	
	/**
	 * gets a description of the bike stack as entered by the user
	 * @return
	 */
	public String getDescription()
	{
		return _description;
	}
	
	/**
	 * sets the description for this stack
	 * @param description
	 */
	public void setDescription(String description)
	{
		_description = description;
	}
	
	/**
	 * sets the user identification, the raw string will be one-way-hashed in
	 * some way shape or form.... eventually.
	 * @param userName
	 */
	public void setUser(String userName)
	{
		_user = userName;
	}
	
	public String getUser()
	{
		return _user;
	}
	
	/**
	 * sets the users email address
	 * @param email
	 */
	public void setEmail(String email)
	{
		_email = email;
	}
	
	public String getEmail()
	{
		return _email;
	}
	
	/**
	 * sets the location/position of this stack by the given
	 * lat/lng.
	 * @param latitude
	 * @param longitude
	 */
	public void setLocation(double latitude, double longitude)
	{
		_latitude = latitude;
		_longitude = longitude;
	}
	
	/**
	 * sets the injury severity of this stack
	 * @param injurySeverity
	 */
	public void setInjurySeverity(int injurySeverity)
	{
		_injuryIndex = injurySeverity;
	}
	
	public int getInjurySeverity()
	{
		return _injuryIndex;
	}
	
	/**
	 * sets the type of stack
	 * @param type
	 */
	public void setType(int type)
	{
		_type = type;
	}
	
	/**
	 * returns the type of this stack
	 * @return
	 */
	public int getType()
	{
		return _type;
	}

	public String getLink() 
	{
		return _link;
	}

	public void setLink(String link) 
	{
		this._link = link;
	}
	
	/**
	 * gets a URL that the user can copy and paste to link to this bikebingle 
	 * specifically.
	 * @return
	 */
	public String getQueryUrl()
	{
		String reqBit = "?" +
			            "bingleid=" + Integer.toString(_id) + "&" +
		                "lat=" + Double.toString(_latitude) + "&" +
		                "lng=" + Double.toString(_longitude);
 		
		return GWT.getModuleBaseURL() + reqBit;
	}
	
}
