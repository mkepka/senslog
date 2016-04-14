package cz.hsrs.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import net.sf.json.JSONObject;

public class DBJsonUtils {

       /**
     * Method writes given ResultSet into PrintWriter output as JSON
     * @param writer output destination
     * @param res ResultSet to write into writer
     * @throws SQLException
     */
    public static void writeJSON(PrintWriter writer, ResultSet res) throws SQLException{
        ResultSetMetaData rsmd = res.getMetaData();
        int numColumns = rsmd.getColumnCount();
        
        writer.print("[");
        boolean first = true;
        
        while (res.next()){
            if (!first){
                writer.println(",");
                first = false;
                }
            else {
                first=false;
            }
            JSONObject jsonObj = generateJSONObj(res, rsmd, numColumns);
            writer.print(jsonObj);
        }
        writer.println("]");
    }
    
    private static JSONObject generateJSONObj(ResultSet rs, ResultSetMetaData rsmd, int numColumns){
        JSONObject obj = new JSONObject();
        try{
            for (int i=1; i < numColumns+1; i++) {
                String column_name = rsmd.getColumnName(i);

                if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                    obj.put(column_name, rs.getArray(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                    obj.put(column_name, rs.getLong(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                    obj.put(column_name, rs.getBoolean(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                    obj.put(column_name, rs.getBlob(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                    obj.put(column_name, rs.getDouble(column_name)); 
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                    obj.put(column_name, rs.getFloat(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                    obj.put(column_name, rs.getNString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                    obj.put(column_name, rs.getString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                    obj.put(column_name, rs.getTimestamp(column_name));
                }
                else{
                    obj.put(column_name, rs.getString(column_name));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
          return obj;
    }
    
    public static void writeJSON(PrintWriter writer, List<? extends DBObject> res ) throws SQLException{
        
        writer.print("[");
        boolean first=true;
        Iterator i = res.iterator();
        while (i.hasNext()) {
                if (!first){
                    writer.println(",");
                    first = false;
                    }
                else {
                    first=false;
                }
            //DBObject gr = (element.getClass().newInstance()).getDBObject(res);
            JSONObject jsonObj = JSONObject.fromObject(i.next());
            writer.print(jsonObj);
            //System.out.println(jsonObj);
            
        }
        writer.println("]");
        //writer.println("Nekomprimovano!");
    }
    
    public static void writeJSONCompressed(GZIPOutputStream gzOut, List<? extends DBObject> res ) throws SQLException{
        boolean first=true;
        Iterator i = res.iterator();
        String row="";
        row += "[";
        try {
            while (i.hasNext()){
                
                
                JSONObject jsonObj = JSONObject.fromObject(i.next());
                row += jsonObj;
                if (i.hasNext()==true){
                       row +=",\n";
                }
                else{
                    row +="]";
                }
                first=false;
                
                gzOut.write(row.getBytes(), 0, row.getBytes().length);
                gzOut.flush();
            }
            //String zprava = "\n Komprimovano!";
            //gzOut.write(zprava.getBytes());
            gzOut.close();
        } catch (IOException e) {
            throw new SQLException(e);
        }    
    }
    
    public static void writeJSON(PrintWriter writer, DBObject element, ResultSet res ) throws SQLException{
        try {
            writer.print("[");
            boolean first=true;
            while (res.next()) {
                    if (!first){
                        writer.println(",");
                        first = false;
                        }
                    else {
                        first=false;
                    }
                DBObject gr = (element.getClass().newInstance()).getDBObject(res);
                JSONObject jsonObj = JSONObject.fromObject(gr);
                writer.print(jsonObj);
            }
            writer.println("]");
        } catch (InstantiationException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
}