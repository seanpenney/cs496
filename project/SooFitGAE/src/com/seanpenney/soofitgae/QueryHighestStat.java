package com.seanpenney.soofitgae;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class QueryHighestStat extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject currentObject = new JSONObject();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Query query = pm.newQuery(HighestStat.class);
			List<HighestStat> allStats = (List<HighestStat>) query.execute();
			HighestStat highestStat = allStats.get(0);

			try {
				currentObject.put("calories", highestStat.getCalories());
				currentObject.put("date", highestStat.getDate());

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} finally {
			pm.close();
		}

		try {
			currentObject.write(out);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
