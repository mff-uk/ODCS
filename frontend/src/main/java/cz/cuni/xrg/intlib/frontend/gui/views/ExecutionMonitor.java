package cz.cuni.xrg.intlib.frontend.gui.views;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;

import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.*;

/**
 * @author Maria Kukhar
 */
class ExecutionMonitor extends ViewComponent implements ClickListener {

	@AutoGenerated
	private VerticalLayout monitorTableLayout;

	private VerticalLayout logLayout;

	private HorizontalSplitPanel hsplit;

	private Panel mainLayout;

	@AutoGenerated
	private Label label;

	private IntlibPagedTable monitorTable;

	private DateField dateFilter;

	private TextField nameFilter;

	private TextField userFilter;

	private ComboBox statusFilter;

	private ComboBox debugFilter;

	private IndexedContainer tableData;

	static String filter;

	private Long exeId;

	private String pipeName;

	int style = DateFormat.MEDIUM;

	static String[] visibleCols = new String[]{"date", "name", "user",
		"status", "debug", "obsolete", "actions", "report"};

	static String[] headers = new String[]{"Date", "Name", "User", "Status",
		"Debug", "Obsolete", "Actions", "Report"};

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */
	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public ExecutionMonitor() {
	}

	private MonitorTableFilter tableDataFilter = null;
	private DateFormat localDateFormat = null;

