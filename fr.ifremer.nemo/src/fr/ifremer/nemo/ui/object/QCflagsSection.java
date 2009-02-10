/* ============================================================================
 * Nom du fichier	: QCflagsSection.java
 * Auteur			: dboulang
 * Date				: 28 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.input.FlagFragment;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.EditDesc;
import fr.ifremer.nemo.ui.EditStation;
import fr.ifremer.nemo.ui.FlagMenuItem;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewEdit;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: TODO
 * @author dboulang
 *
 */
public class QCflagsSection extends NemoSection
{
    
    protected Table tabCorrespondance, tabFlagQC;
    private TableColumn flagColumn1, flagColumn2;
    protected FlagTable flagTable;

    
    public QCflagsSection(Shell shell,String name, String title, String description){

        super(shell, name, title, description);
        
        flagTable = new FlagTable();
        tabCorrespondance = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
        
        flagColumn1 = new TableColumn(tabCorrespondance, SWT.NONE);
        flagColumn2 = new TableColumn(tabCorrespondance, SWT.NONE);
        
        flagTable = Utils.configuration.getDefaultFlagTable();
        flagTable.fillTable(tabCorrespondance);
        
        
        tabFlagQC = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
        
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        tabCorrespondance.dispose();
        tabFlagQC.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent)
    {
        // Tableau des correspondances
        tabCorrespondance.setHeaderVisible(true);
        tabCorrespondance.setLinesVisible(true);
        flagColumn1.setWidth(60);
        flagColumn1.setText(Messages.getString("OptionsAction.36")); //$NON-NLS-1$
        flagColumn2.setWidth(210);
        flagColumn2.setText(Messages.getString("OptionsAction.37")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabCorrespondance);

        // Menu contextuel
        Menu menu = new Menu(tabCorrespondance.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.getString("OptionsAction.38")); //$NON-NLS-1$
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.getString("OptionsAction.39")); //$NON-NLS-1$

        tabCorrespondance.setMenu(menu);

        final Table tab = tabCorrespondance;

        final TableEditor editor = new TableEditor(tabCorrespondance);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tabCorrespondance.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                if (event.button == 3) {
                    return;
                }
                Control old = editor.getEditor();
                if (old != null)
                    old.dispose();

                Point pt = new Point(event.x, event.y);

                final TableItem item = tab.getItem(pt);
                if (item == null) {
                    return;
                }
                int column = -1;
                for (int i = 0, n = tab.getColumnCount(); i < n; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {
                        column = i;
                        break;
                    }
                }

                if (column == -1) {
                    return;
                }

                final int col = column;
                final Text text = new Text(tab, SWT.NONE);
                final CCombo combo = new CCombo(tab, SWT.READ_ONLY);

                String[] flags = Flag.flagSDN();
                for (int i = 0; i < flags.length; i++) {
                    combo.add(flags[i]);
                }
                if (flags.length > 20) {
                    combo.setVisibleItemCount(20);
                } else {
                    combo.setVisibleItemCount(flags.length);
                }

                final Flag flag = flagTable.getFlag(item.getText());

