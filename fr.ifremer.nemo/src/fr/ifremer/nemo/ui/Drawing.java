package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class Drawing {

	public static Composite sectionClientType;

	private static Group grou;

	public static void sectionManuelLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Text manuel, Button radio) {

		final Text manu;
		final Text lign, debu, fi;
		final Button inject;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

//		System.out.println("HOP");

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
		// c'est lui qui est passe en ppar

		radio.setParent(sectionClient);
		radio.setText(Messages.getString("Drawing.4"));

		// Button saisieManuelle =
		// ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie
		// manuelle", SWT.RADIO);

		manuel = new Text(sectionClient, SWT.BORDER);
		manu = manuel;
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton radio "position"
		// radio.setParent(sectionClient);
		// radio.setText("Position");
		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
				Messages.getString("Drawing.7"), SWT.RADIO);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 4;
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
		groupe.setVisible(true);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));
		inject = buttonInject;

		section.setClient(sectionClient);

		// une fois les elements initialises, on les passe en static pour y
		// acceder e
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
//						System.out.println("saisie manuelle section " + nomSec); // 
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
//						System.out.println("position dans fichier section "
//								+ nomSec);
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
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
//						System.out.println("bouton injection"); // 
						// System.out.println("valeur de ligne lign==> " +
						// lign.getText());
						// System.out.println("valeur de ligne de
						// ViewEdit.ligneTest ==> " +
						// ViewEdit.ligneTest.getText());
						Utils.inject(lign, debu, fi,
								ViewFile.textAreaDescription);

					}
				});

		// saisie par defaut et groupe desactive
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionManuelComboLigneDebutFin(ScrolledForm form,
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel) {

		final ScrolledForm forme;
		final Text lign, debu, fi;
		forme = form;

		lign = ligne;
		debu = debut;
		fi = fin;

		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(EditDesc.sectionDonnees);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// saisie manuelle
		// faire groupe et radios

		// bouton radio "saisie manuelle"
		Button saisieManuelle = ViewEdit.toolkitEdition.createButton(
				sectionClient, Messages.getString("Drawing.4"), SWT.RADIO);
		saisieManuelle
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("saisie manuelle section " + nomSec); // 
						// raz de la valeur manuelle
						// grou.setVisible(false);
						lign.setEnabled(false);
						debu.setEnabled(false);
						fi.setEnabled(false);
					}
				});

		// saisie manuelle
		manuel = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton radio "position"
		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
				Messages.getString("Drawing.7"), SWT.RADIO);
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("position dans fichier section "
//								+ nomSec); // 
						// raz de la valeur manuelle

						// grou.setVisible(true);
						lign.setEnabled(true);
						debu.setEnabled(true);
						fi.setEnabled(true);
					}
				});

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		Group groupe = new Group(sectionClient, SWT.NONE);
		EditDesc.groupRuptureDonnees.setText(Messages.getString("Drawing.9"));
		EditDesc.groupRuptureDonnees.setLayout(gridLayout1);
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

		// sasie par defaut et groupe desactive
		saisieManuelle.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);

		section.setClient(sectionClient);

	}

	public static void sectionComboLigneDebutFinInject(ScrolledForm form,
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Button radio, Combo combo) {

		final Combo comboList;
		final Text lign, debu, fi;
		final ScrolledForm forme;
		final Button inject;

		forme = form;

		lign = ligne;
		debu = debut;
		fi = fin;
		section.setParent(form);

		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

//		System.out.println("sectionComboLigneDebutFinInject");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// saisie manuelle
		// faire groupe et radios

		// bouton radio "saisie manuelle"
		// c'est lui qui est passe en ppar

		radio.setParent(sectionClient);
		radio.setText(Messages.getString("Drawing.5"));

		// Button saisieManuelle =
		// ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie
		// manuelle", SWT.RADIO);

		combo.setParent(sectionClient);
		comboList = combo;

		// bouton radio "position"
		// radio.setParent(sectionClient);
		// radio.setText("Position");
		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
				Messages.getString("Drawing.7"), SWT.RADIO);

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

		EditDesc.debutNbreMesures.setText("test");

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

		section.setClient(sectionClient);

		// une fois les elements initialises, on les passe en static pour y
		// acceder e
		// partir des listeners

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("saisie manuelle section " + nomSec); // 
						// raz de la valeur manuelle
						comboList.setEnabled(true);
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
//						System.out.println("position dans fichier : section "
//								+ nomSec); // 
						// raz de la valeur manuelle

						comboList.setEnabled(false);

						grou.setVisible(true);
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
//						System.out.println("bouton injection"); // 

						Utils.inject(lign, debu, fi,
								ViewFile.textAreaDescription);

					}
				});

		// saisie manuelle par defaut et groupe desactive
		radio.setSelection(true);
		lign.setEnabled(false);
		debu.setEnabled(false);
		fi.setEnabled(false);
		inject.setEnabled(false);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionComboInjectNbreLignes(ScrolledForm form,
			String nomSection, String description, Section section,
			Button radEOF, Button radLigne, Button radManuel, Text txtManuel, Button btnInject) {

		final Text manuel;
		final ScrolledForm forme;
		final Button inject;
		
		forme = form;

		section.setParent(form);

		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

//		System.out.println("sectionComboLigneDebutFinInject");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// saisie manuelle
		// faire groupe et radios

		// EOF
		radEOF.setParent(sectionClient);
		radEOF.setText("EOF");
		
		// Ligne vide
		radLigne.setParent(sectionClient);
		radLigne.setText(Messages.getString("EditDesc.ligneVide"));
		
		// Indicateur manuel
		radManuel.setParent(sectionClient);
		radManuel.setText(Messages.getString("EditDesc.indicateur"));
		
		txtManuel.setParent(sectionClient);
		manuel = txtManuel;
		
		// bouton d'injection de la position des valeurs
		btnInject.setParent(sectionClient);
		btnInject.setText(Messages.getString("Drawing.1"));
		inject = btnInject;
		
		section.setClient(sectionClient);
		
//		FAE 33 : suppression de la partie "Injecter nbre de lignes"
//		// bouton radio "saisie manuelle"
//		// c'est lui qui est passe en ppar
//		radio.setParent(sectionClient);
//		radio.setText("Indicateur manuel");
//
//		combo.setParent(sectionClient);
//		comboList = combo;
//
//		// bouton radio "position"
//		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
//				"Donnees", SWT.RADIO);
//
//		GridLayout gridLayout1 = new GridLayout();
//		gridLayout1.numColumns = 6;
//		Group groupe = new Group(sectionClient, SWT.NONE);
//		groupe.setText("Nombre de lignes");
//		groupe.setLayout(gridLayout1);
//		grou = groupe;
//
//		nbreLignes.setParent(groupe);
//
//		nbreLignes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		lign = nbreLignes;
//
//		// bouton d'injection de la position des valeurs
//		Button buttonInject = new Button(groupe, SWT.NONE);
//		buttonInject.setText(Messages.getString("Drawing.1"));
//		inject = buttonInject;
//
//		// une fois les elements initialises, on les passe en static pour y
//		// acceder e
//		// partir des listeners
//
//		// listeners
//		// radio saisie
//		radio
//				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
//					public void widgetSelected(
//							org.eclipse.swt.events.SelectionEvent e) {
////						System.out.println("saisie manuelle section " + nomSec); // 
//						// raz de la valeur manuelle
//						comboList.setEnabled(true);
//						// grou.setVisible(false);
//						// lign.setEnabled(false);
//
//					}
//				});
//
//		// radio position
//		position
//				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
//					public void widgetSelected(
//							org.eclipse.swt.events.SelectionEvent f) {
////						System.out.println("position dans fichier : section "
//						//		+ nomSec); // 
//						// raz de la valeur manuelle
//
//						comboList.setEnabled(false);
//
//						grou.setVisible(true);
//						// raz des valeurs injectees
//						lign.setText("");
//						lign.setEnabled(true);
//						inject.setEnabled(true);
//
//					}
//				});
//
		radEOF
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						inject.setEnabled(false);
						manuel.setEnabled(false);
					}
				});
		
		radLigne
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						inject.setEnabled(false);
						manuel.setEnabled(false);
					}
				});

		radManuel
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						inject.setEnabled(true);
						manuel.setEnabled(true);
					}
				});
		
		// bouton inject
		btnInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						manuel.setText(ViewFile.textAreaDescription.getText().substring(
								ViewFile.textAreaDescription.getSelection().x, 
								ViewFile.textAreaDescription.getSelection().y));
						manuel.pack();

					}
				});
		

		// saisie manuelle par defaut et groupe desactive
		if (DataModel.ouverture) {
			if (DataModel.stationModel_modele.isEndOnEOF()) {
				EditDesc.selectComboEnd(EditDesc.radEOF);
			} else if (DataModel.stationModel_modele.isEndOnEOF()) {
				EditDesc.selectComboEnd(EditDesc.radLigne);
			} else if (DataModel.stationModel_modele.isEndOnEOF()) {
				EditDesc.selectComboEnd(EditDesc.radManuel);
			}
		} else {
			// par defaut manuel
			EditDesc.selectComboEnd(EditDesc.radEOF);
		}
		
		

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionLabelText(ScrolledForm form, String nomSection,
			String description, Section section, Text manuel) {
		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final ScrolledForm forme;
		forme = form;
		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// label avec le texte de "description"
		Label label1 = new Label(section, SWT.NULL);
		label1.setText(description);

		// zone d'edition
		manuel = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		section.setClient(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionRadioManuelRadioSimpleInjectLigneDebutFinInject(
			ScrolledForm form, String nomSection, String description,
			Section section, Text manuel, Button radio, String txtRadio1,
			String txtRadio2, Text ligne, Text debut, Text fin) {

		final ScrolledForm forme;
		final Button inject;
		final Text manu;
		final Text lign, debu, fi;
		final Group group;

		manu = manuel;
		forme = form;

		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// radio manuel dont on garde le pointeur
		radio.setParent(sectionClient);
		radio.setText(txtRadio1);

		// zone d'edition manuelle
		manuel.setParent(sectionClient);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(sectionClient, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

		inject = buttonInject;

		// radio 2 simple
		Button radio2 = new Button(sectionClient, SWT.RADIO);
		radio2.setText(txtRadio2);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.9"));
		groupe.setLayout(gridLayout1);
		group = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText(Messages.getString("Drawing.10"));

		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText(Messages.getString("Drawing.11"));

		EditDesc.debutNbreMesures.setText("test");

		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText(Messages.getString("Drawing.12"));
		fin.setParent(groupe);

		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		// groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject2 = new Button(groupe, SWT.NONE);
		buttonInject2.setText(Messages.getString("Drawing.1"));
		groupe.setEnabled(false);

		section.setClient(sectionClient);

		// listener sur le premier radio global

		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
//						System.out
//								.println("saisie manuel nbre lignes entete station"); // 
						manu.setEnabled(true);
						inject.setEnabled(true);
						group.setEnabled(false);

					}
				});

		radio2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
//						System.out
//								.println("bouton nbre octets 1ere ligne entete station"); // 
						manu.setText("");
						manu.setEnabled(false);
						inject.setEnabled(false);
						group.setEnabled(true);

					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
//						System.out.println("bouton injection"); // 

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		lign = ligne;
		debu = debut;
		fi = fin;

		// bouton inject
		buttonInject2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
//						System.out.println("bouton injection"); // 

						Utils.inject(lign, debu, fi,
								ViewFile.textAreaDescription);

					}
				});

		// saisie par defaut et groupe desactive

		inject.setEnabled(true);
		radio.setSelection(true);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionLabelTextInjectNbreLignes(ScrolledForm form,
			String nomSection, String description, Section section, Text manuel) {
		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final Text manu;
		final ScrolledForm forme;
		forme = form;
		manu = manuel;

		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});

		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		GridLayout grid = new GridLayout();
		// grid.numColumns = 1;

		sectionClient.setLayout(grid);

		// label avec le texte de "description"
		Label label1 = new Label(section, SWT.NULL);
		label1.setText(description);

		// zone d'edition
		manuel.setParent(sectionClient);

		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(sectionClient, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

		section.setClient(sectionClient);
		// listener bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
//						System.out.println("bouton injection nbre lignes"); // 

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		section.pack();

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionTextInjectNbreLignesNbreOctets(ScrolledForm form,
			String nomSection, String description, Section section, Text manuel) {

		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final Text manu;
		final ScrolledForm forme;
		forme = form;
		manu = manuel;

		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setText(nomSection);
		section.setDescription(description);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		// label avec le texte de "description"
		Label label1 = new Label(section, SWT.NULL);
		label1.setText(description);

		// zone d'edition
		manuel.setParent(sectionClient);

		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(sectionClient, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

		section.setClient(sectionClient);

		// listener bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
//						System.out.println("bouton injection nbre lignes"); // 

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void ajoutSectionDansListeDesc(Section section) {

		EditDesc.listeSectionsDesc.add(section);
		//System.out.println("ajout section ok");

	}

	public static void ajoutSectionDansListeSurvey(Section section) {

		EditSurvey.listeSectionsSurvey.add(section);
		//System.out.println("ajout section ok");

	}
	
	public static void sectionReferenceCampagne(
			Section section, String nomSection, String descSection,
			Text txtRefCampagne, String strRefCampagne,
			Text txtNomCampagne, String strNomCampagne,
			Combo cmbNavire, String strNavire) {
		
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		
		// Reference campagne
		Label lblRefCampagne = new Label(sectionClient, SWT.NULL);
		lblRefCampagne.setText(strRefCampagne);

		txtRefCampagne.setParent(sectionClient);
		txtRefCampagne.setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		
		// Nom campagne
		Label lblNomCampagne = new Label(sectionClient, SWT.NULL);
		lblNomCampagne.setText(strNomCampagne);

		txtNomCampagne.setParent(sectionClient);
		txtNomCampagne.setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		
		// Navire
		Label lblNavire = new Label(sectionClient, SWT.NULL);
		lblNavire.setText(strNavire);
		
		cmbNavire.setParent(sectionClient);
		cmbNavire.setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		
		section.setClient(sectionClient);
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);
		ViewEdit.formEdition.reflow(true);
		
		// Creation du menu contextuel pour la recherche filtree
		Utils.createSearchMenu(cmbNavire, Global.SHIP);
	}

	public static void sectionNlabelsNtext(Section section, String nomSection,
			ArrayList<Text> listeText, ArrayList<String> listeNoms,
			String descriptionSection, boolean blnPays) {

		int nbre, i;
		nbre = listeText.size();
		
		// affichage section
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descriptionSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		
//		 Combo pays
		if (blnPays) {
			Label labelPays = new Label(sectionClient, SWT.NULL);
			labelPays.setText(Messages.getString("EditSurvey.21"));
			labelPays.setParent(sectionClient);
			labelPays.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));

			EditSurvey.comboCountries.setParent(sectionClient);
			EditSurvey.comboCountries.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));
		}
			
		for (i = 0; i < nbre; i++) {

			// label avec le texte de "description"
			Label label = new Label(sectionClient, SWT.NULL);
			label.setText(listeNoms.get(i));

			// affichage zone texte dans la section
			listeText.get(i).setParent(sectionClient);
			listeText.get(i).setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));
		}
		
