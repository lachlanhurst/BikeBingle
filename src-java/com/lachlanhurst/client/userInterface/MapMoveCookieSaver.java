package com.lachlanhurst.client.userInterface;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Cookies;

public class MapMoveCookieSaver implements MapMoveEndHandler 
{
	public static final String COOKIE_MAP_LAT_LONG = "BikeStackStatsMap";
	
	public void onMoveEnd(MapMoveEndEvent event) 
	{
		int zoom = event.getSender().getZoomLevel();
		LatLng ll = event.getSender().getCenter();
		String value = getMapPosAsString(ll,zoom);
		Date expiry = getExpiryDateForCookie();
		//GWT.log("set map cookie:" + value + " expires: " + expiry.toString(), null);
		Cookies.setCookie(COOKIE_MAP_LAT_LONG, value, expiry);
	}
	
	public static String getMapPosAsString(LatLng ll, int zoom)
	{
		String value  = "";
		value += Double.toString(ll.getLatitude()) + ",";
		value += Double.toString(ll.getLongitude()) + ",";
		value += Integer.toString(zoom);
		return value;
	}
	
	public static LatLng getCenterFromCookie(String cookie)
	{
		String[] bits = cookie.split(",");
		double lat = Double.parseDouble(bits[0]);
		double lng = Double.parseDouble(bits[1]);
		return LatLng.newInstance(lat, lng);
	}
	
	public static int getZoomFromCookie(String cookie)
	{
		String[] bits = cookie.split(",");
		return Integer.parseInt(bits[2]);
	}
	
	public static Date getExpiryDateForCookie()
	{
		Date d = new Date();
		//set expiry for two weeks later
		Date res = new Date(d.getTime() + 1000*60*60*24*14);
		return res;
	}
}
