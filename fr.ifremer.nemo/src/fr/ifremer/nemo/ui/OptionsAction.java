package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.Configuration.enumMatch;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class OptionsAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;

	public static Composite top, compoHaut, compoLCD, compoBouton,
					compoWS, compoWSVocab1, compoWSVocab2= null;
	
	public static Group groupLangue, groupCentreDonnees, groupWS, 
			groupDossier, groupDossier1, groupDossier2, groupCDI = null;

	public static Combo comboLangue, comboCentreDonnees, comboIdEdmoRespCdi, comboRecherche,
				comboMethodeCdi,comboCodeEdmoDonnees, comboIdEdmoRespDonnees, comboIdEdmoDistDonnees, 
				comboWSTag = null;

	public static Button buttonCancel, buttonValider, buttonParcourirModele,
			buttonParcourirCampagne = null;

	public static Label labelNemo, labelNautilus, labelModèle, labelCampagne,
			labelIdEdmoRespCdi, labelUrlCdi, labelMethodeCdi,
			labelCodeEdmoDonnees, labelIdEdmoRespDonnees, labelIdEdmoDistDonnees,
			labelWSVocab, labelWSPredicate, labelWSTag = null;

	public static Text textAreaNemo, textAreaNautilus, textDossierModele, textDossierCampagne, 
			textUrlCdi, textVocabURL, textWSPredicate = null;
	
	public static Table tableWSVocab;

	public final static String ID = "fr.ifremer.nemo.ui.OptionsAction"; //$NON-NLS-1$

	// vars nécessaires à la fenêtre d'options

	public static Shell shelloptions;

	public static int options;

	public OptionsAction(IWorkbenchWindow window) {

		this.window = window;
		setId(ID);
		setText(Messages.getString("OptionsAction.1")); //$NON-NLS-1$
		window.getSelectionService().addSelectionListener(this);

	}

	public void dispose() {
		window.getSelectionService().addSelectionListener(this);

	}

	public void run() {
		// lancement IHM options
//		System.out.println("Menu options"); //$NON-NLS-1$

		shelloptions = new Shell(Application.display, SWT.BORDER | SWT.TITLE
				| SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

		shelloptions.setSize(600, 550);
		shelloptions.setText(Messages.getString("OptionsAction.3")); //$NON-NLS-1$
		shelloptions.setLayout(new GridLayout());
		
		createPartControl(shelloptions);

		// shelloptions.pack();
		shelloptions.open();
		options = 1;

		// chargement des options
		Utils.initConfig();

		remplissageOptions();

	}

	public void remplissageOptions() {
		// remplissage IHM avec les valeurs du fichier de config

		// DOSSIERS
		textDossierModele.setText(Utils.configuration.getModelsDirectory());
		textDossierCampagne.setText(Utils.configuration.getCruisesDirectory());

		// LANGUE
		if (Utils.configuration.getLocale().toString().matches("fr_FR")) { //$NON-NLS-1$
			// on met la combo langue à FR
			comboLangue.select(0);
		} else {
			// on met la combo langue à GB
			comboLangue.select(1);
		}
		
		// CENTRE DE DONNEES
		int taille = NemoLists.getDataCentres().length;
		for (int i = 0; i < taille; i++) {
			comboCentreDonnees.add(NemoLists.getDataCentres()[i].getCode() + " - "
					+ NemoLists.getDataCentres()[i].getName());
		}

		Utils.selectComboCode(comboCentreDonnees, Utils.configuration.getDataCenter());
//		int i = 0;
//		comboCentreDonnees.select(i);
//		String dataCenter = Utils.configuration.getDataCenter();
//		int j = dataCenter.length();
//		while (i < taille && !comboCentreDonnees.getText().substring(0,j).equals(dataCenter)) {
//			i++;
//			comboCentreDonnees.select(i);
//		}

		// WS
		textAreaNemo.setText(Utils.configuration.getNemoWebServiceURL());
		textAreaNautilus
				.setText(Utils.configuration.getNautilusWebServiceURL());
		
		textVocabURL.setText(Utils.configuration.getVocabURL());
		textWSPredicate.setText(Utils.configuration.getPredicate());
		
		for (enumMatch match : enumMatch.values()) {
			comboWSTag.add(match.name());
		}
		Utils.selectComboValeur(comboWSTag,Utils.configuration.getMatch());
		
//		textL011.setText(Utils.configuration.getVocabListURL("L011"));
//		textL021.setText(Utils.configuration.getVocabListURL("L021"));
//		textL031.setText(Utils.configuration.getVocabListURL("L031"));
//		textL041.setText(Utils.configuration.getVocabListURL("L041"));
//		textL051.setText(Utils.configuration.getVocabListURL("L051"));
//		textL061.setText(Utils.configuration.getVocabListURL("L061"));
//		textL071.setText(Utils.configuration.getVocabListURL("L071"));
//		textL081.setText(Utils.configuration.getVocabListURL("L081"));
//		textL091.setText(Utils.configuration.getVocabListURL("L091"));
		
		
		// CDI
		HashMap<Integer, String> edmoHash = NemoLists.getEdmo();

		TreeMap<Integer, String> edmoTree = new TreeMap<Integer, String>();
		
		EdmoHandler.listeOrigine = new ArrayList<String>();
		
		for (Entry<Integer, String> currentEntry : edmoHash.entrySet()) {
			edmoTree.put(currentEntry.getKey(), currentEntry.getValue());
		}
	
		String strTemp = "";
		for (Entry<Integer, String> currentEntry : edmoTree.entrySet()) {
			strTemp = currentEntry.getKey() + " - " + currentEntry.getValue();
			EdmoHandler.listeOrigine.add(strTemp);
			comboIdEdmoRespCdi.add(strTemp);
			comboCodeEdmoDonnees.add(strTemp);
			comboIdEdmoRespDonnees.add(strTemp);
			comboIdEdmoDistDonnees.add(strTemp);
		}
		
		Utils.selectComboCode(comboIdEdmoRespCdi,Utils.configuration.getIdEdmoRespCdi());
		Utils.selectComboCode(comboCodeEdmoDonnees,Utils.configuration.getCodeEdmoDonnees());
		Utils.selectComboCode(comboIdEdmoRespDonnees,Utils.configuration.getIdEdmoRespDonnees());
		Utils.selectComboCode(comboIdEdmoDistDonnees,Utils.configuration.getIdEdmoDistDonnees());
		
		textUrlCdi.setText(Utils.configuration.getCdiURL());
		
//		 FAE 58 CDL 09/04/08
		
		CodeTableRecordType[] records = NemoLists.getVocabList("L071");
		
		TreeSet<String> champsMethodesCdi = new TreeSet<String>();
			
		for (CodeTableRecordType record : records)
		{	
			String champCombo = "";
			// System.out.println(record.getEntryKey());
			String contenuCleDecoup[] = record.getEntryKey().split("/");
			champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
			champCombo += " - " + record.getEntryTerm();
			champsMethodesCdi.add(champCombo);
		}
		
		for (String champ : champsMethodesCdi) {
			comboMethodeCdi.add(champ);
		}
		
		Utils.selectComboCode(comboMethodeCdi,Utils.configuration.getCdiMethod());
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

	/**
	 * This method initializes compoHaut
	 * 
	 */
	private void createCompoHaut() {
		compoHaut = new Composite(top, SWT.NONE);
		
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoHaut);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compoHaut);
		
		createCompoLCD();
		createGroupDossier();
	}
	
	/**
	 * This method initializes compoLCD
	 * 
	 */
	private void createCompoLCD() {
		compoLCD = new Composite(compoHaut, SWT.NONE);
		
		GridLayoutFactory.fillDefaults().applyTo(compoLCD);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compoLCD);
		
		createGroupLangue();
		createGroupCentreDonnees();
	}
	
	/**
	 * This method initializes groupLangue
	 * 
	 */
	private void createGroupLangue() {
		groupLangue = new Group(compoLCD, SWT.NONE);
//		groupLangue.setLayout(null);
		groupLangue.setText(Messages.getString("OptionsAction.10")); //$NON-NLS-1$
		createComboLangue();
//		groupLangue.setBounds(new Rectangle(4, 10, 168, 65)); // 166
		GridLayoutFactory.fillDefaults().applyTo(groupLangue);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupLangue);
		groupLangue.pack();
	}

	/**
	 * This method initializes comboLangue
	 * 
	 */
	private void createComboLangue() {
		comboLangue = new Combo(groupLangue, SWT.READ_ONLY);
//		comboLangue.setBounds(new Rectangle(2, 16, 160, 21));

		comboLangue.add(Messages.getString("OptionsAction.11")); //$NON-NLS-1$
		comboLangue.add(Messages.getString("OptionsAction.12")); //$NON-NLS-1$
		comboLangue.select(0);
	}
	
	/**
	 * This method initializes groupCentreDonnees
	 * 
	 */
	private void createGroupCentreDonnees() {
		groupCentreDonnees = new Group(compoLCD, SWT.NONE);
		groupCentreDonnees.setText(Messages.getString("OptionsAction.20"));
		GridLayoutFactory.fillDefaults().applyTo(groupCentreDonnees);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupCentreDonnees);
		
		createComboCentreDonnees();
	}
	
	/**
	 * This method initializes comboCentreDonnees
	 * 
	 */
	private void createComboCentreDonnees() {
		comboCentreDonnees = new Combo(groupCentreDonnees, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboCentreDonnees);
		comboCentreDonnees.pack();
	}
	
	/**
	 * This method initializes groupTop3
	 * 
	 */
	private void createCompoBouton() {
		compoBouton = new Composite(top, SWT.NONE);
		
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoBouton);
		GridDataFactory.fillDefaults().grab(true,false).align(SWT.CENTER, SWT.NONE).applyTo(compoBouton);
		
		buttonValider = new Button(compoBouton, SWT.NONE);
		buttonValider.setText(Messages.getString("OptionsAction.17")); //$NON-NLS-1$
		buttonValider.pack();
		
		buttonCancel = new Button(compoBouton, SWT.NONE);
		buttonCancel.setText(Messages.getString("OptionsAction.16")); //$NON-NLS-1$
		buttonCancel.pack();
		
		compoBouton.pack();
	}
	
	/**
	 * This method initializes groupDossier
	 * 
	 */
	private void createGroupDossier() {
		groupDossier = new Group(compoHaut, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(groupDossier);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupDossier);
		
		
//		groupDossier.setLayout(null);
		groupDossier.setText(Messages.getString("OptionsAction.5")); //$NON-NLS-1$
//		groupDossier.setBounds(new Rectangle(176, 10, 242, 150));
		
		/*labelModèle = new Label(groupDossier, SWT.NONE);
//		labelModèle.setBounds(new Rectangle(13, 15, 62, 13));
		labelModèle.setText(Messages.getString("OptionsAction.18")); //$NON-NLS-1$
		labelModèle.pack();*/
		
		groupDossier1 = new Group(groupDossier, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupDossier1);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupDossier1);
		groupDossier1.setText(Messages.getString("OptionsAction.18")); //$NON-NLS-1$
		groupDossier1.pack();
		
		textDossierModele = new Text(groupDossier1, SWT.BORDER);
