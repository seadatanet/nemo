package fr.ifremer.nemo.ui;

import java.text.ParseException;
import java.util.regex.Pattern;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.input.HorodatedParameterModel;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

public class Etape {

	/*
	 * Gestion des différents affichages lors de la sélection des onglets de la
	 * fenêtre principale et des différentes étapes.
	 * 
	 */
	
	public static void validationDesc() {

		
		// Fichier avec séparateur
		if (ViewFile.checkSeparateur.getSelection() && !Preformat.isPreformat()) {
			try {
				Preformat.run();
			} catch (Exception ex){
				Utils.messageErreur(Messages.getString("Etape.120"));
				ex.printStackTrace();
			}
		}
		
		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		Boolean ok, resultat;
		ok = true;
		//Table table;

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// section entete de fichier
		resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteFichier,
				Messages.getString("Etape.1")); //$NON-NLS-1$
		ok = ok && resultat;

		// section entete station
		resultat = Regexp.checkTextValeur(EditDesc.lignesEnteteStation,
				Messages.getString("Etape.2")); //$NON-NLS-1$
		ok = ok && resultat;

		// section fin de station
		resultat = Regexp.checkTextValeur(EditDesc.lignesFinStation, Messages
				.getString("Etape.3")); //$NON-NLS-1$
		ok = ok && resultat;
		
		// section fin de données	
		if (EditDesc.radManuel.getSelection()) {
			if (EditDesc.txtManuel.getText().matches("")) {
				ok = false;
				Utils.messageErreurAppend(Messages.getString("EditDesc.indicateurVide"));
			}
		}
		
//		FAE 33
//		if (!EditDesc.buttonIndicFinDonnees.getSelection()) {
//			resultat = Regexp.checkTextValeur(EditDesc.nbreLignesMesures,
//					Messages.getString("Etape.4")); //$NON-NLS-1$
//
//			ok = ok && resultat;
//		} else {
//			// format de la combolist
//			if (EditDesc.comboIndicRuptureDonnees.getText().matches("")) {
//				Utils.messageErreurAppend(Messages.getString("Etape.100"));
//				ok = false;
//			}
//
//		}

		// check des valeurs date/heure séries tempo
		if (ViewFile.checkBoxTempo.getSelection()) {
			if (!ViewFile.textTempoHeureRef.getText().matches("")) { //$NON-NLS-1$
				try {
					DateTimeFormat format = new DateTimeFormat(
							ViewFile.comboTempoHeure.getText());
					
					format.parseObject(ViewFile.textTempoHeureRef.getText());
				
				} catch (ParseException e) {

					Utils.messageErreurAppend(Messages.getString("Etape.6")); //$NON-NLS-1$
					ok = false;
				}
			} // fin tempoHeureRef ""
			if (!ViewFile.textTempoDateRef.getText().matches("")) { //$NON-NLS-1$
				try {
					DateTimeFormat format = new DateTimeFormat(
							ViewFile.comboTempoDate.getText());
					
					format.parseObject(ViewFile.textTempoDateRef.getText());
				
				} catch (ParseException e) {

					Utils.messageErreurAppend(Messages.getString("Etape.8")); //$NON-NLS-1$
					ok = false;
				}
			}

		} // fin tempo
		
