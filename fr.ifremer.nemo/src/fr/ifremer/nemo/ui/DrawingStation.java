package fr.ifremer.nemo.ui;

import java.io.File;
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

import fr.ifremer.nemo.input.HeaderFragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Messages;

public class DrawingStation {

	private static Group grou;
	
	// private static Text txtTest;

	public static void sectionManuelLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Text manuel, Button radio, Button position, boolean blnManuel) {

		final Text lign, debu, fi;
		final Button inject;
		final Text manu;

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
		radio.setText(Messages.getString("Drawing.4"));

		manuel.setParent(sectionClient);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.CENTER);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(groupe);
		// ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.CENTER);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(groupe);
		// debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.CENTER);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.CENTER);
		buttonInject.setText(Messages.getString("Drawing.1"));

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
		
		/*// par défaut manuel
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		radio.setSelection(blnManuel);
		position.setSelection(!blnManuel);
		manu.setEnabled(blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectFreqEchant(
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Button position,
			Text conversion, Text testConversion, boolean blnManuel) {

		final Text lign, debu, fi;
		final Button inject;
		final Text manu;


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
		radio.setText(Messages.getString("Drawing.4"));

		manuel.setParent(sectionClient);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// radio position

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.CENTER);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(groupe);

		// debut
		Label label2 = new Label(groupe, SWT.CENTER);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(groupe);

		// fin
		Label label3 = new Label(groupe, SWT.CENTER);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.CENTER);
		buttonInject.setText(Messages.getString("Drawing.1"));

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

		/*// par défaut manuel
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		radio.setSelection(blnManuel);
		position.setSelection(!blnManuel);
		manu.setEnabled(blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
		

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionIncrementeLigneDebutFinInject(String nomSection,
			String description, Section section, Text numero, Text ligne, Text debut,
			Text fin, Button radio, Button position, boolean blnManuel) {

		// le ppar radio est le pointeur du bouton "incremente"
		final Text num, lign, debu, fi, test;
		final Button inject, pos;


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

		Composite compo1 = new Composite(sectionClient, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo1);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo1);
		
		// bouton radio "saisie manuelle"
		// c'est lui qui est passé en ppar
		radio.setParent(compo1);
		radio.setText(Messages.getString("Drawing.6"));

		numero.setParent(compo1);
		
		// bouton radio "position"
		// radio.setParent(sectionClient);
		// radio.setText("Position");

		// Button position = new Button(sectionClient, SWT.RADIO);
		position.setParent(compo1);
		position.setText(Messages.getString("Drawing.8"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(groupe);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));
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
		pos = position;
		num = numero;

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						num.setText("1");
						
						// grou.setVisible(false);
						num.setEnabled(true);
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
						num.setEnabled(false);
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
						if (pos.getSelection()) {
							if (lign.getText().equals("0")) {
								// On récupère le numéro de station dans le nom du fichier
								String strFichier = "";
								if (ViewFile.radioFichier.getSelection()) {
									strFichier = Utils.extraitFichierChemin(ViewFile.nomFichier);
								} else {
									File directory = new File(ViewFile.nomRepertoire);
									File[] subfiles = directory.listFiles();
									strFichier = subfiles[0].getName();
								}
								
								test.setText(strFichier.substring(Integer.parseInt(debu.getText()) - 1, Integer.parseInt(fi.getText())));
							} else {
								Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
							}
							
							// test.setText(test.getText().format("%05d", Integer.parseInt(test.getText().trim())));
							test.setText(Formatter.getSingleton().padZero(test.getText().trim(), 5));
							test.pack();
						} else {
							test.setText(Formatter.getSingleton().padZero(num.getText().trim(), 5));
							test.pack();
						}	
					}
				});
		
		
		
		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
				
		
		radio.setSelection(blnManuel);
		numero.setEnabled(blnManuel);
		position.setSelection(!blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
		
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionTypeDonnees(Section section, String nom, Combo cmbRefType) {

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
				.setDescription(Messages.getString("Drawing.13"));

		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		section.setClient(sectionClient);

		sectionClient.setLayout(new GridLayout());

		// EditStation.comboRefStationType.setParent(sectionClient);
		cmbRefType.setParent(sectionClient);
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionHistoriqueAcquisition(String nomSection,
			String description, Section section, Text ligne1, Text debut1,
			Text fin1, Text ligne2, Text debut2, Text fin2, Text zone1,
			Text zone2, Button radio, Button position, boolean blnManuel) {

		final Button inject1, inject2;
		final Text  lign1, lign2, fi1, fi2, debu1, debu2, zon1, zon2;



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
		radio.setText(Messages.getString("Drawing.4"));

		// ligne 1
		Label label3 = new Label(sectionClient, SWT.NULL);
		label3.setText(Messages.getString("Drawing.14"));
		zone1.setParent(sectionClient);

		zone1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne 2
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText(Messages.getString("Drawing.15"));
		zone2.setParent(sectionClient);

		zone2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne 1
		Label label11 = new Label(groupe, SWT.NULL);
		label11.setText(Messages.getString("Drawing.14"));
		ligne1.setParent(groupe);
		ligne1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut1
		Label label21 = new Label(groupe, SWT.NULL);
		label21.setText(Messages.getString("Drawing.16"));
		debut1.setParent(groupe);
		debut1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin1
		Label label31 = new Label(groupe, SWT.NULL);
		label31.setText(Messages.getString("Drawing.18"));
		fin1.setParent(groupe);
		fin1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne2
		Label label12 = new Label(groupe, SWT.NULL);
		label12.setText(Messages.getString("Drawing.15"));
		ligne2.setParent(groupe);
		ligne2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut2
		Label label22 = new Label(groupe, SWT.NULL);
		label22.setText(Messages.getString("Drawing.17"));
		debut2.setParent(groupe);
		debut2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin2
		Label label32 = new Label(groupe, SWT.NULL);
		label32.setText(Messages.getString("Drawing.19"));
		fin2.setParent(groupe);
		fin2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// boutons d'injection de la position des valeurs
		Button buttonInject1 = new Button(groupe, SWT.NONE);
		buttonInject1.setText(Messages.getString("Drawing.2"));
		Button buttonInject2 = new Button(groupe, SWT.NONE);
		buttonInject2.setText(Messages.getString("Drawing.3"));

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

		
		radio.setSelection(blnManuel);
		position.setSelection(!blnManuel);
		zon1.setEnabled(blnManuel);
		zon2.setEnabled(blnManuel);
		lign1.setEnabled(!blnManuel);
		debu1.setEnabled(!blnManuel);
		fi1.setEnabled(!blnManuel);
		lign2.setEnabled(!blnManuel);
		debu2.setEnabled(!blnManuel);
		fi2.setEnabled(!blnManuel);
		inject1.setEnabled(!blnManuel);
		inject2.setEnabled(!blnManuel);
		
		/*// par défaut saisie manuelle
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
		inject2.setEnabled(false);*/
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionSurfaceSample(String nomSection,
			String description, Section section, Text ligne1, Text debut1,
			Text fin1, Text ligne2, Text debut2, Text fin2, Text zone1,
			Text zone2, Button radio, Button position, boolean blnManuel) {

		final Button inject1, inject2;
		final Text  lign1, lign2, fi1, fi2, debu1, debu2, zon1, zon2;


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
		radio.setText(Messages.getString("Drawing.4"));

		// ligne 1
		Label label3 = new Label(sectionClient, SWT.NULL);
		label3.setText(Messages.getString("Drawing.14"));
		zone1.setParent(sectionClient);

		zone1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne 2
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText(Messages.getString("Drawing.15"));
		zone2.setParent(sectionClient);

		zone2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne 1
		Label label11 = new Label(groupe, SWT.NULL);
		label11.setText(Messages.getString("Drawing.14"));
		ligne1.setParent(groupe);
		ligne1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut1
		Label label21 = new Label(groupe, SWT.NULL);
		label21.setText(Messages.getString("Drawing.16"));
		debut1.setParent(groupe);
		debut1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin1
		Label label31 = new Label(groupe, SWT.NULL);
		label31.setText(Messages.getString("Drawing.18"));
		fin1.setParent(groupe);
		fin1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// ligne2
		Label label12 = new Label(groupe, SWT.NULL);
		label12.setText(Messages.getString("Drawing.15"));
		ligne2.setParent(groupe);
		ligne2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut2
		Label label22 = new Label(groupe, SWT.NULL);
		label22.setText(Messages.getString("Drawing.17"));
		debut2.setParent(groupe);
		debut2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin2
		Label label32 = new Label(groupe, SWT.NULL);
		label32.setText(Messages.getString("Drawing.19"));
		fin2.setParent(groupe);
		fin2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// boutons d'injection de la position des valeurs
		Button buttonInject1 = new Button(groupe, SWT.NONE);
		buttonInject1.setText(Messages.getString("Drawing.2"));
		Button buttonInject2 = new Button(groupe, SWT.NONE);
		buttonInject2.setText(Messages.getString("Drawing.3"));

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

		
		
		radio.setSelection(blnManuel);
		position.setSelection(!blnManuel);
		zon1.setEnabled(blnManuel);
		zon2.setEnabled(blnManuel);
		lign1.setEnabled(!blnManuel);
		debu1.setEnabled(!blnManuel);
		fi1.setEnabled(!blnManuel);
		lign2.setEnabled(!blnManuel);
		debu2.setEnabled(!blnManuel);
		fi2.setEnabled(!blnManuel);
		inject1.setEnabled(!blnManuel);
		inject2.setEnabled(!blnManuel);
		
		/*// par défaut saisie manuelle
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
		inject2.setEnabled(false);*/

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
		
		if (DataModel.ouverture) {
			PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("comment");
			
			boolean blnProvided = propertyModel.isUserProvided();
			EditStation.buttonCommentaireSaisie.setSelection(blnProvided);
			EditStation.textCommentaire.setEnabled(!blnProvided);
			EditStation.textCommentaireDebut.setEnabled(!blnProvided);
			EditStation.textCommentaireFin.setEnabled(!blnProvided);
			EditStation.buttonCommentairePos.setEnabled(!blnProvided);
		} else {
			// par défaut manuel
			EditStation.buttonCommentaireSaisie.setSelection(true);
			EditStation.textCommentaire.setEnabled(false);
			EditStation.textCommentaireDebut.setEnabled(false);
			EditStation.textCommentaireFin.setEnabled(false);
			EditStation.buttonCommentairePos.setEnabled(false);
		}

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionFlagQC(Section section, String nomSection,
			String description, Text text) {


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
			Text fin, Text manuel, Button radio, Button position, Button buttonVar, boolean blnManuel) {

		// ppar manuel = zone de commentaire
		final Text lign, debu, fi;
		final Button inject, var;
		final Text manu;



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
		radio.setText(Messages.getString("Drawing.4"));

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
		position.setText(Messages.getString("Drawing.7"));

		// GridLayout gridLayout1 = new GridLayout();
		// gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		// groupe.setLayout(gridLayout1);
		grou = groupe;
		
		GridLayoutFactory.fillDefaults().applyTo(groupe);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(groupe);
		
		Composite compo1 = new Composite(groupe,SWT.NONE);		
		GridLayoutFactory.fillDefaults().numColumns(6).applyTo(compo1);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo1);
		
		// ligne
		Label label1 = new Label(compo1, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(compo1);
		// ligne = new Text(groupe, SWT.BORDER);
		// ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(compo1, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(compo1);
		// = new Text(groupe, SWT.BORDER);
		// debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(compo1, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(compo1);
		// = new Text(groupe, SWT.BORDER);
		// fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);
		
		Composite compo2 = new Composite(groupe,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo2);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo2);
		
		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(compo2, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));
		
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(buttonInject);
		
		
		// bouton de fin de ligne variable
		// buttonVar = new Button(groupe, SWT.CHECK);
		buttonVar.setText(Messages.getString("Drawing.20"));
		buttonVar.setParent(compo2);
		// buttonVar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonVar);
		
		section.setClient(sectionClient);
		
		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;
		inject = buttonInject;
		var = buttonVar;

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
						var.setEnabled(false);

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
						var.setEnabled(true);

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
		
		// bouton Var
		buttonVar
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						fi.setEnabled(!var.getSelection());

					}
				});
		
		
		radio.setSelection(blnManuel);
		position.setSelection(!blnManuel);
		manu.setEnabled(blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
		var.setEnabled(!blnManuel);
		
		if (DataModel.ouverture) {
			PropertyModel propertyModel = DataModel.stationModel_modele.propertiesAt("comment");
			
			if (!propertyModel.isUserProvided()) {
				HeaderFragment frag = propertyModel.getFragment();
				
				fi.setEnabled(!frag.isVariable());
				
			}
		}
	
		/*// saisie manuelle par défaut
		radio.setSelection(true);
		manu.setEnabled(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);
		var.setEnabled(false);*/
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionHeure(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin,
			Text heureSaisie, Label label, boolean blnManuel) {

		// garder le groupe
		// virer le second bouton radio !
		// ajouter combo de format heure


		final Text lign, debu, fi, test;
		final Button inject;

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
		EditStation.buttonHeureSaisie.setText(Messages.getString("Drawing.4"));
		EditStation.buttonHeureSaisie.setSelection(true);

		EditStation.textHeureSaisie.setParent(sectionClient);
		EditStation.textHeureSaisie.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		EditStation.buttonHeurePos.setParent(sectionClient);
		EditStation.buttonHeurePos.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(groupe);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

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
							Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
						}
						
						try {
							DateTimeFormat format = new DateTimeFormat(
									EditStation.comboHeure.getText());
							
							Long HourLong = (Long) format
									.parseObject(test.getText());
							
							Formatter formatter;
							
							formatter = Formatter.getSingleton();
							
							test.setText(formatter.formatDateTime("hh24mm", HourLong, "9999"));
							
						} catch (ParseException e) {

							Utils.messageErreurAppend(Messages
									.getString("Etape.121")); //$NON-NLS-1$
							test.setText(Messages
									.getString("Etape.120"));
							test.pack();
						}
						test.pack();
						
					}
				});
		
		
		EditStation.buttonHeureSaisie.setSelection(blnManuel);
		EditStation.textHeureSaisie.setEnabled(blnManuel);
		EditStation.buttonHeurePos.setSelection(!blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
	
		/*// par défaut manuel
		EditStation.buttonHeureSaisie.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionDate(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin, Text dateSaisie,
			Label label, boolean blnManuel) {

		// garder le groupe
		// virer le second bouton radio !
		// ajouter combo de format heure

		final Text lign, debu, fi, test;
		final Button inject;



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
		EditStation.buttonDateSaisie.setText(Messages.getString("Drawing.4"));
		EditStation.buttonDateSaisie.setSelection(true);

		EditStation.textDateSaisie.setParent(sectionClient);
		EditStation.textDateSaisie.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		EditStation.buttonDatePos.setParent(sectionClient);
		EditStation.buttonDatePos.setText(Messages.getString("Drawing.7"));

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(groupe);
		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

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
						if (EditStation.buttonDateSaisie.getSelection()) {
							Utils.test(test, EditStation.textDateSaisie);
						} else {
							Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
						}
						
						try {
							DateTimeFormat format = new DateTimeFormat(
									EditStation.comboDate.getText());
							
							Long DateLong = (Long) format
									.parseObject(test.getText());
							
							Formatter formatter;
							
							formatter = Formatter.getSingleton();
							
							test.setText(formatter.formatDateTime("ddMMyyyy", DateLong));
							
							
						} catch (ParseException e) {

							Utils.messageErreurAppend(Messages
									.getString("Etape.122")); //$NON-NLS-1$
							test.setText(Messages
									.getString("Etape.120"));
							test.pack();
						}
						test.pack();
					}
				});
		
		
		EditStation.buttonDateSaisie.setSelection(blnManuel);
		EditStation.textDateSaisie.setEnabled(blnManuel);
		EditStation.buttonDatePos.setSelection(!blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
	
		/*// par défaut manuel
		EditStation.buttonDateSaisie.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectLatLongComboFormat(
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Combo combo,
			Button position, Button buttonVar, boolean blnManuel, boolean blnLatitude) {
		final Text manu;
		final Text lign, debu, fi, test;
		final Button inject, var;
		final boolean latitude;

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
		radio.setText(Messages.getString("Drawing.4"));

		manuel.setParent(sectionClient);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		// GridLayout gridLayout1 = new GridLayout();
		// gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		// groupe.setLayout(gridLayout1);
		grou = groupe;
		
		GridLayoutFactory.fillDefaults().applyTo(groupe);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(groupe);
				
		Composite compo1 = new Composite(groupe,SWT.NONE);		
		GridLayoutFactory.fillDefaults().numColumns(6).applyTo(compo1);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo1);
		
		// ligne
		Label label1 = new Label(compo1, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(compo1);
		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(compo1, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(compo1);
		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(compo1, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(compo1);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		Composite compo2 = new Composite(groupe,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo2);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo2);
		
		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(compo2, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));
		inject = buttonInject;

		// bouton de fin de ligne variable
		buttonVar.setText(Messages.getString("Drawing.20"));
		buttonVar.setParent(compo2);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonVar);

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
		latitude = blnLatitude;
		var = buttonVar;

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
						var.setEnabled(false);
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
						var.setEnabled(true);
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
		
//		 bouton Var
		buttonVar
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						fi.setEnabled(!var.getSelection());
					}
				});
		
