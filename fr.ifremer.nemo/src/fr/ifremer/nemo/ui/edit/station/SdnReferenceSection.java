package fr.ifremer.nemo.ui.edit.station;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.NemoSection;

public class SdnReferenceSection extends NemoSection {
	private Group manualReferences ;
	SdnReferencesTableEditor table;
	public SdnReferenceSection(String name, String title, String description,
			EditForm form) {
		super(name, title, description, form);

	}

	@Override
	protected void drawSection(Composite parent, boolean filled) {
		manualReferences = new Group(parent, SWT.SHADOW_ETCHED_IN);
		manualReferences.setText("manual ref");// TODO msg
		
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(manualReferences);
	    GridDataFactory.fillDefaults().grab(true, true).applyTo(manualReferences);
	        
		Button addReference = new Button(manualReferences, SWT.PUSH);
		addReference.setText("add reference");// TODO msg
		AddReferenceListener addReferenceListener = new AddReferenceListener();
		addReference.addListener(SWT.Selection, addReferenceListener);
	    
	    table = new SdnReferencesTableEditor(manualReferences);
	    
	    
	    Button deleteReferences = new Button(manualReferences, SWT.PUSH);
	    deleteReferences.setText("delete selected references");// TODO msg
		DeleteReferenceListener deleteReferenceListener = new DeleteReferenceListener();
		deleteReferences.addListener(SWT.Selection, deleteReferenceListener);
		
		
	}
	@Override
	public void razValues() {
		// TODO Auto-generated method stub

	}

	
	public class AddReferenceListener implements Listener {
		public void handleEvent(Event event) {
			table.addRow();
		}
	}
	
	public class DeleteReferenceListener implements Listener {
		public void handleEvent(Event event) {
			table.deleteSelectedRows();
		}
	}

	public TableItem[] getSdnReferences() {
		return table.getItems();
		
	}
	@Override
    public void initModel(StationModel stationModel)
    {
        stationModel.setSdnReferences(table.getItems());
    }
}
