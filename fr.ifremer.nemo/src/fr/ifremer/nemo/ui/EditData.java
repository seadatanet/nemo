package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.util.Messages;

public class EditData {

	// fonctions de remplissage de la zone d'édition pour les campagnes

	// /////////////////////////////////////////

	public static Section sectionTypeCourant, sectionTypeAjouter, sectionHeure,
			sectionDate, sectionTotal;

	public static Text textTotalLigne, textTotalDebut, textTotalFin;

	public static Text textTempo, textTypeCourantCode, textTypeCourantUnite,
			textTypeCourantConversion, textTypeCourantDebut,
			textTypeCourantFin, textTypeCourantFormat, textTypeCourantEntree,
			textTypeCourantSortie, textTypeCourantLigne, textTypeAjouterCode,
			textTypeAjouterUnite, textTypeAjouterConversion,
			textTypeAjouterDebut, textTypeAjouterFin, textTypeAjouterLigne,
			textTypeAjouterEntree, textTypeAjouterSortie,
			textTypeAjouterFormat;


	public static Label labelTypeCourantCode, labelTypeCourantUnite,
			labelTypeCourantConversion, labelTypeCourantDebut,
			labelTypeCourantFin, labelTypeCourantFormat,
			labelTypeCourantEntree, labelTypeCourantSortie;

	public static Button buttonRAZ, buttonReset, buttonValidate, buttonExpand,
			buttonExpand2, buttonLigneDonnees, buttonFreqEchant;

	public static ArrayList<Section> listeSectionsData, listeSectionsParam;

	public static ArrayList<String> listeSectionsNoms, listeTypeDonnees;

	public static ArrayList<Label> listeSectionsLabel;

	public static ArrayList<Text> listeTextData;

	public static int expand, validee, loaded;

	public static List listeType;

	public static Boolean okApresReset;

	public static ScrolledForm formEdition;

	public static Combo comboHeure, comboDate, comboTotal;
	
	public static Button buttonTestDateHeure;
	
	public static Text textTestDateHeure;

	private static Shell shell = new Shell();
	// //////////////////////////////////////////

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier

		formEdition = ViewEdit.formEdition;
		ViewLog.aideData();
		
		okApresReset = true;

		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		// affichage du texte dans la zone de gauche

		ViewFile.styledTextData.setText(Utils.texteBrut);

		loaded = 1;

		// de base les sections sont refermées
		expand = 0;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * réafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditData.1")); //$NON-NLS-1$

		disposeElements();
		
		listeTextData = new ArrayList<Text>();
		
		// init composants IHM
		initSections();
		initCombos();
		initText();
		initBoutons();
		initListeType();

		// init de la arrayList des sections
		listeSectionsData = new ArrayList<Section>();
		listeSectionsNoms = new ArrayList<String>();

		// boutons collapse all / expand all
		// buttonExpand.setParent(ViewEdit.formEdition.getBody());
		// buttonExpand.setText("+");

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditData.3")); //$NON-NLS-1$

		// reset
		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$

		if (ViewFile.checkBoxTempo.getSelection()) {
			// série temporelle
			buttonFreqEchant.setParent(ViewEdit.formEdition.getBody());
			buttonFreqEchant.setText(Messages.getString("EditData.4")); //$NON-NLS-1$
			buttonLigneDonnees.setParent(ViewEdit.formEdition.getBody());
			buttonLigneDonnees.setText(Messages.getString("EditData.5")); //$NON-NLS-1$

			// on cree les sections puis on les cache dans un autre shell et on
			// les re-affiche only si bouton coché


			DrawingData
					.sectionTotal(
							Messages.getString("EditData.6"), Messages.getString("EditData.7"), sectionTotal, textTotalLigne, textTotalDebut, textTotalFin, buttonTestDateHeure,textTestDateHeure ); //$NON-NLS-1$ //$NON-NLS-2$

			buttonFreqEchant.setSelection(false);
			buttonLigneDonnees.setSelection(true);

			// sections désactivées de base
			test();

			sectionTotal.setEnabled(true);

			formEdition.redraw();
			formEdition.reflow(true);

		}

		formEdition.reflow(true);

		buttonExpand2.setText("+"); //$NON-NLS-1$
		
		ViewFile.affiche = 3;
		ViewEdit.formEdition.reflow(true);

		// listeners sur les boutons radio
		buttonFreqEchant
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						test();

