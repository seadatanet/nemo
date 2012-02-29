/*
 * ============================================================================
 * Nom du fichier : DataDomain.java
 * Application : NEMO version 1.2.20
 * Auteur : Delphine BOULANGER
 * Date de création : 30 nov. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.MissingResourceException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;

/**
 * Class DataDomain.java Description: Classe pour la description des données de
 * l'onglet Data
 * 
 * @author Delphine BOULANGER
 */
public class DataDomain {

	private enum URNtype {
		subject, object, units;
	}

	/**
	 * Informations descrivant l'onglet Data
	 */
	protected DataModel _model;

	/**
	 * reference sur l'objet gérant les paramètres (règles de
	 * recherche et d'ajout en fonction des param types...
	 */
	private ParameterManager myParameterManager;

	public DataDomain() {
		initValues();
	}

	public void initValues() {
		_model = new DataModel();

		myParameterManager = new ParameterManager(_model);
	}

	public void update(InputModel model) {
		updateData(model.getDataModel());
	}

	public void updateData(DataModel model) {
		_model = model;
		myParameterManager.setAddedParameterModel();
	}

	public StringBuffer getDataHeader(Conversion c) {
		StringBuffer dataHeader = new StringBuffer();

		if (c == Conversion.MEDATLAS)
			dataHeader.append(c.getDebut());

		int nbParamTot = myParameterManager.getAddedParameterModels(c).length + _model.getParameterModels().length;
		int cptParam = 0;

		// paramètres ajoutés
		for (ParameterModel pModel : myParameterManager.getAddedParameterModels(c)) {
			cptParam++;
			Parameter param = pModel.getParameter();
			dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot)));
		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			cptParam++;
			Parameter param = pModel.getParameter();
			dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot)));
		}
		return dataHeader;
	}

	public StringBuffer getDataLine(Conversion c, Station currentStation, String inputLine, int indexRecord) throws Exception {
		StringBuffer dataLine = new StringBuffer();
		switch (c) {
		case ODV:
			dataLine.append(getOdvDataLine(currentStation, inputLine, indexRecord));
			break;
		case MEDATLAS:
			dataLine.append(getMedatlasDataLine(currentStation, inputLine, indexRecord));
			break;
		}
		return dataLine;
	}

	private StringBuffer getOdvDataLine(Station currentStation, String inputLine, int indexRecord) throws Exception {

		StringBuffer odvDataLine = new StringBuffer();

		int nbParamTot = myParameterManager.getOdvParameterModels().length + _model.getParameterModels().length;
		int cptParam = 0;

		// paramètres ajoutés
		long currentDate = DateTimeUtils.getCurrentDate(currentStation, indexRecord);
		//FAE 11356
		if(Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES 
		        && Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData())
		    currentDate = Long.valueOf(DateTimeUtils.convertTimestamp(inputLine));
		for (ParameterModel pModel : myParameterManager.getOdvParameterModels()) {
			cptParam++;
			pModel.computeAddedParameterValue(currentStation, inputLine, currentDate);

			String defaultValue = pModel.getParameter().getDefault();
			String stringValue = pModel.getLineValue().getValue();

			// odvDataLine.append(Global.TAB);
			if (!stringValue.equals(defaultValue))
				odvDataLine.append(stringValue);
			odvDataLine.append(Global.TAB);
			odvDataLine.append(pModel.getLineValue().getQc());

			if (cptParam < nbParamTot)
				odvDataLine.append(Global.TAB);
		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			cptParam++;
			pModel.computeParameterValue(inputLine);

			String defaultValue = pModel.getParameter().getDefault();
			String stringValue = Formatter.getSingleton().formatParameter(pModel.getParameter().getFormat(), pModel.getLineValue().getValue());

			// odvDataLine.append(Global.TAB);
			if (!stringValue.equals(defaultValue))
				odvDataLine.append(stringValue);
			odvDataLine.append(Global.TAB);
			odvDataLine.append(pModel.getLineValue().getQc());

			if (cptParam < nbParamTot)
				odvDataLine.append(Global.TAB);
		}
		// odvDataLine.append(Global.RN);
		return odvDataLine;
	}

	private StringBuffer getMedatlasDataLine(Station currentStation, String inputLine, int indexRecord) throws Exception {

		StringBuffer medatlasDataLine = new StringBuffer();

		// -- VALEURS
		// paramètres ajoutés
		long currentDate = 0;
		if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.PROFILE)
			currentDate = DateTimeUtils.getCurrentDate(currentStation, indexRecord);
		
		//FAE 11356
		if(Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES 
		        && Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData())
            currentDate = Long.valueOf(DateTimeUtils.convertTimestamp(inputLine));
		for (ParameterModel pModel : myParameterManager.getMedParameterModels()) {
			pModel.computeAddedParameterValue(currentStation, inputLine, currentDate);

			medatlasDataLine.append(pModel.getLineValue().getValue());
			medatlasDataLine.append(Global.SPACE);
		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			pModel.computeParameterValue(inputLine);

			medatlasDataLine.append(Formatter.getSingleton().formatParameter(pModel.getParameter().getFormat(), pModel.getLineValue().getValue()));
			medatlasDataLine.append(Global.SPACE);
		}

		// -- FLAGS QUALITE
		// paramètres ajoutés
		for (ParameterModel pModel : myParameterManager.getMedParameterModels()) {
			medatlasDataLine.append(pModel.getLineValue().getQc());
		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			medatlasDataLine.append(pModel.getLineValue().getQc());
		}

		return medatlasDataLine;
	}

	public StringBuffer getMedatlasLastDataLine() {
		StringBuffer medLastDataLine = new StringBuffer();

		int cptParam = 0;

		// paramètres ajoutés
		for (ParameterModel pModel : myParameterManager.getMedParameterModels()) {
			Parameter param = pModel.getParameter();
			medLastDataLine.append(param.getDefault());
			medLastDataLine.append(Global.SPACE);
			cptParam++;
		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			Parameter param = pModel.getParameter();
			medLastDataLine.append(param.getDefault());
			medLastDataLine.append(Global.SPACE);
			cptParam++;
		}

		// ajouts des qcFlags default
		for (int i = 0; i < cptParam; i++) {
			medLastDataLine.append(Global.DEFAULT_FLAG);
		}

		return medLastDataLine;
	}

	public StringBuffer getMedParametersDesc() throws ParseException, UnsupportedEncodingException {
		StringBuffer paramsDesc = new StringBuffer();

		for (ParameterModel pModel : myParameterManager.getMedParameterModels()) {
			paramsDesc.append(Conversion.MEDATLAS.getDebut());
			paramsDesc.append(getMedParameterDesc(pModel));
		}

		for (ParameterModel pModel : _model.getParameterModels()) {
			paramsDesc.append(Conversion.MEDATLAS.getDebut());
			paramsDesc.append(getMedParameterDesc(pModel));
		}
		return paramsDesc;
	}

	private StringBuffer getMedParameterDesc(ParameterModel pModel) throws ParseException, UnsupportedEncodingException {
		// *PRES SEA PRESSURE sea surface=0 (decibar=10000 pascals ) def.=-999.9
		Parameter param = pModel.getParameter();
		StringBuffer paramDesc = new StringBuffer();

		paramDesc.append(Elts.getElt(Elts.param_code, param.getCode()));
		paramDesc.append(Global.SPACE);
		paramDesc.append(Elts.getElt(Elts.param_fullname, param.getFullName()));
		paramDesc.append(Elts.getElt(Elts.param_unit, param.getUnit()));
		paramDesc.append(Elts.getElt(Elts.param_default, param.getDefault()));
		paramDesc.append(Global.RN);

		return paramDesc;
	}

	// RELEASE
	public void release() {
		_model.release();
		myParameterManager.releaseAddedParameterModels();
	}

	public StringBuffer getSDNparameters(Conversion c) {
		StringBuffer sdnParams = new StringBuffer();

		sdnParams.append(c.getDebut());
		sdnParams.append(Intern.file_mapping);
		sdnParams.append(Global.RN);

		/**
		 * added parameters
		 */
		if (c.equals(Conversion.ODV)) {
			for (ParameterModel param : myParameterManager.getAddedParameterModels(c)) {
				param.getParameter().setP011(_model.getParameterModels()[0].getParameter().isP011());
			}
		} else {
			for (ParameterModel param : myParameterManager.getAddedParameterModels(c)) {
				param.getParameter().setP011(false);
			}
		}

		for (ParameterModel param : myParameterManager.getAddedParameterModels(c)) {
			sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c));
		}

		/**
		 * parameters
		 */
		for (ParameterModel param : _model.getParameterModels()) {
			sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c));
		}

		return sdnParams;
	}

	private StringBuffer parameterMap(String start, Parameter param, Conversion c) {
		String code = param.getCode();
		StringBuffer sdnMapping = new StringBuffer();
		sdnMapping.append(start);

		/**
		 * BGT 6838: bug introduit parce que j'avais enlevé le try/catch ci
		 * dessous
		 * en fait, on essaie de trouver le code dans la liste Intern
		 * si c'est un addedParameter, on va le trouver, et on ajoute la valeur
		 * de la variable Inter.code (qui contient TOUTE la ligne à ajouter)
		 * sinon, on fabrique la ligne
		 */
		try {
			sdnMapping.append(Intern.getString(code));
		} catch (MissingResourceException e) {
			/**
			 * -----------------------------------------------------
			 * subject
			 */
			// FAE 4045 getLocalCode
			sdnMapping.append(getURNSyntaxe(URNtype.subject, param.getLocalCode(), null, null));
			/**
			 * -----------------------------------------------------
			 * object
			 * 
			 * si param P011, on met le code P011
			 * si param P091, on met le premier P011 de la liste de P011
			 * correspondants au P091
			 */
			if (param.isP011()) {
				sdnMapping.append(getURNSyntaxe(URNtype.object, param.getCodeP011(), null, Intern.mapping_objet));
			} else {
				sdnMapping.append(getURNSyntaxe(URNtype.object, param.getCode(), Intern.mapping_subject, Intern.mapping_objet));
			}

			/**
			 * -----------------------------------------------------
			 * units
			 * 
			 * unitCode utilisé seulement pour les parametres P011 choisis
			 * depuis P021
			 * (qui n'ont pas d'unité, et dont l'unité est donc choisie
			 * manuellement
			 * par l'utilisateur depuis la liste P061)
			 * 
			 * 
			 * s'il est vide, on va chercher l'unité correspondant au P091
			 * choisi ou ayant servi à choisir le P021
			 * sinon, on prend sa valeur qui a été renseignée quen l'utilisateur
			 * a choisi l'unité dans la liste des P061
			 */
			if (param.getUnitCode() == null || param.getUnitCode().equals(Global.EMPTY_STRING)) {
				sdnMapping.append(getURNSyntaxe(URNtype.units, param.getCode(), Intern.mapping_subject, Intern.mapping_unit));
			} else {
				// FAE 6542
				sdnMapping.append(getURNSyntaxe(URNtype.units, param.getUnitCode(), null, Intern.mapping_unit));
			}
		}
		sdnMapping.append(Global.RN);
		return sdnMapping;
	}

	private static StringBuffer getURNSyntaxe(URNtype type, String code, String list1, String list2) {
		StringBuffer urnSyntaxe = new StringBuffer();

		urnSyntaxe.append(Intern.xml_tag_start);
		urnSyntaxe.append(type.toString());
		urnSyntaxe.append(Intern.xml_tag_end);

		urnSyntaxe.append(Intern.file_mapping_sdn);
		urnSyntaxe.append(Global.DBL_PT);

		switch (type) {
		case subject:
			urnSyntaxe.append(Intern.file_mapping_subject);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(code);
			break;
		case object:
		case units:
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCode;
			if (list1 != null)
				pdvCode = fr.ifremer.nemo.util.Utils.extraitCodeURN(NemoLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown));
			else
				pdvCode = code;
			urnSyntaxe.append(pdvCode);
			break;
		}

		urnSyntaxe.append(Intern.xml_tag_start);
		urnSyntaxe.append(Intern.xml_tag_close);
		urnSyntaxe.append(type.toString());
		urnSyntaxe.append(Intern.xml_tag_end);

		return urnSyntaxe;
	}

	/**
	 * Getter pour model.
	 * 
	 * @return DataModel
	 * @see DataDomain.java#model
	 */
	public DataModel getModel() {
		return _model;
	}

	/**
	 * Setter pour model
	 * 
	 * @param model
	 *            :
	 *            nouvelle valeur pour model
	 * @see DataDomain.java#model
	 */
	public void setModel(DataModel model) {
		_model = model;
	}

	public ParameterManager getMyParameterManager() {
		return myParameterManager;
	}

	public void setMyParameterManager(ParameterManager myParameterManager) {
		this.myParameterManager = myParameterManager;
	}

}
