package fr.ifremer.nemo.command_batch_builder.directory;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for directory mode
 * @author pmahoudo
 *
 */
public class DirectoryCSVModel {
	
	List<DirectoryCSVLineModel> lineModelList;
	
	public DirectoryCSVModel() {
		lineModelList = new ArrayList<DirectoryCSVLineModel>();
	}

	public List<DirectoryCSVLineModel> getLineModelList() {
		return lineModelList;
	}

	public void setLineModelList(List<DirectoryCSVLineModel> lineModelList) {
		this.lineModelList = lineModelList;
	}
	
	public void addLineModel(DirectoryCSVLineModel lineModel) {
		lineModelList.add(lineModel);
	}

}
