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
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.MissingResourceException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Class DataDomain.java Description: Classe pour la description des données de
 * l'onglet Data
 * 
 * @author Delphine BOULANGER
 */
public class DataDomain {

	private enum URNtype {
		subject, object, units, fallrate, instrument;
	}

	/**
	 * Informations descrivant l'onglet Data
	 */
	protected DataModel _model;

	/**
	 * reference sur l'objet gérant les paramètres (règles de recherche et
	 * d'ajout en fonction des param types...
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
			dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot), null, false));

		}

		// paramètres mesurés
		for (ParameterModel pModel : _model.getParameterModels()) {
			cptParam++;
			Parameter param = pModel.getParameter();
//			int parameterSize = pModel.getFragment().getLastCharOffset() - pModel.getFragment().getFirstCharOffset() + 1;
			dataHeader.append(param.getOutputCode(c, (cptParam == nbParamTot), pModel.isText()));
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
		// FAE 11356
		if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES && Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData())
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
			// 11976
			String stringValue = "";
			if (!pModel.getLineValue().getValue().equals("")) {

				stringValue = Formatter.getSingleton().formatParameter(pModel.getParameter().getFormat(), pModel.getLineValue().getValue());
			}
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

		// FAE 11356
		if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES && Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData())
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

	public StringBuffer getSDNparameters(Conversion c) throws GenerationException {
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
			sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c, param.isText()));
		}

		/**
		 * parameters
		 */
		int i=0;
		for (ParameterModel param : _model.getParameterModels()) {
			String fallrateCode=null;
			if (i==0){
				fallrateCode = getModel().getFallrateCode();
				if (fallrateCode.isEmpty()){
					fallrateCode=null;
				}
			}
			sdnParams.append(parameterMap(c.getDebut(), param.getParameter(), c, param.isText(), fallrateCode));
			i++;
		}

		return sdnParams;
	}

	private Object parameterMap(String debut, Parameter parameter,
			Conversion c, boolean text) throws GenerationException {
		return parameterMap(debut, parameter, c, text, null);
	}

	private StringBuffer parameterMap(String start, Parameter param, Conversion c, boolean isText,String fallrateCode) throws GenerationException {
		String code = param.getCode();
		StringBuffer sdnMapping = new StringBuffer();
		sdnMapping.append(start);

		/**
		 * BGT 6838: bug introduit parce que j'avais enlevé le try/catch ci
		 * dessous en fait, on essaie de trouver le code dans la liste Intern si
		 * c'est un addedParameter, on va le trouver, et on ajoute la valeur de
		 * la variable Inter.code (qui contient TOUTE la ligne à ajouter) sinon,
		 * on fabrique la ligne
		 */
		try {
			sdnMapping.append(Intern.getString(code));
		} catch (MissingResourceException e) {
			/**
			 * ----------------------------------------------------- subject
			 */
			// FAE 4045 getLocalCode
			sdnMapping.append(getURNSyntaxe(URNtype.subject, param.getLocalCode(), null, null, null));
			/**
			 * ----------------------------------------------------- object si
			 * param P011, on met le code P011 si param P091, on met le premier
			 * P011 de la liste de P011 correspondants au P091
			 */
			if (param.isP011()) {
				sdnMapping.append(getURNSyntaxe(URNtype.object, param.getCodeP011(), null, Intern.mapping_objet, isText));
			} else {
				sdnMapping.append(getURNSyntaxe(URNtype.object, param.getCode(), Intern.mapping_subject, Intern.mapping_objet, isText));
			}

			/**
			 * ----------------------------------------------------- units
			 * unitCode utilisé seulement pour les parametres P011 choisis
			 * depuis P021 (qui n'ont pas d'unité, et dont l'unité est donc
			 * choisie manuellement par l'utilisateur depuis la liste P061) s'il
			 * est vide, on va chercher l'unité correspondant au P091 choisi ou
			 * ayant servi à choisir le P021 sinon, on prend sa valeur qui a été
			 * renseignée quen l'utilisateur a choisi l'unité dans la liste des
			 * P061
			 */

			if (param.getUnitCode() == null || param.getUnitCode().equals(Global.EMPTY_STRING)) {
				sdnMapping.append(getURNSyntaxe(URNtype.units, param.getCode(), Intern.mapping_subject, Intern.mapping_unit, isText));
			} else {
				// FAE 6542
				sdnMapping.append(getURNSyntaxe(URNtype.units, param.getUnitCode(), null, Intern.mapping_unit, isText));
			}
		}
		
		// instrument
		if (param.getInstrumentCode()!=null && !param.getInstrumentCode().isEmpty()){
			sdnMapping.append(getURNSyntaxe(URNtype.instrument, param.getInstrumentCode(), null, Intern.mapping_instrument, isText));
		}
		// fall rate
		if (fallrateCode!=null){
			sdnMapping.append(getURNSyntaxe(URNtype.fallrate, fallrateCode, null, Intern.mapping_fallrate, isText));
		}
		sdnMapping.append(Global.RN);
		return sdnMapping;
	}

	private static StringBuffer getURNSyntaxe(URNtype type, String code, String list1, String list2, Boolean isText) throws GenerationException {
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
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeobject;
			if (list1 != null)
				pdvCodeobject = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeobject = code;
			if (pdvCodeobject==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodeobject);
			break;
		case units:
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeunits;
			if (list1 != null || isText)
				pdvCodeunits = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeunits = code;
			if (pdvCodeunits==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramunit, code, list1, list2));
			}
			// FAE 10755
			if (isText)
				urnSyntaxe.append(Intern.NemoLists_unknown);
			else
				urnSyntaxe.append(pdvCodeunits);
			break;
		case fallrate:
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodefallrate;
			if (list1 != null)
				pdvCodefallrate = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodefallrate = code;
			if (pdvCodefallrate==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodefallrate);
			break;
		case instrument:
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeinstrument;
			if (list1 != null)
				pdvCodeinstrument = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeinstrument = code;
			if (pdvCodeinstrument==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodeinstrument);
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
	 *            : nouvelle valeur pour model
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
