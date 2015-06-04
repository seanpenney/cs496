package com.seanpenney.soofit;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Sean on 6/4/2015.
 */
public class MyMapFragment extends SupportMapFragment {
    private GoogleMap supportMap;
    private PolylineOptions options;
    private View parentView;
    private TextView distanceVal;
    private TextView caloriesVal;
    private EditText enteredWeight;
    private Button start;
    private Button end;
    private LatLng previous;
    private int firstReading = 1; // flag to know if first GPS reading


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = getActivity().findViewById(R.id.main_linear_layout);
        setupHeader(); // Set up header buttons/input
        setupFooter(); // Set up footer display

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setupHeader() {
        RelativeLayout header = (RelativeLayout) parentView.findViewById(R.id.content_header_relative_layout);
        header.removeAllViews();

        TextView weight = new TextView(getActivity());
        weight.setId(weight.generateViewId());
        weight.setText("Enter your weight:  ");
        weight.setTextSize(30);

        enteredWeight = new EditText(getActivity());
        enteredWeight.setId(enteredWeight.generateViewId());
        enteredWeight.setHint("Enter weight in pounds");
        enteredWeight.setTextSize(20);

        start = new Button(getActivity());
        start.setId(start.generateViewId());
        start.setText("Start Moving");
        start.setPadding(20, 20, 20, 20);

        end = new Button(getActivity());
        end.setId(end.generateViewId());
        end.setText("End Workout");
        end.setPadding(20, 20, 20, 20);

        RelativeLayout.LayoutParams weightParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams enteredWeightParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams startParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams endParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        enteredWeightParams.addRule(RelativeLayout.RIGHT_OF, weight.getId());
        startParams.addRule(RelativeLayout.BELOW, weight.getId());
        endParams.addRule(RelativeLayout.BELOW, start.getId());

        header.addView(weight, weightParams);
        header.addView(enteredWeight, enteredWeightParams);
        header.addView(start, startParams);
        header.addView(end, endParams);
    }

    private void setupFooter() {
        RelativeLayout footer = (RelativeLayout) parentView.findViewById(R.id.content_footer_relative_layout);
        footer.removeAllViews();

        TextView distance = new TextView(getActivity());
        distance.setId(distance.generateViewId());
        distance.setTextSize(20);
        distance.setText("Distance:  ");

        distanceVal = new TextView(getActivity());
        distanceVal.setId(distanceVal.generateViewId());
        distanceVal.setTextSize(20);
        distanceVal.setText("Current Distance");

        TextView calories = new TextView(getActivity());
        calories.setId(calories.generateViewId());
        calories.setTextSize(20);
        calories.setText("Calories Burned:  ");

        caloriesVal = new TextView(getActivity());
        caloriesVal.setId(caloriesVal.generateViewId());
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

    @Override
    public void onStart() {
        super.onStart();
        initMap();

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

    private void startWorkout() {
        Toast.makeText(getActivity(), "Map workout started", Toast.LENGTH_SHORT).show();
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

                // move to location
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(12).build();
                supportMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                float currentMeters = recalculateMeters();
                distanceVal.setText(String.format("%.2f", currentMeters * 0.000621371) + " miles");
                caloriesVal.setText(String.format("%.2f", currentMeters * 0.000621371 * 100));
            }
        });
    }

    private void endWorkout() {
        Toast.makeText(getActivity(), "Map workout ended", Toast.LENGTH_SHORT).show();
        supportMap.setOnMyLocationChangeListener(null);
    }


    private void initMap() {
        supportMap = getMap();

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

    private float recalculateMeters() {
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
}