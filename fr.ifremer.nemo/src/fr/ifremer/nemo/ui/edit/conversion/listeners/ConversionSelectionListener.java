/* ============================================================================
 * Nom du fichier	: conversionSelectionListener.java
 * Auteur			: sbregent
 * Date				: 21 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.conversion.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: Listener sur le type de conversion
 * @author sbregent
 *
 */
public class ConversionSelectionListener implements SelectionListener
{

    /**
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not Implemented yet !");
    }

    /**
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e)
    {
        EditConversionForm convForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        try
        {
            // Test si la config est bien remplie
        	// INFO BGT: deja testé sur bouton browse
//            if (!convForm.checkConfig())
//            {
//                throw new Exception("CDI cancelled"); //$NON-NLS-1$
//            }
            EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
            // RaZ
            Nemo.getInstance().getDomain().getCruise().setDataSetRef(Global.EMPTY_STRING);
            Nemo.getInstance().getDomain().getMedia().initOutput();
            Nemo.getInstance().getDomain().getConversion().setNomMappingHorsPrefix(Global.EMPTY_STRING);
            Utils.messageErreur(Global.EMPTY_STRING);

            // type de la conversion
             E_TypeConversion currentConversion = RulesManager.getInstance().getMyConversionType();
            Nemo.getInstance().getDomain().getConversion().setCurrentConversion(currentConversion);
            boolean sortData = editFileForm.isSortForced();
            Nemo.getInstance().getDomain().getConversion().setSortData(sortData);


            if (editFileForm.isOkApresReset()
                    && Nemo.getInstance().getViewMain().getEditCruiseForm().isOkApresReset()
                    && Nemo.getInstance().getViewMain().getEditStationForm().isOkApresReset()
                    && Nemo.getInstance().getViewMain().getEditDataForm().isOkApresReset())
            {

                if (currentConversion == E_TypeConversion.UNKNOWN)
                {
                    Utils.messageErreur(Messages.error_convert_cancel);
                    throw new Exception("Cancelled"); //$NON-NLS-1$
                }
                //FAE 6399 correction: process != E_ProcessType.COLLECTION_DIR
                // et non == 
                if ((currentConversion != E_TypeConversion.MEDATLAS)
                    && RulesManager.getInstance().getMyProcessType() != E_ProcessType.COLLECTION_DIR)
                {
                    if (!convForm.askDataSetRef())
                    {
                        throw new Exception("Cancelled"); //$NON-NLS-1$
                    }
                }

                // On demande les infos pour faire le résumé CDI,
                // qui sera fait plus tard
                if (Nemo.getInstance().getConfig().isWriteCdi())
                {
                    CDIResumeWriter.getInstance().reset();
                    CDIResumeWriter.getInstance()
                        .choixFichierSortie();
                }

                boolean okChoixSortie=false;
                
                if (currentConversion == E_TypeConversion.ODV_MONOSTATION || currentConversion == E_TypeConversion.NETCDF_MONOSTATION)
                {
                    okChoixSortie = convForm.choixDossierSortie();
                }
                else if(currentConversion == E_TypeConversion.MEDATLAS)
                {
                    okChoixSortie = convForm.choixFichierSortie();
                }
                else if(currentConversion == E_TypeConversion.ODV_MULTISTATIONS || currentConversion == E_TypeConversion.NETCDF_MULTISTATIONS)
                {
                    if(RulesManager.getInstance().isProcessCollectionDir())
                    {
                        okChoixSortie = convForm.choixDossierSortie();
                    }else{
                        okChoixSortie = convForm.choixFichierSortie();
                    }
                }

                if (okChoixSortie)
                {
                    convForm.definePrefixDossier();

                    // affichage des résultats en fin de conversion
                    boolean ok=convForm.runSWTProgress();
                    convForm.afficheResultat(ok);
                    if (ok){
                    convForm.fillBatchCommandLine();
                    }
                }
            }
            else
            {
                Utils
                    .messageErreur(Messages.error_convert_invalideSteps);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Utils.messageErreur(Messages.error_convert_cancel);
        }
    }

}
