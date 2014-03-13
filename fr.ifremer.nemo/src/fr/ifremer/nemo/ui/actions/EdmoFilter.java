package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.ifremer.nemo.config.EdmoEntity;

public class EdmoFilter extends ViewerFilter {

	private String searchString;

	public void setSearchText(String s) {
		// ensure that the value can be used for matching
		this.searchString = ".*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		EdmoEntity p = (EdmoEntity) element;
		if (p.getName().toLowerCase().matches(searchString.toLowerCase())) {
			return true;
		}
		if (p.getCountry().toLowerCase().matches(searchString.toLowerCase())) {
			return true;
		}

		if (p.getCode().toString().matches(searchString.toLowerCase())) {
			return true;
		}
		return false;
	}
}
