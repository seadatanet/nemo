package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import com.ago.xml.Element2Field;

import fr.ifremer.nemo.config.NemoLists;

public class OptionsAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {

	private final IWorkbenchWindow window;

	public static Composite top, compoHaut, compoLCD, compoBouton = null;
	
	public static Group groupLangue, groupCentreDonnees, groupWS, 
			groupDossier, groupDossier1, groupDossier2, groupCDI = null;

	public static Combo comboLangue, comboCentreDonnees = null;

	public static Button buttonCancel, buttonValider, buttonParcourirModèle,
			buttonParcourirCampagne = null;

	public static Label labelNemo, labelNautilus, labelModèle, labelCampagne,
			labelIdEdmoRespCdi, labelUrlCdi, labelMethodeCdi,
			labelCodeEdmoDonnées, labelIdEdmoRespDonnées, labelIdEdmoDistDonnées = null;

	public static Text textAreaNemo, textAreaNautilus, textDossierModèle, textDossierCampagne, 
			textIdEdmoRespCdi, textUrlCdi, textMethodeCdi,
			textCodeEdmoDonnées, textIdEdmoRespDonnées, textIdEdmoDistDonnées = null;

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

		shelloptions.setSize(600, 300);
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
		textDossierModèle.setText(Utils.configuration.getModelsDirectory());
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

		int i = 0;
		comboCentreDonnees.select(i);
		String dataCenter = Utils.configuration.getDataCenter();
		int j = dataCenter.length();
		while (i < taille && !comboCentreDonnees.getText().substring(0,j).equals(dataCenter)) {
			i++;
			comboCentreDonnees.select(i);
		}

		// WS
		textAreaNemo.setText(Utils.configuration.getNemoWebServiceURL());
		textAreaNautilus
				.setText(Utils.configuration.getNautilusWebServiceURL());
		
		// CDI
		textIdEdmoRespCdi.setText(Utils.configuration.getIdEdmoRespCdi());
		textCodeEdmoDonnées.setText(Utils.configuration.getCodeEdmoDonnées());
		textIdEdmoRespDonnées.setText(Utils.configuration.getIdEdmoRespDonnées());
		textIdEdmoDistDonnées.setText(Utils.configuration.getIdEdmoDistDonnées());
		textUrlCdi.setText(Utils.configuration.getCdiURL());
		textMethodeCdi.setText(Utils.configuration.getCdiMethod());

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
		
		textDossierModèle = new Text(groupDossier1, SWT.BORDER);
//		textDossierModèle.setBounds(new Rectangle(5, 31, 229, 19));
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textDossierModèle);
		buttonParcourirModèle = new Button(groupDossier1, SWT.NONE);
//		buttonParcourirModèle.setBounds(new Rectangle(170, 53, 64, 23)); // 64
		buttonParcourirModèle.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
		buttonParcourirModèle.pack();
		
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
		buttonParcourirModèle
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println(Messages
//								.getString("OptionsAction.7")); //$NON-NLS-1$

						String nomRepertoire;
						Shell shell = new Shell();
						DirectoryDialog dialog = new DirectoryDialog(shell,
								SWT.OPEN | SWT.APPLICATION_MODAL);
						nomRepertoire = dialog.open();
						if (nomRepertoire != null) {
							textDossierModèle.setText(nomRepertoire);

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
						Shell shell = new Shell();
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
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupWS);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupWS);

		labelNemo = new Label(groupWS, SWT.NONE);
//		labelNemo.setBounds(new Rectangle(13, 40, 62, 13));
		labelNemo.setText("NEMO"); //$NON-NLS-1$
		labelNemo.pack();

		textAreaNemo = new Text(groupWS, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
//		textAreaNemo.setBounds(new Rectangle(85, 18, 316, 55));
		GridDataFactory.fillDefaults().grab(true,true).applyTo(textAreaNemo);
		
		labelNautilus = new Label(groupWS, SWT.NONE);
//		labelNautilus.setBounds(new Rectangle(13, 100, 62, 13));
		labelNautilus.setText("NAUTILUS"); //$NON-NLS-1$
		labelNautilus.pack();
				
		textAreaNautilus = new Text(groupWS, SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
//		textAreaNautilus.setBounds(new Rectangle(85, 82, 316, 55));
		GridDataFactory.fillDefaults().grab(true,true).applyTo(textAreaNautilus);
	}
	
	/**
	 * This method initializes groupCDI
	 * 
	 */
	private void createGroupCDI() {
		groupCDI = new Group(top, SWT.NONE);
		groupCDI.setText("CDI");

		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupCDI);
		GridDataFactory.fillDefaults().grab(true,true).applyTo(groupCDI);
		
		labelIdEdmoRespCdi = new Label(groupCDI, SWT.NONE);
		labelIdEdmoRespCdi.setText(Messages.getString("OptionsAction.21"));
		labelIdEdmoRespCdi.pack();
		
		textIdEdmoRespCdi = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textIdEdmoRespCdi);
		
		labelCodeEdmoDonnées = new Label(groupCDI, SWT.NONE);
		labelCodeEdmoDonnées.setText(Messages.getString("OptionsAction.24"));
		labelCodeEdmoDonnées.pack();
		
		textCodeEdmoDonnées = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textCodeEdmoDonnées);
		
		labelIdEdmoRespDonnées = new Label(groupCDI, SWT.NONE);
		labelIdEdmoRespDonnées.setText(Messages.getString("OptionsAction.25"));
		labelIdEdmoRespDonnées.pack();
		
		textIdEdmoRespDonnées = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textIdEdmoRespDonnées);
		
		labelIdEdmoDistDonnées = new Label(groupCDI, SWT.NONE);
		labelIdEdmoDistDonnées.setText(Messages.getString("OptionsAction.26"));
		labelIdEdmoDistDonnées.pack();
		
		textIdEdmoDistDonnées = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textIdEdmoDistDonnées);
		
		labelUrlCdi = new Label(groupCDI, SWT.NONE);
		labelUrlCdi.setText(Messages.getString("OptionsAction.22"));
		labelUrlCdi.pack();
		
		textUrlCdi = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textUrlCdi);
		
		labelMethodeCdi = new Label(groupCDI, SWT.NONE);
		labelMethodeCdi.setText(Messages.getString("OptionsAction.23"));
		labelMethodeCdi.pack();
		
		textMethodeCdi = new Text(groupCDI, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).applyTo(textMethodeCdi);
		
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
		//TODO createGroupCDI();
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

						shelloptions.close();

					}
				});

	}

}
