/* ============================================================================
 * Nom du fichier	: OpenMappingAction.java
 * Auteur			: jollivie
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.mapping;

/**
 * Description: ImportMappingAction
 * @author jollivie
 *
 */
import java.io.FileInputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Messages;

public class ImportMappingAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction {
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomMapping;

    public ImportMappingAction(IWorkbenchWindow window) {

        this.window = window;
        setId(ID);
        setText(Messages.getString("ImportMappingAction.1")); //$NON-NLS-1$
        //setText("Import"); //$NON-NLS-1$
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose() {
        window.getSelectionService().addSelectionListener(this);

    }

    public void run() {
        FileInputStream stream = null;
//      System.out.println("Menu open"); //$NON-NLS-1$


            InputModel inputModel = new InputModel();

            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            FileDialog dialog = new FileDialog(shell, SWT.OPEN
                    | SWT.APPLICATION_MODAL);
            dialog.setFilterPath(Utils.configuration.getModelsDirectory()
                    + "\\"); //$NON-NLS-1$
            dialog.setText(Messages.getString("ImportMappingAction.5")); //$NON-NLS-1$
            String extensions[] = new String[1];
            extensions[0] = "*.txt"; //$NON-NLS-1$
            dialog.setFilterExtensions(extensions);
            nomMapping = dialog.open();

            if (nomMapping == null) {
            } else {
                
                new MappingTable().importer(nomMapping);
            }

    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
    }

}
