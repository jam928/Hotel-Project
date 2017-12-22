<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.io.*" %>

<!DOCTYPE html>
<html><head>
  <meta charset="ISO-8859-1">
  <title>Edit Customer Account</title>
  <link rel="stylesheet" type="text/css" href="css/style.css">
  <script type="text/javascript">      

function validate_form(){
    
    var phone_user = document.forms["formE"]["phone"].value;
    var phoneno = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;
    
    if(phone_user.match(phoneno)) {
        return true;
    } else {
        alert("Phone MUST be XXX-XXX-XXXX ");
        return false;
    }

}
  </script>
</head><body>
<h1 align="">User Account Edit</h1>
<ul>
<li><a class="active" href="Welcome.jsp">Home</a></li>
<li><a href="ReservationStatistics.html">Administrator</a></li>
</ul>
<%
try{
    Class.forName("com.mysql.jdbc.Driver"); 
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root",""); 
    Statement st= con.createStatement(); 
    String sql = "SELECT * FROM Customer where CID = " + session.getAttribute("CID") +";";

    ResultSet rs= st.executeQuery(sql);    

    while(rs.next()){  
    
%>

<form name="formE" action="WriteEdit.jsp" method="post" onsubmit="return validate_form()">
  <div align="" style="margin:1em;">
  <table>
    <tbody>
      <tr>
        <td>Name</td>
        <td><input name="name" placeholder="Name" required value="<%= rs.getString("Name")%>"> </td>
      </tr>
      <tr>
        <td>Address</td>
        <td><input name="address" placeholder="Address" value="<%= rs.getString("Address")%>"> required </td>
      </tr>
      <tr>
        <td>Phone number</td>
        <td><input type="tel" name="phone" placeholder="xxx-xxx-xxxx" required value="<%= rs.getString("Phone_no")%>"></td>
      </tr>
      <tr>
        <td>Email</td>
        <td><input type="email" placeholder="you@there.com" name="email" required value="<%= rs.getString("Email")%>"></td>
      </tr>
      <tr>
        <td>Password</td>
        <td><input type="password" name="password" required value="<%= rs.getString("Phone_no")%>"></td>
      </tr>
    </tbody>
  </table>
  </div>
      
  <div align=""> <input value="Update" type="submit" > </div>
  
</form>
<%  }

} catch(Exception e){
    e.printStackTrace();
}
%>

</body></html>