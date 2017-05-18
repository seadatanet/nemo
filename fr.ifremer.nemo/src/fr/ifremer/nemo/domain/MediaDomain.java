/* ============================================================================
 * Nom du fichier	: FileDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Global;

/**
 * Class FileDomain.java
 * Description: classe pour la description des données de l'onglet File
 * @author Delphine BOULANGER 
 */
public class MediaDomain
{
    /**
     * Informations descrivant l'onglet File
     */
    protected MediaModel _model;
    
    protected String _outputFileName;
    
    protected HashMap<String, List<String>> _inputFilesNames;
    
    protected HashMap<String, List<String>> _outputFiles;
    
    public MediaDomain(){
        initValues();
    }
    
    /*public MediaDomain(MediaModel model){
        initValues();
        update(model);
    }*/
    
    public void initValues(){
        _model = new MediaModel();
        _outputFileName = null;
        _inputFilesNames = null;
        _outputFiles = new HashMap<String, List<String>>();
    }
    
    public void initOutput(){
        _outputFileName = null;
        _outputFiles = new HashMap<String, List<String>>();        
    }
    
    public void update(InputModel model){
        updateMedia(model.getMediaModel());
    }
    
    public void updateMedia(MediaModel model){
        _model = model;
        
   /*     if (ViewMain.radioFichier.getSelection())
            if (ViewMain.nomFichier != null)
                _model.setSourcePath(ViewMain.nomFichier);
        else
            if (ViewMain.nomRepertoire != null)
                _model.setSourcePath(ViewMain.nomRepertoire);
       
        if (_model.getSourcePath() != null && !_model.getSourcePath().equals(Global.EMPTY_STRING))
            buildInputFilesNames();*/
        
    }
    
    
    /**
     * stockage des chemins et noms des fichiers d'entree
     * dans une map dont cle=repertoire, valeur=nom fichier
     *
     */
    public void buildInputFilesNames(){
        _inputFilesNames = new HashMap<String, List<String>>();
        // FAE1105 : tri des stations selon Nom Fichier - Date - Référence Station
        File inputFile = new File(_model.getSourcePath());
        List<String> names = new ArrayList<String>();
        if (inputFile.isDirectory()){
            names = new ArrayList<String>();
            for (File file : inputFile.listFiles()){
                if (file.isDirectory()){
                    for (File subfile : file.listFiles()){
                        names.add(subfile.getName());
                    }
                    Collections.sort(names);
                    _inputFilesNames.put(file.getPath(), names);
                    names = new ArrayList<String>();
                }
                else{
                    names.add(file.getName());
                }
            }
            if (!names.isEmpty()){
                Collections.sort(names);
                _inputFilesNames.put(inputFile.getPath(), names);
            }
            
        }
        else{
           
            names.add(inputFile.getName());
            _inputFilesNames.put(inputFile.getParentFile().getPath(), names);
        }
        
    }
    
    //RELEASE
    public void release(){
        _model.release();
    }

    /**
     * Getter pour model.
     * @return MediaModel
     * @see MediaDomain.java#model
     */
    public MediaModel getModel(){
        return _model;
    }

    /**
     * Setter pour model
     * @param model : nouvelle valeur pour model
     * @see MediaDomain.java#model
     */
    public void setModel(MediaModel model){
        _model = model;
    }

    /**
     * Getter pour _outputFileName.
     * @return String
     * @see MediaDomain.java#_outputFileName
     */
    public String getOutputFileName()
    {
        return _outputFileName;
    }

    /**
     * Setter pour _outputFileName
     * @param outputFileName : nouvelle valeur pour outputFileName
     * @see MediaDomain.java#_outputFileName
     */
    public void setOutputFileName(String outputFileName)
    {
        _outputFileName = outputFileName;
    }

    /**
     * Getter pour _inputFilesNames.
     * @return HashMap<String,List<String>>
     * @see MediaDomain.java#_inputFilesNames
     */
    public HashMap<String, List<String>> getInputFilesNames()
    {
        return _inputFilesNames;
    }
    
    /**
     * 
     * @param key: outputFileName
     * @param refStation: station reference
     */
    public void addStationInOutput(String key, String local_cdi_id){
        if (_outputFiles.containsKey(key)){
            _outputFiles.get(key).add(local_cdi_id);
        }
        else{
            List<String> stations = new ArrayList<String>();
            stations.add(local_cdi_id);
            _outputFiles.put(key, stations);
        }
    }

    /**
     * Getter pour _outputFiles.
     * @return HashMap<String,List<String>>
     * @see MediaDomain.java#_outputFiles
     */
    public HashMap<String, List<String>> getOutputFiles()
    {
        return _outputFiles;
    }
    
    public int getStationHeaderNumberLines(){
        // nombre lignes entete station
        String stationPattern = _model.getStationHeaderPattern();
        int numLine = 0;
        if (!stationPattern.equals(Global.EMPTY_STRING)){
            String[] texte = Utils.texteBrut.split("\n"); //$NON-NLS-1$
            for (numLine = 0; numLine < texte.length; numLine++){
                if (texte[numLine].trim().startsWith(stationPattern.trim())){
                    numLine++;
                    break;
                }
            }
            numLine -= _model.getFileHeader();
        }
        numLine+=_model.getStationHeader();
        return numLine;   
    }
    public boolean isColTitleInInputFile(){
    	return _model.isColTitlesInInputFile();
    }
    public int getColTitlesLineNb(){
    	return _model.getColTitlesInInputFilesLineNb();
    }
    public int getShellStationHeaderNumberLines() throws IOException{
        // nombre lignes entete station
        String stationPattern = _model.getStationHeaderPattern();
        int numLine = _model.getStationHeader();
        String rep = (String) _inputFilesNames.keySet().toArray()[0];
        String file = rep + "/" + _inputFilesNames.get(rep).get(0);
        BufferedReader br = new BufferedReader(new FileReader(new File(file)));
        if (!stationPattern.equals(Global.EMPTY_STRING)){
            String texte = br.readLine(); //$NON-NLS-1$
            numLine = 0;
            while (texte != null){
                if (texte.trim().startsWith(stationPattern.trim())){
                    numLine++;
                    break;
                }
                numLine++;
                texte = br.readLine();
            }
            numLine -= _model.getFileHeader();
        }
        return numLine;   
    }
    
}