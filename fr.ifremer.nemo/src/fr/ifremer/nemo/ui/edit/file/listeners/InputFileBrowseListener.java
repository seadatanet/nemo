/*
 * ============================================================================
 * Nom du fichier : inputFileBrowseListener.java
 * Auteur : sbregent
 * Date : 22 nov. 10
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;

/**
 * Description: listener sur le bouton browse de l'onget File (ouverture fichier
 * entree)
 * 
 * @author sbregent
 * 
 */
public class InputFileBrowseListener extends AbstractInputFileReadListener {


	public InputFileBrowseListener(EditFileForm editFileForm) {
		super(editFileForm);
	}

	@Override
	protected void reinit() {
		// 6414
		Nemo.getInstance().getViewMain().getEditFileForm().reinitAllButProcess();
		// 25574
		Nemo.getInstance().resetModel();
	}

	@Override
	protected boolean openCruiseFile() {
		FileDialog dialog = getFileDialog();
		myEditFileForm.nomFichier = dialog.open();
		
		return readCruiseFile(true);
	}
	
	@Override
	protected boolean openCruiseDirectory() {
		DirectoryDialog dialog = getDirectoryDialog();
		myEditFileForm.nomRepertoire = dialog.open();
		
		return readCruiseDirectory(true);
	}

	@Override
	protected boolean openCollectionGroupedByCruise() {
		DirectoryDialog dialog = getDirectoryDialog();
		myEditFileForm.nomRepertoire = dialog.open();
		
		return readCollectionGroupedByCruise(true);
	}

	
	@Override
	protected boolean openCollectionNotGroupedByCruise() {
		DirectoryDialog dialog = getDirectoryDialog();
		myEditFileForm.nomRepertoire = dialog.open();
		
		return readCollectionNotGroupedByCruise(true);
	}


	private FileDialog getFileDialog() {
		Shell shell = Utils.getMainShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
		return dialog;
	}

	private DirectoryDialog getDirectoryDialog() {
		Shell shell = Utils.getMainShell();
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
		return dialog;
	}
	
	
	@Override
	protected void updateDisplay() {
		myEditFileForm.nomRepertoireOrigin = myEditFileForm.nomRepertoire;
		myEditFileForm.nomFichierOrigin = myEditFileForm.nomFichier;
		// reinitialisation de la conversion et de l'affichage
		RulesManager.getInstance().updateFromBrowse();
		Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
	}
}
