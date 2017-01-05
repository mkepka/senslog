package cz.hsrs.rest.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class VgiObservationsRdfBean {
    
    private List<VgiObservationRdfBean> observations;
    
    public VgiObservationsRdfBean(){}
    
    public VgiObservationsRdfBean(List<VgiObservationRdfBean> observationList){
        this.observations = observationList;
    }
    
    @XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    public List<VgiObservationRdfBean> getObsList(){
        return this.observations;
    }
}