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
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class EditSurvey {
	// fonctions de remplissage de la zone d'edition pour les campagnes

	public static Section sectionReference, sectionLocalisation, sectionSource,
			sectionCentre, sectionType, sectionCommentaire;

	public static Text textCampagne, textNom, textDateDebut, textDateFin,
			textPays, textLabo, textChef, textProjet,
			textConfidentiel, textCommentaire;

	public static Label labelCampagne, labelNom, labelCodeNavire,
			labelNomNavire, labelDateDebut, labelDateFin, labelRegion,
			labelPays, labelLabo, labelChef, labelProjet, labelCentre,
			labelConfidentiel, labelType, labelNbre;

	public static Button buttonRAZ, buttonInitServeur, buttonValidate,
			buttonXML, buttonExpand;

	public static ArrayList<Section> listeSectionsSurvey, listeSectionsParam;

	public static ArrayList<String> listeSectionsNoms, listeTypeDonnees;

	public static ArrayList<Label> listeSectionsLabel;

	public static ArrayList<Text> listeTextSurvey;

    public static boolean expand, validee, loaded;

	public static Combo comboConfid, comboDataCentres, comboCountries,
			comboNavire, comboRegion;

	public static List listeType;

	public static Boolean okApresReset;

	public static ScrolledForm formEdition;

	private static Shell shell = new Shell();

	public static void affichageEdition() {
		// initialisation zone edition pour etape description de fichier

		formEdition = ViewEdit.formEdition;
		ViewLog.aideSurvey();
		okApresReset = true;

		// affichage du texte dans la zone de gauche
		ViewFile.styledTextCampagne.setText(Utils.texteBrut);

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING); 

		// de base les sections sont refermees
		expand = false;

		if (!loaded) {

			// INFOS sur les fichiers utilises
			if (ViewFile.radioFichier.getSelection()) {
				// fichier campagne
				Utils.messageInfoAppend(Messages.view_file_cruiseFile);
			} else {
				// dossier campagne
				Utils.messageInfoAppend(Messages.view_file_cruiseDir + ViewFile.nomRepertoire);
			}

			if (ViewFile.radSerieTempo.getSelection()) {
                // Série temporelle
				Utils.messageInfoAppend(Messages.typeFile_timeseries);
			}else if (ViewFile.radTrajectoire.getSelection()){
                // Trajectoire
                Utils.messageInfoAppend(Messages.typeFile_trajectory);
            }

		}
		loaded = true;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * reafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.edit_cruise_title);

        shell.close();
        shell = new Shell();
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
		buttonValidate.setText(Messages.button_validateStep);

		// boutons collapse all / expand all

		// bouton serveur
		buttonInitServeur.setParent(ViewEdit.formEdition.getBody());
		buttonInitServeur.setText(Messages.button_serverIni);

		// bouton XML
		buttonXML.setParent(ViewEdit.formEdition.getBody());
		buttonXML.setText(Messages.button_xmlIni); 

		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText(Messages.button_reset);

		buttonExpand.setParent(ViewEdit.formEdition.getBody());
		buttonExpand.setText(Global.PLUS); 

		formEdition.redraw();
		formEdition.reflow(true);


		// initialisation liste des sections de la partie description fichier

		// section reference
		Drawing.sectionReferenceCampagne(sectionReference, 
                                         Messages.edit_cruise_sectionReference, 
                                         Messages.edit_cruise_sectionReference_lib, 
                                         textCampagne, Messages.edit_cruise_cruiseRef, 
                                         textNom, Messages.edit_cruise_cruiseName, 
                                         comboNavire, Messages.edit_cruise_ship); 

		// section localisation
		listeText.add(textDateDebut);
		listeNoms.add(Messages.edit_cruise_startDate); 
		listeText.add(textDateFin);
		listeNoms.add(Messages.edit_cruise_endDate);
		// FAE 0001186 : Region en liste
		Drawing.sectionNlabelsNtext(sectionLocalisation, Messages.edit_cruise_sectionLocation, 
                                    listeText, listeNoms, 
                                    Messages.edit_cruise_sectionLocation_lib, false);
		listeText.clear();
		listeNoms.clear();

		ViewEdit.formEdition.reflow(true);

        
		// section source des donnees
		listeText.add(textLabo);
		listeNoms.add(Messages.edit_cruise_lab);
		listeText.add(textChef);
		listeNoms.add(Messages.edit_cruise_chief); 
		listeText.add(textProjet);
		listeNoms.add(Messages.edit_cruise_project); 
		Drawing.sectionNlabelsNtext(sectionSource, Messages.edit_cruise_sectionDataSource, 
                                    listeText, listeNoms, 
                                    Messages.edit_cruise_sectionDataSource_lib, true);
		listeText.clear();
		listeNoms.clear();

        
		// section centre archivage
		Drawing.sectionNlabelsNtextConfidentialite(sectionCentre, Messages.edit_cruise_sectionArchiving, 
                                                   Messages.edit_cruise_sectionArchiving_lib, comboConfid); 

        
		// section types de donnees
		Drawing.sectionTypeDonnees(sectionType, Messages.edit_cruise_sectionDataTypes, 
                                   Messages.edit_cruise_sectionDataTypes_lib, listeType); 

        
		// section commentaire
		Drawing.sectionCommentaireSurvey(sectionCommentaire, Messages.edit_cruise_sectionComments, 
                                         textCommentaire, Messages.edit_cruise_sectionComments_lib); 

		// listeners
		buttonExpand
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						int i;

						for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

							if (!expand) {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(true);

							} else {
								EditSurvey.listeSectionsSurvey.get(i)
										.setExpanded(false);

							}

						}
						if (!expand) {

							buttonExpand.setText(Global.MOINS); 
							expand = true;
						} else {
							expand = false;

							buttonExpand.setText(Global.PLUS);
						}

						// refresh global
						formEdition.reflow(true);

					}
				});

		buttonValidate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// on passe en ppar l'etape e laquelle on se trouve
						// Etape.etape(1);
                        
    					Etape.validationSurvey();
    					formEdition.redraw();
    					formEdition.reflow(true);

					}
				});

		buttonRAZ
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						razText(listeTextSurvey);

					}
				});

		buttonInitServeur
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						init_webService();
					}
				});

		buttonXML
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
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
						String tampon80 = Global.EMPTY_STRING;
						String tamponSortie = Global.EMPTY_STRING;

						while (tamponOrigine.length() > 80) {

							tampon80 = tamponOrigine.substring(0, 80);

							lastIndexOfSpace = tampon80.lastIndexOf(Global.SPACE);
							indexOfCR = tampon80.indexOf("\r"); //$NON-NLS-1$

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
									+ Global.RN;

						}

						tamponSortie += tamponOrigine;
						textCommentaire.setText(tamponSortie);
						textCommentaire.pack();

					}

				});

		// refresh du formulaire
		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 1;

		// remplissage de l'IHM par le modele charge
        if (DataModel.ouverture) {
			DataModel.fillSurvey();
		} else {
			Utils.selectComboCode(EditSurvey.comboDataCentres,
					Utils.configuration.getDataCenter());
		}

	}

	public static void init_webService() {
		// selection de la ref campagne dans une liste
		FiltrageTableType.go(Global.WSSURVEY);

	}

	public static void webService(String reference) {
		// remplissage du dataModel e partir des valeurs du ws

		TableDataSurvey.types.clear();

		SWTProcessWS.referenceWS = reference;
		SWTProcessWS.fichiersConstantes = false;

		if (ViewFile.dossierCampagne) {
			SWTProcessWS.dossier = true;
		} else {
			SWTProcessWS.dossier = false;
		}

		new SWTProgressWS().run();

		// mise e jour de l'onglet courant
		majApresWS();

	}

	public static void ouvertureDescriptionXML() {

		String nomXML;
		File fich;

		// remplissage du dataModel e partir des valeurs du fichier XML de
		// description

		// verifier que un fichier est charge avant d'ouvrir un modele
		if ((!EditDesc.validee) && (!loaded)) {
			Utils.messageErreurAppend(Messages.error_cruise_openXml);
		} else {
			FileDialog dialog = new FileDialog(shell, SWT.OPEN
					| SWT.APPLICATION_MODAL);
			dialog.setText(Messages.edit_cruise_openXML); 
			String extensions[] = new String[1];
			extensions[0] = "*.xml"; //$NON-NLS-1$
			dialog.setFilterExtensions(extensions);
			nomXML = dialog.open();
			if (nomXML == null) {
			} else {

				fich = new File(nomXML);

				Utils.messageErreurAppend("fiche:" + fich);//$NON-NLS-1$
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

		if (ViewFile.dossierCampagne) {

			try {
				DataModel.dirSurvey.releaseModel();
				Survey survey = new Survey();
				survey.importFromXML(fichier);
				survey.setDataCentre(Utils.configuration.getDataCenter());

				// Mej des types de donnees capteurs
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

				// Mej des types de donnees capteurs
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

		// mise e jour de l'onglet courant
		majApresWS();
	}

	public static void majApresWS() {

		Survey survey;
		if (ViewFile.dossierCampagne) {
			survey = DataModel.dirSurvey.getModel();
		} else {
			survey = DataModel.singleSurvey.getModel();
		}

		// injection des valeurs de l'IHM -> datamodel  référence
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
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
			if (survey.getFormattedStartDate() != null) {
				EditSurvey.textDateDebut.setText(formatDate.format(new Date(
						survey.getStartDate())));
			}
			if (survey.getFormattedEndDate() != null) {
				EditSurvey.textDateFin.setText(formatDate.format(new Date(
						survey.getEndDate())));
			}
		} catch (ParseException e) {
			// 
			e.printStackTrace();
		}

		if (survey.getRegionName() != null) {
			Utils.selectComboLibelle(comboRegion, survey.getRegionName());
		}

        if (survey.getCountryCode() != null) {
            Utils.selectComboCode(comboCountries, survey.getCountryCode());
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
			// selection dans la comboList
			Utils.selectComboCode(comboDataCentres, survey.getDataCentre());
		}

		if (survey.getAvailabilityCode() != null) {
			if (survey.getAvailabilityCode().equals("P")) { //$NON-NLS-1$
				EditSurvey.comboConfid.select(0);
			}
			if (survey.getAvailabilityCode().equals("L")) { //$NON-NLS-1$
				EditSurvey.comboConfid.select(1);
			}
			if (survey.getAvailabilityCode().equals("C")) { //$NON-NLS-1$
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
                typeSurvey.setQc(Formatter.getSingleton().formatBoolean(dataTypes[i].isQc()));

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
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		int i;

		// effacement des boutons valider/expand/init serveur
		EditSurvey.buttonExpand.setParent(shell);
		EditSurvey.buttonValidate.setParent(shell);
		EditSurvey.buttonInitServeur.setParent(shell);
		EditSurvey.buttonXML.setParent(shell);
		EditSurvey.buttonRAZ.setParent(shell);

		for (i = 0; i <= EditSurvey.listeSectionsSurvey.size() - 1; i++) {

			EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);

			EditSurvey.listeSectionsSurvey.get(i).setParent(shell);

		}

		// effacement titre formulaire
		ViewEdit.formEdition.setText(Global.EMPTY_STRING); 

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void ajoutModifyListenerText(ArrayList<Text> liste) {
		int taille = liste.size();
		for (int i = 0; i < taille; i++) {
			liste.get(i).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// on modifie la valeur du champ texte

                    // des qu'on modifie une valeur,
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();

				}
			});

		}
	}

	public static void DrawingBack() {
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideSurvey();
		int i;
		ScrolledForm s;
		s = ViewEdit.formEdition;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING); 

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

		ViewEdit.formEdition.setText(Messages.edit_cruise_title); 

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
		textLabo = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textLabo);
		textChef = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textChef);
		textProjet = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textProjet);
		textConfidentiel = new Text(shell, SWT.BORDER);
		listeTextSurvey.add(textConfidentiel);
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
		buttonValidate = new Button(shell, SWT.NONE);
		buttonXML = new Button(shell, SWT.NONE);

	}

	public static void initCombos() {

		comboConfid = new Combo(shell, SWT.READ_ONLY);
		comboConfid.add("P - Public"); //$NON-NLS-1$
		comboConfid.add("L - Limited"); //$NON-NLS-1$
		comboConfid.add("C - Confidential"); //$NON-NLS-1$
		comboConfid.select(0);

		// remplissage des centres de donnees
		comboDataCentres = new Combo(shell, SWT.READ_ONLY);

		CodeTableRecordType[] centres = NemoLists.getVocabList(Intern.list_dataCenter); 

		TreeMap<String, String> treeCentre = new TreeMap<String, String>();

		for (CodeTableRecordType record : centres) {
			treeCentre.put(Utils.extraitCodeURN(record.getEntryKey()), record
					.getEntryTerm());
		}

		NemoLists.listeCentreDonnees = new ArrayList<String>();
		String temp = Global.EMPTY_STRING;
		for (Entry<String, String> currentEntry : treeCentre.entrySet()) {
			temp = currentEntry.getKey() + Global.SEP + currentEntry.getValue(); 
			NemoLists.listeCentreDonnees.add(temp);
			comboDataCentres.add(temp);
		}
		Utils.createSearchMenu(comboDataCentres, Global.DATACENTRE);

		// Remplissage liste navires
		comboNavire = new Combo(shell, SWT.READ_ONLY);
		CodeTableRecordType[] navires = NemoLists.getVocabList(Intern.list_ship);

		TreeMap<String, String> treeNavire = new TreeMap<String, String>();

		for (CodeTableRecordType record : navires) {
			treeNavire.put(record.getEntryTerm(), Utils.extraitCodeURN(record
					.getEntryKey()));
		}

		NemoLists.listeNavire = new ArrayList<String>();
		temp = Global.EMPTY_STRING;
		for (Entry<String, String> currentEntry : treeNavire.entrySet()) {
			temp = currentEntry.getValue() + Global.SEP + currentEntry.getKey();
			NemoLists.listeNavire.add(temp);
			comboNavire.add(temp);
		}
		
		// Remplissage des Pays
		comboCountries = new Combo(shell, SWT.NONE);
		CodeTableRecordType[] records = NemoLists.getVocabList(Intern.list_countries);
		
		TreeMap<String, String> treePays = new TreeMap<String, String>();
			
		for (CodeTableRecordType record : records){	
			treePays.put(record.getEntryTerm(), Utils.extraitCodeURN(record.getEntryKey()));
		}
		
		for (Entry<String, String> currentEntry : treePays.entrySet()) {
			comboCountries.add(currentEntry.getValue() + Global.SEP + currentEntry.getKey()); 
		}
		
		
        // Remplissage des Regions
		comboRegion = new Combo(shell, SWT.NONE);
		NemoLists.listeRegion = new ArrayList<String>();
		CodeTableRecordType[] regions = NemoLists.getVocabList(Intern.list_regions);
		
		TreeMap<String, String> treeRegion = new TreeMap<String, String>();
			
		for (CodeTableRecordType record : regions)
		{	
			treeRegion.put(record.getEntryTerm(), Utils.extraitCodeURN(record.getEntryKey()));            
		}

		temp = Global.EMPTY_STRING;
		for (Entry<String, String> currentEntry : treeRegion.entrySet()) {
			temp = currentEntry.getValue() + Global.SEP + currentEntry.getKey();
            NemoLists.listeRegion.add(temp);
			comboRegion.add(temp);
		}
        
	}

	public static void initListeType() {

		listeType = new List(shell, SWT.BORDER);

	}

	public static void razText(ArrayList<Text> liste) {

		for (int i = 0; i < liste.size(); i++) {
			liste.get(i).setText(Global.EMPTY_STRING); 
		}

		// textCentre.setText("FI");
		validee = false;

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

		Utils.invalidEtapes();
		
		// FAE0001187 : RaZ Navire et Pays et Region
		comboCountries.deselectAll();
		comboNavire.deselectAll();
		comboRegion.deselectAll();
	}

	public static void disposeElements() {


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
			buttonValidate.dispose();
			buttonXML.dispose();
		}

		if (listeType != null)
			listeType.dispose();

	}
    


    public static void invalidEtape(){
        if (!ViewFile.tabItem1.isDisposed()) {
            okApresReset = false;
            validee = false;
            if (ViewFile.affiche != 1 && loaded){
                loaded = false;
                disposeElements();
            }
            ViewFile.tabItem1.setText(Messages.onglet_cruise);
        }
    }

}
