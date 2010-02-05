/*
 * MappingDatabaseEditView.java
 */

package fr.ifremer.nemo.mapping;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;
import javax.swing.Icon;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;

import fr.ifremer.nemo.mapping.utils.UtilDisplay;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * The application's main frame.
 */
@SuppressWarnings("nls")
public class MappingDatabaseEditView extends FrameView {
    
    public MappingDatabaseEditView(SingleFrameApplication app) {
        super(app);

        initComponents();
        
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
	messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
	messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        }); 
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        // tracking table selection
        masterTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    firePropertyChange("recordSelected", !isRecordSelected(), isRecordSelected());
                }
            });

        // tracking changes to save
        bindingGroup.addBindingListener(new AbstractBindingListener() {
            @Override
            public void targetChanged(Binding binding, PropertyStateEvent event) {
                // save action observes saveNeeded property
                setSaveNeeded(true);
            }
        });

        // have a transaction started
        entityManager.getTransaction().begin();


        //FIXME - La bar de recherche s'initialise bizare, alors j'efface le contenu pour l'instant
        jTextField3.setText(Global.EMPTY_STRING);

        setSaveNeeded(false);
    }


    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
            firePropertyChange("saveNeeded", !saveNeeded, saveNeeded);
        }
    }

    public boolean isRecordSelected() {
        return masterTable.getSelectedRow() != -1;
    }
    

    @Action
    public void newRecord() {
        
    // check parameters
    StringBuffer errorMsg = new StringBuffer();
    String localCdiId = this.jTextField1.getText();
    String format = (String) this.jTextField2.getSelectedItem();
    String modusText = ((String) this.modusField.getSelectedItem());
    String filename = this.filenameField.getText();
    
    if (localCdiId == null || localCdiId.trim().equals("")) 
        errorMsg.append(Messages.CouplingTableAjout_localcdiid);
    if (modusText == null || modusText.trim().equals("")) 
        errorMsg.append(Messages.CouplingTableAjout_modus);
    if (format == null || format.trim().equals("")) 
        errorMsg.append(Messages.CouplingTableAjout_format);
    if (filename == null || filename.trim().equals("")) 
        errorMsg.append(Messages.CouplingTableAjout_filename);
    
    if (errorMsg.length() > 0){
        UtilDisplay.error(errorMsg, MappingDatabaseEditApp.getApplication().getMainFrame());
    }else{
        try
        {
            fr.ifremer.nemo.mapping.Mapping m = new fr.ifremer.nemo.mapping.Mapping(localCdiId, format);
            m.setFilename(filename);
            m.setModus(modusText.substring(0, 1));
            m.setDate(new Date());        
            entityManager.persist(m);
            list.add(m);
            
            this.jTextField1.setText("");
            this.jTextField2.setSelectedIndex(0);
            this.filenameField.setText("");
            this.modusField.setSelectedIndex(0);
        }
        catch (EntityExistsException e)
        {
            UtilDisplay.error(Messages.CouplingTableAjout_1, MappingDatabaseEditApp.getApplication().getMainFrame());
    
        }
            int row = list.size()-1;
            masterTable.setRowSelectionInterval(row, row);
            masterTable.scrollRectToVisible(masterTable.getCellRect(row, 0, true));
            setSaveNeeded(true);
        }
    
    }

    @Action(enabledProperty = "recordSelected")
    public void deleteRecord() {
        int[] selected = masterTable.getSelectedRows();
        List<fr.ifremer.nemo.mapping.Mapping> toRemove = new ArrayList<fr.ifremer.nemo.mapping.Mapping>(selected.length);
        
        for (int idx=0; idx<selected.length; idx++) {
            String selectedLocalCdiId = (String) masterTable.getValueAt(selected[idx], 0);
            String selectedFormat = (String) masterTable.getValueAt(selected[idx], 2);
            
            for (Mapping item : list){
                String localCdiId = item.getMappingPK().getLocalCdiId();
                String format = item.getMappingPK().getFormat();
                if (localCdiId.equals(selectedLocalCdiId) 
                        && format.equals(selectedFormat)){
                    toRemove.add(item);
                    entityManager.remove(item);
                    break;
                }
            }
        }
        list.removeAll(toRemove);
        setSaveNeeded(true);
    }

    @Action()
    @SuppressWarnings("static-access")
    public Task cancel() {
        RefreshTask task = new RefreshTask(getApplication());
        fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getInstance().hide(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getApplication().getView());

        return task;
    }

    @Action()
    @SuppressWarnings({"static-access", "fallthrough"})
    public Task exit() {
        Task task = null;
        if (isSaveNeeded()){
            int quit = UtilDisplay.confirm(Messages.CouplingTableExit_confirm, MappingDatabaseEditApp.getApplication().getMainFrame()); //$NON-NLS-1$
            switch (quit){
                case 0 :// YES -> save & quit
                    task = save(); 
                    fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getInstance().hide(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getApplication().getView());
                    break;
                case 1 : // NO -> quit without save
                    task = cancel(); 
                    break;
                default : // CANCEL -> return to MappingDatabaseEditApp
                    break;
            }
        }else{
            // no need to save -> quit
            fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getInstance().hide(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getApplication().getView());
        }
        return task;
    }

    @Action(enabledProperty = "saveNeeded")
    public Task save() {
       // fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getInstance().hide(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.getApplication().getView());

        return new SaveTask(getApplication());
    }

    private class SaveTask extends Task {
        SaveTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @Override protected Void doInBackground() {
            try {
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
            } catch (RollbackException rex) {
                rex.printStackTrace();
                entityManager.getTransaction().begin();
                List<fr.ifremer.nemo.mapping.Mapping> merged = new ArrayList<fr.ifremer.nemo.mapping.Mapping>(list.size());
                for (fr.ifremer.nemo.mapping.Mapping m : list) {
                    merged.add(entityManager.merge(m));
                }
                list.clear();
                list.addAll(merged);
            }
            return null;
        }
        @Override protected void finished() {
            setSaveNeeded(false);
        }
    }

    /**/

    public void reload() {
            try {
                entityManager.getTransaction().rollback();
                entityManager.getTransaction().begin();
                java.util.Collection data = query.getResultList();
                for (Object entity : data) {
                    entityManager.refresh(entity);
                }
                list.clear();
                list.addAll(data);
            } catch(Exception ignore) { }
     }
    
    /**
     * An example action method showing how to create asynchronous tasks
     * (running on background) and how to show their progress. Note the
     * artificial 'Thread.sleep' calls making the task long enough to see the
     * progress visualization - remove the sleeps for real application.
     */
    @Action
    public Task refresh() {
  
        
       return new RefreshTask(getApplication());
    }

    public class RefreshTask extends Task {
        RefreshTask(org.jdesktop.application.Application app) {
            super(app);
        }
        @SuppressWarnings("unchecked")
        @Override protected Void doInBackground() {
            try {
                setProgress(0, 0, 4);
                setMessage(Messages.CouplingTableRefresh_1); //"Rolling back the current changes..."
                setProgress(1, 0, 4);
                entityManager.getTransaction().rollback();
                setProgress(2, 0, 4);

                setMessage(Messages.CouplingTableRefresh_2);
                entityManager.getTransaction().begin();
                setProgress(3, 0, 4);

                setMessage(Messages.CouplingTableRefresh_3);
                java.util.Collection data = query.getResultList();
                for (Object entity : data) {
                    entityManager.refresh(entity);
                }
                setProgress(4, 0, 4);

                Thread.sleep(0L); // remove for real app
                list.clear();
                list.addAll(data);
            } catch(InterruptedException ignore) { }
            return null;
        }
        @Override protected void finished() {
            setMessage("Done.");
            setSaveNeeded(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        mainPanel = new javax.swing.JPanel();
        masterScrollPane = new javax.swing.JScrollPane();
        masterTable = new javax.swing.JTable();
        modusLabel = new javax.swing.JLabel();
        filenameLabel = new javax.swing.JLabel();
        modusField = new javax.swing.JComboBox();
        modusField.addItem("");
        modusField.addItem(Mapping.MODUS_MONOSTATION + Messages.CouplingTableLabel_modus1 );
        modusField.addItem(Mapping.MODUS_MULTISTATIONS + Messages.CouplingTableLabel_modus3);
        filenameField = new javax.swing.JTextField();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JComboBox();
        jTextField2.addItem("");
        jTextField2.addItem(Conversion.MEDATLAS.toString());
        jTextField2.addItem(Conversion.ODV.toString());
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.class).getContext().getResourceMap(MappingDatabaseEditView.class);
        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory(resourceMap.getString("entityManager.persistenceUnit")).createEntityManager(); // NOI18N
        query = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery(resourceMap.getString("query.query")); // NOI18N
        list = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        dateColumnFormat1 = new fr.ifremer.nemo.mapping.utils.DateColumnFormat();
        rowSorterToStringConverter1 = new fr.ifremer.nemo.mapping.utils.RowSorterToStringConverter();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(651, 468));
        mainPanel.setRequestFocusEnabled(false);

        masterScrollPane.setName("masterScrollPane"); // NOI18N

        masterTable.setDragEnabled(true);
        masterTable.setAutoCreateRowSorter(true);
        masterTable.setName("masterTable"); // NOI18N

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, masterTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mappingPK.localCdiId}"));
        columnBinding.setColumnName(Messages.CouplingTableTable_localcdiid);
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${modus}"));
        columnBinding.setColumnName(Messages.CouplingTableTable_modus);
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mappingPK.format}"));
        columnBinding.setColumnName(Messages.CouplingTableTable_format);
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${filename}"));
        columnBinding.setColumnName(Messages.CouplingTableTable_filename);
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${date}"));
        columnBinding.setColumnName(Messages.CouplingTableTable_date);
        columnBinding.setColumnClass(java.util.Date.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        masterScrollPane.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(1).setHeaderValue(Messages.CouplingTableTable_modus); // NOI18N
        masterTable.getColumnModel().getColumn(2).setHeaderValue(Messages.CouplingTableTable_format); // NOI18N
        masterTable.getColumnModel().getColumn(3).setHeaderValue(Messages.CouplingTableTable_filename); // NOI18N
        masterTable.getColumnModel().getColumn(4).setHeaderValue(Messages.CouplingTableTable_date); // NOI18N
        masterTable.getColumnModel().getColumn(4).setCellEditor(dateColumnFormat1.getEditor());
        masterTable.getColumnModel().getColumn(4).setCellRenderer(dateColumnFormat1.getRenderer());

        modusLabel.setText(Messages.CouplingTableLabel_modus); // NOI18N
        modusLabel.setName("modusLabel"); // NOI18N

        filenameLabel.setText(Messages.CouplingTableLabel_filename); // NOI18N
        filenameLabel.setName("filenameLabel"); // NOI18N

        modusField.setName("modusField"); // NOI18N

        filenameField.setName("filenameField"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(fr.ifremer.nemo.mapping.MappingDatabaseEditApp.class).getContext().getActionMap(MappingDatabaseEditView.class, this);
        newButton.setAction(actionMap.get("newRecord")); // NOI18N
        newButton.setText(Messages.CouplingTableAction_newrecord);// NOI18N
        newButton.setName("newButton"); // NOI18N

        deleteButton.setAction(actionMap.get("deleteRecord")); // NOI18N
        deleteButton.setText(Messages.CouplingTableAction_delete);// NOI18N        
        deleteButton.setName("deleteButton"); // NOI18N

        jTextField1.setName("LocalCdiIdField"); // NOI18N

        jTextField2.setName("FormatField"); // NOI18N

        jLabel1.setText(Messages.CouplingTableLabel_LocalCdiId); // NOI18N
        jLabel1.setName("LocalCdiIdLabel"); // NOI18N

        jLabel2.setText(Messages.CouplingTableLabel_format); // NOI18N
        jLabel2.setName("formatLabel"); // NOI18N

        jTextField3.setName("jTextField3"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${rowSorter}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(rowSorterToStringConverter1);
        bindingGroup.addBinding(binding);

        jLabel3.setText(Messages.CouplingTableLabel_filter); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(masterScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(newButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(deleteButton))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(8, 8, 8)
                                .add(jLabel1))
                            .add(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(modusLabel))
                            .add(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(filenameLabel)
                                    .add(jLabel2))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, filenameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                            .add(modusField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel3)
                        .add(40, 40, 40)
                        .add(jTextField3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)))
                .addContainerGap())
        );

        mainPanelLayout.linkSize(new java.awt.Component[] {deleteButton, newButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .add(8, 8, 8)
                .add(masterScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modusLabel)
                    .add(modusField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(filenameLabel)
                    .add(filenameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(deleteButton)
                    .add(newButton))
                .add(46, 46, 46))
        );

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jButton1.setAction(actionMap.get("cancel")); // NOI18N
        jButton1.setText(Messages.CouplingTableAction_cancel); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        saveButton.setAction(actionMap.get("save")); // NOI18N
        saveButton.setText(Messages.CouplingTableAction_save); // NOI18N        
        saveButton.setName("saveButton"); // NOI18N

        exitButton.setAction(actionMap.get("exit")); // NOI18N
        exitButton.setText(Messages.CouplingTableAction_exit); // NOI18N        
        exitButton.setName("exitButton"); // NOI18N

        refreshButton.setAction(actionMap.get("refresh")); // NOI18N
        refreshButton.setText(Messages.CouplingTableAction_refresh); // NOI18N             
        refreshButton.setName("refreshButton"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 607, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap(550, Short.MAX_VALUE)
                .add(refreshButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(saveButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(exitButton)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(exitButton)
                    .add(jButton1)
                    .add(saveButton)
                    .add(refreshButton))
                .add(9, 9, 9)
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        rowSorterToStringConverter1.setTable(masterTable);

        setComponent(mainPanel);
        setStatusBar(statusPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.ifremer.nemo.mapping.utils.DateColumnFormat dateColumnFormat1;
    private javax.swing.JButton deleteButton;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JTextField filenameField;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox jTextField2;
    private javax.swing.JTextField jTextField3;
    private java.util.List<fr.ifremer.nemo.mapping.Mapping> list;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane masterScrollPane;
    private javax.swing.JTable masterTable;
    private javax.swing.JComboBox modusField;
    private javax.swing.JLabel modusLabel;
    private javax.swing.JButton newButton;
    private javax.swing.JProgressBar progressBar;
    private javax.persistence.Query query;
    private javax.swing.JButton refreshButton;
    private fr.ifremer.nemo.mapping.utils.RowSorterToStringConverter rowSorterToStringConverter1;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private boolean saveNeeded;
}
