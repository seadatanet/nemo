package fr.ifremer.nemo.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import fr.ifremer.nemo.ui.EditConversion;
import fr.ifremer.nemo.ui.Utils;

public class CDIMappingWriter {
	private static CDIMappingWriter _instance;
	
	private LinkedHashMap<String, String> _mapping;
	
	public CDIMappingWriter() {
		_mapping = new LinkedHashMap<String, String>();
	}
	
	public void reset() {
		if (_mapping != null) 
			_mapping.clear();
		
		_mapping = new LinkedHashMap<String, String>();
	}
	
	public static CDIMappingWriter getInstance() {
		if (_instance == null) {
			_instance = new CDIMappingWriter();
		}
		return _instance;
	}
	
	public void addMapping(String localCdiId, String fichier) {
		_mapping.put(localCdiId, Utils.configuration.getArboMapping()+ "\\" + fichier);
	}
	
	public void writeMapping() throws IOException {
		File output = new File(EditConversion.nomDossierSortie + "\\" + Utils.configuration.getFichierMappingTxt());
		
		FileWriter writer = null;

		try {
			writer = new FileWriter(output);

			String sep = "\t";
			
			writer.write("LOCAL_CDI_ID" + sep + "FILE_NAME");

			for (Entry currentEntry : _mapping.entrySet()) {
				writer.write("\r\n");
				
				// LOCAL_CDI_ID
				writer.write(currentEntry.getKey() + sep);
				
				// FILE_NAME
				writer.write(currentEntry.getValue() + "");								
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
			reset();
		}
	}
}
