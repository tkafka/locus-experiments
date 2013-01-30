package locus.api.android;

import static locus.api.android.utils.PeriodicUpdatesConst.*;

import locus.api.android.utils.LocusUtils;
import locus.api.objects.extra.Location;
import locus.api.utils.Utils;
import android.content.Context;
import android.content.Intent;

public class PeriodicUpdate {

	// private temporary variables for checking changes
	private Location mLastMapCenter;
	private Location mLastGps;
	private int mLastZoomLevel;
	
	// checker for new location
	private double mLocMinDistance;
	
	// instance
	private static PeriodicUpdate mPU;
	public static PeriodicUpdate getInstance() {
		if (mPU == null) {
			mPU = new PeriodicUpdate();
		}
		return mPU;
	}
	
	private PeriodicUpdate() {
		this.mLastZoomLevel = -1;
		this.mLocMinDistance = 1.0;
	}
	
	/**
	 * Set notification limit used for check if distance between previous and
	 * new location is higher than this value. So new locations is market as NEW 
	 * @param minDistance distance in metres
	 */
	public void setLocNotificationLimit(double locMinDistance) {
		this.mLocMinDistance = locMinDistance;
	}
	
	public interface OnUpdate {
		
		public void onUpdate(UpdateContainer update);
		
		public void onIncorrectData();
	}
	
	public class UpdateContainer {

		// STATE CUSTOM VARIABLES
		
		// is new GPS location available
		public boolean newMyLocation = false;
		// is new map center available
		public boolean newMapCenter = false;
		// is new zoom level on map
		public boolean newZoomLevel = false;
		/**
		 * Indicate if user is currently touching a map screen. It do not indicate
		 * which specific action is doing, only that something is happening.
		 */
		public boolean isUserTouching = false;
		
		// LOCATION, GPS, BASIC VALUES
		
		/**
		 * Is "My location" enabled
		 */
		public boolean enabledMyLocation = false;
		// last GPS location
		public Location locMyLocation = null;
		public int gpsSatsUsed = 0;
		public int gpsSatsAll = 0;
		public float declination = 0.0f;
		public float orientCourse = 0.0f;
		public float orientCourseOpposit = 0.0f;
		public float orientPitch = 0.0f;
		public float orientRoll = 0.0f;
		public float orientGpsShift = 0.0f;
		public float orientGpsAngle = 0.0f;
		
		// MAP STUFF
		
		// is map currently visible
		public boolean mapVisible = false;
		public float mapRotate = 0.0f;
		// last map center location
		public Location locMapCenter = null;
		// location of top-left map corner
		public Location mapTopLeft = null;
		// location of bottom-right map corner
		public Location mapBottomRight = null;
		// current map zoom level (zoom 8 == whole world (1 tile 256x256px))
		public int mapZoomLevel = -1;
		
		// TRACK RECORDING PART
		
		// is track recording enabled
		public boolean trackRecRecording = false;
		// if track rec is enabled, is running or paused
		public boolean trackRecPaused = false;
		// already recorded distance in metres
		public double trackRecDist = 0.0;
		public double trackRecDistDownhill = 0.0;
		public double trackRecDistUphill = 0.0;
		public float trackRecAltMin = 0.0f;
		public float trackRecAltMax = 0.0f;
		public float trackRecAltDownhill = 0.0f;
		public float trackRecAltUphill = 0.0f;
		public float trackRecAltCumulative = 0.0f;
		// already recorded times in ms
		public long trackRecTime = 0L;
		public long trackRecTimeMove = 0L;
		public float trackRecSpeedAvg = 0.0f;
		public float trackRecSpeedAvgMove = 0.0f;
		public float trackRecSpeedMax = 0.0f;
		// already recorded points
		public int trackRecPoints = 0;
		
		// GUIDING PART

		public boolean guidingEnabled = false;
		public String guideWptName = null;
		public Location guideWptLoc = null;
		public double guideWptDist = 0.0;
		public double guideWPtDistToFinish = 0.0;
		public float guideWptAzim = 0.0f;
		public float guideWptAngle = 0.0f;
		public long guideWptTime = 0L;
		public long guideWptTimeToFinish = 0L;
		
		@Override
		public String toString() {
			return Utils.toString(this);
		}
	}
	
