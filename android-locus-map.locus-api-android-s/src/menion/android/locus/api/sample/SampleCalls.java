/*  
 * Copyright 2011, Asamm soft, s.r.o.
 * 
 * This file is part of LocusAddonPublicLibSample.
 * 
 * LocusAddonPublicLibSample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * LocusAddonPublicLibSample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with LocusAddonPublicLibSample.  If not, see <http://www.gnu.org/licenses/>.
 */

package menion.android.locus.api.sample;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import locus.api.android.ActionDisplayPoints;
import locus.api.android.ActionDisplayTracks;
import locus.api.android.ActionDisplayVarious;
import locus.api.android.ActionFiles;
import locus.api.android.ActionTools;
import locus.api.android.objects.PackWaypoints;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.RequiredVersionMissingException;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.ExtraStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.ColorStyle;
import locus.api.objects.extra.ExtraStyle.LineStyle.Units;
import locus.api.objects.extra.Circle;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Track;
import locus.api.objects.extra.Waypoint;
import locus.api.objects.geocaching.GeocachingData;
import locus.api.objects.geocaching.GeocachingWaypoint;
import locus.api.utils.Logger;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;

public class SampleCalls {

	private static final String TAG = "SampleCalls";
	
	private static File getTempGpxFile() {
		return new File("/mnt/sdcard/Locus/_test/temporary_path.gpx");
	}
	
	/**************************************************/
	/*                  POINTS PART                   */
	/**************************************************/
	
