package com.seanpenney.universityphotos;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.seanpenney.universityphotos.PMF;

@SuppressWarnings("serial")
public class Query_UniversityServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONArray combined = new JSONArray();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Query query = pm.newQuery(Photo.class);
			List<Photo> allPhotos = (List<Photo>)query.execute();
			for (int i = 0; i < allPhotos.size(); i++) {
				Photo currentPhoto = allPhotos.get(i);
				JSONObject currentObject = new JSONObject();
				try {
					currentObject.put("name", currentPhoto.getName());
					currentObject.put("url", currentPhoto.getUrl());
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
