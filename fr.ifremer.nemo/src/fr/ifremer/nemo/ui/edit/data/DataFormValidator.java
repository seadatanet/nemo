package fr.ifremer.nemo.ui.edit.data;

import java.util.regex.Pattern;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_MesureType;

/**
 * Description: validation de l'onglet data
 * @author sbregent 
 */
public class DataFormValidator extends FormValidator
{
    RulesManager rm ;
    GroupVerticalParameters verticalParamGroup;
   private String p_depthBBed_entryKey=Global.EMPTY_STRING;
   private String p_depthBSurf_entryKey=Global.EMPTY_STRING;
   private String p_pressure_entryKey=Global.EMPTY_STRING;
   private String p_salinity_entryKey=Global.EMPTY_STRING;
   private String p_temperature=Global.EMPTY_STRING;
    
    public DataFormValidator()
    {
        super();
       
    }
    
    
    private void reinitVerticalParams()
    {
        p_depthBBed_entryKey=Global.EMPTY_STRING;
        p_depthBSurf_entryKey=Global.EMPTY_STRING;
        p_pressure_entryKey=Global.EMPTY_STRING;
        p_salinity_entryKey=Global.EMPTY_STRING;
        p_temperature=Global.EMPTY_STRING;
    }
    @Override
    public void validate()
    {
        reinitVerticalParams();
        rm = Nemo.getInstance().getViewMain().getMyRulesManager();
        verticalParamGroup =Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
        EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();

        Boolean ok = true;

        // retour e l'onglet message d'erreur
        ViewLog.tabFolderLog.setSelection(0);

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // mise e jour avant toute chose
        editDataForm.getMyTableData().majTypeData();


        int nbreParam = editDataForm.getMyTableData().getTypes().size();

        /**-----------------------------------------------------------------------
         * data05 presence d'au moins 1 paramètre dans le tableau
         */
        ok = checkNbParams(nbreParam);
        // -------------------------------------------------------------------------

        if (ok)
        {
            /** data10 */
            ok = ok && updateTestValues();

            int[] debuts, fins;
            debuts = new int[nbreParam];
            fins = new int[nbreParam];

            for (int i = 0; i < nbreParam; i++)
            {
                TypeData typeCourant = editDataForm.getMyTableData().getTypes().get(i);
                debuts[i] = typeCourant.getDebut();
                fins[i] = typeCourant.getFin();
                
                /** data20 verif presence de l'unité pour chaque paramètre -FAE 6538 */
                ok = ok && checkUniteIsPresent(typeCourant);

                /** data30 verif compatibilité valeur par defaut et valeur output */
                ok = ok && checkDefaultAndOutputCompatibility( typeCourant);

                /** data40 check des valeurs debut<fin */
                ok = ok && checkStartAndEnd(typeCourant);

                /** data45 check des valeurs debut<fin flag */
                ok = ok && checkStartAndEndFlags(typeCourant);
            }

            /** data50 verif que les valeurs de « debut » et « fin » sont différentes des valeurs affichées par défaut **/
            ok = ok && checkStartAndEndAreFilled(nbreParam,debuts, fins );
        
            
            //  ----------------------------------------------------------
            // PARAMETRES
           ok= ok && detectVerticalParameters();
            if (ok)
            {
                editConversionForm.setWarningData(Global.EMPTY_STRING);

                if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile() )
                {
                    /** data60/data65 parametre de reference verticale en premiere position **/
                    boolean verticParamOk;
                    verticParamOk = checkVerticalParameterIsInFirstPosition();

                    /** data70 Si data60 ko => Medatlas impossible
                 A moins que l'on ait les parametre DEPH, PSAL et TEMP ( FAE 3228 )**/

                    if (!verticParamOk && (Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToMedatlas()))
                    {
                        verticParamOk = checkDepthAndSalinityAndTemperature();
                    }
                    ok = ok && verticParamOk;
                }
            }
        }
        
        
        
