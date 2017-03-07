package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Clientv2 implements Runnable{
	
	private String dest;
	private Date dateDepart;
	private int nbPlaceRes;
	private SimpleDateFormat formatter;
	private Thread thread;
	private Socket socket;
	private BufferedReader socketIn;
	private ObjectOutputStream out;
	private static final String SERVEUR_IP = "localhost"; //Adresse Ip du serveur
	private static final int PORT = 3000; //port du serveur
	Scanner sc;
	private boolean fini;
	private boolean tempsEcoulé;
	
	
	public Clientv2(){	
		try {
			socket = new Socket(SERVEUR_IP,PORT);
			socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			new PrintWriter(socket.getOutputStream(), true);
			out = new ObjectOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		this.fini = false;
		this.tempsEcoulé = false;
		sc = new Scanner(System.in);
		this.thread = new Thread(this);
		this.thread.start();	
	}

	@Override
	public void run() {
		/*
		 * Ici on utilise l'instruction switch pour savoir quelle étape
		 * de la réservation lancer comme ça si une erreur survient on 
		 * peut revenir à une étape précedente.
		 */
		while(!fini){
			try {
				switch(Integer.parseInt(socketIn.readLine())){
					case 0 : fini = true;
					break;
					case 1 : envoyerInfo();
					break;
					case 2 : récupererVols();
					break;
					case 3 : envoyerChoix();
					break;
					case 4 : confirmationRéservation();
					break;	
					case 5 : tempsEcoulé();
					break;
					case 6 : System.out.println("Choix erroné merci d'en choisir un nouveau :");
							 envoyerChoix();
					break;
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	//fonction appelée quand le temps est écoulé 
	private void tempsEcoulé() {
		this.tempsEcoulé = true;
		this.fini = true;
		System.out.println("Temps de session écoulé rupture de la connexion.");
		
	}

	private void confirmationRéservation() {
		System.out.println("Le vol a bien été réservé.\nFin du service de réservation.");	
	}

	//Ici on enregistre le choix de l'utilisateur pour ensuite l'envoyer au serveur
	private void envoyerChoix() {
		boolean ok = false;
		int choixR = 0;
		
		while(!ok){
			choixR = sc.nextInt();
			sc.nextLine();
			System.out.println("Confirmez votre choix : " + choixR + " (O/N)");
			String réponse = sc.nextLine();
			if (réponse.equals("O"))
				ok = true;
			else System.out.println("Veuillez renvoyer le numéro de la ligne désirée.");
		}
		
		try {
			out.writeObject(choixR);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Ici on récupère les vols sous forme de texte avec un numéro de ligne
	//qui sera demandé au client lors de son choix de réservation
	private void récupererVols() {
		if(tempsEcoulé)
			return;
		try {
			String réponse = "";
			while(true){
				réponse = socketIn.readLine();
				if(réponse.equals("*"))
					break;
				System.out.println(réponse);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	//Ici on envoi les infos de réservation demandées au client
	private void envoyerInfo() {
		remplirInfo();
		try {
			out.writeObject(this.dest);
			out.flush();
			out.writeObject(this.dateDepart);
			out.flush();
			out.writeObject(this.nbPlaceRes);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void remplirInfo(){
		System.out.println("Veuillez entrer la destination : ");
			this.setDest(sc.nextLine());
		System.out.println("Veuillez entrer la date de départ (jj/mm/aaaa) : ");
			String dateDp = sc.nextLine();
			try {
				this.setDateDepart(formatter.parse(dateDp));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}		
		System.out.println("Veuillez entrer le nombre de place(s) à réserver : ");
		this.setNbPlaceRes(sc.nextInt());
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setDateDepart(Date dateDepart) {
		this.dateDepart = dateDepart;
	}

	public void setNbPlaceRes(int nbPlaceRes) {
		this.nbPlaceRes = nbPlaceRes;
	}
}
