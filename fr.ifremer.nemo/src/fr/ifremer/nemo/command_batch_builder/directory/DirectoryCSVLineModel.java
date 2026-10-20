package fr.ifremer.nemo.command_batch_builder.directory;

import fr.ifremer.nemo.util.Global;

/**
 * Model for a line of the CSV mapping file for directory mode
 * @author pmahoudo
 */
public class DirectoryCSVLineModel {
	String fileNameIn;
	String xmlModelName;
	String fileNameOut;

	// OPTIONS
	String datasetRef;
	String cruiseName;
	String cruiseComment;
	String localCsrIdentifier;
	String bshCsrReference;
	String cdiSummary;
	String shipCode;

	public DirectoryCSVLineModel(String line) {
		// Split CSV line to array (limit = -1 for last empty column option)
		String[] parts = line.split(Global.SEMICOLON, -1);
		fileNameIn = parts[0];
		xmlModelName = parts[1];
		fileNameOut = parts[2];
		datasetRef = parts[3];
		cruiseName = parts[4];
		cruiseComment = parts[5];
		localCsrIdentifier = parts[6];
		bshCsrReference = parts[7];
		cdiSummary = parts[8];
		shipCode = parts[9];

	}

	public String getFileNameIn() {
		return fileNameIn;
	}

	public void setFileNameIn(String fileNameIn) {
		this.fileNameIn = fileNameIn;
	}

	public String getXMLModelName() {
		return xmlModelName;
	}

	public void setXMLModelName(String modelName) {
		this.xmlModelName = modelName;
	}

	public String getFileNameOut() {
		return fileNameOut;
	}

	public void setFileNameOut(String fileNameOut) {
		this.fileNameOut = fileNameOut;
	}

	public String getDatasetRef() {
		return datasetRef;
	}

	public void setDatasetRef(String datasetRef) {
		this.datasetRef = datasetRef;
	}

	public String getCruiseName() {
		return cruiseName;
	}

	public void setCruiseName(String cruiseName) {
		this.cruiseName = cruiseName;
	}

	public String getCruiseComment() {
		return cruiseComment;
	}

	public void setCruiseComment(String cruiseComment) {
		this.cruiseComment = cruiseComment;
	}

	public String getLocalCsrIdentifier() {
		return localCsrIdentifier;
	}

	public void setLocalCsrIdentifier(String localCsrIdentifier) {
		this.localCsrIdentifier = localCsrIdentifier;
	}

	public String getBshCsrReference() {
		return bshCsrReference;
	}

	public void setBshCsrReference(String bshCsrReference) {
		this.bshCsrReference = bshCsrReference;
	}

	public String getCdiSummary() {
		return cdiSummary;
	}

	public void setCdiSummary(String cdiSummary) {
		this.cdiSummary = cdiSummary;
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
	}

}
