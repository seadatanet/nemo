package fr.ifremer.nemo.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.mapping.Mapping;
import fr.ifremer.nemo.mapping.MappingTable;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.SurveyWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.output.SurveyWriterFactory;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class SWTProcess implements IRunnableWithProgress {

	public static ProgressNotification notifier;

	public static Survey survey;

    private SurveyWriter writer;
    
    private Conversion formatConversion;
    private boolean monoStation;

	public static IProgressMonitor monitor2;
    
    private Mapping mapping;
    private MappingTable MappingTable = new MappingTable();    
    
    
    public SWTProcess(Conversion a_formatConversion,
                      boolean a_monoStation){
        super();
        formatConversion = a_formatConversion;
        monoStation = a_monoStation;
    }
    

	@SuppressWarnings("unused")
    public void run(IProgressMonitor monitor) 
                    throws InvocationTargetException, InterruptedException 
    {
        
	    try{
            System.out.println("START at " + new Date());
            
            monitor.beginTask(Messages.SWTProcess_1, IProgressMonitor.UNKNOWN);
            monitor2 = monitor;
            
    		if (ViewFile.isCollection()) {
                runMultiProcess();
    		} 
            else {
                runSingleProcess(Global.EMPTY_STRING, null, null);
    		}
            
            // Ecriture du resume CDI
            if (Configuration.getSingleton().isWriteCdi()){
                monitor2.setTaskName("write CDI resume");
                CDIResumeWriter.getInstance().writeResume(formatConversion);
            }

            System.out.println("END   at " + new Date() + "\n---------------");
            monitor.done();
        }
        catch (Exception e) {
            throw new InterruptedException(Messages.SWTProcess_5 + e.getMessage());
        }
	}
    
    private void runMultiProcess() throws Exception{

        if (monitor2.isCanceled())
            throw new InterruptedException(Global.EMPTY_STRING); 
        
        int numItem = 0;
        String[] items = new File(ViewFile.nomRepertoire).list();

        for (String item : items) {
            numItem++;
            String msg = " : " + item + " ( " + numItem + " / " + items.length + " ) "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            String multiStationFileName = null;
            String surveyName = Intern.unknown;
            

            if (ViewFile.collectionCas1){
                DataModel.dirSurvey.setDirectory(ViewFile.nomRepertoire + "/" + item); //$NON-NLS-1$
                multiStationFileName = EditConversion.nomDossierSortie + "/" + item + Global.EXT_TXT; //$NON-NLS-1$
                surveyName = item;
            }
            else{
                DataModel.singleSurvey.setFile(ViewFile.nomRepertoire + "/" + item); //$NON-NLS-1$
                multiStationFileName = EditConversion.nomDossierSortie + "\\" + item.substring(0, item.lastIndexOf('.')) + Global.EXT_TXT;                 //$NON-NLS-1$
            }

            runSingleProcess(msg, multiStationFileName, surveyName);       
        }
    }
    
    private void runSingleProcess(String msg,
                                  String multiStationFileName,
                                  String surveyName) throws Exception{

        // NOTIFIER
        notifier();            
        
        
        // CONVERSION EN OBJET
        if (!monitor2.isCanceled()){
            System.out.println("START convertion at " + new Date());
            executeConvert(msg, surveyName); 
            System.out.println("END convertion at " + new Date());               
        }

        // ECRITURE DES FICHIERS
        if (!monitor2.isCanceled()){
            System.out.println("START writing at " + new Date());
            monitor2.setTaskName(Messages.SWTProcess_4);
            writer = SurveyWriterFactory.createWriter(formatConversion);
            
            if ((formatConversion == Conversion.ODV) && (monoStation)){
                writeMonoStationFile();
            }else{
                String fileName = EditConversion.nomFichierSortie;
                if (multiStationFileName != null){
                    fileName = multiStationFileName;
                }
                writeMultisationFile(fileName, msg);
            }
            System.out.println("END writing at " + new Date());
        }
        
        if ((!monitor2.isCanceled()) 
                && (Configuration.getSingleton().isWriteCdi())){
            CDIResumeWriter.getInstance().addSurvey(survey);
        }
        
        if (monitor2.isCanceled())
            throw new InterruptedException(Global.EMPTY_STRING); 
    }
    
    private void notifier(){
        notifier = new ProgressNotification() {
            public void progress(final int val) {
                try {
                    Application.display.syncExec(new Runnable() {
                        public void run() {
                            monitor2.subTask(Messages.SWTProcess_2 + val + Global.POURCENT);
                        }
                    });
                } catch (Exception e) {
                }
            }
        };
    }
    
    private void executeConvert(String msgComplement, 
                                String surveyName) 
                        throws Exception{
        notifier.progress(0);
        monitor2.setTaskName(Messages.SWTProcess_3 + msgComplement);
        
        // TODO optimiser la conversion
        survey = DataModel.inputModel.convert(monitor2, notifier);
        
        if (surveyName != null)
            survey.setName(surveyName);
        
        if (!ViewFile.collectionCas1){
           DataModel.inputModel.setConverted(true);    
        }
    }
    
    private void writeMonoStationFile() throws GenerationException
    {
        int i = 0;
        int totalStations = survey.getStations().length;

        for (Station station : survey.getStations()) {
            if (!monitor2.isCanceled()){
                i++;
                monitor2.setTaskName(Messages.SWTProcess_4 + " : " + i + " / " + totalStations);  //$NON-NLS-1$//$NON-NLS-2$
                String localCdiId = station.getLocalCdiId(survey.getReference(), formatConversion.toString());
                
                
                EditConversion.nomFichierSortie = EditConversion.nomDossierSortie
                + "\\" + localCdiId + Global.EXT_TXT; //$NON-NLS-1$
                File fichierSortie = new java.io.File(
                    EditConversion.nomFichierSortie);
                
                writer.writeSurvey(survey, fichierSortie, formatConversion, notifier);
                station.setDataSize(fichierSortie.length() / 1024f / 1024f);
                
                if (Configuration.getSingleton().isWriteMapping()){
                    mapping = new Mapping(localCdiId, formatConversion.toString()); 
                    
                    //Si le nom hors prefix est vide, il ne faut pas ajouter le séparateur
                    if(EditConversion.nomHorsPrefix.equals(Global.EMPTY_STRING))
                        mapping.setFilename(EditConversion.nomHorsPrefix+fichierSortie.getName());
                    else
                        mapping.setFilename(EditConversion.nomHorsPrefix+File.separator+fichierSortie.getName());
                    
                    mapping.setDate(new Date());
                    mapping.setModus(Mapping.MODUS_MONOSTATION);
                    MappingTable.persist(mapping);
                }
            }
        }
    }
    
    private void writeMultisationFile(String outputFileName, 
                                      String msg) 
                                                    throws GenerationException
    {
        File fichierSortie = new java.io.File(outputFileName);
        
        if (msg != null){
            monitor2.setTaskName(Messages.SWTProcess_4 + msg);
        }

        writer.writeSurvey(survey, fichierSortie, formatConversion, notifier);
        survey.setDataSize(fichierSortie.length() / 1024f / 1024f);                    
    
        if (Configuration.getSingleton().isWriteMapping()){
            for (Station station : survey.getStations()) {
                String localCdiId = station.getLocalCdiId(survey.getReference(), formatConversion.toString());
                mapping = new Mapping(localCdiId, formatConversion.toString()); 
                mapping.setFilename(EditConversion.nomHorsPrefix);
                mapping.setDate(new Date());
                mapping.setModus(Mapping.MODUS_MULTISTATIONS);
                
                MappingTable.persist(mapping);
            }
        }
    }
    
    public Conversion getFormatConversion(){
        return formatConversion;
    }
}
