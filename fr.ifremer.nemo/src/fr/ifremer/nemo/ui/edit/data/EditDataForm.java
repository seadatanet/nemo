package fr.ifremer.nemo.ui.edit.data;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.util.Global.DataListEnum;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.data.listeners.DataListRadioSelectionListener;
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
    
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);
        myTableData = new TableData();
        myTableData.go();

        // init composants IHM
        initSections();

        createGroupDataList();

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
                    Etape.validationData();
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
        DataListEnum dataList = guessDataList(parameterModels);
        Nemo.getInstance().getViewMain().getMyRulesManager().updateFromModel(dataList)    ;    
      
    }
    
    private DataListEnum guessDataList(ParameterModel[]  parameterModelList)
    {
        DataListEnum res;
        ParameterModel parameterModel = parameterModelList[0];
        res = Nemo.getInstance().getDomain().getData().getModel().getDataList();

        if (res == DataListEnum.UNKNOWN){
            // on devine le type de liste utilisée d'après les valeurs disponibles dans le parametre
            if (parameterModel.getParameter().getUnitCode()!=Global.EMPTY_STRING  )
            {
                res = DataListEnum.P011_VIA_P021;
            }else if(parameterModel.getParameter().isP011())
            {
                res = DataListEnum.P011_VIA_P091;
            }else{
                res = DataListEnum.P091;
            }
        }
        return res;

    }
    
    public void enableP011viaP021(boolean enabled)
    {
        button_P011viaP021.setEnabled(enabled);
    }
    public void selectDataList( DataListEnum theDataList)
    {
        button_P091.setSelection(false);
        button_P011viaP091.setSelection(false);
        button_P011viaP021.setSelection(false);

        if (theDataList == DataListEnum.P091)
        { 
            button_P091.setSelection(true);
        }
        else if(theDataList == DataListEnum.P011_VIA_P091)
        {
            button_P011viaP091.setSelection(true);
        }
        else if(theDataList == DataListEnum.P011_VIA_P021)
        {
            button_P011viaP021.setSelection(true);
        }


    }

    private DataListEnum getSelectedDataList()
    {
        DataListEnum res=DataListEnum.UNKNOWN;
        if (button_P091.getSelection())
        { 
            res =  DataListEnum.P091;
        }
        else if(button_P011viaP091.getSelection())
        {
            res =  DataListEnum.P011_VIA_P091;
        }
        else if(button_P011viaP021.getSelection())
        {
            res =  DataListEnum.P011_VIA_P021;
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
        dataModel.setDataList(Nemo.getInstance().getViewMain().getMyRulesManager().getMyDataList());
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

   
}
