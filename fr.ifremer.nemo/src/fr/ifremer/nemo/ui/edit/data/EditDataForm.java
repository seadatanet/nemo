package fr.ifremer.nemo.ui.edit.data;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.data.listeners.DataListRadioSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.MesureTypeBelowSeaBedSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.MesureTypeBelowSeaSurfaceSelectionListener;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm extends EditForm
{
    
    // les données utilisées dans cette table myTable
    private TableData myTableData;
    private  StyledText  textAreaData;
    private  Composite compositeData;
    
    
    
    // choix du format des parametres
    private Button button_P011viaP091;
    private Button button_P091;
    private Button button_P011viaP021;
    // choix du type de mesure
    private Button mesType_belowSeaSurface ;
    private Button mesType_belowSeaBed ;
    
    private GroupVerticalParameters myGroupeVerticalParameters;
   
    
    public EditDataForm()
    {
        super();
        myValidator = new DataFormValidator();
    }
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);
        myGroupeVerticalParameters = new GroupVerticalParameters();
        
        myTableData = new TableData();
        myTableData.go();

        // init composants IHM
        initSections();

        // groupe de selection du dataList (pour selectionner la liste à utiliser: P091, P011viaP091 ou P011viaP021)
        createGroupDataList();
        
        // groupe de selection du type de mesure (sous l'eau ou dans le sol)
        createGroupMesureType();
       
        // groupe de selection des paramètres verticaux
        createGroupVerticalParametersChoices();
        
        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_data_title);

        // bouton validate
        createButtonValidate();
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    validate();
                }
            });

        // reset
        createButtonRaz();

        // bouton collapse all / expand all
        createButtonExpand();

        drawSections();
    }

    @Override
    protected void initSections()
    {
        
    }
   
    @Override
    public void afficheEdition()
    {
        super.afficheEdition();

        ViewLog.aideData();

        // affichage du texte dans la zone de gauche
        textAreaData.setText(Utils.texteBrut);

        setLoaded(true);
        Nemo.getInstance().getViewMain().setAffiche(3);

        // remplissage de l'IHM par le modele charge
        // nom du model
        String modelName = Nemo.getInstance().getNomModele();
        
        if (myTableData.getTypes() != null && !myTableData.getTypes().isEmpty()){
            myTableData.razAndRecreateTable();
        }
        else if (!Global.EMPTY_STRING.equals(modelName)){
            fillFromModel();
        }
        else{
            myTableData.fillTypes();
        }
    }

    @Override
    public void drawingBack()
    {
        super.drawingBack();

        ViewLog.aideData();
        Nemo.getInstance().getViewMain().setAffiche(3);
    }

    @Override
    public void razText()
    {
        myTableData.fillTypes();
    }

    @Override
    public void actualiseEdition()
    {
        // pas d'actualisation nécessaire pour cet onglet
    }

    public void invalidEtape()
    {
        super.invalidEtape(3);
        Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data);
    }
    
    /**
     * fill with current model values 
     */
    @Override
    public void fillFromModel()
    {
       
        myTableData.reinitTypes();
        myGroupeVerticalParameters.removeAllToAllCombos();
        ParameterModel[] parameterModels =
            Nemo.getInstance().getDomain().getData().getModel().getParameterModels();

        // remplissage des parametres mesures
        for (ParameterModel paraCourant : parameterModels)
        {
            TypeData typeData = new TypeData(paraCourant);
            myTableData.addType(typeData);
        }

        // refresh de la table de l'onglet Data
        myTableData.razAndRecreateTable();
        // update dataList to use   
        DataListTypeEnum dataList = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        Nemo.getInstance().getViewMain().getMyRulesManager().updateFromModel(dataList)    ;    
        
        // update mesureType to use   
        E_MesureType mesureType = Nemo.getInstance().getDomain().getData().getModel().getMesureType();
        Nemo.getInstance().getViewMain().getMyRulesManager().updateFromModel(mesureType)    ;    
      
    }
    
    
    
    public void enableP011viaP021(boolean enabled)
    {
        button_P011viaP021.setEnabled(enabled);
    }
    public void selectDataList( DataListTypeEnum theDataList)
    {
        button_P091.setSelection(false);
        button_P011viaP091.setSelection(false);
        button_P011viaP021.setSelection(false);

        if (theDataList == DataListTypeEnum.P091)
        { 
            button_P091.setSelection(true);
        }
        else if(theDataList == DataListTypeEnum.P011_VIA_P091)
        {
            button_P011viaP091.setSelection(true);
        }
        else if(theDataList == DataListTypeEnum.P011_VIA_P021)
        {
            button_P011viaP021.setSelection(true);
        }


    }
    public void selectMesureType(E_MesureType mesureType)
    {
        mesType_belowSeaSurface .setSelection(false);
        mesType_belowSeaBed .setSelection(false);

        if (mesureType == E_MesureType.BSBED)
        { 
            mesType_belowSeaBed.setSelection(true);
        }
        else if(mesureType == E_MesureType.BSSURF)
        {
            mesType_belowSeaSurface.setSelection(true);
        }
    }
    private DataListTypeEnum getSelectedDataList()
    {
        DataListTypeEnum res=DataListTypeEnum.UNKNOWN;
        if (button_P091.getSelection())
        { 
            res =  DataListTypeEnum.P091;
        }
        else if(button_P011viaP091.getSelection())
        {
            res =  DataListTypeEnum.P011_VIA_P091;
        }
        else if(button_P011viaP021.getSelection())
        {
            res =  DataListTypeEnum.P011_VIA_P021;
        }
        return res;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void initFromNewModel()
    {
        // recuperation du model correspondant
        fr.ifremer.nemo.input.DataModel dataModel = new fr.ifremer.nemo.input.DataModel();
        
        // mise e jour des types avant validation
        myTableData.majTypeData();

        // reset systematique des parametres stockes dans le dataModel
        Nemo.getInstance().getDomain().getData().getModel().releaseParameterModels();

        for (TypeData typeCourant : myTableData.getTypes())
        {
            ParameterModel parameterModel = new ParameterModel(typeCourant);
            dataModel.addParameterModel(parameterModel);
        }
        dataModel.setDataListType(Nemo.getInstance().getViewMain().getMyRulesManager().getMyDataListType());
        Nemo.getInstance().getDomain().getData().updateData(dataModel);
        Nemo.getInstance().getViewMain().getEditDataForm().setValidated(true);

    }
    
    public void createLeft(Composite left)
    {
        
        left.setLayout(new FillLayout(SWT.VERTICAL));

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        setTextAreaData(
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                | SWT.V_SCROLL));
        getTextAreaData().setFont(Utils.fontCourier);
        setCompositeData( new Composite(left, SWT.NONE));
        compositeData.setLayout(new FillLayout());
    }
    public void reinitTableData()
    {
       myTableData.reinitTable();
    }
    
    /**
     * This method initializes data list group
     */
    private void createGroupDataList()
    {
        Group group = new Group(formEdition.getBody(), SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_parametersList);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        
        DataListRadioSelectionListener listener = new DataListRadioSelectionListener(this);
        button_P091 = new Button(group, SWT.RADIO);
        button_P091.setText(Messages.settings_parameterListP091);
        button_P091.addSelectionListener(listener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P091);
        
        button_P011viaP091 = new Button(group, SWT.RADIO);
        button_P011viaP091.setText(Messages.settings_parameterListP011viaP091);
        button_P011viaP091.addSelectionListener(listener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP091);
        
        button_P011viaP021 = new Button(group, SWT.RADIO);
        button_P011viaP021.setText(Messages.settings_parameterListP011viaP021);
        button_P011viaP021.addSelectionListener(listener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP021);
    }
    /**
     * Group pour choix du type de mesure
     */
    private void createGroupMesureType()
    {
        Group group = new Group(formEdition.getBody(), SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.edit_data_mesureType_label);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        
        MesureTypeBelowSeaSurfaceSelectionListener surfListener = new MesureTypeBelowSeaSurfaceSelectionListener();
        mesType_belowSeaSurface = new Button(group, SWT.RADIO);
        mesType_belowSeaSurface.setText(Messages.edit_data_belowSeaSurface);
        mesType_belowSeaSurface.addSelectionListener(surfListener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(mesType_belowSeaSurface);
        
        MesureTypeBelowSeaBedSelectionListener bedListener = new MesureTypeBelowSeaBedSelectionListener();
        mesType_belowSeaBed = new Button(group, SWT.RADIO);
        mesType_belowSeaBed.setText(Messages.edit_data_belowSeaBed);
        mesType_belowSeaBed.addSelectionListener(bedListener);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(mesType_belowSeaBed);
        
    }
    
    private void createGroupVerticalParametersChoices()
    {
      
       myGroupeVerticalParameters.createGroup(formEdition);
    }
    
    
    public TableData getMyTableData()
    {
        return myTableData;
    }

    public StyledText getTextAreaData()
    {
        return textAreaData;
    }

    public void setTextAreaData(StyledText textAreaData)
    {
        this.textAreaData = textAreaData;
    }

    public Composite getCompositeData()
    {
        return compositeData;
    }

    public void setCompositeData(Composite compositeData)
    {
        this.compositeData = compositeData;
    }

    public Button getButton_P011viaP021()
    {
        return button_P011viaP021;
    }

    public Button getButton_P011viaP091()
    {
        return button_P011viaP091;
    }

    public Button getButton_P091()
    {
        return button_P091;
    }
    public GroupVerticalParameters getMyGroupeVerticalParameters()
    {
        return myGroupeVerticalParameters;
    }

   
}
