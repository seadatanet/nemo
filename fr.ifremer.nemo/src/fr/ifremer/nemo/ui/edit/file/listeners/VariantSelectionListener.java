package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Global.E_Variant;

/**
 * Description: listener sur la selection du type de variante ODV
 * @author sbregent
 *
 */
public class VariantSelectionListener  implements SelectionListener
{
    // reference sur l'editeur
    private final EditFileForm myEditFileForm;
    
    public VariantSelectionListener(   EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected( SelectionEvent e)
    {

    	E_Variant currentVariant = E_Variant.PHYSICOCHEMICAL;
        if (e.getSource().equals(myEditFileForm.getRadPhysicochemical()))
        {
            currentVariant = E_Variant.PHYSICOCHEMICAL;
        }
        else if(e.getSource().equals(myEditFileForm.getRadBiological()))
        {
            currentVariant =E_Variant.BIOLOGICAL;
        }
        else if(e.getSource().equals(myEditFileForm.getRadFlowcytometry()))
        {
            currentVariant = E_Variant.FLOWCYTO;
        } else if(e.getSource().equals(myEditFileForm.getRadMicrolitter()))
        {
            currentVariant = E_Variant.MICROLITTER;
        }

        RulesManager.getInstance().updateFromIHM(currentVariant);
        RulesManager.getInstance().defineSectionMandatory();
    }
    
}