//		 bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
							if (latitude) {
								if (EditStation.buttonLatSaisie.getSelection()) {
									Utils.test(test, EditStation.textLatitude);
								} else {
									Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
								}
							} else {
								if (EditStation.buttonLongSaisie.getSelection()) {
								Utils.test(test, EditStation.textLongitude);
								} else {
									Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
								}
							}
						
						if (latitude) {
							try {
								fr.ifremer.nemo.util.LatitudeFormat format = new fr.ifremer.nemo.util.LatitudeFormat(
										EditStation.comboLatitude.getText());
								format.setVariableLength(var.getSelection());
								
								Double latitudeDouble = (Double) format
										.parseObject(test.getText());

								Formatter formatter;

								formatter = Formatter.getSingleton();

								test.setText(formatter.formatLatitude(
										"NDD MM.mm", latitudeDouble));
							} catch (ParseException e) {

								Utils.messageErreurAppend(Messages
										.getString("Etape.53")); //$NON-NLS-1$
								test.setText(Messages
										.getString("Etape.120"));
								test.pack();
							}

						} else {
							try {
								fr.ifremer.nemo.util.LongitudeFormat format = new fr.ifremer.nemo.util.LongitudeFormat(
										EditStation.comboLongitude.getText());
								format.setVariableLength(var.getSelection());

								Double longitudeDouble = (Double) format
										.parseObject(test.getText());

								Formatter formatter;

								formatter = Formatter.getSingleton();

								test.setText(formatter.formatLongitude(
										"NDDD MM.mm", longitudeDouble));
							} catch (ParseException e) {

								Utils.messageErreurAppend(Messages
										.getString("Etape.58")); //$NON-NLS-1$
								test.setText(Messages
										.getString("Etape.120"));
								test.pack();
							}
						}	
						
						test.pack();
					}
				});
		
		radio.setSelection(blnManuel);
		manu.setEnabled(blnManuel);
		position.setSelection(!blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
		var.setEnabled(!blnManuel);
		
		if (DataModel.ouverture) {
			PropertyModel propertyModel;
			if (latitude) {
				propertyModel = DataModel.stationModel_modele.propertiesAt("latitude");
			} else {
				propertyModel = DataModel.stationModel_modele.propertiesAt("longitude");
			}
			
			if (!propertyModel.isUserProvided()) {
				HeaderFragment frag = propertyModel.getFragment();
				
				fi.setEnabled(!frag.isVariable());
				
			}
		}

		/*// saisie manuelle par défaut
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);*/
		
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void sectionManuelLigneDebutFinInjectLatLongFin(
			String nomSection, String description, Section section, Combo combo, Text ligne,
			Text debut, Text fin, Text manuel, Button radio, Button position, Button buttonVar, boolean blnManuel, boolean blnLatitude) {

		final Text manu;
		final Text lign, debu, fi, test;
		final Button inject, rad, var;
		final boolean latitude;

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
		
		radio.setParent(sectionClient);
		radio.setText(Messages.getString("Drawing.4"));

		manuel.setParent(sectionClient);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// manu = manuel;

		position.setParent(sectionClient);
		position.setText(Messages.getString("Drawing.7"));

		// GridLayout gridLayout1 = new GridLayout();
		// gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		// groupe.setLayout(gridLayout1);
		grou = groupe;

		GridLayoutFactory.fillDefaults().applyTo(groupe);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(groupe);
		
		Composite compo1 = new Composite(groupe,SWT.NONE);		
		GridLayoutFactory.fillDefaults().numColumns(6).applyTo(compo1);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo1);
		
		// ligne
		Label label1 = new Label(compo1, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));
		ligne.setParent(compo1);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(compo1, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));
		debut.setParent(compo1);

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(compo1, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(compo1);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite compo2 = new Composite(groupe,SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo2);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compo2);
		
		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(compo2, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));
		inject = buttonInject;
		
