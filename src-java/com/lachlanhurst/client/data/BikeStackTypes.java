package com.lachlanhurst.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.lachlanhurst.client.data.images.BikeStackImageBundle;

public class BikeStackTypes 
{
	
	private static final String[] NAMES = {"Dooring",
		                                  "Mechanical failure",
		                                  "Hit from behind",
		                                  "Hit from head on",
		                                  "Hit by turning vehicle",
		                                  "Road surface hazard",
		                                  "Pedestrian",
		                                  "Stationary object",
		                                  "Bicycle ran red",
                                          "Vehcile ran red",
                                          "Bicycle failed to stop",
                                          "Vehicle failed to stop",
                                          "Bicycle turned into vehicle",
                                          "Toe-clips / clipless pedals",
                                          "Pissed and Fell Over",
                                          "Light/Heavy railway tracks"
	                                     };
	
	private static final String[] DESCRIPTIONS = {
		"Car door opened into path of oncoming cyclist",
        "Bicycle suffered mechanical failure causing accident (chain came off, forks snapped, etc)",
        "Vehicle travelling in same direction hit cyclist from behind",
        "Vehicle travelling in oposite direction hit cyclist head on",
        "Vehicle turning across path of cyclist",
        "Accident caused by a hazard on the road surface (pothole, light rail tracks, etc)",
        "Accident involving a pedestrian",
        "Cyclist rode into a stationary object (tree, car, house, etc)",
        "Cyclist rode through red light, failed to stop causing accident",
        "Vehcile drove through red light, failed to stop causing accident",
        "Cyclist failed to yield at stop sign",
        "Vehicle failed to yield at stop sign",
        "Bicycle turned in front of vehicle",
        "Inability to release feet from pedals resulted in cyclist accident",
        "Consumption of excessive alcohol and/or drugs caused accident",
        "Bicycle tyres got caught in or slipped on a light/heavy rail line"
       };
	
	private static final String NAME_OTHER = "Other";
	private static final String DESCRIPTION_OTHER = "Please provide optional description";
	private static final String ICON_NAME_OTHER = "stOther";
	private static int TYPE_ID_OTHER = -1;
	
	private static final String NAME_LOW_RES = "Multiple Bingles";
	private static final String DESCRIPTION_LOW_RES = "Please provide optional description";
	private static final String ICON_NAME_LOW_RES = "stLowRes";
	private static int TYPE_ID_LOW_RES = -2;
	
	private static final String ICON_ROOT_DIR = "stackTypeIcons";
	
	private static final String[] ICON_NAMES_LIST = {
		                                             "stDooring",
		                                             "stMechanical",
		                                             "stVehicleBehind",
		                                             "stVehicleHeadOn",
		                                             "stVehicleTurning",
		                                             "stRoadHazard",
		                                             "stPedestrian",
		                                             "stStationary",
		                                             "stBikeRedLight",
		                                             "stCarRedLight",
		                                             "stBikeStopSign",
		                                             "stCarStopSign",
		                                             "stBikeTurning",
		                                             "stClippedIn",
		                                             "stPissedAndFellOver",
		                                             "stRainLines"
		                                            };
	
	public static final String ICON_RES_S = "20";
	public static final String ICON_RES_L = "40";
	public static final String ICON_RES = "60";
	
	private Icon[] _iconsSmall = null;
	private Icon _iconSmallOther = null;
	private Icon _iconSmallLowRes = null;
	
	private Icon[] _iconsLarge = null;
	private Icon _iconLargeOther = null;
	private Icon _iconLargeLowRes = null;
	
	private BikeStackImageBundle _bikeStacksImageBundle = null;
	
	public BikeStackTypes()
	{
		//only load the small icons to start off with, the large ones might not be required
		//so load them later only if necessary.
		loadSmallIcons();
		loadImages();
	}
	
	protected void loadSmallIcons()
	{
		Point small = Point.newInstance(10, 10);
		_iconSmallOther = Icon.newInstance(getUrlForImage(ICON_NAME_OTHER, ICON_RES_S));
		_iconSmallOther.setIconAnchor(small);
		_iconSmallLowRes = Icon.newInstance(getUrlForImage(ICON_NAME_LOW_RES, ICON_RES_S));
		_iconSmallLowRes.setIconAnchor(small);
		_iconsSmall = new Icon[ICON_NAMES_LIST.length];
		for (int i = 0; i < ICON_NAMES_LIST.length; i++)
		{
			_iconsSmall[i] = Icon.newInstance(getUrlForImage(ICON_NAMES_LIST[i], ICON_RES_S));
			_iconsSmall[i].setIconAnchor(small);
		}
	}
	
	protected void loadLargeIcons()
	{
		Point large = Point.newInstance(20, 20);
		_iconLargeOther = Icon.newInstance(getUrlForImage(ICON_NAME_OTHER, ICON_RES_L));
		_iconLargeOther.setIconAnchor(large);
		_iconLargeLowRes = Icon.newInstance(getUrlForImage(ICON_NAME_LOW_RES, ICON_RES_L));
		_iconLargeLowRes.setIconAnchor(large);
		_iconsLarge = new Icon[ICON_NAMES_LIST.length];
		for (int i = 0; i < ICON_NAMES_LIST.length; i++)
		{
			_iconsLarge[i] = Icon.newInstance(getUrlForImage(ICON_NAMES_LIST[i], ICON_RES_L));
			_iconsLarge[i].setIconAnchor(large);
		}
	}
	
