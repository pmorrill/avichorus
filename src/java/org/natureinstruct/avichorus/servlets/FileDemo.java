/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.natureinstruct.avichorus.AVCContext;
import org.natureinstruct.avichorus.AVCRecording;
import org.natureinstruct.avichorus.SOXUtilities;

/**
 *
 * @author pmorrill
 */
@WebServlet(name = "FileDemo", urlPatterns = {"","/demo/*"})
public class FileDemo extends HttpServlet {

        /**
         * Processes requests for both HTTP <code>GET</code> and
         * <code>POST</code> methods.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                
                AVCContext ctx = new AVCContext(request,response);
                try (PrintWriter out = response.getWriter()) {
                        if ( !ctx.connect() ) {
                                out.println("Error setting up db connection.");
                                return;
                        }
                        SOXUtilities u = new SOXUtilities(ctx);
                        
                        String p = request.getServletPath();
                        String action = request.getPathInfo();
                        if ( p.equals("") && action.equals("/") ) {
                                response.sendRedirect(request.getServletContext().getContextPath()+"/demo/list");
                                return;
                        }
                        String msg = "";
                        String id = request.getParameter("id");
                                        
                        switch ( action ) {
                                
                                case "/list":
                                        AVCRecording[] recordings = AVCRecording.listFiles(ctx);
                                        request.setAttribute("recordingList",recordings);
                                        request.getServletContext().getRequestDispatcher("/WEB-INF/listRecordings.jsp").forward(request,response);
                                        return;

                                case "/spectrograms":
                                        /* create a specttrograms into tmp directory */
                                        if ( id != null ) {
                                                AVCRecording rec = new AVCRecording(ctx,Long.parseLong(id));
                                                System.out.println("Creating spectrogram on ID "+rec.getId());
                                                rec.createSpectrograms(ctx);
                                        }
                                        response.sendRedirect(request.getServletContext().getContextPath()+"/demo/list");
                                        return;
                                        
                                case "/convert":
                                        /* convert a wav file into mp3, and store in temp directory */
                                        if ( id != null ) {
                                                AVCRecording rec = new AVCRecording(ctx,Long.parseLong(id));
                                                rec.convertToMPEG3(ctx);
                                        }
                                        response.sendRedirect(request.getServletContext().getContextPath()+"/demo/list");
                                        break;
                                        
                                case "/play":
                                        msg = "Viewing / analysing spectrogram not available";
                                        
                                        /* view the recording */
                                        break;
                        }
                
                
                        response.setContentType("text/html;charset=UTF-8");
                        /* TODO output your page here. You may use following sample code. */
                        out.println("<!DOCTYPE html>");
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<title>Servlet FileDemo</title>");                        
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<h1>Your request is not available yet</h1><p>"+msg+"</p>");
                        out.println("</body>");
                        out.println("</html>");
                }
        }

        // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
        /**
         * Handles the HTTP <code>GET</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                processRequest(request, response);
        }

        /**
         * Handles the HTTP <code>POST</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
                processRequest(request, response);
        }

        /**
         * Returns a short description of the servlet.
         *
         * @return a String containing servlet description
         */
        @Override
        public String getServletInfo() {
                return "Short description";
        }// </editor-fold>

}
