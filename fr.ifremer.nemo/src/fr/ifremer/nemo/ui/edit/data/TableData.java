package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import sdn.vocabulary.interfaces.IElement;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.ui.FilteredTableFactory;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.tables.FormatsFilteredTable;
import fr.ifremer.nemo.ui.tables.InstrumentFilteredTable;
import fr.ifremer.nemo.ui.tables.ParameterFilteredTable;
import fr.ifremer.nemo.ui.tables.StandardNameFilteredTable;
import fr.ifremer.nemo.ui.tables.UnitFilteredTable;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSParameter;

public class TableData {
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
	 * @param typesFormat
	 *            the typesFormat to set
	 */
	public void setTypesFormat(ArrayList<TypeFormat> typesFormat) {
		this.typesFormat = typesFormat;
	}

	/**
	 * Parametres à afficher dans le tableau de l'onglet data
	 */
	private ArrayList<TypeData> types;

	/**
	 * la table contenant les parametres, affichée dans l'onglet data
	 */
	private Table myTable;

	/**
	 * reference sur le panneau de choix des parametres
	 */
	private ParameterFilteredTable myParameterFilteredTable;

	private InstrumentFilteredTable myInstrumentTable;

	/**
	 * liste des titres de colonnes (ordonnée)
	 */
	private HashMap<Integer, TableDataColumn> columnsMap;
	private final HashMap<String, Integer> colNbById =
			new HashMap<String, Integer>();

	/**
	 * columns IDs
	 */
	private static String COL_IS_STRING = "COL_IS_STRING"; //$NON-NLS-1$
	public static String COL_CODE = "COL_CODE"; //$NON-NLS-1$
	private static String COL_USERLABEL = "COL_USERLABEL"; //$NON-NLS-1$
	public static String COL_UNIT = "COL_UNIT"; //$NON-NLS-1$
	private static String COL_CONVERSION = "COL_CONVERSION"; //$NON-NLS-1$
	private static String COL_TEST = "COL_TEST"; //$NON-NLS-1$
	private static String COL_START = "COL_START"; //$NON-NLS-1$
	private static String COL_END = "COL_END"; //$NON-NLS-1$
	private static String COL_FORMAT = "COL_FORMAT"; //$NON-NLS-1$
	private static String COL_VALUE_IN = "COL_VALUE_IN"; //$NON-NLS-1$
	private static String COL_VALUE_OUT = "COL_VALUE_OUT"; //$NON-NLS-1$
	private static String COL_TEST_IN = "COL_TEST_IN"; //$NON-NLS-1$
	private static String COL_TEST_OUT = "COL_TEST_OUT"; //$NON-NLS-1$
	private static String COL_START_FLAG = "COL_START_FLAG"; //$NON-NLS-1$
	private static String COL_END_FLAG = "COL_END_FLAG"; //$NON-NLS-1$
	public static String COL_UNIT_CODE = "COL_UNIT_CODE"; //$NON-NLS-1$
	private static String COL_STANDARD_NAME = "COL_STANDARD_NAME"; //$NON-NLS-1$
	private static String COL_LONG_NAME = "COL_LONG_NAME"; //$NON-NLS-1$
	public static String COL_INSTRUMENT = "COL_INSTRUMENT"; //$NON-NLS-1$
	public static String COL_INSTRUMENT_CODE = "COL_INSTRUMENT_CODE"; //$NON-NLS-1$


	private final String[] colOrder =
		{ COL_IS_STRING, COL_CODE, COL_STANDARD_NAME, COL_LONG_NAME, COL_USERLABEL, COL_UNIT,
			COL_CONVERSION, COL_TEST, COL_START, COL_END, COL_FORMAT,
			COL_VALUE_IN, COL_VALUE_OUT, COL_TEST_IN, COL_TEST_OUT,
			COL_START_FLAG, COL_END_FLAG, COL_UNIT_CODE,COL_INSTRUMENT,COL_INSTRUMENT_CODE
		};

	/**
	 * Indexes for contextual menu items
	 */
	int MENU_ITEM_SETVALUE = 0;
	int MENU_ITEM_UPDATETEST = 1;
	int MENU_ITEM_PARAMLIST = 2;
	int MENU_ITEM_SELECTUNIT = 3;
	int MENU_ITEM_SELECTFORMAT = 4;
	int MENU_ITEM_SELECTSTANDARDNAME = 5;
	int MENU_ITEM_INSTRUMENTLIST = 6;
	int MENU_ITEM_UPTOTOPPARAM = 7;
	int MENU_ITEM_UPPARAM = 8;
	int MENU_ITEM_DOWNPARAM = 9;
	int MENU_ITEM_DOWNTOBOTTOMPARAM = 10;
	int MENU_ITEM_REMOVEPARAM = 11;
	int MENU_ITEM_SETFLAG = 12;
	int MENU_ITEM_DELATEFLAG = 13;
	int MENU_ITEM_DELETEALLPARAMS = 14;
	/*
	 * map key=menuItem / value=index in menu (can change depending to configuration)
	 */
	HashMap<Integer, Integer> menuMap = new HashMap<Integer, Integer>();
	/**
	 * constructeur initialise automatiquement les typesWS
	 */
	private final GroupVerticalParameters myGroupVerticalParameters;

	public TableData() {
		initTypesWS();
		initColumnsMap();

		myGroupVerticalParameters =
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getMyGroupeVerticalParameters();
	}

	private boolean isValueOutWritable() {
		return (RulesManager.getInstance().isConversionToMedatlas() || RulesManager.getInstance().isConversionToNetCDF());
	}

	private boolean isValueOutMandatory() {
		return RulesManager.getInstance().isConversionToMedatlas();
	}

	private boolean isLabelWritable() {
		return RulesManager.getInstance().isConversionToODV()
				&& !RulesManager.getInstance().isDataListTypeP091();
	}

	private boolean isStandardNameWritable() {
		return RulesManager.getInstance().isConversionToNetCDF();
	}

	private boolean isLongNameWritable() {
		return RulesManager.getInstance().isConversionToNetCDF();
	}

	private boolean isCheckBoxWritable() {
		return !(RulesManager.getInstance().isConversionToMedatlas() || RulesManager.getInstance().isConversionToNetCDF());
	}

