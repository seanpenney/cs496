package com.seanpenney.universityphotos;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.seanpenney.universityphotos.PMF;

@SuppressWarnings("serial")
public class Add_UniversityServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		String university_name = req.getParameter("name");
		String university_photo = req.getParameter("url");

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Photo photoObj = new Photo();
			photoObj.setName(university_name);
			photoObj.setUrl(university_photo);
			long currentTime = System.currentTimeMillis();
			photoObj.setDate(currentTime);
			pm.makePersistent(photoObj);
		} finally {
			pm.close();
		}

		JSONObject json = new JSONObject();
		try {
			json.put("name", university_name);
			json.put("photo", university_photo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print(json.toString());
	}
}
