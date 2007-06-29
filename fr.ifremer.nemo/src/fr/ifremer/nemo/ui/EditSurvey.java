package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
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
import org.xml.sax.SAXException;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.util.Formatter;



public class EditSurvey {
// fonctions de remplissage de la zone d'édition pour les campagnes

///////////////////////////////////////////

	public static Section sectionReference, sectionLocalisation, sectionSource, sectionCentre, sectionType, sectionCommentaire;
	public static Text textCampagne, textNom, textCodeNavire, textNomNavire, textDateDebut, textDateFin, textRegion, textPays, textLabo, textChef, textProjet, textCentre, textConfidentiel, textType, textNbre, textCommentaire;
	public static Label labelCampagne, labelNom, labelCodeNavire, labelNomNavire, labelDateDebut, labelDateFin, labelRegion, labelPays, labelLabo, labelChef,labelProjet, labelCentre, labelConfidentiel, labelType, labelNbre;
	public static Button buttonRAZ, buttonInitServeur, buttonValidate, buttonXML, buttonExpand, buttonExpand2;
	public static ArrayList <Section> listeSectionsSurvey, listeSectionsParam;
	public static ArrayList <String> listeSectionsNoms, listeTypeDonnees;
	public static ArrayList <Label> listeSectionsLabel;
	public static ArrayList <Text> listeTextSurvey;
	public static int expand, validee, loaded;
	public static List listeType;
	public static Boolean ws;
	public static ScrolledForm formEdition;
	public static Shell shell;

//////////////////////////////////////////	//
		
	
		public static void affichageEdition(){
			// initialisation zone édition pour étape description de fichier 
		
			formEdition = ViewEdit.formEdition;
			
			ws = false;
			
			//affichage du texte dans la zone de gauche
			
			//Utils.remplissageTexte(ViewFile.styledTextCampagne);
			ViewFile.styledTextCampagne.setText(Utils.texteBrut);
			
//			raz message erreur
			Utils.messageErreur(""); //$NON-NLS-1$
			
			//de base les sections sont refermées
			expand = 0;
			
			if (loaded ==0){
			
			//INFOS sur les fichiers utilisés
			if (ViewFile.radioFichier.getSelection()){
				//fichier campagne
				Utils.messageInfoAppend(Messages.getString("EditSurvey.1")); //$NON-NLS-1$
			}else{
				//dossier campagne
				Utils.messageInfoAppend(Messages.getString("EditSurvey.2")+ViewFile.nomRepertoire); //$NON-NLS-1$
			}
			
			if (ViewFile.checkBoxTempo.getSelection()){
				Utils.messageInfoAppend(Messages.getString("EditSurvey.3")); //$NON-NLS-1$
			}
			
			}
			loaded = 1;
			
			/* recreer un formEdition sur le toolkit principal
			 * pour pouvoir faire un dispose() dessus mais garder toutes les sections 
			 * afin de les réafficher par la suite dans DrawingBack.
			 */
			
			//ViewEdit.formEdition.dispose();
			
			//formEdition = ViewEdit.toolkitEdition.createScrolledForm(ViewEdit.top);
			ViewEdit.formEdition.getBody().setLayout(new GridLayout());
			ViewEdit.formEdition.setText(Messages.getString("EditSurvey.4")); //$NON-NLS-1$
			
			
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
			
			//buttonExpand.setParent(ViewEdit.formEdition.getBody());
			buttonExpand.setText("+"); //$NON-NLS-1$
					
			buttonRAZ.setParent(ViewEdit.formEdition.getBody());
			buttonRAZ.setText("Reset"); //$NON-NLS-1$
			
			formEdition.redraw();
			formEdition.reflow(true);
			//formEdition.
		
			//bouton serveur
		buttonInitServeur.setParent(ViewEdit.formEdition.getBody());
		buttonInitServeur.setText(Messages.getString("EditSurvey.7")); //$NON-NLS-1$
		
			//bouton XML
		buttonXML.setParent(ViewEdit.formEdition.getBody());
		buttonXML.setText(Messages.getString("EditSurvey.8")); //$NON-NLS-1$
		
//		bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditSurvey.9")); //$NON-NLS-1$
		
		formEdition.reflow(true);
				
		// initialisation   liste des sections de la partie description fichier

		//section référence
		listeText.add(textCampagne); listeNoms.add(Messages.getString("EditSurvey.10")); //$NON-NLS-1$
		listeText.add(textNom); listeNoms.add(Messages.getString("EditSurvey.11")); //$NON-NLS-1$
		listeText.add(textCodeNavire); listeNoms.add(Messages.getString("EditSurvey.12")); //$NON-NLS-1$
		listeText.add(textNomNavire); listeNoms.add(Messages.getString("EditSurvey.13")); //$NON-NLS-1$
		
		Drawing.sectionNlabelsNtext(sectionReference, Messages.getString("EditSurvey.14"), listeText, listeNoms, Messages.getString("EditSurvey.15"));	 //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();	
		listeNoms.clear();
		
		
			
		//section localisation		
		listeText.add(textDateDebut); listeNoms.add(Messages.getString("EditSurvey.16")); //$NON-NLS-1$
		listeText.add(textDateFin); listeNoms.add(Messages.getString("EditSurvey.17")); //$NON-NLS-1$
		listeText.add(textRegion); listeNoms.add(Messages.getString("EditSurvey.18")); //$NON-NLS-1$
		Drawing.sectionNlabelsNtext(sectionLocalisation, Messages.getString("EditSurvey.19"), listeText, listeNoms, Messages.getString("EditSurvey.20"));	 //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();
		listeNoms.clear();
			
		/*
		Text label = new Text(ViewEdit.formEdition.getBody(), SWT.BORDER);
		label.setText("hop");
		*/
		
		ViewEdit.formEdition.reflow(true);
		
		
		//section source des donnees
		listeText.add(textPays); listeNoms.add(Messages.getString("EditSurvey.21")); //$NON-NLS-1$
		listeText.add(textLabo); listeNoms.add(Messages.getString("EditSurvey.22")); //$NON-NLS-1$
		listeText.add(textChef); listeNoms.add(Messages.getString("EditSurvey.23")); //$NON-NLS-1$
		listeText.add(textProjet); listeNoms.add(Messages.getString("EditSurvey.24")); //$NON-NLS-1$
		Drawing.sectionNlabelsNtext(sectionSource, Messages.getString("EditSurvey.25"), listeText, listeNoms, Messages.getString("EditSurvey.26"));	 //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();	
		listeNoms.clear();
			
		//section centre archivage
		listeText.add(textCentre); listeNoms.add(Messages.getString("EditSurvey.27")); //$NON-NLS-1$
		listeText.add(textConfidentiel); listeNoms.add(Messages.getString("EditSurvey.28")); //$NON-NLS-1$
		Drawing.sectionNlabelsNtext(sectionCentre, Messages.getString("EditSurvey.29"), listeText, listeNoms, Messages.getString("EditSurvey.30"));	 //$NON-NLS-1$ //$NON-NLS-2$
		listeText.clear();	
		listeNoms.clear();
									  
		
		
		//section types de données
		listeText.add(textNbre); listeNoms.add(Messages.getString("EditSurvey.31")); //$NON-NLS-1$
		listeText.add(textType); listeNoms.add(Messages.getString("EditSurvey.32")); //$NON-NLS-1$
		Drawing.sectionTypeDonnees(sectionType, Messages.getString("EditSurvey.33"), listeText, listeNoms, Messages.getString("EditSurvey.34"), listeType); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		//section commentaire
		Drawing.sectionCommentaireSurvey(sectionCommentaire, Messages.getString("EditSurvey.35"), textCommentaire, Messages.getString("EditSurvey.36")); //$NON-NLS-1$ //$NON-NLS-2$
		

		//buttonExpand2.setParent(ViewEdit.formEdition.getBody());
		buttonExpand2.setText("+"); //$NON-NLS-1$
		

//	listeners




	buttonExpand
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			//System.out.println("expand/collapse all"); // 
			//raz de la valeur manuelle
			int i;
			
			Shell s = new Shell();
			
			//System.out.println("expand ? = " + expand);
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				if (expand == 0){ 
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
					
				} 
				else{
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
					
					

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
			//System.out.println("expand/collapse all"); // 
			//raz de la valeur manuelle
			int i;
			
			Shell s = new Shell();
			
		//	System.out.println("expand ? = " + expand);
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				if (expand == 0){ 
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
					
				} 
				else{
					EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
					
					

				}
				
			}
			if (expand == 0) {
			//	System.out.println("expand passe à 1");
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
	
	
	
	buttonValidate
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			//System.out.println("validation"); // 
			
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
			//System.out.println("RAZ"); // 
			
			razText(listeTextSurvey);
			

		}
	});
	
	
	buttonInitServeur
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			//System.out.println("WebService"); 
				init_webService();
			}
	});
	
	buttonXML
	.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			//System.out.println("description XML"); 
				ouvertureDescriptionXML();
			}
	});
	
	
	//raz du message d'erreurs
	Utils.messageErreur(""); //$NON-NLS-1$
	
	
	
		  
