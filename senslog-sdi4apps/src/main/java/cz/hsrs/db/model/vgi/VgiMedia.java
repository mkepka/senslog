package cz.hsrs.db.model.vgi;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.vgi.util.VgiParams;

/**
 * Class represents VgiMedia object
 * containing connected media file to the VgiObservation
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiMedia {

    @XmlElement(name = VgiParams.MEDIA_ID_NAME)
    private int mediaId;
    @XmlElement(name = VgiParams.OBS_VGI_ID_NAME)
    private int obsId;
    @XmlElement(name = VgiParams.TIME_RECEIVED_NAME)
    private String timeReceivedString;
    private Date timeReceived;
    @XmlElement(name = VgiParams.OBSERVED_MEDIA_NAME)
    private byte[] observedMedia;
    private byte[] thumbnail;
    private String datatype;
    
    /**
     * Empty constructor
     */
    public VgiMedia(){
    }
    
    /**
     * Constructor for selecting only media file from DB
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
        this.datatype = mediaDatatype;
        try {
            this.timeReceived = DateUtil.parseTimestampMicro(timeReceivedString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Constructor for selecting only thumbnail of media file from DB
     * @param mediaId
     * @param obsId
     * @param timeReceivedString
     * @param mediaDatatype
     * @param thumbnail
     */
    public VgiMedia(int mediaId, int obsId, String timeReceivedString, String mediaDatatype,
            byte[] thumbnail) {
        this.mediaId = mediaId;
        this.obsId = obsId;
        this.timeReceivedString = timeReceivedString;
        this.thumbnail = thumbnail;
        this.datatype = mediaDatatype;
        try {
            this.timeReceived = DateUtil.parseTimestampMicro(timeReceivedString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Constructor for complete object selected form DB
     * @param mediaId
     * @param obsId
     * @param timeReceivedString
     * @param observedMedia
     * @param mediaDatatype
     */
    public VgiMedia(int mediaId, int obsId, String timeReceivedString,
            byte[] observedMedia, byte[] thumbnail, String mediaDatatype) {
        this.mediaId = mediaId;
        this.obsId = obsId;
        this.timeReceivedString = timeReceivedString;
        this.observedMedia = observedMedia;
        this.thumbnail = thumbnail;
        this.datatype = mediaDatatype;
        try {
            this.timeReceived = DateUtil.parseTimestampMicro(timeReceivedString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for Media file metadata
     * @param mediaId - ID of VgiMedia
     * @param obsId - ID of master VgiObservation
     * @param timeReceivedString - time stamp when media was received to the DB
     * @param mediaDatatype - data type of media file
     */
    public VgiMedia(int mediaId, int obsId, String timeReceivedString, String mediaDatatype) {
        this.mediaId = mediaId;
        this.obsId = obsId;
        this.timeReceivedString = timeReceivedString;
        this.datatype = mediaDatatype;
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
     * @return the thumbnail
     */
    public byte[] getThumbnail() {
        return thumbnail;
    }

    /**
     * @return the mediaDatatype
     */
    public String getMediaDatatype() {
        return datatype;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiMedia [mediaId=" + mediaId + ", obsId=" + obsId
                + ", timeReceivedString=" + timeReceivedString
                + ", mediaDatatype=" + datatype + "]";
    }
}