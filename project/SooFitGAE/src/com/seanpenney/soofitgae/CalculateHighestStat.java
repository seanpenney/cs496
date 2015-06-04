package com.seanpenney.soofitgae;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.seanpenney.soofitgae.PMF;

@SuppressWarnings("serial")
public class CalculateHighestStat extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			double highestCalories = 0;
			double date = 0;
			Query query = pm.newQuery(PedometerStats.class);
			List<PedometerStats> allPedometerStats = (List<PedometerStats>) query
					.execute();
			for (int i = 0; i < allPedometerStats.size(); i++) {
				PedometerStats currentStat = allPedometerStats.get(i);
				double currentCalories = currentStat.getCalories();
				if (currentCalories > highestCalories) {
					highestCalories = currentCalories;
					date = currentStat.getDate();
				}
			}
			Query previousHighest = pm.newQuery(HighestStat.class);
			List<HighestStat> highestStatList = (List<HighestStat>) previousHighest
					.execute();
			pm.deletePersistentAll(highestStatList);
			previousHighest.closeAll();

			HighestStat max = new HighestStat();
			max.setCalories(highestCalories);
			max.setDate(date);
			pm.makePersistent(max);
			query.closeAll();
		} finally {
			pm.close();
		}
	}
}
