package fr.ifremer.nemo.ui;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class EditDesc {

///////////////////////////////////////////

public static Section sectionEnteteFichier, sectionEnteteStation, sectionFinStation, sectionDate, sectionHeure, sectionDonnees, sectionRuptureDonnees, sectionTest, sectionTest2 = null;
public static Text lignesEnteteFichier, lignesEnteteStation, lignesFinStation, octetsEnteteStationLigne, octetsEnteteStationDebut, octetsEnteteStationFin, enteteDonnees, upletDonnees, finDonnees, indicFinDonnees, ruptureDonneesNoLigne, ruptureDonneesDebut, ruptureDonneesFin, ligneNbreMesures, debutNbreMesures, finNbreMesures, ligneTest, debutTest, finTest, manuelTest, manuelTest2, nbreLignesMesures = null;
public static Label labelEnteteFichier, labelEnteteStation, labelFinStation, labelDonneesEntete, labelDonneesUplet, labelDonneesFinDonnees, labelRuptureIndic, labelRuptureDonneesNoLigne, labelRuptureDonneesDebut, labelRuptureDonneesFin = null;
public static Button buttonRAZ, buttonExpandDesc, buttonExpandDesc2, buttonValidateDesc, buttonLignesEnteteStation, buttonIndicFinDonnees, buttonIndicFinDonneesPosition;
public static Group groupRuptureDonnees;
public static Combo comboIndicRuptureDonnees;
public static ArrayList <Text> listeTextDesc;
public static ArrayList <Section> listeSectionsDesc;
public static int expand, validee, loaded;
//expand : savoir si sections ouvertes ou non
//validee : si etape validee
//loaded : si l'étape est chargée et affichée
public static ScrolledForm formEdition;

////////////////////////////////////////////
	

	public static void affichageEdition(){
		// initialisation zone édition pour étape description de fichier 
	
		if (ViewFile.fileLoaded){
			
		formEdition = ViewEdit.formEdition;
		
		//raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$
				
		//de base les sections sont refermées
		expand = 0;
		
		//flags de toutes les étapes à 0
		EditDesc.validee = 0;
		EditSurvey.validee = 0;
		EditStation.validee = 0;
		EditData.validee = 0;
		EditConversion.validee = 0;
		
		loaded = 1;
		
		
		/* recreer un formEdition sur le toolkit principal
		 * pour pouvoir faire un dispose() dessus mais garder toutes les sections 
		 * afin de les réafficher par la suite dans DrawingBack.
		 */
		
		
		
		//ViewEdit.formEdition.dispose();
		
		//formEdition = ViewEdit.toolkitEdition.createScrolledForm(ViewEdit.top);
		GridLayout gridlayout = new GridLayout();
		  gridlayout.numColumns = 1;
		  formEdition.getBody().setLayout(gridlayout);
		
		formEdition.setText(Messages.getString("EditDesc.descriptiondeFichier")); //$NON-NLS-1$
		System.out.println("ok editDesc"); //$NON-NLS-1$
	
		
	
		
//		init de la arrayList des sections
		listeSectionsDesc = new ArrayList <Section> ();
		
//		init de la arraylist des textes
		listeTextDesc = new ArrayList <Text> ();
		
		//init composants IHM
		initSections();
		initText();
		initBoutons();
		initCombos();
		
		
		

		
		
		//boutons collapse all / expand all
		buttonExpandDesc.setParent(formEdition.getBody());
		buttonExpandDesc.setText("+"); //$NON-NLS-1$
		
		//bouton RAZ
		buttonRAZ.setParent(formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$
		
//		bouton "valider étaper"
		buttonValidateDesc.setParent(formEdition.getBody());
		buttonValidateDesc.setText(Messages.getString("EditDesc.validerLetape")); //$NON-NLS-1$
		
		formEdition.redraw();
		formEdition.reflow(true);
		//formEdition.
		
		System.out.println("ok editDesc 2"); //$NON-NLS-1$
		
	// initialisation   liste des sections de la partie description fichier
		

//section entete fichier 
Drawing.sectionLabelTextInjectNbreLignes(formEdition, Messages.getString("EditDesc.enteteFichier"), Messages.getString("EditDesc.surlignezLignes"), sectionEnteteFichier, lignesEnteteFichier); //$NON-NLS-1$ //$NON-NLS-2$


//section entete station
//Drawing.sectionRadioManuelRadioSimpleInjectLigneDebutFinInject(formEdition, "Entête Station                         ", "Donnez le nombre de lignes de l'entête station ou précisez que le nombre d'octets est présent sur la première ligne de données", sectionEnteteStation, lignesEnteteStation , buttonLignesEnteteStation, "Nombre de lignes", "Nombre octets sur 1ere ligne données", octetsEnteteStationLigne, octetsEnteteStationDebut, octetsEnteteStationFin);
Drawing.sectionLabelTextInjectNbreLignes(formEdition,Messages.getString("EditDesc.enteteStation"),Messages.getString("EditDesc.nbreLignesEntete"), sectionEnteteStation, lignesEnteteStation); //$NON-NLS-1$ //$NON-NLS-2$



//section fin de station
Drawing.sectionLabelTextInjectNbreLignes(formEdition,Messages.getString("EditDesc.finStation"),Messages.getString("EditDesc.nbreLignesFinStation"), sectionFinStation, lignesFinStation); //$NON-NLS-1$ //$NON-NLS-2$

//Drawing.sectionManuelLigneDebutFinInject("test", "blabla description", sectionTest, enteteDonnees, upletDonnees, finDonnees, indicFinDonnees, buttonLignesEnteteStation);

//section rupture données + remplissage du combo list
//Drawing.sectionComboLigneDebutFinInject(formEdition,"Indicateur de Fin de Données           ", "Indiquez ici quel est l'indicateur de rupture de données, ou surlignez les lignes de mesures puis cliquez sur injecter", sectionRuptureDonnees, ruptureDonneesNoLigne, ruptureDonneesDebut, ruptureDonneesFin, buttonIndicFinDonnees, comboIndicRuptureDonnees);
Drawing.sectionComboInjectNbreLignes(formEdition, Messages.getString("EditDesc.indicFinDonnees"), Messages.getString("EditDesc.indiquezIndicFinDonnees"), sectionRuptureDonnees, nbreLignesMesures, buttonIndicFinDonnees, comboIndicRuptureDonnees); //$NON-NLS-1$ //$NON-NLS-2$
//comboIndicRuptureDonnees.add("");
comboIndicRuptureDonnees.add(Messages.getString("EditDesc.ligneVide")); //$NON-NLS-1$
comboIndicRuptureDonnees.add("EOF"); //$NON-NLS-1$
comboIndicRuptureDonnees.add("END"); //$NON-NLS-1$


//Drawing.sectionLabelText("automatique","numéro de ligne", sectionTest2, indicFinDonnees);											  

//boutons collapse all / expand all
buttonExpandDesc2.setParent(formEdition.getBody());
buttonExpandDesc2.setText("+"); //$NON-NLS-1$

//listeners

buttonExpandDesc
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("expand/collapse all"); // TODO Auto-generated Event stub widgetSelected() //$NON-NLS-1$
		//raz de la valeur manuelle
		int i;
		
		//Shell s = new Shell();
		
		System.out.println("expand ? = " + expand); //$NON-NLS-1$
		for (i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){
			
			if (expand == 0){ 
			EditDesc.listeSectionsDesc.get(i).setExpanded(true);
			//EditDesc.listeSectionsDesc.get(i).setParent(s);
			EditDesc.listeSectionsDesc.get(i).pack();
			} 
			else{
				EditDesc.listeSectionsDesc.get(i).setExpanded(false);
				//EditDesc.listeSectionsDesc.get(i).setParent(formEdition.getBody());
				

			} 
			
		}
		if (expand == 0) {
			System.out.println("expand passe à 1"); //$NON-NLS-1$
			
			expand = 1;
			buttonExpandDesc.setText("-"); //$NON-NLS-1$
			buttonExpandDesc2.setText("-");} //$NON-NLS-1$
		else{expand = 0;
		System.out.println("expand passe à 0"); //$NON-NLS-1$
		buttonExpandDesc.setText("+"); //$NON-NLS-1$
		buttonExpandDesc2.setText("+"); //$NON-NLS-1$
		} 
		
		//refresh global
		formEdition.reflow(true);
		
	}
});
												  

buttonExpandDesc2
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("expand/collapse all"); // TODO Auto-generated Event stub widgetSelected() //$NON-NLS-1$
		//raz de la valeur manuelle
		int i;
		
		//Shell s = new Shell();
		
		System.out.println("expand ? = " + expand); //$NON-NLS-1$
		for (i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){
			
			if (expand == 0){ 
			EditDesc.listeSectionsDesc.get(i).setExpanded(true);
			//EditDesc.listeSectionsDesc.get(i).setParent(s);
			} 
			else{
				EditDesc.listeSectionsDesc.get(i).setExpanded(false);
				//EditDesc.listeSectionsDesc.get(i).setParent(formEdition.getBody());
				

			}
			
		}
		if (expand == 0) {
			System.out.println("expand passe à 1"); //$NON-NLS-1$
			
			expand = 1;
			buttonExpandDesc2.setText("-"); //$NON-NLS-1$
			buttonExpandDesc.setText("-");} //$NON-NLS-1$
		else{expand = 0;
		System.out.println("expand passe à 0"); //$NON-NLS-1$
		buttonExpandDesc2.setText("+"); //$NON-NLS-1$
		buttonExpandDesc.setText("+"); //$NON-NLS-1$
		} 
		
		//refresh global
		formEdition.reflow(true);
		
	}
});


