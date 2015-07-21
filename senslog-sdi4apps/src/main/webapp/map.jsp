<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ page import = "cz.hsrs.servlet.security.*"%>
    <%@ page import = "cz.hsrs.servlet.lang.*"%>
    <%@ page import = "java.util.Map"%>
    <%@ page import = "cz.hsrs.db.pool.*"%>
    
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-US" lang="en-US">
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>MapLogAgri</title>
    <!-- Include Ext and app-specific scripts: -->
    <script type="text/javascript" src="/wwwlibs/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="/wwwlibs/ext/ext-all.js"></script>
    <script type="text/javascript" src="/wwwlibs/hslayers/OpenLayers-debug.js"></script>
    <script type="text/javascript" src="/wwwlibs/hslayers/MapPortal.js"></script>
    <script type="text/javascript" src="/wwwlibs/hslayers/HS.js"></script>
    <script type="text/javascript">
                OpenLayers.ProxyHost = "proxy.jsp?url=";
                HS.setLang(HSLayers.Util.getCookie("language"));
    </script>
    <script type="text/javascript" src="/wwwlibs/hslayers/HSLayers/Control/BoxLayerSwitcher.js"></script>
    <script type="text/javascript" src="/wwwlibs/proj4js/proj4js.js"></script>

    <!-- Include Ext stylesheets here: -->
    <link rel="stylesheet" type="text/css" href="/wwwlibs/ext/resources/css/ext-all.css" />
    <link rel="stylesheet" type="text/css" href="/wwwlibs/ext/resources/css/xtheme-gray.css" />

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
    <script type="text/javascript" src="js/maplog-app.js"></script>
    <link rel="stylesheet" type="text/css" href="css/maplog.css" />
  </head>
  <body>
        <% JSPHelper.checkAuthentication(request, response);
        Map<String, String> l = Labels.getLabels().get(request.getCookies());
        %>
        <div id="headding">
            <div id="head">
                <%try {%>
                    <%if (SQLExecutor.getBrand_picture_name()!=null){%>
                        <%="<img src=\"img/"+SQLExecutor.getBrand_picture_name()+"\" style=\"padding-left:20px; padding-top: 2px\">"%> 
                    <%}%>
                <% } catch (Exception e) {session.invalidate();}%>
            </div>
            <div id="headnavigation">
                <ul class="navigation">
                    <li><a href="./crossroad.jsp"><%=l.get(Labels.intro)%></a></li>
                    <li><a href="./map.jsp"><%=l.get(Labels.map)%></a></li>
                    <!-- <li><a href="./map-osm.jsp"><%=l.get(Labels.mapW)%></a></li>-->
                    <li><a href="./admin.jsp"><%=l.get(Labels.administration)%></a></li>
                    <li><a href="./kniha_jizd.jsp"><%=l.get(Labels.logbook)%></a></li>
                </ul>
                <div id="logininfo">
                    <%try {%>
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
                    <%} catch (Exception e) {session.invalidate();}%>
                </div>
            </div>
        </div>
        <div id="maplogbody"> </div>
  </body>
</html>