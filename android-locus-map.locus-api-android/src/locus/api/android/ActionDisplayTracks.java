package locus.api.android;

import java.util.ArrayList;

import locus.api.android.utils.LocusConst;
import locus.api.android.utils.RequiredVersionMissingException;
import locus.api.objects.Storable;
import locus.api.objects.extra.Track;
import android.content.Context;
import android.content.Intent;

public class ActionDisplayTracks extends ActionDisplay {

	// SEND ONE SINGLE TRACK
	
	public static boolean sendTrack(Context context, Track track,
			boolean callImport)
			throws RequiredVersionMissingException {
		return sendTrack(LocusConst.ACTION_DISPLAY_DATA, context, track, callImport, false);
	}
	
	public static boolean sendTrack(Context context, Track track, 
			boolean callImport, boolean startNavigation)
			throws RequiredVersionMissingException {
		return sendTrack(LocusConst.ACTION_DISPLAY_DATA, context, track, callImport, startNavigation);
	}
	
	public static boolean sendTrackSilent(Context context, Track track)
			throws RequiredVersionMissingException {
		return sendTrack(LocusConst.ACTION_DISPLAY_DATA_SILENTLY, context, track, false, false);
	}
	
	private static boolean sendTrack(String action, Context context, Track track,
			boolean callImport, boolean startNavigation)
			throws RequiredVersionMissingException {
		if (track == null)
			return false;
		Intent intent = new Intent();
		intent.putExtra(LocusConst.INTENT_EXTRA_TRACKS_SINGLE, track.getAsBytes());
		intent.putExtra(LocusConst.INTENT_EXTRA_START_NAVIGATION, startNavigation);
		return sendData(action, context, intent, callImport);
	}
	
	// SEND TRACK PACK (MORE THEN ONE)
	
	public static boolean sendTracks(Context context, ArrayList<Track> tracks, boolean callImport)
			throws RequiredVersionMissingException {
		return sendTracks(LocusConst.ACTION_DISPLAY_DATA, context, tracks, callImport);
	}
	
	public static boolean sendTracksSilent(Context context, ArrayList<Track> tracks)
			throws RequiredVersionMissingException {
		return sendTracks(LocusConst.ACTION_DISPLAY_DATA_SILENTLY, context, tracks, false);
	}
	
	private static boolean sendTracks(String action, Context context, ArrayList<Track> tracks, boolean callImport)
			throws RequiredVersionMissingException {
		if (tracks == null || tracks.size() == 0)
			return false;
		Intent intent = new Intent();
		intent.putExtra(LocusConst.INTENT_EXTRA_TRACKS_MULTI, 
				Storable.getAsBytes(tracks));
		return sendData(action, context, intent, callImport);
	}
}
