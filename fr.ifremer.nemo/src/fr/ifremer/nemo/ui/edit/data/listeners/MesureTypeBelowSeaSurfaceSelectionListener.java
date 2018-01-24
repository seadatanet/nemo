/* ============================================================================
 * Nom du fichier	: DataListRadioSelectionListener.java
 * Auteur			: sbregent
 * Date				: 17 jav. 2010
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
public class MesureTypeBelowSeaSurfaceSelectionListener implements SelectionListener
{
    
    
    public MesureTypeBelowSeaSurfaceSelectionListener()
    {
    }
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {
    	RulesManager.getInstance().updateFromIHM(Global.E_MesureType.BSSURF);
    	Nemo.getInstance().getViewMain().getEditDataForm().invalidEtape();
    }
}