						formEdition.reflow(true);

					}
				});

		buttonLigneDonnees
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						test2();
						formEdition.reflow(true);
					}
				});

		// listeners

		buttonExpand
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						int i;

						// System.out.println("expand ? = " + expand);
						for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

							if (expand == 0) {
								EditData.listeSectionsData.get(i).setExpanded(
										true);

							} else {
								EditData.listeSectionsData.get(i).setExpanded(
										false);

							}

						}
						if (expand == 0) {
							// System.out.println("expand passe à 1");
							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$

							expand = 1;
						} else {
							expand = 0;
							// System.out.println("expand passe à 0");
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

						int i;

						// System.out.println("expand ? = " + expand);
						for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

							if (expand == 0) {
								EditData.listeSectionsData.get(i).setExpanded(
										true);

							} else {
								EditData.listeSectionsData.get(i).setExpanded(
										false);

							}

						}
						if (expand == 0) {
							// System.out.println("expand passe à 1");
							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$

							expand = 1;
						} else {
							expand = 0;
							// System.out.println("expand passe à 0");
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

						Etape.validationData();

						formEdition.redraw();
						formEdition.reflow(true);

					}
				});

		buttonRAZ
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("RAZ"); //

						razText(listeTextData);

					}
				});

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// refresh du formulaire

		;
		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 3;
		
		// remplissage de l'IHM par le modèle chargé
		
		// DataModel.ouverture);

		if (DataModel.ouverture) {
			DataModel.fillData();
		}

	}

	public static void test() {
		int i;

		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setEnabled(false); // Parent(s);

		}

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void test2() {
		int i;

		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setEnabled(true); // Parent(s);

		}

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void razEdition() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		int i;


		// effacement des boutons valider/expand/init serveur
		EditData.buttonExpand.setParent(shell);
		EditData.buttonExpand2.setParent(shell);
		EditData.buttonValidate.setParent(shell);
		EditData.buttonRAZ.setParent(shell);

		buttonFreqEchant.setParent(shell);
		buttonLigneDonnees.setParent(shell);

		// System.out.println("expand ? = " + expand);
		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setParent(shell);

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

		ViewLog.aideData();
		int i;

		ScrolledForm s;
		s = ViewEdit.formEdition;
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$
		// effacement des boutons valider/expand/init serveur
		// EditData.buttonExpand.setParent(s.getBody());

		EditData.buttonValidate.setParent(s.getBody());
		EditData.buttonRAZ.setParent(s.getBody());

		if (ViewFile.checkBoxTempo.getSelection()) {
			buttonFreqEchant.setParent(s.getBody());
			buttonLigneDonnees.setParent(s.getBody());
		}

		// System.out.println("expand ? = " + expand);
		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setExpanded(false);

			EditData.listeSectionsData.get(i).setParent(s.getBody());

		}

		// EditData.buttonExpand2.setParent(s.getBody());

		ViewEdit.formEdition.setText(Messages.getString("EditData.20")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 3;

	}

	public static void initSections() {


		sectionTypeCourant = new Section(shell, SWT.NONE);
		sectionTypeAjouter = new Section(shell, SWT.NONE);
		sectionHeure = new Section(shell, SWT.NONE);
		sectionDate = new Section(shell, SWT.NONE);
		sectionTotal = new Section(shell, SWT.NONE);
	}

	public static void initText() {


		textTypeCourantCode = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantCode);
		textTypeCourantConversion = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantConversion);
		textTypeCourantLigne = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantLigne);
		textTypeCourantDebut = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantDebut);
		textTypeCourantEntree = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantEntree);
		textTypeCourantFin = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantFin);
		textTypeCourantFormat = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantFormat);
		textTypeCourantSortie = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantSortie);
		textTypeCourantUnite = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeCourantUnite);

		textTypeAjouterCode = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterCode);
		textTypeAjouterConversion = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterConversion);
		textTypeAjouterLigne = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterLigne);
		textTypeAjouterDebut = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterDebut);
		textTypeAjouterEntree = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterEntree);
		textTypeAjouterFin = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterFin);
		textTypeAjouterFormat = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterFormat);
		textTypeAjouterSortie = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterSortie);
		textTypeAjouterUnite = new Text(shell, SWT.BORDER);
		listeTextData.add(textTypeAjouterUnite);


		textTotalLigne = new Text(shell, SWT.BORDER);
		listeTextData.add(textTotalLigne);
		textTotalDebut = new Text(shell, SWT.BORDER);
		listeTextData.add(textTotalDebut);
		textTotalFin = new Text(shell, SWT.BORDER);
		listeTextData.add(textTotalFin);

		textTempo = new Text(shell, SWT.BORDER);
		listeTextData.add(textTempo);
		
		textTestDateHeure = new Text(shell, SWT.BORDER);

		// remplissage de la liste des textes et ajout listeners
		ajoutModifyListenerText(listeTextData);

	}

	public static void initBoutons() {


		buttonExpand = new Button(shell, SWT.NONE);
		buttonExpand2 = new Button(shell, SWT.NONE);
		buttonValidate = new Button(shell, SWT.NONE);
		buttonReset = new Button(shell, SWT.NONE);
		buttonRAZ = new Button(shell, SWT.NONE);
		buttonLigneDonnees = new Button(shell, SWT.RADIO);
		buttonFreqEchant = new Button(shell, SWT.RADIO);
		
		buttonTestDateHeure = new Button(shell, SWT.PUSH);
	}

	public static void initListeType() {

		listeType = new List(shell, SWT.BORDER);

	}

	public static void initCombos() {


		comboHeure = new Combo(shell, SWT.NONE);
		comboHeure.add("hh:mm PM"); //$NON-NLS-1$
		comboHeure.add("hh:mm:ss PM"); //$NON-NLS-1$
		comboHeure.add("hh24:mm"); //$NON-NLS-1$
		comboHeure.add("hh24:mm:ss"); //$NON-NLS-1$
		comboHeure.add("hh24mm"); //$NON-NLS-1$
		comboHeure.add("hh24mmss"); //$NON-NLS-1$
		comboHeure.add("hh24 mm"); //$NON-NLS-1$
		comboHeure.add("hh24:cc"); //$NON-NLS-1$
		comboHeure.add("ttttt"); //$NON-NLS-1$

		comboDate = new Combo(shell, SWT.NONE);
		comboDate.add("DD/MM/YY"); //$NON-NLS-1$
		comboDate.add("DD/MM/YYYY"); //$NON-NLS-1$
		comboDate.add("DDMMYY"); //$NON-NLS-1$
		comboDate.add("DDMMYYYY"); //$NON-NLS-1$
		comboDate.add("MMM DD YYYY"); //$NON-NLS-1$
		comboDate.add("YY/MM/DD"); //$NON-NLS-1$
		comboDate.add("YYMMDD"); //$NON-NLS-1$
		comboDate.add("YYYY/MM/DD"); //$NON-NLS-1$
		comboDate.add("YYYYMMDD"); //$NON-NLS-1$

		comboTotal = new Combo(shell, SWT.NONE);
		comboTotal.add("DDMMYYYY hh:mm:ss"); //$NON-NLS-1$
		comboTotal.add("DDMMYYYY hh24:mm"); //$NON-NLS-1$
		comboTotal.add("DDMMYYYY hh24mm"); //$NON-NLS-1$
		comboTotal.add("DD/MM/YYYY hh:mm:ss"); //$NON-NLS-1$
		comboTotal.add("DD/MM/YYYY hh24:mm"); //$NON-NLS-1$
		comboTotal.add("DD/MM/YYYY hh24mm"); //$NON-NLS-1$

	}

	public static void razText(ArrayList<Text> liste) {

		for (int i = 0; i < liste.size(); i++) {
			liste.get(i).setText(""); //$NON-NLS-1$

		}

		// raz de la table de parameters
		// effacement de la liste des types de données dans la table
		TableData.types = null;
		TableData.types = new ArrayList<TypeData>();
		// razTableData
		TableData.razTableData();

		// refresh de la table
		// initTable();
		TableData.creationTable();

		EditData.okApresReset = false;
		ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
		Utils.messageErreur("");

	}

	private static void disposeElements()
	{
		shell.close();
		
		shell = new Shell();

		if (sectionTypeCourant != null) {
			sectionTypeCourant.dispose();
			sectionTypeAjouter.dispose();
			sectionHeure.dispose();
			sectionDate.dispose();
			sectionTotal.dispose();
		}

		if (buttonExpand != null) {
			buttonExpand.dispose();
			buttonExpand2.dispose();
			buttonValidate.dispose();
			buttonReset.dispose();
			buttonRAZ.dispose();
			buttonLigneDonnees.dispose();
			buttonFreqEchant.dispose();
			buttonTestDateHeure.dispose();
		}

		if (listeTextData != null) {
			for (Text element : listeTextData) {
				element.dispose();
			}
		}
		if (comboDate != null) {
			comboDate.dispose();
			comboHeure.dispose();
			comboTotal.dispose();
		}
		
		if (listeType != null)
		{
			listeType.dispose();
		}
	}
	
}
