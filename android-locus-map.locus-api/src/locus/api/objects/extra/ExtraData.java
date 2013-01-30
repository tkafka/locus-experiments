/*  
 * Copyright 2012, Asamm Software, s. r. o.
 * 
 * This file is part of LocusAPI.
 * 
 * LocusAPI is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *  
 * LocusAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *  
 * You should have received a copy of the Lesser GNU General Public
 * License along with LocusAPI. If not, see 
 * <http://www.gnu.org/licenses/lgpl.html/>.
 */

package locus.api.objects.extra;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import locus.api.objects.Storable;
import locus.api.utils.Logger;

public class ExtraData extends Storable {

	private static final String TAG = "ExtraData";

	/** source unknown or undefined */
	public static final int SOURCE_UNKNOWN = 0;
	/** special point for parking service */
	public static final int SOURCE_PARKING_SERVICE = 1;
	/** additional waypoint for geocache */
	public static final int SOURCE_GEOCACHING_WAYPOINT = 2;
	/** temporary point on map (not stored in database) */
	public static final int SOURCE_MAP_TEMP = 3;
	/** waypoint on route, location with some more values */
	public static final int SOURCE_ROUTE_WAYPOINT = 4;
	/** only location on route */
	public static final int SOURCE_ROUTE_LOCATION = 5;
	/** point coming from My Maps source */
	public static final int SOURCE_MY_MAPS = 6;
	/** point coming from OpenStreetBugs */
	public static final int SOURCE_OPENSTREETBUGS = 7;
	
	// PRIVATE REFERENCES (0 - 29)
	
	public static final int PAR_SOURCE = 0;
	public static final int PAR_MAP_ID = 1;
	public static final int PAR_MAP_EDIT_URI = 2;
	public static final int PAR_GOOGLE_PLACES_ID = 3;
	public static final int PAR_GOOGLE_PLACES_REFERENCE = 4;
	public static final int PAR_STYLE_NAME = 5;
	
	public static final int PAR_EXTRA_01 = 10;
	public static final int PAR_EXTRA_02 = 11;
	public static final int PAR_AREA_SIZE = 12;
	
	public static final int PAR_INTENT_EXTRA_CALLBACK = 20;
	public static final int PAR_INTENT_EXTRA_ON_DISPLAY = 21;

	// PUBLIC VALUES (30 - 49)
	
	// visible description
	public static final int PAR_DESCRIPTION = 30;
	// storage for comments
	public static final int PAR_COMMENT = 31;

	// LOCATION PARAMETERS (50 - 59)
	
	public static final int PAR_STREET = 50;
	public static final int PAR_CITY = 51;
	public static final int PAR_REGION = 52;
	public static final int PAR_POST_CODE = 53;
	public static final int PAR_COUNTRY = 54;
	
	// ROUTE PARAMETERS (100 - 199)
	
