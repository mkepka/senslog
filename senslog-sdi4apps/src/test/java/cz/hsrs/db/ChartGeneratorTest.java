package cz.hsrs.db;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.SensorUtil;
import cz.hsrs.db.util.UnitUtil;

public class ChartGeneratorTest {

	private static long unit_id = 111;
	private static int sensor_id1 = 111;
	private static int sensor_id2 = 112;
	private static int sensor_id3 = 113;
	static String datePos = "2001-07-15 00:30";
	static String dateObs1 = "2001-07-15 00:31";
	static String dateObs2 = "2001-07-15 00:32";
	static String dateObs3 = "2001-07-15 00:33";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	static ChartGenerator gen;
	static SensorUtil utilS;
	static UnitUtil utilU;

	@BeforeClass
	public static void genData() throws Exception {
		DBHelper.setConnection();
		gen = new ChartGenerator("./");
		utilS = new SensorUtil();
		utilU = new UnitUtil();
		DatabaseFeedOperation.insertObservation(format.parse(dateObs1),
				unit_id, sensor_id1, 1.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs2),
				unit_id, sensor_id1, 2.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs3),
				unit_id, sensor_id1, 1.5);
		
		DatabaseFeedOperation.insertObservation(format.parse(dateObs1),
				unit_id, sensor_id2, 3.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs2),
				unit_id, sensor_id2, 4.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs3),
				unit_id, sensor_id2, 2.0);
		
		DatabaseFeedOperation.insertObservation(format.parse(dateObs1),
				unit_id, sensor_id3, 5.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs2),
				unit_id, sensor_id3, 4.0);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs3),
				unit_id, sensor_id3, 6.0);
	}

	@AfterClass
	public static void delData() throws Exception {
		utilU.deleteUnit(unit_id);
	}

	@Test
	public void testSensorChart() throws Exception {
		Sensor s = utilS.getSensorById(sensor_id1);
		File f = gen.getSensorChart(s, unit_id, format.parse(dateObs1), format
				.parse(dateObs3), 700, 400);		
		showImage(f);
		System.out.println(f.getPath());
	}
	
	@Test
	public void testUnitChart() throws Exception {
		Sensor s = utilS.getSensorById(111);
		List<File> fl = gen.getUnitCharts(unit_id, format.parse(dateObs1), format
				.parse(dateObs3), 700, 400);
		
		for (Iterator<File> iterator = fl.iterator(); iterator.hasNext();) {
			File file =  iterator.next();
			showImage(file);
			System.out.println(file.getName());
		}
		
		System.out.println(fl.size());
	}

	private void showImage(File f) {
		JFrame frame = new JFrame("Display image");
		Panel panel = new ShowImage(f);
		frame.getContentPane().add(panel);
		frame.setSize(600, 300);
		frame.setVisible(true);	

	}

	public class ShowImage extends Panel {
		BufferedImage image;

		public ShowImage(File f) {
			try {											
				image = ImageIO.read(f);
			} catch (IOException ie) {
				System.out.println("Error:" + ie.getMessage());
			}
		}

		public void paint(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}
	}
}
