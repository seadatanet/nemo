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
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;

/**
 * Description: TODO
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

        System.out.println("below sea bed");
        Nemo.getInstance().getViewMain().getMyRulesManager().updateFromIHM(Global.E_MesureType.BSBED);
    }
}
