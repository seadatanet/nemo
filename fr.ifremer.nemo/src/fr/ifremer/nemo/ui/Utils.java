package fr.ifremer.nemo.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.input.HeaderFragment;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.Global;
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

	private static FlagTable _blank;

	public static void insertTextFich(String fich, StyledText zone, boolean raz)
			throws IOException {

		int nbreLignes, i, cumul, indexMax;
		String ligne;

		texteBrut = ""; //$NON-NLS-1$
		texteFinal = ""; //$NON-NLS-1$
		nbreLignes = 0;

		lignesBrutes = new ArrayList<String>();
		stringBuffer = new StringBuffer();

		// lecture et concatenation
		BufferedReader entree = new BufferedReader(new FileReader(fich));
		do {
			// System.out.println(nbreLignes);
			ligne = entree.readLine();

			if (ligne != null) {

				lignesBrutes.add(ligne);
				stringBuffer.append(ligne);
				stringBuffer.append("\n"); //$NON-NLS-1$

				nbreLignes = nbreLignes + 1;

			}

		} while (ligne != null);
		entree.close();

		// calcul des tailles cumulees
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
		// ViewLog.styledTextLogErreurs.setText(Messages.getString("Utils.4"));
		// //$NON-NLS-1$

		// debut d'infos du fichier

		ViewLog.styledTextLogInfos
				.setText(Messages.getString("Utils.5") + fich); //$NON-NLS-1$
		ViewLog.styledTextLogInfos
				.append(Messages.getString("Utils.6") + nbreLignes); //$NON-NLS-1$

		// reactivation de l'option serie temporelle onglet DESC
		// ViewFile.checkBoxTempo.setEnabled(true);
		ViewFile.radProfilVertical.setEnabled(true);
		ViewFile.radSerieTempo.setEnabled(true);
		ViewFile.radTrajectoire.setEnabled(true);

		// FAE 089
		// ViewFile.checkBoxTempo.setSelection(false);

		// RAZ global si reouverture fichier en cours de processus

		EditDesc.nomNemo();

		if (raz) {
			Utils.razGlobal();

			DataModel.ouverture = false;

			// reset de l'etape de conversion
			ViewFile.etape = 0;
		}
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

		// affichage de la regle de numeros 161 caracteres (calculer la taille
		// max des lignes en entree puis seulement rajouter
		// la regle concatenee au debut du texteBrut

		texteBrut = ""; //$NON-NLS-1$

	}

	public static void inject(Text ligne, Text debut, Text fin,
			StyledText styledtext) {
		int deb, fi, lign, i;

		// on recupere les coordonnees de la selection

		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		if (deb == fi) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {

			// System.out.println("deb clic = " + deb);
			// System.out.println("fi clic = " + fi);

			lign = 1;
			// calcul de la ligne en fonction de la valeur de x
			// parcours du tableau des tailles de lignes cumulees

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

	public static void test(Text test, StyledText styledtext, Text ligne,
			Text debut, Text fin) {
		int deb, fi, lign;

		if (ligne.getText().equals("")) {
			lign = DataModel.stationModel.getNumberOfLines() + 1;
		} else {
			lign = Integer.parseInt(ligne.getText());
		}

		deb = Integer.parseInt(debut.getText()) + taillesCumLignes[lign - 1];
		fi = Integer.parseInt(fin.getText()) + taillesCumLignes[lign - 1];

		styledtext.setSelection(deb - 1, fi);

		// remplissage test dans interface
		test.setText(styledtext.getText(deb - 1, fi - 1));
		test.pack();
	}

	public static void test(Text test, Text saisie) {
		test.setText(saisie.getText());

		test.pack();
	}

	public static void injectData(StyledText styledtext, int debut, int fin) {
		int deb, fi, lign, i, index;

		// on recupere les coordonnees de la selection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;
		index = ViewFile.tableData.getSelectionIndex();

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		if (deb == fi) {
			Utils.messageErreur(Messages.getString("Utils.26"));

		} else {

			lign = 1;
			// calcul de la ligne en fonction de la valeur de x
			// parcours du tableau des tailles de lignes cumulees

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
			ViewFile.tableData.getItems()[index].setText(debut, String
					.valueOf(deb));
			ViewFile.tableData.getItems()[index].setText(fin, String
					.valueOf(fi));

			Utils.messageErreur("");
		}
	}

	public static void injectFlag(StyledText styledtext, FlagTable flagTable,
			Table table, int indexLigne, int indexDebut, int indexFin,
			int indexTest1, int indexTest2) {
		int index = table.getSelectionIndex();
		if (index >= 0 && table.getItems() != null) {
			int deb, fi, lign, i;

			// on recupere les coordonnees de la selection
			deb = styledtext.getSelection().x;
			fi = styledtext.getSelection().y;

			// System.out.println("deb clic = " + deb);
			// System.out.println("fi clic = " + fi);

			if (deb == fi) {
				Utils.messageErreur(Messages.getString("Utils.26"));
			} else {

				lign = 1;
				// calcul de la ligne en fonction de la valeur de x
				// parcours du tableau des tailles de lignes cumulees

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

				deb = deb - taillesCumLignes[lign - 1] + 1; // debut reel sur
				// ligne
				// :
				// nbre caracs par rapport
				// debut de ligne
				fi = fi - taillesCumLignes[lign - 1]; // fin reelle sur ligne

				// System.out.println("DEBUT = " + deb + " FIN= " + fi);

				// remplissage ligne dans interface
				table.getItems()[index].setText(indexLigne, String
						.valueOf(lign));

				// remplissage debut et fin dans interface
				table.getItems()[index]
						.setText(indexDebut, String.valueOf(deb));
				table.getItems()[index].setText(indexFin, String.valueOf(fi));

				// Test apres injection
				testFlag(styledtext, flagTable, table, indexLigne, indexDebut,
						indexFin, indexTest1, indexTest2);

				Utils.messageErreur("");
			}
		}
	}

	public static void testFlag(StyledText styledtext, FlagTable flagTable,
			Table table, int indexLigne, int indexDebut, int indexFin,
			int indexTest1, int indexTest2) {
		int deb, fi, lign;

		for (int i = 0; i < table.getItemCount(); i++) {
			try {
				lign = Integer
						.parseInt(table.getItems()[i].getText(indexLigne));

				deb = Integer.parseInt(table.getItems()[i].getText(indexDebut))
						+ taillesCumLignes[lign - 1];
				fi = Integer.parseInt(table.getItems()[i].getText(indexFin))
						+ taillesCumLignes[lign - 1];

				// styledtext.setSelection(deb - 1, fi);

				// remplissage test entree dans interface
				table.getItems()[i].setText(indexTest1, styledtext.getText(
						deb - 1, fi - 1));

				// remplissage test sortie dans interface
				String flagSDN = "0";
				int size = flagTable.getFlags().size();
				for (int j = 0; j < size; j++) {
					if (flagTable.getFlags().get(j).getLocal().equals(
							table.getItems()[i].getText(indexTest1))) {
						flagSDN = Utils.extraitCode(flagTable.getFlags().get(j)
								.getSdn());
						break;
					}
				}
				table.getItems()[i].setText(indexTest2, flagSDN);
			} catch (Exception e) {

			}
		}
	}

	public static void injectNbreLignes(Text nbrelignes, StyledText styledtext) {
		int deb, fi, lignDepart, lignFin, i;

		// on recupere les coordonnees de la selection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

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
		// reconstitue la position x et y du surlignement souris e partir du
		// fragment

		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();
		offsetDebut = frag.getFirstCharOffset();
		offsetFin = frag.getLastCharOffset();

		cursor.setX(taillesCumLignes[ligneDebut - 1] + offsetDebut - 1);
		cursor.setY(taillesCumLignes[ligneFin - 1] + offsetFin);

	}

	public static void calculOffsetDebOffsetFin(HeaderFragment frag,
			Cursor cursor) {
		// calcule debut et fin e partir du surlignement souris
		int offsetDebut, offsetFin, ligneDebut, ligneFin;

		ligneDebut = frag.getFirstLine();
		ligneFin = frag.getLastLine();

		offsetDebut = cursor.getX() - taillesCumLignes[ligneDebut - 1] + 1;
		offsetFin = cursor.getY() - taillesCumLignes[ligneFin - 1];
		// System.out.println("offsetdebut =" + offsetDebut + " offsetfin="
		// + offsetFin);
		if (offsetDebut < 0) {
			frag.setFirstCharOffset(1);
		} else {
			frag.setFirstCharOffset(offsetDebut);
		}

		if (offsetFin > 0) {
			frag.setLastCharOffset(offsetFin);
		}

		/*
		 * if (EditStation.buttonVar.getSelection()) {
		 * frag.setLastCharOffset(-1); } else {
		 * frag.setLastCharOffset(offsetFin); }
		 */

	}

	public static void calculRealOffsetFin(HeaderFragment frag) {
		int offsetFin;
		int enteteFichier = Integer.parseInt(EditDesc.lignesEnteteFichier
				.getText());

		// calcul de l'offset de fin après rechargement du modèle
		if (enteteFichier > 0) {
			// prise en compte de l'entête de fichier
			offsetFin = frag.getLastCharOffset()
					+ taillesCumLignes[frag.getLastLine()
							+ Integer.parseInt(EditDesc.lignesEnteteFichier
									.getText())]
					- taillesCumLignes[frag.getFirstLine()
							+ Integer.parseInt(EditDesc.lignesEnteteFichier
									.getText())];
		} else {
			// il n'y a pas d'entête de fichier
			offsetFin = frag.getLastCharOffset()
					+ taillesCumLignes[frag.getLastLine() - 1]
					- taillesCumLignes[frag.getFirstLine() - 1];
		}

		if (offsetFin > 0) {
			frag.setLastCharOffset(offsetFin);
		}
	}

	public static void calculNbreLignesPuisRemplissageFrag(HeaderFragment frag,
			int headerLines) {
		int fi, lignDepart, lignFin, i, nbrelignes, longueur;

		// on recupere les coordonnees de la selection
		fi = frag.getLastCharOffset();
		longueur = frag.getLastCharOffset();
		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = frag.getFirstLine() + headerLines;
		lignFin = lignDepart;
		// lignDepart = 1;
		// lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

		// // calcul ligne depart
		// for (i = 0; i < lignesBrutes.size(); i++) {
		//
		// if (deb == taillesCumLignes[i]) {
		// lignDepart = i + 1;
		// break;
		// }
		// if (i == (lignesBrutes.size() - 1)) {
		// lignDepart = i + 1;
		// break;
		// }
		// if ((deb >= taillesCumLignes[i]) && (deb < taillesCumLignes[i + 1]))
		// {
		// lignDepart = i + 1;
		// break;
		// }
		//
		// }

		// System.out.println("ligne depart = " + lignDepart);

		// calcul ligne fin

		for (i = lignDepart - 1; i < lignesBrutes.size(); i++) {

			if (longueur == taillesCumLignes[i]
					- taillesCumLignes[lignDepart - 1]) {
				lignFin = i + 1;
				break;
			}
			if (i == (lignesBrutes.size() - 1)) {
				lignFin = i + 1;
				break;
			}
			if ((longueur >= taillesCumLignes[i]
					- taillesCumLignes[lignDepart - 1])
					&& (longueur < taillesCumLignes[i + 1]
							- taillesCumLignes[lignDepart - 1])) {
				lignFin = i + 1;
				break;
			}

		}

		// System.out.println("ligne fin = " + lignFin);

		// nbre de lignes effectif
		nbrelignes = (lignFin - lignDepart + 1);

		if (nbrelignes == 1) {
			// frag.setSingleLine(true);
			frag.setLastLine(frag.getFirstLine());
		} else {
			frag.setSingleLine(false);
			frag.setLastLine(frag.getFirstLine() + nbrelignes - 1);

			// calcul de l'offset reel sur la derniere ligne
			int cpt = fi;
			for (int j = lignDepart - 1; j < lignFin - 1; j++) {
				cpt = cpt - Utils.lignesBrutes.get(j).length();
			}
			// en sortie de boucle, cpt a la valeur de l'offset de fin sur la
			// derniere ligne du commentaire
			// System.out.println("offset en ligne fin = " + cpt);
			// System.out.println("ligne fin = " + lignFin);
			// System.out.println("ligne debut = " + lignDepart);

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

		// on recupere les coordonnees de la selection
		deb = cursor.getX();
		fi = cursor.getY();

		// System.out.println("deb clic = " + deb);
		// System.out.println("fi clic = " + fi);

		lignDepart = frag.getFirstLine();
		lignFin = frag.getFirstLine();
		// lignDepart = 1;
		// lignFin = 1;

		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

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
		// messageErreur("Vous devez imperativement terminer la ou les etapes
		// precedentes avant de continuer." +"\n==>etapes " + tmp + " e " +
		// onglet);

		// check etape/onglet

		// e partir de onglet 0

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
			if (EditStation.validee) {
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
				if (EditStation.validee) {
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
		// e partir de onglet 1

		if ((onglet == 2) && (ViewFile.affiche == 1)) {
			ViewLog.aideStation();
			if (EditStation.validee) {
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
				if (EditStation.validee) {
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
		// e partir de onglet 2

		if ((onglet == 3) && (ViewFile.affiche == 2)) {
			ViewLog.aideData();
			if (EditData.validee == 1) {
				EditStation.razEdition();
				EditData.DrawingBack();
			} else {
				if (EditStation.validee) {
					EditStation.razEdition();

					if (EditData.loaded == 1) {
						EditData.DrawingBack();
					} else {
						EditData.affichageEdition();
					}

				} else {
					if (EditStation.loaded) {
						EditStation.razEdition();
						ViewFile.affiche = 3;
					}

				}
			}
		}

		if ((onglet == 1) && (ViewFile.affiche == 2)) {
			ViewLog.aideSurvey();
			if (EditSurvey.validee == 1) {
				if (EditStation.loaded) {
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
				if (EditStation.loaded) {
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

				if (EditStation.loaded) {
					EditStation.razEdition();
					ViewFile.affiche = 4;

				}
			}
		}

		// *********************************************************************//

		// e partir de onglet 3
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
			if (EditStation.validee) {
				if (EditData.loaded == 1) {
					EditData.razEdition();
				}
				EditStation.DrawingBack();
			} else {
				if (EditStation.loaded) {
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
			if (EditStation.validee) {
				if (EditConversion.loaded == 1) {
					EditConversion.razEdition();
				}
				EditStation.DrawingBack();
			} else {
				if (EditStation.loaded) {
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

	public static Boolean findFiles(String directoryPath, boolean raz) {
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		Arrays.sort(subfiles);

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

			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}

		if (ok) {

			ViewFile.nomFichier = directoryPath + "\\" + subfiles[0].getName(); //$NON-NLS-1$
			ViewFile.nomRepertoire = directoryPath;

			try {
				Utils.insertTextFich(ViewFile.nomFichier,
						ViewFile.textAreaDescription, raz);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return ok;

	}

	public static Boolean findFilesCollectCas1(String directoryPath, boolean raz) {
		File directory = new File(directoryPath);

		Boolean ok = true;

		File[] subfiles = directory.listFiles();

		Arrays.sort(subfiles);

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

			String mots1[] = p.split(subfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}

		File[] subsubfiles = subfiles[0].listFiles();

		Arrays.sort(subsubfiles);

		String nom2 = subsubfiles[0].getName();

		// verif sur l'extension de tous les fichiers du dossier
		mots = p.split(nom2);

		if (mots.length == 1) {
			ext = "";
		} else {
			ext = mots[1];
		}

		for (int i = 0; i < nbre; i++) {

			String mots1[] = p.split(subsubfiles[i].getName());
			String ext1;
			if (mots1.length == 1) {
				ext1 = "";
			} else {
				ext1 = mots1[1];
			}

			if (!ext.equals(ext1)) {
				ok = false;
				Utils.messageErreur(Messages.getString("Utils.27"));
			}

		}

		if (ok) {

			ViewFile.nomFichier = directoryPath
					+ "/" + subfiles[0].getName() + "/" + subsubfiles[0].getName(); //$NON-NLS-1$
			ViewFile.nomRepertoire = directoryPath;

			try {
				Utils.insertTextFich(ViewFile.nomFichier,
						ViewFile.textAreaDescription, raz);
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

		ViewLog.styledTextLogErreurs.update();
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

		ViewLog.styledTextLogErreurs.update();

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

		ViewLog.styledTextLogErreurs.update();
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

		ViewLog.styledTextLogErreurs.update();

	}

	public static void initConfig() {
		// chargement de la config
		if (configuration == null) {
			configuration = Configuration.getSingleton();
		} else {
			Configuration.releaseSingleton();
			configuration = Configuration.getSingleton();
		}
	}

	public static void saveConfig() {
		// sauvegarde de la config dans le fichier qui va bien
		// CONTACT
		configuration.setContact(""); //$NON-NLS-1$

		// DOSSIERS
		configuration.setModelsDirectory(OptionsAction.textDossierModele
				.getText());
		configuration.setCruisesDirectory(OptionsAction.textDossierCampagne
				.getText());

		// LANGUE
		if (OptionsAction.comboLangue.getItem(
				OptionsAction.comboLangue.getSelectionIndex()).equals(
				Messages.getString("OptionsAction.11"))) { //$NON-NLS-1$
			configuration.setLang("fr_FR"); //$NON-NLS-1$
		} else {
			configuration.setLang("en_GB"); //$NON-NLS-1$
		}

		// CENTRE DE DONNEES
		configuration.setDataCenter(Utils
				.extraitCode(OptionsAction.comboCentreDonnees.getText()));

		// WS
		configuration.setNautilusWebServiceURL(OptionsAction.textAreaNautilus
				.getText());
		configuration
				.setNemoWebServiceURL(OptionsAction.textAreaNemo.getText());
		configuration.setVocabURL(OptionsAction.textVocabURL.getText());

		configuration.getVocabLists().put("C77", "");
		configuration.getVocabLists().put("C174", "");
		configuration.getVocabLists().put("C180", "");
		configuration.getVocabLists().put("L05", "");
		configuration.getVocabLists().put("L021", "");
		configuration.getVocabLists().put("L031", "");
		configuration.getVocabLists().put("L051", "");
		configuration.getVocabLists().put("L061", "");
		configuration.getVocabLists().put("L071", "");
		configuration.getVocabLists().put("L081", "");
		configuration.getVocabLists().put("L201", "");
		configuration.getVocabLists().put("L300", "");
		configuration.getVocabLists().put("P012", "");
		configuration.getVocabLists().put("P061", "");
		configuration.getVocabLists().put("P091", "");
		configuration.getVocabLists().put("C16", "");
		for (Entry<String, String> currentEntry : Utils.configuration
				.getVocabLists().entrySet()) {
			configuration.setVocabListURL(currentEntry.getKey());
		}

		// CDI
        configuration.setWriteCdi(OptionsAction.checkCDI.getSelection());
		configuration.setIdEdmoRespCdi(Utils
				.extraitCode(OptionsAction.comboIdEdmoRespCdi.getText()));
		configuration.setCodeEdmoDonnees(Utils
				.extraitCode(OptionsAction.comboCodeEdmoDonnees.getText()));
		configuration.setIdEdmoRespDonnees(Utils
				.extraitCode(OptionsAction.comboIdEdmoRespDonnees.getText()));
		configuration.setIdEdmoDistDonnees(Utils
				.extraitCode(OptionsAction.comboIdEdmoDistDonnees.getText()));
		configuration.setCdiURL(OptionsAction.textUrlCdi.getText());
		configuration.setCdiMethod(Utils
				.extraitCode(OptionsAction.comboMethodeCdi.getText()));
        configuration.setAccesDonnees(Utils
            .extraitCode(OptionsAction.comboAccesDonnees.getText()));
        configuration.setTypePlateForme(Utils
            .extraitCode(OptionsAction.comboTypePlateForme.getText()));
		configuration.setFichierMapping(OptionsAction.textFichierMapping
				.getText());
		configuration.setArboMapping(OptionsAction.textArboMapping.getText());

		// Flags qualite
		// int i, j;
		// ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
		//		
		// for (i = 0; i < 4; i++) {
		// FlagTable tmpTable = new FlagTable();
		// tmpTable.setCode("C" + i);
		// tmpTable.setLibelle("L" + i);
		//			
		// ArrayList<Flag> flags = new ArrayList<Flag>();
		// for (j = 0; j < 10; j++) {
		// Flag tmpFlag = new Flag();
		// tmpFlag.setLocal("loc" + i + "" + j);
		// tmpFlag.setSdn("sdn" + i + "" + j);
		// flags.add(tmpFlag);
		// }
		// tmpTable.setFlags(flags);
		//			
		// flagTables.add(tmpTable);
		// }
		// configuration.setFlagTables(flagTables);
		configuration.setDefaultCode(OptionsAction.getDefaultCode());
		ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
		for (int i = 0; i < OptionsAction.getFlagTables().size(); i++) {
			FlagTable flagTable = new FlagTable(OptionsAction.getFlagTables()
					.get(i));
			flagTables.add(flagTable);
		}
		configuration.setFlagTables(flagTables);

		// Enregistrement de la configuration
		configuration.save();

		// System.out.println("sauvegarde options... ok");

	}

	public static void razGlobal() {
		// efface tous les onglets qui ont ete affiches jusque le en preparation
		// du chargement
		// d'un nouveau modele

		// si au moins une des sections d'un onglet est creee alors on peut les
		// effacer toutes
		if (EditDesc.sectionEnteteFichier != null) {
			// //effacement boutons
			EditDesc.razEdition();
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

		if (EditStation.dataType != null) {

			// effacement des boutons valider/expand/init serveur
			EditStation.buttonExpand.dispose();
			EditStation.buttonValidate.dispose();
			EditStation.buttonRAZ.dispose();

            for (NemoSection section : EditStation.listeSectionsStation){
                section.dispose();
            }

			// titre e vide
			ViewEdit.formEdition.setText(""); //$NON-NLS-1$

			// refresh global
			ViewEdit.formEdition.reflow(true);

			EditStation.loaded = false;
			EditStation.validee = false;
		}

		if (EditData.sectionTypeAjouter != null) {
			// effacement des boutons valider/expand/init serveur

			EditData.buttonValidate.dispose();
			EditData.buttonRAZ.dispose();

			// EditData.sectionTotal.dispose();
			for (int i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {
				EditData.listeSectionsData.get(i).dispose();
			}

			// titre e vide
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
		if (!ViewFile.tabItem1.isDisposed()) {
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
		int size = combo.getItemCount();

		if (valeur.equals("")) {
			combo.select(-1);
		} else {
			combo.select(i);

			while (i < size && !combo.getText().equalsIgnoreCase(valeur)) {
				i++;
				combo.select(i);
			}

			if (!combo.getText().equalsIgnoreCase(valeur)) {
				combo.add(valeur);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboCode(Combo combo, String code) {
		int i = 0;
		int length = code.length();
		int size = combo.getItemCount();

		if (code.equals(Global.EMPTY_STRING)) {
			combo.select(-1);
		} else {
			combo.select(i);

			while (i < size
					&& !combo.getText().substring(0,
							Math.min(combo.getText().length(), length)).equals(
							code)) {
				i++;
				combo.select(i);
			}

			if (!combo.getText().substring(0,
					Math.min(combo.getText().length(), length)).equals(code)) {
				combo.add(code);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboLibelle(Combo combo, String libelle) {
		int i = 0;
		int length = libelle.length();
		int size = combo.getItemCount();
		boolean blnFound = false;

		if (libelle.equals("")) {
			combo.select(-1);
		} else {
			combo.select(i);

			while (i < size) {
				// && !combo.getText().substring(0,
				// Math.min(combo.getText().length(), length)).equals(
				// libelle)) {
				int debutLibelle = combo.getText().length() - length;
				if (debutLibelle >= 0) {
					if (combo.getText().substring(debutLibelle)
							.equalsIgnoreCase(libelle)) {
						blnFound = true;
						break;
					}
				}
				i++;
				combo.select(i);
			}

			if (!blnFound) {
				combo.add(libelle);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static void selectComboCodePlus(Combo combo, String code,
			String valeur) {
		int i = 0;
		int length = code.length();
		int size = combo.getItemCount();

		if (code.equals(Global.EMPTY_STRING)) {
			combo.select(-1);
		} else {
			combo.select(i);

			while (i < size
					&& !combo.getText().substring(0,
							Math.min(combo.getText().length(), length)).equals(
							code)) {
				i++;
				combo.select(i);
			}

			if (!combo.getText().substring(0,
					Math.min(combo.getText().length(), length)).equals(code)) {
				combo.add(code + " - " + valeur);
				combo.select(combo.getItemCount() - 1);
			}
		}
	}

	public static String extraitCode(String valeur) {
		if (valeur.indexOf(" ") > -1) {
			return valeur.substring(0, valeur.indexOf(" "));
		} else {
			return valeur;
		}
	}

	public static String extraitCodeURN(String valeur) {
		if (valeur.lastIndexOf('/') > -1) {
			return valeur.substring(valeur.lastIndexOf('/') + 1);
		} else {
			return valeur;
		}

	}

	public static String extraitValeur(String valeur) {
		if (valeur.indexOf(" - ") > -1) {
			return valeur.substring(valeur.indexOf(" - ") + 3);
		} else {
			return "";
		}
	}

	public static String extraitFichierChemin(String chemin) {
		if (chemin.lastIndexOf('\\') > -1) {
			return chemin.substring(chemin.lastIndexOf('\\') + 1);
		} else {
			return chemin;
		}
	}

	public static String extraitCheminFichier(String chemin) {
		if (chemin.lastIndexOf('\\') > -1) {
			return chemin.substring(0, chemin.lastIndexOf('\\'));
		} else {
			return chemin;
		}
	}

	public static String concat(String list1, String list2) {
		return list1 + "_" + list2;
	}

	public static String validFileName(String value) {
		char newChar = '_';
		value = value.replace(' ', newChar);
		value = value.replace('/', newChar);
		value = value.replace('\\', newChar);
		value = value.replace(':', newChar);
		value = value.replace('*', newChar);
		value = value.replace('?', newChar);
		value = value.replace('\"', newChar);
		value = value.replace('<', newChar);
		value = value.replace('>', newChar);
		value = value.replace('|', newChar);
		return value;
	}

	public static void createSearchMenu(Combo combo, String table) {
		Menu menu = new Menu(combo.getShell(), SWT.POP_UP);

		MenuItem itemSearch = new MenuItem(menu, SWT.NONE);
		itemSearch.setText(Messages.getString("OptionsAction.27")); //$NON-NLS-1$

		combo.setMenu(menu);

		final Combo comb = combo;
		final String tab = table;

		itemSearch.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				FiltrageTableType.comboRecherche = comb;
				FiltrageTableType.go(tab);
			}
		});
	}

	public static int calculNbLigne(File file) {
		int nbLignes = 0;

		try {
			FileReader reader = new FileReader(file);
			BufferedReader readerLigne = new BufferedReader(reader);

			while (readerLigne.readLine() != null) {
				nbLignes++;
			}
			readerLigne.close();
			return nbLignes;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static FlagTable getBlankFlagTable() {
		if (_blank == null) {
			_blank = new FlagTable();
			String[] flags = Flag.flagSDN();
			for (int i = 0; i < flags.length; i++) {
				_blank.getFlags().add(
						new Flag(Utils.extraitCode(flags[i]), flags[i]));
			}
		}

		return _blank;
	}

	public static void forceMenu(Table table, Menu menu) {
		final Menu men = menu;
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				// Lors d'un clic droit sur le tableau, on force l'affichage du
				// menu contextuel
				if (event.button == 3) {
					men.setVisible(true);
				}
			}
		});
	}
}
