/**
 * 
 */
package cz.hsrs.db.model.vgi;

import java.text.ParseException;

/**
 * Class representing spatial Envelope of features
 * to be selected
 * @author mkepka
 *
 */
public class Envelope2D {
    private double xMin;
    private double yMin;
    private double xMax;
    private double yMax;
    private int SRID;
    
    /**
     * Empty constructor for serialization
     */
    public Envelope2D(){
    }
    
    /**
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @param sRID
     */
    public Envelope2D(double xMin, double yMin, double xMax, double yMax,
            int sRID) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        SRID = sRID;
    }

    /**
     * Constructor for parsing of array of coordinates from OpenLayers
     * Correct format: [xmin, ymin, xmax, ymax, SRID]
     * @param extentArray - extent of MapWindow in format
     * @throws ParseException 
     */
    public Envelope2D(String extentArray) throws ParseException{
        if(extentArray.contains("[") && extentArray.contains("]")){
            String arr = extentArray.replace("[", ""); 
            arr = arr.replace("]", "");
            String[] parsed = arr.split(",");
            if(parsed.length == 5){
                this.xMin = Double.parseDouble(parsed[0]); 
                this.yMin = Double.parseDouble(parsed[1]);
                this.xMax = Double.parseDouble(parsed[2]);
                this.yMax = Double.parseDouble(parsed[3]);
                this.SRID = Integer.parseInt(parsed[4]);
            }
            else{
                throw new ParseException("Extent does not contain correct number of coordinates!", 1);
            }
        }
        else{
            throw new ParseException("Extent string is not in correct format!", 1);
        }
    }

    /**
     * @return the xMin
     */
    public double getXMin() {
        return xMin;
    }

    /**
     * @return the yMin
     */
    public double getYMin() {
        return yMin;
    }

    /**
     * @return the xMax
     */
    public double getXMax() {
        return xMax;
    }

    /**
     * @return the yMax
     */
    public double getYMax() {
        return yMax;
    }

    /**
     * @return the sRID
     */
    public int getSRID() {
        return SRID;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Envelope2D [xMin=" + xMin + ", yMin=" + yMin + ", xMax=" + xMax
                + ", yMax=" + yMax + ", SRID=" + SRID + "]";
    }
}