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

import fr.ifremer.nemo.domain.DataType;

public class EditStation {
//	 fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

	public static Text textRefStationLigne, textRefStationDebut, textRefStationFin, textHistoriqueAcquisition1, textHistoriqueAcquisition2, textCommentaire, textHistoriqueAcqLigne1,textHistoriqueAcqLigne2, textHistoriqueAcqDebut1, textHistoriqueAcqDebut2, textHistoriqueAcqFin1, textHistoriqueAcqFin2, textHistorique1, textHistorique2, textConversionHeure, textConversionDate, textTU, textTULigne, textTUDebut, textTUFin, textHeureLigne, textHeureDebut, textHeureFin, textSurface1, textSurface2, textSurfaceLigne1, textSurfaceLigne2, textSurfaceDebut1, textSurfaceDebut2, textSurfaceFin1, textSurfaceFin2, textDateLigne, textDateDebut, textDateFin, textLatitude, textLatitudeLigne, textLatitudeDebut, textLatitudeFin, textLongitude, textLongitudeLigne, textLongitudeDebut, textLongitudeFin, textProfondeur, textProfondeurLigne, textProfondeurDebut, textProfondeurFin, textCommentaireLigne, textCommentaireDebut, textCommentaireFin;
	public static Text textLatFin, textLatFinLigne, textLatFinDebut, textLatFinFin, textLongFin, textLongFinLigne, textLongFinDebut, textLongFinFin, textProfFin, textProfFinLigne, textProfFinDebut, textProfFinFin, textProfCapteur, textProfCapteurLigne, textProfCapteurDebut, textProfCapteurFin, textDeclinMagnet, textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin, textFreqEchant, textFreqEchantLigne, textFreqEchantDebut, textFreqEchantFin, textFreqEchantConv, textFreqEchantTest, textTestConvHeure, textTestConvDate, textHeureSaisie, textDateSaisie;
	public static Text textQCDate, textQCLat, textQCLong, textQCProf;
	public static Button buttonValidate, buttonRAZ, buttonExpand, buttonExpand2, buttonRefStationIncremente, buttonHistoriqueAcqSaisie, buttonSurfaceSaisie, buttonTUSaisie, buttonLatSaisie, buttonLongSaisie, buttonProfondeurSaisie, buttonCommentaireSaisie;
	public static Button buttonHistoriqueAcqPos,buttonRefStationPos,buttonSurfacePos,buttonLatPos,buttonLongPos,buttonCommentairePos,buttonProfondeurPos;
	public static Button buttonLatFinPos, buttonLongFinPos, buttonProfFinPos, buttonProfCapteurPos, buttonDeclinMagnetPos, buttonFreqEchantPos, buttonLatFinSaisie, buttonLongFinSaisie, buttonProfFinSaisie, buttonProfCapteurSaisie, buttonDeclinMagnetSaisie, buttonFreqEchantSaisie;
	public static Button buttonHeureReference, buttonDateReference, buttonHeureReferencePos, buttonDateReferencePos;
	public static Button buttonHeurePos, buttonHeureSaisie, buttonDatePos, buttonDateSaisie, buttonTUPos;

