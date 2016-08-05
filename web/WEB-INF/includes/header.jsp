<%-- 
    Document   : header
    Created on : 22-May-2016, 7:48:53 AM
    Author     : pmorrill
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Avichorus Demo - ${pageTitle}</title>
    <script type='text/javascript' src='http://www.natureinstruct.org/common/jquery/jquery-1.9.0.js'></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/styles/media.css" />" />
    <link rel="stylesheet" type="text/css" href="<c:url value="/styles/taggingDlg.css" />" />
    <script type="text/javascript" src="http://maps.google.com/maps/api/js"></script>
    <script type="text/javascript" src="<c:url value="/scripts/jwplayer.js" />"></script>
    <script type='text/javascript' src='<c:url value="/scripts/avcr.js" />'></script>
    <script type='text/javascript' src='<c:url value="/scripts/taggingDlg.js" />'></script>

  </head>
  <body>
    <div id="wrapper">
