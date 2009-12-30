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
import java.io.File;

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

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class ImportMappingAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomMapping;

    public ImportMappingAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.ImportMappingAction_1);
        //setText("Import"); //$NON-NLS-1$
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    @Override
    public void run()
    {

        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        FileDialog dialog =
            new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
        dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory()
                + File.separator);
        dialog.setText(Messages.ImportMappingAction_5);
        String extensions[] = new String[1];
        extensions[0] = Global.STAR + Global.EXT_TXT;
        dialog.setFilterExtensions(extensions);
        nomMapping = dialog.open();

        if (nomMapping == null)
        {
        }
        else
        {

            new MappingTable().importer(nomMapping);
        }

    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }

}
