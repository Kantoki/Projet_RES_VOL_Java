package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import outils.TempsSession;
import serveur.Serveur;
import vols.Vol;

public class ServiceRéservation implements Runnable {
	private static Integer nbClient = 0;
	private int numClient;
	private PrintWriter socketOut;
	private ObjectInputStream in;
	private Thread thread;
	private TempsSession temps;
	private String dest;
	private Date dateDépart;
	private int nbPlace;
	private int choixClient;
	private Serveur serveur;
	ArrayList<Vol> volsTrouvés;
	private InetAddress ipClient;
	private boolean fini;
	private boolean tempsEcoulé;
	

	public ServiceRéservation(Socket socketReçu,Serveur serveur) {
		try {
			new BufferedReader(new InputStreamReader(socketReçu.getInputStream()));
			socketOut = new PrintWriter(socketReçu.getOutputStream(), true);
			in = new ObjectInputStream(socketReçu.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serveur = serveur;
		this.tempsEcoulé = false;
		this.ipClient = socketReçu.getInetAddress();
		this.numClient = nbClient++;
		this.temps = new TempsSession(this);
		thread = new Thread(this);
		thread.start();
		
	}

	@Override
	public void run() {
		System.out.println("Connexion du client " + numClient + " à l'adresse : " + ipClient);
		temps.start();
		
		try {
			while(!fini){
				socketOut.println("1");
				socketOut.flush();
				réceptionrequete();
				socketOut.println("2");
				socketOut.flush();
				reponseVols();
				socketOut.println("3");
				socketOut.flush();
				récupererChoix();
				réserver();
				socketOut.println("4");
				socketOut.flush();
				finRéservation();
				nettoyerBuffer();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin du service de réservation...");
		close();
	}
	//Après la réservation on envoi la confirmation au client
	private void finRéservation() {
		if(tempsEcoulé)
			return;
		socketOut.println("0");
		socketOut.flush();
		fini =true;
	}

	private void nettoyerBuffer() {
		socketOut.flush();	
	}
	//Ici on va chercher le vol sélectionné pour réserver les places du client
	public void réserver() {
		if(tempsEcoulé)
			return;
		//ici on va chercher le vol puis réserver les places
		Vol v = volsTrouvés.get(choixClient);
		/*
		 * On verrouille la ressource car elle peut être utilisé par plusieurs 
		 * clients en même temps
		 */
		synchronized (v) {
			try {
				if(v.isEnRéservation())
					v.wait();
				else
					v.réserver(nbPlace);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			v.notifyAll();
		}
		System.out.println("Réservation du client " + numClient + " pour le vol : " + volsTrouvés.get(choixClient).toString());
	}
	//Ici on récupère le choix du client pour savoir quel vols réserver
	private void récupererChoix() {
		if(tempsEcoulé)
			return;
		try {
			int choix = (int) in.readObject();
			if(choix >= this.volsTrouvés.size()){
				socketOut.println("6");
				this.récupererChoix();
			}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Ici on récupère la requete de vol du client en vérifiant que les informations sont correctes 
	private void réceptionrequete() {
		if(tempsEcoulé)
			return;
		try {
			while (!verifier()) {
				dest = (String) in.readObject();
				dateDépart = (Date) in.readObject();
				nbPlace = (int) in.readObject();
			}
			System.out.println("Requete reçue du client " + numClient + " Dest : "+ dest+" - Date :" + dateDépart+ " - Places : "+ nbPlace);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	//Ici on cherhce les vols en rapport avec les informations données par le client
	private void reponseVols() {
		if(tempsEcoulé)
			return;
		volsTrouvés = serveur.rechercheVols(dest, dateDépart, nbPlace);
		if(volsTrouvés.size() == 0){
			socketOut.println("Aucun vol n'a été trouvé.");
			return;
		}

		socketOut.println("Voici les vols trouvés");
		
		for(int i = 0; i < volsTrouvés.size(); ++i){
			String réponse = Integer.toString(i)+ " " + volsTrouvés.get(i)+ "\n";
			socketOut.println(réponse);
			nettoyerBuffer();
		}
		socketOut.println("Veuillez renvoyer le numéro de la ligne désirée.");
		socketOut.println("*");
	}


	private boolean verifier() {
		if(dest == null){
			return false;
		}
	
		if(dateDépart == null){
			return false;
		}
		if(nbPlace == 0){
			return false;
		}
		return true;
	}

	public void terminer() {
		socketOut.println("5");
		socketOut.flush();
		this.tempsEcoulé = true;
		this.fini = true;
		
	}

	private void close() {
		try {
			socketOut.close();
			in.close();
			temps.stop();
			thread.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	




}
