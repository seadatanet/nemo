package fr.ifremer.nemo.ui;

import java.io.File;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.NemoShell;
import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable
{

    public static PlatformUI platformUI;

    public static Display display;

    public static Logger batchLogger;
    
    public static Logger guiLogger;

    public static Options opt;

    private static final String optBatchMode = "batch";

    public static String appDir = "";

    /** obligatory options **/
    private static final String optInputFile = Global.optInputFile;
    private static final String optOutputFile = Global.optOutputFile;
    private static final String optModelFile = Global.optModelFile;
    private static final String optConv = Global.optConv;

    /** mandatory options **/

    // ODV mono or multi: default is mono - set multi only if optOdvMulti is
    // present
    private static final String optOdvMono = Global.optOdvMono;
    private static final String optOdvMulti = Global.optOdvMulti;

    private static final String optSortDatas = Global.optSortDatas;
    private static final String optDatasetRef = Global.optDatasetRef;

    private static final String optCdiSummary = Global.optCdiSummary;
    private static final String optCouplingTableUpdate =
        Global.optCouplingTableUpdate;
    private static final String optCouplingTablePrefix =
        Global.optCouplingTablePrefix;

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

            // retrieve application path (FAE 9910)
            appDir =
                Application.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath();
            if (appDir.endsWith("jar"))
            {
                appDir =
                    (new File(Application.class.getProtectionDomain()
                        .getCodeSource().getLocation().getFile())
                        .getParentFile().getParent());

                Intern.dir_resources =
                    appDir + File.separator + Intern.dir_resources;
            }

            /**************************************************************************
             * BATCH MODE
             **************************************************************************/
            if (cl.hasOption(optBatchMode))
            {
                Nemo.getInstance().setBatchMode(true);
                String inputFile;
                String outputFile;
                String modelFile;
                Conversion convType = null;
                E_TypeConversion convOdv = null;
                String datasetReference = "";
                boolean sortDatas = false;
                String cdiSummaryFile = Global.EMPTY_STRING;
                boolean couplingTableUpdate = false;
                String couplingTablePrefix = Global.EMPTY_STRING;

                // LOGGER
                DOMConfigurator.configure(appDir + File.separator
                        + "resources/log4jBatch.xml"); //$NON-NLS-1$
                batchLogger = Logger.getLogger("nemoBatch");//$NON-NLS-1$
                batchLogger.setLevel(Level.DEBUG);

                if (checkBatchOptionsOk(cl))
                {
                    inputFile = cl.getOptionValue(optInputFile);
                    outputFile = cl.getOptionValue(optOutputFile);
                    modelFile = cl.getOptionValue(optModelFile);

                    if (cl.getOptionValue(optConv) != null)
                    {
                        convType =
                            Conversion.valueOf(cl.getOptionValue(optConv)
                                .toUpperCase());

                        // CONVERSION TYPE
                        switch (convType)
                        {
                            case MEDATLAS:
                                convOdv = E_TypeConversion.MEDATLAS;
                            break;
                            case ODV:
                                convOdv = E_TypeConversion.ODV_MONOSTATION;
                                if (cl.hasOption(optOdvMulti))
                                    convOdv =
                                        E_TypeConversion.ODV_MULTISTATIONS;
                            break;
                            case NETCDF:
                                convOdv = E_TypeConversion.NETCDF_MONOSTATION;
                                if (cl.hasOption(optOdvMulti))
                                    convOdv =
                                        E_TypeConversion.NETCDF_MULTISTATIONS;
                            break;
                        }

                    }
                    else
                    {
                        convOdv = E_TypeConversion.UNKNOWN;
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
                    if (cl.hasOption(optCouplingTableUpdate))
                    {
                        couplingTableUpdate = true;
                        couplingTablePrefix =
                            cl.getOptionValue(optCouplingTablePrefix);
                    }

                    // print arguments in the log file
                    batchLogger.info("Batch mode starting. Arguments :");
                    StringBuilder sb = new StringBuilder();
                    for (String s : strArgs)
                        sb.append(s + " ");
                    batchLogger.info(sb.toString());

                    boolean success =
                        NemoShell.main(inputFile, outputFile, modelFile,
                            convType, sortDatas, convOdv, datasetReference,
                            cdiSummaryFile, couplingTableUpdate,
                            couplingTablePrefix, batchLogger);

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
                Nemo.getInstance().setBatchMode(false);
                display = PlatformUI.createDisplay();
                
                // LOGGER
                DOMConfigurator.configure(appDir + File.separator
                        + "resources/log4jBatch.xml"); //$NON-NLS-1$
                guiLogger = Logger.getLogger("nemoGui");//$NON-NLS-1$
                guiLogger.setLevel(Level.DEBUG);
                try
                {
                     
                    ManageLists.initLists();
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
                    guiLogger.info("Gui mode successfully ended.");
                    display.dispose();
                }
            }
        }
        catch (ParseException e)
        {
            exitPrintingUsage();
            e.printStackTrace();
            guiLogger.debug("Gui mode ended with errors.", e);
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
        opt.addOption(optConv, true, "Conversion type (ovd, medatlas or NETCDF)");
        opt.addOption(
            optOdvMono,
            false,
            "ODV conversion type for mono station output file (one output file per station) Default mode if neither mono or multi option precised.");
        opt.addOption(
            optOdvMulti,
            false,
            "ODV conversion type for multi stations output file (one unique file for all stations).");
        opt.addOption(
            optDatasetRef,
            true,
            "Optional dataset reference, default value is the cruise reference or the cruise name if reference is empty");

        opt.addOption(optCdiSummary, true, "CDI Summary file path (mandatory)");
        opt.addOption(optCouplingTableUpdate, false,
            "update the coupling table database (optional)");
        opt.addOption(optCouplingTablePrefix, true,
            "coupling table database prefix (needed if couplingTableUpdate is present)");
    }

    /**
     * check option received from batch command
     * 
     * @param cl
     * @return
     */
    private boolean checkBatchOptionsOk(CommandLine cl)
    {
        boolean checkOptionsOk = true;

        // check mandatory options
        if (cl.getOptionValue(optInputFile) == null
                || cl.getOptionValue(optOutputFile) == null
                || cl.getOptionValue(optModelFile) == null)
        {
            batchLogger
                .info("Error, input output and model file are mandatory options");
            checkOptionsOk = false;
        }

        if (cl.getOptionValue(optConv) != null)
        {
            // check conversion value
            if (!(cl.getOptionValue(optConv).toUpperCase()
                .equals(Conversion.MEDATLAS.name()) || cl
                .getOptionValue(optConv).toUpperCase()
                .equals(Conversion.ODV.name()) || cl
                .getOptionValue(optConv).toUpperCase()
                .equals(Conversion.NETCDF.name())))
            {
                batchLogger
                    .info("Error, Conversion type must be \"medatlas\" , \"odv\" or \"NETCDF\" ");
                checkOptionsOk = false;
            }
        }

        // if update coupling table is used, we must know the prefix to use
        if (cl.hasOption(optCouplingTableUpdate))
        {
            if (!cl.hasOption(optCouplingTablePrefix))
            {
                checkOptionsOk = false;
            }
        }

        return checkOptionsOk;
    }
}
