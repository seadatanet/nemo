package fr.ifremer.nemo.ui;

import java.text.ParseException;
import java.util.regex.*;

import org.eclipse.swt.widgets.Table;

import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.DateTimeFormatParser;


public class Etape {

	/*
	 * Gestion des différents affichages lors de la sélection des onglets
	 * de la fenêtre principale et des différentes étapes. 
	 * 
	 */ 
	
	
	public static void controle(){
		
		
		
	}
	
	
	public static void etape(int numero){
		//numero est l'etape à laquelle on voudrait passer en validant l'étape courante
		//ViewFile.etape
	
	}
	
	
	public static void validationDesc(){
		Boolean ok, resultat;
		ok = true;
		Table table;
	
//		raz du message d'erreurs
		Utils.messageErreur("");
		
		//section entete de fichier
		resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteFichier, "Entête de fichier");
		ok = ok && resultat;
		
		//section entete station
		resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteStation, "Entête de station");
			ok = ok && resultat;
		
		/*
		if (EditDesc.buttonLignesEnteteStation.getSelection()){
			resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteStation, "Entête de station");	
		ok = ok && resultat;
		}else{
			resultat = Regexp.checkLigneDebutfin(EditDesc.octetsEnteteStationLigne, EditDesc.octetsEnteteStationDebut, EditDesc.octetsEnteteStationFin, "Entête Station");
			ok = ok && resultat; 
		}
		*/
		
