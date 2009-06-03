package fr.ifremer.nemo.ui.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

import com.ago.xml.XMLBootstrapParser;

import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class OpenAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomModele, nomFichierModele;

    public OpenAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.menu_openModel);
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    @Override
    public void run()
    {
        FileInputStream stream = null;

        // verifier que un fichier est charge avant d'ouvrir un modele
        if (!ViewMain.editFileForm.loaded)
        {
            Utils.messageErreurAppend(Messages.error_openModel);
        }
        else
        {
            InputModel inputModel = new InputModel();

            Shell shell =
                PlatformUI.getWorkbench().getDisplay().getActiveShell();
            FileDialog dialog =
                new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
            dialog.setFilterPath(Utils.configuration.getModelsDirectory()
                    + File.separator);
            dialog.setText(Messages.dialog_openModel);

            String extensions[] = new String[1];
            extensions[0] = Global.STAR + Global.EXT_XML;
            dialog.setFilterExtensions(extensions);

            nomModele = dialog.open();

            if (nomModele != null)
            {
                // ouverture du fichier XML de modele
                XMLBootstrapParser parser = new XMLBootstrapParser(inputModel);

                try
                {
                    stream = new java.io.FileInputStream(nomModele);
                }
                catch (FileNotFoundException e)
                {
                    Utils.messageErreurAppend(Messages.error);
                    e.printStackTrace();
                }

                parser.parse(stream, false);

                File fich = new File(nomModele);
                nomFichierModele = fich.getName();

                DataModel.ouvertureModele(inputModel);
                DataModel.ouverture = true;

                ViewLog.styledTextLogInfos.append(Messages.success_openModel
                        + nomModele);

                // reinitialisation du tableau de paramètres
                if (ViewMain.tableData != null)
                    TableData.go();
            }
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }

}