		if (ok) {
			EditDesc.okApresReset = true;
			okDesc();
		} else {
			ViewFile.tabItem.setText(Messages.getString("Etape.9")); //$NON-NLS-1$
			ViewFile.etape = 0;
		}
	}

	public static void validationSurvey() {
		// test de toutes les valeurs de Survey

		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		// mise à jour de la table
		TableDataSurvey.majTypeData();

		Boolean ok, resultat;
		ok = true;

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// check valeurs obligatoires
		resultat = Regexp.checkPresenceValeurObligatoire(
				EditSurvey.textCampagne, Messages.getString("Etape.11")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(EditSurvey.textNom,
				Messages.getString("Etape.12")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				EditSurvey.textCodeNavire, Messages.getString("Etape.13")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				EditSurvey.textNomNavire, Messages.getString("Etape.14")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				EditSurvey.textDateDebut, Messages.getString("Etape.15")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(
				EditSurvey.textDateFin, Messages.getString("Etape.16")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(EditSurvey.textRegion,
				Messages.getString("Etape.17")); //$NON-NLS-1$
		ok = ok && resultat;
//		resultat = Regexp.checkPresenceValeurObligatoire(EditSurvey.textPays,
//				Messages.getString("Etape.18")); //$NON-NLS-1$
		ok = ok && resultat;
		resultat = Regexp.checkPresenceValeurObligatoire(EditSurvey.textChef,
				Messages.getString("Etape.19")); //$NON-NLS-1$
		ok = ok && resultat;

		// CHECK DATES
		if (!EditSurvey.textDateDebut.getText().matches("")) { //$NON-NLS-1$
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("DD/MM/YYYY"); //$NON-NLS-1$
				
				dateFormat.parseObject(EditSurvey.textDateDebut.getText());
						
			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.getString("Etape.24")); //$NON-NLS-1$
				ok = false;
				e.printStackTrace();
			}
		}
		if (!EditSurvey.textDateFin.getText().matches("")) { //$NON-NLS-1$
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("DD/MM/YYYY"); //$NON-NLS-1$
				
				dateFormat.parseObject(EditSurvey.textDateFin.getText());

			} catch (ParseException e) {
				Utils.messageErreurAppend(Messages.getString("Etape.27")); //$NON-NLS-1$
				ok = false;
				e.printStackTrace();
			}

		}

		if (TableDataSurvey.types.size() == 0) {
			Utils.messageErreur(Messages.getString("Etape.28")); //$NON-NLS-1$
			ok = false;
		} else {
			// vérif si pas de "nombre=0"
			int taille = TableDataSurvey.types.size();
			for (int i = 0; i < taille; i++) {
				int nbre = TableDataSurvey.types.get(i).getNombre();
				if (nbre == 0) {
					Utils.messageErreur(Messages.getString("Etape.99")); //$NON-NLS-1$
					ok = false;
				}
			}

		}

		if (ok) {
			EditSurvey.okApresReset = true;
			okSurvey();
		} else {
			if (!ViewFile.tabItem1.isDisposed()) {
				ViewFile.tabItem1.setText(Messages.getString("Etape.29")); //$NON-NLS-1$
			}
			ViewFile.etape = 1;
		}

	}

	public static void validationStation() {
		// test des valeurs de Station

		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		// System.out.println("Debut check station");

		Boolean ok, resultat1, resultat2, depasse, metaInData2;
		ok = true;
		depasse = false;
		metaInData2 = EditStation.btnMetaInData2.getSelection();

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// NUMERO STATION
		if (EditStation.buttonRefStationPos.getSelection()) {
			// position dans le fichier, vérif des valeurs
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textRefStationLigne,
					EditStation.textRefStationDebut,
					EditStation.textRefStationFin, Messages
							.getString("Etape.31")); //$NON-NLS-1$
			if (resultat1 && !metaInData2) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textRefStationLigne.getText()),
						Messages.getString("Etape.31"));

			}
			if (depasse) {
				ok = false;
			} else {
				ok = ok && resultat1;
			}

		}

		// FAE022
		if (!ViewFile.isCollection()) {
			// TYPE DE DONNEES
			if (EditStation.comboRefStationType.getSelectionIndex() == -1) {
				Utils.messageErreurAppend(Messages.getString("Etape.32")); //$NON-NLS-1$
				ok = false;
			}
		}
		// HISTORIQUE ACQUISITION
		if (EditStation.buttonHistoriqueAcqPos.getSelection()) {
			// position dans fichier, verif des valeurs
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textHistoriqueAcqLigne1,
					EditStation.textHistoriqueAcqDebut1,
					EditStation.textHistoriqueAcqFin1, Messages
							.getString("Etape.33")); //$NON-NLS-1$
			ok = ok && resultat1;

			if (resultat1) {

				int nbreLignes = Utils
						.calculNbreLignes(EditStation.cursHistorique1);

				if (nbreLignes > 1) {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.113"));
				} else {
					// nbre de ligne bon mais est-ce au bon endroit ?
					// ////////////
					int ligneFin = Integer
							.parseInt(EditStation.textHistoriqueAcqLigne1
									.getText())
							+ nbreLignes - 1;
					int enteteFichier;
					if (ViewFile.dossierCampagne) {
						// dossier
						enteteFichier = 0;
					} else {
						// fichier campagne
						enteteFichier = DataModel.singleSurvey
								.getHeaderLinesNumber();
					}
					int ligneLimite = enteteFichier
							+ DataModel.stationModel.getNumberOfLines();

					if (ligneFin > ligneLimite) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.114"));
					} else {
						ok = ok && true;
					}
					// ////////////
				}
			}

			// on permet de n'utiliser qu'une ligne sur les deux!
			if (!EditStation.textHistoriqueAcqLigne2.getText().matches("")) {
				resultat2 = Regexp.checkLigneDebutfin(
						EditStation.textHistoriqueAcqLigne2,
						EditStation.textHistoriqueAcqDebut2,
						EditStation.textHistoriqueAcqFin2, Messages
								.getString("Etape.34"));

				if (resultat2) {
					int nbreLignes = Utils
							.calculNbreLignes(EditStation.cursHistorique2);

					if (nbreLignes > 1) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.115"));
					} else {
						// nbre de ligne bon mais est-ce au bon endroit
						int ligneFin = Integer
								.parseInt(EditStation.textHistoriqueAcqLigne2
										.getText())
								+ nbreLignes - 1;
						int enteteFichier;
						if (ViewFile.dossierCampagne) {
							// dossier
							enteteFichier = 0;
						} else {
							// fichier campagne
							enteteFichier = DataModel.singleSurvey
									.getHeaderLinesNumber();
						}
						int ligneLimite = enteteFichier
								+ DataModel.stationModel.getNumberOfLines();

						if (ligneFin > ligneLimite) {
							ok = ok && false;
							Utils.messageErreurAppend(Messages
									.getString("Etape.116"));
						} else {
							ok = ok && true;
						}

					}
				}

			} else {
				resultat2 = true;
			}
			ok = ok && resultat2;

		}

		// COMMENTAIRE
		if (EditStation.buttonCommentairePos.getSelection()) {
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textCommentaireLigne,
					EditStation.textCommentaireDebut,
					EditStation.textCommentaireFin, Messages
							.getString("Etape.35")); //$NON-NLS-1$
			ok = ok && resultat1;

			if (resultat1) {
				// VERIF si le commentaire ne dépasse pas le nombre de lignes
				// d'entete station!

				int nbreLignes = Utils
						.calculNbreLignes(EditStation.cursCommentaire);
				int ligneFin = Integer
						.parseInt(EditStation.textCommentaireLigne.getText())
						+ nbreLignes - 1;
				int enteteFichier;
				if (ViewFile.dossierCampagne) {
					// dossier
					enteteFichier = 0;
				} else {
					// fichier campagne
					enteteFichier = DataModel.singleSurvey
							.getHeaderLinesNumber();
				}
				int ligneLimite = enteteFichier
						+ DataModel.stationModel.getNumberOfLines();

				if (ligneFin > ligneLimite) {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.108"));
				} else {
					ok = ok && true;
				}

			}

		}

		// ECHANTILLON SURFACE
		if (EditStation.buttonSurfacePos.getSelection()) {
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textSurfaceLigne1,
					EditStation.textSurfaceDebut1, EditStation.textSurfaceFin1,
					Messages.getString("Etape.36")); //$NON-NLS-1$
			ok = ok && resultat1;
			if (resultat1) {
				int nbreLignes = Utils
						.calculNbreLignes(EditStation.cursEchant1);

				if (nbreLignes > 1) {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.109"));
				} else {
					// nbre de ligne bon mais est-ce au bon endroit ?

					int ligneFin = Integer
							.parseInt(EditStation.textSurfaceLigne1.getText())
							+ nbreLignes - 1;
					int enteteFichier;
					if (ViewFile.dossierCampagne) {
						// dossier
						enteteFichier = 0;
					} else {
						// fichier campagne
						enteteFichier = DataModel.singleSurvey
								.getHeaderLinesNumber();
					}
					int ligneLimite = enteteFichier
							+ DataModel.stationModel.getNumberOfLines();

					if (ligneFin > ligneLimite) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.110"));
					} else {
						ok = ok && true;
					}

				}
			}

			// permettre une seule ligne sur les deux!
			if (!EditStation.textSurfaceLigne2.getText().matches("")) {
				resultat2 = Regexp.checkLigneDebutfin(
						EditStation.textSurfaceLigne2,
						EditStation.textSurfaceDebut2,
						EditStation.textSurfaceFin2, Messages
								.getString("Etape.37"));

				if (resultat2) {

					int nbreLignes = Utils
							.calculNbreLignes(EditStation.cursEchant2);

					if (nbreLignes > 1) {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.112"));
					} else {

						int ligneFin = Integer
								.parseInt(EditStation.textSurfaceLigne2
										.getText())
								+ nbreLignes - 1;
						int enteteFichier;
						if (ViewFile.dossierCampagne) {
							// dossier
							enteteFichier = 0;
						} else {
							// fichier campagne
							enteteFichier = DataModel.singleSurvey
									.getHeaderLinesNumber();
						}
						int ligneLimite = enteteFichier
								+ DataModel.stationModel.getNumberOfLines();

						if (ligneFin > ligneLimite) {
							ok = ok && false;
							Utils.messageErreurAppend(Messages
									.getString("Etape.111"));
						} else {
							ok = ok && true;
						}

					}

				}

			}

			else {
				resultat2 = true;
			}
			ok = ok && resultat2;

		}

		String ligne, string = null;

		// HEURE
		// check valeurs ligne/debut/fin

		if (EditStation.buttonHeureSaisie.getSelection()) {
			// saisie à la main
			try {
				DateTimeFormat format = new DateTimeFormat(
						EditStation.comboHeure.getText());

				format.parseObject(EditStation.textHeureSaisie.getText());
			
			} catch (ParseException e) {

				Utils
						.messageErreurAppend(Messages.getString("Etape.38") + EditStation.comboHeure.getText() + Messages.getString("Etape.39") + EditStation.textHeureSaisie.getText()); //$NON-NLS-1$ //$NON-NLS-2$

				ok = ok && false;
				e.printStackTrace();

			}

		} else {
			// position dans fichier

			resultat1 = Regexp.checkLigneDebutfin(EditStation.textHeureLigne,
					EditStation.textHeureDebut, EditStation.textHeureFin,
					Messages.getString("Etape.40")); //$NON-NLS-1$

			if (resultat1 && !(metaInData2 || ViewFile.checkBoxTempo.getSelection())) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textHeureLigne.getText()),
						Messages.getString("Etape.40"));

			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			try {

				// récupérer le texte en question et creer une nouvelle heure
				ligne = Utils.lignesBrutes.get(Integer
						.parseInt(EditStation.textHeureLigne.getText()) - 1);
				string = ligne.substring(Integer
						.parseInt(EditStation.textHeureDebut.getText()) - 1,
						Integer.parseInt(EditStation.textHeureFin.getText()));

				DateTimeFormat format = new DateTimeFormat(
						EditStation.comboHeure.getText());

				format.parseObject(string);
			} catch (ParseException e) {
				// System.out.println("erreur format Heure ");
				Utils
						.messageErreurAppend(Messages.getString("Etape.41") + EditStation.comboHeure.getText() + Messages.getString("Etape.42") + string); //$NON-NLS-1$ //$NON-NLS-2$

				ok = ok && false;
				e.printStackTrace();

			}

		}

		// DATE

		if (EditStation.buttonDateSaisie.getSelection()) {
			// saisie manuelle
			try {
				DateTimeFormat format = new DateTimeFormat(
						EditStation.comboDate.getText());
				
				format.parseObject(EditStation.textDateSaisie.getText());
			
			} catch (ParseException e) {

				Utils
						.messageErreurAppend(Messages.getString("Etape.43") + EditStation.comboDate.getText() + Messages.getString("Etape.44") + EditStation.textDateSaisie.getText()); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
		} else {
			// position dans fichier

			// check valeurs ligne/debut/fin
			resultat1 = Regexp.checkLigneDebutfin(EditStation.textDateLigne,
					EditStation.textDateDebut, EditStation.textDateFin,
					Messages.getString("Etape.45")); //$NON-NLS-1$

			if (resultat1 && !(metaInData2 || ViewFile.checkBoxTempo.getSelection())) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textDateLigne.getText()),
						Messages.getString("Etape.45"));

			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			try {

				// récupérer le texte en question et creer une nouvelle heure
				// on a ligne debut fin donc parcours lignes de texte
				ligne = Utils.lignesBrutes.get(Integer
						.parseInt(EditStation.textDateLigne.getText()) - 1);
				string = ligne.substring(Integer
						.parseInt(EditStation.textDateDebut.getText()) - 1,
						Integer.parseInt(EditStation.textDateFin.getText()));

				DateTimeFormat format = new DateTimeFormat(
						EditStation.comboDate.getText());

				format.parseObject(string);

			} catch (ParseException e) {
				// System.out.println("erreur format date ");
				Utils
						.messageErreurAppend(Messages.getString("Etape.46") + EditStation.comboDate.getText() + Messages.getString("Etape.47") + string); //$NON-NLS-1$ //$NON-NLS-2$
				ok = ok && false;
				e.printStackTrace();

			}

		}

		// PROFONDEUR

		// //$NON-NLS-1$
		if (EditStation.buttonProfondeurPos.getSelection()) {
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textProfondeurLigne,
					EditStation.textProfondeurDebut,
					EditStation.textProfondeurFin, Messages
							.getString("Etape.49")); //$NON-NLS-1$

			if (resultat1 && !metaInData2) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textProfondeurLigne.getText()),
						Messages.getString("Etape.49"));

				// check de la valeur lue dans le fichier
				ligne = Utils.lignesBrutes
						.get(Integer.parseInt(EditStation.textProfondeurLigne
								.getText()) - 1);
				string = ligne.substring(
						Integer.parseInt(EditStation.textProfondeurDebut
								.getText()) - 1, Integer
								.parseInt(EditStation.textProfondeurFin
										.getText()));
				if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", string
						.trim())) {
					ok = ok && true;

				} else {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.52")); //$NON-NLS-1$
				}
			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			if (resultat1) {

			}

		} else {
			if (!EditStation.textProfondeur.getText().matches("")
					&& !EditStation.textProfondeur.getText().matches("NaN")) {

				if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$",
						EditStation.textProfondeur.getText())) {
					ok = ok && true;

				} else {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.52")); //$NON-NLS-1$
				}
			}

		}

		// DECALAGE/TU
		if (EditStation.buttonTUPos.getSelection()) {
			resultat1 = Regexp.checkLigneDebutfin(EditStation.textTULigne,
					EditStation.textTUDebut, EditStation.textTUFin, Messages
							.getString("Etape.118")); //$NON-NLS-1$

			if (resultat1 && !metaInData2) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textTULigne.getText()), Messages
						.getString("Etape.118"));
				// check de la valeur lue dans le fichier
				ligne = Utils.lignesBrutes.get(Integer
						.parseInt(EditStation.textTULigne.getText()) - 1);
				string = ligne.substring(Integer
						.parseInt(EditStation.textTUDebut.getText()) - 1,
						Integer.parseInt(EditStation.textTUFin.getText()));
				if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", string
						.trim())) {
					ok = ok && true;

				} else {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.119")); //$NON-NLS-1$
				}
			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			if (resultat1) {

			}

		} else {
			if (!EditStation.textTU.getText().matches("")
					&& !EditStation.textTU.getText().matches("NaN")) {

				if (Pattern.matches("^[+-]?[0-9]*[0-9][.]?[0-9]*$",
						EditStation.textTU.getText())) {
					ok = ok && true;

				} else {
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.119")); //$NON-NLS-1$
				}
			}

		}

		// LATITUDE
		if (EditStation.buttonLatSaisie.getSelection()) {
			// saisie manuelle
			try {
				LatitudeFormat latitudeFormat = new LatitudeFormat(
						EditStation.comboLatitude.getText());
				latitudeFormat.setVariableLength(EditStation.buttonVarLat.getSelection());
				latitudeFormat.parseObject(EditStation.textLatitude.getText());

			} catch (ParseException e) {
				ok = false;
				Utils.messageErreurAppend(Messages.getString("Etape.53")); //$NON-NLS-1$

				e.printStackTrace();
			}
		} else {
			// position dans fichier
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textLatitudeLigne,
					EditStation.textLatitudeDebut, EditStation.textLatitudeFin,
					Messages.getString("Etape.54")); //$NON-NLS-1$
			if (resultat1 && !metaInData2) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textLatitudeLigne.getText()),
						Messages.getString("Etape.54"));

			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			// récupérer le texte en question et creer une nouvelle latitude
			// on a ligne debut fin donc parcours lignes de texte

			ligne = Utils.lignesBrutes.get(Integer
					.parseInt(EditStation.textLatitudeLigne.getText()) - 1);
			int endLine = Integer.parseInt(EditStation.textLatitudeFin.getText());
			if (ligne.length() < endLine) {
				endLine = ligne.length();
			}
			string = ligne.substring(Integer
					.parseInt(EditStation.textLatitudeDebut.getText()) - 1,
					endLine);

			try {
				LatitudeFormat latitudeFormat = new LatitudeFormat(
						EditStation.comboLatitude.getText());
				latitudeFormat.setVariableLength(EditStation.buttonVarLat.getSelection());
				latitudeFormat.parseObject(string);

			} catch (ParseException e) {
				Utils
						.messageErreurAppend(Messages.getString("Etape.55") + EditStation.comboLatitude.getText() + Messages.getString("Etape.56") + string + "<"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				e.printStackTrace();
				ok = false;
			}

			// //////////////////////////////////////////////
		}

		// LONGITUDE
		if (EditStation.buttonLongSaisie.getSelection()) {
			// saisie manuelle
			try {
				LongitudeFormat longitudeFormat = new LongitudeFormat(
						EditStation.comboLongitude.getText());
				longitudeFormat.setVariableLength(EditStation.buttonVarLong.getSelection());
				longitudeFormat
						.parseObject(EditStation.textLongitude.getText());
			} catch (ParseException e) {
				ok = false;
				Utils.messageErreurAppend(Messages.getString("Etape.58")); //$NON-NLS-1$
				e.printStackTrace();
			}
		} else {
			// position dans fichier
			resultat1 = Regexp.checkLigneDebutfin(
					EditStation.textLongitudeLigne,
					EditStation.textLongitudeDebut,
					EditStation.textLongitudeFin, Messages
							.getString("Etape.59")); //$NON-NLS-1$
			if (resultat1 && !metaInData2) {
				depasse = Regexp.depassementHeaderStation(Integer
						.parseInt(EditStation.textLongitudeLigne.getText()),
						Messages.getString("Etape.59"));

			}

			if (depasse) {
				ok = false;
			} else {

				ok = ok && resultat1;
			}

			ligne = Utils.lignesBrutes.get(Integer
					.parseInt(EditStation.textLongitudeLigne.getText()) - 1);
			int endLine = Integer.parseInt(EditStation.textLongitudeFin.getText());
			if (ligne.length() < endLine) {
				endLine = ligne.length();
			}
			string = ligne.substring(Integer
					.parseInt(EditStation.textLongitudeDebut.getText()) - 1,
					endLine);

			try {
				LongitudeFormat longitudeFormat = new LongitudeFormat(
						EditStation.comboLongitude.getText());
				longitudeFormat.setVariableLength(EditStation.buttonVarLong.getSelection());
				longitudeFormat.parseObject(string);

			} catch (ParseException e) {
				Utils
						.messageErreurAppend(Messages.getString("Etape.60") + string + Messages.getString("Etape.61") + EditStation.comboLongitude.getText() + "<"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				// System.out.println("Erreur de format de longitude");
				e.printStackTrace();
				ok = false;
			}

		}

		// SERIE TEMPO
		if (ViewFile.checkBoxTempo.getSelection()) {

			// LATITUDE DE FIN
			if (EditStation.buttonLatFinSaisie.getSelection()) {
				// saisie manuelle
				if (EditStation.textLatFin.getText().matches("")) { //$NON-NLS-1$
					// valeur non rentrée
					ok = ok && true;
				} else {
					if (!EditStation.textLatFin.getText().matches("")) {//$NON-NLS-1$
						try {
							LatitudeFormat latitudeFormat = new LatitudeFormat(
									EditStation.comboLatitudeFin.getText());
							latitudeFormat.setVariableLength(EditStation.buttonVarLatFin.getSelection());
							latitudeFormat.parseObject(EditStation.textLatFin
									.getText());
						} catch (ParseException e) {

							Utils.messageErreurAppend(Messages
									.getString("Etape.64")); //$NON-NLS-1$
							e.printStackTrace();
						}
					}
				}
			} else {
				// position dans fichier
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textLatFinLigne,
						EditStation.textLatFinDebut, EditStation.textLatFinFin,
						Messages.getString("Etape.65")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp.depassementHeaderStation(Integer
							.parseInt(EditStation.textLatFinLigne.getText()),
							Messages.getString("Etape.65"));

				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
			}

			// LONGITUDE DE FIN
			if (EditStation.buttonLongFinSaisie.getSelection()) {
				// saisie manuelle
				if (EditStation.textLongFin.getText().matches("")) { //$NON-NLS-1$
					// valeur non rentrée
					ok = ok && true;
				} else {
					if (!EditStation.textLongFin.getText().matches("")) {
						try {
							LongitudeFormat longitudeFormat = new LongitudeFormat(
									EditStation.comboLongitudeFin.getText());
							longitudeFormat.setVariableLength(EditStation.buttonVarLongFin.getSelection());
							longitudeFormat.parseObject(EditStation.textLongFin
									.getText());
						} catch (ParseException e) {
							ok = false;
							Utils.messageErreurAppend(Messages
									.getString("Etape.67")); //$NON-NLS-1$
							e.printStackTrace();
						}
					}
				}
			} else {
				// position dans fichier
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textLongFinLigne,
						EditStation.textLongFinDebut,
						EditStation.textLongFinFin, Messages
								.getString("Etape.68")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp.depassementHeaderStation(Integer
							.parseInt(EditStation.textLongFinLigne.getText()),
							Messages.getString("Etape.68"));

				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
			}

			// PROFONDEUR DE FIN

			if (EditStation.buttonProfFinPos.getSelection()) {
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textProfFinLigne,
						EditStation.textProfFinDebut,
						EditStation.textProfFinFin, Messages
								.getString("Etape.69")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp.depassementHeaderStation(Integer
							.parseInt(EditStation.textProfFinLigne.getText()),
							Messages.getString("Etape.69"));
					// check de la valeur lue dans le fichier
					ligne = Utils.lignesBrutes
							.get(Integer.parseInt(EditStation.textProfFinLigne
									.getText()) - 1);
					string = ligne.substring(
							Integer.parseInt(EditStation.textProfFinDebut
									.getText()) - 1, Integer
									.parseInt(EditStation.textProfFinFin
											.getText()));
					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", string
							.trim())) {
						ok = ok && true;
						// Utils.messageErreurAppend("...ok"); //$NON-NLS-1$
					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.69")); //$NON-NLS-1$
					}
				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
				if (resultat1) {
					// Utils.messageErreurAppend("...ok"); //$NON-NLS-1$
				}

			} else {
				if (!EditStation.textProfFin.getText().matches("")
						&& !EditStation.textProfFin.getText().matches("NaN")) { //$NON-NLS-1$

					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*$",
							EditStation.textProfFin.getText())) {
						ok = ok && true;

					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.70")); //$NON-NLS-1$
					}
				} else {
					// valeur non rentrée
					ok = ok && true;
				}
			}

			// PROFONDEUR DU CAPTEUR

			if (EditStation.buttonProfCapteurPos.getSelection()) {
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textProfCapteurLigne,
						EditStation.textProfCapteurDebut,
						EditStation.textProfCapteurFin, Messages
								.getString("Etape.75")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp
							.depassementHeaderStation(Integer
									.parseInt(EditStation.textProfCapteurLigne
											.getText()), Messages
									.getString("Etape.75"));
					// check de la valeur lue dans le fichier
					ligne = Utils.lignesBrutes.get(Integer
							.parseInt(EditStation.textProfCapteurLigne
									.getText()) - 1);
					string = ligne
							.substring(Integer
									.parseInt(EditStation.textProfCapteurDebut
											.getText()) - 1, Integer
									.parseInt(EditStation.textProfCapteurFin
											.getText()));
					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*$", string
							.trim())) {
						ok = ok && true;

					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.75")); //$NON-NLS-1$
					}
				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
				if (resultat1) {

				}

			} else {
				Utils.messageErreurAppend(Messages.getString("Etape.77")); //$NON-NLS-1$
				if (EditStation.textProfCapteur.getText().matches("")) { //$NON-NLS-1$
					Utils.messageErreurAppend(Messages.getString("Etape.79")); //$NON-NLS-1$
				} else {
					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$",
							EditStation.textProfCapteur.getText())) {
						ok = ok && true;

					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.81")); //$NON-NLS-1$
					}
				}
			}

			// DECLINAISON MAGNETIQUE
			if (EditStation.buttonDeclinMagnetPos.getSelection()) {
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textDeclinMagnetLigne,
						EditStation.textDeclinMagnetDebut,
						EditStation.textDeclinMagnetFin, Messages
								.getString("Etape.82")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp
							.depassementHeaderStation(Integer
									.parseInt(EditStation.textDeclinMagnetLigne
											.getText()), Messages
									.getString("Etape.82"));
					// check de la valeur lue dans le fichier
					ligne = Utils.lignesBrutes.get(Integer
							.parseInt(EditStation.textDeclinMagnetLigne
									.getText()) - 1);
					string = ligne.substring(Integer
							.parseInt(EditStation.textDeclinMagnetDebut
									.getText()) - 1,
							Integer.parseInt(EditStation.textDeclinMagnetFin
									.getText()));
					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", string
							.trim())) {
						ok = ok && true;

					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.82")); //$NON-NLS-1$
					}
				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
				if (resultat1) {

				}

			} else {
				if (!EditStation.textDeclinMagnet.getText().matches("")) {
					// vérif de ce qui est entré par l'utilisateur
					if (Pattern.matches(
							"^[+-]{0,1}[0-9]*[0-9][.]?[0-9]*[0-9]*$",
							EditStation.textDeclinMagnet.getText())) {
						// ok
						ok = ok && true;

					} else {
						// erreur
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.102")); //$NON-NLS-1$
					}

				} else {

					ok = ok && true;
				}

			}

			// FREQUENCE ECHANTILLONNAGE --> voir heure !
			// que si date n'est pas dans fichier en entrée
			if (EditStation.buttonFreqEchantPos.getSelection()) {
				// position dans le fichier
				resultat1 = Regexp.checkLigneDebutfin(
						EditStation.textFreqEchantLigne,
						EditStation.textFreqEchantDebut,
						EditStation.textFreqEchantFin, Messages
								.getString("Etape.104")); //$NON-NLS-1$
				if (resultat1 && !metaInData2) {
					depasse = Regexp.depassementHeaderStation(
							Integer.parseInt(EditStation.textFreqEchantLigne
									.getText()), Messages
									.getString("Etape.104"));
					// check de la valeur lue dans le fichier
					ligne = Utils.lignesBrutes
							.get(Integer
									.parseInt(EditStation.textFreqEchantLigne
											.getText()) - 1);
					string = ligne.substring(
							Integer.parseInt(EditStation.textFreqEchantDebut
									.getText()) - 1, Integer
									.parseInt(EditStation.textFreqEchantFin
											.getText()));
					if (Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$", string
							.trim())) {
						ok = ok && true;
						// Utils.messageErreurAppend("...ok"); //$NON-NLS-1$
					} else {
						ok = ok && false;
						Utils.messageErreurAppend(Messages
								.getString("Etape.104")); //$NON-NLS-1$
					}
				}

				if (depasse) {
					ok = false;
				} else {

					ok = ok && resultat1;
				}
				if (resultat1) {
					// Utils.messageErreurAppend("...ok"); //$NON-NLS-1$
				}

			} else {
				// saisie manuelle
				if (	//FAE 066 / 064
						//Pattern.matches("^[0-9]*[0-9][.]?[0-9][0-9]*$",
					Pattern.matches("^[0-9]*[0-9][.]?[0-9]*[0-9]*$",
						EditStation.textFreqEchant.getText())) {
					// ok
					ok = ok && true;

				} else {
					// erreur
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.103")); //$NON-NLS-1$
				}

			}

		} // fin série tempo

		if (ok) {
			EditStation.okApresReset = true;
			okStation();
		} else {
			ViewFile.tabItem2.setText(Messages.getString("Etape.85")); //$NON-NLS-1$
			ViewFile.etape = 2;
		}

		// okStation();
	}

	public static void validationData() {
		// test de toutes les valeurs debut / fin des Data
		Boolean ok = true;
		Boolean resultat, valeurNonRemplie = false;
		// retour à l'onglet message d'erreur
		ViewLog.tabFolderLog.setSelection(0);

		// mise à jour avant toute chose
		TableData.majTypeData();

		int nbreParam = TableData.types.size();
		int[] debuts, fins;
		int cptDebut = 0;
		int cptFin = 0;

		Utils.messageErreur(""); //$NON-NLS-1$

		if (nbreParam == 0) {
			ok = false;
			Utils.messageErreur(Messages.getString("Etape.117"));
		} else {

			debuts = new int[nbreParam];
			fins = new int[nbreParam];

			// remplissage des listes
			for (int i = 0; i < nbreParam; i++) {
				TypeData typeCourant = TableData.types.get(i);
				debuts[i] = typeCourant.getDebut();
				fins[i] = typeCourant.getFin();

				/*
				 * // test direct sur valeur entree if
				 * (Pattern.matches("^.{0,1}[0-9]*[0-9].*[0-9][0-9]*$",
				 * typeCourant.getEntree())) { // ok ok = ok && true; } else { //
				 * erreur ok = ok && false;
				 * Utils.messageErreurAppend(Messages.getString("Etape.105"));
				 * //$NON-NLS-1$ }
				 */

				// test direct sur valeur entree
				// ok
				ok = ok && true;

				// test direct sur valeur sortie
				if (Pattern.matches("^.{0,1}[0-9]*[0-9].*[0-9][0-9]*$",
						typeCourant.getSortie())) {
					// ok
					ok = ok && true;

				} else {
					// erreur
					ok = ok && false;
					Utils.messageErreurAppend(Messages.getString("Etape.106")); //$NON-NLS-1$
				}

				// check des valeurs début<fin
				if (typeCourant.getDebut() > typeCourant.getFin()) {
					Utils
							.messageErreurAppend(Messages.getString("Etape.98") + " " + typeCourant.getCode()); //$NON-NLS-1$
					ok = false;
				}
			}

			// parcours des listes et test des valeurs
			for (int i = 0; i < nbreParam; i++) {
				for (int j = 0; j < nbreParam; j++) {
					// System.out.println(debuts[i]+"<=>"+debuts[j] + "###"+
					// fins[i] +"<=>"+ fins[j]);
					if (debuts[i] == debuts[j]) {
						if (debuts[i] == 0) {
							valeurNonRemplie = true;
						}
						cptDebut = cptDebut + 1;
					}
					if (fins[i] == fins[j]) {
						cptFin = cptFin + 1;
					}
				}
			}

			if (valeurNonRemplie) {
				Utils.messageErreurAppend(Messages.getString("Etape.88")); //$NON-NLS-1$
				ok = false;
			}

			// SERIE TEMPO
			if ((ViewFile.checkBoxTempo.getSelection())
					&& (EditData.buttonLigneDonnees.getSelection())) {
				// check valeurs série tempo

				resultat = Regexp.checkLigneDebutfin(EditData.textTotalLigne,
						EditData.textTotalDebut, EditData.textTotalFin,
						Messages.getString("Etape.89")); //$NON-NLS-1$
				ok = ok && resultat;

				try {
					DateTimeFormat format = new DateTimeFormat(
							EditData.comboHeure.getText());
					HorodatedParameterModel horo = new HorodatedParameterModel();
					horo.setFormat(format);
				} catch (ParseException e) {
					// System.out.println("Erreur format date+heure");
					e.printStackTrace();
				}

			}

			if (!ViewFile.checkBoxTempo.getSelection()) {
				// TEST pour savoir si PRES est bien en premier mais pas pour
				// séries tempo
				TypeData premierType = TableData.types.get(0);

				// split sur " - "
				Pattern p = Pattern.compile(" - ");
				String[] mots = p.split(premierType.getCode());
				String code = mots[0];

				if (!code.matches("PRES")) {
					// PRES n'est pas en premier
					Utils.messageErreurAppend(Messages.getString("Etape.107"));
					ok = ok && false;
				} else {
					ok = ok && true;
				}

			}

		}
		if (ok) {
			EditData.okApresReset = true;
			okData();
		}
	}

	private static void okDesc() {
		// envoi des valeurs dans le modèle
		// attention aux boutons radio

		// puis remplissage du modèle avec les valeurs de l'IHM
		DataModel.initModele(ViewFile.dossierCampagne, ViewFile.checkBoxTempo
				.getSelection(), ViewFile.nomFichier);

		Utils.messageOK(Messages.getString("Etape.90")); //$NON-NLS-1$

		// on rajoute "ok" dans l'onglet description
		ViewFile.tabItem.setText(Messages.getString("Etape.91")); //$NON-NLS-1$

		// on valide l'étape 1
		EditDesc.validee = 1;

		try {
			// FAE 022
			if (ViewFile.isCollection()) {
				EditSurvey.okApresReset = true;
				EditSurvey.validee = 1;
				
				DataModel.initSurvey();
				ViewFile.etape = 2;
			} else {
				ViewFile.etape = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// System.out.println("Etape.okDesc()");

	}

	private static void okSurvey() {

		DataModel.initSurvey();
		if (!ViewFile.tabItem1.isDisposed()) {
			ViewFile.tabItem1.setText(Messages.getString("Etape.92")); //$NON-NLS-1$
		}
		Utils.messageOK(Messages.getString("Etape.93")); //$NON-NLS-1$

		// validation etape 2
		ViewFile.etape = 2;
		// envoi des valeurs dans le modèle

	}

	private static void okStation() {

		// envoi des valeurs dans le modèle
		Utils.messageOK(Messages.getString("Etape.94")); //$NON-NLS-1$
		DataModel.initStation();
		ViewFile.tabItem2.setText(Messages.getString("Etape.95")); //$NON-NLS-1$
		ViewFile.etape = 3;
	}

	private static void okData() {
		Utils.messageOK(Messages.getString("Etape.96")); //$NON-NLS-1$
		DataModel.initData();
		ViewFile.tabItem3.setText(Messages.getString("Etape.97")); //$NON-NLS-1$
		ViewFile.etape = 4;
		// envoi des valeurs dans le modèle

	}

}