	public static Combo comboRefStationType, comboHeure, comboDate, comboLatitude, comboLongitude;
	public static Section sectionQCDate ,sectionQCLat, sectionQCLong ,sectionQCProf;
	public static Section sectionStation, sectionTypeDonnees, sectionHistorique, sectionCommentaire, sectionSurface, sectionHeure, sectionDate, sectionTU, sectionLatitude, sectionLongitude, sectionProfondeur;
	public static Section sectionLatFin, sectionLongFin, sectionProfFin, sectionProfCapteur, sectionDeclinMagnet, sectionFreqEchant;
	public static Label labelConversionHeure, labelConversionDate;
	public static Boolean okApresReset;
	//public static int posxCommentaire, posyCommentaire, posxEchantillon1, posyEchantillon1, posxEchantillon2, posyEchantillon2, posxHistoriqueAcq1, posyHistoriqueAcq1, posxHistoriqueAcq2, posyHistoriqueAcq2;
	
	
	
///////////////////////////////////////////
	

public static ArrayList <Section> listeSectionsStation, listeSectionsParam;
public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
public static ArrayList <Label> listeSectionsLabel;
public static ArrayList <Text> listeTextStation;
public static ArrayList <Button> listeBoutonsPos;
public static ArrayList <Button> listeBoutonsManuel;
public static int expand, validee, loaded, offsetDebutHeure, offsetFinHeure, offsetDebutDate, offsetFinDate;
public static List listeType;
public static ScrolledForm formEdition;
public static Cursor cursCommentaire, cursEchant1, cursEchant2, cursHistorique1, cursHistorique2;

////////////////////////////////////////////


public static void affichageEdition(){
// initialisation zone édition pour étape description de fichier 

	cursCommentaire = new Cursor();
	cursEchant1 = new Cursor();
	cursEchant2 = new Cursor();
	cursHistorique1 = new Cursor();
	cursHistorique2 = new Cursor();
	
	
//	posxCommentaire =0;
//	posyCommentaire=0;
//	posxEchantillon1=0;
//	posyEchantillon1=0;
//	posxEchantillon2=0;
//	posyEchantillon2=0;
//	posxHistoriqueAcq1=0;
//	posyHistoriqueAcq1=0;
//	posxHistoriqueAcq2=0;
//	posyHistoriqueAcq2=0;	
	
	
formEdition = ViewEdit.formEdition;
ViewLog.aideStation();
//raz message erreur
Utils.messageErreur(""); //$NON-NLS-1$
okApresReset = true;

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
ViewEdit.formEdition.setText(Messages.getString("EditStation.1")); //$NON-NLS-1$


//init de la arrayList des sections
listeSectionsStation = new ArrayList <Section> ();
listeSectionsNoms = new ArrayList <String> ();
listeTextStation =  new ArrayList <Text> ();
listeBoutonsManuel =  new ArrayList <Button> ();
listeBoutonsPos =  new ArrayList <Button> ();


//init composants IHM
initSections();
initCombos();
initText();
initBoutons();
initListeType();
initLabels();


//boutons collapse all / expand all

formEdition.redraw();
formEdition.reflow(true);
//formEdition.

//bouton validate
buttonValidate.setParent(ViewEdit.formEdition.getBody());
buttonValidate.setText(Messages.getString("EditStation.4")); //$NON-NLS-1$

buttonRAZ.setParent(ViewEdit.formEdition.getBody());
buttonRAZ.setText("Reset"); //$NON-NLS-1$

buttonExpand.setParent(ViewEdit.formEdition.getBody());
buttonExpand.setText("+"); //$NON-NLS-1$

formEdition.reflow(true);

// initialisation liste des sections de la partie description fichier

//section reference station
DrawingStation.sectionIncrementeLigneDebutFinInject(Messages.getString("EditStation.5"), Messages.getString("EditStation.6"), EditStation.sectionStation, textRefStationLigne, textRefStationDebut, textRefStationFin, buttonRefStationIncremente, buttonRefStationPos); //$NON-NLS-1$ //$NON-NLS-2$



//section reference type de données
DrawingStation.sectionTypeDonnees(sectionTypeDonnees, Messages.getString("EditStation.0"));


//section historique acquisition
DrawingStation.sectionHistoriqueAcquisition(Messages.getString("EditStation.7"), Messages.getString("EditStation.8"), sectionHistorique, textHistoriqueAcqLigne1, textHistoriqueAcqDebut1, textHistoriqueAcqFin1, textHistoriqueAcqLigne2, textHistoriqueAcqDebut2, textHistoriqueAcqFin2, textHistorique1, textHistorique2, buttonHistoriqueAcqSaisie, buttonHistoriqueAcqPos ); //$NON-NLS-1$ //$NON-NLS-2$


//section commentaire
DrawingStation.sectionCommentaireLigneDebutFinInject(Messages.getString("EditStation.9"), Messages.getString("EditStation.10"), sectionCommentaire, textCommentaireLigne, textCommentaireDebut, textCommentaireFin, textCommentaire, buttonCommentaireSaisie, buttonCommentairePos); //$NON-NLS-1$ //$NON-NLS-2$


//section surface sample : identique à historique acquisition
DrawingStation.sectionSurfaceSample(Messages.getString("EditStation.11"), Messages.getString("EditStation.12"), sectionSurface, textSurfaceLigne1, textSurfaceDebut1, textSurfaceFin1, textSurfaceLigne2, textSurfaceDebut2, textSurfaceFin2, textSurface1, textSurface2, buttonSurfaceSaisie, buttonSurfacePos); //$NON-NLS-1$ //$NON-NLS-2$


//section décallage TU
DrawingStation.sectionManuelLigneDebutFinInject(Messages.getString("EditStation.86"), "", sectionTU, textTULigne, textTUDebut, textTUFin, textTU, buttonTUSaisie, buttonTUPos);
textTU.setText("0.0");

//section heure
DrawingStation.sectionHeure(Messages.getString("EditStation.13"), Messages.getString("EditStation.14"), sectionHeure, textHeureLigne, textHeureDebut, textHeureFin, textHeureSaisie, labelConversionHeure); //$NON-NLS-1$ //$NON-NLS-2$


//section date
DrawingStation.sectionDate(Messages.getString("EditStation.15"), Messages.getString("EditStation.16"), sectionDate, textDateLigne, textDateDebut, textDateFin, textDateSaisie, labelConversionDate); //$NON-NLS-1$ //$NON-NLS-2$

//flags QC
//DrawingStation.sectionDateQC(Messages.getString("EditStation.17"), Messages.getString("EditStation.18"), sectionQCDate); //$NON-NLS-1$ //$NON-NLS-2$

//section Latitude
DrawingStation.sectionManuelLigneDebutFinInjectLatLongComboFormat(Messages.getString("EditStation.19"), Messages.getString("EditStation.20"), sectionLatitude, textLatitudeLigne, textLatitudeDebut, textLatitudeFin, textLatitude, buttonLatSaisie, comboLatitude, buttonLatPos); //$NON-NLS-1$ //$NON-NLS-2$

if (ViewFile.checkBoxTempo.getSelection()){
	//section latitude de fin
	DrawingStation.sectionManuelLigneDebutFinInjectLatLongFin(Messages.getString("EditStation.21"), "", sectionLatFin,textLatFinLigne, textLatFinDebut, textLatFinFin, textLatFin, buttonLatFinSaisie, buttonLatFinPos); //$NON-NLS-1$ //$NON-NLS-2$
}

//section longitude
DrawingStation.sectionManuelLigneDebutFinInjectLatLongComboFormat(Messages.getString("EditStation.23"), Messages.getString("EditStation.24"), sectionLongitude, textLongitudeLigne, textLongitudeDebut, textLongitudeFin, textLongitude, buttonLongSaisie, comboLongitude, buttonLongPos); //$NON-NLS-1$ //$NON-NLS-2$

if (ViewFile.checkBoxTempo.getSelection()){
	//section longitude de fin
	DrawingStation.sectionManuelLigneDebutFinInjectLatLongFin(Messages.getString("EditStation.25"), "", sectionLongFin, textLongFinLigne, textLongFinDebut,textLongFinFin, textLongFin, buttonLongFinSaisie,  buttonLongFinPos);	 //$NON-NLS-1$ //$NON-NLS-2$
}


//section profondeur
DrawingStation.sectionManuelLigneDebutFinInject(Messages.getString("EditStation.27"), Messages.getString("EditStation.28"), sectionProfondeur, textProfondeurLigne, textProfondeurDebut, textProfondeurFin, textProfondeur, buttonProfondeurSaisie, buttonProfondeurPos); //$NON-NLS-1$ //$NON-NLS-2$

if (ViewFile.checkBoxTempo.getSelection()){
	//section profondeur de fin
	DrawingStation.sectionManuelLigneDebutFinInject(Messages.getString("EditStation.29"),"", sectionProfFin, textProfFinLigne, textProfFinDebut, textProfFinFin, textProfFin, buttonProfFinSaisie, buttonProfFinPos); //$NON-NLS-1$ //$NON-NLS-2$

	//section profondeur de capteur
	DrawingStation.sectionManuelLigneDebutFinInject(Messages.getString("EditStation.31"), Messages.getString("EditStation.32"), sectionProfCapteur, textProfCapteurLigne, textProfCapteurDebut, textProfCapteurFin, textProfCapteur, buttonProfCapteurSaisie, buttonProfCapteurPos); //$NON-NLS-1$ //$NON-NLS-2$

	//section déclinaison magnétique
	DrawingStation.sectionManuelLigneDebutFinInject(Messages.getString("EditStation.33"), Messages.getString("EditStation.34"), sectionDeclinMagnet, textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin, textDeclinMagnet, buttonDeclinMagnetSaisie, buttonDeclinMagnetPos); //$NON-NLS-1$ //$NON-NLS-2$
	
			  
	//if (!ViewFile.textTempoHeureRef.getText().matches("")){		  
	//section fréquence échantillonnage
	DrawingStation.sectionManuelLigneDebutFinInjectFreqEchant(Messages.getString("EditStation.35"), Messages.getString("EditStation.36"), sectionFreqEchant, textFreqEchantLigne, textFreqEchantDebut, textFreqEchantFin, textFreqEchant, buttonFreqEchantSaisie, buttonFreqEchantPos, textFreqEchantConv, textFreqEchantTest); //$NON-NLS-1$ //$NON-NLS-2$
	//}
}	



//section séries temporelles
//DrawingStation.sectionSerieTempo();


//buttonExpand2.setParent(ViewEdit.formEdition.getBody());
buttonExpand2.setText("+"); //$NON-NLS-1$


ViewEdit.formEdition.reflow(true);



//listeners




buttonExpand
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//System.out.println("expand/collapse all"); 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

//System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){

if (expand == 0){ 
EditStation.listeSectionsStation.get(i).setExpanded(true);

} 
else{
	EditStation.listeSectionsStation.get(i).setExpanded(false);



}

}
if (expand == 0) {
//System.out.println("expand passe à 1");
buttonExpand.setText("-"); //$NON-NLS-1$
buttonExpand2.setText("-"); //$NON-NLS-1$
expand = 1;}
else{expand = 0;
//System.out.println("expand passe à 0");
buttonExpand.setText("+"); //$NON-NLS-1$
buttonExpand2.setText("+"); //$NON-NLS-1$
} 

//refresh global
formEdition.reflow(true);

}
});
				  

