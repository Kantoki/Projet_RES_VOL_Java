package vols;

import java.util.ArrayList;
import java.util.Date;


public class Compagnie {
	private static int nbComp = 1;
	private int num�ro;
	private String nom;
	private ArrayList<Vol> vols;

	
	
	
	public Compagnie(String nom) {
		super();
		this.nom = nom;
		vols = new ArrayList<Vol>();
		this.num�ro = nbComp++;
	}

	public ArrayList<Vol> getVols(String dest, Date dateD�part, int nbPlace) {
		ArrayList<Vol> volsTrouv�s = new ArrayList<Vol>(); 
		for(Vol v : vols){
			if(v.verifier(dest, dateD�part, nbPlace))
				volsTrouv�s.add(v);
		}
		return volsTrouv�s;
	}
	
	public void ajouterVol(String destination, Date dateD�part, int nbPlaceDispo, float prix){
		vols.add(new Vol(destination, dateD�part, nbPlaceDispo, prix));
	}
	
	public String getNom(){
		return nom;
	}
}
