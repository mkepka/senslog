<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.servlet.feeder.*"%>
    <%@ page import = "cz.hsrs.servlet.provider.*"%>
    <%@ page import = "cz.hsrs.servlet.security.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<% JSPHelper.checkAuthentication(request, response); %> 

         <form method="get" action="FeederServlet" >
         <table>
            
      
            <tr>
                <td>Operation  </td>    
                <td>  <INPUT TYPE=TEXT NAME=<%=ServiceParameters.OPERATION%> VALUE= <%=ServiceParameters.INSERT_POSITION%> ></td>
                
            </tr>    
                      
            
            <tr>                        
                <td><%=ServiceParameters.DATE%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.DATE%> VALUE="2008-01-02 12:00:00" ></td>                      
            </tr>
            
            <tr>                        
                <td><%=ServiceParameters.LAT%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.LAT%> VALUE="" ></td>                      
            </tr>
            <tr>                        
                <td><%=ServiceParameters.LON%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.LON%> VALUE="" ></td>                      
            </tr>
            <tr>                                           
                <td><%=ServiceParameters.ALT%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.ALT%> VALUE="" ></td>                      
            </tr>
            <tr>                                           
                <td><%=ServiceParameters.DOP%></td>    
                <td>  <INPUT TYPE=TEXT NAME= <%=ServiceParameters.DOP%> VALUE="" ></td>                      
            </tr>
            <tr>                        
                <td><%=ServiceParameters.UNIT_ID%></td>    
                <td> 
                <INPUT TYPE=TEXT NAME= <%=ServiceParameters.UNIT_ID%>                 
                VALUE="<%=session.getAttribute(ServiceParameters.UNIT_ID)%>" "readonly" />
                </td>                      
            </tr>
           
            
            <tr>
                <td> <INPUT TYPE="SUBMIT" NAME="" VALUE="Submit"/></td>
            </tr>    
        </table>
    </form>     
</body>
</html>