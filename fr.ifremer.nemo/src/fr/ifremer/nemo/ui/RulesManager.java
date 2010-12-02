/* ============================================================================
 * Nom du fichier	: RulesManager.java
 * Auteur			: sbregent
 * Date				: 29 nov. 10
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Description: cette classe centralise les règles NEMO et leur gestion pour l'IHM (ne concerne pas le mode batch)
 * @author sbregent
 *
 */
public class RulesManager
{

    
    /**
     * referance sur le panneau file
     */
    EditFileForm myEditFileForm;
    /**
     * description des paramètres Input
     *
     */
    private E_ProcessType myProcessType;
    private E_TypeConversion myConversionType;
    private E_InputType myFileType;
    
    /**
     * description des autorisations d'après les règles
     */
    private boolean cruiseEnabled;
    private boolean medatlasEnabled;
    private boolean odvMonostationEnabled;
    private boolean odvMultistationEnabled;
    
    /**
     * valeurs par défaut (à l'ouverture)
     */
    public static boolean DEFAULT_CRUISE_ENABLED=true;
    public static boolean DEFAULT_MEDATLAS_ENABLED=true;
    public static  boolean DEFAULT_ODVMONOSTATION_ENABLED=true;
    public static boolean DEFAULT_ODVMULTISTATION_ENABLED=true;
    
    public static E_ProcessType DEFAULT_PROCESSTYPE = E_ProcessType.CRUISE_FILE;
    public static E_TypeConversion DEFAULT_CONVERSIONTYPE = E_TypeConversion.UNKNOWN;
    public static E_InputType DEFAULT_FILETYPE =E_InputType.PROFILE;
    
    
    private String messageLog=Global.EMPTY_STRING;
    
    public RulesManager()
    {
        myEditFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
       setDefaultValues();
    }
    public void init()
    {
        setDefaultValues();
    }
    
    private void setDefaultValues()
    {
        messageLog=Global.EMPTY_STRING;
        myProcessType = DEFAULT_PROCESSTYPE;
        myFileType = DEFAULT_FILETYPE;
        cruiseEnabled = DEFAULT_CRUISE_ENABLED;
        medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
        odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
        odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
        
        // en dernier, on recupère la conversion choisie dans les settings
        // on l'applique si elle est compatible avec les autres parametres, sinon on la met à UNKNOWN
        myConversionType = getDefaultConversionTypeWithRules();
    }
    
    public void resetFileForm()
    {
        setDefaultValues();
        myEditFileForm.resetDefaultSelectionSeparator();
        myEditFileForm.resetDefaultSelectionFileType();
        myEditFileForm.resetDefaultEnableConversionTypes();
        myConversionType = getDefaultConversionTypeWithRules();
        myEditFileForm.selectConversion(myConversionType);
    }
    
    /**
     * calcule la conversion a utiliser d'après la conversion choisie dans les settings et les choix effectués
     *  pour process et fileType
     * @return la configuration des ettings si compatible avec les règles, sinon E_TypeConversion.UNKNOWN
     */
    private E_TypeConversion getDefaultConversionTypeWithRules()
    {
        E_TypeConversion defConversionType = Nemo.getInstance().getConfig().getDefaultConversionType();
        E_TypeConversion calc = E_TypeConversion.UNKNOWN;
        rule_process(false);
        rule_fileType(false);
        if (defConversionType == E_TypeConversion.MEDATLAS && !isMedatlasEnabled())
        {
            calc = E_TypeConversion.UNKNOWN;
        }else
        {
            calc = defConversionType;
        }
        return calc;
    }
    private E_TypeConversion getCurrentConversionTypeWithRules(E_TypeConversion conv)
    {
        E_TypeConversion applicableConv = conv;
        
        
        if (
                (!odvMonostationEnabled && conv==E_TypeConversion.ODV_MONOSTATION)
                ||
                (!odvMonostationEnabled && conv==E_TypeConversion.ODV_MONOSTATION)
                ||
                (!medatlasEnabled && conv==E_TypeConversion.MEDATLAS)
                )
        {
            applicableConv = E_TypeConversion.UNKNOWN;
            messageLog += Messages.edit_file_info_conversionNotApplicable+Global.RN;
        }
        
        
        return applicableConv;
    }    
    
    private void reinitRules()
    {
        messageLog = Global.EMPTY_STRING;
        cruiseEnabled = DEFAULT_CRUISE_ENABLED;
        medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
        odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
        odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
    }
    /**
     * RECEPTION MAJ PROCESSTYPE
     */
   public void updateFromIHM(E_ProcessType processType)
   {
      
       // si modif process, on fait un reset global
       resetFileForm();
       
       myProcessType = processType;
       reinitRules();
       rule_process(true);
       rule_fileType(true);
       myConversionType = getDefaultConversionTypeWithRules();
       rule_conversionType(true);
       myEditFileForm.updateEnableConversionTypes();
       myEditFileForm.selectConversion(myConversionType);
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
   }
   
   /**
    * RECEPTION MAJ FILETYPE
    */
   
   public void updateFromIHM(E_InputType fileType)
   {
       medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
       odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
       odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
       
       myFileType = fileType;
       reinitRules();
       rule_process(true);
       rule_fileType(true);
       
       myConversionType = getCurrentConversionTypeWithRules(myConversionType);
       rule_conversionType(true);

       myEditFileForm.updateEnableConversionTypes();
       myEditFileForm.selectConversion(myConversionType);
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
   }
   public void updateFromModel(E_InputType fileType)
   {
       medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
       odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
       odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
   
       myFileType = fileType;
       reinitRules();
       rule_fileType(true);
       
       myEditFileForm.selectTypeFile(myFileType);

       myConversionType = getCurrentConversionTypeWithRules(myConversionType);
       rule_conversionType(true);
       
       myEditFileForm.updateEnableConversionTypes();
       myEditFileForm.selectConversion(myConversionType);
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
   }