                switch (column) {
                case 0:
                    text.setForeground(item.getForeground());

                    text.setText(item.getText(column));
                    text.setForeground(item.getForeground());
                    text.selectAll();
                    text.setFocus();

                    editor.minimumWidth = text.getBounds().width;

                    editor.setEditor(text, item, column);

                    text.addModifyListener(new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            flag.setLocal(text.getText());
                            item.setText(col, text.getText());
                        }
                    });

                    break;
                case 1:
                    combo.setForeground(item.getForeground());

                    combo.setText(item.getText(column));
                    combo.setForeground(item.getForeground());
                    editor.minimumWidth = combo.getBounds().width;

                    editor.setEditor(combo, item, column);

                    combo.addModifyListener(new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            flag.setSdn(combo.getText());
                            item.setText(col, combo.getText());
                        }
                    });
                    break;
                default:
                    return;
                }
            }
        });

        Utils.forceMenu(tabCorrespondance, menu);

        itemAdd.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                TableItem item = new TableItem(tab, SWT.NONE);
                item.setText(0, Messages.getString("OptionsAction.41"));        //$NON-NLS-1$
                item.setText(1, Messages.getString("OptionsAction.41"));        //$NON-NLS-1$

                Flag temp = new Flag(item.getText(0), item.getText(1));
                flagTable.getFlags().add(temp);
            }
        });

        itemRemove.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                flagTable.getFlags().remove(
                        flagTable.getFlag(tab.getSelection()[0]
                                .getText()));
                tab.remove(tab.getSelectionIndex());
                ViewEdit.formEdition.reflow(true);
            }
        });

        MenuItem itemLoad = new MenuItem(menu, SWT.CASCADE);
        itemLoad.setText(Messages.getString("OptionsAction.43"));               //$NON-NLS-1$
        final Menu submenu = new Menu(tabCorrespondance.getShell(),
                SWT.DROP_DOWN);
        itemLoad.setMenu(submenu);
        final ArrayList<FlagTable> flagTables = FlagTable
                .getOrderedFlagTables(Utils.configuration.getFlagTables());

        for (int i = 0; i < flagTables.size(); i++) {
            final MenuItem childItem = new MenuItem(submenu, SWT.PUSH);
            childItem.setText(flagTables.get(i).getCode() + " - "               //$NON-NLS-1$
                    + flagTables.get(i).getLibelle());
            final int j = i;
            childItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e) {
                    flagTable = flagTables.get(j);
                    flagTable.fillTable(tab);
                    ViewEdit.formEdition.reflow(true);
                }
            });
        }
        tabCorrespondance.setParent(parent);

        // Tableau des flags
        tabFlagQC.setHeaderVisible(true);
        tabFlagQC.setLinesVisible(true);
        tabFlagQC.setParent(parent);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlagQC);

        TableColumn tableColumn0 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn0.setWidth(0);
        tableColumn0.setResizable(false);
        tableColumn0.setText("Code"); //$NON-NLS-1$

        TableColumn tableColumn1 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn1.setWidth(110);
        tableColumn1.setText(Messages.getString("EditStation.38")); //$NON-NLS-1$

        TableColumn tableColumn2 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn2.setWidth(40);
        tableColumn2.setText(Messages.getString("EditStation.39")); //$NON-NLS-1$   

        TableColumn tableColumn3 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn3.setWidth(40);
        tableColumn3.setText(Messages.getString("EditStation.40")); //$NON-NLS-1$   

        TableColumn tableColumn4 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn4.setWidth(40);
        tableColumn4.setText(Messages.getString("EditStation.41")); //$NON-NLS-1$   

        TableColumn tableColumn5 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn5.setWidth(60);
        tableColumn5.setText(Messages.getString("EditStation.47")); //$NON-NLS-1$   

        TableColumn tableColumn6 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn6.setWidth(60);
        tableColumn6.setText(Messages.getString("EditStation.48")); //$NON-NLS-1$   

        // Menu contextuel
        Menu menuFlag = new Menu(tabFlagQC.getShell(), SWT.POP_UP);
        MenuItem itemAddFlag = new MenuItem(menuFlag, SWT.CASCADE);
        itemAddFlag.setText(Messages.getString("EditStation.43")); //$NON-NLS-1$
        final Menu subFlag = new Menu(tabFlagQC.getShell(), SWT.DROP_DOWN);
        itemAddFlag.setMenu(subFlag);

        MenuItem itemRemoveFlag = new MenuItem(menuFlag, SWT.NONE);
        itemRemoveFlag.setText(Messages.getString("EditStation.44")); //$NON-NLS-1$
        
        new MenuItem(menuFlag,SWT.SEPARATOR);
        
        MenuItem itemInjectFlag = new MenuItem(menuFlag, SWT.NONE);
        itemInjectFlag.setText(Messages.getString("TableData.11")); //$NON-NLS-1$

        MenuItem itemTestFlag = new MenuItem(menuFlag, SWT.NONE);
        itemTestFlag.setText(Messages.getString("EditStation.42")); //$NON-NLS-1$

        if (ViewFile.radProfilVertical.getSelection()) {
            final FlagMenuItem childDateTime = new FlagMenuItem(subFlag,
                    tabFlagQC, Global.DATETIME, Messages.getString(Global.DATETIME).trim());

            final FlagMenuItem childLatitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_LAT, Messages.getString(S_LAT).trim());

            final FlagMenuItem childLongitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_LON, Messages.getString(S_LON).trim());

            final FlagMenuItem childDepth = new FlagMenuItem(subFlag,
                    tabFlagQC, S_DEPTH, Messages.getString(S_DEPTH).trim()); 
        } else {
            final FlagMenuItem childDateTime = new FlagMenuItem(subFlag,
                    tabFlagQC, Global.DATETIME, Messages.getString(Global.DATETIME).trim()); 
            final FlagMenuItem childEndDateTime = new FlagMenuItem(subFlag,
                    tabFlagQC, Global.ENDDATETIME, Messages.getString(Global.ENDDATETIME).trim()); 

            final FlagMenuItem childLatitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_LAT, Messages.getString(S_LAT).trim());
            final FlagMenuItem childEndLatitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_ELAT, Messages.getString(S_ELAT).trim()); 

            final FlagMenuItem childLongitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_LON, Messages.getString(S_LON).trim()); 
            final FlagMenuItem childEndLongitude = new FlagMenuItem(subFlag,
                    tabFlagQC, S_ELON, Messages.getString(S_ELON).trim()); 

            final FlagMenuItem childDepth = new FlagMenuItem(subFlag,
                    tabFlagQC, S_DEPTH, Messages.getString(S_DEPTH).trim()); 
            final FlagMenuItem childEndDepth = new FlagMenuItem(subFlag,
                    tabFlagQC, S_EDEPTH, Messages.getString(S_EDEPTH).trim()); 
        }

        final Table tabFlag = tabFlagQC;
        
        itemRemoveFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                tabFlag.remove(tabFlag.getSelectionIndex());

                ViewEdit.formEdition.reflow(true);
            }
        });
        
        itemInjectFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                Utils.injectFlag(ViewFile.styledTextStation, flagTable, tabFlag, 2, 3, 4, 5, 6);
            }
        });
        
        itemTestFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                Utils.testFlag(ViewFile.styledTextStation, flagTable, tabFlag, 2, 3, 4, 5, 6);
            }
        });

        tabFlagQC.setMenu(menuFlag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void razValues()
    {
        // nothing
    }


    /**
     * Getter sur tabCorrespondance.
     * @return the tabCorrespondance
     */
    public Table getTabCorrespondance()
    {
        return tabCorrespondance;
    }

    /**
     * Setter sur tabCorrespondance.
     * @param tabCorrespondance the tabCorrespondance to set
     */
    public void setTabCorrespondance(Table tabCorrespondance)
    {
        this.tabCorrespondance = tabCorrespondance;
    }

    /**
     * Getter sur tabFlagQC.
     * @return the tabFlagQC
     */
    public Table getTabFlagQC()
    {
        return tabFlagQC;
    }

    /**
     * Setter sur tabFlagQC.
     * @param tabFlagQC the tabFlagQC to set
     */
    public void setTabFlagQC(Table tabFlagQC)
    {
        this.tabFlagQC = tabFlagQC;
    }


    

    public FlagTable getFlagTable() {
        FlagTable tmpFlagTable;
        if (flagTable != null)
        {
            tmpFlagTable = flagTable;
        }
        else 
        {            
            tmpFlagTable = Utils.getBlankFlagTable();
        }
        return tmpFlagTable;
    }

    public void setFlagTable(FlagTable table) {
        flagTable = table;
    }

//    public void fillTabCorrespondanceTable() {
//        tabCorrespondance.removeAll();
//        ArrayList<Flag> flags = flagTable.getOrderedFlags();
//        for (int i = 0; i < flags.size(); i++) {
//            TableItem itemTemp = new TableItem(tabCorrespondance, SWT.NONE);
//            
//            itemTemp.setText(0, flags.get(i).getLocal());
//            itemTemp.setText(1, flags.get(i).getSdn());
//        }
//    }
    

    
    @Override
    public void initModel(StationModel stationModel){
        stationModel.setFlagTable(getFlagTable());
    }
    
    @Override
    public void fillFromModel(StationModel stationModel){
        if (stationModel.getFlagTable() != null)
        {
            flagTable = stationModel.getFlagTable();
            flagTable.fillTable(EditStation.qcFlags.getTabCorrespondance());
        }
    }
    
    public FlagFragment fillFlagFragment(String property)
    {
        int i;
        FlagFragment flag = new FlagFragment();

        for (i = 0; i < tabFlagQC.getItemCount(); i++)
        {
            if (tabFlagQC.getItems()[i].getText(0).equals(property))
            {
                flag.setLine(Integer
                    .valueOf(tabFlagQC.getItems()[i].getText(2))
                        - Integer.valueOf(EditDesc.lignesEnteteFichier
                            .getText()));
                flag.setFirstCharOffset(tabFlagQC.getItems()[i]
                    .getText(3));
                flag.setLastCharOffset(tabFlagQC.getItems()[i]
                    .getText(4));
                return flag;
            }
        }
        return null;
    }


    public void getFlagFragment(FlagFragment flag, 
                                String propertyCode,
                                String propertyName)
    {
        if (flag != null)
        {
            // Test si la metadonnee est déjà presente dans le tableau
            int size = getTabFlagQC().getItemCount();
            for (int i = 0; i < size; i++)
            {
                if (getTabFlagQC().getItems()[i].getText(0).equals(propertyCode))
                {
                    return;
                }
            }

            TableItem item = new TableItem(getTabFlagQC(), SWT.NONE);
            item.setText(0, propertyCode);
            item.setText(1, propertyName);
            item.setText(2, String.valueOf(flag.getLine()
                    + Integer.valueOf(EditDesc.lignesEnteteFichier.getText())));
            item.setText(3, String.valueOf(flag.getFirstCharOffset()));
            item.setText(4, String.valueOf(flag.getLastCharOffset()));
        }
    }
}
