package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class EditData {

//	 fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

public static Section sectionTypeCourant, sectionTypeAjouter, sectionHeure, sectionDate, sectionTotal;
public static Text textTotalLigne, textTotalDebut, textTotalFin;
public static Text textTempo, textTypeCourantCode, textTypeCourantUnite, textTypeCourantConversion, textTypeCourantDebut, textTypeCourantFin, textTypeCourantFormat, textTypeCourantEntree, textTypeCourantSortie, textTypeCourantLigne, textTypeAjouterCode, textTypeAjouterUnite, textTypeAjouterConversion, textTypeAjouterDebut, textTypeAjouterFin, textTypeAjouterLigne, textTypeAjouterEntree, textTypeAjouterSortie, textTypeAjouterFormat;
public static Text textHeureLigne, textHeureDebut, textHeureFin, textDateLigne, textDateDebut, textDateFin, textHeureConversion, textHeureTest;
public static Label labelTypeCourantCode, labelTypeCourantUnite, labelTypeCourantConversion, labelTypeCourantDebut, labelTypeCourantFin, labelTypeCourantFormat, labelTypeCourantEntree, labelTypeCourantSortie; 
public static Button buttonRAZ, buttonReset, buttonValidate, buttonExpand, buttonExpand2, buttonLigneDonnees, buttonFreqEchant;
public static ArrayList <Section> listeSectionsData, listeSectionsParam;
public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
public static ArrayList <Label> listeSectionsLabel;
public static ArrayList <Text> listeTextData;
public static int expand, validee, loaded;
public static List listeType;
public static ScrolledForm formEdition;
public static Combo comboHeure, comboDate, comboTotal;

////////////////////////////////////////////

public static void affichageEdition(){
// initialisation zone édition pour étape description de fichier 

formEdition = ViewEdit.formEdition;

listeTextData = new ArrayList <Text>();

//raz message erreur
Utils.messageErreur("");

//affichage du texte dans la zone de gauche

//Utils.remplissageTexte(ViewFile.styledTextData);
ViewFile.styledTextData.setText(Utils.texteBrut);

loaded = 1;

//de base les sections sont refermées
expand = 0;

/* recreer un formEdition sur le toolkit principal
* pour pouvoir faire un dispose() dessus mais garder toutes les sections 
* afin de les réafficher par la suite dans DrawingBack.
*/

//ViewEdit.formEdition.dispose();

//formEdition = ViewEdit.toolkitEdition.createScrolledForm(ViewEdit.top);
ViewEdit.formEdition.getBody().setLayout(new GridLayout());
ViewEdit.formEdition.setText("Description des données");

//init composants IHM
initSections();
initCombos();
initText();
initBoutons();
initListeType();

//init de la arrayList des sections
listeSectionsData = new ArrayList <Section> ();
listeSectionsNoms = new ArrayList <String> ();

ArrayList <Text> listeText = new ArrayList <Text> ();
ArrayList <String> listeNoms = new ArrayList <String> ();



//boutons collapse all / expand all
//buttonExpand.setParent(ViewEdit.formEdition.getBody());
//buttonExpand.setText("+");


//reset
buttonRAZ.setParent(ViewEdit.formEdition.getBody());
buttonRAZ.setText("Reset");

//bouton validate
buttonValidate.setParent(ViewEdit.formEdition.getBody());
buttonValidate.setText("Valider étape");



if (ViewFile.checkBoxTempo.getSelection()){
	//série temporelle
	buttonFreqEchant.setParent(ViewEdit.formEdition.getBody());
	buttonFreqEchant.setText("Calcul temporel par fréquence d'échantillonnage");
	buttonLigneDonnees.setParent(ViewEdit.formEdition.getBody());
	buttonLigneDonnees.setText("Informations temporelles présentes dans les données");
	
	//on cree les sections puis on les cache dans un autre shell et on les re-affiche only si bouton coché
//	DrawingData.sectionHeure("Heure                                  ", "Sélectionnez l'heure présente dans les lignes de données", sectionHeure, textHeureLigne, textHeureDebut, textHeureFin);
//	DrawingData.sectionDate("Date                                   ", "Sélectionnez la date présente dans les lignes de données", sectionDate, textDateLigne, textDateDebut, textDateFin);
	DrawingData.sectionTotal("Date/Heure                             ", "Sélectionnez la date et l'heure présentes dans les lignes de données", sectionTotal, textTotalLigne, textTotalDebut, textTotalFin);
	
	
	buttonFreqEchant.setSelection(true);
	//sections désactivées de base
	test();
	
	formEdition.redraw();
	formEdition.reflow(true);
	
	
	
	
//	Shell s = new Shell();
//	sectionHeure.setParent(s);
//	sectionDate.setParent(s);
	
}



formEdition.reflow(true);










//section type courant
//DrawingData.sectionTypeCourant(sectionTypeCourant, textTypeCourantCode, textTypeCourantUnite, textTypeCourantConversion, textTypeCourantDebut, textTypeCourantFin, textTypeCourantFormat, textTypeCourantEntree, textTypeCourantSortie);

//section type ajouter
//DrawingData.sectionTypeAjouter(sectionTypeAjouter, textTypeAjouterCode, textTypeAjouterUnite, textTypeAjouterConversion, textTypeAjouterDebut, textTypeAjouterFin, textTypeAjouterFormat, textTypeAjouterEntree, textTypeAjouterSortie);

//bouton expand
//buttonExpand2.setParent(ViewEdit.formEdition.getBody());
buttonExpand2.setText("+");

ViewFile.affiche = 3;
ViewEdit.formEdition.reflow(true);

//listeners sur les boutons radio
buttonFreqEchant
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

	System.out.println("bouton freq echant");

test();

formEdition.reflow(true);

}
});



