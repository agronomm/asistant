package com.agronomm.asistant.web;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AudioHandler extends HttpServlet {


    public AudioHandler() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("audio/x-wav");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "Keep-Alive");

        //////////////////////////////////////

        InputStream in = null;
        ServletOutputStream out = null;

        try {

            System.err.println("[audio] Get stream!");

            // URL = http://localhost:8080/control/audio?cam=10
            URL cam = new URL("http://192.168.10." + request.getParameter("cam") + "/audio.cgi");
            URLConnection uc = cam.openConnection();
            out = response.getOutputStream();

            in = uc.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(in);

            byte[] bytes = new byte[8192];
            int bytesRead;

            BufferedOutputStream bos = null;
            bos = new BufferedOutputStream(out);

            int count = 0;

            while ((bytesRead = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, bytesRead);
                bos.flush();
                count++;
                System.err.println("COUNT: " + count + " BYTES: " + bytesRead);
            }

            System.err.println("READ = -1!");

        } catch (IOException ex) {
            // Disconnect detected
            System.err.println("[audio " + request.getParameter("cam") + "] Audio client disconnected");
            // Прерываем поток, иначе передача не будет остановена
            Thread.currentThread().interrupt();
        }
    }
}







