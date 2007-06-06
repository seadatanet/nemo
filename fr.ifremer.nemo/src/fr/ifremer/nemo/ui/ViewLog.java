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

	private TabFolder tabFolder = null;

	private TabFolder tabFolder1 = null;

	private TabFolder tabFolder2 = null;

	private TabFolder tabFolder3 = null;

	

	private Composite compositeLogGauche = null;

	private Composite compositeLogDroite = null;

	private Button buttonLogBack = null;

	private Button buttonLogNext = null;

	private Button buttonLogConversion = null;

	private TabFolder tabFolderLog = null;

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
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL);
		styledTextLogHelp = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

	

}
