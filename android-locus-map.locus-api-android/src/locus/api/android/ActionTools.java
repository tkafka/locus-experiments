package locus.api.android;

import java.io.InvalidObjectException;

import locus.api.android.utils.LocusConst;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.RequiredVersionMissingException;
import locus.api.objects.extra.Waypoint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class ActionTools {

	/**************************************************/
	/*                  FILE PICKER                   */
	/**************************************************/
	
	/**
	 * Allow to call activity for File pick. You can use Locus picker for this purpose, but
	 * check if Locus version 231 and above are installed <b>isLocusAvailable(context, 231)</b>!
	 * @param activity
	 * @param id
	 * @throws ActivityNotFoundException
	 */
	public static void actionPickFile(Activity activity, int requestCode) 
			throws ActivityNotFoundException {
		intentPick("org.openintents.action.PICK_FILE",
				activity, requestCode, null, null);
	}
	
	public static void actionPickFile(Activity activity, int requestCode, 
			String title, String[] filter) throws ActivityNotFoundException {
		intentPick("org.openintents.action.PICK_FILE",
				activity, requestCode, title, filter);
	}
	
	public static void actionPickDir(Activity activity, int requestCode) 
			throws ActivityNotFoundException {
		intentPick("org.openintents.action.PICK_DIRECTORY",
				activity, requestCode, null, null);
	}
	
	public static void actionPickDir(Activity activity, int requestCode,
			String title) throws ActivityNotFoundException {
		intentPick("org.openintents.action.PICK_DIRECTORY",
				activity, requestCode, title, null);
	}
	
	private static void intentPick(String action, Activity activity, int requestCode,
			String title, String[] filter) {
		Intent intent = new Intent(action);
		if (title != null && title.length() > 0)
			intent.putExtra("org.openintents.extra.TITLE", title);
		if (filter != null && filter.length > 0)
			intent.putExtra("org.openintents.extra.FILTER", filter);
		activity.startActivityForResult(intent, requestCode);
	}
	
	/**************************************************/
	/*                  BASIC TASKS                   */
	/**************************************************/
	
	public static void actionPickLocation(Activity act) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act)) {
			Intent intent = new Intent(LocusConst.ACTION_PICK_LOCATION);
			act.startActivity(intent);			
		} else {
			throw new RequiredVersionMissingException(LocusUtils.LOCUS_API_SINCE_VERSION);
		}
	}
	
	public static void actionStartNavigation(Activity act, 
			String name, double latitude, double longitude) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act)) {
			Intent intent = new Intent(LocusConst.ACTION_NAVIGATION_START);
			if (name != null)
				intent.putExtra(LocusConst.INTENT_EXTRA_NAME, name);
			intent.putExtra(LocusConst.INTENT_EXTRA_LATITUDE, latitude);
			intent.putExtra(LocusConst.INTENT_EXTRA_LONGITUDE, longitude);
			act.startActivity(intent);			
		} else {
			throw new RequiredVersionMissingException(LocusUtils.LOCUS_API_SINCE_VERSION);
		}
	}
	
	public static void actionStartNavigation(Activity act, Waypoint wpt) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act)) {
			Intent intent = new Intent(LocusConst.ACTION_NAVIGATION_START);
			LocusUtils.addWaypointToIntent(intent, wpt);
			act.startActivity(intent);			
		} else {
			throw new RequiredVersionMissingException(LocusUtils.LOCUS_API_SINCE_VERSION);
		}
	}
	
	public static void actionStartGuiding(Activity act, 
			String name, double latitude, double longitude) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act, 243)) {
			Intent intent = new Intent(LocusConst.ACTION_GUIDING_START);
			if (name != null) {
				intent.putExtra(LocusConst.INTENT_EXTRA_NAME, name);
			}
			intent.putExtra(LocusConst.INTENT_EXTRA_LATITUDE, latitude);
			intent.putExtra(LocusConst.INTENT_EXTRA_LONGITUDE, longitude);
			act.startActivity(intent);			
		} else {
			throw new RequiredVersionMissingException(243);
		}
	}
	
	public static void actionStartGuiding(Activity act, Waypoint wpt) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act, 243)) {
			Intent intent = new Intent(LocusConst.ACTION_GUIDING_START);
			LocusUtils.addWaypointToIntent(intent, wpt);
			act.startActivity(intent);
		} else {
			throw new RequiredVersionMissingException(243);
		}
	}
	
	/**************************************************/
	/*               BROADCAST INTENTS                */
	/**************************************************/
	
	public static void actionTrackRecordStart(Activity act, String packageName) 
			throws RequiredVersionMissingException {
		act.sendBroadcast(actionTrackRecord(act,
				LocusConst.ACTION_TRACK_RECORD_START, packageName));
	}
	
	public static void actionTrackRecordPause(Activity act, String packageName) 
			throws RequiredVersionMissingException {
		act.sendBroadcast(actionTrackRecord(act,
				LocusConst.ACTION_TRACK_RECORD_PAUSE, packageName));
	}
	
	public static void actionTrackRecordStop(Activity act, String packageName, boolean autoSave) 
			throws RequiredVersionMissingException {
		Intent intent = actionTrackRecord(act, 
				LocusConst.ACTION_TRACK_RECORD_STOP, packageName);
		intent.putExtra(LocusConst.INTENT_EXTRA_TRACK_REC_AUTO_SAVE, autoSave);
		act.sendBroadcast(intent);
	}
	
	public static void actionTrackRecordAddWpt(Activity act, String packageName) 
			throws RequiredVersionMissingException {
		act.sendBroadcast(actionTrackRecord(act, 
				LocusConst.ACTION_TRACK_RECORD_ADD_WPT, packageName));
	}
	
	private static Intent actionTrackRecord(Activity act, String action, String packageName) 
			throws RequiredVersionMissingException {
		if (LocusUtils.isLocusAvailable(act, 242)) {
			Intent intent = new Intent(action);
			intent.setPackage(packageName);
			return intent;			
		} else {
			throw new RequiredVersionMissingException(242);
		}
	}
	
	/**************************************************/
	/*                  DATA HANDLING                 */
	/**************************************************/
	
	public static Waypoint getLocusWaypoint(Context context, long wptId) 
			throws RequiredVersionMissingException {
		// generate cursor
		Cursor cursor = null;
		String scheme = getContentProviderScheme(context,
				LocusUtils.LOCUS_API_SINCE_VERSION);
		if (scheme != null) {
			cursor = context.getContentResolver().query(
					Uri.parse(scheme + "waypoint/" + wptId),
					null, null, null, null);
		} else {
			throw new RequiredVersionMissingException(LocusUtils.LOCUS_API_SINCE_VERSION);
		}
		
		// handle result
		try {
			cursor.moveToFirst();
			Waypoint wpt = new Waypoint(cursor.getBlob(1));
			return wpt;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}
	
	/**
	 * Update waypoint in Locus
	 * @param context
	 * @param wpt waypoint to update. Do not modify waypoint's ID value, because it's key to update
	 * @param forceOverwrite if set to <code>true</code>, new waypoint will completely rewrite all
	 *  user's data (do not use if necessary). If set to <code>false</code>, Locus will handle update based on user's 
	 *  settings (if user have defined "keep values", it will keep it)
	 * @return number of affected waypoints
	 * @throws RequiredVersionMissingException
	 */
	public static int updateLocusWaypoint(Context context, Waypoint wpt, boolean forceOverwrite) 
			throws RequiredVersionMissingException {
		// generate cursor
		String scheme = getContentProviderScheme(context,
				LocusUtils.LOCUS_API_SINCE_VERSION);
		if (scheme != null) {
			// define empty cursor
			ContentValues cv = new ContentValues();
			cv.put("waypoint", wpt.getAsBytes());
			cv.put("forceOverwrite", forceOverwrite);
			return context.getContentResolver().update(
					Uri.parse(scheme + "waypoint"), cv, null, null);
		} else {
			throw new RequiredVersionMissingException(LocusUtils.LOCUS_API_SINCE_VERSION);
		}
	}
	
	/**************************************************/
	/*                  WMS FUNCTIONS                 */
	/**************************************************/
	
	/*
	  Add own WMS map
	  ------------------------------------
	  - this feature allow 3rd party application, add web address directly to list of WMS services in
	  Map Manager screen / WMS tab
	 */
	
	public static void callAddNewWmsMap(Context context, String wmsUrl)
			throws RequiredVersionMissingException, InvalidObjectException {
		// check availability and start action
		if (!LocusUtils.isLocusAvailable(context, 217)) {
			throw new RequiredVersionMissingException(217);
		}
		if (TextUtils.isEmpty(wmsUrl)) {
			throw new InvalidObjectException("WMS Url address \'" + wmsUrl + "\', is not valid!");
		}
		
		Intent intent = new Intent(LocusConst.ACTION_ADD_NEW_WMS_MAP);
		intent.putExtra(LocusConst.INTENT_EXTRA_ADD_NEW_WMS_MAP_URL, wmsUrl);
		context.startActivity(intent);
	}
	
	/**************************************************/
	/*                 INFO FUNCTIONS                 */
	/**************************************************/
	
	public static String getLocusRootDirectory(Context context) 
			throws RequiredVersionMissingException {
		String value = getInfoValue(context, 
				LocusUtils.LOCUS_API_SINCE_VERSION, "rootDir");
		return value;
	}
	
	public static boolean isPeriodicUpdatesEnabled(Context context) 
			throws RequiredVersionMissingException {
		String value = getInfoValue(context, 243, "periodicUpdates");
		return value != null && value.equals("1");
	}
	
	private static String getInfoValue(Context ctx, int minVersion, String requiredKey) 
			throws RequiredVersionMissingException {
		// generate cursor
		Cursor cursor = null;
		String scheme = getContentProviderScheme(ctx, minVersion);
		if (scheme != null) {
			cursor = ctx.getContentResolver().query(
					Uri.parse(scheme + "info"),
					null, null, null, null);
		} else {
			throw new RequiredVersionMissingException(minVersion);
		}
				
		// handle result
		try {
			for (int i = 0; i < cursor.getCount(); i++)  {
				cursor.moveToPosition(i);
				String key = cursor.getString(0);
				String value = cursor.getString(1);
				if (key.equals(requiredKey)) {
					return value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}
	
	/**************************************************/
	/*                    VARIOUS                     */
	/**************************************************/
	
	private static String getContentProviderScheme(Context context, int minVersion) {
		if (LocusUtils.isLocusProAvailable(context, minVersion)) {
			return "content://menion.android.locus.pro.LocusDataProvider/";
		} else if (LocusUtils.isLocusFreeAvailable(context, minVersion)) {
			return "content://menion.android.locus.free.LocusDataProvider/";
		}
		return null;
	}
}
