/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.natureinstruct.avichorus.AVCContext;
import org.natureinstruct.avichorus.AVCTag;
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
	protected void processRequest(HttpServletRequest request,HttpServletResponse response)
		throws ServletException,IOException {
                
                AVCContext ctx = new AVCContext(request,response);
		try ( PrintWriter out = response.getWriter() ) {
                        if ( !ctx.connect() ) {
                                out.println("Error setting up db connection.");
                                return;
                        }
                        SOXUtilities u = new SOXUtilities(ctx);
                        
                        String p = request.getServletPath();
                        String action = request.getPathInfo();
                        if ( p.equals("") && action.equals("/") ) {
				response.sendRedirect(request.getServletContext().getContextPath() + "/demo/list");
                                return;
                        }
                        String msg = "";
                        String id = request.getParameter("id");
			String tagIdStr;
			String recordingIdStr;
			Long tagId = null;
			AVCRecording recording = null;
			AVCTag tag = null;
                                        
                        switch ( action ) {
                                
                                case "/map-data":
                                        AVCRecording[] recordingList = AVCRecording.listFiles(ctx);
					JSONObject map = AVCRecording.getMap(ctx,"ABMI-TEST");
                                        request.setAttribute("recordingList",recordingList);
                                        response.setContentType("application/json;charset=UTF-8");
                                        map.writeJSONString(out);
                                        return;
                                        
                                case "/list":
                                        AVCRecording[] recordings = AVCRecording.listFiles(ctx);
                                        request.setAttribute("recordingList",recordings);
                                        request.setAttribute("pageTitle","Recording List");
					request.setAttribute("errorMsg",request.getSession().getAttribute("errorMsg"));
					request.getSession().removeAttribute("errorMsg");
                                        request.getServletContext().getRequestDispatcher("/WEB-INF/listRecordings.jsp").forward(request,response);
                                        return;

                                case "/play":
                                        Long rid;
                                        try {
                                                rid = Long.parseLong(request.getParameter("id"));
					} catch (NumberFormatException e) { msg = "Error opening recording id"; break; }
					recording = new AVCRecording(ctx,rid);
                                        request.setAttribute("recordingBean",recording);
					request.setAttribute("recordingTags",recording.listTags());
                                        request.setAttribute("displayWidth",1000);
                                        request.setAttribute("recordingImages",recording.getMonoImages());
                                        request.setAttribute("pageTitle","Play a Recording");
                                        request.getServletContext().getRequestDispatcher("/WEB-INF/audio.jsp").forward(request,response);
                                        return;

                                case "/spectrograms":
                                        /* create a specttrograms into tmp directory */
                                        if ( id != null ) {
                                                AVCRecording rec = new AVCRecording(ctx,Long.parseLong(id));
						if ( !rec.createSpectrograms(ctx) ) request.getSession().setAttribute("errorMsg","Failed to create a spectrogram");
                                        }
					response.sendRedirect(request.getServletContext().getContextPath() + "/demo/list");
                                        return;
                                        
                                case "/convert":
                                        /* convert a wav file into mp3, and store in temp directory */
                                        if ( id != null ) {
                                                AVCRecording rec = new AVCRecording(ctx,Long.parseLong(id));
						if ( !rec.convertToMPEG3(ctx) ) request.getSession().setAttribute("errorMsg","Failed to convert the file to MP3");
                                        }
					response.sendRedirect(request.getServletContext().getContextPath() + "/demo/list");
                                        return;

				case "/tag-form":
					String pc_id = request.getParameter("rid");
					tagIdStr = request.getParameter("tagid");
					AVCRecording rec = new AVCRecording(ctx,Long.parseLong(pc_id));
					try {
						tagId = Long.parseLong(tagIdStr);
					} catch (NumberFormatException ex) {

					}
					request.setAttribute("tagBean",new AVCTag(ctx,rec,tagId));
					Map<String,String> codes = new LinkedHashMap<>();

					request.setAttribute("speciesList",AVCTag.getSpeciesList(ctx,codes));
					request.setAttribute("speciesCodes",codes);
					request.setAttribute("alternateTaxa",AVCTag.getAltTaxaList());
					request.getServletContext().getRequestDispatcher("/WEB-INF/tagForm.jsp").forward(request,response);
					return;

				case "/tag":
					recordingIdStr = request.getParameter("fkRecordingID");
					tagIdStr = request.getParameter("nTagID");
					try {
						recording = new AVCRecording(ctx,Long.parseLong(recordingIdStr));
						tagId = Long.parseLong(tagIdStr);
					} catch (NumberFormatException ex) {
						tagId = null;
					}
					if ( recording != null ) {
						tag = new AVCTag(ctx,recording,tagId);
						if ( tag != null ) {
							long tid = tag.saveTagFromPost(request);
							response.setContentType("application/json");
							out.println("{\"id\":"+tid+",\"species\":\""+tag.getSpeciesName()+"\",\"recordingId\":" + recordingIdStr + "}");
							return;
						}
					}
					return;
				
				case "/tag-table":
					recordingIdStr = request.getParameter("id");
					try {
						recording = new AVCRecording(ctx,Long.parseLong(recordingIdStr));
						request.setAttribute("recordingTags",recording.listTags());
						request.getServletContext().getRequestDispatcher("/WEB-INF/tagTable.jsp").forward(request,response);
					} catch (NumberFormatException ex) { }
					return;
					
				case "/delete":
					tagId = Long.parseLong(request.getParameter("id"));
					tag = new AVCTag(ctx,recording,tagId);
					rid = tag.getRecordingId();
					tag.delete();
					response.sendRedirect(request.getServletContext().getContextPath() + "/demo/play?id=" + rid);
					return;
                        }
                
                        response.setContentType("text/html;charset=UTF-8");
                        /* TODO output your page here. You may use following sample code. */
                        out.println("<!DOCTYPE html>");
                        out.println("<html>");
                        out.println("<head>");
			out.println("<title>Servlet FileDemo</title>");
                        out.println("</head>");
                        out.println("<body>");
			out.println("<h1>Your request is not available yet</h1><p>" + msg + "</p>");
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
