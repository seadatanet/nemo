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
import java.io.FileReader;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.mapping.MappingTable;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.output.NemoWriterFactory;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Preformat;
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
            E_TypeConversion odvConv, String datasetRef, String cdiSummaryFile, String couplingTableFile, Logger logger)
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

            if (nemo.getDomain().getMedia().getModel().getSeparator() != Global.EMPTY_STRING)
                inputFile =
                    Preformat.runBatch(new File(inputFile)).getAbsolutePath();

            nemo.getDomain().getMedia().getModel().setSourcePath(inputFile);

            nemo.getDomain().getConversion().setCurrentConversion(odvConv);

            nemo.getDomain().getMedia().buildInputFilesNames();

            nemo.getDomain().getMedia().setOutputFileName(outputFile);

            nemo.getDomain().getConversion().setSortData(sort);

            // The default dataset ref
            String strDefault = datasetRef;

            if (strDefault.equals(Global.EMPTY_STRING))
                strDefault =
                    nemo.getDomain().getCruise().getModel().getReference();

            if (strDefault.equals(Global.EMPTY_STRING))
                strDefault = nemo.getDomain().getCruise().getModel().getName();

            if (odvConv != E_TypeConversion.MEDATLAS)
                nemo.getDomain().getCruise().setDataSetRef(strDefault);

            // Is a station property in file
            int nbLignes = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader() + Nemo.getInstance().getDomain().getMedia().getShellStationHeaderNumberLines();
            
            for (PropertyModel pModel : nemo.getDomain().getCruise()
                .getStation().getModel().getProperties())
            {
                if (pModel.getFragment() != null)
                {
                    if (pModel.getFragment().getLastLine() > nbLignes)
                    {
                        nemo.getDomain().getCruise().getStation()
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
           
            // export coupling table (FAE 4525)
            new MappingTable().exporter(couplingTableFile);
            
            return true;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

}
