package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;

import cz.cuni.mff.xrg.odcs.frontend.container.ValueItem;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PipelineEdit;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.ActionColumnGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Vaadin implementation for PipelineListView.
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
public class PipelineListViewImpl extends CustomComponent implements PipelineListPresenter.PipelineListView {

	private static final Logger LOG = LoggerFactory.getLogger(PipelineListViewImpl.class);
	
	private VerticalLayout mainLayout;
	private IntlibPagedTable tablePipelines;
	private Button btnCreatePipeline;
	
	@Autowired
	private Utils utils;

	public boolean isModified() {
		//There are no editable fields.
		return false;
	}

	private void buildPage(final PipelineListPresenter presenter) {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		HorizontalLayout topLine = new HorizontalLayout();
		topLine.setSpacing(true);

		btnCreatePipeline = new Button();
		btnCreatePipeline.setCaption("Create pipeline");
		btnCreatePipeline.setHeight("25px");
		btnCreatePipeline.setWidth("120px");
		btnCreatePipeline
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.navigateToEventHandler(PipelineEdit.class, "New");
			}
		});
		topLine.addComponent(btnCreatePipeline);

		Button buttonDeleteFilters = new Button();
		buttonDeleteFilters.setCaption("Clear Filters");
		buttonDeleteFilters.setHeight("25px");
		buttonDeleteFilters.setWidth("120px");
		buttonDeleteFilters
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				tablePipelines.resetFilters();
			}
		});
		topLine.addComponent(buttonDeleteFilters);

		mainLayout.addComponent(topLine);

		tablePipelines = new IntlibPagedTable();
		tablePipelines.setWidth("100%");

		mainLayout.addComponent(tablePipelines);
		mainLayout.addComponent(tablePipelines.createControls());
		tablePipelines.setPageLength(utils.getPageLength());

		// add column
		tablePipelines.setImmediate(true);
//		tablePipelines.addGeneratedColumn("description", new CustomTable.ColumnGenerator() {
//			@Override
//			public Object generateCell(CustomTable source, Object itemId, Object columnId) {
//				String description = (String) source.getItem(itemId).getItemProperty(columnId).getValue();
//				if (description.length() > App.MAX_TABLE_COLUMN_LENGTH) {
//					Label descriptionLabel = new Label(description.substring(0, App.MAX_TABLE_COLUMN_LENGTH - 3) + "...");
//					descriptionLabel.setDescription(description);
//					return descriptionLabel;
//				} else {
//					return description;
//				}
//			}
//		});
		tablePipelines.addGeneratedColumn("", 3, createColumnGenerator(presenter));
		tablePipelines.setVisibleColumns();

		tablePipelines.setFilterBarVisible(true);
		tablePipelines.setFilterLayout();
		tablePipelines.setSelectable(true);
		tablePipelines.addItemClickListener(
				new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (!tablePipelines.isSelected(event.getItemId())) {					
					ValueItem item = (ValueItem) event.getItem();
					final long pipelineId = item.getId();
					
					presenter.navigateToEventHandler(PipelineEdit.class, pipelineId);
				}
			}
		});

		setCompositionRoot(mainLayout);
	}

	private ActionColumnGenerator createColumnGenerator(final PipelineListPresenter presenter) {

		ActionColumnGenerator generator = new ActionColumnGenerator();
		// add action buttons

		generator.addButton("Edit", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				presenter.navigateToEventHandler(PipelineEdit.class, id);
			}
		});

		generator.addButton("Copy", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				presenter.copyEventHandler(id);
			}
		});

		generator.addButton("Delete", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(final long id) {
				presenter.deleteEventHandler(id);
			}
		});

		generator.addButton("Run", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				presenter.runEventHandler(id, false);
			}
		});

		generator.addButton("Debug", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				presenter.runEventHandler(id, true);
			}
		});

		generator.addButton("Schedule", "80px", new ActionColumnGenerator.Action() {
			@Override
			protected void action(long id) {
				presenter.scheduleEventHandler(id);
			}
		});

		return generator;
	}

	@Override
	public Object enter(PipelineListPresenter presenter) {
		buildPage(presenter);

		return this;
	}

	@Override
	public void setDisplay(PipelineListPresenter.PipelineListData dataObject) {
		tablePipelines.setContainerDataSource(dataObject.getContainer());
	}
}
