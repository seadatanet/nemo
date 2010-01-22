package fr.ifremer.nemo.output.medatlas;

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
import fr.ifremer.nemo.util.presdepth.CalcPres;


/**
 * specific NemoWriter : MEDATLAS
 * 
 */
public class MedatlasWriter extends NemoWriter{

    Conversion c = Conversion.MEDATLAS;
    
	public MedatlasWriter() {
        super();
	}

	/* (non-Javadoc)
	 * @see fr.ifremer.nemo.output.NemoWriter#writeFile()
	 */
	@Override
	public final void writeFile(String inputFilePath) throws GenerationException {
        try{
            String outputFileName = iDomain.getMedia().getOutputFileName();
            outputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
            
            CruiseDomain cruise = iDomain.getCruise();
            StationDomain station = cruise.getStation();
            DataDomain data = station.getData();
            
    
            // écriture de l'entête campagne
            StringBuffer cruiseHeader = cruise.getHeader(c);
            outputFileWriter.println(cruiseHeader);
            
            station.updateRef();
            
            
            for (Station currentStation : station.getStations(inputFilePath)){
                
                CalcPres.init();
                
                File inputFile= new File(inputFilePath + File.separator + currentStation.getFileName());
                bufferedReader = new BufferedReader(new FileReader(inputFile));
                //readerSize = Long.valueOf(inputFile.length()).intValue();
                
                int indexRecord = 0;
    
                //bufferedReader.mark(readerSize + 1);
                boolean stationWrited = false;
        		
        		// scan the InputFile line by line
        		while ((readedLine = bufferedReader.readLine()) != null) {
        			numLine++;
                    
                    if (numLine == 1){
                        consumeFileHeader();
                    }
                    
                    if (isEndData() && stationWrited){
                        // écriture de la dernière ligne de données
                        outputFileWriter.println(data.getMedatlasLastDataLine());
                        iDomain.getMedia().addStationInOutput(outputFileName, currentStation.getReference());
                    }
                    
                    if (isStartStationHeader()){
                        // écriture de l'entête station
                        String[] inputStationHeader = consumeStationHeader();
                        if (inputStationHeader != null &&
                                currentStation.getReference().equals(station.getReference(currentStation.getFileName(), inputStationHeader))){
                            outputFileWriter.println(station.getHeader(c, currentStation, inputStationHeader));
            
                            // écriture de l'entête des données (titres des colonnes)
                            outputFileWriter.println(data.getDataHeader(c));
                            
                            if (station.isSomeInData()){
                                // la première ligne des données est déjà "consommée" dans stationHeaderBuffer 
                                outputFileWriter.println(data.getDataLine(c, currentStation, inputStationHeader[inputStationHeader.length -1], indexRecord));
                                indexRecord++;
                            }
                            outputFileWriter.println(data.getDataLine(c, currentStation, readedLine, indexRecord));
                            stationWrited = true;
                        }
                        else if (stationWrited || inputStationHeader == null){
                            break;
                        }
                    }
                    else if (stationWrited){
                        // écriture de la ligne de données
                        indexRecord++;
                        outputFileWriter.println(data.getDataLine(c, currentStation, readedLine, indexRecord));
                    }
        		}
                
                if (iDomain.getMedia().getModel().getEndData() == E_EndData.EOF && stationWrited){
                    // écriture de la dernière ligne de données
                    outputFileWriter.println(data.getMedatlasLastDataLine());
                    iDomain.getMedia().addStationInOutput(outputFileName, currentStation.getReference());
                }
                
                numLine = 0;
                //bufferedReader.reset();
                
            }
        } catch (Exception e){
            throw new GenerationException(e);
        }
	}
}
