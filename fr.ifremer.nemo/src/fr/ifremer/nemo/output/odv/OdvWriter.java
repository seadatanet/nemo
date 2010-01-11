package fr.ifremer.nemo.output.odv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;


/**
 * specific NemoWriter : ODV
 * 
 */
public class OdvWriter extends NemoWriter{
	
    Conversion c = Conversion.ODV;

	public OdvWriter() {
        
	}

	/* (non-Javadoc)
	 * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
	 */
	@Override
	public final void writeFile(String inputFilePath) throws GenerationException {
	    try{
            CruiseDomain cruise = iDomain.getCruise();
            StationDomain station = cruise.getStation();
            
            String outputFileName = iDomain.getMedia().getOutputFileName();
            E_ProcessType processType = iDomain.getMedia().getModel().getProcessType();
            if (processType == E_ProcessType.COLLECTION_DIR)
                outputFileName += File.separator + cruise.getModel().getReference();
            
    
            E_TypeConversion selectedConversion = iDomain.getConversion().getCurrent();
            if (selectedConversion == E_TypeConversion.ODV_MONOSTATION){
                // Conversion ODV - fichiers monostation => N fichiers de sortie
                
                for (Station st : station.getStations(inputFilePath)){
                    
                    String localCdiId = station.getLocalCdiId(cruise, Conversion.ODV, st.getReference());
                    String mOutputFileName = outputFileName + File.separator + localCdiId + Conversion.ODV.getExt();
                    outputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(mOutputFileName)));
    
                    // écriture de l'entête campagne
                    StringBuffer cruiseHeader = cruise.getHeader(c);
                    outputFileWriter.println(cruiseHeader);
                    
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
                    writeStation(st, inputFilePath, outputFileName);
                }
                
            }
        } catch (Exception e){
            throw new GenerationException(e);
        }

	}
    
    private void writeDataLine(StringBuffer stationHeader,
                               Station currentStation,
                               String line,
                               int indexRecord) throws Exception{
        DataDomain data = iDomain.getCruise().getStation().getData();

        StringBuffer dataLine = new StringBuffer();
        dataLine.append(stationHeader);
        dataLine.append(data.getDataLine(c, currentStation, line, indexRecord));
        
        outputFileWriter.println(dataLine);
    }
    
    private void writeStation(Station currentStation,
                              String inputFilePath,
                              String outputFileName) throws Exception{

        StationDomain station = iDomain.getCruise().getStation();
        File inputFile= new File(inputFilePath + File.separator + currentStation.getFileName());
        bufferedReader = new BufferedReader(new FileReader(inputFile));
        //bufferedReader.mark(readerSize + 1);

        StringBuffer stationHeader = station.getOdvEmptyStationHeader();
        boolean stationWrited = false;
        int indexRecord = 0;

        // scan the InputFile line by line
        while ((readedLine = bufferedReader.readLine()) != null) {
            numLine++;
            
            if (numLine == 1){
                consumeFileHeader();
            }

            if (isStartStationHeader()){
                String[] inputStationHeader = consumeStationHeader();
                if (inputStationHeader != null &&
                        currentStation.getReference().equals(station.getReference(currentStation.getFileName(), inputStationHeader))){

                    stationHeader = station.getHeader(iDomain.getCruise(), c, currentStation, inputStationHeader);
                    
                    if (station.isSomeInData()){
                        // la première ligne des données est déjà "consommée" dans stationHeaderBuffer 
                        writeDataLine(stationHeader, currentStation, inputStationHeader[inputStationHeader.length -1], indexRecord);
                        indexRecord++;
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    
                    writeDataLine(stationHeader, currentStation, readedLine, indexRecord);
                    stationHeader = station.getOdvEmptyStationHeader();
                    
                    stationWrited = true;
                }
                else if (stationWrited  || inputStationHeader == null){
                    break;
                }
            }
            else if (stationWrited){        
                // ligne de données : à ne prendre en compte qua si on est sur la bonne station
                indexRecord++;
                writeDataLine(stationHeader, currentStation, readedLine, indexRecord);
                stationHeader = station.getOdvEmptyStationHeader();
            }
        }
        
        if (iDomain.getMedia().getModel().getEndData() == E_EndData.EOF && stationWrited){
            // écriture de la dernière ligne de données
            iDomain.getMedia().addStationInOutput(outputFileName, currentStation.getReference());
        }
        
        numLine = 0;
        //bufferedReader.reset();
    }

}
