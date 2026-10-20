package fr.ifremer.nemo.command_batch_builder.directory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import fr.ifremer.nemo.command_batch_builder.FileCommandBatchModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

public class DirectoryCommandBatchModel extends Observable {

	// IN / OUT
	private String dirNameIn;
	private String dirXMLModel;
	private String dirNameOut;
	private String csvName;


	// CONVERSION
	private Conversion convType;
	private boolean isMulti;

	// OPTIONS
	private boolean sort;
	private String dirCdiSUmmary;
	private boolean couplingTableUpdate;
	private String couplingTablePrefix;
	
	//CSVMODEL
	DirectoryCSVModel directoryCSVModel;
	List<FileCommandBatchModel> fileModelList;

	public DirectoryCommandBatchModel() {
		fileModelList = new ArrayList<FileCommandBatchModel>();
		init();
	}

	public void init() {
		// IN / OUT
		dirNameIn = Global.EMPTY_STRING;
		dirXMLModel = Global.EMPTY_STRING;
		dirNameOut = Global.EMPTY_STRING;
		csvName = Global.EMPTY_STRING;



		// CONVERSION
		convType = Conversion.MEDATLAS;
		isMulti = false;
		// OPTIONS
		sort = false;
		dirCdiSUmmary = Global.EMPTY_STRING;	
		couplingTableUpdate = false;
		couplingTablePrefix = Global.EMPTY_STRING;

		fileModelList.clear();
	}
	
	public void readAndLoadCSVModel() throws IOException {
		
		
		directoryCSVModel = new DirectoryCSVModel();
		
		BufferedReader reader = new BufferedReader(new FileReader(csvName));
	    String line;
	    // On lit la première ligne (l'entête) et on l'ignore
	    reader.readLine();
	    while ((line = reader.readLine()) != null) {
	    	
		  DirectoryCSVLineModel lineModel = new DirectoryCSVLineModel(line);
	      directoryCSVModel.addLineModel(lineModel);
	    }
	    reader.close();
	    
	    // Compute the list of fileModel
	    computeFileModelList();
	    
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(directoryCSVModel);
	}
	
	public void computeFileModelList() {
		//
		fileModelList = new ArrayList<FileCommandBatchModel>();
		for (DirectoryCSVLineModel line : directoryCSVModel.getLineModelList()) {
			FileCommandBatchModel fileCommandBatchModel = new FileCommandBatchModel();

			if (line.getFileNameIn() != null) {
				fileCommandBatchModel.setFileNameIn(dirNameIn+ File.separator +line.getFileNameIn());
			}
			if (line.getXMLModelName() != null) {
				fileCommandBatchModel.setXMLModelName(dirXMLModel+ File.separator +line.getXMLModelName());
			}
			if (line.getFileNameOut() != null) {
				fileCommandBatchModel.setFileNameOut(dirNameOut+ File.separator +line.getFileNameOut());
			}


			fileCommandBatchModel.setConvType(convType);

			fileCommandBatchModel.setMulti(isMulti);

			if (line.getDatasetRef() != null) {
				fileCommandBatchModel.setDatasetRef(line.getDatasetRef());
			}
			if (line.getCruiseName() != null) {
				fileCommandBatchModel.setCruiseName(line.getCruiseName());
			}
			if (line.getCruiseComment() != null) {
				fileCommandBatchModel.setCruiseComment(line.getCruiseComment());
			}
			if (line.getLocalCsrIdentifier() != null) {
				fileCommandBatchModel.setLocalCsrIdentifier(line.getLocalCsrIdentifier());
			}
			if (line.getBshCsrReference() != null) {
				fileCommandBatchModel.setBshCsrReference(line.getBshCsrReference());
			}
			// sort
			fileCommandBatchModel.setSort(sort);

			// cdisummary
			if (line.getCdiSummary() != null) {
				fileCommandBatchModel.setCdiSummary(line.getCdiSummary());
			}
			// coupling Table
			fileCommandBatchModel.setCouplingTableUpdate(couplingTableUpdate);
			fileCommandBatchModel.setCouplingTablePrefix(couplingTablePrefix);

			if (line.getShipCode() != null) {
				fileCommandBatchModel.setShipCode(line.getShipCode());
			}
			fileModelList.add(fileCommandBatchModel);
		}
	}
	
