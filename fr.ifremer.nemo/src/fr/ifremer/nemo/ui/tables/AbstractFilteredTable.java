/* ============================================================================
 * Nom du fichier	: AbstractFilteredTable.java
 * Auteur			: ycaudal
 * Date				: 22 déc. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: Classe abstraite pour la gestion des tables sur lesquelles on peut filtrer les données
 * 
 * @author ycaudal
 * 
 */
public abstract class AbstractFilteredTable
{
	protected Composite top;

	//    protected Table filtreTableData, filtreTableSurvey;
	/**
	 * la table permettant d'afficher les parametres
	 */
	protected Table myFiltreTable;

	protected CCombo _comboRecherche;

	protected Text filtreText;

	protected Shell shell;

	protected Label labelText;

	protected Button filtreButtonSelectionner;

	protected Button filtreButtonCancel;

	public void open(final Composite parent)
	{
		Display.getCurrent().syncExec(new Runnable(){

			public void run()
			{
				try
				{
					//                    shell = new Shell(parent.getShell(), SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL |SWT.RESIZE);
					shell = new Shell(Application.display, SWT.APPLICATION_MODAL|SWT.CLOSE|SWT.RESIZE);

					shell.setSize(400, 400);

					//                    GridLayout gridLayout = new GridLayout(1,false);
					//                    shell.setLayout(gridLayout);
					shell.setLayout(new FillLayout());

					shell.setText(getDialogTitle());

					top = new Composite(shell, SWT.NONE);
					//                    top.setLayout(null);
					GridLayout gridLayout = new GridLayout(2,false);

					top.setLayout(gridLayout);
					createDataList();

					labelText = new Label(top, SWT.NONE);
					//                  labelText.setBounds(new Rectangle(3, 4, 298, 13));
					labelText.setText(Messages.table_label_filter);
					labelText.pack();
					filtreText = new Text(top, SWT.BORDER);

					//                    filtreText.setBounds(new Rectangle(3, 21, 298, 19));
					filtreText.setText(Global.EMPTY_STRING);


					createTable();

					filtreButtonSelectionner = new Button(top, SWT.NONE);
					//                    filtreButtonSelectionner.setBounds(new Rectangle(62, 289, 65, 23));
					filtreButtonSelectionner.setText(Messages.button_select);
					filtreButtonSelectionner.pack();
					filtreButtonCancel = new Button(top, SWT.NONE);
					//                    filtreButtonCancel.setBounds(new Rectangle(168, 289, 65, 23));
					filtreButtonCancel.setText(Messages.button_cancel);
					filtreButtonCancel.pack();



					filtreButtonCancel
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						@Override
						public void widgetSelected(
								org.eclipse.swt.events.SelectionEvent e)
						{
							shell.close();
						}
					});

					filtreButtonSelectionner
					.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
						@Override
						public void widgetSelected(
								org.eclipse.swt.events.SelectionEvent e)
						{
							selectAction();
						} // ///////////////FIN

					});

					// listener keypressed sur le champ text
					filtreText.addListener(SWT.KeyUp, new Listener() {
						public void handleEvent(Event e)
						{
							filterAction(filtreText.getText());
						}
					});

					shell.pack();
					shell.open();

					// Set up the event loop.
					while (!shell.isDisposed()) {
						if (!Display.getCurrent().readAndDispatch()) {
							// If no more entries in event queue
							Display.getCurrent().sleep();
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		});

	}
	protected void createTable(){
		myFiltreTable = new Table(top, SWT.FULL_SELECTION|SWT.V_SCROLL);

		myFiltreTable.setHeaderVisible(true);
		myFiltreTable.setLinesVisible(true);
		myFiltreTable.setSize(30, 30);
		//		myFiltreTable.setBounds(new Rectangle(3, 45, 300, 300));
		GridData gd_table = new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1);
		gd_table.heightHint=600;
		myFiltreTable.setLayoutData(gd_table);
	}
	protected void refreshTable(){
		myFiltreTable.removeAll();
		populateTableWithFilteredList();
	}
	protected abstract void populateTableWithFilteredList();
	/**
	 * 
	 */
	protected abstract void filterAction(String value);

	/**
	 * 
	 */
	protected abstract void selectAction();


	/**
	 * 
	 */
	protected abstract void createDataList();

	/**
	 * @return
	 */
	protected abstract String getDialogTitle();

	protected void razTable(Table table)
	{
		// maj
		table.dispose();
	}
}
