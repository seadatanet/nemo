package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditData {

	// fonctions de remplissage de la zone d'edition pour les campagnes

	// /////////////////////////////////////////

	public static Section sectionTypeCourant, sectionTypeAjouter;

	public static Text textTypeCourantCode, textTypeCourantUnite,
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

	public static Button buttonRAZ, buttonReset, buttonValidate;

	public static ArrayList<Section> listeSectionsData, listeSectionsParam;

	public static ArrayList<String> listeSectionsNoms, listeTypeDonnees;

	public static ArrayList<Label> listeSectionsLabel;

	public static ArrayList<Text> listeTextData;

	public static boolean validee, loaded;

	public static List listeType;

	public static Boolean okApresReset;

	public static ScrolledForm formEdition;

	private static Shell shell = new Shell();
	// //////////////////////////////////////////

	public static void affichageEdition() {
		// initialisation zone edition pour etape description de fichier

		formEdition = ViewEdit.formEdition;
		ViewLog.aideData();
		
		okApresReset = true;

		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		// affichage du texte dans la zone de gauche

		ViewFile.styledTextData.setText(Utils.texteBrut);

		loaded = true;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * reafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditData.8")); //$NON-NLS-1$

		disposeElements();
		
		listeTextData = new ArrayList<Text>();
		
		// init composants IHM
		initSections();
		initText();
		initBoutons();
		initListeType();

		// init de la arrayList des sections
		listeSectionsData = new ArrayList<Section>();
		listeSectionsNoms = new ArrayList<String>();

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditData.3")); //$NON-NLS-1$

		// reset
		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$

		formEdition.reflow(true);
		
		ViewFile.affiche = 3;
		ViewEdit.formEdition.reflow(true);

		buttonValidate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// on passe en ppar l'etape e laquelle on se trouve
						// Etape.etape(1);
                        // reinitialisation de la conversion
                        if (DataModel.inputModel != null) DataModel.inputModel.setConverted(false);


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
		
		// remplissage de l'IHM par le modele charge
		
		// DataModel.ouverture);

		if (DataModel.ouverture) {
			DataModel.fillData();
		}
	}

	public static void razEdition() {
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		int i;

		// effacement des boutons valider/expand/init serveur
		EditData.buttonValidate.setParent(shell);
		EditData.buttonRAZ.setParent(shell);

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

					// des qu'on modifie une valeur, on invalide l'etape
					invalidEtape();
                    Utils.messageErreur(Global.EMPTY_STRING);

				}
			});

		}
	}

	public static void DrawingBack() {
		// reset de toute la zone d'edition apres etape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideData();
		int i;

		ScrolledForm s;
		s = ViewEdit.formEdition;
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$
		// effacement des boutons valider/expand/init serveur

		EditData.buttonValidate.setParent(s.getBody());
		EditData.buttonRAZ.setParent(s.getBody());

		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setExpanded(false);

			EditData.listeSectionsData.get(i).setParent(s.getBody());

		}

		ViewEdit.formEdition.setText(Messages.getString("EditData.20")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 3;

	}

	public static void initSections() {
		sectionTypeCourant = new Section(shell, SWT.NONE);
		sectionTypeAjouter = new Section(shell, SWT.NONE);
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

		// remplissage de la liste des textes et ajout listeners
		ajoutModifyListenerText(listeTextData);

	}

	public static void initBoutons() {
		buttonValidate = new Button(shell, SWT.NONE);
		buttonReset = new Button(shell, SWT.NONE);
		buttonRAZ = new Button(shell, SWT.NONE);
	}

	public static void initListeType() {

		listeType = new List(shell, SWT.BORDER);

	}

	public static void razText(ArrayList<Text> liste) {

		for (int i = 0; i < liste.size(); i++) {
			liste.get(i).setText(""); //$NON-NLS-1$

		}

		// raz de la table de parameters
		// effacement de la liste des types de donnees dans la table
		TableData.types = null;
		TableData.types = new ArrayList<TypeData>();
		// razTableData
		TableData.razTableData();

		// refresh de la table
		// initTable();
		TableData.creationTable();
		
        invalidEtape();
		Utils.messageErreur(Global.EMPTY_STRING);
	}

	private static void disposeElements()
	{
		shell.close();
		
		shell = new Shell();

		if (sectionTypeCourant != null) {
			sectionTypeCourant.dispose();
			sectionTypeAjouter.dispose();
		}

		if (listeTextData != null) {
			for (Text element : listeTextData) {
				element.dispose();
			}
		}
		
		if (listeType != null)
		{
			listeType.dispose();
		}
	}


    public static void invalidEtape(){
        okApresReset = false;
        ViewFile.tabItem3.setText(Messages.getString("EditData.1"));         //$NON-NLS-1$

        // reinitialisation de la conversion
        if (DataModel.inputModel != null) DataModel.inputModel.setConverted(false);
    }
}
