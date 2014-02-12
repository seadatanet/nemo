package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import fr.ifremer.nemo.config.EdmoEntity;

public class EdmoLabelProvider implements ITableLabelProvider {

	public EdmoLabelProvider() {
		super();
	}

	@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String result = null;
		switch (columnIndex) {
		case 0:
			result = ((EdmoEntity) element).getCode().toString();
			break;

		case 1:
			result = ((EdmoEntity) element).getName();
			break;

		case 2:
			result = ((EdmoEntity) element).getCountry();
			break;
		default:
			result = "?";
		}

		return result;
	}

}
