package fr.ifremer.nemo.ui.edit.data;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerDialog extends Dialog {

	private final String title;
	private final String message;
	private  Spinner spinner;
	private int value = 0;

	/**
	 * Constructor.
	 * 
	 * @param parentShell The parent shell.
	 */
	public SpinnerDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		// Dialog window
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		Label label = new Label(container, SWT.NONE);
		label.setText(message);
		
        // create a spinner with min value 0 and max value 1000
       spinner = new Spinner(container, SWT.NONE);
       spinner.setMinimum(1);
       spinner.setMaximum(100);
       spinner.setSelection(1);
       spinner.setIncrement(1);
       spinner.setPageIncrement(10);
//       GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
//       gridData.widthHint = SWT.DEFAULT;
//       gridData.heightHint = SWT.DEFAULT;
//       gridData.horizontalSpan = 2;
//       spinner.setLayoutData(gridData);


		return container;
	}

	
	

	// Set the Dialog title
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	// The Dialog is resizable
	@Override
	protected boolean isResizable() {
		return false;
	}

	// On OK, the selection is set
	@Override
	protected void okPressed() {
		value = spinner.getSelection();
		super.okPressed();
	}

	public int getValue() {
		return value;
	}
	
	public Spinner getSpinner() {
		return spinner;
	}
	
}
