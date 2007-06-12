package fr.ifremer.nemo.ui;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.rpc.ServiceException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Survey;



public class EditSurvey {
// fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

	public static Section sectionReference, sectionLocalisation, sectionSource, sectionCentre, sectionType, sectionCommentaire;
	public static Text textCampagne, textNom, textCodeNavire, textNomNavire, textDateDebut, textDateFin, textRegion, textPays, textLabo, textChef, textProjet, textCentre, textConfidentiel, textType, textNbre, textCommentaire;
	public static Label labelCampagne, labelNom, labelCodeNavire, labelNomNavire, labelDateDebut, labelDateFin, labelRegion, labelPays, labelLabo, labelChef,labelProjet, labelCentre, labelConfidentiel, labelType, labelNbre;
	public static Button buttonRAZ, buttonInitServeur, buttonValidate, buttonExpand, buttonExpand2;
	public static ArrayList <Section> listeSectionsSurvey, listeSectionsParam;
	public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
	public static ArrayList <Label> listeSectionsLabel;
	public static ArrayList <Text> listeTextSurvey;
	public static int expand, validee, loaded;
	public static List listeType;
	public static Boolean ws;
	public static ScrolledForm formEdition;

//////////////////////////////////////////	//
		
	
		public static void affichageEdition(){
			// initialisation zone édition pour étape description de fichier 
		
			formEdition = ViewEdit.formEdition;
			
			ws = false;
			
			//affichage du texte dans la zone de gauche
			
			//Utils.remplissageTexte(ViewFile.styledTextCampagne);
			ViewFile.styledTextCampagne.setText(Utils.texteBrut);
			
//			raz message erreur
			Utils.messageErreur("");
			
			//de base les sections sont refermées
			expand = 0;
			
			loaded = 1;
			
			/* recreer un formEdition sur le toolkit principal
			 * pour pouvoir faire un dispose() dessus mais garder toutes les sections 
			 * afin de les réafficher par la suite dans DrawingBack.
			 */
			
			//ViewEdit.formEdition.dispose();
			
			//formEdition = ViewEdit.toolkitEdition.createScrolledForm(ViewEdit.top);
			ViewEdit.formEdition.getBody().setLayout(new GridLayout());
			ViewEdit.formEdition.setText("Description de campagne");
			
			
//			init de la arrayList des sections
			listeSectionsSurvey = new ArrayList <Section> ();
			listeSectionsNoms = new ArrayList <String> ();
			listeTextSurvey = new ArrayList <Text> ();
			ArrayList <Text> listeText = new ArrayList <Text> ();
			ArrayList <String> listeNoms = new ArrayList <String> ();
			
			
			//init composants IHM
			initSections();
			initText();
			initBoutons();
			initListeType();
			
			
			
			
			
			//boutons collapse all / expand all
			
			buttonExpand.setParent(ViewEdit.formEdition.getBody());
			buttonExpand.setText("+");
					
			buttonRAZ.setParent(ViewEdit.formEdition.getBody());
			buttonRAZ.setText("Reset");
			
			formEdition.redraw();
			formEdition.reflow(true);
			//formEdition.
		
			//bouton serveur
		buttonInitServeur.setParent(ViewEdit.formEdition.getBody());
		buttonInitServeur.setText("Initialisation serveur");
		
//		bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText("Valider étape");
		
		formEdition.reflow(true);
				
		// initialisation   liste des sections de la partie description fichier

		//section référence
		listeText.add(textCampagne); listeNoms.add("Campagne");
		listeText.add(textNom); listeNoms.add("Nom");
		listeText.add(textCodeNavire); listeNoms.add("Code Navire");
		listeText.add(textNomNavire); listeNoms.add("Nom Navire");
		
		Drawing.sectionNlabelsNtext(sectionReference, "Référence                              ", listeText, listeNoms, "Décrivez les références de la campagne");	
		listeText.clear();	
		listeNoms.clear();
		
		
		
			
		//section localisation		
		listeText.add(textDateDebut); listeNoms.add("Date de début");
		listeText.add(textDateFin); listeNoms.add("Date de fin");
		listeText.add(textRegion); listeNoms.add("Région");
		Drawing.sectionNlabelsNtext(sectionLocalisation, "Localisation                           ", listeText, listeNoms, "Décrivez la localisation de la campagne");	
		listeText.clear();
		listeNoms.clear();
			
		/*
		Text label = new Text(ViewEdit.formEdition.getBody(), SWT.BORDER);
		label.setText("hop");
		*/
		
		ViewEdit.formEdition.reflow(true);
		
		
		//section source des donnees
		listeText.add(textPays); listeNoms.add("Pays");
		listeText.add(textLabo); listeNoms.add("Laboratoire");
		listeText.add(textChef); listeNoms.add("Chef de mission");
		listeText.add(textProjet); listeNoms.add("Projet");
		Drawing.sectionNlabelsNtext(sectionSource, "Source des données                     ", listeText, listeNoms, "Décrivez la provenance des données de la campagne");	
		listeText.clear();	
		listeNoms.clear();
			
		//section centre archivage
		listeText.add(textCentre); listeNoms.add("Centre de données");
		listeText.add(textConfidentiel); listeNoms.add("Confidentialité");
		Drawing.sectionNlabelsNtext(sectionCentre, "Centre d'archivage                     ", listeText, listeNoms, "Décrivez le centre d'archivage des données de la campagne");	
		listeText.clear();	
		listeNoms.clear();
									  
		
		
		//section types de données
		listeText.add(textNbre); listeNoms.add("Nombre");
		listeText.add(textType); listeNoms.add("Type de données");
		Drawing.sectionTypeDonnees(sectionType, "Type de données                        ", listeText, listeNoms, "Ajoutez ou modifiez les types des données mesurées pendant la campagne", listeType);
		
		
		//section commentaire
		Drawing.sectionCommentaireSurvey(sectionCommentaire, "Commentaires éventuels                 ", textCommentaire, "Commentaire éventuel");
		

		buttonExpand2.setParent(ViewEdit.formEdition.getBody());
		buttonExpand2.setText("+");
		

//	listeners




	buttonExpand
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("expand/collapse all"); // 
			//raz de la valeur manuelle
			int i;
			
			Shell s = new Shell();
			
			System.out.println("expand ? = " + expand);
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				if (expand == 0){ 
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
					
				} 
				else{
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
					
					

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
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				if (expand == 0){ 
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
					
				} 
				else{
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
					
					

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
			
			Etape.validationSurvey();
			
			 formEdition.redraw();
			 formEdition.reflow(true);
			
			
		}
	});

	
	buttonRAZ
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("RAZ"); // 
			
