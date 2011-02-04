/* ============================================================================
 * Nom du fichier	: FileTypeSelectionListener.java
 * Auteur			: sbregent
 * Date				: 30 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;

/**
 * Description: listener sur case a cocher multistation onglet File
 * @author sbregent
 *
 */
public class InputFileCheckMultistationsListener  implements SelectionListener
{
    // reference sur l'editeur
    private EditFileForm myEditFileForm;
    
    public InputFileCheckMultistationsListener(   EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected( SelectionEvent e)
    {
        RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
        if (myEditFileForm.isMultiStationFileChecked())
        {
            myEditFileForm.getSectionFileHeader().setEnabled(true);
        }else{
            myEditFileForm.getSectionFileHeader().setEnabled(false);
        }
        //FAE 4181 (sinon la fleche ne se dégrise pas...)
        myEditFileForm.getSectionFileHeader().getSection().layout();
    }
    
}
