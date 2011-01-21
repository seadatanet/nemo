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
import org.eclipse.swt.widgets.Combo;

import fr.ifremer.nemo.ui.edit.data.EditDataForm;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class PressureSelectionListener implements SelectionListener
{
    // reference sur l'editeur
    private EditDataForm myEditDataForm;
    
    
    public PressureSelectionListener(EditDataForm editDataForm)
    {
        myEditDataForm = editDataForm;
    }
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {
Combo combo = (Combo) g.getSource();
System.out.println(combo.getSelectionIndex());
       System.out.println("choose"); //$NON-NLS-1$
     
    }
}
