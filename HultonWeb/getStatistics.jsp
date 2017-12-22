<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Results</title>
</head>
<body>
<h4 align="center">Reservation Statistics Results</h4>
<%
String beginDate = request.getParameter("beginDate"); 
String endDate = request.getParameter("endDate");
String option  = null;
option = request.getParameter("res_statistics");
String sql = null;
try {
    
	Class.forName("com.mysql.jdbc.Driver"); 
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");        
	Statement st= conn.createStatement();
	ResultSet rs = null;
        if ( option.equals("highest_rated_room")) {


            sql = " SELECT hotel.hotelid, hotel.`Street`, " +
            " (SELECT  room.roomtype" +
            "  FROM room " +
            "  INNER JOIN review ON room.Room_no = review.Room_no AND room.HotelID = review.HotelID" +
            "  INNER JOIN reservation ON room.`Room_no` = reservation.`Room_no` AND room.`HotelID` = reservation.`HotelID`" +
            "  WHERE room.HotelID = hotel.hotelid " +
            "  AND reservation.`InDate` >= '" + beginDate + "' " +
            "  AND reservation.`OutDate` <= '" + endDate + "'" +
            "  ORDER BY rating DESC LIMIT 1) AS roomtype" +
            " FROM hotel";


		
            rs = st.executeQuery(sql);
            // print out the results
            while(rs.next())
            {
                    // get the column name of type
                    String roomType = rs.getString("roomtype");
                    int hotelID = rs.getInt("HotelID");
                    String hoteladdr = rs.getString("Street");
                    
                    out.println(roomType + ", " + hotelID + ", " + hoteladdr);
            }
            
        } else if (option.equals ("five_best_customers")) { 

            sql = "SELECT c.name" +
                     " FROM customer as c" + 
                     " INNER JOIN" + 
                     " (SELECT CID, SUM(TotalAmt) as totalAmount" + 
                     " FROM reservation" + 
			 " WHERE ( reservation.InDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
			 " AND ( reservation.OutDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
                     " GROUP BY CID" +
                     " ORDER BY totalAmount DESC" + 
                     " LIMIT 5) AS C2" + 
                     " ON c.CID = c2.cid";
            rs = st.executeQuery(sql);
            // print out the results
		out.println("5 best customers" + "<br/>");
		out.println("<br/>");
            while(rs.next())
            {
			String customerName = rs.getString("name");
			out.println(customerName + "<br/>");
            }
            
        } else if (option.equals ("highest_rated_breakfast")) { 
	
            sql = "SELECT r.bType" + 
             " FROM review as r" +
             " INNER JOIN" +
                " (SELECT review.ReviewID, max(review.rating)" + 
                "  FROM review, reservation, hotel" + 
                "  WHERE (reservation.InDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
                " AND (reservation.OutDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
                "  AND reservation.HotelID = review.HotelID" + 
                "  AND reservation.room_No = review.room_No" + 
                "  AND review.HotelID = hotel.HotelID" +
                "  GROUP BY review.ReviewID) AS r2" + 
                " ON r.ReviewID = r2.reviewID";
            rs = st.executeQuery(sql);
		// print out the results
		out.println("Highest Rated Breakfast Type" + "<br/>");
		out.println("<br/>");
            while(rs.next())
            {
                    String breakfastType = rs.getString("bType");
                    out.println(breakfastType);
            }
            
        } else if (option.equals ("highest_rated_service")) { 

            sql = "SELECT r.sType" + 
             " FROM review as r" +
             " INNER JOIN" +
                " (SELECT review.ReviewID, max(review.rating)" + 
                "  FROM review, reservation, hotel" + 
                "  WHERE (reservation.InDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
                " AND (reservation.OutDate BETWEEN '" + beginDate + "' AND '" + endDate + "') " + 
                "  AND reservation.HotelID = review.HotelID" + 
                "  AND reservation.room_No = review.room_No" + 
                "  AND review.HotelID = hotel.HotelID" +
                "  GROUP BY review.ReviewID) AS r2" + 
                " ON r.ReviewID = r2.reviewID";
            rs = st.executeQuery(sql);
            // print out the results
		out.println("Highest Rated Service Type" + "<br/>");
		out.println("<br/>");
            while(rs.next())
            {
			String serviceType = rs.getString("sType");
			out.println(serviceType + "<br/>");
            }
	}
}
catch(Exception e){
	e.printStackTrace();
}
%>
<ul>
  <li><a class="active" href="ReservationStatistics.html">Back to the Report Page</a></li>
</ul>
</body>
</html>