package com.seanpenney.universityphotos;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.*;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.seanpenney.universityphotos.PMF;

@SuppressWarnings("serial")
public class Delete_OldServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Query query = pm.newQuery(Photo.class);
			List<Photo> allPhotos = (List<Photo>)query.execute();
			List<Photo> deletePhotos = new LinkedList<Photo>();
			for (int i = 0; i < allPhotos.size(); i++) {
				Photo currentPhoto = allPhotos.get(i);
				long photoTime = currentPhoto.getDate();
				if ((System.currentTimeMillis() - photoTime) > 300000) {// if photo age is greater than 5 minutes
					deletePhotos.add(currentPhoto);
				}
			}
			pm.deletePersistentAll(deletePhotos);
			query.closeAll();
		} finally {
			pm.close();
		}
	}
}
