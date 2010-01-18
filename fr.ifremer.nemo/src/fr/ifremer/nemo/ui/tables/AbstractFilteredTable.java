/* ============================================================================
 * Nom du fichier	: AbstractFilteredTable.java
 * Auteur			: ycaudal
 * Date				: 22 déc. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.tables;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
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
 * Description: TODO
 * 
 * @author ycaudal
 * 
 */
public abstract class AbstractFilteredTable
{
    protected Composite top;

    protected Table filtreTableData, filtreTableSurvey;

    protected Combo _comboRecherche;

    protected Text filtreText;

    protected Shell shell;

    protected Label labelText;

    protected Button filtreButtonSelectionner;

    protected Button filtreButtonCancel;

    public void open()
    {
        try
        {

            Display display = Application.display;
            
            shell = 
                new Shell(display, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
            
            shell.setSize(300, 500);

            shell.setLayout(new FillLayout());

            shell.setText(getDialogTitle());

            top = new Composite(shell, SWT.NONE);
            top.setLayout(null);

            createDataList();

            filtreText = new Text(top, SWT.BORDER);
            filtreText.setBounds(new Rectangle(3, 21, 298, 19));
            filtreText.setText(Global.EMPTY_STRING);
            labelText = new Label(top, SWT.NONE);
            labelText.setBounds(new Rectangle(3, 4, 298, 13));
            labelText.setText(Messages.table_label_filter);
            labelText.pack();

            filtreButtonSelectionner = new Button(top, SWT.NONE);
            filtreButtonSelectionner.setBounds(new Rectangle(62, 289, 65, 23));
            filtreButtonSelectionner.setText(Messages.button_select);
            filtreButtonSelectionner.pack();
            filtreButtonCancel = new Button(top, SWT.NONE);
            filtreButtonCancel.setBounds(new Rectangle(168, 289, 65, 23));
            filtreButtonCancel.setText(Messages.button_cancel);
            filtreButtonCancel.pack();

            createTable();

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
              if (!display.readAndDispatch()) {
                // If no more entries in event queue
                display.sleep();
              }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
    protected abstract void createTable();

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
