<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>   
    <%@ page import = "cz.hsrs.servlet.security.*"%>
    <%@ page import = "cz.hsrs.servlet.lang.*"%>
    <%@ page import = "java.util.Map"%>
    <%@ page import = "cz.hsrs.db.pool.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <meta name="description" content="MapLog -- GPS/GPRS - Real Time car tracking system" />
        <meta name="keywords" content="GPS,Tracking,Car,GPRS" />
        <meta name="author" content="Help Service - Remote Sensing" />
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
         <wicket:link>
        <link rel="stylesheet" type="text/css" href="css/maplog.css" />
         </wicket:link>
        <title>MapLogAgri</title>
    </head>
    <body>
        <% JSPHelper.checkAuthentication(request, response);
           Map<String, String> l = Labels.getLabels().get(request.getCookies());
        %>
        <div id="headding">
            <div id="head">
                <!-- <img src="img/logo-foodie.png" style="padding-left:20px; padding-top: 2px">-->
                <%try {%>
                    <%if(SQLExecutor.getBrand_picture_name()!=null){%>
                        <%="<img src=\"img/"+SQLExecutor.getBrand_picture_name()+"\" style=\"padding-left:20px; padding-top: 2px\">"%> 
                    <%}%>
                <% } catch (Exception e) {session.invalidate();}%>
            </div>
            <div id="headnavigation">
                <!-- ul class="navigation">
                    <li><a href="uvod.html">Úvod</a></li>
                    <li><a href="uvod.html">Přínosy</a></li>
                    <li><a href="uvod.html">Princip činnosti</a></li>
                    <li><a href="uvod.html">Technické parametry</a></li>
                    <li><a href="uvod.html">Demo</a></li>
                    <li><a href="uvod.html">Kontakt</a></li>
                </ul-->
                <div id="logininfo">
                <%try {%>
                    <%=l.get(Labels.loginas) %> <%=JSPHelper.getUserName(session)%> <a href="Logout"><%=l.get(Labels.logout) %></a>
                    <% LoginUser user = (LoginUser)session.getAttribute(JSPHelper.USERATTRIBUTE);
                    if(user != null){
                        if(user.getUserLanguage().equalsIgnoreCase("cz")==true){ %>
                            <a href="ChangeLang?lang=en&coming=/crossroad.jsp"><img src="icons/en.gif" /></a>
                    <%  } 
                        else if (user.getUserLanguage().equalsIgnoreCase("en")==true){ %>
                            <a href="ChangeLang?lang=cz&coming=/crossroad.jsp"><img src="icons/cz.gif" /></a>
                    <%  }
                        else{ %>
                            <!-- place to add more languages -->
                    <%  } 
                    }%>
                <%} catch (Exception e) {session.invalidate();}%>
                </div>
            </div>
        </div>
        <div id="mainnav">
            <div>
                <a href="map.jsp"><img src="img/mapa.png" /></a>
                <a href="map.jsp"><%=l.get(Labels.map)%></a>
            </div>
            <div>
                <a href="admin.jsp"><img src="img/administrace.png" /></a>
                <a href="admin.jsp"><%=l.get(Labels.administration)%></a>
            </div>
            <div>
                <a href="kniha_jizd.jsp"><img src="img/kniha_jizd.png" /></a>
                <a href="kniha_jizd.jsp"><%=l.get(Labels.logbook)%></a>
            </div>
<!--            <div>
                <a href="map-osm.jsp"><img src="img/mapa-osm.png" /></a>
                <a href="map-osm.jsp"><%=l.get(Labels.mapW)%></a>
		</div>-->
        </div>
        <div style="clear:both"></div>
        <div id="footer">
<!--            &copy; Help Service - Remote Sensing s.r.o, <a href="http://bnhelp.cz">http://bnhelp.cz</a>
	Tel.: +420 317 724 620, e-mail: bnhelp@bnhelp.cz, <%=l.get(Labels.adress)%>: Vnoučkova 614, 256 01 Benešov-->
        </div>
    </body>
</html>
