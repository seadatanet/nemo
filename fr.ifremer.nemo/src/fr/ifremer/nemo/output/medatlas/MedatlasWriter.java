package fr.ifremer.nemo.output.medatlas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.presdepth.CalcPres;

/**
 * specific NemoWriter : MEDATLAS
 */
public class MedatlasWriter extends NemoWriter
{

    Conversion c = Conversion.MEDATLAS;
    HashMap<Double, String> stationDatas;
    int indexRecord;

    /**
     * pour les references de stations incrementees, on gere avec cette variable
     * - on laisse tomber le code de
     * stationDomain.getReference(currentStation.getFileName(),
     * inputStationHeader, false); currentStation.getReference renvoie des
     * nombres a la suite, puiseque les stations mises en memoire dans le
     * domaine ont eu un increment attribué quand incrementer: si on a un
     * repertoire de fichiers contenant chacun une station, il faut incrementer
     * a chaque fichier si on a 1 fichier contenant 1 ou plusieurs stations, il
     * faut incrementer a chaque station lue
     **/
    int increment = -1;
    boolean isInputIsAFile = false;
    boolean isRefIncremented = false;

    public MedatlasWriter()
    {
        super();
        stationDatas = new HashMap<Double, String>();
    }

    /* (non-Javadoc)
     * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
     */
    @Override
    public final void writeFile(String inputFilePath)
            throws GenerationException
    {
        try
        {
            isRefIncremented =
                isRefIncremented =
                    iDomain.getStation().getModel().getReferenceProperty()
                        .isUserProvided();
            if (isRefIncremented)
            {
                increment =
                    Integer.valueOf(Nemo.getInstance().getDomain().getStation()
                        .getModel().getReferenceProperty().getUserValue());
            }
            isInputIsAFile =
                iDomain.getMedia().getModel().getProcessType() == E_ProcessType.CRUISE_FILE;

            String outputFileName = iDomain.getMedia().getOutputFileName();
            outputFileWriter =
                new PrintWriter(new BufferedWriter(new FileWriter(
                    outputFileName)));
            CruiseDomain cruiseDomain = iDomain.getCruise();
            StationDomain stationDomain = iDomain.getStation();
            DataDomain dataDomain = iDomain.getData();

            // écriture de l'entête campagne
            StringBuffer cruiseHeader = cruiseDomain.getHeader(c);
            outputFileWriter.println(cruiseHeader);

            stationDomain.updateRef();

            /**
             * boucle sur les stations prealablement lues et stockées dans le
             * stationDomain (donc TOUTES les stations, quel que soit le nb de
             * fichiers en entree)
             */
            for (Station currentStation : stationDomain
                .getStations(inputFilePath))
            {

                // FAE 6143 check doublons
                stationDomain.getAndCheckLocalCDI(cruiseDomain, Conversion.ODV,
                    currentStation.getReference());
                CalcPres.init();

                File inputFile =
                    new File(inputFilePath + File.separator
                            + currentStation.getFileName());
                bufferedReader = new BufferedReader(new FileReader(inputFile));

                indexRecord = 0;
                stationDatas.clear();
                boolean stationWrited = false;

                // scan the InputFile line by line
                while ((readedLine = bufferedReader.readLine()) != null)
                {
                    numLine++;

                    /**
                     * 1ere ligne entree
                     */
                    if (numLine == 1)
                    {
                        consumeFileHeader();
                    }
                    /**
                     * derniere ligne d'une station en sortie (end station)
                     * reperee grace a endData
                     */
                    if (isEndData() && stationWrited)
                    {
                        // écriture de la dernière ligne de données
                        writeLastDataLine(dataDomain, currentStation,
                            outputFileName);
                    }
                    /**
                     * entete station
                     */
                    if (isStartStationHeader())
                    {

                        // écriture de l'entête station
                        String[] inputStationHeader = consumeStationHeader();
                        String readedRef = "";
                        if (inputStationHeader != null)
                        {
// readedRef= stationDomain.getReference(currentStation.getFileName(),
// inputStationHeader, false);
                            readedRef =
                                readReference(currentStation.getFileName(),
                                    inputStationHeader, false);
                        }

                        if (inputStationHeader != null
                                && currentStation.getReference().equals(
                                    readedRef))
                        {

                            outputFileWriter.println(stationDomain.getHeader(c,
                                currentStation, inputStationHeader));

                            // écriture de l'entête des données (titres des
// colonnes)
                            outputFileWriter.println(dataDomain
                                .getDataHeader(c));

                            if (stationDomain.isSomeInData())
                            {
                                // la première ligne des données est déjà
// "consommée" dans stationHeaderBuffer
                                String inputLine =
                                    inputStationHeader[inputStationHeader.length - 1];
                                writeDataLine(dataDomain, currentStation,
                                    inputLine);
                            }
                            writeDataLine(dataDomain, currentStation,
                                readedLine);
                            stationWrited = true;
                        }
                        else if (stationWrited || inputStationHeader == null)
                        {
                            break;
                        }
                        if (isInputIsAFile)
                        {

                            increment++;
                        }
                    }
                    /**
                     * donnees (stationWrited = l'entete de station a deja ete
                     * ecrit)
                     */
                    else if (stationWrited)
                    {
                        // écriture de la ligne de données
                        writeDataLine(dataDomain, currentStation, readedLine);
                    }
                }
                // fin de lecture du fichier d'entree courant
                if (isInputIsAFile && isRefIncremented)
                {
                    increment =
                        Integer.valueOf(Nemo.getInstance().getDomain()
                            .getStation().getModel().getReferenceProperty()
                            .getUserValue());
                }
                /**
                 * derniere ligne d'une station en sortie (end station) reperee
                 * grace a endData=EOF
                 */
                if (iDomain.getMedia().getModel().getEndData() == E_EndData.EOF
                        && stationWrited)
                {
                    // écriture de la dernière ligne de données
                    writeLastDataLine(dataDomain, currentStation,
                        outputFileName);

                }

                // FAE6315 on ajoute la station pour le CDI ici au lieu de le
// faire dans la methode writeLastDataLine
                // qui n'est pas appelée dans tous les cas (slmt si endData=EOF)
                if (stationWrited)
                {
                    iDomain.getMedia().addStationInOutput(outputFileName,
                        currentStation.getReference());
                }

                numLine = 0;
                if (!isInputIsAFile && isRefIncremented)
                {
                    increment++;
                }
                stationDomain.updateRef();
            }
            // fin de la boucle sur les station du stationDomain
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GenerationException(e);
        }
    }

