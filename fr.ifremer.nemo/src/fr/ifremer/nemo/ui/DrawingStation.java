package fr.ifremer.nemo.ui;

import java.text.ParseException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Formatter;

public class DrawingStation {

	private static Group grou;
	
	// private static Text txtTest;

	public static void sectionManuelLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Text manuel, Button radio, Button position) {

		final Text lign, debu, fi;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		//System.out.println("sectionManuelLigneDebutFinInject DrawingStation ==> ok");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);

		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		manuel.setParent(sectionClient);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.CENTER);
		label1.setText("Ligne");
		ligne.setParent(groupe);
		// ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.CENTER);
		label2.setText("Début");
		debut.setParent(groupe);
		// debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.CENTER);
		label3.setText("Fin");
		fin.setParent(groupe);

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.CENTER);
		buttonInject.setText("Injecter");

		inject = buttonInject;

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						manu.setEnabled(true);
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						// grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);

					}
				});

		// par défaut manuel
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectFreqEchant(
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Button position,
			Text conversion, Text testConversion) {

		final Text lign, debu, fi, conversio, testConversio;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out
//				.println("sectionManuelLigneDebutFinInject DrawingStation ==> ok");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);

		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group groupeConv = new Group(sectionClient, SWT.NONE);
		groupeConv.setText("Conversion");
		groupeConv.setLayout(gridLayout);

		// zone de conversion et test de conversion
		Label labelConv = new Label(groupeConv, SWT.NONE);
		labelConv.setText("Formule");
		conversion.setParent(groupeConv);

		Label labelTest = new Label(groupeConv, SWT.NONE);
		labelTest.setText("Test");
		testConversion.setParent(groupeConv);

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		manuel.setParent(sectionClient);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// radio position

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.CENTER);
		label1.setText("Ligne");
		ligne.setParent(groupe);

		// debut
		Label label2 = new Label(groupe, SWT.CENTER);
		label2.setText("Début");
		debut.setParent(groupe);

		// fin
		Label label3 = new Label(groupe, SWT.CENTER);
		label3.setText("Fin");
		fin.setParent(groupe);

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.CENTER);
		buttonInject.setText("Injecter");

		inject = buttonInject;

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;

		// listeners

		// listener zone de test
		testConversion
				.addMouseListener(new org.eclipse.swt.events.MouseListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

					}

					public void mouseDoubleClick(MouseEvent e) {
						// 
						EditStation.conversionFreqEchant();

//						System.out.println("mise à jour champ test conversion");
					}

					public void mouseDown(MouseEvent e) {

//						System.out.println("mise à jour champ test conversion");
					}

					public void mouseUp(MouseEvent e) {
						// 
//						System.out.println("mise à jour champ test conversion");
					}
				});

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						manu.setEnabled(true);
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						// grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);

					}
				});

		// par défaut manuel
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionIncrementeLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Button radio, Button position) {

		// le ppar radio est le pointeur du bouton "incremente"
		final String nomSec;
		final Text lign, debu, fi, test;
		final Button inject;

		nomSec = nomSection;

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out
//				.println("sectionIncrementeLigneDebutFinInject() DrawingStation ==> ok : section créée");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);

		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar
		radio.setParent(sectionClient);
		radio.setText("Position incrémentale");

		// bouton radio "position"
		// radio.setParent(sectionClient);
		// radio.setText("Position");

		// Button position = new Button(sectionClient, SWT.RADIO);
		position.setParent(sectionClient);
		position.setText("Position fixée");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");
		inject = buttonInject;

		inject.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		section.setClient(sectionClient);
		
//		 Zone de test
		Composite compoTest = new Composite(sectionClient,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
		
		Button btnTest = new Button(compoTest,SWT.NONE);
		btnTest.setText("Test");
		
		Text txtTest = new Text(compoTest, SWT.BORDER);
		txtTest.setEnabled(false);
		txtTest.setText("");
		
		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		test = txtTest;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {
//						System.out.println("position dans fichier section "
//								+ nomSec); // TODO Auto-generated Event stub
						// widgetSelected()
						// raz de la valeur manuelle

						grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);

					}
				});

		// bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (EditStation.buttonHeureSaisie.getSelection()) {
							Utils.test(test, EditStation.textHeureSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, debu,
									fi);
						}
					}
				});
		
		
		
		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		radio.setSelection(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionTypeDonnees(Section section, String nom) {

		// EditStation.sectionTypeDonnees.setParent(EditStation.formEdition);
		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out
//				.println("sectionTypeDonnees() DrawingStation ==> ok : section créée");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		section.setText(nom);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section
				.setDescription("Sélectionnez ici dans la liste le type des données mesurées");

		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		section.setClient(sectionClient);

		sectionClient.setLayout(new GridLayout());

		EditStation.comboRefStationType.setParent(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionHistoriqueAcquisition(String nomSection,
			String description, Section section, Text ligne1, Text debut1,
			Text fin1, Text ligne2, Text debut2, Text fin2, Text zone1,
			Text zone2, Button radio, Button position) {

		final String nomSec;
		final Text lign, debu, fi;
		final Button inject1, inject2;
		final Text manu, lign1, lign2, fi1, fi2, debu1, debu2, zon1, zon2;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditStation.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		// ligne 1
		Label label3 = new Label(sectionClient, SWT.NULL);
		label3.setText("Ligne 1");
		zone1.setParent(sectionClient);

		zone1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne 2
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText("Ligne 2");
		zone2.setParent(sectionClient);

		zone2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne 1
		Label label11 = new Label(groupe, SWT.NULL);
		label11.setText("Ligne 1");
		ligne1.setParent(groupe);
		ligne1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut1
		Label label21 = new Label(groupe, SWT.NULL);
		label21.setText("Début 1");
		debut1.setParent(groupe);
		debut1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin1
		Label label31 = new Label(groupe, SWT.NULL);
		label31.setText("Fin 1");
		fin1.setParent(groupe);
		fin1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne2
		Label label12 = new Label(groupe, SWT.NULL);
		label12.setText("Ligne 2");
		ligne2.setParent(groupe);
		ligne2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut2
		Label label22 = new Label(groupe, SWT.NULL);
		label22.setText("Début 2");
		debut2.setParent(groupe);
		debut2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin2
		Label label32 = new Label(groupe, SWT.NULL);
		label32.setText("Fin 2");
		fin2.setParent(groupe);
		fin2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// boutons d'injection de la position des valeurs
		Button buttonInject1 = new Button(groupe, SWT.NONE);
		buttonInject1.setText("Injecter ligne 1");
		Button buttonInject2 = new Button(groupe, SWT.NONE);
		buttonInject2.setText("Injecter ligne 2");

		inject1 = buttonInject1;
		inject2 = buttonInject2;

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign1 = ligne1;
		debu1 = debut1;
		fi1 = fin1;
		lign2 = ligne2;
		debu2 = debut2;
		fi2 = fin2;
		zon1 = zone1;
		zon2 = zone2;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						zon1.setEnabled(true);
						zon2.setEnabled(true);
						// grou.setVisible(false);
						lign1.setEnabled(false);
						debu1.setEnabled(false);
						fi1.setEnabled(false);
						lign2.setEnabled(false);
						debu2.setEnabled(false);
						fi2.setEnabled(false);
						inject1.setEnabled(false);
						inject2.setEnabled(false);

					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						zon1.setText("");
						zon2.setText("");
						zon1.setEnabled(false);
						zon2.setEnabled(false);
						// grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign1.setText("");
						debu1.setText("");
						fi1.setText("");
						lign2.setText("");
						debu2.setText("");
						fi2.setText("");

						lign1.setEnabled(true);
						debu1.setEnabled(true);
						fi1.setEnabled(true);
						lign2.setEnabled(true);
						debu2.setEnabled(true);
						fi2.setEnabled(true);
						inject1.setEnabled(true);
						inject2.setEnabled(true);

					}
				});

		// bouton inject1
		buttonInject1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign1, debu1, fi1,
								ViewFile.styledTextStation);
						EditStation.cursHistorique1
								.setX(ViewFile.styledTextStation.getSelection().x);
						EditStation.cursHistorique1
								.setY(ViewFile.styledTextStation.getSelection().y);
					}
				});

		// bouton inject2
		buttonInject2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign2, debu2, fi2,
								ViewFile.styledTextStation);
						EditStation.cursHistorique2
								.setX(ViewFile.styledTextStation.getSelection().x);
						EditStation.cursHistorique2
								.setY(ViewFile.styledTextStation.getSelection().y);

					}
				});

		// par défaut saisie manuelle
		radio.setSelection(true);
		zon1.setEnabled(true);
		zon2.setEnabled(true);
		// grou.setVisible(false);
		lign1.setEnabled(false);
		debu1.setEnabled(false);
		fi1.setEnabled(false);
		lign2.setEnabled(false);
		debu2.setEnabled(false);
		fi2.setEnabled(false);

		inject1.setEnabled(false);
		inject2.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionSurfaceSample(String nomSection,
			String description, Section section, Text ligne1, Text debut1,
			Text fin1, Text ligne2, Text debut2, Text fin2, Text zone1,
			Text zone2, Button radio, Button position) {

		final String nomSec;
		final Text lign, debu, fi;
		final Button inject1, inject2;
		final Text manu, lign1, lign2, fi1, fi2, debu1, debu2, zon1, zon2;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditStation.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		// ligne 1
		Label label3 = new Label(sectionClient, SWT.NULL);
		label3.setText("Ligne 1");
		zone1.setParent(sectionClient);

		zone1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne 2
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText("Ligne 2");
		zone2.setParent(sectionClient);

		zone2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne 1
		Label label11 = new Label(groupe, SWT.NULL);
		label11.setText("Ligne 1");
		ligne1.setParent(groupe);
		ligne1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut1
		Label label21 = new Label(groupe, SWT.NULL);
		label21.setText("Début 1");
		debut1.setParent(groupe);
		debut1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin1
		Label label31 = new Label(groupe, SWT.NULL);
		label31.setText("Fin 1");
		fin1.setParent(groupe);
		fin1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne2
		Label label12 = new Label(groupe, SWT.NULL);
		label12.setText("Ligne 2");
		ligne2.setParent(groupe);
		ligne2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut2
		Label label22 = new Label(groupe, SWT.NULL);
		label22.setText("Début 2");
		debut2.setParent(groupe);
		debut2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin2
		Label label32 = new Label(groupe, SWT.NULL);
		label32.setText("Fin 2");
		fin2.setParent(groupe);
		fin2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// boutons d'injection de la position des valeurs
		Button buttonInject1 = new Button(groupe, SWT.NONE);
		buttonInject1.setText("Injecter ligne 1");
		Button buttonInject2 = new Button(groupe, SWT.NONE);
		buttonInject2.setText("Injecter ligne 2");

		inject1 = buttonInject1;
		inject2 = buttonInject2;

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign1 = ligne1;
		debu1 = debut1;
		fi1 = fin1;
		lign2 = ligne2;
		debu2 = debut2;
		fi2 = fin2;
		zon1 = zone1;
		zon2 = zone2;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						zon1.setEnabled(true);
						zon2.setEnabled(true);
						// grou.setVisible(false);
						lign1.setEnabled(false);
						debu1.setEnabled(false);
						fi1.setEnabled(false);
						lign2.setEnabled(false);
						debu2.setEnabled(false);
						fi2.setEnabled(false);
						inject1.setEnabled(false);
						inject2.setEnabled(false);

					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						zon1.setText("");
						zon2.setText("");
						zon1.setEnabled(false);
						zon2.setEnabled(false);
						// grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign1.setText("");
						debu1.setText("");
						fi1.setText("");
						lign2.setText("");
						debu2.setText("");
						fi2.setText("");

						lign1.setEnabled(true);
						debu1.setEnabled(true);
						fi1.setEnabled(true);
						lign2.setEnabled(true);
						debu2.setEnabled(true);
						fi2.setEnabled(true);
						inject1.setEnabled(true);
						inject2.setEnabled(true);

					}
				});

		// bouton inject1
		buttonInject1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign1, debu1, fi1,
								ViewFile.styledTextStation);
						EditStation.cursEchant1.setX(ViewFile.styledTextStation
								.getSelection().x);
						EditStation.cursEchant1.setY(ViewFile.styledTextStation
								.getSelection().y);

					}
				});

		// bouton inject2
		buttonInject2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign2, debu2, fi2,
								ViewFile.styledTextStation);

						EditStation.cursEchant2.setX(ViewFile.styledTextStation
								.getSelection().x);
						EditStation.cursEchant2.setY(ViewFile.styledTextStation
								.getSelection().y);
					}
				});

		// par défaut saisie manuelle
		radio.setSelection(true);
		zon1.setEnabled(true);
		zon2.setEnabled(true);
		// grou.setVisible(false);
		lign1.setEnabled(false);
		debu1.setEnabled(false);
		fi1.setEnabled(false);
		lign2.setEnabled(false);
		debu2.setEnabled(false);
		fi2.setEnabled(false);

		inject1.setEnabled(false);
		inject2.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionCommentaire(Section section) {

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out.println("sectionCommentaire() DrawingStation");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText("Commentaire");
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section
				.setDescription("Entrez un commentaire ou surlignez les lignes correspondantes et cliquez sur injecter");
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		section.setClient(sectionClient);

		EditStation.textCommentaire.setParent(sectionClient);
		EditStation.textCommentaire.setTextLimit(500);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionFlagQC(Section section, String nomSection,
			String description, Text text) {

		final Text zone;
		zone = text;

		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});

		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);

		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		Button button = new Button(sectionClient, SWT.RADIO);
		button.setText("essai");
		text.setParent(sectionClient);
		text.setTextLimit(1);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionCommentaireLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Text manuel, Button radio, Button position) {

		// ppar manuel = zone de commentaire
		final Text lign, debu, fi;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// saisie manuelle
		// faire groupe et radios

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		// manuel = new Text(sectionClient, SWT.BORDER);
		manuel.setParent(sectionClient);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3,
					1);
			gridData.minimumHeight = 40;
			gridData.minimumWidth = 50;
			manuel.setLayoutData(gridData);
		}

		position.setParent(sectionClient);
		position.setText("position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);
		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");
		inject = buttonInject;
		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						manu.setEnabled(true);
						// grou.setVisible(false);

						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);

					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						// grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");

						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);

					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);
						EditStation.cursCommentaire
								.setX(ViewFile.styledTextStation.getSelection().x);
						EditStation.cursCommentaire
								.setY(ViewFile.styledTextStation.getSelection().y);

					}
				});

		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		radio.setSelection(!blnProvided);
		manu.setEnabled(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionHeure(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin,
			Text heureSaisie, Label label) {

		// garder le groupe
		// virer le second bouton radio !
		// ajouter combo de format heure

		final String nomSec;
		final Text lign, debu, fi, test;
		final Button inject;
		
		nomSec = nomSection;
		section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out.println("section heure station ==> ok");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditStation.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group groupeFormat = new Group(sectionClient, SWT.NONE);
		groupeFormat.setLayout(new GridLayout());
		groupeFormat.setText("Format");

		// combo format de l'heure
		EditStation.comboHeure.setParent(groupeFormat);

		EditStation.buttonHeureSaisie.setParent(sectionClient);
		EditStation.buttonHeureSaisie.setText("Saisie manuelle");
		EditStation.buttonHeureSaisie.setSelection(true);

		EditStation.textHeureSaisie.setParent(sectionClient);
		EditStation.textHeureSaisie.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		EditStation.buttonHeurePos.setParent(sectionClient);
		EditStation.buttonHeurePos.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");

		section.setClient(sectionClient);

		inject = buttonInject;
		
		// Zone de test
		Composite compoTest = new Composite(sectionClient,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
		
		Button btnTest = new Button(compoTest,SWT.NONE);
		btnTest.setText("Test");
		
		Text txtTest = new Text(compoTest, SWT.BORDER);
		txtTest.setEnabled(false);
		txtTest.setText("");
		

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		test = txtTest;

		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		buttonInject.setEnabled(false);

		// listener zone de test
		EditStation.textTestConvHeure
				.addMouseListener(new org.eclipse.swt.events.MouseListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

					}

					public void mouseDoubleClick(MouseEvent e) {

					}

					public void mouseDown(MouseEvent e) {

					}

					public void mouseUp(MouseEvent e) {

					}
				});

		// listeners boutons
		EditStation.buttonHeureSaisie
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						EditStation.textHeureSaisie.setEnabled(true);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);

						lign.setText("");
						debu.setText("");
						fi.setText("");

					}
				});

		EditStation.buttonHeurePos
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						EditStation.textHeureSaisie.setEnabled(false);
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);

						EditStation.textHeureSaisie.setText("");

					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);
						EditStation.offsetDebutHeure = ViewFile.styledTextStation
								.getSelection().x;
						EditStation.offsetFinHeure = ViewFile.styledTextStation
								.getSelection().y;
					}
				});
		
		// bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (EditStation.buttonHeureSaisie.getSelection()) {
							Utils.test(test, EditStation.textHeureSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, debu, fi);
						}
						
						/*try {
							
							
							DateTimeFormat format = new DateTimeFormat(
									EditStation.comboHeure.getText());
							
							Long HourLong = (Long) format
									.parseObject(test.getText());
							
							Formatter formatter;
							
							formatter = Formatter.getSingleton();
							
							test.setText(formatter.formatDateTime("hh24:mm", HourLong, "9999"));
							
							
						} catch (ParseException e) {

							Utils.messageErreurAppend(Messages
									.getString("Etape.6")); //$NON-NLS-1$
						}*/
						test.pack();
						
					}
				});
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		EditStation.buttonHeureSaisie.setSelection(!blnProvided);
		EditStation.textHeureSaisie.setEnabled(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionDate(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin, Text dateSaisie,
			Label label) {

		// garder le groupe
		// virer le second bouton radio !
		// ajouter combo de format heure

		final Text lign, debu, fi, test;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		// System.out.println("section date station");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		// GridData gd = new GridData();
		// gd.horizontalSpan = 2;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group groupeFormat = new Group(sectionClient, SWT.NONE);
		groupeFormat.setLayout(new GridLayout());
		groupeFormat.setText("Format");

		// combo format de l'heure
		EditStation.comboDate.setParent(groupeFormat);

		EditStation.buttonDateSaisie.setParent(sectionClient);
		EditStation.buttonDateSaisie.setText("Saisie manuelle");
		EditStation.buttonDateSaisie.setSelection(true);

		EditStation.textDateSaisie.setParent(sectionClient);
		EditStation.textDateSaisie.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		EditStation.buttonDatePos.setParent(sectionClient);
		EditStation.buttonDatePos.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Position");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);
		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");

		section.setClient(sectionClient);
		
		// Zone de test
		Composite compoTest = new Composite(sectionClient,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
		
		Button btnTest = new Button(compoTest,SWT.NONE);
		btnTest.setText("Test");
		
		Text txtTest = new Text(compoTest, SWT.BORDER);
		txtTest.setEnabled(false);
		txtTest.setText("");

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		test = txtTest;

		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		buttonInject.setEnabled(false);

		// listeners
		inject = buttonInject;

		// listener zone de test
		EditStation.textTestConvDate
				.addMouseListener(new org.eclipse.swt.events.MouseListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

					}

					public void mouseDoubleClick(MouseEvent e) {

					}

					public void mouseDown(MouseEvent e) {

					}

					public void mouseUp(MouseEvent e) {

					}
				});

		// listeners boutons
		EditStation.buttonDateSaisie
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						EditStation.textDateSaisie.setEnabled(true);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);

						lign.setText("");
						debu.setText("");
						fi.setText("");

					}
				});

		EditStation.buttonDatePos
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						EditStation.textDateSaisie.setEnabled(false);
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);

						EditStation.textDateSaisie.setText("");

					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);
						EditStation.offsetDebutDate = ViewFile.styledTextStation
								.getSelection().x;
						EditStation.offsetFinDate = ViewFile.styledTextStation
								.getSelection().y;

					}
				});
		
		// bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (EditStation.buttonHeureSaisie.getSelection()) {
							Utils.test(test, EditStation.textHeureSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, debu, fi);
						}
						
						test.pack();
					}
				});
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		EditStation.buttonDateSaisie.setSelection(!blnProvided);
		EditStation.textDateSaisie.setEnabled(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectLatLongComboFormat(
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Combo combo,
			Button position) {
		final String nomSec;
		final Text manu;
		final Text lign, debu, fi, test;
		final Button inject;

		nomSec = nomSection;
		manu = manuel;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		Group groupFormat = new Group(sectionClient, SWT.NONE);
		groupFormat.setText("Format");
		groupFormat.setLayout(new GridLayout());
		// combo format
		combo.setParent(groupFormat);

		// saisie manuelle
		// faire groupe et radios

		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		manuel.setParent(sectionClient);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);
		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");
		inject = buttonInject;

		section.setClient(sectionClient);
		
//		 Zone de test
		Composite compoTest = new Composite(sectionClient,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
		
		Button btnTest = new Button(compoTest,SWT.NONE);
		btnTest.setText("Test");
		
		Text txtTest = new Text(compoTest, SWT.BORDER);
		txtTest.setEnabled(false);
		txtTest.setText("");

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		test = txtTest;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						manu.setEnabled(true);
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");

						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);

					}
				});
		
