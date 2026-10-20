/* ============================================================================
 * Nom du fichier	: AutoLine.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station.autoinput.autoline;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.Cursor;
import fr.ifremer.nemo.ui.edit.UIobject;
import fr.ifremer.nemo.ui.edit.station.StationSection;
import fr.ifremer.nemo.ui.edit.station.autoinput.GrpAutoInput;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: objet graphique contenant l'injection d'une "ligne"
 * 
 * @author dboulang
 */
public abstract class AutoLine implements UIobject {
	public static enum MetaInData {
		OBLIGATOIRE, INTERDIT, FACULTATIF;
	}

	private final GrpAutoInput parentAutoInput;
	private int lineIndex = 0;
	protected String addedLabel;
	protected Couple numberLine;
	protected Button buttonSet;
	protected Cursor cursor;
	protected boolean unique;
	protected MetaInData enableInData;
	protected boolean inData = false;

	public AutoLine(GrpAutoInput parentAutoInput, int i, boolean unique, MetaInData enableInData, boolean withCursor) {
		this.parentAutoInput = parentAutoInput;
		lineIndex = i;
		this.unique = unique;
		this.enableInData = enableInData;
		this.addedLabel = Global.EMPTY_STRING;

		if (!unique)
			addedLabel = Global.SPACE + (i + 1);

		numberLine = new Couple(null, (Messages.label_line + addedLabel), ComboType.NONE, Global.EMPTY_STRING);

		if (withCursor)
			cursor = new Cursor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(Composite compo, boolean filled) {
		Group group = new Group(compo, SWT.NONE);
		group.setText(Messages.label_position);
		group.setLayout(new GridLayout());

		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		Composite compo1 = drawLineCompo(group);
		compo1.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		drawLine(compo1, filled);

		Composite compo2 = drawSetCompo(group, 1);
		compo2.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		drawSet(compo2, 1);
	}

	public static Composite drawLineCompo(Group group) {
		Composite compo = new Composite(group, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(6).applyTo(compo);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

		return compo;
	}

	public abstract void drawLine(Composite compo, boolean filled);

	public static Composite drawSetCompo(Group group, int nbLines) {
		Composite compo = new Composite(group, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(nbLines + 1).applyTo(compo);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

		return compo;
	}

	// public abstract void drawSet(Composite compo, int i);

	public void drawSet(Composite compo, int i) {
		String addedLabel = Global.EMPTY_STRING;
		if (!unique)
			addedLabel = Global.SPACE + Messages.label_line + Global.SPACE + (i + 1);

		buttonSet = new Button(compo, SWT.NONE);
		buttonSet.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		buttonSet.setText(Messages.button_set + addedLabel);

		buttonSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent g) {
				buttonSetAction();
			}
		});
		
	}

	void buttonSetAction() {
		StyledText styledtext = Nemo.getInstance().getViewMain().getEditStationForm().getStationSourceViewer()
				.getTextWidget();
		int deb, fi, lineIdx;

		// on recupere les coordonnees de la selection
		deb = styledtext.getSelection().x;
		fi = styledtext.getSelection().y;
		// int carretOffset = styledtext.getCaretOffset();

		if (deb == fi) {
			// message d'erreur
			Utils.messageErreurAppend(Messages.Utils_26);
		} else {
			lineIdx = 1;
			// calcul de la ligne en fonction de la valeur de x
			// parcours du tableau des tailles de lignes cumulees
			for (int idx = 0; idx < Utils.lignesBrutes.size(); idx++) {
				if (deb == Utils.taillesCumLignes[idx]) {
					lineIdx = idx + 1;
					break;
				}
				if (idx == (Utils.lignesBrutes.size() - 1)) {
					lineIdx = idx + 1;
					break;
				}
				if ((deb >= Utils.taillesCumLignes[idx]) && (deb < Utils.taillesCumLignes[idx + 1])) {
					lineIdx = idx + 1;
					break;
				}
			}

			// test sur 1ère ligne de données ou Métadata
			boolean ligneOK = checkInDataOrHeader(String.valueOf(lineIdx), Global.SPACE);
			if (ligneOK) {
				// Convert Column=> Char / Char=> Column if needed
				MediaModel model = Nemo.getInstance().getDomain().getMedia().getModel();
				AutoLine autolineToSet = this;
				StationSection stationSection = this.getParentAutoInput().getParentSection();
				if (!inData) {
					if (model.isUseSeparatorInStationHeader() && this instanceof AutoLineStartEnd) {
						GrpAutoInput grpAutoInput = stationSection.convertAutoInputFromStartEndToColumn();
						autolineToSet = grpAutoInput.getLines().get(lineIndex);

					} else if (!model.isUseSeparatorInStationHeader() && this instanceof AutoLineColumn) {
						GrpAutoInput grpAutoInput = stationSection.convertAutoInputFromColumnToStartEnd();
						autolineToSet = grpAutoInput.getLines().get(lineIndex);
					}
				} else {
					if (!model.getSeparator().isEmpty() && model.getUseTableForCsvData() && this instanceof AutoLineStartEnd) {
						GrpAutoInput grpAutoInput = stationSection
								.convertAutoInputFromStartEndToColumn();
						autolineToSet = grpAutoInput.getLines().get(lineIndex);
					} else if ((model.getSeparator().isEmpty() || !model.getUseTableForCsvData()) && this instanceof AutoLineColumn) {
						GrpAutoInput grpAutoInput = stationSection.convertAutoInputFromColumnToStartEnd();
						autolineToSet = grpAutoInput.getLines().get(lineIndex);
					}
				}
				// réactivation du champ "ligne"
				autolineToSet.getNumberLine().setEnabled(ligneOK);
				// remplissage ligne dans interface
				autolineToSet.getNumberLine().setText(String.valueOf(lineIdx));
				Utils.messageErreur(Global.EMPTY_STRING);
				autolineToSet.specificButtonSetAction(lineIdx, deb, fi);
			}
		}
		if (cursor != null) {
			cursor.setX(Nemo.getInstance().getViewMain().getEditStationForm().getStationSourceViewer().getTextWidget()
					.getSelection().x);
			cursor.setY(Nemo.getInstance().getViewMain().getEditStationForm().getStationSourceViewer().getTextWidget()
					.getSelection().y);
		}
	}

