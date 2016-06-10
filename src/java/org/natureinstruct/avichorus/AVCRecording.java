/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus;

import java.io.File;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Simple Bean to manage a avichorus recording file and accompanying spectrograms
 * 
 * @author pmorrill
 */
public class AVCRecording implements Serializable {
        protected long                          id;
        protected String                        name;
        protected String                        path;
        protected String                        type;
        protected String                        alternateName;
        protected ArrayList<String>             leftSpectrograms;
        protected ArrayList<String>             rightSpectrograms;
        protected ArrayList<String>             monoSpectrograms;
        protected HashMap<String,Object>        hm;
        protected String                        spectrogramPath;
        protected String                        projName;

        public Long getId() { return id; }
        public String getPath() { return path; }
        public String getName() { return name; }
        public String getProjectName() { return projName; }
        public String getAlternateName() { return alternateName; }
        public String getWave() { return type.contains("wav") ? "Y" : "N"; }
        public String getType() { return type; }
        public String getLeft() { return leftSpectrograms == null ? "" : leftSpectrograms.toString(); }
        public String getRight() { return rightSpectrograms == null ? "" : rightSpectrograms.toString(); }
        public String getMono() { return monoSpectrograms == null ? "" : monoSpectrograms.toString(); }
        public String getTempPath() { return spectrogramPath; }
        
        /**
         * Open record from database is all
         * 
         * @param ctx
         * @param id 
         */
        public AVCRecording(AVCContext ctx, Long id) {
                String sql = "SELECT recordings.*,projects.chName as projName,files.chMimeType FROM recordings "
                        + "INNER JOIN projects ON fkProjectID=nProjectID "
                        + "INNER JOIN files ON fkFileID=nFileID WHERE nRecordingID = ?";
                try (PreparedStatement st = ctx.getConnection().prepareStatement(sql)) {
                       st.setLong(1,id);
                       ResultSetMetaData md;
                       ResultSet rs = st.executeQuery();
                       if ( rs.next() ) {
                                hm = new HashMap<>();
                                md = rs.getMetaData();
                                for ( int i = 1; i < md.getColumnCount() + 1; i++ ) {
                                        hm.put(md.getColumnLabel(i),rs.getObject(i));
                                }
                       }
                       Integer idi = (Integer)hm.get("nRecordingID");
                       this.id = idi.longValue();
                       name = (String)hm.get("chName");
                       type = (String)hm.get("chMimeType");
                       projName = (String)hm.get("projName");
                       if ( type.contains("wav") ) getTempMPEG3(ctx);
                       this.path = ctx.getFileBasePath() + File.separator + name;
                       getSpectrograms(ctx);
                        spectrogramPath = getSpectrogramPath(ctx);
                } catch (Exception e) {
                        System.out.println("SQLException on extractRecord: "+e.getMessage());
               }
                 
        }
        
        /**
         * Return the spectrogram path for this recording
         * TODO: override this function to suite your needs
         * 
         * @param ctx
         * @return 
         */
        protected String getSpectrogramPath(AVCContext ctx) {
                String subPath = ctx.getTmpPath()+File.separator+"spectrograms" + 
                        File.separator+String.format("%04d",id);
                return subPath;
        }
        
        /**
         * If available, set the temporary mp3 file name into the bean;
         * TODO: override this function to suite your needs
         * 
         * @param ctx 
         */
        protected final void getTempMPEG3(AVCContext ctx) {
                if ( id > 0L ) {
                        String subPath = getSpectrogramPath(ctx);
                        File f = new File(subPath+File.separator+name.replace("wav","mp3"));
                        if ( f.isFile() ) alternateName = f.getName();
                }
        }
        
        /**
         * Construct a list of spectrograms currently in the recording's temporary
         * spectrogram directory
         * TODO: override this function to suite your needs
         * 
         * @param ctx 
         */
        protected final void getSpectrograms(AVCContext ctx) {
                if ( id > 0L ) {
                        String subPath = getSpectrogramPath(ctx);
                        File fp2 = new File(subPath);
                        if ( !fp2.isDirectory() ) fp2.mkdirs();
                        else {
                                monoSpectrograms = new ArrayList<>();
                                leftSpectrograms = new ArrayList<>();
                                rightSpectrograms = new ArrayList<>();
                                String[] files = fp2.list();
                                for ( String s : files ) {
                                        /* add to left and right and mono spectrograms as needed */
                                        if ( s.contains("M") ) monoSpectrograms.add(s);
                                        if ( s.contains("L") ) leftSpectrograms.add(s);
                                        if ( s.contains("R") ) rightSpectrograms.add(s);
                                }
                        }
                }
        }
        
        /**
         * Open a utility instance and create the specotrams for this recording
         * 
         * @param ctx 
         */
        public void createSpectrograms(AVCContext ctx) {
                if ( id == 0 ) return;
                SOXUtilities u = new SOXUtilities(ctx);
                u.createSpectrogramsToTemp(this);
        }
        
        /**
         * Open a utility instance and convert this recording to mp3
         * 
         * @param ctx 
         */
        public void convertToMPEG3(AVCContext ctx) {
                if ( id == 0 ) return;
                if ( !getType().contains("wav") ) return;
                SOXUtilities u = new SOXUtilities(ctx);
                u.convertToTempMPEG3(this);
        }
        
        /**
         * Static function returns an array of recording instances for
         * listing on a web page
         * 
         * @param ctx
         * @return 
         */
        static public AVCRecording[] listFiles(AVCContext ctx) {
                ArrayList<AVCRecording> aList = new ArrayList<>();
                String msg = new String();
                String sql = "SELECT files.chName,nRecordingID FROM recordings INNER JOIN files ON fkFileID=nFileID";
                try (Statement st = ctx.getConnection().createStatement()) {
                        ResultSet rs = st.executeQuery(sql);
                        while ( rs.next() ) {
                                long id = rs.getInt("nRecordingID");
                                String fname = rs.getString("chName");
                                AVCRecording rec = new AVCRecording(ctx,id);
                                aList.add(rec);
                        }
                } catch (Exception e) {
                        System.out.println("Exception in listing recordings: "+e.getMessage());
                }
                AVCRecording[] recs = new AVCRecording[1];
                return aList.toArray(recs);
        }
        
}
