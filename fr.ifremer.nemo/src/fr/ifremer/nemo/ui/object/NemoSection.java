/* ============================================================================
 * Nom du fichier	: UIsection.java
 * Auteur			: dboulang
 * Date				: 28 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.ViewEdit;
import fr.ifremer.nemo.util.Formatter;

/**
 * Description: classe abstraite définissant une Section NEMO
 * @author dboulang
 *
 */
public abstract class NemoSection implements UIobject
{
    // constantes des noms possibles
    public static final String F_FILEHEADER     = "enteteFichier";          //$NON-NLS-1$
    public static final String F_STATIONHEADER  = "enteteStation";          //$NON-NLS-1$
    public static final String F_ENDSTATION     = "finStation";             //$NON-NLS-1$
    public static final String F_ENDDATA        = "indicFinDonnees";        //$NON-NLS-1$
//    public static final String C_REFERENCE      = "";                       //$NON-NLS-1$
//    public static final String C_LOCATION       = "";                       //$NON-NLS-1$
//    public static final String C_DATASOURCE     = "";                       //$NON-NLS-1$
//    public static final String C_ARCHIVING      = "";                       //$NON-NLS-1$
//    public static final String C_DATATYPE       = "";                       //$NON-NLS-1$
//    public static final String C_COMMENTS       = "";                       //$NON-NLS-1$
    public static final String S_DCHIST         = "dcHistory";              //$NON-NLS-1$
    public static final String S_COMMENT        = "comment";                //$NON-NLS-1$
    public static final String S_SURFACE        = "surfaceSamples";         //$NON-NLS-1$
    public static final String S_OFFSETTU       = "offsetTU";               //$NON-NLS-1$
    public static final String S_TIME           = "time";                   //$NON-NLS-1$
    public static final String S_DATE           = "date";                   //$NON-NLS-1$
    public static final String S_LAT            = "latitude";               //$NON-NLS-1$
    public static final String S_LON            = "longitude";              //$NON-NLS-1$
    public static final String S_DEPTH          = "bottomDepth";            //$NON-NLS-1$
    public static final String S_ELAT           = "endLatitude";            //$NON-NLS-1$
    public static final String S_ELON           = "endLongitude";           //$NON-NLS-1$
    public static final String S_EDEPTH         = "endDepth";               //$NON-NLS-1$
    public static final String S_SENSORDEPTH    = "sensorDepth";            //$NON-NLS-1$
    public static final String S_MAGNETIC       = "magneticDeclination";    //$NON-NLS-1$
    public static final String S_SAMPLINGRATE   = "samplingRate";           //$NON-NLS-1$
    public static final String S_EDATE          = "endDate";                //$NON-NLS-1$
    public static final String S_ETIME          = "endTime";                //$NON-NLS-1$

    public static final String QC               = "QC";                     //$NON-NLS-1$

    public static final String S_STATIONNUMBER  = "reference";              //$NON-NLS-1$
    public static final String S_DATATYPE       = "dataType";               //$NON-NLS-1$
    public static final String S_QCFLAG         = "qcFlag";                 //$NON-NLS-1$
    
    protected Section section;
    protected String name;      // nom correspondant au tag xml (PropertyModel)
    protected String title;
    private String description;
    
    public NemoSection(Shell shell, String name, String title, String description){
        section  = new Section(shell, SWT.NONE);
        this.name = name;
        this.title = Formatter.getSingleton().padString(title,40);
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose(){
        section.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void draw(Composite parent){
        section = ViewEdit.toolkitEdition.createSection(parent,
                  Section.DESCRIPTION |  ExpandableComposite.TITLE_BAR
                  |  ExpandableComposite.TWISTIE |  ExpandableComposite.COMPACT);
//        GridDataFactory.fillDefaults().grab(false, true).minSize(510, 20).applyTo(section);
        Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);             //$NON-NLS-1$
        section.setFont(font);
        
        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                ViewEdit.formEdition.reflow(true);
            }
        });
        section.setText(title);
        section.setDescription(description);
        
        
        Composite sectionClient = ViewEdit.toolkitEdition.createComposite(section);
        sectionClient.setLayout(new GridLayout());

        drawSection(sectionClient);
        
        section.setClient(sectionClient); 
        section.pack();
    }
    
    protected abstract void drawSection(Composite parent);

    /**
     * {@inheritDoc}
     */
    public abstract void razValues();

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled)
    {
        section.setEnabled(enabled);
        if (!enabled){
            section.setExpanded(enabled);
            section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        }else{
            section.setTitleBarForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
    }

    public void setExpanded(boolean expanded){
        section.setExpanded(expanded);
    }
    
    public boolean isEnabled(){
        return section.isEnabled();
    }
    /**
     * init the section
     * @param parentForm
     */
    public void initSection(ScrolledForm parentForm){
        section.setParent(parentForm);
    }  
    /**
     * init the section
     * @param parentComposite
     */
    public void initSection(Composite parentComposite){
        section.setParent(parentComposite);
    }
    
    public abstract void initModel(StationModel stationModel);
    
    public abstract void fillFromModel(StationModel stationModel);

    /**
     * Getter sur name.
     * @return the name
     */
    public String getName()
    {
        return name;
    }

}
