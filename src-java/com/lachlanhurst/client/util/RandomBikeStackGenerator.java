package com.lachlanhurst.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.Bounds;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.Random;
import com.lachlanhurst.client.data.BikeStack;
import com.lachlanhurst.client.data.BikeStackTypes;

public class RandomBikeStackGenerator 
{

	
	public static final int getRandomStackType()
	{
		return (int)((12) * Random.nextDouble());
	}
	
	public static List getLineOfStacks(int number, LatLngBounds map)
	{
		LatLng northEast = map.getNorthEast();
		LatLng southWest = map.getSouthWest();
		double width = (southWest.getLatitude() - northEast.getLatitude());
		double height = (northEast.getLongitude() - southWest.getLongitude());
		
		double heightStep = height/number;
		double widthStep = width/number;
		
		List res = new ArrayList();
		
		for (int i = 0; i < number; i++)
		{
			BikeStack stack = new BikeStack();
			
			//set the user name
			if (i % 6 == 0)
				stack.setUser("one");
			else if (i % 5 == 0)
				stack.setUser("two");
			else if (i % 4 == 0)
				stack.setUser("three");
			else if (i % 3 == 0)
				stack.setUser("four");
			else if (i % 2 == 0)
				stack.setUser("five");
			else
				stack.setUser("six");
			
			double lat = -1* widthStep * i + southWest.getLatitude() + ((Random.nextDouble() * widthStep)/3);
			double lng = heightStep * i + southWest.getLongitude() + ((Random.nextDouble() * heightStep)/3);
			
			stack.setLocation(lat, lng);
			stack.setType(getRandomStackType());
			
			stack.setOccuredDate(new Date());
			
			stack.setDescription("this is the stacks desciption for stack entered by: " + stack.getUser());
			res.add(stack);
			
		}
		
		return res;
	}
	
	
	public static BikeStack getRandomStack(int index, LatLngBounds map, boolean doLinearRnd)
	{
		BikeStack stack = new BikeStack();
		
		//set the user name
		if (index % 6 == 0)
			stack.setUser("one");
		else if (index % 5 == 0)
			stack.setUser("two");
		else if (index % 4 == 0)
			stack.setUser("three");
		else if (index % 3 == 0)
			stack.setUser("four");
		else if (index % 2 == 0)
			stack.setUser("five");
		else
			stack.setUser("six");
		
		
		LatLng northEast = map.getNorthEast();
		LatLng southWest = map.getSouthWest();
		
		//GWT.log(Double.toString(northEast.getLatitude()) + ", " + southWest.getLatitude(), null);
		//GWT.log(Double.toString(map.getBounds().toSpan().getLatitude()), null);
		
		
		//set pos
		double lat; 
		double lng; 
		if (doLinearRnd)
		{
			lat = getRandomManipulatedDoubleBetween(northEast.getLatitude(), southWest.getLatitude());
			lng = getRandomManipulatedDoubleBetween(northEast.getLongitude(), southWest.getLongitude());
		}
		else
		{
			lat = getRandomDoubleBetween(northEast.getLatitude(), southWest.getLatitude());
			lng = getRandomDoubleBetween(northEast.getLongitude(), southWest.getLongitude());
		}
		stack.setLocation(lat, lng);
		stack.setType(getRandomStackType());
		stack.setDescription("this is the stacks desciption for stack entered by: " + stack.getUser());
		
		return stack;
	}
	
	public static double getRandomManipulatedDoubleBetween(double upper, double lower)
	{
		double factor = Random.nextDouble();
		
		double max,min;
		if (upper > lower)
		{
			max = upper;
			min = lower;
		}
		else
		{
			max = lower;
			min = upper;
		}
		
		double range = (max - min) /3;
		double randomBit = factor * range;
		return min + randomBit;
	}
	
	public static double getRandomDoubleBetween(double upper, double lower)
	{
		double factor = Random.nextDouble();
		
		double max,min;
		if (upper > lower)
		{
			max = upper;
			min = lower;
		}
		else
		{
			max = lower;
			min = upper;
		}
		
		double range = max - min;
		double randomBit = factor * range;
		return min + randomBit;
	}
	
}
