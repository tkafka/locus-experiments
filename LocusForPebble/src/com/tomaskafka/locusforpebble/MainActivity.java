package com.tomaskafka.locusforpebble;

import java.util.Locale;

import locus.api.android.PeriodicUpdate;
import locus.api.android.PeriodicUpdate.OnUpdate;
import locus.api.android.PeriodicUpdate.UpdateContainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	PeriodicUpdate periodicUpdate;
	PebbleOnUpdateHandler pebbleOnUpdateHandler;
	
	public static View trackView;
	
	public static void updateDataFromLocus(UpdateContainer update) {
		if (trackView != null) {
			TextView 
				lat = (TextView) trackView.findViewById(R.id.textViewLatitude),
				lon = (TextView) trackView.findViewById(R.id.textViewLongtitude),
				alt = (TextView) trackView.findViewById(R.id.textViewAltitude),
				msg = (TextView) trackView.findViewById(R.id.textViewMessage);
			
			
			String text = "";

			if (update.enabledMyLocation)
			{
				if (update.newMyLocation) {
					text = "NEW: ";
				} else {
					text = "Old: ";
				}
				
				if (update.locMyLocation != null) {
					// text += "Lat: " + update.locMyLocation.getLatitude() + ", Long: " + update.locMyLocation.getLongitude() + ", Alt: " + update.locMyLocation.getAltitude();
					text += "Known location!";
				} else {

					text += "enabledMyLocation, but no location :(";
				}
				
			} else {
				text = "disabledMyLocation :(";
			} 
			
			msg.setText(text);
			
			// Toast.makeText(trackView.getContext(), str, Toast.LENGTH_LONG).show();

			
			if (update.locMyLocation != null) {
				lat.setText("Lat: " + update.locMyLocation.getLatitude());
				lon.setText("Lon: " + update.locMyLocation.getLongitude());
				alt.setText("Alt: " + update.locMyLocation.getAltitude());
			} else {
				lat.setText("Lat: -");
				lon.setText("Lon: -");
				alt.setText("Alt: -");
			}
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// periodicUpdate = PeriodicUpdate.getInstance();
		// pebbleOnUpdateHandler = new PebbleOnUpdateHandler();
		
		// Intent receiveIntent = new Intent();
		
		
		// periodicUpdate.onReceive(getApplicationContext(), getIntent(), pebbleOnUpdateHandler);
		
		// Location location = periodicUpdate.getLastGps();
		// Toast.makeText(getApplicationContext(), "Location:\n" + location.toString(), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	protected class PebbleOnUpdateHandler implements OnUpdate {

		@Override
		public void onIncorrectData() {
			Toast.makeText(getApplicationContext(), "onIncorrectData()", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onUpdate(UpdateContainer updateContainer) {
			Toast.makeText(getApplicationContext(), updateContainer.toString(), Toast.LENGTH_LONG).show();
			
		}
		
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) throws IllegalArgumentException {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			switch (position) {
			case 0:
				fragment = new TrackSectionFragment();
				/*
				 * Bundle args = new Bundle();
				 * args.putInt(TrackSectionFragment.ARG_SECTION_NUMBER, position
				 * + 1); fragment.setArguments(args);
				 */
				return fragment;
			case 1:
				fragment = new SettingsSectionFragment();
				return fragment;
			default:
				throw new IllegalArgumentException("invalid position");
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_track).toUpperCase(l);
			case 1:
				return getString(R.string.title_section_settings)
						.toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class TrackSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public TrackSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			/*
			 * // Create a new TextView and set its text to the fragment's
			 * section // number argument value. TextView textView = new
			 * TextView(getActivity()); textView.setGravity(Gravity.CENTER);
			 * textView
			 * .setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER
			 * ))); return textView;
			 */

			// View view =
			// LayoutInflater.from(context).inflate(R.layout.recording_details,
			// null);
			View view = inflater.inflate(R.layout.recording_details, null);
			
			trackView = view;
			
			return view;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class SettingsSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public SettingsSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.settings, null);
			return view;
		}
	}

}
