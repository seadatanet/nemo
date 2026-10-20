/* ============================================================================
 * Nom du fichier	: InputFileRadioSelectionListener.java
 * Auteur			: sbregent
 * Date				: 22 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Global.E_ProcessType;
/**
 * Description: gestion des boutons radio et checkbox pour la e groupe inputFile de l'onglet File
 * @author sbregent
 *
 */
public class InputFileRadioSelectionListener implements SelectionListener
{
    // reference sur l'editeur
    private final EditFileForm myEditFileForm;
    
    public InputFileRadioSelectionListener(   EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {
      
        E_ProcessType processType=E_ProcessType.CRUISE_FILE;
        
        if (g.getSource().equals(myEditFileForm.getRadioFichier()))
        {
            processType=E_ProcessType.CRUISE_FILE;
        }
        else if(g.getSource().equals(myEditFileForm.getRadioDossier()))
        {
            processType=E_ProcessType.CRUISE_DIR;
        }
        else if (g.getSource().equals(myEditFileForm.getRadioCollectNotGroupedByCruise()))
        {
            processType=E_ProcessType.COLLECTION_FILES;
        }
        else if (g.getSource().equals(myEditFileForm.getRadioCollectGroupedByCruise()))
        {
            processType=E_ProcessType.COLLECTION_DIR;
        } 
        RulesManager.getInstance().updateFromIHM(processType);
        selectTypeProcess(g.getSource());
       
    }
    protected void selectTypeProcess(Object source)
    {
       
//         les boutons radios ne sont plus dans le même conteneur :
//         il faut forcer la deselection
        if (source.equals(myEditFileForm.getRadioCollectNotGroupedByCruise())
                || source.equals(myEditFileForm.getRadioCollectGroupedByCruise()))
        {
            myEditFileForm.getRadioDossier().setSelection(false);
            myEditFileForm.getRadioFichier().setSelection(false);
            myEditFileForm.getChkMultiStations().setEnabled(true);
        }
        else
        {
            myEditFileForm.getRadioCollectNotGroupedByCruise().setSelection(false);
            myEditFileForm.getRadioCollectGroupedByCruise().setSelection(false);
            myEditFileForm.getChkMultiStations().setSelection(false);
            myEditFileForm.getChkMultiStations().setEnabled(false);
        }
    }
    
    
}
