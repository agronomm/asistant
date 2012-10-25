package com.agronomm.asistant.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;

import com.agronomm.asistant.executor.CommandProcessorServlet;
import com.agronomm.asistant.voice.VoiceTolkerServlet;


public class WebService implements Runnable {

	Thread t = null;
	private String prPath;

	public WebService() {
		t = new Thread(this);
		t.start();
	}

	public Thread getThread() {
		return t;
	}
	
	public void setPath(String prPath) {
		this.prPath = prPath;
	}
	

	public synchronized void run() {
		System.out.println("[web] Begin server starting");

		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(prPath + "conf/main.property");
			prop.load(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Jetty

		try {
			String port = prop.getProperty("httpPort", "8080");
			com.agronomm.asistant.facade.ServletFacade.serverPort = port;

			// System.setProperty("org.eclipse.jetty.http.LEVEL", "WARN");

			// Тут определяется контекст для контроллера
			ServletContextHandler context0 = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context0.setContextPath("/control");
			context0.addServlet(new ServletHolder(new VoiceTolkerServlet()), "/speak/*");
			context0.addServlet(new ServletHolder(new CommandProcessorServlet()), "/processcommand/*");
			
			//context0.addServlet(new ServletHolder(new ControlHandler()), "/zwave/*");
			//context0.addServlet(new ServletHolder(new VideoHandler()), "/video/*");
			//context0.addServlet(new ServletHolder(new AudioHandler()), "/audio/*");
			//context0.addServlet(new ServletHolder(new DeviceValuesHandler()), "/device/values/*");
			//context0.addServlet(new ServletHolder(new DeviceHandler()), "/device/*");

			ServletContextHandler context1 = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context1.setContextPath("/");
			context1.addServlet(new ServletHolder(new HTMLHandler(prPath)), "/*");

			// Тут определяется контекст для статики (html, cs, картинки и т.д.)
			ResourceHandler resource_handler = new ResourceHandler();
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/static");
			context.setResourceBase("./www/");
			context.setClassLoader(Thread.currentThread().getContextClassLoader());
			context.setHandler(resource_handler);

			// Выставляем контексты в коллекцию
			ContextHandlerCollection contexts = new ContextHandlerCollection();
			contexts.setHandlers(new Handler[] { context0, context1, context });

			// Назначаем коллекцию контекстов серверу и запускаем его
			Server server = new Server(Integer.valueOf(port));
			server.setHandler(contexts);
			server.start();
			System.out.println("[web] Server started on port " + port);
			server.join();

		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Авторизация
	private static final SecurityHandler basicAuth(String username, String password, String realm) {

		HashLoginService l = new HashLoginService();
		l.putUser(username, Credential.getCredential(password), new String[] { "user" });
		l.setName(realm);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[] { "user" });
		constraint.setAuthenticate(true);

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
		csh.setAuthenticator(new BasicAuthenticator());
		csh.setRealmName("myrealm");
		csh.addConstraintMapping(cm);
		csh.setLoginService(l);

		return csh;
	}
}