	@AutoGenerated
	private Panel buildMainLayout() {
		// common part: create layout

		mainLayout = new Panel("");

		hsplit = new HorizontalSplitPanel();
		mainLayout.setContent(hsplit);

		monitorTableLayout = new VerticalLayout();
		monitorTableLayout.setImmediate(true);
		monitorTableLayout.setMargin(true);
		monitorTableLayout.setSpacing(true);
		monitorTableLayout.setWidth("100%");
		monitorTableLayout.setHeight("100%");


		// top-level component properties

		setWidth("100%");
		setHeight("100%");

		filter = new String();

		GridLayout filtersLayout = new GridLayout(6, 1);
		filtersLayout.setWidth("100%");
		filtersLayout.setSpacing(true);

		dateFilter = new DateField();
		dateFilter.setImmediate(true);
		dateFilter.setCaption("Date form:");
		dateFilter.setWidth("110px");
		dateFilter.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub

				if (event.getProperty().getValue() != null) {
					DateFormat df = DateFormat.getDateInstance(style, getLocale());
					String s = df.format(event.getProperty().getValue());

					//				Format formatter = new SimpleDateFormat("dd.MM.yyyy");
					//				String s = formatter.format(event.getProperty().getValue().toString().toUpperCase(locale));

					tableDataFilter.setDateFilter(s);
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();

				} else {
					tableDataFilter.setDateFilter("");
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();
				}
			}
		});



		filtersLayout.addComponent(dateFilter, 0, 0);
		filtersLayout.setColumnExpandRatio(0, 0.08f);
		filtersLayout.setComponentAlignment(dateFilter, Alignment.BOTTOM_LEFT);

		if (tableDataFilter == null) {
			DateFormat df =DateFormat.getDateInstance(style, getLocale());
			tableDataFilter = new MonitorTableFilter(df);
		}
		nameFilter = new TextField();
		nameFilter.setImmediate(true);
		nameFilter.setCaption("Pipeline:");
		nameFilter.setInputPrompt("name of pipeline");
		nameFilter.setWidth("110px");
		nameFilter.setTextChangeEventMode(TextChangeEventMode.LAZY);
		nameFilter.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {

				tableDataFilter.setNameFilter(event.getText());
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);
				monitorTable.refreshRowCache();

			}
		});

		filtersLayout.addComponent(nameFilter, 1, 0);
		filtersLayout.setColumnExpandRatio(1, 0.08f);
		filtersLayout.setComponentAlignment(nameFilter, Alignment.BOTTOM_LEFT);

		userFilter = new TextField();
		userFilter.setCaption("User:");
		userFilter.setInputPrompt("user name");
		userFilter.setWidth("110px");
		userFilter.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {

				tableDataFilter.setUserFilter(event.getText());
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);
				monitorTable.refreshRowCache();

			}
		});

		filtersLayout.addComponent(userFilter, 2, 0);
		filtersLayout.setColumnExpandRatio(2, 0.08f);
		filtersLayout.setComponentAlignment(userFilter, Alignment.BOTTOM_LEFT);

		statusFilter = new ComboBox();
		//statusFilter.setNullSelectionAllowed(false);
		statusFilter.setImmediate(true);
		statusFilter.setCaption("Status:");
		statusFilter.setInputPrompt("execution status");
		statusFilter.setWidth("110px");
		statusFilter.setTextInputAllowed(false);

		statusFilter.addItem("CANCELLED");
		statusFilter.addItem("FAILED");
		statusFilter.addItem("FINISHED_SUCCESS");
		statusFilter.addItem("FINISHED_WARNING");
		statusFilter.addItem("RUNNING");
		statusFilter.addItem("SCHEDULED");
		statusFilter.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				Object a = event.getProperty().getValue();
				if (event.getProperty().getValue() != null) {
					tableDataFilter.setStatusFilter(event.getProperty()
							.getValue().toString());
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();

				} else {
					tableDataFilter.setStatusFilter("");
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();
				}
			}
		});
		filtersLayout.addComponent(statusFilter, 3, 0);
		filtersLayout.setColumnExpandRatio(3, 0.08f);
		filtersLayout.setComponentAlignment(statusFilter, Alignment.BOTTOM_LEFT);

		debugFilter = new ComboBox();
		//debugFilter.setNullSelectionAllowed(false);
		debugFilter.setImmediate(true);
		debugFilter.setCaption("Debug:");
		debugFilter.setInputPrompt("true/false");
		debugFilter.setWidth("110px");
		debugFilter.setTextInputAllowed(false);
		
		debugFilter.addItem("true");
		debugFilter.addItem("false");
		debugFilter.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub

				if (event.getProperty().getValue() != null) {
					tableDataFilter.setDebugFilter(event.getProperty()
							.getValue().toString());
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();
				} else {
					tableDataFilter.setDebugFilter("");
					tableData.removeAllContainerFilters();
					tableData.addContainerFilter(tableDataFilter);
					monitorTable.refreshRowCache();
				}
			}
		});
		filtersLayout.addComponent(debugFilter, 4, 0);
		filtersLayout.setColumnExpandRatio(4, 0.08f);
		filtersLayout.setComponentAlignment(debugFilter, Alignment.BOTTOM_LEFT);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("110px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub

				dateFilter.setValue(null);
				nameFilter.setValue("");
				userFilter.setValue("");
				statusFilter.setValue(null);
				debugFilter.setValue(null);
				dateFilter.setValue(null);
				tableDataFilter.setNameFilter("");
				tableDataFilter.setUserFilter("");

				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);

				monitorTable.refreshRowCache();

			}
		});
		filtersLayout.addComponent(buttonDeleteFilters, 5, 0);
		filtersLayout.setColumnExpandRatio(5, 0.7f);
		filtersLayout.setComponentAlignment(buttonDeleteFilters,
				Alignment.BOTTOM_RIGHT);
		monitorTableLayout.addComponent(filtersLayout);

		tableData = getTableData(App.getApp().getPipelines().getAllExecutions());


		monitorTable = new IntlibPagedTable();
		monitorTable.setSelectable(true);
		monitorTable.setContainerDataSource(tableData);
		monitorTable.setWidth("100%");
		monitorTable.setHeight("100%");
		monitorTable.setImmediate(true);
		monitorTable.setVisibleColumns(visibleCols); // Set visible columns
		monitorTable.setColumnHeaders(headers);
		//"date", "name", "user",
		//"status", "debug", "obsolete", "actions", "report"
