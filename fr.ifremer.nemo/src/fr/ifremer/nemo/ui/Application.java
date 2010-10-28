package fr.ifremer.nemo.ui;

import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.NemoShell;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable
{

    public static PlatformUI platformUI;

    public static Display display;

    public static Logger batchLogger;

    public static Options opt;


    private static final String optBatchMode = "batch";

    /** obligatory options**/
    private static final String optInputFile = "i";
    private static final String optOutputFile = "o";
    private static final String optModelFile = "m";
    private static final String optConv = "c";

    /** mandatory options **/

    // ODV mono or multi: default is mono - set multi only if optOdvMulti is present
    private static final String optOdvMono = "mono"; 
    private static final String optOdvMulti = "multi";

    private static final String optSortDatas = "sort"; 
    private static final String optDatasetRef = "datasetRef"; 
    
    private static final String  optCdiSummary = "cdiSummary"; 
    private static final String  optCouplingTable = "couplingTable"; 
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
     */
    public Object run(Object args) throws Exception
    {

        String[] strArgs = (String[]) args;
        defineBatchOptions();
        BasicParser p = new BasicParser();

        try
        {

            CommandLine cl = p.parse(opt, strArgs);

            /**************************************************************************
             * BATCH MODE
             **************************************************************************/ 
            if (cl.hasOption(optBatchMode))
            {

                String inputFile;
                String outputFile;
                String modelFile;
                Conversion convType;
                E_TypeConversion convOdv = null;
                String datasetReference = "";
                boolean sortDatas = false;
                String cdiSummaryFile=Global.EMPTY_STRING;
                String couplingTableFile=Global.EMPTY_STRING;
                
                // LOGGER
                DOMConfigurator.configure("resources/log4jBatch.xml"); //$NON-NLS-1$
                batchLogger = Logger.getLogger("nemoBatch");//$NON-NLS-1$
                batchLogger.setLevel(Level.DEBUG);


                if ( checkBatchOptionsOk(cl))
                {
                    inputFile = cl.getOptionValue(optInputFile);
                    outputFile = cl.getOptionValue(optOutputFile);
                    modelFile = cl.getOptionValue(optModelFile);
                    convType = Conversion.valueOf(cl.getOptionValue(optConv).toUpperCase());
                    // CONVERSION TYPE
                    switch (convType)
                    {
                        case MEDATLAS:
                            convOdv = E_TypeConversion.MEDALAS;
                            break;
                        case ODV:
                            convOdv = E_TypeConversion.ODV_MONOSTATION;
                            if (cl.hasOption(optOdvMulti))
                                convOdv = E_TypeConversion.ODV_MULTISTATIONS;
                            break;
                    }
                    
                    // DATASET REFERENCE
                    if (cl.hasOption(optDatasetRef))
                    {
                        datasetReference = cl.getOptionValue(optDatasetRef);
                    }
                    // SORT DATA
                    if (cl.hasOption(optSortDatas))
                    {
                        sortDatas = true;
                    }

                    // CDI SUMMARY
                    if (cl.hasOption(optCdiSummary))
                    {
                        cdiSummaryFile = cl.getOptionValue(optCdiSummary);
                    }
                    // COUPLING TABLE
                    if (cl.hasOption(optCouplingTable))
                    {
                        couplingTableFile = cl.getOptionValue(optCouplingTable);
                    }
                    
                    // print arguments in the log file
                    batchLogger.info("Batch mode starting. Arguments :");
                    StringBuilder sb = new StringBuilder();
                    for (String s : strArgs)
                        sb.append(s + " ");
                    batchLogger.info(sb.toString());

                    boolean success =
                        NemoShell.main(inputFile, outputFile, modelFile, convType,
                            sortDatas, convOdv, datasetReference, cdiSummaryFile, couplingTableFile, batchLogger);

                    if (success)
                    {
                        batchLogger.info("Batch mode successfully ended.");
                        System.exit(0);
                    }
                    else
                    {
                        batchLogger.info("Batch mode ended with errors.");
                        System.exit(0);
                    }
                }
                else
                {
                    exitPrintingUsage(); 
                }

            }

            /**************************************************************************
             * GUI MODE
             **************************************************************************/ 
            else
            {
                display = PlatformUI.createDisplay();
                try
                {
                    // Utils.initConfig();
                    Nemo.getInstance();

                    int returnCode =
                        PlatformUI.createAndRunWorkbench(display,
                            new ApplicationWorkbenchAdvisor());
                    if (returnCode == PlatformUI.RETURN_RESTART)
                    {
                        return IPlatformRunnable.EXIT_RESTART;
                    }

                    return IPlatformRunnable.EXIT_OK;
                }
                finally
                {
                    display.dispose();
                }
            }
        }
        catch (ParseException e)
        {
            exitPrintingUsage();
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param opt
     * @param e
     */
    private void exitPrintingUsage()
    {
        batchLogger.error("Arguments error. Nemo batch stopped.");
        HelpFormatter hf = new HelpFormatter();
        PrintWriter pw = new PrintWriter(System.out);
        hf.printUsage(pw, 200, "nemo_batch", opt);
        pw.flush();
        hf.printHelp("nemo_batch", opt);

        System.exit(1);
    }

    private void defineBatchOptions()
    {
        opt = new Options();

        opt.addOption(optBatchMode, false,
        "Not necessary using nemo_batch launcher");
        opt.addOption(optInputFile, true, "Input file / directory");
        opt.addOption(optOutputFile, true, "Output file / directory");
        opt.addOption(optModelFile, true, "Model file (xml)");
        opt.addOption(optConv, true, "Conversion type (ovd or medatlas)");
        opt
        .addOption(
            optOdvMono,
            false,
        "ODV conversion type for mono station output file (one output file per station) Default mode if neither mono or multi option precised.");
        opt
        .addOption(
            optOdvMulti,
            false,
        "ODV conversion type for multi stations output file (one unique file for all stations).");
        opt
        .addOption(
            optDatasetRef,
            true,
        "Optional dataset reference, default value is the cruise reference or the cruise name");
        
        opt.addOption(optCdiSummary, true, "CDI Summary file path (mandatory)");
        opt.addOption(optCouplingTable, true, "Coupling table file path (mandatory)");
    }


    /**
     * check option received from batch command
     * @param cl
     * @return
     */
    private boolean checkBatchOptionsOk(CommandLine cl)
    {
        boolean checkOptionsOk=true;

        // check obligatory options
        if ( 
                cl.getOptionValue(optInputFile) == null 
                ||
                cl.getOptionValue(optOutputFile) == null
                ||
                cl.getOptionValue(optModelFile) == null
                ||
                cl.getOptionValue(optConv) == null
        )
        {
            checkOptionsOk=false;
        }else
        {
            // check conversion value
           if ( !(
                   cl.getOptionValue(optConv).toUpperCase().equals(Conversion.MEDATLAS.name())
                   ||
                   cl.getOptionValue(optConv).toUpperCase().equals(Conversion.ODV.name())
                   ))
                   {
               checkOptionsOk=false;
                   }
        }


        return checkOptionsOk;
    }
}
