package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.sismer_tools.resources.ODVConstants.E_Variant;

/**
 * Description: listener sur la selection du type de variante (pour fichiers d'entree en ODV)
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
    
    @Override
	public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    @Override
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
        // invalidation de l'etape quand modif
        Utils.invalidEtapes();
    }
    
}
