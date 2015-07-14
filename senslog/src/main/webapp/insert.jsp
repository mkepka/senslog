<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "cz.hsrs.servlet.feeder.*"%>
<%@ page import = "cz.hsrs.servlet.provider.*"%>
<%@ page import = "cz.hsrs.servlet.security.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="cz.hsrs.db.*"%>
<%@ page import="cz.hsrs.db.util.*"%>
<%@ page import="cz.hsrs.db.model.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%	
		JSPHelper.checkAuthentication(request, response);		
		UserUtil uUtil = new UserUtil();
		List<Unit> units = uUtil.getUnitsByUser(JSPHelper.getUserName(session)); 
		SimpleDateFormat formater = uUtil.getDateFormater();
		String actual = formater.format(new Date()).replace(" ", "T");
	%>
	
	 <form method="post" action="FeederServlet">
         <table>
            <tr>
                <td>Operation  </td>    
                <td>  <INPUT TYPE=TEXT NAME=<%=ServiceParameters.OPERATION%> VALUE= <%=ServiceParameters.INSERT_POSITION%> ></td>
                 <td> Required </td>                
            </tr>
            <tr>                        
                <td><%=ServiceParameters.DATE%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.DATE%> VALUE=<%=actual%> ></td>
                <td> Required </td>                      
            </tr>
            <tr>                        
                <td><%=ServiceParameters.LAT%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.LAT%> VALUE="" ></td>
                <td> Required </td>                      
            </tr>
            <tr>                        
                <td><%=ServiceParameters.LON%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.LON%> VALUE="" ></td>
                <td> Required </td>                      
            </tr>
            <tr>                                           
                <td><%=ServiceParameters.ALT%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.ALT%> VALUE="" ></td>
                <td> Optional </td>                      
            </tr>
            <tr>                                           
                <td><%=ServiceParameters.DOP%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.DOP%> VALUE="" ></td>
                <td> Optional </td>                      
            </tr>
            <tr>                        
                <td><%=ServiceParameters.UNIT_ID%></td>    
                <td><select name=<%=ServiceParameters.UNIT_ID%>>
					<% for (int i = 0; i < units.size();i++){ 
							if (request.getParameter("unit_id").equalsIgnoreCase(""+units.get(i).getUnitId())==true){
					%>
								<option value="<%=units.get(i).getUnitId()%>" selected="selected"><%=units.get(i).getUnitId()%></option>
							<%}
							else {%>
								<option value="<%=units.get(i).getUnitId()%>"><%=units.get(i).getUnitId()%></option>
							<%}
						}%>	
					</select> </td>
				<td> Required </td>	              
            </tr>
            <tr>
                <td> <INPUT TYPE="SUBMIT" NAME="" VALUE="Submit"/></td>
            </tr>    
        </table>
    </form>
    <div><a href="./index.jsp">Back to the list of units</a></div>     	
</body>
</html>
