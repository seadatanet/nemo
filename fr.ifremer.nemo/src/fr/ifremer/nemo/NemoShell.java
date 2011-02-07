/* ============================================================================
 * Nom du fichier	: NemoShell.java
 * Application      : NEMO version 1.2.20
 * Auteur			: dboulang
 * Date	de création : 29 déc. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;

import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.mapping.Mapping;
import fr.ifremer.nemo.mapping.MappingTable;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.output.NemoWriterFactory;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Preformat;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Class NemoShell.java Description: Main class to launch nemo in batch mode.
 * 
 * @author ycaudal
 */
public class NemoShell
{

    /**
     * @param args
     */
    public static boolean main(String inputFile, String outputFile,
            String modelFile, Conversion conv, boolean sortDatas,
            E_TypeConversion odvConv, String datasetRef, String cdiSummaryFile,
            boolean couplingTableUpdate, String couplingTablePrefix,
            Logger logger)
    {

        try
        {

            Nemo nemo = Nemo.getInstance();

            if (!new File(inputFile).exists())
            {
                logger.error("Input file not found");
                return false;
            }
            if (!new File(modelFile).exists())
            {
                logger.error("Model file not found");
                return false;
            }
            
            boolean sort = sortDatas;

            nemo.openModel(modelFile);
            InputDomain inputDomain = Nemo.getInstance().getDomain();
            if (inputDomain.getMedia().getModel().getSeparator() != Global.EMPTY_STRING)
                inputFile =
                    Preformat.runBatch(new File(inputFile)).getAbsolutePath();

            nemo.getDomain().getMedia().getModel().setSourcePath(inputFile);
 
            
            
            E_TypeConversion convertModel = inputDomain.getConversion().getModel().getOutputFormat();
            E_TypeConversion convertArgument = odvConv;
            

            // les deux sont égaux et différents de UNKNOWN
            if (convertModel == convertArgument)
            {
                if (convertModel != E_TypeConversion.UNKNOWN)
                {
                    nemo.getDomain().getConversion().setCurrentConversion(convertArgument);
                }
                else
                {
                    //ERREUR les deux sont UNKNOWN
                    logger.error("Error, Conversion format must be set");
                    return false;
                }
            }
            // Argument UNKNOWN Modele NON UNKNOWN
            else if (convertArgument == E_TypeConversion.UNKNOWN)
            {
                switch (convertModel)
                {
                    case ODV_MONOSTATION :
                    case ODV_MULTISTATIONS :
                        conv = Conversion.ODV;
                        break;
                    case MEDATLAS :
                        conv = Conversion.MEDATLAS;
                        break;                               
                    default :
                        return false;                                
                }
                
                inputDomain.getConversion().setCurrentConversion(convertModel);                    
            }
            // Modele UNKNOWN Argument NON UNKNOWN
            else if (convertModel == E_TypeConversion.UNKNOWN)
            {
                inputDomain.getConversion().setCurrentConversion(convertArgument);                        
            }
            else
            {
                //ERREUR argument et modele sont différents !!
                logger.error("Error, Conversion format incompatible with the model");
                return false;                
            } 
            
            inputDomain.getConversion().setCurrentConversion(odvConv);

            inputDomain.getMedia().buildInputFilesNames();

            inputDomain.getMedia().setOutputFileName(outputFile);

            inputDomain.getConversion().setSortData(sort);

            // The default dataset ref
            String strDefault = datasetRef;

            if (strDefault.equals(Global.EMPTY_STRING))
                strDefault =
                    inputDomain.getCruise().getModel().getReference();

            if (strDefault.equals(Global.EMPTY_STRING))
                strDefault = inputDomain.getCruise().getModel().getName();

            if (odvConv != E_TypeConversion.MEDATLAS)
                inputDomain.getCruise().setDataSetRef(strDefault);

            // Is a station property in file
            int nbLignes = inputDomain.getMedia().getModel().getFileHeader() + Nemo.getInstance().getDomain().getMedia().getShellStationHeaderNumberLines();
            
            for (PropertyModel pModel : inputDomain.getStation().getModel().getProperties())
            {
                if (pModel.getFragment() != null)
                {
                    if (pModel.getFragment().getLastLine() > nbLignes)
                    {
                        nemo.getDomain().getStation()
                            .setSomeInData(true);
                        pModel.setInData(true);
                    }
                }
            }

            File repertory = null;
            
            if (odvConv.equals(E_TypeConversion.ODV_MONOSTATION))
            {
                repertory = new File(outputFile);                
            }
            else
            {
                repertory = new File(outputFile).getParentFile();
            }
            
            if (!(repertory == null) && !repertory.exists())
                repertory.mkdirs();
    
            NemoWriter writer = NemoWriterFactory.createWriter(conv);

            writer.write(null);

            
            // export CDI summary (FAE 4525)
            if (! cdiSummaryFile.equals(Global.EMPTY_STRING))
            {
                CDIResumeWriter.getInstance().reset();
                CDIResumeWriter.getInstance().setNomFichierSortie(cdiSummaryFile);
                CDIResumeWriter.getInstance().writeResume(conv);
            }
           
            if (couplingTableUpdate){
            // update coupling table (FAE 4525)
            MappingTable mappingTable = new MappingTable();
            mapping(conv, mappingTable, couplingTablePrefix);
         //   mappingTable.exporter(couplingTableFile, logger);
            }
            
            return true;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            logger.error(Nemo.getInstance().getLog_currentInputFile()+" l."+Nemo.getInstance().getLog_currentInputLine());
            return false;
        }

    }
    
    
    
    
    
