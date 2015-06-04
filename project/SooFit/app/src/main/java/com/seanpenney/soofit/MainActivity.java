package com.seanpenney.soofit;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnMapReadyCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    static final LatLng CORVALLIS = new LatLng(44.5667, -123.2833);
    SupportMapFragment mapFragment;
    GoogleMap supportMap;
    PolylineOptions options;
    TextView distanceVal;
    TextView caloriesVal;
    LatLng previous;
    int firstReading = 1; // flag for first GPS reading
    public static int startButton;
    public static int endButton;
    public static int distanceValId;
    public static int weightEntryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_main);


        // Set up header buttons/input
        setupHeader();

        // Set up footer display
        setupFooter();


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        onNavigationDrawerItemSelected(0); // default to map fragment
    }

    private void setupFooter() {
        RelativeLayout footer = (RelativeLayout) findViewById(R.id.content_footer_relative_layout);
        footer.removeAllViews();

        TextView distance = new TextView(this);
        distance.setId(distance.generateViewId());
        distance.setTextSize(20);
        distance.setText("Distance:  ");

        TextView calories = new TextView(this);
        calories.setId(calories.generateViewId());
        calories.setTextSize(20);
        calories.setText("Calories Burned:  ");

        distanceVal = new TextView(this);
        distanceValId = distanceVal.generateViewId();
        distanceVal.setId(distanceValId);
        distanceVal.setTextSize(20);
        distanceVal.setText("Current Distance");

        caloriesVal = new TextView(this);
        caloriesVal.setTextSize(20);
        caloriesVal.setText("Current Calories");

        RelativeLayout.LayoutParams distanceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams distanceValParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        distanceValParams.addRule(RelativeLayout.RIGHT_OF, distance.getId());
        footer.addView(distance, distanceParams);
        footer.addView(distanceVal, distanceValParams);

        RelativeLayout.LayoutParams caloriesParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams caloriesValParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        caloriesParams.addRule(RelativeLayout.BELOW, distance.getId());
        footer.addView(calories, caloriesParams);
        caloriesValParams.addRule(RelativeLayout.RIGHT_OF, calories.getId());
        caloriesValParams.addRule(RelativeLayout.BELOW, distance.getId());
        footer.addView(caloriesVal, caloriesValParams);
    }

    private void setupHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.content_header_relative_layout);
        header.removeAllViews();

        TextView weight = new TextView(this);
        weight.setId(weight.generateViewId());
        weight.setText("Enter your weight:  ");
        weight.setTextSize(30);

        EditText weightEntry = new EditText(this);
        weightEntryId = weightEntry.generateViewId();
        weightEntry.setId(weightEntryId);
        weightEntry.setHint("Enter weight in pounds");
        weightEntry.setTextSize(20);

        Button start = new Button(this);
        startButton = start.generateViewId();
        start.setId(startButton);
        start.setText("Start Moving");
        start.setPadding(20, 20, 20, 20);

        Button end = new Button(this);
        endButton = end.generateViewId();
        end.setId(endButton);
        end.setText("End Workout");
        end.setPadding(20, 20, 20, 20);

        RelativeLayout.LayoutParams weightParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams weightEntryParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams startParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams endParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        weightEntryParams.addRule(RelativeLayout.RIGHT_OF, weight.getId());
        startParams.addRule(RelativeLayout.BELOW, weight.getId());
        endParams.addRule(RelativeLayout.BELOW, start.getId());
        header.addView(weight, weightParams);
        header.addView(weightEntry, weightEntryParams);
        header.addView(start, startParams);
        header.addView(end, endParams);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWorkout();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endWorkout();
            }
        });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                mapFragment = new SupportMapFragment();
                fragmentTransaction.replace(R.id.container, mapFragment).commit();

                mapFragment.getMapAsync(this); // Set up map when ready
                onSectionAttached(1);

                break;


            case 1:
                PedometerFragment pedometerFragment = new PedometerFragment();
                fragmentTransaction.replace(R.id.container, pedometerFragment).commit();
                onSectionAttached(2);
                break;

            case 2:
                StatsFragment statsFragment = new StatsFragment();
                fragmentTransaction.replace(R.id.container, statsFragment).commit();
                onSectionAttached(3);
                break;

            default:
                fragmentTransaction.replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
                break;
        }
    }

    private void endWorkout() {
        Toast.makeText(this, "Map workout ended", Toast.LENGTH_SHORT).show();
        supportMap.setOnMyLocationChangeListener(null);
    }

    private void startWorkout() {
        Toast.makeText(this, "Map workout started", Toast.LENGTH_SHORT).show();
        // setup map
        supportMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                LatLng location = null;
                if (firstReading == 1) {
                    location = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    previous = location;
                    firstReading = 0;
                } else {
                    if (Math.abs(arg0.getLatitude() - previous.latitude) > .0001 || Math.abs(arg0.getLongitude() - previous.longitude) > .0001) {
                        location = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                        previous = location;
                    } else {
                        return;
                    }
                }
                Log.i("Latitude", " : " + arg0.getLatitude());
                Log.i("Longitude", " : " + arg0.getLongitude());

                options.add(location);
                supportMap.addPolyline(options);
                float currentMiles = recalculateMiles();
                distanceVal.setText(String.format("%.2f", currentMiles * 0.000621371) + " miles");
                caloriesVal.setText(String.format("%.2f", currentMiles * 0.000621371 * 100));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        supportMap = googleMap;
        MarkerOptions marker = new MarkerOptions().position(CORVALLIS).title("Corvallis").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        // adding marker
        supportMap.addMarker(marker);

        // move to location
        CameraPosition cameraPosition = new CameraPosition.Builder().target(CORVALLIS).zoom(12).build();
        supportMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // show location
        supportMap.setMyLocationEnabled(true);

        // show my location button
        supportMap.getUiSettings().setMyLocationButtonEnabled(true);

        // disable compass button
        supportMap.getUiSettings().setCompassEnabled(false);

        // disable map rotate
        supportMap.getUiSettings().setRotateGesturesEnabled(false);

        options = new PolylineOptions().width(5).color(Color.BLUE);
        supportMap.addPolyline(options);


    }

    private float recalculateMiles() {
        float totalDistance = 0;

        for (int i = 1; i < options.getPoints().size(); i++) {
            Location currLocation = new Location("Current location");
            currLocation.setLatitude(options.getPoints().get(i).latitude);
            currLocation.setLongitude(options.getPoints().get(i).longitude);

            Location lastLocation = new Location("Last location");
            lastLocation.setLatitude(options.getPoints().get(i - 1).latitude);
            lastLocation.setLongitude(options.getPoints().get(i - 1).longitude);

            totalDistance += lastLocation.distanceTo(currLocation);
        }

        return totalDistance;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Map";
                break;
            case 2:
                mTitle = "Pedometer";
                break;
            case 3:
                mTitle = "Stats";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
