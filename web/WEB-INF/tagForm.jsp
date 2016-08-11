<%-- 
    Document   : tagForm
    Created on : 11-Jul-2016, 12:04:50 PM
    Author     : pmorrill
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div id="note-form">
  
  <p style="padding-bottom: 5px; width: 520px"><strong>Enter a four-letter species code name (AOU abbreviation), 
      or select a species from the dropdown box. Adjust the confidence level according to your confidence 
      in the identification. If possible, indicate the individual bird captured within the box you have outlined. You may optionally provide a comment on this tag.</strong></p>
      <form id="active-notes" action="<c:url value="/demo/tag" />" method="post" class="form_notes">
<input type="hidden" name="chBoxPosition" class="coords"  value="" />
<input type="hidden" name="chBoxSize" class="box_size"  value="" />
<input type="hidden" name="nChannels" value="${tagBean.channels}" />
<input type="hidden" name="chImageSize" value="${tagBean.imageWidth};${tagBean.imageHeight}" />
<input type="hidden" name="fltImageSpeed" value="${tagBean.displaySpeed}" />
<input type="hidden" name="fltImageRange" value="${tagBean.displayRange}" />
<input type="hidden" name="fkFileID" value="${tagBean.fileId}" />
<input type="hidden" name="fkRecordingID" value="${tagBean.recordingId}" />
<input type="hidden" name="nTagID" value="${tagBean.id}" />


<div class="sp-select">
<div class="sp-code">Species Code:&nbsp;<input id="sp-code" style="width: 40px" tabindex="1" />&nbsp;&nbsp;Or:&nbsp;&nbsp;</div>
<div class="sp-options">
  
<div class="active">
<select name="fkSpecID" id="spec-id" tabindex="1">
<option value="0"> ---- Select ---- </option>
    <c:forEach var="sp" items="${speciesList}">
      <option value="${sp.key}" <c:if test="${tagBean.speciesSelected == sp.key}">selected="selected"</c:if>>${sp.value}</option>
    </c:forEach>
</select>
</div>
</div> <!-- sp_options -->
</div> <!-- sp_select -->
<div style="text-align: center; clear: both">
If you cannot identify the species, select an explanation here, and/or add comments below:<br />
<select name="chAltTaxa" id="alt-taxa" tabindex="1">
<option value=""> ---- Select ---- </option>
<c:forEach var="i" begin="0" end="${fn:length(alternateTaxa)-1}">
  <option value="${alternateTaxa[i]}" <c:if test="${tagBean.alternateTaxa == alternateTaxa[i]}"> selected="selected"</c:if>>${alternateTaxa[i]}</option>
</c:forEach>
</select>
</div>
<div class="divide" style="text-align: right">
Individual: 
<select name="nBird" tabindex="1">
<c:forEach var="i" begin="1" end="8">
  <option value="${i}" <c:if test="${tagBean.bird == i}">selected</c:if>>${i}</option>
</c:forEach>
</select>
Confidence Level:&nbsp;&nbsp;
<select name="chConfidence" tabindex="1">
	<option value="High" <c:if test="${tagBean.confidenceLevel == 'High'}">selected</c:if>>High</option>
	<option value="Fair" <c:if test="${tagBean.confidenceLevel == 'Fair'}">selected</c:if>>Fair</option>
    <option value="Low" <c:if test="${tagBean.confidenceLevel == 'Low'}">selected</c:if>>Low</option>
</select>
</div>


Optional Comment (max 256 chars):<br />
<textarea name="chComment" tabindex="1">${tagBean.comment}</textarea>
<p style="text-align: right">
<input type="submit" class="btn_save" value="Save Tag" accesskey="s" tabindex="1" />&nbsp;&nbsp;
<input type="button" value="Cancel" onclick="close_ajax_dlg();" accesskey="x" tabindex="1" />
</p>
</form>

<script type="text/javascript">
var sp = {};
<c:forEach var="scode" items="${speciesCodes}">
  sp.${scode.key} = ${scode.value};
</c:forEach>
  
$(function() {
	$('#sp-code').keyup(function(e) {
      var v = $(e.target).val();
      if ( v ) var vv = eval('sp.'+v.toUpperCase());
      $('#spec-id').val(vv);
      $('#alt-taxa').val('');
    });
	$('#spec-id').change(function(e) { $('#alt-taxa').val(''); });
	$('#alt-taxa').change(function(e) { $('#spec-id').val(0); });
    
	$('.btn_save').click(function(e) {
		if ( $('#spec-id').val() == 0 && $('#alt-taxa').val() == '' ) { alert('You must select a species or an alternative taxon'); return false; }
	        return true;
	});
	$('.btn_delete').click(function(e) { avcr_remove_tag(${tagBean.id}); close_ajax_dlg(); });
	$('#note-form').keyup(function(e) { if ( e.keyCode == 13 ) { e.preventDefault(); $('.btn_save').click() }}); /* add 'Enter' handler */    
});  
</script>
  
</div>
