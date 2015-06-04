package com.seanpenney.soofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sean on 6/3/2015.
 */
public class StatsFragment extends Fragment {
    private final String queryURL = "https://1-dot-soooofit.appspot.com/query_highest";
    private TextView caloriesText;
    private TextView dateText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment, container, false);

        View parentView = getActivity().findViewById(R.id.main_linear_layout);

        RelativeLayout header = (RelativeLayout) parentView.findViewById(R.id.content_header_relative_layout);
        RelativeLayout footer = (RelativeLayout) parentView.findViewById(R.id.content_footer_relative_layout);

        header.removeAllViews();
        footer.removeAllViews();

        caloriesText = (TextView) view.findViewById(R.id.stats_calories);
        dateText = (TextView) view.findViewById(R.id.stats_date);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new QueryHighestStats(new AsyncResponse() {
            @Override
            public void processFinish(double calories, double date) {
                caloriesText.setText("Most calories burned: " + String.format("%.2f", calories));

                Date dateSet = new Date((long)date);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                dateText.setText("Record set on: " + dateFormat.format(dateSet));
            }
        }).execute(queryURL);
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    private class QueryHighestStats extends AsyncTask<String, Void, String> {
        public AsyncResponse delegate = null;

        public QueryHighestStats(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        // query server for highest stats, get back JSON data
        @Override
        protected String doInBackground(String... urls) {
            int TIMEOUT_MILLISEC = 10000; // 10 seconds
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);

            HttpGet request = new HttpGet(urls[0]);
            HttpResponse response = null;
            InputStream inputStream = null;
            try {
                response = client.execute(request);
                inputStream = response.getEntity().getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                try {
                    return convertInputStreamToString(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "Request did not work";
        }

        // process JSON data
        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double calories = 0;
            double date = 0;
            try {
                calories = jsonResult.getDouble("calories");
                date = jsonResult.getDouble("date");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            delegate.processFinish(calories, date);
        }
    }
}