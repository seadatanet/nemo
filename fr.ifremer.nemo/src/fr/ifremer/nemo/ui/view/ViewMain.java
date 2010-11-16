/* ============================================================================
 * Nom du fichier	: ViewMain.java
 * Auteur			: dboulang
 * Date				: 3 juin 2009
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

/**
 * Description: vue principale
 * 
 * @author dboulang
 */
public class ViewMain extends ViewPart
{
    public static final String ID = "fr.ifremer.nemo.ui.view.ViewMain"; //$NON-NLS-1$

    // Formulaires d'édition
    private  EditFileForm editFileForm;
    private  EditCruiseForm editCruiseForm;
    private  EditStationForm editStationForm;
    private  EditDataForm editDataForm;
    private  EditConversionForm editConversionForm;
    
    // tabs
    public  TabItem tabFile, tabCruise, tabStation, tabData, tabConversion;
    
    // IHM config
    private Color backgroundEdit;

   
    private  int etape;
    private  int  affiche;

   
    public  boolean writeResume;
    protected  Composite  textFileCruise;
    private  TabFolder tabFolder;
    public  Composite compositeData;

    public  Button btnTestWS = null;

    public  StyledText textAreaCruise,  textAreaStation,
    textAreaData;
    public  Table tableData;

    
    
    public ViewMain()
    {
        System.out.println("constructeur ViewMain");
        editCruiseForm = new EditCruiseForm(); 
        editDataForm = new EditDataForm();
        editFileForm = new EditFileForm();
        editStationForm = new EditStationForm();
        editConversionForm = new EditConversionForm();
        Nemo.getInstance().setViewMain(this);
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent)
    {

        // etape de depart
        setEtape(0);

        // init des flags
        FiltrageTableType.listeOuverte = 0;
        setAffiche(0);
        editFileForm.setFileLoaded(false);

        backgroundEdit = new Color(parent.getDisplay(), 255, 255, 255);

        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        tabFolder = new TabFolder(top, SWT.NONE);
        tabFolder
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e)
            {
                int tmp = tabFolder.getSelectionIndex();

                if (editFileForm.isCollection() && tmp > 0)
                    tmp++;

                Utils.validationEtape(tmp);
            }
        });

        Composite contentFile = new Composite(tabFolder, SWT.NONE);
        createContentFile(contentFile);

        Composite contentCruise = new Composite(tabFolder, SWT.NONE);
        createContentCruise(contentCruise);

        Composite contentStation = new Composite(tabFolder, SWT.NONE);
        createContentStation(contentStation);

        Composite contentData = new Composite(tabFolder, SWT.NONE);
        createContentData(contentData);

        Composite contentConversion = new Composite(tabFolder, SWT.NONE);
        createContentConversion(contentConversion);

        tabFile = new TabItem(tabFolder, SWT.NONE);
        tabFile.setText(Messages.onglet_file);
        tabFile.setControl(contentFile);

        tabCruise = new TabItem(tabFolder, SWT.NONE);
        tabCruise.setText(Messages.onglet_cruise);
        tabCruise.setControl(contentCruise);

        tabStation = new TabItem(tabFolder, SWT.NONE);
        tabStation.setText(Messages.onglet_station);
        tabStation.setControl(contentStation);

        tabData = new TabItem(tabFolder, SWT.NONE);
        tabData.setText(Messages.onglet_data);
        tabData.setControl(contentData);

        tabConversion = new TabItem(tabFolder, SWT.NONE);
        tabConversion.setText(Messages.onglet_convert);
        tabConversion.setControl(contentConversion);

    }
    
    
    private SashForm getNewParentForm(Composite parent)
    {
        parent.setLayout(new FillLayout());
        SashForm parentform = new SashForm(parent, SWT.HORIZONTAL);
        parentform.setLayout(new FillLayout());

        return parentform;
    }
    private Composite getNewRightComposite(SashForm parentForm)
    {
        //RIGHT PANEL
        Composite right = new Composite(parentForm, SWT.BORDER);
        right.setBackground(backgroundEdit);
        right.setLayout(new FillLayout());
        
        return right;
    }
    
    
    protected void createContentFile(Composite parent)
    {
        SashForm parentForm = getNewParentForm(parent);
        
        //LEFT PANEL
        SashForm left = new SashForm(parentForm, SWT.VERTICAL);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout(SWT.VERTICAL));
        
        editFileForm.createLeftForm(left);