	/**
	 * Index to point list 
	 * <br />
	 * Locus internal variable, DO NOT SET
	 */
	public static final int PAR_RTE_INDEX = 100;
	/**
	 * Distance (in metres) from current navPoint to next 
	 * <br />
	 * Locus internal variable, DO NOT SET
	 */
	public static final int PAR_RTE_DISTANCE = 101;
	/**
	 * time (in sec) from current navPoint to next
	 */
	public static final int PAR_RTE_TIME = 102;
	/** 
	 * speed (in m/s) from current navPoint to next 
	 */
	public static final int PAR_RTE_SPEED = 103;
	/** 
	 *  Number of seconds to transition between successive links along
	 *  the route. These take into account the geometry of the intersection,
	 *  number of links at the intersection, and types of roads at
	 *  the intersection. This attempts to estimate the time in seconds it
	 *  would take for stops, or places where a vehicle must slow to make a turn.
	 */
	public static final int PAR_RTE_TURN_COST = 104;
	/**
	 * String representation of next street label
	 */
	public static final int PAR_RTE_STREET = 109;
	/**
	 * used to determine which type of action should be taken in order to stay on route </br></br>
	 * <b>0</b> - No maneuver occurs here.</br>
	 * <b>1</b> - Continue straight.</br>
	 * <b>2</b> - No maneuver occurs here. Road name changes.</br>
	 * <b>3</b> - Make a slight left.</br>
	 * <b>4</b> - Turn left.</br>
	 * <b>5</b> - Make a sharp left.</br>
	 * <b>6</b> - Make a slight right.</br>
	 * <b>7</b> - Turn right.</br>
	 * <b>8</b> - Make a sharp right.</br>
	 * <b>9</b> - Stay left.</br>
	 * <b>10</b> - Stay right.</br>
	 * <b>11</b> - Stay straight.</br>
	 * <b>12</b> - Make a U-turn.</br>
	 * <b>13</b> - Make a left U-turn.</br>
	 * <b>14</b> - Make a right U-turn.</br>
	 * <b>15</b> - Exit left.</br>
	 * <b>16</b> - Exit right.</br>
	 * <b>17</b> - Take the ramp on the left.</br>
	 * <b>18</b> - Take the ramp on the right.</br>
	 * <b>19</b> - Take the ramp straight ahead.</br>
	 * <b>20</b> - Merge left.</br>
	 * <b>21</b> - Merge right.</br>
	 * <b>22</b> - Merge.</br>
	ENTERING 	23 	Enter state/province.</br>
	 * <b>24</b> - Arrive at your destination.</br>
	 * <b>25</b> - Arrive at your destination on the left.</br>
	 * <b>26</b> - Arrive at your destination on the right.</br>
	 * <b>27</b> - Enter the roundabout and take the 1st exit.</br>
	 * <b>28</b> - Enter the roundabout and take the 2nd exit.</br>
	 * <b>29</b> - Enter the roundabout and take the 3rd exit.</br>
	 * <b>30</b> - Enter the roundabout and take the 4th exit.</br>
	 * <b>31</b> - Enter the roundabout and take the 5th exit.</br>
	 * <b>32</b> - Enter the roundabout and take the 6th exit.</br>
	 * <b>33</b> - Enter the roundabout and take the 7th exit.</br>
	 * <b>34</b> - Enter the roundabout and take the 8th exit.</br>
	TRANSIT_TAKE 	35 	Take a public transit bus or rail line.</br>
	TRANSIT_TRANSFER 	36 	Transfer to a public transit bus or rail line.</br>
	TRANSIT_ENTER 	37 	Enter a public transit bus or rail station</br>
	TRANSIT_EXIT 	38 	Exit a public transit bus or rail station</br>
	TRANSIT_REMAIN_ON 	39 	Remain on the current bus/rail car</br>
	 * <b>50</b> - Pass POI */
	public static final int PAR_RTE_POINT_ACTION = 110;
	/** type of route (car_fast, car_short, cyclo, foot) */
	public static final int PAR_RTE_COMPUTE_TYPE = 120;
	
	// OSM BUGS (300 - 309)
	
	public static final int PAR_OSM_BUGS_ID = 301;
	public static final int PAR_OSM_BUGS_CLOSED = 302;
	
	// PHONES (1000 - 1099)
	
	private static final int PAR_PHONES_MIN = 1000;
	private static final int PAR_PHONES_MAX = 1099;
	
	// EMAILS (1100 - 1199)
	
	private static final int PAR_EMAILS_MIN = 1100;
	private static final int PAR_EMAILS_MAX = 1199;
	
	// URLS (1200 - 1299)
	
	private static final int PAR_URLS_MIN = 1200;
	private static final int PAR_URLS_MAX = 1299;
	
	// PHOTOS (1300 - 1399)
	
	private static final int PAR_PHOTOS_MIN = 1300;
	private static final int PAR_PHOTOS_MAX = 1399;
	
	// OTHER FILES (1800 - 1999)
	private static final int PAR_OTHER_FILES_MIN = 1800;
	private static final int PAR_OTHER_FILES_MAX = 1999;
	
	/** table for additional parameters */
	Hashtable<Integer, String> parameters;
	
	public ExtraData() {
		super();
	}
	
	public ExtraData(DataInputStream dis) throws IOException {
		super(dis);
	}
	
	public ExtraData(byte[] data) throws IOException {
		super(data);
	}
	
    /*******************************************/
    /*             STORABLE PART               */
    /*******************************************/
	
	@Override
	protected int getVersion() {
		return 0;
	}

	@Override
	protected void readObject(int version, DataInputStream dis)
			throws IOException {
		int size = dis.readInt();
		parameters.clear();
		for (int i = 0; i < size; i++) {
			Integer key = dis.readInt();
			String value = readStringUTF(dis);
			parameters.put(key, value);
		}
	}

	@Override
	protected void writeObject(DataOutputStream dos) throws IOException {
		dos.writeInt(parameters.size());
		Enumeration<Integer> keys = parameters.keys();
		while (keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			dos.writeInt(key);
			writeStringUTF(dos, parameters.get(key));
		}
	}

	@Override
	public void reset() {
		parameters = new Hashtable<Integer, String>();
	}
	
    /*******************************************/
    /*             HANDLERS PART               */
    /*******************************************/
	
