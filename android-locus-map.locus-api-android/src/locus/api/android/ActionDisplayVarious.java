package locus.api.android;

import java.util.ArrayList;

import locus.api.android.objects.PackWaypoints;
import locus.api.android.utils.LocusConst;
import locus.api.android.utils.RequiredVersionMissingException;
import locus.api.objects.Storable;
import locus.api.objects.extra.Circle;
import android.content.Context;
import android.content.Intent;

public class ActionDisplayVarious extends ActionDisplay {

	/**
	 * Simple way how to send circles over intent to Locus. 
	 * @param context actual {@link Context}
	 * @param data {@link PackWaypoints} object that should be send to Locus
	 * @param callImport whether import with this data should be called after Locus starts
	 * @return true if success
	 * @throws RequiredVersionMissingException 
	 */
	public static boolean sendCirclesSilent(Context context, ArrayList<Circle> circles)
			throws RequiredVersionMissingException {
		return sendCirclesSilent(LocusConst.ACTION_DISPLAY_DATA_SILENTLY, 
				context, circles, false);
	}
	
	private static boolean sendCirclesSilent(String action, Context context, ArrayList<Circle> circles, boolean callImport)
			throws RequiredVersionMissingException {
		if (circles == null)
			return false;
		Intent intent = new Intent();
		intent.putExtra(LocusConst.INTENT_EXTRA_CIRCLES_MULTI, 
				Storable.getAsBytes(circles));
		return sendData(action, context, intent, callImport, 242);
	}
	
	/**
	 * Allow to remove visible circles defined by it's ID value
	 * @param ctx current context that send broadcast
	 * @param itemsId list of circles IDs that should be removed from map
	 * @throws RequiredVersionMissingException
	 */
	public static void removeCirclesSilent(Context ctx, ArrayList<Integer> itemsId) 
			throws RequiredVersionMissingException {
		removeDataSilently(ctx, LocusConst.INTENT_EXTRA_CIRCLES_MULTI, itemsId);
	}
}
