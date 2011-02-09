package fr.ifremer.nemo.ui.edit.data;

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Combo;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.ParameterManager;
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
    ParameterManager myParameterManager;
   private String p_depthBBed_entryKey=Global.EMPTY_STRING;
   private String p_depthBSurf_entryKey=Global.EMPTY_STRING;
   private String p_pressure_entryKey=Global.EMPTY_STRING;
   private Combo comboDBSBed, comboDBBSurf, comboP ;
    public DataFormValidator()
    {
        super();
       
    }
    
    
    private void reinitVerticalParams()
    {
        p_depthBBed_entryKey=Global.EMPTY_STRING;
        p_depthBSurf_entryKey=Global.EMPTY_STRING;
        p_pressure_entryKey=Global.EMPTY_STRING;
    }
    @Override
    public void validate()
    {
       
        
        reinitVerticalParams();
        rm = Nemo.getInstance().getViewMain().getMyRulesManager();
        verticalParamGroup =Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
        comboDBSBed = verticalParamGroup.getComboDBSBed();    
        comboDBBSurf = verticalParamGroup.getComboDBSSurf();
        comboP = verticalParamGroup.getComboP();
        EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
        myParameterManager =Nemo.getInstance().getDomain().getData().getMyParameterManager();

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
                    /** data60/data65 profil parametre de reference verticale en premiere position **/
                    boolean verticProfilParamOk;
                    verticProfilParamOk = checkProfilVerticalParameterIsInFirstPosition();

                    /** data70 Si data60 ko => Medatlas impossible
                 A moins que l'on ait les parametre DEPH, PSAL et TEMP ( FAE 3228 )**/

                    if (!verticProfilParamOk 
                            && (Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToMedatlas()))
                    {
                        verticProfilParamOk = checkDepthAndSalinityAndTemperature();
                    }
                    ok = ok && verticProfilParamOk;
                }
                else if(Nemo.getInstance().getViewMain().getMyRulesManager().isTrajectory())
                {
                    /** data80/data85 traj parametre de reference verticale en premiere position **/
                    boolean verticTrajParamOk;
                    verticTrajParamOk = checkTrajectoryVerticalParameterIsInFirstPosition();
                    ok = ok && verticTrajParamOk;
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
        
        // envoi des valeurs dans le modele 
        VerticalParametersList modelList = Nemo.getInstance().getDomain().getData().getModel().getVerticalParametersList();
        modelList.setP_depthBSBed(p_depthBBed_entryKey);
        modelList.setP_depthBSSurf(p_depthBSurf_entryKey);
        modelList.setP_pressure(p_pressure_entryKey);
        Nemo.getInstance().getDomain().getData().getModel().setVerticalParametersList(modelList);
        editDataForm.initFromNewModel();
        
       
        
        Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data_valid);
        Nemo.getInstance().getViewMain().setEtape(4);
       
        Nemo.getInstance().getDomain().getData().getModel().setMesureType(Nemo.getInstance().getViewMain().getMyRulesManager().getMyMesureType());
        
        Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();

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
        ParameterManager pm = Nemo.getInstance().getDomain().getData().getMyParameterManager();

        boolean isDEPH = !p_depthBSurf_entryKey.equals(Global.EMPTY_STRING); //false;
        
        boolean isPSAL = false;
        boolean isTEMP = false;
        for (TypeData donnee : editDataForm.getMyTableData().getTypes())
        {
            if (donnee.getEntryKey().equals(pm.getP_salinityCode()))
            {
                isPSAL = true;
            }
            else if (donnee.getEntryKey().equals(pm.getP_temperatureCode()))
            {
                isTEMP = true;
            }
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
    private boolean checkProfilVerticalParameterIsInFirstPosition()
    {
        boolean checkOk = true;
        
        // recup du premier parametre
        TypeData premierType = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().get(0);
        String firstParameterCode = premierType.getCodeToUse();
        E_MesureType mesureType=Nemo.getInstance().getViewMain().getMyRulesManager().getMyMesureType();
      
        // cas below sea bed (data65)
        if(mesureType==E_MesureType.BSBED)
        {
            checkOk = firstParameterCode.equals(p_depthBBed_entryKey);
            if (!checkOk){
                Utils.messageErreurAppend(Messages.error_data_firstParam_depthBSBed);
            }
        }
        // cas below sea surface (data60)
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
    private boolean checkTrajectoryVerticalParameterIsInFirstPosition()
    {
        boolean checkOk = true;
        
        // recup du premier parametre
        TypeData premierType = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().get(0);
        String firstParameterCode = premierType.getCodeToUse();
        E_MesureType mesureType=Nemo.getInstance().getViewMain().getMyRulesManager().getMyMesureType();
      
        // cas below sea bed (data85)
        if(mesureType==E_MesureType.BSBED 
                && !p_depthBBed_entryKey.equals(Global.EMPTY_STRING))
        {
            checkOk = firstParameterCode.equals(p_depthBBed_entryKey);
            if (!checkOk){
                Utils.messageErreurAppend(Messages.error_data_firstParam_depthBSBed);
            }
        }
        // cas below sea surface (data80)
        else if(mesureType==E_MesureType.BSSURF
                && !p_depthBSurf_entryKey.equals(Global.EMPTY_STRING))
        {
            checkOk = firstParameterCode.equals(p_depthBSurf_entryKey)
            || firstParameterCode.equals(p_pressure_entryKey);
            if (!checkOk){
                Utils.messageErreurAppend(Messages.error_data_firstParam_depthBSSurf);
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
       RulesManager rm =  Nemo.getInstance().getViewMain().getMyRulesManager();

       if( rm.isDataListTypeP091())
      {
          detectVerticalParametersForP091();
      }
      else if(rm.isDataListTypeP011viaP021())
      {
          detectVerticalParametersForP011();
      }
      else if(rm.isDataListTypeP011viaP091())
      {
          detectVerticalParametersForP011viaP091();
          
      }
       
      return checkVerticalParametersWellDetected();
   }
   
   private void detectVerticalParametersForP011()
   {
      
       int index;
       // depth below sea surface
       if (rm.isP_depthBSSurf_enabled()){
           index= comboDBBSurf.getSelectionIndex();
           if (index != -1){
               p_depthBSurf_entryKey = comboDBBSurf.getItem(index);
           }
       }
       // Depth below sea bed
       if (rm.isP_depthBSBed_enabled()){
           index= comboDBSBed.getSelectionIndex();
           if (index != -1){
               p_depthBBed_entryKey = comboDBSBed.getItem(index);
           }
       }
       // pressure
       if (rm.isP_pressure_enabled()){
           index= comboP.getSelectionIndex();
           if (index != -1){
               p_pressure_entryKey = comboP.getItem(index);
           }
       }

   }
 
   private boolean checkVerticalParametersWellDetected()
   {
       
       boolean ok=true;
       if (rm.isP_depthBSBed_enabled() && p_depthBBed_entryKey.equals(Global.EMPTY_STRING))
       {
           Utils.messageErreurAppend(Messages.edit_data_verticalParameters_info_chooseDepthBSBed 
               +Global.SPACE
               + "("
               + myParameterManager.getP_depthBSBedCode()
               + ")"
               );
           ok=false;
       }
       
       
       if (
               (rm.isP_depthBSSurf_enabled()&& rm.isP_pressure_enabled() )
               &&
               (p_depthBSurf_entryKey.equals(Global.EMPTY_STRING) && p_pressure_entryKey.equals(Global.EMPTY_STRING))
               )
       {
           String message = Messages.edit_data_verticalParameters_info_chooseDepthBSurfOrPressure;
           message += Global.SPACE;
           message += "(";
               message += myParameterManager.getP_depthBSSurfCode();
           message +=  " / ";
           message +=  myParameterManager.getP_pressureCode();
           message += ")";
               Utils.messageErreurAppend(message);
           ok=false;
       }
       return ok;
   }
   private  void detectVerticalParametersForP091()
   {
        autoFillVerticalParametersForP091();
   }
   private  void detectVerticalParametersForP011viaP091()
   {
       
       // on regarde si un choix a été fait dans les combos
       // sinon et si un seul choix possible, on le fait
       // sinon et si plusieurs choix possibles, on prend celui qui est en 1er param si il correspond au type de combo?
       
       // reinit des valeurs
       reinitVerticalParams();
        
        // Depth below sea bed
        if (rm.isP_depthBSBed_enabled())
        {
            p_depthBBed_entryKey = fillComboAsP011viaP091(comboDBSBed );
        }
        // depth below sea surface
        if (rm.isP_depthBSSurf_enabled())
        {
            p_depthBSurf_entryKey = fillComboAsP011viaP091(comboDBBSurf );
        }
        // pressure
        if (rm.isP_pressure_enabled())
        {
            p_pressure_entryKey = fillComboAsP011viaP091(comboP );
        }  
   }
  private String fillComboAsP091(Combo combo)
  {
      String entryKey=Global.EMPTY_STRING;
      int waitedNbItems = 2; // la valeur vide "" + la valeur attendue pour ce param en P091
      int indexToSelect = 1; // index de la valeur à selectionner (index de la valeur vide = 0, index de la valeur attendue est 1)
      if(combo.getItemCount()== waitedNbItems)
      {
          entryKey = combo.getItem(indexToSelect);
          combo.select(indexToSelect);
      }
      return entryKey;
  }
  
  
  private String fillComboAsP011viaP091(Combo combo)
  { 
      String entryKey=Global.EMPTY_STRING;
      int waitedNbItems = 2; // la valeur vide "" + la valeur attendue pour ce param en P091
      int indexToSelect = 1; // index de la valeur à selectionner (index de la valeur vide = 0, index de la valeur attendue est 1)
      TypeData premierType = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().get(0);
      String firstParameterCode = premierType.getCodeToUse();
      
      
      // cas ideal: on n'a qu'une possibilite -> on la selectionne
      if(combo.getItemCount()== waitedNbItems)
      {
          entryKey = combo.getItem(indexToSelect);
          combo.select(indexToSelect);
      }
      // cas ambigu: on a plusieurs choix possibles -> on selectionne celui qui correspond au premier parametre du tableau
      else if(combo.getItemCount()> waitedNbItems)
      {
          String selected=combo.getItem(combo.getSelectionIndex());
          if( combo.getItem(combo.getSelectionIndex()).equals(Global.EMPTY_STRING))
          {
              for (int i=0; i<combo.getItems().length; i++)
              {
                  if (combo.getItem(i).equals(firstParameterCode)){
                      combo.select(i);
                      entryKey = combo.getItem(i);
                  }
              }
          }else{
              entryKey = selected;
          }
      }
      return entryKey;
  }
   private void autoFillVerticalParametersForP091()
   {
       // reinit des valeurs
      reinitVerticalParams();
       
       // Depth below sea bed
       if (rm.isP_depthBSBed_enabled()){
           p_depthBBed_entryKey = fillComboAsP091(comboDBSBed);
       }
       // depth below sea surface
       if (rm.isP_depthBSSurf_enabled()){
           p_depthBSurf_entryKey = fillComboAsP091(comboDBBSurf);
       }
       // pressure
       if (rm.isP_pressure_enabled()){
           p_pressure_entryKey = fillComboAsP091(comboP );
       }
   }
}
