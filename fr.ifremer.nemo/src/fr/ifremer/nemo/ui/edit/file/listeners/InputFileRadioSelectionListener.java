/* ============================================================================
 * Nom du fichier	: InputFileRadioSelectionListener.java
 * Auteur			: sbregent
 * Date				: 22 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;
import org.eclipse.swt.events.SelectionListener;
import  org.eclipse.swt.events.SelectionEvent;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
/**
 * Description: gestion des boutons radio et checkbox pour la e groupe inputFile de l'onglet File
 * @author sbregent
 *
 */
public class InputFileRadioSelectionListener implements SelectionListener
{
    // reference sur l'editeur
    private EditFileForm myEditFileForm;
    
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
        selectTypeProcess(g);
        myEditFileForm.init();
        
        if (g.getSource().equals(myEditFileForm.getRadioCollectNotGroupedByCruise())
                || g.getSource().equals(myEditFileForm.getRadioCollectGroupedByCruise()))
        {
            Nemo.getInstance().getViewMain().enableCruise(false);
        }
        else{
            Nemo.getInstance().getViewMain().enableCruise(true);
        }
      
    }
    protected void selectTypeProcess(SelectionEvent g)
    {
       
//         les boutons radios ne sont plus dans le même conteneur :
//         il faut forcer la deselection
        if (g.getSource().equals(myEditFileForm.getRadioCollectNotGroupedByCruise())
                || g.getSource().equals(myEditFileForm.getRadioCollectGroupedByCruise()))
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