	/**
	 * initialise les colonnes
	 */
	private void initColumnsMap() {

		// init col nb by id
		for (int i = 0; i < colOrder.length; i++) {
			colNbById.put(colOrder[i], i);
		}

		TableDataColumn col;
		columnsMap = new HashMap<Integer, TableDataColumn>();

		col =
				new TableDataColumn(COL_IS_STRING, colNbById.get(COL_IS_STRING),
						Messages.table_label_is_string, 25, true,
						false);

		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_CODE, colNbById.get(COL_CODE),
						Messages.table_label_code, 100, false, true);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_STANDARD_NAME, colNbById.get(COL_STANDARD_NAME),
						Messages.table_label_standard_name, 100, false,
						false);
		columnsMap.put(col.pos, col);

		col =
				new TableDataColumn(COL_LONG_NAME, colNbById.get(COL_LONG_NAME),
						Messages.table_label_long_name, 100, isLongNameWritable(),
						false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_USERLABEL,
						colNbById.get(COL_USERLABEL),
						Messages.table_label_userlabel, 100, isLabelWritable(),
						false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_UNIT, colNbById.get(COL_UNIT),
						Messages.table_label_unit, 90, false, true);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_CONVERSION, colNbById
						.get(COL_CONVERSION),
						Messages.label_conversion, 70, true, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_TEST, colNbById.get(COL_TEST),
						Messages.label_test, 50, false, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_START, colNbById.get(COL_START),
						Messages.label_start, 50, false, true);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_END, colNbById.get(COL_END),
						Messages.label_end, 50, false, true);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_FORMAT, colNbById.get(COL_FORMAT),
						Messages.label_format, 70, true, true);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_VALUE_IN, colNbById.get(COL_VALUE_IN),
						Messages.table_label_valueIn, 50, true, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_VALUE_OUT,
						colNbById.get(COL_VALUE_OUT),
						Messages.table_label_valueOut, 50,
						isValueOutWritable(), false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_TEST_IN, colNbById.get(COL_TEST_IN),
						Messages.table_label_testIn, 50, false, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_TEST_OUT, colNbById.get(COL_TEST_OUT),
						Messages.table_label_testOut, 50, false, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_START_FLAG, colNbById
						.get(COL_START_FLAG),
						Messages.table_label_startFlag, 50, false, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_END_FLAG, colNbById.get(COL_END_FLAG),
						Messages.table_label_endFlag, 50, false, false);
		columnsMap.put(col.pos, col);
		col =
				new TableDataColumn(COL_UNIT_CODE,
						colNbById.get(COL_UNIT_CODE),
						Global.EMPTY_STRING, 0, false, false);
		columnsMap.put(col.pos, col);

		col =
				new TableDataColumn(COL_INSTRUMENT, colNbById.get(COL_INSTRUMENT),
						Messages.table_label_intrument, 50, false, false);
		columnsMap.put(col.pos, col);

		col =
				new TableDataColumn(COL_INSTRUMENT_CODE, colNbById.get(COL_INSTRUMENT_CODE),
						Global.EMPTY_STRING, 0, false, false);
		columnsMap.put(col.pos, col);

	}
	public int getColNb(String columnID){
		return colNbById.get(columnID);
	}

	public void reinitTable() {
		if (myTable != null) {
			initTypesWS();
			go();
		}
	}

	public void razAndRecreateTable() {
		razTableData();
		creationTable();

	}

	public void go() {
		initTypesWS();
		types = new ArrayList<TypeData>();
		if (!(myTable == null || myTable.isDisposed())) {
			razTableData();
		}
		creationTable();
	}

	public void setMyParameterFilteredTable(ParameterFilteredTable paramTable) {
		myParameterFilteredTable = paramTable;
	}

	/**
	 * reinitialise la liste des paramètres de la table
	 */
	public void reinitTypes() {
		types = null;
		types = new ArrayList<TypeData>();
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
	 * creation de la table contenant les paramètres dans l'onglet data
	 */
	public void creationTable() {

		myTable =
				new Table(Nemo.getInstance().getViewMain().getEditDataForm()
						.getCompositeData(), SWT.CHECK | SWT.FULL_SELECTION
						| SWT.H_SCROLL
						| SWT.H_SCROLL);

		myTable.setHeaderVisible(true);
		myTable.setLinesVisible(true);

		for (int i = 0; i < columnsMap.size(); i++) {
			TableColumn tableColumn = new TableColumn(myTable, SWT.NONE);
			tableColumn.setWidth(columnsMap.get(i).width);
			tableColumn.setText(columnsMap.get(i).label.toUpperCase());

			if (columnsMap.get(i).id.equals(COL_USERLABEL)) {
				columnsMap.get(i).enable(isLabelWritable());
			}
			if (columnsMap.get(i).id.equals(COL_STANDARD_NAME)) {
				columnsMap.get(i).setWidth(isStandardNameWritable());
			}
			if (columnsMap.get(i).id.equals(COL_LONG_NAME)) {
				columnsMap.get(i).enable(isLongNameWritable());
				columnsMap.get(i).setMandatory(isLongNameWritable());
			}

			if (columnsMap.get(i).id.equals(COL_IS_STRING)) {
				tableColumn.setToolTipText(Messages.table_checkColumn_tooltip);
				tableColumn.setResizable(false);
				columnsMap.get(i).enable(isCheckBoxWritable());
			}

			if (columnsMap.get(i).id.equals(COL_VALUE_OUT)) {
				// columnsMap.get(i).setWritable(isLabelWritable());
				columnsMap.get(i).enable(isValueOutWritable());
				columnsMap.get(i).setMandatory(isValueOutMandatory());
			}

			// tableColumn.setAlignment(SWT.RIGHT);
		}
		initTable();

		// agrandissement de la table
		Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData()
		.getShell().layout();

	}

	private void checkTypesWS() {
		initTypesWS();
	}

	/**
	 * chargement des parametres pour l'affichage de la liste de choix des
	 * parametres (menu Parameter List du tableau de l'onget data)
	 */
	public void initTypesWS() {
		typesWS = null;
		typesWS = new ArrayList<TypeData>();
		if (RulesManager.getInstance()
				.getMyDataListType() != DataListTypeEnum.P011_VIA_P021)

		{
			iniTypesWSP091();
		} else

		{
			iniTypesWSP021();
		}
	}

	private void iniTypesWSP091() {
		int taille = NemoLists.getParameters().length;

		for (int i = 0; i < taille; i++) {
			// initialisation de la ArrayList des types des donnees a partir du
			// fichier P091 parameters.xml
			WSParameter wsParameter = new WSParameter();
			wsParameter = NemoLists.getParameters()[i];

			TypeData type = new TypeData(wsParameter);

			// ajout dans liste
			typesWS.add(type);
		}
	}

	private void iniTypesWSP021() {
		// init des données a partir du fichier P021 p021.xml
		Collection<IElement> listP021 = VocabLists.getVocabList(Intern.list_P021).getElements();
		TypeData type;

		for (IElement record : listP021) {
			type = new TypeData(record);
			type.setIsP011(false);
			// ajout dans liste
			typesWS.add(type);
		}
	}

	/**
	 * initialize the P061 types to use as units choice in P011viaP021 case
	 */
	public void initTypesP061() {
		if (typesP061 == null) {
			typesP061 = new ArrayList<TypeUnit>();
			// init des données a partir du fichier P021 p021.xml
			Collection<IElement> listP061 = VocabLists.getVocabList(Intern.list_P061).getElements();
			TypeUnit typeUnit;
			for (IElement record : listP061) {
				typeUnit = new TypeUnit(record);
				typesP061.add(typeUnit);
			}
		}
		Collections.sort(typesP061);
	}
	/**
	 * initialize the L22
	 */
	public void initTypesL22() {
		if (typesL22 == null) {
			typesL22 = new ArrayList<TypeInstrument>();
			Collection<IElement> listL22 = VocabLists.getVocabList(Intern.list_L22).getElements();
			TypeInstrument typeInstrument;
			for (IElement record : listL22) {
				typeInstrument = new TypeInstrument(record);
				typesL22.add(typeInstrument);
			}
		}
		Collections.sort(typesL22);

	}
	/**
	 * initialize the L22
	 */
	public void initTypesL33() {
		if (typesL33 == null) {
			typesL33 = new ArrayList<TypeFallRate>();
			Collection<IElement> listL33 = VocabLists.getVocabList(Intern.list_L33).getElements();
			TypeFallRate typeFallrate;
			for (IElement record : listL33) {
				typeFallrate = new TypeFallRate(record);
				typesL33.add(typeFallrate);
			}
		}
		Collections.sort(typesL33);
	}
	/**
	 * fill types - add PRES automatically for Medatlas in P091 (see param rules table, data01) 
	 */
	public void fillTypes() {
		// initialisation de la ArrayList des types des donnees e partir du
		types = null;
		types = new ArrayList<TypeData>();


		razAndRecreateTable();
	}

	/**
	 * initialisation de la table des paramètres de l'onglet data
	 */
	public void initTable() {

		/*
		 * Remplir la table avec les tableItems avant de linker les editors !
		 */

		final Table table;
		table = myTable;

		// REMPLISSAGE temporaire de la table
		initTableData();
		// remplirTableData();

		// LINK des editors

		// acces direct aux valeurs des colonnes des items
		final TableEditor editor = new TableEditor(table);

		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		table.setSize(size);
		/**
		 * listener pour la case a cocher 10755
		 */
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{

				if (event.detail == SWT.CHECK)
				{
					if (((TableItem) event.item).getChecked())
					{
						Utils.messageOK(Messages.edit_data_parameterIsText);

						((TableItem) event.item).setData(COL_CONVERSION,
								((TableItem) event.item).getText(colNbById
										.get(COL_CONVERSION)));
						((TableItem) event.item).setData(COL_UNIT,
								((TableItem) event.item).getText(colNbById
										.get(COL_UNIT)));
						((TableItem) event.item).setData(COL_FORMAT,
								((TableItem) event.item).getText(colNbById
										.get(COL_FORMAT)));
						((TableItem) event.item).setText(colNbById
								.get(COL_CONVERSION), "");
						((TableItem) event.item).setText(colNbById
								.get(COL_UNIT),
								"");
						((TableItem) event.item).setText(colNbById
								.get(COL_FORMAT),
								"");
					}
					else
					{
						Utils.messageOK(Messages.edit_data_parameterIsNumeric);

						((TableItem) event.item).setText(colNbById
								.get(COL_CONVERSION),
								(String) ((TableItem) event.item)
								.getData(COL_CONVERSION));
						((TableItem) event.item)
						.setText(colNbById.get(COL_UNIT),
								(String) ((TableItem) event.item)
								.getData(COL_UNIT));
						((TableItem) event.item).setText(colNbById
								.get(COL_FORMAT),
								(String) ((TableItem) event.item)
								.getData(COL_FORMAT));
					}
				}
			}
		});
		/**
		 * Listener pour le clic sur la table les colonnes modifiables sont
		 * format entree sortie conversion
		 */
		table.addListener(SWT.MouseDown, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{

				// clic 3e bouton (=clic droit): comportement par defaut
				if (event.button == 3)
				{
					if (RulesManager.getInstance()
							.getMyDataListType() == DataListTypeEnum.P011_VIA_P021)
					{
						Point pt = new Point(event.x, event.y);
						TableItem itemTmp = table.getItem(pt);
						if (itemTmp != null) {
							table.getMenu()
							.getItem(menuMap.get(MENU_ITEM_SELECTFORMAT))
							.setEnabled(!itemTmp.getChecked());
							table.getMenu().getItem(
									menuMap.get(MENU_ITEM_SELECTUNIT)).setEnabled(
											!itemTmp.getChecked());
						}
					}
					return;
				}

				// FAE 9670 surlignage de la position des paramètres dans le
				// fichier
				if (event.button == 1)
				{
					int indice = myTable.getSelectionIndex();
					if (indice >= 0)
					{
						if (RulesManager.getInstance().isInputWithSeparatorsAndModelUpToDate()){
							//33278: split as columns
							int col = Integer.valueOf(myTable.getItem(indice)
									.getText(
											colNbById.get(COL_START)));
							Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().setIndexSelected(col);
							Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().refresh();
							
						}else{
						selectInViewFile(
								Integer.valueOf(myTable.getItem(indice)
										.getText(
												colNbById.get(COL_START))),
												Integer.valueOf(myTable.getItem(indice)
														.getText(
																colNbById.get(COL_END))));
						}
					}
				}

				// calcul du numero de ligne selectionne
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);

				if (item == null)
				{
					return;
				}

				// edition directe de l'element selectionne
				Rectangle clientArea = table.getClientArea();
				int index = table.getTopIndex();

				while (index < table.getItemCount())
				{

					boolean visible = false;
					final TableItem itemTmp = table.getItem(index);
					for (int colNumber = 0; colNumber < table.getColumnCount(); colNumber++)
					{
						// edition des colonnes autorisées en écriture
						// uniquement

						if (isCellWritable(colNumber, itemTmp))
						{
							Rectangle rect = itemTmp.getBounds(colNumber);
							if (rect.contains(pt))
							{
								final int column = colNumber;
								final Text text = new Text(table, SWT.NONE);
								text.setText(itemTmp.getText(colNumber));

								/**
								 * Listener sur le texte d'une cellule
								 */
								Listener textListener = new Listener()
								{

									String s = text.getText();

									public void handleEvent(final Event e)
									{

										switch (e.type)
										{
										case SWT.FocusOut:
											itemTmp.setText(column,
													text.getText());
											text.dispose();
											break;
										case SWT.Traverse:
											switch (e.detail)
											{
											case SWT.TRAVERSE_RETURN:
												itemTmp.setText(column,
														text.getText());

											case SWT.TRAVERSE_ESCAPE:
												text.dispose();
												e.doit = false;
											}
											break;

										case SWT.Modify:
											if (!((Text) e.widget)
													.getText().equals(s))
												Utils.invalidEtapes();
											break;
										}
									}
								};
								text.addListener(SWT.FocusOut, textListener);
								text.addListener(SWT.Traverse, textListener);
								text.addListener(SWT.Modify, textListener);
								editor.setEditor(text, itemTmp, colNumber);
								text.selectAll();

								text.setFocus();
								return;
							}
							if (!visible && rect.intersects(clientArea))
							{
								visible = true;
							}
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});

		table.addListener(SWT.KeyUp, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.keyCode == SWT.ARROW_DOWN
						|| event.keyCode == SWT.ARROW_UP)
				{
					int indice = myTable.getSelectionIndex();
					selectInViewFile(
							Integer.valueOf(myTable.getItem(indice).getText(
									colNbById.get(COL_START))),
									Integer.valueOf(myTable.getItem(indice).getText(
											colNbById.get(COL_END))));
				}
			}
		});

		/**
		 * *********************************************************************
		 * ******* creation du menu
		 */

		Menu menu = new Menu(table.getShell(), SWT.POP_UP);
		int index = 0;
		
		
		// ========= Injecter debut/fin
		MenuItem itemInject = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_SETVALUE, index);
		index++;
		itemInject.setText(Messages.menu_label_setValue);
		itemInject.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (RulesManager.getInstance().isInputWithSeparatorsAndModelUpToDate()){
					// 33278 CSV files
					int selectionIndex = myTable.getSelectionIndex();
					int selectColumn = Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getIndexSelected();
					// remplissage debut et fin dans interface
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[selectionIndex].setText(colNbById.get(COL_START), String.valueOf(selectColumn));
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[selectionIndex].setText(colNbById.get(COL_END), String.valueOf(selectColumn));
					majTypeData();
					// refresh de la table
					razAndRecreateTable();
					Utils.invalidEtapes();
					
				}else{
					// text files
					// table.forceFocus();
					Utils.injectData(Nemo.getInstance().getViewMain()
							.getEditDataForm().getTextAreaData(),
							colNbById.get(COL_START), colNbById.get(COL_END));
					majTypeData();

					// refresh de la table
					razAndRecreateTable();
					Utils.invalidEtapes();
				}
			}
		});
		// ========= Maj champ test
		MenuItem itemMaj = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_UPDATETEST, index);
		index++;
		itemMaj.setText(Messages.menu_label_updateTest);
		itemMaj.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				majChampTest();
			}
		});
		// ========= Liste de paramètres
		MenuItem itemList = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_PARAMLIST, index);
		index++;
		itemList.setText(Messages.menu_label_paramList);
		itemList.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				if (RulesManager.getInstance().getMyDataListType() != DataListTypeEnum.UNKNOWN)
				{
					checkTypesWS();
					openMyParameterFilteredTable();
				}
				else
				{
					Utils
					.messageErreur(Messages.edit_data_error_dataListUnknown);
				}
			}
		});

		// instrument
		MenuItem instrumentList = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_INSTRUMENTLIST, index);
		index++;
		instrumentList.setText(Messages.menu_label_instrumentList);
		instrumentList.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				initTypesL22();
				openMyInstrumentTable();
			}
		});

		// ========= Liste des unités P061 (P011 via P021 seulement)
		if (RulesManager.getInstance().getMyDataListType() == DataListTypeEnum.P011_VIA_P021) {
			MenuItem itemAddP061Unit =	new MenuItem(menu, SWT.NONE, index);
			menuMap.put(MENU_ITEM_SELECTUNIT, index);
			index++;
			itemAddP061Unit.setText(Messages.menu_label_selectP061Unit);
			itemAddP061Unit.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					initTypesP061();
					int indice = myTable.getSelectionIndex();
					openMyUnitFilteredTable(indice);
				}
			});
		}

		// ========= Selection d'un format (P011 via P021 seulement)
		if (RulesManager.getInstance().getMyDataListType() == DataListTypeEnum.P011_VIA_P021) {
			MenuItem selectFormat =	new MenuItem(menu, SWT.NONE, index);
			menuMap.put(MENU_ITEM_SELECTFORMAT, index);
			index++;
			selectFormat.setText(Messages.menu_label_selectFormat);
			selectFormat.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					//					initTypesP061();
					int indice = myTable.getSelectionIndex();
					openMyFormatFilteredTable(indice);
				}
			});
		}
		// ========= Selection d'un Standard Names
		if (RulesManager.getInstance().isConversionToNetCDF()) {
			MenuItem selectStandardName = new MenuItem(menu, SWT.NONE, index);
			menuMap.put(MENU_ITEM_SELECTSTANDARDNAME, index);
			index++;
			selectStandardName.setText(Messages.menu_label_selectStandardName);
			selectStandardName.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent(Event e)
				{
					//initTypesP07();
					int indice = myTable.getSelectionIndex();
					openMyStandardNameFilteredTable(indice);
				}
			});
		}
		// ========= Monter paramètre sélectionné tout en haut de la liste
		MenuItem itemUpToTop =
				new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_UPTOTOPPARAM, index);
		index++;
		itemUpToTop.setText(Messages.menu_label_upToTopParam);
		itemUpToTop.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				// System.out.println("up");
				monterEltToTopTableData();

				Utils.invalidEtapes();
			}
		});

		// ========= Monter paramètre sélectionné
		MenuItem itemUp = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_UPPARAM, index);
		index++;
		itemUp.setText(Messages.menu_label_upParam);
		itemUp.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				// System.out.println("up");
				monterEltTableData();

				Utils.invalidEtapes();
			}
		});
		// ========= descendre paramètres sélectionné
		MenuItem itemDown = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_DOWNPARAM, index);
		index++;
		itemDown.setText(Messages.menu_label_downParam);
		itemDown.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				descendreEltTableData();

				Utils.invalidEtapes();
			}
		});
		// ========= descendre paramètre sélectionné en bas de liste
		MenuItem itemDownToBottom =
				new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_DOWNTOBOTTOMPARAM, index);
		index++;
		itemDownToBottom.setText(Messages.menu_label_downToBottomParam);
		itemDownToBottom.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				descendreEltToBottomTableData();

				Utils.invalidEtapes();
			}
		});
		// ========= supprimer paramètre sélectionné
		MenuItem itemDel = new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_REMOVEPARAM, index);
		index++;
		itemDel.setText(Messages.menu_label_removeParam);
		itemDel.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				supprEltTableData();
				Utils.invalidEtapes();
			}
		});

		// separateur
		new MenuItem(menu, SWT.SEPARATOR, index);
		index++;

		// ========= Injecter flag
		MenuItem itemInjectFlag =
				new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_SETFLAG, index);
		index++;
		itemInjectFlag.setText(Messages.menu_label_setFlag);
		itemInjectFlag.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				

				if (RulesManager.getInstance().isInputWithSeparatorsAndModelUpToDate()){
					// 33278 CSV files
					int selectionIndex = myTable.getSelectionIndex();
					int selectColumn = Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getIndexSelected();
					// remplissage debut et fin dans interface
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[selectionIndex].setText(colNbById.get(COL_START_FLAG), String.valueOf(selectColumn));
					Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getMyTable().getItems()[selectionIndex].setText(colNbById.get(COL_END_FLAG), String.valueOf(selectColumn));
					majTypeData();
					// refresh de la table
					razAndRecreateTable();
					Utils.invalidEtapes();
					
				}else{
				
				table.forceFocus();
				Utils.injectData(Nemo.getInstance().getViewMain()
						.getEditDataForm().getTextAreaData(),
						colNbById.get(COL_START_FLAG), colNbById
						.get(COL_END_FLAG));

				Utils.invalidEtapes();
				}
			}
		});
		// ========= supprimer flag
		MenuItem itemRemoveFlag =
				new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_DELATEFLAG, index);
		index++;
		itemRemoveFlag.setText(Messages.menu_label_deleteFlag);
		itemRemoveFlag.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				table.forceFocus();
				table.getItems()[table.getSelectionIndex()].setText(
						colNbById.get(COL_TEST_IN), Global.EMPTY_STRING);
				table.getItems()[table.getSelectionIndex()].setText(
						colNbById.get(COL_TEST_OUT), Global.EMPTY_STRING);
				table.getItems()[table.getSelectionIndex()].setText(
						colNbById.get(COL_START_FLAG), Global.EMPTY_STRING);
				table.getItems()[table.getSelectionIndex()].setText(
						colNbById.get(COL_END_FLAG), Global.EMPTY_STRING);
				types.get(table.getSelectionIndex()).setDebutFlag(null);
				types.get(table.getSelectionIndex()).setFinFlag(null);

				Utils.invalidEtapes();
			}
		});

		// separateur
		new MenuItem(menu, SWT.SEPARATOR, index);
		index++;

		// ========= Suppression de tous les parametres à la fois
		MenuItem deleteAll =
				new MenuItem(menu, SWT.NONE, index);
		menuMap.put(MENU_ITEM_DELETEALLPARAMS, index);
		index++;
		deleteAll.setText(Messages.menu_label_deleteAllParameters);
		deleteAll.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				table.clearAll();
				supprAllEltTableData();

			}
		});

		table.setMenu(menu);
		Utils.forceMenu(table, menu);

		// agrandissement de la table
		Nemo.getInstance().getViewMain().getEditDataForm().getCompositeData()
		.getParent().layout(true, true);
		/*table.setLayout(new FillLayout());*/
		table.redraw();
	}

	/**
	 * ouvre la fenetre de choix de parametres
	 */
	public void openMyParameterFilteredTable() {
		getMyParameterFilteredTable().open(
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getCompositeData().getParent());
	}
	public void openMyInstrumentTable() {
		getMyInstrumentTable().open(
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getCompositeData().getParent());
	}

	public void openMyUnitFilteredTable(int rowIndex) {
		getMyUnitFilteredTable().open(
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getCompositeData().getParent());
	}

	public void openMyFormatFilteredTable(int rowIndex) {
		getMyFormatFilteredTable().open(
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getCompositeData().getParent());
	}

	public void openMyStandardNameFilteredTable(int rowIndex) {
		getMyStandardNameFilteredTable().open(
				Nemo.getInstance().getViewMain().getEditDataForm()
				.getCompositeData().getParent());
	}

	public void initTableData() {
		// creation des TableItems e partir de la ArrayList types

		// nouvelle creation des items
		for (int i = 0; i < types.size(); i++) {

			final TableItem item = new TableItem(myTable, SWT.NONE);
			String code;
			String label = "";

			if (types.get(i).isP011()) {
				code =
						types.get(i).getEntryKeyP011() + Global.SEP
						+ types.get(i).getEntryTermP011();

			} else {
				code = types.get(i).getFormattedCodeAndName();
			}

			if (types.get(i).isP011()) {
				if (RulesManager.getInstance().isConversionToODV()) {
					String userLabel =
							LabelParameterManager.getInstance().getUserLabel(
									types.get(i).getEntryKeyP011());
					if (userLabel != null) {
						label = userLabel;
					}
				}
			}

			String standardName = "";
			if (RulesManager.getInstance().isConversionToNetCDF()) {
				String userStandardName = StandardNameParameterManager.getInstance().getUserStandardName(types.get(i).getEntryKeyP011());
				if (userStandardName != null) {
					standardName = userStandardName;
				}
			}

			String longName = "";
			if (RulesManager.getInstance().isConversionToNetCDF()) {
				String userLongName = NetCDFLongNameParameterManager.getInstance().getUserLongName(types.get(i).getEntryKeyP011());
				if (userLongName != null) {
					longName = userLongName;
				}
			}

			item.setText(new String[]
					{
					null,
					code,
					standardName,
					longName,
					label,
					types.get(i).getUnite(),
					(types.get(i).isText() ? null : types.get(i)
							.getConversion()), types.get(i).getTest(),
							String.valueOf(types.get(i).getDebut()),
							String.valueOf(types.get(i).getFin()),
							types.get(i).getFormat(), types.get(i).getEntree(),
							types.get(i).getSortie(), types.get(i).getTestIn(),
							types.get(i).getTestOut(), types.get(i).getDebutFlag(),
							types.get(i).getFinFlag(),
							// FAE 6542
							types.get(i).getUniteCode() ,
							types.get(i).getInstrument(),
							types.get(i).getInstrumentCode()
					});
			if (isCheckBoxWritable())
				item.setChecked(types.get(i).isText());
			else
				item.setChecked(false);
		}

	}

	public void razTableData() {
		// effacement de tous les items de la table
		myTable.dispose();

	}

	public void monterEltToTopTableData() {
		// on monte d'un cran l'element e la position indice
		// swap (indice-1) <-> indice
		//

		// Utils.messageErreur(Global.EMPTY_STRING);
		int nbre;
		ArrayList<TypeData> listeTempo;
		nbre = types.size();

		int indice = myTable.getSelectionIndex();

		// si l'indice est 0, on ne fait rien
		if (indice != 0) {

			listeTempo = new ArrayList<TypeData>();

			// mise e jour prealable
			majTypeData();
			listeTempo.add(types.get(indice));

			// parcours de la liste
			for (int i = 0; i < (indice - 1); i++) {
				listeTempo.add(types.get(i));
			}
			// swap des 2
			listeTempo.add(types.get(indice - 1));

			// fin de parcours
			for (int i = (indice + 1); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}

			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}

			// refresh de la table
			razAndRecreateTable();

		}
	}

	public void monterEltTableData() {
		// on monte d'un cran l'element e la position indice
		// swap (indice-1) <-> indice
		//

		// Utils.messageErreur(Global.EMPTY_STRING);
		int nbre;
		ArrayList<TypeData> listeTempo;
		nbre = types.size();

		int indice = myTable.getSelectionIndex();

		// si l'indice est 0, on ne fait rien
		if (indice != 0) {

			listeTempo = new ArrayList<TypeData>();

			// mise e jour prealable
			majTypeData();

			// parcours de la liste
			for (int i = 0; i < (indice - 1); i++) {
				listeTempo.add(types.get(i));
			}
			// swap des 2
			listeTempo.add(types.get(indice));
			listeTempo.add(types.get(indice - 1));

			// fin de parcours
			for (int i = (indice + 1); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}

			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}

			// refresh de la table
			razAndRecreateTable();

		}

	}

	public void descendreEltTableData() {
		// on descend d'un cran l'element e la position indice
		// swap (indice-1) <-> indice

		int indice = myTable.getSelectionIndex();

		// Si derniere ligne, on ne fait rien
		if (indice != myTable.getItemCount() - 1) {

			int nbre;
			ArrayList<TypeData> listeTempo;
			nbre = types.size();

			listeTempo = new ArrayList<TypeData>();

			// mise e jour prealable
			majTypeData();

			// parcours de la liste
			for (int i = 0; i < indice; i++) {
				listeTempo.add(types.get(i));
			}
			// swap des 2
			listeTempo.add(types.get(indice + 1));
			listeTempo.add(types.get(indice));

			// fin de parcours
			for (int i = (indice + 2); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}

			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}
			razAndRecreateTable();
		}
	}

	public void descendreEltToBottomTableData() {
		// on descend d'un cran l'element e la position indice
		// swap (indice-1) <-> indice

		int indice = myTable.getSelectionIndex();

		// Si derniere ligne, on ne fait rien
		if (indice != myTable.getItemCount() - 1) {

			int nbre;
			ArrayList<TypeData> listeTempo;
			nbre = types.size();

			listeTempo = new ArrayList<TypeData>();

			// mise e jour prealable
			majTypeData();

			// parcours de la liste
			for (int i = 0; i < indice; i++) {
				listeTempo.add(types.get(i));
			}

			// fin de parcours
			for (int i = (indice + 1); i < nbre; i++) {
				listeTempo.add(types.get(i));
			}
			listeTempo.add(types.get(indice));
			// effacement de la liste principale avant re-remplissage
			for (int i = 0; i < nbre; i++) {
				types.remove(0);
			}

			// re-remplissage de la liste principale
			nbre = listeTempo.size();
			for (int i = 0; i < nbre; i++) {
				types.add(listeTempo.get(i));
			}
			razAndRecreateTable();
		}
	}

	public void supprAllEltTableData() {
		// majTypeData();
		types.clear();
		myGroupVerticalParameters.removeAllToAllCombos();
		// refresh de la table
		razAndRecreateTable();
	}

	public void supprEltTableData() {
		// suppression de la ligne selectionnee
		int indice = myTable.getSelectionIndex();
		// d'abord mise e jour des donnees
		majTypeData();

		// on supprime l'element e l'indice donne
		// split sur " - "
		TypeData typeData = types.get(indice);
		// Pattern p = Pattern.compile(Global.SEP);
		// String mots[] = p.split(typeData.getCode());
		// String code = mots[0];
		//
		//
		System.out.println("suppr " + typeData.getEntryKey());

		if(myGroupVerticalParameters.getSelection().contains(typeData.getEntryKey())){
			Nemo.getInstance().getViewMain().getEditDataForm().resetFallRate();
			Nemo.getInstance().getViewMain().getEditDataForm().setFallrateGroupEnabled(false);
		}

		types.remove(indice);

		myGroupVerticalParameters.removeToAllCombo(typeData);

		// refresh de la table
		razAndRecreateTable();

	}

	public void removeAllElts() {
		types.clear();
		// refresh de la table
		razAndRecreateTable();

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
		return (!myTable.getItem(index).getText(colNbById.get(COL_START))
				.equals(Global.EMPTY_STRING));
	}

	/**
	 * complete if start and conversion are set
	 * 
	 * @param index
	 * @return
	 */
	private boolean isNumericFieldParamsComplete(int index) {
		return (!myTable.getItem(index).getText(colNbById.get(COL_START))
				.equals(Global.EMPTY_STRING))
				&& (!myTable.getItem(index).getText(
						colNbById.get(COL_CONVERSION)).equals(Global.EMPTY_STRING));
	}

	private boolean isParameterReady(int index) {
		return (isNumericField(index) && isNumericFieldParamsComplete(index))
				|| (!isNumericField(index) && isNonNumericFieldParamsComplete(index));

	}

	private boolean isSelectedValueEqualsToDefaultIn(int index, String selection) {
		return selection.equals(myTable.getItem(index).getText(
				colNbById.get(COL_VALUE_IN)).trim());

	}

	public boolean majChampTest() {

		// maj preventive de tout
		majTypeData();

		boolean ok = true;

		int nbre;
		String selection = "";
		String ligne, resultat = "";
		String defaultValue = "";
		double resultJEP;

		nbre = myTable.getItemCount();
		MediaDomain mediaDomain = Nemo.getInstance().getDomain().getMedia();
		if (RulesManager.getInstance().isInputWithSeparatorsAndModelUpToDate()){
			// 33278: split as columns
			ArrayList<String> input = (ArrayList<String>)Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().getInput();
			ligne=input.get(0);
		
		}else{
		
			ligne = Utils.lignesBrutes.get(mediaDomain.getModel().getFileHeader()
					+ mediaDomain.getStationHeaderNumberLines());
		}
		
		for (int i = 0; i < nbre; i++) {

			if (isParameterReady(i)) {
				try {
					if (Nemo.getInstance().getDomain().getMedia().getModel().getSeparator().isEmpty() || !RulesManager.getInstance().isInputWithSeparatorsAndModelUpToDate()){
					selection =
							ligne.substring(
									Integer.valueOf(myTable.getItem(i).getText(
											colNbById.get(COL_START))) - 1,
											Integer.valueOf(myTable.getItem(i).getText(
													colNbById.get(COL_END)))).trim();
					}else{
						// 33278: split as columns
						selection = ligne.split(
								Nemo.getInstance().getDomain().getMedia().getModel().getSeparator())
								[Integer.valueOf(myTable.getItem(i).getText(colNbById.get(COL_START)))].trim();
					}

				} catch (Exception e) {
					System.out.println("erreur substring "
							+ myTable.getItem(i).getText(
									colNbById.get(COL_CODE)));
					ok = false;
					Utils.messageErreurAppend(Messages.Etape_98
							+ " "
							+ myTable.getItem(i).getText(
									colNbById.get(COL_CODE)));
					break;
				}
				defaultValue =
						myTable.getItem(i).getText(
								colNbById.get(COL_VALUE_OUT));
			}

			/**
			 * Parametres numeriques
			 */
			if (isNumericField(i) && isNumericFieldParamsComplete(i)) {
				// on a selectionne un debut et une fin et il y a une conversion
				// e faire

				// on formate puis on reaffiche la liste
				try {
					String formatString =
							myTable.getItem(i).getText(colNbById.get(COL_FORMAT));
					String type =
							formatString.substring(formatString.length() - 1);

					if (isSelectedValueEqualsToDefaultIn(i, selection)) {

						// entree par defaut, le test est la valeur de sortie
						// par defaut formatee
						String fmOut = Global.EMPTY_STRING;
						String outputDefaultValue =
								myTable.getItem(i).getText(
										colNbById.get(COL_VALUE_OUT));

						// Float ou Decimal ?
						if ((type.equals("f") || type.equals("F")) && !outputDefaultValue.trim().isEmpty()) { //$NON-NLS-1$//$NON-NLS-2$
							fmOut =
									String
									.format(java.util.Locale.US,
											formatString,
											Float
											.parseFloat(outputDefaultValue));
						} else if ((type.equals("d") || type.equals("D")) && !outputDefaultValue.trim().isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$
							fmOut =
									String.format(java.util.Locale.US,
											formatString,
											Integer
											.parseInt(outputDefaultValue));
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
							//selectionValue = Float.valueOf(selection);
							selectionValue = Double.valueOf(selection);
						} catch (NumberFormatException e) {
							System.out.println("erreur conversion on parameter"
									+ myTable.getItem(i).getText(
											colNbById.get(COL_CODE)));
							ok = false;
							Utils.messageErreurAppend(Messages.ParameterModel_1
									+ " "
									+ myTable.getItem(i).getText(
											colNbById.get(COL_CODE)));
							Utils.messageErreurAppend("expected a numeric value, found \'"+selection + "'");// TODO
							break;

						}
						parser.addVariable("x", selectionValue); //$NON-NLS-1$
						parser.parseExpression(myTable.getItem(i).getText(
								colNbById.get(COL_CONVERSION)));
						resultJEP = parser.getValue();

						if (type.equals("f") || type.equals("F")) { //$NON-NLS-1$//$NON-NLS-2$
							resultat =
									String.format(
											myTable.getItem(i).getText(
													colNbById.get(COL_FORMAT)),
													new Double(
															resultJEP));
						} else if (type.equals("d") || type.equals("D")) { //$NON-NLS-1$ //$NON-NLS-2$
							resultat =
									String.format(
											myTable.getItem(i).getText(
													colNbById.get(COL_FORMAT)),
													(Double.valueOf(resultJEP))
													.intValue());
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
			if ((!myTable.getItem(i).getText(colNbById.get(COL_START_FLAG))
					.equals(Global.EMPTY_STRING))
					&& (!myTable.getItem(i)
							.getText(colNbById.get(COL_END_FLAG))
							.equals(Global.EMPTY_STRING))) {
				//FAE 12631
				//                ligne =
				//                        Utils.lignesBrutes
				//                            .get(mediaDomain.getModel()
				//                                .getFileHeader()
				//                                    + mediaDomain.getStationHeaderNumberLines()
				//                                    + 1);

				selection =
						ligne.substring(
								Integer.valueOf(myTable.getItem(i).getText(
										colNbById.get(COL_START_FLAG))) - 1,
										Integer.valueOf(myTable.getItem(i).getText(
												colNbById.get(COL_END_FLAG)))).trim();

				types.get(i).setTestIn(selection);

				String flagSDN = Global.ZERO;
				ArrayList<Flag> flags =
						Nemo.getInstance().getDomain().getStation().getModel()
						.getFlagTable().getFlags();
				int size = flags.size();
				for (int j = 0; j < size; j++) {
					if (flags.get(j).getLocal()
							.equals(types.get(i).getTestIn())) {
						flagSDN =
								fr.ifremer.nemo.util.Utils.extraitCode(flags
										.get(j)
										.getSdn());
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
		razAndRecreateTable();

		return ok;
	}

	public void majTypeData() {
		// on met a jour tous les types de donnees dans l'ArrayList global
		// a partir des valeurs de la table

		int nbre;
		nbre = myTable.getItemCount();

		for (int i = 0; i < nbre; i++) {
			if (!types.get(i).isP011()) {
				types.get(i).setEntryKeyAndEntryTerm(
						myTable.getItem(i).getText(colNbById.get(COL_CODE)));
			}
			String userLabel =
					myTable.getItem(i).getText(colNbById.get(COL_USERLABEL));

			if (!userLabel.equals(Global.EMPTY_STRING)) {
				LabelParameterManager.getInstance().updateLabel(
						types.get(i).getEntryKeyP011(), userLabel);
			}
			// types.get(i).setUserLabel(myTable.getItem(i).getText(colNbById.get(COL_USERLABEL)));

			String userStandardName =
					myTable.getItem(i).getText(colNbById.get(COL_STANDARD_NAME));

			if (!userStandardName.equals(Global.EMPTY_STRING)) {
				StandardNameParameterManager.getInstance().updateStandardName(
						types.get(i).getEntryKeyP011(), userStandardName);
			}

			String userLongName =
					myTable.getItem(i).getText(colNbById.get(COL_LONG_NAME));

			if (!userLongName.equals(Global.EMPTY_STRING)) {
				NetCDFLongNameParameterManager.getInstance().updateLongName(
						types.get(i).getEntryKeyP011(), userLongName);
			}

			//types.get(i).setStandardName(myTable.getItem(i).getText(colNbById.get(COL_STANDARD_NAME)));

			types.get(i).setUnite(
					myTable.getItem(i).getText(colNbById.get(COL_UNIT)));
			types.get(i).setConversion(
					myTable.getItem(i).getText(colNbById.get(COL_CONVERSION)));
			types.get(i).setTest(
					myTable.getItem(i).getText(colNbById.get(COL_TEST)));
			types.get(i).setDebut(
					Integer.valueOf(myTable.getItem(i).getText(
							colNbById.get(COL_START))));
			types.get(i).setFin(
					Integer.valueOf(myTable.getItem(i).getText(
							colNbById.get(COL_END))));
			types.get(i).setFormat(
					myTable.getItem(i).getText(colNbById.get(COL_FORMAT)));
			types.get(i).setEntree(
					myTable.getItem(i).getText(colNbById.get(COL_VALUE_IN)));
			types.get(i).setSortie(
					myTable.getItem(i).getText(colNbById.get(COL_VALUE_OUT)));

			if (!myTable.getItem(i).getText(colNbById.get(COL_START_FLAG))
					.equals(Global.EMPTY_STRING)
					&& !myTable.getItem(i).getText(colNbById.get(COL_END_FLAG))
					.equals(Global.EMPTY_STRING)) {
				types.get(i).setDebutFlag(
						myTable.getItem(i).getText(
								colNbById.get(COL_START_FLAG)));
				types
				.get(i)
				.setFinFlag(
						myTable.getItem(i).getText(colNbById.get(COL_END_FLAG)));
			}
			// FAE 6542
			types.get(i).setUniteCode(
					myTable.getItem(i).getText(colNbById.get(COL_UNIT_CODE)));


			types.get(i).setInstrument(
					myTable.getItem(i).getText(colNbById.get(COL_INSTRUMENT)));

			types.get(i).setInstrumentCode(
					myTable.getItem(i).getText(colNbById.get(COL_INSTRUMENT_CODE)));



			types.get(i).setIsText(isSelected(i));

		}
	}

	public boolean isSelected(int index) {
		return myTable.getItem(index).getChecked();
	}

	public ArrayList<TypeData> getTypesWS() {
		return typesWS;
	}

	public ArrayList<TypeData> getTypes() {
		return types;
	}

	public Table getMyTable() {
		return myTable;
	}

	public ParameterFilteredTable getMyParameterFilteredTable() {
		if (myParameterFilteredTable == null) {
			FilteredTableFactory filteredFactory =
					Nemo.getInstance().getViewMain()
					.getMyFilteredTableFactory();
			myParameterFilteredTable =
					(ParameterFilteredTable) filteredFactory.go(Nemo
							.getInstance()
							.getViewMain().getEditDataForm().getCompositeData()
							.getParent(), Global.PARAMETER, null);
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
		TypeData typeParameter;
		int index = myTable.getSelectionIndex();
		typeParameter = types.get(index);

		StandardNameFilteredTable theTable = new StandardNameFilteredTable(typeParameter, RulesManager.getInstance().getMyDataListType());
		return theTable;
	}

	public void setUnit(TypeUnit typeUnit) {
		int selectedRow = myTable.getSelectionIndex();
		if (selectedRow > -1) {
			TableItem item = myTable.getItem(selectedRow);
			item.setText(colNbById.get(COL_UNIT), typeUnit.getTerm());
			// FAE 6542
			item.setText(colNbById.get(COL_UNIT_CODE), typeUnit.getUnitKey());
		}
	}
	public void setInstrument(TypeInstrument type) {
		int selectedRow = myTable.getSelectionIndex();
		if (selectedRow > -1) {
			TableItem item = myTable.getItem(selectedRow);
			item.setText(colNbById.get(COL_INSTRUMENT), type.getTerm());
			item.setText(colNbById.get(COL_INSTRUMENT_CODE), type.getKey());
		}
	}

	public void setStandardName(TypeStandardName typeStandardName) {
		int selectedRow = myTable.getSelectionIndex();
		if (selectedRow > -1) {
			TableItem item = myTable.getItem(selectedRow);
			item.setText(colNbById.get(COL_STANDARD_NAME), typeStandardName.getTerm());
		}
	}

	public void setFormat(TypeFormat typeFormat) {
		int selectedRow = myTable.getSelectionIndex();
		if (selectedRow > -1) {
			TableItem item = myTable.getItem(selectedRow);
			item.setText(colNbById.get(COL_FORMAT), typeFormat.getFormat());
			item.setText(colNbById.get(COL_VALUE_OUT), typeFormat.getExample());
		}
	}

	public ArrayList<TypeUnit> getTypesP061() {
		return typesP061;
	}
	/**
	 * L22 list for instruments
	 * @return
	 */
	public ArrayList<TypeInstrument> getTypesL22() {
		return typesL22;
	}
	/**
	 * L33 list for fall rates
	 * @return
	 */
	public ArrayList<TypeFallRate> getTypesL33() {

		if (typesL33 == null){
			initTypesL33();
		}
		return typesL33;
	}



	// FAE 9670 surlignage de la position des paramètres dans le fichier
	public void selectInViewFile(int debut, int fin) {
		
		
		// TODO 33278: on garde la colonne start pour stocker le no de colonne pour les csv. La colonne end ne sert a rien et sera masquée
		int lign = 0;
		if (!RulesManager.getInstance()
				.isCruiseInADirectory()
				|| Nemo.getInstance().getViewMain().getEditFileForm()
				.isMultiStationFileChecked()) {
			// fichier campagne
			lign =
					Nemo.getInstance().getDomain().getMedia().getModel()
					.getFileHeader();
		}
		lign +=
				Nemo.getInstance().getDomain().getMedia()
				.getStationHeaderNumberLines() + 1;

		debut += Utils.taillesCumLignes[lign - 1];
		fin += Utils.taillesCumLignes[lign - 1];
		Nemo.getInstance().getViewMain().getEditDataForm().getTextAreaData()
		.setSelection(debut - 1, fin);
	}

	private boolean isCellWritable(int colNumber, TableItem itemTmp) {
		boolean isWritable = false;
		if (columnsMap.get(colNumber).writable) {
			// case where parameter is type string
			if (itemTmp.getChecked()) {
				isWritable =
						!
						(columnsMap.get(colNumber).id.equals(COL_FORMAT)
								|| columnsMap.get(colNumber).id
								.equals(COL_CONVERSION)
								|| columnsMap.get(colNumber).id.equals(COL_UNIT));
			} else {
				isWritable = true;
			}
		}

		return isWritable;
	}
}
