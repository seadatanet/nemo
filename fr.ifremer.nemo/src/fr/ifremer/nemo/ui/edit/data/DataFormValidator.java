package fr.ifremer.nemo.ui.edit.data;

import java.util.regex.Pattern;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.FormValidator;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: TODO
 * @author sbregent 
 */
public class DataFormValidator extends FormValidator
{
    
    
    public DataFormValidator()
    {
        super();
    }
    @Override
    public void validate()
    {
        EditConversionForm editConversionForm = Nemo.getInstance().getViewMain().getEditConversionForm();
        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();

        Boolean ok = true;
      
        Boolean exitValid = false;
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
        if (nbreParam == 0)
        {
            ok = false;
            Utils.messageErreurAppend(Messages.Etape_117);
            exitValid=true;
        }
        // -------------------------------------------------------------------------
        
        
        /**-----------------------------------------------------------------------
         * data10
         */
        ok = updateTestValues();
        if (!ok)
        {
            Utils.messageErreurAppend(Messages.Etape_131);
        }
        // -------------------------------------------------------------------------
        
       
        if (!exitValid)
        {
            int[] debuts, fins;
            String[] debutsFlag, finsFlag;
            int cptDebut = 0;
            int cptFin = 0;

            debuts = new int[nbreParam];
            fins = new int[nbreParam];
            debutsFlag = new String[nbreParam];
            finsFlag = new String[nbreParam];

            // remplissage des listes
            for (int i = 0; i < nbreParam; i++)
            {

                TypeData typeCourant = editDataForm.getMyTableData().getTypes().get(i);

                debuts[i] = typeCourant.getDebut();
                fins[i] = typeCourant.getFin();
                debutsFlag[i] = typeCourant.getDebutFlag();
                finsFlag[i] = typeCourant.getFinFlag();

                /**-----------------------------------------------------------------------
                 * data20 verif presence de l'unité pour chaque paramètre
                 */
                // FAE 6538
                if (typeCourant.getUnite().equals(Global.EMPTY_STRING)){
                    ok = false;
                    Utils.messageErreurAppend(Messages.Etape_118);
                    continue;
                }
                //  ----------------------------------------------------------

                /**-----------------------------------------------------------------------
                 * data30 verif compatibilité valeur par defaut et valeur output
                 */

                ok = checkDefaultAndOutputCompatibility(ok, typeCourant);
                //  ----------------------------------------------------------

                /**-----------------------------------------------------------------------
                 * data40 check des valeurs debut<fin
                 */
                if (typeCourant.getDebut() > typeCourant.getFin())
                {
                    Utils.messageErreurAppend(Messages.Etape_98 + Global.SPACE
                        + typeCourant.getCodeToUse());
                    ok = false;
                }
                //  ----------------------------------------------------------
                
                /**-----------------------------------------------------------------------
                 * data45 check des valeurs debut<fin flag
                 */
                if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
                        && !typeCourant.getFinFlag()
                        .equals(Global.EMPTY_STRING))
                {
                    if (Integer.parseInt(typeCourant.getDebutFlag()) > Integer
                            .parseInt(typeCourant.getFinFlag()))
                    {
                        Utils.messageErreurAppend(Messages.Etape_125
                            + Global.SPACE + typeCourant.getCodeToUse());
                        ok = false;
                    }
                }
            }
            
            /**-----------------------------------------------------------------------
             * data50
             * verif que les valeurs de « debut » et « fin » sont différentes des valeurs affichées par défaut
             **/
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
            //  ----------------------------------------------------------
            
            
            /**-----------------------------------------------------------------------
             * data60
             * verif que les valeurs de « debut » et « fin » sont différentes des valeurs affichées par défaut
             **/
            // TEST pour savoir si PRES est bien en premier mais pas pour
            // profile ou trajectoire
            TypeData premierType = editDataForm.getMyTableData().getTypes().get(0);

            // split sur " - "
//            Pattern p = Pattern.compile(Global.SEP);
//            String[] mots = p.split(premierType.getCode());
//            String firstParameterCode = mots[0];
            String firstParameterCode = premierType.getEntryKey();
            editConversionForm.setWarningData(Global.EMPTY_STRING);

            if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile())
            {
                boolean checkPressureOk;

                checkPressureOk = checkPressureIsInFirstPosition(firstParameterCode);

                // Si PRES n'est pas premier => Medatlas impossible
                // A moins que l'on ait les parametre DEPH, PSAL et TEMP ( FAE 3228 )

                if (!checkPressureOk){
                    if ((Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType() == E_TypeConversion.MEDATLAS))
                    {

                        checkPressureOk = checkDepthAndSalinityAndTemperature();
                        if (!checkPressureOk){

                            editConversionForm
                            .setWarningData(Messages.ViewLog_7);
                        }
                    }
                    else
                    {
                        editConversionForm
                        .setWarningData(Global.EMPTY_STRING);
                    }
                }
                ok = ok && checkPressureOk;
            }
        }
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
        // envoi des valeurs dans le modele

    }
    
    
    /**
     * 
     * @return
     */
    private static boolean checkDepthAndSalinityAndTemperature()
    {

        EditDataForm editDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
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
        return isPSAL && isTEMP && isDEPH;

    }
    
    /**
     * verification: un parametre de type pression ou equivalent doit être présent sur la première ligne
     * Note: dans certains cas on calculera la pression a partir de la profondeur ou inversement. 
     * cf. DataDomain.setAddedParameter et AddedParameter
     * @param firstParameterCode
     * @return
     */
    private static boolean checkPressureIsInFirstPosition(String firstParameterCode)
    {
        boolean checkOk = true;
        DataListTypeEnum dataList = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        
        if (dataList == DataListTypeEnum.P011_VIA_P091 || dataList == DataListTypeEnum.P091)
        {
            //FAE 5819 : ajout du code DPSF(P091) ou MINCDIST(P011) comme parametre possible
            if (!(firstParameterCode.equals(Intern.paramPRES) 
                    || firstParameterCode.equals(Intern.paramDEPH)
                    || firstParameterCode.equals(Intern.paramDPSF)
            ))
            {
                // PRES ou DEPH ou DPSF n'est pas en premier
                Utils.messageErreurAppend(Messages.error_data_p091_firstPRES);
                checkOk=false;
            }
        }
        else if (dataList == DataListTypeEnum.P011_VIA_P021)
        {
            //FAE 5819 : ajout du code DPSF(P091) ou MINCDIST(P011) comme parametre possible
            if (!(firstParameterCode.equals(Intern.paramPREX) 
                    || firstParameterCode.equals(Intern.paramMPMN)
                    || firstParameterCode.equals(Intern.paramAHGT)
            ))
            {
                // PREX ou MPMN ou AHGT n'est pas en premier
                Utils.messageErreurAppend(Messages.error_data_p011_firstPRES);
                checkOk=false;
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
        return res;
    }
    
    
   private boolean checkDefaultAndOutputCompatibility(boolean ok, TypeData typeCourant )
   {
       boolean okRes = ok;
       String fmOut = new String();

       // test direct sur valeur entree
       okRes = okRes && true;

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
               okRes = okRes && true;

           }
           else
           {
               // erreur
               okRes = okRes && false;
               Utils
               .messageErreurAppend(Messages.Etape_106
                   + " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
           }

       }
       catch (Exception e)
       {
           // erreur
           okRes = okRes && false;
           Utils
           .messageErreurAppend(Messages.Etape_106
               + " " + typeCourant.getCodeToUse() + " : " + Messages.Etape_106b); //$NON-NLS-1$ //$NON-NLS-2$
           System.out.print(e.getMessage());
       }

       return okRes;
   }
}
