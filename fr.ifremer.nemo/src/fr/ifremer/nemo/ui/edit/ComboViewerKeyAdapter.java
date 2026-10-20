package fr.ifremer.nemo.ui.edit;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;

public class ComboViewerKeyAdapter extends KeyAdapter {

	ComboViewer comboviewer;
	Combo combo;
	ComboViewerFilter filter;
	Pattern alphaNumPattern = Pattern.compile("[a-zA-Z0-9]+");

	public ComboViewerKeyAdapter(ComboViewer comboviewer, ComboViewerFilter filter) {
		super();

		this.comboviewer = comboviewer;
		this.combo = comboviewer.getCombo();
		this.filter = filter;
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		String enteredCharacterInStr = String.valueOf(keyEvent.character);
		boolean isAlphaNumeric = alphaNumPattern.matcher(enteredCharacterInStr).find();

		// if ((keyEvent.keyCode >= 97 && keyEvent.keyCode <= 122) ||
		// (keyEvent.keyCode >= 48 && keyEvent.keyCode <= 57) ||


		// alphanum or backspace or ctrl+v
		   if ((keyEvent.stateMask != SWT.CTRL && isAlphaNumeric) || keyEvent.keyCode == SWT.BS || (keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 118)) { // alphanum
			// if (getModifierKeyCode(keyEvent) != SWT.CTRL && keyEvent.keyCode
			// != SWT.CR) { // not enter

			int count1 = comboviewer.getCombo().getItemCount();

			String textField = combo.getText();
			filter.setSearchText(textField);
			comboviewer.refresh();

			if (keyEvent.keyCode == SWT.BS) { // if backspace, compute itemcount
				int count2 = comboviewer.getCombo().getItemCount();
				if (count1 != count2) {
					// if item count before and after backspace, close the list
					// before open for redraw size
					combo.setListVisible(false);
				}
			}
			combo.setListVisible(true);
			combo.select(0);
			combo.setText(textField);

			combo.setSelection(new org.eclipse.swt.graphics.Point(textField.length(), textField.length()));

		} else if (keyEvent.keyCode == SWT.CR) { // enter
			ISelection selection;
			int index = combo.getSelectionIndex();
			if (combo.getItemCount() != 0 && index != -1) {
				selection = new StructuredSelection(combo.getItem(index));
			} else {
				combo.setText("");
				selection = new StructuredSelection("");
			}
			comboviewer.refresh();
			System.out.println(selection.toString());
			comboviewer.setSelection(selection);

		}
	}

}