	public static void callSendOnePoint(Activity act) {
		try {
			PackWaypoints pw = new PackWaypoints("callSendOnePoint");
			pw.addWaypoint(generateWaypoint(0));
			if (ActionDisplayPoints.sendPack(act, pw, true)) {
				Logger.w(TAG, "waypoint sended succesfully");
			} else {
				Logger.w(TAG, "problem with sending");
			}
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void callSendOnePointWithIcon(Activity act) {
		try {
			PackWaypoints pw = new PackWaypoints("callSendOnePointWithIcon");
			pw.setBitmap(BitmapFactory.decodeResource(
					act.getResources(), R.drawable.ic_launcher));
			pw.addWaypoint(generateWaypoint(0));
			if (ActionDisplayPoints.sendPack(act, pw, false)) {
				Logger.w(TAG, "waypoint sended succesfully");
			} else {
				Logger.w(TAG, "problem with sending");
			}
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/* send more points - LIMIT DATA TO MAX 1000 (really max 1500),
     *  more cause troubles. It easy and fast method but depend on data size, so intent
     *  with lot of geocaches will be really limited */
	public static void callSendMorePoints(Activity act) {
		try {
			PackWaypoints pw = new PackWaypoints("callSendMorePoints");
			for (int i = 0; i < 1000; i++)
				pw.addWaypoint(generateWaypoint(i));
			if (ActionDisplayPoints.sendPack(act, pw, true)) {
				Logger.w(TAG, "waypoint sended succesfully");
			} else {
				Logger.w(TAG, "problem with sending");
			}
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/* similar to previous method. Every PackWaypoints object have defined icon that is
	 * applied on every points. So if you want to send more points with various icons,
	 * you have to define for every pack specific PackWaypoints object */
	public static void callSendMorePointsWithIcons(Activity act) {
		try {
			ArrayList<PackWaypoints> data = new ArrayList<PackWaypoints>();
			
			PackWaypoints pd1 = new PackWaypoints("test01");
			ExtraStyle es1 = new ExtraStyle();
			es1.setIconStyle("http://www.googlemapsmarkers.com/v1/009900/", 1.0f);
			pd1.setExtraStyle(es1);
			for (int i = 0; i < 100; i++)
				pd1.addWaypoint(generateWaypoint(i));
			
			PackWaypoints pd2 = new PackWaypoints("test02");
			ExtraStyle es2 = new ExtraStyle();
			es2.setIconStyle("http://www.googlemapsmarkers.com/v1/990000/", 1.0f);
			pd2.setExtraStyle(es2);
			for (int i = 0; i < 100; i++)
				pd2.addWaypoint(generateWaypoint(i));
			
			data.add(pd1);
			data.add(pd2);
			if (ActionDisplayPoints.sendPacks(act, data, false)) {
				Logger.w(TAG, "waypoint sended succesfully");
			} else {
				Logger.w(TAG, "problem with sending");
			}
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void callSendOnePointGeocache(Activity act) {
		try {
			PackWaypoints pd = new PackWaypoints("callSendOnePointGeocache");
			pd.addWaypoint(generateGeocache(0));

			ActionDisplayPoints.sendPack(act, pd, false);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/* limit here is much more tight! Intent have limit on data size (around 1MB, so if you want to send
     * geocaches, don't rather use this method */
	public static void callSendMorePointsGeocacheIntentMehod(Activity act) {
		try {
			PackWaypoints pw = new PackWaypoints("test6");
			for (int i = 0; i < 100; i++)
				pw.addWaypoint(generateGeocache(i));

			ActionDisplayPoints.sendPack(act, pw, false);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/* allow to display special point, that when shown, will call back to this application. You may use this
	 * for loading extra data. So you send simple point and when show, you display extra information */ 
	public static void callSendOnePointWithCallbackOnDisplay(Activity act) {
		try {
			PackWaypoints pd = new PackWaypoints("test2");
			Waypoint p = generateWaypoint(0);
			p.setExtraOnDisplay(
					"menion.android.locus.api.sample",
					"menion.android.locus.api.sample.MainActivity",
					"myOnDisplayExtraActionId",
					"id01");
			pd.addWaypoint(p);
			
			ActionDisplayPoints.sendPack(act, pd, false);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/**************************************************/
	/*                  TRACKS PART                   */
	/**************************************************/
	
	public static void callSendOneTrack(Activity act) {
		try {
			Track track = generateTrack(50, 15);
			ActionDisplayTracks.sendTrack(act, track, false);
		} catch (RequiredVersionMissingException e) {
			Logger.e(TAG, "callSendOneTrack()", e);
		}
	}
	
	public static void callSendMultipleTracks(Activity act) {
		try {
			ArrayList<Track> tracks = new ArrayList<Track>();
			for (int i = 0; i < 5; i++) {
				Track track = generateTrack(50 - i * 0.1, 15);
				tracks.add(track);
			}
			ActionDisplayTracks.sendTracks(act, tracks, false);
		} catch (RequiredVersionMissingException e) {
			Logger.e(TAG, "callSendOneTrack()", e);
		}
	}
	
	/**************************************************/
	/*                   TOOLS PART                   */
	/**************************************************/
	
	public static void callSendFileToSystem(Activity act) {
		if (ActionFiles.importFileSystem(act, getTempGpxFile())) {
			Logger.w(TAG, "export succesfully");
		} else {
			Logger.w(TAG, "export failed");
		}
	}
	
	public static void callSendFileToLocus(Activity act) {
		if (ActionFiles.importFileLocus(act, getTempGpxFile(), false)) {
			Logger.w(TAG, "export succesfully");
		} else {
			Logger.w(TAG, "export failed");
		}
	}
	
	/* send date with method, that store byte[] data in raw file and send locus link to this file */
	public static void callSendDateOverFile(Activity act) {
		try {
			// get filepath
			File externalDir = Environment.getExternalStorageDirectory();
			if (externalDir == null || !(externalDir.exists())) {
				Logger.e(TAG, "problem with obtain of External dir");
				return;
			}
			
			String filePath = externalDir.getAbsolutePath();
			if (!filePath.endsWith("/"))
				filePath += "/";
			filePath += "Android/data/menion.android.locus.api.sample/files/testFile.locus";

			PackWaypoints pw = new PackWaypoints("test07");
			for (int i = 0; i < 1000; i++)
				pw.addWaypoint(generateGeocache(i));
			
			ArrayList<PackWaypoints> data = new ArrayList<PackWaypoints>();
			data.add(pw);
			
			ActionDisplayPoints.sendPacksFile(act, data, filePath, false);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void pickLocation(Activity act) {
		try {
			ActionTools.actionPickLocation(act);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static String getRootDirectory(Activity act) {
		try {
			return ActionTools.getLocusRootDirectory(act);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void startNavigation(Activity act) {
		try {
			ActionTools.actionStartNavigation(act, 
					generateWaypoint(1));
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void trackRecordStart(Activity act) {
		try {
			ActionTools.actionTrackRecordStart(act,
					LocusUtils.getLocusPackageName(act));
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void trackRecordPause(Activity act) {
		try {
			ActionTools.actionTrackRecordPause(act,
					LocusUtils.getLocusPackageName(act));
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}

	public static void trackRecordStop(Activity act) {
		try {
			ActionTools.actionTrackRecordStop(act,
					LocusUtils.getLocusPackageName(act), false /* no autosave */);
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void trackRecordAddWpt(Activity act) {
		try {
			ActionTools.actionTrackRecordAddWpt(act,
					LocusUtils.getLocusPackageName(act));
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	public static void showCircles(FragmentActivity activity) {
		try {
			ArrayList<Circle> circles = new ArrayList<Circle>();

			Circle c0 = new Circle(new Location("c1", 50.15, 15), 10000000, true);
			c0.styleNormal = new ExtraStyle();
			c0.styleNormal.setPolyStyle(Color.argb(50, Color.red(Color.RED), 
					Color.green(Color.RED), Color.blue(Color.RED)), true, true);
			circles.add(c0);
			
			Circle c1 = new Circle(new Location("c1", 50, 15), 1000);
			c1.styleNormal = new ExtraStyle();
			c1.styleNormal.setLineStyle(Color.BLUE, 2);
			circles.add(c1);
			
			Circle c2 = new Circle(new Location("c2", 50.1, 15), 1500);
			c2.styleNormal = new ExtraStyle();
			c2.styleNormal.setLineStyle(Color.RED, 3);
			circles.add(c2);
			
			Circle c3 = new Circle(new Location("c1", 50.2, 15), 2000);
			c3.styleNormal = new ExtraStyle();
			c3.styleNormal.setLineStyle(Color.GREEN, 4);
			c3.styleNormal.setPolyStyle(Color.LTGRAY, true, true);
			circles.add(c3);
			
			Circle c4 = new Circle(new Location("c1", 50.3, 15), 1500);
			c4.styleNormal = new ExtraStyle();
			c4.styleNormal.setLineStyle(Color.MAGENTA, 0);
			c4.styleNormal.setPolyStyle(
					Color.argb(100, Color.red(Color.MAGENTA), 
							Color.green(Color.MAGENTA), Color.blue(Color.MAGENTA)),
					true, true);
			circles.add(c4);
			
			ActionDisplayVarious.sendCirclesSilent(activity, circles);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RequiredVersionMissingException e) {
			e.printStackTrace();
		}
	}
	
	/**************************************************/
	/*                 PRIVATE TOOLS                  */
	/**************************************************/
	
    protected static Waypoint generateWaypoint(int id) {
		// create one simple point with location
		Location loc = new Location(TAG);
		loc.setLatitude(Math.random() + 50.0);
		loc.setLongitude(Math.random() + 14.0);
		return new Waypoint("Testing point - " + id, loc);
    }
    
    protected static Waypoint generateGeocache(int id) {
    	Waypoint wpt = generateWaypoint(id);
    	
    	// generate new data
    	GeocachingData gcData = new GeocachingData();
    	
    	// fill data with variables
    	gcData.setCacheID("GC2Y0RJ"); // REQUIRED
    	gcData.setName("Kokotín"); // REQUIRED
    	
    	// rest is optional so fill as you want - should work
    	gcData.type = (int) (Math.random() * 13);
    	gcData.setOwner("Menion1");
    	gcData.setPlacedBy("Menion2");
    	gcData.difficulty = 3.5f;
    	gcData.terrain = 3.5f;
    	gcData.setContainer(GeocachingData.CACHE_SIZE_LARGE);
    	gcData.setShortDescription("bla bla, this is some short description also with <b>HTML tags</b>", true);
    	String longDesc = "";
    	for (int i = 0; i < 5; i++) {
    		longDesc += "Oh, what a looooooooooooooooooooooooong description, never imagine it could be sooo<i>oooo</i>long!<br /><br />Oh, what a looooooooooooooooooooooooong description, never imagine it could be sooo<i>oooo</i>long!";
    	}
    	gcData.setLongDescription(longDesc, false);
    	
    	// add one waypoint
    	GeocachingWaypoint gcWpt = new GeocachingWaypoint();
    	gcWpt.type = GeocachingWaypoint.CACHE_WAYPOINT_TYPE_PARKING;
    	gcWpt.desc = "Description of waypoint";
    	gcWpt.name = "Just an waypoint";
    	gcWpt.lat = Math.random() + 50.0;
    	gcWpt.lon = Math.random() + 14.0;
    	gcData.waypoints.add(gcWpt);
    	
    	// set data and return point
    	wpt.gcData = gcData;
    	return wpt;
    }
    
    private static Track generateTrack(double startLat, double startLon) {
		Track track = new Track();
		track.setName("track from API (" + startLat + "|" + startLon + ")");
		track.addParameter(ExtraData.PAR_DESCRIPTION, "simple track bla bla bla ...");
		
		// set style
		ExtraStyle style = new ExtraStyle();
		style.setLineStyle(ColorStyle.SIMPLE, Color.CYAN, 7.0f, Units.PIXELS);
		track.styleNormal = style;
		
		// generate points
		double lat = startLat;
		double lon = startLon;
		ArrayList<Location> locs = new ArrayList<Location>();
		for (int i = 0; i < 1000; i++) {
			lat += ((Math.random() - 0.5) * 0.01);
			lon += (Math.random() * 0.001);
			Location loc = new Location(TAG);
			loc.setLatitude(lat);
			loc.setLongitude(lon);
			locs.add(loc);
		}
		track.setPoints(locs);
		
		// set some points as highlighted wpts
		ArrayList<Waypoint> wpts = new ArrayList<Waypoint>();
		wpts.add(new Waypoint("p1", locs.get(100)));
		wpts.add(new Waypoint("p2", locs.get(300)));
		wpts.add(new Waypoint("p3", locs.get(800)));
		track.setWaypoints(wpts);
		return track;
    }
}
