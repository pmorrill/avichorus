<%-- 
    Document   : listRecordings
    Created on : 5-Jun-2016, 11:09:12 AM
    Author     : pmorrill
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<c:import url="includes/header.jsp" />
    <h1>AVCR Recordings and Test Functions</h1>
    <p>Running Sox commands to create spectrograms or convert a wav file to mp3 is time-consuming: you will have to implement these in
      commandline jobs rather than as done here. But, the links below and accompanying code demonstrate the commands needed to perform these tasks.<p/>
    <p>Make sure that you refer to the SoX documentation to see what changes you might want to make to the parameters when creating
      spectgrams.</p>
    <table>
      <tr>
        <th>ID</th>
        <th>Project</th>
        <th>Recording</th>
        <th>Type</th>
        <th>Spectrograms</th>
        <th>Tools</th>
      </tr>
    <c:forEach var="i" begin="0" end="${fn:length(recordingList)-1}">
      <tr>
        <td rowspan="2">${recordingList[i].id}</td>
        <td rowspan="2">${recordingList[i].projectName}</td>
        <td title="${recordingList[i].path}">${recordingList[i].name}
          <c:if test='${recordingList[i].wave == "Y" && recordingList[i].alternateName != null }'>
            <br />&nbsp;Temp. MPEG version: ${recordingList[i].alternateName}
          </c:if>
        </td>
        <td>${recordingList[i].type}</td>
        <td>
          Left: ${recordingList[i].left}<br />
          Right: ${recordingList[i].right}<br />
          Mono: ${recordingList[i].mono}<br />
        </td>
        <td class="ctr">
          <a href='<c:url value="/demo/spectrograms?id=${recordingList[i].id}" />'>(Re)Create Spectrograms</a><br />
          <c:if test='${recordingList[i].wave == "Y"}'>
            <a href='<c:url value="/demo/convert?id=${recordingList[i].id}" />'>(Re)Create MP3</a><br />
          </c:if>
          <a href='<c:url value="/demo/play?id=${recordingList[i].id}" />'>Analyse</a>
        </td>
      </tr>
      <tr>
        <td colspan="4">
          Temp Path for this recording: <strong>${recordingList[i].tempPath}</strong>
        </td>
    </c:forEach>
    </table>
    <p>The so-called 'Temp Path' for a recording is created by the software to store spectrograms and converted (mp3) files if necessary. The path is
      constructed based on the system temp drive: this directory logic will have to be replaced on your production system.</p>
</div>
  </body>
</html>
