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

    public static String nomFichierSortieXML="";

    public static File fichierSortieXML;

    public SaveAction(IWorkbenchWindow window)
    {
        this.window = window;
        setId(ID);
        setText(Messages.SaveAction_1);
        window.getSelectionService().addSelectionListener(this);

    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    @Override
    public void run()
    {
        // sauvegarde du modele
        FileOutputStream fileOutputStream = null;

        if (!ViewMain.editDataForm.validee)
        {
            Utils.messageErreur(Messages.SaveAction_3);
        }
        else
        {
            if (ViewMain.editFileForm.okApresReset
                    && ViewMain.editCruiseForm.okApresReset
                    && ViewMain.editStationForm.okApresReset
                    && ViewMain.editDataForm.okApresReset)
            {
                choixFichierSortie();
                try
                {
                    fileOutputStream = new FileOutputStream(fichierSortieXML);
                }
                catch (FileNotFoundException e)
                {
                    //
                    e.printStackTrace();
                }
                Nemo.getInstance().saveModel(fileOutputStream);

                Nemo.getInstance().setNomModele(fichierSortieXML.getName());

                Nemo.refreshNomNemo();

                Utils.messageOK(Messages.SaveAction_4);
            }
            else
            {
                Utils.messageErreur(Messages.SaveAction_3);
            }
        }
    }

    public static void choixFichierSortie()
    {
       //4063 gestion de la sauvegarde du modèle
        String currentModelPath="";
        File currentModelFile;
        String currentModelFileName;

        String filterPath = Nemo.getInstance().getConfig().getModelsDirectory()+ File.separator;
        String extensions[] = new String[1];
        extensions[0] = Global.STAR + Global.EXT_XML;
        currentModelFileName =  extensions[0] ;

        
        // verification du nom et chemin et modele courant 
        //si le modele est un fichier qui a été ouvert, ou si il a déjà été enregistré, on conserve ces infos pour proposer d'enregistrer sous le nom courant)
        // sinon on utilise les valeurs par défaut (repertoire des modeles de la config et nom de fichier = *.xml)
        // nom connu car deja enregistre recemment
        if (!nomFichierSortieXML.equals("")){
            currentModelPath=nomFichierSortieXML;
        }
        // nom connu car le modele est un fichier existant qui a été ouvert
        else if(
                (Nemo.getInstance().getCurrentOpenModelPath()!= null)   
                &&
                (! Nemo.getInstance().getCurrentOpenModelPath().equals(""))
        ){
            currentModelPath=Nemo.getInstance().getCurrentOpenModelPath();
        }
        if (!currentModelPath.equals(""))
        {
            currentModelFile = new File(currentModelPath);
            currentModelFileName = currentModelFile.getName();
            int indexEndPath = currentModelPath.indexOf(currentModelFileName);
            filterPath = currentModelPath.substring(0,indexEndPath);

        }



        Shell shell = Utils.getMainShell();

        FileDialog dialog =
            new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.SaveAction_5);
        dialog.setFilterPath(filterPath);

        dialog.setFilterExtensions(extensions);
        dialog.setFileName(currentModelFileName );


        nomFichierSortieXML = dialog.open();

        if (!nomFichierSortieXML.endsWith(Global.EXT_XML))
        {
            nomFichierSortieXML+=Global.EXT_XML;
        }
           
        if (nomFichierSortieXML != null)
        {
            fichierSortieXML = new File(nomFichierSortieXML);
            if (fichierSortieXML.exists())
            {
                Shell shell2 = Utils.getMainShell();

                MessageBox mb =
                    new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES | SWT.NO
                            | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.SaveAction_6);

                int reponse = mb.open();
                if (reponse == SWT.NO)
                {
                    choixFichierSortie();
                }
            }

            Nemo.getInstance().setCurrentOpenModelPath(nomFichierSortieXML);
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }
}