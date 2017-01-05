package cz.hsrs.rest.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

public class VgiObservationRdfBean {

    @XmlAttribute(name = "about", namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    public String about;
    
    @XmlElement(name = "label", namespace = "http://www.w3.org/2000/01/rdf-schema")
    public String label;
    
    @XmlElement(name = "asWKT", namespace = "http://www.opengis.net/ont/geosparql")
    public KombiElement asWkt;
    
    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    public AttributeElement identifier;
    
    @XmlElement(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/")
    public String publisher;
    
    @XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/")
    public String title;
    
    @XmlElement(name = "rights", namespace = "http://purl.org/dc/elements/1.1/")
    public AttributeElement rights;
    
    @XmlElement(name = "source", namespace = "http://purl.org/dc/elements/1.1/")
    public AttributeElement source;
    
    @XmlElement(name = "created", namespace = "http://purl.org/dc/terms/")
    public KombiElement created;
    
    @XmlElement(name = "class", namespace = "http://www.openvoc.eu/poi")
    public AttributeElement poiClass;

    public VgiObservationRdfBean(){
    }
    

    /**
     * 
     * @param geomAsWkt
     * @param obsVgiId
     * @param name
     * @param unitId
     * @param timestamp
     * @param categoryId
     */
    public VgiObservationRdfBean(String geomAsWkt, int obsVgiId,
            String name, long unitId, String timestamp, int categoryId) {
        String ID = "http://portal.sdi4apps.eu/SensLog/poi/#"+obsVgiId; // jaky zvolit ID?
        this.about = ID;
        this.label = name;
        this.asWkt = new KombiElement("http://www.openlinksw.com/schemas/virtrdf#Geometry", geomAsWkt);
        this.identifier = new AttributeElement(ID);
        this.publisher = "SPOI (http://sdi4apps.eu/spoi)";
        this.title = name;
        this.rights = new AttributeElement("http://opendatacommons.org/licenses/odbl/1.0/");
        this.source = new AttributeElement("http://portal.sdi4apps.eu/SensLog/unit/#"+unitId);
        this.created = new KombiElement("http://www.w3.org/2001/XMLSchema#date", timestamp);
        this.poiClass = new AttributeElement("http://portal.sdi4apps.eu/SensLog/category/#"+String.valueOf(categoryId));
    }
    
    static class KombiElement{
        private String att;
        private String val;
        
        public KombiElement(){}
        
        public KombiElement(String thisAtt, String thisValue) {
            this.att = thisAtt;
            this.val = thisValue;
        }
        @XmlAttribute(name = "datatype", namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
        public String getThisAtt(){
            return att; 
        }
        
        @XmlValue
        public String getValue(){
            return val;
        }
    }
    
    static class AttributeElement{
        private String att;
        
        public AttributeElement(){}
        
        public AttributeElement(String thisAtt) {
            this.att = thisAtt;
        }
        @XmlAttribute(name = "resource", namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
        public String getThisAtt(){
            return att; 
        }
    }

}