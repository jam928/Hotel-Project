<%-- 
    Document   : get_hotels
    Created on : 5/2/17 6:32 pm
    Author     : scott
--%>

<%@ page contentType="application/json" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.JSONArray" %> 
<%@ page import="org.json.JSONObject" %> 
<%
try{
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
    Statement st= con.createStatement(); 
    String sql = "SELECT hotelid, CONCAT (street, \" in \", city) AS hotel_loc FROM hotel WHERE hotel.Country = '" + request.getParameter("country") + "' AND hotel.state='" + request.getParameter("state") + "';";    

    // put results from db into json
    ResultSet rs= st.executeQuery(sql);     
    JSONArray jArray = new JSONArray(); 
    while(rs.next()){  
        JSONObject jo = new JSONObject();
        jo.put("hotelid", rs.getInt("hotelid"));
        jo.put("hotel_loc", rs.getString("hotel_loc"));        
        jArray.put (jo);
    }      
    
    // output json    
    response.getWriter().write(jArray.toString());
    
    
} catch(Exception e){
    e.printStackTrace();
}
%>