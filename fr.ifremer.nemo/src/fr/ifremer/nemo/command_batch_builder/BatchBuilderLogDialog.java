package fr.ifremer.nemo.command_batch_builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Display the content of the bath log file.
 * @author pmahoudo
 *
 */
public class BatchBuilderLogDialog extends TitleAreaDialog {

	int batchResult;
	String logFileName;
	
	public BatchBuilderLogDialog(Shell parentShell, int batchResult, String logFileName) {
		super(parentShell);
		this.batchResult = batchResult;
		this.logFileName = logFileName;
		setShellStyle(SWT.RESIZE | SWT.MAX | SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER);
		setBlockOnOpen(false);

	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.batchBuilder_logdialog_title);
		String message = Messages.batchBuilder_logdialog_message;
		int messageType; 
		if (batchResult == 0) {
			messageType = IMessageProvider.INFORMATION;
		} else {
			messageType = IMessageProvider.ERROR;
		}
		setMessage(message, messageType);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(810, 500);
		newShell.setText(Messages.batchBuilder_logdialog_shelltitle);
	}

	@Override
	protected Point getInitialSize() {
		return getShell().computeSize(450, SWT.DEFAULT, true);
	}

	@Override
	protected boolean isResizable() {
		return true; // Allow the user to change the dialog size!
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea (parent); // Let the dialog create the parent composite

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true; // Layout vertically, too! 
		gridData.verticalAlignment = GridData.FILL;

		StyledText styledText = new StyledText(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLayoutData(gridData);


		try {
			BufferedReader br = new BufferedReader(new FileReader(logFileName));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line).append(Global.LINE_SEPARATOR);
			}
			br.close();

			styledText.setText(stringBuilder.toString());
			styledText.setTopIndex(styledText.getLineCount() - 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.batchBuilder_logdialog_close, true);

	}

	@Override
	protected void cancelPressed() {
		// fermeture de la fenetre
		close();
	}
}
