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
	private boolean tempsEcoul�;
	
	
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
		this.tempsEcoul� = false;
		sc = new Scanner(System.in);
		this.thread = new Thread(this);
		this.thread.start();	
	}

	@Override
	public void run() {
		/*
		 * Ici on utilise l'instruction switch pour savoir quelle �tape
		 * de la r�servation lancer comme �a si une erreur survient on 
		 * peut revenir � une �tape pr�cedente.
		 */
		while(!fini){
			try {
				switch(Integer.parseInt(socketIn.readLine())){
					case 0 : fini = true;
					break;
					case 1 : envoyerInfo();
					break;
					case 2 : r�cupererVols();
					break;
					case 3 : envoyerChoix();
					break;
					case 4 : confirmationR�servation();
					break;	
					case 5 : tempsEcoul�();
					break;
					case 6 : System.out.println("Choix erron� merci d'en choisir un nouveau :");
							 envoyerChoix();
					break;
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	//fonction appel�e quand le temps est �coul� 
	private void tempsEcoul�() {
		this.tempsEcoul� = true;
		this.fini = true;
		System.out.println("Temps de session �coul� rupture de la connexion.");
		
	}

	private void confirmationR�servation() {
		System.out.println("Le vol a bien �t� r�serv�.\nFin du service de r�servation.");	
	}

	//Ici on enregistre le choix de l'utilisateur pour ensuite l'envoyer au serveur
	private void envoyerChoix() {
		boolean ok = false;
		int choixR = 0;
		
		while(!ok){
			choixR = sc.nextInt();
			sc.nextLine();
			System.out.println("Confirmez votre choix : " + choixR + " (O/N)");
			String r�ponse = sc.nextLine();
			if (r�ponse.equals("O"))
				ok = true;
			else System.out.println("Veuillez renvoyer le num�ro de la ligne d�sir�e.");
		}
		
		try {
			out.writeObject(choixR);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Ici on r�cup�re les vols sous forme de texte avec un num�ro de ligne
	//qui sera demand� au client lors de son choix de r�servation
	private void r�cupererVols() {
		if(tempsEcoul�)
			return;
		try {
			String r�ponse = "";
			while(true){
				r�ponse = socketIn.readLine();
				if(r�ponse.equals("*"))
					break;
				System.out.println(r�ponse);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	//Ici on envoi les infos de r�servation demand�es au client
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
		System.out.println("Veuillez entrer la date de d�part (jj/mm/aaaa) : ");
			String dateDp = sc.nextLine();
			try {
				this.setDateDepart(formatter.parse(dateDp));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}		
		System.out.println("Veuillez entrer le nombre de place(s) � r�server : ");
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
