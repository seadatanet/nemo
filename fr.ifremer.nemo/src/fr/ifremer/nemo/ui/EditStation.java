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

public class EditStation {
//	 fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

	public static Text textRefStationLigne, textRefStationDebut, textRefStationFin, textHistoriqueAcquisition1, textHistoriqueAcquisition2, textCommentaire, textHistoriqueAcqLigne1,textHistoriqueAcqLigne2, textHistoriqueAcqDebut1, textHistoriqueAcqDebut2, textHistoriqueAcqFin1, textHistoriqueAcqFin2, textHistorique1, textHistorique2, textConversionHeure, textConversionDate, textTU, textTULigne, textTUDebut, textTUFin, textHeureLigne, textHeureDebut, textHeureFin, textSurface1, textSurface2, textSurfaceLigne1, textSurfaceLigne2, textSurfaceDebut1, textSurfaceDebut2, textSurfaceFin1, textSurfaceFin2, textDateLigne, textDateDebut, textDateFin, textLatitude, textLatitudeLigne, textLatitudeDebut, textLatitudeFin, textLongitude, textLongitudeLigne, textLongitudeDebut, textLongitudeFin, textProfondeur, textProfondeurLigne, textProfondeurDebut, textProfondeurFin, textCommentaireLigne, textCommentaireDebut, textCommentaireFin;
	public static Text textLatFin, textLatFinLigne, textLatFinDebut, textLatFinFin, textLongFin, textLongFinLigne, textLongFinDebut, textLongFinFin, textProfFin, textProfFinLigne, textProfFinDebut, textProfFinFin, textProfCapteur, textProfCapteurLigne, textProfCapteurDebut, textProfCapteurFin, textDeclinMagnet, textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin, textFreqEchant, textFreqEchantLigne, textFreqEchantDebut, textFreqEchantFin, textFreqEchantConv, textFreqEchantTest, textTestConvHeure, textTestConvDate, textHeureSaisie, textDateSaisie;
	public static Button buttonValidate, buttonRAZ, buttonExpand, buttonExpand2, buttonRefStationIncremente, buttonHistoriqueAcqSaisie, buttonSurfaceSaisie, buttonTUSaisie, buttonLatSaisie, buttonLongSaisie, buttonProfondeurSaisie, buttonCommentaireSaisie;
	public static Button buttonHistoriqueAcqPos,buttonRefStationPos,buttonSurfacePos,buttonLatPos,buttonLongPos,buttonCommentairePos,buttonProfondeurPos;
	public static Button buttonLatFinPos, buttonLongFinPos, buttonProfFinPos, buttonProfCapteurPos, buttonDeclinMagnetPos, buttonFreqEchantPos, buttonLatFinSaisie, buttonLongFinSaisie, buttonProfFinSaisie, buttonProfCapteurSaisie, buttonDeclinMagnetSaisie, buttonFreqEchantSaisie;
	public static Button buttonHeureReference, buttonDateReference, buttonHeureReferencePos, buttonDateReferencePos;
	public static Combo comboRefStationType, comboHeure, comboDate, comboLatitude, comboLongitude;
	public static Section sectionStation, sectionTypeDonnees, sectionHistorique, sectionCommentaire, sectionSurface, sectionHeure, sectionDate, sectionTU, sectionLatitude, sectionLongitude, sectionProfondeur;
	public static Section sectionLatFin, sectionLongFin, sectionProfFin, sectionProfCapteur, sectionDeclinMagnet, sectionFreqEchant;
	public static Label labelConversionHeure, labelConversionDate;
	
	
	
///////////////////////////////////////////
	

public static ArrayList <Section> listeSectionsStation, listeSectionsParam;
public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
public static ArrayList <Label> listeSectionsLabel;
public static ArrayList <Text> listeTextStation;
public static int expand, validee, loaded, offsetDebutHeure, offsetFinHeure, offsetDebutDate, offsetFinDate;
public static List listeType;
public static ScrolledForm formEdition;

////////////////////////////////////////////


public static void affichageEdition(){
// initialisation zone édition pour étape description de fichier 

formEdition = ViewEdit.formEdition;

//raz message erreur
Utils.messageErreur("");

//affichage du texte dans la zone de gauche

//Utils.remplissageTexte(ViewFile.styledTextStation);
ViewFile.styledTextStation.setText(Utils.texteBrut);

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
ViewEdit.formEdition.setText("Description de station");


//init de la arrayList des sections
listeSectionsStation = new ArrayList <Section> ();
listeSectionsNoms = new ArrayList <String> ();
listeTextStation =  new ArrayList <Text> ();

//init composants IHM
initSections();
initCombos();
initText();
initBoutons();
initListeType();
initLabels();


//boutons collapse all / expand all
buttonExpand.setParent(ViewEdit.formEdition.getBody());
buttonExpand.setText("+");
formEdition.redraw();
formEdition.reflow(true);
//formEdition.

buttonRAZ.setParent(ViewEdit.formEdition.getBody());
buttonRAZ.setText("Reset");


//bouton validate
buttonValidate.setParent(ViewEdit.formEdition.getBody());
buttonValidate.setText("Valider étape");

formEdition.reflow(true);

// initialisation liste des sections de la partie description fichier

//section reference station
DrawingStation.sectionIncrementeLigneDebutFinInject("Numéro de station                      ", "Choisissez si le numéro de station est calculé ou lu dans le fichier.", EditStation.sectionStation, textRefStationLigne, textRefStationDebut, textRefStationFin, buttonRefStationIncremente, buttonRefStationPos);



//section reference type de données
DrawingStation.sectionTypeDonnees(sectionTypeDonnees);


//section historique acquisition
DrawingStation.sectionHistoriqueAcquisition("Historique Acquisition                 ", "Entrez ici les lignes correspondant à l'historique des mesures...", sectionHistorique, textHistoriqueAcqLigne1, textHistoriqueAcqDebut1, textHistoriqueAcqFin1, textHistoriqueAcqLigne2, textHistoriqueAcqDebut2, textHistoriqueAcqFin2, textHistorique1, textHistorique2, buttonHistoriqueAcqSaisie, buttonHistoriqueAcqPos );


//section commentaire
DrawingStation.sectionCommentaireLigneDebutFinInject("Commentaire                            ", "", sectionCommentaire, textCommentaireLigne, textCommentaireDebut, textCommentaireFin, textCommentaire, buttonCommentaireSaisie, buttonCommentairePos);


//section surface sample : identique à historique acquisition
DrawingStation.sectionHistoriqueAcquisition("Echantillon de surface                 ", "", sectionSurface, textSurfaceLigne1, textSurfaceDebut1, textSurfaceFin1, textSurfaceLigne2, textSurfaceDebut2, textSurfaceFin2, textSurface1, textSurface2, buttonSurfaceSaisie, buttonSurfacePos);


//section décallage TU
//DrawingStation.sectionManuelLigneDebutFinInject("Décallage/TU                           ", "", sectionTU, textTULigne, textTUDebut, textTUFin, textTU, buttonTUSaisie);


//section heure
DrawingStation.sectionHeure("Heure                                  ", "", sectionHeure, textHeureLigne, textHeureDebut, textHeureFin, textHeureSaisie, labelConversionHeure);


//section date
DrawingStation.sectionDate("Date                                   ", "", sectionDate, textDateLigne, textDateDebut, textDateFin, textDateSaisie, labelConversionDate);


//section Latitude
DrawingStation.sectionManuelLigneDebutFinInjectLatLongComboFormat("Latitude                               ", "", sectionLatitude, textLatitudeLigne, textLatitudeDebut, textLatitudeFin, textLatitude, buttonLatSaisie, comboLatitude, buttonLatPos);

if (ViewFile.checkBoxTempo.getSelection()){
	//section latitude de fin
	DrawingStation.sectionManuelLigneDebutFinInjectLatLongFin("Latitude de fin                        ", "", sectionLatFin,textLatFinLigne, textLatFinDebut, textLatFinFin, textLatFin, buttonLatFinSaisie, buttonLatFinPos);
}

//section longitude
DrawingStation.sectionManuelLigneDebutFinInjectLatLongComboFormat("Longitude                              ", "", sectionLongitude, textLongitudeLigne, textLongitudeDebut, textLongitudeFin, textLongitude, buttonLongSaisie, comboLongitude, buttonLongPos);

if (ViewFile.checkBoxTempo.getSelection()){
	//section llongitude de fin
	DrawingStation.sectionManuelLigneDebutFinInjectLatLongFin("Longitude de fin                       ", "", sectionLongFin, textLongFinLigne, textLongFinDebut,textLongFinFin, textLongFin, buttonLongFinSaisie,  buttonLongFinPos);	
}


//section profondeur
DrawingStation.sectionManuelLigneDebutFinInject("Profondeur                             ", "", sectionProfondeur, textProfondeurLigne, textProfondeurDebut, textProfondeurFin, textProfondeur, buttonProfondeurSaisie, buttonProfondeurPos);

if (ViewFile.checkBoxTempo.getSelection()){
	//section profondeur de fin
	DrawingStation.sectionManuelLigneDebutFinInject("Profondeur de fin                      ","", sectionProfFin, textProfFinLigne, textProfFinDebut, textProfFinFin, textProfFin, buttonProfFinSaisie, buttonProfFinPos);

	//section profondeur de capteur
	DrawingStation.sectionManuelLigneDebutFinInject("Profondeur du capteur                  ", "", sectionProfCapteur, textProfCapteurLigne, textProfCapteurDebut, textProfCapteurFin, textProfCapteur, buttonProfCapteurSaisie, buttonProfCapteurPos);

	//section déclinaison magnétique
			  DrawingStation.sectionManuelLigneDebutFinInject("Déclinaison magnétique                 ", "", sectionDeclinMagnet, textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin, textDeclinMagnet, buttonDeclinMagnetSaisie, buttonDeclinMagnetPos);
	
			  
	//if (!ViewFile.textTempoHeureRef.getText().matches("")){		  
	//section fréquence échantillonnage
	DrawingStation.sectionManuelLigneDebutFinInjectFreqEchant("Fréquence échantillonnage              ", "", sectionFreqEchant, textFreqEchantLigne, textFreqEchantDebut, textFreqEchantFin, textFreqEchant, buttonFreqEchantSaisie, buttonFreqEchantPos, textFreqEchantConv, textFreqEchantTest);
	//}
}	

//TODO : section flags date/heure

//section séries temporelles
//DrawingStation.sectionSerieTempo();


buttonExpand2.setParent(ViewEdit.formEdition.getBody());
buttonExpand2.setText("+");


ViewEdit.formEdition.reflow(true);



//listeners




buttonExpand
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
System.out.println("expand/collapse all"); 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){

if (expand == 0){ 
EditStation.listeSectionsStation.get(i).setExpanded(true);

} 
else{
	EditStation.listeSectionsStation.get(i).setExpanded(false);



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
System.out.println("expand/collapse all"); 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){

if (expand == 0){ 
EditStation.listeSectionsStation.get(i).setExpanded(true);

} 
else{
	EditStation.listeSectionsStation.get(i).setExpanded(false);



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
Etape.etape(2);	

Etape.validationStation();

formEdition.redraw();
formEdition.reflow(true);


}
});


//raz du message d'erreurs
Utils.messageErreur("");








//refresh du formulaire



formEdition.reflow(true);

//affichage courant
ViewFile.affiche = 2;

//remplissage de l'IHM par le modèle chargé ?
System.out.println("Valeur de DataModel.ouverture = "+ DataModel.ouverture);			 
			 