    private String readReference(String inputFileName,
            String[] inputStationHeader, boolean mustIncrement)
            throws ParseException
    {
        String ref = null;
        if (iDomain.getStation().getModel().getReferenceProperty()
            .isUserProvided())
        {
            ref = String.valueOf(increment);
        }
        else
        {
            ref =
                iDomain.getStation().getReference(inputFileName,
                    inputStationHeader, false);
        }
        return ref;
    }

    private void writeDataLine(DataDomain data, Station currentStation,
            String inputLine) throws Exception
    {
        if (iDomain.getConversion().isSortData())
        {
            // préparation des données pour le tri
            Double key = Double.NaN;
            if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE)
            {
                ParameterModel keyParam =
                    data.getModel().getParameterModels()[0];
                keyParam.computeParameterValue(inputLine);
                key = keyParam.getLineValue().getValue();
            }
            else
            {
                key =
                    Long.valueOf(
                        DateTimeUtils.getCurrentDate(currentStation,
                            indexRecord)).doubleValue();
            }

            stationDatas.put(key,
                data.getDataLine(c, currentStation, inputLine, indexRecord)
                    .toString());

        }
        else
        {
            // écriture directe
            outputFileWriter.println(data.getDataLine(c, currentStation,
                inputLine, indexRecord));
        }
        indexRecord++;
    }

    private void writeLastDataLine(DataDomain data, Station currentStation,
            String outputFileName)
    {
        if (iDomain.getConversion().isSortData())
        {
            // écriture des données après tri
            List<Double> keys = new ArrayList<Double>();
            keys.addAll(stationDatas.keySet());
            Collections.sort(keys);
            for (Double key : keys)
            {
                outputFileWriter.println(stationDatas.get(key));
            }
        }
        // écriture de la dernière ligne de données
        outputFileWriter.println(data.getMedatlasLastDataLine());
    }

}
