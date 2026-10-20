package fr.ifremer.nemo.input.fragment.linefragment;

import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.ModelDescription;

/**
 * CsvLineFragment with column Number
 *
 */
public class CsvLineFragment extends LineFragment {

	// Column Number
	private int _colNb = -1;

	public CsvLineFragment() {
		super();
	}

	public CsvLineFragment(InputLineFragment rawLineFragment) {
		super(rawLineFragment);

		if (rawLineFragment.getColNb() != -1) {
			this.setColNb(rawLineFragment.getColNb());
		} else {
			this.setColNb(rawLineFragment.getFirstCharOffset());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute(ModelDescription.a_colNb, getColNb());
	}

	/**
	 * Get a value in a line. If it's a raw string line,
	 * 
	 * @param a_Line
	 * @return String
	 */
	@Override
	public String retreive(String a_Line) {
		String[] splittedLine = a_Line
				.split(Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit(), -1);
		return splittedLine[_colNb - 1].trim();
	}

	/**
	 * 
	 * @param a_Line
	 * @return String
	 */
	@Override
	public String fullRetreive(String a_Line) {
		String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();
		return a_Line.split(separator)[_colNb - 1];

	}

	/**
	 * Getter for column number
	 * 
	 * @return
	 */
	public int getColNb() {
		return _colNb;
	}

	public void setColNb(int colNb) {
		_colNb = colNb;
	}

}
