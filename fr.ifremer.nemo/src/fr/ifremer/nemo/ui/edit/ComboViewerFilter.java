package fr.ifremer.nemo.ui.edit;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

import fr.ifremer.nemo.util.Global;

public class ComboViewerFilter extends ViewerFilter {

	/**
	 * The labelProvider of ComboViewer Parent
	 */
	LabelProvider labelProvider;
	ComboViewer comboViewer;
	ComboViewerKeyAdapter comboViewerKeyAdapter;


	MatchTypeEnum matchType;

	public static enum MatchTypeEnum{
		BEGINS_WITH, CONTAINS, ENDS_WITH, EXACT;

		String emptyString = Global.EMPTY_STRING;
		String anyString = Global.ANY_STRING;

		/**
		 * Return the startString of the search
		 * @return
		 */
		public String getStartString() {

			String startString = Global.EMPTY_STRING;

			switch (this) {
			case BEGINS_WITH :
				startString = emptyString;
				break;
			case CONTAINS :
				startString = anyString;
				break;
			case ENDS_WITH :
				startString = anyString;
				break;
			case EXACT :
				startString = emptyString;
				break;
			default :
				startString = emptyString;
				break;
			}

			return startString;

		}

		/**
		 * Return the end string of the search
		 * @return
		 */
		public String getEndString() {

			String endString = Global.EMPTY_STRING;

			switch (this) {
			case BEGINS_WITH :
				endString = anyString;
				break;
			case CONTAINS :
				endString = anyString;
				break;
			case ENDS_WITH :
				endString = emptyString;
				break;
			case EXACT :
				endString = emptyString;
				break;
			default :
				endString = emptyString;
				break;
			}

			return endString;

		}

	}


	/**
	 * The search String in combo
	 */
	private String searchStr = "";

	public ComboViewerFilter(ComboViewer comboViewer, MatchTypeEnum matchType) {
		super();
		this.comboViewer = comboViewer;
		this.labelProvider = (LabelProvider) comboViewer.getLabelProvider();
		this.matchType = matchType;

		// Add KeyListner
		comboViewerKeyAdapter = new ComboViewerKeyAdapter(comboViewer, this);
		comboViewer.getCombo().addKeyListener(comboViewerKeyAdapter);

		comboViewer.getCombo().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void focusLost(FocusEvent e) {
				ComboViewerFilter.this.searchStr = "";
				ComboViewerFilter.this.comboViewer.refresh();
			}

		});
	}



	public void setSearchText(String str) {
		// ensure that the value can be used for matching 	
//		if (!str.isEmpty()) {
//			this.searchStr = matchType.getStartString() + str + matchType.getEndString();
//		} else {
//			this.searchStr = Global.ANY_STRING;
//		}
		
		this.searchStr = str;
	}


	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (element == null) {
			return false;
		}
		String searchStrFormat = searchStr;
		
		
		if (!searchStrFormat.isEmpty()) {
			searchStrFormat = matchType.getStartString() + searchStrFormat + matchType.getEndString();
		} else {
			searchStrFormat = Global.ANY_STRING;
		}

		
		if (searchStrFormat.contains("+")) {
			searchStrFormat = searchStrFormat.replace("+", "\\+");
		}
		if (searchStrFormat.contains("(")) {
			searchStrFormat = searchStrFormat.replace("(", "\\(");
		}
		if (searchStrFormat.contains(")")) {
			searchStrFormat = searchStrFormat.replace(")", "\\)");
		}
		if (searchStrFormat.contains("?")) {
			searchStrFormat = searchStrFormat.replace("?", "\\?");
		}
		String stringToCompare = labelProvider.getText(element);
		if (stringToCompare != null) {
			if ((stringToCompare.matches("(?i:" + searchStrFormat + ")"))) {
				return true;
			}
		}

		return false;
	}

} 