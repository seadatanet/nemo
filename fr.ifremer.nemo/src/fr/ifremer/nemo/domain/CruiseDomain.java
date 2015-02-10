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
import fr.ifremer.nemo.input.StationModel;
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
	// protected StationDomain _station; //onglet Station
	final private Formatter form;

	private String _dataSetRef;

	public CruiseDomain() {
		form = Formatter.getSingleton();
		initValues();
	}

	public void initValues() {
		_model = new CruiseModel();
		// _station = new StationDomain();
		_dataSetRef = Global.EMPTY_STRING;
	}

	public void update(InputModel model) {
		updateCruise(model.getCruiseModel());
	}

	public void updateCruise(CruiseModel cruiseModel) {
		_model = cruiseModel;
		// TODO update Domain
	}

	// RELEASE
	public void release() {
		// _station.release();
		_model.release();
	}

	public StringBuffer getHeader(Conversion c, List<Station> st) throws ParseException,
	UnsupportedEncodingException, GenerationException {
		StringBuffer cruiseHeader = new StringBuffer();
		switch (c) {
		case ODV:
			cruiseHeader = getOdvHeader(st);
			break;
		case MEDATLAS:
			cruiseHeader = getMedatlasHeader();
			break;
		}
		return cruiseHeader;
	}

	private StringBuffer getOdvHeader(List<Station> stationsList)  {
		CruiseDomain cruiseDomain = Nemo.getInstance().getDomain().getCruise();
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
		StationModel stationModel = stationDomain.getModel();
		DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
		
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
		

		/**
		 * SDN references
		 */
		


//		List<SdnXLinkReference> refs = stationModel.getSdnXLinkReferences();
//		if (refs !=null){
//			for (SdnXLinkReference ref : refs){
//				odvCruiseHeader.append(start);
//				odvCruiseHeader.append(ref.toString(false));
//				odvCruiseHeader.append(Global.RN);
//			}
//		}
		
		SdnXLinkReference csr = new SdnXLinkReference();
		try {
			csr.setAsCsr(Integer.valueOf(Nemo.getInstance().getConfig().getIdEdmoRespCdi()), cruiseDomain.getLocalCsrIdentifier());
		} catch (NumberFormatException e1) {
			// TODO 20755 Auto-generated catch block
			e1.printStackTrace();
		} catch (SDNXLinkException e1) {
			// TODO 20755 Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Station st : stationsList){
			SdnXLinkReference cdi = new SdnXLinkReference();
			// TODO 20755: check if it is the good edmo code
			String localCdiId;
			try {
				localCdiId = stationDomain.getLocalCdiId(cruiseDomain, Conversion.ODV, st.getReference());
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
		/**
		 * SDN semantic header
		 */
		try {
			odvCruiseHeader.append(dataDomain.getSDNparameters(Conversion.ODV));
		} catch (GenerationException e) {
			// TODO 20755 Auto-generated catch block
			e.printStackTrace();
		}
		odvCruiseHeader.append(start);
		odvCruiseHeader.append(Global.RN);

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
			// medCruiseHeader.append(Global.RN);

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


	public String getLocalCsrIdentifier(){
//		StringBuffer sdnRef = null;
//
////		if (_model.getCorrespondingURL()!=null){	
////			sdnRef = new StringBuffer();
////			sdnRef.append(_model.getCorrespondingURL().toString());
////		}
//		if (_model.getLocalCsrIdentifier()!=null){	
//			sdnRef = new StringBuffer();
////			sdnRef.append(_model.getCorrespondingURL().toString());
//		}
//		return sdnRef;
		
		return _model.getLocalCsrIdentifier();


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

	// /**
	// * Getter pour station.
	// * @return StationDomain
	// * @see InputDomain.java#station
	// */
	// public StationDomain getStation(){
	// return _station;
	// }
	//
	// /**
	// * Setter pour station
	// * @param station : nouvelle valeur pour station
	// * @see InputDomain.java#station
	// */
	// public void setStation(StationDomain station){
	// _station = station;
	// }

	/**
	 * Getter pour _dataSetRef.
	 * 
	 * @return String
	 * @see CruiseDomain.java#_dataSetRef
	 */
//	public String getDataSetRef() {
//		return _dataSetRef;
//	}

	/**
	 * Setter pour _dataSetRef
	 * 
	 * @param dataSetRef
	 *            : nouvelle valeur pour dataSetRef
	 * @see CruiseDomain.java#_dataSetRef
	 */
//	public void setDataSetRef(String dataSetRef) {
//		_dataSetRef = dataSetRef;
//	}
}
