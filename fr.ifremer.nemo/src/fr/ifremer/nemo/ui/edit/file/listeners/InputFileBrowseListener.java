/* ============================================================================
 * Nom du fichier : inputFileBrowseListener.java
 * Auteur : sbregent
 * Date : 22 nov. 10
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

/**
 * Description: listener sur le bouton browse de l'onget File (ouverture fichier entree)
 * @author sbregent
 *
 */
public class InputFileBrowseListener extends SelectionAdapter
{

	// reference sur l'editeur
    private EditFileForm myEditFileForm;

	public InputFileBrowseListener(EditFileForm editFileForm)
	{
		myEditFileForm = editFileForm;
	}

	private void reinit()
	{
		// 6414
		Nemo.getInstance().getViewMain().getEditFileForm().reinitAllButProcess();
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{

		E_ProcessType currentProcessType = Nemo.getInstance().getViewMain().getMyRulesManager().getMyProcessType();

		Preformat.setPreformat(false);

		if (currentProcessType == E_ProcessType.CRUISE_FILE)
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
		myEditFileForm.nomRepertoireOrigin = myEditFileForm.nomRepertoire;
		// reinitialisation de la conversion et de l'affichage
		Nemo.getInstance().getViewMain().getMyRulesManager().updateFromBrowse();
		Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
	}

	private void browseForCruiseFile()
	{
		try
		{
			FileDialog dialog = getFileDialog();
			myEditFileForm.nomFichier = dialog.open();

			if (myEditFileForm.nomFichier != null)
			{
				reinit();
				myEditFileForm.setFileLoaded(true);
				myEditFileForm.nomRepertoire = new File(myEditFileForm.nomFichier).getParent() + File.separator;

				// affichage du corps du fichier
                 Utils.insertTextFich(myEditFileForm.nomFichier,
                     Nemo.getInstance().getViewMain().getEditCruiseForm().getTextAreaCruise(),
                     true);
				myEditFileForm.getTextAreaFile().setText(Utils.texteBrut);
				// affichage etape0 dans formulaire d'edition
				myEditFileForm.afficheEdition();

             }
             else
			{
                 ViewLog.styledTextLogErreurs
                 .setText(Messages.error_file_selectFile);
			}
         }
         catch (IOException e1)
		{
             ViewLog.styledTextLogErreurs
             .setText(Messages.error);
			e1.printStackTrace();
		}
	}

	private void browseForCruiseDirectory()
	{
         try{
			DirectoryDialog dialog = getDirectoryDialog();
			myEditFileForm.nomRepertoire = dialog.open();

			if (myEditFileForm.nomRepertoire != null)
			{
				reinit();
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
		} catch (Exception e)
		{
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectDir);
		}
	}

	private void browseForCollectionGroupedByCruise()
	{

		try
		{
			DirectoryDialog dialog = getDirectoryDialog();
			myEditFileForm.nomRepertoire = dialog.open();

			if (myEditFileForm.nomRepertoire != null)
			{
				reinit();
				myEditFileForm.setFileLoaded(true);

				if (Utils.findFilesCollectCas1(myEditFileForm.nomRepertoire, false))
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
		} catch (Exception e)
		{
             ViewLog.styledTextLogErreurs
             .setText(Messages.error_file_selectDir);
		}
	}

	private void browseForCollectionNotGroupedByCruise()
	{
		try
		{
			DirectoryDialog dialog = getDirectoryDialog();
			myEditFileForm.nomRepertoire = dialog.open();

			if (myEditFileForm.nomRepertoire != null)
			{
				reinit();
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
		} catch (Exception e)
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