//		refresh du formulaire
							  
							  
						  
					 formEdition.reflow(true);
					 
					 
					 //affichage courant
					 ViewFile.affiche = 1;
					 
//					remplissage de l'IHM par le modèle chargé ?
	//	System.out.println("Valeur de DataModel.ouverture = "+ DataModel.ouverture);			 
					 
		 if (DataModel.ouverture){
			 DataModel.fillSurvey();
							 }
		
		}
		
		
		
		public static void webServiceUpdate(){
			
			shell = new Shell(SWT.NONE);	
			shell.setSize(200, 20);
    		shell.setBounds(50, 50, 500, 20);
    		shell.open();
    		
    		final ProgressBar progressbar = new ProgressBar(shell,
    				  SWT.HORIZONTAL | SWT.SMOOTH  | SWT.APPLICATION_MODAL);
    				progressbar.setMinimum(0);
    				progressbar.setMaximum(100);
    				//progressbar.setSize(200,20);
    				
    				progressbar.setBounds(0,0, 500, 20);
    				shell.pack();
    				shell.setText(Messages.getString("EditSurvey.47")); //$NON-NLS-1$
    		
    		////////////////////////////////////////////
    		
    	/*	
    		new Thread() {
    		      public void run() {
    		       
//    		    	interrogation WService
    		    	  NemoLists.updateCountries();
    		    	  progressbar.setSelection(14);
    		    	  NemoLists.updateDataTypes();
    		    	  progressbar.setSelection(28);
    		    	  NemoLists.updateRegions();   
    		    	  progressbar.setSelection(42);
    		    	  NemoLists.updateShips();
    		    	  progressbar.setSelection(56);
    		    	  NemoLists.updateDataCentres();
    		    	  progressbar.setSelection(70);
    		    	  NemoLists.updateParameters();
    		    	  progressbar.setSelection(84);
    		    	  NemoLists.updateSurveys();
    		    	  progressbar.setSelection(100);
    		    
    					  Application.display.asyncExec(new Runnable() {
    	    		            public void run() {
    	    		             
    	    		            }
    	    		          });
    				
    					
    					  Application.display.asyncExec(new Runnable() {
    	    		            public void run() {
    	    		              
    	    		            }
    	    		          });
    					
    					  Application.display.asyncExec(new Runnable() {
    	    		            public void run() {
    	    		             
    	    		            }
    	    		          });
    	    		      
    					
    					  Application.display.asyncExec(new Runnable() {
    	    		            public void run() {
    	    		             
    	    		            }
    	    		          });
    					
    					  Application.display.asyncExec(new Runnable() {
    	    		            public void run() {
    	    		             
    	    		            }
    	    		          });
    		    	  
    		          
    		        
    		        
    		      }   
    		    	  
    		    }.start();
    		
    		
    		*/
    		
    				 NemoLists.updateCountries();
   		    	  progressbar.setSelection(14);
   		    	  NemoLists.updateDataTypes();
   		    	  progressbar.setSelection(28);
   		    	  NemoLists.updateRegions();   
   		    	  progressbar.setSelection(42);
   		    	  NemoLists.updateShips();
   		    	  progressbar.setSelection(56);
   		    	  NemoLists.updateDataCentres();
   		    	  progressbar.setSelection(70);
   		    	  NemoLists.updateParameters();
   		    	  progressbar.setSelection(84);
   		    	  NemoLists.updateSurveys();
   		    	  progressbar.setSelection(100);
    		
    		
    		    shell.close();	
    		
    
			
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
					//
					Utils.messageErreur(Messages.getString("EditSurvey.48")); //$NON-NLS-1$
					e.printStackTrace();
				} catch (RemoteException e) {
					// 
					Utils.messageErreur(Messages.getString("EditSurvey.49")); //$NON-NLS-1$
					e.printStackTrace();
				} catch (ServiceException e) {
					// 
					Utils.messageErreur(Messages.getString("EditSurvey.50")); //$NON-NLS-1$
					e.printStackTrace();
				}
			}else{
			try {
				DataModel.singleSurvey.getModel().fillFromSISMER(reference);
			} catch (MalformedURLException e) {
				Utils.messageErreur(Messages.getString("EditSurvey.51")); //$NON-NLS-1$
				e.printStackTrace();
			} catch (RemoteException e) {
				Utils.messageErreur(Messages.getString("EditSurvey.52")); //$NON-NLS-1$
				e.printStackTrace();
			} catch (ServiceException e) {
				Utils.messageErreur(Messages.getString("EditSurvey.53")); //$NON-NLS-1$
				e.printStackTrace();
			}
			}
			
			//mise à jour de l'onglet courant
			majApresWS();
			
			
		}
		
		public static void ouvertureDescriptionXML(){
			
			String nomFichierXML, nomXML;
			File fich;
			
			//remplissage du dataModel à partir des valeurs du fichier XML de description
			//System.out.println("Menu open xml");
			
			//vérifier que un fichier est chargé avant d'ouvrir un modèle
			if ((EditDesc.validee !=1)&&(EditSurvey.loaded !=1)){
				Utils.messageErreur(Messages.getString("EditSurvey.54")); //$NON-NLS-1$
			}
			else{
			Shell shell = new Shell();
			FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
			//dialog.setFilterPath(Utils.configuration.getModelsDirectory()+"\\");
			dialog.setText(Messages.getString("EditSurvey.55")); //$NON-NLS-1$
			String extensions[] = new String[1];
			extensions[0]="*.xml"; //$NON-NLS-1$
			dialog.setFilterExtensions(extensions);
			nomXML = dialog.open();
			if (nomXML == null) {}else{
			
				fich = new File(nomXML);
				nomFichierXML = fich.getName();
				
				EditSurvey.chargementXML(fich);
			
				
				
				}
			
			
			
			}	
			
		}
		