			razText(listeTextSurvey);
			

		}
	});
	
	
	buttonInitServeur
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			System.out.println("WebService"); 
				init_webService();
			}
	});
	
	
	//raz du message d'erreurs
	Utils.messageErreur("");
	
	
	
		  
//		refresh du formulaire
							  
							  
						  
					 formEdition.reflow(true);
					 
					 
					 //affichage courant
					 ViewFile.affiche = 1;
					 
//					remplissage de l'IHM par le modèle chargé ?
		System.out.println("Valeur de DataModel.ouverture = "+ DataModel.ouverture);			 
					 
		 if (DataModel.ouverture){
			 DataModel.fillSurvey();
							 }
		
		}
		
		
		
		public static void webServiceUpdate(){
			
			//chargement de la config dans le dataModel
			//TODO à faire !
			
			//interrogation WService
			NemoLists.updateCountries();
			NemoLists.updateDataTypes();
			NemoLists.updateRegions();
		
			NemoLists.updateShips();
			NemoLists.updateDataCentres();
			NemoLists.updateParameters();
			NemoLists.updateSurveys();
			
		}
		
		public static void init_webService(){
			//sélection de la ref campagne dans une liste 
		ws = true;
		FiltrageTableType.go();
			
		}
		
		public static void webService(String reference){
			//remplissage du dataModel à partir des valeurs du ws
			
			if (ViewFile.dossierCampagne){
				try {
					DataModel.dirSurvey.getModel().fillFromSISMER(reference);
				} catch (MalformedURLException e) {
					// TODO Bloc catch auto-généré
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Bloc catch auto-généré
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Bloc catch auto-généré
					e.printStackTrace();
				}
			}else{
			try {
				DataModel.singleSurvey.getModel().fillFromSISMER(reference);
			} catch (MalformedURLException e) {
				// TODO Bloc catch auto-généré
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Bloc catch auto-généré
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Bloc catch auto-généré
				e.printStackTrace();
			}
			}
			
			//mise à jour de l'onglet courant
			majApresWS();
			
			
		}
		
		public static void majApresWS(){
			System.out.println("Remplissage onglet survey");
			
			
			Survey survey; 
			if (ViewFile.dossierCampagne){
				survey = DataModel.dirSurvey.getModel();
			} else {
				survey = DataModel.singleSurvey.getModel();	
			}
			 
			//station = stationModel.getModel();
			
			//injection des valeurs de l'IHM -> datamodel
			//référence
			EditSurvey.textCampagne.setText(survey.getReference());
			EditSurvey.textNom.setText(survey.getName());
			EditSurvey.textCodeNavire.setText(survey.getShipCode());
			EditSurvey.textNomNavire.setText(survey.getShipName());
			
			//Localisation
			EditSurvey.textDateDebut.setText(""+survey.getStartDate());
			EditSurvey.textDateFin.setText(""+survey.getEndDate());
			EditSurvey.textRegion.setText(survey.getRegionName());
			
			//Source de données
			EditSurvey.textPays.setText(survey.getCountryCode());
			EditSurvey.textLabo.setText(survey.getLaboratories());
			EditSurvey.textChef.setText(survey.getChiefScientist());
			EditSurvey.textProjet.setText(survey.getProjectName());
			
			
			//Centre d'archivage
			EditSurvey.textCentre.setText(survey.getDataCentre());
			EditSurvey.textConfidentiel.setText(""+survey.getAvailability());
			
			//Commentaire
			EditSurvey.textCommentaire.setText(survey.getComment());
			
			//Remplissage de la liste des dataTypes de la campagne
			
			DataType[] dataTypes = survey.getDataTypes();
			int taille = dataTypes.length;
			
			for (int i=0; i<taille;i++){
				TypeSurvey typeSurvey = new TypeSurvey();
				typeSurvey.setCode(dataTypes[i].getCode());
				typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());
				typeSurvey.setQc(""+dataTypes[i].isQc());
				typeSurvey.setSensor(dataTypes[i].isSensor());
				
				TableDataSurvey.types.add(typeSurvey);
				
				System.out.println("TypeSurvey " +typeSurvey.getCode()+" ajouté");
				
			}
			
			
			EditSurvey.razEdition();
			EditSurvey.DrawingBack();
			
			//////////////////////////////////////////
			//refresh de la liste des dataTypes
			TableDataSurvey.razTableData();
			TableDataSurvey.creationTable();
			Composite c = EditSurvey.sectionType;
		    while (c != null) {
				c.setRedraw(false);
				c = c.getParent();
				if (c instanceof ScrolledForm) {
					break;
				}
			}
			c = EditSurvey.sectionType;
			while (c != null) {
				c.layout(true);
				c = c.getParent();
				if (c instanceof ScrolledForm) {
					((ScrolledForm) c).reflow(true);
					break;
				}
			}
			c = EditSurvey.sectionType;
			while (c != null) {
				c.setRedraw(true);
				c = c.getParent();
				if (c instanceof ScrolledForm) {
					break;
				}
			}
			
			//////////////////////////////////////////
			
			System.out.println("Fin remplissage onglet Survey");
			
			
		}
		
		public static void razEdition(){
			// reset de toute la zone d'édition après étape descriptioon de fichier
			//on retire toutes les sections et les boutons expand et validation
			
			
		int i;
			
			Shell s = new Shell();
			
			//effacement des boutons valider/expand/init serveur
			EditSurvey.buttonExpand.setParent(s);
			EditSurvey.buttonExpand2.setParent(s);
			EditSurvey.buttonValidate.setParent(s);
			EditSurvey.buttonInitServeur.setParent(s);
			EditSurvey.buttonRAZ.setParent(s);
			
			
			System.out.println("expand ? = " + expand);
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				
				EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
				System.out.println("===> " + EditSurvey.listeSectionsSurvey.get(i).getText());
				EditSurvey.listeSectionsSurvey.get(i).setParent(s);
				System.out.println("effacement zone edition etape description campagne ==> ok");
				}
		
			
			//effacement titre formulaire
			ViewEdit.formEdition.setText("");
			
			//refresh global
			ViewEdit.formEdition.reflow(true);
			
			
			
		}
		
	
		public static void DrawingBack(){
			// reset de toute la zone d'édition après étape descriptioon de fichier
			//on retire toutes les sections et les boutons expand et validation
			
			
		int i;
			ScrolledForm s;
			s = ViewEdit.formEdition;
//			raz message erreur
			Utils.messageErreur("");
			
			//reaffichage des boutons valider/expand/init serveur
			EditSurvey.buttonExpand.setParent(s.getBody());
			
			EditSurvey.buttonValidate.setParent(s.getBody());
			EditSurvey.buttonInitServeur.setParent(s.getBody());
			EditSurvey.buttonRAZ.setParent(s.getBody());
			
			
			System.out.println("expand ? = " + expand);
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				
				EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
				System.out.println("===> " + EditSurvey.listeSectionsSurvey.get(i).getText());
				EditSurvey.listeSectionsSurvey.get(i).setParent(s.getBody());
				System.out.println("re-affichage zone edition etape description campagne ==> ok");
				}
		
			EditSurvey.buttonExpand2.setParent(s.getBody());
			
			
			ViewEdit.formEdition.setText("Description de Campagne");
			
			//refresh global
			ViewEdit.formEdition.reflow(true);
			
