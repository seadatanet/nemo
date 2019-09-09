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
    	// indicates if conversion was changed (med/odv/cf, or only mono/multi)
//    	boolean isConversionChanged=false;
        E_TypeConversion currentConv =E_TypeConversion.UNKNOWN;

        if (g.getSource().equals(myEditFileForm.getRadMedatlas()))
        {
            currentConv = E_TypeConversion.MEDATLAS;
//            isConversionChanged=true;
        }
        else if(g.getSource().equals(myEditFileForm.getRadNetCDF())){
//        	 isConversionChanged=true;
            if (myEditFileForm.getRadODVMonoFichier().getSelection())
            {
                currentConv = E_TypeConversion.NETCDF_MULTISTATIONS;
            }else if(myEditFileForm.getRadODVMonoStation().getSelection())
            {
                currentConv = E_TypeConversion.NETCDF_MONOSTATION;
            }else{
            	myEditFileForm.getRadODVMonoFichier().setSelection(true);
                currentConv =E_TypeConversion.NETCDF_MULTISTATIONS;
            }
        }
        else if(g.getSource().equals(myEditFileForm.getRadODV()))
        {
//        	 isConversionChanged=true;
            if (myEditFileForm.getRadODVMonoFichier().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MULTISTATIONS;
            }else if(myEditFileForm.getRadODVMonoStation().getSelection())
            {
                currentConv = E_TypeConversion.ODV_MONOSTATION;
            }else{
            	myEditFileForm.getRadODVMonoFichier().setSelection(true);
                currentConv =E_TypeConversion.ODV_MULTISTATIONS;
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
        
        
        Nemo.getInstance().getDomain().getConversion().setCurrentConversion(currentConv);
        Nemo.getInstance().getViewMain().getEditFileForm().afficheGrpODV();
        
        if ( currentConv == E_TypeConversion.UNKNOWN) {
            Utils.invalidEtapes();
        	return; // no need to update all if still unknown (user has to choose between mono and multi)
        }
        Nemo.getInstance().getDomain().getConversion().getModel().setOutputFormat(currentConv);
        Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();
        RulesManager.getInstance().updateFromIHM(currentConv);
        // invalidation de l'etape quand modif
        Utils.invalidEtapes();
    }
}
