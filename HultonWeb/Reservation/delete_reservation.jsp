<%-- 
    Document   : delete_reservation.jsp
    Created on : 5/6/2017
    Author     : scott
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
try{
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
    Statement st= con.createStatement(); 
    String sql = "DELETE FROM Reservation WHERE InvoiceNo = " + request.getParameter("invoiceno") + ";";
    
    // delete the reservation
    int i=st.executeUpdate(sql); 
    if (i > 0){
        out.println("DELETE Reservation Success");
    } else{
        out.println("DELETE Reservation Fail");
    }

    
} catch(Exception e){
    //e.printStackTrace();
    out.println("DELETE Reservation Fail, exception");
}
%>