/* ============================================================================
 * Nom du fichier	: QCflagsSection.java
 * Auteur			: dboulang
 * Date				: 28 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit.station;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: QCflagsSection de l'onglet Station
 * 
 * @author dboulang
 */
public class QCflagsSection extends NemoSection
{

    protected Table tabCorrespondance, tabFlagQC;
    private TableColumn flagColumn1, flagColumn2;
    private TableColumn tableColumn0, tableColumn1, tableColumn2, tableColumn3,
            tableColumn4, tableColumn5, tableColumn6;
    protected FlagTable flagTable;
    protected EditStationForm myForm;

    public QCflagsSection(String name, String title, String description , EditStationForm form)
    {

        super(name, title, description, form);
        

        flagTable = new FlagTable();

        flagTable = Nemo.getInstance().getConfig().getDefaultFlagTable();
        myForm = form;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
        super.dispose();
        tabCorrespondance.dispose();
        tabFlagQC.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawSection(Composite parent, boolean filled)
    {
        // Tableau des correspondances

        tabCorrespondance = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        flagColumn1 = new TableColumn(tabCorrespondance, SWT.NONE);
        flagColumn2 = new TableColumn(tabCorrespondance, SWT.NONE);
        flagTable.fillTable(tabCorrespondance);

        tabCorrespondance.setHeaderVisible(true);
        tabCorrespondance.setLinesVisible(true);
        flagColumn1.setWidth(60);
        flagColumn1.setText(Messages.table_label_local);
        flagColumn2.setWidth(210);
        flagColumn2.setText(Messages.table_label_seadatanet);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(
            tabCorrespondance);

        // Menu contextuel
        Menu menu = new Menu(tabCorrespondance.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.menu_label_add);
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.menu_label_remove);

        tabCorrespondance.setMenu(menu);

        final Table tab = tabCorrespondance;

        final TableEditor editor = new TableEditor(tabCorrespondance);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tabCorrespondance.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event)
            {
                if (event.button == 3)
                {
                    return;
                }
                Control old = editor.getEditor();
                if (old != null)
                    old.dispose();

                Point pt = new Point(event.x, event.y);

                final TableItem item = tab.getItem(pt);
                if (item == null)
                {
                    return;
                }
                int column = -1;
                for (int i = 0, n = tab.getColumnCount(); i < n; i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt))
                    {
                        column = i;
                        break;
                    }
                }

                if (column == -1)
                {
                    return;
                }

                final int col = column;
                final Text text = new Text(tab, SWT.NONE);
                final CCombo combo = new CCombo(tab, SWT.READ_ONLY);

                String[] flags = Flag.flagSDN();
                for (int i = 0; i < flags.length; i++)
                {
                    combo.add(flags[i]);
                }
                if (flags.length > 20)
                {
                    combo.setVisibleItemCount(20);
                }
                else
                {
                    combo.setVisibleItemCount(flags.length);
                }

                final Flag flag = flagTable.getFlag(item.getText());

                switch (column)
                {
                    case 0:
                        text.setForeground(item.getForeground());

                        text.setText(item.getText(column));
                        text.setForeground(item.getForeground());
                        text.selectAll();
                        text.setFocus();

                        editor.minimumWidth = text.getBounds().width;

                        editor.setEditor(text, item, column);

                        text.addModifyListener(new ModifyListener() {
                            public void modifyText(ModifyEvent event)
                            {
                                flag.setLocal(text.getText());
                                item.setText(col, text.getText());
                                Utils.invalidEtapes();
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
                            public void modifyText(ModifyEvent event)
                            {
                                flag.setSdn(combo.getText());
                                item.setText(col, combo.getText());
                                Utils.invalidEtapes();
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
            public void handleEvent(Event e)
            {
                TableItem item = new TableItem(tab, SWT.NONE);
                item.setText(0, Messages.menu_label_new);
                item.setText(1, Messages.menu_label_new);

                Flag temp = new Flag(item.getText(0), item.getText(1));
                flagTable.getFlags().add(temp);
                Utils.invalidEtapes();
            }
        });

        itemRemove.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                flagTable.getFlags().remove(
                    flagTable.getFlag(tab.getSelection()[0].getText()));
                tab.remove(tab.getSelectionIndex());
                myForm.formEdition.reflow(true);
                Utils.invalidEtapes();
            }
        });

        MenuItem itemLoad = new MenuItem(menu, SWT.CASCADE);
        itemLoad.setText(Messages.menu_label_load);
        final Menu submenu =
            new Menu(tabCorrespondance.getShell(), SWT.DROP_DOWN);
        itemLoad.setMenu(submenu);
        final ArrayList<FlagTable> flagTables =
            FlagTable.getOrderedFlagTables(Nemo.getInstance().getConfig().getFlagTables());

        for (int i = 0; i < flagTables.size(); i++)
        {
            final MenuItem childItem = new MenuItem(submenu, SWT.PUSH);
            childItem.setText(flagTables.get(i).getCode() + Global.SEP
                    + flagTables.get(i).getLibelle());
            final int j = i;
            childItem.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event e)
                {
                    flagTable = flagTables.get(j);
                    flagTable.fillTable(tab);
                    myForm.formEdition.reflow(true);
                    Utils.invalidEtapes();
                }
            });
        }
        // tabCorrespondance.setParent(parent);

        // Tableau des flags

        tabFlagQC = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        tableColumn0 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn1 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn2 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn3 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn4 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn5 = new TableColumn(tabFlagQC, SWT.NONE);
        tableColumn6 = new TableColumn(tabFlagQC, SWT.NONE);
        tabFlagQC.setHeaderVisible(true);
        tabFlagQC.setLinesVisible(true);
        // tabFlagQC.setParent(parent);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlagQC);

        tableColumn0.setWidth(0);
        tableColumn0.setResizable(false);
        tableColumn0.setText(Messages.table_label_code);

        tableColumn1.setWidth(110);
        tableColumn1.setText(Messages.table_label_meta);

        tableColumn2.setWidth(40);
        tableColumn2.setText(Messages.label_line);

        tableColumn3.setWidth(40);
        tableColumn3.setText(Messages.label_start);

        tableColumn4.setWidth(40);
        tableColumn4.setText(Messages.label_end);

        tableColumn5.setWidth(60);
        tableColumn5.setText(Messages.table_label_testIn);

        tableColumn6.setWidth(60);
        tableColumn6.setText(Messages.table_label_testOut);

        // Menu contextuel
        Menu menuFlag = new Menu(tabFlagQC.getShell(), SWT.POP_UP);
        MenuItem itemAddFlag = new MenuItem(menuFlag, SWT.CASCADE);
        itemAddFlag.setText(Messages.menu_label_addFlag);
        final Menu subFlag = new Menu(tabFlagQC.getShell(), SWT.DROP_DOWN);
        itemAddFlag.setMenu(subFlag);

        MenuItem itemRemoveFlag = new MenuItem(menuFlag, SWT.NONE);
        itemRemoveFlag.setText(Messages.menu_label_removeFlag);

        new MenuItem(menuFlag, SWT.SEPARATOR);

        MenuItem itemInjectFlag = new MenuItem(menuFlag, SWT.NONE);
        itemInjectFlag.setText(Messages.menu_label_setFlag);

        MenuItem itemTestFlag = new MenuItem(menuFlag, SWT.NONE);
        itemTestFlag.setText(Messages.label_test);

        if (Nemo.getInstance().getViewMain().getMyRulesManager().isProfile())
        {
            final FlagMenuItem childDateTime =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.DATETIME, Messages
                    .getString(Intern.DATETIME).trim());

            final FlagMenuItem childLatitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_LAT, Messages.getString(
                    Intern.S_LAT).trim());

            final FlagMenuItem childLongitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_LON, Messages.getString(
                    Intern.S_LON).trim());

            final FlagMenuItem childDepth =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_DEPTH, Messages
                    .getString(Intern.S_DEPTH).trim());
        }
        else
        {
            final FlagMenuItem childDateTime =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.DATETIME, Messages
                    .getString(Intern.DATETIME).trim());
            final FlagMenuItem childEndDateTime =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.ENDDATETIME,
                    Messages.getString(Intern.ENDDATETIME).trim());

            final FlagMenuItem childLatitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_LAT, Messages.getString(
                    Intern.S_LAT).trim());
            final FlagMenuItem childEndLatitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_ELAT, Messages
                    .getString(Intern.S_ELAT).trim());

            final FlagMenuItem childLongitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_LON, Messages.getString(
                    Intern.S_LON).trim());
            final FlagMenuItem childEndLongitude =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_ELON, Messages
                    .getString(Intern.S_ELON).trim());

            final FlagMenuItem childDepth =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_DEPTH, Messages
                    .getString(Intern.S_DEPTH).trim());
            final FlagMenuItem childEndDepth =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_EDEPTH, Messages
                    .getString(Intern.S_EDEPTH).trim());

            final FlagMenuItem childSensorDepth =
                new FlagMenuItem(subFlag, tabFlagQC, Intern.S_SENSORDEPTH, Messages
                    .getString(Intern.S_SENSORDEPTH).trim());

        }

        final Table tabFlag = tabFlagQC;

        itemRemoveFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                tabFlag.remove(tabFlag.getSelectionIndex());
                myForm.formEdition.reflow(true);
                Utils.invalidEtapes();
            }
        });

        itemInjectFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                Utils.injectFlag(Nemo.getInstance().getViewMain().getEditStationForm().getTextAreaStation(), flagTable, tabFlag,
                    2, 3, 4, 5, 6);
                Utils.invalidEtapes();
            }
        });

        itemTestFlag.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                Utils.testFlag(Nemo.getInstance().getViewMain().getEditStationForm().getTextAreaStation(), flagTable, tabFlag, 2,
                    3, 4, 5, 6);
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
     * 
     * @return the tabCorrespondance
     */
    public Table getTabCorrespondance()
    {
        return tabCorrespondance;
    }

    /**
     * Setter sur tabCorrespondance.
     * 
     * @param tabCorrespondance
     *            the tabCorrespondance to set
     */
    public void setTabCorrespondance(Table tabCorrespondance)
    {
        this.tabCorrespondance = tabCorrespondance;
    }

    /**
     * Getter sur tabFlagQC.
     * 
     * @return the tabFlagQC
     */
    public Table getTabFlagQC()
    {
        return tabFlagQC;
    }

    /**
     * Setter sur tabFlagQC.
     * 
     * @param tabFlagQC
     *            the tabFlagQC to set
     */
    public void setTabFlagQC(Table tabFlagQC)
    {
        this.tabFlagQC = tabFlagQC;
    }

    public FlagTable getFlagTable()
    {
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

    public void setFlagTable(FlagTable table)
    {
        flagTable = table;
    }

    @Override
    public void initModel(StationModel stationModel)
    {
        stationModel.setFlagTable(getFlagTable());
    }

    @Override
    public void fillFromModel(StationModel stationModel)
    {
        if (stationModel.getFlagTable() != null)
        {
            flagTable = stationModel.getFlagTable();
            flagTable.fillTable(myForm.qcFlags
                .getTabCorrespondance());
        }
    }

    public Fragment fillFlagFragment(String property)
    {
        int i;
        Fragment flag = new Fragment();

        for (i = 0; i < tabFlagQC.getItemCount(); i++)
        {
            if (tabFlagQC.getItems()[i].getText(0).equals(property))
            {
                flag.setFirstLine(Integer
                    .valueOf(tabFlagQC.getItems()[i].getText(2))
                        - Integer
                            .valueOf(Nemo.getInstance().getViewMain().getEditFileForm().getSectionFileHeader()
                                .getValue()));
                flag.setLastLine(flag.getFirstLine());
                flag.setFirstCharOffset(tabFlagQC.getItems()[i].getText(3));
                flag.setLastCharOffset(tabFlagQC.getItems()[i].getText(4));
                return flag;
            }
        }
        return null;
    }

    public Fragment getFlagFragment(String property)
    {
        int i;
        Fragment flag = new Fragment();

        for (i = 0; i < getTabFlagQC().getItemCount(); i++)
        {
            if (getTabFlagQC().getItems()[i].getText(0).equals(property))
            {
                flag.setFirstLine(Integer.valueOf(getTabFlagQC().getItems()[i]
                    .getText(2))
                        - Integer
                            .valueOf(Nemo.getInstance().getViewMain().getEditFileForm().getSectionFileHeader()
                                .getValue()));
                flag.setLastLine(flag.getFirstLine());
                flag
                    .setFirstCharOffset(getTabFlagQC().getItems()[i].getText(3));
                flag.setLastCharOffset(getTabFlagQC().getItems()[i].getText(4));
                return flag;
            }
        }
        return null;
    }

    public void setFlagFragment(Fragment flag, 
                                String propertyCode,
                                String propertyName){
        if (flag != null)
        {
            // Test si la metadonnee est déjà presente dans le tableau
            int size = getTabFlagQC().getItemCount();
            for (int i = 0; i < size; i++)
            {
                if (getTabFlagQC().getItems()[i].getText(0)
                    .equals(propertyCode))
                {
                    return;
                }
            }

            TableItem item = new TableItem(getTabFlagQC(), SWT.NONE);
            item.setText(0, propertyCode);
            item.setText(1, propertyName);
            item.setText(2, String.valueOf(flag.getFirstLine()+ Nemo.getInstance().getDomain().getMedia().getModel().getFileHeader()));
            item.setText(3, String.valueOf(flag.getFirstCharOffset()));
            item.setText(4, String.valueOf(flag.getLastCharOffset()));
        }
    }
}