//		textDossierModèle.setBounds(new Rectangle(5, 31, 229, 19));
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textDossierModele);
		buttonParcourirModele = new Button(groupDossier1, SWT.NONE);
//		buttonParcourirModèle.setBounds(new Rectangle(170, 53, 64, 23)); // 64
		buttonParcourirModele.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
		buttonParcourirModele.pack();
		
		/*labelCampagne = new Label(groupDossier, SWT.NONE);
//		labelCampagne.setBounds(new Rectangle(13, 80, 62, 13));
		labelCampagne.setText(Messages.getString("OptionsAction.19")); //$NON-NLS-1$
		labelCampagne.pack();*/
		
		groupDossier2 = new Group(groupDossier, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupDossier2);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupDossier2);
		groupDossier2.setText(Messages.getString("OptionsAction.19")); //$NON-NLS-1$
		groupDossier2.pack();
		
		textDossierCampagne = new Text(groupDossier2, SWT.BORDER);
//		textDossierCampagne.setBounds(new Rectangle(5, 96, 229, 19));
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textDossierCampagne);
		buttonParcourirCampagne = new Button(groupDossier2, SWT.NONE);
//		buttonParcourirCampagne.setBounds(new Rectangle(170, 118, 64, 23)); // 64
		buttonParcourirCampagne.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
		buttonParcourirCampagne.pack();
		

		// listener sur le bouton
		buttonParcourirModele
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println(Messages
//								.getString("OptionsAction.7")); //$NON-NLS-1$

						String nomRepertoire;
						Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
						DirectoryDialog dialog = new DirectoryDialog(shell,
								SWT.OPEN | SWT.APPLICATION_MODAL);
						nomRepertoire = dialog.open();
						if (nomRepertoire != null) {
							textDossierModele.setText(nomRepertoire);

						} else {

//							System.out.println("pas de dossier sélectionné"); //$NON-NLS-1$
							ViewLog.styledTextLogErreurs.setText(Messages
									.getString("OptionsAction.9")); //$NON-NLS-1$

						}

					}
				});

