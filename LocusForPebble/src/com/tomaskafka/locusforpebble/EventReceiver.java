package com.tomaskafka.locusforpebble;

import locus.api.android.PeriodicUpdate;
import locus.api.android.PeriodicUpdate.UpdateContainer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class EventReceiver extends BroadcastReceiver {

	private static final String TAG = "EventReceiver";
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;
		
		// get valid instance of PeriodicUpdate object
		PeriodicUpdate pu = PeriodicUpdate.getInstance();

		// set notification of new locations to 10m
		pu.setLocNotificationLimit(10.0);
		
		// handle event
		pu.onReceive(context, intent, new PeriodicUpdate.OnUpdate() {
			
			public void onIncorrectData() {
				Toast.makeText(context, "onIncorrectData()", Toast.LENGTH_LONG).show();
			}

			public void onUpdate(UpdateContainer update) {
				Log.i(TAG, "onUpdate(" + update + ")");
				
				// sending data back to locus based on events if new map center and map is visible!
				/*
				if (!update.newMapCenter || !update.mapVisible)
					return;
				*/
				
				MainActivity.updateDataFromLocus(update);
				
				
				// Toast.makeText(context, "ZoomLevel:" + update.mapZoomLevel, Toast.LENGTH_LONG).show();
				// Toast.makeText(context, "UpdateContainer:\n" + update.toString(), Toast.LENGTH_LONG).show();
				
				/*
				try {
					// sending back few points near received
					Location mapCenter = update.locMapCenter;
					PackWaypoints pw = new PackWaypoints("send_point_silently");
					for (int i = 0; i < 10; i++) {
						Location loc = new Location(TAG);
						loc.setLatitude(mapCenter.getLatitude() + (Math.random() - 0.5) / 100.0);
						loc.setLongitude(mapCenter.getLongitude() + (Math.random() - 0.5) / 100.0);
						// point name determine if 
						pw.addWaypoint(new Waypoint("Testing point - " + i, loc));
					}

					ActionDisplayPoints.sendPackSilent(context, pw);
				} catch (RequiredVersionMissingException e) {
					e.printStackTrace();
				}
				*/
			}
		});
		
	}
}
