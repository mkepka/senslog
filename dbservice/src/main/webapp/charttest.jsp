<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="cz.hsrs.db.*"%>
<%@ page import="cz.hsrs.db.util.*"%>
<%@ page import="cz.hsrs.db.model.*"%>
<%@ page import = "cz.hsrs.servlet.security.*"%>
<%@ page import = "cz.hsrs.servlet.feeder.*"%>
<%@ page import = "java.text.SimpleDateFormat"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<% 
   // JSPHelper.checkAuthentication(request, response); 

    UnitUtil utilU = new UnitUtil();
    SensorUtil utilS = new SensorUtil();
	long uid = Long.parseLong(request.getParameter("unit_id"));
	long sid = Long.parseLong(request.getParameter("sensor_id"));		
	Unit unit = utilU.getUnit(uid);
	Date d = new Date();
	Date dateFrom = new Date(d.getTime() - (1000 * 60 * 60 * 24 * 14));
	Date dateTo = new Date(d.getTime());
    Sensor sensors = utilS.getSensorById(sid); 
    DBChartUtils util = new DBChartUtils();
	Map<Date, Double> data = util.getObservationsBySensor(sid, uid, dateFrom, dateTo);
	
	SimpleDateFormat formater = new SimpleDateFormat("yyyy, MM, dd, HH, mm, ss");
	
%>

<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <title>Google Visualization API Sample</title>
  <script type="text/javascript" src="http://www.google.com/jsapi"></script>
  <script type="text/javascript">

    google.load('visualization', '1', {packages: ['annotatedtimeline']});
    function drawVisualization() {
      var data = new google.visualization.DataTable();

      
      data.addColumn('date', 'Date');
      data.addColumn('number', 'Unit: <%=unit.getUnitId()%>');      
      data.addRows(<%=data.size()%>);

  	<% int i=0;
  	   for (Iterator<Date> j = data.keySet().iterator(); j.hasNext();) {
		Date time = (Date) j.next();
	//ts.add(new FixedMillisecond(time.getTime()), (Number) data
	//			.get(time)); 
%>
      d = new Date();
      d.setTime(<%=time.getTime()%>);
      data.setValue(<%=i%>, 0, d);
      data.setValue(<%=i%>, 1, <%=data.get(time) %>);   

  	<%i++; 
  	}%>
      
      var annotatedtimeline = new google.visualization.AnnotatedTimeLine(
          document.getElementById('visualization'));
      annotatedtimeline.draw(data, {'displayAnnotations': true});
    }
    google.setOnLoadCallback(drawVisualization);

   
  </script>
</head>
<body style="font-family: Arial;border: 0 none;">


<div id="visualization" style="width: 800px; height: 400px;"></div>
  
   
</body>
</html>