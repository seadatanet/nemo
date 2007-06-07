package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

public class ViewFile extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewFile"; // 

	public static int etape, ongletCourant, affiche, tailleCompositeData;
	public static boolean dossierCampagne, fileLoaded;
	
	public static Composite compositeData;
	
			
	public static Composite top = null;

	private TabFolder tabFolder = null;

	private Composite composite1 = null;

	private Composite composite2 = null;

	private Composite composite3 = null;

	public static Composite composite4 = null;

	private Composite composite11 = null;

	private Composite composite12 = null;

	public static Button checkBox, medatlas, odv = null;

	private Button radioFichier = null;

	private Button radioDossier = null;

	private Button buttonParcourir = null;
	
	public static List listeData;

	//private Text textAreaDescription = null;

	private Label labelDescription = null;

	public static StyledText styledTextCampagne, textAreaDescription, styledTextStation, styledTextData;
	
	public static TabItem tabItem, tabItem1, tabItem2, tabItem3, tabItem4, tabItem9;

	public static Composite compositeConversion = null;

	public static Group groupTempo = null;

	private Text textTempoDateRef = null;

	private Label labelDateRef = null;

	private Label labelHeureRef = null;

	private Text textTempoHeureRef = null;

	private Combo comboTempoDate = null;

	private Combo comboTempoHeure = null;
	
	public static String nomFichier, nomRepertoire;
	public static File dossier;
	public static Table tableData;

	@Override
	public void createPartControl(Composite parent) {
		// 
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());

		//validation de chaque étape
		EditDesc.validee = 0;
		
		//chargement de la configuration
		Utils.initConfig();
				
		//ouverture de modèle à false
		DataModel.ouverture = false;
		
		//étape de départ
		etape = 0;
		OptionsAction.options = 0;
		
		//aucune étape n'est affichée
		EditDesc.loaded = 0;
		EditSurvey.loaded =0;
		EditStation.loaded = 0;
		EditData.loaded = 0;
		EditConversion.loaded = 0;
		
		createTabFolder();
	
	}

	@Override
	public void setFocus() {
		// 

	}

	/**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(top, SWT.NONE);
		createComposite1();
		createComposite2();
		createComposite3();
		createComposite4();
		createComposite14();
		
		
		//init des flags
		FiltrageTableType.listeOuverte = 0;
		ongletCourant = 0;
		affiche = 0;
		fileLoaded = false;
		
		
		//////////////////////////////////////////////////////////
		// listener sur les onglets
		//////////////////////////////////////////////////////////
		tabFolder.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			
				System.out.println("affichage courant " + affiche);
				int tmp = tabFolder.getSelectionIndex();
				
				//tabFolder.setSelection(0);
				Utils.validationEtape(tmp);
				System.out.println("onglet sélectionné " + tmp); 
				 
				
				//rajouter test de validation au changement d'onglet
			
			}
		});
		 tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Description");
		tabItem.setControl(composite1);
		 tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Campagne");
		tabItem1.setControl(composite2);
		
		 tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("Station");
		tabItem2.setControl(composite3);
		 tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText("Données");
		tabItem3.setControl(composite4);
		 tabItem9 = new TabItem(tabFolder, SWT.NONE);
		tabItem9.setText("Conversion");
		tabItem9.setControl(compositeConversion);
		
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		composite1 = new Composite(tabFolder, SWT.NONE);
		composite1.setLayout(new FillLayout(SWT.VERTICAL));
		
		//séries temporelles
		
		
		//bouton fichier
		
		
		//bouton dossier
		
		
		createComposite12();
		createComposite11();
		
	}

	/**
	 * This method initializes composite2	
	 *
	 */
	private void createComposite2() {
		composite2 = new Composite(tabFolder, SWT.NONE);
		composite2.setLayout(new FillLayout());
		styledTextCampagne = new StyledText(composite2, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL |SWT.READ_ONLY );
	}

	/**
	 * This method initializes composite3	
	 *
	 */
	private void createComposite3() {
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		composite3 = new Composite(tabFolder, SWT.NONE);
		composite3.setLayout(new FillLayout());
		styledTextStation = new StyledText(composite3, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL |SWT.READ_ONLY );
		styledTextStation.setFont(font);
	}

	/**
	 * This method initializes composite4	
	 *
	 */
	private void createComposite4() {
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		composite4 = new Composite(tabFolder, SWT.NONE);
		composite4.setLayout(new FillLayout(SWT.VERTICAL));
		styledTextData = new StyledText(composite4, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL |SWT.READ_ONLY );
		styledTextData.setFont(font);
		createCompositeData(composite4);
	}

	/**
	 * This method initializes composite11	
	 *
	 */
	private void createComposite11() {
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite11 = new Composite(composite1, SWT.NONE);
		composite11.setLayout(gridLayout);
		
		
		radioFichier = new Button(composite11, SWT.RADIO);
		radioFichier.setSelection(true);
		radioFichier.setText("fichier campagne");
		radioDossier = new Button(composite11, SWT.RADIO);
		radioDossier.setText("dossier campagne");
		radioFichier
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("fichier campagne"); // 
					}
				});
		
		//labelDescription = new Label(composite11, SWT.NONE);
		//labelDescription.setText("Sélection-->");
		//labelDescription.setLayoutData(gridData);
	
		
		radioDossier
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("dossier campagne)"); // 
					}
				});
	
	
	
		buttonParcourir = new Button(composite11, SWT.NONE);
		buttonParcourir.setText("Parcourir");
		checkBox = new Button(composite11, SWT.CHECK);
		checkBox.setText("Série temporelle");
		checkBox.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				//si pas coché on active le groupTempo sinon on grise
				if (checkBox.getSelection()){
				System.out.println("série temporelle"); // 
				groupTempo.setVisible(true);}else{groupTempo.setVisible(false);}
			
			}
		});
		createGroupTempo();
		buttonParcourir
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("parcourir"); // 
					
					//parcours des fichiers/dossiers
								//si fichier campagne
						
						
						
						if (radioFichier.getSelection()){
							
							
							dossierCampagne = false;
							
							Shell shell = new Shell();
							FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
							nomFichier = dialog.open();
							if (nomFichier != null){
								
								
							//labelDescription.setText("Fichier-->");
							//textAreaDescription.setText(nomFichier);
							
							fileLoaded = true;
							
							//init du modèle
							//Utils.initModele(true, checkBox.getSelection(), nomFichier);
							
							
							//affichage du fichier campagne dans onglet Campagne
							try {
								Utils.insertTextFich(nomFichier, styledTextCampagne);
								//Utils.remplissageTexte(textAreaDescription);
								textAreaDescription.setText(Utils.texteBrut);
							} catch (IOException e1) {
								// 
								e1.printStackTrace();
							} 
														
							} else { 
								fileLoaded = false;
								System.out.println("pas de fichier sélectionné");
								ViewLog.styledTextLogErreurs.setText("\nVeuillez sélectionner un fichier pour continuer.");
								
							}
							}
								//si dossier campagne
							if (radioDossier.getSelection()){
								dossierCampagne = true;
								
								Shell shell = new Shell();
								DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
								nomRepertoire = dialog.open();
								if (nomRepertoire != null){
									fileLoaded = true;
									Utils.findFiles(nomRepertoire);
									textAreaDescription.setText(Utils.texteBrut);
								//labelDescription.setText("Dossier-->");
								//textAreaDescription.setText(Utils.texteBrut);
									}
								 else { 
										fileLoaded = false;
										System.out.println("pas de dossier sélectionné");
										ViewLog.styledTextLogErreurs.setText("\nVeuillez sélectionner un dossier pour continuer.");
										
									}
								
								
								}
					
							
