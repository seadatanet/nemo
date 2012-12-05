/* ============================================================================
 * Nom du fichier	: MyProgressMonitorDialog.java
 * Auteur			: dboulang
 * Date				: 26 mai 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Description: surdéfinition de ProgressMonitorDialog 
 *              pour changer le titre de la fenêtre et le label du bouton Cancel
 * @author dboulang
 *
 */
public class MyProgressMonitorDialog extends ProgressMonitorDialog
{
    private String shellTitle;
    private String cancelLabel;
    
    

    /**
     * @param parent
     * @param shellTitle
     * @param cancelLabel
     */
    public MyProgressMonitorDialog(Shell parent, String shellTitle, String cancelLabel)
    {
        super(parent);
        this.shellTitle = shellTitle;
        this.cancelLabel = cancelLabel;
    }
    
     /*
      * (non-Javadoc) Method declared in Window.
      */
     @Override
     protected void configureShell(final Shell shell) {
         super.configureShell(shell);
         if (shellTitle != null)
             shell.setText(shellTitle);
     }
     
     /*
      * (non-Javadoc) Method declared in ProgressMonitorDialog.
      */
     @Override
     protected void createCancelButton(Composite parent) {
         super.createCancelButton(parent);
         if (cancelLabel != null)
             cancel.setText(cancelLabel);
     }

}