buttonValidateDesc
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("validation"); // TODO Auto-generated Event stub widgetSelected() //$NON-NLS-1$
		
		//on passe en ppar l'étape à laquelle on se trouve
		Etape.etape(0);	
		
		
		//TODO remettre validation normale
		//razEdition();
		
		//EditData.affichageEdition();
		//EditStation.affichageEdition();
		Etape.validationDesc();
		

	}
});

buttonRAZ
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("RAZ"); // TODO Auto-generated Event stub widgetSelected() //$NON-NLS-1$
		
		razText(listeTextDesc);
		

	}
});
		  
//	refresh du formulaire
 formEdition.reflow(true);
	
//affichage courant
ViewFile.affiche = 0;

//remplissage de l'IHM par le modèle chargé ?
if (DataModel.ouverture){
	DataModel.fillDesc();
}


//nom de l'application
nomNemo();


		}

}
	
	
	public static void razEdition(){
		// reset de toute la zone d'édition après étape descriptioon de fichier
		//on retire toutes les sections et les boutons expand et validation
				
	int i;
		
	Shell s = new Shell();
	
		//effacement boutons expand
		buttonExpandDesc.setParent(s);
		buttonExpandDesc2.setParent(s);
		buttonRAZ.setParent(s);
		
		//effacement bouton valider etape
		buttonValidateDesc.setParent(s);
		
		System.out.println("expand ? = " + expand); //$NON-NLS-1$
		for (i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){
			
			
			EditDesc.listeSectionsDesc.get(i).setExpanded(true);
			EditDesc.listeSectionsDesc.get(i).setParent(s);
			System.out.println("effacement zone edition etape description fichier ==> ok"); //$NON-NLS-1$
			}
	
		
		
		//effacement titre formulaire
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$
		
		//refresh global
		ViewEdit.formEdition.reflow(true);
				
	}
	
	
	public static void DrawingOuvertureModele(){
		//on initialise tous les composants IHM en vue du remplissage par le dataModel chargé
		
		
		
		
	}
	
	
	public static void DrawingBack(){
		// reset de toute la zone d'édition après étape description de fichier
		//on retire toutes les sections et les boutons expand et validation
				
	int i;

	nomNemo();
	
//	raz message erreur
	Utils.messageErreur(""); //$NON-NLS-1$
	
		ScrolledForm s;
		s = ViewEdit.formEdition;
		//affichage boutons expand
		buttonExpandDesc.setParent(s.getBody());
		buttonRAZ.setParent(s.getBody());
		
		//affichage bouton valider etape
		buttonValidateDesc.setParent(s.getBody());
		
		System.out.println("expand ? = " + expand); //$NON-NLS-1$
		for (i=0;i<=EditDesc.listeSectionsDesc.size()-1;i++){
			
			
			EditDesc.listeSectionsDesc.get(i).setExpanded(false);
			EditDesc.listeSectionsDesc.get(i).setParent(s.getBody());
			System.out.println("Affichage section etape description fichier ==> ok"); //$NON-NLS-1$
			}
	
		buttonExpandDesc2.setParent(s.getBody());
		
		//effacement titre formulaire
		ViewEdit.formEdition.setText(Messages.getString("EditDesc.descriptionFichier")); //$NON-NLS-1$
		
		//refresh global
		ViewEdit.formEdition.reflow(true);
		
//		affichage courant
		ViewFile.affiche = 0;
				
	}
	
	
