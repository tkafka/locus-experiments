package menion.android.locus.api.sample;

import locus.api.android.ActionTools;
import locus.api.android.utils.LocusUtils;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Waypoint;
import locus.api.utils.Logger;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityGeocacheTools extends FragmentActivity {

	private static final String TAG = "ActivityGeocacheTools";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText("ActivityGeocacheTools - only for receiving Tools actions from cache");
		setContentView(tv);
		
        // finally check intent that started this sample
        checkStartIntent();
	}
	
    private void checkStartIntent() {
        Intent intent = getIntent();
        Logger.d(TAG, "received intent:" + intent);
        if (intent == null)
        	return;
        
        if (LocusUtils.isIntentPointTools(intent)) {
        	try {
        		final Waypoint wpt = LocusUtils.handleIntentPointTools(this, intent);
        		if (wpt == null) {
        			Toast.makeText(ActivityGeocacheTools.this, "Wrong INTENT - no point!", Toast.LENGTH_SHORT).show();
        		} else {
        			new AlertDialog.Builder(this).
        			setTitle("Intent - On Point action").
        			setMessage("Received intent with point:\n\n" + wpt.getName() + "\n\nloc:" + wpt.getLocation() + 
        					"\n\ngcData:" + (wpt.gcData == null ? "sorry, but no..." : wpt.gcData.getCacheID())).
        					setNegativeButton("Close", new DialogInterface.OnClickListener() {
        						public void onClick(DialogInterface dialog, int which) {
        							// just do some action on required coordinates
        						}
        					}).
        					setPositiveButton("Send updated back", new DialogInterface.OnClickListener() {
        						public void onClick(DialogInterface dialog, int which) {
        							// because current test version is registered on geocache data,
        							// I'll send as result updated geocache
        							try {
        								wpt.addParameter(ExtraData.PAR_DESCRIPTION, "UPDATED!");
        								wpt.getLocation().setLatitude(wpt.getLocation().getLatitude() + 0.001);
        								wpt.getLocation().setLongitude(wpt.getLocation().getLongitude() + 0.001);
        								ActionTools.updateLocusWaypoint(ActivityGeocacheTools.this, wpt, false);
        								finish();
        							} catch (Exception e) {
        								Logger.e(TAG, "isIntentPointTools(), problem with sending new waypoint back", e);
        							}
        						}
        					}).show();
        		}
        	} catch (Exception e) {
        		Logger.e(TAG, "handle point tools", e);
        	}
        }	
    }
}
