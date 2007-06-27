package fr.ifremer.nemo.ui;

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

public class DrawingData {

	private static Group grou;
//	private static Text lign, debu, fi;
	
	

	public static void sectionDate(String nomSection, String description, Section section, Text ligne, Text debut, Text fin){
		
			
		final Text lign, debu, fi;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;
//		manu = manuel;


		section.setParent(EditDesc.formEdition);

			section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
			
		//System.out.println("section date station");
			
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  EditDesc.formEdition.reflow(true);
					  }
					 });
			  		section.setText(nomSection);
			  		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  		section.setFont(font);
			  		section.setDescription(description);
					Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					sectionClient.setLayout(new GridLayout());
					
					 
					 Group groupeFormat =  new Group(sectionClient, SWT.NONE);
					 groupeFormat.setLayout(new GridLayout());
					 groupeFormat.setText("Format");
					 
//					combo format de l'heure
					  EditData.comboDate.setParent(groupeFormat);
					
					
					
					  GridLayout gridLayout1 = new GridLayout();
					  gridLayout1.numColumns = 6;
					  Group groupe = new Group(sectionClient, SWT.NONE);
					  groupe.setText("Position");
					  groupe.setLayout(gridLayout1);
					  grou = groupe;
					  
					  //ligne
					  Label label1 = new Label(groupe, SWT.NULL);
					  label1.setText("Ligne");
					  ligne.setParent(groupe);
					  //ligne = new Text(groupe, SWT.BORDER);
					  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
					  //debut
					  Label label2 = new Label(groupe, SWT.NULL);
					  label2.setText("Début");
					  debut.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //fin
					  Label label3 = new Label(groupe, SWT.NULL);
					  label3.setText("Fin");
					  fin.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  
					  //combo format date
				//	  EditData.comboDate.setParent(groupe);
					  
					  
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(groupe, SWT.NONE);
						buttonInject.setText("Injecter");
					
						section.setClient(sectionClient);		
					  
						// une fois les éléments initialisés, on les passe en static pour y accéder à 
						//partir des listeners
						lign = ligne; 
						debu = debut;
						fi = fin;
						
						lign.setEnabled(true); 
						debu.setEnabled(true); 
						fi.setEnabled(true);
						buttonInject.setEnabled(true);
					  
					  //listeners
				
					  
					  //bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
								//System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
								//System.out.println("valeur de ligne lign==> " + lign.getText());
								//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
							Utils.inject(lign, debu, fi, ViewFile.styledTextData);
							
							
							}
						});
					  
				
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeData(section);
					  
					  
			
		}	

		
public static void sectionTotal(String nomSection, String description, Section section, Text ligne, Text debut, Text fin){
		
	
		
		final Text lign, debu, fi;
		final Button inject;
		final String nomSec;
		final Text manu;

		nomSec = nomSection;
//		manu = manuel;


		section.setParent(EditDesc.formEdition);

			section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
			
		//System.out.println("section date station");
			
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  EditDesc.formEdition.reflow(true);
					  }
					 });
			  		section.setText(nomSection);
			  		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  		section.setFont(font);
			  		section.setDescription(description);
					Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					sectionClient.setLayout(new GridLayout());

					 
					 Group groupeFormat =  new Group(sectionClient, SWT.NONE);
					 groupeFormat.setLayout(new GridLayout());
					 groupeFormat.setText("Format");
					 