 if (DataModel.ouverture){
	 DataModel.fillStation();
					 }



}


public static void razEdition(){
// reset de toute la zone d'édition après étape descriptioon de fichier
//on retire toutes les sections et les boutons expand et validation


int i;

Shell s = new Shell();

//effacement des boutons valider/expand/init serveur
EditStation.buttonExpand.setParent(s);
EditStation.buttonExpand2.setParent(s);
EditStation.buttonValidate.setParent(s);
EditStation.buttonRAZ.setParent(s);


System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){


EditStation.listeSectionsStation.get(i).setExpanded(true);
System.out.println("===> " + EditStation.listeSectionsStation.get(i).getText());
EditStation.listeSectionsStation.get(i).setParent(s);
System.out.println("effacement zone edition etape description station ==> ok");
}

//titre à vide
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
	EditStation.buttonExpand.setParent(s.getBody());
	
	EditStation.buttonValidate.setParent(s.getBody());
	EditStation.buttonRAZ.setParent(s.getBody());


	System.out.println("expand ? = " + expand);
	for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){


	EditStation.listeSectionsStation.get(i).setExpanded(false);
	System.out.println("===> " + EditStation.listeSectionsStation.get(i).getText());
	EditStation.listeSectionsStation.get(i).setParent(s.getBody());
	System.out.println("reaffichage zone edition etape description station ==> ok");
	}

	EditStation.buttonExpand2.setParent(s.getBody());
	
	ViewEdit.formEdition.setText("Description de Station");
	
	
