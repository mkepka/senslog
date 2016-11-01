package cz.hsrs.track;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

public class TrackIgnitionSolverTest {
    
    private static long unit_id = 111;
    
    private static int on = 1;
    private static int off = 0;

    private static TrackUtil tUtil;
    
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mmZ");
    
    static Date d1, d2, d3, d4, d5, d6, d7;
    static UnitPosition p1, p2, p3, p4, p5, p6, p7;
    static Observation o1, o2, o3, o4, o5, o6, o7;
    
    //static Connection con;
    static UnitUtil util;

    @BeforeClass
    public static void set() throws SQLException, ParseException {
        DBHelper.setConnection();
    
        //stmt = ConnectionManager.getConnection().createStatement();    
        d1 = format.parse("2001-07-17 10:00+0100");
        d2 = format.parse("2001-07-17 10:02+0100");
        d3 = format.parse("2001-07-17 10:04+0100");
        d4 = format.parse("2001-07-17 10:06+0100");
        d5 = format.parse("2001-07-17 10:08+0100");
        d6 = format.parse("2001-07-17 10:10+0100");
        d7 = format.parse("2001-07-17 10:12+0100");
        
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
    public void testInsertObservationsSynchro() throws SQLException, NoItemFoundException{
        TrackUtil u = new TrackUtil();
        
        p1.insertToDb();
        o1 = new Observation(d1, 0.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o1).solve();
        
        p2.insertToDb();
        o2 = new Observation(d2, 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o2).solve();
        
        TrackData d1 = u.getTrack(unit_id, d2);
            
        Assert.assertTrue(u.hasTrack(unit_id));
        
        p3.insertToDb();
        o3 = new Observation(d3, 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o3).solve();
        Assert.assertEquals(3, u.getTrackLenght(d1.getGid()));
        
        p4.insertToDb();
        o4 = new Observation(d4, 0.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o4).solve();
        
        Assert.assertEquals(4, u.getTrackLenght(d1.getGid()));
        
        /**
         * zapalovani na miste
         */
        for (int i = 1; i < 4; i++){
            d4 =new Date(d4.getTime()+(1000 * 60));
            UnitPosition p = new UnitPosition(unit_id, 18, 6, d4);
            p.insertToDb();
            Observation o = new Observation(d4, 0.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
            new TrackIgnitionSolver(o).solve();
        }
        
        /**dalsi cesta*/
        for (int i = 1; i < 4; i++){
            d4 = new Date(d4.getTime() + 1000*60);
            UnitPosition p1 = new UnitPosition(unit_id, 18, 1+i, d4);
            p1.insertToDb();
            Observation o = new Observation(d4, 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
            new TrackIgnitionSolver(o).solve();
        }
        
        TrackData track2 = u.getTrack(unit_id, d4);
        Assert.assertEquals(4, u.getTrackLenght(track2.getGid()));
        Assert.assertEquals(4, u.getTrackLenght(d1.getGid()));
        
        
    }
    
    @Test
    public void test2TrackSynchro() throws SQLException, NoItemFoundException, ParseException{
        TrackUtil u = new TrackUtil();
        
        p1.insertToDb();
        insObs(p1, 1);
        
        p2.insertToDb();
        insObs(p2, 1);
        
        TrackData d = u.getTrack(unit_id, p2.internalGetTimestamp());
        Assert.assertEquals(2, u.getTrackLenght(d.getGid()));
        
        /** chcipnem */
        p3.insertToDb();
        insObs(p3, 0);        
    
        Assert.assertEquals(3, u.getTrackLenght(d.getGid()));
        /**startujem */
        
        p4.insertToDb();
        insObs(p4, 0);        
        
        p5.insertToDb();
        insObs(p5,1);
        
        p6.insertToDb();
        insObs(p6,1);
        
        p7.insertToDb();
        insObs(p7,0);
        TrackData d2 = u.getTrack(unit_id, p5.internalGetTimestamp());
        Assert.assertEquals(4, u.getTrackLenght(d2.getGid()));
        
        try {
        TrackData d3 = u.getTrack(unit_id, format.parse("2001-07-17 10:05+0100"));
        Assert.fail("should never reach here");
        } catch (NoItemFoundException e) {
            Assert.assertTrue(true);
        }
    }
        
    @Test
    public void testAsynchro1() throws SQLException, NoItemFoundException{
        TrackUtil u = new TrackUtil();
        
        p1.insertToDb();
        o1 = new Observation(p1.internalGetTimestamp(), 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o1).solve();
        
        p2.insertToDb();
        o2 = new Observation(p2.internalGetTimestamp(), 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o2).solve();
        
        p5.insertToDb();
        o5 = new Observation(p5.internalGetTimestamp(), 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o5).solve();
        
        p6.insertToDb();
        o6 = new Observation(p6.internalGetTimestamp(), 1.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o6).solve();
        
        p4.insertToDb();
        o4 = new Observation(p4.internalGetTimestamp(), 0.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o4).solve();
        
        p3.insertToDb();
        o3 = new Observation(p3.internalGetTimestamp(), 0.0 , TrackIgnitionSolver.IGNITION_SENSOR_ID, unit_id);
        new TrackIgnitionSolver(o3).solve();
        
        Assert.assertTrue(true);
    }
    
    @Test
    public void testAsynchro2() throws Exception {
        TrackUtil u = new TrackUtil();
        d1 = format.parse("2001-08-17 10:00+0100");
        d2 = format.parse("2001-08-17 10:02+0100");
        d3 = format.parse("2001-08-17 10:04+0100");
        d4 = format.parse("2001-08-17 10:06+0100");
        d5 = format.parse("2001-08-17 10:08+0100");
        d6 = format.parse("2001-08-17 10:10+0100");
        d7 = format.parse("2001-08-17 10:12+0100");
        
        p1 = new UnitPosition(unit_id, 18, 1, d1);
        p2 = new UnitPosition(unit_id, 18, 2, d2);
        p3 = new UnitPosition(unit_id, 18, 3, d3);
        p4 = new UnitPosition(unit_id, 18, 4, d4);
        p5 = new UnitPosition(unit_id, 18, 5, d5);
        p6 = new UnitPosition(unit_id, 18, 6, d6);
        p7 = new UnitPosition(unit_id, 18, 6, d7);
        
        p1.insertToDb();
        insObs(p1, 1);
        p2.insertToDb();
        insObs(p2, 1);    
        
        p3.insertToDb();
        insObs(p3, 0);
        p4.insertToDb();
        insObs(p4, 1);
        
        p6.insertToDb();
        insObs(p6, 1);
        p5.insertToDb();
        insObs(p5, 1);

        TrackData t1 = u.getTrack(unit_id, p1.internalGetTimestamp());
        Assert.assertEquals(u.getTrackLenght(t1.getGid()),3);
        
        TrackData t2 = u.getTrack(unit_id, p5.internalGetTimestamp());
        Assert.assertEquals(t2.getEnd(), p6.internalGetTimestamp());
        Assert.assertEquals(u.getTrackLenght(t2.getGid()),4);
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
    
        //util.deleteCompany(comp_id);
    }
}