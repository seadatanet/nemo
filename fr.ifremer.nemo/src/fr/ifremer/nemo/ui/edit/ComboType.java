/* ============================================================================
 * Nom du fichier	: ComboType.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

/**
 * Description: enumération des types de Combo possibles
 * 
 * @author dboulang
 */
public enum ComboType
{
    NONE,
    BLOCK,
    FRQ,
    DATE,
    TIME,
    LAT,
    LON,
    DATATYPE,
    SHIP,
    REGION,
    COUNTRY,
    DATACENTER,
    CONFIDENT;

    public Combo createCombo(Composite parent)
    {
        Combo valueCombo;
        switch (this)
        {
            case DATE:
            case TIME:
            case LAT:
            case LON:
            case DATATYPE:
                valueCombo = new Combo(parent, SWT.BORDER);
            break;
            case REGION:
            case COUNTRY:
                valueCombo = new Combo(parent, SWT.NONE);
            break;
            case SHIP:
            case DATACENTER:
            case CONFIDENT:
                valueCombo = new Combo(parent, SWT.READ_ONLY);
            break;
            default:
                valueCombo = null;
            break;
        }
        return valueCombo;
    }

    public Combo fillCombo(Combo valueCombo)
    {
        String temp;
        switch (this)
        {
            case DATE:
                valueCombo.add("DD/MM/YY"); //$NON-NLS-1$
                valueCombo.add("DD/MM/YYYY"); //$NON-NLS-1$
                valueCombo.add("DDMMYY"); //$NON-NLS-1$
                valueCombo.add("DDMMYYYY"); //$NON-NLS-1$
                valueCombo.add("MMM DD YYYY"); //$NON-NLS-1$
                valueCombo.add("YY/MM/DD"); //$NON-NLS-1$
                valueCombo.add("YYMMDD"); //$NON-NLS-1$
                valueCombo.add("YYYY/MM/DD"); //$NON-NLS-1$
                valueCombo.add("YYYY-MM-DD"); //$NON-NLS-1$
                valueCombo.add("YYYYMMDD"); //$NON-NLS-1$
                valueCombo.add("DDDDDDD.d"); //$NON-NLS-1$
            break;
            case TIME:
                valueCombo.add("hh:mm PM"); //$NON-NLS-1$
                valueCombo.add("hh:mm:ss PM"); //$NON-NLS-1$
                valueCombo.add("hh24:mm"); //$NON-NLS-1$
                valueCombo.add("hh24:mm:ss"); //$NON-NLS-1$
                valueCombo.add("hh24:mm:ss.sss"); //$NON-NLS-1$
                valueCombo.add("hh24mm"); //$NON-NLS-1$
                valueCombo.add("hh24mmss"); //$NON-NLS-1$
                valueCombo.add("hh24 mm"); //$NON-NLS-1$
                // véritable "Decimal Time" : valueCombo.add("cc.cc.cc");                  //$NON-NLS-1$
                valueCombo.add("hh24.ccc"); //$NON-NLS-1$
                valueCombo.add("D.dddddd"); //$NON-NLS-1$
                valueCombo.add("ttttt"); //$NON-NLS-1$
            break;
            case LAT:
                valueCombo.add("+DD MM.mm"); //$NON-NLS-1$
                valueCombo.add("+DD.ddd"); //$NON-NLS-1$
                valueCombo.add("+DDMMSS"); //$NON-NLS-1$
                valueCombo.add("NDD MM.mm"); //$NON-NLS-1$
                valueCombo.add("NDD.ddd"); //$NON-NLS-1$
                valueCombo.add("NDDMMSS"); //$NON-NLS-1$
            break;
            case LON:
                valueCombo.add("+DDD MM.mm"); //$NON-NLS-1$
                valueCombo.add("+DDD.ddd"); //$NON-NLS-1$
                valueCombo.add("+DDDMMSS"); //$NON-NLS-1$
                valueCombo.add("NDDD MM.mm"); //$NON-NLS-1$
                valueCombo.add("NDDD.ddd"); //$NON-NLS-1$
                valueCombo.add("NDDDMMSS"); //$NON-NLS-1$
            break;
            case DATATYPE:
                // FAE022
                if (! Nemo.getInstance().getViewMain().getEditFileForm().isCollection() && TableDataSurvey.types != null)
                {
                    int taille = TableDataSurvey.types.size();
                    for (int i = 0; i < taille; i++)
                    {
                        // on n'ajoute que les sensors
                        if (TableDataSurvey.types.get(i).getSensor())
                        {
                            valueCombo.add(TableDataSurvey.types.get(i)
                                .getCode());
                        }
                    }
                }
            break;
            case SHIP:
                CodeTableRecordType[] navires =
                    NemoLists.getVocabList(Intern.list_ship);

                TreeMap<String, String> treeNavire =
                    new TreeMap<String, String>();

                for (CodeTableRecordType record : navires)
                {
                    treeNavire.put(record.getEntryTerm(), Utils
                        .extraitCodeURN(record.getEntryKey()));
                }

                NemoLists.listeNavire = new ArrayList<String>();
                temp = Global.EMPTY_STRING;
                for (Entry<String, String> currentEntry : treeNavire.entrySet())
                {
                    temp =
                        currentEntry.getValue() + Global.SEP
                                + currentEntry.getKey();
                    NemoLists.listeNavire.add(temp);
                    valueCombo.add(temp);
                }
                Utils.createSearchMenu(valueCombo, Global.SHIP);
            break;
            case REGION:
                NemoLists.listeRegion = new ArrayList<String>();
                CodeTableRecordType[] regions =
                    NemoLists.getVocabList(Intern.list_regions);

                TreeMap<String, String> treeRegion =
                    new TreeMap<String, String>();

                for (CodeTableRecordType record : regions)
                {
                    treeRegion.put(record.getEntryTerm(), Utils
                        .extraitCodeURN(record.getEntryKey()));
                }

                temp = Global.EMPTY_STRING;
                for (Entry<String, String> currentEntry : treeRegion.entrySet())
                {
                    temp =
                        currentEntry.getValue() + Global.SEP
                                + currentEntry.getKey();
                    NemoLists.listeRegion.add(temp);
                    valueCombo.add(temp);
                }
                Utils.createSearchMenu(valueCombo, Global.REGION);
            break;
            case COUNTRY:
                CodeTableRecordType[] records =
                    NemoLists.getVocabList(Intern.list_countries);

                TreeMap<String, String> treePays =
                    new TreeMap<String, String>();

                for (CodeTableRecordType record : records)
                {
                    treePays.put(record.getEntryTerm(), Utils
                        .extraitCodeURN(record.getEntryKey()));
                }

                for (Entry<String, String> currentEntry : treePays.entrySet())
                {
                    valueCombo.add(currentEntry.getValue() + Global.SEP
                            + currentEntry.getKey());
                }
            break;
            case DATACENTER:
                CodeTableRecordType[] centres =
                    NemoLists.getVocabList(Intern.list_dataCenter);

                TreeMap<String, String> treeCentre =
                    new TreeMap<String, String>();

                for (CodeTableRecordType record : centres)
                {
                    treeCentre.put(Utils.extraitCodeURN(record.getEntryKey()),
                        record.getEntryTerm());
                }

                NemoLists.listeCentreDonnees = new ArrayList<String>();
                temp = Global.EMPTY_STRING;
                for (Entry<String, String> currentEntry : treeCentre.entrySet())
                {
                    temp =
                        currentEntry.getKey() + Global.SEP
                                + currentEntry.getValue();
                    NemoLists.listeCentreDonnees.add(temp);
                    valueCombo.add(temp);
                }
                Utils.createSearchMenu(valueCombo, Global.DATACENTRE);
            break;
            case CONFIDENT:
                valueCombo.add("P - Public"); //$NON-NLS-1$
                valueCombo.add("L - Limited"); //$NON-NLS-1$
                valueCombo.add("C - Confidential"); //$NON-NLS-1$
            break;
            default:
            break;
        }
        return valueCombo;
    }
}
