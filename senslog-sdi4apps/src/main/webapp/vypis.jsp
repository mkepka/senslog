<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="cz.hsrs.db.*"%>
<%@ page import="cz.hsrs.db.util.*"%>
<%@ page import="cz.hsrs.db.model.*"%>
<%@ page import = "cz.hsrs.servlet.security.*"%>
<%@ page import = "cz.hsrs.servlet.feeder.*"%>


<html>
<head>     
        <link rel="stylesheet" type="text/css" href="css/maplog.css" />       
        <link rel="stylesheet" type="text/css" href="css/signin.css" />      
        <title>Sensor web - Login</title>
    </head>
<% 
	boolean hasPos = true;
    JSPHelper.checkAuthentication(request, response); 

    UnitUtil utils = new UnitUtil();
	long id = Long.parseLong(request.getParameter("unit_id"));
	session.setAttribute(ServiceParameters.UNIT_ID, id);
	String map = "";
	UnitPosition pos = null;
	Unit unit = utils.getUnit(id);
	try {
	pos = utils.getLastUnitPosition(id);
	
	map = "center="+pos.getY()+","+pos.getX()+
				 "&zoom=13&size=200x200&markers=color:blue|label:"+id+"|"+pos.getY()+","+pos.getX()+
				 "&sensor=false";	
	} catch (NoItemFoundException e){
		hasPos =false;
		%> Unit has no position.		
		<% 
		
	}
       
    ChartGenerator gen = new ChartGenerator(getServletContext().getRealPath("./chart"));
	Date d = new Date();
	Date dateFrom = new Date(d.getTime() - (1000 * 60 * 60 * 24 * 7));
	Date dateTo = new Date(d.getTime());
    List<File> fl = gen.getUnitCharts(Long.parseLong(request.getParameter("unit_id")), dateFrom, dateTo, 900, 250);
%>

<head>
<div align="left">
	<a href="./insert.jsp?unit_id=<%=request.getParameter("unit_id")%>">Insert new position here</a>
</div>

<div align="right">
	Logged in as
	<%=JSPHelper.getUserName(session)%>
	<a href="Logout">Logout</a>
</div>

<script type="text/javascript"
    src="http://maps.google.com/maps/api/js?sensor=false">
</script>
<script type="text/javascript">
<% try { %>
  function initialize() {
    var latlng = new google.maps.LatLng(<%=pos.getY() %>, <%=pos.getX() %>);
    var myOptions = {
      zoom: 13,
      center: latlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"),
        myOptions);
    var marker = new google.maps.Marker({
        position: latlng, 
        map: map, 
        title:"<%=id%>"
    })
  }
<% } catch (Exception e){
	hasPos =false;
	%> 
Unit has not position. Please set it 
<a href="insposition.jsp?unit_id="<%=id%> here/>.

<% };%>  

</script>

</HEAD>

<body onload="initialize()">

<body>

<% if (hasPos) { %>
  <div id="map_canvas" style="width:400pt; height:250pt"></div>
<%}%>
<h3>Unit ID : <%=unit.getUnitId()%>, Popis: <%=unit.getDescription()%> </h3>
<table border="0">

	<% for (Iterator<File> i = fl.iterator(); i.hasNext();){ 
            File f = i.next();
            %>
	<tr>
		<td><img src="./chart/<%= f.getName() %>"></img></td>
	</tr>	
	<% } %>
	
</table>

</BODY>
</HTML>