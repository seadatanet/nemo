package fr.ifremer.nemo.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;

public class ViewLog extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewLog"; // TODO Needs to be whatever is mentioned in plugin.xml

	private Composite top = null;

	public static TabFolder tabFolder = null;

	public static TabFolder tabFolder1 = null;

	public static TabFolder tabFolder2 = null;

	public static TabFolder tabFolder3 = null;

	

	private Composite compositeLogGauche = null;

	private Composite compositeLogDroite = null;

	private Button buttonLogBack = null;

	private Button buttonLogNext = null;

	private Button buttonLogConversion = null;

	public static TabFolder tabFolderLog = null;

	public static StyledText styledTextLogErreurs, styledTextLogInfos, styledTextLogHelp = null;

	

	@Override
	public void createPartControl(Composite parent) {
		// TODO Raccord de méthode auto-généré
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		//createCompositeLogDroite();
	
	}

	@Override
	public void setFocus() {
		// TODO Raccord de méthode auto-généré

	}

	/**
	 * This method initializes compositeLogGauche	
	 *
	 */
	private void createCompositeLogGauche() {
		compositeLogGauche = new Composite(top, SWT.NONE);
		compositeLogGauche.setLayout(new FillLayout());
		createTabFolderLog();
	}

	/*
	
	
	private void createCompositeLogDroite() {
		compositeLogDroite = new Composite(top, SWT.NONE);
		compositeLogDroite.setLayout(new RowLayout());
		buttonLogBack = new Button(compositeLogDroite, SWT.NONE);
		buttonLogBack.setText("Précédent");
		buttonLogBack.setEnabled(false);
		buttonLogNext = new Button(compositeLogDroite, SWT.NONE);
		buttonLogNext.setText("Suivant");
		buttonLogConversion = new Button(compositeLogDroite, SWT.NONE);
		buttonLogConversion.setText("Conversion");
		buttonLogConversion.setEnabled(false);
	}

	*/
	
	/**
	 * This method initializes tabFolderLog	
	 *
	 */
	private void createTabFolderLog() {
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("widgetSelected()"); // TODO Auto-generated Event stub widgetSelected()
					}
				});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText("Erreurs");
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText("Infos");
		TabItem tabItem2 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem2.setText("Aide");
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.READ_ONLY);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.READ_ONLY);
		styledTextLogHelp = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.READ_ONLY);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

public static void aideDesc(){
ViewLog.styledTextLogHelp.setText(
		"Onglet Description de Campagne\n\n" +
		"Le principe est de décrire une campagne en délimitant les zones des métadonnées au sein des entetes fichier ou station.\n" +
		"Pour un fichier de campagne : il faut bien différencier l'entete du fichier global et l'entete d'une station.");	
}	

public static void aideSurvey(){
	
}

public static void aideStation(){
	
}

public static void aideData(){
	
}


}
