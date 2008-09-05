package fr.ifremer.nemo.flag;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class implements an ICellModifier An ICellModifier is called when the
 * user modifes a cell in the tableViewer
 */
public class FlagCellModifier implements ICellModifier {
	private FlagTableViewer flagTableViewer;

	// private String[] columnNames;

	/**
	 * Constructor
	 * 
	 * @param TableViewerExample
	 *            an instance of a TableViewerExample
	 */
	public FlagCellModifier(FlagTableViewer flagTableViewer) {
		super();
		this.flagTableViewer = flagTableViewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 *      java.lang.String)
	 */
	public Object getValue(Object element, String property) {

		// Find the index of the column
		int columnIndex = flagTableViewer.getColumnNames().indexOf(property);

		Object result = null;
		Flag flag = (Flag) element;

		switch (columnIndex) {
		case 0:
			result = flag.getLocal();
			break;
		case 1:
			String stringValue = flag.getLocal();
			String[] choices = Flag.flagSDN();
			int i = choices.length - 1;
			while (!stringValue.equals(choices[i]) && i > 0)
				--i;
			result = new Integer(i);
			break;
		default:
			result = "";
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {

		// Find the index of the column
		int columnIndex = flagTableViewer.getColumnNames().indexOf(property);

		TableItem item = (TableItem) element;
		Flag flag = (Flag) item.getData();
		String valueString;

		switch (columnIndex) {
		case 0:
			flag.setLocal((String)value);
			break;
		case 1:
			valueString = flagTableViewer.getChoices(property)[((Integer) value)
					.intValue()].trim();
			if (!flag.getSdn().equals(valueString)) {
				flag.setSdn(valueString);
			}
			break;
		default:
		}
		flagTableViewer.getFlagTable().flagChanged(flag);
	}
}
