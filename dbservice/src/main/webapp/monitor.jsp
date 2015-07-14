<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.db.pool.*"%>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
Počet otevrených připojení: <%=SQLExecutor.getNumberOfConnection()%>
<br/>
Počet používanných:         <%=SQLExecutor.getNumberOfUsedConnection()%>
<br/>
<a href="FeederServlet?Operation=RESET">RESET</a>     
  
</body>
</html>