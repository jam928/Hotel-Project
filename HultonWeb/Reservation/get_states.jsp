<%-- 
    Document   : get_states
    Created on : Apr 29, 2017, 11:30:01 AM
    Author     : scott
--%>

<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.JSONArray" %> 
<%
try{
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
    Statement st= con.createStatement(); 
    String sql = "SELECT DISTINCT state FROM hotel WHERE country= '" + request.getParameter("country") + "' ORDER BY state;";

    // put results from db into json
    ResultSet rs= st.executeQuery(sql);     
    JSONArray jArray = new JSONArray(); 
    while(rs.next()){  
        jArray.put (rs.getString(1));
    }      
    
    // output json    
    response.getWriter().write(jArray.toString());
    
    
} catch(Exception e){
    e.printStackTrace();
}
%>