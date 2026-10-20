package fr.ifremer.nemo.test;

import org.apache.logging.log4j.Logger;

public class Message {
	private Logger logger;
	public String parameterList;
	public String convType;
	public boolean isActivated = true;
	public boolean isTestFound = true;
	public String dirName;
	public String testName = null;
	public boolean isModelFound;
	public boolean isReferenceFound = false;
	public boolean conversionRes;
	public String conversionError;
	public boolean batchCompRes;
	public boolean manualCompRes;
	public String manualCompName;
	public boolean isManualFound;
	public String diff="";
	public String processTime;
	public boolean batchCdiSummaryCompRes;
	public boolean octopusCheck=true;
	
	
	public Message(Logger logger) {
		this.logger=logger;
	}

	public void print() {
		String mes = "";
		mes += "### " + dirName + " ###  :  ";
		if (testName != null) {
			mes += " " + testName;
			if (!isActivated) {
				mes += ("- DESACTIVATED -");
				write(mes);
				return;
			}
			if (!isTestFound) {
				mes += ("- No test found -");
				write(mes);
				return;
			}
			if (!isModelFound) {
				mes += ("- KO No model found -");
				write(mes);
				return;
			}
			if (!conversionRes) {
				mes += " | conversion: " + getString(conversionRes);
				mes += " !!!! " + conversionError;
			} else {
				mes += " | conversion: " + getString(conversionRes);
			}
			if (isReferenceFound) {
				mes += " | batch comparison: " + getString(batchCompRes);
			} else {
				mes += " | Reference file not found";
			}
			if (!(batchCompRes && conversionRes)) {
			}
		} else {
			mes += " no test found";
		}
		write(mes);
	}

	private String getString(boolean res) {
		String str = "";
		if (res) {
			str = "OK";
		} else {
			str = "KO";
		}
		return str;
	}

	private void write(String mes) {
		logger.info(mes);// + "\r\n");
	}
}
