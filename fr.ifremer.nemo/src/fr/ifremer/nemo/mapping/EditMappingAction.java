/* ============================================================================
 * Nom du fichier   : OpenMappingAction.java
 * Auteur           : jollivie
 * Date             : 21 janv. 09
 * Copyright        : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.mapping;

/**
 * Description: TODO
 * @author jollivie
 *
 */
import java.awt.Dialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

public class EditMappingAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction {
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.EditMappingAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomModele, nomFichierModele;

    public EditMappingAction(IWorkbenchWindow window) {

        this.window = window;
        setId(ID);
        //setText(Messages.getString("OpenAction.1")); //$NON-NLS-1$
        setText("Editer"); //$NON-NLS-1$
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose() {
        window.getSelectionService().addSelectionListener(this);

    }

    public void run() {
        //InputModel inputModel = new InputModel();

        MappingDatabaseEditApp mappingEditeur = MappingDatabaseEditApp.getApplication();
        mappingEditeur.getMainFrame().setAlwaysOnTop(true);
      //  mappingEditeur.getMainFrame()
        mappingEditeur.startup();

        
        //Application te = new MappingDatabaseEditApp();
        
        //Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();      

        //new String[] arg = {""};
        //te.launch(MappingDatabaseEditApp.class, new String[1]);
    
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
    }

}
