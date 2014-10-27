package fr.ifremer.nemo.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class OpenAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.OpenAction"; //$NON-NLS-1$

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
        // verifier que un fichier est charge avant d'ouvrir un modele
        if (!Nemo.getInstance().getViewMain().getEditFileForm().isLoaded())
        {
            Utils.messageErreurAppend(Messages.error_openModel);
        }
        else
        {
            Shell shell = Utils.getMainShell();
            FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
            dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory() + File.separator);
            dialog.setText(Messages.dialog_openModel);

            String extensions[] = new String[1];
            extensions[0] = Global.STAR + Global.EXT_XML;
            dialog.setFilterExtensions(extensions);

            String nomModele = dialog.open();

            if (nomModele != null)
            {
                try
                {
                    // FAE 9656 si model non validé affichage d'un warning
                    if (!Nemo.getInstance().openModel(nomModele, null))
                    {
                        MessageBox mb =
                            new MessageBox(Utils.getMainShell(),
                                SWT.ICON_WARNING | SWT.OK
                                        | SWT.APPLICATION_MODAL);
                        mb.setMessage(Messages.warning_unvalid_openModel);
                        mb.open();
                    }

                    // forcage affichage onglet 0
                    Nemo.getInstance().getViewMain().selectTabByIndex(0);
                    Nemo.getInstance().getViewMain().setAffiche(0);

                    // RAZ global
                    Utils.razGlobal();

                    // reset de l'etape de conversion
                    Nemo.getInstance().getViewMain().setEtape(0);

                    // lancement du processus sur le premier onglet et
                    // effacement preventif
                    // si besoin
                    // fillDesc(true);
                    Nemo.getInstance().getViewMain().getEditFileForm()
                        .afficheEdition();

                    ViewLog.styledTextLogInfos
                        .append(Messages.success_openModel + nomModele);

                    // reinitialisation du tableau de paramètres
                    Nemo.getInstance().getViewMain().getEditDataForm()
                        .reinitTableData();
// if (Nemo.getInstance().getViewMain().getEditDataForm().getMyTable() != null)
// {
// Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().go();
// }
                    // 4063 affectation du nom de modele courant
                    Nemo.getInstance().setCurrentOpenModelPath(nomModele);
                }
                catch (Exception e)
                {
                    Utils.messageErreurAppend(Messages.error);
                    Application.batchLogger.debug(Messages.error, e);
                    e.printStackTrace();
                }
            }
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }
}