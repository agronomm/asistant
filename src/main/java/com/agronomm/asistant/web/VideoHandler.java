package com.agronomm.asistant.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoHandler extends HttpServlet {


    public VideoHandler() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // правильный boundary - самое главное
        response.setContentType("multipart/x-mixed-replace; boundary=--video boundary--");
        response.setStatus(HttpServletResponse.SC_OK);

        //////////////////////////////////////

        InputStream in = null;
        OutputStream out = null;

        try {

            System.err.println("[cam " + request.getParameter("cam") + "] Client connected");

            // URL = http://localhost:8080/control/video?cam=10
            URL cam = new URL("http://192.168.10." + request.getParameter("cam") + "/video.cgi");
            URLConnection uc = cam.openConnection();
            out = new BufferedOutputStream(response.getOutputStream());

            in = uc.getInputStream();
            byte[] bytes = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes, 0, bytesRead);
            }

        } catch (IOException ex) {
            // Disconnect detected
            System.err.println("[cam " + request.getParameter("cam") + "] Client disconnected");
            // Прерываем поток, иначе передача не будет остановена
            Thread.currentThread().interrupt();
        }
    }
}







