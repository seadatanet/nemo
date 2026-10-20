package fr.ifremer.nemo.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.util.Messages;

public class ViewLog extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.view.ViewLog"; //$NON-NLS-1$

	private Composite top = null;

	private Composite compositeLogGauche = null;

	public static TabFolder tabFolderLog = null;

	public static StyledText styledTextLogErreurs;

	public static StyledText styledTextLogInfos = null;

	@Override
	public void createPartControl(Composite parent) {

		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		// createCompositeLogDroite();

	}

	@Override
	public void setFocus() {
		//

	}

	/**
	 * This method initializes compositeLogGauche
	 */
	private void createCompositeLogGauche() {
		compositeLogGauche = new Composite(top, SWT.NONE);
		compositeLogGauche.setLayout(new FillLayout());
		createTabFolderLog();
	}

	/**
	 * This method initializes tabFolderLog
	 */
	private void createTabFolderLog() {
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			}
		});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText(Messages.ViewLog_2);
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText(Messages.ViewLog_3);
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

}