//		 Combo region
		if (!blnPays) {
			Label labelReg = new Label(sectionClient, SWT.NULL);
			labelReg.setText(Messages.getString("EditSurvey.18"));
			labelReg.setParent(sectionClient);
			labelReg.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));

			EditSurvey.comboRegion.setParent(sectionClient);
			EditSurvey.comboRegion.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));
			
			// Creation du menu contextuel pour la recherche filtree
			Utils.createSearchMenu(EditSurvey.comboRegion, Global.REGION);
			
		}

		section.setClient(sectionClient);
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);
		ViewEdit.formEdition.reflow(true);

	}

	public static void sectionNlabelsNtextConfidentialite(Section section,
			String nomSection, ArrayList<Text> listeText,
			ArrayList<String> listeNoms, String descriptionSection,
			Combo confidentialite) {


		// affichage section
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		
		GridLayoutFactory.fillDefaults().applyTo(section);
		GridDataFactory.fillDefaults().grab(false,false).applyTo(section);
		
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descriptionSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		
		GridLayoutFactory.fillDefaults().applyTo(sectionClient);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(sectionClient);
		
		// combolist centres de donnees
		Label label = new Label(sectionClient, SWT.NULL);
		label.setText(Messages.getString("EditSurvey.62"));
		EditSurvey.comboDataCentres.setParent(sectionClient); 		
		
		GridLayoutFactory.fillDefaults().applyTo(EditSurvey.comboDataCentres);
		// FAE 1190 : champ trop long
		GridDataFactory.swtDefaults().hint(410, 10).applyTo(EditSurvey.comboDataCentres);
		
		// ajout de la combolist confidentialite
		Label label2 = new Label(sectionClient, SWT.NULL);
		label2.setText(Messages.getString("EditSurvey.28"));
		confidentialite.setParent(sectionClient);

		section.setClient(sectionClient);
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);
		ViewEdit.formEdition.reflow(true);

	}

	public static void sectionNlabelsNtextListe(Section section,
			String nomSection, ArrayList<Text> listeText,
			ArrayList<String> listeNoms, String descriptionSection, List liste) {

		int nbre, i;
		nbre = listeText.size();

		// affichage section
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descriptionSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		sectionClientType = sectionClient;

		for (i = 0; i < nbre; i++) {

			// label avec le texte de "description"

			Label label = new Label(sectionClient, SWT.NULL);
			label.setText(listeNoms.get(i));

			// affichage zone texte dans la section
			listeText.get(i).setParent(sectionClient);
			listeText.get(i).setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));

		}

		// affichage d'une table
		TableDataSurvey.go();

		// ajout liste et boutons deplacement des elements
		// mettre les listeners dessus !!!!!

		section.setClient(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);

	}

	public static void sectionTypeDonnees(Section section, String nomSection,
			ArrayList<Text> listeText, ArrayList<String> listeNoms,
			String descriptionSection, List liste) {

		// affichage section
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descriptionSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		sectionClientType = sectionClient;

		// affichage d'une table
		TableDataSurvey.go();

		// ajout liste et boutons deplacement des elements
		// mettre les listeners dessus !!!!!

		section.setClient(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);

	}

	public static void sectionCommentaireSurvey(Section section,
			String nomSection, Text commentaire, String descriptionSection) {

		// affichage section
		section.setParent(ViewEdit.formEdition);
		section = ViewEdit.toolkitEdition.createSection(ViewEdit.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				ViewEdit.formEdition.reflow(true);
			}
		});
		section.setText(nomSection);
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section.setDescription(descriptionSection);
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalSpan = 2;

		commentaire.setParent(sectionClient);
		commentaire.setLayoutData(new GridData(GridData.FILL_BOTH));
		commentaire.setTextLimit(50000);
		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3,
					1);
			gridData.minimumHeight = 40;
			gridData.minimumWidth = 50;
			commentaire.setLayoutData(gridData);
		}

		{
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3,
					1);
			gridData.minimumHeight = 40;
			gridData.minimumWidth = 50;
			commentaire.setLayoutData(gridData);
		}

		section.setClient(sectionClient);
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);

		ViewEdit.formEdition.reflow(true);
	}

}
