<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'index.jsp' starting page</title>
</head>

<body>
	<form method="get" action="${pageContext.request.contextPath }/servlet/HandlerServlet">
		<input type="text"   name="username"><br/> 
		<input type="submit" value="get">
	</form>

	<form method="post" action="${pageContext.request.contextPath }/servlet/HandlerServlet">
		<input type="text"   name="username"><br/>
		<textarea rows="5" cols="40" name="desc"></textarea><br/>
		<input type="submit" value="post">
	</form>

</body>
</html>
