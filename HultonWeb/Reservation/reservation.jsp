<%-- 
    Document   : reservation.jsp
    Created on : 5/6/2017
    Author     : scott
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.JSONArray" %> 
<%
try{
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
    Statement st= con.createStatement(); 
    String sql = "insert into reservation (ResDate, TotalAmt, CID, Room_no, HotelID, InDate, OutDate, Cnumber) values " +
        "( now(), " + request.getParameter("total") + ", " + session.getAttribute("CID") + ", " + request.getParameter("Room_no") +
        ", " + request.getParameter("HotelID") + ", '" + request.getParameter("date_checkin") + "', '" + request.getParameter("date_checkout") +
        "', '" + request.getParameter("creditcard") + "')";

    // save reservation in db
    int i=st.executeUpdate(sql); 
    if (i > 0){
        out.println("INSERT Reservation Success");
    } else{
        out.println("INSERT Reservation Fail");
    }

    
} catch(Exception e){
    //e.printStackTrace();
    out.println("INSERT Reservation Fail");
}
%>