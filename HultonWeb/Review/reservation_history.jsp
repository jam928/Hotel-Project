<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/style.css">
<title>Reservation History</title>
</head>
<body>
<h1 >Reservation History</h1>
<ul>
<li><a class="active" href="../Welcome.jsp">Home</a></li>
<li><a href="../ReservationStatistics.html">Administrator</a></li>
</ul>
<form name="form1" action="review.jsp" method="get">
    <div >
        <table style="display: inline-block;" border="1" >
            <tr>
                <th>Rooms:</th>
            </tr>
            <%
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                    Statement st = con.createStatement();
                    String sql = "select a.room_no, a.resdate, a.hotelid, b.roomtype " +
                            "from reservation a, room b " +
                            "where cid = " + session.getAttribute("CID") + " " +
                            "and a.hotelid = b.hotelid " +
                            "and a.room_no = b.room_no;";
                    ResultSet rs_room = st.executeQuery(sql);
                    while(rs_room.next()){
                        String room_no = rs_room.getString("room_no");
                        String hotelid = rs_room.getString("hotelid");
            %>
            <tr>
                <td>
                    <a href="review_room.jsp?room_no=<%=room_no%>&hotelid=<%=hotelid%>">
                        <%=rs_room.getString("resdate") %> <%=rs_room.getString("roomtype") %> <%=room_no%> at Hotel <%=hotelid%>
                    </a>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                    rs_room.close();
                    st.close();
                    con.close();
                }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        %>
        <table style="display: inline-block;" border="1" >
            <tr>
                <th>Breakfasts:</th>
            </tr>
            <%
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                    Statement st = con.createStatement();
                    String sql = "select a.btype, a.resdate, a.hotelid " +
                            "from reservation a, breakfast b " +
                            "where a.cid = " + session.getAttribute("CID") + " " +
                            "and a.btype = b.btype " +
                            "and a.hotelid = b.hotelid;";
                    ResultSet rs_b = st.executeQuery(sql);
                    while(rs_b.next()){
                        String btype = rs_b.getString("btype");
                        String hotelid = rs_b.getString("hotelid");
            %>
            <tr>
                <td><a href="review_breakfast.jsp?btype=<%=btype%>&hotelid=<%=hotelid%>"><%=rs_b.getString("resdate") %> <%=btype %> at Hotel <%=hotelid%></a></td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                    rs_b.close();
                    st.close();
                    con.close();
                }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        %>
        <table style="display: inline-block;" border="1" >
            <tr>
                <th>Services:</th>
            </tr>
            <%
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                    Statement st = con.createStatement();
                    String sql = "select a.stype, a.resdate, a.hotelid " +
                            "from reservation a, service s " +
                            "where a.cid = " + session.getAttribute("CID") + " " +
                            "and a.stype = s.stype " +
                            "and a.hotelid = s.hotelid;";                            
                    ResultSet rs_s = st.executeQuery(sql);
                    while(rs_s.next()){
                        String stype = rs_s.getString("stype");
                        String hotelid = rs_s.getString("hotelid");
            %>
            <tr>
                <td><a href="review_service.jsp?stype=<%=stype%>&hotelid=<%=hotelid%>"><%=rs_s.getString("resdate") %> <%=stype%> at Hotel <%=hotelid%></a></td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                    rs_s.close();
                    st.close();
                    con.close();
                }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        %>
    </div>
</form>
</body>
</html>