//		monitorTable.setColumnExpandRatio("date", 2);
//		monitorTable.setColumnExpandRatio("name", 4);
//		monitorTable.setColumnExpandRatio("user", 2);
//		monitorTable.setColumnExpandRatio("status", 1);
//		monitorTable.setColumnExpandRatio("debug", 1);
//		monitorTable.setColumnExpandRatio("obsolete", 1);
//		monitorTable.setColumnExpandRatio("actions", 2);
//		monitorTable.setColumnExpandRatio("report", 2);

		monitorTable.addGeneratedColumn("status", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				ExecutionStatus type = (ExecutionStatus) source.getItem(itemId)
						.getItemProperty(columnId).getValue();
				ThemeResource img = null;
				switch (type) {
					case FINISHED_SUCCESS:
						img = new ThemeResource("icons/ok.png");
						break;
					case FINISHED_WARNING:
						img = new ThemeResource("icons/warning.png");
						break;
					case FAILED:
						img = new ThemeResource("icons/error.png");
						break;
					case RUNNING:
						img = new ThemeResource("icons/running.png");
						break;
					case SCHEDULED:
						img = new ThemeResource("icons/scheduled.png");
						break;
					case CANCELLED:
						img = new ThemeResource("icons/cancelled.png");
						break;
					default:
						//no icon
						break;
				}
				Embedded emb = new Embedded(type.name(), img);
				emb.setDescription(type.name());
				return emb;
			}
		});
		
		monitorTable.addGeneratedColumn("debug", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				String debugValue = (String) source.getItem(itemId).getItemProperty(columnId).getValue();
				boolean inDebug = debugValue.equals("true");
				Embedded emb = null;
				if(inDebug) {
					emb = new Embedded("True", new ThemeResource("icons/debug.png"));
					emb.setDescription("TRUE");
				}
				return emb;
			}
		});


		monitorTable.addGeneratedColumn("actions",
				new GenerateActionColumnMonitor(this));

		monitorTableLayout.addComponent(monitorTable);
		monitorTableLayout.addComponent(monitorTable.createControls());
		monitorTable.setPageLength(20);

		Button refreshButton = new Button("Refresh", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				refreshData();
				monitorTable.setVisibleColumns(visibleCols);
			}
		});
		monitorTableLayout.addComponent(refreshButton);



		hsplit.setFirstComponent(monitorTableLayout);
		hsplit.setSecondComponent(null);
		hsplit.setSplitPosition(100, Unit.PERCENTAGE);
		hsplit.setLocked(true);

		monitorTable.refreshRowCache();

		return mainLayout;
	}

	private void refreshData() {
		int page = monitorTable.getCurrentPage();
		tableData = getTableData(App.getApp().getPipelines().getAllExecutions());
		monitorTable.setContainerDataSource(tableData);
		monitorTable.setCurrentPage(page);
	}

	private VerticalLayout buildlogLayout() {

		logLayout = new VerticalLayout();
		logLayout.setImmediate(true);
		logLayout.setMargin(true);
		logLayout.setSpacing(true);
		logLayout.setWidth("100%");
		logLayout.setHeight("100%");

		PipelineExecution pipelineExec = App.getApp().getPipelines()
				.getExecution(exeId);
		DebuggingView debugView = new DebuggingView(pipelineExec, null,
				pipelineExec.isDebugging());
		logLayout.addComponent(debugView);
		logLayout.setExpandRatio(debugView, 1.0f);

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidth("100%");

		Button buttonClose = new Button();
		buttonClose.setCaption("Close");
		buttonClose.setHeight("25px");
		buttonClose.setWidth("100px");
		buttonClose
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				hsplit.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
				hsplit.setLocked(true);
			}
		});
		buttonBar.addComponent(buttonClose);
		buttonBar.setComponentAlignment(buttonClose, Alignment.BOTTOM_LEFT);

		Button buttonExport = new Button();
		buttonExport.setCaption("Export");
		buttonExport.setHeight("25px");
		buttonExport.setWidth("100px");
		buttonExport
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
			}
		});
		buttonBar.addComponent(buttonExport);
		buttonBar.setComponentAlignment(buttonExport, Alignment.BOTTOM_RIGHT);

		logLayout.addComponent(buttonBar);
		logLayout.setExpandRatio(buttonBar, 0);

		return logLayout;

	}

	public static IndexedContainer getTableData(List<PipelineExecution> data) {

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
			//		if (p.equals("exeid")==false)
			if (p.equals("status")) {
				result.addContainerProperty(p, ExecutionStatus.class, null);
			} else {
				result.addContainerProperty(p, String.class, "");
			}
		}
		result.addContainerProperty("exeid", Long.class, "");


		for (PipelineExecution item : data) {

			Object num = result.addItem();
			if (item.getStart() == null) {
				result.getContainerProperty(num, "date").setValue("");
			} else {

//				Format formatter = new SimpleDateFormat();
//			    String s = formatter.format(item.getStart());
				result.getContainerProperty(num, "date").setValue(item
						.getStart().toLocaleString());
			}


			result.getContainerProperty(num, "exeid").setValue(item.getId());
			result.getContainerProperty(num, "user").setValue(" ");
			result.getContainerProperty(num, "name").setValue(item.getPipeline()
					.getName());
			result.getContainerProperty(num, "status").setValue(item
					.getExecutionStatus());
			result.getContainerProperty(num, "debug").setValue((item
					.isDebugging()) ? "true" : "false");

		}

		return result;
	}

	private List<Record> buildStubMessageData() {
		List<Record> stubList = new ArrayList<>();
		/*		Record m = new Record(new Date(), RecordType.DPUINFO, null,
		 "Test message", "Long test message");
		 m.setId(1);
		 stubList.add(m);
		 Record m2 = new Record(new Date(), RecordType.DPUWARNING, null,
		 "Test warning", "Long test warning message");
		 m2.setId(2);
		 stubList.add(m2);*/

		return stubList;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Button senderButton = event.getButton();
		if (senderButton != null) {
			ActionButtonData senderData = (ActionButtonData) senderButton
					.getData();
			String caption = senderData.action;
			Object itemId = senderData.data;

			exeId = (Long) tableData.getContainerProperty(itemId, "exeid")
					.getValue();
			pipeName = (String) tableData.getContainerProperty(itemId, "name")
					.getValue();
			switch (caption) {
				case "stop":
					break;
				case "showlog":
					logLayout = buildlogLayout();
					hsplit.setSplitPosition(55, Unit.PERCENTAGE);
					hsplit.setSecondComponent(logLayout);
					//hsplit.setHeight("960px");
					hsplit.setLocked(false);
					break;
				case "debug":
					logLayout = buildlogLayout();
					hsplit.setSplitPosition(55, Unit.PERCENTAGE);
					hsplit.setSecondComponent(logLayout);
					//hsplit.setHeight("960px");
					hsplit.setLocked(false);
					break;
			}

		}
	}
}