//					combo format de l'heure+date
					  EditData.comboTotal.setParent(groupeFormat);
					
					  GridLayout gridLayout1 = new GridLayout();
					  gridLayout1.numColumns = 6;
					  Group groupe = new Group(sectionClient, SWT.NONE);
					  groupe.setText("Position");
					  groupe.setLayout(gridLayout1);
					  grou = groupe;
					  
					  //ligne
					 // Label label1 = new Label(groupe, SWT.NULL);
					 // label1.setText("Ligne");
					 // ligne.setParent(groupe);
					  //ligne = new Text(groupe, SWT.BORDER);
					  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
					  //debut
					  Label label2 = new Label(groupe, SWT.NULL);
					  label2.setText("Début");
					  debut.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //fin
					  Label label3 = new Label(groupe, SWT.NULL);
					  label3.setText("Fin");
					  fin.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  
					  //combo format date
				//	  EditData.comboDate.setParent(groupe);
					  
					  
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(groupe, SWT.NONE);
						buttonInject.setText("Injecter");
					
						section.setClient(sectionClient);		
					  
						// une fois les éléments initialisés, on les passe en static pour y accéder à 
						//partir des listeners
						ligne.setText("1"); //valeur bidon pour la ligne
						
						lign = ligne; 
						debu = debut;
						fi = fin;
						
						lign.setEnabled(true); 
						debu.setEnabled(true); 
						fi.setEnabled(true);
						buttonInject.setEnabled(true);
					  
					  //listeners
				
					  
					  //bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
								//System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
								//System.out.println("valeur de ligne lign==> " + lign.getText());
								//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
							Utils.inject(lign, debu, fi, ViewFile.styledTextData);
							debu.pack();
							fi.pack();
							
							}
						});
					  
				
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeData(section);
					  
					  
			
		}	

	public static void sectionHeure(String nomSection, String description, Section section, Text ligne, Text debut, Text fin){
		
		
	
		
		final String nomSec;
		final Text lign, debu, fi;
		final Button inject;

		nomSec = nomSection;
			section = ViewEdit.toolkitEdition.createSection(EditStation.formEdition.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
	
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  EditStation.formEdition.reflow(true);
					  }
					 });
			  		section.setText(nomSection);
			  		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  	  section.setFont(font);
			  		section.setDescription(description);
					Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					sectionClient.setLayout(new GridLayout());
					  
					
					  GridLayout gridLayout = new GridLayout();
					  gridLayout.numColumns = 4;
					  
					  Group groupeConv = new Group(sectionClient, SWT.NONE);
					 groupeConv.setText("Conversion");
					  groupeConv.setLayout(gridLayout);
					  
					 	//zone de conversion et test de conversion
					 Label labelConv= new Label(groupeConv, SWT.NONE);
					 labelConv.setText("Formule");
					 EditData.textHeureConversion.setParent(groupeConv);
					 
					 Label labelTest= new Label(groupeConv, SWT.NONE);
					 labelTest.setText("Test");
					 EditData.textHeureTest.setParent(groupeConv);
					 
					 Group groupeFormat =  new Group(sectionClient, SWT.NONE);
					 groupeFormat.setLayout(new GridLayout());
					 groupeFormat.setText("Format");
					 
