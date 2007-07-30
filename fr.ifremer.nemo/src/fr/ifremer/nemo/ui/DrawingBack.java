package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
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
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class DrawingBack {

	/*
	 * ATTENTION : On est obligé de conserver les sections en public static
	 * depuis ViewEdit à cause du refresh sur le ViewEdit.formEdit : le principe
	 * est le suivant : on fait un dispose() sur chaque section du formulaire
	 * afin de le récupérer vide. Ensuite on recrée les sections du formulaire
	 * correspondant à la nouvelle étape. Donc on a une fonction clear() pour
	 * chaque étape afin de préparer le formulaire pour la suivante. *
	 */

	private static Group grou;

	private static Text lign, debu, fi;

	public static void sectionManuelLigneDebutFinInject(String nomSection,
			String description, Section section, Text ligne, Text debut,
			Text fin, Text manuel, Button radio) {

		final String nomSec;
		final Text manu;

		nomSec = nomSection;
		// manu = manuel;

		section.setParent(EditDesc.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		System.out.println("HOP");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
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
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Saisie manuelle");

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
				"Position", SWT.RADIO);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
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
		groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");

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
						System.out.println("saisie manuelle section " + nomSec); // 
						// raz de la valeur manuelle
						manu.setEnabled(true);
						grou.setVisible(false);
					}
				});

		// radio position
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {
						System.out.println("position dans fichier section "
								+ nomSec); // 
						// raz de la valeur manuelle

						manu.setText("");
						manu.setEnabled(false);
						grou.setVisible(true);

						// raz valeurs manuelles
						// raz des valeurs injectees
						lign.setText("");
						debu.setText("");
						fi.setText("");
					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						System.out.println("bouton injection"); // 
						// System.out.println("valeur de ligne lign==> " +
						// lign.getText());
						// System.out.println("valeur de ligne de
						// ViewEdit.ligneTest ==> " +
						// ViewEdit.ligneTest.getText());
						Utils.inject(lign, debu, fi,
								ViewFile.textAreaDescription);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionManuelComboLigneDebutFin(ScrolledForm form,
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Text manuel) {

		final String nomSec;
		final ScrolledForm forme;

		forme = form;

		nomSec = nomSection;

		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
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
				sectionClient, "Saisie manuelle", SWT.RADIO);
		saisieManuelle
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("saisie manuelle section " + nomSec); // 
						// raz de la valeur manuelle
						grou.setVisible(false);
					}
				});

		// combo list
		Combo combo = new Combo(section, SWT.READ_ONLY);

		// saisie manuelle
		manuel = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton radio "position"
		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
				"Position", SWT.RADIO);
		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("position dans fichier section "
								+ nomSec); // 
						// raz de la valeur manuelle

						grou.setVisible(true);
					}
				});

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		Group groupe = new Group(sectionClient, SWT.NONE);
		EditDesc.groupRuptureDonnees.setText("Localisation");
		EditDesc.groupRuptureDonnees.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");
		ligne.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
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
		groupe.setVisible(false);

		section.setClient(sectionClient);

	}

	/*
	 * public static void sectionComboLigneDebutFinInject(String nomSection,
	 * String description, Section section, Text ligne, Text debut, Text fin,
	 * Combo combo){ //choix du format dans un combo list //ou alors position en
	 * ligne debut fin
	 * 
	 * final String nomSec;
	 * 
	 * 
	 * nomSec = nomSection;
	 * 
	 * section = ViewEdit.toolkitEdition.createSection(form.getBody(),
	 * Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.COMPACT);
	 * 
	 * 
	 * section.addExpansionListener(new ExpansionAdapter() { public void
	 * expansionStateChanged(ExpansionEvent e) { form.reflow(true); } });
	 * section.setText(nomSection); section.setDescription(description);
	 * Composite sectionClient =
	 * ViewEdit.toolkitEdition.createComposite(EditDesc.sectionDonnees);
	 * sectionClient.setLayout(new GridLayout()); GridData gd = new GridData();
	 * gd.horizontalSpan = 2;
	 * 
	 * //saisie manuelle //faire groupe et radios
	 * 
	 * //bouton radio "saisie manuelle" Button saisieManuelle =
	 * ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie manuelle",
	 * SWT.RADIO); saisieManuelle .addSelectionListener(new
	 * org.eclipse.swt.events.SelectionAdapter() { public void
	 * widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
	 * System.out.println("saisie manuelle section " + nomSec); // //raz de la
	 * valeur manuelle grou.setVisible(false); } });
	 * 
	 * 
	 * //combo list combo.setParent(section);
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //bouton radio "position" Button position =
	 * ViewEdit.toolkitEdition.createButton(sectionClient, "Position",
	 * SWT.RADIO); position .addSelectionListener(new
	 * org.eclipse.swt.events.SelectionAdapter() { public void
	 * widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
	 * System.out.println("position dans fichier section " + nomSec); // //raz
	 * de la valeur manuelle
	 * 
	 * grou.setVisible(true); } });
	 * 
	 * 
	 * 
	 * GridLayout gridLayout1 = new GridLayout(); gridLayout1.numColumns = 2;
	 * Group groupe = new Group(sectionClient, SWT.NONE);
	 * EditDesc.groupRuptureDonnees.setText("Localisation");
	 * EditDesc.groupRuptureDonnees.setLayout(gridLayout1); grou = groupe;
	 * 
	 * //ligne Label label1 = new Label(groupe, SWT.NULL);
	 * label1.setText("Ligne"); ligne.setParent(groupe); //= new Text(groupe,
	 * SWT.BORDER); ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	 * 
	 * //debut Label label2 = new Label(groupe, SWT.NULL);
	 * label2.setText("Début"); debut.setParent(groupe); //= new Text(groupe,
	 * SWT.BORDER); debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	 * 
	 * //fin Label label3 = new Label(groupe, SWT.NULL); label3.setText("Fin");
	 * fin.setParent(groupe); //= new Text(groupe, SWT.BORDER);
	 * fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	 * 
	 * //groupe non visible de base groupe.setVisible(false);
	 * 
	 * section.setClient(sectionClient);
	 * 
	 * 
	 * 
	 * 
	 *  }
	 * 
	 */

	public static void sectionComboLigneDebutFinInject(ScrolledForm form,
			String nomSection, String description, Section section, Text ligne,
			Text debut, Text fin, Button radio, Combo combo) {

		final String nomSec;
		final Combo comboList;
		final Text lig, debu, fi;
		final ScrolledForm forme;

		forme = form;

		lig = ligne;
		debu = debut;
		fi = fin;
		nomSec = nomSection;

		section.setParent(form);

		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		System.out.println("sectionComboLigneDebutFinInject");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
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
		// c'est lui qui est passé en ppar

		radio.setParent(sectionClient);
		radio.setText("Sélection manuelle");

		// Button saisieManuelle =
		// ViewEdit.toolkitEdition.createButton(sectionClient, "Saisie
		// manuelle", SWT.RADIO);

		combo.setParent(sectionClient);
		comboList = combo;

		ligne.setText("toto");

		// bouton radio "position"
		// radio.setParent(sectionClient);
		// radio.setText("Position");
		Button position = ViewEdit.toolkitEdition.createButton(sectionClient,
				"Position", SWT.RADIO);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText("Localisation");
		groupe.setLayout(gridLayout1);
		grou = groupe;

		// ligne
		Label label1 = new Label(groupe, SWT.NULL);
		label1.setText("Ligne");

		// ligne = new Text(groupe, SWT.BORDER);
		ligne.setParent(groupe);

		ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// debut
		Label label2 = new Label(groupe, SWT.NULL);
		label2.setText("Début");

		Boolean tmp = debut.setParent(groupe);
		System.out.println("parent ?====> " + tmp);
		EditDesc.debutNbreMesures.setText("test");

		// = new Text(groupe, SWT.BORDER);
		debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// fin
		Label label3 = new Label(groupe, SWT.NULL);
		label3.setText("Fin");
		fin.setParent(groupe);
		// = new Text(groupe, SWT.BORDER);
		fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// groupe non visible de base
		groupe.setVisible(false);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText("Injecter");

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners

		// listeners

		// radio saisie
		radio
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("saisie manuelle section " + nomSec); // 
						// raz de la valeur manuelle
						comboList.setEnabled(true);
						grou.setVisible(false);
					}
				});

		// radio position

		position
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent f) {
						System.out.println("position dans fichier : section "
								+ nomSec); // 
						// raz de la valeur manuelle

						comboList.setEnabled(false);

						grou.setVisible(true);
						// raz des valeurs injectees
						lig.setText("");
						debu.setText("");
						fi.setText("");

					}
				});

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						System.out.println("bouton injection"); // 
						// System.out.println("valeur de ligne lign==> " +
						// lign.getText());
						// System.out.println("valeur de ligne de
						// ViewEdit.ligneTest ==> " +
						// ViewEdit.ligneTest.getText());
						Utils.inject(lig, debu, fi,
								ViewFile.textAreaDescription);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionLabelText(ScrolledForm form, String nomSection,
			String description, Section section, Text manuel) {
		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final String nomSec;
		final ScrolledForm forme;

		forme = form;
		nomSec = nomSection;

		section.setParent(form);
		section = ViewEdit.toolkitEdition.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE
						| Section.COMPACT);

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});
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

		// zone d'édition
		manuel = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		section.setClient(sectionClient);

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionLabelTextInjectNbreLignes(ScrolledForm form,
			String nomSection, String description, Section section, Text manuel) {
		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final String nomSec;
		final Text manu;
		final ScrolledForm forme;

		forme = form;
		nomSec = nomSection;

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

		// zone d'édition
		manuel.setParent(sectionClient);

		// = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(sectionClient, SWT.NONE);
		buttonInject.setText("Injecter");

		section.setClient(sectionClient);

		// listener bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
						System.out.println("bouton injection nbre lignes"); // 

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void sectionTextInjectNbreLignesNbreOctets(ScrolledForm form,
			String nomSection, String description, Section section, Text manuel) {

		// creation d'une section simple avec 1 label description et une zone de
		// texte

		final String nomSec;
		final Text manu;
		final ScrolledForm forme;

		forme = form;
		nomSec = nomSection;

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

		// zone d'édition
		manuel.setParent(sectionClient);

		// = new Text(sectionClient, SWT.BORDER);
		manuel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(sectionClient, SWT.NONE);
		buttonInject.setText("Injecter");

		section.setClient(sectionClient);

		// listener bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {
						System.out.println("bouton injection nbre lignes"); // 

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeDesc(section);

	}

	public static void ajoutSectionDansListeDesc(Section section) {

		EditDesc.listeSectionsDesc.add(section);
		System.out.println("ajout section ok");

	}

}
