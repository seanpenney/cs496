package com.seanpenney.soofitgae;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.HTTP;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.apphosting.api.ApiProxy.LogRecord.Level;
import com.seanpenney.soofitgae.PMF;

public class AddPedometerStats extends HttpServlet {
	private static final Logger log = Logger.getLogger(AddPedometerStats.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		JSONObject jsonObject = null;
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
		}

		try {
			jsonObject = new JSONObject(jb.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		int steps = 0;
		double weight = 0;
		try {
			steps = jsonObject.getInt("steps");
			weight = jsonObject.getDouble("weight");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		double caloriesBurnedPerMile = .5 * weight; // .5 represents calories
													// burned per pound per mile
		double caloriesPerStep = caloriesBurnedPerMile / 1400; // 1400
																// represents
																// steps per
																// mile
		double caloriesBurned = steps * caloriesPerStep;
		double distance = steps / (double) 1400; // distance in mile

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		PedometerStats pStats = new PedometerStats();
		try {

			pStats.setWeight(weight);
			pStats.setSteps(steps);
			pStats.setDistance(distance);
			pStats.setCalories(caloriesBurned);

			long currentTime = System.currentTimeMillis();
			pStats.setDate(currentTime);

			pm.makePersistent(pStats);
		} finally {
			pm.close();
		}

		JSONObject json = new JSONObject();
		try {
			json.put("weight", pStats.getWeight());
			json.put("steps", pStats.getSteps());
			json.put("distance", pStats.getDistance());
			json.put("calories burned", pStats.getCalories());
			json.put("date", pStats.getDate());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(json.toString());
	}

}
