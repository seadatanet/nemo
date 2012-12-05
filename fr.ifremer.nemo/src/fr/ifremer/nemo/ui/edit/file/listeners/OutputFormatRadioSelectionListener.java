/* ============================================================================
 * Nom du fichier	: OutputFormatRadioSelectionListener.java
 * Auteur			: sbregent
 * Date				: 29 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.file.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: listener
 * @author sbregent
 *
 */
public class OutputFormatRadioSelectionListener implements SelectionListener
{
    // reference sur l'editeur
    private final EditFileForm myEditFileForm;
    
    
    public OutputFormatRadioSelectionListener(EditFileForm editFileForm)
    {
        myEditFileForm = editFileForm;
    }
    public void widgetDefaultSelected( SelectionEvent g)
    {
        widgetSelected(g);
    }
    
    public void widgetSelected(SelectionEvent g)
    {

        RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
        E_TypeConversion currentConv =E_TypeConversion.UNKNOWN;

        if (g.getSource().equals(myEditFileForm.getRadMedatlas()))
        {
            currentConv = E_TypeConversion.MEDATLAS;
        }
        else if(g.getSource().equals(myEditFileForm.getRadNetCDF())){
            if (myEditFileForm.getRadODVMonoFichier().getSelection())
            {
                currentConv = E_TypeConversion.NETCDF_MULTISTATIONS;
            }else if(myEditFileForm.getRadODVMonoStation().getSelection())
            {
                currentConv = E_TypeConversion.NETCDF_MONOSTATION;
            }else{
                currentConv =E_TypeConversion.UNKNOWN;
            }
        }
        else if(g.getSource().equals(myEditFileForm.getRadODV()))
        {
            if (myEditFileForm.getRadODVMonoFichier().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MULTISTATIONS;
            }else if(myEditFileForm.getRadODVMonoStation().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MONOSTATION;
            }else{
                currentConv =E_TypeConversion.UNKNOWN;
            }
        }
        else if(g.getSource().equals(myEditFileForm.getRadODVMonoFichier()))
        {
            if (myEditFileForm.getRadODV().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MULTISTATIONS;
            }
            else
            {
                currentConv = E_TypeConversion.NETCDF_MULTISTATIONS;
            }
        }
        else if(g.getSource().equals(myEditFileForm.getRadODVMonoStation()))
        {
            
            if (myEditFileForm.getRadODV().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MONOSTATION;
            }
            else
            {
                currentConv = E_TypeConversion.NETCDF_MONOSTATION;
            }
        }
        
        rulesMgr.defineSectionMandatory();
     
//        boolean confirm = MessageDialog.openConfirm(null, 
//            Messages.edit_data_dataListDialogBox_title,
//            Messages.edit_data_dataListDialogBox_text);
     
        Nemo.getInstance().getDomain().getConversion().setCurrentConversion(currentConv);
        Nemo.getInstance().getViewMain().getEditFileForm().afficheGrpODV();
        
        
        Nemo.getInstance().getDomain().getConversion().getModel().setOutputFormat(currentConv);
        Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
        rulesMgr.updateFromIHM(currentConv);
        // invalidation de l'etape quand modif
        Utils.invalidEtapes();
    }
}
