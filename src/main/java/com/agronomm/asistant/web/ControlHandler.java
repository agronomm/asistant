package com.agronomm.asistant.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControlHandler extends HttpServlet {
    private String greeting = "Hello World";

    public ControlHandler() {
    }

    public ControlHandler(String greeting) {
        this.greeting = greeting;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>" + greeting + "</h1>");
        response.getWriter().println("session: " + request.getSession(true).getId() + "<br>query: " + request.getQueryString() + "<br>dick:" + request.getParameter("dick"));
    }
}