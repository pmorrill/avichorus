<%-- 
    Document   : listRecordings
    Created on : 5-Jun-2016, 11:09:12 AM
    Author     : pmorrill
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:import url="includes/header.jsp" />
    <h1>AVCR Recordings List: ABMI Demo Project</h1>
		<c:if test="${! empty errorMsg}">
			<div style="border: 1px solid #565656; padding: 3px; font-size: 1.2em; color: #880000; margin: 12px 0px; font-weight: bold">${errorMsg}</div>
		</c:if>
    <p>Running Sox commands to create spectrograms or convert a wav file to mp3 is time-consuming: you will have to implement these in
      commandline jobs rather than as done here. But, the links below and accompanying code demonstrate the commands needed to perform these tasks.<p/>
    <p>Make sure that you refer to the SoX documentation to see what changes you might want to make to the parameters when creating
      spectrograms.</p>
    <table>
      <tr>
        <th>ID</th>
        <th>Project</th>
        <th>Recording</th>
        <th>Type</th>
        <th>Spectrograms</th>
        <th>Tools</th>
      </tr>
			<c:forEach items="${recordingList}" var="r">
      <tr>
        <td rowspan="2">${r.id}</td>
        <td rowspan="2">${r.projectName}</td>
        <td title="${r.path}">${r.name}
          <c:if test='${r.wave == "Y" && r.alternateName != null }'>
            <br />&nbsp;Temp. MPEG version: ${r.alternateName}
          </c:if>
        </td>
        <td>${r.type}</td>
        <td>
          Left: ${r.left}<br />
          Right: ${r.right}<br />
          Mono: ${r.mono}<br />
        </td>
        <td class="ctr">
          <a href='<c:url value="/demo/spectrograms?id=${r.id}" />'>(Re)Create Spectrograms</a><br />
          <c:if test='${recordingList[i].wave == "Y"}'>
            <a href='<c:url value="/demo/convert?id=${r.id}" />'>(Re)Create MP3</a><br />
          </c:if>
          <a href='<c:url value="/demo/play?id=${r.id}" />'>Analyse</a>
        </td>
      </tr>
      <tr>
        <td colspan="4">
          Temp Path for this recording: <strong>${r.tempPath}</strong>
        </td>
    </c:forEach>
    </table>
    <p>The so-called 'Temp Path' for a recording is created by the software to store spectrograms and converted (mp3) files if necessary. The path is
      constructed based on the system temp drive: this directory logic will have to be replaced on your production system.</p>

<h2>You may select a recording from the map:</h2>
<div id="recordingMap">
  
</div>
</div>
  <script type="text/javascript">
$(function() {
  $.get("<c:url value="/demo/map-data" />",function(json) {
      map(json);
    })
});
    </script>
  </body>
</html>
