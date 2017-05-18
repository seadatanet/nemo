package fr.ifremer.nemo.ui.edit.data.inputData;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.TableColumn;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.Utils;

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
	private TableViewer viewer;
	private final int gwidth = 550;

	// separator is part of the input model, so it is set in fill method
	String separator;
	private int indexSelected=-1;
	public InputTableData(Composite parent) {
		this.parent=parent;
	}

	public void init(ArrayList<String> lignesBrutes){
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
//					System.out.println("getBackground");
					return red;//here gray is color object defined somewhere in class
				}
				@Override
				public void update(ViewerCell cell) {
//					System.out.println("update createColumns");
					if (cell.getColumnIndex() == indexSelected){
						cell.setBackground(red );
					}else{
						cell.setBackground(null );
					}
					String res="";
					if (separator!=null){
						try{
							res= ((String)cell.getElement()).split(separator)[cell.getColumnIndex()];
						}catch (Exception e) {
							Nemo.getInstance().getLogger().debug("error on data, maybe a station header " + e);
						}
					}
					cell.setText(res);
				}
			});


			
			colCode.getColumn().setText(colTitle);
			((TableColumnLayout) compoTable.getLayout()).setColumnData(colCode.getColumn(), new ColumnWeightData(10, true));
			colCode.getColumn().addSelectionListener(getSelectionAdapter(colCode.getColumn(), colNumber));

			colNumber++;
		}

	}
	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				indexSelected = index;
				viewer.refresh();
				Nemo.getInstance().getLogger().debug("column selected : " + index);

			}
		};
		return selectionAdapter;
	}




	private void fill(ArrayList<String> lignesBrutes) {
		ArrayList<String> linesWithoutFileHeader = new ArrayList<>();
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new DataProvider());
		int cpt = 0;
		String[] split;


		int fileHeaderSize = Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader();
		int stationHeaderSize = Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines();
		int header = fileHeaderSize + stationHeaderSize;
		boolean columnsTitlesCreated=false;
		
		for (String line : lignesBrutes){
			if  (Nemo.getInstance().getDomain().getMedia().isColTitleInInputFile() && cpt==Nemo.getInstance().getDomain().getMedia().getColTitlesLineNb()-1 ){
				split = line.split(separator);
				createColumns(split);
				columnsTitlesCreated=true;
			}
			cpt++;
		}
		 cpt = 0;
		for (String line : lignesBrutes){
			
			if (cpt<header){
				// 	skip file and station header
			}
			else{
				if (!columnsTitlesCreated){
					split = line.split(separator);
					String[] colTitles = new String[split.length];

					for (int colNb=0; colNb<split.length; colNb++)
					{
						colTitles[colNb] = String.valueOf(colNb+1);
					}
					createColumns(colTitles);
					columnsTitlesCreated=true;
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
