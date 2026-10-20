package fr.ifremer.nemo.ui.edit.data.inputData;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.util.Intern;

/**
 * usefull doc:
 * http://www.vogella.com/tutorials/EclipseJFaceTableAdvanced/article.html
 * @author altran
 *
 */
public class InputTableData {



	final Color red = new Color(null, 239,180,110);
	final Color white = new Color(null, 255,255,255);




	private Composite compoTable;
	private final Composite parent;
	protected TableViewer viewer;
	private final int gwidth = 550;

	// separator is part of the input model, so it is set in fill method
	String separator;
	protected int indexSelected=-1;
	public InputTableData(Composite parent) {
		this.parent=parent;
	}

	public void init(List<String> lignesBrutes){
		separator = Nemo.getInstance().getDomain().getMedia().getModel().getSeparator();

		createCompoTable();
		fill(lignesBrutes);
	}
	private void createCompoTable() {
		compoTable = new Composite(parent, SWT.NONE);
		compoTable.setFont(Utils.font08);
		compoTable.setBackground(new Color(null, 255, 0, 0));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		TableColumnLayout layout = new TableColumnLayout();
		compoTable.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = gwidth;
		data.heightHint = 200;
		compoTable.setLayoutData(data);

		viewer = new TableViewer(compoTable, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL );



		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);


