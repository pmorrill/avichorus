/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.natureinstruct.avichorus;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Tag bean class
 * 
 * @author pmorrill
 */
public class AVCTag implements Serializable {
	AVCContext		ctx;
	AVCRecording	pCount;
	Long			id;
	Map<String,Object>		fieldValues;
	
	public Integer getImageWidth() { return pCount.spectrogramWidth; }
	public Integer getImageHeight() { return pCount.spectrogramHeight; }
	public Long getRecordingId() { return pCount.id; }
	public Long getFileId() { return pCount.id; }
	public Long getId() { return id; }
	public Integer getSpeciesSelected() { return 0; }
	public String getAlternateTaxa() { return ""; }
	public Integer getBird() { return 5; }
	public String getConfidenceLevel() { return "Fair"; }
	public String getComment() { return ""; }
	public String getChannels() { return "1"; }
	public String getDisplaySpeed() { return "80"; }
	public String getDisplayRange() { return "14"; }
	
	public AVCTag(AVCContext ctx,AVCRecording pCount,Long id) {
		this.ctx = ctx;
		this.pCount = pCount;
		openTag(id);
	}
	
	protected final void openTag(Long id) {
		fieldValues = null;
		if ( id == null ) return;
		try ( PreparedStatement st = ctx.getConnection().prepareStatement("SELECT * FROM tags WHERE nTagID = ?") ) {
			st.setLong(1,id);
			try ( ResultSet rs = st.executeQuery() ) {
				ResultSetMetaData rsmd = rs.getMetaData();
				fieldValues = new LinkedHashMap<>();
				for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					String fName = rsmd.getColumnLabel(i);
					fieldValues.put(fName,rs.getObject(fName));
				}		
			}
		} catch (Exception ex) {
			fieldValues = null;
		}
		if ( fieldValues != null ) this.id = (Long)fieldValues.get("nTagID");
	}
	
	public Long saveTagFromPost(HttpServletRequest request) {
		Map<String,Object> hm = new HashMap<>();
		Object tagId = null;
		if ( fieldValues == null || this.id == null ) {
			fieldValues = new HashMap<>();
		} else {
			tagId = fieldValues.get("nTagID");
		}
		String v = request.getParameter("fkSpecID");
		String alt = request.getParameter("chAltTaxa");
		try {
			fieldValues.put("fkRecordingID",Long.parseLong(request.getParameter("fkRecordingID")));
			fieldValues.put("fkSpecID",Long.parseLong(v));
			fieldValues.put("chAltTaxa","");
		} catch (Exception e) {
			if ( alt != null ) fieldValues.put("chAltTaxa",alt);
			fieldValues.put("fkSpecID",0);
		}
		
		/* try to determine the tagging box location and size,
			in seconds (width) and frequency (height) */
		try {
			String pos = request.getParameter("chBoxPosition");
			String size = request.getParameter("chBoxSize");
			String imageSize = request.getParameter("chImageSize");
			String imageRange = request.getParameter("fltImageRange");
			String imageSpeed = request.getParameter("fltImageSpeed");

			String[] posXY = null;
			String[] deltaXY = null;
			String[] imageWH = null;

			if ( pos != null && size != null ) {
				posXY = pos.split(";");
				deltaXY = size.split(";");
				imageWH = imageSize.split(";");

				Double freqRate = Double.parseDouble(imageWH[1]) / Double.parseDouble(imageRange);	/* px per kdb */

				/* get box start and width in seconds */
				Double startSec = Double.parseDouble(posXY[0]) / Double.parseDouble(imageSpeed);
				Double widthSec = Double.parseDouble(deltaXY[0]) / Double.parseDouble(imageSpeed);

				/* gte box range in frequency: store start freq (upper left) and range */
				Double startFreq = Double.parseDouble(imageRange) - (Double.parseDouble(posXY[1]) / freqRate);
				Double rangeFreq = Double.parseDouble(posXY[1]) / freqRate;

				fieldValues.put("fltStart",startSec);
				fieldValues.put("fltDuration",widthSec);
				fieldValues.put("fltBoxY",startFreq);
				fieldValues.put("fltHeight",rangeFreq);
			}
		} catch (Exception e) {

		}

		fieldValues.put("chConfidence",request.getParameter("chConfidence"));
		fieldValues.put("chComment",request.getParameter("chComment"));
		try {
			fieldValues.put("nBird",Long.parseLong(request.getParameter("nBird")));
		} catch (Exception e) {
			fieldValues.put("nBird",new Long(1));
		}
		
		System.out.println(fieldValues);
		if ( tagId == null ) insertTag(fieldValues);
		else updateTag(fieldValues);
		return this.id;
	}
	
	protected boolean insertTag(Map<String,Object> hm) {
		String sql = "INSERT INTO tags (fkRecordingID,fkSpecID,chAltTaxa,nBird,chConfidence,chComment,"
			+ "fltStart,fltDuration,fltBoxY,fltHeight,nChannel,bStereoTag) values (?,?,?,?,?,?,?,?,?,?"
			+ ",0,false)";
		this.id = 0L;
		try ( PreparedStatement st = ctx.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS) ) {
			st.setLong(1,(Long)hm.get("fkRecordingID"));
			st.setLong(2,(Long)hm.get("fkSpecID"));
			st.setString(3,(String)hm.get("chAltTaxa"));
			st.setLong(4,(Long)hm.get("nBird"));
			st.setString(5,(String)hm.get("chConfidence"));
			st.setString(6,(String)hm.get("chComment"));
			
			st.setDouble(7,(Double)hm.get("fltStart"));
			st.setDouble(8,(Double)hm.get("fltDuration"));
			st.setDouble(9,(Double)hm.get("fltBoxY"));
			st.setDouble(10,(Double)hm.get("fltHeight"));
			
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			if ( rs.next() ) this.id = new Long(rs.getInt(1));
			return true;
		} catch (Exception e) {
			System.out.println("Failed inserting record: "+e.getMessage());
		}
		return false;
	}
	
	protected boolean updateTag(Map<String,Object> hm) {
		
		return true;
	}

	public static Map<Integer,String> getSpeciesList(AVCContext ctx,Map<String,String> codes) {
		Map<Integer,String> hm = new LinkedHashMap<>();
		ArrayList<String> lCodes = new ArrayList<>();
		try ( PreparedStatement st = ctx.getConnection().prepareStatement("SELECT * FROM specs") ) {
			try ( ResultSet rs = st.executeQuery() ) {
				while (rs.next()) {
					Integer id = rs.getInt("nSpecID");
					hm.put(id,rs.getString("chCName"));
					codes.put(rs.getString("chAbbrev"),id.toString());
				}
			}
		} catch (Exception ex) {
			
		}
		return hm;
	}
	
	public static String[] getAltTaxaList() {
		String[] altTaxa = {"Unidentified bird",
			"Unidentified goose",
			"Unidentified duck",
			"Mallard / American Black Duck",
			"Unidentified grouse",
			"Common Gallinule / American Coot",
			"Unidentified shorebird",
			"Unidentified gull",
			"Unidentified tern",
			"Black-billed / Yellow-billed Cuckoo",
			"Unidentified Empidonax flycatcher",
			"Unidentified hummingbird",
			"Unidentified woodpecker",
			"Unidentified sapsucker",
			"Black-backed / American Three-toed Woodpecker",
			"Unidentified vireo",
			"Red-eyed / Philadelphia Vireo",
			"Unidentified warbler",
			"Golden-winged / Blue-winged Warbler",
			"Wilson\'s / Nashville Warbler",
			"Yellow-rumped / Orange-crowned Warbler",
			"Nashville / Tennessee Warbler",
			"Nashville / Yellow-rumped Warbler",
			"Chestnut-sided / Yellow Warbler",
			"Red-winged / Rusty Blackbird",
			"Purple Finch / Pine Grosbeak",
			"Unidentified redpoll",
			"Tailed Frog",
			"Plains Spadefoot",
			"Great Basin Spadefoot",
			"American Toad",
			"Western Toad",
			"Fowler\'s Toad",
			"Great Plains Toad",
			"Spring Peeper",
			"Gray Treefrog",
			"Pacific Treefrog",
			"Striped Chorus Frog",
			"Northern Cricket Frog",
			"Wood Frog"};
		return altTaxa;
	}

}
