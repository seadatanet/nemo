package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.PrintWriter;
import java.text.MessageFormat;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.FrameworkUtil;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.NemoShell;
import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.NemoVersion;
import fr.ifremer.sismer_tools.JavaVersionDectect;
/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable
{

    public static PlatformUI platformUI;

    public static Display display;


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
        	 Nemo.getInstance().setLogger(LogManager.getLogger(Global.LOGGER_NAME_BATCH));
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
        	 // LOGGER
            LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
            File file = new File(appDir + File.separator  + "resources/log4j2.xml");

            context.setConfigLocation(file.toURI());
            if (cl.hasOption(optBatchMode))
            {
            	  Nemo.getInstance().setLogger(LogManager.getLogger(Global.LOGGER_NAME_BATCH));
            }else{
            	Nemo.getInstance().setLogger(LogManager.getLogger(Global.LOGGER_NAME_GUI));
            }
         // check java version
        	int javaVersion=JavaVersionDectect.getJavaVersion();
        	if(javaVersion>=9) {
        		Nemo.getInstance().getLogger().warn(
        		MessageFormat.format(Messages.JavaVersionHigherThanNine,javaVersion));
    
        	}
            
            /**************************************************************************
             * BATCH MODE
             **************************************************************************/
            if (cl.hasOption(optBatchMode))
            {
                Nemo.getInstance().setBatchMode(true);
                NemoVersion.check(FrameworkUtil.getBundle(getClass()).getVersion());
                
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

              
                
                String edmoCode = Nemo.getInstance().getConfig().getIdEdmoRespCdi();
                if (edmoCode==null || edmoCode.isEmpty()){
                	Nemo.getInstance().getLogger().error(Messages.error_batch_edmo); 
                	System.out.println(Messages.error_batch_edmo);
                	 System.exit(1);
                }
                else if (checkBatchOptionsOk(cl))
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
                    Nemo.getInstance().getLogger().info("Batch mode starting. Arguments :");
                    StringBuilder sb = new StringBuilder();
                    for (String s : strArgs)
                        sb.append(s + " ");
                    Nemo.getInstance().getLogger().info(sb.toString());

                    boolean success =
                        NemoShell.main(inputFile, outputFile, modelFile,
                            convType, sortDatas, convOdv, datasetReference,
                            cdiSummaryFile, couplingTableUpdate,
                            couplingTablePrefix, Nemo.getInstance().getLogger());

                    if (success)
                    {
                    	Nemo.getInstance().getLogger().info("Batch mode successfully ended.");
                        System.exit(0);
                    }
                    else
                    {
                    	Nemo.getInstance().getLogger().info("Batch mode ended with errors.");
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
           
            	 Nemo.getInstance().setLogger(LogManager.getLogger(Global.LOGGER_NAME_GUI));
                Nemo.getInstance().setBatchMode(false);
                display = PlatformUI.createDisplay();
                
               
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
                	Nemo.getInstance().getLogger().info("GUI mode successfully ended.");
                    display.dispose();
                }
            }
        }
        catch (ParseException e)
        {
            exitPrintingUsage();
            e.printStackTrace();
            Nemo.getInstance().getLogger().debug("GUI mode ended with errors.", e);
        }

        return null;
    }

    /**
     * @param opt
     * @param e
     */
    private void exitPrintingUsage()
    {
    	Nemo.getInstance().getLogger().error("Arguments error. Nemo batch stopped.");
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
        opt.addOption(optConv, true, "Conversion type (odv, medatlas or NETCDF)");
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
        opt.addOption(optSortDatas, false, "Sort data within stations by increasing reference parameter");
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
        	Nemo.getInstance().getLogger()
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
            	Nemo.getInstance().getLogger()
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
