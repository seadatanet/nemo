package fr.ifremer.nemo.ui;

import java.util.regex.*;

import org.eclipse.swt.widgets.Table;


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
		if (EditDesc.buttonLignesEnteteStation.getSelection()){
			resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteStation, "Entête de station");	
		ok = ok && resultat;
		}
	
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
//		 test de toutes les valeurs de Survey	
		
		Boolean ok, resultat;
		ok = true;
		
//		raz du message d'erreurs
		Utils.messageErreur("");
		
		if (ok){ 	
			okSurvey();
			}else {ViewFile.tabItem1.setText("*** Campagne ***");
			ViewFile.etape = 1;}
		

	}

	public static void validationStation(){
//		test des valeurs de Station
		
		
		Boolean ok, resultat;
		ok = true;
		
//		raz du message d'erreurs
		Utils.messageErreur("");
		
		if (ok){ 	
			okStation();
			}else {ViewFile.tabItem2.setText("*** Station ***");
			ViewFile.etape = 2;}
		
		
		
		okStation();
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
		
		//validation etape 2
		ViewFile.etape = 2;
//		envoi des valeurs dans le modèle
		
		
	}
	

	private static void okStation(){
		
//		envoi des valeurs dans le modèle	
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
