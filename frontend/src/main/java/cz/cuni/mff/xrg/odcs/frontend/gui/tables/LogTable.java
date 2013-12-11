package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.commons.app.facade.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.GraphIterator;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.CachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.details.LogMessageDetail;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tepi.filtertable.FilterGenerator;
import org.tepi.filtertable.datefilter.DateInterval;

/**
 * Table for displaying {@link Log}s.
 *
 * @author Petyr
 * @author bogo777
 */
public class LogTable extends CustomComponent {

	private static final Logger LOG = LoggerFactory.getLogger(LogTable.class);
	private VerticalLayout mainLayout;
	private IntlibPagedTable table;
	private PipelineExecution execution;
	private FilterGenerator filterGenerator;
	private LogMessageDetail detail = null;
	/**
	 * Access to data for retrieving detail log information. TODO replace with
	 * ContainerSource
	 */
	private final CachedSource<Log> dataSouce;
	private final ReadOnlyContainer<Log> container;
	/**
	 * Contains names of {@link DPUInstanceRecord}s. Used as a cache for
	 * generated column.
	 */
	private final Map<Long, String> dpuNames = new HashMap<>();
	private final LogFacade logFacade;

	/**
	 * Default constructor.
	 *
	 * @param dataSouce
	 */
	public LogTable(CachedSource<Log> dataSouce, LogFacade logFacade, int pageLenght) {
		this.dataSouce = dataSouce;
		this.container = new ReadOnlyContainer<>(dataSouce);
		this.logFacade = logFacade;

		// build layout
		buildLayout(pageLenght);
	}

