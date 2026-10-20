package fr.ifremer.nemo.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.mapping.EditMappingAction;
import fr.ifremer.nemo.mapping.ExportMappingAction;
import fr.ifremer.nemo.mapping.ImportMappingAction;
import fr.ifremer.nemo.ui.actions.BatchBuilderAction;
import fr.ifremer.nemo.ui.actions.ExitAction;
import fr.ifremer.nemo.ui.actions.UserManualAction;
import fr.ifremer.nemo.ui.actions.OpenAction;
import fr.ifremer.nemo.ui.actions.OptionsAction;
import fr.ifremer.nemo.ui.actions.SaveAction;
import fr.ifremer.nemo.ui.actions.UpdateAction;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	public static IWorkbenchAction newAction, updateAction, exitAction, exitActionQuit, aboutAction, openAction,
			openXMLAction, saveXMLAction, optionsAction, batchBuilderAction, userManualAction, saveAction, saveAsAction,
			mappingDatabaseImportAction, mappingDatabaseExportAction, mappingDatabaseEditAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitActionQuit = ActionFactory.QUIT.create(window);
		register(exitActionQuit);

		// MODEL
		openAction = new OpenAction(window);
		register(openAction);

		saveAction = new SaveAction(window);
		register(saveAction);

		exitAction = new ExitAction(window);
		register(exitAction);

		// MAPPING
		mappingDatabaseImportAction = new ImportMappingAction(window);
		mappingDatabaseImportAction.setEnabled(Nemo.getInstance().getConfig().isWriteMapping());
		register(mappingDatabaseImportAction);

		mappingDatabaseExportAction = new ExportMappingAction(window);
		mappingDatabaseExportAction.setEnabled(Nemo.getInstance().getConfig().isWriteMapping());
		register(mappingDatabaseExportAction);

		mappingDatabaseEditAction = new EditMappingAction(window);
		mappingDatabaseEditAction.setEnabled(Nemo.getInstance().getConfig().isWriteMapping());
		register(mappingDatabaseEditAction);

		// OPTIONS
		optionsAction = new OptionsAction(window);
		register(optionsAction);

		// Vocabulary Update
		updateAction = new UpdateAction(window);
		register(updateAction);

		// Batch Helper
		batchBuilderAction = new BatchBuilderAction(window);
		register(batchBuilderAction);

		// ?
		userManualAction = new UserManualAction(window);
		register(userManualAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.menu_model, Global.ET_CO + Messages.menu_model);

		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager mappingMenu = new MenuManager(Messages.menu_couplingTable,
				Global.ET_CO + Messages.menu_couplingTable);
		mappingMenu.add(mappingDatabaseImportAction);
		mappingMenu.add(mappingDatabaseExportAction);
		fileMenu.add(new Separator());
		mappingMenu.add(mappingDatabaseEditAction);

		MenuManager optionsMenu = new MenuManager(Messages.menu_options, Global.ET_CO + Messages.menu_options);
		optionsMenu.add(optionsAction);
		optionsMenu.add(updateAction);
		optionsMenu.add(batchBuilderAction);

		MenuManager helpMenu = new MenuManager(Messages.menu_help, Global.ET_CO + Messages.menu_help);
		helpMenu.add(userManualAction);
		helpMenu.add(aboutAction);
		
		

		menuBar.add(fileMenu);
		menuBar.add(mappingMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);

		saveAction.setEnabled(true);

	}

}