class MonitorTableFilter implements Filter {

	/**
	 * Filters for Execution Monitor Table
	 */
	private static final long serialVersionUID = 1L;
	// private String needle;

	private Date dateFilter;

	private String userFilter;

	private String nameFilter;

	private String statusFilter;

	private String debugFilter;

	private DateFormat df = null;
	public MonitorTableFilter(DateFormat dateFormat) {
		this.df = dateFormat;
		// this.needle = needle.toLowerCase();
	}

	public void setNameFilter(String value) {
		this.nameFilter = value.toLowerCase();

	}

	public void setUserFilter(String value) {
		this.userFilter = value.toLowerCase();

	}

	public void setStatusFilter(String value) {
		this.statusFilter = value.toLowerCase();

	}

	public void setDebugFilter(String value) {
		this.debugFilter = value.toLowerCase();

	}

	public void setDateFilter(String value) {
		Date date = null;
		if (value != "") {
			try {
				date = df.parse(value);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.dateFilter = date;

	}

	private boolean stringIsSet(String value) {
		if (value != null && value.length() > 0) {
			return true;
		}
		return false;
	}
	
	private boolean dateIsSet(Date value) {
		if (value != null) {
			return true;
		}
		return false;
	}


	public boolean passesFilter(Object itemId, Item item) {

		if (stringIsSet(this.userFilter)) {
			String objectUser = ((String) item.getItemProperty("user")
					.getValue()).toLowerCase();
			if (objectUser.contains(this.userFilter) == false) {
				return false;
			}

		}
		if (stringIsSet(this.nameFilter)) {
			String objectName = ((String) item.getItemProperty("name")
					.getValue()).toLowerCase();
			if (objectName.contains(this.nameFilter) == false) {
				return false;
			}
		}

		if (stringIsSet(this.statusFilter)) {
			String objectStatus = ((ExecutionStatus) item.getItemProperty("status")
					.getValue()).name().toLowerCase();
			if (objectStatus.contains(this.statusFilter) == false) {
				return false;
			}
		}

		if (stringIsSet(this.debugFilter)) {
			String objectDebug = ((String) item.getItemProperty("debug")
					.getValue()).toLowerCase();
			if (objectDebug.contains(this.debugFilter) == false) {
				return false;
			}
		}

		if (dateIsSet(this.dateFilter)) {
			String objectDate = ((String) item.getItemProperty("date")
					.getValue()).toString();

			Date date = null;
			if (objectDate != "") {
				try {
					date = df.parse(objectDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				if (date.getTime() < this.dateFilter.getTime()) {
					return false;
				}
			} else
				return false;
		}

		return true;
	}

	public boolean appliesToProperty(Object id) {
		return true;
	}
}