//	refresh global
	ViewEdit.formEdition.reflow(true);

//	affichage courant
	ViewFile.affiche = 2;

	}



public static void initSections(){


Shell shell = new Shell();	
sectionStation = new Section(shell, SWT.NONE);
sectionTypeDonnees = new Section(shell, SWT.NONE);
sectionHistorique = new Section(shell, SWT.NONE);
sectionCommentaire = new Section(shell, SWT.NONE);
sectionSurface = new Section(shell, SWT.NONE);
sectionHeure =  new Section(shell, SWT.NONE);
sectionDate = new Section(shell, SWT.NONE);
sectionTU = new Section(shell, SWT.NONE);
sectionLatitude = new Section(shell, SWT.NONE);
sectionLongitude = new Section(shell, SWT.NONE);
sectionProfondeur = new Section(shell, SWT.NONE);
sectionLatFin =  new Section(shell, SWT.NONE);
sectionLongFin = new Section(shell, SWT.NONE);
sectionProfFin= new Section(shell, SWT.NONE);
sectionProfCapteur= new Section(shell, SWT.NONE);
sectionDeclinMagnet= new Section(shell, SWT.NONE);
sectionFreqEchant= new Section(shell, SWT.NONE);

}


public static void initText(){

Shell shell = new Shell();

textRefStationLigne = new Text(shell, SWT.BORDER);
textRefStationDebut = new Text(shell, SWT.BORDER);
textRefStationFin = new Text(shell, SWT.BORDER);

textHistoriqueAcqLigne1= new Text(shell, SWT.BORDER);
textHistoriqueAcqLigne2= new Text(shell, SWT.BORDER);
textHistoriqueAcqDebut1 = new Text(shell, SWT.BORDER);
textHistoriqueAcqDebut2= new Text(shell, SWT.BORDER);
textHistoriqueAcqFin1= new Text(shell, SWT.BORDER);
textHistoriqueAcqFin2 = new Text(shell, SWT.BORDER);
textHistorique1 = new Text(shell, SWT.BORDER);
textHistorique2  = new Text(shell, SWT.BORDER);
textHistoriqueAcquisition1 = new Text(shell, SWT.BORDER);
textHistoriqueAcquisition2 = new Text(shell, SWT.BORDER);

textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
textCommentaireLigne= new Text(shell, SWT.BORDER);
textCommentaireDebut= new Text(shell, SWT.BORDER);
textCommentaireFin = new Text(shell, SWT.BORDER);

textConversionDate  = new Text(shell, SWT.BORDER);
textConversionHeure  = new Text(shell, SWT.BORDER);
textTestConvHeure = new Text(shell, SWT.BORDER);
textTestConvDate = new Text(shell, SWT.BORDER);

textHeureLigne = new Text(shell, SWT.BORDER);
textHeureDebut = new Text(shell, SWT.BORDER);
textHeureFin  = new Text(shell, SWT.BORDER);
textHeureSaisie   = new Text(shell, SWT.BORDER);

textDateLigne = new Text(shell, SWT.BORDER);
textDateDebut = new Text(shell, SWT.BORDER);
textDateFin  = new Text(shell, SWT.BORDER);
textDateSaisie   = new Text(shell, SWT.BORDER);

textLatitude = new Text(shell, SWT.BORDER);
textLatitudeLigne= new Text(shell, SWT.BORDER);
textLatitudeDebut= new Text(shell, SWT.BORDER);
textLatitudeFin = new Text(shell, SWT.BORDER);
textLatFinLigne= new Text(shell, SWT.BORDER);
textLatFinDebut= new Text(shell, SWT.BORDER);
textLatFinFin= new Text(shell, SWT.BORDER);
textLatFin = new Text(shell, SWT.BORDER);

textLongitude = new Text(shell, SWT.BORDER);
textLongitudeLigne = new Text(shell, SWT.BORDER);
textLongitudeDebut = new Text(shell, SWT.BORDER);
textLongitudeFin  = new Text(shell, SWT.BORDER);
textLongFinLigne = new Text(shell, SWT.BORDER);
textLongFinDebut = new Text(shell, SWT.BORDER);
textLongFinFin = new Text(shell, SWT.BORDER);
textLongFin = new Text(shell, SWT.BORDER);

textProfondeur = new Text(shell, SWT.BORDER);
textProfondeurLigne = new Text(shell, SWT.BORDER);
textProfondeurDebut = new Text(shell, SWT.BORDER);
textProfondeurFin = new Text(shell, SWT.BORDER);
textProfFin = new Text(shell, SWT.BORDER);
textProfFinLigne = new Text(shell, SWT.BORDER);
textProfFinDebut = new Text(shell, SWT.BORDER);
textProfFinFin = new Text(shell, SWT.BORDER);
textProfCapteur = new Text(shell, SWT.BORDER);
textProfCapteurLigne = new Text(shell, SWT.BORDER);
textProfCapteurDebut = new Text(shell, SWT.BORDER);
textProfCapteurFin = new Text(shell, SWT.BORDER);

textDeclinMagnet = new Text(shell, SWT.BORDER);
textDeclinMagnetLigne = new Text(shell, SWT.BORDER);
textDeclinMagnetDebut = new Text(shell, SWT.BORDER);
textDeclinMagnetFin = new Text(shell, SWT.BORDER);

textFreqEchant = new Text(shell, SWT.BORDER);
textFreqEchantLigne = new Text(shell, SWT.BORDER);
textFreqEchantDebut = new Text(shell, SWT.BORDER);
textFreqEchantFin = new Text(shell, SWT.BORDER);
textFreqEchantConv = new Text(shell, SWT.BORDER);
textFreqEchantTest = new Text(shell, SWT.BORDER);


textSurface1 = new Text(shell, SWT.BORDER);
textSurface2= new Text(shell, SWT.BORDER);
textSurfaceDebut1= new Text(shell, SWT.BORDER);
textSurfaceDebut2= new Text(shell, SWT.BORDER);
textSurfaceFin1= new Text(shell, SWT.BORDER);
textSurfaceFin2= new Text(shell, SWT.BORDER);
textSurfaceLigne1= new Text(shell, SWT.BORDER);
textSurfaceLigne2= new Text(shell, SWT.BORDER);

textTU = new Text(shell, SWT.BORDER);
textTULigne = new Text(shell, SWT.BORDER);
textTUDebut = new Text(shell, SWT.BORDER);
textTUFin = new Text(shell, SWT.BORDER);

//remplissage de la arrayList




}	

