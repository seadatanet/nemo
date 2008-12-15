package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.xml.sax.SAXException;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class EditSurvey {
	// fonctions de remplissage de la zone d'édition pour les campagnes

	public static Section sectionReference, sectionLocalisation, sectionSource,
			sectionCentre, sectionType, sectionCommentaire;

	public static Text textCampagne, textNom, textDateDebut, textDateFin,
			textRegion, textPays, textLabo, textChef, textProjet,
			textConfidentiel, textType, textNbre, textCommentaire;

	public static Label labelCampagne, labelNom, labelCodeNavire,
			labelNomNavire, labelDateDebut, labelDateFin, labelRegion,
			labelPays, labelLabo, labelChef, labelProjet, labelCentre,
			labelConfidentiel, labelType, labelNbre;

	public static Button buttonRAZ, buttonInitServeur, buttonValidate,
			buttonXML, buttonExpand, buttonExpand2;

	public static ArrayList<Section> listeSectionsSurvey, listeSectionsParam;

	public static ArrayList<String> listeSectionsNoms, listeTypeDonnees;

	public static ArrayList<Label> listeSectionsLabel;

	public static ArrayList<Text> listeTextSurvey;

	public static int expand, validee, loaded;

	public static Combo comboConfid, comboDataCentres, comboCountries,
			comboNavire, comboRegion;

	public static List listeType;

	public static Boolean okApresReset;

	public static ScrolledForm formEdition;

	private static Shell shell = new Shell();

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier

		formEdition = ViewEdit.formEdition;
		ViewLog.aideSurvey();
		okApresReset = true;

		// affichage du texte dans la zone de gauche
		ViewFile.styledTextCampagne.setText(Utils.texteBrut);

		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		// de base les sections sont refermées
		expand = 0;

		if (loaded == 0) {

			// INFOS sur les fichiers utilisés
			if (ViewFile.radioFichier.getSelection()) {
				// fichier campagne
				Utils.messageInfoAppend(Messages.getString("EditSurvey.1")); //$NON-NLS-1$
			} else {
				// dossier campagne
				Utils
						.messageInfoAppend(Messages.getString("EditSurvey.2") + ViewFile.nomRepertoire); //$NON-NLS-1$
			}

			// if (ViewFile.checkBoxTempo.getSelection()) {
			if (ViewFile.radSerieTempo.getSelection()) {
				Utils.messageInfoAppend(Messages.getString("EditSurvey.3")); //$NON-NLS-1$
			}

		}
		loaded = 1;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * réafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditSurvey.4")); //$NON-NLS-1$

		disposeElements();

		// init de la arrayList des sections
		listeSectionsSurvey = new ArrayList<Section>();
		listeSectionsNoms = new ArrayList<String>();
		listeTextSurvey = new ArrayList<Text>();
		ArrayList<Text> listeText = new ArrayList<Text>();
		ArrayList<String> listeNoms = new ArrayList<String>();

		// init composants IHM
		initSections();
		initText();
		initBoutons();
		initListeType();
		initCombos();

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditSurvey.9")); //$NON-NLS-1$

		// boutons collapse all / expand all

		// bouton serveur
		buttonInitServeur.setParent(ViewEdit.formEdition.getBody());
		buttonInitServeur.setText(Messages.getString("EditSurvey.7")); //$NON-NLS-1$

		// bouton XML
		buttonXML.setParent(ViewEdit.formEdition.getBody());
		buttonXML.setText(Messages.getString("EditSurvey.8")); //$NON-NLS-1$

		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$

		buttonExpand.setParent(ViewEdit.formEdition.getBody());
		buttonExpand.setText("+"); //$NON-NLS-1$

		formEdition.redraw();
		formEdition.reflow(true);

		// formEdition.reflow(true);

		// initialisation liste des sections de la partie description fichier

		// section référence
		Drawing.sectionReferenceCampagne(sectionReference, Messages
				.getString("EditSurvey.14"), Messages
				.getString("EditSurvey.15"), textCampagne, Messages
				.getString("EditSurvey.10"), textNom, Messages
				.getString("EditSurvey.11"), comboNavire, Messages
				.getString("EditSurvey.ship"));

		// section localisation
		listeText.add(textDateDebut);
		listeNoms.add(Messages.getString("EditSurvey.16")); //$NON-NLS-1$
		listeText.add(textDateFin);
		listeNoms.add(Messages.getString("EditSurvey.17")); //$NON-NLS-1$
		// FAE 0001186 : Region en liste
		Drawing
				.sectionNlabelsNtext(
						sectionLocalisation,
						Messages.getString("EditSurvey.19"), listeText, listeNoms, Messages.getString("EditSurvey.20"), false); //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();
		listeNoms.clear();

		ViewEdit.formEdition.reflow(true);

		// section source des donnees
		// listeText.add(textPays);
		// listeNoms.add(Messages.getString("EditSurvey.21")); //$NON-NLS-1$
		listeText.add(textLabo);
		listeNoms.add(Messages.getString("EditSurvey.22")); //$NON-NLS-1$
		listeText.add(textChef);
		listeNoms.add(Messages.getString("EditSurvey.23")); //$NON-NLS-1$
		listeText.add(textProjet);
		listeNoms.add(Messages.getString("EditSurvey.24")); //$NON-NLS-1$
		Drawing
				.sectionNlabelsNtext(
						sectionSource,
						Messages.getString("EditSurvey.25"), listeText, listeNoms, Messages.getString("EditSurvey.26"), true); //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();
		listeNoms.clear();

		// section centre archivage
		// listeText.add(textCentre);
		listeNoms.add(Messages.getString("EditSurvey.27")); //$NON-NLS-1$

		Drawing
				.sectionNlabelsNtextConfidentialite(
						sectionCentre,
						Messages.getString("EditSurvey.29"), listeText, listeNoms, Messages.getString("EditSurvey.30"), comboConfid); //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();
		listeNoms.clear();
		// FI par défaut pour le centre de données
		// textCentre.setTextLimit(2);
		// textCentre.setText("FI");

		// section types de données
		listeText.add(textNbre);
		listeNoms.add(Messages.getString("EditSurvey.31")); //$NON-NLS-1$
		listeText.add(textType);
		listeNoms.add(Messages.getString("EditSurvey.32")); //$NON-NLS-1$
		Drawing
				.sectionTypeDonnees(
						sectionType,
						Messages.getString("EditSurvey.33"), listeText, listeNoms, Messages.getString("EditSurvey.34"), listeType); //$NON-NLS-1$ //$NON-NLS-2$

		// section commentaire
		Drawing
				.sectionCommentaireSurvey(
						sectionCommentaire,
						Messages.getString("EditSurvey.35"), textCommentaire, Messages.getString("EditSurvey.36")); //$NON-NLS-1$ //$NON-NLS-2$

		// buttonExpand2.setParent(ViewEdit.formEdition.getBody());
		buttonExpand2.setText("+"); //$NON-NLS-1$

		// listeners

		buttonExpand
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						int i;

						for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

							if (expand == 0) {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(true);

							} else {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(false);

							}

						}
						if (expand == 0) {

							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$
							expand = 1;
						} else {
							expand = 0;

							buttonExpand.setText("+"); //$NON-NLS-1$
							buttonExpand2.setText("+"); //$NON-NLS-1$
						}

						// refresh global
						formEdition.reflow(true);

					}
				});

		buttonExpand2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						int i;

						for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

							if (expand == 0) {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(true);

							} else {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(false);

							}

						}
						if (expand == 0) {

							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$
							expand = 1;
						} else {
							expand = 0;

							buttonExpand.setText("+"); //$NON-NLS-1$
							buttonExpand2.setText("+"); //$NON-NLS-1$
						}

						// refresh global
						formEdition.reflow(true);

					}
				});

		buttonValidate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// on passe en ppar l'étape à laquelle on se trouve
						// Etape.etape(1);

						Etape.validationSurvey();

						formEdition.redraw();
						formEdition.reflow(true);

					}
				});

		buttonRAZ
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						razText(listeTextSurvey);

					}
				});

		buttonInitServeur
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						init_webService();
					}
				});

		buttonXML
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						ouvertureDescriptionXML();
					}
				});

		textCommentaire
				.addFocusListener(new org.eclipse.swt.events.FocusListener() {
					public void focusGained(org.eclipse.swt.events.FocusEvent e) {

					}

					public void focusLost(org.eclipse.swt.events.FocusEvent e) {

						int lastIndex = 1;
						int lastIndexOfSpace = 1;
						int indexOfCR = 1;
						String tamponOrigine = textCommentaire.getText();
						String tampon80 = "";
						String tamponSortie = "";

						while (tamponOrigine.length() > 80) {

							tampon80 = tamponOrigine.substring(0, 80);

							lastIndexOfSpace = tampon80.lastIndexOf(" ");
							indexOfCR = tampon80.indexOf("\r");

							if (indexOfCR > -1) {
								lastIndex = indexOfCR;
								tamponOrigine = tamponOrigine
										.substring(lastIndex + 2);
							} else {
								lastIndex = lastIndexOfSpace;
								tamponOrigine = tamponOrigine
										.substring(lastIndex + 1);
							}

							tamponSortie += tampon80.substring(0, lastIndex)
									.trim()
									+ "\r\n";

						}

						tamponSortie += tamponOrigine;
						// System.out.println(tamponSortie);
						textCommentaire.setText(tamponSortie);
						textCommentaire.pack();

					}

				});

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// refresh du formulaire

		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 1;

		// remplissage de l'IHM par le modèle chargé
		if (DataModel.ouverture) {
			DataModel.fillSurvey();
		} else {
			Utils.selectComboCode(EditSurvey.comboDataCentres,
					Utils.configuration.getDataCenter());
		}

	}

	public static void webServiceUpdate() {

		SWTProcessWS.fichiersConstantes = true;

		new SWTProgressWS().run();

		// affichage du résultat des mises à jour des fichiers
		String message = "";

		if (NemoLists.okDataTypes) {
			message = message + Messages.getString("EditSurvey.65") + "  OK - "
					+ NemoLists.nbDataTypes + " "
					+ Messages.getString("EditSurvey.70");
		} else {
			message = message + Messages.getString("EditSurvey.65") + "  "
					+ Messages.getString("EditSurvey.71");
		}

		if (NemoLists.okParameters) {
			message = message + Messages.getString("EditSurvey.66") + "  OK - "
					+ NemoLists.nbParameters + " "
					+ Messages.getString("EditSurvey.70");
		} else {
			message = message + Messages.getString("EditSurvey.66") + "  "
					+ Messages.getString("EditSurvey.71");
		}

		if (NemoLists.okRegions) {
			message = message + Messages.getString("EditSurvey.67") + "  OK - "
					+ NemoLists.nbRegions + " "
					+ Messages.getString("EditSurvey.70");
		} else {
			message = message + Messages.getString("EditSurvey.67") + "  "
					+ Messages.getString("EditSurvey.71");
		}

		if (NemoLists.okSurveys) {
			message = message + Messages.getString("EditSurvey.69") + "  OK - "
					+ NemoLists.nbSurveys + " "
					+ Messages.getString("EditSurvey.70");
		} else {
			message = message + Messages.getString("EditSurvey.69") + "  "
					+ Messages.getString("EditSurvey.71");
		}

		message = message + Messages.getString("EditSurvey.73") + "  OK - "
				+ NemoLists.nbVocabLists + " "
				+ Messages.getString("EditSurvey.70");

		if (NemoLists.okEdmo) {
			message = message + Messages.getString("EditSurvey.74") + "  OK - "
					+ NemoLists.nbEdmo + " "
					+ Messages.getString("EditSurvey.70");
		} else {
			message = message + Messages.getString("EditSurvey.74") + "  "
					+ Messages.getString("EditSurvey.71");
		}

		MessageDialog.openInformation(shell, "WebServices Info", message);

	}

	public static void init_webService() {
		// sélection de la ref campagne dans une liste
		FiltrageTableType.go(Global.WSSURVEY);

	}

	public static void webService(String reference) {
		// remplissage du dataModel à partir des valeurs du ws

		TableDataSurvey.types.clear();

		SWTProcessWS.referenceWS = reference;
		SWTProcessWS.fichiersConstantes = false;

		if (ViewFile.dossierCampagne) {
			SWTProcessWS.dossier = true;
		} else {
			SWTProcessWS.dossier = false;
		}

		new SWTProgressWS().run();

		// mise à jour de l'onglet courant
		majApresWS();

	}

	public static void ouvertureDescriptionXML() {

		String nomXML;
		File fich;

		// remplissage du dataModel à partir des valeurs du fichier XML de
		// description

		// vérifier que un fichier est chargé avant d'ouvrir un modèle
		if ((EditDesc.validee != 1) && (EditSurvey.loaded != 1)) {
			Utils.messageErreur(Messages.getString("EditSurvey.54")); //$NON-NLS-1$
		} else {
			FileDialog dialog = new FileDialog(shell, SWT.OPEN
					| SWT.APPLICATION_MODAL);
			// dialog.setFilterPath(Utils.configuration.getModelsDirectory()+"\\");
			dialog.setText(Messages.getString("EditSurvey.55")); //$NON-NLS-1$
			String extensions[] = new String[1];
			extensions[0] = "*.xml"; //$NON-NLS-1$
			dialog.setFilterExtensions(extensions);
			nomXML = dialog.open();
			if (nomXML == null) {
			} else {

				fich = new File(nomXML);

				Utils.messageErreur("fiche:" + fich);
				EditSurvey.chargementXML(fich);

			}

		}

	}

	public static void chargementXML(File fichier) {

		// reset de la liste des dataTypes
		TableDataSurvey.types.clear();

		// Liste des capteurs
		ArrayList<String> listSensor = new ArrayList<String>();
		WSDataType[] listWSDT = NemoLists.getDataTypes();
		for (int i = 0; i < listWSDT.length; i++) {
			if (listWSDT[i].isSensor()) {
				listSensor.add(listWSDT[i].getCode());
			}
		}

		// System.out.println("");
		// for (String temp : listSensor)
		// {
		// System.out.println(temp);
		// }

		if (ViewFile.dossierCampagne) {

			try {
				DataModel.dirSurvey.releaseModel();
				Survey survey = new Survey();
				survey.importFromXML(fichier);
				survey.setDataCentre(Utils.configuration.getDataCenter());

				// Màj des types de données capteurs
				for (int index = 0; index < survey.getDataTypes().length; index++) {
					survey.dataTypesAt(index).setSensor(
							listSensor.contains(survey.dataTypesAt(index)
									.getCode().substring(0, 3)));
				}

				DataModel.dirSurvey.setModel(survey);

			} catch (FileNotFoundException e) {
				// 
				e.printStackTrace();
			} catch (SAXException e) {
				// 
				e.printStackTrace();
			} catch (TransformerException e) {
				//
				e.printStackTrace();
			}

		} else {

			try {
				DataModel.singleSurvey.releaseModel();

				Survey survey = new Survey();
				survey.importFromXML(fichier);
				survey.setDataCentre(Utils.configuration.getDataCenter());

				// Màj des types de données capteurs
				for (int index = 0; index < survey.getDataTypes().length; index++) {
					survey.dataTypesAt(index).setSensor(
							listSensor.contains(survey.dataTypesAt(index)
									.getCode().substring(0, 3)));
				}

				DataModel.singleSurvey.setModel(survey);

				DataModel.singleSurvey.getModel();
			} catch (FileNotFoundException e) {
				// 
				e.printStackTrace();
			} catch (SAXException e) {
				// 
				e.printStackTrace();
			} catch (TransformerException e) {
				// 
				e.printStackTrace();
			}

		}

		// mise à jour de l'onglet courant
		majApresWS();
	}

	public static void majApresWS() {

		Survey survey;
		if (ViewFile.dossierCampagne) {
			survey = DataModel.dirSurvey.getModel();
		} else {
			survey = DataModel.singleSurvey.getModel();
		}

		// station = stationModel.getModel();

		// injection des valeurs de l'IHM -> datamodel
		// référence
		if (survey.getReference() != null) {

			EditSurvey.textCampagne.setText(survey.getReference());

		}
		if (survey.getName() != null) {
			EditSurvey.textNom.setText(survey.getName());
		}
		if (survey.getShipCode() != null) {
			if (survey.getShipName() != null) {
				Utils.selectComboCodePlus(EditSurvey.comboNavire, survey
						.getShipCode(), survey.getShipName());
			} else {
				Utils.selectComboCode(EditSurvey.comboNavire, survey
						.getShipCode());
			}
		}

		try {
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			if (survey.getFormattedStartDate() != null) {
				EditSurvey.textDateDebut.setText(formatDate.format(new Date(
						survey.getStartDate())));
				// EditSurvey.textDateDebut.setText(survey.getFormattedStartDate());
			}
			if (survey.getFormattedEndDate() != null) {
				EditSurvey.textDateFin.setText(formatDate.format(new Date(
						survey.getEndDate())));
				// EditSurvey.textDateFin.setText(survey.getFormattedEndDate());
			}
		} catch (ParseException e) {
			// 
			e.printStackTrace();
		}

		if (survey.getRegionName() != null) {
			EditSurvey.textRegion.setText(survey.getRegionName());
		}

		// Source de données
		// if (survey.getCountryCode() != null) {
		// EditSurvey.textPays.setText(survey.getCountryCode());
		// }
		if (survey.getCountryCode() != null) {
			Utils.selectComboCode(EditSurvey.comboCountries, survey
					.getCountryCode());
		}

		if (survey.getLaboratories() != null) {
			EditSurvey.textLabo.setText(survey.getLaboratories());
		}

		if (survey.getChiefScientist() != null) {
			EditSurvey.textChef.setText(survey.getChiefScientist());
		}

		if (survey.getProjectName() != null) {
			EditSurvey.textProjet.setText(survey.getProjectName());
		}

		// Centre d'archivage
		if (survey.getDataCentre() != null) {
			// EditSurvey.textCentre.setText(survey.getDataCentre());

			// sélection dans la comboList
			Utils.selectComboCode(comboDataCentres, survey.getDataCentre());
		}

		if (survey.getAvailabilityCode() != null) {
			if (survey.getAvailabilityCode().matches("P")) {
				EditSurvey.comboConfid.select(0);
			}
			if (survey.getAvailabilityCode().matches("L")) {
				EditSurvey.comboConfid.select(1);
			}
			if (survey.getAvailabilityCode().matches("C")) {
				EditSurvey.comboConfid.select(2);
			}
		}

		// Commentaire
		if (survey.getComment() != null) {
			EditSurvey.textCommentaire.setText(survey.getComment());
		}

		// Remplissage de la liste des dataTypes de la campagne

		if (survey.getDataTypes() != null) {
			DataType[] dataTypes = survey.getDataTypes();
			int taille = dataTypes.length;

			// reset de la liste des dataTypes de l'IHM
			TableDataSurvey.types.clear();
			TableDataSurvey.types = new ArrayList<TypeSurvey>();

			for (int i = 0; i < taille; i++) {
				TypeSurvey typeSurvey = new TypeSurvey();
				typeSurvey.setCode(dataTypes[i].getCode());
				typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());
				if (dataTypes[i].isQc()) {
					typeSurvey.setQc("Y");
				} else {
					typeSurvey.setQc("N");
				}

				typeSurvey.setSensor(dataTypes[i].isSensor());

				TableDataSurvey.types.add(typeSurvey);

			}
		}

		EditSurvey.razEdition();
		EditSurvey.DrawingBack();

		// refresh de la liste des dataTypes
		TableDataSurvey.razTableData();
		TableDataSurvey.creationTable();
		Composite c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				((ScrolledForm) c).reflow(true);
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}

	}

	public static void razEdition() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		int i;

		// effacement des boutons valider/expand/init serveur
		EditSurvey.buttonExpand.setParent(shell);
		EditSurvey.buttonExpand2.setParent(shell);
		EditSurvey.buttonValidate.setParent(shell);
		EditSurvey.buttonInitServeur.setParent(shell);
		EditSurvey.buttonXML.setParent(shell);
		EditSurvey.buttonRAZ.setParent(shell);

		for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

			EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);

			EditSurvey.listeSectionsSurvey.get(i).setParent(shell);

		}

		// effacement titre formulaire
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

	}

	public static void ajoutModifyListenerText(ArrayList<Text> liste) {
		int taille = liste.size();
		for (int i = 0; i < taille; i++) {
			liste.get(i).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// on modifie la valeur du champ texte

					// dès qu'on modifie une valeur, on invalide l'étape
					// remise du nom onglet à valeur par défaut

					// on invalide toutes les étapes suivantes avec le second
					// flag okApresReset à false
					EditSurvey.okApresReset = false;
					if (!ViewFile.tabItem1.isDisposed()) {
						ViewFile.tabItem1.setText(Messages
								.getString("EditSurvey.4"));
					}
					EditStation.okApresReset = false;
					ViewFile.tabItem2.setText(Messages
							.getString("EditStation.1"));
					EditData.okApresReset = false;
					ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
					Utils.messageErreur("");

				}
			});

		}
	}

	public static void DrawingBack() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideSurvey();
		int i;
		ScrolledForm s;
		s = ViewEdit.formEdition;
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		// reaffichage des boutons valider/expand/init serveur

		EditSurvey.buttonValidate.setParent(s.getBody());
		EditSurvey.buttonInitServeur.setParent(s.getBody());
		EditSurvey.buttonXML.setParent(s.getBody());
		EditSurvey.buttonRAZ.setParent(s.getBody());
		EditSurvey.buttonExpand.setParent(s.getBody());

		for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

			EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);

			EditSurvey.listeSectionsSurvey.get(i).setParent(s.getBody());

		}

		// EditSurvey.buttonExpand2.setParent(s.getBody());

		ViewEdit.formEdition.setText(Messages.getString("EditSurvey.61")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 1;

	}

	public static void initSections() {

		sectionReference = new Section(shell, SWT.NONE);
		sectionLocalisation = new Section(shell, SWT.NONE);
		sectionSource = new Section(shell, SWT.NONE);
		sectionCentre = new Section(shell, SWT.NONE);
		sectionType = new Section(shell, SWT.NONE);
		sectionCommentaire = new Section(shell, SWT.NONE);

	}

	public static void initText() {

		textCampagne = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textCampagne);
		textNom = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textNom);
		textDateDebut = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textDateDebut);
		textDateFin = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textDateFin);
		textRegion = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textRegion);
		textLabo = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textLabo);
		textChef = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textChef);
		textProjet = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textProjet);
		textConfidentiel = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textConfidentiel);
		textType = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textType);
		textNbre = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textNbre);
		textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		listeTextSurvey.add(textCommentaire);

		// remplissage de la liste des textes et ajout listeners
		ajoutModifyListenerText(listeTextSurvey);

	}

	public static void initBoutons() {

		buttonRAZ = new Button(shell, SWT.NONE);
		buttonInitServeur = new Button(shell, SWT.NONE);
		buttonExpand = new Button(shell, SWT.NONE);
		buttonExpand2 = new Button(shell, SWT.NONE);
		buttonValidate = new Button(shell, SWT.NONE);
		buttonXML = new Button(shell, SWT.NONE);

	}

	public static void initCombos() {

		comboConfid = new Combo(shell, SWT.READ_ONLY);
		comboConfid.add("P - Public");
		comboConfid.add("L - Limited");
		comboConfid.add("C - Confidential");
		comboConfid.select(0);

		// remplissage des centres de données
		comboDataCentres = new Combo(shell, SWT.READ_ONLY);

		CodeTableRecordType[] centres = NemoLists.getVocabList("L300");

		TreeMap<String, String> treeCentre = new TreeMap<String, String>();

		for (CodeTableRecordType record : centres) {
			treeCentre.put(Utils.extraitCodeURN(record.getEntryKey()), record
					.getEntryTerm());
		}

		NemoLists.listeCentreDonnees = new ArrayList<String>();
		String temp = "";
		for (Entry<String, String> currentEntry : treeCentre.entrySet()) {
			temp = currentEntry.getKey() + " - " + currentEntry.getValue();
			NemoLists.listeCentreDonnees.add(temp);
			comboDataCentres.add(temp);
		}
		Utils.createSearchMenu(comboDataCentres, Global.DATACENTRE);

		// Remplissage liste navires
		comboNavire = new Combo(shell, SWT.READ_ONLY);
		CodeTableRecordType[] navires = NemoLists.getVocabList("C174");

		TreeMap<String, String> treeNavire = new TreeMap<String, String>();

		for (CodeTableRecordType record : navires) {
			treeNavire.put(record.getEntryTerm(), Utils.extraitCodeURN(record
					.getEntryKey()));
		}

		NemoLists.listeNavire = new ArrayList<String>();
		temp = "";
		for (Entry<String, String> currentEntry : treeNavire.entrySet()) {
			temp = currentEntry.getValue() + " - " + currentEntry.getKey();
			NemoLists.listeNavire.add(temp);
			comboNavire.add(temp);
		}
		
		// Remplissage des Pays
		comboCountries = new Combo(shell, SWT.NONE);
		CodeTableRecordType[] records = NemoLists.getVocabList("C180");
		
		TreeMap<String, String> treePays = new TreeMap<String, String>();
			
		for (CodeTableRecordType record : records){	
			treePays.put(record.getEntryTerm(), Utils.extraitCodeURN(record.getEntryKey()));
		}
		
		for (Entry<String, String> currentEntry : treePays.entrySet()) {
			comboCountries.add(currentEntry.getValue() + " - " + currentEntry.getKey());
		}
		
		// Remplissage des Regions
		comboRegion = new Combo(shell, SWT.NONE);
		CodeTableRecordType[] regions = NemoLists.getVocabList("C16");
		
		TreeMap<String, String> treeRegion = new TreeMap<String, String>();
			
		for (CodeTableRecordType record : regions)
		{	
			treeRegion.put(record.getEntryTerm(), Utils.extraitCodeURN(record.getEntryKey()));
		}

		NemoLists.listeRegion = new ArrayList<String>();
		temp = "";
		for (Entry<String, String> currentEntry : treeRegion.entrySet()) {
			temp = currentEntry.getValue() + " - " + currentEntry.getKey();
			NemoLists.listeRegion.add(temp);
			comboRegion.add(temp);
		}
	}

	public static void initListeType() {

		listeType = new List(shell, SWT.BORDER);

	}

	public static void razText(ArrayList<Text> liste) {

		for (int i = 0; i < liste.size(); i++) {
			liste.get(i).setText(""); //$NON-NLS-1$

		}

		// textCentre.setText("FI");
		validee = 0;

		// effacement de la liste des types de données dans la table
		TableDataSurvey.types = new ArrayList<TypeSurvey>();
		// razTableData
		TableDataSurvey.razTableData();

		// refresh de la table
		TableDataSurvey.creationTable();

		Composite c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				((ScrolledForm) c).reflow(true);
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}

		EditSurvey.okApresReset = false;
		ViewFile.tabItem1.setText(Messages.getString("EditSurvey.4"));
		EditStation.okApresReset = false;
		ViewFile.tabItem2.setText(Messages.getString("EditStation.1"));
		EditData.okApresReset = false;
		ViewFile.tabItem3.setText(Messages.getString("EditData.1"));

		Utils.messageErreur("");
		
		// FAE0001187 : RaZ Navire et Pays
		comboCountries.deselectAll();
		comboNavire.deselectAll();
	}

	private static void disposeElements() {

		shell.close();

		shell = new Shell();

		if (listeSectionsLabel != null) {
			for (Label element : listeSectionsLabel) {
				element.dispose();
			}
		}

		if (listeSectionsParam != null) {
			for (Section element : listeSectionsParam) {
				element.dispose();
			}
		}

		if (listeSectionsSurvey != null) {
			for (Section element : listeSectionsSurvey) {
				element.dispose();
			}
		}

		if (listeTextSurvey != null) {
			for (Text element : listeTextSurvey) {
				element.dispose();
			}
		}

		if (comboConfid != null)
			comboConfid.dispose();

		if (comboCountries != null)
			comboCountries.dispose();

		if (comboDataCentres != null)
			comboDataCentres.dispose();

		if (sectionReference != null) {
			sectionReference.dispose();
			sectionLocalisation.dispose();
			sectionSource.dispose();
			sectionCentre.dispose();
			sectionType.dispose();
			sectionCommentaire.dispose();
		}

		if (buttonRAZ != null) {
			buttonRAZ.dispose();
			buttonInitServeur.dispose();
			buttonExpand.dispose();
			buttonExpand2.dispose();
			buttonValidate.dispose();
			buttonXML.dispose();
		}

		if (listeType != null)
			listeType.dispose();

	}

}
