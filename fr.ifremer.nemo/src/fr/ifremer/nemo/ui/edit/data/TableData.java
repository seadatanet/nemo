package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.ui.FilteredTableFactory;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.ConversionEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.DnDSortingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.DnDSortingSupport.IModelManipulator;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.FormatEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.InputValEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.InstrumentCodeEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.InstrumentEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.LongNameEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.TextEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.UnitEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.UserLabelEditingSupport;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.ValidationCellLabelProvider;
import fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit.ValueOutEditingSupport;
import fr.ifremer.nemo.ui.tables.FormatsFilteredTable;
import fr.ifremer.nemo.ui.tables.InstrumentFilteredTable;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.StandardNameFilteredTable;
import fr.ifremer.nemo.ui.tables.UnitFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.model.Parameter;
import fr.ifremer.sismer_tools.DateTools;
import fr.ifremer.sismer_tools.SismerToolsException;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

public class TableData {
	final Color red = new Color(null, 239, 180, 110);
	final Color red2 = new Color(null, 209, 150, 180);
	final Color white = new Color(null, 255, 255, 255);

	/**
	 * Parametres provenant des web services (fichiers xml mis à jour par les
	 * web services) les parametres P091 sont lus depuis le fichier
	 * parameters.xml pour les autres, il faut lire le fichier Pxxx.xml
	 */
	private ArrayList<TypeData> typesWS;
	/**
	 * Parametres P061 provenant des web services (fichier P061.xml mis à jour
	 * par les web services) ces parametres sont utilisés uniquement dans le cas
	 * où on est en P011viaP021, pour affecter une unité
	 */
	private ArrayList<TypeUnit> typesP061;
	private ArrayList<TypeInstrument> typesL22;
	private ArrayList<TypeFallRate> typesL33;
	/**
	 * formats output
	 */
	private ArrayList<TypeFormat> typesFormat;
	HashMap<Integer, Integer> mapColLineIndexes;
	// HashMap<Integer, TypeData> inputColumnTypeMap;

	/**
	 * Getter sur typesFormat.
	 * 
	 * @return the typesFormat
	 */
	public ArrayList<TypeFormat> getTypesFormat() {
		return typesFormat;
	}

	/**
	 * Setter sur typesFormat.
	 * 
	 * @param typesFormat the typesFormat to set
	 */
	public void setTypesFormat(ArrayList<TypeFormat> typesFormat) {
		this.typesFormat = typesFormat;
	}

	/**
	 * Parametres à afficher dans le tableau de l'onglet data
	 */
	protected ArrayList<TypeData> types;

	/**
	 * la table contenant les parametres, affichée dans l'onglet data
	 */
	private Composite tableComposite;
	protected TableViewer tableViewer;
	private TableColumnLayout tableColumnLayout;

	/**
	 * Allow selection in InputTableData
	 */
	boolean waitingInputTableDataSelectionStartEnd = false;
	boolean waitingInputTableDataSelectionFlag = false;

	/**
	 * reference sur le panneau de choix des parametres
	 */
	private ParameterFilteredTable myParameterFilteredTable;

	private InstrumentFilteredTable myInstrumentTable;

	// Parameter Table Column
	TableViewerColumn isStringColumn;
	TableViewerColumn standardNameColumn;
	TableViewerColumn longNameColumn;
	TableViewerColumn userLabelColumn;
	TableViewerColumn valOutColumn;
	TableViewerColumn startColumn;
	TableViewerColumn endColumn;
	TableViewerColumn columnColumn;
	TableViewerColumn flagStartColumn;
	TableViewerColumn flagEndColumn;
	TableViewerColumn flagColumnColumn;

	/**
	 * constructeur initialise automatiquement les typesWS
	 */
	private final GroupVerticalParameters myGroupVerticalParameters;

	public TableData() {
		initTypesWS();
		// initColumnsMap();

		myGroupVerticalParameters = Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
	}

	public void reinitTable() {
		initTypeData();
		refreshTableColumnLayout();
		tableViewer.refresh();
	}

	public void refreshTable() {
		tableViewer.refresh();
	}

	public void createTableData() {
		initTypeData();

		if (!(tableComposite == null || tableComposite.isDisposed())) {
			tableComposite.dispose();
		}
		createTableViewer();
	}

	/**
	 * init parameter list & selectable parameter list
	 */
	public void initTypeData() {
		initTypesWS();
		reinitTypes();
	}

	public void setMyParameterFilteredTable(ParameterFilteredTable paramTable) {
		myParameterFilteredTable = paramTable;
	}

	/**
	 * reinitialise la liste des paramètres de la table
	 */
	public void reinitTypes() {
		if (types == null) {
			types = new ArrayList<TypeData>();
		} else {
			types.clear();
		}
	}

	/**
	 * ajoute un parametre à la liste des parametres de la table
	 * 
	 * @param typeData
	 */
	public void addType(TypeData typeData) {
		types.add(typeData);
		myGroupVerticalParameters.addTypeData(typeData);
	}

	/**
	 * chargement des parametres pour l'affichage de la liste de choix des
	 * parametres (menu Parameter List du tableau de l'onget data)
	 */
	public void initTypesWS() {
		typesWS = new ArrayList<TypeData>();
		if (RulesManager.getInstance().getMyDataListType() != DataListTypeEnum.P011_VIA_P021)

		{
			iniTypesWSP091();
		} else

		{
			iniTypesWSP021();
		}
	}

	private void iniTypesWSP091() {
		typesWS = getTypesWSP091();
	}
	
	private ArrayList<TypeData> getTypesWSP091() {
		ArrayList<TypeData> outputList = new ArrayList<TypeData>();
		
		Parameter[] parameters = NemoLists.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			outputList.add(new TypeData(parameters[i]));
		}
		