//        left.setWeights(new int[] {45, 55});

        //RIGHT PANEL
        Composite right = getNewRightComposite(parentForm);
        editFileForm.createEditionForm(right);
    }

    protected void createContentConversion(Composite parent)
    {
        SashForm parentForm = getNewParentForm(parent);

//        Composite left = new Composite(parentForm, SWT.NONE);
//         left.setBackground(parent.getBackground());
//        left.setLayout(new FillLayout());

        Composite right = new Composite(parentForm, SWT.BORDER);
        right.setBackground(backgroundEdit);
        right.setLayout(new FillLayout());

        editConversionForm = new EditConversionForm();
        editConversionForm.createEditionForm(right);

//        parentForm.setWeights(new int[] {60, 40});
    }
    
    protected void createContentCruise(Composite parent)
    {
        SashForm parentForm = getNewParentForm(parent);
        
        //LEFT
        Composite left = new Composite(parentForm, SWT.NONE);
        left.setLayout(new FillLayout());

        textFileCruise = new Composite(left, SWT.NONE);
        textFileCruise.setLayout(new FillLayout());
        textAreaCruise =
            new StyledText(textFileCruise, SWT.MULTI | SWT.READ_ONLY
                | SWT.H_SCROLL | SWT.V_SCROLL);
        textAreaCruise.setFont(Utils.fontCourier);

        // RIGHT
        Composite right = getNewRightComposite(parentForm);
        editCruiseForm.createEditionForm(right);

    }

    protected void createContentStation(Composite parent)
    {
        SashForm parentForm = getNewParentForm(parent);

        //LEFT
        Composite left = new Composite(parentForm, SWT.NONE);
        left.setLayout(new FillLayout());

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaStation =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                | SWT.V_SCROLL);
        textAreaStation.setFont(Utils.fontCourier);

        //RIGHT
        Composite right = getNewRightComposite(parentForm);
        editStationForm.createEditionForm(right);

    }

    protected void createContentData(Composite parent)
    {
        SashForm parentForm = getNewParentForm(parent);
        
        //LEFT
        SashForm left = new SashForm(parentForm, SWT.VERTICAL);
        left.setLayout(new FillLayout(SWT.VERTICAL));

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaData =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                | SWT.V_SCROLL);
        textAreaData.setFont(Utils.fontCourier);

        compositeData = new Composite(left, SWT.NONE);
        compositeData.setLayout(new FillLayout());
        TableData.go();

        
        //RIGHT
        Composite right = getNewRightComposite(parentForm);
        editDataForm.createEditionForm(right);
        
        parentForm.setWeights(new int[] {75, 25});
    }

    public void init()
    {

        // reinitialisation de la conversion
        Nemo.getInstance().resetModel();

        editFileForm.setFileLoaded(false);
        Preformat.setPreformat(false);

        // reinitialisation de l'affichage
        // --- les formulaires
        Utils.razGlobal();
        editFileForm.setFileLoaded(false);

        // --- renitialisation du tableau de paramètres (onglet Data)
        if (tableData != null)
        {
            TableData.go();
        }

        // --- le titre de l'application
        ApplicationWorkbenchWindowAdvisor.configurer_titre
        .setTitle(Intern.application_title);

        // --- l'onglet d'erreur
        Utils.messageErreur(Global.EMPTY_STRING);


        
        // init onglet File
        // --- le contenu du fichier
      editFileForm.init();

      editFileForm.setCruiseInADirectory( false);
    }

    public void enableCruise(boolean enabled)
    {
        if (enabled)
        {
            if (tabCruise.isDisposed())
            {
                Composite contentCruise = new Composite(tabFolder, SWT.NONE);
                createContentCruise(contentCruise);

                tabCruise = new TabItem(tabFolder, SWT.NONE, 1);
                tabCruise.setText(Messages.onglet_cruise);
                tabCruise.setControl(contentCruise);
            }
        }
        else
        {
            tabCruise.dispose();
        }
    }

   

   

   

   

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub
    }

    public EditFileForm getEditFileForm()
    {
        return editFileForm;
    }
    public EditConversionForm getEditConversionForm()
    {
        return editConversionForm;
    }
    public EditStationForm getEditStationForm()
    {
        return editStationForm;
    }
    public EditDataForm getEditDataForm()
    {
        return editDataForm;
    }
    public EditCruiseForm getEditCruiseForm()
    {
        return editCruiseForm;
    }

    public int getEtape()
    {
        return etape;
    }

    public void setEtape(int etape)
    {
        this.etape = etape;
    }

    public Color getBackgroundEdit()
    {
        return backgroundEdit;
    }

    public int getAffiche()
    {
        return affiche;
    }

    public void setAffiche(int affiche)
    {
        this.affiche = affiche;
    }

    public void selectTabByIndex(int tabIndex)
    {
        tabFolder.setSelection(tabIndex);
    }
    
   
}