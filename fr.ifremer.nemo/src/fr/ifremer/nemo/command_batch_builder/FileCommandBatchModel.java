package fr.ifremer.nemo.command_batch_builder;

import java.util.Observable;

import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

public class FileCommandBatchModel extends Observable {

	// IN / OUT
	private String fileNameIn;
	private String xmlModelName;
	private String fileNameOut;

	// CONVERSION
	private Conversion convType;
	private boolean isMulti;
	// OPTIONS
	private String datasetRef;
	private String cruiseName;
	private String cruiseComment;
	private String localCsrIdentifier;
	private String bshCsrReference;
	private boolean sort;
	private String cdiSummary;
	private boolean couplingTableUpdate;
	private String couplingTablePrefix;
	private String shipCode;

	public FileCommandBatchModel() {
		init();
	}

	public void init() {
		// IN / OUT
		fileNameIn = Global.EMPTY_STRING;
		xmlModelName = Global.EMPTY_STRING;
		fileNameOut = Global.EMPTY_STRING;

		// CONVERSION
		convType = Conversion.MEDATLAS;
		isMulti = false;
		// OPTIONS
		datasetRef = Global.EMPTY_STRING;
		cruiseName = Global.EMPTY_STRING;
		cruiseComment = Global.EMPTY_STRING;
		localCsrIdentifier = Global.EMPTY_STRING;
		bshCsrReference = Global.EMPTY_STRING;
		sort = false;
		cdiSummary = Global.EMPTY_STRING;
		couplingTableUpdate = false;
		couplingTablePrefix = Global.EMPTY_STRING;
		shipCode = Global.EMPTY_STRING;
	}

	public String buildCommand() {
		StringBuilder command = new StringBuilder();

		if (fileNameIn != Global.EMPTY_STRING && xmlModelName != Global.EMPTY_STRING && fileNameOut != Global.EMPTY_STRING) {

			// The launch is OS-dependent.
			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win"); //$NON-NLS-1$ //$NON-NLS-2$

			if (isWindows) {
				command.append(Global.BATCH_NAME);
			} else {
				command.append("./" + Global.BATCH_NAME);
				// linux arguments
				command.append(Global.SPACE + Global.MOINS + Global.optNoSplash + Global.SPACE+ Global.MOINS + Global.optBatch);
			}



			command.append(Global.SPACE);

			// input
			command.append(Global.MOINS + Global.optInputFile + Global.SPACE + Global.QUOTE + fileNameIn + Global.QUOTE);
			// model
			command.append(Global.SPACE);
			command.append(Global.MOINS + Global.optModelFile + Global.SPACE + Global.QUOTE + xmlModelName + Global.QUOTE);
			// output
			command.append(Global.SPACE);
			command.append(Global.MOINS + Global.optOutputFile + Global.SPACE + Global.QUOTE + fileNameOut + Global.QUOTE);
			// conversion
			command.append(Global.SPACE);
			command.append(Global.MOINS + Global.optConv + Global.SPACE + convType.toString());

			// multi
			if (isMulti) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optOdvMulti);
			}
			if (!datasetRef.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optDatasetRef + Global.SPACE + Global.QUOTE + datasetRef + Global.QUOTE);
			}
			if (!cruiseName.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optCruiseName + Global.SPACE + Global.QUOTE + cruiseName + Global.QUOTE);
			}
			if (!cruiseComment.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optCruiseComment + Global.SPACE + Global.QUOTE + cruiseComment + Global.QUOTE);
			}
			if (!localCsrIdentifier.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optLocalCsrIdentifier + Global.SPACE + Global.QUOTE + localCsrIdentifier + Global.QUOTE);
			}
			if (!bshCsrReference.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optBshCsrReference + Global.SPACE + Global.QUOTE + bshCsrReference + Global.QUOTE);
			}
			if (sort) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optSortDatas);
			}
			if (!cdiSummary.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optCdiSummary + Global.SPACE + Global.QUOTE + cdiSummary + Global.QUOTE);
			}

			if (couplingTableUpdate) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optCouplingTableUpdate);
				if (!couplingTablePrefix.isEmpty()) {
					command.append(Global.SPACE);
					command.append(Global.MOINS + Global.optCouplingTablePrefix + Global.SPACE + Global.QUOTE + couplingTablePrefix
							+ Global.QUOTE);
				}
			}

			if (!shipCode.isEmpty()) {
				command.append(Global.SPACE);
				command.append(Global.MOINS + Global.optShipCode + Global.SPACE + Global.QUOTE + shipCode + Global.QUOTE);
			}

		} else {
			command.append(Global.EMPTY_STRING);
		}

		return command.toString();
	}

	public String getFileNameIn() {
		return fileNameIn;
	}

	public void setFileNameIn(String fileNameIn) {
		this.fileNameIn = fileNameIn;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(fileNameIn);
	}

	public String getFileNameOut() {
		return fileNameOut;
	}

	public void setFileNameOut(String fileNameOut) {
		this.fileNameOut = fileNameOut;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(fileNameOut);
	}

	public String getXMLModelName() {
		return xmlModelName;
	}

	public void setXMLModelName(String modelName) {
		this.xmlModelName = modelName;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(modelName);
	}

	public Conversion getConvType() {
		return convType;
	}

	public void setConvType(Conversion convType) {
		this.convType = convType;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(convType);
	}

	public boolean isMulti() {
		return isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(isMulti);
	}

	public String getDatasetRef() {
		return datasetRef;
	}

	public void setDatasetRef(String datasetRef) {
		this.datasetRef = datasetRef;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(datasetRef);
	}

	public String getCruiseName() {
		return cruiseName;
	}

	public void setCruiseName(String cruiseName) {
		this.cruiseName = cruiseName;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(cruiseName);
	}

	public String getCruiseComment() {
		return cruiseComment;
	}

	public void setCruiseComment(String cruiseComment) {
		this.cruiseComment = cruiseComment;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(cruiseComment);
	}

	public String getLocalCsrIdentifier() {
		return localCsrIdentifier;
	}

	public void setLocalCsrIdentifier(String localCsrIdentifier) {
		this.localCsrIdentifier = localCsrIdentifier;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(localCsrIdentifier);
	}

	public String getBshCsrReference() {
		return bshCsrReference;
	}

	public void setBshCsrReference(String bshCsrReference) {
		this.bshCsrReference = bshCsrReference;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(bshCsrReference);
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(sort);
	}

	public String getCdiSummary() {
		return cdiSummary;
	}

	public void setCdiSummary(String cdiSummary) {
		this.cdiSummary = cdiSummary;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(cdiSummary);
	}

	public boolean isCouplingTableUpdate() {
		return couplingTableUpdate;
	}

	public void setCouplingTableUpdate(boolean couplingTableUpdate) {
		this.couplingTableUpdate = couplingTableUpdate;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(couplingTableUpdate);
	}

	public String getCouplingTablePrefix() {
		return couplingTablePrefix;
	}

	public void setCouplingTablePrefix(String couplingTablePrefix) {
		this.couplingTablePrefix = couplingTablePrefix;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(couplingTablePrefix);
	}

	public String getShipCode() {
		return shipCode;
	}

	public void setShipCode(String shipCode) {
		this.shipCode = shipCode;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(shipCode);
	}

}
