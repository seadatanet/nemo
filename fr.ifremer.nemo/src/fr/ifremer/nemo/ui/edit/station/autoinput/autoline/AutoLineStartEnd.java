package fr.ifremer.nemo.ui.edit.station.autoinput.autoline;

import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.station.autoinput.GrpAutoInput;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Widget with line start & end
 *
 */
public class AutoLineStartEnd extends AutoLine {

	protected Couple start; // no CSV header
	protected Couple end; // no CSV header

	public AutoLineStartEnd(GrpAutoInput parentAutoInput, int i, boolean unique, MetaInData enableInData, boolean withCursor) {
		super(parentAutoInput, i, unique, enableInData, withCursor);
		start = new Couple(null, (Messages.label_start + addedLabel), ComboType.NONE, Global.EMPTY_STRING);
		end = new Couple(null, (Messages.label_end + addedLabel), ComboType.NONE, Global.EMPTY_STRING);

	}

	@Override
	public void drawLine(Composite compo, boolean filled) {
		numberLine.draw(compo, filled);
		start.draw(compo, filled);
		end.draw(compo, filled);
	}

	@Override
	public void specificButtonSetAction(int lineIdx, int deb, int fi) {
		// debut reel sur ligne
		deb = deb - Utils.taillesCumLignes[lineIdx - 1] + 1;

		// nbre caracs par rapport debut de ligne
		// fin reelle sur ligne
		fi = fi - Utils.taillesCumLignes[lineIdx - 1];
		
		// remplissage debut et fin dans interface
		start.setText(String.valueOf(deb));
		end.setText(String.valueOf(fi));

		numberLine.getValueText().pack();
		start.getValueText().pack();
		end.getValueText().pack();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		start.dispose();
		end.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void razValues() {
		super.razValues();
		start.razValues();
		end.razValues();
	}

	@Override
	public boolean isEmpty() {
		boolean empty = true;
		if (!start.isEmpty() && !end.isEmpty()) {
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
		start.setEnabled(enabled);
		end.setEnabled(enabled);
	}

	/**
	 * Getter sur start.
	 * 
	 * @return the start
	 */
	public Couple getStart() {
		return start;
	}

	/**
	 * Getter sur end.
	 * 
	 * @return the end
	 */
	public Couple getEnd() {
		return end;
	}
	
	@Override
	public String getValue() throws StringIndexOutOfBoundsException {
		return getValue(false);
	}

	public String getValue(boolean posVariable) throws StringIndexOutOfBoundsException {

		String result = Global.EMPTY_STRING;
		if (numberLine.getText() != null && start.getText() != null && !start.getText().equals(Global.EMPTY_STRING)
				&& end.getText() != null && !end.getText().equals(Global.EMPTY_STRING)) {

			String ligne = getRawLine();

			int iEnd = Integer.parseInt(end.getText());
			if (posVariable) {
				int index = Integer.parseInt(start.getText());
				String nextChar = Global.ZERO;
				boolean continueBoucle = true;
				while (continueBoucle && index < ligne.length()) {
					nextChar = ligne.substring(index, index + 1);
					if (index < iEnd || nextChar.equals(".") //$NON-NLS-1$
							|| nextChar.equals(",") //$NON-NLS-1$
							|| fr.ifremer.nemo.util.Utils.isNumeric(nextChar)) {
						index++;
					} else {
						continueBoucle = false;
					}
				}
				iEnd = index;
			}
			result = ligne.substring(Integer.parseInt(start.getText()) - 1, iEnd);
		}

		return result;
	}

	public void setValue(String line, String start, String end) {
		numberLine.setText(line);
		this.start.setText(start);
		this.end.setText(end);
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

		deb = Integer.parseInt(start.getText()) + Utils.taillesCumLignes[lign - 1];
		fi = Integer.parseInt(end.getText()) + Utils.taillesCumLignes[lign - 1];

		Nemo.getInstance().getViewMain().getEditStationForm().getStationSourceViewer().getTextWidget()
				.setSelection(deb - 1, fi);
	}

	@Override
	protected boolean customCheckValues(StringBuilder logSb) {
		boolean ok = true;
		boolean result;

		String debut = start.getText();
		String fin = end.getText();

		// format des differents champs

		// debut
		result = start.checkValue();
		ok = ok && result;
		if (!result)
			logSb.append("==>" + Messages.Regexp_10);

		// fin
		result = end.checkValue();
		ok = ok && result;
		if (!result)
			logSb.append("==>" + Messages.Regexp_12);

		// tests des relations des champs
		if (!debut.trim().equals(Global.EMPTY_STRING) && (!fin.trim().equals(Global.EMPTY_STRING))) {
			if (Integer.parseInt(debut) > Integer.parseInt(fin)) {
				ok = ok && false;
				logSb.append("==>" + Messages.Regexp_15);
			}
		}
		return ok;
	}

}