public static void initBoutons(){

Shell shell = new Shell();

buttonRAZ = new Button(shell, SWT.NONE);
buttonExpand = new Button(shell, SWT.NONE);
buttonExpand2 = new Button(shell, SWT.NONE);
buttonValidate = new Button(shell, SWT.NONE);

buttonHistoriqueAcqSaisie = new Button(shell, SWT.RADIO);
buttonHistoriqueAcqPos = new Button(shell, SWT.RADIO);

buttonRefStationIncremente = new Button(shell, SWT.RADIO);
buttonRefStationPos = new Button(shell, SWT.RADIO);

buttonSurfaceSaisie = new Button(shell, SWT.RADIO);
buttonSurfacePos = new Button(shell, SWT.RADIO);

buttonTUSaisie  = new Button(shell, SWT.RADIO);

buttonLatSaisie  = new Button(shell, SWT.RADIO);
buttonLatPos  = new Button(shell, SWT.RADIO);
buttonLatFinPos = new Button(shell, SWT.RADIO);
buttonLatFinSaisie = new Button(shell, SWT.RADIO);

buttonLongSaisie  = new Button(shell, SWT.RADIO);
buttonLongPos  = new Button(shell, SWT.RADIO);
buttonLongFinPos = new Button(shell, SWT.RADIO);
buttonLongFinSaisie = new Button(shell, SWT.RADIO);

buttonCommentaireSaisie  = new Button(shell, SWT.RADIO);
buttonCommentairePos  = new Button(shell, SWT.RADIO);

buttonProfondeurSaisie  = new Button(shell, SWT.RADIO);
buttonProfondeurPos  = new Button(shell, SWT.RADIO);
buttonProfFinPos = new Button(shell, SWT.RADIO);
buttonProfFinSaisie = new Button(shell, SWT.RADIO);

buttonProfCapteurPos = new Button(shell, SWT.RADIO);
buttonProfCapteurSaisie = new Button(shell, SWT.RADIO);

buttonDeclinMagnetPos = new Button(shell, SWT.RADIO);
buttonDeclinMagnetSaisie = new Button(shell, SWT.RADIO);

buttonFreqEchantPos = new Button(shell, SWT.RADIO);
buttonFreqEchantSaisie = new Button(shell, SWT.RADIO);

buttonDateReference= new Button(shell, SWT.RADIO);
buttonHeureReference= new Button(shell, SWT.RADIO);
buttonHeureReferencePos =  new Button(shell, SWT.RADIO);
buttonDateReferencePos =  new Button(shell, SWT.RADIO);
}


