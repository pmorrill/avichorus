/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Context class establishes connection to database, reads the critical parameters
 * from the web.xml file; Re-write or replace this class to add support for user credentials,
 * connection pooling, etc
 * 
 * @author pmorrill
 */
public class AVCContext {
        private String                                dbString;
        private String                                soxCmd;
        private String                                soxiCmd;
        private Connection                            db;
        private String                                fileBasePath;
        private String                                  spectroBasePath;
        private String                                  spectroBaseUrl;
        private Long                                    userId;
                
        /**
         * Constructor pulls parameters from web.xml
         * 
         * @param request
         * @param response 
         */
        public AVCContext(HttpServletRequest request,HttpServletResponse response) {
                ServletContext servletCtx = request.getServletContext();
                dbString = servletCtx.getInitParameter("dbString");
                soxCmd = servletCtx.getInitParameter("soxCmd");
                soxiCmd = servletCtx.getInitParameter("soxiCmd");
                fileBasePath = servletCtx.getRealPath("/WEB-INF/test-recordings");
                fileBasePath = servletCtx.getRealPath("/recordings");
                spectroBasePath = servletCtx.getRealPath("/spectrograms");
                spectroBaseUrl = servletCtx.getContextPath()+"/spectrograms";
                userId = 1L; /* demo user is pmorrill : see users table in avichorus.sql */
        }
        
        /**
         * Open a connection to your database described in the dbString parameter
         * of web.xml
         * 
         * @return 
         */
        public boolean connect() {
                try {
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Class.forName("com.mysql.jdbc.Driver");
                        db = DriverManager.getConnection(dbString);
                } catch (ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                } catch (SQLException e) {
                        /* todo: handle db exception */
                        System.out.println(e.getMessage());
                        return false;
                }
                return true;
        }
        
        protected String getSoxCmd() { return soxCmd; }
        protected String getSoxiCmd() { return soxiCmd; }
        protected String getFileBasePath() { return fileBasePath; }
        protected String getSpectroBasePath() { return spectroBasePath; }
        protected String getSpectroBaseUrl() { return spectroBaseUrl; }
        protected String getTmpPath() { return System.getProperty("java.io.tmpdir"); }
        protected Connection getConnection() { return db; }
        protected Long getUserId() { return userId; }
}
