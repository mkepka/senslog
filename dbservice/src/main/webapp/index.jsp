<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.servlet.feeder.*"%>
    <%@ page import = "cz.hsrs.servlet.provider.*"%>
    <%@ page import = "cz.hsrs.servlet.security.*" %>
    <%@ page import = "cz.hsrs.db.util.*" %>
    <%@ page import = "cz.hsrs.db.model.*" %>
    <%@ page import = "java.util.*" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sensor web</title>
</head>
<body>

    
    <%
    JSPHelper.checkAuthentication(request, response);		
	UserUtil uUtil = new UserUtil();
	List<Unit> units = uUtil.getUnitsByUser(JSPHelper.getUserName(session));
    %>
    Login as: <%=JSPHelper.getUserName(session)%> <a href="Logout">Logout</a>
    <table border="1">
    	<tr>
    		<th> Available units : <%=JSPHelper.getUserName(session)%></th>
    	</tr>
    	<% for(int i = 0;i<units.size(); i++){%>
    	<tr>
    		<td> <a href="vypis.jsp?unit_id=<%=units.get(i).getUnitId()%>" > <%=units.get(i).getUnitId()%> </a></td>
    	</tr>
    	<%} %>
    </table>
</body>
</html>