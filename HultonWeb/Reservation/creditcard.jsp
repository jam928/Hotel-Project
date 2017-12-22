<%-- 
    Document   : creditcard.jsp
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
    String sql = "insert into creditcard(Cnumber,ExpDate,SecCode,Type,Name,BillingAddr) values " +
            "('" + request.getParameter("creditcard") + "', '2017-05-18',123,'VISA', 'Customer name here','Customer address');";

    // save cc in db    
    int i=st.executeUpdate(sql); 
    if (i > 0){
        out.println("INSERT CC Success");
    } else{
        out.println("INSERT CC Fail");
    }

    
} catch(Exception e){
    //e.printStackTrace();
    out.println("INSERT CC Fail, possible PK conflict");
}
%>