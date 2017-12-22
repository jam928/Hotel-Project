<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/style.css">
<title>Review</title>
</head>

<body>
<h1 >Service Review</h1>
<form name="form">
    <div >
        <table style="display: inline-block;" border="1" >
            <%
                String stype = request.getParameter("stype");
                String hotelid = request.getParameter("hotelid");
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                    Statement st = con.createStatement();
                    ResultSet review_s = st.executeQuery("select textcomment " +
                            "from review a " +
                            "where a.stype=\"" + stype + "\" " +
                            "and a.hotelid=" + hotelid + " " +
                            "and cid=" + session.getAttribute("CID") + ";");
                    while(review_s.next()){
            %>
            <tr>
                <td>
                    <%=review_s.getString("textcomment")
                    %>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                review_s.close();
                st.close();
                con.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        %>
        <div >
            <button type="button"><a href="review_form_service.jsp?stype=<%=stype%>&hotelid=<%=hotelid%>">Write a new review!</a></button>
        </div>
    </div>
</form>
</body>
</html>