   /**
    * RECEPTION MAJ CONVERSION
    */
   
   public void updateFromIHM( E_TypeConversion convType)
   {
       reinitRules();
       rule_process(true);
       rule_fileType(true);
       myConversionType=convType;
       rule_conversionType(true);
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
       
   }
   public void updateFromModel( E_TypeConversion convType)
   {
       
       reinitRules();
       rule_process(true);
       rule_fileType(true);
       myConversionType=convType;
       rule_conversionType(true);
       
       myConversionType = getCurrentConversionTypeWithRules(convType);
       
       myEditFileForm.updateEnableConversionTypes();
       myEditFileForm.selectConversion(myConversionType);
       
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
       
   }
   /**
    * CLIC SUR BOUTON BROWSE
    */
   public void updateFromBrowse()
   {
       // conservation du process courant
       E_ProcessType currentProcess = myProcessType;
       // conservation du message courant (le browse peut avoir affiché un message)
       messageLog =ViewLog.styledTextLogErreurs
       .getText()+Global.RN;
       
       myProcessType = currentProcess;
       rule_process(true);
       rule_fileType(true);
       myConversionType = getDefaultConversionTypeWithRules();
       rule_conversionType(true);
       myEditFileForm.updateEnableConversionTypes();
       myEditFileForm.selectConversion(myConversionType);
       Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
       
       ViewLog.styledTextLogErreurs.setText(messageLog);
   }
   
   /******************************************
    * REGLES
    * ******************************************/
   
   private void rule_process(boolean addToLog)
   {
       // FAE 0001134: dans le cas d'une collection, on ne peut generer que le format ODV
       if (isProcessCollection())
       {
           medatlasEnabled=false;
           if(addToLog){
           messageLog += Messages.edit_file_info_medatlasForbiddenBecauseCollection+Global.RN;
           }
       }
   }
   
   private void rule_fileType(boolean addToLog)
   {
       // FAE1386 : desactiver "One File per station" si Trajectory
       if (myFileType == E_InputType.TRAJECTORY )
       {
           odvMonostationEnabled=false;
           if(addToLog){
               messageLog += Messages.edit_file_info_odvMonoForbiddenBecauseTrajectory+Global.RN;
           }
       }
   }
   private void rule_conversionType(boolean addToLog)
   {

       if ( myConversionType==E_TypeConversion.ODV_MONOSTATION ||
               myConversionType==E_TypeConversion.ODV_MULTISTATIONS)
       {
           cruiseEnabled=false;
           if(addToLog){
               messageLog += Messages.edit_file_info_cruiseDisabledBecauseODV+Global.RN;
           }
       }
   }
   /***********************************************************************************************************
   * recup proprietes (raccourcis)
   *********************************************************************************************************/
   // PROCESS
 
   public boolean isCruiseInADirectory()
   {
        return myProcessType == E_ProcessType.COLLECTION_DIR ||
        myProcessType == E_ProcessType.CRUISE_DIR ;
   }
   public boolean isProcessCollectionDir()
   {
       //CollectionGroupedByCruise
       return myProcessType == E_ProcessType.COLLECTION_DIR;
   }
   public boolean isProcessCollectionFiles()
   {
       //Collection NOT GroupedByCruise
       return myProcessType == E_ProcessType.COLLECTION_FILES;
   }
   public boolean isProcessCollection()
   {
       return (myProcessType == E_ProcessType.COLLECTION_DIR || myProcessType == E_ProcessType.COLLECTION_FILES);
   }
   // CONVERSION
   public boolean isConversionToODV()
   {
       return (myConversionType == E_TypeConversion.ODV_MONOSTATION 
               ||myConversionType == E_TypeConversion.ODV_MULTISTATIONS);
   }
 
   
   /***********************************************************************************************************
    * getters & setters
    *********************************************************************************************************/
   //PROCESS
   public E_ProcessType getMyProcessType()
   {
       return myProcessType;
   }
   public boolean isProcessCruiseFile()
   {
       return myProcessType == E_ProcessType.CRUISE_FILE;
   }
   public boolean isProcessCruiseDir()
   {
       return myProcessType == E_ProcessType.CRUISE_DIR;
   }
   // FILETYPE
   public E_InputType getMyFileType()
   {
       return myFileType;
   }
   public boolean isTrajectory()
   {
       return myFileType == E_InputType.TRAJECTORY;
   }
   public boolean isProfile()
   {
       return myFileType == E_InputType.PROFILE;
   }
   public boolean isTimeSeries()
   {
       return myFileType == E_InputType.TIMESERIES;
   }
   //CONVERSION
   public boolean isConversionToODVMonoStation()
   {
       return myConversionType == E_TypeConversion.ODV_MONOSTATION ;
   }
   public boolean isConversionToODVMultiStation()
   {
       return myConversionType == E_TypeConversion.ODV_MULTISTATIONS;
   }
   public boolean isConversionToMedatlas()
   {
       return myConversionType == E_TypeConversion.MEDATLAS;
   }
    public E_TypeConversion getMyConversionType()
    {
        return myConversionType;
    }
    public boolean isMedatlasEnabled()
    {
        return medatlasEnabled;
    }

    public boolean isOdvMonostationEnabled()
    {
        return odvMonostationEnabled;
    }

    public boolean isOdvMultistationEnabled()
    {
        return odvMultistationEnabled;
    }
   //CRUISE
    public boolean isCruiseEnabled()
    {
        return cruiseEnabled;
    }
    

  
}