    private static void mapping(Conversion conv, MappingTable mappingTable, String couplingTablePrefix)
    {
        Mapping mapping;


        InputDomain inputDomain = Nemo.getInstance().getDomain();
        CruiseDomain cDomain = inputDomain.getCruise();
        StationDomain sDomain = inputDomain.getStation();
        try
        {
            for (String outputPath : inputDomain.getMedia().getOutputFiles().keySet()){
                File outputFile = new File(outputPath);
                for (String stRef : inputDomain.getMedia().getOutputFiles().get(outputPath)){
                    Station st = sDomain.getStationByRef(stRef);
                    String localCdiId;

                    localCdiId = sDomain.getLocalCdiId(cDomain, conv, st.getReference());

                    mapping = new Mapping(localCdiId, conv.toString());

                    String modus = Mapping.MODUS_MULTISTATIONS;

                    StringBuffer filename = new StringBuffer();
                    inputDomain.getConversion().setNomMappingHorsPrefix(couplingTablePrefix);
                    filename.append(inputDomain.getConversion().getNomMappingHorsPrefix());

                    if (inputDomain.getMedia().getModel().getProcessType() == E_ProcessType.CRUISE_DIR){
                        if (!filename.equals(Global.EMPTY_STRING))
                            filename.append(File.separator);
                        if (inputDomain.getConversion().getCurrent() == E_TypeConversion.ODV_MONOSTATION){
                            filename.append(outputFile.getParentFile().getName());
                            filename.append(File.separator);
                            modus = Mapping.MODUS_MONOSTATION;
                        }
                        filename.append(outputFile.getName());
                    }
                    else{
                        if (inputDomain.getConversion().getCurrent() == E_TypeConversion.ODV_MONOSTATION){
                            if (!filename.equals(Global.EMPTY_STRING))
                                filename.append(File.separator);
                            filename.append(outputFile.getName());
                            modus = Mapping.MODUS_MONOSTATION;
                        }
                    }

                    mapping.setFilename(filename.toString());
                    mapping.setDate(new Date());
                    mapping.setModus(modus);

                    mappingTable.persist(mapping);
                }
            }
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
