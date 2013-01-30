package menion.android.locus.api.sample;

import locus.api.android.ActionTools;
import locus.api.utils.Logger;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TabToolsFragment extends Fragment {

	private static final String TAG = "TabToolsIntent";
	
    public TabToolsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = View.inflate(getActivity(), R.layout.tab_tools_fragment, null);
    	
        Button btn08 = (Button) view.findViewById(R.id.button8);
        btn08.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendFileToSystem(getActivity());
			}
		});
        
        Button btn09 = (Button) view.findViewById(R.id.button9);
        btn09.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendFileToLocus(getActivity());
			}
		});

        
        Button btn14 = (Button) view.findViewById(R.id.button14);
        btn14.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.pickLocation(getActivity());
			}
		});
        
        Button btn15 = (Button) view.findViewById(R.id.button15);
        btn15.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// filter data so only visible will be GPX and KML files
				ActionTools.actionPickFile(getActivity(),
						0, "Give me a FILE!!",
						new String[] {".gpx", ".kml"});
			}
		});
        
        Button btn16 = (Button) view.findViewById(R.id.button16);
        btn16.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ActionTools.actionPickDir(getActivity(), 1);
			}
		});
        
        Button btn17 = (Button) view.findViewById(R.id.button17);
        btn17.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	        	new AlertDialog.Builder(getActivity()).
	        	setTitle("Locus Root directory").
	        	setMessage("dir:" + SampleCalls.getRootDirectory(getActivity()) +
	        			"\n\n'null' means no required version installed or different problem").
	        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
		});
        
        Button btn18 = (Button) view.findViewById(R.id.button18);
        btn18.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					ActionTools.callAddNewWmsMap(getActivity(),
							"http://wms.geology.cz/wmsconnector/com.esri.wms.Esrimap/CGS_Geomagnetic_Field");
				} catch (Exception e) {
					Logger.e(TAG, "onClick()", e);
				}
			}
		});
        
        Button btn19 = (Button) view.findViewById(R.id.button19);
        btn19.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.startNavigation(getActivity());
			}
		});

        Button btn20 = (Button) view.findViewById(R.id.button20);
        btn20.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.trackRecordStart(getActivity());
			}
		});
        
        Button btn21 = (Button) view.findViewById(R.id.button21);
        btn21.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.trackRecordStop(getActivity());
			}
		});
        
        Button btn22 = (Button) view.findViewById(R.id.button22);
        btn22.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ActivityDashboard.class));
			}
		});
        
        Button btn23 = (Button) view.findViewById(R.id.button23);
        btn23.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.showCircles(getActivity());
			}
		});
    	return view;
    }
}
