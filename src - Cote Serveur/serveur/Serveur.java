package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import service.ServiceRéservation;
import vols.Compagnie;
import vols.Vol;

public class Serveur implements Runnable{
	public static final int PORT = 3000;
	private ServerSocket monServeur;
	private ArrayList<Compagnie> compagnies;
	private Thread thread;
	
	public Serveur(ArrayList<Compagnie> compagnies){
		try{
			monServeur = new ServerSocket(3000);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		this.compagnies = compagnies;
		thread = new Thread(this);
		thread.start();
	}
	@Override
	public void run() {
		while(true){
			try{
				Socket socketReçu = monServeur.accept();
				new ServiceRéservation(socketReçu, this);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public ArrayList<Vol> rechercheVols(String dest, Date dateDépart, int nbPlace) {
		ArrayList<Vol> volsTrouvés = new ArrayList<Vol>();
		for(Compagnie c : compagnies){
			volsTrouvés.addAll(c.getVols(dest, dateDépart, nbPlace));
		}
		return volsTrouvés;
	}
	
	public void addCompagnie(String nom){
			this.compagnies.add(new Compagnie(nom));
	}
	
	public void addVol(String nomCompagnie, String destination, Date dateDépart2, int nbPlaceDispo, float prix) {
			for (Compagnie c : compagnies)
				if (c.getNom().equals(nomCompagnie))
					c.ajouterVol(destination, dateDépart2, nbPlaceDispo, prix);

	}

	
}