buttonExpand2
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//System.out.println("expand/collapse all"); 
//raz de la valeur manuelle
int i;

Shell s = new Shell();

//System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){

if (expand == 0){ 
EditStation.listeSectionsStation.get(i).setExpanded(true);

} 
else{
	EditStation.listeSectionsStation.get(i).setExpanded(false);



}

}
if (expand == 0) {
System.out.println("expand passe à 1"); //$NON-NLS-1$
buttonExpand.setText("-"); //$NON-NLS-1$
buttonExpand2.setText("-"); //$NON-NLS-1$
expand = 1;}
else{expand = 0;
System.out.println("expand passe à 0"); //$NON-NLS-1$
buttonExpand.setText("+"); //$NON-NLS-1$
buttonExpand2.setText("+"); //$NON-NLS-1$
} 

//refresh global
formEdition.reflow(true);

}
});


buttonValidate
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//System.out.println("validation"); // 

//on passe en ppar l'étape à laquelle on se trouve
Etape.etape(2);	

Etape.validationStation();

formEdition.redraw();
formEdition.reflow(true);


}
});



buttonRAZ
.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		//System.out.println("RAZ"); // 
		
		razText(listeTextStation);
		

	}
});


//raz du message d'erreurs
Utils.messageErreur(""); //$NON-NLS-1$