buttonLigneDonnees
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

	System.out.println("bouton lignes données");
	
	//buttonFreqEchant.setSelection(false);
//	EditData.sectionHeure.setEnabled(true);
//	EditData.sectionDate.setEnabled(true);
//	EditData.sectionHeure.setParent(ViewEdit.formEdition.getBody());
//	EditData.sectionDate.setParent(ViewEdit.formEdition.getBody());
	
	test2();
	formEdition.reflow(true);
}
});


//listeners




buttonExpand
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
System.out.println("expand/collapse all"); // 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

System.out.println("expand ? = " + expand);
for (i=0;i<=EditData.listeSectionsData.size()-1;i++){

if (expand == 0){ 
EditData.listeSectionsData.get(i).setExpanded(true);

} 
else{
	EditData.listeSectionsData.get(i).setExpanded(false);



}

}
if (expand == 0) {
System.out.println("expand passe à 1");
buttonExpand.setText("-");
buttonExpand2.setText("-");

expand = 1;}
else{expand = 0;
System.out.println("expand passe à 0");
buttonExpand.setText("+");
buttonExpand2.setText("+");
} 

//refresh global
formEdition.reflow(true);

}
});
				  

buttonExpand2
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
System.out.println("expand/collapse all"); // 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

System.out.println("expand ? = " + expand);
for (i=0;i<=EditData.listeSectionsData.size()-1;i++){

if (expand == 0){ 
EditData.listeSectionsData.get(i).setExpanded(true);

} 
else{
	EditData.listeSectionsData.get(i).setExpanded(false);



}

}
if (expand == 0) {
System.out.println("expand passe à 1");
buttonExpand.setText("-");
buttonExpand2.setText("-");

expand = 1;}
else{expand = 0;
System.out.println("expand passe à 0");
buttonExpand.setText("+");
buttonExpand2.setText("+");
} 

//refresh global
formEdition.reflow(true);

}
});



buttonValidate
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
System.out.println("validation"); // 

//on passe en ppar l'étape à laquelle on se trouve
Etape.etape(1);	

Etape.validationData();

formEdition.redraw();
formEdition.reflow(true);


}
});


buttonRAZ
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("RAZ"); // 
		
		razText(listeTextData);
		

	}
});


//raz du message d'erreurs
Utils.messageErreur("");




//refresh du formulaire


;
formEdition.reflow(true);


//affichage courant
ViewFile.affiche = 3;


//remplissage de l'IHM par le modèle chargé ?
System.out.println("Valeur de DataModel.ouverture = "+ DataModel.ouverture);			 
			 
 if (DataModel.ouverture){
	 DataModel.fillData();
					 }

}


