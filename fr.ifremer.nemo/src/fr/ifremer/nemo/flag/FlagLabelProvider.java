package fr.ifremer.nemo.flag;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * Label provider for the TableViewerExample
 * 
 * @see org.eclipse.jface.viewers.LabelProvider 
 */
public class FlagLabelProvider 
	extends LabelProvider
	implements ITableLabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		Flag flag = (Flag) element;
		switch (columnIndex) {
			case 0:
				result = flag.getLocal();
				break;
			case 1 :
				result = flag.getSdn();
				break;
			default :
				break; 	
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
//		return (columnIndex == 0) ?   // COMPLETED_COLUMN?
//			getImage(((ExampleTask) element).isCompleted()) :
//			null;
		return null;
	}

}