/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.imageio.ImageIO;

/**
 * A Simple Bean to manage a avichorus recording file and accompanying spectrograms
 * 
 * @author pmorrill
 */
public class AVCRecording implements Serializable {
        
	private final static String 		SUBPATH_FMT = "%04d";
	protected long 				id;
	protected String name;
	protected String path;
	protected String type;
	protected String alternateName;
	protected ArrayList<String> leftSpectrograms;
	protected ArrayList<String> rightSpectrograms;
	protected ArrayList<String> monoSpectrograms;
	protected ArrayList<String> monoSpectrogramUrls;
	protected ArrayList<String> leftSpectrogramUrls;
	protected ArrayList<String> rightSpectrogramUrls;
	protected AVCContext ctx;
	protected HashMap<String,Object> hm;

        /* path to the temp folder to place generated spectrograms (demo only) */
        protected String                        spectrogramTempPath;
        /* base path to the live spectrograms available for web use */
        protected String                        spectrogramBasePath;
        /* base url for the live spectrograms */
        protected String                        spectrogramBaseUrl;
        
        protected String                        projName;
        protected String                        recordingUrl;

        protected int                           spectrogramWidth;
        protected int                           spectrogramHeight;
        
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
        
        public String[] getMonoImages() {
                String[] images = new String[monoSpectrogramUrls.size()];
                return monoSpectrogramUrls.toArray(images);
        }
        public String[] getLeftImages() {
                String[] images = new String[leftSpectrogramUrls.size()];
                return leftSpectrogramUrls.toArray(images);
        }
        public String[] getRightImages() {
                String[] images = new String[rightSpectrogramUrls.size()];
                return rightSpectrogramUrls.toArray(images);
        }
        public int getSpectrogramWidth() { return spectrogramWidth; }
        public int getSpectrogramHeight() { return spectrogramHeight; }
        
        public String getTempPath() { return spectrogramTempPath; }
        public String getSpectrogramPath() { return spectrogramBasePath; }
        public String getSpectrogramUrl() { return spectrogramBaseUrl; }
        public String getRecordingUrl() { return recordingUrl; }
        public Double getLength() {
                SOXUtilities sx = new SOXUtilities(ctx);
                return sx.getRecordingLength(path);
        }
        public int getLeftOffset() { return 350; }
        
        /**
         * The display speed is 80 px per second on all of our default spectrograms
         * 
         * @return 
         */
        public Double getDisplaySpeed() { return SOXUtilities.DEFAULT_SP_RES; }
        
        /**
	 * The horizontal axis length is no more than 2 s longer than the recording
	 * itself
         * 
         * @return 
         */
        public String getHorizontalLegend() {
                Double x = getLength();
                if ( x == null ) return "";
                int c = (int)Math.floor(x / 2) * 2 + 2;
                String a = "xaxis-r80L" + c + "s.png";
                return a;
        }
        
        /**
         * Open record from database is all
         * 
         * @param ctx
         * @param id 
         */
        public AVCRecording(AVCContext ctx, Long id) {
                this.ctx = ctx;
                String sql = "SELECT recordings.*,projects.chName as projName,files.chMimeType FROM recordings "
                        + "INNER JOIN projects ON fkProjectID=nProjectID "
                        + "INNER JOIN files ON fkFileID=nFileID WHERE nRecordingID = ?";
		try ( PreparedStatement st = ctx.getConnection().prepareStatement(sql) ) {
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
                        Integer idi = (Integer) hm.get("nRecordingID");
                       	this.id = idi.longValue();
                        name = (String) hm.get("chName");
                        type = (String) hm.get("chMimeType");
                        projName = (String) hm.get("projName");
			String subP = String.format(AVCRecording.SUBPATH_FMT,this.id);
                       	this.path = ctx.getFileBasePath() + File.separator + name;
			if ( type.contains("wav") ) {
                                getTempMPEG3(ctx);
                                recordingUrl = "/recordings/" + name.replace("wav","mp3");
                        } else recordingUrl = "/recordings/" + name;
                       	getSpectrograms(ctx);
                        spectrogramTempPath = getSpectrogramPath(ctx);
                        spectrogramBasePath = ctx.getSpectroBasePath() + File.separator + subP;
                        spectrogramBaseUrl = ctx.getSpectroBaseUrl() + "/" + subP;
                        buildSpectrogramUrls();
                } catch (Exception e) {
			System.out.println("SQLException on extractRecord: " + e.getMessage());
                }
        }
        
