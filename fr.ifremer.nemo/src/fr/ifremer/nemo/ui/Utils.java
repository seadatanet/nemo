package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import fr.ifremer.nemo.input.*;

public class Utils {

	public static String texteBrut, texteFinal; // texte brut entier et texte
	public static StringBuffer stringBuffer;
	// avec regle dynamique

	public static ArrayList<String> lignesBrutes; // lignes du texte

	public static int[] taillesCumLignes;

	public static StyleRange[] stylesCouleurs;

	public static Font font;

	public static ProgressBar progressbar;

	public static InputModel inputModel;

	public static void insertTextFich(String fich, StyledText zone)
			throws IOException {

		// int maxLignes;

		int nbreLignes, tailleLigne, tailleMax, nbreRegle, i, j, k, tmp, cumul, indexMax;
		long tailleLigneOctets, tailleOctets, tailleOctetsTot;
		String ligne;

		
		tailleLigne = 0;
		tailleMax = 0;
		texteBrut = "";
		texteFinal = "";
		tailleOctets = tailleOctetsTot = tailleLigneOctets = 0;
		nbreLignes = 0;

		// initialisation du tableau de lignes en fonction des préférences de
		// Nemo (nbre max de lignes)
		// pour le moment 1000 max
		lignesBrutes = new ArrayList<String>();
		stringBuffer = new StringBuffer();
		
		File objFich = new File(fich);
		tailleOctetsTot = objFich.length();
		System.out.println("taille fichier ==>" + objFich.length());
	

		// lecture et concaténation
		BufferedReader entree = new BufferedReader(new FileReader(fich));
		do {
			// System.out.println(nbreLignes);
			ligne = entree.readLine();

			if (ligne != null) {
				tailleLigne = ligne.length();

				lignesBrutes.add(ligne);
				stringBuffer.append(ligne);
				stringBuffer.append("\n");
				
				nbreLignes = nbreLignes + 1;
				// System.out.println(nbreLignes % 50);
				if ((nbreLignes % 100) == 0) {
					System.out.println("ligne lue : " + nbreLignes);
				}

			}
			
		} while (ligne != null);
		entree.close();
		System.out.println("Fin lecture fichier " + fich);

		// calcul des tailles cumulées
		taillesCumLignes = new int[nbreLignes];
		indexMax = nbreLignes - 1;
		cumul = 0;

		taillesCumLignes[0] = 0;
		for (i = 1; i <= indexMax; i++) {
			cumul = cumul + lignesBrutes.get(i - 1).length() + 1;
			taillesCumLignes[i] = cumul;

		}

		
		texteBrut = stringBuffer.toString();
		
		// affichage
		System.out.println("Fin de insertText ()");

		// affichage texte
		// changement de font
		font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		zone.setFont(font);

		// zone.getHorizontalBar().

		// zone.setText(texteFinal);

		// log ok
		ViewLog.styledTextLogErreurs.setText("Chargement du fichier réussi.");

		// début d'infos du fichier
		ViewLog.styledTextLogInfos.setText("Localisation du fichier : " + fich);
		ViewLog.styledTextLogInfos.append("\nNombre de lignes : " + nbreLignes);

	}

	
	public static void remplissageTexte(StyledText styledText){
		int taille;
		taille = lignesBrutes.size();
	
		/*
		if (taille >= 1000){
			for (int i =0; i<500;i++){
				
				String tmp = lignesBrutes.get(i);
				stringBuffer.append(tmp);
				//texteBrut = texteBrut + tmp;
				stringBuffer.append("\n");
				//texteBrut = texteBrut + "\n";
				System.out.println(""+i/5+"%");
//				styledText.append(tmp);
//				styledText.append("\n");
			}
			
			
			
			for (int i =(taille-500); i<taille;i++){
				String tmp = lignesBrutes.get(i);
				stringBuffer.append(tmp);
				stringBuffer.append("\n");
//				texteBrut = texteBrut + tmp;
//				texteBrut = texteBrut + "\n";
//				styledText.append(tmp);
//				styledText.append("\n");
				System.out.println(""+(taille-i)/5+"%");
			}
			
			
			
		}else{
		
		for (int i =0; i<taille;i++){
			String tmp = lignesBrutes.get(i);
			texteBrut = texteBrut + tmp;
			styledText.append(tmp);
			styledText.append("\n");
		}
		}
		*/
		
		for (int i =0; i<taille;i++){
			
			String tmp = lignesBrutes.get(i);
			stringBuffer.append(tmp);
			stringBuffer.append("\n");
		
		}
		
		texteBrut = stringBuffer.toString();
	}
	