//refresh du formulaire



formEdition.reflow(true);

//affichage courant
ViewFile.affiche = 2;

//remplissage de l'IHM par le modèle chargé ?
//System.out.println("Valeur de DataModel.ouverture = "+ DataModel.ouverture);			 
			 
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


//System.out.println("expand ? = " + expand);
for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){


EditStation.listeSectionsStation.get(i).setExpanded(true);
//System.out.println("===> " + EditStation.listeSectionsStation.get(i).getText());
EditStation.listeSectionsStation.get(i).setParent(s);
//System.out.println("effacement zone edition etape description station ==> ok");
}

//titre à vide
ViewEdit.formEdition.setText(""); //$NON-NLS-1$

//refresh global
ViewEdit.formEdition.reflow(true);



}


public static void DrawingBack(){
//	 reset de toute la zone d'édition après étape descriptioon de fichier
//	on retire toutes les sections et les boutons expand et validation

	ViewLog.aideStation();
	int i;

	ScrolledForm s;
	s = ViewEdit.formEdition;
//	raz message erreur
	Utils.messageErreur(""); //$NON-NLS-1$

//	effacement des boutons valider/expand/init serveur
	DataType dataType;
	initTypes();
	if (DataModel.ouverture){
		dataType = DataModel.station_modele.getDataType();
	}else{
	//re-remplissage et sélection du type de la station
	dataType = DataModel.station.getDataType();}
	String code = dataType.getCode();
	comboRefStationType.add(code, 0);
	comboRefStationType.select(0);
	
	
	EditStation.buttonValidate.setParent(s.getBody());
	EditStation.buttonRAZ.setParent(s.getBody());
	EditStation.buttonExpand.setParent(s.getBody());

	//System.out.println("expand ? = " + expand);
	for (i=0;i<=EditStation.listeSectionsStation.size()-1;i++){


	EditStation.listeSectionsStation.get(i).setExpanded(false);
	//System.out.println("===> " + EditStation.listeSectionsStation.get(i).getText());
	EditStation.listeSectionsStation.get(i).setParent(s.getBody());
	//System.out.println("reaffichage zone edition etape description station ==> ok");
	}

	//EditStation.buttonExpand2.setParent(s.getBody());
	
	ViewEdit.formEdition.setText(Messages.getString("EditStation.51")); //$NON-NLS-1$
	
	
//	refresh global
	ViewEdit.formEdition.reflow(true);

//	affichage courant
	ViewFile.affiche = 2;

	}



