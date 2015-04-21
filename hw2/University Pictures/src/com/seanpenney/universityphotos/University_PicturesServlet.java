package com.seanpenney.universityphotos;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class University_PicturesServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Welcome to the university photo application");
	}
}