	protected void loadImages()
	{
		_bikeStacksImageBundle = (BikeStackImageBundle) GWT.create(BikeStackImageBundle.class);

		
	}
	
	/**
	 * gets the list of type names
	 * @return list of strings
	 */
	public List getListOfTypeNames()
	{
		List l = new ArrayList();
		for (int i = 0; i < NAMES.length; i++)
		{
			l.add(NAMES[i]);
		}
		l.add(NAME_OTHER);
		return l;
	}
	
	/**
	 * gets the number of types
	 * @return
	 */
	public int getTypeCount()
	{
		//the length of the names array plus 1 (for the other catagory)
		return NAMES.length + 1;
	}
	
	/**
	 * calculates the id from the provided index
	 * @param index
	 */
	public int getIdFromIndex(int index)
	{
		if (index < NAMES.length)
		{
			return index;
		}
		else if (index == NAMES.length)
		{
			return TYPE_ID_OTHER;
		}
		else
		{
			throw new RuntimeException("Unexpected/unsupported index provided");
		}
	}
	
	/**
	 * gets the type name for the given type id.
	 * @param index
	 * @return
	 */
	public String getTypeNameById(int id)
	{
		if (id == TYPE_ID_OTHER)
		{
			return NAME_OTHER;
		}
		else if (id == TYPE_ID_LOW_RES)
		{
			return NAME_LOW_RES;
		}
		else if (id < NAMES.length)
		{
			return NAMES[id];
		}
		else
		{
			throw new RuntimeException("could not get type for id " + Integer.toString(id));
		}
	}
	
	public String getTypeDescriptionById(int id)
	{
		if (id == TYPE_ID_OTHER)
		{
			return DESCRIPTION_OTHER;
		}
		else if (id == TYPE_ID_LOW_RES)
		{
			return DESCRIPTION_LOW_RES;
		}
		else if (id < NAMES.length)
		{
			return DESCRIPTIONS[id];
		}
		else
		{
			throw new RuntimeException("could not get description for id " + Integer.toString(id));
		}
	}
	
	/**
	 * gets the appropriate icon for the given id.
	 * @param id id of the bingle type to get the icon for
	 * @param smallIcon if true a small icon will be returned, if false a large icon
	 * @return
	 */
	public Icon getIconById(int id, boolean smallIcon)
	{
		if (smallIcon)
		{
			if (id == TYPE_ID_OTHER)
			{
				return _iconSmallOther;
			}
			else if (id == TYPE_ID_LOW_RES)
			{
				return _iconSmallLowRes;
			}
			else if (id < NAMES.length)
			{
				return _iconsSmall[id];
			}
			else
			{
				throw new RuntimeException("could not get small icon for id " + Integer.toString(id));
			}
		}
		else
		{
			if (_iconLargeOther == null)
			{
				loadLargeIcons();
			}
			if (id == TYPE_ID_OTHER)
			{
				return _iconLargeOther;
			}
			else if (id == TYPE_ID_LOW_RES)
			{
				return _iconLargeLowRes;
			}
			else if (id < NAMES.length)
			{
				return _iconsLarge[id];
			}
			else
			{
				throw new RuntimeException("could not get large icon for id " + Integer.toString(id));
			}
		}
	}
	
	public Image getImageById(int id)
	{
		if (id == TYPE_ID_OTHER)
		{
			return _bikeStacksImageBundle.stOther_60().createImage();
		}
		else if (id == TYPE_ID_LOW_RES)
		{
			return _bikeStacksImageBundle.stLowRes_60().createImage();
		}
		else if (id < NAMES.length)
		{
			switch (id) {
			case 0:
				return _bikeStacksImageBundle.stDooring_60().createImage();
			case 1:
				return _bikeStacksImageBundle.stMechanical_60().createImage();
			case 2:
				return _bikeStacksImageBundle.stVehicleBehind_60().createImage();
			case 3:
				return _bikeStacksImageBundle.stVehicleHeadOn_60().createImage();
			case 4:
				return _bikeStacksImageBundle.stVehicleTurning_60().createImage();
			case 5:
				return _bikeStacksImageBundle.stRoadHazard_60().createImage();
			case 6:
				return _bikeStacksImageBundle.stPedestrian_60().createImage();
			case 7:
				return _bikeStacksImageBundle.stStationary_60().createImage();
			case 8:
				return _bikeStacksImageBundle.stBikeRedLight_60().createImage();
			case 9:
				return _bikeStacksImageBundle.stCarRedLight_60().createImage();
			case 10:
				return _bikeStacksImageBundle.stBikeStopSign_60().createImage();
			case 11:
				return _bikeStacksImageBundle.stCarStopSign_60().createImage();
			case 12:
				return _bikeStacksImageBundle.stBikeTurning_60().createImage();
			case 13:
				return _bikeStacksImageBundle.stClippedIn_60().createImage();
			case 14:
				return _bikeStacksImageBundle.stPissedAndFellOver_60().createImage();
			case 15:
				return _bikeStacksImageBundle.stRainLines_60().createImage();
			default:
				throw new RuntimeException("could not get image for id " + Integer.toString(id));
			}
			
		}
		else
		{
			throw new RuntimeException("could not get image for id " + Integer.toString(id));
		}
	}
	
	protected String getUrlForImage(String name, String size)
	{
		return ICON_ROOT_DIR + "/" + name + "_" + size + ".png"; 
	}

}