	public boolean addParameter(int key, String value) {
//Logger.d(TAG, "addParameter(" + key + ", " + value + ")");
		// check on 'null' value
		if (value == null) {
			return false;
		}
		// remove previous parameter
		removeParameter(key);
		
		// trim new value and insert into table
		value = value.trim();
		if (value.length() == 0) {
			return false;
		}
		if (key > 1000 && key < 2000) {
			Logger.e(TAG, "addParam(" + key + ", " + value + "), values 1000 - 1999 reserved!");
			return false;
		}
		parameters.put(key, value);
		return true;
	}
	
	public String getParameter(int key) {
		return parameters.get(key);
	}
	
	public boolean hasParameter(int key) {
		return parameters.get(key) != null;
	}
	
	public String removeParameter(int key) {
		return parameters.remove(key);
	}
	
	public int getCount() {
		return parameters.size();
	}
	
	// PHONE
	
	public boolean addPhone(String phone) {
		return addToStorage(phone, PAR_PHONES_MIN, PAR_PHONES_MAX);
	}
	
	public ArrayList<String> getPhones() {
		return getFromStorage(PAR_PHONES_MIN, PAR_PHONES_MAX);
	}
	
	public boolean removePhone(String phone) {
		return removeFromStorage(phone, PAR_PHONES_MIN, PAR_PHONES_MAX);
	}
	
	// EMAIL
	
	public boolean addEmail(String email) {
		return addToStorage(email, PAR_EMAILS_MIN, PAR_EMAILS_MAX);
	}
	
	public ArrayList<String> getEmails() {
		return getFromStorage(PAR_EMAILS_MIN, PAR_EMAILS_MAX);
	}
	
	public boolean removeEmail(String email) {
		return removeFromStorage(email, PAR_EMAILS_MIN, PAR_EMAILS_MAX);
	}
	
	// URL
	
	public boolean addUrl(String url) {
		return addToStorage(url, PAR_URLS_MIN, PAR_URLS_MAX);
	}
	
	public ArrayList<String> getUrls() {
		return getFromStorage(PAR_URLS_MIN, PAR_URLS_MAX);
	}
	
	public boolean removeUrl(String url) {
		return removeFromStorage(url, PAR_URLS_MIN, PAR_URLS_MAX);
	}
	
	// PHOTO
	
	public boolean addPhoto(String photo) {
		return addToStorage(photo, PAR_PHOTOS_MIN, PAR_PHOTOS_MAX);
	}
	
	public ArrayList<String> getPhotos() {
		return getFromStorage(PAR_PHOTOS_MIN, PAR_PHOTOS_MAX);
	}
	
	public boolean removePhoto(String photo) {
		return removeFromStorage(photo, PAR_PHOTOS_MIN, PAR_PHOTOS_MAX);
	}

	// OTHER FILES
	
	public boolean addOtherFile(String filpath) {
		return addToStorage(filpath, PAR_OTHER_FILES_MIN, PAR_OTHER_FILES_MAX);
	}
	
	public ArrayList<String> getOtherFiles() {
		return getFromStorage(PAR_OTHER_FILES_MIN, PAR_OTHER_FILES_MAX);
	}
	
	public boolean removeOtherFile(String filpath) {
		return removeFromStorage(filpath, PAR_OTHER_FILES_MIN, PAR_OTHER_FILES_MAX);
	}
	
	// PRIVATE TOOLS

	private boolean addToStorage(String item, int rangeFrom, int rangeTo) {
		if (item == null || item.length() == 0)
			return false;
		
		for (int i = rangeFrom; i <= rangeTo; i++) {
			String value = parameters.get(i);
			if (value == null) {
				parameters.put(i, item);
				return true;
			} else if (value.equalsIgnoreCase(item)) {
				// item already exists
				return false;
			} else {
				// some other item already included, move to next index
			}
		}
		return false;
	}
	
	private ArrayList<String> getFromStorage(int rangeFrom, int rangeTo) {
		ArrayList<String> data = new ArrayList<String>();
		for (int i = rangeFrom; i <= rangeTo; i++) {
			String value = parameters.get(i);
			if (value != null) {
				data.add(value);
			}
		}
		return data;
	}
	
	private boolean removeFromStorage(String item, int rangeFrom, int rangeTo) {
		if (item == null || item.length() == 0)
			return false;
		
		for (int i = rangeFrom; i <= rangeTo; i++) {
			String value = parameters.get(i);
			if (value == null) {
				// no item
			} else if (value.equalsIgnoreCase(item)) {
				parameters.remove(i);
				return true;
			} else {
				// some other item already included, move to next index
			}
		}
		return false;
	}
}
