package fr.ifremer.nemo.ui.edit.data;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm extends EditForm
{
    
    // les données utilisées dans cette table myTable
    private TableData myTableData;
    private  StyledText  textAreaData;
    private  Composite compositeData;
    
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);
        myTableData = new TableData();
        myTableData.go();

        // init composants IHM
        initSections();

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
        Nemo.getInstance().getConfig().getDataListToUse(myTableData);

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

   
}
