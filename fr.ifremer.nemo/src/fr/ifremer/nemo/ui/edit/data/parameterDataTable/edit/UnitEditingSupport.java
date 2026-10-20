package fr.ifremer.nemo.ui.edit.data.parameterDataTable.edit;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.ComboViewerFilter;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.data.TypeUnit;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;

public class UnitEditingSupport extends EditingSupport {

	private static Logger logger = LoggerFactory.getLogger(UnitEditingSupport.class);

	private final TableViewer viewer;
	private final ValidationComboBoxViewerCellEditor editor;

	public UnitEditingSupport(final TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;

		this.editor = new ValidationComboBoxViewerCellEditor(viewer.getTable());
//		((Combo)this.editor.getControl()).setEditable(false);
		this.editor.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					TypeUnit unit = (TypeUnit) element;
					return unit.getTerm();
				}
				return "";
			}
		});
		this.editor.setContentProvider(new ArrayContentProvider());
		Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().initTypesP061();
		List<TypeUnit> unitList = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypesP061();

		this.editor.setInput(unitList);
		this.editor.getViewer().insert(null, 0);
		// Add filter
		this.editor.getViewer().addFilter(new ComboViewerFilter(this.editor.getViewer(),  ComboViewerFilter.MatchTypeEnum.BEGINS_WITH));

		this.editor.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TypeUnit unit = (TypeUnit) selection.getFirstElement();
				if (null != unit) {
					IStructuredSelection selectedType = (IStructuredSelection) viewer.getSelection();
					TypeData typeData = (TypeData) selectedType.getFirstElement();
					if (isUnitP09()) {
						if (null != typeData && unit.getTerm() != typeData.getUnite()) {
							setValue(typeData, unit);
						}
					} else {
						if (null != typeData && unit.getTerm() != typeData.getP06Label()) {
							setValue(typeData, unit);
						}
					}
				}
			}
		});
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		TypeData typeData = (TypeData) element;
		if (RulesManager.getInstance().getMyDataListType() == DataListTypeEnum.P011_VIA_P021 && !typeData.isText()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected Object getValue(Object element) {
		TypeData typeData = (TypeData) element;
		String unitLabel = null;
		// if (typeData.isText()) {
		if (isUnitP09()) { // 25794
			unitLabel = typeData.getUnite();
		} else {
			unitLabel = typeData.getP06Label();
		}
		// }
		TypeUnit typeUnit = Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData()
				.getUnitByName(unitLabel);
		if (typeUnit != null) {
			return Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getUnitByName(unitLabel);
		} else {
//			return new TypeUnit(unitLabel, null, null,null);
			return unitLabel;
		}

		// return unitLabel;
	}

	@Override
	protected void setValue(Object element, Object value) {
//		String unit = ((String) value).trim();
		TypeData typeData = (TypeData) element;
//		typeData.setUnite(unit);
//		if (!isUnitP09()) {
//			typeData.setP06Label(unit);
//		}
//		if (value instanceof TypeUnit) {
		TypeUnit unit = (TypeUnit) value;
		// only set new value if it differs from old one
		if (null != unit) {
			if (!unit.getTerm().equals(typeData.getUnite())) {
				typeData.setUnite(unit.getTerm());
				typeData.setUniteCode(unit.getUnitKey());
				if (!isUnitP09()) {
					typeData.setP06Label(unit.getTerm());
				}
				viewer.update(element, null);
			}
		} else {
			typeData.setUnite(null);
			typeData.setP06Label(null);
			viewer.update(element, null);
		}
	}

	boolean isUnitP09() {
		return RulesManager.getInstance().isDataListTypeP091()
				|| RulesManager.getInstance().isDataListTypeP011viaP091();
	}

}
