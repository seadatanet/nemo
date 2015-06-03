/*
 * ============================================================================
 * Nom du fichier : CruiseDomain.java
 * Application : NEMO version 1.2.20
 * Auteur : Delphine BOULANGER
 * Date de création : 30 nov. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.sismer_tools.seadatanet.SDNXLinkException;
import fr.ifremer.sismer_tools.seadatanet.SdnXLinkReference;

/**
 * Class CruiseDomain.java Description: Classe pour la description des données
 * de l'onglet Cruise
 * 
 * @author Delphine BOULANGER
 */
public class CruiseDomain {
	/**
	 * Informations descrivant l'onglet Cuise
	 */
	protected CruiseModel _model;
	final private Formatter form;


	public CruiseDomain() {
		form = Formatter.getSingleton();
		initValues();
	}

	public void initValues() {
		_model = new CruiseModel();
	}

	public void update(InputModel model) {
		updateCruise(model.getCruiseModel());
	}

	public void updateCruise(CruiseModel cruiseModel) {
		_model = cruiseModel;
	}

	public void release() {
		_model.release();
	}

	public StringBuffer getHeader(Conversion c, List<Station> stationsList) throws ParseException,
	UnsupportedEncodingException, GenerationException {
		StringBuffer cruiseHeader = new StringBuffer();
		switch (c) {
		case ODV:
			cruiseHeader = getOdvHeader(stationsList);
			break;
		case MEDATLAS:
			cruiseHeader = getMedatlasHeader();
			break;
		}
		return cruiseHeader;
	}

	private StringBuffer getOdvHeader(List<Station> stationsList) throws GenerationException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

		String start = Conversion.ODV.getDebut();
		StringBuffer odvCruiseHeader = new StringBuffer();

		if (!_model.getComment().equals(Global.EMPTY_STRING)) {
			for (String line : getCommentLines()) {
				odvCruiseHeader.append(start);
				odvCruiseHeader.append(line);
				odvCruiseHeader.append(Global.RN);
			}
		}

		odvCruiseHeader.append(start);
		odvCruiseHeader.append(Global.RN);

		// SDN REFERENCES
		if (_model.getLocalCsrIdentifier() != null && !_model.getLocalCsrIdentifier().isEmpty()){
			SdnXLinkReference csr = new SdnXLinkReference();
			try {
				csr.setAsCsr(Integer.valueOf(Nemo.getInstance().getConfig().getIdEdmoRespCdi()), _model.getLocalCsrIdentifier());
				odvCruiseHeader.append(start);
				odvCruiseHeader.append(csr.toString(true));
				odvCruiseHeader.append(Global.RN);
			} catch (NumberFormatException e1) {
				// TODO 20755 Auto-generated catch block
				e1.printStackTrace();
			} catch (SDNXLinkException e1) {
				// TODO 20755 Auto-generated catch block
				e1.printStackTrace();
			}
		}

		String[] shipCodeAndName = Nemo.getInstance().getDomain().getCruise().getModel().getValidShipCodeAndName();
		if ( ! shipCodeAndName[0].isEmpty()	)
		{
			try {

					SdnXLinkReference shipRef = new SdnXLinkReference();
					shipRef.setAsShipReference(shipCodeAndName[0] );
					odvCruiseHeader.append(start);
					odvCruiseHeader.append(shipRef.toString(true));
					odvCruiseHeader.append(Global.RN);
			} catch (NumberFormatException e1) {
				// TODO 20755 Auto-generated catch block
				e1.printStackTrace();
			} catch (SDNXLinkException e1) {
				// TODO 20755 Auto-generated catch block
				e1.printStackTrace();
			}
		}
		for (Station st : stationsList){
			SdnXLinkReference cdi = new SdnXLinkReference();
			String localCdiId;
			try {
				localCdiId = stationDomain.getLocalCdiId(this, Conversion.ODV, st.getReference());
				cdi.setAsCdi(Integer.valueOf(Nemo.getInstance().getConfig().getIdEdmoRespCdi()), localCdiId, true);
			} catch (UnsupportedEncodingException e) {
				// TODO 20755 Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO 20755 Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO 20755 Auto-generated catch block
				e.printStackTrace();
			} catch (SDNXLinkException e) {
				// TODO 20755 Auto-generated catch block
				e.printStackTrace();
			}


			odvCruiseHeader.append(start);
			odvCruiseHeader.append(cdi.toString(true));
			odvCruiseHeader.append(Global.RN);
		}

		// SDN PARAMETERS
		DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
		odvCruiseHeader.append(dataDomain.getSDNparameters(Conversion.ODV));
		odvCruiseHeader.append(start);
		odvCruiseHeader.append(Global.RN);

