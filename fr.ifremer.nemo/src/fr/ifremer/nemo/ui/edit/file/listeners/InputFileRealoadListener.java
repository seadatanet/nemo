/*
 * ============================================================================
 * Nom du fichier : inputFileBrowseListener.java
 * Auteur : sbregent
 * Date : 22 nov. 10
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;

/**
 * Description: listener sur le bouton reload de l'onget File (reload fichier
 * entree)
 * 
 * @author pmahoudo
 * 
 */
public class InputFileRealoadListener extends AbstractInputFileReadListener {
	
	
	public InputFileRealoadListener(EditFileForm editFileForm) {
		super(editFileForm);
	}

	@Override
	protected void reinit() {
//		// reinit
		Nemo.getInstance().getViewMain().setAffiche(0);

		// reset de l'etape de conversion
		Nemo.getInstance().getViewMain().setEtape(0);
		// lancement du processus sur le premier onglet et
		// effacement preventif
		// si besoin
		// fillDesc(true);
		Nemo.getInstance().getViewMain().getEditFileForm().afficheEdition();
		
		// will redraw the input data array
		Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
		// reinitialisation du tableau de paramètres
		Nemo.getInstance().getViewMain().getEditDataForm().reinitTableData();

		Nemo.getInstance().getViewMain().getEditDataForm().reinitParameters();
		ViewLog.styledTextLogErreurs.setText(Global.EMPTY_STRING);
	}

	@Override
	protected boolean openCruiseFile() {
		return readCruiseFile(false);
	}
	
	@Override
	protected boolean openCruiseDirectory() {
		return readCruiseDirectory(false);
	}

	@Override
	protected boolean openCollectionGroupedByCruise() {
		return readCollectionGroupedByCruise(false);
	}

	
	@Override
	protected boolean openCollectionNotGroupedByCruise() {
		return readCollectionNotGroupedByCruise(false);
	}

	
@Override
protected void updateDisplay() {
	myEditFileForm.nomRepertoireOrigin = myEditFileForm.nomRepertoire;
	myEditFileForm.nomFichierOrigin = myEditFileForm.nomFichier;
	Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
}

}
