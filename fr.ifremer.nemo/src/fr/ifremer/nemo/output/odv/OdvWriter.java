package fr.ifremer.nemo.output.odv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;


/**
 * specific NemoWriter : ODV
 * 
 */
public class OdvWriter extends NemoWriter{
	
    Conversion c = Conversion.ODV;
    HashMap<Double, String> stationDatas;
    int indexRecord;

	public OdvWriter() {
        super();
        stationDatas = new HashMap<Double, String>();
	}

	/* (non-Javadoc)
	 * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
	 */
	@Override
	public final void writeFile(String inputFilePath) throws GenerationException {
	    try{
            CruiseDomain cruise = iDomain.getCruise();
            StationDomain station = cruise.getStation();
            
            station.updateRef();
            
            String outputFileName = iDomain.getMedia().getOutputFileName();
            E_ProcessType processType = iDomain.getMedia().getModel().getProcessType();
            if (processType == E_ProcessType.COLLECTION_DIR){
                cruise.setDataSetRef(new File(inputFilePath).getName());
                outputFileName += File.separator + cruise.getDataSetRef();
            }
            
    
            E_TypeConversion selectedConversion = iDomain.getConversion().getCurrent();
            if (selectedConversion == E_TypeConversion.ODV_MONOSTATION){
                // Conversion ODV - fichiers monostation => N fichiers de sortie
                
                for (Station st : station.getStations(inputFilePath)){
                    
                    if (processType == E_ProcessType.COLLECTION_DIR){
                        new File(outputFileName).mkdir();
                    }
                    
                    String localCdiId = station.getLocalCdiId(cruise, Conversion.ODV, st.getReference());
                    String mOutputFileName = outputFileName + File.separator + localCdiId + Conversion.ODV.getExt();
                    outputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(mOutputFileName)));
    
                    // écriture de l'entête campagne
                    StringBuffer cruiseHeader = cruise.getHeader(c);
                    outputFileWriter.println(cruiseHeader);
                    
                    //FAE 6087 reinitialisation de la reference à chaque fichier
                    iDomain.getCruise().getStation().updateRef();
                    
                    writeStation(st, inputFilePath, mOutputFileName);
                    
                    outputFileWriter.close();
                }
            }
            else{
                // conversion ODV multistations => 1 fichier de sortie
                
                if (processType == E_ProcessType.COLLECTION_DIR)
                    outputFileName += Conversion.ODV.getExt();
                outputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
                
                // écriture de l'entête campagne
                StringBuffer cruiseHeader = cruise.getHeader(c);
                outputFileWriter.println(cruiseHeader);
    
                for (Station st : station.getStations(inputFilePath)){
                    //FAE 6087 reinitialisation de la reference à lecture fichier entree (car pour ecrire une station on lit tout le fichier)
                    iDomain.getCruise().getStation().updateRef();
                    writeStation(st, inputFilePath, outputFileName);
                }
                
                outputFileWriter.close();
                
            }
        } catch (Exception e){
            throw new GenerationException(e);
        }

	}
    
    private void writeDataLine(StringBuffer stationHeader,
                               Station currentStation,
                               String line) throws Exception{
        DataDomain data = iDomain.getCruise().getStation().getData();

        StringBuffer dataLine = new StringBuffer();
        if (indexRecord == 0){
            dataLine.append(stationHeader);
        }
        else if(iDomain.getMedia().getModel().getInputType() == E_InputType.TRAJECTORY){
            dataLine.append(iDomain.getCruise().getStation().getOdvTrajectoryHeader(currentStation, line, indexRecord));
        }
        else{
            dataLine.append(iDomain.getCruise().getStation().getOdvEmptyStationHeader());
        }
        dataLine.append(data.getDataLine(c, currentStation, line, indexRecord));
        
        if (iDomain.getConversion().isSortData()){
            // préparation des données pour le tri
            Double key = Double.NaN;
            if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
                ParameterModel keyParam = data.getModel().getParameterModels()[0];
                keyParam.computeParameterValue(line);
                key = keyParam.getLineValue().getValue();
            }else{
                key = Long.valueOf(DateTimeUtils.getCurrentDate(currentStation, indexRecord)).doubleValue();
            }
            
            stationDatas.put(key, dataLine.toString());
            
        }else{
            // écriture directe
            outputFileWriter.println(dataLine);
        }
        indexRecord++;
    }
    
    private void writeStation(Station currentStation,
                              String inputFilePath,
                              String outputFileName) throws Exception{

        StationDomain station = iDomain.getCruise().getStation();
        File inputFile= new File(inputFilePath + File.separator + currentStation.getFileName());
        bufferedReader = new BufferedReader(new FileReader(inputFile));

        StringBuffer stationHeader = station.getOdvEmptyStationHeader();
        boolean stationWrited = false;
        indexRecord = 0;      
        stationDatas.clear();

        // scan the InputFile line by line
        while ((readedLine = bufferedReader.readLine()) != null) {
            numLine++;
            
            if (numLine == 1){
                consumeFileHeader();
            }

            if (isStartStationHeader()){
                String[] inputStationHeader = consumeStationHeader();
                
                String stRef=null;
                // si inputStationHeader est null, c'est qu'on a terminé la derniere station
                if(inputStationHeader != null)
                {
                    stRef = iDomain.getCruise().getStation().getReference(currentStation.getFileName(), inputStationHeader);
                }
               
                if (inputStationHeader != null && currentStation.getReference().equals(stRef)){
                   
                    stationHeader =  iDomain.getCruise().getStation().getHeader(c, currentStation, inputStationHeader);
                    
                    if (station.isSomeInData()){
                        // la première ligne des données est déjà "consommée" dans stationHeaderBuffer 
                        writeDataLine(stationHeader, currentStation, inputStationHeader[inputStationHeader.length -1]);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    
                    // FAE 4673 Pb si une seule ligne données et pas d'entête station
                    if (!isEndData())
                    {
                        writeDataLine(stationHeader, currentStation, readedLine);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    
                    stationWrited = true;
                }
                else if (stationWrited  || inputStationHeader == null){
                    break;
                }
            }
            else if (stationWrited){        
                // ligne de données : à ne prendre en compte qua si on est sur la bonne station
                writeDataLine(stationHeader, currentStation, readedLine);
                stationHeader = station.getOdvEmptyStationHeader();
            }
        }
        
        if (stationWrited){
            if (iDomain.getConversion().isSortData()){
                // écriture des données après tri
                List<Double> keys = new ArrayList<Double>();
                keys.addAll(stationDatas.keySet());
                Collections.sort(keys);
                for (Double key : keys){
                    outputFileWriter.println(stationDatas.get(key));
                }
            }    
            // enregistrement de la station
            iDomain.getMedia().addStationInOutput(outputFileName, currentStation.getReference());
        }
        
        numLine = 0;
    }

}
