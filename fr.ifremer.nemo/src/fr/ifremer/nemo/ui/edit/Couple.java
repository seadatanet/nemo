/* ============================================================================
 * Nom du fichier	: Couple.java
 * Auteur			: dboulang
 * Date				: 21 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import java.util.regex.Pattern;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: Objet graphique constitué d'un couple libellé / valeur
 * 
 * @author dboulang
 */
public class Couple implements UIobject
{

    private String groupTitle;
    private Label label;
    private String labelString;
    private ComboType type;
    private String defaultValue;
    private Text valueText;
    private Combo valueCombo;
    private boolean fullSize;
    private int textLimit;

    public Couple(String groupTitle, String labelText, ComboType type,
            String defaultValue)
    {
        // fullSize = true; // par défaut

        // Grouped data
        this.groupTitle = groupTitle;

        // Label
        this.labelString = labelText;

        // Text or Combo
        addValue(type, defaultValue);
    }

    public Couple(ComboType type, String defaultValue)
    {
        addValue(type, defaultValue);
    }

    public void addValue(ComboType cType, String value)
    {
        // Default value
        this.defaultValue = value;
        this.type = cType;
        completeLabels(cType);
    }

    public void setFullSize(boolean full)
    {
        this.fullSize = full;
    }

    public void setTextLimit(int limit)
    {
        this.textLimit = limit;
    }

