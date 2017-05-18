package fr.ifremer.nemo.ui.edit.data.inputData;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

public class DataColumnLabelProvider extends ColumnLabelProvider {
	final Color red = new Color(null, 255,0,0);
	final Color white = new Color(null, 255,255,255);
	String separator;
	
	public DataColumnLabelProvider(String separator) {
		this.separator=separator;
	}
	  @Override
	  public Color getBackground(Object element) {
		  return red;
	  }

	 
}
