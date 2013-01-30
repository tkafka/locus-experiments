package menion.android.locus.api.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TabTracksFragment extends Fragment {

    public TabTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = View.inflate(getActivity(), R.layout.tab_tracks_fragment, null);
    	
        Button btn12 = (Button) view.findViewById(R.id.button12);
        btn12.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendOneTrack(getActivity());
			}
		});
        
        Button btn13 = (Button) view.findViewById(R.id.button13);
        btn13.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SampleCalls.callSendMultipleTracks(getActivity());
			}
		});
        
    	return view;
    }
}
