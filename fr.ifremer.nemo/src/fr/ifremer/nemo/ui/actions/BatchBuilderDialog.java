package fr.ifremer.nemo.ui.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.command_batch_builder.BatchBuilderLogDialog;
import fr.ifremer.nemo.command_batch_builder.FileCommandBatchModel;
import fr.ifremer.nemo.command_batch_builder.directory.DirectoryCommandBatchModel;
import fr.ifremer.nemo.output.cdi_summary.CDIResumeWriter;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class BatchBuilderDialog extends TitleAreaDialog implements Observer {

	private final boolean isWindows;
	private final String nemoBatchDirectoryPath;
	private final String logFileName;
	private String message = null;
	private int messageType;

	private FileCommandBatchModel fileModel;
	private DirectoryCommandBatchModel dirModel;

	public enum CommandMode {
		FILE, DIRECTORY;
	}

	private CommandMode commandMode;

	// SWT
	private TabFolder tabFolder;
	// FILE
	private Text fileInputText, fileModelText, fileOutputText;
	private Button fileConvMedButton, fileConvOdvButton, fileConvNetcdfButton;
	private Button fileConvMonoButton, fileConvMultiButton;
	private Text fileDatasetRefText, fileCruiseNameText, fileCruiseCommentText, fileLocalCsrIdentifierText,
	fileBshCsrReferenceText, fileCdiSummaryText, fileCouplingTablePrefixText, fileShipCodeText;
	private Button fileSortButton, fileCouplingTableUpdateButton;
	private StyledText  commandText;

	// DIR
	private Text dirInputText, dirXMLModelText, dirOutputText, dirCSVText;
	private Button dirConvMedButton, dirConvOdvButton, dirConvNetcdfButton;
	private Button dirConvMonoButton, dirConvMultiButton;
	private Button dirSortButton, dirCouplingTableUpdateButton;
	private Text dirCdiSummaryText, dirCouplingTablePrefixText;

	public BatchBuilderDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.MAX | SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER);
		setBlockOnOpen(false);
		
		// The launch is OS-dependent.
		isWindows = System.getProperty("os.name").toLowerCase().startsWith("win"); //$NON-NLS-1$ //$NON-NLS-2$
		//////////////////// PROD
		nemoBatchDirectoryPath = Platform.getLocation().toString() + File.separator+ "..";