public static void initSections(){


Shell shell = new Shell();
sectionQCDate = new Section(shell, SWT.NONE);
sectionQCLat= new Section(shell, SWT.NONE);
sectionQCLong= new Section(shell, SWT.NONE);
sectionQCProf= new Section(shell, SWT.NONE);
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

textQCDate= new Text(shell, SWT.BORDER);listeTextStation.add(textQCDate);
textQCLat = new Text(shell, SWT.BORDER);listeTextStation.add(textQCLat);
textQCLong = new Text(shell, SWT.BORDER);listeTextStation.add(textQCLong);
textQCProf = new Text(shell, SWT.BORDER);listeTextStation.add(textQCProf);

textRefStationLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textRefStationLigne);
textRefStationDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textRefStationDebut);
textRefStationFin = new Text(shell, SWT.BORDER);listeTextStation.add(textRefStationFin);

textHistoriqueAcqLigne1= new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqLigne1);
textHistoriqueAcqLigne2= new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqLigne2);
textHistoriqueAcqDebut1 = new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqDebut1);
textHistoriqueAcqDebut2= new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqDebut2);
textHistoriqueAcqFin1= new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqFin1);
textHistoriqueAcqFin2 = new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcqFin2);
textHistorique1 = new Text(shell, SWT.BORDER);listeTextStation.add(textHistorique1);
textHistorique2  = new Text(shell, SWT.BORDER);listeTextStation.add(textHistorique2);
textHistoriqueAcquisition1 = new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcquisition1);
textHistoriqueAcquisition2 = new Text(shell, SWT.BORDER);listeTextStation.add(textHistoriqueAcquisition2);

textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);listeTextStation.add(textCommentaire);
textCommentaireLigne= new Text(shell, SWT.BORDER);listeTextStation.add(textCommentaireLigne);
textCommentaireDebut= new Text(shell, SWT.BORDER);listeTextStation.add(textCommentaireDebut);
textCommentaireFin = new Text(shell, SWT.BORDER);listeTextStation.add(textCommentaireFin);

textConversionDate  = new Text(shell, SWT.BORDER);listeTextStation.add(textConversionDate);
textConversionHeure  = new Text(shell, SWT.BORDER);listeTextStation.add(textConversionHeure);
textTestConvHeure = new Text(shell, SWT.BORDER);listeTextStation.add(textTestConvHeure);
textTestConvDate = new Text(shell, SWT.BORDER);listeTextStation.add(textTestConvDate);

textHeureLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textHeureLigne);
textHeureDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textHeureDebut);
textHeureFin  = new Text(shell, SWT.BORDER);listeTextStation.add(textHeureFin);
textHeureSaisie   = new Text(shell, SWT.BORDER);listeTextStation.add(textHeureSaisie);

textDateLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textDateLigne);
textDateDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textDateDebut);
textDateFin  = new Text(shell, SWT.BORDER);listeTextStation.add(textDateFin);
textDateSaisie   = new Text(shell, SWT.BORDER);listeTextStation.add(textDateSaisie);

textLatitude = new Text(shell, SWT.BORDER);listeTextStation.add(textLatitude);
textLatitudeLigne= new Text(shell, SWT.BORDER);listeTextStation.add(textLatitudeLigne);
textLatitudeDebut= new Text(shell, SWT.BORDER);listeTextStation.add(textLatitudeDebut);
textLatitudeFin = new Text(shell, SWT.BORDER);listeTextStation.add(textLatitudeFin);
textLatFinLigne= new Text(shell, SWT.BORDER);listeTextStation.add(textLatFinLigne);
textLatFinDebut= new Text(shell, SWT.BORDER);listeTextStation.add(textLatFinDebut);
textLatFinFin= new Text(shell, SWT.BORDER);listeTextStation.add(textLatFinFin);
textLatFin = new Text(shell, SWT.BORDER);listeTextStation.add(textLatFin);

