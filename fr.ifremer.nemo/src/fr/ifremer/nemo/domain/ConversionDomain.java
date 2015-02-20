/* ============================================================================
 * Nom du fichier	: ConversionDomain.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date	de création : 30 nov. 09
 * Client			: Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.File;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.ConversionModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

/**
 * Class ConversionDomain.java
 * Description: classe pour la description des données de l'onglet conversion
 * @author Delphine BOULANGER 
 */
public class ConversionDomain
{
	/**
	 * Informations descrivant l'onglet Conversion
	 */
	protected ConversionModel _model;

	/**
	 * Conversion courrante
	 */
	protected E_TypeConversion _current;

	/**
	 * indicateur de tri des données en sortie
	 */
	protected boolean _sortData;

	/**
	 * pour mapping
	 */
	protected String couplingTablePrefix;

	public ConversionDomain(){
		initValues();
	}

	public void initValues(){
		_model = new ConversionModel();
		_current = E_TypeConversion.UNKNOWN;
	}

	public String getCouplingTablePrefix() {
		return couplingTablePrefix;
	}

	public void setCouplingTablePrefix(String couplingTablePrefix) {
		this.couplingTablePrefix = couplingTablePrefix;
	}

	public void update(InputModel model){
		updateConversion(model.getConversionModel());
	}

	public void updateConversion(ConversionModel model){
		_model = model;
	}

	//RELEASE
	public void release(){
		_model.release();
	}

	/**
	 * Getter pour model.
	 * @return ConversionModel
	 * @see ConversionDomain.java#model
	 */
	public ConversionModel getModel(){
		return _model;
	}

	/**
	 * Setter pour model
	 * @param model : nouvelle valeur pour model
	 * @see ConversionDomain.java#model
	 */
	public void setModel(ConversionModel model){
		_model = model;
	}

	/**
	 * Getter pour _currentConversion.
	 * @return E_TypeConversion
	 * @see ConversionDomain.java#_currentConversion
	 */
	public E_TypeConversion getCurrent()
	{
		return _current;
	}

	/**
	 * Setter pour _currentConversion
	 * @param currentConversion : nouvelle valeur pour currentConversion
	 * @see ConversionDomain.java#_currentConversion
	 */
	public void setCurrentConversion(E_TypeConversion currentConversion)
	{
		_current = currentConversion;
	}



	/**
	 * 
	 * @return path to put in the coupling table, computed from the absolute output filePath and the couplingTablePrefix
	 */
	
	/**
	 * Getter pour _sortData.
	 * @return boolean
	 * @see ConversionDomain.java#_sortData
	 */
	public boolean isSortData()
	{
		return _sortData;
	}

	/**
	 * Setter pour _sortData
	 * @param sortData : nouvelle valeur pour sortData
	 * @see ConversionDomain.java#_sortData
	 */
	public void setSortData(boolean sortData)
	{
		_sortData = sortData;
	}

	/**
	 * 
	 * @param couplingPrefix
	 * @param outputFileAbsolutePath
	 * @return prefix, fixed if neccessary, or null if given prefix is inconsistent with outputFileAbsolutePath
	 */
	public String getValidPrefix(String couplingPrefix, String outputFileAbsolutePath) {
		
		String validPrefix=null;
		if (couplingPrefix.equals(File.separator)){
			validPrefix="";
		}
		if( outputFileAbsolutePath.startsWith(couplingPrefix) || couplingPrefix.isEmpty()){
			validPrefix= couplingPrefix;
		}
		
//		System.out.println("couplingPrefix=" + couplingPrefix);
//		System.out.println("validPrefix=" + validPrefix);
		return validPrefix;
	}
	public String getFileNameInCoupling(String absolutePath)
	{
		String couplingFilePath="";
		if (couplingTablePrefix.isEmpty()){
			couplingFilePath= absolutePath;
		}else if (couplingTablePrefix.endsWith(File.separator)) {
			couplingFilePath= absolutePath.replace(couplingTablePrefix,Global.EMPTY_STRING);
		} else{
			couplingFilePath= absolutePath.replace(couplingTablePrefix + File.separator,Global.EMPTY_STRING);
		}
//		System.out.println(couplingFilePath);
		return couplingFilePath;

	}
	public String getOutputPath() {
		File output =new File(Nemo.getInstance().getDomain().getMedia().getOutputFileName());
		String outputPath = output.getParentFile().getPath();
		//FAE 17128
		if (RulesManager.getInstance().isConversionToNetCDFMonoStation()
				|| RulesManager.getInstance().isConversionToODVMonoStation()
				|| Nemo.getInstance().getDomain().getMedia().getModel().getProcessType() == E_ProcessType.COLLECTION_DIR) {
			outputPath = output.getAbsolutePath();
		}
		return outputPath;
	} 
}
