package fr.ifremer.nemo.ui.edit.station.autoinput.autoline;

import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.station.autoinput.GrpAutoInput;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Widget with line and column
 *
 */
public class AutoLineColumn extends AutoLine {

	protected Couple column; // CSV header

	public AutoLineColumn(GrpAutoInput parentAutoInput, int i, boolean unique, MetaInData enableInData, boolean withCursor) {
		super(parentAutoInput, i, unique, enableInData, withCursor);
		column = new Couple(null, (Messages.label_column + addedLabel), ComboType.NONE, Global.EMPTY_STRING);
	}

	@Override
	public void drawLine(Composite compo, boolean filled) {
		numberLine.draw(compo, filled);
		column.draw(compo, filled);
	}

	@Override
	public void specificButtonSetAction(int lineIdx, int deb, int fi) {
		String separator = RulesManager.getInstance().getSeparator();
		String csvLine = Utils.lignesBrutes.get(lineIdx - 1);

		// debut reel sur ligne
		deb = deb - Utils.taillesCumLignes[lineIdx - 1];

		// nbre caracs par rapport debut de ligne
		// fin reelle sur ligne
		fi = fi - Utils.taillesCumLignes[lineIdx - 1];

		int columnIndexStart = fr.ifremer.nemo.util.Utils.getColumnIndexInCsvLineFromCharIndex(csvLine, separator, deb);

		// TODO fi check if same column

		// remplissage colonne dans interface
		column.setText(String.valueOf(columnIndexStart));

		numberLine.getValueText().pack();
		column.getValueText().pack();
		// redraw selection in viewfile
		selectInViewFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		column.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void razValues() {
		super.razValues();
		column.razValues();
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		if (!column.isEmpty()) {
			empty = false;
		}
		return empty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		column.setEnabled(enabled);
	}

	/**
	 * Getter sur column.
	 * 
	 * @return the column
	 */
	public Couple getColumn() {
		return column;
	}

	@Override
	public String getValue() throws StringIndexOutOfBoundsException {

		String result = Global.EMPTY_STRING;
		if (numberLine.getText() != null && column.getText() != null && !column.getText().equals(Global.EMPTY_STRING)) {
			String ligne = getRawLine();
			int index = Integer.parseInt(column.getText());
			// Split CSV line to array (limit = -1 for last empty column option)
			String separator = RulesManager.getInstance().getSeparator();
			String[] parts = ligne.split(separator, -1);
			if ((index - 1) <= parts.length) {
				result = parts[index - 1];
			}
		}

		return result;
	}

	public void setValue(String line, String column) {
		numberLine.setText(line);
		this.column.setText(column);
		checkInDataOrHeader(line, null);
		// numberLine.setEnabled(!inData);
	}

	@Override
	public void selectInViewFile() {
		int lign, deb, fi;
		if (numberLine.getText().equals(Global.EMPTY_STRING)) {
			lign = getStationHeaderPos() + 1;
		} else {
			lign = Integer.parseInt(numberLine.getText());
		}

		String separator = RulesManager.getInstance().getSeparator();
		String csvLine = Utils.lignesBrutes.get(lign -1);
		int columnIndex = Integer.parseInt(column.getText());
		int[] startEnd = fr.ifremer.nemo.util.Utils.getStartEndIndexInCsvLineFromColumnIndex(csvLine, separator,
				columnIndex);

		deb = startEnd[0] + Utils.taillesCumLignes[lign - 1];
		fi = startEnd[1] + Utils.taillesCumLignes[lign - 1];

		Nemo.getInstance().getViewMain().getEditStationForm().getStationSourceViewer().getTextWidget()
		.setSelection(deb - 1, fi);
	}

	@Override
	protected boolean customCheckValues(StringBuilder logSb) {
		boolean ok = true;
		boolean result;

		String colonne = column.getText();

		// format des differents champs

		// TODO MHO
		// column
		result = column.checkValue();
		ok = ok && result;
		if (!result)
			logSb.append("==>" + Messages.Regexp_10);

		// tests des relations des champs
		if (!colonne.trim().equals(Global.EMPTY_STRING)) {
			if (Integer.parseInt(colonne) < 0) {
				ok = ok && false;
				logSb.append("==>" + Messages.Regexp_15);
			}
		}
		return ok;
	}

}
