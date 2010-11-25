/* ============================================================================
 * Nom du fichier	: inputFileBrowseListener.java
 * Auteur			: sbregent
 * Date				: 22 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Messages;
import  org.eclipse.swt.events.SelectionEvent;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class InputFileBrowseListener extends SelectionAdapter
{

    // reference sur l'editeur
    private EditFileForm myEditFileForm;
    
    public InputFileBrowseListener(   EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    
    @Override
    public void widgetSelected( SelectionEvent e)
    {

        // reinitialisation de la conversion et de l'affichage
        myEditFileForm.init();

        if (myEditFileForm.getRadioFichier().getSelection())
        {
            // si fichier campagne
            browseForCruiseFile();
        }
        else if (myEditFileForm.getRadioDossier().getSelection())
        {
            // si dossier campagne
            browseForCruiseDirectory();
        }
        else if (myEditFileForm.getRadioCollectGroupedByCruise().getSelection())
        {
            browseForCollectionGroupedByCruise();
        }
        else if (myEditFileForm.getRadioCollectNotGroupedByCruise().getSelection())
        {
            browseForCollectionNotGroupedByCruise();
        }
        
        Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
    }
     private void browseForCruiseFile()
     {
         // reactivation de l'onglet campagne
//         Nemo.getInstance().getViewMain().enableCruise(true);

         FileDialog dialog = getFileDialog();
         myEditFileForm.nomFichier = dialog.open();

         if (myEditFileForm.nomFichier != null)
         {

             myEditFileForm.setFileLoaded(true);
             myEditFileForm.nomRepertoire =  new File(myEditFileForm.nomFichier).getParent() + File.separator;
             try
             {
                 // affichage du corps du fichier
                 Utils.insertTextFich(myEditFileForm.nomFichier,
                     Nemo.getInstance().getViewMain().getEditCruiseForm().getTextAreaCruise(),
                     true);
                 myEditFileForm.getTextAreaFile().setText(Utils.texteBrut);
                 // affichage etape0 dans formulaire d'edition
                 myEditFileForm.afficheEdition();
             }
             catch (IOException e1)
             {
                 ViewLog.styledTextLogErreurs
                 .setText(Messages.error);
                 e1.printStackTrace();
             }
         }
         else
         {
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectFile);
         }
     }
     
     private void browseForCruiseDirectory()
     {
         myEditFileForm.setCruiseInADirectory(true);

         // reactivation de l'onglet campagne
//         Nemo.getInstance().getViewMain().enableCruise(true);

         DirectoryDialog dialog = getDirectoryDialog();
         myEditFileForm.nomRepertoire = dialog.open();

         if (myEditFileForm.nomRepertoire != null)
         {
             myEditFileForm.setFileLoaded(true);
             if (Utils.findFiles(myEditFileForm.nomRepertoire, true))
             {
                 // affichage du corps du fichier
                 myEditFileForm.getTextAreaFile().setText(Utils.texteBrut);
                 // affichage etape0 dans formulaire d'edition
                 myEditFileForm.afficheEdition();
             }
         }
         else
         {
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectDir);
         }
     }
     
     
     private void browseForCollectionGroupedByCruise()
     {
         myEditFileForm.setCruiseInADirectory (true);
         myEditFileForm.setCollectionGroupedByCruise(true);

         // desactivation de l'onglet campagne
//         Nemo.getInstance().getViewMain().enableCruise(false);

         DirectoryDialog dialog = getDirectoryDialog();
         myEditFileForm.nomRepertoire = dialog.open();
         
         if (myEditFileForm.nomRepertoire != null)
         {
             myEditFileForm.setFileLoaded(true);

             if (Utils.findFilesCollectCas1(myEditFileForm.nomRepertoire, false))
             {
                 // affichage du corps du fichier
                 myEditFileForm.getTextAreaFile().setText(Utils.texteBrut);

                 // affichage etape0 dans formulaire d'edition
                 myEditFileForm.afficheEdition();

                 // dans le cas d'une collection, on ne peut
                 // generer que le format ODV
                 myEditFileForm.enableCollectionMedatlas(false);
             }
         }
         else
         {
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectDir);
         } 
     }
     
     private void browseForCollectionNotGroupedByCruise()
     {
         myEditFileForm.setCollectionNotGroupedByCruise(true);

         // desactivation de l'onglet campagne
//         Nemo.getInstance().getViewMain().enableCruise(false);

         DirectoryDialog dialog = getDirectoryDialog();
         myEditFileForm.nomRepertoire = dialog.open();

         if (myEditFileForm.nomRepertoire != null)
         {
             myEditFileForm.setFileLoaded(true);

             if (Utils.findFiles(myEditFileForm.nomRepertoire, true))
             {
                 // affichage du corps du fichier
                 myEditFileForm.getTextAreaFile().setText(Utils.texteBrut);

                 // affichage etape0 dans formulaire d'edition
                 myEditFileForm.afficheEdition();

                 // dans le cas d'une collection, on ne peut
                 // generer que le format ODV
                 myEditFileForm.enableCollectionMedatlas(false);
                 myEditFileForm.enableODVMonoFichier(false); // FAE 0001134
             }
         }
         else
         {
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectDir);
         }
     }
     
     
     private FileDialog getFileDialog()
     {
         Shell shell = Utils.getMainShell();
         FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
         dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
         return dialog;
     }
     
     private DirectoryDialog getDirectoryDialog()
     {
         Shell shell = Utils.getMainShell();
         DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
         dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
         return dialog;
     }
}
