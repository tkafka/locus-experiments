package menion.android.locus.api.sample;

import java.io.File;

import locus.api.android.ActionTools;
import locus.api.android.utils.LocusUtils;
import locus.api.objects.extra.ExtraData;
import locus.api.objects.extra.Location;
import locus.api.objects.extra.Waypoint;
import locus.api.utils.Logger;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String TAG = "MainActivity";
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        // finally check intent that started this sample
        checkStartIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	if (i == 0) {
        		return new TabPointsFragment();
        	} else if (i == 1) {
        		return new TabTracksFragment();
        	} else if (i == 2) {
        		return new TabToolsFragment();
        	} else {
        		return new Fragment();
        	}
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Points";
                case 1: return "Tracks";
                case 2: return "Utils";
            }
            return null;
        }
    }
    
    /**************************************************/
    
    
    private void checkStartIntent() {
        Intent intent = getIntent();
        // Logger.d(TAG, "received intent:" + intent);
        if (intent == null)
        	return;
        
        if (LocusUtils.isIntentGetLocation(intent)) {
        	new AlertDialog.Builder(this).
        	setTitle("Intent - Get location").
        	setMessage("By pressing OK, dialog disappear and to Locus will be returned some location!").
        	setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Location loc = new Location("Unknown source");
					loc.setLatitude(Math.random() * 85);
					loc.setLongitude(Math.random() * 180);
					if (!LocusUtils.sendGetLocationData(MainActivity.this,
							"Non sence", loc)) {
						Toast.makeText(MainActivity.this, "Wrong data to send!", Toast.LENGTH_SHORT).show();
					}
				}
			}).show();
        } else 
        	
        if (LocusUtils.isIntentPointTools(intent)) {
        	try {
        		final Waypoint wpt = LocusUtils.handleIntentPointTools(this, intent);
        		if (wpt == null) {
        			Toast.makeText(MainActivity.this, "Wrong INTENT - no point!", Toast.LENGTH_SHORT).show();
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
        								ActionTools.updateLocusWaypoint(MainActivity.this, wpt, false);
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
        } else 
        	
        if (LocusUtils.isIntentMainFunction(intent)) {
        	LocusUtils.handleIntentMainFunction(intent,
        			new LocusUtils.OnIntentMainFunction() {
				public void onReceived(Location locGps, Location locMapCenter) {
		        	new AlertDialog.Builder(MainActivity.this).
		        	setTitle("Intent - Main function").
		        	setMessage("GPS location:" + locGps + "\n\nmapCenter:" + locMapCenter).
		        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
				}
				
				public void onFailed() {
					Toast.makeText(MainActivity.this, "Wrong INTENT!", Toast.LENGTH_SHORT).show();
				}
			});
        } else 
        	
        if (LocusUtils.isIntentSearchList(intent)) {
        	LocusUtils.handleIntentSearchList(intent,
        			new LocusUtils.OnIntentMainFunction() {
				public void onReceived(Location locGps, Location locMapCenter) {
		        	new AlertDialog.Builder(MainActivity.this).
		        	setTitle("Intent - Search list").
		        	setMessage("GPS location:" + locGps + "\n\nmapCenter:" + locMapCenter).
		        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					}).show();
				}
				
				public void onFailed() {
					Toast.makeText(MainActivity.this, "Wrong INTENT!", Toast.LENGTH_SHORT).show();
				}
			});
        } else 
        	
        if (LocusUtils.isIntentPointsScreenTools(intent)) {
        	final long[] waypointIds = LocusUtils.handleIntentPointsScreenTools(intent);
        	if (waypointIds == null || waypointIds.length == 0) {
	        	new AlertDialog.Builder(MainActivity.this).
	        	setTitle("Intent - Points screen (Tools)").
	        	setMessage("Problem with loading waypointIds").
	        	setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
        	} else {
	        	new AlertDialog.Builder(MainActivity.this).
	        	setTitle("Intent - Points screen (Tools)").
	        	setMessage("Loaded from file, points:" + waypointIds.length).
	        	setPositiveButton("Load all now", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						loadPointsFromLocus(waypointIds);
						finish();
					}
				}).show();
        	}
        } else 
        	
        if (intent.hasExtra("myOnDisplayExtraActionId")) {
        	String value = intent.getStringExtra("myOnDisplayExtraActionId");

        	// now create full point version and send it back for returned value
			Waypoint wpt = SampleCalls.generateWaypoint(0);
			wpt.setName("Improved version!");
			wpt.addParameter(ExtraData.PAR_DESCRIPTION, 
					"Extra description to ultra improved point!, received value:" + value);
			
			Intent retInent = LocusUtils.prepareResultExtraOnDisplayIntent(wpt, true);
			setResult(RESULT_OK, retInent);
			finish();
			// or you may set RESULT_CANCEL if you don't have improved version of Point, then locus
			// just show current available version
        } else
        	
        if (LocusUtils.isIntentReceiveLocation(intent)) {
        	Waypoint wpt = LocusUtils.getWaypointFromIntent(intent);
        	if (wpt != null) {
            	new AlertDialog.Builder(this).
            	setTitle("Intent - PickLocation").
            	setMessage("Received intent with point:\n\n" + wpt.getName() + "\n\nloc:" + wpt.getLocation() + 
            			"\n\ngcData:" + (wpt.gcData == null ? "sorry, but no..." : wpt.gcData.getCacheID())).
            	setPositiveButton("Close", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {}
    			}).show();
        	} else {
        		Logger.w(TAG, "request PickLocation, canceled");
        	}
        }	
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
    	if (requestCode == 0) { 
    		// pick file
    		if (resultCode == RESULT_OK && data != null) {
    			File file = new File(data.getData().toString());
    			Toast.makeText(this, "Process successful\n\nFile:" + file.getName() + 
    					", exists:" + file.exists(), Toast.LENGTH_SHORT).show();
    		} else {
    			Toast.makeText(this, "Process unsuccessful", Toast.LENGTH_SHORT).show();
    		}
    	}
    	
    	else if (requestCode == 1) { 
    		// pick directory
    		if (resultCode == RESULT_OK && data != null) {
    			File dir = new File(data.getData().toString());
    			Toast.makeText(this, "Process successful\n\nDir:" + dir.getName() + 
    					", exists:" + dir.exists(), Toast.LENGTH_SHORT).show();
    		} else {
    			Toast.makeText(this, "Process unsuccessful", Toast.LENGTH_SHORT).show();
    		}
    	}
    }
    
    private void loadPointsFromLocus(long[] wptsIds) {
    	if (wptsIds == null || wptsIds.length == 0) {
    		Toast.makeText(this, "No points to load", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	for (long wptId : wptsIds) {
    		try {
    			Waypoint wpt = ActionTools.getLocusWaypoint(this, wptId);
    			if (wpt != null) {
Logger.d(TAG, "loadPointsFromLocus(), wptId:" + wptId + ", vs:" + wpt.id);
    				// do some modifications
					wpt.addParameter(ExtraData.PAR_DESCRIPTION, "UPDATED!");
					wpt.getLocation().setLatitude(wpt.getLocation().getLatitude() + 0.001);
					wpt.getLocation().setLongitude(wpt.getLocation().getLongitude() + 0.001);
					
					// update waypoint in Locus database
    				if (ActionTools.updateLocusWaypoint(this, wpt, false) == 1) {
    					Toast.makeText(this, "Loaded and updated (" + wpt.getName() + ")",
    							Toast.LENGTH_SHORT).show();    					
    				} else {
    					Toast.makeText(this, "Loaded, but problem with update (" + wpt.getName() + ")",
    							Toast.LENGTH_SHORT).show();
    				}
    			} else {
    				Toast.makeText(this, "Waypoint: " + wptId + ", not loaded", Toast.LENGTH_SHORT).show();
    			}
    		} catch (Exception e) {
    			Logger.e(TAG, "loadPointsFromLocus(" + wptsIds + ")", e);
    		}
    	}
    }
}
