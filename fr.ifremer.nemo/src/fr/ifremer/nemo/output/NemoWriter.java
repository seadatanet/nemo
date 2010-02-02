package fr.ifremer.nemo.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collections;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;


/**
 * 
 */
public abstract class NemoWriter{
	
	/**
	 * Input File Reader
	 */
	protected BufferedReader bufferedReader = null;
    //protected String inputFilePath;
    //protected int readerSize = 0;
    
	/**
	 * Ouput File Writer
	 */
	protected PrintWriter outputFileWriter = null;
	/**
	 * current readed line
	 */
	protected String readedLine = Global.EMPTY_STRING;
	/**
	 * current number of line
	 */
	protected int numLine = 0;
    
    protected final InputDomain iDomain;


	protected NemoWriter() {
        iDomain = Nemo.getInstance().getDomain();
	}
	
	/**
	 * @throws GenerationException
	 */
	public void write(ProgressNotification a_Progress) throws GenerationException{
        
        MediaDomain media = iDomain.getMedia();
        StationDomain stationDomain = iDomain.getCruise().getStation();
        stationDomain.updateRef();

		try {
            
            int readerSize = 0;         
            for (String inputFilePath : media.getInputFilesNames().keySet()){
                for (String inputFileName : media.getInputFilesNames().get(inputFilePath)){
                    
                    File inputFile= new File(inputFilePath + File.separator + inputFileName);
                    bufferedReader = new BufferedReader(new FileReader(inputFile));
                    readerSize = Long.valueOf(inputFile.length()).intValue();
                    bufferedReader.mark(readerSize + 1);

                    if (!stationDomain.stationIsRegistered(inputFilePath, inputFileName)){
                        // première lecture pour obtenir le nombre de valeurs par stations
                        Station st = null;
                        int nbrecords = 0;
            
                        while ((readedLine = bufferedReader.readLine()) != null) {
                            numLine++;
                            
                            if (numLine == 1){
                                consumeFileHeader();
                            }
            
                            
                            if (isEndData() && (st != null)){
                                st.setNbRecords(nbrecords);
                                stationDomain.addStation(inputFilePath, st);
                                st = null;
                            }
                            
                            if (isStartStationHeader()){
                                nbrecords = 0;

                                String[] inputStationHeader = consumeStationHeader();
                                if (inputStationHeader != null){
                                    st = new Station(inputFile.getName(),inputStationHeader);
                                    String ref = stationDomain.getReference(inputFile.getName(), inputStationHeader);
                                    st.setReference(ref);
                                    
                                    if (stationDomain.isSomeInData()){
                                        // la première ligne des données est déjà "consommée" dans stationHeaderBuffer 
                                        st.updateGlobalParamQc(inputStationHeader[inputStationHeader.length -1]);
                                        nbrecords++;
                                        
                                        // sampling rate peut etre à calculer
                                        if (stationDomain.getModel().getProperty(Intern.S_DATE).isInData()){
                                            st.calculSamplingRate(readedLine);
                                        }
                                    }
                                    st.updateGlobalParamQc(readedLine);
                                    nbrecords++;
                                }
                                else{
                                    break;
                                }
                            }
                            else if (st != null){
                                if (media.getModel().getInputType() == E_InputType.TRAJECTORY){
                                    st.completeMedatlasTrajectory(readedLine);
                                }
                                st.updateGlobalParamQc(readedLine);
                                nbrecords++;
                            }
                        }
                        
                        if (st != null){
                            // endData = EOF : la station n'a pas été enregistrée
                            st.setNbRecords(nbrecords);
                            stationDomain.addStation(inputFilePath, st);
                        }

                    }
                    
                    // FAE1105 : tri des stations selon Nom Fichier - Date - Référence Station
                    Collections.sort(stationDomain.getStations(inputFilePath));
                    
                    numLine = 0;
                    bufferedReader.reset();
                    
                }
                bufferedReader.close();
                
                writeFile(inputFilePath);   
            }

		} catch (Exception e) {
            stationDomain.resetStations();
            e.printStackTrace();
			throw new GenerationException(e);

		} finally {
            numLine = 0;
			try {
				if (bufferedReader != null)
					bufferedReader.close();
				if (outputFileWriter != null)
					outputFileWriter.close();
			} catch (IOException ioe) {
				throw new GenerationException(ioe);
			}
		}
		
	}

	/**
	 * adapted writing processing
	 * @throws SplitterException
	 * @throws IOException
	 * @throws ParseException
	 */
	public abstract void writeFile(String inputFilePath) 
           throws Exception;
    
    /**
     * place le Reader après l'entête de fichier
     * @return StringBuffer (fileHeader)
     * @throws IOException 
     */
    protected String[] consumeFileHeader() throws IOException{
        StringBuffer fileHeader = new StringBuffer();
        int nbLignes = iDomain.getMedia().getModel().getFileHeader();

        for (int i = 0; i < nbLignes; i++){
            fileHeader.append(consumeLine());
        }
        return fileHeader.toString().split(Global.RN);
    }
    
    /**
     * 
     * @return
     */
    protected boolean isStartStationHeader(){
        boolean start = false;
        MediaModel media = iDomain.getMedia().getModel();
        int nbLignesFileHeader = media.getFileHeader();
        if (numLine == (nbLignesFileHeader + 1)){
            start = true;
        }
        else if (media.getProcessType() == E_ProcessType.CRUISE_FILE || media.isMultiStations()){
            start = isEndData();
        }
        return start;
    }
    
    protected boolean isEndData(){
        MediaModel media = iDomain.getMedia().getModel();
        
        boolean end = false;
        switch (media.getEndData()){
            case NEXT_START_WITH :
            case END_WITH :
                end = readedLine.startsWith(media.getEndDataPattern());
                break;
            case EMPTY_LINE :
                end = readedLine.trim().matches("^$"); //$NON-NLS-1$
                break;
            default :
                end = false;
                break;
        }
        return end;
    }
    
    
    /**
     * 
     * @return
     * @throws IOException
     */
    protected String[] consumeStationHeader() throws IOException{
        
        MediaModel media = iDomain.getMedia().getModel();
        StringBuffer stationHeader = new StringBuffer();
        
        if (numLine > (media.getFileHeader() + 1)){
            // consommation de la ligne de fin de données
            if (media.getEndData() != E_EndData.NEXT_START_WITH){
                consumeLine();
            }
            
            // consommation des lignes séparant les stations
            for (int i = 0; i < media.getEndStation(); i++){
                if (readedLine != null)
                    consumeLine();
            }
        }
        
        // récupération des lignes de l'entête station
        String pattern = media.getStationHeaderPattern();
        if (!pattern.equals(Global.EMPTY_STRING)){
            while (readedLine != null && !readedLine.startsWith(pattern)){
                // récupération de toutes les lignes avant "pattern"
                stationHeader.append(consumeLine());
            }
            // récupération de la ligne commencant par "pattern"
            stationHeader.append(consumeLine());
        }
        for (int i = 0; i < media.getStationHeader(); i++){
            if (readedLine != null)
                stationHeader.append(consumeLine());
        }
        
        // + 1 ligne si certaines informations station sont dans les données
        if (iDomain.getCruise().getStation().isSomeInData() && readedLine != null){
            stationHeader.append(consumeLine());
        }
        
        if (readedLine == null)
            return null;
        
        return stationHeader.toString().split(Global.RN);
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
    private StringBuffer consumeLine() throws IOException{
        StringBuffer buffer = new StringBuffer();
        buffer.append(readedLine);
        buffer.append(Global.RN);
        
        readedLine = bufferedReader.readLine();
        numLine++;
        return buffer;
    }
}