public static void test(){
	int i;

	Shell s = new Shell();



	System.out.println("expand ? = " + expand);
	for (i=0;i<=EditData.listeSectionsData.size()-1;i++){

	System.out.println("===> " + EditData.listeSectionsData.get(i).getText());
	EditData.listeSectionsData.get(i).setEnabled(false); //Parent(s);
	System.out.println("EditData.test()... ok");
	}


//	refresh global
	ViewEdit.formEdition.reflow(true);
}

public static void test2(){
	int i;

	Shell s = new Shell();



	System.out.println("expand ? = " + expand);
	for (i=0;i<=EditData.listeSectionsData.size()-1;i++){

	System.out.println("===> " + EditData.listeSectionsData.get(i).getText());
	EditData.listeSectionsData.get(i).setEnabled(true); //Parent(s);
	System.out.println("EditData.test()... ok");
	}


//	refresh global
	ViewEdit.formEdition.reflow(true);
}

public static void razEdition(){
// reset de toute la zone d'édition après étape descriptioon de fichier
//on retire toutes les sections et les boutons expand et validation


int i;

Shell s = new Shell();

//effacement des boutons valider/expand/init serveur
EditData.buttonExpand.setParent(s);
EditData.buttonExpand2.setParent(s);
EditData.buttonValidate.setParent(s);
EditData.buttonRAZ.setParent(s);

buttonFreqEchant.setParent(s);
buttonLigneDonnees.setParent(s);


System.out.println("expand ? = " + expand);
for (i=0;i<=EditData.listeSectionsData.size()-1;i++){


	//EditData.listeSectionsData.get(i).setExpanded(true);
System.out.println("===> " + EditData.listeSectionsData.get(i).getText());
EditData.listeSectionsData.get(i).setParent(s);
System.out.println("effacement zone edition etape description campagne ==> ok");
}



//effacement titre formulaire
ViewEdit.formEdition.setText("");

//refresh global
ViewEdit.formEdition.reflow(true);

}

public static void DrawingBack(){
//	 reset de toute la zone d'édition après étape descriptioon de fichier
//	on retire toutes les sections et les boutons expand et validation


	int i;

	ScrolledForm s;
	s = ViewEdit.formEdition;
//	raz message erreur
	Utils.messageErreur("");
//	effacement des boutons valider/expand/init serveur
	//EditData.buttonExpand.setParent(s.getBody());
	
	EditData.buttonRAZ.setParent(s.getBody());
	EditData.buttonValidate.setParent(s.getBody());
	
	
	if (ViewFile.checkBoxTempo.getSelection()){
	buttonFreqEchant.setParent(s.getBody());
	buttonLigneDonnees.setParent(s.getBody());}

	System.out.println("expand ? = " + expand);
	for (i=0;i<=EditData.listeSectionsData.size()-1;i++){


		EditData.listeSectionsData.get(i).setExpanded(false);
	System.out.println("===> " + EditData.listeSectionsData.get(i).getText());
	EditData.listeSectionsData.get(i).setParent(s.getBody());
	System.out.println("reaffichage zone edition etape description campagne ==> ok");
	}

	//EditData.buttonExpand2.setParent(s.getBody());

	ViewEdit.formEdition.setText("Description de Données");
	
//	refresh global
	ViewEdit.formEdition.reflow(true);
	
//	affichage courant
	ViewFile.affiche = 3;

	}




public static void initSections(){


Shell shell = new Shell();	

sectionTypeCourant  = new Section(shell, SWT.NONE);
sectionTypeAjouter  = new Section(shell, SWT.NONE);
sectionHeure = new Section(shell, SWT.NONE);
sectionDate  = new Section(shell, SWT.NONE);
sectionTotal = new Section(shell, SWT.NONE);
}


public static void initText(){

Shell shell = new Shell();

textTypeCourantCode = new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantCode);
textTypeCourantConversion = new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantConversion);
textTypeCourantLigne = new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantLigne);
textTypeCourantDebut = new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantDebut);
textTypeCourantEntree= new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantEntree);
textTypeCourantFin= new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantFin);
textTypeCourantFormat = new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantFormat);
textTypeCourantSortie= new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantSortie);
textTypeCourantUnite= new Text(shell, SWT.BORDER);listeTextData.add(textTypeCourantUnite);