//			affichage courant
			ViewFile.affiche = 1;
			
		}	
		
		
	public static void initSections(){
			

		Shell shell = new Shell();	

		sectionReference = new Section(shell, SWT.NONE);
		sectionLocalisation = new Section(shell, SWT.NONE);
		sectionSource = new Section(shell, SWT.NONE);
		sectionCentre = new Section(shell, SWT.NONE);
		sectionType= new Section(shell, SWT.NONE);
		sectionCommentaire= new Section(shell, SWT.NONE);
			
		}
		
		
	public static void initText(){
		
		Shell shell = new Shell();
		
		textCampagne= new Text(shell, SWT.BORDER);
		textNom = new Text(shell, SWT.BORDER);
		textCodeNavire = new Text(shell, SWT.BORDER);
		textNomNavire = new Text(shell, SWT.BORDER);
		textDateDebut = new Text(shell, SWT.BORDER);
		textDateFin = new Text(shell, SWT.BORDER);
		textRegion = new Text(shell, SWT.BORDER);
		textPays = new Text(shell, SWT.BORDER);
		textLabo = new Text(shell, SWT.BORDER);
		textChef= new Text(shell, SWT.BORDER);
		textProjet = new Text(shell, SWT.BORDER);
		textCentre = new Text(shell, SWT.BORDER);
		textConfidentiel = new Text(shell, SWT.BORDER);
		textType = new Text(shell, SWT.BORDER);
		textNbre = new Text(shell, SWT.BORDER);
		textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		
		//remplissage de la liste des textes
		listeTextSurvey.add(textCampagne);
		listeTextSurvey.add(textNom);
		listeTextSurvey.add(textCodeNavire);
		listeTextSurvey.add(textNomNavire);
		listeTextSurvey.add(textDateDebut);
		listeTextSurvey.add(textDateFin);
		listeTextSurvey.add(textRegion);
		listeTextSurvey.add(textPays);
		listeTextSurvey.add(textLabo);
		listeTextSurvey.add(textChef);
		listeTextSurvey.add(textProjet);
		listeTextSurvey.add(textCentre);
		listeTextSurvey.add(textConfidentiel);
		listeTextSurvey.add(textType);
		listeTextSurvey.add(textNbre);
		listeTextSurvey.add(textCommentaire);
		
		
	}	
		
	public static void initBoutons(){
		
		Shell shell = new Shell();
		buttonRAZ = new Button(shell, SWT.NONE);
		buttonInitServeur = new Button(shell, SWT.NONE);
		buttonExpand = new Button(shell, SWT.NONE);
		buttonExpand2 = new Button(shell, SWT.NONE);
		buttonValidate = new Button(shell, SWT.NONE);
				
	}
		

	public static void initListeType(){
		
		Shell shell = new Shell();
		listeType = new List(shell, SWT.BORDER);
		
		
	}
	
	public static void razText(ArrayList <Text> liste){
		
		for (int i =0; i< liste.size(); i++){
			liste.get(i).setText("");
					
		}
		
	}
	
	
	
}
