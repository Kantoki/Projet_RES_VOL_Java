package vols;

import java.util.Date;


public class Vol {
	private static int nbvols = 1;
	private int num�ro;
	private String destination;
	private Date dateD�part;
	private Integer nbPlaceDispo;
	private float prix;
	private boolean enR�servation;
	
	
	
	public Vol(String destination, Date dateD�part, int nbPlaceDispo, float prix) {
		super();
		this.destination = destination;
		this.dateD�part = dateD�part;
		this.nbPlaceDispo = nbPlaceDispo;
		this.prix = prix;
		this.num�ro = nbvols++;
		this.enR�servation =false;
	}

	@Override
	public String toString(){
		return "Vol num�ro : "+ Integer.toString(num�ro) +" - Prix : " + prix + "� - Places restantes : " + Integer.toString(nbPlaceDispo);
		}

	public boolean verifier(String dest, Date dateD�part2, int nbPlace) {
		if(!destination.equals(dest))
			return false;
		if(!dateD�part.equals(dateD�part2))
			return false;
		if(nbPlaceDispo < nbPlace)
			return false;
		return true;
	}

	public void r�server(int nbPlace) {
			this.enR�servation = true;
			nbPlaceDispo = nbPlaceDispo - nbPlace > 0 ? nbPlaceDispo - nbPlace : nbPlaceDispo;
			this.enR�servation = false;
	}

	public boolean isEnR�servation() {
		return enR�servation;
	}
}