	/**
	 * Build user interface.
	 */
	private void buildLayout(int pageLenght) {
		mainLayout = new VerticalLayout();

		table = new IntlibPagedTable();
		table.setSelectable(true);
		table.setSizeFull();

		table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (!table.isSelected(event.getItemId())) {
					ValueItem item = (ValueItem) event.getItem();

					final long logId = item.getId();
					Log log = dataSouce.getObject(logId);
					showLogDetail(log);
				}
			}
		});
		// add interpreter for dpu column
		table.addGeneratedColumn("dpu", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Long dpuId = (Long) source.getItem(itemId).getItemProperty(columnId).getValue();
				if (dpuId == null) {
					return null;
				}
				return dpuNames.get(dpuId);
			}
		});
		table.addGeneratedColumn("logLevel", new CustomTable.ColumnGenerator() {
			@Override
			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
				Integer level = (Integer) source.getItem(itemId).getItemProperty(columnId).getValue();
				return Level.toLevel(level);
			}
		});

		// add filter generation
		ComboBox levelSelector = new ComboBox();
		levelSelector.setImmediate(true);
		levelSelector.setNullSelectionAllowed(false);
		levelSelector.addItem(Level.ALL);
		for (Level level : logFacade.getAllLevels(false)) {
			levelSelector.addItem(level);
			levelSelector.setItemCaption(level, level.toString() + "+");
		}

		ComboBox dpuSelector = new ComboBox();
		dpuSelector.setImmediate(true);

		filterGenerator = createFilterGenerator(dpuSelector, levelSelector);
		table.setFilterGenerator(filterGenerator);
		table.setSortEnabled(false);
		table.setFilterBarVisible(true);
		table.setPageLength(pageLenght);

		// add to the main layout
		mainLayout.addComponent(table);
		mainLayout.addComponent(table.createControls());

		Button download = new Button("Download");
		FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {
			@Override
			public String getFilename() {
				return "log.txt";
			}

			@Override
			public InputStream getStream() {
				// get current dpu
				DPUInstanceRecord dpu = (DPUInstanceRecord) table.getFilterFieldValue("dpu");
				Level level = Level.toLevel((Integer) table.getFilterFieldValue("logLevel"));
				String message = (String) table.getFilterFieldValue("message");
				String source = (String) table.getFilterFieldValue("source");
				Object date = table.getFilterFieldValue("timestamp");
				Date start = null;
				Date end = null;
				if (date != null) {
					DateInterval interval = (DateInterval) date;
					start = interval.getFrom();
					end = interval.getTo();
				}
				return logFacade.getLogsAsStream(execution, dpu, level, message, source, start, end);
			}
		});
		fileDownloader.extend(download);
		mainLayout.addComponent(download);
		setCompositionRoot(mainLayout);

	}

	private ComboBox refreshDpuSelector() {
		// get DPU selector
		ComboBox dpuSelector = (ComboBox) table.getFilterField("dpu");
		// refresh it's content
		dpuSelector.removeAllItems();

		if (execution == null) {
			// no execution set .. 
			return null;
		}

		if (isRunning(execution)) {

			Node debugNode = execution.getDebugNode();
			DependencyGraph dependencyGraph = debugNode == null ? new DependencyGraph(execution.getPipeline().getGraph()) : new DependencyGraph(execution.getPipeline().getGraph(), debugNode);
			GraphIterator iterator = dependencyGraph.iterator();
			while (iterator.hasNext()) {
				DPUInstanceRecord item = iterator.next().getDpuInstance();
				if (!dpuSelector.containsId(item)) {
					dpuSelector.addItem(item);
				}
			}
		} else {

			ExecutionContextInfo ctx = execution.getContextReadOnly();
			if (ctx != null) {
				for (DPUInstanceRecord item : ctx.getDPUIndexes()) {
					if (!dpuSelector.containsId(item)) {
						dpuSelector.addItem(item);
					}
				}
			}
		}
		return dpuSelector;
	}

	/**
	 * Set active DPU for which the logs are shown. Also do the site refresh.
	 *
	 * @param dpu
	 */
	public void setDpu(DPUInstanceRecord dpu) {

		ComboBox dpuSelector = refreshDpuSelector();
		if(dpuSelector == null) {
			return;
		}
		// if the curent DPU is presented set is as active
		if (dpu != null && dpuSelector.containsId(dpu)) {
			dpuSelector.select(dpu);
		}
	}

	private boolean isRunning(PipelineExecution execution) {
		PipelineExecutionStatus status = execution.getStatus();
		return status == PipelineExecutionStatus.RUNNING || status == PipelineExecutionStatus.CANCELLING || status == PipelineExecutionStatus.QUEUED;
	}

	/**
	 * Set execution and DPU to show logs for, also reload the data.
	 *
	 * @param exec
	 * @param dpuInstance
	 */
	public void setExecution(PipelineExecution exec, DPUInstanceRecord dpuInstance) {
		this.execution = exec;
		// refresh DPU names
		dpuNames.clear();
		for (Node node : exec.getPipeline().getGraph().getNodes()) {
			DPUInstanceRecord nodeDpu = node.getDpuInstance();
			dpuNames.put(nodeDpu.getId(), nodeDpu.getName());
		}
		// also remove all filters
		this.container.removeAllContainerFilters();
		// set container to the table -- we may possibly re-set this
		// but that does not do anything bad
		table.setContainerDataSource(this.container);
		// move to the last page
		table.setCurrentPage(table.getTotalAmountOfPages());
		// set DPU
		setDpu(dpuInstance);
	}

	/**
	 * Reload data from source, do not refresh the source it self!!
	 *
	 * @return
	 */
	public boolean refresh(PipelineExecution exec) {
		this.execution = exec;
		refreshDpuSelector();
		int lastPage = table.getTotalAmountOfPages();
		if (table.getCurrentPage() == lastPage) {
			container.refresh();
		} else {
			table.setCurrentPage(lastPage);
		}
		return true;
	}

	/**
	 * Show windows with detail of single log.
	 *
	 * @param log
	 */
	protected void showLogDetail(Log log) {
		if (detail == null) {
			final LogMessageDetail detailWindow = new LogMessageDetail(log);
			detailWindow.setHeight(600, Unit.PIXELS);
			detailWindow.setWidth(500, Unit.PIXELS);
			detailWindow.setImmediate(true);
			detailWindow.setContentHeight(600, Unit.PIXELS);
			detailWindow.addResizeListener(new Window.ResizeListener() {
				@Override
				public void windowResized(Window.ResizeEvent e) {
					detailWindow.setContentHeight(e.getWindow().getHeight(), Unit.PIXELS);
				}
			});
			detailWindow.addCloseListener(new Window.CloseListener() {
				@Override
				public void windowClose(Window.CloseEvent e) {
					detail = null;
				}
			});
			detail = detailWindow;
			UI.getCurrent().addWindow(detailWindow);
		} else {
			detail.loadMessage(log);
			detail.bringToFront();
		}
	}

	private FilterGenerator createFilterGenerator(final ComboBox dpuSelector, final ComboBox levelSelector) {
		return new FilterGenerator() {
			@Override
			public Container.Filter generateFilter(Object propertyId, Object value) {
				if (propertyId.equals("logLevel")) {
					Level level = (Level) value;
					return new Compare.GreaterOrEqual("logLevel", level.toInt());
				} else if (propertyId.equals("timestamp")) {
					DateInterval interval = (DateInterval) value;
					if (interval.getFrom() == null) {
						return new Compare.LessOrEqual("timestamp", interval.getTo().getTime());
					} else if (interval.getTo() == null) {
						return new Compare.GreaterOrEqual("timestamp", interval.getFrom().getTime());
					} else {
						return new Between("timestamp", interval.getFrom().getTime(), interval.getTo().getTime());
					}
				} else if (propertyId.equals("dpu")) {
					if (value != null) {
						Long id = ((DPUInstanceRecord) value).getId();
						return new Compare.Equal("dpu", id);
					} else {
						return null;
					}
				}
				return null;
			}

			@Override
			public AbstractField<?> getCustomFilterComponent(Object propertyId) {
				if (propertyId == null) {
					return null;
				}
				if (propertyId.equals("logLevel")) {
					return levelSelector;
				} else if (propertyId.equals("dpu")) {
					return dpuSelector;
				}
				return null;
			}

			@Override
			public void filterRemoved(Object propertyId) {
			}

			@Override
			public void filterAdded(Object propertyId, Class<? extends Container.Filter> filterType, Object value) {
			}

			@Override
			public Container.Filter filterGeneratorFailed(Exception reason, Object propertyId, Object value) {
				return null;
			}

			@Override
			public Container.Filter generateFilter(Object propertyId, Field<?> originatingField) {
				return null;
			}
		};
	}
}
