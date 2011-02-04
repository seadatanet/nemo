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
import fr.ifremer.nemo.util.Global.E_InputType;

/**
 * Description: listener sur la selection du type de fichier
 * @author sbregent
 *
 */
public class FileTypeSelectionListener  implements SelectionListener
{
    // reference sur l'editeur
    private EditFileForm myEditFileForm;
    
    public FileTypeSelectionListener(   EditFileForm editFileForm)
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

        E_InputType currentFileType = E_InputType.PROFILE ;
        if (e.getSource().equals(myEditFileForm.getRadProfilVertical()))
        {
            currentFileType = E_InputType.PROFILE;
        }
        else if(e.getSource().equals(myEditFileForm.getRadSerieTempo()))
        {
            currentFileType =E_InputType.TIMESERIES;
        }
        else if(e.getSource().equals(myEditFileForm.getRadTrajectoire()))
        {
            currentFileType = E_InputType.TRAJECTORY;
        }

        rulesMgr.updateFromIHM(currentFileType);
    }
    
}
