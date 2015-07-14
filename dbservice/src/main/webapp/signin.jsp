<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.servlet.security.*"%>  
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="css/maplog.css" />
        <link rel="stylesheet" type="text/css" href="css/signin.css" />
        <title>MapLogAgri - Login</title>
    </head>

    <body>
        <div id="headding">
            <div id="head">
			<img src="img/logo-foodie.png" style="padding-left:20px; padding-top: 2px">
		</div>
        </div>
        <!-- <h1>KdeMamKaru.cz</h1>
        <div><a href="http://www.kdemamkaru.cz"><img src="img/logo.png" alt="logo firmy" width="166" height="52"/></a></div>
        <div><img src="img/maplog_02_cz.gif" alt="banner"/></div>
        <div id="headnavigation">
            <ul class="navigation">
                <li><a href="http://www.kdemamkaru.cz/">Kontakt</a></li>
            </ul>
        </div> -->
        
        <form action="ControllerServlet" method=post id="loginform">
        <table>
            <tr>
                <td><label for="login">Login</label></td>
                <td><input type="text" name="username" value=""/></td>
            </tr>
            <tr>
                <td><label for="password">Password</label></td>
                <td> <input type="password" name="password" value="" /></td>
            </tr>
            <tr><td colspan="2"><input type="submit" value="sign in" /></td></tr>
            <tr><td colspan="2"> <%=JSPHelper.getFeedback(session)%>   </td></tr>
            <tr><td colspan="2"><input type="hidden" name="coming" value="<%=request.getParameter("coming")%>"/></td></tr>
        </table>
        </form>       
           
        <div style="clear:both"></div>
        <div id="footer">
            &copy; CCSS, <a href="http://www.ccss.cz/">http://www.ccss.cz/</a>            
         version: <%=ControllerServlet.VERSION%>  build: <%=ControllerServlet.BUILD%>
        </div>
    </body>
</html>
