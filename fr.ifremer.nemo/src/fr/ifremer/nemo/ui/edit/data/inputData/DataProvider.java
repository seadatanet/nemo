package fr.ifremer.nemo.ui.edit.data.inputData;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.ifremer.nemo.Nemo;

public class DataProvider implements ITableLabelProvider{
	private final String separator;

	public DataProvider() {
		this.separator=Nemo.getInstance().getDomain().getMedia().getModel().getSeparator();
	}
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String res="";
		try{
			res= ((String)element).split(separator)[columnIndex];
		}catch (Exception e) {
			Nemo.getInstance().getLogger().debug("error on data, maybe a station header " + e);
		}
		return res;
	}

}