//		 listener sur le bouton
		buttonParcourirCampagne
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println(Messages
//								.getString("OptionsAction.7")); //$NON-NLS-1$

						String nomRepertoire;
						Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
						DirectoryDialog dialog = new DirectoryDialog(shell,
								SWT.OPEN | SWT.APPLICATION_MODAL);
						nomRepertoire = dialog.open();
						if (nomRepertoire != null) {
							textDossierCampagne.setText(nomRepertoire);

						} else {

//							System.out.println("pas de dossier sélectionné"); //$NON-NLS-1$
							ViewLog.styledTextLogErreurs.setText(Messages
									.getString("OptionsAction.9")); //$NON-NLS-1$

						}

					}
				});
	}

	/**
	 * This method initializes groupWS
	 * 
	 */
	private void createGroupWS() {
		groupWS = new Group(top, SWT.NONE);
//		groupWS.setLayout(null);
		groupWS.setText("Web Services"); //$NON-NLS-1$
//		groupWS.setBounds(new Rectangle(4, 169, 414, 146));
		GridLayoutFactory.fillDefaults().applyTo(groupWS);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupWS);

		compoWS = new Composite(groupWS, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoWS);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(compoWS);
		
		labelNemo = new Label(compoWS, SWT.NONE);
//		labelNemo.setBounds(new Rectangle(13, 40, 62, 13));
		labelNemo.setText("NEMO"); //$NON-NLS-1$
		labelNemo.pack();

		textAreaNemo = new Text(compoWS, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
//		textAreaNemo.setBounds(new Rectangle(85, 18, 316, 55));
		GridDataFactory.fillDefaults().grab(true,true).applyTo(textAreaNemo);
		
		labelNautilus = new Label(compoWS, SWT.NONE);
//		labelNautilus.setBounds(new Rectangle(13, 100, 62, 13));
		labelNautilus.setText("NAUTILUS"); //$NON-NLS-1$
		labelNautilus.pack();
				
		textAreaNautilus = new Text(compoWS, SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
//		textAreaNautilus.setBounds(new Rectangle(85, 82, 316, 55));
		GridDataFactory.fillDefaults().grab(true,true).applyTo(textAreaNautilus);
		
		// WS Vocab		
		compoWSVocab1 = new Composite(groupWS, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoWSVocab1);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(compoWSVocab1);
		
		labelWSVocab = new Label(compoWSVocab1, SWT.NONE);
		labelWSVocab.setText(Messages.getString("OptionsAction.28"));
		textVocabURL = new Text(compoWSVocab1, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textVocabURL);
		
//		compoWSVocab2 = new Composite(groupWS, SWT.NONE);
//		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoWSVocab2);
//		GridDataFactory.fillDefaults().grab(true,true).applyTo(compoWSVocab2);
		
		labelWSPredicate = new Label(compoWSVocab1, SWT.NONE);
		labelWSPredicate.setText(Messages.getString("OptionsAction.29"));
		
		textWSPredicate = new Text(compoWSVocab1,SWT.BORDER);
		
		labelWSTag = new Label(compoWSVocab1, SWT.NONE);
		labelWSTag.setText(Messages.getString("OptionsAction.30"));
		
		comboWSTag = new Combo(compoWSVocab1, SWT.READ_ONLY);
	}
	
	/**
	 * This method initializes groupCDI
	 * 
	 */
	private void createGroupCDI() {
		groupCDI = new Group(top, SWT.NONE);
		groupCDI.setText("CDI");

		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupCDI);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(groupCDI);
		
		labelIdEdmoRespCdi = new Label(groupCDI, SWT.NONE);
		labelIdEdmoRespCdi.setText(Messages.getString("OptionsAction.21"));
		labelIdEdmoRespCdi.pack();
		
		comboIdEdmoRespCdi = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboIdEdmoRespCdi);
		
		labelCodeEdmoDonnees = new Label(groupCDI, SWT.NONE);
		labelCodeEdmoDonnees.setText(Messages.getString("OptionsAction.24"));
		labelCodeEdmoDonnees.pack();
		
		comboCodeEdmoDonnees = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboCodeEdmoDonnees);
		
		labelIdEdmoRespDonnees = new Label(groupCDI, SWT.NONE);
		labelIdEdmoRespDonnees.setText(Messages.getString("OptionsAction.25"));
		labelIdEdmoRespDonnees.pack();
		
		comboIdEdmoRespDonnees = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboIdEdmoRespDonnees);
		
		labelIdEdmoDistDonnees = new Label(groupCDI, SWT.NONE);
		labelIdEdmoDistDonnees.setText(Messages.getString("OptionsAction.26"));
		labelIdEdmoDistDonnees.pack();
		
		comboIdEdmoDistDonnees = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboIdEdmoDistDonnees);
		
		labelUrlCdi = new Label(groupCDI, SWT.NONE);
		labelUrlCdi.setText(Messages.getString("OptionsAction.22"));
		labelUrlCdi.pack();
		
		textUrlCdi = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textUrlCdi);
		
		labelMethodeCdi = new Label(groupCDI, SWT.NONE);
		labelMethodeCdi.setText(Messages.getString("OptionsAction.23"));
		labelMethodeCdi.pack();
		
		comboMethodeCdi = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(comboMethodeCdi);
		
		// Menu recherche combos
		createMenu(comboIdEdmoRespCdi);
		createMenu(comboCodeEdmoDonnees);
		createMenu(comboIdEdmoRespDonnees);
		createMenu(comboIdEdmoDistDonnees);
	}
	
	private void createMenu(Combo combo) {
		Menu menu = new Menu(comboIdEdmoRespCdi.getShell(), SWT.POP_UP);
		
		MenuItem itemSearch = new MenuItem(menu, SWT.NONE);
		itemSearch.setText(Messages.getString("OptionsAction.27")); //$NON-NLS-1$
		
		combo.setMenu(menu);
		
		final Combo comb = combo;
		
		itemSearch.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				comboRecherche = comb;
				FiltrageTableType.go();
				
			}
		});
	}

	public void createPartControl(Composite parent) {

		top = new Composite(parent, SWT.NONE);
		
		GridLayoutFactory.fillDefaults().applyTo(top);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(top);
		
//		top.setLayout(null);
/*		createGroupLangue();
		createGroupDossier();*/
		createCompoHaut();
		createGroupWS();
		createGroupCDI();
		createCompoBouton();
		/*buttonCancel = new Button(top, SWT.NONE);
//		buttonCancel.setBounds(new Rectangle(224, 333, 69, 23)); // 66
		buttonCancel.setText(Messages.getString("OptionsAction.16")); //$NON-NLS-1$
		buttonCancel.pack();*/

		/*buttonValider = new Button(top, SWT.NONE);
//		buttonValider.setBounds(new Rectangle(139, 333, 69, 23));
		buttonValider.setText(Messages.getString("OptionsAction.17")); //$NON-NLS-1$
		buttonValider.pack();*/

		// listener cancel
		buttonCancel
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("options cancel");
						OptionsAction.options = 0;
						shelloptions.close();

					}
				});

		// listener valider
		buttonValider
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("options valider");

						// sauvegarde de la config
						Utils.saveConfig();

						OptionsAction.options = 0;
						shelloptions.close();

					}
				});

	}

}
