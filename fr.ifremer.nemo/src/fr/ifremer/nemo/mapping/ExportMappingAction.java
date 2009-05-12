/* ============================================================================
 * Nom du fichier	: OpenMappingAction.java
 * Auteur			: jollivie
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.mapping;

/**
 * Description: ExportMappingAction
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

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class ExportMappingAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomMapping;

    public ExportMappingAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.ExportMappingAction_1);
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
            new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setFilterPath(Utils.configuration.getModelsDirectory()
                + File.separator);
        dialog.setText(Messages.ExportMappingAction_5);

        dialog.setFileName(Configuration.getSingleton().getFichierMapping());
        String extensions[] = new String[1];
        extensions[0] = Global.STAR + Global.EXT_TXT;
        dialog.setFilterExtensions(extensions);
        nomMapping = dialog.open();

        if (nomMapping == null)
        {
        }
        else
        {

            // ouverture du fichier XML de modele
            new MappingTable().exporter(nomMapping);
        }

    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }

}
