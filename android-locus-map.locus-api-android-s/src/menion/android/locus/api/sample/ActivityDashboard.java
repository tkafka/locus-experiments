package menion.android.locus.api.sample;

import locus.api.android.PeriodicUpdate;
import locus.api.android.PeriodicUpdate.UpdateContainer;
import locus.api.android.utils.LocusConst;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class ActivityDashboard extends FragmentActivity {

	// receiver for events
	private BroadcastReceiver receiver;
	
	private TextView tv01;
	private TextView tv02;
	private TextView tv03;
	private TextView tv04;
	private TextView tv05;
	private TextView tv06;
	private TextView tv07;
	private TextView tv08;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		tv01 = (TextView) findViewById(R.id.textView1);
		tv02 = (TextView) findViewById(R.id.textView2);
		tv03 = (TextView) findViewById(R.id.textView3);
		tv04 = (TextView) findViewById(R.id.textView4);
		tv05 = (TextView) findViewById(R.id.textView5);
		tv06 = (TextView) findViewById(R.id.textView6);
		tv07 = (TextView) findViewById(R.id.textView7);
		tv08 = (TextView) findViewById(R.id.textView8);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// register receiver
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (isFinishing()) {
					return;
				}
				
				// handle received data
				PeriodicUpdate.getInstance().onReceive(
						ActivityDashboard.this,
						intent, updateHandler);
			}
		};

		// register receiver
		IntentFilter filter = new IntentFilter(
				LocusConst.ACTION_PERIODIC_UPDATE);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		// set receiver
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}
	
	// last reported values
	private UpdateContainer lastUpdate;
	
	private PeriodicUpdate.OnUpdate updateHandler = 
			new PeriodicUpdate.OnUpdate() {
		
		public void onUpdate(UpdateContainer update) {
			lastUpdate = update;
			update();
		}
		
		public void onIncorrectData() {}
	};
	
	private void update() {
		// check if data exists
		if (lastUpdate == null) {
			return;
		}
		
		// refresh content
		tv01.setText(String.valueOf(lastUpdate.locMyLocation.getLatitude()));
		tv02.setText(String.valueOf(lastUpdate.locMyLocation.getLongitude()));
		tv03.setText(String.valueOf(lastUpdate.gpsSatsUsed));
		tv04.setText(String.valueOf(lastUpdate.gpsSatsAll));
		tv05.setText(String.valueOf(lastUpdate.mapVisible));
		tv06.setText(String.valueOf(lastUpdate.locMyLocation.getAccuracy()));
		tv07.setText(String.valueOf(lastUpdate.locMyLocation.getBearing()));
		tv08.setText(String.valueOf(lastUpdate.locMyLocation.getSpeed()));
	}
}