public static void initSections(){
		

	Shell shell = new Shell();	
	sectionEnteteFichier = new Section(shell, SWT.NONE);
	 sectionEnteteStation = new Section(shell, SWT.NONE);
	 sectionFinStation= new Section(shell, SWT.NONE);
	 sectionDate= new Section(shell, SWT.NONE);
	 sectionHeure= new Section(shell, SWT.NONE);
	 sectionDonnees= new Section(shell, SWT.NONE);
	 sectionRuptureDonnees= new Section(shell, SWT.NONE);
	 sectionTest= new Section(shell, SWT.NONE);
	 sectionTest2= new Section(shell, SWT.NONE);
	 
	System.out.println("init section ok"); //$NON-NLS-1$
	}
	
	
public static void initText(){
	
	Shell shell = new Shell();
	lignesEnteteFichier = new Text(shell, SWT.BORDER);listeTextDesc.add(lignesEnteteFichier);
	lignesEnteteStation = new Text(shell, SWT.BORDER);listeTextDesc.add(lignesEnteteStation);
	octetsEnteteStationLigne = new Text(shell, SWT.BORDER); listeTextDesc.add(octetsEnteteStationLigne);
	octetsEnteteStationDebut = new Text(shell, SWT.BORDER);listeTextDesc.add(octetsEnteteStationDebut);
	octetsEnteteStationFin = new Text(shell, SWT.BORDER);listeTextDesc.add(octetsEnteteStationFin);
	lignesFinStation = new Text(shell, SWT.BORDER);listeTextDesc.add(lignesFinStation);
	enteteDonnees = new Text(shell, SWT.BORDER);listeTextDesc.add(enteteDonnees);
	upletDonnees = new Text(shell, SWT.BORDER);listeTextDesc.add(upletDonnees);
	finDonnees = new Text(shell, SWT.BORDER);listeTextDesc.add(finDonnees);
	indicFinDonnees = new Text(shell, SWT.BORDER);listeTextDesc.add(indicFinDonnees);
	ruptureDonneesNoLigne = new Text(shell, SWT.BORDER);listeTextDesc.add(ruptureDonneesNoLigne);
	ruptureDonneesDebut = new Text(shell, SWT.BORDER);listeTextDesc.add(ruptureDonneesDebut);
	ruptureDonneesFin = new Text(shell, SWT.BORDER);listeTextDesc.add(ruptureDonneesFin);
	ligneNbreMesures  = new Text(shell, SWT.BORDER);listeTextDesc.add(ligneNbreMesures);
	debutNbreMesures  = new Text(shell, SWT.BORDER);listeTextDesc.add(debutNbreMesures);
	finNbreMesures  = new Text(shell, SWT.BORDER);listeTextDesc.add(finNbreMesures);
	nbreLignesMesures = new Text(shell, SWT.BORDER);listeTextDesc.add(nbreLignesMesures);
	
	//remplissage de la liste des textes

}	
	