//					combo format de l'heure
					  EditData.comboHeure.setParent(groupeFormat);
						  
					  GridLayout gridLayout1 = new GridLayout();
					  gridLayout1.numColumns = 6;
					  Group groupe = new Group(sectionClient, SWT.NONE);
					  groupe.setText("Position");
					  groupe.setLayout(gridLayout1);
					  grou = groupe;
					  
					  //ligne
					  Label label1 = new Label(groupe, SWT.NULL);
					  label1.setText("Ligne");
					  ligne.setParent(groupe);
					  //ligne = new Text(groupe, SWT.BORDER);
					  ligne.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
					  //debut
					  Label label2 = new Label(groupe, SWT.NULL);
					  label2.setText("Début");
					  debut.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //fin
					  Label label3 = new Label(groupe, SWT.NULL);
					  label3.setText("Fin");
					  fin.setParent(groupe);
					  //= new Text(groupe, SWT.BORDER);
					  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(groupe, SWT.NONE);
						buttonInject.setText("Injecter");
					
						section.setClient(sectionClient);		
					  
						// une fois les éléments initialisés, on les passe en static pour y accéder à 
						//partir des listeners
						lign = ligne; 
						debu = debut;
						fi = fin;
						
						lign.setEnabled(true); 
						debu.setEnabled(true); 
						fi.setEnabled(true);
						buttonInject.setEnabled(true);
					  
					  //listeners
				
					  
					  //bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
								System.out.println("bouton injection"); // TODO Auto-generated Event stub widgetSelected()
								//System.out.println("valeur de ligne lign==> " + lign.getText());
								//System.out.println("valeur de ligne de ViewEdit.ligneTest ==> " + ViewEdit.ligneTest.getText());
							Utils.inject(lign, debu, fi, ViewFile.styledTextData);
							
							}
						});
					  
			 
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeData(section);
					  
			
		}	

	
	
	
	public static void sectionTypeCourant(Section section, Text code, Text unite, Text conversion, Text debut, Text fin, Text format, Text entree, Text sortie){
		final String nomSec;
		final Text manu;
		final Text lign, debu, fi;
		
//		manu = manuel;



		section.setParent(EditData.formEdition);

			section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
			
		System.out.println("section type courant DrawingData");
			
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  EditDesc.formEdition.reflow(true);
					  }
					 });
			  		section.setText("Gérer les Types existants              ");
			  		 Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  		  section.setFont(font);
			  		section.setDescription("Consultez, modifiez ou supprimez les types de données en les sélectionnant dans la liste de gauche...");
					 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					 sectionClient.setLayout(new GridLayout());
					
					 GridLayout gridLayout1 = new GridLayout();
					 gridLayout1.numColumns = 2;
					 					  
					  //code
					  Label label1 = new Label(sectionClient, SWT.NULL);
					  label1.setText("Code");
					  code.setParent(sectionClient);
					  code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
					  //unité
					  Label label4 = new Label(sectionClient, SWT.NULL);
					  label4.setText("Unité");
					  unite.setParent(sectionClient);
					  unite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					
					  //conversion
					  Label label5 = new Label(sectionClient, SWT.NULL);
					  label5.setText("Formule de conversion");
					  conversion.setParent(sectionClient);
					  conversion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					 
					  //format
					  Label label6 = new Label(sectionClient, SWT.NULL);
					  label6.setText("Format");
					  format.setParent(sectionClient);
					  format.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  Group groupe = new Group(sectionClient, SWT.NONE);
						 groupe.setText("Position");
						 groupe.setLayout(gridLayout1);
						 grou = groupe;
					  
					  //debut
					  Label label2 = new Label(groupe, SWT.NULL);
					  label2.setText("Début");
					  debut.setParent(groupe);
					  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //fin
					  Label label3 = new Label(groupe, SWT.NULL);
					  label3.setText("Fin");
					  fin.setParent(groupe);
					  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(groupe, SWT.NONE);
						buttonInject.setText("Injecter");
					
						//bouton supprimer / modifier
						Button buttonModifier = new Button(sectionClient, SWT.NONE);
						buttonModifier.setText("Modifier");
						
						Button buttonSupprimer = new Button(sectionClient, SWT.NONE);
						buttonSupprimer.setText("Supprimer");
						
						
						section.setClient(sectionClient);		
					  
						// une fois les éléments initialisés, on les passe en static pour y accéder à 
						//partir des listeners
					
						debu = debut;
						fi = fin;
						
					  
					  //listeners
				
					//bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
								System.out.println("bouton injection"); 
								
							Utils.inject(EditData.textTempo, debu, fi, ViewFile.styledTextData);
							
							}
						});
					  
					  
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeData(section);
					  
					  
			
		}		
	
	
	public static void sectionTypeAjouter(Section section, Text code, Text unite, Text conversion, Text debut, Text fin, Text format, Text entree, Text sortie){
		final String nomSec;
		final Text manu, debu, lign, fi;
		
//		manu = manuel;



		section.setParent(EditData.formEdition);

			section = ViewEdit.toolkitEdition.createSection(EditDesc.formEdition.getBody(), 
					  Section.DESCRIPTION|Section.TITLE_BAR|
					  Section.TWISTIE|Section.COMPACT);
			
		System.out.println("section type ajouter DrawingData");
			
			  section.addExpansionListener(new ExpansionAdapter() {
					  public void expansionStateChanged(ExpansionEvent e) {
						  EditDesc.formEdition.reflow(true);
					  }
					 });
			  		section.setText("Nouveau Type                           ");
			  		 Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
			  		  section.setFont(font);
			  		section.setDescription("Créez ici un nouveau type de données en entrant ses différents paramètres ainsi que sa position...");
					 Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
					 sectionClient.setLayout(new GridLayout());
					
					 GridLayout gridLayout1 = new GridLayout();
					 gridLayout1.numColumns = 2;
					 					  
					  //code
					  Label label1 = new Label(sectionClient, SWT.NULL);
					  label1.setText("Code");
					  code.setParent(sectionClient);
					  code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
					  //unité
					  Label label4 = new Label(sectionClient, SWT.NULL);
					  label4.setText("Unité");
					  unite.setParent(sectionClient);
					  unite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					
					  //conversion
					  Label label5 = new Label(sectionClient, SWT.NULL);
					  label5.setText("Formule de conversion");
					  conversion.setParent(sectionClient);
					  conversion.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					 
					  //format
					  Label label6 = new Label(sectionClient, SWT.NULL);
					  label6.setText("Format");
					  format.setParent(sectionClient);
					  format.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  Group groupe = new Group(sectionClient, SWT.NONE);
						 groupe.setText("Position");
						 groupe.setLayout(gridLayout1);
						 grou = groupe;
					  
					  //debut
					  Label label2 = new Label(groupe, SWT.NULL);
					  label2.setText("Début");
					  debut.setParent(groupe);
					  debut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //fin
					  Label label3 = new Label(groupe, SWT.NULL);
					  label3.setText("Fin");
					  fin.setParent(groupe);
					  fin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					  
					  //bouton d'injection de la position des valeurs
					  	Button buttonInject = new Button(groupe, SWT.NONE);
						buttonInject.setText("Injecter");
					
						//bouton supprimer / modifier
						Button buttonModifier = new Button(sectionClient, SWT.NONE);
						buttonModifier.setText("Confirmer création");
						
						section.setClient(sectionClient);		
					  
						// une fois les éléments initialisés, on les passe en static pour y accéder à 
						//partir des listeners
					
						debu = debut;
						fi = fin;
						
					  
					  //listeners
				
					//bouton inject
					  buttonInject
						.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
								System.out.println("bouton injection"); 
							
								//le textTempo c'est pour simuler une variable ligne dont on n'a pas besoin ici!
							Utils.inject(EditData.textTempo, debu, fi, ViewFile.styledTextData);
							
							}
						});
					  
					  
					  //ajout de la section dans la liste des sections de description
					  ajoutSectionDansListeData(section);
					  
					  
			
		}	
	
	
	
	
	
	public static void ajoutSectionDansListeData(Section section){
		
		EditData.listeSectionsData.add(section);
		System.out.println("ajout section ok");
		
	}
	
	
	
	
	
	
	
}
