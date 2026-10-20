package fr.ifremer.nemo.ui.actions;

import java.util.ArrayList;

import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.util.Global;

public class FlagsChoices {
	
	// flag tables
	private  ArrayList<FlagTable> _flagTables = new ArrayList<FlagTable>();
	// default flag table
	private  String _defaultFlagTable = Global.EMPTY_STRING;
	// default flag
	private  String _defaultFlag = Global.EMPTY_STRING;
	
	
	private boolean choiceValidated=false;
	
	public  ArrayList<FlagTable> getFlagTables() {
		return _flagTables;
	}
	public  void setFlagTables(ArrayList<FlagTable> flagTables) {
		_flagTables = flagTables;
	}
	public  String getDefaultFlagTable() {
		return _defaultFlagTable;
	}
	public  void setDefaultFlagTable(String defaultFlagTable) {
		_defaultFlagTable = defaultFlagTable;
	}
	public  String getDefaultFlag() {
		return _defaultFlag;
	}
	public  void setDefaultFlag(String defaultFlag) {
		_defaultFlag = defaultFlag;
	}
	public boolean isChoiceValidated() {
		return choiceValidated;
	}
	public void setChoiceValidated(boolean choiceValidated) {
		this.choiceValidated = choiceValidated;
	}
	
}
