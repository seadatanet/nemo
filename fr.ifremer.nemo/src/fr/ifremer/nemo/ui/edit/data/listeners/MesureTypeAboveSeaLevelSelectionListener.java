/* ============================================================================
 * Nom du fichier	: DataListRadioSelectionListener.java
 * Auteur			: dcapitaine
 * Date				: 11 fev. 2013
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.data.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Global;

/**
 * Description: listener
 * @author sbregent
 *
 */
public class MesureTypeAboveSeaLevelSelectionListener implements SelectionListener {

	public MesureTypeAboveSeaLevelSelectionListener() {
	}

	public void widgetDefaultSelected(SelectionEvent g) {
		widgetSelected(g);
		
	}

	public void widgetSelected(SelectionEvent g) {
		RulesManager.getInstance().updateFromIHM(Global.E_MesureType.ASLVL);
		Nemo.getInstance().getViewMain().getEditDataForm().invalidEtape();
	}
}