	public static void insertTexte(String chaine) {

		// affichage de la règle de numéros 161 caractères (calculer la taille
		// max des lignes en entrée puis seulement rajouter
		// la règle concaténée au début du texteBrut

		texteBrut = "";

		// ViewFich2.zone.setText(texteBrut);

		// miseCouleurMot(22, 27, ViewFich2.zone);

	}

	public static void inject(Text ligne, Text debut, Text fin,
			StyledText styledtext) {
		int deb, fi, lign, i;

		// on récupère les coordonnées de la sélection

		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		System.out.println("deb clic = " + deb);
		System.out.println("fi clic = " + fi);

		lign = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulées

		for (i = 0; i < lignesBrutes.size(); i++) {

			if (deb == taillesCumLignes[i]) {
				lign = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1)) {
				lign = i + 1;
				break;
			}
			if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1])) {
				lign = i + 1;
				break;
			}

			System.out.println("cumul = " + taillesCumLignes[i] + "i= " + i);

		}

		// remplissage ligne dans interface
		ligne.setText("" + lign);

		deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne :
		// nbre caracs par rapport
		// debut de ligne
		fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

		// remplissage debut et fin dans interface
		debut.setText("" + deb);
		fin.setText("" + fi);

	}

	public static void injectData(StyledText styledtext) {
		int deb, fi, lign, i;

		// on récupère les coordonnées de la sélection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		System.out.println("deb clic = " + deb);
		System.out.println("fi clic = " + fi);

		lign = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulées

		for (i = 0; i < lignesBrutes.size(); i++) {

			if (deb == taillesCumLignes[i]) {
				lign = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1)) {
				lign = i + 1;
				break;
			}
			if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1])) {
				lign = i + 1;
				break;
			}

			System.out.println("cumul = " + taillesCumLignes[i] + " i= " + i);

		}

		// remplissage ligne dans interface
		// ligne = lign;

		deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne :
		// nbre caracs par rapport
		// debut de ligne
		fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

		// System.out.println("DEBUT = " + deb + " FIN= " + fi);

		// remplissage debut et fin dans interface
		TableData.debut = deb;
		TableData.fin = fi;

		System.out.println("DEBUT data= " + TableData.debut + " FIN data= "
				+ TableData.fin);

	}

	public static void injectNbreLignes(Text nbrelignes, StyledText styledtext) {
		int deb, fi, lignDepart, lignFin, i;

		// on récupère les coordonnées de la sélection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		System.out.println("deb clic = " + deb);
		System.out.println("fi clic = " + fi);

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulées

		// calcul ligne depart
		for (i = 0; i < lignesBrutes.size(); i++) {

			if (deb == taillesCumLignes[i]) {
				lignDepart = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1)) {
				lignDepart = i + 1;
				break;
			}
			if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1])) {
				lignDepart = i + 1;
				break;
			}

			// System.out.println("cumul = " + taillesCumLignes[i] + "i= " + i);

		}

		System.out.println("ligne depart = " + lignDepart);

		// calcul ligne fin

		for (i = 0; i < lignesBrutes.size(); i++) {

			if (fi == taillesCumLignes[i]) {
				lignFin = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1)) {
				lignFin = i + 1;
				break;
			}
			if ((fi >= taillesCumLignes[i]) && (fi < taillesCumLignes[i + 1])) {
				lignFin = i + 1;
				break;
			}

		}

		System.out.println("ligne fin = " + lignFin);

		// remplissage nbreLignes dans interface
		nbrelignes.setText("" + (lignFin - lignDepart + 1));

	}

	public static void validationEtape(int onglet) {
		// messageErreur("Vous devez impérativement terminer la ou les étapes
		// précédentes avant de continuer." +"\n==>étapes " + tmp + " à " +
		// onglet);

		// check etape/onglet

		// à partir de onglet 0

		if ((onglet == 1) && (ViewFile.affiche == 0)) {
			if (EditSurvey.validee == 1) {
				EditDesc.razEdition();
				EditSurvey.DrawingBack();
			} else {
				if (EditDesc.validee == 1) {
					EditDesc.razEdition();
					EditSurvey.affichageEdition();
				} else {
					if (EditDesc.loaded == 1) {
						EditDesc.razEdition();
					}
					ViewFile.affiche = 1;
				}

			}
		}

		if ((onglet == 2) && (ViewFile.affiche == 0)) {
			if (EditStation.validee == 1) {
				EditDesc.razEdition();
				EditStation.DrawingBack();
			} else {
				if (EditSurvey.validee == 1) {
					EditDesc.razEdition();
					EditStation.affichageEdition();
				} else {
					if (EditDesc.loaded == 1) {
						EditDesc.razEdition();
					}
					ViewFile.affiche = 2;
				}

			}
		}

		if ((onglet == 3) && (ViewFile.affiche == 0)) {
			if (EditData.validee == 1) {
				EditDesc.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditDesc.razEdition();
					EditData.affichageEdition();
				} else {
					if (EditDesc.loaded == 1) {
						EditDesc.razEdition();
					}
					ViewFile.affiche = 3;
				}

			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 0)) {
			if (EditData.validee == 1) {
				EditDesc.razEdition();
				EditSurvey.affichageEdition();
			} else {
				if (EditDesc.loaded == 1) {
					EditDesc.razEdition();
				}
				ViewFile.affiche = 4;
			}
		}

		// *********************************************************************//
		// à partir de onglet 1

		if ((onglet == 2) && (ViewFile.affiche == 1)) {
			if (EditStation.validee == 1) {
				EditSurvey.razEdition();
				EditStation.DrawingBack();
			} else {
				if (EditSurvey.validee == 1) {
					EditSurvey.razEdition();
					EditStation.affichageEdition();
				} else {
					if (EditSurvey.loaded == 1) {
						EditSurvey.razEdition();
						ViewFile.affiche = 2;
					}

				}
			}
		}

		if ((onglet == 0) && (ViewFile.affiche == 1)) {
			if (EditDesc.validee == 1) {
				if (EditSurvey.loaded == 1) {
					EditSurvey.razEdition();
				}
				EditDesc.DrawingBack();
			} else {
				if (ViewFile.fileLoaded) {
					EditDesc.affichageEdition();
				}

				ViewFile.affiche = 0;
			}
		}

		if ((onglet == 3) && (ViewFile.affiche == 1)) {
			if (EditData.validee == 1) {
				EditSurvey.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditSurvey.razEdition();
					EditData.affichageEdition();
				} else {
					if (EditSurvey.loaded == 1) {
						EditSurvey.razEdition();
						ViewFile.affiche = 3;
					}

				}
			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 1)) {
			if (EditData.validee == 1) {
				EditSurvey.razEdition();
				EditConversion.affichageEdition();
			} else {
				EditSurvey.razEdition();
				ViewFile.affiche = 4;
			}
		}

		// *********************************************************************//
		// à partir de onglet 2

		if ((onglet == 3) && (ViewFile.affiche == 2)) {
			if (EditData.validee == 1) {
				EditStation.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditStation.razEdition();
					EditData.affichageEdition();
				} else {
					if (EditStation.loaded == 1) {
						EditStation.razEdition();
						ViewFile.affiche = 3;
					}

				}
			}
		}

		if ((onglet == 1) && (ViewFile.affiche == 2)) {
			if (EditSurvey.validee == 1) {
				if (EditStation.loaded == 1) {
					EditStation.razEdition();
				}
				EditSurvey.DrawingBack();
			} else {
				if (EditSurvey.loaded == 1) {
					EditSurvey.affichageEdition();
				}
				ViewFile.affiche = 1;
			}
		}

		if ((onglet == 0) && (ViewFile.affiche == 2)) {
			if (EditDesc.validee == 1) {
				if (EditStation.loaded == 1) {
					EditStation.razEdition();
				}
				EditDesc.DrawingBack();
			} else {
				if (ViewFile.fileLoaded) {
					EditDesc.affichageEdition();
				}
				ViewFile.affiche = 0;
			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 2)) {
			if (EditData.validee == 1) {
				EditStation.razEdition();
				EditConversion.affichageEdition();
			} else {

				if (EditStation.loaded == 1) {
					EditStation.razEdition();
					ViewFile.affiche = 4;

				}
			}
		}

		// *********************************************************************//

		// à partir de onglet 3
		if ((onglet == 0) && (ViewFile.affiche == 3)) {
			if (EditDesc.validee == 1) {
				if (EditData.loaded == 1) {
					EditData.razEdition();
				}
				EditDesc.DrawingBack();
			} else {
				if (ViewFile.fileLoaded) {
					EditDesc.affichageEdition();
				}
				ViewFile.affiche = 0;
			}
		}

		if ((onglet == 1) && (ViewFile.affiche == 3)) {
			if (EditSurvey.validee == 1) {
				if (EditData.loaded == 1) {
					EditData.razEdition();
				}
				EditSurvey.DrawingBack();
			} else {
				if (EditSurvey.loaded == 1) {
					EditSurvey.affichageEdition();
				}
				ViewFile.affiche = 1;
			}
		}

		if ((onglet == 2) && (ViewFile.affiche == 3)) {
			if (EditStation.validee == 1) {
				if (EditData.loaded == 1) {
					EditData.razEdition();
				}
				EditStation.DrawingBack();
			} else {
				if (EditStation.loaded == 1) {
					EditStation.affichageEdition();
				}
				ViewFile.affiche = 2;
			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 3)) {
			if (EditData.validee == 1) {
				EditData.razEdition();
				EditConversion.affichageEdition();
			} else {
				if (EditData.loaded == 1) {
					EditData.razEdition();
					ViewFile.affiche = 4;
				}
			}

		}

		// *********************************************************************//

		if ((onglet == 0) && (ViewFile.affiche == 4)) {
			if (EditDesc.validee == 1) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditDesc.DrawingBack();
			} else {
				if (ViewFile.fileLoaded) {
					EditDesc.affichageEdition();
				}
				ViewFile.affiche = 0;
			}
		}

		if ((onglet == 1) && (ViewFile.affiche == 4)) {
			if (EditSurvey.validee == 1) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditSurvey.DrawingBack();
			} else {
				if (EditSurvey.loaded == 1) {
					EditSurvey.affichageEdition();
				}
				ViewFile.affiche = 1;
			}
		}

		if ((onglet == 2) && (ViewFile.affiche == 4)) {
			if (EditStation.validee == 1) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditStation.DrawingBack();
			} else {
				if (EditStation.loaded == 1) {
					EditStation.affichageEdition();
				}
				ViewFile.affiche = 2;
			}
		}

		if ((onglet == 3) && (ViewFile.affiche == 4)) {
			if (EditData.validee == 1) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditData.DrawingBack();
			} else {
				if (EditData.loaded == 1) {
					EditData.affichageEdition();
				}
				ViewFile.affiche = 3;
			}
		}

	}

	public static void findFiles(String directoryPath) {
		File directory = new File(directoryPath);

		File[] subfiles = directory.listFiles();
		ViewFile.nomFichier = directoryPath + "\\" + subfiles[0].getName();
		ViewFile.nomRepertoire = directoryPath;

		System.out.println("Le chemin ==>" + directoryPath);
		System.out.println("Le fichier ==>" + ViewFile.nomFichier);

		try {
			Utils.insertTextFich(ViewFile.nomFichier,
					ViewFile.textAreaDescription);
		} catch (IOException e) {

			e.printStackTrace();
		}

		// if (!directory.exists()) {
		// System.out.println("Le fichier/répertoire '" + directoryPath
		// + "' n'existe pas");
		// } else if (!directory.isDirectory()) {
		// System.out.println("Le chemin '" + directoryPath
		// + "' correspond à un fichier et non à un répertoire");
		// } else {
		// File[] subfiles = directory.listFiles();
		// String message = "Le répertoire '" + directoryPath + "' contient "
		// + subfiles.length + " fichier"
		// + (subfiles.length > 1 ? "s" : "");
		// System.out.println(message);
		// for (int i = 0; i < subfiles.length; i++) {
		// System.out.println(subfiles[i].getName());
		// }
		// }
	}

	public static void messageErreur(String message) {
		ViewLog.styledTextLogErreurs.setText(message);
		Color truc = new Color(Application.display, 255, 0, 0);
		// ViewLog.styledTextLogErreurs.setLineBackground(0, 1, truc);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = message.length();
		// style.fontStyle = SWT.BOLD;
		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		/*
		 * mettre en gras et en couleur only le texte du message !
		 * message.length et ensuite new styleRange() de 0 à length
		 * 
		 * 
		 */

	}

	public static void messageErreurAppend(String message) {

		// TO DO : remettre en couleur les messages !!!
		ViewLog.styledTextLogErreurs.append(message);
		Color truc = new Color(Application.display, 255, 0, 0);
		// ViewLog.styledTextLogErreurs.setLineBackground(0, 1, truc);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();
		// style.fontStyle = SWT.BOLD;
		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		/*
		 * mettre en gras et en couleur only le texte du message !
		 * message.length et ensuite new styleRange() de 0 à length
		 * 
		 * 
		 */

	}

}
