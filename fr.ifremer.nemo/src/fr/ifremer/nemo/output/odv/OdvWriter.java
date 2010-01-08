package fr.ifremer.nemo.output.odv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.util.Conversion;
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
	public final void writeFile(String inputFilePath,
                                int readerSize) throws GenerationException {
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
                    
                    writeStation(st, readerSize);
                    
                    outputFileWriter.close();
                    iDomain.getMedia().addStationInOutput(mOutputFileName, st.getReference());
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
                    writeStation(st, readerSize);
                    iDomain.getMedia().addStationInOutput(outputFileName, st.getReference());
                }
                
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
        dataLine.append(stationHeader);
        dataLine.append(data.getDataLine(c, currentStation, line));
        
        outputFileWriter.println(dataLine);
    }
    
    private void writeStation(Station currentStation,
                              int readerSize) throws Exception{

        StationDomain station = iDomain.getCruise().getStation();
        bufferedReader.mark(readerSize + 1);

        StringBuffer stationHeader = station.getOdvEmptyStationHeader();
        boolean stationWrited = false;

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
                        writeDataLine(stationHeader, currentStation, inputStationHeader[inputStationHeader.length -1]);
                        stationHeader = station.getOdvEmptyStationHeader();
                    }
                    
                    writeDataLine(stationHeader, currentStation, readedLine);
                    stationHeader = station.getOdvEmptyStationHeader();
                    
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
        numLine = 0;
        bufferedReader.reset();
    }

}
