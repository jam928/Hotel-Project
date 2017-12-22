<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Review Submission</title>
</head>
<body>
<%
	String hotelid = request.getParameter("hotelid");
	String stype = request.getParameter("stype");
	String textcomment = request.getParameter("textcomment");
	int rating = Integer.parseInt(request.getParameter("rating"));
	try{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root","");
		Statement st= con.createStatement();
		String sql = "insert into review(rating,textcomment,stype,hotelid,cid) " +
				"values(" + rating + ",\"" + textcomment + "\",\"" + stype + "\"," + hotelid + "," + session.getAttribute("CID") + ");";
		int i = st.executeUpdate(sql);
		if (i > 0){
		    out.println(" <a href=\"\" target=\"\"> Review Succeeds! </a>");
			String url = new String("../Welcome.jsp");
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", url);
		}
		else{
			out.println(" <a href=\"\" target=\"\"> Review failed! </a>");
		}
		st.close();
		con.close();
	}
	catch(Exception e) {
		e.printStackTrace();
	}
%>
</body>
</html>