textTypeAjouterCode= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterCode);
textTypeAjouterConversion= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterConversion);
textTypeAjouterLigne= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterLigne);
textTypeAjouterDebut= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterDebut);
textTypeAjouterEntree= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterEntree);
textTypeAjouterFin= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterFin);
textTypeAjouterFormat= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterFormat);
textTypeAjouterSortie= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterSortie);
textTypeAjouterUnite= new Text(shell, SWT.BORDER);listeTextData.add(textTypeAjouterUnite);

textHeureLigne= new Text(shell, SWT.BORDER);listeTextData.add(textHeureLigne);
textHeureDebut= new Text(shell, SWT.BORDER);listeTextData.add(textHeureDebut);
textHeureFin= new Text(shell, SWT.BORDER);listeTextData.add(textHeureFin);
textHeureConversion= new Text(shell, SWT.BORDER);listeTextData.add(textHeureConversion);
textHeureTest= new Text(shell, SWT.BORDER);listeTextData.add(textHeureTest);

textDateLigne= new Text(shell, SWT.BORDER);listeTextData.add(textDateLigne);
textDateDebut= new Text(shell, SWT.BORDER);listeTextData.add(textDateDebut);
textDateFin= new Text(shell, SWT.BORDER);listeTextData.add(textDateFin);
textTotalLigne= new Text(shell, SWT.BORDER);listeTextData.add(textTotalLigne);
textTotalDebut= new Text(shell, SWT.BORDER);listeTextData.add(textTotalDebut);
textTotalFin= new Text(shell, SWT.BORDER);listeTextData.add(textTotalFin);

textTempo = new Text(shell, SWT.BORDER);listeTextData.add(textTempo);

//ajout des zones de texte dans la liste pour activer le reset


}	

public static void initBoutons(){

Shell shell = new Shell();

buttonExpand = new Button(shell, SWT.NONE);
buttonExpand2 = new Button(shell, SWT.NONE);
buttonValidate = new Button(shell, SWT.NONE);
buttonReset = new Button(shell, SWT.NONE);
buttonRAZ= new Button(shell, SWT.NONE);
buttonLigneDonnees = new Button(shell, SWT.RADIO);
buttonFreqEchant = new Button(shell, SWT.RADIO);

}


public static void initListeType(){

Shell shell = new Shell();
listeType = new List(shell, SWT.BORDER);


}
	
	
public static void initCombos(){
	
	Shell shell = new Shell();
	
	comboHeure = new Combo(shell, SWT.NONE);
	comboHeure.add("hh:mm:ss");
	comboHeure.add("hh:mm");
	comboHeure.add("hh24:mm:ss");
	comboHeure.add("hh24:mm");
	comboHeure.add("hh24mmss");
	comboHeure.add("hh24mm");

	comboDate = new Combo(shell, SWT.NONE);
	comboDate.add("ddMMyyyy");
	comboDate.add("ddMMyy");
	comboDate.add("MMddyy");
	comboDate.add("yyddMM");
	comboDate.add("dd:MM:yyyy");
	comboDate.add("dd:MM:yy");
	comboDate.add("dd/MM/yy");
	comboDate.add("dd/MM/yyyy");
	
	comboTotal = new Combo(shell, SWT.NONE);
	comboTotal.add("ddMMyyyy hh:mm:ss");
	comboTotal.add("ddMMyyyy hh24:mm");
	comboTotal.add("ddMMyyyy hh24mm");
	comboDate.add("dd/MM/yyyy hh:mm:ss");
	comboDate.add("dd/MM/yyyy hh24:mm");
	comboDate.add("dd/MM/yyyy hh24mm");
	

}	

public static void razText(ArrayList <Text> liste){
	
	for (int i =0; i< liste.size(); i++){
		liste.get(i).setText("");
				
	}
	
	//raz de la table de parameters
//	effacement de la liste des types de données dans la table
	TableData.types = new ArrayList<TypeData>();
	  //razTableData
	TableData.razTableData();
		 
		 //refresh de la table
		 //initTable();
	TableData.creationTable();
	
	
	
	
}

	
}
