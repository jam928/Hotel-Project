<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="../css/style.css">
<title>Review</title>
	<script type="text/javascript">
        function isFilled(){
            const text = document.forms["review_form"]["textcomment"].value;
            if (!text){
                alert("Comment should not be empty!");
                return false;
            }
            return true;
        }
	</script>
</head>
<body>
<%
String btype=request.getParameter("btype");
String hotelid=request.getParameter("hotelid");
%>
<h1 >Write review for <%=btype%> breakfast @ Hotel <%=hotelid%></h1>
<form name="review_form_breakfast" action="review_submit_breakfast.jsp?btype=<%=btype%>&hotelid=<%=hotelid%>" method="post" onsubmit="return isFilled()">
	<div >
		<textarea name="textcomment" rows="4" cols="50">
		</textarea>
	</div>
	<div style="height: 35px; position:relative; top:40px;" >
		<p style="display: inline-block">Rating: </p>
		<select style="display: inline-block" name="rating">
			<option value=1> 1 </option>
			<option value=2> 2 </option>
			<option value=3 selected="selected"> 3 </option>
		</select>
	</div>
	<div style="height: 35px; position:relative; top:50px;" ><input value="Submit!" type="submit"></div>
</form>
</body>
</html>