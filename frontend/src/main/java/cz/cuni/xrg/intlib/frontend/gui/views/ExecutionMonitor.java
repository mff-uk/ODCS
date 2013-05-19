package cz.cuni.xrg.intlib.frontend.gui.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.BrowserTable;
import cz.cuni.xrg.intlib.frontend.gui.components.QueryView;
import cz.cuni.xrg.intlib.frontend.gui.components.RecordsTable;

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
	private Table monitorTable;
	private DateField dateFilter;
	private TextField nameFilter;
	private TextField userFilter;
	private ComboBox statusFilter;
	private ComboBox DebugFilter;
	private IndexedContainer tableData;
	static String filter;
	private Integer exeId ;
	private String pipeName;

	static String[] visibleCols = new String[] { "date", "name", "user",
			"status", "debug", "obsolete", "actions", "report" };
	static String[] headers = new String[] {  "Date", "Name", "User", "Status",
			"Debug", "Obsolete", "Actions", "Report" };

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

		// label

		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<h1>ExecutionMonitor</h>");
		label.setContentMode(ContentMode.HTML);
		monitorTableLayout.addComponent(label);

		Label filtersLabel = new Label();
		filtersLabel.setCaption("Filters:");
		filtersLabel.setWidth("100px");
		monitorTableLayout.addComponent(filtersLabel);

		filter = new String();

		HorizontalLayout filtersLayout = new HorizontalLayout();
		filtersLayout.setWidth("100%");
		filtersLayout.setSpacing(true);

		dateFilter = new DateField();
		dateFilter.setDateFormat("yyyy.MM.dd");
		dateFilter.setWidth("90%");
		filtersLayout.addComponent(dateFilter);

		if (tableDataFilter == null) {
			tableDataFilter = new MonitorTableFilter();
		}
		nameFilter = new TextField();
		nameFilter.setInputPrompt("Pipeline Name Filter");
		nameFilter.setWidth("90%");
		nameFilter.setTextChangeEventMode(TextChangeEventMode.LAZY);
		nameFilter.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				tableDataFilter.setNameFilter(event.getText());

				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);

			}
		});

		filtersLayout.addComponent(nameFilter);

		userFilter = new TextField();
		userFilter.setInputPrompt("User Filter");
		userFilter.setWidth("90%");
		userFilter.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {

				tableDataFilter.setUserFilter(event.getText());
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(tableDataFilter);

			}
		});

		filtersLayout.addComponent(userFilter);

		statusFilter = new ComboBox();
		statusFilter.setInputPrompt("Status Filter");
		statusFilter.setWidth("90%");
		statusFilter.addItem("Running");
		statusFilter.addItem("Finished no errors");
		statusFilter.addItem("Finished with errors");
		filtersLayout.addComponent(statusFilter);

		DebugFilter = new ComboBox();
		DebugFilter.setInputPrompt("Debug Filter");
		DebugFilter.setWidth("90%");
		DebugFilter.addItem("Debug Yes");
		filtersLayout.addComponent(DebugFilter);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Delete Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("100%");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						dateFilter.setValue(null);
						nameFilter.setValue("");
						userFilter.setValue("");
						statusFilter.setValue(null);
						DebugFilter.setValue(null);
						tableData.removeAllContainerFilters();

					}
				});
		filtersLayout.addComponent(buttonDeleteFilters);

		monitorTableLayout.addComponent(filtersLayout);

		tableData = getTableData(App.getApp().getPipelines().getAllExecutions());
		
		
		monitorTable = new Table("");
		monitorTable.setSelectable(true);
		monitorTable.setContainerDataSource(tableData);
		monitorTable.setWidth("100%");
		monitorTable.setHeight("100%");
		monitorTable.setImmediate(true);
		monitorTable.setVisibleColumns(visibleCols); // Set visible columns
		monitorTable.setColumnHeaders(headers);
		// monitorTable.setPageLength(10);

		monitorTable.addGeneratedColumn("actions",
				new GenerateActionColumnMonitor(this));

		monitorTableLayout.addComponent(monitorTable);
		// mainLayout.addComponent(monitorTable.createControls());


		

		hsplit.setFirstComponent(monitorTableLayout);
		hsplit.setSecondComponent(null);
		hsplit.setSplitPosition(100, Sizeable.UNITS_PERCENTAGE);
		hsplit.setLocked(true);
		

		return mainLayout;
	}
	
	
	private VerticalLayout buildlogLayout() {
		
		logLayout = new VerticalLayout();
		logLayout.setImmediate(true);
		logLayout.setMargin(true);
		logLayout.setSpacing(true);
		logLayout.setWidth("100%");
		logLayout.setHeight("100%");

		GridLayout infoBar = new GridLayout();
		infoBar.setWidth("100%");
		infoBar.setSpacing(true);
		infoBar.setRows(2);
		infoBar.setColumns(5);
		
		infoBar.addComponent(new Label("Messages overview for: "), 0, 0);
		infoBar.addComponent(new Label("Pipeline: "), 1, 0);
		infoBar.addComponent(new Label("User: "), 1, 1);
		infoBar.addComponent(new Label("Start: "), 3, 0);
		infoBar.addComponent(new Label("End: "), 3, 1);
		

		
		Label pipeline = new Label();
		pipeline.setCaption(pipeName);
		infoBar.addComponent(pipeline,2,0);
						
		Label user = new Label();
		user.setCaption("");
		infoBar.addComponent(user, 2, 1);
		
		Label start = new Label();
		start.setCaption("");
		infoBar.addComponent(start, 4, 0);
		
		Label end = new Label();
		end.setCaption("");
		infoBar.addComponent(end, 4, 1);
		

		
		logLayout.addComponent(infoBar);
		
		List<Record> records = App.getDPUs().getAllDPURecords();
		
		List<Record> filteredRecords = new ArrayList<Record>();
		for (Record item : records){
			if (item.getExecution().getId() == exeId)
				filteredRecords.add(item);		
		} 
		
		RecordsTable executionRecordsTable = new RecordsTable(filteredRecords);
		executionRecordsTable.setWidth("100%");
		executionRecordsTable.setHeight("100px");
		
		
		logLayout.addComponent(executionRecordsTable);
		
		TabSheet tabs = new TabSheet();
	//	tabs.setHeight("500px");

		//Table with data

		BrowserTable browserTable = new BrowserTable(buildStubRDFData());
		tabs.addTab(browserTable, "Browse");


		//RecordsTable with different data source
		List<Record> fullRecords = App.getDPUs().getAllDPURecords();
		RecordsTable fullRecordsTable = new RecordsTable(fullRecords);
		fullRecordsTable.setWidth("100%");
		fullRecordsTable.setHeight("100%");
		Tab logTab = tabs.addTab(fullRecordsTable, "Log");

		//Query View
		QueryView queryView = new QueryView();
		tabs.addTab(queryView, "Query");
		tabs.setSelectedTab(logTab);


		logLayout.addComponent(tabs);

		
		
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
		buttonBar.setComponentAlignment(buttonClose,Alignment.BOTTOM_LEFT);

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
		buttonBar.setComponentAlignment(buttonExport,Alignment.BOTTOM_RIGHT);
		
		logLayout.addComponent(buttonBar);
		
		return logLayout;
		
	}
	
	

	public static IndexedContainer getTableData(List<PipelineExecution> data) {

		IndexedContainer result = new IndexedContainer();

		for (String p : visibleCols) {
	//		if (p.equals("exeid")==false)
				result.addContainerProperty(p, String.class, "");
		}
		result.addContainerProperty("exeid", Integer.class, "");

		
		for (PipelineExecution item : data)
		{

			Object num = result.addItem();
			result.getContainerProperty(num, "exeid").setValue(item.getId());
			result.getContainerProperty(num, "date").setValue(" ");
			result.getContainerProperty(num, "user").setValue(" ");
			result.getContainerProperty(num, "name").setValue(item.getPipeline().getName());
			result.getContainerProperty(num, "status").setValue(item.getExecutionStatus().toString());
			result.getContainerProperty(num, "debug").setValue((item.isDebugging())?"true":"false");
			
		}
 
		return result;
	}

	
	private List<RDFTriple> buildStubRDFData() {
		List<RDFTriple> rdfTripleList = new ArrayList<>();

		rdfTripleList.add(new RDFTriple(1, "rdf:Description", "rdf:about", "http://www.recshop.fake/cd/Empire Burlesque"));
		rdfTripleList.add(new RDFTriple(2, "rdf:Description", "cd:artist", "Bob Dylan"));
		rdfTripleList.add(new RDFTriple(3, "rdf:Description", "cd:country", "USA"));
		rdfTripleList.add(new RDFTriple(4, "rdf:Description", "cd:company", "Columbia"));
		rdfTripleList.add(new RDFTriple(5, "rdf:Description", "cd:price", "10.90"));
		rdfTripleList.add(new RDFTriple(6, "rdf:Description", "cd:year", "1985"));

		return rdfTripleList;
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
			ActionButtonData senderData = (ActionButtonData)senderButton.getData();
			String caption = senderData.action;
			Object itemId = senderData.data;
			
			exeId = (Integer) tableData.getContainerProperty(itemId,"exeid").getValue();
			pipeName = (String) tableData.getContainerProperty(itemId, "name").getValue();
			
			if (caption.equals("stop")) {
			} else if (caption.equals("showlog")) {
				
				logLayout = buildlogLayout();
				hsplit.setSplitPosition(55, Sizeable.UNITS_PERCENTAGE);
				hsplit.setSecondComponent(logLayout);
				hsplit.setLocked(false);
				

				
			} else if (caption.equals("debug")) {
			}

		}
	}

}

class MonitorTableFilter implements Filter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private String needle;
	private String userFilter;
	private String nameFilter;

	public MonitorTableFilter() {
		// this.needle = needle.toLowerCase();
	}

	public void setNameFilter(String value) {
		this.nameFilter = value.toLowerCase();

	}

	public void setUserFilter(String value) {
		this.userFilter = value.toLowerCase();

	}

	private boolean stringIsSet(String value) {
		if (value != null && value.length() > 0)
			return true;
		return false;
	}

	public boolean passesFilter(Object itemId, Item item) {

		if (stringIsSet(this.userFilter)) {
			String objectUser = ((String) item.getItemProperty("user")
					.getValue()).toLowerCase();
			if (objectUser.contains(this.userFilter) == false)
				return false;

		}
		if (stringIsSet(this.nameFilter)) {
			String objectUser = ((String) item.getItemProperty("name")
					.getValue()).toLowerCase();
			if (objectUser.contains(this.nameFilter) == false)
				return false;
		}

		return true;
	}

	public boolean appliesToProperty(Object id) {
		return true;
	}
}