//		 bouton de fin de ligne variable
		buttonVar.setText(Messages.getString("Drawing.20"));
		buttonVar.setParent(compo2);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonVar);

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
		rad = radio;
		latitude = blnLatitude;
		var = buttonVar;

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
		
//		 bouton Var
		buttonVar
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						fi.setEnabled(!var.getSelection());

					}
				});
		
//		 bouton test
		btnTest
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						if (rad.getSelection()) {
							Utils.test(test, manu);
						} else {
							Utils.test(test, ViewFile.styledTextStation, lign, debu, fi);
						}
						
						if (latitude) {
							try {
								fr.ifremer.nemo.util.LatitudeFormat format = new fr.ifremer.nemo.util.LatitudeFormat(
										EditStation.comboLatitudeFin.getText());
								format.setVariableLength(var.getSelection());

								Double latitudeDouble = (Double) format
										.parseObject(test.getText());

								Formatter formatter;

								formatter = Formatter.getSingleton();

								test.setText(formatter.formatLatitude(
										"NDD MM.mm", latitudeDouble));
							} catch (ParseException e) {

								Utils.messageErreurAppend(Messages
										.getString("Etape.53")); //$NON-NLS-1$
								test.setText(Messages
										.getString("Etape.120"));
								test.pack();
							}

						} else {
							try {
								fr.ifremer.nemo.util.LongitudeFormat format = new fr.ifremer.nemo.util.LongitudeFormat(
										EditStation.comboLongitudeFin.getText());
								format.setVariableLength(var.getSelection());

								Double longitudeDouble = (Double) format
										.parseObject(test.getText());

								Formatter formatter;

								formatter = Formatter.getSingleton();

								test.setText(formatter.formatLongitude(
										"NDDD MM.mm", longitudeDouble));
							} catch (ParseException e) {

								Utils.messageErreurAppend(Messages
										.getString("Etape.58")); //$NON-NLS-1$
								test.setText(Messages
										.getString("Etape.120"));
								test.pack();
							}
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
		
		
		radio.setSelection(blnManuel);
		manu.setEnabled(blnManuel);
		position.setSelection(!blnManuel);
		lign.setEnabled(!blnManuel);
		debu.setEnabled(!blnManuel);
		fi.setEnabled(!blnManuel);
		inject.setEnabled(!blnManuel);
		var.setEnabled(!blnManuel);
		
		if (DataModel.ouverture) {
			PropertyModel propertyModel;
			if (latitude) {
				propertyModel = DataModel.stationModel_modele.getTimeSeriesModel().propertiesAt("endLatitude");
			} else {
				propertyModel = DataModel.stationModel_modele.getTimeSeriesModel().propertiesAt("endLongitude");
			}
			
			if (!propertyModel.isUserProvided()) {
				HeaderFragment frag = propertyModel.getFragment();
				
				fi.setEnabled(!frag.isVariable());
				
			}
		}

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeStation(section);

	}

	public static void ajoutSectionDansListeStation(Section section) {

		EditStation.listeSectionsStation.add(section);
		//System.out.println("ajout section ok");

	}

	public static void sectionQC(String nomSection, String description,
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
