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
		radEOF
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
                        EditDesc.selectComboEnd(EditDesc.radEOF);
					}
				});
		
		radLigne
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
                        EditDesc.selectComboEnd(EditDesc.radLigne);
					}
				});

		radManuel
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
                        EditDesc.selectComboEnd(EditDesc.radManuel);
					}
				});
		
		// bouton inject
		btnInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
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
			@Override
            public void expansionStateChanged(ExpansionEvent e) {
				forme.reflow(true);
			}
		});

		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$
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
		buttonInject.setText(Messages.getString("Drawing.1")); //$NON-NLS-1$

		section.setClient(sectionClient);
		// listener bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent h) {

						Utils.injectNbreLignes(manu,
								ViewFile.textAreaDescription);

					}
				});

		section.pack();

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
			//EditSurvey.comboCountries.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridDataFactory.swtDefaults().hint(385, 12).applyTo(EditSurvey.comboCountries);
		}
			
		for (i = 0; i < nbre; i++) {

			// label avec le texte de "description"
			Label label = new Label(sectionClient, SWT.NULL);
			label.setText(listeNoms.get(i));

			// affichage zone texte dans la section
			listeText.get(i).setParent(sectionClient);
			//listeText.get(i).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridDataFactory.swtDefaults().hint(400, 12).applyTo(listeText.get(i));
		}
		
//		 Combo region
		if (!blnPays) {
			Label labelReg = new Label(sectionClient, SWT.NULL);
			labelReg.setText(Messages.getString("EditSurvey.18"));
			labelReg.setParent(sectionClient);
			labelReg.setLayoutData(
					new GridData(GridData.FILL_HORIZONTAL));

			EditSurvey.comboRegion.setParent(sectionClient);
			//EditSurvey.comboRegion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridDataFactory.swtDefaults().hint(385, 12).applyTo(EditSurvey.comboRegion);
			
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
		GridDataFactory.swtDefaults().hint(385, 10).applyTo(EditSurvey.comboDataCentres);
		
		// ajout de la combolist confidentialite
		Label label2 = new Label(sectionClient, SWT.NULL);
		label2.setText(Messages.getString("EditSurvey.28"));
		confidentialite.setParent(sectionClient);

		section.setClient(sectionClient);
		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeSurvey(section);
		ViewEdit.formEdition.reflow(true);

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