		viewer.getTable().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	}
	private String getSeparatorForSplit(){
		if (separator.equals("|")){ //$NON-NLS-1$
			return "\\|"; //$NON-NLS-1$
		}
		return separator;
	}
	/**
	 * create the columns for the table
	 * 
	 * @param viewer
	 */
	private void createColumns(String[] split) {

		int colNumber=0;
		for (String colTitle : split){
			TableViewerColumn colCode = new TableViewerColumn(viewer, SWT.LEFT);

			colCode.setLabelProvider(new ColumnLabelProvider(){

				@Override
				public Color getBackground(Object element) {
					return red;//here gray is color object defined somewhere in class
				}
				@Override
				public void update(ViewerCell cell) {
					String res="";
					if (separator!=null){
						try{

							res= ((String)cell.getElement()).split(getSeparatorForSplit())[cell.getColumnIndex()];
						}catch (Exception e) {
							Nemo.getInstance().getLogger().debug("error on data, maybe a station header " + e);
						}
					}
					cell.setText(res);
				}
			});


			colCode.getColumn().setText(colTitle);
			int colWidth = colTitle.length()*10;
			if (colWidth<100) {
				colWidth=100;
			}
			((TableColumnLayout) compoTable.getLayout()).setColumnData(colCode.getColumn(), new ColumnWeightData(0,colWidth, true));
			colCode.getColumn().addSelectionListener(getSelectionAdapter(colCode.getColumn(), colNumber));

			colNumber++;
		}
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {

		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				indexSelected = index;

				//Nemo.getInstance().getLogger().debug("column selected : " + index);
				int colCount = viewer.getTable().getColumnCount();

				Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().setIndexSelected(indexSelected);
				Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().getTable().getItem(0).setBackground(indexSelected, red);
				TableItem item = Nemo.getInstance().getViewMain().getEditDataForm().getInputTableData().getViewer().getTable().getItem(0);
				for (int colCpt=0; colCpt<colCount; colCpt++){
					item.setBackground(colCpt, white);
				}
				item.setBackground(indexSelected, red);


				TableData tableData = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData();
				if (tableData.isWaitingInputTableDataSelectionStartEnd()) {
					// if "set start/end" selected in context menu- TableData
					tableData.inputTableDataSelectionStartEndFlag(false);
					// set cursor to default
					viewer.getTable().setCursor(Display.getCurrent().getSystemCursor(SWT.DEFAULT));
				} else if (tableData.isWaitingInputTableDataSelectionFlag()) {
					// if "set flag" selected in context menu- TableData
					tableData.inputTableDataSelectionStartEndFlag(true);
					// set cursor to default
					viewer.getTable().setCursor(Display.getCurrent().getSystemCursor(SWT.DEFAULT));
				} else {
					tableData.selectTypeFromInputTableData(indexSelected);

				} 


			}
		};
		return selectionAdapter;
	}




	private void fill(List<String> lignesBrutes) {
		ArrayList<String> linesWithoutFileHeader = new ArrayList<>();
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new DataProvider());
		int cpt = 0;
		String[] split;


		int fileHeaderSize = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
		int stationHeaderSize = Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines();
		int header = fileHeaderSize + stationHeaderSize;
		boolean columnsTitlesCreated=false;

		// store column title lines (calculate quantity from string value)
		ArrayList<String[]> listColumnTitleLines = new ArrayList<String[]>();

		boolean isColTitleInInputFile = Nemo.getInstance().getDomain().getMedia().isColTitleInInputFile();
		int colTitlesLineNb = Nemo.getInstance().getDomain().getMedia().getColTitlesLineNb();
		int colTitlesLastLineNb = Nemo.getInstance().getDomain().getMedia().getColTitlesLastLineNb();

		// Column titles

		if (isColTitleInInputFile) {

			if (colTitlesLastLineNb == 0) {
				// only one line, we can generate
				// Split CSV line to array (limit = -1 for last empty column option)
				split = lignesBrutes.get(colTitlesLineNb - 1).split(getSeparatorForSplit(), -1);
				createColumns(split);
				columnsTitlesCreated = true;

			} else {
				// Case of multiple title lines.
				for (String line : lignesBrutes) {

					cpt++; // first line is indexed "1".

					// Split and collect the column titles
					// Split CSV line to array (limit = -1 for last empty column option)
					if (cpt >= colTitlesLineNb && cpt <= colTitlesLastLineNb) {
						listColumnTitleLines.add(line.split(getSeparatorForSplit(), -1));
					}

					// Loop only until the last title line
					if (cpt == colTitlesLastLineNb) {
						break;
					}
				}

				// here we have all the title lines, we can create the column titles.
				createColumns(concatMultipleColumnTitles(listColumnTitleLines));
				columnsTitlesCreated = true;

			}
		}

		// Main loop on data lines
		cpt = 0;
		for (String line : lignesBrutes) {

			if (cpt<header) {
				// 	skip file and station header
			}
			else if (cpt >= 100 + header) {

				// Display only the N first data lines
				break;
			}
			else {
				// No column titles yet? Replace them with column numbers.
				if (!columnsTitlesCreated) {
					split = line.split(getSeparatorForSplit());
					String[] colTitles = new String[split.length];

					for (int colNb = 0; colNb < split.length; colNb++)
					{
						colTitles[colNb] = String.valueOf(colNb + 1);
					}
					createColumns(colTitles);
					columnsTitlesCreated = true;
				}

				linesWithoutFileHeader.add(line);
			}

			cpt++;
		}
		try{
			viewer.setInput(linesWithoutFileHeader);
		}catch (Exception e) {
			Nemo.getInstance().getLogger().error("error while setting input data in table");
		}


	}

	/**
	 * Concat String elements at the same index from multiple String[] to create
	 * a String[] of concatenated column titles.
	 * 
	 * @param lines ArrayList of String[] (get from input lines).
	 * @return A String[] of column titles that are the concatenation of input
	 * elements.
	 */
	private String[] concatMultipleColumnTitles(ArrayList<String[]> lines) {

		String glue = Intern.separatorForColumnTitleRendering;
		
		// Compite the max number of columns between each lines
		int maxNbColunm = 0;
		for (String[] splittedLine : lines) {
			maxNbColunm = Math.max(splittedLine.length, maxNbColunm);
		}
		String[] output = new String[maxNbColunm];

		// Main loop on the lines
		for (String[] splittedLine : lines) {

			int cpt = 0;

			// loop on each column of the current line
			for (String oneColumn : splittedLine) {

				String oneColTrimmed = oneColumn.trim();
				if (oneColTrimmed.isEmpty()) {
					oneColTrimmed = "?";
				}

				if (output[cpt] != null) {
					output[cpt] = output[cpt] + glue + oneColTrimmed;
				} else {
					output[cpt] = oneColTrimmed;
				}
				cpt++;
			}
		}

		return output;
	}

	public void dispose(){ 
		compoTable.dispose();
	}

	public int getIndexSelected() {
		return indexSelected;
	}
	public void setIndexSelected(int index) {
		indexSelected = index;
	}
	public TableViewer getViewer() {
		return viewer;
	}
}
