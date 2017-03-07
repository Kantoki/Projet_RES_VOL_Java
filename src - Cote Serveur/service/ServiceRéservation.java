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

public class ServiceR�servation implements Runnable {
	private static Integer nbClient = 0;
	private int numClient;
	private PrintWriter socketOut;
	private ObjectInputStream in;
	private Thread thread;
	private TempsSession temps;
	private String dest;
	private Date dateD�part;
	private int nbPlace;
	private int choixClient;
	private Serveur serveur;
	ArrayList<Vol> volsTrouv�s;
	private InetAddress ipClient;
	private boolean fini;
	private boolean tempsEcoul�;
	

	public ServiceR�servation(Socket socketRe�u,Serveur serveur) {
		try {
			new BufferedReader(new InputStreamReader(socketRe�u.getInputStream()));
			socketOut = new PrintWriter(socketRe�u.getOutputStream(), true);
			in = new ObjectInputStream(socketRe�u.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serveur = serveur;
		this.tempsEcoul� = false;
		this.ipClient = socketRe�u.getInetAddress();
		this.numClient = nbClient++;
		this.temps = new TempsSession(this);
		thread = new Thread(this);
		thread.start();
		
	}

	@Override
	public void run() {
		System.out.println("Connexion du client " + numClient + " � l'adresse : " + ipClient);
		temps.start();
		
		try {
			while(!fini){
				socketOut.println("1");
				socketOut.flush();
				r�ceptionrequete();
				socketOut.println("2");
				socketOut.flush();
				reponseVols();
				socketOut.println("3");
				socketOut.flush();
				r�cupererChoix();
				r�server();
				socketOut.println("4");
				socketOut.flush();
				finR�servation();
				nettoyerBuffer();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin du service de r�servation...");
		close();
	}
	//Apr�s la r�servation on envoi la confirmation au client
	private void finR�servation() {
		if(tempsEcoul�)
			return;
		socketOut.println("0");
		socketOut.flush();
		fini =true;
	}

	private void nettoyerBuffer() {
		socketOut.flush();	
	}
	//Ici on va chercher le vol s�lectionn� pour r�server les places du client
	public void r�server() {
		if(tempsEcoul�)
			return;
		//ici on va chercher le vol puis r�server les places
		Vol v = volsTrouv�s.get(choixClient);
		/*
		 * On verrouille la ressource car elle peut �tre utilis� par plusieurs 
		 * clients en m�me temps
		 */
		synchronized (v) {
			try {
				if(v.isEnR�servation())
					v.wait();
				else
					v.r�server(nbPlace);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			v.notifyAll();
		}
		System.out.println("R�servation du client " + numClient + " pour le vol : " + volsTrouv�s.get(choixClient).toString());
	}
	//Ici on r�cup�re le choix du client pour savoir quel vols r�server
	private void r�cupererChoix() {
		if(tempsEcoul�)
			return;
		try {
			int choix = (int) in.readObject();
			if(choix >= this.volsTrouv�s.size()){
				socketOut.println("6");
				this.r�cupererChoix();
			}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Ici on r�cup�re la requete de vol du client en v�rifiant que les informations sont correctes 
	private void r�ceptionrequete() {
		if(tempsEcoul�)
			return;
		try {
			while (!verifier()) {
				dest = (String) in.readObject();
				dateD�part = (Date) in.readObject();
				nbPlace = (int) in.readObject();
			}
			System.out.println("Requete re�ue du client " + numClient + " Dest : "+ dest+" - Date :" + dateD�part+ " - Places : "+ nbPlace);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	//Ici on cherhce les vols en rapport avec les informations donn�es par le client
	private void reponseVols() {
		if(tempsEcoul�)
			return;
		volsTrouv�s = serveur.rechercheVols(dest, dateD�part, nbPlace);
		if(volsTrouv�s.size() == 0){
			socketOut.println("Aucun vol n'a �t� trouv�.");
			return;
		}

		socketOut.println("Voici les vols trouv�s");
		
		for(int i = 0; i < volsTrouv�s.size(); ++i){
			String r�ponse = Integer.toString(i)+ " " + volsTrouv�s.get(i)+ "\n";
			socketOut.println(r�ponse);
			nettoyerBuffer();
		}
		socketOut.println("Veuillez renvoyer le num�ro de la ligne d�sir�e.");
		socketOut.println("*");
	}


	private boolean verifier() {
		if(dest == null){
			return false;
		}
	
		if(dateD�part == null){
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
		this.tempsEcoul� = true;
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