		// HEADER LABELS
		odvCruiseHeader.append(stationDomain.getOdvStationHeaderLabels());
		odvCruiseHeader.append(dataDomain.getDataHeader(Conversion.ODV));

		return odvCruiseHeader;
	}

	private StringBuffer getMedatlasHeader() throws ParseException,
	UnsupportedEncodingException {
		String start = Conversion.MEDATLAS.getDebut();
		SimpleDateFormat fmt = new SimpleDateFormat(CruiseModel.MED_DATE_FORMAT);

		StringBuffer medCruiseHeader = new StringBuffer();

		medCruiseHeader.append(start);
		medCruiseHeader.append(Elts.getElt(Elts.cruise_ref,
				_model.getReference()));
		medCruiseHeader.append(Global.SPACE);
		medCruiseHeader.append(Elts.getElt(Elts.cruise_name, _model.getName()));
		medCruiseHeader.append(Global.SPACE);
		medCruiseHeader.append(Elts.getElt(Elts.cruise_shipcode,
				_model.getShipCode()));
		medCruiseHeader.append(Global.SPACE);
		medCruiseHeader.append(Elts.getElt(Elts.cruise_shipname,
				_model.getShipName()));
		medCruiseHeader.append(Global.RN);

		medCruiseHeader.append(fmt.format(new Date(_model.getStartDate())));
		medCruiseHeader.append(Global.MOINS);
		medCruiseHeader.append(fmt.format(new Date(_model.getEndDate())));
		medCruiseHeader.append(Global.SPACE);
		medCruiseHeader.append(Elts.getElt(Elts.cruise_region,
				_model.getRegionName()));
		medCruiseHeader.append(Global.RN);

		medCruiseHeader.append(Elts.getElt(Elts.cruise_country,
				_model.getCountryCode()));
		medCruiseHeader.append(Global.SPACE);
		String cruiseLabo = Elts.getElt(Elts.cruise_labo,
				_model.getLaboratories());
		medCruiseHeader.append(cruiseLabo.replaceAll(Global.RN, " "));
		medCruiseHeader.append(Global.RN);

		medCruiseHeader.append(Elts.getElt(Elts.cruise_chiefscientist,
				_model.getChiefScientist()));
		medCruiseHeader.append(Elts.getElt(Elts.cruise_project,
				_model.getProjectName()));
		medCruiseHeader.append(Global.RN);

		medCruiseHeader.append(Elts.getElt(Elts.cruise_datacenter,
				_model.getDataCentre()));
		medCruiseHeader.append(form.padString(Global.EMPTY_STRING, 18));
		medCruiseHeader.append(Elts.getElt(Elts.cruise_availability, _model
				.getAvailability().getCode()));
		medCruiseHeader.append(Global.RN);

		int end = _model.getDataTypesLength();
		int length = 0;
		for (DataType dType : _model.getDataTypes()) {
			medCruiseHeader.append(Elts.getElt(Elts.datatype_code,
					dType.getCode()));
			medCruiseHeader.append(Elts.getElt(Elts.datatype_nbprofile,
					Integer.toString(dType.getNumberOfProfiles())));
			medCruiseHeader.append(Elts.getElt(Elts.qc,
					form.formatBoolean(dType.isQc())));
			length++;
			if (length != end)
				medCruiseHeader.append(Global.RN);
		}

		if (!_model.getComment().equals(Global.EMPTY_STRING)) {
			medCruiseHeader.append(Global.RN);
			medCruiseHeader.append(Elts.kw_cruise_comment);

			for (String line : getCommentLines()) {
				medCruiseHeader.append(Global.RN);
				medCruiseHeader.append(line);
			}
		}

		return medCruiseHeader;
	}

	/**
	 * 
	 * @return String[]
	 */
	private String[] getCommentLines() {
		LineNumberReader reader = new LineNumberReader(new StringReader(
				_model.getComment()));
		String line = null;
		java.util.Vector<String> lines = new java.util.Vector<String>();
		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
			}
		}
		String[] lineArray = new String[lines.size()];
		return lines.toArray(lineArray);
	}

	/**
	 * Getter pour model.
	 * 
	 * @return CruiseModel
	 * @see CruiseDomain.java#model
	 */
	public CruiseModel getModel() {
		return _model;
	}

	/**
	 * Setter pour model
	 * 
	 * @param model
	 *            : nouvelle valeur pour model
	 * @see CruiseDomain.java#model
	 */
	public void setModel(CruiseModel model) {
		_model = model;
	}


}
