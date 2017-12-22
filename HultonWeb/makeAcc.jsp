<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Account Confirmation</title>
</head>
<body>
<%
String name=request.getParameter("name"); 
String address = request.getParameter("address");
String phone = request.getParameter("phone");
String email = request.getParameter("email");
String password = request.getParameter("password");
String userEmail = null;

try{
Class.forName("com.mysql.jdbc.Driver"); 
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
Statement st= con.createStatement(); 
ResultSet rs = st.executeQuery("SELECT email FROM customer WHERE email LIKE '"+email+"'");

String query_string = "insert into customer values (NULL,'"+name+"','"+address+"','"+phone+"','"+email+"','"+password+"')";
System.out.println(query_string);
int i=st.executeUpdate(query_string); 
if (i > 0){
		out.println(" <a href=\"home.html\" target=\"_self\"> Account Creation Successfull! Click Here to Log In</a>");
	}
	else{
		out.println(" <a href=\"AccountCreate.html\" target=\"_self\"> Account Creation Failed!Click Here to Try Again</a>");
	}
}
catch(Exception e){
	e.printStackTrace();
}

%>
</body>
</html>