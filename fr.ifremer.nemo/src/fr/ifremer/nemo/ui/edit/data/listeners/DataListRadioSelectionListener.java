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
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;

/**
 * Description: listener sur les boutons de selection du type de liste (P091, P011viaP091 ou P011viaP021)
 * @author sbregent
 *
 */
public class DataListRadioSelectionListener implements SelectionListener
{
    // reference sur l'editeur
    private EditDataForm myEditDataForm;
    
    
    public DataListRadioSelectionListener(EditDataForm editDataForm)
    {
        myEditDataForm = editDataForm;
    }
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {

        DataListTypeEnum currentDataList =DataListTypeEnum.UNKNOWN;

        if (g.getSource().equals(myEditDataForm.getButton_P091()))
        {
            currentDataList = DataListTypeEnum.P091;
        }
        else if(g.getSource().equals(myEditDataForm.getButton_P011viaP091()))
        {
            currentDataList = DataListTypeEnum.P011_VIA_P091;
        }
        else if(g.getSource().equals(myEditDataForm.getButton_P011viaP021()))
        {
            currentDataList = DataListTypeEnum.P011_VIA_P021;
        }
        
        RulesManager.getInstance().updateFromIHM(currentDataList);
     
    }
}
