package fr.ifremer.nemo.ui;

import java.text.ParseException;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Messages;

public class DrawingData {

	public static void sectionDate(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin) {

		final Text lign, debu, fi;
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

		Group groupeFormat = new Group(sectionClient, SWT.NONE);
		groupeFormat.setLayout(new GridLayout());
		groupeFormat.setText("Format");

		// combo format de l'heure
		EditData.comboDate.setParent(groupeFormat);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
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

		// combo format date
		// EditData.comboDate.setParent(groupe);

		// bouton d'injection de la position des valeurs
		Button buttonInject = new Button(groupe, SWT.NONE);
		buttonInject.setText(Messages.getString("Drawing.1"));

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;

		lign.setEnabled(true);
		debu.setEnabled(true);
		fi.setEnabled(true);
		buttonInject.setEnabled(true);

		// listeners

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign, debu, fi, ViewFile.styledTextData);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeData(section);

	}

	public static void sectionTotal(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin,
			Button buttonTestConversion, Text textTestConversion) {

		final Text lign, debu, fi;
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

		Group groupeFormat = new Group(sectionClient, SWT.NONE);
		groupeFormat.setLayout(new GridLayout());
		groupeFormat.setText("Format");

		// combo format de l'heure+date
		EditData.comboTotal.setParent(groupeFormat);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
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

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		ligne.setText("1"); // valeur bidon pour la ligne

		lign = ligne;
		debu = debut;
		fi = fin;

		lign.setEnabled(true);
		debu.setEnabled(true);
		fi.setEnabled(true);
		buttonInject.setEnabled(true);

		// listeners

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign, debu, fi, ViewFile.styledTextData);
						debu.pack();
						fi.pack();

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeData(section);

		Composite compoTest = new Composite(sectionClient, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(compoTest);
		buttonTestConversion.setParent(compoTest);
		buttonTestConversion.setText("Test");
		textTestConversion.setParent(compoTest);
		textTestConversion.setEnabled(false);
		
		buttonTestConversion.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent g) {

					EditData.textTotalLigne.setText(String.valueOf(1 + Integer.parseInt(EditDesc.lignesEnteteFichier.getText()) + Integer.parseInt(EditDesc.lignesEnteteStation.getText())) );
					Utils.test(EditData.textTestDateHeure, ViewFile.styledTextStation, EditData.textTotalLigne, EditData.textTotalDebut, EditData.textTotalFin);
					System.out.println(EditData.textTestDateHeure.getText());
				
				try {
					DateTimeFormat format = new DateTimeFormat(
							EditData.comboTotal.getText());
					
					
					
					Long DateLong = (Long) format
							.parseObject(EditData.textTestDateHeure.getText());
					
					Formatter formatter;
					
					formatter = Formatter.getSingleton();
					
					EditData.textTestDateHeure.setText(formatter.formatDateTime("yyyy MM dd  hh24mmss", DateLong));
					
					
				} catch (ParseException e) {

					Utils.messageErreurAppend(Messages
							.getString("Etape.122")); //$NON-NLS-1$
					EditData.textTestDateHeure.setText(Messages
							.getString("Etape.120"));
					EditData.textTestDateHeure.pack();
				}
				EditData.textTestDateHeure.pack();
				EditData.textTestDateHeure.getParent().pack();
			}
		});

	}

	public static void sectionHeure(String nomSection, String description,
			Section section, Text ligne, Text debut, Text fin) {

		final Text lign, debu, fi;
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

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group groupeConv = new Group(sectionClient, SWT.NONE);
		groupeConv.setText("Conversion");
		groupeConv.setLayout(gridLayout);

		// zone de conversion et test de conversion
		Label labelConv = new Label(groupeConv, SWT.NONE);
		labelConv.setText("Formule");
		EditData.textHeureConversion.setParent(groupeConv);

		Label labelTest = new Label(groupeConv, SWT.NONE);
		labelTest.setText("Test");
		EditData.textHeureTest.setParent(groupeConv);

		Group groupeFormat = new Group(sectionClient, SWT.NONE);
		groupeFormat.setLayout(new GridLayout());
		groupeFormat.setText("Format");

		// combo format de l'heure
		EditData.comboHeure.setParent(groupeFormat);

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 6;
		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
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

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners
		lign = ligne;
		debu = debut;
		fi = fin;

		lign.setEnabled(true);
		debu.setEnabled(true);
		fi.setEnabled(true);
		buttonInject.setEnabled(true);

		// listeners

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {

						Utils.inject(lign, debu, fi, ViewFile.styledTextData);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeData(section);

	}

	public static void sectionTypeCourant(Section section, Text code,
			Text unite, Text conversion, Text debut, Text fin, Text format,
			Text entree, Text sortie) {
		final Text debu, fi;

		// manu = manuel;

		section.setParent(EditData.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		// System.out.println("section type courant DrawingData");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText("Gérer les Types existants              ");
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section
				.setDescription("Consultez, modifiez ou supprimez les types de données en les sélectionnant dans la liste de gauche...");
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;

		// code
		Label label1 = new Label(sectionClient, SWT.NULL);
		label1.setText("Code");
		code.setParent(sectionClient);
		code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// unité
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText("Unité");
		unite.setParent(sectionClient);
		unite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// conversion
		Label label5 = new Label(sectionClient, SWT.NULL);
		label5.setText("Formule de conversion");
		conversion.setParent(sectionClient);
		conversion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// format
		Label label6 = new Label(sectionClient, SWT.NULL);
		label6.setText("Format");
		format.setParent(sectionClient);
		format.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
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

		// bouton supprimer / modifier
		Button buttonModifier = new Button(sectionClient, SWT.NONE);
		buttonModifier.setText("Modifier");

		Button buttonSupprimer = new Button(sectionClient, SWT.NONE);
		buttonSupprimer.setText("Supprimer");

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners

		debu = debut;
		fi = fin;

		// listeners

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						// System.out.println("bouton injection");

						Utils.inject(EditData.textTempo, debu, fi,
								ViewFile.styledTextData);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeData(section);

	}

	public static void sectionTypeAjouter(Section section, Text code,
			Text unite, Text conversion, Text debut, Text fin, Text format,
			Text entree, Text sortie) {
		final Text debu, fi;

		section.setParent(EditData.formEdition);

		section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition
				.getBody(), Section.DESCRIPTION | Section.TITLE_BAR
				| Section.TWISTIE | Section.COMPACT);

		// System.out.println("section type ajouter DrawingData");

		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				EditDesc.formEdition.reflow(true);
			}
		});
		section.setText("Nouveau Type                           ");
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		section.setFont(font);
		section
				.setDescription("Créez ici un nouveau type de données en entrant ses différents paramètres ainsi que sa position...");
		Composite sectionClient = ViewEdit.toolkitEdition
				.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;

		// code
		Label label1 = new Label(sectionClient, SWT.NULL);
		label1.setText("Code");
		code.setParent(sectionClient);
		code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// unité
		Label label4 = new Label(sectionClient, SWT.NULL);
		label4.setText("Unité");
		unite.setParent(sectionClient);
		unite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// conversion
		Label label5 = new Label(sectionClient, SWT.NULL);
		label5.setText("Formule de conversion");
		conversion.setParent(sectionClient);
		conversion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// format
		Label label6 = new Label(sectionClient, SWT.NULL);
		label6.setText("Format");
		format.setParent(sectionClient);
		format.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group groupe = new Group(sectionClient, SWT.NONE);
		groupe.setText(Messages.getString("Drawing.7"));
		groupe.setLayout(gridLayout1);
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

		// bouton supprimer / modifier
		Button buttonModifier = new Button(sectionClient, SWT.NONE);
		buttonModifier.setText("Confirmer création");

		section.setClient(sectionClient);

		// une fois les éléments initialisés, on les passe en static pour y
		// accéder à
		// partir des listeners

		debu = debut;
		fi = fin;

		// listeners

		// bouton inject
		buttonInject
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						// System.out.println("bouton injection");

						// le textTempo c'est pour simuler une variable ligne
						// dont on n'a pas besoin ici!
						Utils.inject(EditData.textTempo, debu, fi,
								ViewFile.styledTextData);

					}
				});

		// ajout de la section dans la liste des sections de description
		ajoutSectionDansListeData(section);

	}

	public static void ajoutSectionDansListeData(Section section) {

		EditData.listeSectionsData.add(section);
		// System.out.println("ajout section ok");

	}

}
