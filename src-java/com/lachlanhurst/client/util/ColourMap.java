package com.lachlanhurst.client.util;

import com.google.gwt.core.client.GWT;

/**
 * nice little util class to help get a colur scale/key from
 * given values.  Will be used by the intensity plot
 * @author lachlan
 *
 */
public class ColourMap 
{
	/*public static final String[] COLOURS = {
		"#FFFF00",
		"#FFEE00",
		"#FFDD00",
		"#FFCC00",
		"#FFBB00",
		"#FFAA00",
		"#FF9900",
		"#FF8800",
		"#FF7700",
		"#FF6600",
		"#FF5500",
		"#FF4400",
		"#FF3300",
		"#FF2200",
		"#FF1100",
		"#FF0000",
	};*/
	
	public static final String[] COLOURS = {
		"#00FF00",
		"#FFCC00",
		"#FF6600",
		"#FF0000"
	};
	
	/**
	 * returns a HTML hexadecimal colour from the colours array base on the
	 * fraction of value/max.
	 * @param value
	 * @param max
	 * @return
	 */
	public static String getColour(int value, int max)
	{
		//GWT.log("value = " + value + " max = " + max, null);
		double fraction = (((double)value)/((double)max));
		int index = (int)(fraction * (COLOURS.length-1)); 
		return COLOURS[index];
	}
	
}
