<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="css/style.css">
<title>User Welcome Page</title>
</head>
<body>
<h1>User Home</h1>
<ul>
<li><a class="active" href="Welcome.jsp">Home</a></li>
<li><a href="ReservationStatistics.html">Administrator</a></li>
</ul>
<%
out.println(" Welcome " + session.getAttribute("name") + " !");
%>

<br>
<br>
<br>
<a href="Reservation/Reservation.html" target="_self"><span style="font-weight: bold;">Reserve A Room</span></a><br>
<br>
<br>
<a href="Reservation/Cancel.html" target="_self"><span style="font-weight: bold;">Cancel A Reservation</span></a><br>
<br>
<br>
<a href="Review/reservation_history.jsp" target="_self"><span style="font-weight: bold;">Leave A Review</span></a><br>
<br>
<br>
<a href="Edit.jsp" target="_self"><span style="font-weight: bold;">Edit User Account Information</span></a><br>

<br>
</body>
</html>


