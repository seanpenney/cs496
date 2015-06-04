package com.seanpenney.soofitgae;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.seanpenney.soofitgae.PMF;

@SuppressWarnings("serial")
public class QueryPedometerStats extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONArray combined = new JSONArray();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Query query = pm.newQuery(PedometerStats.class);
			List<PedometerStats> allPedometerStats = (List<PedometerStats>) query
					.execute();
			for (int i = 0; i < allPedometerStats.size(); i++) {
				PedometerStats currentStat = allPedometerStats.get(i);
				JSONObject currentObject = new JSONObject();
				try {
					currentObject.put("weight", currentStat.getWeight());
					currentObject.put("steps", currentStat.getSteps());
					currentObject.put("distance", currentStat.getDistance());
					currentObject.put("calories burned",
							currentStat.getCalories());
					currentObject.put("data", currentStat.getDate());

					combined.put(currentObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} finally {
			pm.close();
		}

		try {
			combined.write(out);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}