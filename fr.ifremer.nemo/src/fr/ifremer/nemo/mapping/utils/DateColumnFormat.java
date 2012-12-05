package fr.ifremer.nemo.mapping.utils;
/*
 * DateColumnFormat.java
 * makes available a simple DateRenderer und DateEditor for JTable.
 * Usage example:
 * DateColumnFormat dateColumnFormat = new DateColumnFormat();
 * table.setDefaultRenderer(Date.class, dateColumnFormat.getRenderer());
 * table.setDefaultEditor(Date.class, dateColumnFormat.getEditor());
 */

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class DateColumnFormat {

    private SimpleDateFormat formatterR,  formatterE;
    private DefaultTableCellRenderer renderer;
    private DefaultCellEditor editor;

    /**
     * Constructs a <code>DateColumnFormat</code> using the pattern "dd/MM/yyyy" and
     * the default date format symbols for the default locale.
     */
    public DateColumnFormat() {
        this(null);
    }

    /**
     * Constructs a <code>DateColumnFormat</code> using the given pattern and
     * the default date format symbols for the default locale.
     *
     *
     * @param pattern the pattern describing the date format, "dd/MM/yyyy" if null
     */
    public DateColumnFormat(String pattern) {
        this(pattern, pattern);
    }

    /**
     * Constructs a <code>DateColumnFormat</code> using the given patterns and
     * the default date format symbols for the default locale.
     *
     *
     * @param patternR the pattern describing the date format of the renderer, "dd/MM/yyyy" if null
     * @param patternE the pattern describing the date format of the editor, "dd/MM/yyyy" if null
     */
    public DateColumnFormat(String patternR, String patternE) {
        if (patternR == null) {
            patternR = "dd/MM/yyyy HH:mm:ss";
        }
        formatterR = new SimpleDateFormat(patternR);
        formatterR.setLenient(false);
        if (patternE == null) {
            patternE = "dd/MM/yyyy HH:mm:ss";
        }
        formatterE = new SimpleDateFormat(patternE);
        formatterE.setLenient(false);
        renderer = new DateRenderer();
        editor = new DateEditor();
    }

    /**
     * Returns the date cell renderer.
     * @return the table date cell renderer
     */
    public DefaultTableCellRenderer getRenderer() {
        return renderer;
    }

    /**
     * Returns the date cell editor.
     * @return the table date cell editor
     */
    public DefaultCellEditor getEditor() {
        return editor;
    }

    private class DateRenderer extends DefaultTableCellRenderer {

        public DateRenderer() {
            super();
        }

        @Override
        public void setValue(Object value) {
            setText((value == null) ? "" : formatterR.format(value));
        }
    }

    private class DateEditor extends DefaultCellEditor {

        public DateEditor() {
            super(new JTextField());
        }

        @Override
        public boolean stopCellEditing() {
            String value = ((JTextField) getComponent()).getText();
            if (!value.equals("")) {
                try {
                    formatterE.parse(value);
                } catch (ParseException e) {
                    ((JComponent) getComponent()).setBorder(new LineBorder(Color.red));
                    return false;
                }
            }
            return super.stopCellEditing();
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value,
                final boolean isSelected, final int row, final int column) {
            JTextField tf = ((JTextField) getComponent());
            tf.setBorder(new LineBorder(Color.black));
            try {
                tf.setText(formatterE.format(value));
            } catch (Exception e) {
                tf.setText("");
            }
            return tf;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                Date value = formatterE.parse(((JTextField) getComponent()).getText());
                return value;
            } catch (ParseException ex) {
                return null;
            }
        }
    }
}