        /**
         * 
         */
        if (ok)
        {
            editDataForm.setOkApresReset (true);
            onSuccess();
        }
        else
        {
            Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_error);
            Nemo.getInstance().getViewMain().setEtape(3);
        }    
    }
    
    @Override
    protected void onSuccess()
    {
        super.onSuccess();
        EditDataForm editDataForm= Nemo.getInstance().getViewMain().getEditDataForm();
        editDataForm.initFromNewModel();
        Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_valid);
        Nemo.getInstance().getViewMain().setEtape(4);
        
        
        VerticalParametersList modelList = Nemo.getInstance().getDomain().getData().getModel().getVerticalParametersList();
        modelList.setP_depthBSBed(p_depthBBed_entryKey);
        modelList.setP_depthBSSurf(p_depthBSurf_entryKey);
        modelList.setP_pressure(p_pressure_entryKey);
        Nemo.getInstance().getDomain().getData().getModel().setMesureType(Nemo.getInstance().getViewMain().getMyRulesManager().getMyMesureType());
        // envoi des valeurs dans le modele

    }
    
    private boolean checkStartAndEndAreFilled(int nbreParam, int[]debuts, int[]fins )
    {
        boolean ok=true;
        int cptDebut = 0;
        int cptFin = 0;
        Boolean valeurNonRemplie = false;
        // parcours des listes et test des valeurs
        for (int i = 0; i < nbreParam; i++)
        {
            for (int j = 0; j < nbreParam; j++)
            {
                if (debuts[i] == debuts[j])
                {
                    if (debuts[i] == 0)
                    {
                        valeurNonRemplie = true;
                    }
                    cptDebut = cptDebut + 1;
                }
                if (fins[i] == fins[j])
                {
                    cptFin = cptFin + 1;
                }
            }
        }
        if (valeurNonRemplie)
        {
            Utils.messageErreurAppend(Messages.Etape_88);
            ok = false;
        }
        
        return ok;
    }
    
    private boolean checkNbParams(int nbParams)
    {
        boolean ok= (nbParams > 0);
        if (!ok)
        {
            Utils.messageErreurAppend(Messages.Etape_117);
        }
        return ok;
    }
    
    
    private boolean checkUniteIsPresent(TypeData typeCourant)
    {
        boolean ok;
        ok = !(typeCourant.getUnite().equals(Global.EMPTY_STRING));
        if (!ok){
            Utils.messageErreurAppend(Messages.Etape_118);
        }
        return ok;
    }
    
    
    private boolean checkStartAndEnd(TypeData typeCourant)
    {
        boolean ok=true;
        if (typeCourant.getDebut() > typeCourant.getFin())
        {
            Utils.messageErreurAppend(Messages.Etape_98 + Global.SPACE
                + typeCourant.getCodeToUse());
            ok = false;
        }
        return ok;
    }
    
    private boolean checkStartAndEndFlags(TypeData typeCourant)
    {
        boolean ok=true;
        if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
                && 
                !typeCourant.getFinFlag().equals(Global.EMPTY_STRING))
        {
            if (Integer.parseInt(typeCourant.getDebutFlag()) > Integer.parseInt(typeCourant.getFinFlag()))
            {
                Utils.messageErreurAppend(Messages.Etape_125
                    + Global.SPACE + typeCourant.getCodeToUse());
                ok = false;
            }
        }
        return ok;
    }
    
    /**
     * 
     * @return
     */
    private boolean checkDepthAndSalinityAndTemperature()
    {
        boolean ok;
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
        EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        boolean isPSAL = false;
        boolean isTEMP = false;
        boolean isDEPH = false;

        for (TypeData donnee : editDataForm.getMyTableData().getTypes())
        {
            if (donnee.getEntryKey().equals(
                Intern.paramDEPH))
                isDEPH = true;
            else if (donnee.getEntryKey().equals(
                Intern.paramPSAL))
                isPSAL = true;
            else if (donnee.getEntryKey().equals(
                Intern.paramTEMP))
                isTEMP = true;
        }
        ok=isPSAL && isTEMP && isDEPH;
        if (!ok)
        {
            editConversionForm.setWarningData(Messages.ViewLog_7);
        }
        return ok;

    }
    
    /**
     * verification: un parametre de type pression ou equivalent doit être présent sur la première ligne
     * Note: dans certains cas on calculera la pression a partir de la profondeur ou inversement. 
     * cf. DataDomain.setAddedParameter et AddedParameter
     * @param firstParameterCode
     * @return
     */
    private boolean checkVerticalParameterIsInFirstPosition()
    {
        boolean checkOk = true;
        
        // recup du premier parametre
        TypeData premierType = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().get(0);
        String firstParameterCode = premierType.getCodeToUse();
        E_MesureType mesureType=Nemo.getInstance().getViewMain().getMyRulesManager().getMyMesureType();
      
        // cas below sea bed
        if(mesureType==E_MesureType.BSBED)
        {
            checkOk = firstParameterCode.equals(p_depthBBed_entryKey);
            if (!checkOk){
                Utils.messageErreurAppend(Messages.error_data_firstParam_depthBSBed);
            }
        }
        // cas below sea surface
        else if(mesureType==E_MesureType.BSSURF)
        {
            checkOk = firstParameterCode.equals(p_depthBSurf_entryKey)
            || firstParameterCode.equals(p_pressure_entryKey);
            if (!checkOk){
                Utils.messageErreurAppend(Messages.error_data_firstParam_depthBSSurfOrPres);
            }
        }

        return checkOk;
    }
    
    
    private boolean updateTestValues()
    {
        boolean res;
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
        // test de toutes les valeurs debut / fin des Data
        try
        {
            res = editDataForm.getMyTableData().majChampTest();
        }
        catch (Exception e)
        {
            res = false;
            e.printStackTrace();
        }
        
        if (!res)
        {
            Utils.messageErreurAppend(Messages.Etape_131);
        }
        
        return res;
    }
    
    
   private boolean checkDefaultAndOutputCompatibility( TypeData typeCourant )
   {
       boolean okRes = true;
       String fmOut = new String();


       // test direct sur valeur sortie (valeur par defaut)
       try
       {
           fmOut = Global.EMPTY_STRING;
           String formatString = typeCourant.getFormat();

           // compatiblité des tailles
           int index = 0;
           int indexSign = formatString.indexOf('+');
           if (indexSign == -1)
           {
               index = formatString.indexOf('%') + 1;
           }
           else
           {
               index = indexSign + 1;
           }
           int totLength =
               Integer.parseInt(formatString.substring(index,
                   index + 1));

           // Float ou Decimal ?
           String type =
               formatString.substring(formatString.length() - 1);
           if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$ //$NON-NLS-2$
               fmOut =
                   String.format(java.util.Locale.US, formatString,
                       Float.parseFloat(typeCourant.getSortie()));
           }
           else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
               fmOut =
                   String.format(java.util.Locale.US, formatString,
                       Integer.parseInt(typeCourant.getSortie()));
           }

           if ((fmOut.equals(typeCourant.getSortie()))
                   && (typeCourant.getSortie().length() == totLength))
           {
               // ok
               okRes =true;

           }
           else
           {
               // erreur
               okRes = false;
               Utils
               .messageErreurAppend(Messages.Etape_106
                   + " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
           }

       }
       catch (Exception e)
       {
           // erreur
           okRes =  false;
           Utils
           .messageErreurAppend(Messages.Etape_106
               + " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
           System.out.print(e.getMessage());
       }

       return okRes;
   }
   
   
   private boolean detectVerticalParameters()
   {
      if( Nemo.getInstance().getViewMain().getMyRulesManager().isDataListTypeP011viaP021())
      {
           detectVerticalParametersForP011();
      }
      else
      {
           detectVerticalParametersForP091();
      }
      return checkVerticalParametersWellDetected();
   }
   
   private void detectVerticalParametersForP011()
   {
       int index;
       // depth below sea surface
       if (rm.isP_depthBSSurf_enabled()){
           index= verticalParamGroup.getComboDBSSurf().getSelectionIndex();
           if (index != -1){
               p_depthBSurf_entryKey = verticalParamGroup.getComboDBSSurf().getItem(index);
           }
       }
       // Depth below sea bed
       if (rm.isP_depthBSBed_enabled()){
           index= verticalParamGroup.getComboDBSBed().getSelectionIndex();
           if (index != -1){
               p_depthBBed_entryKey = verticalParamGroup.getComboDBSBed().getItem(index);
           }
       }
       // pressure
       if (rm.isP_pressure_enabled()){
           index= verticalParamGroup.getComboP().getSelectionIndex();
           if (index != -1){
               p_pressure_entryKey = verticalParamGroup.getComboP().getItem(index);
           }
       }

   }
   
   private boolean checkVerticalParametersWellDetected()
   {
       
       boolean ok=true;
       if (rm.isP_depthBSBed_enabled() && p_depthBBed_entryKey.equals(Global.EMPTY_STRING))
       {
           Utils.messageErreurAppend("must choose depht below bed");
           //TODO BGT message
           ok=false;
       }
       
       if (
               (rm.isP_depthBSSurf_enabled()&& rm.isP_pressure_enabled() )
               &&
               (p_depthBSurf_entryKey.equals(Global.EMPTY_STRING) && p_pressure_entryKey.equals(Global.EMPTY_STRING))
               ){
           Utils.messageErreurAppend("must choose depth below sea surf or pressure");
           //TODO BGT message
           ok=false;
       }
       return ok;
   }
   private  void detectVerticalParametersForP091()
   {
        autoFillVerticalParametersForP091();
   }
   
   private void autoFillVerticalParametersForP091()
   {
       // reinit des valeurs
      reinitVerticalParams();
       
       int waitedNbItems = 2; // la valeur vide "" + la valeur attendue pour ce param en P091
       int indexToSelect = 1; // index de la valeur à selectionner (index de la valeur vide = 0, index de la valeur attendue est 1)
       
       // Depth below sea bed
       if (rm.isP_depthBSBed_enabled()){
           if(verticalParamGroup.getComboDBSBed().getItemCount()== waitedNbItems)
           {
               p_depthBBed_entryKey = verticalParamGroup.getComboDBSBed().getItem(indexToSelect);
               verticalParamGroup.getComboDBSBed().select(indexToSelect);
           }
       }
       // depth below sea surface
       if (rm.isP_depthBSSurf_enabled()){
           if(verticalParamGroup.getComboDBSSurf().getItemCount()== waitedNbItems)
           {
               p_depthBSurf_entryKey = verticalParamGroup.getComboDBSSurf().getItem(indexToSelect);
               verticalParamGroup.getComboDBSSurf().select(indexToSelect);
           }
       }
       // pressure
       if (rm.isP_pressure_enabled()){
           if(verticalParamGroup.getComboP().getItemCount()== waitedNbItems)
           {
               p_pressure_entryKey = verticalParamGroup.getComboP().getItem(indexToSelect);
               verticalParamGroup.getComboP().select(indexToSelect);
           }
       }
   }
}
