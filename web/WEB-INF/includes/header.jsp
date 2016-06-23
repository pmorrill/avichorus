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
    <script type="text/javascript" src="http://maps.google.com/maps/api/js"></script>
    <script type="text/javascript" src="<c:url value="/scripts/jwplayer.js" />"></script>
    <script type='text/javascript' src='<c:url value="/scripts/avcr.js" />'></script>

    <style type="text/css">
      body > div { width: 95%; max-width: 1000px; margin: 0px auto; }
      table { width: auto; margin: 15px auto 10px 20px; background-color: #454545; }
      th { text-align: left; background-color: #efefde; padding: 5px; text-align: center; }
      td { text-align: left; background-color: #ffffff; padding: 5px; font-size: 0.8em }
      td.stat_0 { color: #880000; }
      td.stat_1 { color: #888888; }
      td.stat_2 { color: #888800; }
      td.stat_3 { color: #008800; }
      .ctr { text-align: center; }
      #signin { margin-left: 40px; }
    </style>
  </head>
  <body>
    <div>
