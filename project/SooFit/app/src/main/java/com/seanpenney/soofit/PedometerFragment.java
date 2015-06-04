package com.seanpenney.soofit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Sean on 5/29/2015.
 */
public class PedometerFragment extends Fragment implements SensorEventListener {
    private final String addUrl = "https://1-dot-soooofit.appspot.com/add_pedometer";
    private double GRAVITY_THRESHOLD = .7;
    private SensorManager sensorManager;
    private View parentView;
    private TextView distanceVal;
    private TextView caloriesVal;
    private EditText enteredWeight;
    private Button start;
    private Button end;
    private TextView pedometerCount;
    private int stepCounter = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pedometer_fragment, container, false);
        pedometerCount = (TextView) view.findViewById(R.id.pedometer_count);

        parentView = getActivity().findViewById(R.id.main_linear_layout);
        setupHeader(); // Set up header buttons/input
        setupFooter(); // Set up footer display

        return view;
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
        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

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
        Toast.makeText(getActivity(), "Pedometer workout started", Toast.LENGTH_SHORT).show();

        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void endWorkout() {
        Toast.makeText(getActivity(), "Pedometer workout ended", Toast.LENGTH_SHORT).show();

        sensorManager.unregisterListener(this);
        startSendData();
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        final float x = event.values[0];
        final float y = event.values[1];
        final float z = event.values[2];
        final float g = Math.abs((x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));

        if (g < GRAVITY_THRESHOLD) {
            Log.i("Gravity reading", Float.toString(g));
            stepCounter++;
        }
        pedometerCount.setText(Integer.toString(stepCounter));

        try {
            double caloriesBurnedPerMile = .5 * Double.parseDouble(enteredWeight.getText().toString());
            double caloriesPerStep = caloriesBurnedPerMile / 1400;
            double caloriesBurned = stepCounter * caloriesPerStep;
            double distance = stepCounter / (double) 1400;

            caloriesVal.setText(String.format("%.2f", caloriesBurned));
            distanceVal.setText(String.format("%.2f", distance));
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please Enter Weight", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void startSendData() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SendPedometerDataTask().execute(addUrl, Integer.toString(stepCounter), enteredWeight.getText().toString());
        } else {
            Toast.makeText(getActivity(), "No network connection available", Toast.LENGTH_SHORT).show();
        }
    }

    private class SendPedometerDataTask extends AsyncTask<String, Void, String> {
        // send # of steps and user weight to Servlet
        @Override
        protected String doInBackground(String... urls) {
            JSONObject json = new JSONObject();
            try {
                json.put("steps", urls[1]);
                json.put("weight", urls[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int TIMEOUT_MILLISEC = 10000;  // 10 seconds
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(urls[0]);
            try {
                request.setEntity(new ByteArrayEntity(
                        json.toString().getBytes("UTF8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "1";
        }
    }
}