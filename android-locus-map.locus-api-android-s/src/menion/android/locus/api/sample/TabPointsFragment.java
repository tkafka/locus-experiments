package menion.android.locus.api.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TabPointsFragment extends Fragment {

    public TabPointsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = View.inflate(getActivity(), R.layout.tab_points_fragment, null);
    	
        Button btn01 = (Button) view.findViewById(R.id.button1);
        btn01.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendOnePoint(getActivity());
			}
		});
        
        Button btn02 = (Button) view.findViewById(R.id.button2);
        btn02.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendOnePointWithIcon(getActivity());
			}
		});
        
        Button btn03 = (Button) view.findViewById(R.id.button3);
        btn03.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendMorePoints(getActivity());
			}
		});
        
        Button btn04 = (Button) view.findViewById(R.id.button4);
        btn04.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendMorePointsWithIcons(getActivity());
			}
		});
        
        Button btn05 = (Button) view.findViewById(R.id.button5);
        btn05.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendOnePointGeocache(getActivity());
			}
		});
        
        Button btn06 = (Button) view.findViewById(R.id.button6);
        btn06.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendMorePointsGeocacheIntentMehod(getActivity());
			}
		});
        
        Button btn10 = (Button) view.findViewById(R.id.button10);
        btn10.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendDateOverFile(getActivity());
			}
		});
        
        Button btn11 = (Button) view.findViewById(R.id.button11);
        btn11.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendOnePointWithCallbackOnDisplay(getActivity());
			}
		});
        
    	return view;
    }
}
