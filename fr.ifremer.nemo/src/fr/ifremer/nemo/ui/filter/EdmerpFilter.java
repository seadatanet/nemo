package fr.ifremer.nemo.ui.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.ifremer.nemo.config.EdmerpEntity;

/**
 * Filter for Survey selection dialog.
 */
public class EdmerpFilter extends ViewerFilter {
	
	private String searchString;
	
	/**
	 * To search the pattern in a string.
	 * @param s String to search.
	 */
	public void setSearchText(String s) {
		
		// case insensitive, search everywhere within the string.
		this.searchString = "(?i).*" + s + ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		
		EdmerpEntity ee = (EdmerpEntity) element;
		
		if (String.valueOf(ee.getCode()).matches(searchString)) {
			return true;
		}
		
		if (ee.getName().matches(searchString)) {
			return true;
		}
		
		return false;
	}

}