//							affichage etape0 dans formulaire d'édition de paramètres
							EditDesc.affichageEdition();		
					
					}
				});
	}

	/**
	 * This method initializes composite12	
	 *
	 */
	private void createComposite12() {
		
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL );
		
		composite12 = new Composite(composite1, SWT.NONE);
		composite12.setLayout(new FillLayout());
		textAreaDescription = new StyledText(composite12, SWT.MULTI | SWT.READ_ONLY |SWT.H_SCROLL | SWT.V_SCROLL);
		textAreaDescription.setFont(font);
	
	}

	/**
	 * This method initializes composite14	
	 *
	 */
	private void createComposite14() {
		compositeConversion = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		compositeConversion.setLayout(layout);
		
		Group group = new Group(compositeConversion, SWT.NONE);
		group.setText("Choix du format de sortie");
		group.setLayout(layout);
		
//		Label label = new Label(compositeConversion, SWT.NONE);
//		label.setText("Choix du format de sortie...");
		
		medatlas = new Button(group, SWT.RADIO);
		medatlas.setText("Medatlas");
		medatlas.setSelection(true);
		odv = new Button(group, SWT.RADIO);
		odv.setText("ODV");
	}

	/**
	 * This method initializes groupTempo	
	 *
	 */
	private void createGroupTempo() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		groupTempo = new Group(composite11, SWT.NONE);
		groupTempo.setText("Série temporelle");
		groupTempo.setLayout(gridLayout1);
		labelDateRef = new Label(groupTempo, SWT.NONE);
		labelDateRef.setText("Date de référence");
		Label filler3 = new Label(groupTempo, SWT.NONE);
		textTempoHeureRef = new Text(groupTempo, SWT.BORDER);
		createComboTempoDate();
		labelHeureRef = new Label(groupTempo, SWT.NONE);
		labelHeureRef.setText("Heure de référence");
		Label filler2 = new Label(groupTempo, SWT.NONE);
		textTempoDateRef = new Text(groupTempo, SWT.BORDER);
		
		//on le désactive de base
		groupTempo.setVisible(false);
		createComboTempoHeure();
	}

	/**
	 * This method initializes comboTempoDate	
	 *
	 */
	private void createComboTempoDate() {
		comboTempoDate = new Combo(groupTempo, SWT.READ_ONLY);
		comboTempoDate.add("DD/MM/YYYY");
		comboTempoDate.add("MM/DD/YYYY");
		comboTempoDate.add("YYYY/DD/MM");
		comboTempoDate.select(0);
	}

	/**
	 * This method initializes comboTempoHeure	
	 *
	 */
	private void createComboTempoHeure() {
		comboTempoHeure = new Combo(groupTempo, SWT.READ_ONLY);
		comboTempoHeure.add("HH24:MI");
		comboTempoHeure.add("HH12:MI");
		comboTempoHeure.add("HH24MI");
		comboTempoHeure.select(0);
	}


	private void createCompositeData(Composite compo) {
		compositeData = new Composite(compo, SWT.NONE);
		compositeData.setLayout(new FillLayout());
		//compositeData.setLayout(new GridLayout());
		//compositeData.setLayout(null);
		
		//ajout de la liste des donnees
		//tableData = new Table(compositeData, SWT.BORDER | SWT.FULL_SELECTION);
		
		//TableData.go();
		
//		tableData.setHeaderVisible(true);
//	    tableData.setLinesVisible(true);
//		//listeData = new List(compositeData, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		
		
	}
	

}
