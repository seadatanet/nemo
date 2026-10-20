package fr.ifremer.nemo.ui.actions;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;

import fr.ifremer.nemo.config.EdmoEntity;

public class EdmoSorter extends ViewerSorter {

	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	private final int sortIdentifier;

	public static final int SORT_CODE = 0;
	public static final int SORT_NAME = 1;
	public static final int SORT_COUNTRY = 2;
	public static final int SORT_EXIST = 3;

	public EdmoSorter(int sortIdentifier, int dir) {
		this.propertyIndex = 0;
		this.sortIdentifier = sortIdentifier;
		this.direction = dir;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		/**
		 * Returns a negative, zero, or positive number depending on whether the
		 * first element is less than, equal to, or greater than the second
		 * element.
		 */
		EdmoEntity p1 = (EdmoEntity) e1;
		EdmoEntity p2 = (EdmoEntity) e2;
		int rc = 0;
		switch (sortIdentifier) {
		case 0:
			rc = p2.getCode().compareTo(p1.getCode());
			break;
		case 1:
			rc = p1.getName().compareTo(p2.getName());
			break;
		case 2:
			rc = p1.getCountry().compareTo(p2.getCountry());
			break;
		case 3:
			rc = p1.getOrgExist().compareTo(p2.getOrgExist());
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (this.direction == SWT.DOWN) {
			rc = rc * -1;
		}

		return rc;
	}

}