public static void initBoutons(){
	
	Shell shell = new Shell();
	buttonRAZ = new Button(shell, SWT.NONE);
	buttonLignesEnteteStation = new Button(shell, SWT.RADIO);
	buttonIndicFinDonnees = new Button(shell, SWT.RADIO);
	buttonIndicFinDonneesPosition = new Button(shell, SWT.RADIO);
	buttonExpandDesc = new Button(shell, SWT.NONE);
	buttonExpandDesc2 = new Button(shell, SWT.NONE);
	buttonValidateDesc = new Button(shell, SWT.NONE);
	
}
	

public static void initCombos(){
	
	Shell shell = new Shell();
	comboIndicRuptureDonnees = new Combo(shell, SWT.NONE);
	
	
}

public static void razText(ArrayList <Text> liste){
	
	for (int i =0; i< liste.size(); i++){
		liste.get(i).setText(""); //$NON-NLS-1$
				
	}
	
}


public static void nomNemo(){
//	nom de l'application
	if (ViewFile.dossierCampagne){
		//dossier campagne
		
		System.out.println(">>>>"+ViewFile.nomRepertoire); //$NON-NLS-1$
		File fich = new File(ViewFile.nomRepertoire);
		if (DataModel.ouverture){
			ApplicationWorkbenchWindowAdvisor.configurer_titre.setTitle(Messages.getString("EditDesc.nemoDossier")+fich.getName()+Messages.getString("EditDesc.modele")+OpenAction.nomFichierModele+" ]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			
		}else{
			ApplicationWorkbenchWindowAdvisor.configurer_titre.setTitle(Messages.getString("EditDesc.dossier")+fich.getName()+" ]");			 //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		
	}else{
		//fichier campagne
		File fich;
		fich = new File(ViewFile.nomFichier);
				
		if (DataModel.ouverture){
			ApplicationWorkbenchWindowAdvisor.configurer_titre.setTitle(Messages.getString("EditDesc.fichier")+fich.getName()+Messages.getString("EditDesc.modele")+OpenAction.nomFichierModele+" ]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}else{
			ApplicationWorkbenchWindowAdvisor.configurer_titre.setTitle(Messages.getString("EditDesc.fichier")+fich.getName()+" ]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	
}


}
