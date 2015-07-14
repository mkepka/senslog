package cz.hsrs.db;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.util.SensorUtil;

public class ChartGenerator {

	private String dir;
	// private Map<Date,Double> data = null;
	private SensorUtil util = new SensorUtil();

	public ChartGenerator(String dir) {
		super();

		this.dir = dir;

	}

	public List<File> getUnitCharts(long unit_id, Date timestampFrom,
			Date timestampTo, int width, int height) throws Exception {
		List<Sensor> sensors = util.getSensors(unit_id);
		List<File> files = new ArrayList<File>();
		Sensor s = null;

		for (Iterator<Sensor> i = sensors.iterator(); i.hasNext();) {
			s = i.next();
			File f = getSensorChart(s, unit_id, timestampFrom, timestampTo,
					width, height);
			files.add(f);

		}

		return files;
	}

	public File getSensorChart(Sensor sensor, long unit_id, Date timestampFrom,
			Date timestampTo, int width, int height) throws Exception {

		TimeSeriesCollection dataset = getDataset(sensor.getSensorId(), unit_id,
				timestampFrom, timestampTo);

		String label = sensor.getPhenomenon().getPhenomenonName() + " "
				+ sensor.getPhenomenon().getUnit();
		String title = "Unit: " + unit_id + " Sensor: " + sensor.getSensorId();

		JFreeChart chart =createChart(title, label, dataset); 
			
		//	ChartFactory.createTimeSeriesChart(title, "Time",
		//		label, dataset, true, false, false);

		// chart.setBackgroundPaint(new Color(237, 245, 254));
		chart.setBackgroundPaint(Color.WHITE);
		String fileName = (new Long((new Date()).getTime())).toString();

		File pngFile = File.createTempFile(fileName, ".png", new File(dir));

		// pngFile.deleteOnExit();
		// File pngFile = new File(dir+/"+ fileName + ".png");
		ChartUtilities.saveChartAsPNG(pngFile, chart, width, height);
		// System.out.println(pngFile.getAbsolutePath());
		return pngFile;
	}

	private JFreeChart createChart(String title, String valuelabel, XYDataset dataset) { 
   JFreeChart chart = ChartFactory.createTimeSeriesChart(
		    title,  // title
            "Date",             // x-axis label
            valuelabel,         // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
       // plot.se.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            
            renderer.setSeriesShape(0, new Ellipse2D.Double(-1, -1, 2, 2));
            renderer.setBaseShapesVisible(true);
           /// renderer.setBaseShapesFilled(false);
          //  renderer.setBaseShape(new Rectangle2D.Double(-23, -3, 10, 10) );
        }
        
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM.dd. HH:mm"));       
        
        return chart;

    }
	/*public File getSensorChart(long sensor_id, long unit_id, Date timestampFrom,
			Date timestampTo, int width, int height) throws Exception {
	
		XYDataset dataset = getDataset(sensor_id, unit_id, timestampFrom, timestampTo);

		JFreeChart chart = ChartFactory.createTimeSeriesChart("todo", "Time",
				"todo", dataset, true, false, false);

		// chart.setBackgroundPaint(new Color(237, 245, 254));
		chart.setBackgroundPaint(Color.WHITE);
		String fileName = (new Long((new Date()).getTime())).toString();

		File pngFile = File.createTempFile(fileName, ".png", new File(dir));

		ChartUtilities.saveChartAsPNG(pngFile, chart, width, height);	
		return pngFile;
	}*/

	private TimeSeriesCollection getDataset(long sensor_id, long unit_id,
			Date timestampFrom, Date timestampTo) throws Exception {

		TimeSeriesCollection dataset = new TimeSeriesCollection();		
		Sensor sens = util.getSensorById(sensor_id);
		String sens_name = sens.getSensorName();
		
		if (sens_name==null) {
			sens_name = String.valueOf(sensor_id);
		}
		TimeSeries ts = new TimeSeries(sens_name, FixedMillisecond.class);

		DBChartUtils util = new DBChartUtils();
		Map<Date, Double> data = util.getObservationsBySensor(sensor_id,
				unit_id, timestampFrom, timestampTo);

		for (Iterator<Date> j = data.keySet().iterator(); j.hasNext();) {
			Date time = (Date) j.next();
			ts.add(new FixedMillisecond(time.getTime()), (Number) data
					.get(time));
		}
		dataset.addSeries(ts);

		return dataset;
	}		
}
