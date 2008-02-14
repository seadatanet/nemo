package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.input.HeaderFragment;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.util.Messages;

public class Utils {

	public static String texteBrut, texteFinal; // texte brut entier et texte

	public static StringBuffer stringBuffer;

	public static int progression;

	public static ArrayList<String> lignesBrutes; // lignes du texte

	public static int[] taillesCumLignes;

	public static Configuration configuration;

	public static StyleRange[] stylesCouleurs;

	public static Font font;

	public static ProgressBar progressbar;

	public static InputModel inputModel;

	public static void insertTextFich(String fich, StyledText zone)
			throws IOException {

		int nbreLignes, tailleLigne, tailleMax, nbreRegle, i, j, k, tmp, cumul, indexMax;
		long tailleLigneOctets, tailleOctets, tailleOctetsTot;
		String ligne;

		tailleLigne = 0;
		tailleMax = 0;
		texteBrut = ""; //$NON-NLS-1$
		texteFinal = ""; //$NON-NLS-1$
		tailleOctets = tailleOctetsTot = tailleLigneOctets = 0;
		nbreLignes = 0;

		lignesBrutes = new ArrayList<String>();
		stringBuffer = new StringBuffer();

		File objFich = new File(fich);
		tailleOctetsTot = objFich.length();

		// lecture et concaténation
		BufferedReader entree = new BufferedReader(new FileReader(fich));
		do {
			// System.out.println(nbreLignes);
			ligne = entree.readLine();

			if (ligne != null) {
				tailleLigne = ligne.length();

				lignesBrutes.add(ligne);
				stringBuffer.append(ligne);
				stringBuffer.append("\n"); //$NON-NLS-1$

				nbreLignes = nbreLignes + 1;

			}

		} while (ligne != null);
		entree.close();

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

		// affichage texte
		// changement de font
		font = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$
		zone.setFont(font);

		// log ok
		ViewLog.styledTextLogErreurs.setText(Messages.getString("Utils.4")); //$NON-NLS-1$

		// début d'infos du fichier

		ViewLog.styledTextLogInfos
				.setText(Messages.getString("Utils.5") + fich); //$NON-NLS-1$
		ViewLog.styledTextLogInfos
				.append(Messages.getString("Utils.6") + nbreLignes); //$NON-NLS-1$

		// réactivation de l'option série temporelle onglet DESC
		ViewFile.checkBoxTempo.setEnabled(true);
		ViewFile.checkBoxTempo.setSelection(false);

		// RAZ global si réouverture fichier en cours de processus
		Utils.razGlobal();

		EditDesc.nomNemo();
		DataModel.ouverture = false;

		// reset de l'étape de conversion
		ViewFile.etape = 0;

	}

	public static void remplissageTexte(StyledText styledText) {
		int taille;
		taille = lignesBrutes.size();

		for (int i = 0; i < taille; i++) {

			String tmp = lignesBrutes.get(i);
			stringBuffer.append(tmp);
			stringBuffer.append("\n"); //$NON-NLS-1$

		}

		texteBrut = stringBuffer.toString();
	}

	public static void insertTexte(String chaine) {

		// affichage de la règle de numéros 161 caractères (calculer la taille
		// max des lignes en entrée puis seulement rajouter
		// la règle concaténée au début du texteBrut

		texteBrut = ""; //$NON-NLS-1$

	}

