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

    String sql = "SELECT room.* , " + 
    "(SELECT discount FROM `offer-room` WHERE " +
    "(( '" + request.getParameter("checkin") + "'  BETWEEN sdate AND edate) OR ('" + request.getParameter("checkout") + "' BETWEEN  sdate AND edate)) " +
    "AND `offer-room`.hotelid = room.`HotelID` AND `offer-room`.room_no = room.`Room_no`) AS discount " + 
    "FROM room " +
    "WHERE room.`HotelID` = " + request.getParameter("hotelid") + " " +
    "AND NOT EXISTS ( " +
    "	SELECT * FROM reservation " +
    "	WHERE " +
    "		((reservation.`OutDate` BETWEEN '" + request.getParameter("checkin") + "' AND '" + request.getParameter("checkout") + "') OR " +
    "		(reservation.`InDate` BETWEEN '" + request.getParameter("checkin") + "' AND '" + request.getParameter("checkout") + "'))	" +
    "		AND reservation.`Room_no` = room.`Room_no` " +
    "		AND reservation.`HotelID` = room.`HotelID` " +
    ") " +
    "AND roomtype='" + request.getParameter("roomtype") + "' " +
    "ORDER BY room.`Room_no`;";
	
    // put results from db into json
    ResultSet rs= st.executeQuery(sql);     
    JSONArray jArray = new JSONArray(); 
    while(rs.next()){  
        JSONObject jo = new JSONObject();
        jo.put("Room_no", rs.getInt("Room_no"));
        jo.put("HotelID", rs.getInt("HotelID"));
        jo.put("Price", rs.getBigDecimal("Price"));        
        jo.put("Capacity", rs.getInt("Capacity"));        
        jo.put("Floor_no", rs.getInt("Floor_no"));
        jo.put("Description", rs.getString("Description")); 
        jo.put("Discount", rs.getInt("Discount"));
        jo.put("Image_HTML", rs.getString("Image_HTML"));
 
        jArray.put (jo);
    }      
    
    // output json    
    response.getWriter().write(jArray.toString());
    
    
} catch(Exception e){
    e.printStackTrace();
}
%>