        /**
         * Build a list of urls to all the spectrograms
         * 
         */
        protected void buildSpectrogramUrls() {
                File fp = new File(spectrogramBasePath);
                monoSpectrogramUrls = new ArrayList<>();
                leftSpectrogramUrls = new ArrayList<>();
                rightSpectrogramUrls = new ArrayList<>();
                if ( fp.isDirectory() ) {
                        String[] fs = fp.list();
                        for ( String s : fs ) {
                                File sf = new File(s);
				if ( sf.getName().startsWith("M") ) {
                                        monoSpectrogramUrls.add(spectrogramBaseUrl + "/" + sf.getName());
                                        try {
                                                BufferedImage readImage = null;
                                                readImage = ImageIO.read(new File(spectrogramBasePath + File.separator + s));
                                                spectrogramHeight = readImage.getHeight();
                                                spectrogramWidth += readImage.getWidth();
                                        } catch (Exception e) { spectrogramWidth = 0; spectrogramHeight = 0; }
                                }
				else if ( sf.getName().startsWith("L") ) leftSpectrogramUrls.add(spectrogramBaseUrl + "/" + sf.getName());
				else if ( sf.getName().startsWith("R") ) rightSpectrogramUrls.add(spectrogramBaseUrl + "/" + sf.getName());
                        }
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
		String subPath = ctx.getTmpPath() + File.separator + "spectrograms"
			+ File.separator + String.format(AVCRecording.SUBPATH_FMT,id);
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
			File f = new File(subPath + File.separator + name.replace("wav","mp3"));
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
	 * Open a utility instance and create the spectrograms for this recording
         * 
         * @param ctx 
         */
	public boolean createSpectrograms(AVCContext ctx) {
		if ( id == 0 ) return false;
                SOXUtilities u = new SOXUtilities(ctx);
		return !u.createSpectrogramsToTemp(this);
        }
        
        /**
         * Open a utility instance and convert this recording to mp3
         * 
         * @param ctx 
         */
	public boolean convertToMPEG3(AVCContext ctx) {
		if ( id == 0 ) return false;
		if ( !getType().contains("wav") ) return false;
                SOXUtilities u = new SOXUtilities(ctx);
		return u.convertToTempMPEG3(this);
	}

	public List<Map<String,Object>> listTags() {
		List<Map<String,Object>> aList = new ArrayList();
		String sql = "SELECT a.*,b.chCName FROM tags a LEFT JOIN specs b ON a.fkSpecID = b.nSpecID WHERE a.fkRecordingID = ? ORDER BY a.fltStart";
		try ( PreparedStatement st = ctx.getConnection().prepareStatement(sql) ) {
			st.setLong(1,this.id);
			ResultSet rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while ( rs.next() ) {
				Map<String,Object> hm = new HashMap<>();
				String cname = null;
				for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					String s = rsmd.getColumnName(i);
					Object o = rs.getObject(i);
					hm.put(s,o);
					if ( s.equals("chCName") && o != null ) cname = (String)o;
        }
        
				String tname = cname == null || cname.isEmpty() ? (String)hm.get("chAltTaxa") : cname;
				if ( tname == null || tname.isEmpty() ) continue;
				hm.put("taxaName",tname);
				
				/* the following need to be calculated */
				Float s = (Float)hm.get("fltStart") * 80;
				hm.put("left",s);
				Float d = (Float)hm.get("fltDuration") * 80;
				hm.put("width",d);
				Float t = (14 - (Float)hm.get("fltBoxY")) * spectrogramHeight/14;
				hm.put("top",t);
				Float h = (Float)hm.get("fltHeight") * spectrogramHeight/14;
				hm.put("height",h);
				hm.put("color","recon_3");
				aList.add(hm);
			}
		} catch (Exception ex) {
			return null;
		}
		return aList;
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
		try ( Statement st = ctx.getConnection().createStatement() ) {
                        ResultSet rs = st.executeQuery(sql);
                        while ( rs.next() ) {
                                long id = rs.getInt("nRecordingID");
                                String fname = rs.getString("chName");
                                AVCRecording rec = new AVCRecording(ctx,id);
                                aList.add(rec);
                        }
                } catch (Exception e) {
			System.out.println("Exception in listing recordings: " + e.getMessage());
                }
                AVCRecording[] recs = new AVCRecording[1];
                return aList.toArray(recs);
        }
        
        static public JSONObject getMap(AVCContext ctx,String project) {
                JSONObject j = new JSONObject();
                boolean headers = false;
                String sql = "SELECT * FROM recordings a INNER JOIN projects b ON fkProjectID = nProjectID WHERE b.chAbbrev = ?";
                j.put("project",project);
                j.put("zoom",4);
                j.put("centerx",-102);
                j.put("centery",50.9);
                j.put("cluster",false);
                double xc = 0, yc = 0;
		try ( PreparedStatement st = ctx.getConnection().prepareStatement(sql) ) {
                        st.setString(1,project);
                        Long pid = new Long(0);
                        ResultSet rs = st.executeQuery();
                        JSONArray j3 = new JSONArray();
                        int i = 0;
                        while ( rs.next() ) {
                                i++;
                                pid = rs.getLong("nProjectID");
                                JSONObject j2 = new JSONObject();
                                xc += rs.getDouble("fltLongitude");
                                j2.put("longitude",rs.getDouble("fltLongitude"));
                                yc += rs.getDouble("fltLatitude");
                                j2.put("latitude",rs.getDouble("fltLatitude"));
                                String dtt = rs.getDate("dtDate").toString();
                                j2.put("recording",dtt);
                                j2.put("province",rs.getString("chProvince"));
                                j2.put("habitat",rs.getString("chHabitat"));
                                j2.put("siteName",rs.getString("chSiteName"));
                                j2.put("timeClass",rs.getString("chTimeClass"));
                                j2.put("recordingId",rs.getLong("nRecordingID"));
                                j3.add(j2);
                        }
                        j.put("markers",j3);
                        j.put("project_id",pid);
                        j.put("centerx",xc / i);
                        j.put("centery",yc / i);
                } catch (Exception e) { }
                return j;
        }
        
}
