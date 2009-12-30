package fr.ifremer.nemo.ui.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class SaveAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{
    private final IWorkbenchWindow window;

    public final static String ID = "fr.ifremer.nemo.ui.SaveAction"; //$NON-NLS-1$

    // vars necessaires e la fenetre d'options

    public static Shell shelloptions;

    public static String nomFichierSortieXML;

    public static File fichierSortieXML;

    public SaveAction(IWorkbenchWindow window){
        this.window = window;
        setId(ID);
        setText(Messages.SaveAction_1);
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose(){
        window.getSelectionService().addSelectionListener(this);

    }

    @Override
    public void run(){
        // sauvegarde du modele
        FileOutputStream fileOutputStream = null;

        if (!ViewMain.editDataForm.validee){
            Utils.messageErreur(Messages.SaveAction_3);
        }
        else{
            if (ViewMain.editFileForm.okApresReset
                    && ViewMain.editCruiseForm.okApresReset
                    && ViewMain.editStationForm.okApresReset
                    && ViewMain.editDataForm.okApresReset){
                choixFichierSortie();
                try{
                    fileOutputStream = new FileOutputStream(fichierSortieXML);
                }
                catch (FileNotFoundException e){
                    //
                    e.printStackTrace();
                }
                Nemo.getInstance().saveModel(fileOutputStream);
                Utils.messageOK(Messages.SaveAction_4);
            }
            else{
                Utils.messageErreur(Messages.SaveAction_3);
            }
        }
    }

    public static void choixFichierSortie(){
        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        
        FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.SaveAction_5);
        dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory() + File.separator);
        String extensions[] = new String[1];
        extensions[0] = Global.STAR + Global.EXT_XML;
        dialog.setFilterExtensions(extensions);

        nomFichierSortieXML = dialog.open();

        if (nomFichierSortieXML != null){
            fichierSortieXML = new File(nomFichierSortieXML);
            if (fichierSortieXML.exists()){
                Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

                MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.SaveAction_6);

                int reponse = mb.open();
                if (reponse == SWT.NO){
                    choixFichierSortie();
                }
            }
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming){
    }
}