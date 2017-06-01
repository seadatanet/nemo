package fr.ifremer.nemo.util;

import fr.ifremer.nemo.ui.edit.data.TypeData;


public class BODCResult{
	public  enum BODC_RES_STATUS {OK, KO, WARNING};
	private String errorMsg="";
	private String warningMsg="";
	private BODC_RES_STATUS resStatus;
	private TypeData typeData;
	private String fallrateCode;
	private String fallrateLabel;
	private String shipCode;
	private String shipLabel;
	
	private String regionCode;
	private String regionLabel;
	
	private String countryCode;
	private String countryLabel;
	
	private String centerCode;
	private String centerLabel;
	
	private String dataTypeCode;
	
	public BODCResult(BODC_RES_STATUS resStatus) {
		this.resStatus = resStatus;
	}
	
	public BODCResult(BODC_RES_STATUS resStatus, String errorMsg, String warningMsg) {
		this.resStatus = resStatus;
		this.errorMsg = errorMsg;
		this.warningMsg = warningMsg;
	}

	public BODCResult(TypeData typeData){
		this.typeData=typeData;
	}
	public BODCResult(TypeData typeData, BODC_RES_STATUS resStatus) {
		this.typeData=typeData;
		this.resStatus = resStatus;
	}
	public String getErrorMsg() {
		return errorMsg;
	}


	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public String getWarningMsg() {
		return warningMsg;
	}


	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}


	public BODC_RES_STATUS getResStatus() {
		return resStatus;
	}


	public void setResStatus(BODC_RES_STATUS resStatus) {
		this.resStatus = resStatus;
	}
	public TypeData getTypeData() {
		return typeData;
	}
	public String getFallrateCode() {
		return fallrateCode;
	}
	public String getFallrateLabel() {
		return fallrateLabel;
	}
	public void setFallrateCode(String fallrateCode) {
		this.fallrateCode = fallrateCode;
	}
	public void setFallrateLabel(String fallrateLabel) {
		this.fallrateLabel = fallrateLabel;
	}
	public String getShipLabel() {
		return shipLabel;
	}
	public void setShipLabel(String shipLabel) {
		this.shipLabel = shipLabel;
	}
	public String getShipCode() {
		return shipCode;
	}
	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public String getRegionLabel() {
		return regionLabel;
	}
	public void setRegionLabel(String regionLabel) {
		this.regionLabel = regionLabel;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryLabel() {
		return countryLabel;
	}

	public void setCountryLabel(String countryLabel) {
		this.countryLabel = countryLabel;
	}

	public String getCenterLabel() {
		return centerLabel;
	}

	public void setCenterLabel(String centerLabel) {
		this.centerLabel = centerLabel;
	}

	public String getCenterCode() {
		return centerCode;
	}

	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}

	public String getDataTypeCode() {
		return dataTypeCode;
	}

	public void setDataTypeCode(String dataTypeCode) {
		this.dataTypeCode = dataTypeCode;
	}
}
