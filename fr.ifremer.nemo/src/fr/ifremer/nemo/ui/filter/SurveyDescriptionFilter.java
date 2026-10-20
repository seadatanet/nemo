package fr.ifremer.nemo.ui.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fr.ifremer.nemo.ws.model.SurveyDescription;

/**
 * Filter for Survey selection dialog.
 */
public class SurveyDescriptionFilter extends ViewerFilter {
	
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
		
		SurveyDescription survey = (SurveyDescription) element;
		
		if (survey.getReference().matches(searchString)) {
			return true;
		}
		
		if (survey.getName().matches(searchString)) {
			return true;
		}
		
		return false;
	}

}