		return outputList;
	}

	private void iniTypesWSP021() {
		typesWS = getTypesWSP021();
	}
	
	private ArrayList<TypeData> getTypesWSP021() {
		
		ArrayList<TypeData> outputList = new ArrayList<TypeData>();
		
		// init des données a partir du fichier P021 p021.xml
		Collection<IElement> listP021;
		SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		try {
			listP021 = sdnVocabsMgr.getVocabList(Intern.list_P021).getElements();

			TypeData type;

			for (IElement record : listP021) {
				type = new TypeData(record);
				type.setIsP011(false);
				// ajout dans liste
				outputList.add(type);
			}
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
		
		return outputList;
	}

	/**
	 * initialize the P061 types to use as units choice in P011viaP021 case
	 */
	public void initTypesP061() {
		SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		try {
			if (typesP061 == null) {
				typesP061 = new ArrayList<TypeUnit>();
				// init des données a partir du fichier P021 p021.xml
				Collection<IElement> listP061 = sdnVocabsMgr.getVocabList(Intern.list_P061).getElements();
				TypeUnit typeUnit;
				for (IElement record : listP061) {
					typeUnit = new TypeUnit(record);
					typesP061.add(typeUnit);
				}
			}
			Collections.sort(typesP061);
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
	}

	/**
	 * initialize the L22
	 */
	public void initTypesL22() {
		SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		try {
			if (typesL22 == null) {
				typesL22 = new ArrayList<TypeInstrument>();
				Collection<IElement> listL22 = sdnVocabsMgr.getVocabList(Intern.list_L22).getElements();
				TypeInstrument typeInstrument;
				for (IElement record : listL22) {
					typeInstrument = new TypeInstrument(record);
					typesL22.add(typeInstrument);
				}
			}
			Collections.sort(typesL22);
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
	}

	/**
	 * initialize the L22
	 */
	public void initTypesL33() {
		SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		try {
			if (typesL33 == null) {
				typesL33 = new ArrayList<TypeFallRate>();
				Collection<IElement> listL33 = sdnVocabsMgr.getVocabList(Intern.list_L33).getElements();
				TypeFallRate typeFallrate;
				for (IElement record : listL33) {
					typeFallrate = new TypeFallRate(record);
					typesL33.add(typeFallrate);
				}
			}
			Collections.sort(typesL33);
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}
	}

	/**
	 * fill types
	 */
	public void fillTypes() {
		// initialisation de la ArrayList des types des donnees e partir du
		types = null;
		types = new ArrayList<TypeData>();
		try {
			List<TypeData> specificParams = VariantsSpecificsParameters.getInstance().getParameters();
			for (TypeData td : specificParams) {
				addType(td);
			}

			tableViewer.setInput(types);
			refreshTable();
		} catch (Exception e) {
			Utils.messageErreur(e.getMessage());
		}
	}

	/**
	 * ouvre la fenetre de choix de parametres
	 */
	public void openMyParameterFilteredTable() {
		getMyParameterFilteredTable()
		.open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
	}

	private boolean isUnitP09() {
		return RulesManager.getInstance().isDataListTypeP091()
				|| RulesManager.getInstance().isDataListTypeP011viaP091();
	}

	public void supprAllEltTableData() {
		// majTypeData();
		types.clear();
		myGroupVerticalParameters.removeAllToAllCombos();
		// refresh de la table
		refreshTable();
	}

	public void deleteTypeData(TypeData typeDataToDelete) {
		// // suppression de la ligne selectionnee
		if (myGroupVerticalParameters.getSelection().contains(typeDataToDelete.getEntryKey())) {
			Nemo.getInstance().getViewMain().getEditDataForm().resetFallRate();
			Nemo.getInstance().getViewMain().getEditDataForm().setFallrateGroupEnabled(false);
		}

		types.remove(typeDataToDelete);
		if (RulesManager.getInstance().getMyVerticalParametersList().getP_depthBSSurf()
				.equals(typeDataToDelete.getEntryKeyP011())) {
			RulesManager.getInstance().getMyVerticalParametersList().setP_depthBSSurf(""); //$NON-NLS-1$
		}
		if (RulesManager.getInstance().getMyVerticalParametersList().getP_depthBSBed()
				.equals(typeDataToDelete.getEntryKeyP011())) {
			RulesManager.getInstance().getMyVerticalParametersList().setP_depthBSBed(""); //$NON-NLS-1$
		}
		if (RulesManager.getInstance().getMyVerticalParametersList().getP_heightASLvl()
				.equals(typeDataToDelete.getEntryKeyP011())) {
			RulesManager.getInstance().getMyVerticalParametersList().setP_heightASLvl(""); //$NON-NLS-1$
		}
		if (RulesManager.getInstance().getMyVerticalParametersList().getP_pressure()
				.equals(typeDataToDelete.getEntryKeyP011())) {
			RulesManager.getInstance().getMyVerticalParametersList().setP_pressure(""); //$NON-NLS-1$
		}
		myGroupVerticalParameters.removeToAllCombo(typeDataToDelete);
		tableViewer.refresh();
	}

	public void deleteAllTypeData() {
		types.clear();
		myGroupVerticalParameters.removeAllToAllCombos();
	}

	public void removeAllElts() {
		types.clear();
		// refresh de la table
		refreshTable();
	}

	private boolean isNumericField(int index) {
		return !isSelected(index);
	}

	/**
	 * complete if start is set
	 * 
	 * @param index
	 * @return
	 */
	private boolean isNonNumericFieldParamsComplete(int index) {
		// TODO
		// return
		// (!myTable.getItem(index).getText(colNbById.get(COL_START)).equals(Global.EMPTY_STRING));
		// return (!(types.get(index).getDebut()==null));
		return true;
	}

	/**
	 * complete if start and conversion are set
	 * 
	 * @param index
	 * @return
	 */
	private boolean isNumericFieldParamsComplete(int index) {
		// TODO
		// return
		// (!myTable.getItem(index).getText(colNbById.get(COL_START)).equals(Global.EMPTY_STRING))
		// &&
		// (!myTable.getItem(index).getText(colNbById.get(COL_CONVERSION)).equals(Global.EMPTY_STRING));
		//
		return (!types.get(index).getConversion().equals(Global.EMPTY_STRING));
	}

	private boolean isParameterReady(int index) {
		return (isNumericField(index) && isNumericFieldParamsComplete(index))
				|| (!isNumericField(index) && isNonNumericFieldParamsComplete(index));

	}

	private boolean isSelectedValueEqualsToDefaultIn(int index, String selection) {
		return selection.equals(types.get(index).getEntree().trim());

	}

	public boolean updateTestText() {

		boolean ok = true;

		int nbre;
		String selection = ""; //$NON-NLS-1$
		String ligne, resultat = ""; //$NON-NLS-1$
		String defaultValue = ""; //$NON-NLS-1$
		double resultJEP;

		nbre = types.size();
		MediaDomain mediaDomain = Nemo.getInstance().getDomain().getMedia();
		if (RulesManager.getInstance().isUsingTableForCsvData()) {
			// 33278: split as columns
			ArrayList<String> input = (ArrayList<String>) Nemo.getInstance().getViewMain().getEditDataForm()
					.getInputTableData().getViewer().getInput();
			ligne = input.get(0);

		} else {

			ligne = Utils.lignesBrutes
					.get(mediaDomain.getModel().getFileHeader() + mediaDomain.getStationHeaderNumberLines());
		}

		for (int i = 0; i < nbre; i++) {
			selection = ""; //$NON-NLS-1$
			defaultValue = ""; //$NON-NLS-1$
			String code;
			if (types.get(i).isP011()) {
				code = types.get(i).getEntryKeyP011() + Global.SEP + types.get(i).getEntryTermP011();
			} else {
				code = types.get(i).getFormattedCodeAndName();
			}
			if (isParameterReady(i)) {
				try {
					if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty()
							|| !RulesManager.getInstance().isUsingTableForCsvData()) {
						selection = ligne.substring(types.get(i).getStartIndex() - 1, types.get(i).getEndIndex())
								.trim();
					} else {
						// 33278: split as columns
						selection = ligne.split(
								Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit(),
								-1)[types.get(i).getColumnIndex() - 1].trim();
					}

				} catch (Exception e) {

					System.out.println("erreur substring " + code); //$NON-NLS-1$
					ok = false;
					if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty()
							|| !RulesManager.getInstance().isUsingTableForCsvData()) {
						Utils.messageErreurAppend(Messages.Etape_98 + " " + code); //$NON-NLS-1$	
					} else {
						Utils.messageErreurAppend(Messages.Etape_98_2 + " " + code); //$NON-NLS-1$	
					}
	
					break;
				}
				defaultValue = types.get(i).getSortie();
			}

			/**
			 * Parametres numeriques
			 */
			if (isNumericField(i) && isNumericFieldParamsComplete(i)) {
				// on a selectionne un debut et une fin et il y a une conversion
				// e faire

				// on formate puis on reaffiche la liste
				try {
					String formatString = types.get(i).getFormat();
					String type = ""; //$NON-NLS-1$
					try {
						if (formatString.equals(Global.DATETIME_CHOICE_ISO)) {
							type = Global.DATETIME_CHOICE_ISO;
						} else {
							type = formatString.substring(formatString.length() - 1);
							if (!(type.equals("f") || type.equals("F") || type.equals("d") || type.equals("D"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

								Utils.messageErreurAppend("Unrecognized format: " + formatString);// TODO //$NON-NLS-1$
								ok = false;
								resultat = Messages.error;

							}
						}
					} catch (Exception e) {
						Utils.messageErreurAppend("Format is empty, please choose one");// TODO //$NON-NLS-1$
						ok = false;
						resultat = Messages.error;
						throw e;
					}
					if (type.equals(Global.DATETIME_CHOICE_ISO)) {
						// FAE 10755 - gestion de la chaine de charactere
						if (isSelectedValueEqualsToDefaultIn(i, selection)) {
							resultat = defaultValue;
						} else {
							resultat = selection;
						}
					} else if (isSelectedValueEqualsToDefaultIn(i, selection)) {

						// entree par defaut, le test est la valeur de sortie
						// par defaut formatee
						String fmOut = Global.EMPTY_STRING;
						String outputDefaultValue = types.get(i).getSortie();

						// Float ou Decimal ?
						if ((type.equals("f") || type.equals("F")) && !outputDefaultValue.trim().isEmpty()) { //$NON-NLS-1$//$NON-NLS-2$
							fmOut = String.format(java.util.Locale.US, formatString,
									Double.parseDouble(outputDefaultValue));
						} else if ((type.equals("d") || type.equals("D")) && !outputDefaultValue.trim().isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
							fmOut = String.format(java.util.Locale.US, formatString,
									Integer.parseInt(outputDefaultValue));
						}

						if (fmOut.equals(outputDefaultValue)) {
							// ok
							resultat = fmOut;
						} else {
							// erreur
							ok = false;
							resultat = Messages.error;
						}

					}
					// selection n'est pas la valeur par defaut
					else {
						// valeur normale, on applique la formule puis formatage
						org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
						parser.addStandardFunctions();
						parser.addStandardConstants();

						// 11976
						Double selectionValue = null;
						try {
							// selectionValue = Float.valueOf(selection);
							selectionValue = Double.valueOf(selection);
						} catch (NumberFormatException e) {
							System.out.println("erreur conversion on parameter" + code); //$NON-NLS-1$
							ok = false;
							Utils.messageErreurAppend(Messages.ParameterModel_1 + " " + code); //$NON-NLS-1$
							Utils.messageErreurAppend("expected a numeric value, found \'" + selection + "'");// TODO //$NON-NLS-1$ //$NON-NLS-2$
							break;

						}
						parser.addVariable("x", selectionValue); //$NON-NLS-1$
						parser.parseExpression(types.get(i).getConversion());
						resultJEP = parser.getValue();

						if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
							resultat = String.format(types.get(i).getFormat(), new Double(resultJEP));
						} else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
							resultat = String.format(types.get(i).getFormat(),
									// (Double.valueOf(resultJEP))
									// .intValue()
									(Double.valueOf(resultJEP)).longValue());
						} else {
							// error
							ok = false;
							resultat = Messages.error;
						}
					}
				} catch (Exception e) {
					ok = false;
					resultat = Messages.error;

					// e.printStackTrace();
				}
			}
			/**
			 * Parametres non numeriques
			 */
			else if (!isNumericField(i) && isNonNumericFieldParamsComplete(i)) {
				// FAE 10755 - gestion de la chaine de charactere
				if (isSelectedValueEqualsToDefaultIn(i, selection)) {
					resultat = defaultValue;
				} else {
					resultat = selection;
				}
			}

			types.get(i).setTest(resultat);

			/**
			 * FLAGS
			 */
			if ((types.get(i).getFlagStartIndex() != -1 && types.get(i).getFlagEndIndex() != -1) || types.get(i).getFlagColumnIndex() != -1) {
				if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty()
						|| !RulesManager.getInstance().isUsingTableForCsvData()) {
					selection = ligne.substring(Integer.valueOf(types.get(i).getFlagStartIndex()) - 1,
							Integer.valueOf(types.get(i).getFlagEndIndex())).trim();
				} else {
					// 33278: split as columns
					
					String[] splitLine = ligne
							.split(Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit(), -1);
					int flagColIndex = Integer.valueOf(types.get(i).getFlagColumnIndex());
					if (flagColIndex <= splitLine.length) {
						selection = splitLine[flagColIndex - 1].trim();
					} else {
						Utils.messageErreurAppend(Messages.Etape_98_3 + " '" + types.get(i).getCodeToUse()); //$NON-NLS-1$	

						ok = false;
						resultat = Messages.error;
					}
				}

				types.get(i).setTestIn(selection);

				String flagSDN = Global.ZERO;
				ArrayList<Flag> flags = Nemo.getInstance().getDomain().getStation().getModel().getFlagTable()
						.getFlags();
				int size = flags.size();
				for (int j = 0; j < size; j++) {
					if (flags.get(j).getLocal().equals(types.get(i).getTestIn())) {
						flagSDN = fr.ifremer.nemo.util.Utils.extraitCode(flags.get(j).getSdn());
						break;
					}
				}

				types.get(i).setTestOut(flagSDN);
			} else {
				types.get(i).setTestIn(Global.EMPTY_STRING);
				types.get(i).setTestOut(Global.EMPTY_STRING);
			}
		}

		// refresh de la table
		refreshTable();

		return ok;
	}

	public boolean isSelected(int index) {
		return types.get(index).isText();
	}

	public ArrayList<TypeData> getTypesWS() {
		return typesWS;
	}

	public ArrayList<TypeData> getTypes() {
		return types;
	}

	public ParameterFilteredTable getMyParameterFilteredTable() {
		if (myParameterFilteredTable == null) {
			FilteredTableFactory filteredFactory = Nemo.getInstance().getViewMain().getMyFilteredTableFactory();
			myParameterFilteredTable = (ParameterFilteredTable) filteredFactory.go(
					Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent(), Global.PARAMETER,
					null);
		}
		return myParameterFilteredTable;
	}

	public InstrumentFilteredTable getMyInstrumentTable() {
		if (myInstrumentTable == null) {
			myInstrumentTable = new InstrumentFilteredTable();
		}
		return myInstrumentTable;
	}

	public void reinitMyParameterFilteredTable() {
		myParameterFilteredTable = null;
	}

	public UnitFilteredTable getMyUnitFilteredTable() {
		UnitFilteredTable theTable = new UnitFilteredTable();
		return theTable;
	}

	public FormatsFilteredTable getMyFormatFilteredTable() {
		FormatsFilteredTable theTable = new FormatsFilteredTable();
		return theTable;
	}

	public StandardNameFilteredTable getMyStandardNameFilteredTable() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		TypeData typeParameter = (TypeData) selection.getFirstElement();

		StandardNameFilteredTable theTable = new StandardNameFilteredTable(typeParameter,
				RulesManager.getInstance().getMyDataListType());
		return theTable;
	}

	public void setUnit(TypeUnit typeUnit) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();
		for (TypeData selectedTypeData : selectedTypeDataList) {
			selectedTypeData.setUnite(typeUnit.getTerm());
			if (!isUnitP09()) { // 25794
				selectedTypeData.setP06Label(typeUnit.getTerm());
			} else {

			}
			// FAE 6542
			selectedTypeData.setUniteCode(typeUnit.getUnitKey());

			// FAE50999: set the effective ISO format
			if (selectedTypeData.getUniteCode().contentEquals(SdnVocabularyManager.P06_TISO)
					&& selectedTypeData.getTest() != null && !selectedTypeData.getTest().isEmpty()) {
				try {
					selectedTypeData
					.setEffectiveIsoDateFormat(DateTools.detectIso(selectedTypeData.getTest()).getPattern());
				} catch (SismerToolsException e) {
					Utils.messageErreur(e.getMessage());
				}
			}

		}
		tableViewer.refresh();
	}

	public void setInstrument(TypeInstrument type) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();
		for (TypeData selectedTypeData : selectedTypeDataList) {
			selectedTypeData.setInstrument(type.getTerm());
			selectedTypeData.setInstrumentCode(type.getKey());
		}
		tableViewer.refresh();
	}

	public void setStandardName(TypeStandardName typeStandardName) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();
		for (TypeData selectedTypeData : selectedTypeDataList) {
			StandardNameParameterManager.getInstance().updateStandardName(selectedTypeData.getEntryKeyP011(),
					typeStandardName.getTerm());
		}
		tableViewer.refresh();
	}

	public void setFormat(TypeFormat typeFormat) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();
		for (TypeData selectedTypeData : selectedTypeDataList) {
			selectedTypeData.setFormat(typeFormat.getFormat());
			selectedTypeData.setSortie(typeFormat.getExample());
		}
		tableViewer.refresh();
	}

	public ArrayList<TypeUnit> getTypesP061() {
		return typesP061;
	}

	public TypeUnit getUnitByName(String unitName) {
		if (unitName != null) {
			for (TypeUnit unit : typesP061) {
				if (unitName.equals(unit.getTerm())) {
					return unit;
				}
			}
		}
		return null;
	}

	/**
	 * L22 list for instruments
	 * 
	 * @return
	 */
	public ArrayList<TypeInstrument> getTypesL22() {
		return typesL22;
	}

	public TypeInstrument getInstrumentByName(String instrumentName) {
		if (instrumentName != null) {
			for (TypeInstrument instrument : typesL22) {
				if (instrumentName.equals(instrument.getTerm())) {
					return instrument;
				}
			}
		}
		return null;
	}

	public TypeInstrument getInstrumentByCode(String instrumentCode) {
		if (instrumentCode != null) {
			for (TypeInstrument instrument : typesL22) {
				if (instrumentCode.equals(instrument.getKey())) {
					return instrument;
				}
			}
		}
		return null;
	}

	/**
	 * L33 list for fall rates
	 * 
	 * @return
	 */
	public ArrayList<TypeFallRate> getTypesL33() {

		if (typesL33 == null) {
			initTypesL33();
		}
		return typesL33;
	}

	/**
	 * Select in text area (no csv) from selected parameter
	 */
	public void selectInViewFileFromViewerParameterSelection() {

		SourceViewer sourceViewer = Nemo.getInstance().getViewMain().getEditDataForm().getDataSourceViewer();
		if (sourceViewer != null) {
			// FAE 9670 surlignage de la position des paramètres dans le fichier

			IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

			// Remove all styleRange
			sourceViewer.getTextWidget().setStyleRanges(new StyleRange[0]);

			@SuppressWarnings("unchecked")
			List<TypeData> selectedTypeDataList = selection.toList();
			for (TypeData selectedTypeData : selectedTypeDataList) {

				// TODO 33278: on garde la colonne start pour stocker le no de
				// colonne
				// pour les csv. La colonne end ne sert a rien et sera masquée
				int lign = 0;
				if (!RulesManager.getInstance().isCruiseInADirectory()
						|| Nemo.getInstance().getViewMain().getEditFileForm().isMultiStationFileChecked()) {
					// fichier campagne
					lign = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
				}
				lign += Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines() + 1;

				// Start / End
				int start = selectedTypeData.getStartIndex();
				int end = selectedTypeData.getEndIndex();
				start += Utils.taillesCumLignes[lign - 1];
				end += Utils.taillesCumLignes[lign - 1];
				// set styleRange for start/end
				StyleRange startEndStyleRange = new StyleRange();
				startEndStyleRange.start = start - 1;
				startEndStyleRange.length = (end - start + 1);
				startEndStyleRange.background = red;
				startEndStyleRange.borderStyle = SWT.BORDER_SOLID;
				startEndStyleRange.borderColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
				try {
					sourceViewer.getTextWidget().setStyleRange(startEndStyleRange);
				} catch (IllegalArgumentException iae) {
					// Utils.messageErreur(Messages.error_table_dataTypes);
					Utils.messageErreur("TODO java.lang.IllegalArgumentException: Index out of bounds"); //$NON-NLS-1$

					System.out.println("TODO java.lang.IllegalArgumentException: Index out of bounds"); //$NON-NLS-1$
				}

				// Flag
				if (selectedTypeData.getFlagStartIndex() != -1 && selectedTypeData.getFlagEndIndex() != -1) {
					int startFlag = selectedTypeData.getFlagStartIndex();
					int endFlag = selectedTypeData.getFlagEndIndex();
					startFlag += Utils.taillesCumLignes[lign - 1];
					endFlag += Utils.taillesCumLignes[lign - 1];
					// set styleRange for startFlag/endFlag
					StyleRange flagStyleRange = new StyleRange();
					flagStyleRange.start = startFlag - 1;
					flagStyleRange.length = (endFlag - startFlag + 1);
					flagStyleRange.background = red2;
					flagStyleRange.borderStyle = SWT.BORDER_SOLID;
					flagStyleRange.borderColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
					try {
						sourceViewer.getTextWidget().setStyleRange(flagStyleRange);
					} catch (IllegalArgumentException iae) {
						Utils.messageErreur("TODO java.lang.IllegalArgumentException: Index out of bounds"); //$NON-NLS-1$
						System.out.println("TODO java.lang.IllegalArgumentException: Index out of bounds"); //$NON-NLS-1$
					}
				}
			}
		}
	}

	// TO DELTE
	public HashMap<Integer, Integer> getMapColLineIndexes() {
		return mapColLineIndexes;
	}

	/**
	 * Color
	 */
	protected void selectDataFromViewerParameterSelection() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		TypeData firstSelectedTypeData = (TypeData) selection.getFirstElement();
		TableViewer inputTableViewer = Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer();
		if (firstSelectedTypeData != null) {
			int colNb = firstSelectedTypeData.getColumnIndex();
				Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().setIndexSelected(colNb - 1);	
				// Show selected column column
				if (colNb <= inputTableViewer.getTable().getColumnCount() && colNb > 0) {
					inputTableViewer.getTable().showColumn(inputTableViewer.getTable().getColumn(colNb - 1));
				}
				// Show selected flag column if exist
				int flagColNb = firstSelectedTypeData.getFlagColumnIndex();
				if (flagColNb > 0 && flagColNb <= inputTableViewer.getTable().getColumnCount()) {
					inputTableViewer.getTable().showColumn(inputTableViewer.getTable().getColumn(flagColNb - 1));
				}
			
		}

		TableItem firstLine = inputTableViewer.getTable().getItem(0);

		// Uncolor all columns
		int colCount = inputTableViewer.getTable().getColumnCount();

		for (int colCpt = 0; colCpt < colCount; colCpt++) {
			firstLine.setBackground(colCpt, white);
		}

		List<TypeData> selectedTypeDataList = selection.toList();
		for (TypeData selectedTypeData : selectedTypeDataList) {
			int colNb = selectedTypeData.getColumnIndex();
			firstLine.setBackground(colNb - 1, red);
			// Color column flag
			if (selectedTypeData.getFlagColumnIndex() != -1) {
				int colFlagNb = selectedTypeData.getFlagColumnIndex();
				firstLine.setBackground(colFlagNb - 1, red2);
			}
		}
	}

	private void createTableViewer() {
		tableComposite = new Composite(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData(), SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableViewer = new TableViewer(tableComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		tableComposite.setLayoutData(gridData);
		createColumns();
		createMenu();

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// activate the tooltip support for the viewer
		ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.RECREATE);

		tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		if (null != types) {
			tableViewer.setInput(types);
		} else {
			tableViewer.setInput(new ArrayList<TypeData>());
		}

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// FAE 9670 surlignage de la position des paramètres dans le
				// fichier
				if (RulesManager.getInstance().isUsingTableForCsvData()) {
					selectDataFromViewerParameterSelection();
				} else {
					selectInViewFileFromViewerParameterSelection();
				}
			}

		});

		// Add Drag and Drop support
		TypeModelManipulator manipulator = new TypeModelManipulator();
		DnDSortingSupport.addDnDSortingSupport(tableViewer, manipulator);

		// define layout for the viewer
		// GridData gridData = new GridData();
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}

	public TableViewer getViewer() {
		return tableViewer;
	}

	/**
	 * Create the columns for the table
	 */
	private void createColumns() {
		isStringColumn = createIsStringColumn(); // isString Column
		createCodeColumn(); // Code Column
		standardNameColumn = createStandardNameColumn(); // Standard name Column
		longNameColumn = createLongNameColumn(); // Long name Column
		userLabelColumn = createUserLabelColumn(); // User Label Column
		createUnitColumn(); // Unit Column
		createConversionColumn(); // Conversion Column
		createTestColumn(); // Test Column
		startColumn = createStartColumn(); // Start Column
		endColumn = createEndColumn(); // End Column
		columnColumn = createColumnColumn(); // Column Column
		createFormatColumn(); // Format Column
		createInputDefValueColumn(); // InputDefValue Column
		valOutColumn = createOutputDefValueColumn(); // OutputDefValue Column
		createTestInColumn(); // Test In Column
		createTestOutColumn(); // Test Out Column
		flagStartColumn = createStartFlagColumn(); // StartFlag Column
		flagEndColumn = createEndFlagColumn(); // EndFlag Column
		flagColumnColumn = createFlagColumnColumn(); // Flag Column Column
		createUnitCodeColumn(); // Unit Code Column
		createInstrumentColumn(); // Instrument Column
		createInstrumentCodeColumn(); // Instrument Code Column

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int style) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setAlignment(style);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private TableViewerColumn createIsStringColumn() {
		// COL_IS_STRING
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_is_string.toUpperCase(), 25, SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TypeData typeData = (TypeData) element;
				String isChecked;
				if (typeData.isText()) {
					isChecked = "\u2612"; //$NON-NLS-1$
				} else {
					isChecked = "\u2610"; //$NON-NLS-1$
				}
				return isChecked;
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});
		col.getColumn().setToolTipText(Messages.table_checkColumn_tooltip);
		col.setEditingSupport(new TextEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createCodeColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_code.toUpperCase(), 100, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String code;
					if (typeData.isP011()) {
						code = typeData.getEntryKeyP011() + Global.SEP + typeData.getEntryTermP011();

					} else {
						code = typeData.getFormattedCodeAndName();
					}
					cell.setText(code);
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createStandardNameColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_standard_name.toUpperCase(), 100,
				SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String standardName = ""; //$NON-NLS-1$
					if (RulesManager.getInstance().isConversionToNetCDF()) {
						String userStandardName = StandardNameParameterManager.getInstance()
								.getUserStandardName(typeData.getEntryKeyP011());
						if (userStandardName != null) {
							standardName = userStandardName;
						}
					}
					cell.setText(standardName);
				}
			}

		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createLongNameColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_long_name.toUpperCase(), 100, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String longName = ""; //$NON-NLS-1$
					if (RulesManager.getInstance().isConversionToNetCDF()) {
						String userLongName = NetCDFLongNameParameterManager.getInstance()
								.getUserLongName(typeData.getEntryKeyP011());
						if (userLongName != null) {
							longName = userLongName;
						}
					}
					cell.setText(longName);
				}
			}

		});
		col.setEditingSupport(new LongNameEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createUserLabelColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_userlabel.toUpperCase(), 100, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String label = ""; //$NON-NLS-1$
					if (typeData.isP011()) {
						if (RulesManager.getInstance().isConversionToODV()) {
							String userLabel = LabelParameterManager.getInstance()
									.getUserLabel(typeData.getEntryKeyP011());
							if (userLabel != null) {
								label = userLabel;
							}
							if (label.isEmpty()) {
								String specificLabel = LabelParameterManager.getInstance()
										.getSpecificParameterLabel(typeData.getEntryKeyP011());
								if (specificLabel != null) {
									label = specificLabel;
								}
							}
						}
					}
					cell.setText(label);
				}
			}

		});
		col.setEditingSupport(new UserLabelEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createUnitColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_unit.toUpperCase(), 90, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String unitLabel = null;
					if (!typeData.isText()) {
						if (isUnitP09()) { // 25794
							unitLabel = typeData.getUnite();
						} else {
							unitLabel = typeData.getP06Label();
						}
					}
					cell.setText(unitLabel);
				}
			}

		});
		col.setEditingSupport(new UnitEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createConversionColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.label_conversion.toUpperCase(), 70, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String conversion = ""; //$NON-NLS-1$
					if (typeData.isText()) {
						conversion = ""; //$NON-NLS-1$
					} else {
						conversion = typeData.getConversion();
					}
					cell.setText(conversion);

				}
			}

		});
		col.setEditingSupport(new ConversionEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createTestColumn() {
		TableViewerColumn col = createTableViewerColumn(Messages.label_test.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getTest());
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createStartColumn() {
		// Start
		TableViewerColumn col = createTableViewerColumn(Messages.label_start.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(String.valueOf(typeData.getStartIndex()));
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createEndColumn() {
		// End
		TableViewerColumn col = createTableViewerColumn(Messages.label_end.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(String.valueOf(typeData.getEndIndex()));
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createColumnColumn() {
		// Column
		TableViewerColumn col = createTableViewerColumn(Messages.label_column.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(String.valueOf(typeData.getColumnIndex()));
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createFormatColumn() {
		// Format
		TableViewerColumn col = createTableViewerColumn(Messages.label_format.toUpperCase(), 70, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					String format = null;
					if (!typeData.isText()) {
						format = typeData.getFormat();
					}
					cell.setText(format);
				}
			}
		});
		col.setEditingSupport(new FormatEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createInputDefValueColumn() {
		// Input Value
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_valueIn.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getEntree());
				}
			}
		});
		col.setEditingSupport(new InputValEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createOutputDefValueColumn() {
		// Output Value
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_valueOut.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(true) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getSortie());
				}
			}
		});
		col.setEditingSupport(new ValueOutEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createTestInColumn() {
		// test in
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_testIn.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getTestIn());
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createTestOutColumn() {
		// test out
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_testOut.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getTestOut());
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createStartFlagColumn() {
		// start flag
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_startFlag.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					if (typeData.getFlagStartIndex() != -1) {
						cell.setText(String.valueOf(typeData.getFlagStartIndex()));
					} else {
						cell.setText(Global.EMPTY_STRING);
					}
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createEndFlagColumn() {
		// end flag
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_endFlag.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					if (typeData.getFlagEndIndex() != -1) {
						cell.setText(String.valueOf(typeData.getFlagEndIndex()));
					} else {
						cell.setText(Global.EMPTY_STRING);
					}
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createFlagColumnColumn() {
		// column flag
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_columnFlag.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					if (typeData.getFlagColumnIndex() != -1) {
						cell.setText(String.valueOf(typeData.getFlagColumnIndex()));
					} else {
						cell.setText(Global.EMPTY_STRING);
					}
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createUnitCodeColumn() {
		// unit code
		TableViewerColumn col = createTableViewerColumn(Global.EMPTY_STRING.toUpperCase(), 0, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getUniteCode());
				}
			}
		});
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createInstrumentColumn() {
		// instrument
		TableViewerColumn col = createTableViewerColumn(Messages.table_label_intrument.toUpperCase(), 50, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getInstrument());
				}
			}
		});
		col.setEditingSupport(new InstrumentEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	private TableViewerColumn createInstrumentCodeColumn() {
		// instrument code
		TableViewerColumn col = createTableViewerColumn(Global.EMPTY_STRING.toUpperCase(), 0, SWT.LEFT);
		col.setLabelProvider(new ValidationCellLabelProvider(false) {

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				if (null != cell.getElement() && cell.getElement() instanceof TypeData) {
					TypeData typeData = (TypeData) cell.getElement();
					cell.setText(typeData.getInstrumentCode());
				}
			}
		});
		col.setEditingSupport(new InstrumentCodeEditingSupport(tableViewer));
		tableColumnLayout.setColumnData(col.getColumn(), new ColumnWeightData(0, col.getColumn().getWidth(), false));
		return col;
	}

	/**
	 * Refresh param column layout (change with conversion format)
	 */
	public void refreshTableColumnLayout() {
		if (!RulesManager.getInstance().isConversionToMedatlas()
				&& !RulesManager.getInstance().isConversionToNetCDF()) {
			isStringColumn.getColumn().setWidth(25);
			isStringColumn.getColumn().setResizable(false);
		} else {
			showColumn(isStringColumn, false);
		}

		boolean isNetCDF = RulesManager.getInstance().isConversionToNetCDF();
		showColumn(standardNameColumn, isNetCDF);
		showColumn(longNameColumn, isNetCDF);

		boolean isUserLabel = RulesManager.getInstance().isConversionToODV()
				&& !RulesManager.getInstance().isDataListTypeP091();
		showColumn(userLabelColumn, isUserLabel);

		boolean isMedatlas = RulesManager.getInstance().isConversionToMedatlas();
		showColumn(valOutColumn, isMedatlas);

		// Is CSV Mode for data
		boolean isCSV = RulesManager.getInstance().isUsingTableForCsvData();
		showColumn(startColumn, !isCSV);
		showColumn(endColumn, !isCSV);
		showColumn(columnColumn, isCSV);
		showColumn(flagStartColumn, !isCSV);
		showColumn(flagEndColumn, !isCSV);
		showColumn(flagColumnColumn, isCSV);
	}

	/**
	 * Show or hide column
	 * 
	 * @param column
	 * @param show
	 */
	private void showColumn(TableViewerColumn column, boolean show) {
		if (show) {
			column.getColumn().setWidth(100);
			column.getColumn().setResizable(true);
		} else {
			column.getColumn().setWidth(0);
			column.getColumn().setResizable(false);
		}
	}

	private void createMenu() {
		MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});
		Table table = tableViewer.getTable();
		Menu menu = contextMenu.createContextMenu(table);
		table.setMenu(menu);
	}

	/**
	 * Fill dynamic context menu
	 *
	 * @param contextMenu
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	protected void fillContextMenu(IMenuManager contextMenu) {
		// Show / Hide menu item
		IStructuredSelection selection = tableViewer.getStructuredSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();

		boolean isTypeDataSelected = !selection.isEmpty();// Is selection
		// Is CSV Mode
		boolean isCSV = RulesManager.getInstance().isUsingTableForCsvData();
		// Is Medatlas Conversion
		boolean isMedConv = RulesManager.getInstance().isConversionToMedatlas();
		// Is NetCDF conversion
		boolean isCDFConv = RulesManager.getInstance().isConversionToNetCDF();

		// Is P011_VIA_P021
		boolean isP09 = RulesManager.getInstance().isDataListTypeP091();
		// Is P011_VIA_P021
		boolean isP011viaP091 = RulesManager.getInstance().isDataListTypeP011viaP091();
		// Is P011_VIA_P021
		boolean isP011viaP021 = RulesManager.getInstance().isDataListTypeP011viaP021();

		// P09 & P01 parameter coes in input file
		boolean P09CodesInInputFile = Nemo.getInstance().getDomain().getMedia().isP09CodeInInputFile();
		boolean P01CodesInInputFile = Nemo.getInstance().getDomain().getMedia().isP01CodeInInputFile();

		// Check if all selections are numeric
		boolean isAllNum = true;
		for (TypeData selectedTypeData2 : selectedTypeDataList) {
			if (selectedTypeData2.isText()) {
				isAllNum = false;
				break;
			}
		}

		// is last selectedTpyeData has column
		boolean lastSelectedTypeDataHasColumn = false;
		// is last selectedTpyeData has flag column
		boolean lastSelectedTypeDataHasFlagColumn = false;
		if (isTypeDataSelected && isCSV) {
			TypeData lastSelectedTypeData = selectedTypeDataList.get(selectedTypeDataList.size() - 1);
			lastSelectedTypeDataHasColumn = lastSelectedTypeData.getColumnIndex() != 0;
			lastSelectedTypeDataHasFlagColumn = lastSelectedTypeData.getFlagColumnIndex() != -1;
		}

		// ========= Injecter debut/fin/colonne
		if (isTypeDataSelected) {
			String actionName;
			if (!isCSV) {
				actionName = Messages.menu_label_set_start_end;
			}else {
				actionName = Messages.menu_label_set_column;
			}
			createContextMenuAction(contextMenu, actionName, contextMenuInjectRunnable());
		}
		// ========= Auto fill next params column
		if (isTypeDataSelected & isCSV & (lastSelectedTypeDataHasColumn)) {
			createContextMenuAction(contextMenu, Messages.menu_label_autofillNextColumn,
					contextMenuAutoFillColumnEndRunnable(false));
		}
		// ========= Maj champ test
		createContextMenuAction(contextMenu, Messages.menu_label_updateTest, contextMenuMajRunnable());
		
		// ========= select instrument
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_instrumentList,
					contextMenuSetInstrumentRunnable());
		}

		// ========= delete instrument
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_deleteinstrument,
					contextMenuDeleteInstrumentRunnable());
		}
		// ========= Liste des unités P061 (P011 via P021 seulement)
		if (isTypeDataSelected & isP011viaP021 & isAllNum) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectP061Unit, contextMenuAddP061UnitRunnable());
		}
		// ========= Selection d'un format (P011 via P021 seulement)
		if (isTypeDataSelected & isAllNum) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectFormat, contextMenuSelectFormatRunnable());
		}
		// ========= Conversion
		if (isTypeDataSelected & isAllNum) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectConversion,
					contextMenuSelectConversionRunnable());
		}
		// ========= Input def value
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectInput, contextMenuSelectInputDefRunnable());
		}
		// ========= Output def value
		if (isTypeDataSelected & isMedConv) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectOutput,
					contextMenuSelectOutputDefRunnable());
		}
		// ========= Selection d'un Standard Names
		if (isTypeDataSelected & isCDFConv) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectStandardName,
					contextMenuSelectStandardNameRunnable());
		}
		
		// ========= Selection d'un Long Names
		if (isTypeDataSelected & isCDFConv) {
			createContextMenuAction(contextMenu, Messages.menu_label_selectLongName,
					contextMenuSelectLongNameRunnable());
		}
		// ========= separateur
		contextMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// ========= Ajout paramètre
		createContextMenuAction(contextMenu, Messages.menu_label_addParameter, contextMenuAddParameterRunnable());

		// ========= Auto Fill P09/P01 paramètres (#61128)
		//		- Si une sortie "P09" est demandée ET la ligne de P09 est sélectionnée dans l'onglet "File"
		//		- Si une sortie "P01 via P09" est demandée ET (la ligne de P09 ET/OU la ligne de P01 sont fournis dans l'onglet "File") ; si sortie MEDATLAS, P01 seul ne suffit pas, P09 est obligatoire.
		//		- Si une sortie "P01 via P02" est demandée ET au moins la ligne de P01 est fournie dans l'onglet "File"
		if ((isP09 & P09CodesInInputFile)
				|| (isP011viaP091 && !isMedConv && (P09CodesInInputFile || P01CodesInInputFile))
				|| (isP011viaP091 && isMedConv && (P09CodesInInputFile))
				|| (isP011viaP021 && (P09CodesInInputFile || P01CodesInInputFile))) {
			createContextMenuAction(contextMenu, Messages.menu_label_autofillParameters,
					autoFillParametersRunnable());
		}

		// ========= supprimer paramètre sélectionné
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_removeParam,
					contextMenuSelectDeleteParameterRunnable());
		}
		// ========= separateur
		contextMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		// ========= Injecter flag
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_setFlag, contextMenuInjectFlagRunnable());
		}
		// ========= Auto fill next params flag column
		if (isTypeDataSelected & isCSV & (lastSelectedTypeDataHasFlagColumn)) {
			createContextMenuAction(contextMenu, Messages.menu_label_autofillNextFlag,
					contextMenuAutoFillColumnEndRunnable(true));
		}
		// ========= supprimer flag
		if (isTypeDataSelected) {
			createContextMenuAction(contextMenu, Messages.menu_label_deleteFlag, contextMenuDeleteFlagRunnable());
		}
	}

	private Action createContextMenuAction(IMenuManager contextMenu, String label, Runnable runnable) {
		Action action = new Action(label) {
			@Override
			public void run() {
				runnable.run();
			}
		};
//		action.setImageDescriptor(getImageDescriptor("new.gif"));
		contextMenu.add(action);
		return action;
	}

	protected Runnable contextMenuInjectRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// set cursor to to tableviewer and editdataform composite
				tableViewer.getControl().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_APPSTARTING));
				if (RulesManager.getInstance().isUsingTableForCsvData()) {
					Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().getControl()
							.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
				} else {
					Nemo.getInstance().getViewMain().getEditDataForm().getDataSourceViewer().getTextWidget()
							.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_IBEAM));
				}
				waitingInputTableDataSelectionStartEnd = true;
			}
		};
		return runnable;
	}

	protected Runnable contextMenuAutoFillColumnEndRunnable(boolean flag) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String title;
				if (!flag) {
					title = Messages.menu_label_autofillNextColumn;
				} else {
					title = Messages.menu_label_autofillNextFlag;
				}
				SpinnerDialog dialog = new SpinnerDialog(Display.getCurrent().getActiveShell(), title,
						Messages.spinnerDialog_message);
				if (dialog.open() == Window.OK) {
					int incrementalValue = dialog.getValue();

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<TypeData> selectedTypeDataList = selection.toList();
					TypeData lastSelectedTypeData = selectedTypeDataList.get(selectedTypeDataList.size() - 1);
					@SuppressWarnings("unchecked")
					List<TypeData> typeDataList = (List<TypeData>) tableViewer.getInput();
					int lastSelectedTypeDataIndex = typeDataList.indexOf(lastSelectedTypeData);
					int increment = 0;
					for (int i = lastSelectedTypeDataIndex + 1; i < typeDataList.size(); i++) {
						increment++;
						TypeData typData = typeDataList.get(i);

						if (!flag) { // normal mode
							if (lastSelectedTypeData.getColumnIndex() != 0) {
								typData.setColumnIndex(
										lastSelectedTypeData.getColumnIndex() + increment * incrementalValue);
							}
						} else { // flag mode
							if (lastSelectedTypeData.getFlagColumnIndex() != -1) {
								int flagColumnIndex = lastSelectedTypeData.getFlagColumnIndex();
								typData.setFlagColumnIndex(flagColumnIndex + increment * incrementalValue);
							}
						}

					}
					tableViewer.refresh();
				}
			}
		};
		return runnable;
	}


	protected Runnable contextMenuMajRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				updateTestText();
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSetInstrumentRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				initTypesL22();
				getMyInstrumentTable()
						.open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
			}
		};
		return runnable;
	}

	protected Runnable contextMenuDeleteInstrumentRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				@SuppressWarnings("unchecked")
				List<TypeData> selectedTypeDataList = selection.toList();
				for (TypeData selectedTypeData : selectedTypeDataList) {
					// delete instrument
					selectedTypeData.setInstrument(""); //$NON-NLS-1$
					selectedTypeData.setInstrumentCode(""); //$NON-NLS-1$
				}
				tableViewer.refresh();
			}
		};
		return runnable;
	}

	protected Runnable contextMenuAddP061UnitRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				initTypesP061();
				getMyUnitFilteredTable()
						.open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSelectFormatRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				getMyFormatFilteredTable()
						.open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSelectConversionRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						Messages.edit_data_selectConversionDialog_title, Messages.edit_data_selectConversionDialog_text,
						null, null);
				int result = dlg.open();
				if (result == 0) { // if OK
					String conversionValue = dlg.getValue();

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<TypeData> selectedTypeDataList = selection.toList();
					for (TypeData selectedTypeData : selectedTypeDataList) {
						selectedTypeData.setConversion(conversionValue);
					}
					tableViewer.refresh();
				}
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSelectInputDefRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						Messages.edit_data_selectInputDialog_title, Messages.edit_data_selectInputDialog_text, null,
						null);
				int result = dlg.open();
				if (result == 0) { // if OK
					String inputValue = dlg.getValue();

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<TypeData> selectedTypeDataList = selection.toList();
					for (TypeData selectedTypeData : selectedTypeDataList) {
						selectedTypeData.setEntree(inputValue);
					}
					tableViewer.refresh();
				}
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSelectOutputDefRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						Messages.edit_data_selectOutputDialog_title, Messages.edit_data_selectOutputDialog_text, null,
						null);
				int result = dlg.open();
				if (result == 0) { // if OK
					String outputValue = dlg.getValue();

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<TypeData> selectedTypeDataList = selection.toList();
					for (TypeData selectedTypeData : selectedTypeDataList) {
						selectedTypeData.setSortie(outputValue);
					}
					tableViewer.refresh();
				}
			}
		};
		return runnable;
	}

	protected Runnable contextMenuSelectStandardNameRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				getMyStandardNameFilteredTable()
						.open(Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData().getParent());
			}
		};
		return runnable;
	}
	
	protected Runnable contextMenuSelectLongNameRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
						Messages.edit_data_selectLongNameDialog_title, Messages.edit_data_selectLongNameDialog_text, null,
						null);
				int result = dlg.open();
				if (result == 0) { // if OK
					String longNameValue = dlg.getValue();

					IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<TypeData> selectedTypeDataList = selection.toList();
					for (TypeData selectedTypeData : selectedTypeDataList) {
						NetCDFLongNameParameterManager.getInstance().updateLongName(selectedTypeData.getEntryKeyP011(),
								longNameValue);
					}
					tableViewer.refresh();
				}
			}
		};
		return runnable;
	}

	protected Runnable contextMenuAddParameterRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (RulesManager.getInstance().getMyDataListType() != DataListTypeEnum.UNKNOWN) {
					initTypesWS();
					openMyParameterFilteredTable();
				} else {
					Utils.messageErreur(Messages.edit_data_error_dataListUnknown);
				}
			}
		};
		return runnable;
	}
	
	protected Runnable autoFillParametersRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				autoFillParameters();
			}
		};
		return runnable;	
	}

	protected Runnable contextMenuSelectDeleteParameterRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				@SuppressWarnings("unchecked")
				List<TypeData> selectedTypeDataList = selection.toList();
				for (TypeData selectedTypeData : selectedTypeDataList) {
					deleteTypeData(selectedTypeData);
				}
				Utils.invalidEtapes();
			}
		};
		return runnable;
	}

	protected Runnable contextMenuInjectFlagRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// set cursor to to tableviewer and editdataform composite
				tableViewer.getControl().setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_APPSTARTING));
				if (RulesManager.getInstance().isUsingTableForCsvData()) {
					Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().getControl()
							.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
				} else {
					Nemo.getInstance().getViewMain().getEditDataForm().getDataSourceViewer().getTextWidget()
							.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_IBEAM));
				}

				waitingInputTableDataSelectionFlag = true;
			}
		};
		return runnable;
	}

	protected Runnable contextMenuDeleteFlagRunnable() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				@SuppressWarnings("unchecked")
				List<TypeData> selectedTypeDataList = selection.toList();
				for (TypeData selectedTypeData : selectedTypeDataList) {
					selectedTypeData.setTestIn(Global.EMPTY_STRING);
					selectedTypeData.setTestOut(Global.EMPTY_STRING);
					selectedTypeData.setFlagStartIndex(-1);
					selectedTypeData.setFlagEndIndex(-1);
					selectedTypeData.setFlagColumnIndex(-1);
				}

				tableViewer.refresh();
				Utils.invalidEtapes();
			}
		};
		return runnable;
	}

	/**
	 * Return true if context menu "select start/end"
	 * 
	 * @return
	 */
	public boolean isWaitingInputTableDataSelectionStartEnd() {
		return waitingInputTableDataSelectionStartEnd;
	}

	/**
	 * Return true if context menu "select flag"
	 * 
	 * @return
	 */
	public boolean isWaitingInputTableDataSelectionFlag() {
		return waitingInputTableDataSelectionFlag;
	}

	/**
	 * Set start/end from inputTableData selection in current tableviewer
	 * selection
	 */
	public void inputTableDataSelectionStartEndFlag(boolean flags) {
		// int debut = -1;
		// int fin = -1;
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<TypeData> selectedTypeDataList = selection.toList();

		if (RulesManager.getInstance().isUsingTableForCsvData()) {
			// 33278 CSV files
			int selectedColumnIndex = Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData()
					.getIndexSelected();
			int selectedColumnNumber = selectedColumnIndex + 1;
			// // remplissage debut et fin dans interface
			// debut = selectedColumnNumber;
			// fin = selectedColumnNumber;

			for (TypeData selectedTypeData : selectedTypeDataList) {
				if (!flags) {
					selectedTypeData.setColumnIndex(selectedColumnNumber);
				} else {
					selectedTypeData.setFlagColumnIndex(selectedColumnNumber);
				}
			}
			tableViewer.refresh();
			Utils.invalidEtapes();
			// FAE 9670 surlignage de la position des paramètres dans le fichier
			selectDataFromViewerParameterSelection();

		} else {
			int ligne = -1;
			int debut = -1;
			int fin = -1;
			int[] intervall = Utils.computeInterval(
					Nemo.getInstance().getViewMain().getEditDataForm().getDataSourceViewer().getTextWidget());
			if (intervall != null) {
				ligne = intervall[0];
				debut = intervall[1];
				fin = intervall[2];
			}

			if (debut != -1 && fin != -1) {
				for (TypeData selectedTypeData : selectedTypeDataList) {
					if (!flags) {
						selectedTypeData.setStartIndex(debut);
						selectedTypeData.setEndIndex(fin);
					} else {
						selectedTypeData.setFlagStartIndex(debut);
						selectedTypeData.setFlagEndIndex(fin);
					}
				}
				tableViewer.refresh();
				Utils.invalidEtapes();

				// FAE 9670 surlignage de la position des paramètres dans le
				// fichier
				selectInViewFileFromViewerParameterSelection();
			}

		}
		if (!flags) {
			waitingInputTableDataSelectionStartEnd = false;
		} else {
			waitingInputTableDataSelectionFlag = false;
		}
		tableViewer.getControl().setCursor(Display.getCurrent().getSystemCursor(SWT.DEFAULT));
	}

	/**
	 * Allows to insert elements before or after another element.
	 * 
	 * @author pmahoudo
	 *
	 */
	public class TypeModelManipulator implements IModelManipulator {

		@Override
		public boolean insertBefore(Object source, Object target) {
			// TODO FIXME MHO bug parfois ???
			IStructuredSelection selection = (IStructuredSelection) source;
			@SuppressWarnings("unchecked")
			List<TypeData> selectedTypeDataList = selection.toList();
			TypeData targetType = (TypeData) target;

			if (!selectedTypeDataList.contains(targetType)) {
				// remove source
				for (TypeData selectedTypeData : selectedTypeDataList) {
					int sourceIndex = types.indexOf(selectedTypeData);
					types.remove(sourceIndex);
				}

				// Add source to target index
				int targetIndex = types.indexOf(targetType);
				for (TypeData selectedTypeData : selectedTypeDataList) {
					types.add(targetIndex, selectedTypeData);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean insertAfter(Object source, Object target) {
			IStructuredSelection selection = (IStructuredSelection) source;
			@SuppressWarnings("unchecked")
			List<TypeData> selectedTypeDataList = selection.toList();
			TypeData targetType = (TypeData) target;

			if (!selectedTypeDataList.contains(targetType)) {
				// remove source
				for (TypeData selectedTypeData : selectedTypeDataList) {
					int sourceIndex = types.indexOf(selectedTypeData);
					types.remove(sourceIndex);
				}

				// Add source to target index
				int targetIndex = types.indexOf(targetType);
				for (TypeData selectedTypeData : selectedTypeDataList) {
					types.add(targetIndex + 1, selectedTypeData);
				}
				return true;
			}
			return false;
		}
	}

	/**
	 * Select typeData in current tableViewer from a column index in
	 * InputTableData
	 * 
	 * @param columnIndex
	 */
	public void selectTypeFromInputTableData(int columnIndex) {
		// unselect all before
		tableViewer.setSelection(StructuredSelection.EMPTY);

		List<TypeData> selectedTypeList = new ArrayList<TypeData>();
		for (TypeData typeData : types) {
			if (typeData.getColumnIndex() == columnIndex + 1 || typeData.getFlagColumnIndex() == columnIndex + 1) {
				selectedTypeList.add(typeData);
			}
		}

		StructuredSelection selection = new StructuredSelection(selectedTypeList);
		tableViewer.setSelection(selection, true);
	}

	/**
	 * Auto Fill typeData list (CSV & P09 or P01 via P09) with codes from the
	 * selected line in File Tab
	 */
	public void autoFillParameters() {

		boolean isParamCodeP09InInputFile = Nemo.getInstance().getDomain().getMedia().isP09CodeInInputFile();
		int paramCodeLineNbP09 = Nemo.getInstance().getDomain().getMedia().getP09CodeLineNb();
		List<String> codesP09Array = new ArrayList<String>();
		boolean hasP09Codes = false;

		boolean isParamCodeP01InInputFile = Nemo.getInstance().getDomain().getMedia().isP01CodeInInputFile();
		int paramCodeLineNbP01 = Nemo.getInstance().getDomain().getMedia().getP01CodeLineNb();
		List<String> codesP01Array = new ArrayList<String>();
		boolean hasP01Codes = false;

		// vocab manager objects
		SdnVocabularyManager sdnVocabsMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
		ICollection collection;

		// The CSV separator
		String separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparatorForSplit();

		// The entire text in the text editor, split by lines
		List<String> lignesBrutes = Arrays.asList(Utils.document.get().split("\n"));

		// P09 code list construction from input file
		if (isParamCodeP09InInputFile) {
			codesP09Array = Arrays.asList(lignesBrutes.get(paramCodeLineNbP09 - 1).split(separator));
			
			// trim elements
			codesP09Array.replaceAll(String::trim);
			
			for (String aCode : codesP09Array) {
				if (!aCode.isEmpty()) {
					hasP09Codes = true;
					break;
				}
			}
		}

		// P01 code list construction from input file
		if (isParamCodeP01InInputFile) {
			codesP01Array = Arrays.asList(lignesBrutes.get(paramCodeLineNbP01 - 1).split(separator));
			
			// trim elements
			codesP01Array.replaceAll(String::trim);
			
			for (String aCode : codesP01Array) {
				if (!aCode.isEmpty()) {
					hasP01Codes = true;
					break;
				}
			}
		}

		// The working TypeData (will check eligibility during the tests)
		TypeData selectedTypeData;

		// P09 are wanted in output file
		if (RulesManager.getInstance().isDataListTypeP091() || (hasP09Codes && !(RulesManager.getInstance().isDataListTypeP011viaP021()))) {

			// if containes codes, reset parameter list
			if (hasP09Codes) {
				boolean confirm = true;
				if (Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().size() > 0) {
					confirm = MessageDialog.openConfirm(null, Messages.edit_data_dataListDialogBox_title,
							Messages.edit_data_dataListDialogBox_text);
				}
				if (confirm) {
					reinitTable();

					Utils.invalidEtapes();

					// scan P09 codes from input files
					for (int i = 0; i < codesP09Array.size(); i++) {

						try {
							selectedTypeData = new TypeData();

							String p09Code = codesP09Array.get(i);

							boolean p01Found = false;
							boolean p09Found = false;
							boolean p06Found = false;

							// check code validity
							collection = sdnVocabsMgr.getVocabList(SdnVocabularyManager.LIST_P09);

							// check if exist in P09 vocab list
							if (collection.hasElementFromKey(p09Code)) {

								p09Found = true;

								// data from P09
								selectedTypeData.setEntryKey(p09Code);
								selectedTypeData
										.setEntryTerm(sdnVocabsMgr.getLabel(p09Code, SdnVocabularyManager.LIST_P09));

								// deduce P06 from P09 found in input file
								List<IElement> listeP06_P09 = sdnVocabsMgr.getVocabMatchs(Intern.list_P061,
										Intern.list_P091, p09Code);

								for (IElement p06Elt : listeP06_P09) {
									// data from P06
									p06Found = true;
									selectedTypeData.setUniteCode(p06Elt.getKey());
									selectedTypeData.setUnite(p06Elt.getLabel());
									selectedTypeData.setP06Label(p06Elt.getLabel());
									break;
								}

								// get directly P01 code from input file
								if (hasP01Codes) {
									String p01Code = codesP01Array.get(i);

									// check code validity
									collection = sdnVocabsMgr.getVocabList(SdnVocabularyManager.LIST_P01);

									if (collection.hasElementFromKey(p01Code)) {
										if (!(RulesManager.getInstance().isDataListTypeP091())) {
											selectedTypeData.setIsP011(true);
										}
										selectedTypeData.setEntryKeyP011(p01Code);
										selectedTypeData.setEntryTermP011(
												sdnVocabsMgr.getLabel(p01Code, SdnVocabularyManager.LIST_P01));

										p01Found = true;
									}

								}

								if (!p01Found) {

									// deduce P01 from P09 found in input file

									List<IElement> listeP01_P09 = sdnVocabsMgr.getVocabMatchs(Intern.list_P011,
											Intern.list_P091, p09Code);

									for (IElement p01Elt : listeP01_P09) {
										// data from P01
										p01Found = true;
										if (!(RulesManager.getInstance().isDataListTypeP091())) {
											selectedTypeData.setIsP011(true);
										}
										selectedTypeData.setEntryKeyP011(p01Elt.getKey());
										selectedTypeData.setEntryTermP011(p01Elt.getLabel());
										break; // first element
									}
								}
							}

							// Here we have an eligible TYPEDATA
							if (p09Found && p01Found && p06Found) {
								autofillAddTypeData(selectedTypeData, i);
							}
						} catch (VocabularyException | SismerToolsException e) {
							Nemo.getInstance().getLogger().error(e.getMessage());
						}

					}
				}
			}
		} else if (RulesManager.getInstance().isDataListTypeP011viaP091()
				|| RulesManager.getInstance().isDataListTypeP011viaP021()) {
			// P01 asked in output

			// if containes codes, reset parameter list
			if (hasP01Codes) {
				boolean confirm = true;
				if (Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().size() > 0) {
					confirm = MessageDialog.openConfirm(null, Messages.edit_data_dataListDialogBox_title,
							Messages.edit_data_dataListDialogBox_text);
				}
				if (confirm) {
					reinitTable();

					Utils.invalidEtapes();

					for (int i = 0; i < codesP01Array.size(); i++) {

						try {
							selectedTypeData = new TypeData();

							String p01Code = codesP01Array.get(i);

							boolean p01Found = false;
							boolean p02OrP09Found = false;
							boolean p06Found = false;

							// verif validite code
							collection = sdnVocabsMgr.getVocabList(SdnVocabularyManager.LIST_P01);

							// check if code exists in P01 vocab list
							if (collection.hasElementFromKey(p01Code)) {

								p01Found = true;

								selectedTypeData.setIsP011(true);
								selectedTypeData.setEntryKeyP011(p01Code);
								selectedTypeData.setEntryTermP011(
										sdnVocabsMgr.getLabel(p01Code, SdnVocabularyManager.LIST_P01));

								if (RulesManager.getInstance().isDataListTypeP011viaP091()) {
									// P09
									if (hasP09Codes) {
	
										// scan P09 codes given by the input file
										String p09Code = codesP09Array.get(i);
	
										// check if code exists in P09 vocab list
										collection = sdnVocabsMgr.getVocabList(SdnVocabularyManager.LIST_P09);
										if (collection.hasElementFromKey(p09Code)) {
	
											p02OrP09Found = true;
	
											selectedTypeData.setEntryKey(p09Code);
											selectedTypeData.setEntryTerm(
													sdnVocabsMgr.getLabel(p09Code, SdnVocabularyManager.LIST_P09));
										}
									} else {
										// Search P09 associated to P01 in mapping
										List<IElement> listeP09_P01 = sdnVocabsMgr.getVocabMatchs(Intern.list_P091,
												Intern.list_P011, p01Code);
	
										for (IElement p09Elt : listeP09_P01) {
											// data from P01
											p02OrP09Found = true;
											selectedTypeData.setEntryKey(p09Elt.getKey());
											selectedTypeData.setEntryTerm(p09Elt.getLabel());
											break; // first element
										}
									}
								}
								else {
									// P02
									String p02Code = Global.EMPTY_STRING;
									
									// Search P02 associated to P01 in mapping
									List<IElement> listeP02_P01 = sdnVocabsMgr.getVocabMatchs(Intern.list_P021,
											Intern.list_P011, p01Code);

									for (IElement p02Elt : listeP02_P01) {
										// data from P01
										p02OrP09Found = true;
										p02Code = p02Elt.getKey();
										selectedTypeData.setEntryKey(p02Code);
										selectedTypeData.setEntryTerm(p02Elt.getLabel());
										selectedTypeData.setFormat(Global.EMPTY_STRING);
										break; // first element
									}
									
									if (p02OrP09Found) {

										// P09 used for format.
										boolean p09Found = false;

										// P09 may be available in data
										if (hasP09Codes) {

											// scan P09 codes given by the input
											// file
											String p09Code = codesP09Array.get(i);

											// check if code exists in P09 vocab
											// list
											collection = sdnVocabsMgr.getVocabList(SdnVocabularyManager.LIST_P09);
											if (collection.hasElementFromKey(p09Code)) {

												p09Found = true;

												// Search format from P09
												for (TypeData typeDataWS : getTypesWSP091()) {

													if (typeDataWS.getEntryKey().equals(p09Code)) {

														selectedTypeData.setFormat(typeDataWS.getFormat());
														break;
													}
												}
											}
										}

										if (!p09Found) {

											// Get format from mapping P9/P2
											List<IElement> listeP09_P02 = sdnVocabsMgr.getVocabMatchs(Intern.list_P091,
													Intern.list_P021, p02Code);

											for (IElement p09Elt : listeP09_P02) {

												// Search format from P09
												for (TypeData typeDataWS : getTypesWSP091()) {

													if (typeDataWS.getEntryKey().equals(p09Elt.getKey())) {

														selectedTypeData.setFormat(typeDataWS.getFormat());
														break;
													}
												}
											}
										}
									}
								}

								// P06 from P01
								List<IElement> listeP06_P01 = sdnVocabsMgr.getVocabMatchs(Intern.list_P061,
										Intern.list_P011, p01Code);

								for (IElement p06Elt : listeP06_P01) {
									// data from P06
									p06Found = true;
									selectedTypeData.setUniteCode(p06Elt.getKey());
									selectedTypeData.setUnite(p06Elt.getLabel());
									selectedTypeData.setP06Label(p06Elt.getLabel());
									break;
								}
							}

							// Here we have an eligible TYPEDATA
							// we don't necessarily have a P09
							if (p02OrP09Found && p01Found && p06Found) {
								autofillAddTypeData(selectedTypeData, i);
							}
						} catch (VocabularyException | SismerToolsException e) {
							Nemo.getInstance().getLogger().error(e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Add new TypeData during Autofill.
	 * 
	 * @param typeData The TypeData (will be cloned).
	 * @param numCol the column number (starts at 0).
	 */
	private void autofillAddTypeData(TypeData typeData, int numCol) {

		TypeData addedTypeData = new TypeData(typeData);
		
		if (!(RulesManager.getInstance().isDataListTypeP011viaP021())) {

			addedTypeData.setFormat(Global.EMPTY_STRING);

			// get additional data from Parameters.xml (P09)
			for (TypeData typeDataWS : getTypesWSP091()) {

				if (typeDataWS.getEntryKey().equals(typeData.getEntryKey())) {

					addedTypeData.setFormat(typeDataWS.getFormat());

					if (!RulesManager.getInstance().isConversionToMedatlas()) {
						addedTypeData.setSortie(Global.EMPTY_STRING);
					} else {
						addedTypeData.setSortie(typeDataWS.getSortie());

						// Replace P06 label by the one in parameters
						addedTypeData.setUnite(typeDataWS.getUnite());
						addedTypeData.setP06Label(typeDataWS.getP06Label());
					}
					break;
				}
			}
		}

		addedTypeData.setEntree(Global.EMPTY_STRING);
		addedTypeData.setColumnIndex(numCol + 1);
		addType(addedTypeData);

		// refresh de la table
		refreshTable();
	}
}
