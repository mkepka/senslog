package cz.hsrs.db.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for parsing incoming timestamps as String to Date 
 * @author mkepka
 *
 */
public class DateUtil {

    public static String patternSecs = "yyyy-MM-dd HH:mm:ss";
    public static int patternSecsLen = 19;
    /**
     * yyyy-MM-dd HH:mm:ss
     * length=19
     */
    public static SimpleDateFormat formatSecs = new SimpleDateFormat(patternSecs);
    //--------------------------------------------------------------------------------------------
    public static String patternSecsWT = "yyyy-MM-dd'T'HH:mm:ss";
    public static int patternSecsWTLen = 19;
    /**
     * yyyy-MM-dd'T'HH:mm:ss
     * length=19
     */
    public static SimpleDateFormat formatSecsWT = new SimpleDateFormat(patternSecsWT);
    //--------------------------------------------------------------------------------------------
    public static String patternMiliSecs = "yyyy-MM-dd HH:mm:ss.SSS";
    public static int patternMiliSecsLen = 23;
    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     * length=23
     */
    public static SimpleDateFormat formatMiliSecs = new SimpleDateFormat(patternMiliSecs);
    //--------------------------------------------------------------------------------------------
    public static String patternMiliSecsWT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static int patternMiliSecsWTLen = 23;
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS
     * length=23
     */
    public static SimpleDateFormat formatMiliSecsWT = new SimpleDateFormat(patternMiliSecsWT);
    //--------------------------------------------------------------------------------------------
    public static String patternSecsTZ = "yyyy-MM-dd HH:mm:ssZ";
    public static int patternSecsTZLen = 24;
    /**
     * yyyy-MM-dd HH:mm:ssZ
     * length=24
     */
    public static SimpleDateFormat formatSecsTZ = new SimpleDateFormat(patternSecsTZ);
    //--------------------------------------------------------------------------------------------
    public static String patternSecsTZwT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static int patternSecsTZwTLen = 24;
    /**
     * yyyy-MM-ddTHH:mm:ssZ
     * length=24
     */
    public static SimpleDateFormat formatSecsTZwT = new SimpleDateFormat(patternSecsTZwT);
    //--------------------------------------------------------------------------------------------
    public static String patternISO = "yyyy-MM-dd HH:mm:ssZZ:ZZ";
    public static int patternISOLen = 25;
    //--------------------------------------------------------------------------------------------
    public static String patternMiliSecsTZ = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static int patternMiliSecsTZLen = 28;
    /**
     * yyyy-MM-dd HH:mm:ss.SSSZ
     * length=28
     */
    public static SimpleDateFormat formatMiliSecsTZ = new SimpleDateFormat(patternMiliSecsTZ);
    //--------------------------------------------------------------------------------------------
    public static String patternMiliSecsTZwT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public int patternMiliSecsTZwTLen = 28;
    /**
     * yyyy-MM-ddTHH:mm:ss.SSSZ
     * length=28
     */
    public static SimpleDateFormat formatMiliSecsTZwT = new SimpleDateFormat(patternMiliSecsTZwT);
    //--------------------------------------------------------------------------------------------
    public static String patternISOMilis = "yyyy-MM-dd HH:mm:ss.SSSZZ:ZZ";
    public static int patternISOMilisLen = 29;
    /**
     * Method parses timestamp String to Date
     * uses several patterns to test correct format
     * @param time - timestamp as String
     * @return Timestamp as Date
     * @throws ParseException 
     */
    public static Date parseTimestamp(String time) throws ParseException{
        boolean containsZ = time.contains("Z");
        if(containsZ){
            time = time.replace("Z", "+0000");
        }
        int len = time.length();
        
        char suffixTZ = time.charAt(len-3);
        if(suffixTZ == '+' || suffixTZ == '-'){
            time = time+"00";
            len = time.length();
        }
        
        boolean containsT = time.contains("T");
        boolean containsSpace = time.contains(" ");
        
        if(len == patternSecsLen){
            if(!containsT && containsSpace){
                try {
                    Date date = formatSecs.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else if(containsT && !containsSpace){
                try {
                    Date date = formatSecsWT.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else if(len == patternMiliSecsLen){
            char dot = time.charAt(len-4);
            boolean containsDot = false;
            if(dot == '.'){
                containsDot = true;
            }
            if(!containsT && containsSpace && containsDot){
                try {
                    Date date = formatMiliSecs.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else if(containsT && !containsSpace && containsDot){
                try {
                    Date date = formatMiliSecsWT.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else if(len == patternSecsTZLen){
            if(!containsT && containsSpace){
                try {
                    Date date = formatSecsTZ.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else if(containsT && !containsSpace){
                try {
                    Date date = formatSecsTZwT.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else if(len == patternMiliSecsTZLen){
            char dot = time.charAt(len-9);
            boolean containsDot = false;
            if(dot == '.'){
                containsDot = true;
            }
            if(!containsT && containsSpace && containsDot){
                try {
                    Date date = formatMiliSecsTZ.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else if(containsT && !containsSpace && containsDot){
                try {
                    Date date = formatMiliSecsTZwT.parse(time);
                    return date;
                } catch (ParseException e) {
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else if(len == patternISOLen){
            char colonTZ = time.charAt(len-3);
            if(colonTZ == ':'){
                String part1 = time.substring(0, len-3);
                String part2 = time.substring(len-2, len);
                try{
                    if(containsT){
                        Date date = formatSecsTZwT.parse(part1+part2);
                        return date;
                    }else if(containsSpace){
                        Date date = formatSecsTZ.parse(part1+part2);
                        return date;
                    }
                    else{
                        throw new ParseException("Unsupported pattern!", 0);
                    }
                } catch(ParseException e){
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else if(len == patternISOMilisLen){
            char colonTZ = time.charAt(len-3);
            if(colonTZ == ':'){
                String part1 = time.substring(0, len-3);
                String part2 = time.substring(len-2, len);
                try{
                    if(containsT){
                        Date date = formatMiliSecsTZwT.parse(part1+part2);
                        return date;
                    }else if(containsSpace){
                        Date date = formatMiliSecsTZ.parse(part1+part2);
                        return date;
                    }
                    else{
                        throw new ParseException("Unsupported pattern!", 0);
                    }
                } catch(ParseException e){
                    throw new ParseException(e.getMessage(), 0);
                }
            }
            else{
                throw new ParseException("Unsupported pattern!", 0);
            }
        }
        else{
            throw new ParseException("Unsupported pattern!", 0);
        }
    }
    
    /**
     * yyyy-MM-dd HH:mm:ss.SSSsssZZZZZ
     * @param microTime
     * @throws ParseException 
     */
    public static Date parseTimestampMicro(String microTime) throws ParseException{
        /**
         * time string contains microseconds
         */
        int len = microTime.length();
        
        char suffixTZ = microTime.charAt(len-3);
        if(suffixTZ == '+' || suffixTZ == '-'){
            microTime = microTime+"00";
            len = microTime.length();
        }
        
        char dot = microTime.charAt(len-4);
        boolean containsDot = false;
        if(dot == '.'){
            containsDot = true;
        }
        
        String milisTime = microTime.substring(0, patternMiliSecsLen);
        String timeZone = microTime.substring(len-5, len);
        
        String subTime = ""+milisTime+timeZone;
        
        Date date = formatMiliSecsTZ.parse(subTime);
        
        return date;
    }
}