	public static void inject(Text ligne, Text debut, Text fin,
			StyledText styledtext) {
		int deb, fi, lign, i;

		// on récupère les coordonnées de la sélection

		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

//		System.out.println("deb clic = " + deb);
//		System.out.println("fi clic = " + fi);

		if (deb == fi) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {

			// System.out.println("deb clic = " + deb);
			// System.out.println("fi clic = " + fi);

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
				if ((deb >= taillesCumLignes[i])
						&& (deb < taillesCumLignes[i + 1])) {
					lign = i + 1;
					break;
				}

			}

			// remplissage ligne dans interface
			ligne.setText("" + lign); //$NON-NLS-1$

			deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne

			// nbre caracs par rapport
			// debut de ligne
			fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

			// remplissage debut et fin dans interface
			debut.setText("" + deb); //$NON-NLS-1$
			fin.setText("" + fi); //$NON-NLS-1$

			debut.pack();
			ligne.pack();
			fin.pack();

			Utils.messageErreur("");
		}
	}
	
	public static void test(Text test, StyledText styledtext, Text ligne, Text debut, Text fin) {
		int deb, fi;

		/*// on récupère les coordonnées de la sélection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;*/
		
		deb = Integer.parseInt(debut.getText()) + taillesCumLignes[Integer.parseInt(ligne.getText()) - 1];
		fi = Integer.parseInt(fin.getText()) + taillesCumLignes[Integer.parseInt(ligne.getText()) - 1];
		
		styledtext.setSelection(deb-1, fi);
		
		// remplissage test dans interface
		test.setText(styledtext.getText(deb-1, fi-1));
		test.pack();
	}
	
	public static void test(Text test, Text saisie) {
		test.setText(saisie.getText());

		test.pack();
	}

	public static void injectData(StyledText styledtext) {
		int deb, fi, lign, i;

		// on récupère les coordonnées de la sélection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		if (deb == fi) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {

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
				if ((deb >= taillesCumLignes[i])
						&& (deb < taillesCumLignes[i + 1])) {
					lign = i + 1;
					break;
				}

			}

			// remplissage ligne dans interface
			// ligne = lign;

			deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur ligne
			// :
			// nbre caracs par rapport
			// debut de ligne
			fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

			// System.out.println("DEBUT = " + deb + " FIN= " + fi);

			// remplissage debut et fin dans interface
			TableData.debut = deb;
			TableData.fin = fi;

			Utils.messageErreur("");
		}
	}

	public static int calculNbreLignes(Cursor cursor) {

		int nbre = 0, lignDepart, lignFin;

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulées

		if (cursor.getX() == cursor.getY()) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {
			// calcul ligne depart
			for (int i = 0; i < lignesBrutes.size(); i++) {

				if (cursor.getX() == taillesCumLignes[i]) {
					lignDepart = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1)) {
					lignDepart = i + 1;
					break;
				}
				if ((cursor.getX() >= taillesCumLignes[i])
						&& (cursor.getX() < taillesCumLignes[i + 1])) {
					lignDepart = i + 1;
					break;
				}

			}
			
			// calcul ligne fin
			for (int i = 0; i < lignesBrutes.size(); i++) {

				if (cursor.getY() == taillesCumLignes[i]) {
					lignFin = i + 1;
					break;
				}
				if (i == (lignesBrutes.size() - 1)) {
					lignFin = i + 1;
					break;
				}
				if ((cursor.getY() >= taillesCumLignes[i])
						&& (cursor.getY() < taillesCumLignes[i + 1])) {
					lignFin = i + 1;
					break;
				}

			}

			// remplissage nbreLignes dans interface
			nbre = (lignFin - lignDepart + 1); //$NON-NLS-1$

		}

		return nbre;
	}

	public static void injectNbreLignes(Text nbrelignes, StyledText styledtext) {
		int deb, fi, lignDepart, lignFin, i;

		// on récupère les coordonnées de la sélection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulées

		if (deb == fi) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {

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
				if ((deb >= taillesCumLignes[i])
						&& (deb < taillesCumLignes[i + 1])) {
					lignDepart = i + 1;
					break;
				}

			}

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
				if ((fi >= taillesCumLignes[i])
						&& (fi < taillesCumLignes[i + 1])) {
					lignFin = i + 1;
					break;
				}

			}

			// System.out.println("ligne fin = " + lignFin);

			// remplissage nbreLignes dans interface
			nbrelignes.setText("" + (lignFin - lignDepart + 1)); //$NON-NLS-1$
			nbrelignes.pack();
			Utils.messageErreur("");
		}
	}

	public static void calculPosxPosy(HeaderFragment frag, Cursor cursor) {
		// reconstitue la position x et y du surlignement souris à partir du
		// fragment

		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();
		offsetDebut = frag.getFirstCharOffset();
		offsetFin = frag.getLastCharOffset();
		
		cursor.setX(taillesCumLignes[ligneDebut - 1] + offsetDebut);
		cursor.setY(taillesCumLignes[ligneFin - 1] + offsetFin);

	}

	public static void calculOffsetDebOffsetFin(HeaderFragment frag,
			Cursor cursor) {
		// calcule debut et fin à partir du surlignement souris
		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();

		offsetDebut = cursor.getX() - taillesCumLignes[ligneDebut - 1] + 1;
		offsetFin = cursor.getY() - taillesCumLignes[ligneFin - 1];
//		System.out.println("offsetdebut =" + offsetDebut + "  offsetfin="
//				+ offsetFin);
		if (offsetDebut < 0) {
			frag.setFirstCharOffset(1);
		} else {
			frag.setFirstCharOffset(offsetDebut);
		}
		
		if (offsetFin > 0) {
			frag.setLastCharOffset(offsetFin);
		}
		
		/*if (EditStation.buttonVar.getSelection()) {
			frag.setLastCharOffset(-1);
		} else {
			frag.setLastCharOffset(offsetFin);
		}*/

	}

	public static void calculNbreLignesPuisRemplissageFrag(HeaderFragment frag) {
		int deb, fi, lignDepart, lignFin, i, nbrelignes;

		// on récupère les coordonnées de la sélection
		deb = frag.getFirstCharOffset();
		fi = frag.getLastCharOffset();

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		// lignDepart = frag.getFirstLine();
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

		}

		// System.out.println("ligne depart = " + lignDepart);

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

		// System.out.println("ligne fin = " + lignFin);

		// nbre de lignes effectif
		nbrelignes = (lignFin - lignDepart + 1);

		if (nbrelignes == 1) {
			// frag.setSingleLine(true);
			frag.setLastLine(lignDepart);
		} else {
			frag.setSingleLine(false);
			frag.setLastLine(frag.getFirstLine() + nbrelignes - 1);

			// calcul de l'offset réel sur la dernière ligne
			int cpt = fi;
			for (int j = lignDepart - 1; j < lignFin - 1; j++) {
				cpt = cpt - Utils.lignesBrutes.get(j).length();
			}
			// en sortie de boucle, cpt a la valeur de l'offset de fin sur la
			// dernière ligne du commentaire
//			System.out.println("offset en ligne fin = " + cpt);
//			System.out.println("ligne fin = " + lignFin);
//			System.out.println("ligne debut = " + lignDepart);

			if (cpt > 0) {
				cpt = cpt - (nbrelignes - 1);
			}
			
			frag.setLastCharOffset(cpt);
		}

	}

	public static void calculNbreLignesPuisRemplissageFragClic(
			HeaderFragment frag, Cursor cursor) {
		int deb, fi, lignDepart, lignFin, i, nbrelignes;

		// calcul nbre ligne/debut/fin en fonction de la zone de surlignement
		// souris

		// on récupère les coordonnées de la sélection
		deb = cursor.getX();
		fi = cursor.getY();

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		// lignDepart = frag.getFirstLine();
		lignDepart = 1;
		lignFin = 1;
		// lignFin =frag.getFirstLine();

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

		// System.out.println("ligne depart = " + lignDepart);

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

		// System.out.println("ligne fin = " + lignFin);

		// nbre de lignes effectif
		nbrelignes = (lignFin - lignDepart + 1);

		if (nbrelignes == 1) {
			// frag.setSingleLine(true);
			frag.setLastLine(lignDepart);
		} else {
			frag.setSingleLine(false);
			frag.setLastLine(frag.getFirstLine() + nbrelignes - 1);

		}
		calculOffsetDebOffsetFin(frag, cursor);

	}

	public static void validationEtape(int onglet) {
		// messageErreur("Vous devez impérativement terminer la ou les étapes
		// précédentes avant de continuer." +"\n==>étapes " + tmp + " à " +
		// onglet);

		// check etape/onglet

		// à partir de onglet 0

		if ((onglet == 1) && (ViewFile.affiche == 0)) {
			ViewLog.aideSurvey();
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
			ViewLog.aideStation();
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
			ViewLog.aideData();
			if (EditData.validee == 1) {
				EditDesc.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditDesc.razEdition();

					if (EditData.loaded == 1) {
						EditData.DrawingBack();
					} else {
						EditData.affichageEdition();
					}

				} else {
					if (EditDesc.loaded == 1) {
						EditDesc.razEdition();
					}
					ViewFile.affiche = 3;
				}

			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 0)) {
			ViewLog.aideConversion();
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
			ViewLog.aideStation();
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
			ViewLog.aideDesc();
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
			ViewLog.aideData();
			if (EditData.validee == 1) {
				EditSurvey.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditSurvey.razEdition();

					if (EditData.loaded == 1) {
						EditData.DrawingBack();
					} else {
						EditData.affichageEdition();
					}

				} else {
					if (EditSurvey.loaded == 1) {
						EditSurvey.razEdition();
						ViewFile.affiche = 3;
					}

				}
			}
		}

		if ((onglet == 4) && (ViewFile.affiche == 1)) {
			ViewLog.aideConversion();
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
			ViewLog.aideData();
			if (EditData.validee == 1) {
				EditStation.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee == 1) {
					EditStation.razEdition();

					if (EditData.loaded == 1) {
						EditData.DrawingBack();
					} else {
						EditData.affichageEdition();
					}

				} else {
					if (EditStation.loaded == 1) {
						EditStation.razEdition();
						ViewFile.affiche = 3;
					}

				}
			}
		}

		if ((onglet == 1) && (ViewFile.affiche == 2)) {
			ViewLog.aideSurvey();
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
			ViewLog.aideDesc();
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
			ViewLog.aideConversion();
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
			ViewLog.aideDesc();
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
			ViewLog.aideSurvey();
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
			ViewLog.aideStation();
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
			ViewLog.aideConversion();
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
			ViewLog.aideDesc();
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
			ViewLog.aideSurvey();
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
			ViewLog.aideStation();
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
			ViewLog.aideData();
			if (EditData.validee == 1) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditData.DrawingBack();
			} else {
				if (EditData.loaded == 1) {
					EditData.DrawingBack();
				}
				ViewFile.affiche = 3;
			}
		}

	}

	public static Boolean findFiles(String directoryPath) {
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		int nbre = subfiles.length;
		String nom = subfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		Pattern p = Pattern.compile("[.]");
		String[] mots = p.split(nom);
		String ext;
		if (mots.length == 1) {
			ext = "";
		} else {
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++) {
			Pattern p1 = Pattern.compile("[.]");
			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.matches(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}

		if (ok) {

			ViewFile.nomFichier = directoryPath + "/" + subfiles[0].getName(); //$NON-NLS-1$
			ViewFile.nomRepertoire = directoryPath;

			try {
				Utils.insertTextFich(ViewFile.nomFichier,
						ViewFile.textAreaDescription);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return ok;
		
	}
	
	public static Boolean findFilesCollectCas1(String directoryPath) {
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		int nbre = subfiles.length;
		String nom = subfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		Pattern p = Pattern.compile("[.]");
		String[] mots = p.split(nom);
		String ext;
		if (mots.length == 1) {
			ext = "";
		} else {
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++) {
			Pattern p1 = Pattern.compile("[.]");
			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.matches(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}

		File[] subsubfiles = subfiles[0].listFiles();

		int nbre2 = subsubfiles.length;
		String nom2 = subsubfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		mots = p.split(nom2);

		if (mots.length == 1) {
			ext = "";
		} else {
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++) {
			Pattern p1 = Pattern.compile("[.]");
			String mots1[] = p.split(subsubfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.matches(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}
		
		if (ok) {

			ViewFile.nomFichier = directoryPath + "/" + subfiles[0].getName() + "/" +  subsubfiles[0].getName(); //$NON-NLS-1$
			ViewFile.nomRepertoire = directoryPath;

			try {
				Utils.insertTextFich(ViewFile.nomFichier,
						ViewFile.textAreaDescription);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return ok;
		
	}
	


	public static void messageErreur(String message) {
		ViewLog.styledTextLogErreurs.setText(message);
		Color truc = new Color(Application.display, 255, 0, 0);
		// ViewLog.styledTextLogErreurs.setLineBackground(0, 1, truc);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = message.length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

	}

	public static void messageErreurAppend(String message) {

		ViewLog.styledTextLogErreurs.append("\n");

		ViewLog.styledTextLogErreurs.append(message);
		Color truc = new Color(Application.display, 255, 0, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

	}

	public static void messageOK(String message) {

		ViewLog.styledTextLogErreurs.setText(message);
		Color truc = new Color(Application.display, 0, 151, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

	}

	public static void messageOKAppend(String message) {

		ViewLog.styledTextLogErreurs.append(message);
		Color truc = new Color(Application.display, 0, 151, 0);

		StyleRange style = new StyleRange();
		style.start = 0;
		style.length = ViewLog.styledTextLogErreurs.getText().length();

		style.foreground = truc;

		ViewLog.styledTextLogErreurs.setStyleRange(style);
		ViewLog.tabFolderLog.setSelection(0);

	}

	public static void initConfig() {
		// chargement de la config
		if (configuration == null) {
			configuration = Configuration.getSingleton();
		} else {

			configuration.releaseSingleton();
			configuration = Configuration.getSingleton();
		}

	}

	public static void saveConfig() {
		// sauvegarde de la config dans le fichier qui va bien
		// CONTACT
		configuration.setContact(""); //$NON-NLS-1$
		
		// DOSSIERS
		configuration.setModelsDirectory(OptionsAction.textDossierModele.getText());
		configuration.setCruisesDirectory(OptionsAction.textDossierCampagne.getText());
		
		// LANGUE
		if (OptionsAction.comboLangue.getItem(
				OptionsAction.comboLangue.getSelectionIndex()).matches(
				"Français")) { //$NON-NLS-1$
			configuration.setLang("fr_FR"); //$NON-NLS-1$
		} else {
			configuration.setLang("en_GB"); //$NON-NLS-1$
		}
		
		// CENTRE DE DONNEES
		String dataCenter = OptionsAction.comboCentreDonnees.getText();
		dataCenter = dataCenter.substring(0,dataCenter.indexOf(" "));
		configuration.setDataCenter(dataCenter);

		// WS
		configuration.setNautilusWebServiceURL(OptionsAction.textAreaNautilus
				.getText());
		configuration
				.setNemoWebServiceURL(OptionsAction.textAreaNemo.getText());

		// CDI
		configuration.setIdEdmoRespCdi(OptionsAction.textIdEdmoRespCdi.getText());
		configuration.setCodeEdmoDonnees(OptionsAction.textCodeEdmoDonnees.getText());
		configuration.setIdEdmoRespDonnees(OptionsAction.textIdEdmoRespDonnees.getText());
		configuration.setIdEdmoDistDonnees(OptionsAction.textIdEdmoDistDonnees.getText());
		configuration.setCdiURL(OptionsAction.textUrlCdi.getText());
		configuration.setCdiMethod(OptionsAction.textMethodeCdi.getText());
		
		// Enregistrement de la configuration
		configuration.save();
		
		// System.out.println("sauvegarde options... ok");

	}

	public static void razGlobal() {
		// efface tous les onglets qui ont été affichés jusque là en préparation
		// du chargement
		// d'un nouveau modèle

		// si au moins une des sections d'un onglet est créée alors on peut les
		// effacer toutes
		if (EditDesc.sectionEnteteFichier != null) {
			// //effacement boutons
			EditDesc.razEdition();
			// EditDesc.buttonExpandDesc.dispose();
			// EditDesc.buttonExpandDesc2.dispose();
			// EditDesc.buttonRAZ.dispose();
			// EditDesc.buttonValidateDesc.dispose();
			// //effacement sections
			// for (int i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){
			// EditDesc.listeSectionsDesc.get(i).dispose();
			// System.out.println("effacement zone edition etape description
			// fichier ==> ok");
			// }
			// //effacement titre formulaire
			// ViewEdit.formEdition.setText("");
			//			
			// //refresh global
			// ViewEdit.formEdition.reflow(true);
		}

		if (EditSurvey.sectionReference != null) {

			// effacement des boutons valider/expand/init serveur
			EditSurvey.buttonExpand.dispose();
			EditSurvey.buttonExpand2.dispose();
			EditSurvey.buttonValidate.dispose();
			EditSurvey.buttonXML.dispose();
			EditSurvey.buttonInitServeur.dispose();
			EditSurvey.buttonRAZ.dispose();

			for (int i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {
				EditSurvey.listeSectionsSurvey.get(i).dispose();
			}
			// effacement titre formulaire
			ViewEdit.formEdition.setText(""); //$NON-NLS-1$

			// refresh global
			ViewEdit.formEdition.reflow(true);

			EditSurvey.validee = 0;
			EditSurvey.loaded = 0;

		}

		if (EditStation.sectionTypeDonnees != null) {

			// effacement des boutons valider/expand/init serveur
			EditStation.buttonExpand.dispose();
			EditStation.buttonExpand2.dispose();
			EditStation.buttonValidate.dispose();
			EditStation.buttonRAZ.dispose();

			for (int i = 0; i <= EditStation.listeSectionsStation.size() - 1; i++) {
				EditStation.listeSectionsStation.get(i).dispose();
			}

			// titre à vide
			ViewEdit.formEdition.setText(""); //$NON-NLS-1$

			// refresh global
			ViewEdit.formEdition.reflow(true);

			EditStation.loaded = 0;
			EditStation.validee = 0;
		}

		if (EditData.sectionTotal != null) {
			// effacement des boutons valider/expand/init serveur

			EditData.buttonValidate.dispose();
			EditData.buttonRAZ.dispose();

			// effacement des boutons radio
			EditData.buttonFreqEchant.dispose();
			EditData.buttonLigneDonnees.dispose();
			// EditData.sectionTotal.dispose();
			for (int i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {
				EditData.listeSectionsData.get(i).dispose();
			}

			// titre à vide
			ViewEdit.formEdition.setText(""); //$NON-NLS-1$

			// refresh global
			ViewEdit.formEdition.reflow(true);

			EditData.loaded = 0;
			EditData.validee = 0;

		}

		// effacement de la table data
		if (ViewFile.tableData != null) {
			ViewFile.tableData.dispose();
		}
		;

		EditDesc.validee = 0;

		// effacement des listes des onglets Survey et Data
		if (TableDataSurvey.types != null) {
			TableDataSurvey.types = new ArrayList<TypeSurvey>();
		}
		if (TableData.types != null) {
			TableData.types = new ArrayList<TypeData>();
		}

		// reset des noms des onglets
		ViewFile.tabItem.setText(Messages.getString("Utils.21")); //$NON-NLS-1$
		if (!ViewFile.tabItem1.isDisposed())
		{
			ViewFile.tabItem1.setText(Messages.getString("Utils.22")); //$NON-NLS-1$		
		}
		ViewFile.tabItem2.setText(Messages.getString("Utils.23")); //$NON-NLS-1$
		ViewFile.tabItem3.setText(Messages.getString("Utils.24")); //$NON-NLS-1$
		ViewFile.tabItem9.setText(Messages.getString("Utils.25")); //$NON-NLS-1$


		
		// effacement du bouton conversion
		if (EditConversion.buttonConversion != null) {
			EditConversion.razEdition();
		}

		ViewEdit.formEdition.reflow(true);

	}

	public static void messageInfo(String message) {
		ViewLog.styledTextLogInfos.setText(message);
	}

	public static void messageInfoAppend(String message) {
		ViewLog.styledTextLogInfos.append("\n" + message); //$NON-NLS-1$
	}

	public static void selectComboValeur(Combo combo, String valeur) {
		int i = 0;
		combo.select(i);
		
		while (i < combo.getItemCount() && !combo.getText().equals(valeur)) {
			i++;
			combo.select(i);
		}
		
		if (!combo.getText().equals(valeur)) {
			combo.add(valeur);
			combo.select(combo.getItemCount() - 1 );
		}
	}
}
