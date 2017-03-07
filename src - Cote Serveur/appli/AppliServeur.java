package appli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import serveur.Serveur;
import vols.Compagnie;

public class AppliServeur {

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		Serveur s = new Serveur(new ArrayList<Compagnie>());
		s.addCompagnie("AirFrance");
		s.addVol("AirFrance", "Bamako", formater.parse("07/10/2016"), 210, 400.8f);
		s.addVol("AirFrance", "Bamako", formater.parse("07/10/2016"), 180, 390.8f);
		//on ajoute les vols pour les tests
		
	}

}




/*
Chose à terminer : 
Coder les vérrou, les affichages des réservation et les affichages des opérations, les moyens d'annuler les réservations
de recommencer la réservation.
*/