<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Updated Customer Account</title>
<link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<%
String name=request.getParameter("name"); 
String address = request.getParameter("address");
String phone = request.getParameter("phone");
String email = request.getParameter("email");
String password = request.getParameter("password");

try{
Class.forName("com.mysql.jdbc.Driver"); 
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
String sql = "UPDATE customer SET name=?, address=?, phone_no=?, email=?, password=? WHERE CID = " + session.getAttribute("CID") + ";";
PreparedStatement statement = con.prepareStatement(sql);
statement.setString(1, name);
statement.setString(2, address);
statement.setString(3, phone);
statement.setString(4, email);
statement.setString(5, password);

int i=statement.executeUpdate(); 
if (i > 0){
		out.println(" <a href=\"Welcome.jsp\" target=\"_self\"> Account Information Edited. Click here to go back</a>");
	}
	else{
		out.println(" <a href=\"Edit.jsp\" target=\"_self\"> Account Edit Failed! Click Here to Try Again</a>");
	}
}
catch(Exception e){
	e.printStackTrace();
}

%>
</body>
</html>