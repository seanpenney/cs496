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
    private SensorManager sensorManager;
    private TextView pedometerCount;
    private EditText enteredWeight;
    private double GRAVITY_THRESHOLD = .7;
    private int stepCounter = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pedometer_fragment, container, false);

        View parentView = getActivity().findViewById(R.id.main_linear_layout);

        RelativeLayout header = (RelativeLayout) parentView.findViewById(R.id.content_header_relative_layout);
        RelativeLayout footer = (RelativeLayout) parentView.findViewById(R.id.content_footer_relative_layout);


        Button start = (Button) header.findViewById(MainActivity.startButton);
        Button end = (Button) header.findViewById(MainActivity.endButton);
        TextView distance = (TextView) footer.findViewById(MainActivity.distanceValId);
        pedometerCount = (TextView) view.findViewById(R.id.pedometer_count);
        enteredWeight = (EditText) header.findViewById(MainActivity.weightEntryId);
        if (enteredWeight != null) {
            Toast.makeText(getActivity(), "enteredWeight not null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "enteredWeight null", Toast.LENGTH_SHORT).show();
        }


        distance.setText("Current steps");

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

        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        return view;
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
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO
    }

    @Override
    public void onResume() {
        super.onResume();

/*        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }*/

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
            //new SendPedometerDataTask().execute(addUrl, Integer.toString(stepCounter), enteredWeight.getText().toString());
            new SendPedometerDataTask().execute(addUrl, Integer.toString(stepCounter), enteredWeight.getText().toString());
        } else {
            Toast.makeText(getActivity(), "No network connection available", Toast.LENGTH_SHORT).show();
        }

    }

    private class SendPedometerDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            JSONObject json = new JSONObject();

            try {
                json.put("steps", urls[1]);
                json.put("weight", urls[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
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
