package vols;

import java.util.Date;


public class Vol {
	private static int nbvols = 1;
	private int numéro;
	private String destination;
	private Date dateDépart;
	private Integer nbPlaceDispo;
	private float prix;
	private boolean enRéservation;
	
	
	
	public Vol(String destination, Date dateDépart, int nbPlaceDispo, float prix) {
		super();
		this.destination = destination;
		this.dateDépart = dateDépart;
		this.nbPlaceDispo = nbPlaceDispo;
		this.prix = prix;
		this.numéro = nbvols++;
		this.enRéservation =false;
	}

	@Override
	public String toString(){
		return "Vol numéro : "+ Integer.toString(numéro) +" - Prix : " + prix + "€ - Places restantes : " + Integer.toString(nbPlaceDispo);
		}

	public boolean verifier(String dest, Date dateDépart2, int nbPlace) {
		if(!destination.equals(dest))
			return false;
		if(!dateDépart.equals(dateDépart2))
			return false;
		if(nbPlaceDispo < nbPlace)
			return false;
		return true;
	}

	public void réserver(int nbPlace) {
			this.enRéservation = true;
			nbPlaceDispo = nbPlaceDispo - nbPlace > 0 ? nbPlaceDispo - nbPlace : nbPlaceDispo;
			this.enRéservation = false;
	}

	public boolean isEnRéservation() {
		return enRéservation;
	}
}
