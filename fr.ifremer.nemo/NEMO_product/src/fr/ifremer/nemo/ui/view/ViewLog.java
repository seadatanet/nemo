package fr.ifremer.nemo.ui.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class ViewLog extends ViewPart
{

	public static final String ID = "fr.ifremer.nemo.ui.view.ViewLog"; //$NON-NLS-1$

	private Composite top = null;

	private Composite compositeLogGauche = null;

	public static TabFolder tabFolderLog = null;

	public static StyledText styledTextLogErreurs;

	public static StyledText styledTextLogInfos,
			styledTextLogHelp = null;

	private static StringBuffer aideFile = new StringBuffer();
	private static StringBuffer aideCruise = new StringBuffer();
	private static StringBuffer aideStation = new StringBuffer();
	private static StringBuffer aideData = new StringBuffer();
	private static StringBuffer aideConversion = new StringBuffer();

	@Override
	public void createPartControl(Composite parent)
	{

		getHelpText();
		//
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		// createCompositeLogDroite();

	}

	private void getHelpText()
	{
		try
		{
			// récupération de l'aide en ligne
			String langue = Nemo.getInstance().getConfig().getLang();

			String fileName =
					Intern.dir_resources + File.separator + Intern.help_file + langue
							+ Global.EXT_TXT;

			File file = new File(fileName);
			FileInputStream stream = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(stream, "UTF8"); //$NON-NLS-1$
			BufferedReader bufferedReader = new BufferedReader(reader);

			String readedLine = Global.EMPTY_STRING;
			int cpt = 0;
			while ((readedLine = bufferedReader.readLine()) != null)
			{
				if (readedLine.startsWith(Intern.help_separator))
				{
					cpt++;
				} else
				{
					switch (cpt)
					{
					case 0:
						aideFile.append(readedLine);
						aideFile.append(Global.RN);
						break;
					case 1:
						aideCruise.append(readedLine);
						aideCruise.append(Global.RN);
						break;
					case 2:
						aideStation.append(readedLine);
						aideStation.append(Global.RN);
						break;
					case 3:
						aideData.append(readedLine);
						aideData.append(Global.RN);
						break;
					case 4:
						aideConversion.append(readedLine);
						aideConversion.append(Global.RN);
						break;
					}
				}
			}
		} catch (Exception e)
		{
			// do nothing
		}
	}

	@Override
	public void setFocus()
	{
		//

	}

	/**
	 * This method initializes compositeLogGauche
	 */
	private void createCompositeLogGauche()
	{
		compositeLogGauche = new Composite(top, SWT.NONE);
		compositeLogGauche.setLayout(new FillLayout());
		createTabFolderLog();
	}

	/**
	 * This method initializes tabFolderLog
	 */
	private void createTabFolderLog()
	{
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
				{
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e)
				{
				}
				});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText(Messages.ViewLog_2);
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText(Messages.ViewLog_3);
		TabItem tabItem2 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem2.setText(Messages.ViewLog_4);
		styledTextLogErreurs =
				new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
						| SWT.READ_ONLY);
		styledTextLogInfos =
				new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);
		styledTextLogHelp =
				new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP
						| SWT.READ_ONLY);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

	public static void aideFile()
	{
		try {
			ViewLog.styledTextLogHelp.setText(aideFile.toString());
		}catch (Exception e) {
			// ViewLog can be null the first time (windows), so no error
		}
	}

	public static void aideCruise()
	{
		try {
			ViewLog.styledTextLogHelp.setText(aideCruise.toString());
		}catch (Exception e) {
			// ViewLog can be null the first time (windows), so no error
		}
	}

	public static void aideStation()
	{
		try {
			ViewLog.styledTextLogHelp.setText(aideStation.toString());
		}catch (Exception e) {
			// ViewLog can be null the first time (windows), so no error
		}
	}

	public static void aideData()
	{
		try {
			ViewLog.styledTextLogHelp.setText(aideData.toString());
		}catch (Exception e) {
			// ViewLog can be null the first time (windows), so no error
		}
	}

	public static void aideConversion()
	{
		try {
			ViewLog.styledTextLogHelp.setText(aideConversion.toString());
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

}