//		////////////////////// DEV
//		nemoBatchPath = "C:\\Users\\pmahoudo\\Projets\\SeaDatanet\\Nemo\\recette\\current";
		// Log file name
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		logFileName = nemoBatchDirectoryPath + File.separator +((RollingFileAppender)context.getConfiguration().getAppender("nemoBATCHFile")).getFileName();			

	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.batchBuilder_title);
		setMessage(Messages.batchBuilder_message, IMessageProvider.INFORMATION);
		updateIHM();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(810, 860);
		newShell.setText(Messages.batchBuilder_text);
	}

	@Override
	protected Point getInitialSize() {
		return getShell().computeSize(450, SWT.DEFAULT, true);
	}

	public void init() {
		fileModel = new FileCommandBatchModel();
		dirModel = new DirectoryCommandBatchModel();
		commandMode = CommandMode.FILE;

		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		RulesManager rm = RulesManager.getInstance();

		// init FileModel

		if (editFileForm.isCruiseIsAFile()) {
			if (editFileForm.nomFichierOrigin != null) {
				fileModel.setFileNameIn(editFileForm.nomFichierOrigin);
			}
			if (Nemo.getInstance().getCurrentOpenModelPath() != null) {
				fileModel.setXMLModelName(Nemo.getInstance().getCurrentOpenModelPath());
			}
			if (Nemo.getInstance().getDomain().getMedia().getOutputFileName() != null) {
				fileModel.setFileNameOut(Nemo.getInstance().getDomain().getMedia().getOutputFileName());
			}

			if (rm.getConversion() != null) {
				fileModel.setConvType(rm.getConversion());
			}

			if (rm.isConversionToODVMultiStation() || rm.isConversionToNetCDFMultiStation()) {
				fileModel.setMulti(true);
			} else {
				fileModel.setMulti(false);
			}
			if (Nemo.getInstance().getDomain().getCruise().getModel().getReference() != null) {
				fileModel.setDatasetRef(Nemo.getInstance().getDomain().getCruise().getModel().getReference());
			}
			if (Nemo.getInstance().getDomain().getCruise().getModel().getName() != null) {
				fileModel.setCruiseName(Nemo.getInstance().getDomain().getCruise().getModel().getName());
			}
			if (Nemo.getInstance().getDomain().getCruise().getModel().getComment() != null) {
				// Clean comment from \n & \r
				String cleanComment = Nemo.getInstance().getDomain().getCruise().getModel().getComment().replaceAll("[\r\n]", "");
				fileModel.setCruiseComment(cleanComment);
			}
			if (Nemo.getInstance().getDomain().getCruise().getModel().getLocalCsrIdentifier() != null) {
				fileModel.setLocalCsrIdentifier(
						Nemo.getInstance().getDomain().getCruise().getModel().getLocalCsrIdentifier());
			}
			if (Nemo.getInstance().getDomain().getCruise().getModel().getBshCsrReference() != null) {
				fileModel
				.setBshCsrReference(Nemo.getInstance().getDomain().getCruise().getModel().getBshCsrReference());
			}
			// sort
			if (editFileForm.isSortForced()) {
				fileModel.setSort(true);
			} else {
				fileModel.setSort(false);
			}
			// cdisummary
			if (Nemo.getInstance().getConfig().isWriteCdi()
					&& CDIResumeWriter.getInstance().getNomFichierSortie() != null) {
				fileModel.setCdiSummary(CDIResumeWriter.getInstance().getNomFichierSortie());
			}
			// coupling Table
			if (Nemo.getInstance().getConfig().isWriteMapping()) {
				fileModel.setCouplingTableUpdate(true);
				if (Nemo.getInstance().getDomain().getConversion().getCouplingTablePrefix() != null) {
					fileModel.setCouplingTablePrefix(
							Nemo.getInstance().getDomain().getConversion().getCouplingTablePrefix());
				}
			} else {
				fileModel.setCouplingTableUpdate(false);
				fileModel.setCouplingTablePrefix(Global.EMPTY_STRING);
			}

			if (Nemo.getInstance().getDomain().getCruise().getModel().getShipCode() != null) {
				fileModel.setShipCode(Nemo.getInstance().getDomain().getCruise().getModel().getShipCode());
			}

		}

		if (rm.isCruiseInADirectory()) {
			if (editFileForm.nomRepertoireOrigin != null) {
				dirModel.setDirNameIn(editFileForm.nomRepertoireOrigin);
			}
			if (rm.getConversion() != null) {
				dirModel.setConvType(rm.getConversion());
			}
			if (rm.isConversionToODVMultiStation() || rm.isConversionToNetCDFMultiStation()) {
				dirModel.setMulti(true);
			} else {
				dirModel.setMulti(false);
			}
			// sort
			if (editFileForm.isSortForced()) {
				dirModel.setSort(true);
			} else {
				dirModel.setSort(false);
			}
			// coupling Table
			if (Nemo.getInstance().getConfig().isWriteMapping()) {
				dirModel.setCouplingTableUpdate(true);
				if (Nemo.getInstance().getDomain().getConversion().getCouplingTablePrefix() != null) {
					dirModel.setCouplingTablePrefix(
							Nemo.getInstance().getDomain().getConversion().getCouplingTablePrefix());
				}
			} else {
				dirModel.setCouplingTableUpdate(false);
				dirModel.setCouplingTablePrefix(Global.EMPTY_STRING);
			}

		}
		// Add observer to model
		fileModel.addObserver(this);
		dirModel.addObserver(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayout(new GridLayout());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		sashForm.setLayoutData(data);

		tabFolder = new TabFolder(sashForm, SWT.BORDER);
		tabFolder.setLayout(new GridLayout());
		tabFolder.setLayoutData(data);

		// File Tab
		TabItem fileTabItem = new TabItem(tabFolder, SWT.NULL);
		fileTabItem.setText(Messages.batchBuilder_fileTab);

		ScrolledComposite fileScrolledComposite = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		fileScrolledComposite.setLayout(new GridLayout());
		fileScrolledComposite.setLayoutData(data);

		Composite fileComposite = new Composite(fileScrolledComposite, SWT.NONE);
		fileComposite.setLayout(new GridLayout());
		fileComposite.setLayoutData(data);
		fileScrolledComposite.setContent(fileComposite);

		createFileInputOutputComposite(fileComposite);
		createFileConversionComposite(fileComposite);
		createFileOtherComposite(fileComposite);
		// Expand both horizontally and vertically
		fileScrolledComposite.setExpandHorizontal(true);
		fileScrolledComposite.setExpandVertical(true);
		fileScrolledComposite.setMinSize(fileScrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		fileTabItem.setControl(fileScrolledComposite);

		// Directory Tab
		TabItem dirTabItem = new TabItem(tabFolder, SWT.NULL);
		dirTabItem.setText(Messages.batchBuilder_directoryTab);

		ScrolledComposite dirScrolledComposite = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		dirScrolledComposite.setLayout(new GridLayout());
		dirScrolledComposite.setLayoutData(data);

		Composite dirComposite = new Composite(dirScrolledComposite, SWT.NONE);
		dirComposite.setLayout(new GridLayout());
		dirComposite.setLayoutData(data);
		dirScrolledComposite.setContent(dirComposite);

		createDirInputOutputComposite(dirComposite);
		createDirConversionComposite(dirComposite);
		createDirOtherComposite(dirComposite);

		// Expand both horizontally and vertically
		dirScrolledComposite.setExpandHorizontal(true);
		dirScrolledComposite.setExpandVertical(true);

		dirScrolledComposite.setMinSize(dirScrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		dirTabItem.setControl(dirScrolledComposite);

		// Command Composite
		createCommandComposite(sashForm);

		// Set weights of sash children
		sashForm.setWeights(new int[] { 4, 1 });

		tabFolder.addListener(SWT.Selection, event -> {

			int tabItemIndex = tabFolder.getSelectionIndex();
			if (tabItemIndex == 0) {
				commandMode = CommandMode.FILE;
				commandText.setWordWrap(true);
				commandText.setAlwaysShowScrollBars(false);
				this.getButton(IDialogConstants.PROCEED_ID).setVisible(true);
			} else if (tabItemIndex == 1) {
				commandMode = CommandMode.DIRECTORY;
				commandText.setWordWrap(false);
				commandText.setAlwaysShowScrollBars(false);
				this.getButton(IDialogConstants.PROCEED_ID).setVisible(false);
			}

			updateCommandText();
			validate();
		});

		return sashForm;
	}

	private void createFileInputOutputComposite(Composite parent) {
		Group inputGroup = new Group(parent, SWT.NONE);
		inputGroup.setText(Messages.batchBuilder_inputOutput);
		GridLayoutFactory.fillDefaults().applyTo(inputGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		inputGroup.setLayoutData(data);

		// INPUT FILE
		Group inputFileGroup = new Group(inputGroup, SWT.NONE);
		inputFileGroup.setText(Messages.batchBuilder_file_inputFile);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(inputFileGroup);
		inputFileGroup.setLayoutData(data);

		fileInputText = new Text(inputFileGroup, SWT.BORDER);
		// GridDataFactory.fillDefaults().grab(true, false).applyTo(inputText);
		fileInputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// inputText.setFont(Utils.font08);
		fileInputText.addModifyListener(event -> {
			fileModel.setFileNameIn(fileInputText.getText());
			validate();
		});

		Button inputButton = new Button(inputFileGroup, SWT.PUSH);
		inputButton.setText(Messages.button_browse);
		inputButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			String fileName = dialog.open();
			if (fileName != null) {
				fileModel.setFileNameIn(fileName);
				fileInputText.setText(fileName);
			}
		});

		// MODEL FILE
		Group modelFileGroup = new Group(inputGroup, SWT.NONE);
		modelFileGroup.setText(Messages.batchBuilder_file_modelFile);
		// modelFileGroup.setLayout(new GridLayout(2, false));
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(modelFileGroup);
		modelFileGroup.setLayoutData(data);
		fileModelText = new Text(modelFileGroup, SWT.BORDER);
		fileModelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// modelText.setFont(Utils.font08);
		fileModelText.addModifyListener(event -> {
			fileModel.setXMLModelName(fileModelText.getText());
			validate();
		});

		Button modelButton = new Button(modelFileGroup, SWT.PUSH);
		modelButton.setText(Messages.button_browse);
		modelButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory() + File.separator);
			dialog.setText(Messages.dialog_openModel);
			String extensions[] = new String[1];
			extensions[0] = Global.STAR + Global.EXT_XML;
			dialog.setFilterExtensions(extensions);
			String modelName = dialog.open();
			if (modelName != null) {
				fileModel.setXMLModelName(modelName);
				fileModelText.setText(modelName);
			}
		});

		// OUTPUT FILE
		Group outputFileGroup = new Group(inputGroup, SWT.NONE);
		outputFileGroup.setText(Messages.batchBuilder_file_outputFile);
		// outputFileGroup.setLayout(new GridLayout(2, false));
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(outputFileGroup);
		outputFileGroup.setLayoutData(data);
		fileOutputText = new Text(outputFileGroup, SWT.BORDER);
		// outputText.setFont(Utils.font08);
		fileOutputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fileOutputText.addModifyListener(event -> {
			fileModel.setFileNameOut(fileOutputText.getText());
			validate();
		});

		Button outputButton = new Button(outputFileGroup, SWT.PUSH);
		outputButton.setText(Messages.button_browse);
		outputButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.APPLICATION_MODAL);
			dialog.setText(Messages.edit_convert_outputFilename);

			String[] extensions = new String[3];
			String extenstionString1 = Global.EXT_TXT;
			String extenstionString2 = Global.EXT_NC;
			String extenstionString3 = Global.ANY_STRING;
			extensions[0] = Global.STAR + extenstionString1;
			extensions[1] = Global.STAR + extenstionString2;
			extensions[2] = Global.STAR + extenstionString3;
			dialog.setFilterExtensions(extensions);

			// FAE 11171: setFilterPath attend un nom de repertoire
			String nomRepertoire = Nemo.getInstance().getViewMain().getEditFileForm().nomRepertoire;
			if (nomRepertoire != null && !nomRepertoire.isEmpty()) {
				dialog.setFilterPath(nomRepertoire.substring(0, nomRepertoire.lastIndexOf(File.separator)));
			}else {
				dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			}

			String nomFichierSortie = dialog.open();
			if (nomFichierSortie != null) {
				fileModel.setFileNameOut(nomFichierSortie);
				fileOutputText.setText(nomFichierSortie);
			}
		});

	}

	private void createFileConversionComposite(Composite parent) {
		Group conversionGroup = new Group(parent, SWT.NONE);
		conversionGroup.setText(Messages.batchBuilder_conversion);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(conversionGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		conversionGroup.setLayoutData(data);

		// CONVERSION TYPE
		Group convTypeGroup = new Group(conversionGroup, SWT.NONE);
		convTypeGroup.setText(Messages.batchBuilder_conversionType);
		GridLayoutFactory.fillDefaults().applyTo(convTypeGroup);
		convTypeGroup.setLayoutData(data);

		fileConvMedButton = new Button(convTypeGroup, SWT.RADIO);
		fileConvMedButton.setText(Messages.batchBuilder_conversion_medatlas);
		fileConvMedButton.addListener(SWT.Selection, event -> {
			fileModel.setConvType(Conversion.MEDATLAS);
		});
		fileConvOdvButton = new Button(convTypeGroup, SWT.RADIO);
		fileConvOdvButton.setText(Messages.batchBuilder_conversion_odv);
		fileConvOdvButton.addListener(SWT.Selection, event -> {
			fileModel.setConvType(Conversion.ODV);
		});
		fileConvNetcdfButton = new Button(convTypeGroup, SWT.RADIO);
		fileConvNetcdfButton.setText(Messages.batchBuilder_conversion_netcdf);
		fileConvNetcdfButton.addListener(SWT.Selection, event -> {
			fileModel.setConvType(Conversion.NETCDF);
		});

		// MONO / MULTI
		Group convOptionGroup = new Group(conversionGroup, SWT.NONE);
		convOptionGroup.setText(Messages.batchBuilder_conversionOption);
		GridLayoutFactory.fillDefaults().applyTo(convOptionGroup);
		convOptionGroup.setLayoutData(data);

		fileConvMonoButton = new Button(convOptionGroup, SWT.RADIO);
		fileConvMonoButton.setText(Messages.batchBuilder_conversion_mono);
		fileConvMonoButton.addListener(SWT.Selection, event -> {
			fileModel.setMulti(false);
		});
		fileConvMultiButton = new Button(convOptionGroup, SWT.RADIO);
		fileConvMultiButton.setText(Messages.batchBuilder_conversion_multi);
		fileConvMultiButton.addListener(SWT.Selection, event -> {
			fileModel.setMulti(true);
		});
		new Label(convOptionGroup, SWT.NONE);

	}

	private void createFileOtherComposite(Composite parent) {
		Group otherGroup = new Group(parent, SWT.NONE);
		otherGroup.setText(Messages.batchBuilder_other);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(otherGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		otherGroup.setLayoutData(data);

		Label datasetRefLabel = new Label(otherGroup, SWT.NONE);
		datasetRefLabel.setText(Messages.batchBuilder_file_datasetRef);
		fileDatasetRefText = new Text(otherGroup, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		fileDatasetRefText.setLayoutData(data);
		fileDatasetRefText.addModifyListener(event -> {
			fileModel.setDatasetRef(fileDatasetRefText.getText());
		});

		Label cruiseNameLabel = new Label(otherGroup, SWT.NONE);
		cruiseNameLabel.setText(Messages.batchBuilder_file_cruiseName);
		fileCruiseNameText = new Text(otherGroup, SWT.BORDER);
		fileCruiseNameText.setLayoutData(data);
		fileCruiseNameText.addModifyListener(event -> {
			fileModel.setCruiseName(fileCruiseNameText.getText());
		});

		Label cruiseCommentLabel = new Label(otherGroup, SWT.NONE);
		cruiseCommentLabel.setText(Messages.batchBuilder_file_cruiseComment);
		fileCruiseCommentText = new Text(otherGroup, SWT.BORDER);
		fileCruiseCommentText.setLayoutData(data);
		fileCruiseCommentText.addModifyListener(event -> {
			fileModel.setCruiseComment(fileCruiseCommentText.getText());
		});

		Label localCsrIdentifierLabel = new Label(otherGroup, SWT.NONE);
		localCsrIdentifierLabel.setText(Messages.batchBuilder_file_localCsrIdentifier);
		fileLocalCsrIdentifierText = new Text(otherGroup, SWT.BORDER);
		fileLocalCsrIdentifierText.setLayoutData(data);
		fileLocalCsrIdentifierText.addModifyListener(event -> {
			fileModel.setLocalCsrIdentifier(fileLocalCsrIdentifierText.getText());
		});

		Label bshCsrReferenceLabel = new Label(otherGroup, SWT.NONE);
		bshCsrReferenceLabel.setText(Messages.batchBuilder_file_bshCsrReference);
		fileBshCsrReferenceText = new Text(otherGroup, SWT.BORDER);
		fileBshCsrReferenceText.setLayoutData(data);
		fileBshCsrReferenceText.addModifyListener(event -> {
			fileModel.setBshCsrReference(fileBshCsrReferenceText.getText());
		});

		Label fileSortLabel = new Label(otherGroup, SWT.NONE);
		fileSortLabel.setText(Messages.batchBuilder_sort);
		fileSortButton = new Button(otherGroup, SWT.CHECK);
		fileSortButton.addListener(SWT.Selection, event -> {
			fileModel.setSort(fileSortButton.getSelection());
		});

		// CDI summary
		Label cdiSummaryLabel = new Label(otherGroup, SWT.NONE);
		cdiSummaryLabel.setText(Messages.batchBuilder_file_cdiSummary);

		Composite cdiSummaryCompsoite = new Composite(otherGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(cdiSummaryCompsoite);
		cdiSummaryCompsoite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		fileCdiSummaryText = new Text(cdiSummaryCompsoite, SWT.BORDER);
		// GridDataFactory.fillDefaults().grab(true, false).applyTo(inputText);
		fileCdiSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// inputText.setFont(Utils.font08);
		fileCdiSummaryText.addModifyListener(event -> {
			fileModel.setCdiSummary(fileCdiSummaryText.getText());
		});

		Button cdiSummaryButton = new Button(cdiSummaryCompsoite, SWT.PUSH);
		cdiSummaryButton.setText(Messages.button_browse);
		cdiSummaryButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.APPLICATION_MODAL);
			dialog.setText(Messages.CDIResumeWriter_choixFichier);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			String extensions[] = new String[1];
			extensions[0] = Global.STAR + Global.EXT_TXT;
			dialog.setFilterExtensions(extensions);
			String fileName = dialog.open();
			if (fileName != null) {
				fileModel.setCdiSummary(fileName);
				fileCdiSummaryText.setText(fileName);
			}
		});


		Label fileCouplingTableUpdateLabel = new Label(otherGroup, SWT.NONE);
		fileCouplingTableUpdateLabel.setText(Messages.batchBuilder_couplingUpdate);
		fileCouplingTableUpdateButton = new Button(otherGroup, SWT.CHECK);
		fileCouplingTableUpdateButton.addListener(SWT.Selection, event -> {
			fileModel.setCouplingTableUpdate(fileCouplingTableUpdateButton.getSelection());
			fileCouplingTablePrefixText.setEnabled(fileCouplingTableUpdateButton.getSelection());
		});

		Label couplingTablePrefixLabel = new Label(otherGroup, SWT.NONE);
		couplingTablePrefixLabel.setText(Messages.batchBuilder_couplingTablePrefix);
		fileCouplingTablePrefixText = new Text(otherGroup, SWT.BORDER);
		fileCouplingTablePrefixText.setLayoutData(data);
		fileCouplingTablePrefixText.addModifyListener(event -> {
			fileModel.setCouplingTablePrefix(fileCouplingTablePrefixText.getText());
		});

		Label shipCodeLabel = new Label(otherGroup, SWT.NONE);
		shipCodeLabel.setText(Messages.batchBuilder_file_shipCode);
		fileShipCodeText = new Text(otherGroup, SWT.BORDER);
		fileShipCodeText.setLayoutData(data);
		fileShipCodeText.addModifyListener(event -> {
			fileModel.setShipCode(fileShipCodeText.getText());
		});
	}

	private void createDirInputOutputComposite(Composite parent) {
		Group inputGroup = new Group(parent, SWT.NONE);
		inputGroup.setText(Messages.batchBuilder_inputOutput);
		GridLayoutFactory.fillDefaults().applyTo(inputGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		inputGroup.setLayoutData(data);

		// INPUT DIRECTORY
		Group inputDirGroup = new Group(inputGroup, SWT.NONE);
		inputDirGroup.setText(Messages.batchBuilder_dir_inputDir);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(inputDirGroup);
		inputDirGroup.setLayoutData(data);

		dirInputText = new Text(inputDirGroup, SWT.BORDER);
		dirInputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dirInputText.addModifyListener(event -> {
			dirModel.setDirNameIn(dirInputText.getText());
			validate();
		});

		Button inputButton = new Button(inputDirGroup, SWT.PUSH);
		inputButton.setText(Messages.button_browse);
		inputButton.addListener(SWT.Selection, event -> {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(),
					SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			String dirName = dialog.open();
			if (dirName != null) {
				dirModel.setDirNameIn(dirName);
				dirInputText.setText(dirName);
			}
		});
		
		// XML MODEL DIRECTORY
		Group modelDirGroup = new Group(inputGroup, SWT.NONE);
		modelDirGroup.setText(Messages.batchBuilder_dir_modelDir);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(modelDirGroup);
		modelDirGroup.setLayoutData(data);

		dirXMLModelText = new Text(modelDirGroup, SWT.BORDER);
		dirXMLModelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dirXMLModelText.addModifyListener(event -> {
			dirModel.setDirXMLModel(dirXMLModelText.getText());
			validate();
		});

		Button modelButton = new Button(modelDirGroup, SWT.PUSH);
		modelButton.setText(Messages.button_browse);
		modelButton.addListener(SWT.Selection, event -> {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(),
					SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			String dirName = dialog.open();
			if (dirName != null) {
				dirModel.setDirXMLModel(dirName);
				dirXMLModelText.setText(dirName);
			}
		});
		
		// OUTPUT DIRECTORY
		Group outputDirGroup = new Group(inputGroup, SWT.NONE);
		outputDirGroup.setText(Messages.batchBuilder_dir_outputDir);
		// outputFileGroup.setLayout(new GridLayout(2, false));
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(outputDirGroup);
		outputDirGroup.setLayoutData(data);
		dirOutputText = new Text(outputDirGroup, SWT.BORDER);
		// outputText.setFont(Utils.font08);
		dirOutputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dirOutputText.addModifyListener(event -> {
			dirModel.setDirNameOut(dirOutputText.getText());
			validate();
		});
		Button outputButton = new Button(outputDirGroup, SWT.PUSH);
		outputButton.setText(Messages.button_browse);
		outputButton.addListener(SWT.Selection, event -> {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(),
					SWT.SAVE | SWT.APPLICATION_MODAL);
			// FAE 11171: setFilterPath attend un nom de repertoire
			String nomRepertoire = Nemo.getInstance().getViewMain().getEditFileForm().nomRepertoire;
			if (nomRepertoire != null && !nomRepertoire.isEmpty()) {
				dialog.setFilterPath(nomRepertoire.substring(0, nomRepertoire.lastIndexOf(File.separator)));
			}else {
				dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			}

			String nomDirSortie = dialog.open();
			dirModel.setDirNameOut(nomDirSortie);
			dirOutputText.setText(nomDirSortie);
		});

		// CSV MAPPING FILE
		Group csvFileGroup = new Group(inputGroup, SWT.NONE);
		csvFileGroup.setText(Messages.batchBuilder_dir_csvFile);
		// modelFileGroup.setLayout(new GridLayout(2, false));
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(csvFileGroup);
		csvFileGroup.setLayoutData(data);
		dirCSVText = new Text(csvFileGroup, SWT.READ_ONLY | SWT.BORDER);
		dirCSVText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dirCSVText.addModifyListener(event -> {
			dirModel.setCSVName(dirCSVText.getText());
			if (dirCSVText.getText() != null && !dirCSVText.getText().isEmpty()) {
				try {
					dirModel.readAndLoadCSVModel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			validate();
		});
		Button csvButton = new Button(csvFileGroup, SWT.PUSH);
		csvButton.setText(Messages.button_browse);
		csvButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getModelsDirectory() + File.separator);
			dialog.setText(Messages.dialog_openModel);
			String extensions[] = new String[1];
			extensions[0] = Global.STAR + Global.EXT_CSV;
			dialog.setFilterExtensions(extensions);
			String csvName = dialog.open();
			if (csvName != null) {
				dirModel.setCSVName(csvName);
				dirCSVText.setText(csvName);
				try {
					dirModel.readAndLoadCSVModel();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});



	}

	private void createDirConversionComposite(Composite parent) {
		Group conversionGroup = new Group(parent, SWT.NONE);
		conversionGroup.setText(Messages.batchBuilder_conversion);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(conversionGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		conversionGroup.setLayoutData(data);

		// CONVERSION TYPE
		Group convTypeGroup = new Group(conversionGroup, SWT.NONE);
		convTypeGroup.setText(Messages.batchBuilder_conversionType);
		GridLayoutFactory.fillDefaults().applyTo(convTypeGroup);
		convTypeGroup.setLayoutData(data);

		dirConvMedButton = new Button(convTypeGroup, SWT.RADIO);
		dirConvMedButton.setText(Messages.batchBuilder_conversion_medatlas);
		dirConvMedButton.addListener(SWT.Selection, event -> {
			dirModel.setConvType(Conversion.MEDATLAS);
		});
		dirConvOdvButton = new Button(convTypeGroup, SWT.RADIO);
		dirConvOdvButton.setText(Messages.batchBuilder_conversion_odv);
		dirConvOdvButton.addListener(SWT.Selection, event -> {
			dirModel.setConvType(Conversion.ODV);
		});
		dirConvNetcdfButton = new Button(convTypeGroup, SWT.RADIO);
		dirConvNetcdfButton.setText(Messages.batchBuilder_conversion_netcdf);
		dirConvNetcdfButton.addListener(SWT.Selection, event -> {
			dirModel.setConvType(Conversion.NETCDF);
		});

		// MONO / MULTI
		Group convOptionGroup = new Group(conversionGroup, SWT.NONE);
		convOptionGroup.setText(Messages.batchBuilder_conversionOption);
		GridLayoutFactory.fillDefaults().applyTo(convOptionGroup);
		convOptionGroup.setLayoutData(data);

		dirConvMonoButton = new Button(convOptionGroup, SWT.RADIO);
		dirConvMonoButton.setText(Messages.batchBuilder_conversion_mono);
		dirConvMonoButton.addListener(SWT.Selection, event -> {
			dirModel.setMulti(false);
		});
		dirConvMultiButton = new Button(convOptionGroup, SWT.RADIO);
		dirConvMultiButton.setText(Messages.batchBuilder_conversion_multi);
		dirConvMultiButton.addListener(SWT.Selection, event -> {
			dirModel.setMulti(true);
		});
		new Label(convOptionGroup, SWT.NONE);

	}

	private void createDirOtherComposite(Composite parent) {
		Group otherGroup = new Group(parent, SWT.NONE);
		otherGroup.setText(Messages.batchBuilder_other);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(otherGroup);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		otherGroup.setLayoutData(data);

		Label dirSortLabel = new Label(otherGroup, SWT.NONE);
		dirSortLabel.setText(Messages.batchBuilder_sort);
		dirSortButton = new Button(otherGroup, SWT.CHECK);
		dirSortButton.addListener(SWT.Selection, event -> {
			dirModel.setSort(dirSortButton.getSelection());
		});

		
		// CDI summary
		Label cdiSummaryLabel = new Label(otherGroup, SWT.NONE);
		cdiSummaryLabel.setText(Messages.batchBuilder_dir_cdiSummary);

		Composite cdiSummaryCompsoite = new Composite(otherGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(cdiSummaryCompsoite);
		cdiSummaryCompsoite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		dirCdiSummaryText = new Text(cdiSummaryCompsoite, SWT.BORDER);
		dirCdiSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dirCdiSummaryText.addModifyListener(event -> {
			dirModel.setDirCdiSUmmary(dirCdiSummaryText.getText());
		});

		Button cdiSummaryButton = new Button(cdiSummaryCompsoite, SWT.PUSH);
		cdiSummaryButton.setText(Messages.button_browse);
		cdiSummaryButton.addListener(SWT.Selection, event -> {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(),
					SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
			String dirName = dialog.open();
			if (dirName != null) {
				dirModel.setDirCdiSUmmary(dirName);
				dirCdiSummaryText.setText(dirName);
			}
		});

		Label dirCouplingTableUpdateLabel = new Label(otherGroup, SWT.NONE);
		dirCouplingTableUpdateLabel.setText(Messages.batchBuilder_couplingUpdate);
		dirCouplingTableUpdateButton = new Button(otherGroup, SWT.CHECK);
		dirCouplingTableUpdateButton.addListener(SWT.Selection, event -> {
			dirModel.setCouplingTableUpdate(dirCouplingTableUpdateButton.getSelection());
			dirCouplingTablePrefixText.setEnabled(dirCouplingTableUpdateButton.getSelection());
		});

		Label couplingTablePrefixLabel = new Label(otherGroup, SWT.NONE);
		couplingTablePrefixLabel.setText(Messages.batchBuilder_couplingTablePrefix);
		dirCouplingTablePrefixText = new Text(otherGroup, SWT.BORDER);
		dirCouplingTablePrefixText.setLayoutData(data);

	}

	private void createCommandComposite(final Composite parent) {
		Composite commandComposite = new Composite(parent, SWT.NONE);
		commandComposite.setLayout(new GridLayout());
		// GridData data = new GridData(SWT.DEFAULT, SWT.TOP, true,true);
		// commandComposite.setLayoutData(data);
		commandComposite.setLayoutData(new GridData(SWT.FILL, SWT.END, true, true));
		Group commandGroup = new Group(commandComposite, SWT.NONE);
		commandGroup.setText(Messages.batchBuilder_commandLine);
		commandGroup.setLayout(new GridLayout());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		commandGroup.setLayoutData(data);

		commandText = new StyledText(commandGroup, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		commandText.setLayoutData(data);
		commandText.setWordWrap(true);
		commandText.setAlwaysShowScrollBars(false);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		Button resetButton = createButton(parent, IDialogConstants.RETRY_ID, Messages.batchBuilder_button_reset, false);
		Button saveButton = createButton(parent, IDialogConstants.OPEN_ID, Messages.batchBuilder_button_save, false);
		Button runButton = createButton(parent, IDialogConstants.PROCEED_ID, Messages.batchBuilder_button_run, false);
//		Button runButton = createButton(parent, IDialogConstants.OK_ID, Messages.batchBuilder_button_run, false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.button_cancel, false);

		resetButton.addListener(SWT.Selection, event -> {
			fileModel.init();
			dirModel.init();
			updateIHM();
		});

		saveButton.addListener(SWT.Selection, event -> {
			try {
				saveCommandInFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		runButton.setEnabled(false);
		runButton.addListener(SWT.Selection, event -> {
			runBatch();
		});
		

	}

	private void saveCommandInFile() throws IOException {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.APPLICATION_MODAL);
		// dialog.setText(Messages.CDIResumeWriter_choixFichier);
		dialog.setFilterPath(Nemo.getInstance().getConfig().getCruisesDirectory());
		// dialog.setFilterPath(Nemo.getInstance().getConfig().getOutputDirectory());
		String extensions[] = new String[2];
		String osExt;
		if (isWindows) {
			osExt = Global.EXT_BAT;
		} else {
			osExt = Global.EXT_SH;
		}
		extensions[0] = Global.STAR + osExt;
		extensions[1] = Global.STAR + Global.EXT_TXT;
		dialog.setFilterExtensions(extensions);
		String commandFileName = dialog.open();
		boolean save = false;
		if (commandFileName != null) {
			File fich = new File(commandFileName);
			if (fich.exists()) {
				// fichier existe
				Shell shell2 = Utils.getMainShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);

				mb.setMessage(Messages.confirm_eraseFile);

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					saveCommandInFile();
				} else {
					// on écrase le fichier
					save = true;
				}
			} else {
				// le fichier n'existe pas, on le cree
				save = true;
			}

			if (save) {
				File output = new File(commandFileName);	
				OutputStream outputStream = new FileOutputStream(output);
				// UTF8 encoding
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, Global.NEMO_SCRIPT_ENCODING);
				// write command header
				outputStreamWriter.write(buildCommandHeader());

				// write command
				outputStreamWriter.write(commandText.getText());
				outputStreamWriter.flush();
				outputStreamWriter.close();
			}
		}

	}

	protected void runBatch() {

		String commandString = commandText.getText();
		ProcessBuilder builder = constructBatchProcessBuilder(commandString);

		Process process;
		try {
	
			process = builder.start();

			int exitCode =  process.waitFor();
			
			assert exitCode == 0;
			if (new File(logFileName).exists()) {
				BatchBuilderLogDialog dialog = new BatchBuilderLogDialog(Display.getCurrent().getActiveShell(), exitCode, logFileName);
				dialog.open();
			} else {
				if (exitCode == 0) {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.success, Messages.success);
				} else {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),  Messages.error, Messages.error);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void cancelPressed() {
		// fermeture de la fenetre
		close();
	}

	protected void updateIHM() {

		// FILE
		fileInputText.setText(fileModel.getFileNameIn());
		fileOutputText.setText(fileModel.getFileNameOut());
		fileModelText.setText(fileModel.getXMLModelName());

		fileConvMedButton.setSelection(fileModel.getConvType() == Conversion.MEDATLAS);
		fileConvOdvButton.setSelection(fileModel.getConvType() == Conversion.ODV);
		fileConvNetcdfButton.setSelection(fileModel.getConvType() == Conversion.NETCDF);

		fileConvMonoButton.setSelection(!fileModel.isMulti());
		fileConvMultiButton.setSelection(fileModel.isMulti());

		fileDatasetRefText.setText(fileModel.getDatasetRef());
		fileCruiseNameText.setText(fileModel.getCruiseName());
		fileCruiseCommentText.setText(fileModel.getCruiseComment());
		fileLocalCsrIdentifierText.setText(fileModel.getLocalCsrIdentifier());
		fileBshCsrReferenceText.setText(fileModel.getBshCsrReference());
		fileSortButton.setSelection(fileModel.isSort());
		fileCdiSummaryText.setText(fileModel.getCdiSummary());
		fileCouplingTableUpdateButton.setSelection(fileModel.isCouplingTableUpdate());
		fileCouplingTablePrefixText.setEnabled(fileModel.isCouplingTableUpdate());
		fileCouplingTablePrefixText.setText(fileModel.getCouplingTablePrefix());
		fileShipCodeText.setText(fileModel.getShipCode());

		// DIR
		dirInputText.setText(dirModel.getDirNameIn());
		dirXMLModelText.setText(dirModel.getDirXMLModel());
		dirOutputText.setText(dirModel.getDirNameOut());
		dirCSVText.setText(dirModel.getCSVName());

		dirConvMedButton.setSelection(dirModel.getConvType() == Conversion.MEDATLAS);
		dirConvOdvButton.setSelection(dirModel.getConvType() == Conversion.ODV);
		dirConvNetcdfButton.setSelection(dirModel.getConvType() == Conversion.NETCDF);

		dirConvMonoButton.setSelection(!dirModel.isMulti());
		dirConvMultiButton.setSelection(dirModel.isMulti());

		dirSortButton.setSelection(dirModel.isSort());
		dirCdiSummaryText.setText(dirModel.getDirCdiSummary());
		dirCouplingTableUpdateButton.setSelection(dirModel.isCouplingTableUpdate());
		dirCouplingTablePrefixText.setEnabled(dirModel.isCouplingTableUpdate());
		dirCouplingTablePrefixText.setText(dirModel.getCouplingTablePrefix());

	}
	
	private String buildCommand() {
		StringBuilder command = new StringBuilder();
		if (commandMode == CommandMode.FILE) {
			command.append(fileModel.buildCommand());
		} else if (commandMode == CommandMode.DIRECTORY) {
			command.append(dirModel.buildCommand());
		}
		return command.toString();
	}
	
	private String buildCommandHeader() {
		StringBuilder commandHeader = new StringBuilder();

		if (!isWindows) {
			commandHeader.append("#!/bin/sh" + Global.LINE_SEPARATOR+ Global.LINE_SEPARATOR);
		} else {
			// Force utf8 encoding
			commandHeader.append("chcp 65001" + Global.LINE_SEPARATOR);		
		}

		commandHeader.append("cd " +nemoBatchDirectoryPath+ Global.LINE_SEPARATOR);

		return commandHeader.toString();
	}

	private void updateCommandText() {
		// commandText.set();
		commandText.setText(buildCommand());
	}

	@Override
	public void update(Observable o, Object arg) {
		// Update the commande line widget
		updateCommandText();

	}

	/**
	 * Generates and starts a batch Process.
	 * 
	 * @param shell the shell, according to the OS.
	 * @param option shell options.
	 * @param command the command to launch.
	 * @return the generated Process.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private ProcessBuilder constructBatchProcessBuilder(String command) {
		String shell;
		String option;

		if (isWindows) {
			shell = "cmd"; //$NON-NLS-1$
			option = "/c"; //$NON-NLS-1$
		} else {

			shell = "sh"; //$NON-NLS-1$
			option = "-c"; //$NON-NLS-1$
		}

		ProcessBuilder builder = new ProcessBuilder(shell, option, command);
		// Set the process builder in the nemo_batch exec directory
		builder = builder.directory(new File(nemoBatchDirectoryPath));

		return builder;
	}


	/**
	 * Check if form is valid. Sett error message and enable/disable OK Button
	 */
	private void validate() {
		if (commandMode == CommandMode.FILE) {

			if	(fileInputText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_file_inputFile);
			} else if (fileModelText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_file_modelFile);
			} else if (fileOutputText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_file_outputFile);
			} else {
				setErrorMessage(null);
			}	
		} else if (commandMode == CommandMode.DIRECTORY) {
			if	(dirInputText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_dir_inputDir);
			} else if (dirXMLModelText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_dir_modelDir);
			} else if (dirOutputText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_dir_outputDir);
			} else if (dirCSVText.getText().isEmpty()) {
				setErrorMessage(Messages.batchBuilder_error_empty_filed + Messages.batchBuilder_dir_csvFile);

			} else {
				setErrorMessage(null);
			}	
		}	
	}


	@Override
	public void setErrorMessage(String newErrorMessage) {
		Button runButton = getButton(IDialogConstants.PROCEED_ID);
		if (runButton != null) 
			runButton.setEnabled(newErrorMessage == null);
		super.setErrorMessage(newErrorMessage);
	}
	@Override
	public void setMessage(String newMessage, int type){
		if (this.message == null){
			this.message = newMessage;/// Done just once
			this.messageType = type;
		}
		if (newMessage == null && this.message != null){
			newMessage = this.message;
			type = this.messageType;
		}
		super.setMessage(newMessage, type);
	}
}
