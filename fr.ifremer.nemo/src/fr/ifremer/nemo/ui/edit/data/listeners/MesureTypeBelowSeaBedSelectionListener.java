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

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Global;

/**
 * Description: listener
 * @author sbregent
 *
 */
public class MesureTypeBelowSeaBedSelectionListener implements SelectionListener
{
    
    public MesureTypeBelowSeaBedSelectionListener()
    {
    }
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {
    	RulesManager.getInstance().updateFromIHM(Global.E_MesureType.BSBED);
    }
}
