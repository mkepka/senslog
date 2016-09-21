package cz.hsrs.db.model.vgi;

import java.text.ParseException;
import java.util.Date;

import cz.hsrs.db.util.DateUtil;

public class VgiMedia {

    private int mediaId;
    private int obsId;
    private String timeReceivedString;
    private Date timeReceived;
    private byte[] observedMedia;
    private String mediaDatatype;
    
    /**
     * Empty constructor
     */
    public VgiMedia(){
    }
    
    /**
     * @param mediaId
     * @param obsId
     * @param timeReceivedString
     * @param observedMedia
     * @param mediaDatatype
     */
    public VgiMedia(int mediaId, int obsId, String timeReceivedString,
            byte[] observedMedia, String mediaDatatype) {
        this.mediaId = mediaId;
        this.obsId = obsId;
        this.timeReceivedString = timeReceivedString;
        this.observedMedia = observedMedia;
        this.mediaDatatype = mediaDatatype;
        try {
            this.timeReceived = DateUtil.parseTimestampMicro(timeReceivedString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the mediaId
     */
    public int getMediaId() {
        return mediaId;
    }

    /**
     * @return the obsId
     */
    public int getObsId() {
        return obsId;
    }

    /**
     * @return the timeReceivedString
     */
    public String getTimeReceivedString() {
        return timeReceivedString;
    }

    /**
     * 
     * @return
     */
    public Long internalGetTimeReceivedMilis(){
        return this.timeReceived.getTime();
    }
    
    /**
     * @return the observedMedia
     */
    public byte[] getObservedMedia() {
        return observedMedia;
    }

    /**
     * @return the mediaDatatype
     */
    public String getMediaDatatype() {
        return mediaDatatype;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiMedia [mediaId=" + mediaId + ", obsId=" + obsId
                + ", timeReceivedString=" + timeReceivedString
                + ", mediaDatatype=" + mediaDatatype + "]";
    }
    
}