    public void draw(Composite compo, boolean filled)
    {

        Composite parent = compo;

        // Grouped data
        if (groupTitle != null)
        {

            GridLayout gridLayout = new GridLayout();
            if (type == ComboType.FRQ)
                gridLayout.numColumns = 4;

            Group groupe = new Group(compo, SWT.NONE);
            groupe.setLayout(gridLayout);
            groupe.setText(groupTitle);
            parent = groupe;
        }

        // Label
        if (labelString != null
                && !labelString.trim().equals(Global.EMPTY_STRING))
        {
            label = new Label(parent, SWT.NONE);
            label.setBackground(ViewMain.backgroungEdit);
            label.setText(labelString);
        }

        // Text or Combo
        chooseValue(parent, type);
        if (valueText != null)
        {
            valueText.setParent(parent);
            defaultValueTextSize();

            // Default value
            if (!filled && defaultValue != null)
                valueText.setText(defaultValue);

            valueText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event)
                {
                    Utils.invalidEtapes();
                }
            });
        }
        else if (valueCombo != null)
        {
            valueCombo.setParent(parent);
            valueCombo.setBackground(ViewMain.backgroungEdit);
            // valueCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            GridDataFactory.fillDefaults().grab(false, true).hint(Global.SIZE_SECTION, 25)
                .applyTo(valueCombo);

            if (!filled)
            {
                Utils.selectComboCode(valueCombo, defaultValue);
            }
            valueCombo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event)
                {
                    Utils.invalidEtapes();
                }
            });
        }

    }

    public void dispose()
    {
        if (label != null)
            label.dispose();
        if (valueText != null)
            valueText.dispose();
        if (valueCombo != null)
            valueCombo.dispose();
    }

    private void completeLabels(ComboType cType)
    {
        switch (cType)
        {
            case FRQ:
                groupTitle = Messages.label_conversion;
                labelString = Messages.label_formule;
            break;
            case DATE:
            case TIME:
            case LAT:
            case LON:
                groupTitle = Messages.label_format;
            break;
            default:
                // nothing to complete
            break;
        }
    }

    private void chooseValue(Composite parent, ComboType cType)
    {
        switch (cType)
        {
            case BLOCK:
                valueText =
                    new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
                            | SWT.WRAP);
            break;
            case DATE:
            case TIME:
            case LAT:
            case LON:
            case DATATYPE:
            case SHIP:
            case REGION:
            case COUNTRY:
            case DATACENTER:
            case CONFIDENT:
                valueCombo = cType.createCombo(parent);
                valueCombo = cType.fillCombo(valueCombo);
            break;
            default:
                valueText = new Text(parent, SWT.BORDER);
            break;
        }
    }

    // public void refreshCombo(ComboType cType)
    // {
    // valueCombo = cType.fillCombo(valueCombo);
    // }

    public void setEnabled(boolean enabled)
    {
        // Text or Combo
        if (valueText != null)
        {
            valueText.setEnabled(enabled);
        }
        else if (valueCombo != null)
        {
            valueCombo.setEnabled(enabled);
        }
    }

    public void razValues()
    {
        // Text or Combo
        if (valueText != null)
        {
            valueText.setText(defaultValue);
            defaultValueTextSize();
        }
        else if (valueCombo != null)
        {
            Utils.selectComboCode(valueCombo, defaultValue);
            // valueCombo.deselectAll();
        }
    }

    public boolean isEmpty()
    {
        boolean empty = true;
        if ((valueText != null && !valueText.getText().trim().equals(
            Global.EMPTY_STRING))
                || (valueCombo != null && !valueCombo.getText().trim().equals(
                    Global.EMPTY_STRING)))
        {
            empty = false;
        }
        return empty;
    }

    public void razDefaultValues()
    {
        // Text or Combo
        if (valueText != null)
        {
            valueText.setText(Global.EMPTY_STRING);
            defaultValueTextSize();
        }
        else if (valueCombo != null)
        {
            valueCombo.deselectAll();
        }
    }

    private void defaultValueTextSize()
    {
        valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if (type == ComboType.BLOCK)
        {
            valueText.setTextLimit(textLimit);
            GridData gridData =
                new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
            // gridData.minimumHeight = 40;
            // gridData.minimumWidth = 50;
            // gridData.widthHint = 385;
            valueText.setLayoutData(gridData);
        }
        else if (type == ComboType.FRQ)
        {
            GridDataFactory.fillDefaults().grab(true, false)./*
                                                                 * minSize(100,
                                                                 * 10) .
                                                                 */applyTo(valueText);
        }
    }

    /**
     * Getter sur valueCombo.
     * 
     * @return the valueCombo
     */
    public Combo getValueCombo()
    {
        return valueCombo;
    }

    /**
     * Getter sur valueText.
     * 
     * @return the valueText
     */
    public Text getValueText()
    {
        return valueText;
    }

    public String getText()
    {
        String text = null;
        if (valueText != null)
            text = valueText.getText();
        else if (valueCombo != null)
            text = valueCombo.getText();
        return text;
    }

    public void setText(String value)
    {
        if (valueText != null)
        {
            valueText.setText(value);
        }
        else if (valueCombo != null)
        {
            int i = 0;
            int size = valueCombo.getItemCount();

            if (value.equals(Global.EMPTY_STRING))
            {
                valueCombo.select(-1);
            }
            else
            {
                valueCombo.select(i);

                while (i < size
                        && !valueCombo.getText().equalsIgnoreCase(value))
                {
                    i++;
                    valueCombo.select(i);
                }

                if (!valueCombo.getText().equalsIgnoreCase(value))
                {
                    valueCombo.add(value);
                    valueCombo.select(valueCombo.getItemCount() - 1);
                }
            }
        }
    }

    public boolean checkValue()
    {
        if (!Pattern.matches("^[0-9][0-9]*$", getText())) { //$NON-NLS-1$
            return false;
        }
        return true;
    }

    /**
     * Setter sur valueText.
     * 
     * @param valueText
     *            the valueText to set
     */
    public void setValueText(Text valueText)
    {
        this.valueText = valueText;
    }

    /**
     * Setter sur valueCombo.
     * 
     * @param valueCombo
     *            the valueCombo to set
     */
    public void setValueCombo(Combo valueCombo)
    {
        this.valueCombo = valueCombo;
    }

    /**
     * Setter sur groupTitle.
     * 
     * @param groupTitle
     *            the groupTitle to set
     */
    public void setGroupTitle(String groupTitle)
    {
        this.groupTitle = groupTitle;
    }

    /**
     * Setter sur label.
     * 
     * @param label
     *            the label to set
     */
    public void setLabel(Label label)
    {
        this.label = label;
    }

    public String getLabelString()
    {
        return labelString;
    }

    /**
     * Setter sur labelString.
     * 
     * @param labelString
     *            the labelString to set
     */
    public void setLabelString(String labelString)
    {
        this.labelString = labelString;
    }

    public ComboType getType()
    {
        return type;
    }

}
