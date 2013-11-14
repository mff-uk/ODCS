package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUType;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Component for selecting from available DPUs and their DataUnits.
 *
 * @author Bogo
 */
public class DataUnitSelector extends CustomComponent {

	private PipelineExecution pipelineExec;
	GridLayout mainLayout;
	ComboBox dpuSelector;
	private DPUInstanceRecord debugDpu;
	private ExecutionContextInfo ctxReader;
	private CheckBox inputDataUnits;
	private CheckBox outputDataUnits;
	private ComboBox dataUnitSelector;
	private Button browse;
	private Label dataUnitGraph;

	public DataUnitSelector(PipelineExecution execution) {
		pipelineExec = execution;
		buildMainLayout();
	}

	private void buildMainLayout() {
		loadExecutionContextReader();

		mainLayout = new GridLayout(6, 3);
		mainLayout.setSpacing(true);
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		dpuSelector = buildDpuSelector();
		mainLayout.addComponent(dpuSelector, 0, 1);

		Label dpuSelectorLabel = new Label("Select DPU:");
		mainLayout.addComponent(dpuSelectorLabel, 0, 0);

		Label dataUnitLabel = new Label("Select Data Unit:");

		inputDataUnits = new CheckBox("Input");
		inputDataUnits.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				refreshDataUnitSelector();
			}
		});
		inputDataUnits.setEnabled(false);

		outputDataUnits = new CheckBox("Output");
		outputDataUnits.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				refreshDataUnitSelector();
			}
		});
		outputDataUnits.setEnabled(false);

		HorizontalLayout dataUnitTopLine = new HorizontalLayout(dataUnitLabel, inputDataUnits, outputDataUnits);
		dataUnitTopLine.setSpacing(true);
		mainLayout.addComponent(dataUnitTopLine, 1, 0, 5, 0);

		dataUnitSelector = new ComboBox();
		dataUnitSelector.setWidth(100, Unit.PERCENTAGE);
		dataUnitSelector.setEnabled(false);
		dataUnitSelector.setNullSelectionAllowed(false);
		dataUnitSelector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				DataUnitInfo info = (DataUnitInfo) event.getProperty().getValue();
				if (info != null) {
					String id = ctxReader.generateDataUnitId(getSelectedDPU(), info.getIndex()); // where index if from DataUnitInfo and context is Execution context info
					String graphUrl = GraphUrl.translateDataUnitId(id);
					dataUnitGraph.setValue(graphUrl);
				}
			}
		});
		mainLayout.addComponent(dataUnitSelector, 1, 1, 4, 1);

		dataUnitGraph = new Label();
		dataUnitGraph.setWidth(100, Unit.PERCENTAGE);
		mainLayout.addComponent(dataUnitGraph, 1, 2, 5, 2);

		browse = new Button("Browse");
		browse.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				fireEvent(new BrowseRequestedEvent(DataUnitSelector.this));
			}
		});
		browse.setEnabled(false);
		mainLayout.addComponent(browse, 5, 1);

		setCompositionRoot(mainLayout);
	}

	public void refresh(PipelineExecution exec) {
		pipelineExec = exec;
		if (loadExecutionContextReader()) {
			refreshDpuSelector();
		}
	}

	private void fireEvent(Event event) {
		Collection<Listener> ls = (Collection<Listener>) this.getListeners(Component.Event.class);
		for (Listener l : ls) {
			l.componentEvent(event);
		}
	}

	/**
	 * Tries to load context for given pipeline execution.
	 *
	 * @return Load was successful.
	 */
	private boolean loadExecutionContextReader() {
		ctxReader = pipelineExec.getContextReadOnly();
		return ctxReader != null;
	}

	/**
	 * DPU select box factory.
	 */
	private ComboBox buildDpuSelector() {
		dpuSelector = new ComboBox();
		dpuSelector.setImmediate(true);
		if (ctxReader != null) {
			refreshDpuSelector();
		}
		dpuSelector.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Object value = event.getProperty().getValue();

				if (value != null && value.getClass() == DPUInstanceRecord.class) {
					debugDpu = (DPUInstanceRecord) value;
					dataUnitSelector.removeAllItems();
					setDataUnitCheckBoxes(debugDpu.getType());
				} else {
					debugDpu = null;
				}
				refreshDataUnitSelector();
				refreshEnabled();
			}
		});
		return dpuSelector;
	}

	private void setDataUnitCheckBoxes(DPUType type) {
		switch (type) {
			case LOADER:
				inputDataUnits.setEnabled(true);
				inputDataUnits.setValue(true);
				outputDataUnits.setEnabled(false);
				break;
			default:
				inputDataUnits.setEnabled(true);
				inputDataUnits.setValue(true);
				outputDataUnits.setEnabled(true);
				outputDataUnits.setValue(true);
				break;
		}
	}

	/**
	 * Fills DPU selector with DPUs for which there are debug information
	 * available.
	 */
	private void refreshDpuSelector() {
		dpuSelector.removeAllItems();
		Set<DPUInstanceRecord> contextDpuIndexes = ctxReader.getDPUIndexes();
		for (DPUInstanceRecord dpu : contextDpuIndexes) {
			if (!dpuSelector.containsId(dpu)) {
				dpuSelector.addItem(dpu);
				if (dpu.equals(debugDpu)) {
					dpuSelector.select(debugDpu);
				}
			}
		}
	}

	private void refreshDataUnitSelector() {
		if (debugDpu == null) {
			dataUnitSelector.removeAllItems();
			return;
		}
		List<DataUnitInfo> dataUnits = ctxReader.getDPUInfo(debugDpu).getDataUnits();
		Object selected = dataUnitSelector.getValue();
		Object first = null;
		for (DataUnitInfo dataUnit : dataUnits) {
			boolean isInput = dataUnit.isInput();
			if ((isInput && inputDataUnits.getValue()) || (!isInput && outputDataUnits.getValue())) {
				if (!dataUnitSelector.containsId(dataUnit)) {
					dataUnitSelector.addItem(dataUnit);
				}
				if (first == null) {
					first = dataUnit;
				}
			} else {
				if (dataUnitSelector.containsId(dataUnit)) {
					dataUnitSelector.removeItem(dataUnit);
					if(dataUnit.equals(selected)) {
						selected = null;
					}
				}
			}
		}
		if (selected != null) {
			dataUnitSelector.setValue(selected);
		} else if (first != null) {
			dataUnitSelector.setValue(first);
		}
		refreshEnabled();
	}

	private void refreshEnabled() {
		inputDataUnits.setEnabled(debugDpu != null);
		outputDataUnits.setEnabled(debugDpu != null);
		dataUnitSelector.setEnabled(debugDpu != null);
		boolean buttonsEnabled = dataUnitSelector.isEnabled() && dataUnitSelector.getValue() != null;
		browse.setEnabled(buttonsEnabled);
		if(buttonsEnabled) {
			fireEvent(new EnableEvent(dpuSelector));
		} else {
			fireEvent(new DisableEvent(dpuSelector));
		}
	}

	public DPUInstanceRecord getSelectedDPU() {
		return debugDpu;
	}

	public DataUnitInfo getSelectedDataUnit() {
		return (DataUnitInfo) dataUnitSelector.getValue();
	}

	public ExecutionContextInfo getContext() {
		return ctxReader;
	}

	void setSelectedDPU(DPUInstanceRecord dpu) {
		debugDpu = dpu;
		refreshDpuSelector();
	}

	/**
	 * Event sent to Listeners when browse is requested from this component.
	 */
	public class BrowseRequestedEvent extends Component.Event {

		public BrowseRequestedEvent(Component cmp) {
			super(cmp);
		}
	}

	/**
	 * Event sent to Listeners when this component requests disable.
	 */
	public class DisableEvent extends Component.Event {

		public DisableEvent(Component cmp) {
			super(cmp);
		}
	}

	/**
	 * Event sent to Listeners when this component requests enable.
	 */
	public class EnableEvent extends Component.Event {

		public EnableEvent(Component cmp) {
			super(cmp);
		}
	}
}