	public String buildCommand() {
		StringBuilder command = new StringBuilder();		
		
		for (FileCommandBatchModel fileModel : fileModelList) {
			command.append(fileModel.buildCommand());
			command.append(Global.LINE_SEPARATOR);
		}
		return command.toString();
	}
	
	public String getDirNameIn() {
		return dirNameIn;
	}

	public void setDirNameIn(String dirNameIn) {
		this.dirNameIn = dirNameIn;
		for (int i = 0; i< fileModelList.size(); i++) {
			FileCommandBatchModel fileModel = fileModelList.get(i);
			DirectoryCSVLineModel CSVline = directoryCSVModel.getLineModelList().get(i);
			fileModel.setFileNameIn(dirNameIn + File.separator + CSVline.getFileNameIn());
		}
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(dirNameIn);
	}
	
	public String getDirXMLModel() {
		return dirXMLModel;
	}

	public void setDirXMLModel(String dirModel) {
		this.dirXMLModel = dirModel;
		for (int i = 0; i< fileModelList.size(); i++) {
			FileCommandBatchModel fileModel = fileModelList.get(i);
			DirectoryCSVLineModel CSVline = directoryCSVModel.getLineModelList().get(i);
			fileModel.setXMLModelName(dirModel + File.separator + CSVline.getXMLModelName());
		}
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(dirModel);
	}

	public String getDirNameOut() {
		return dirNameOut;
	}

	public void setDirNameOut(String dirNameOut) {
		this.dirNameOut = dirNameOut;
		for (int i = 0; i< fileModelList.size(); i++) {
			FileCommandBatchModel fileModel = fileModelList.get(i);
			DirectoryCSVLineModel CSVline = directoryCSVModel.getLineModelList().get(i);
			fileModel.setFileNameOut(dirNameOut + File.separator + CSVline.getFileNameOut());
		}
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(dirNameOut);
	}

	public String getCSVName() {
		return csvName;
	}

	public void setCSVName(String csvName) {
		this.csvName = csvName;
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(csvName);
	}

	public Conversion getConvType() {
		return convType;
	}

	public void setConvType(Conversion convType) {
		this.convType = convType;
		fileModelList.forEach(fileModel -> fileModel.setConvType(convType));
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
		fileModelList.forEach(fileModel -> fileModel.setMulti(isMulti));
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(isMulti);
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
		fileModelList.forEach(fileModel -> fileModel.setSort(sort));
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(sort);
	}
	
	public String getDirCdiSummary() {
		return dirCdiSUmmary;
	}

	public void setDirCdiSUmmary(String dirCDISUmmary) {
		this.dirCdiSUmmary = dirCDISUmmary;
		for (int i = 0; i< fileModelList.size(); i++) {
			FileCommandBatchModel fileModel = fileModelList.get(i);
			DirectoryCSVLineModel CSVline = directoryCSVModel.getLineModelList().get(i);
			fileModel.setCdiSummary(dirCDISUmmary + File.separator + CSVline.getCdiSummary());
		}
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(dirNameIn);
	}

	public boolean isCouplingTableUpdate() {
		return couplingTableUpdate;
	}

	public void setCouplingTableUpdate(boolean couplingTableUpdate) {
		this.couplingTableUpdate = couplingTableUpdate;
		fileModelList.forEach(fileModel -> fileModel.setCouplingTableUpdate(couplingTableUpdate));
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
		fileModelList.forEach(fileModel -> fileModel.setCouplingTablePrefix(couplingTablePrefix));
		// set change
		setChanged();
		// notify observers for change
		notifyObservers(couplingTablePrefix);
	}

}
