<%-- 
    Document   : tagTable
    Created on : 8-Aug-2016, 9:39:03 AM
    Author     : pmorrill
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<table style="width: 95%; margin: 10px auto">
  <tr>
    <th>Species</th>
    <th>Bird</th>
    <th>Confidence</th>
    <th>Start (s)</th>
    <th>Song Duration (s)</th>
    <th></th>
  </tr>
<c:if test="${recordingTags != null}">
  <c:forEach items="${recordingTags}" var="tag">
    <tr>
      <td>${tag.taxaName}</td>
      <td>${tag.nBird}</td>
      <td>${tag.chConfidence}</td>
      <td>${tag.fltStart}</td>
      <td>${tag.fltDuration}</td>
      <td style="text-align: center"><a href="javascript:;" onclick="edit_tag(${tag.nTagID})">Edit</a>&nbsp;|&nbsp;
        <a href="<c:url value="/demo/delete" />?id=${tag.nTagID}">Delete</a></td>
    </tr>
  </c:forEach>
</c:if>
</table>
