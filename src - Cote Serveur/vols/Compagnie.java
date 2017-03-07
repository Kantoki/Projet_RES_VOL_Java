package vols;

import java.util.ArrayList;
import java.util.Date;


public class Compagnie {
	private static int nbComp = 1;
	private int numéro;
	private String nom;
	private ArrayList<Vol> vols;

	
	
	
	public Compagnie(String nom) {
		super();
		this.nom = nom;
		vols = new ArrayList<Vol>();
		this.numéro = nbComp++;
	}

	public ArrayList<Vol> getVols(String dest, Date dateDépart, int nbPlace) {
		ArrayList<Vol> volsTrouvés = new ArrayList<Vol>(); 
		for(Vol v : vols){
			if(v.verifier(dest, dateDépart, nbPlace))
				volsTrouvés.add(v);
		}
		return volsTrouvés;
	}
	
	public void ajouterVol(String destination, Date dateDépart, int nbPlaceDispo, float prix){
		vols.add(new Vol(destination, dateDépart, nbPlaceDispo, prix));
	}
	
	public String getNom(){
		return nom;
	}
}
