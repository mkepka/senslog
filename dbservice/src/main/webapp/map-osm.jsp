<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.servlet.security.*"%>  
    <%@ page import = "cz.hsrs.servlet.lang.*"%>
    <%@ page import = "java.util.Map"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US" lang="en-US">
  <head>    
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>MapLog</title>      
    <!-- Include Ext and app-specific scripts: -->    
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/ext/ext-3.4.0/adapter/ext/ext-base.js"></script>    
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/ext/ext-3.4.0/ext-all.js"></script>    
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/hslayers/trunk/build/OpenLayers.js"></script>
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/hslayers/trunk/build/MapPortal.js"></script>
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/hslayers/trunk/build/HS.js"></script>
    <script type="text/javascript">
                                OpenLayers.ProxyHost = "proxy.jsp?url="; 
                                HS.setLang(HSLayers.Util.getCookie("language"));
    </script>;
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/hslayers/trunk/build/HSLayers/Control/BoxLayerSwitcher.js"></script>
    <script type="text/javascript" src="http://maplog.lesprojekt.cz/wwwlibs/proj4js/proj4js.js"></script>


    <!-- Include Ext stylesheets here: -->    
    <link rel="stylesheet" type="text/css" href="http://maplog.lesprojekt.cz/wwwlibs/ext/ext-3.4.0/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="http://maplog.lesprojekt.cz/wwwlibs/ext/ext-3.4.0/resources/css/xtheme-access.css" />

    <!-- MapLog Application -->
    <script type="text/javascript" src="js/OL-patch.js"></script>
    <script type="text/javascript" src="js/MapLog.js"></script>
    <script type="text/javascript" src="js/Control/UnitSwitcher.js"></script>
    <script type="text/javascript" src="js/Control/TracQuery.js"></script>
    <script type="text/javascript" src="js/UnitIcon.js"></script>
    <script type="text/javascript" src="js/Layer/UnitVector.js"></script>
    <script type="text/javascript" src="js/Layer/History.js"></script>
    <script type="text/javascript" src="js/UnitFeature.js"></script>
    <script type="text/javascript" src="js/widgets/DetailPanel.js"></script>
    <script type="text/javascript" src="js/widgets/MapPanel.js"></script>
    <script type="text/javascript" src="js/widgets/DetailGrid.js"></script>
    <script type="text/javascript" src="js/widgets/SettingsPanel.js"></script>
    <script type="text/javascript" src="js/Maplog-cze.js"></script>
    <script type="text/javascript" src="js/maplog-common.js"></script>
    <script type="text/javascript" src="js/maplog-app-osm.js"></script>

    <link rel="stylesheet" type="text/css" href="css/maplog.css" />


  </head>
  <body onload="init()">
        <% JSPHelper.checkAuthentication(request, response);   
       Map<String, String> l = Labels.getLabels().get(request.getCookies());
        %>
      <div id="headding">
        <div id="headnavigation">
            <ul class="navigation">
                <li><a href="./crossroad.jsp"><%=l.get(Labels.intro)%></a></li>
                <li><a href="./map.jsp"><%=l.get(Labels.map)%></a></li>
                <li><a href="./map-osm.jsp"><%=l.get(Labels.mapW)%></a></li>
                <li><a href="./admin.jsp"><%=l.get(Labels.administration)%></a></li>
                <li><a href="./kniha_jizd.jsp"><%=l.get(Labels.logbook)%></a></li>
                <li><a href="http://www.kdemamkaru.cz"><%=l.get(Labels.contact)%></a></li>
            </ul>
            <div id="logininfo">
                    <%=l.get(Labels.loginas)%> <%=JSPHelper.getUserName(session)%> <a href="Logout"> <%=l.get(Labels.logout)%></a>
                    <% LoginUser user = (LoginUser)session.getAttribute(JSPHelper.USERATTRIBUTE);
                    if(user.getUserLanguage().equalsIgnoreCase("cz")==true){ %>
                            <a href="ChangeLang?lang=en&coming=/map.jsp"><img src="icons/en.gif" /></a>
                    <%} 
                    else if (user.getUserLanguage().equalsIgnoreCase("en")==true){ %>
                            <a href="ChangeLang?lang=cz&coming=/map.jsp"><img src="icons/cz.gif" /></a>
                    <%} 
                    else{ %>
                            <!-- place to add more languages -->
                    <%} %>                	
            </div>
        </div>
    </div>
    <div id="maplogbody"> </div>
  </body>
</html>
