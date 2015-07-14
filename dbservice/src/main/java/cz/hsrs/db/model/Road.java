package cz.hsrs.db.model;


/**
 *  gid serial NOT NULL,
  cislo bigint,
  oznaceni character varying(10),
  poznamka character varying(50),
  hskod character varying(4),
  hsnazev character varying(50),
  the_geom geometry,
 * @author jezekjan
 *
 */

public class Road {

	int gid;
	long cislo;
	String oznaceni;
	String poznamka;
	String hskod;
	String hsnazev;
	double[] coords;
	
	
	public Road(int gid, long cislo, String oznaceni, String poznamka,
			String hskod, String hsnazev, double[] coords) {
		super();
		this.gid = gid;
		this.cislo = cislo;
		this.oznaceni = oznaceni;
		this.poznamka = poznamka;
		this.hskod = hskod;
		this.hsnazev = hsnazev;
		this.coords = coords;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public long getCislo() {
		return cislo;
	}
	public void setCislo(long cislo) {
		this.cislo = cislo;
	}
	public String getOznaceni() {
		return oznaceni;
	}
	public void setOznaceni(String oznaceni) {
		this.oznaceni = oznaceni;
	}
	public String getPoznamka() {
		return poznamka;
	}
	public void setPoznamka(String poznamka) {
		this.poznamka = poznamka;
	}
	public String getHskod() {
		return hskod;
	}
	public void setHskod(String hskod) {
		this.hskod = hskod;
	}
	public String getHsnazev() {
		return hsnazev;
	}
	public void setHsnazev(String hsnazev) {
		this.hsnazev = hsnazev;
	}
	public double[] getCoords() {
		return coords;
	}
	public void setCoords(double[] coords) {
		this.coords = coords;
	} 
}
