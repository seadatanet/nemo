package fr.ifremer.nemo.output.odv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;

/**
 * specific NemoWriter : ODV
 */
public class OdvWriter extends NemoWriter
{

    int indexRecord;

    public OdvWriter()
    {
        super();
        myConversion = Conversion.ODV;
    }

    @Override
    protected String getOutputFileName(String inputFilePath)
    {
    	   outputFileName = iDomain.getMedia().getOutputFileName();
           if (isProcessType_CollectionDir)
           {
        		cruiseDomain.getModel().setReference(new File(inputFilePath).getName());
    			outputFileName += File.separator + cruiseDomain.getModel().getReference();
   			
           }
           return outputFileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
     */
    @Override
    public final void writeFile(String inputFilePath)
            throws GenerationException
    {
        try
        {
            stationDomain.updateRef();
            outputFileName = getOutputFileName(inputFilePath);
            if (RulesManager.getInstance().isProcessCollectionDir()){
            	Nemo.getInstance().getDomain().getCruise().getModel().setName(new File(inputFilePath).getName());
            	stationDomain.resetlocalCDIList();
            }
            if (isConversionTo_ODVMonostation)
            {
                // Conversion ODV - fichiers monostation => N fichiers de sortie
                Integer ref = 0;

                for (Station st : stationDomain.getStations(inputFilePath))
                {

                    if (isProcessType_CollectionDir)
                    {
                        new File(outputFileName).mkdir();
                    }

                    if (isInputType_Trajectory)
                    {
                        ref++;
                        st.setReference(ref.toString());
                    }

                    // FAE 6143
                        st=stationDomain.getAndCheckLocalCDI(cruiseDomain,
                            Conversion.ODV, st);
                     // WARNING: CDIResumeWriter stations list is also used for coupling table -> never delete this addstation line
                      CDIResumeWriter.getInstance().addStation(inputFilePath,st);
                    String mOutputFileName =
                        outputFileName + File.separator + st.getLocal_cdi_id()
                                + Conversion.ODV.getExt();
                    // outputFileWriter = new PrintWriter(new BufferedWriter(new
                    // FileWriter(mOutputFileName)));
                    outputFileWriter =
                        new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(
                                mOutputFileName), Global.NEMO_ENCODING)));

                    // écriture de l'entête campagne
                    List<Station> stList  = new ArrayList<Station>();
                    stList.add(st);
                    printCruiseHeader(stList);

                    if (isProcessType_CruiseFile)
                    {
                        iDomain.getStation().updateRef();
                    }
                    writeStation(st, inputFilePath, mOutputFileName);

                    outputFileWriter.close();
                }
            }
            else
            {
                // conversion ODV multistations => 1 fichier de sortie

                if (isProcessType_CollectionDir)
                {
                    outputFileName += Conversion.ODV.getExt();
                    iDomain.getStation().updateRef();
                }
                // outputFileWriter = new PrintWriter(new BufferedWriter(new
                // FileWriter(outputFileName)));
                outputFileWriter =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFileName),
                        Global.NEMO_ENCODING)));

                // écriture de l'entête campagne
                printCruiseHeader(stationDomain.getStations(inputFilePath));

                for (Station st : stationDomain.getStations(inputFilePath))
                {
                	
                    // FAE 6087 reinitialisation de la reference à lecture le
                    // fichier entree (car pour ecrire une station on lit tout
                    // le fichier)
                    // 07/12/2010: 6087 ajout du if CRUISE_FILE, sinon erreur
                    // dans le cas cruise dir
                    if (isProcessType_CruiseFile)
                    {
                        iDomain.getStation().updateRef();
                    }
                    
                  
                    st=stationDomain.getAndCheckLocalCDI(cruiseDomain,
                            Conversion.ODV, st);
                      CDIResumeWriter.getInstance().addStation(inputFilePath,st);
                      
                    writeStation(st, inputFilePath, outputFileName);
                }

                outputFileWriter.close();

            }
        }
        catch (Exception e)
        {
            throw new GenerationException(e);
        }

    }

    private void writeDataLine(StringBuffer stationHeader,
            Station currentStation, String line) throws Exception
    {
        DataDomain data = iDomain.getData();

        StringBuffer dataLine = new StringBuffer();
        if (indexRecord == 0)
        {
            dataLine.append(stationHeader);
        }
        else if (isInputType_Trajectory)
        {
            dataLine.append(iDomain.getStation().getOdvTrajectoryHeader(
                currentStation, line, indexRecord));
        }
        else
        {
            dataLine.append(iDomain.getStation().getOdvEmptyStationHeader());
        }
        dataLine.append(data.getDataLine(myConversion, currentStation, line,
            indexRecord));

        if (iDomain.getConversion().isSortData())
        {
            // préparation des données pour le tri
            Double key;
            if (isInputType_Profile)
            {
                ParameterModel keyParam = data.getModel().getParameterModels()[0];
                keyParam.computeParameterValue(line);
                key=Double.valueOf(keyParam.getLineValue().getValue());
            }
            else
            {
                key =Double.valueOf(DateTimeUtils.getCurrentDate(currentStation, indexRecord));
                // FAE 11356
                if (Nemo.getInstance().getDomain().getMedia().getModel()
                    .getInputType() == E_InputType.TIMESERIES
                        && Nemo.getInstance().getDomain().getStation()
                            .getModel().getProperty(Intern.S_DATE).isInData())
                    key = Double.valueOf(DateTimeUtils.convertTimestamp(line));
            }

            stationDatas.put(key, dataLine.toString());

        }
        else
        {
            // écriture directe
            outputFileWriter.println(dataLine);
        }
        indexRecord++;
    }

    private void writeStation(Station currentStation, String inputFilePath,
            String outputFileName) throws Exception
    {

        StationDomain station = iDomain.getStation();

        bufferedReader = getBufferReader(inputFilePath, currentStation);
        StringBuffer stationHeader = station.getOdvEmptyStationHeader();
        boolean stationWrited = false;
        indexRecord = 0;
        stationDatas.clear();

        // scan the InputFile line by line
        while ((readedLine = bufferedReader.readLine()) != null)
        {
            incrementLine();
            if (inFileHeader())
            {
                continue;
            }
            if (isStartStationHeader())
            {
                String[] inputStationHeader =
                    consumeStationHeader(mediaModel.getStationHeader() > 0);

                String stRef = null;
                // si inputStationHeader est null, c'est qu'on a terminé la
                // derniere station
                if (inputStationHeader != null)
                {
                    if (isInputType_Trajectory)
                        stRef = currentStation.getReference();
                    else
                        stRef =
                            iDomain.getStation().getReference(
                                currentStation.getFileName(),
                                inputStationHeader, true);
                }

//                if (inputStationHeader != null  && currentStation.getReference().equals(stRef)  )
                	if (inputStationHeader != null && currentStation.getStartLine()==numLine)//36265
                {

                    stationHeader =
                        iDomain.getStation().getHeader(myConversion,
                            currentStation, inputStationHeader);

                    // FAE 4673 Pb si une seule ligne données et pas d'entête
                    // station
                    if (!isEndData()
                    )
                    {
                        writeDataLine(stationHeader, currentStation, readedLine);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    // FAE 11978 - cas des fichiers ODV en entree
                    if (mediaModel.getEndData() == E_EndData.NEXT_START_WITH
                            && mediaModel.getStationHeader() == 0)
                    {
                        writeDataLine(stationHeader, currentStation, readedLine);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    stationWrited = true;
                }
                else if (stationWrited || inputStationHeader == null)
                {
                    break;
                }
            }
            else if (stationWrited)
            {
            	 if(readedLine.trim().isEmpty()){
    				 continue;
    			 }
                // ligne de données : à ne prendre en compte qua si on est sur
                // la bonne station
                writeDataLine(stationHeader, currentStation, readedLine);
                stationHeader = station.getOdvEmptyStationHeader();
            }
        }

        if (stationWrited)
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
            // enregistrement de la station
              iDomain.getMedia().addStationInOutput(outputFileName,
                      currentStation.getLocal_cdi_id()); 
        }

        numLine = 0;
    }

}