//		section fin de station
		resultat = Regexp.checkTextValeur(EditDesc.lignesFinStation, "Fin de station");
		ok = ok && resultat;
		
		//section fin de données
		if (!EditDesc.buttonIndicFinDonnees.getSelection()){
			resultat = Regexp.checkTextValeur(EditDesc.nbreLignesMesures, "Fin de station");
			//resultat = Regexp.checkLigneDebutfin(EditDesc.ruptureDonneesNoLigne, EditDesc.ruptureDonneesDebut, EditDesc.ruptureDonneesFin, "Indicateur de fin de données");
		ok = ok && resultat;
		}
		
		if (ok){ 	
			okDesc();
			}else {ViewFile.tabItem.setText("*** Description ***");
			ViewFile.etape = 0;}
		}	
		
	
	public static void validationSurvey(){
		//	test de toutes les valeurs de Survey	
		
		Boolean ok, resultat;
		ok = true;
		
		//	raz du message d'erreurs
		Utils.messageErreur("");
		
		if (TableDataSurvey.types.size() == 0){
			Utils.messageErreur("Vous devez choisir au moins un type de données avant de valider l'étape...");
		ok = false;}
		
		
		if (ok){ 	
			okSurvey();
			}else {ViewFile.tabItem1.setText("*** Campagne ***");
			ViewFile.etape = 1;}
		

	}

	public static void validationStation(){
//		test des valeurs de Station
		
		System.out.println("Debut check station");
		
		Boolean ok, resultat1, resultat2;
		ok = true;
		
//		raz du message d'erreurs
		Utils.messageErreur("");
	
		//NUMERO STATION
		if (EditStation.buttonRefStationPos.getSelection()){
			//position dans le fichier, vérif des valeurs
		resultat1 = Regexp.checkLigneDebutfin(EditStation.textRefStationLigne, EditStation.textRefStationDebut, EditStation.textRefStationFin, "Numéro de Station"); 	
		ok = ok && resultat1;
				
		}
		
		
		//TYPE DE DONNEES
		if (EditStation.comboRefStationType.getSelectionIndex() == -1){
			Utils.messageErreurAppend("Vous devez choisir un type de données à associer à la station...");
		ok = false;
		}
		
		
		if (EditStation.buttonHistoriqueAcqPos.getSelection()){
			//position dans fichier, verif des valeurs
		resultat1 = Regexp.checkLigneDebutfin(EditStation.textHistoriqueAcqLigne1, EditStation.textHistoriqueAcqDebut1, EditStation.textHistoriqueAcqFin1, "Historique Acquisition ligne 1"); 
		resultat2 = Regexp.checkLigneDebutfin(EditStation.textHistoriqueAcqLigne2, EditStation.textHistoriqueAcqDebut2, EditStation.textHistoriqueAcqFin2, "Historique Acquisition ligne 2"); 
		ok = ok && resultat1;
		ok = ok && 	resultat2;
			
		}
		
		if (EditStation.buttonCommentairePos.getSelection()){
		resultat1 = Regexp.checkLigneDebutfin(EditStation.textCommentaireLigne, EditStation.textCommentaireDebut, EditStation.textCommentaireFin, "Commentaire");	
			ok = ok && resultat1; 
		}
		
		
		if (EditStation.buttonSurfacePos.getSelection()){
			resultat1 =Regexp.checkLigneDebutfin(EditStation.textSurfaceLigne1, EditStation.textSurfaceDebut1, EditStation.textSurfaceFin1, "Echantillon de surface ligne 1");
			resultat2 =Regexp.checkLigneDebutfin(EditStation.textSurfaceLigne2, EditStation.textSurfaceDebut2, EditStation.textSurfaceFin2, "Echantillon de surface ligne 2");
						
			ok = ok && resultat1;
			ok = ok && resultat2;
			
		}
		
			
		//HEURE
			//check valeurs ligne/debut/fin
		resultat1 =Regexp.checkLigneDebutfin(EditStation.textHeureLigne, EditStation.textHeureDebut, EditStation.textHeureFin, "Heure"); 
		ok = ok && resultat1;
		
		String ligne, string=null;
		//Utils.messageErreurAppend("\n===[Check section : Heure]===\n");
		System.out.println("format heure = "+EditStation.comboHeure.getText()); //Item(EditStation.comboHeure.getSelectionIndex()));
		try {
			System.out.println("format heure = "+EditStation.comboHeure.getText());
			DateTimeFormat format = new DateTimeFormat(EditStation.comboHeure.getText());
			
			//récupérer le texte en question et creer une nouvelle heure
			ligne = Utils.lignesBrutes.get(Integer.parseInt(EditStation.textHeureLigne.getText())-1);
			string = ligne.substring(Integer.parseInt(EditStation.textHeureDebut.getText())-1, Integer.parseInt(EditStation.textHeureFin.getText()));
			System.out.println(ligne);
			System.out.println(string);
			Long hourLong = (Long)format.parseObject(string);
			
			
		} catch (ParseException e) {
			System.out.println("erreur format Heure ");
			Utils.messageErreurAppend("Erreur de format d'Heure : Attendu => "+string+" Lu => "+EditStation.comboHeure.getText());
			
			ok = ok && false;
			e.printStackTrace();
			//Utils.messageErreurAppend(e.)
		}
		
		
		
		//DATE
//		check valeurs ligne/debut/fin
		resultat1 = Regexp.checkLigneDebutfin(EditStation.textDateLigne, EditStation.textDateDebut, EditStation.textDateFin, "Date"); 
		ok = ok && resultat1;
		
		//Utils.messageErreurAppend("\n===[Check section : Date]===\n");
		System.out.println("format date = "+EditStation.comboDate.getText());
		try {
			
			DateTimeFormat format = new DateTimeFormat(EditStation.comboDate.getText());
			
			//récupérer le texte en question et creer une nouvelle heure
			//on a ligne debut fin donc parcours lignes de texte
			ligne = Utils.lignesBrutes.get(Integer.parseInt(EditStation.textDateLigne.getText())-1);
			string = ligne.substring(Integer.parseInt(EditStation.textDateDebut.getText())-1, Integer.parseInt(EditStation.textDateFin.getText()));
			System.out.println(ligne);
			System.out.println(string);
			Long dateLong = (Long)format.parseObject(string);
			
			
		} catch (ParseException e) {
			System.out.println("erreur format date ");
			Utils.messageErreurAppend("\nErreur de format de Date : Attendu => "+string+" Lu => "+EditStation.comboDate.getText());
			ok = ok && false;
			e.printStackTrace();
			//Utils.messageErreurAppend(e.)
		}
		
		
		Utils.messageErreurAppend("\n===[Check section : Profondeur]===\n");
		if (EditStation.buttonProfondeurPos.getSelection()){
			resultat1 = Regexp.checkLigneDebutfin(EditStation.textProfondeurLigne, EditStation.textProfondeurDebut, EditStation.textProfondeurFin, "Profondeur"); 
			ok = ok && resultat1;
			if (resultat1){
				Utils.messageErreurAppend("...ok");
			}
			
		} else {
			
			if (Float.parseFloat(EditStation.textProfondeur.getText()) > 0){
				ok = ok && true;
				Utils.messageErreurAppend("...ok");
			} else {
				ok = ok && false;
				Utils.messageErreurAppend("   Erreur sur la profondeur");
			}
			
		}
		
		
		if (ok){ 	
			okStation();
			}else {ViewFile.tabItem2.setText("*** Station ***");
			ViewFile.etape = 2;}
		
		
		
		//okStation();
	}

	
	
	public static void validationData(){
//		 test de toutes les valeurs de Data
		
		okData();
	}
	
	
	
	private static void okDesc(){
//		envoi des valeurs dans le modèle
		//attention aux boutons radio
	
//		puis remplissage du modèle avec les valeurs de l'IHM
		DataModel.initModele(ViewFile.dossierCampagne, ViewFile.checkBox.getSelection(), ViewFile.nomFichier);
		
		Utils.messageErreur("Etape validée, vous pouvez passer à la suivante...");
		
		//on rajoute "ok" dans l'onglet description
		ViewFile.tabItem.setText("[Description]");
		
		//on valide l'étape 1
		ViewFile.etape = 1;
		EditDesc.validee = 1;
		
		System.out.println("Etape.okDesc()");
		
		
		
	}
	

	private static void okSurvey(){
		
		DataModel.initSurvey();
		ViewFile.tabItem1.setText("[Campagne]");
		
		Utils.messageErreur("Etape validée, vous pouvez passer à la suivante...");
		
		//validation etape 2
		ViewFile.etape = 2;
//		envoi des valeurs dans le modèle
		
		
	}
	

	private static void okStation(){
		
//		envoi des valeurs dans le modèle	
		Utils.messageErreur("Etape validée, vous pouvez passer à la suivante...");
		DataModel.initStation();
		ViewFile.tabItem2.setText("[Station]");
		ViewFile.etape = 3;
	}
	

	private static void okData(){
		DataModel.initData();
		ViewFile.tabItem3.setText("[Données]");
		ViewFile.etape = 4;
//		envoi des valeurs dans le modèle
		
	}
	
	
	
}