textLongitude = new Text(shell, SWT.BORDER);listeTextStation.add(textLongitude);
textLongitudeLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textLongitudeLigne);
textLongitudeDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textLongitudeDebut);
textLongitudeFin  = new Text(shell, SWT.BORDER);listeTextStation.add(textLongitudeFin);
textLongFinLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textLongFinLigne);
textLongFinDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textLongFinDebut);
textLongFinFin = new Text(shell, SWT.BORDER);listeTextStation.add(textLongFinFin);
textLongFin = new Text(shell, SWT.BORDER);listeTextStation.add(textLongFin);

textProfondeur = new Text(shell, SWT.BORDER);listeTextStation.add(textProfondeur);
textProfondeurLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textProfondeurLigne);
textProfondeurDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textProfondeurDebut);
textProfondeurFin = new Text(shell, SWT.BORDER);listeTextStation.add(textProfondeurFin);
textProfFin = new Text(shell, SWT.BORDER);listeTextStation.add(textProfFin);
textProfFinLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textProfFinLigne);
textProfFinDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textProfFinDebut);
textProfFinFin = new Text(shell, SWT.BORDER);listeTextStation.add(textProfFinFin);
textProfCapteur = new Text(shell, SWT.BORDER);listeTextStation.add(textProfCapteur);
textProfCapteurLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textProfCapteurLigne);
textProfCapteurDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textProfCapteurDebut);
textProfCapteurFin = new Text(shell, SWT.BORDER);listeTextStation.add(textProfCapteurFin);

textDeclinMagnet = new Text(shell, SWT.BORDER);listeTextStation.add(textDeclinMagnet);
textDeclinMagnetLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textDeclinMagnetLigne);
textDeclinMagnetDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textDeclinMagnetDebut);
textDeclinMagnetFin = new Text(shell, SWT.BORDER);listeTextStation.add(textDeclinMagnetFin);

textFreqEchant = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchant);
textFreqEchantLigne = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchantLigne);
textFreqEchantDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchantDebut);
textFreqEchantFin = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchantFin);
textFreqEchantConv = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchantConv);
textFreqEchantTest = new Text(shell, SWT.BORDER);listeTextStation.add(textFreqEchantTest);


textSurface1 = new Text(shell, SWT.BORDER);listeTextStation.add(textSurface1);
textSurface2= new Text(shell, SWT.BORDER);listeTextStation.add(textSurface2);
textSurfaceDebut1= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceDebut1);
textSurfaceDebut2= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceDebut2);
textSurfaceFin1= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceFin1);
textSurfaceFin2= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceFin2);
textSurfaceLigne1= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceLigne1);
textSurfaceLigne2= new Text(shell, SWT.BORDER);listeTextStation.add(textSurfaceLigne2);

textTU = new Text(shell, SWT.BORDER);listeTextStation.add(textTU);
textTULigne = new Text(shell, SWT.BORDER);listeTextStation.add(textTULigne);
textTUDebut = new Text(shell, SWT.BORDER);listeTextStation.add(textTUDebut);
textTUFin = new Text(shell, SWT.BORDER);listeTextStation.add(textTUFin);

//remplissage de la liste des textes et ajout listeners
ajoutModifyListenerText(listeTextStation);





}	


public static void ajoutModifyListenerText(ArrayList <Text> liste){
	int taille = liste.size();
	for (int i = 0; i<taille;i++){
	liste.get(i).addModifyListener(new ModifyListener(){
	      public void modifyText(ModifyEvent event) {
	        // on modifie la valeur du champ texte
	        //Text text = (Text) event.widget;
	        //System.out.println(text.getText());
	    	  
	    	//dès qu'on modifie une valeur, on invalide l'étape
//	    	remise du nom onglet à valeur par défaut
	    					    		
	    		//on invalide toutes les étapes suivantes avec le second flag okApresReset à false
	    		EditStation.okApresReset = false;
	    		ViewFile.tabItem2.setText(Messages.getString("EditStation.1"));
	    		EditData.okApresReset = false;
	    		ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
	    		Utils.messageErreur("");
	    	  
	      }
	    });
	
	}
}





public static void initBoutons(){

Shell shell = new Shell();

buttonRAZ = new Button(shell, SWT.NONE);
buttonExpand = new Button(shell, SWT.NONE);
buttonExpand2 = new Button(shell, SWT.NONE);
buttonValidate = new Button(shell, SWT.NONE);

buttonHistoriqueAcqSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonHistoriqueAcqSaisie);
buttonHistoriqueAcqPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonHistoriqueAcqPos);

