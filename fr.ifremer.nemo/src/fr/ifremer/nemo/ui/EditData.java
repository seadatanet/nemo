package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class EditData {

//	 fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

public static Section sectionTypeCourant, sectionTypeAjouter;
public static Text textTempo, textTypeCourantCode, textTypeCourantUnite, textTypeCourantConversion, textTypeCourantDebut, textTypeCourantFin, textTypeCourantFormat, textTypeCourantEntree, textTypeCourantSortie, textTypeCourantLigne, textTypeAjouterCode, textTypeAjouterUnite, textTypeAjouterConversion, textTypeAjouterDebut, textTypeAjouterFin, textTypeAjouterLigne, textTypeAjouterEntree, textTypeAjouterSortie, textTypeAjouterFormat;
public static Label labelTypeCourantCode, labelTypeCourantUnite, labelTypeCourantConversion, labelTypeCourantDebut, labelTypeCourantFin, labelTypeCourantFormat, labelTypeCourantEntree, labelTypeCourantSortie; 
public static Button buttonValidate, buttonExpand, buttonExpand2;
public static ArrayList <Section> listeSectionsData, listeSectionsParam;
public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
public static ArrayList <Label> listeSectionsLabel;
public static int expand, validee, loaded;
public static List listeType;
public static ScrolledForm formEdition;

////////////////////////////////////////////

public static void affichageEdition(){
// initialisation zone édition pour étape description de fichier 

formEdition = ViewEdit.formEdition;

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
buttonExpand.setText("+");
formEdition.redraw();
formEdition.reflow(true);
//formEdition.



//bouton validate
buttonValidate.setParent(ViewEdit.formEdition.getBody());
buttonValidate.setText("Valider étape");

formEdition.reflow(true);

// initialisation   liste des sections de la partie description fichier

//section type courant
//DrawingData.sectionTypeCourant(sectionTypeCourant, textTypeCourantCode, textTypeCourantUnite, textTypeCourantConversion, textTypeCourantDebut, textTypeCourantFin, textTypeCourantFormat, textTypeCourantEntree, textTypeCourantSortie);

//section type ajouter
//DrawingData.sectionTypeAjouter(sectionTypeAjouter, textTypeAjouterCode, textTypeAjouterUnite, textTypeAjouterConversion, textTypeAjouterDebut, textTypeAjouterFin, textTypeAjouterFormat, textTypeAjouterEntree, textTypeAjouterSortie);

//bouton expand
//buttonExpand2.setParent(ViewEdit.formEdition.getBody());
buttonExpand2.setText("+");

ViewFile.affiche = 3;
ViewEdit.formEdition.reflow(true);




//listeners




buttonExpand
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
System.out.println("expand/collapse all"); // TODO Auto-generated Event stub widgetSelected()
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
System.out.println("expand/collapse all"); // TODO Auto-generated Event stub widgetSelected()
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
System.out.println("validation"); // TODO Auto-generated Event stub widgetSelected()

//on passe en ppar l'étape à laquelle on se trouve
Etape.etape(1);	

Etape.validationData();

formEdition.redraw();
formEdition.reflow(true);


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


public static void razEdition(){
// reset de toute la zone d'édition après étape descriptioon de fichier
//on retire toutes les sections et les boutons expand et validation


int i;

Shell s = new Shell();

//effacement des boutons valider/expand/init serveur
EditData.buttonExpand.setParent(s);
EditData.buttonExpand2.setParent(s);
EditData.buttonValidate.setParent(s);

System.out.println("expand ? = " + expand);
for (i=0;i<=EditData.listeSectionsData.size()-1;i++){


	EditData.listeSectionsData.get(i).setExpanded(true);
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
	
	EditData.buttonValidate.setParent(s.getBody());

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
}


public static void initText(){

Shell shell = new Shell();

textTypeCourantCode = new Text(shell, SWT.BORDER);
textTypeCourantConversion = new Text(shell, SWT.BORDER);
textTypeCourantLigne = new Text(shell, SWT.BORDER);
textTypeCourantDebut = new Text(shell, SWT.BORDER);
textTypeCourantEntree= new Text(shell, SWT.BORDER);
textTypeCourantFin= new Text(shell, SWT.BORDER);
textTypeCourantFormat = new Text(shell, SWT.BORDER);
textTypeCourantSortie= new Text(shell, SWT.BORDER);
textTypeCourantUnite= new Text(shell, SWT.BORDER);

textTypeAjouterCode= new Text(shell, SWT.BORDER);
textTypeAjouterConversion= new Text(shell, SWT.BORDER);
textTypeAjouterLigne= new Text(shell, SWT.BORDER);
textTypeAjouterDebut= new Text(shell, SWT.BORDER);
textTypeAjouterEntree= new Text(shell, SWT.BORDER);
textTypeAjouterFin= new Text(shell, SWT.BORDER);
textTypeAjouterFormat= new Text(shell, SWT.BORDER);
textTypeAjouterSortie= new Text(shell, SWT.BORDER);
textTypeAjouterUnite= new Text(shell, SWT.BORDER);




textTempo = new Text(shell, SWT.BORDER);

}	

public static void initBoutons(){

Shell shell = new Shell();

buttonExpand = new Button(shell, SWT.NONE);
buttonExpand2 = new Button(shell, SWT.NONE);
buttonValidate = new Button(shell, SWT.NONE);

}


public static void initListeType(){

Shell shell = new Shell();
listeType = new List(shell, SWT.BORDER);


}
	
	
	
	
	
}