public static void initListeType(){

Shell shell = new Shell();
listeType = new List(shell, SWT.BORDER);

}


public static void initLabels(){
	
Shell shell = new Shell();
labelConversionHeure = new Label(shell, SWT.NONE);
labelConversionDate  = new Label(shell, SWT.NONE);

	
	
}


public static void initComboType(){
	//remplissage de la combobox avec code + libellé séparés par " - " ==> "HO9 - Bouteilles"
	int taille;
	taille = DataModel.listeWSDataTypes.length;
		
	for (int i =0; i<taille; i++){
		String chaine = "";
		
		chaine = chaine + DataModel.listeWSDataTypes[i].getCode() + " - " + DataModel.listeWSDataTypes[i].getLibelle();
		comboRefStationType.add(chaine);
		
			}
	
} 


public static void initCombos(){
	
	Shell shell = new Shell();
	comboRefStationType = new Combo(shell, SWT.READ_ONLY);
	//remplissage avec les dataTypes de la campagne
	
	int taille = TableDataSurvey.types.size();
	for (int i=0;i<taille;i++){
		comboRefStationType.add(TableDataSurvey.types.get(i).getCode());
	}
	
	
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
	comboDate.add("DD:mm:yyyy");
	comboDate.add("dd:MM:yy");
	comboDate.add("dd/MM/yy");
	comboDate.add("dd/MM/yyyy");
	
	comboLatitude = new Combo(shell, SWT.NONE);
	comboLatitude.add("DDD MM ss.s");
	comboLatitude.add("DDD MM.MMM");
	comboLatitude.add("DDD.DDDDD");
	comboLatitude.add("DDD.DD");
	comboLatitude.add("NDD MM.mm");
	
	comboLongitude = new Combo(shell, SWT.NONE);
	comboLongitude.add("DDD MM ss.s");
	comboLongitude.add("DDD MM.MMM");
	comboLongitude.add("DDD.DDDDD");
	comboLongitude.add("DDD.DD");
	comboLongitude.add("NDDD MM.mm");

}

public static void razText(ArrayList <Text> liste){
	
	for (int i =0; i< liste.size(); i++){
		liste.get(i).setText("");
				
	}
	
}

}
