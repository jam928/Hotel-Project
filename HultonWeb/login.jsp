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

try{
    String uemail = request.getParameter("email");
    String upassword = request.getParameter("password");
    int thisCID;
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 

    PreparedStatement pst = conn.prepareStatement("Select CID, name, email, password from customer where email=? ");
    pst.setString(1, uemail);
    ResultSet rs = pst.executeQuery();                        
    if(rs.next()){
        thisCID = rs.getInt("CID");
        session.setAttribute("CID", thisCID );
        session.setAttribute("name", rs.getString("name"));
        response.sendRedirect("Welcome.jsp");
    } else {
        out.println (" <a href=\"home.html\" target=\"_self\"> Log In Failed! Click Here to Try Again </a>");
    }
    
} catch(Exception e) {
    e.printStackTrace();
}

%>
</body>
</html>