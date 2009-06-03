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
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.output.SurveyWriter;
import fr.ifremer.nemo.output.SurveyWriterFactory;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class SWTProcess implements IRunnableWithProgress
{

    public static ProgressNotification notifier;

    public static Survey survey;

    private SurveyWriter writer;

    public static Conversion formatConversion;
    public static boolean monoStation;
    public static Station currentStation;

    public static IProgressMonitor monitor2;

    private Mapping mapping;
    private final MappingTable MappingTable = new MappingTable();

    @SuppressWarnings("unused")
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException
    {

        try
        {
            System.out.println("Conversion START at " + new Date());

            monitor.beginTask(Messages.SWTProcess_1, IProgressMonitor.UNKNOWN);
            monitor2 = monitor;

            if (ViewMain.isCollection())
            {
                runMultiProcess();
            }
            else
            {
                // System.out.println("Campagne");
                runSingleProcess(Global.EMPTY_STRING,
                    ViewMain.editConversionForm.nomDossierSortie, null, null);
            }

            // Ecriture du resume CDI
            if (Configuration.getSingleton().isWriteCdi())
            {
                monitor2.setTaskName(Messages.SWTProcess_CDIresume);
                CDIResumeWriter.getInstance().writeResume(formatConversion);
            }

            System.out.println("Conversion END   at " + new Date()
                    + "\n---------------");
            monitor.done();
        }
        catch (InterruptedException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new InterruptedException(Messages.SWTProcess_5
                    + e.getMessage());
        }
    }

    private void runMultiProcess() throws InterruptedException, Exception
    {

        if (monitor2.isCanceled())
            throw new InterruptedException(Global.EMPTY_STRING);

        int numItem = 0;
        String[] items = new File(ViewMain.nomRepertoire).list();

        for (String item : items)
        {
            numItem++;
            String msg =
                " : " + item + " ( " + numItem + " / " + items.length + " ) "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

            String multiStationsFileName = null;
            String surveyName = Intern.unknown;

            String outputDirectory =
                ViewMain.editConversionForm.nomDossierSortie;
            String inputDirectory =
                ViewMain.nomRepertoire + File.separator + item;

            if (ViewMain.collectionCas1)
            {
                // System.out.println("Collection Campagnes");
                DataModel.dirSurvey.setDirectory(inputDirectory);
                outputDirectory =
                    ViewMain.editConversionForm.nomDossierSortie
                            + File.separator + item;

                multiStationsFileName = outputDirectory + Global.EXT_TXT;
                surveyName = item;
            }
            else
            {
                // System.out.println("Collection Fichiers");
                DataModel.singleSurvey.setFile(inputDirectory);
                multiStationsFileName =
                    outputDirectory + File.separator
                            + item.substring(0, item.lastIndexOf('.'))
                            + Global.EXT_TXT;
            }

            runSingleProcess(msg, outputDirectory, multiStationsFileName,
                surveyName);
        }
    }

    private void runSingleProcess(String msg, String outputDirName,
            String multiStationsFileName, String surveyName)
            throws InterruptedException, Exception
    {

        // NOTIFIER
        notifier();

        // CONVERSION EN OBJET
        if (!monitor2.isCanceled())
        {
            executeConvert(msg, surveyName);
            System.out.println("survey : " + survey.getReference() + " "
                    + survey.getName());
        }

        // ECRITURE DES FICHIERS
        if (!monitor2.isCanceled())
        {
// System.out.println("START writing at " + new Date());
            monitor2.setTaskName(Messages.SWTProcess_4);
            writer = SurveyWriterFactory.createWriter(formatConversion);

            if ((formatConversion == Conversion.ODV) && (monoStation))
            {
                writeMonoStationFile(outputDirName);
            }
            else
            {
                String fileName = ViewMain.editConversionForm.nomFichierSortie;
                if (multiStationsFileName != null)
                {
                    fileName = multiStationsFileName;
                }
                writeMultiSationsFile(fileName, msg);
            }
// System.out.println("END writing at " + new Date());
        }

        if ((!monitor2.isCanceled())
                && (Configuration.getSingleton().isWriteCdi()))
        {
            CDIResumeWriter.getInstance().addSurvey(survey);
        }

        if (monitor2.isCanceled())
            throw new InterruptedException(Global.EMPTY_STRING);
    }

    private void notifier()
    {
        notifier = new ProgressNotification() {
            public void progress(final int val)
            {
                try
                {
                    Application.display.syncExec(new Runnable() {
                        public void run()
                        {
                            monitor2.subTask(Messages.SWTProcess_2 + val
                                    + Global.POURCENT);
                        }
                    });
                }
                catch (Exception e)
                {
                }
            }
        };
    }

    private void executeConvert(String msgComplement, String surveyName)
            throws Exception
    {
        notifier.progress(0);
        monitor2.setTaskName(Messages.SWTProcess_3 + msgComplement);

        survey = DataModel.inputModel.convert(monitor2, notifier);

        if (surveyName != null)
            survey.setName(surveyName);

        if (!ViewMain.isCollection())
        {
            DataModel.inputModel.setConverted(true);
        }
    }

    private void writeMonoStationFile(String outputDirName)
            throws GenerationException
    {
        File output = new File(outputDirName);
        if (!output.exists())
            output.mkdirs();

        int i = 0;
        int totalStations = survey.getStations().length;

        for (Station station : survey.getStations())
        {
            if (!monitor2.isCanceled())
            {
                i++;
                monitor2.setTaskName(Messages.SWTProcess_4
                        + " : " + i + " / " + totalStations); //$NON-NLS-1$//$NON-NLS-2$
                String localCdiId =
                    station.getLocalCdiId(survey, formatConversion.toString());
                currentStation = station;

                ViewMain.editConversionForm.nomFichierSortie =
                    outputDirName + File.separator + localCdiId
                            + Global.EXT_TXT;
                File fichierSortie =
                    new java.io.File(
                        ViewMain.editConversionForm.nomFichierSortie);

                writer.writeSurvey(survey, fichierSortie, formatConversion,
                    notifier);
                station.setDataSize(fichierSortie.length() / 1024f / 1024f);

                if (Configuration.getSingleton().isWriteMapping())
                {
                    mapping =
                        new Mapping(localCdiId, formatConversion.toString());

                    StringBuffer filename = new StringBuffer();
                    if (!ViewMain.editConversionForm.nomHorsPrefix
                        .equals(Global.EMPTY_STRING))
                    {
                        // Si le nom hors prefix est vide, il ne faut pas
                        // ajouter le séparateur
                        filename
                            .append(ViewMain.editConversionForm.nomHorsPrefix);
                        filename.append(File.separator);
                    }
                    if (ViewMain.collectionCas1)
                    {
                        // collection campagne
                        filename
                            .append(fichierSortie.getParentFile().getName());
                        filename.append(File.separator);
                    }
                    filename.append(fichierSortie.getName());
                    mapping.setFilename(filename.toString());

// if(EditConversion.nomHorsPrefix.equals(Global.EMPTY_STRING))
// mapping.setFilename(EditConversion.nomHorsPrefix + fichierSortie.getName());
// else
// mapping.setFilename(EditConversion.nomHorsPrefix + File.separator +
                    // fichierSortie.getName());

                    mapping.setDate(new Date());
                    mapping.setModus(Mapping.MODUS_MONOSTATION);
                    MappingTable.persist(mapping);
                }
            }
        }
    }

    private void writeMultiSationsFile(String outputFileName, String msg)
            throws GenerationException
    {
        File fichierSortie = new java.io.File(outputFileName);

        if (msg != null)
        {
            monitor2.setTaskName(Messages.SWTProcess_4 + msg);
        }

        writer.writeSurvey(survey, fichierSortie, formatConversion, notifier);
        survey.setDataSize(fichierSortie.length() / 1024f / 1024f);

        if (Configuration.getSingleton().isWriteMapping())
        {
            for (Station station : survey.getStations())
            {
                String localCdiId =
                    station.getLocalCdiId(survey, formatConversion.toString());
                mapping = new Mapping(localCdiId, formatConversion.toString());

                StringBuffer filename = new StringBuffer();
                filename.append(ViewMain.editConversionForm.nomHorsPrefix);
                if (ViewMain.collectionCas1)
                {
                    // collection campagne
                    if (!ViewMain.editConversionForm.nomHorsPrefix
                        .equals(Global.EMPTY_STRING))
                    {
                        filename.append(File.separator);
                    }
                    filename.append(fichierSortie.getName());
                }
                mapping.setFilename(filename.toString());
                mapping.setDate(new Date());
                mapping.setModus(Mapping.MODUS_MULTISTATIONS);

                MappingTable.persist(mapping);
            }
        }
    }

    public Conversion getFormatConversion()
    {
        return formatConversion;
    }

    public static Station getCurrentStation()
    {
        return currentStation;
    }

    public static boolean isMonoStation()
    {
        return monoStation;
    }

    public static boolean isOdv()
    {
        return (formatConversion == Conversion.ODV);
    }
}