buttonRefStationIncremente = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonRefStationIncremente);
buttonRefStationPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonRefStationPos);

buttonSurfaceSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonSurfaceSaisie);
buttonSurfacePos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonSurfacePos);

buttonTUSaisie  = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonTUSaisie);
buttonTUPos  = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonTUPos);

buttonLatSaisie  = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonLatSaisie);
buttonLatPos  = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonLatPos);
buttonLatFinPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonLatFinPos);
buttonLatFinSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonLatFinSaisie);

buttonLongSaisie  = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonLongSaisie);
buttonLongPos  = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonLongPos);
buttonLongFinPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonLongFinPos);
buttonLongFinSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonLongFinSaisie);

buttonCommentaireSaisie  = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonCommentaireSaisie);
buttonCommentairePos  = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonCommentairePos);

buttonProfondeurSaisie  = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonProfondeurSaisie);
buttonProfondeurPos  = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonProfondeurPos);
buttonProfFinPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonProfFinPos);
buttonProfFinSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonProfFinSaisie);

buttonProfCapteurPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonProfCapteurPos);
buttonProfCapteurSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonProfCapteurSaisie);

buttonDeclinMagnetPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonDeclinMagnetPos);
buttonDeclinMagnetSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonDeclinMagnetSaisie);

buttonFreqEchantPos = new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonFreqEchantPos);
buttonFreqEchantSaisie = new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonFreqEchantSaisie);

buttonDateReference= new Button(shell, SWT.RADIO);
buttonHeureReference= new Button(shell, SWT.RADIO);

buttonHeureSaisie= new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonHeureSaisie);

buttonDateSaisie= new Button(shell, SWT.RADIO);listeBoutonsManuel.add(buttonDateSaisie);

buttonHeurePos= new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonHeurePos);
buttonDatePos= new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonDatePos);
buttonHeureReferencePos =  new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonHeureReferencePos);
buttonDateReferencePos =  new Button(shell, SWT.RADIO);listeBoutonsPos.add(buttonDateReferencePos);
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

/*
public static void initComboType(){
	//remplissage de la combobox avec code + libellé séparés par " - " ==> "HO9 - Bouteilles"
	int taille;
	taille = DataModel.listeWSDataTypes.length;
		
	for (int i =0; i<taille; i++){
		String chaine = ""; //$NON-NLS-1$
		
		chaine = chaine + DataModel.listeWSDataTypes[i].getCode() + " - " + DataModel.listeWSDataTypes[i].getLibelle(); //$NON-NLS-1$
		comboRefStationType.add(chaine);
		
			}
	
} 
*/

public static void initTypes(){
	//Shell shell = new Shell();
	
	comboRefStationType.removeAll();
	//comboRefStationType = new Combo(shell, SWT.READ_ONLY);
	//remplissage avec les dataTypes de la campagne
	
	int taille = TableDataSurvey.types.size();
	for (int i=0;i<taille;i++){
		//on n'ajoute que les sensors
		if (TableDataSurvey.types.get(i).getSensor()){
		comboRefStationType.add(TableDataSurvey.types.get(i).getCode());
		}
	}	
}

