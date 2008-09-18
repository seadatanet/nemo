package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.util.Messages;

public class Regexp {
	/*
	 * Test des valeurs des champs de l'IHM par utilisation de regexp
	 * 
	 */

	public static void initTab() {

	}

	public static void remplirArrayList(ArrayList<String> liste, String chaine,
			int day, int month, int year, String split) {

		liste = new ArrayList<String>();

		liste.add(chaine);
		liste.add("" + day); //$NON-NLS-1$
		liste.add("" + month); //$NON-NLS-1$
		liste.add("" + year); //$NON-NLS-1$
		liste.add(split);

	}

	public static Boolean depassementHeaderStation(int ligne, String zone,
			boolean metaInData) {
		// verifie que la métadonnée injectée ne se situe pas au delà de
		// l'entete station+fichier
		// retourne true quand ca dépasse

		Boolean depasse = false;
		int enteteFichier;

		if (ViewFile.dossierCampagne) {
			// dossier
			enteteFichier = 0;
		} else {
			// fichier campagne
			enteteFichier = DataModel.singleSurvey.getHeaderLinesNumber();
		}

		if (metaInData) {
			enteteFichier++;
		}

		int ligneLimite = enteteFichier
				+ DataModel.stationModel.getNumberOfLines();

		if (ligne > ligneLimite) {
			depasse = true;
			Utils.messageErreurAppend(zone + " "
					+ Messages.getString("Regexp.19"));
		}

		return depasse;

	}

	public static Boolean checkPresenceValeurObligatoire(String value,
			String nomSection) {

		Boolean ok = true;

		if (value.matches("")) { //$NON-NLS-1$
			Utils
					.messageErreurAppend(Messages.getString("Regexp.4") + nomSection + Messages.getString("Regexp.5")); //$NON-NLS-1$ //$NON-NLS-2$
			ok = false;
		}

		return ok;
	}

	public static Boolean checkLigneDebutfin(String ligne, String debut, String fin,
			String nomSection, boolean blnCheckLigne) {

		String lign, debu, fi, log;
		Boolean ok;

		// raz du message d'erreurs

		ok = true;
		// log = "\n===[Check section : " + nomSection + "]===\n";
		log = "\nSection : " + nomSection + "\n"; //$NON-NLS-1$

		// format des différents champs
		// ligne
		if (blnCheckLigne) {
			if (Pattern.matches("^[0-9][0-9]*$", ligne)) { //$NON-NLS-1$
				// System.out.println("ligne de la section " + nomSection + "
				// ==>
				// ok");
				ok = ok && true;
				// log = log + "Valeur de ligne ok\n";
			} else {
				ok = ok && false;
				log = log + "==>" + Messages.getString("Regexp.8"); //$NON-NLS-1$

			}
		}

		// debut
		if (Pattern.matches("^[1-9][0-9]*$", debut)) { //$NON-NLS-1$
			// System.out.println("debut de la section " + nomSection + " ==>
			// ok");
			ok = ok && true;
			// log = log + "Valeur de début ok\n";
		} else {
			ok = ok && false;
			log = log + "==>" + Messages.getString("Regexp.10"); //$NON-NLS-1$

		}

		// fin
		if (Pattern.matches("^[1-9][0-9]*$", fin)) { //$NON-NLS-1$
			// System.out.println("fin de la section " + nomSection + " ==>
			// ok");
			ok = ok && true;
			// log = log + "Valeur de fin ok\n";
		} else {
			ok = ok && false;
			log = log + "==>" + Messages.getString("Regexp.12"); //$NON-NLS-1$

		}

		// tests des relations des champs

		if (!debut.matches("") && (!fin.matches(""))) { //$NON-NLS-1$ //$NON-NLS-2$
			if (Integer.parseInt(debut) > Integer.parseInt(fin)) {
				ok = ok && false;
				log = log + "==>" + Messages.getString("Regexp.15"); //$NON-NLS-1$
			}
		}

		if (ok) {
			// log = log+"...ok";
		} else {

			Utils.messageErreurAppend(log);
		}
		return ok;

	}

	public static Boolean checkTextValeur(Text text, String nomSection) {
		Boolean ok;
		String valeur, log;

		ok = true;
		// log = "\n===[Check section : " + nomSection + "]===\n";
		log = "\nSection : " + nomSection + "\n"; //$NON-NLS-1$
		valeur = text.getText();

		if (Pattern.matches("^[0-9][0-9]*$", valeur)) { //$NON-NLS-1$
			// System.out.println("valeur de la section " + nomSection + " ==>
			// ok");
			ok = ok && true;
			// log = log + "Valeur ok\n";
		} else {
			ok = ok && false;
			log = log + "==>" + Messages.getString("Regexp.18"); //$NON-NLS-1$

		}

		if (ok) {
			// log = log + "...ok";
		} else {

			Utils.messageErreurAppend(log);
		}
		return ok;
	}

	// public static Boolean checkNonVide(Text text) {
	// Boolean ok = true;
	//		
	// if (EditDesc.radManuel.getSelection()) {
	// if (EditDesc.txtManuel.getText().matches("")) {
	// ok = false;
	// Utils.messageErreurAppend(Messages.getString("EditDesc.indicateurVide"));
	// }
	// }
	//		
	// return ok;
	// }
}
