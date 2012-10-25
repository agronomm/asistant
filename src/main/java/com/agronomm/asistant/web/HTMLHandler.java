package com.agronomm.asistant.web;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.naming.spi.DirectoryManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Парсинг страниц

public class HTMLHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String prPath;
	public HTMLHandler(String prPath) throws IOException {
		this.prPath = prPath;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String path = request.getPathInfo();

		if (path.equals("/")) {
			path = "/index.html";
		}
		
		try {
			/* first, get and initialize an engine */
			VelocityEngine ve = new VelocityEngine();
			ve.init();

			/* next, get the Template */
			System.out.println(ve.resourceExists(prPath+"/resources/webContent/views" + path));
			System.out.println(ve.resourceExists("../resources/webContent/views" + path));
			System.out.println(ve.resourceExists(prPath+"/views" + path));
			System.out.println(ve.resourceExists("." + path));
			System.out.println(ve.resourceExists(prPath+"/webContent/views" + path));
			System.out.println(ve.resourceExists("../webContent/views" + path));
			System.out.println(ve.resourceExists("./resources/webContent/views" + path));
			System.out.println(ve.resourceExists("./resources/webContent/views" + path));
			ve.resourceExists(path);
			Template t = ve.getTemplate("./webContent/views" + path, "UTF-8");

			/* create a context and add data */
			VelocityContext context = new VelocityContext();

			// Данные для страниц - в шаблонизатор
			String fileName = path.substring(path.lastIndexOf('/') + 1, path.length());
			String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));

			PagesContext pc = new PagesContext();
			HashMap<Object, Object> pagesKeysValues = pc.getContext(fileNameWithoutExtn);

			Iterator<Map.Entry<Object, Object>> iterPages = pagesKeysValues.entrySet().iterator();
			while (iterPages.hasNext()) {
				Map.Entry<Object, Object> entry = iterPages.next();
				context.put(entry.getKey().toString(), entry.getValue());
			}

			/* now render the template into a StringWriter */
			StringWriter writer = new StringWriter();
			t.merge(context, writer);

			/* show the World */
			response.getWriter().println(writer.toString());
		}
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			System.err.println("[web] Error in generation HTMLHandler: " + path);
			e.printStackTrace();
		}
	}
}