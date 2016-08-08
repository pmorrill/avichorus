<%-- 
    Document   : audio
    Created on : 10-Jun-2016, 3:37:34 PM
    Author     : pmorrill
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:import url="includes/header.jsp" />
<script type="text/javascript">
  g_media = '<c:url value="${recordingBean.recordingUrl}" />';
  g_speed = ${recordingBean.displaySpeed};
  g_pc_length = ${recordingBean.length};
  g_pc_id = ${recordingBean.id};
</script>
<a href="<c:url value="/demo/list" />">&lt; &lt; Back to Recording List</a>
    <h1>${recordingBean.name}</h1>
    <p style="font-weight: bold">Length: ${recordingBean.length} s.</p>
    <p style="font-weight: bold">Url: ${recordingBean.recordingUrl}</p>
    <div id="spectrogram-wrap" style="width: ${displayWidth}px;">
      <div id="spectrogram-index-marker" style="left: 350px; height: ${recordingBean.spectrogramHeight}px;"></div>
      <img alt="Y-Axis" src="<c:url value="/spectrograms/tmp/yaxis-c1r14h257.png" />" style="position: absolute; top: 1px; left: 1px; z-index: 10">
      <div id="spectrogram-display" style="height: ${recordingBean.spectrogramHeight+40}px;">
        <div style="width: 100px; height: 50px; text-align: left; z-index: 0; position: absolute; left: 400px"><div id="pl_twrap"></div></div>
        <div id="spectrogram-images" style="left: ${recordingBean.leftOffset}px; width: ${recordingBean.spectrogramWidth+displayWidth-350}px">
    <c:forEach var="i" begin="0" end="${fn:length(recordingImages)-1}">
      <img src="${recordingImages[i]}" />
    </c:forEach>
      <img alt="X-Axis (${recordingBean.horizontalLegend})" src="<c:url value="/spectrograms/tmp/${recordingBean.horizontalLegend}" />">
      
      <c:if test="${recordingTags != null}">
        <c:forEach items="${recordingTags}" var="tag">
          <c:if test="${tag.fltStart > 0}">
    <div class="tag_box sel_box tmp_${tag.bTemporary} channel_${tag.nChannel} ${tag.color}" id="${tag.nTagID}" 
         style="left: ${tag.left}px; top: ${tag.top}px; width: ${tag.width-1}px; height: ${tag.height-1}px;" 
         title="View tag : ${tag.taxaName}">${tag.taxaName}&nbsp;</div>        
          </c:if>
        </c:forEach>
      </c:if>
      
        </div>
      </div>
    </div>
    <div id="media_position"></div>

    <div style="text-align: center; padding: 20px">
      <button onclick="avcr_play()">Play / Pause</button>&nbsp;&nbsp;&nbsp;
      <button onclick="avcr_restart()">Restart</button>
    </div>
    <!--div style="width: 100px; height: 50px; text-align: center; z-index: 0; padding-top: 40px"><div id="pl_twrap"></div></div-->
  
  <div id="tag-table">
    <c:import url="tagTable.jsp" />
  </div>
  </div>
  </body>
</html>
