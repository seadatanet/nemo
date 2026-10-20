package fr.ifremer.nemo.input.fragment.stationfragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Utils;

/**
 * CsvLineFragment with column Number
 *
 */
public class CsvFragment extends Fragment {

	// Column Number
	private int _colNb = -1;
	
	
	public CsvFragment() {
		super();
	}

	public CsvFragment(InputFragment rawFragment) {
		super(rawFragment);
		if (rawFragment.getColNb() != -1) {
			this.setColNb(rawFragment.getColNb());
		} else {
			this.setColNb(convertFirstoffsetToColumn(rawFragment.getFirstCharOffset()));
		}
	}

	@Override
	public void writeXMLTags(XMLOutputWriter a_Writer) {
		super.writeXMLTags(a_Writer);
		a_Writer.putTag(ModelDescription.node_colNb, getColNb());
	}

	@Override
	public String retreiveInLine(String a_Line) {
		// 40216: guess the column number from the caracter start number (for
		// files with separator)
		String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();
		return a_Line.split(separator)[_colNb - 1];
	}

	@Override
	public String fullRetreiveInLine(String a_Line) {
		String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();
		return a_Line.split(separator)[_colNb - 1];

	}

	/**
	 * Retrieve data within a line with known separator.
	 * 
	 * The column number of the searched data is given by this fragment.
	 * 
	 * @param a_Line - The source line to search data into.
	 * @param separator - The line separator.
	 * @return the String representing the data.
	 * @throws ParseException
	 */
	public String retrieveInLineWithSeparator(String a_Line, String separator) throws ParseException {
		// String
		// separator=Nemo.getInstance().getDomain().getMedia().getModel().getSeparator();

		String lineSubstring = a_Line;

		if (separator != null) {
			try {
				if (lineSubstring.startsWith(separator)) {
					lineSubstring = lineSubstring.substring(lineSubstring.indexOf(separator) + 1,
							lineSubstring.length());
				}
				return lineSubstring.split(separator)[this.getColNb() - 1];
			} catch (Exception e) {
				// TODO
				throw new ParseException("error", 0);
			}
		}

		// TODO error
		throw new ParseException(
				"error: can not use FreeDecimalDegrees format on files which are not using a separator", 0);
	}

	/**
	 * 40216
	 */
	public int getColNb() {
		return _colNb;
	}

	public void setColNb(int colNb) {
		_colNb = colNb;
	}

	/**
	 * Setter pour _firstCharOffset
	 * 
	 * @param s : nouvelle valeur pour _firstCharOffset sous forme de String
	 * @see Fragment.java#_firstCharOffset
	 */
	public void setColNb(String s) {
		try {
			_colNb = Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Setter pour _firstCharOffset
	 * 
	 * @param s : nouvelle valeur pour _firstCharOffset sous forme de String
	 * @see Fragment.java#_firstCharOffset
	 */
	public int convertFirstoffsetToColumn(int firstCharOffset) {
		int columnIndex = -1;
		try {
			String ligne = null;
			//Find first inputFileName
			String firstFileName;

			if (Nemo.getInstance().getViewMain() != null) { // Nemo IHM mode
				firstFileName =  Nemo.getInstance().getViewMain().getEditFileForm().getNomFichier();
			} else {	// Nemo Batch mode			
				HashMap<String, List<String>> fileMap = Nemo.getInstance().getDomain().getMedia().getInputFilesNames();
				Entry<String, List<String>> entry = fileMap.entrySet().iterator().next();
				String rep = entry.getKey();
				firstFileName = rep + "/" + fileMap.get(rep).get(0);
			}
			
			
//			 HashMap<String, List<String>> fileMap = Nemo.getInstance().getDomain().getMedia().getInputFilesNames();
//			 if (fileMap != null) {
//				 Entry<String, List<String>> entry = fileMap.entrySet().iterator().next();
//					String rep = entry.getKey();
//					 firstFileName = rep + "/" + fileMap.get(rep).get(0);
//			 } else {
//				 firstFileName =  Nemo.getInstance().getViewMain().getEditFileForm().getNomFichier();
//			 }

			BufferedReader entree = new BufferedReader(
					new InputStreamReader(new FileInputStream(firstFileName), Global.NEMO_ENCODING));
			int nbreLignes = 0;
			int lineIndex;
			int fileHeader = 0;
			if (!RulesManager.getInstance().isCruiseInADirectory()
					|| Nemo.getInstance().getViewMain().getEditFileForm().isMultiStationFileChecked()) {
				// fichier campagne
				fileHeader = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
			}
			lineIndex = getFirstLine() -1 + fileHeader;
			String csvLine = null;
			do {
				ligne = entree.readLine();
				if (ligne != null) {
					if (nbreLignes == lineIndex)  {
					csvLine = ligne;
					break;
					}
					nbreLignes = nbreLignes + 1;
				}

			} while (ligne != null);
			entree.close();
			String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();
			columnIndex = Utils.getColumnIndexInCsvLineFromCharIndex(csvLine, separator, firstCharOffset);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnIndex;
	}

}