	public void onReceive(final Context context, Intent i, OnUpdate handler) {
		if (context == null || i == null || handler == null)
			throw new IllegalArgumentException("Incorrect arguments");
		
//		if (!i.getAction().equals(LocusConst.ACTION_PERIODIC_UPDATE)) {
//			handler.onIncorrectData();
//			return;
//		}
		
		// print content of object, for debug only
//		Bundle extra = intent.getExtras();
//		Iterator<String> keys = extra.keySet().iterator();
//		while (keys.hasNext()) {
//			String key = keys.next();
//			Object object = extra.get(key);
//			Log.w("INFO", "key:" + key + ", obj:" + object);	
//		}

		UpdateContainer update = new UpdateContainer();
		
		// LOCATION, GPS, BASIC VALUES
		
		// check current GPS/network location
		update.newMyLocation = false;
		update.enabledMyLocation = i.getBooleanExtra(
				VAR_B_MY_LOCATION_ON, false);
		if (update.enabledMyLocation) {
			update.locMyLocation = LocusUtils.getLocationFromIntent(
					i, VAR_LOC_MY_LOCATION);
			if (mLastGps == null || mLastGps.distanceTo(
					update.locMyLocation) > mLocMinDistance) {
				mLastGps = update.locMyLocation;
				update.newMyLocation = true;
			} 
		}
		
		// get basic variables
		update.gpsSatsUsed = i.getIntExtra(
				VAR_I_GPS_SATS_USED, 0);
		update.gpsSatsAll = i.getIntExtra(
				VAR_I_GPS_SATS_ALL, 0);
		update.declination = i.getFloatExtra(
				VAR_F_DECLINATION, 0);
		update.orientCourse = i.getFloatExtra(
				VAR_F_ORIENT_COURSE, 0);
		update.orientCourseOpposit = i.getFloatExtra(
				VAR_F_ORIENT_COURSE_OPPOSIT, 0);
		update.orientPitch = i.getFloatExtra(
				VAR_F_ORIENT_PITCH, 0);
		update.orientRoll = i.getFloatExtra(
				VAR_F_ORIENT_ROLL, 0);
		update.orientGpsShift = i.getFloatExtra(
				VAR_F_ORIENT_GPS_SHIFT, 0);
		update.orientGpsAngle = i.getFloatExtra(
				VAR_F_ORIENT_GPS_ANGLE, 0);

		// MAP STUFF

		update.mapVisible = i.getBooleanExtra(
				VAR_B_MAP_VISIBLE, false);
		update.newMapCenter = false;
		update.locMapCenter = LocusUtils.getLocationFromIntent(
				i, VAR_LOC_MAP_CENTER);
		if (mLastMapCenter == null || mLastMapCenter.distanceTo(
				update.locMapCenter) > mLocMinDistance) {
			mLastMapCenter = update.locMapCenter;
			update.newMapCenter = true;
		}

		// check MAP
		update.mapTopLeft = LocusUtils.getLocationFromIntent(
				i, VAR_LOC_MAP_BBOX_TOP_LEFT);
		update.mapBottomRight = LocusUtils.getLocationFromIntent(
				i, VAR_LOC_MAP_BBOX_BOTTOM_RIGHT);
		update.mapZoomLevel = i.getIntExtra(
				VAR_I_MAP_ZOOM_LEVEL, 0);
		update.newZoomLevel = update.mapZoomLevel != mLastZoomLevel;
		mLastZoomLevel = update.mapZoomLevel;
		update.mapRotate = i.getFloatExtra(
				VAR_F_MAP_ROTATE, 0.0f);
		update.isUserTouching = i.getBooleanExtra(
				VAR_B_MAP_USER_TOUCHES, false);
		// TRACK RECORDING PART
		
		update.trackRecRecording = i.getBooleanExtra(
				VAR_B_REC_RECORDING, false); 
		update.trackRecPaused = i.getBooleanExtra(
				VAR_B_REC_PAUSED, false);
		if (update.trackRecRecording || update.trackRecPaused) {
			update.trackRecDist = i.getDoubleExtra(
					VAR_D_REC_DIST, 0.0);
			update.trackRecDistDownhill = i.getDoubleExtra(
					VAR_D_REC_DIST_DOWNHILL, 0.0);
			update.trackRecDistUphill = i.getDoubleExtra(
					VAR_D_REC_DIST_UPHILL, 0.0);
			update.trackRecAltMin = i.getFloatExtra(
					VAR_F_REC_ALT_MIN, 0.0f);
			update.trackRecAltMax = i.getFloatExtra(
					VAR_F_REC_ALT_MAX, 0.0f);
			update.trackRecAltDownhill = i.getFloatExtra(
					VAR_F_REC_ALT_DOWNHILL, 0.0f);
			update.trackRecAltUphill = i.getFloatExtra(
					VAR_F_REC_ALT_UPHILL, 0.0f);
			update.trackRecAltCumulative = i.getFloatExtra(
					VAR_F_REC_ALT_CUMULATIVE, 0.0f);
			update.trackRecTime = i.getLongExtra(
					VAR_L_REC_TIME, 0L);
			update.trackRecTimeMove = i.getLongExtra(
					VAR_L_REC_TIME_MOVE, 0L);
			update.trackRecSpeedAvg = i.getFloatExtra(
					VAR_F_REC_SPEED_AVG, 0.0f);
			update.trackRecSpeedAvgMove = i.getFloatExtra(
					VAR_F_REC_SPEED_AVG_MOVE, 0.0f);
			update.trackRecSpeedMax = i.getFloatExtra(
					VAR_F_REC_SPEED_MAX, 0.0f);
			update.trackRecPoints = i.getIntExtra(
					VAR_I_REC_POINTS, 0);
		}
		
		// GUIDING PART
		
		update.guidingEnabled = i.getBooleanExtra(
				VAR_B_GUIDING, false);
		if (update.guidingEnabled) {
			update.guideWptName = i.getStringExtra(
					VAR_S_GUIDE_WPT_NAME);
			update.guideWptLoc = LocusUtils.getLocationFromIntent(
					i, VAR_LOC_GUIDE_WPT);
			update.guideWptDist = i.getDoubleExtra(
					VAR_D_GUIDE_WPT_DIST, 0.0);
			update.guideWPtDistToFinish = i.getDoubleExtra(
					VAR_D_GUIDE_WPT_DIST_TO_FINISH, 0.0);
			update.guideWptAzim = i.getFloatExtra(
					VAR_F_GUIDE_WPT_AZIM, 0.0f);
			update.guideWptAngle = i.getFloatExtra(
					VAR_F_GUIDE_WPT_ANGLE, 0.0f);
			update.guideWptTime = i.getLongExtra(
					VAR_L_GUIDE_WPT_TIME, 0L);
			update.guideWptTimeToFinish = i.getLongExtra(
					VAR_L_GUIDE_WPT_TIME_TO_FINISH, 0L);
		}

		// send update back by handler
		handler.onUpdate(update);
	}
}
