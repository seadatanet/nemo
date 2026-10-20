/* ============================================================================
 * Nom du fichier	: ComboType.java
 * Auteur			: dboulang
 * Date				: 23 janv. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.edit;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

/**
 * Description: enumération des types de Combo possibles
 * 
 * @author dboulang
 */
public enum ComboType {
	NONE, BLOCK, FRQ, DATE, TIME, LAT, LON, DATATYPE, SHIP, REGION, COUNTRY, DATACENTER, CONFIDENT;

	public CCombo createCombo(Composite parent) {
		/**
		 * SHIP, REGION, COUNTRY, DATACENTER, CONFIDENT must be read only
		 */
		CCombo valueCombo;
		switch (this) {
		case DATE:
		case TIME:
		case LAT:
		case LON:
		case DATATYPE:
			valueCombo = new CCombo(parent, SWT.BORDER);
			break;
		case CONFIDENT:
			valueCombo = new CCombo(parent, SWT.READ_ONLY | SWT.BORDER);
			break;
		default:
			valueCombo = null;
			break;
		}
		return valueCombo;
	}

	public ComboViewer createComboViewer(Composite parent) {
		ComboViewer comboViewer;
		switch (this) {
		case SHIP:
		case REGION:
		case COUNTRY:
		case DATACENTER:
			comboViewer = new ComboViewer(parent, SWT.BORDER);
			comboViewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					Entry<String, String> entry = (Entry) element;
					if (element != null) {
						if (!entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
							return entry.getKey() + Global.SEP + entry.getValue();
						} else if (entry.getKey().isEmpty()) {
							return entry.getValue();
						} else if (entry.getValue().isEmpty()) {
							return entry.getKey();
						}
					}
					return "";
				}
			});

			comboViewer.setContentProvider(new IStructuredContentProvider() {
				@Override
				public Object[] getElements(Object inputElement) {
					Collection<?> col = null;
					if (inputElement instanceof Map<?, ?>) {
						col = ((Map<String, String>) inputElement).entrySet();
					}
					return col.toArray();
				}

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

				@Override
				public void dispose() {
				}
			});

			comboViewer.addFilter(new ComboViewerFilter(comboViewer, ComboViewerFilter.MatchTypeEnum.CONTAINS));
			break;
		default:
			comboViewer = null;
			break;
		}
		return comboViewer;
	}

	public CCombo fillCombo(CCombo valueCombo) {
		switch (this) {
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
			// valueCombo.add("DDDDDDD.d"); //$NON-NLS-1$
			valueCombo.add(Global.DATETIME_CHOICE_ISO);
			valueCombo.add(Global.DATETIME_CHOICE_JULIAN);
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
			// véritable "Decimal Time" : valueCombo.add("cc.cc.cc");
			// //$NON-NLS-1$
			valueCombo.add("hh24.ccc"); //$NON-NLS-1$
			// valueCombo.add("D.dddddd"); //$NON-NLS-1$
			valueCombo.add("ttttt"); //$NON-NLS-1$
			valueCombo.add(Global.DATETIME_CHOICE_ISO);
			valueCombo.add(Global.DATETIME_CHOICE_JULIAN);
			break;
		case LAT:
			valueCombo.add("+DD MM.mm"); //$NON-NLS-1$
			valueCombo.add("+DD.ddd"); //$NON-NLS-1$
			valueCombo.add("+DDMMSS"); //$NON-NLS-1$
			valueCombo.add("NDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("NDD.ddd"); //$NON-NLS-1$
			valueCombo.add("NDDMMSS"); //$NON-NLS-1$
			if (RulesManager.getInstance().isSeparatorChecked()) {
				valueCombo.add(EditStationForm.LATLON_CHOICE_DEC_DEGREES_FOR_FILES_WITH_SEP);
			}
			break;
		case LON:
			valueCombo.add("+DDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("+DDD.ddd"); //$NON-NLS-1$
			valueCombo.add("+DDDMMSS"); //$NON-NLS-1$
			valueCombo.add("NDDD MM.mm"); //$NON-NLS-1$
			valueCombo.add("NDDD.ddd"); //$NON-NLS-1$
			valueCombo.add("NDDDMMSS"); //$NON-NLS-1$
			if (RulesManager.getInstance().isSeparatorChecked()) {
				valueCombo.add(EditStationForm.LATLON_CHOICE_DEC_DEGREES_FOR_FILES_WITH_SEP);
			}
			break;
		case DATATYPE:
			// FAE022
			if (!RulesManager.getInstance().isProcessCollection() && TableDataSurvey.types != null) {
				int taille = TableDataSurvey.types.size();
				for (int i = 0; i < taille; i++) {
					// on n'ajoute que les sensors
					if (TableDataSurvey.types.get(i).getSensor()) {
						valueCombo.add(TableDataSurvey.types.get(i).getCode());
					}
				}
				if (valueCombo.getItemCount() == 1) {
					valueCombo.select(0);
				}
			}
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

	public ComboViewer fillComboViewer(ComboViewer comboViewer) {

		try {
			switch (this) {
			case SHIP:
				Collection<IElement> shipCollection = ExternalResourcesManager.getInstance().getSdnVocabularyManager()
						.getVocabList(Intern.list_ship).getElements();
				// Build inputMap
				TreeMap<String, String> shipMap = new TreeMap<String, String>();
				for (IElement record : shipCollection) {
					shipMap.put(record.getKey(), record.getLabel());
				}

				// Set input
				comboViewer.setInput(shipMap);
				comboViewer.setComparator(new ViewerComparatorByKey());
				break;
			case REGION:
				Collection<IElement> regionCollection = ExternalResourcesManager.getInstance().getSdnVocabularyManager()
						.getVocabList(Intern.list_regionsPolygones).getElements();
				// Build inputMap
				TreeMap<String, String> regionMap = new TreeMap<String, String>();
				for (IElement record : regionCollection) {
					regionMap.put(record.getKey(), record.getLabel());
				}

				// Set input
				comboViewer.setInput(regionMap);
				comboViewer.setComparator(new ViewerComparatorByLabel());
				break;

			case COUNTRY:
				Collection<IElement> countryCollection = ExternalResourcesManager.getInstance()
						.getSdnVocabularyManager().getVocabList(Intern.list_countries).getElements();
				// Build inputMap
				Map<String, String> countryMap = new TreeMap<>();
				for (IElement record : countryCollection) {
					countryMap.put(record.getKey(), record.getLabel());
				}

				// Set input
				comboViewer.setInput(countryMap);
				comboViewer.setComparator(new ViewerComparatorByLabel());
				break;
			case DATACENTER:
				Collection<IElement> dataCenterCollection = ExternalResourcesManager.getInstance()
						.getSdnVocabularyManager().getVocabList(Intern.list_dataCenter).getElements();
				// Build inputMap
				TreeMap<String, String> dataCenterMap = new TreeMap<String, String>();
				for (IElement record : dataCenterCollection) {
					dataCenterMap.put(record.getKey(), record.getLabel());
				}

				// Set input
				comboViewer.setInput(dataCenterMap);
				comboViewer.setComparator(new ViewerComparatorByKey());
				break;
			default:
				break;
			}
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}

		return comboViewer;
	}

	class ViewerComparatorByKey extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object elm1, Object elm2) {
			Entry<String, String> entry1 = (Entry<String, String>) elm1;
			Entry<String, String> entry2 = (Entry<String, String>) elm2;
			return entry1.getKey().compareToIgnoreCase(entry2.getKey());
		}
	}

	class ViewerComparatorByLabel extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object elm1, Object elm2) {
			Entry<String, String> entry1 = (Entry<String, String>) elm1;
			Entry<String, String> entry2 = (Entry<String, String>) elm2;
			return entry1.getKey().compareToIgnoreCase(entry1.getKey());
		}
	}

}
