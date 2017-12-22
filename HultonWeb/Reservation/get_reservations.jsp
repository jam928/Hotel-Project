<%-- 
    Document   : get_rooms
    Created on : May 2, 2017, 6:30 pm
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
    String sql = "SELECT Reservation.* , Room.Description FROM reservation " + 
            "INNER JOIN room ON reservation.Room_no = Room.Room_no " + 
            "WHERE CID = " + session.getAttribute("CID") + " ORDER BY InDate DESC;";
             
    // put results from db into json
    ResultSet rs= st.executeQuery(sql);     
    JSONArray jArray = new JSONArray(); 
    while(rs.next()){  
        JSONObject jo = new JSONObject();
        jo.put("InvoiceNo", rs.getInt("InvoiceNo"));
        jo.put("Room_no", rs.getInt("Room_no"));
        jo.put("Description", rs.getString("Description"));
        jo.put("HotelID", rs.getInt("HotelID"));
        jo.put("InDate", rs.getDate("InDate"));
        jo.put("OutDate", rs.getDate("OutDate"));
        
        jArray.put (jo);
    }      
    
    // output json    
    response.getWriter().write(jArray.toString());
    
    
} catch(Exception e){
    e.printStackTrace();
}
%>