//		 bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (EditStation.buttonHeureSaisie.getSelection()) {
							Utils.test(test, EditStation.textHeureSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, debu, fi);
						}
						
						test.pack();
					}
				});

		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		radio.setSelection(!blnProvided);
		manu.setEnabled(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectLatLongFin(
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Button position) {
		final String nomSec;
		final Text manu;
		final Text lign, debu, fi, test;
		final Button inject;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

		manuel.setParent(sectionClient);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		manu = manuel;

		position.setParent(sectionClient);
		position.setText("Position");

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");
		debut.setParent(groupe);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");
		inject = buttonInject;

		section.setClient(sectionClient);
		
//		 Zone de test
		Composite compoTest = new Composite(sectionClient,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoTest);
		
		Button btnTest = new Button(compoTest,SWT.NONE);
		btnTest.setText("Test");
		
		Text txtTest = new Text(compoTest, SWT.BORDER);
		txtTest.setEnabled(false);
		txtTest.setText("");

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		test = txtTest;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						manu.setEnabled(true);
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
						inject.setEnabled(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {

						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");

						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
						inject.setEnabled(true);
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils
								.inject(lign, debu, fi,
										ViewFile.styledTextStation);

					}
				});
		
//		 bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (EditStation.buttonHeureSaisie.getSelection()) {
							Utils.test(test, EditStation.textHeureSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, debu, fi);
						}
						
						test.pack();
					}
				});

		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("dcHistory1");
		
		boolean blnProvided = propertyModel.isUserProvided();
		radio.setSelection(!blnProvided);
		manu.setEnabled(!blnProvided);
		lign.setEnabled(blnProvided);
		debu.setEnabled(blnProvided);
		fi.setEnabled(blnProvided);
		inject.setEnabled(blnProvided);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void ajoutSectionDansListeStation(Section section) {

		EditStation.listeSectionsStation.add(section);
		//System.out.println("ajout section ok");

	}

	public static void sectionDateQC(String nomSection, String description,
			Section section) {

		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		sectionClient.setLayout(gridLayout);

		// label avec le texte de "description"
		Label label1 = new Label(sectionClient, SWT.NULL);
		label1.setText("Flag QC Date/Heure");
		EditStation.textQCDate.setParent(sectionClient);

		EditStation.textQCDate.setTextLimit(1);

		Label label2 = new Label(sectionClient, SWT.NULL);
		label2.setText("Flag QC Latitude");
		EditStation.textQCLat.setParent(sectionClient);

		EditStation.textQCLat.setTextLimit(1);

		Label label3 = new Label(sectionClient, SWT.NULL);
		label3.setText("Flag QC Longitude");
		EditStation.textQCLong.setParent(sectionClient);

		EditStation.textQCLong.setTextLimit(1);

		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText("Flag QC Profondeur");
		EditStation.textQCProf.setParent(sectionClient);

		EditStation.textQCProf.setTextLimit(1);

		section.setClient(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

}