public static void chargementXML(File fichier){

	if (ViewFile.dossierCampagne){
		
			try {
				DataModel.dirSurvey.releaseModel();
				Survey survey = new Survey();
				survey.importFromXML(fichier);
				
				//DataModel.dirSurvey.getModel().importFromXML(fichier);
				DataModel.dirSurvey.setModel(survey);
				
				
			} catch (FileNotFoundException e) {
				// 
				e.printStackTrace();
			} catch (SAXException e) {
				// 
				e.printStackTrace();
			} catch (TransformerException e) {
				//
				e.printStackTrace();
			}
		
	}else{
	
		try {
			DataModel.singleSurvey.releaseModel();
			
			//getModel().releaseDataTypes();
			Survey survey = new Survey();
			survey.importFromXML(fichier);
			
			DataModel.singleSurvey.setModel(survey);
			
			//DataModel.singleSurvey.getModel().importFromXML(fichier);
			DataModel.singleSurvey.getModel();
		} catch (FileNotFoundException e) {
			// 
			e.printStackTrace();
		} catch (SAXException e) {
			// 
			e.printStackTrace();
		} catch (TransformerException e) {
			// 
			e.printStackTrace();
		}

	}
	
	//mise à jour de l'onglet courant
	majApresWS();
}


		
		public static void majApresWS(){
			//System.out.println("Remplissage onglet survey");
			
			
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
			Formatter formatter = new Formatter();
			
			try {
				EditSurvey.textDateDebut.setText(survey.getFormattedStartDate());
				EditSurvey.textDateFin.setText(survey.getFormattedEndDate());
			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}
			
			EditSurvey.textRegion.setText(survey.getRegionName());
			
			//Source de données
			EditSurvey.textPays.setText(survey.getCountryCode());
			EditSurvey.textLabo.setText(survey.getLaboratories());
			EditSurvey.textChef.setText(survey.getChiefScientist());
			EditSurvey.textProjet.setText(survey.getProjectName());
			
			
			//Centre d'archivage
			EditSurvey.textCentre.setText(survey.getDataCentre());
			EditSurvey.textConfidentiel.setText(""+survey.getAvailability()); //$NON-NLS-1$
			
			//Commentaire
			EditSurvey.textCommentaire.setText(survey.getComment());
			
			//Remplissage de la liste des dataTypes de la campagne
			
			DataType[] dataTypes = survey.getDataTypes();
			int taille = dataTypes.length;
			
			//reset de la liste des dataTypes de l'IHM
			TableDataSurvey.types.clear();
			
			for (int i=0; i<taille;i++){
				TypeSurvey typeSurvey = new TypeSurvey();
				typeSurvey.setCode(dataTypes[i].getCode());
				typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());
				typeSurvey.setQc(""+dataTypes[i].isQc()); //$NON-NLS-1$
				typeSurvey.setSensor(dataTypes[i].isSensor());
				
				TableDataSurvey.types.add(typeSurvey);
				
				//System.out.println("TypeSurvey " +typeSurvey.getCode()+" ajouté");
				
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
			
			//System.out.println("Fin remplissage onglet Survey");
			
			
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
			EditSurvey.buttonXML.setParent(s);
			EditSurvey.buttonRAZ.setParent(s);
			
			
			
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				
				EditSurvey.listeSectionsSurvey.get(i).setExpanded(true);
				//System.out.println("===> " + EditSurvey.listeSectionsSurvey.get(i).getText());
				EditSurvey.listeSectionsSurvey.get(i).setParent(s);
				//System.out.println("effacement zone edition etape description campagne ==> ok");
				}
		
			
			//effacement titre formulaire
			ViewEdit.formEdition.setText(""); //$NON-NLS-1$
			
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
			Utils.messageErreur(""); //$NON-NLS-1$
			
			//reaffichage des boutons valider/expand/init serveur
			//EditSurvey.buttonExpand.setParent(s.getBody());
			
			EditSurvey.buttonValidate.setParent(s.getBody());
			EditSurvey.buttonInitServeur.setParent(s.getBody());
			EditSurvey.buttonXML.setParent(s.getBody());
			EditSurvey.buttonRAZ.setParent(s.getBody());
			
			
			for (i=0;i<=EditSurvey.listeSectionsSurvey.size()-1;i++){
				
				
				EditSurvey.listeSectionsSurvey.get(i).setExpanded(false);
				//System.out.println("===> " + EditSurvey.listeSectionsSurvey.get(i).getText());
				EditSurvey.listeSectionsSurvey.get(i).setParent(s.getBody());
			//	System.out.println("re-affichage zone edition etape description campagne ==> ok");
				}
		
			//EditSurvey.buttonExpand2.setParent(s.getBody());
			
			
			ViewEdit.formEdition.setText(Messages.getString("EditSurvey.61")); //$NON-NLS-1$
			
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
		
		textCampagne= new Text(shell, SWT.BORDER);listeTextSurvey.add(textCampagne);
		textNom = new Text(shell, SWT.BORDER);listeTextSurvey.add(textNom);
		textCodeNavire = new Text(shell, SWT.BORDER);		listeTextSurvey.add(textCodeNavire);
		textNomNavire = new Text(shell, SWT.BORDER);listeTextSurvey.add(textNomNavire);
		textDateDebut = new Text(shell, SWT.BORDER);listeTextSurvey.add(textDateDebut);
		textDateFin = new Text(shell, SWT.BORDER);listeTextSurvey.add(textDateFin);
		textRegion = new Text(shell, SWT.BORDER);listeTextSurvey.add(textRegion);
		textPays = new Text(shell, SWT.BORDER);listeTextSurvey.add(textPays);
		textLabo = new Text(shell, SWT.BORDER);listeTextSurvey.add(textLabo);
		textChef= new Text(shell, SWT.BORDER);listeTextSurvey.add(textChef);
		textProjet = new Text(shell, SWT.BORDER);listeTextSurvey.add(textProjet);
		textCentre = new Text(shell, SWT.BORDER);listeTextSurvey.add(textCentre);
		textConfidentiel = new Text(shell, SWT.BORDER);listeTextSurvey.add(textConfidentiel);
		textType = new Text(shell, SWT.BORDER);listeTextSurvey.add(textType);
		textNbre = new Text(shell, SWT.BORDER);listeTextSurvey.add(textNbre);
		textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);listeTextSurvey.add(textCommentaire);
		
		//remplissage de la liste des textes
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}	
		
	public static void initBoutons(){
		
		Shell shell = new Shell();
		buttonRAZ = new Button(shell, SWT.NONE);
		buttonInitServeur = new Button(shell, SWT.NONE);
		buttonExpand = new Button(shell, SWT.NONE);
		buttonExpand2 = new Button(shell, SWT.NONE);
		buttonValidate = new Button(shell, SWT.NONE);
		buttonXML = new Button(shell, SWT.NONE);
				
	}
		

	public static void initListeType(){
		
		Shell shell = new Shell();
		listeType = new List(shell, SWT.BORDER);
		
		
	}
	
	public static void razText(ArrayList <Text> liste){
		
		for (int i =0; i< liste.size(); i++){
			liste.get(i).setText(""); //$NON-NLS-1$
					
		}
		
		//effacement de la liste des types de données dans la table
		TableDataSurvey.types = new ArrayList<TypeSurvey>();
		  //razTableData
		TableDataSurvey.razTableData();
		  
		
			 
			 //refresh de la table
			 //initTable();
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
		
		
		
	}
	
	
	
}
