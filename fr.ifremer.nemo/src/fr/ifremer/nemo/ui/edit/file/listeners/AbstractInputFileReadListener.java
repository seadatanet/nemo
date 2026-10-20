/*
 * ============================================================================
 * Nom du fichier : inputFileBrowseListener.java
 * Auteur : sbregent
 * Date : 22 nov. 10
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

/**
 * Description: abstract listener for Browse and reload button
 * 
 * @author pmahoudo
 * 
 */
public abstract class AbstractInputFileReadListener extends SelectionAdapter {

	// reference sur l'editeur
	protected final EditFileForm myEditFileForm;

	public AbstractInputFileReadListener(EditFileForm editFileForm) {
		myEditFileForm = editFileForm;
	}

	protected abstract void reinit();

	@Override
	public void widgetSelected(SelectionEvent e) {
		// boolean ti indicate if a file or directory has been choosen (false if
		// user clicked on Cancel)
		boolean fileChoosen = false;
		E_ProcessType currentProcessType = RulesManager.getInstance().getMyProcessType();

		Preformat.setPreformat(false);

		if (currentProcessType == E_ProcessType.CRUISE_FILE) {
			// si fichier campagne
			fileChoosen = openCruiseFile();
		} else if (myEditFileForm.getRadioDossier().getSelection()) {
			// si dossier campagne
			fileChoosen = openCruiseDirectory();
		} else if (myEditFileForm.getRadioCollectGroupedByCruise().getSelection()) {
			fileChoosen = openCollectionGroupedByCruise();
		} else if (myEditFileForm.getRadioCollectNotGroupedByCruise().getSelection()) {
			fileChoosen = openCollectionNotGroupedByCruise();
		}
		if (fileChoosen) {
			updateDisplay();
		} else {

			myEditFileForm.nomRepertoire = myEditFileForm.nomRepertoireOrigin;
			myEditFileForm.nomFichier = myEditFileForm.nomFichierOrigin;
		}
	}
	
	protected abstract boolean openCruiseFile();

	protected boolean readCruiseFile(boolean raz) {
		boolean filechoosen = false;
		try {

			if (myEditFileForm.nomFichier != null) {
				reinit();
				myEditFileForm.setFileLoaded(true);
				myEditFileForm.nomRepertoire = new File(myEditFileForm.nomFichier).getParent() + File.separator;

				// affichage du corps du fichier
				Utils.insertTextFich(myEditFileForm.nomFichier,
						Nemo.getInstance().getViewMain().getEditCruiseForm().getCruiseSourceViewer().getTextWidget(),
						raz);
				myEditFileForm.getFileSourceViewer().setDocument(Utils.document);
				// affichage etape0 dans formulaire d'edition
				myEditFileForm.afficheEdition();
				filechoosen = true;

			} else {
				// display error if no file is selected
				if (!myEditFileForm.isFileLoaded()) {
					ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectFile);
				}
			}
		} catch (IOException e1) {
			ViewLog.styledTextLogErreurs.setText(Messages.error);
			e1.printStackTrace();
		}
		return filechoosen;
	}
	
	protected abstract boolean openCruiseDirectory();

	protected boolean readCruiseDirectory(boolean raz) {
		boolean filechoosen = false;
		try {

			if (myEditFileForm.nomRepertoire != null) {
				reinit();
				myEditFileForm.setFileLoaded(true);
				if (Utils.findFiles(myEditFileForm.nomRepertoire, raz)) {
					// affichage du corps du fichier
					myEditFileForm.getFileSourceViewer().setDocument(Utils.document);
					// affichage etape0 dans formulaire d'edition
					myEditFileForm.afficheEdition();
					filechoosen = true;
				}
			} else {
				if (!myEditFileForm.isFileLoaded()) {
					ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
				}
			}
		} catch (Exception e) {
			ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
		}
		return filechoosen;
	}

	protected abstract boolean openCollectionGroupedByCruise();
	
	protected boolean readCollectionGroupedByCruise(boolean raz) {
		boolean filechoosen = false;
		try {

			if (myEditFileForm.nomRepertoire != null) {
				reinit();
				myEditFileForm.setFileLoaded(true);

				if (Utils.findFilesCollectCas1(myEditFileForm.nomRepertoire, raz)) {
					// affichage du corps du fichier
					myEditFileForm.getFileSourceViewer().setDocument(Utils.document);

					// affichage etape0 dans formulaire d'edition
					myEditFileForm.afficheEdition();
				}
				filechoosen = true;
			} else {
				if (!myEditFileForm.isFileLoaded()) {
					ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
				}
			}
		} catch (Exception e) {
			ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
		}
		return filechoosen;
	}

	protected abstract boolean openCollectionNotGroupedByCruise();
	
	protected boolean readCollectionNotGroupedByCruise(boolean raz) {
		boolean filechoosen = false;
		try {

			if (myEditFileForm.nomRepertoire != null) {
				reinit();
				myEditFileForm.setFileLoaded(true);

				if (Utils.findFiles(myEditFileForm.nomRepertoire, raz)) {
					// affichage du corps du fichier
					myEditFileForm.getFileSourceViewer().setDocument(Utils.document);

					// affichage etape0 dans formulaire d'edition
					myEditFileForm.afficheEdition();
				}
				filechoosen = true;
			} else {
				if (!myEditFileForm.isFileLoaded()) {
					ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
				}
			}
		} catch (Exception e) {
			ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
		}
		return filechoosen;
	}
	
	protected abstract void updateDisplay();
}