public static void initCombos(){
	
	Shell shell = new Shell();
	comboRefStationType = new Combo(shell, SWT.READ_ONLY);
	//remplissage avec les dataTypes de la campagne
	
	int taille = TableDataSurvey.types.size();
	for (int i=0;i<taille;i++){
		//on n'ajoute que les sensors
		if (TableDataSurvey.types.get(i).getSensor()){
		comboRefStationType.add(TableDataSurvey.types.get(i).getCode());
		}
	}
	
	
	comboHeure = new Combo(shell, SWT.NONE);
	comboHeure.add("hh24:mm"); //$NON-NLS-1$
	comboHeure.add("hh24mmss"); //$NON-NLS-1$
	comboHeure.add("hh24mm"); //$NON-NLS-1$
	comboHeure.add("hh:mm:ss"); //$NON-NLS-1$
	comboHeure.add("hh:mm"); //$NON-NLS-1$
	comboHeure.add("hh24:mm:ss"); //$NON-NLS-1$
	

	comboDate = new Combo(shell, SWT.NONE);
	comboDate.add("dd/MM/yy"); //$NON-NLS-1$
	comboDate.add("dd/MM/yyyy"); //$NON-NLS-1$
	comboDate.add("ddMMyyyy"); //$NON-NLS-1$
	comboDate.add("ddMMyy"); //$NON-NLS-1$
	comboDate.add("MMddyy"); //$NON-NLS-1$
	comboDate.add("yyddMM"); //$NON-NLS-1$
	comboDate.add("dd:MM:yyyy"); //$NON-NLS-1$
	comboDate.add("dd:MM:yy"); //$NON-NLS-1$
	
	
	comboLatitude = new Combo(shell, SWT.NONE);
	comboLatitude.add("NDD MM.mm"); //$NON-NLS-1$
	comboLatitude.add("DDD MM ss.s"); //$NON-NLS-1$
	comboLatitude.add("DDD MM.mmm"); //$NON-NLS-1$
	comboLatitude.add("DDD.DDDDD"); //$NON-NLS-1$
	comboLatitude.add("DDD.DD"); //$NON-NLS-1$
	comboLatitude.add("+DD.mmm"); //$NON-NLS-1$
	
	comboLongitude = new Combo(shell, SWT.NONE);
	comboLongitude.add("NDDD MM.mm"); //$NON-NLS-1$
	comboLongitude.add("DDD MM ss.s"); //$NON-NLS-1$
	comboLongitude.add("DDD MM.mmm"); //$NON-NLS-1$
	comboLongitude.add("DDD.DDDDD"); //$NON-NLS-1$
	comboLongitude.add("DDD.DD"); //$NON-NLS-1$
	comboLongitude.add("+DDD.mmm"); //$NON-NLS-1$

}

public static void razText(ArrayList <Text> liste){
	
	for (int i =0; i< liste.size(); i++){
		liste.get(i).setText(""); //$NON-NLS-1$
				
	}
	textTU.setText("0.0");
	EditStation.okApresReset = false;
	ViewFile.tabItem2.setText(Messages.getString("EditStation.1"));
	EditData.okApresReset = false;
	ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
	Utils.messageErreur("");
	
	//boutons manuels sélectionnés par défaut
	int nbreManu = listeBoutonsManuel.size();
	int nbrePos = listeBoutonsPos.size();

	
	for (int i=0;i<nbrePos;i++){
		listeBoutonsPos.get(i).setSelection(false);
	}
	
	for (int i=0;i<nbreManu;i++){
		listeBoutonsManuel.get(i).setSelection(true);
	}

	
}

public static void conversionFreqEchant(){
	
	if (!textFreqEchantConv.getText().matches("")){ //$NON-NLS-1$
		//il y a une formule de conversion
		
		if ((buttonFreqEchantSaisie.getSelection()) && (!textFreqEchant.getText().matches(""))){ //$NON-NLS-1$
			//saisie manuelle 
			
			
			org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
			parser.addStandardFunctions();
			parser.addStandardConstants();
			parser.addVariable("x", Float.valueOf(textFreqEchant.getText())); //$NON-NLS-1$
			parser.parseExpression(textFreqEchantConv.getText());
			double resultJEP = parser.getValue();
			
			//String resultat = String.format(ViewFile.tableData.getItem(i).getText(6), resultJEP);
			
			EditStation.textFreqEchantTest.setText(""+resultJEP); //$NON-NLS-1$
			
		}
		if ((buttonFreqEchantPos.getSelection())&&(Regexp.checkLigneDebutfin(textFreqEchantLigne, textFreqEchantDebut, 
				textFreqEchantFin, Messages.getString("EditStation.85")))){ //$NON-NLS-1$
			//position dans fichier
			
			//récupérer le texte en question et creer une nouvelle heure
			String ligne = Utils.lignesBrutes.get(Integer.parseInt(EditStation.textFreqEchantLigne.getText())-1);
			String string = ligne.substring(Integer.parseInt(EditStation.textFreqEchantDebut.getText())-1, Integer.parseInt(EditStation.textFreqEchantFin.getText()));
			
			org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
			parser.addStandardFunctions();
			parser.addStandardConstants();
			parser.addVariable("x", Float.valueOf(string)); //$NON-NLS-1$
			parser.parseExpression(textFreqEchantConv.getText());
			double resultJEP = parser.getValue();
			
			//String resultat = String.format(ViewFile.tableData.getItem(i).getText(6), resultJEP);
			
			EditStation.textFreqEchantTest.setText(""+resultJEP); //$NON-NLS-1$
			
		}
		
		
	}
	
	
	
}


}