	protected abstract void specificButtonSetAction(int lineIdx, int deb, int fi);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		numberLine.dispose();
		buttonSet.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void razValues() {
		inData = false;
		numberLine.razValues();
		if (cursor != null)
			cursor.reset();
	}

	public abstract boolean isEmpty();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled) {
		numberLine.setEnabled(enabled);
		buttonSet.setEnabled(enabled);
	}

	protected int getStationHeaderPos() {
		int stationHeader = getFileHeaderPos();
		stationHeader += Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines();
		// stationHeader += DataModel.stationModel.getNumberOfLines();
		return stationHeader;
	}

	protected int getFileHeaderPos() {
		int fileHeader = 0;
		if (!RulesManager.getInstance().isCruiseInADirectory()
				|| Nemo.getInstance().getViewMain().getEditFileForm().isMultiStationFileChecked()) {
			// fichier campagne
			fileHeader = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
			// fileHeader = DataModel.singleSurvey.getHeaderLinesNumber();
		}
		return fileHeader;
	}

	public boolean checkInDataOrHeader(String ligne, String sectionName) {
		// FAE 1687 (evol) : ajout du test "inFileHeader"
		boolean inStationHeader;
		boolean inFileHeader = false;
		boolean ligneOK = true;
		int lign = 0;

		if (ligne != null && !ligne.equals(Global.EMPTY_STRING)) {
			lign = Integer.parseInt(ligne);
			inData = (lign == (getStationHeaderPos() + 1));
			inFileHeader = ((lign <= (getFileHeaderPos())) && lign != 0);
			inStationHeader = (lign > (getFileHeaderPos())) && (lign <= (getStationHeaderPos()));
		} else {
			inData = true;
			inStationHeader = false;
		}

		if (!inFileHeader) {
			switch (enableInData) {
			case OBLIGATOIRE:
				// OK if inData
				if (!inData) {
					ligneOK = false;
					if (sectionName != null)
						Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.Regexp_19);
				}
				break;
			case INTERDIT:
				// OK if inHeader
				if (!inStationHeader || inData) {
					ligneOK = false;
					if (sectionName != null)
						Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.Regexp_21);
				}
				break;
			case FACULTATIF:
			default:
				// comportement FACULTATIF : OK if inData or inHeader
				if (!inData && !inStationHeader && lign != 0) {
					ligneOK = false;
					if (sectionName != null)
						Utils.messageErreurAppend(sectionName + Global.SPACE + Messages.Regexp_19);
				}
				Nemo.getInstance().getViewMain().getEditStationForm().actualiseEdition();
				break;
			}
		} else {
			ligneOK = false;
			Utils.messageErreurAppend(Messages.Regexp_22);
		}
		return ligneOK;
	}

	/**
	 * Getter sur inData.
	 * 
	 * @return the inData
	 */
	public MetaInData getEnableInData() {
		return enableInData;
	}

	/**
	 * Setter sur inData.
	 * 
	 * @param inData the inData to set
	 */
	public void setEnableInData(MetaInData inData) {
		this.enableInData = inData;
	}

	/**
	 * Getter sur buttonSet.
	 * 
	 * @return the buttonSet
	 */
	public Button getButtonSet() {
		return buttonSet;
	}

	/**
	 * Getter sur numberLine.
	 * 
	 * @return the numberLine
	 */
	public Couple getNumberLine() {
		return numberLine;
	}

	/**
	 * Getter sur inData.
	 * 
	 * @return the inData
	 */
	public boolean isInData() {
		return inData;
	}

	/**
	 * Getter sur cursor.
	 * 
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}

	public abstract String getValue() throws StringIndexOutOfBoundsException;

	/**
	 * Get the raw line from its current line number (this.numberLine) in the
	 * file.
	 * 
	 * Caution: - if this.numberLine is empty, gets the line corresponding to
	 * the station header - if this.numberLine is "0", gets the file name -
	 * otherwise, gets the line corresponding to this.numberLine
	 * 
	 * @return a raw line as a String.
	 */
	public String getRawLine() {

		String ligne = Global.EMPTY_STRING;
		if (numberLine.getText().equals(Global.EMPTY_STRING)) {
			ligne = Utils.lignesBrutes.get(getStationHeaderPos());
		} else if (numberLine.getText().equals(Global.ZERO)) {
			// la ligne correspond au nom du fichier
			if (Nemo.getInstance().getViewMain().getEditFileForm().isCruiseIsAFile()) {
				ligne = fr.ifremer.nemo.util.Utils
						.extraitFichierChemin(Nemo.getInstance().getViewMain().getEditFileForm().nomFichier);
			} else {
				File directory = new File(Nemo.getInstance().getViewMain().getEditFileForm().nomRepertoire);
				File[] subfiles = directory.listFiles();

				// Correction FAE 4076
				if (subfiles[0].isFile())
					ligne = subfiles[0].getName();
				else
					ligne = subfiles[0].listFiles()[0].getName();
			}
		} else {
			ligne = Utils.lignesBrutes.get(Integer.parseInt(numberLine.getText()) - 1);
		}

		return ligne;
	}

	public abstract void selectInViewFile();

	public boolean checkValues(String nomSection) {
		StringBuilder logSb = new StringBuilder("\nSection : " + nomSection + "\n");

		boolean ok = true;
		boolean result;

		// ligne
		if (!isInData()) {
			result = numberLine.checkValue();
			ok = ok && result;
			if (!result)
				logSb.append("==>" + Messages.Regexp_8);
		}

		ok = ok & customCheckValues(logSb);

		if (!ok)
			Utils.messageErreurAppend(logSb.toString());

		return ok;
	}

	protected abstract boolean customCheckValues(StringBuilder logSb);

	public int calculNbreLignes() {

		int nbre = 0, lignDepart, lignFin;

		lignDepart = 1;
		lignFin = 1;
		// calcul de la ligne en fonction de la valeur de x
		// parcours du tableau des tailles de lignes cumulees

		if (cursor.getX() == cursor.getY()) {
			Utils.messageErreurAppend(Messages.Utils_26);

		} else {
			// calcul ligne depart
			for (int i = 0; i < Utils.lignesBrutes.size(); i++) {

				if (cursor.getX() == Utils.taillesCumLignes[i]) {
					lignDepart = i + 1;
					break;
				}
				if (i == (Utils.lignesBrutes.size() - 1)) {
					lignDepart = i + 1;
					break;
				}
				if ((cursor.getX() >= Utils.taillesCumLignes[i]) && (cursor.getX() < Utils.taillesCumLignes[i + 1])) {
					lignDepart = i + 1;
					break;
				}

			}

			// calcul ligne fin
			for (int i = 0; i < Utils.lignesBrutes.size(); i++) {

				if (cursor.getY() == Utils.taillesCumLignes[i]) {
					lignFin = i + 1;
					break;
				}
				if (i == (Utils.lignesBrutes.size() - 1)) {
					lignFin = i + 1;
					break;
				}
				if ((cursor.getY() >= Utils.taillesCumLignes[i]) && (cursor.getY() < Utils.taillesCumLignes[i + 1])) {
					lignFin = i + 1;
					break;
				}

			}

			// remplissage nbreLignes dans interface
			nbre = (lignFin - lignDepart + 1);

		}

		return nbre;
	}

	public boolean checkPosition(int nbreLignes) {
		int ligneFin = Integer.parseInt(numberLine.getText()) + nbreLignes - 1;

		int enteteFichier = 0;
		if (!RulesManager.getInstance().isCruiseInADirectory()
				|| Nemo.getInstance().getViewMain().getEditFileForm().isMultiStationFileChecked()) {
			// fichier campagne
			enteteFichier = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
			// enteteFichier = DataModel.singleSurvey.getHeaderLinesNumber();
		}
		int ligneLimite = enteteFichier + Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines();
		// enteteFichier + DataModel.stationModel.getNumberOfLines();

		if (ligneFin > ligneLimite) {
			return false;
		}
		// else
		return true;
	}

	public GrpAutoInput getParentAutoInput() {
		return parentAutoInput;
	}

}
