package cz.hsrs.track;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

public class TestSpacialIgnitionCases {

    
    private static long unit_id = 111;
        
    private static TrackUtil tUtil;
    
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    
    static Date d1, d2, d3, d4, d5, d6, d7;        
    static UnitPosition p1, p2, p3, p4, p5, p6, p7 ;    
    static Observation o1, o2, o3, o4, o5, o6, o7;
    
    static Connection con;
    static UnitUtil util;

    @BeforeClass
    public static void set() throws SQLException, ParseException {
        DBHelper.setConnection();
    
        //stmt = ConnectionManager.getConnection().createStatement();    
        d1 = format.parse("2001-07-17 10:00");
        d2 = format.parse("2001-07-17 10:02");
        d3 = format.parse("2001-07-17 10:04");
        d4 = format.parse("2001-07-17 10:06");
        d5 = format.parse("2001-07-17 10:08");
        d6 = format.parse("2001-07-17 10:10");
        d7 = format.parse("2001-07-17 10:12");
        
        p1 = new UnitPosition(unit_id, 18, 1, d1);
        p2 = new UnitPosition(unit_id, 18, 2, d2);
        p3 = new UnitPosition(unit_id, 18, 3, d3);
        p4 = new UnitPosition(unit_id, 18, 4, d4);
        p5 = new UnitPosition(unit_id, 18, 5, d5);
        p6 = new UnitPosition(unit_id, 18, 6, d6);
        p7 = new UnitPosition(unit_id, 18, 7, d7);
        
    
        util = new UnitUtil();
    }
    
    @Test
    public void testTunel() throws Exception {
        TrackUtil u = new TrackUtil();        
        UnitPosition p;
        d1 = format.parse("2001-08-17 10:01");
        d2 = format.parse("2001-08-17 10:02");
        d3 = format.parse("2001-08-17 10:03");
        d4 = format.parse("2001-08-17 10:04");
        d5 = format.parse("2001-08-17 10:05");
        d6 = format.parse("2001-08-17 10:06");
        
        /*auto jede*/
        p = new UnitPosition(unit_id, 18, 1.0001, d1);
        p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0002, d2);
        p.insertToDb();    
        insObs(p, 1);
        
        /*auto jede tunelem*/
        try {
            insObs(d3, 1, unit_id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        try {
            insObs(d4, 1, unit_id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
        /*auto jede*/
        
        p = new UnitPosition(unit_id, 18, 1.0002, d5);
         p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0003, d6);
         p.insertToDb();    
        insObs(p, 1);
                
        
        TrackData t = u.getTrack(unit_id, d5);
        Assert.assertEquals(4,u.getTrackLenght(t.getGid()));
        System.out.println(t.getEnd());
        
    }
    
    @Test
    public void testGarage() throws Exception {
        TrackUtil u = new TrackUtil();        
        UnitPosition p;
        d1 = format.parse("2001-08-17 10:01");
        d2 = format.parse("2001-08-17 10:02");
        d3 = format.parse("2001-08-17 10:03");
        d4 = format.parse("2001-08-17 10:04");
        d5 = format.parse("2001-08-17 10:05");
        d6 = format.parse("2001-08-17 10:06");
        
        /*auto jede*/
        p = new UnitPosition(unit_id, 18, 1.0001, d1);
        p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0002, d2);
        p.insertToDb();    
        insObs(p, 1);
        
        /*auto je v garazi*/
        try {
            insObs(d3, 0, unit_id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        try {
            insObs(d4, 0, unit_id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        
        /*auto jede*/
        
        p = new UnitPosition(unit_id, 18, 1.0002, d5);
         p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0003, d6);
         p.insertToDb();    
        insObs(p, 1);
                
        
        TrackData t1 = u.getTrack(unit_id, d2);
        Assert.assertEquals(2,u.getTrackLenght(t1.getGid()));
    
        
        
        TrackData t2 = u.getTrack(unit_id, d6);
        Assert.assertEquals(2,u.getTrackLenght(t2.getGid()));
        
    }
    
    
    @Test
    public void testSemafor() throws Exception {
        TrackUtil u = new TrackUtil();
        UnitPosition p;
        d1 = format.parse("2001-08-17 10:01");
        d2 = format.parse("2001-08-17 10:02");
        d3 = format.parse("2001-08-17 10:03");
        d4 = format.parse("2001-08-17 10:04");
        d5 = format.parse("2001-08-17 10:05");
        d6 = format.parse("2001-08-17 10:06");
        
        /*auto jede*/
        p = new UnitPosition(unit_id, 18, 1.0001, Double.NaN, d1, 12.0, "");
        p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0002, Double.NaN, d2, 0.0, "");
        p.insertToDb();    
        insObs(p, 1);
        
        /*auto je stoji nastartovano*/
        
        p = new UnitPosition(unit_id, 18, 1.0002, Double.NaN, d3, 0.0, "");
        p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0002, Double.NaN, d4, 0.0, "");
        p.insertToDb();    
        insObs(p, 1);
    
        
        /*auto jede*/
        
        p = new UnitPosition(unit_id, 18, 1.0002000001, Double.NaN, d5, 12.0, "");
         p.insertToDb();    
        insObs(p, 1);
        
        p = new UnitPosition(unit_id, 18, 1.0003, Double.NaN, d6, 12.0, "");
         p.insertToDb();    
        insObs(p, 1);
                
        
        TrackData t1 = u.getTrack(unit_id, d3);
        Assert.assertEquals(d1.getTime(), t1.getStart().getTime());
        Assert.assertEquals(d6.getTime(), t1.getEnd().getTime());
        Assert.assertEquals(5,u.getTrackLenght(t1.getGid()));
    }
    
    private void insObs(UnitPosition p, double status) throws SQLException{
        Observation o = new Observation(p.internalGetTimestamp(), status , TrackIgnitionSolver.IGNITION_SENSOR_ID, p.getUnit_id());
        new TrackIgnitionSolver(o).solve();
    }
    
    private void insObs(Date d, double status, long unit_id) throws SQLException{
        Observation o = new Observation(d, status , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o).solve();
    }
    @After
    public void delData() throws Exception{
        util.deleteUnit(unit_id);
    }
}