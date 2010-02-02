package fr.ifremer.nemo.ui.actions;

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
import fr.ifremer.nemo.ui.Utils;
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

    public OpenAction(IWorkbenchWindow window){
        this.window = window;
        setId(ID);
        setText(Messages.menu_openModel);
        window.getSelectionService().addSelectionListener(this);
    }

    public void dispose(){
        window.getSelectionService().addSelectionListener(this);
    }

    @Override
    public void run(){
        // verifier que un fichier est charge avant d'ouvrir un modele
        if (!ViewMain.editFileForm.loaded){
            Utils.messageErreurAppend(Messages.error_openModel);
        }
        else{
            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
            FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
            dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory() + File.separator);
            dialog.setText(Messages.dialog_openModel);

            String extensions[] = new String[1];
            extensions[0] = Global.STAR + Global.EXT_XML;
            dialog.setFilterExtensions(extensions);

            String nomModele = dialog.open();

            if (nomModele != null){
                try{
                    Nemo.getInstance().openModel(nomModele);
                    
                    // forcage affichage onglet 0
                    ViewMain.tabFolder.setSelection(0);
                    ViewMain.affiche = 0;

                    // RAZ global
                    Utils.razGlobal();

                    // reset de l'etape de conversion
                    ViewMain.etape = 0;

                    // lancement du processus sur le premier onglet et effacement preventif
                    // si besoin
                    // fillDesc(true);
                    ViewMain.editFileForm.afficheEdition();

                    ViewLog.styledTextLogInfos.append(Messages.success_openModel + nomModele);

                    // reinitialisation du tableau de paramètres
                    if (ViewMain.tableData != null)
                        TableData.go();
                }
                catch (Exception e){
                    Utils.messageErreurAppend(Messages.error);
                    e.printStackTrace();
                }
            }
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming){
    }
}