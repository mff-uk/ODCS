/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tepi.filtertable.paged.PagedFilterTable;
import org.tepi.filtertable.paged.PagedTableChangeEvent;
import org.vaadin.dialogs.ConfirmDialog;

import ru.xpoft.vaadin.VaadinView;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import cz.cuni.mff.xrg.odcs.commons.app.auth.EntityPermissions;
import cz.cuni.mff.xrg.odcs.commons.app.auth.PermissionUtils;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.LocaleHolder;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleType;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibFilterDecorator;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ParametersHandler;

/**
 * GUI for Scheduler page which opens from the main menu. Contains table with
 * scheduler rules and button for scheduler rule creation.
 *
 * @author Maria Kukhar
 */
@org.springframework.stereotype.Component
@Scope("session")
@VaadinView(Scheduler.NAME)
@Address(url = "Scheduler")
public class Scheduler extends ViewComponent implements PostLogoutCleaner, Presenter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(
            Scheduler.class);

    /**
     * View name.
     */
    public static final String NAME = "Scheduler";

    /**
     * Column widths for schedules table.
     */
    private static final int COLUMN_STATUS_WIDTH = 39;

    private static final int COLUMN_ACTIONS_WIDTH = 160;

    private static final int COLUMN_TIME_WIDTH = 115;

    private static final int COLUMN_DURATION_WIDTH = 170;

    private static final int COLUMN_SCHEDULED_BY_WIDTH = 250;

    private VerticalLayout mainLayout;

    /**
     * Table contains rules of pipeline scheduling.
     */
    private IntlibPagedTable schedulerTable;

    private IndexedContainer tableData;

    static String[] visibleCols = new String[] { "commands", "status", "pipeline", "rule",
            "last", "next", "duration", "scheduledBy" };

    static String[] headers = new String[] { Messages.getString("Scheduler.actions"), Messages.getString("Scheduler.status"), Messages.getString("Scheduler.pipeline"), Messages.getString("Scheduler.rule"),
            Messages.getString("Scheduler.last"), Messages.getString("Scheduler.next"), Messages.getString("Scheduler.last.runTime"),
            Messages.getString("Scheduler.scheduled.by") };

    int style = DateFormat.MEDIUM;

    static String filter;

    private Schedule scheduleDel;

    private Date lastLoad = new Date(0L);

    private RefreshManager refreshManager;

    @Autowired
    private SchedulePipeline schedulePipeline;

    @Autowired
    private ScheduleFacade scheduleFacade;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private PermissionUtils permissionUtils;

    @Autowired
    private Utils utils;

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    private boolean isMainLayoutInitialized = false;

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     * The constructor will not be automatically regenerated by the visual
     * editor.
     */
    public Scheduler() {
    }

    @Override
    public boolean isModified() {
        //There are no editable fields.
        return false;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        if (!isMainLayoutInitialized) {
            buildMainLayout();
            isMainLayoutInitialized = true;
        }
        setCompositionRoot(mainLayout);

        refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
        refreshManager.addListener(RefreshManager.SCHEDULER, new Refresher.RefreshListener() {
            private long lastRefreshFinished = 0;

            @Override
            public void refresh(Refresher source) {
                if (new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
                    boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
                    if (hasModifiedExecutions) {
                        lastLoad = new Date();
                        refreshData();
                    }
                    LOG.debug("Scheduler refreshed.");
                    lastRefreshFinished = new Date().getTime();
                }
            }
        });
        refreshManager.triggerRefresh();
        setParameters(ParametersHandler.getConfiguration(event.getParameters()));
    }

    /**
     * Builds main layout contains table with created scheduling pipeline rules.
     *
     * @return mainLayout VerticalLayout with all components of Scheduler page.
     */
    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");

        // top-level component properties
        setWidth("100%");

        //Layout for buttons Add new scheduling rule and Clear Filters on the top.
        HorizontalLayout topLine = new HorizontalLayout();
        topLine.setSpacing(true);
        //topLine.setWidth(100, Unit.PERCENTAGE);

        Button addRuleButton = new Button();
        addRuleButton.setCaption(Messages.getString("Scheduler.add.rule"));
        addRuleButton.addStyleName("v-button-primary");
        addRuleButton.setVisible(this.permissionUtils.hasUserAuthority("scheduleRule.create"));
        addRuleButton
                .addClickListener(new com.vaadin.ui.Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        // open scheduler dialog
                        showSchedulePipeline(null, null);
                    }
                });
        topLine.addComponent(addRuleButton);
        //topLine.setComponentAlignment(addRuleButton, Alignment.MIDDLE_RIGHT);

        Button buttonDeleteFilters = new Button();
        buttonDeleteFilters.setCaption(Messages.getString("Scheduler.clear.filters"));
        buttonDeleteFilters.addStyleName("v-button-primary");
        buttonDeleteFilters.setHeight("25px");
        buttonDeleteFilters.setWidth("110px");
        buttonDeleteFilters
                .addClickListener(new com.vaadin.ui.Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        schedulerTable.resetFilters();
                        schedulerTable.setFilterFieldVisible("commands", false);
                        schedulerTable.setFilterFieldVisible("duration", false);
                    }
                });
        topLine.addComponent(buttonDeleteFilters);
        //topLine.setComponentAlignment(buttonDeleteFilters, Alignment.MIDDLE_RIGHT);

//		Label topLineFiller = new Label();
//		topLine.addComponentAsFirst(topLineFiller);
//		topLine.setExpandRatio(topLineFiller, 1.0f);
        mainLayout.addComponent(topLine);

        tableData = getTableData(scheduleFacade.getAllSchedules());

        //table with schedule rules records
        schedulerTable = new IntlibPagedTable();
        schedulerTable.setSelectable(true);
        schedulerTable.setContainerDataSource(tableData);
        schedulerTable.setWidth("100%");
        schedulerTable.setHeight("100%");
        schedulerTable.setImmediate(true);
        schedulerTable.setFilterBarVisible(true);
        schedulerTable.setColumnCollapsingAllowed(true);
        //Commands column. Contains commands buttons: Enable/Disable, Edit, Delete
        schedulerTable.addGeneratedColumn("commands",
                new actionColumnGenerator());
        schedulerTable.setColumnWidth("commands", COLUMN_ACTIONS_WIDTH);
        schedulerTable.setColumnWidth("status", COLUMN_STATUS_WIDTH);
        schedulerTable.setColumnWidth("last", COLUMN_TIME_WIDTH);
        schedulerTable.setColumnWidth("next", COLUMN_TIME_WIDTH);
        schedulerTable.setColumnWidth("duration", COLUMN_DURATION_WIDTH);
        schedulerTable.setColumnWidth("scheduledBy", COLUMN_SCHEDULED_BY_WIDTH);
        schedulerTable.setColumnAlignment("status", CustomTable.Align.CENTER);

        //Debug column. Contains debug icons.
        schedulerTable.addGeneratedColumn("status", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, Object itemId,
                    Object columnId) {
                boolean isEnabled = (boolean) source.getItem(itemId).getItemProperty(columnId).getValue();
                ThemeResource img = new ThemeResource(isEnabled ? "icons/ok.png" : "icons/error.png");
                String description = isEnabled ? Messages.getString("Scheduler.image.enabled") : Messages.getString("Scheduler.image.disabled");
                Embedded emb = new Embedded(description, img);
                emb.setDescription(description);
                return emb;
            }
        });
        schedulerTable.setFilterDecorator(new filterDecorator());
        schedulerTable.setFilterFieldVisible("commands", false);
        schedulerTable.setFilterFieldVisible("duration", false);
        schedulerTable.setVisibleColumns((Object[]) visibleCols);
        schedulerTable.setColumnHeaders(headers);
        mainLayout.addComponent(schedulerTable);
        mainLayout.addComponent(schedulerTable.createControls());
        schedulerTable.setPageLength(utils.getPageLength());
        schedulerTable.addItemClickListener(
                new ItemClickEvent.ItemClickListener() {
                    @Override
                    public void itemClick(ItemClickEvent event) {
                        if (!schedulerTable.isSelected(event.getItemId())) {
                            try {
                                Long schId = Long.parseLong(event.getItem().getItemProperty("schid").getValue().toString());
                                showSchedulePipeline(schId, null);
                                changeURI(schId);
                            } catch (NumberFormatException e) {
                                log.error(e.getLocalizedMessage());
                                // cannot cast String to Long probably
                                // sorry no action ..
                            }
                        }
                    }
                });
        schedulerTable.addListener(new PagedFilterTable.PageChangeListener() {
            @Override
            public void pageChanged(PagedTableChangeEvent event) {
                int newPageNumber = event.getCurrentPage();
                pageChangedHandler(newPageNumber);
            }
        });

        return mainLayout;
    }

    /**
     * Container with data for table {@link #schedulerTable}.
     *
     * @param data
     *            List of {@link Schedule}.
     * @return result IndexedContainer with data for {@link #schedulerTable}.
     */
    @SuppressWarnings("unchecked")
    public IndexedContainer getTableData(List<Schedule> data) {

        IndexedContainer result = new IndexedContainer();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, LocaleHolder.getLocale());

        for (String p : visibleCols) {
            // setting type of columns
            switch (p) {
                case "last":
                case "next":
                    result.addContainerProperty(p, Date.class, null);
                    break;
                case "status":
                    result.addContainerProperty(p, Boolean.class, false);
                    break;
                default:
                    result.addContainerProperty(p, String.class, "");
                    break;
            }

        }
        result.addContainerProperty("schid", Long.class, "");

        for (Schedule item : data) {

            Object id = result.addItem();

            if (item.getFirstExecution() == null) {
                result.getContainerProperty(id, "next").setValue(null);
            } else {
                result.getContainerProperty(id, "next").setValue(
                        item.getNextExecutionTimeInfo());
            }

            if (item.getLastExecution() == null) {
                result.getContainerProperty(id, "last").setValue(null);
            } else {

                result.getContainerProperty(id, "last").setValue(
                        item.getLastExecution());
            }

            result.getContainerProperty(id, "status").setValue(item.isEnabled());
            result.getContainerProperty(id, "scheduledBy").setValue(getScheduledByDisplayName(item));

            if (item.getType().equals(ScheduleType.PERIODICALLY)) {

                if (item.isJustOnce()) {
                    result.getContainerProperty(id, "rule").setValue(
                            Messages.getString("Scheduler.run.on", df.format(item.getFirstExecution())));
                } else {
                    if (item.getPeriod().equals(1)) {
                        result.getContainerProperty(id, "rule").setValue(
                                Messages.getString("Scheduler.run.on", df.format(item.getFirstExecution()))
                                        + Messages.getString("Scheduler.and.repeat")
                                        + " "
                                        + Messages.getString("Scheduler." + item.getPeriodUnit().toString()
                                                .toLowerCase() + ".one"));
                    } else if (item.getPeriod() <= 4) {
                        result.getContainerProperty(id, "rule").setValue(
                                Messages.getString("Scheduler.run.on", df.format(item.getFirstExecution()))
                                        + Messages.getString("Scheduler.and.repeat")
                                        + " "
                                        + item.getPeriod().toString()
                                        + " "
                                        + Messages.getString("Scheduler." + item.getPeriodUnit().toString()
                                                .toLowerCase() + ".lte.four"));
                    } else {
                        result.getContainerProperty(id, "rule").setValue(
                                Messages.getString("Scheduler.run.on", df.format(item.getFirstExecution()))
                                        + Messages.getString("Scheduler.and.repeat")
                                        + " "
                                        + item.getPeriod().toString()
                                        + " "
                                        + Messages.getString("Scheduler." + item.getPeriodUnit().toString()
                                                .toLowerCase() + ".more"));
                    }
                }
            } else {

                Set<Pipeline> after = item.getAfterPipelines();
                String afterPipelines = "";
                after.size();
                int i = 0;
                for (Pipeline afteritem : after) {
                    i++;
                    if (i < after.size()) {
                        afterPipelines = afterPipelines + afteritem.getName() + ", ";
                    } else {
                        afterPipelines = afterPipelines + afteritem.getName() + ". ";
                    }
                }
                if (after.size() > 1) {
                    result.getContainerProperty(id, "rule").setValue(Messages.getString("Scheduler.run.after.pipelines") + afterPipelines);
                } else {
                    result.getContainerProperty(id, "rule").setValue(Messages.getString("Scheduler.run.after.pipeline") + afterPipelines);
                }
            }

            result.getContainerProperty(id, "schid").setValue(item.getId());
//			if (item.getOwner() == null) {
//				result.getContainerProperty(id, "user").setValue(" ");
//			} else {
//				result.getContainerProperty(id, "user").setValue(item.getOwner().getUsername());
//			}
            String pipeline = item.getPipeline().getName();
            pipeline = pipeline.length() > 158 ? pipeline.substring(0, 156) + "..." : pipeline;
            result.getContainerProperty(id, "pipeline").setValue(pipeline);
            String description = StringUtils.abbreviate(item.getDescription(), Utils.getColumnMaxLenght());
//			result.getContainerProperty(id, "description").setValue(description);

            PipelineExecution exec = pipelineFacade.getLastExec(item, PipelineExecutionStatus.FINISHED);
            result.getContainerProperty(id, "duration").setValue(DecorationHelper.getDuration(exec));
        }

        return result;

    }

    private static String getScheduledByDisplayName(Schedule schedule) {
        String ownerDisplayName = (schedule.getOwner().getFullName() != null && !schedule.getOwner().getFullName().equals(""))
                ? schedule.getOwner().getFullName() : schedule.getOwner().getUsername();
        if (schedule.getActor() != null) {
            return ownerDisplayName + " (" + schedule.getActor().getName() + ")";
        }
        return ownerDisplayName;
    }

    /**
     * Calls for refresh table {@link #schedulerTable}.
     */
    private void refreshData() {
        int page = schedulerTable.getCurrentPage();
        tableData = getTableData(scheduleFacade.getAllSchedules());
        schedulerTable.setContainerDataSource(tableData);
        schedulerTable.setCurrentPage(page);
        schedulerTable.setVisibleColumns((Object[]) visibleCols);
        schedulerTable.setFilterFieldVisible("commands", false);
        schedulerTable.setFilterFieldVisible("duration", false);

    }

    /**
     * Shows dialog for scheduling pipeline with given scheduling rule.
     *
     * @param id
     *            Id of schedule to show.
     */
    private void showSchedulePipeline(Long id, Long pipelineId) {

        // open scheduler dialog
        if (!schedulePipeline.isInitialized()) {
            schedulePipeline.init();
            schedulePipeline.addCloseListener(new CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    refreshData();
                }
            });
        }

        Schedule schedule = null;
        if (id != null) {
            schedule = scheduleFacade.getSchedule(id);
        }
        schedulePipeline.setSelectedSchedule(schedule);
        schedulePipeline.enableComboPipeline();
        if (pipelineId != null) {
            schedulePipeline.setPipeline(pipelineId);
        }

        if (!UI.getCurrent().getWindows().contains(schedulePipeline)) {
            if (schedulePipeline.isAttached()) {
                schedulePipeline.detach();
                schedulePipeline.close();
            }
            refreshManager.removeListener(RefreshManager.SCHEDULER);
            UI.getCurrent().addWindow(schedulePipeline);
        }
    }

    /**
     * Generate column "commands" in the table {@link #schedulerTable}.
     *
     * @author Maria Kukhar
     */
    class actionColumnGenerator implements CustomTable.ColumnGenerator {

        @Override
        public Object generateCell(final CustomTable source, final Object itemId, Object columnId) {
            final Long schId = Long.parseLong(tableData.getContainerProperty(itemId, "schid").getValue().toString());
            Schedule schedule = scheduleFacade.getSchedule(schId);
            Property propStatus = source.getItem(itemId).getItemProperty("status");

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);

            if (propStatus.getType().equals(Boolean.class)) {
                boolean testStatus = (Boolean) propStatus.getValue();
                //If item in the scheduler table has Disabled status, then for that item will be shown
                //Enable button
                if (!testStatus) {
                    Button enableButton = new Button();
                    enableButton.setDescription(Messages.getString("Scheduler.button.enable"));
                    enableButton.setIcon(new ThemeResource("icons/ok.png"));
                    enableButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            setScheduleEnabled(schId, true);
                            refreshData();
                        }
                    });
                    if (canEdit(schedule)) {
                        layout.addComponent(enableButton);
                    }

                } //If item in the scheduler table has Enabled status, then for that item will be shown
                  //Disable button
                else {
                    Button disableButton = new Button();
                    disableButton.setDescription(Messages.getString("Scheduler.button.disable"));
                    disableButton.addStyleName("small_button");
                    disableButton.setIcon(new ThemeResource("icons/error.png"));
                    disableButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            setScheduleEnabled(schId, false);
                            refreshData();
                        }
                    });
                    if (canEdit(schedule)) {
                        layout.addComponent(disableButton);
                    }
                }

            }
            //Edit button. Opens the window for editing given scheduling rule.
            Button editButton = new Button();
            editButton.setDescription(Messages.getString("Scheduler.edit"));
            editButton.addStyleName("small_button");
            editButton.setIcon(new ThemeResource("icons/gear.png"));
            editButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    showSchedulePipeline(schId, null);
                    changeURI(schId);
                }
            });
            if (canEdit(schedule)) {
                layout.addComponent(editButton);
            }

            //Delete button. Delete scheduling rule from the table.
            Button deleteButton = new Button();
            deleteButton.setDescription(Messages.getString("Scheduler.delete"));
            deleteButton.addStyleName("small_button");
            deleteButton.setIcon(new ThemeResource("icons/trash.png"));
            deleteButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    scheduleDel = scheduleFacade.getSchedule(schId);

                    //open confirmation dialog
                    ConfirmDialog.show(UI.getCurrent(), Messages.getString("Scheduler.delete.scheduling"),
                            Messages.getString("Scheduler.delete.scheduling.description", scheduleDel.getPipeline().getName().toString()), Messages.getString("Scheduler.delete.scheduling.deleteButton"), Messages.getString("Scheduler.delete.scheduling.calcelButton"),
                            new ConfirmDialog.Listener() {
                                private static final long serialVersionUID = 1L;

                                @Override
                                public void onClose(ConfirmDialog cd) {
                                    if (cd.isConfirmed()) {
                                        scheduleFacade.delete(scheduleDel);
                                        refreshData();
                                    }
                                }
                            });
                }
            });
            if (canDelete(schedule)) {
                layout.addComponent(deleteButton);
            }

            return layout;
        }
    }

    private void setScheduleEnabled(Long schId, boolean enabled) {
        Schedule schedule = scheduleFacade.getSchedule(schId);
        schedule.setEnabled(enabled);
        scheduleFacade.save(schedule);
    }

    boolean canDelete(Schedule schedule) {
        return this.permissionUtils.hasPermission(schedule, EntityPermissions.SCHEDULE_RULE_DELETE);
    }

    boolean canEdit(Schedule schedule) {
        return this.permissionUtils.hasPermission(schedule, EntityPermissions.SCHEDULE_RULE_EDIT);
    }

    private class filterDecorator extends IntlibFilterDecorator {

        @Override
        public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
            if ("status".equals(propertyId)) {
                ThemeResource img = new ThemeResource(value ? "icons/ok.png" : "icons/error.png");
                return img;
            } else {
                return super.getBooleanFilterIcon(propertyId, value);
            }
        }

        @Override
        public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
            if (value) {
                return Messages.getString("Scheduler.enabled");
            } else {
                return Messages.getString("Scheduler.disabled");
            }
        }
    }

    @Override
    public void doAfterLogout() {
        isMainLayoutInitialized = false;
    }

    private void changeURI(Long scheduleId) {
        String uriFragment = Page.getCurrent().getUriFragment();
        ParametersHandler handler = new ParametersHandler(uriFragment);
        handler.addParameter("schedule", "" + scheduleId);
        ((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
    }

    @Override
    public Object enter() {
        if (!isMainLayoutInitialized) {
            buildMainLayout();
            isMainLayoutInitialized = true;
        }
        setCompositionRoot(mainLayout);

        refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
        refreshManager.addListener(RefreshManager.SCHEDULER, new Refresher.RefreshListener() {
            private long lastRefreshFinished = 0;

            @Override
            public void refresh(Refresher source) {
                if (new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
                    boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
                    if (hasModifiedExecutions) {
                        lastLoad = new Date();
                        refreshData();
                    }
                    LOG.debug("Scheduler refreshed.");
                    lastRefreshFinished = new Date().getTime();
                }
            }
        });
        refreshManager.triggerRefresh();

        return this;
    }

    @Override
    public void setParameters(Object configuration) {
        if (configuration != null && Map.class.isAssignableFrom(configuration.getClass())) {
            schedulerTable.resetFilters();
            int pageNumber = 0;
            Long pipelineId = null;
            Map<String, String> config = (Map<String, String>) configuration;
            Long scheduleId = null;
            boolean showNewScheduleForm = false;
            for (Map.Entry<String, String> entry : config.entrySet()) {
                if (entry.getKey().contains("New/")) {
                    showNewScheduleForm = true;
                }
                switch (entry.getKey()) {
                    case "schedule":
                    case "New/schedule":
                        scheduleId = Long.parseLong(entry.getValue());
                        schedulerTable.select(scheduleId);
                        changeURI(scheduleId);
                        showDebugEventHandler(scheduleId);
                        break;
                    case "page":
                    case "New/page":
                        pageNumber = Integer.parseInt(entry.getValue());
                        break;
                    case "pipeline":
                    case "New/pipeline":
                        pipelineId = Long.parseLong(entry.getValue());
                        Pipeline pipeline = pipelineFacade.getPipeline(pipelineId);
                        List<Schedule> pipelineSchedules = scheduleFacade.getSchedulesFor(pipeline);
                        schedulerTable.removeAllItems();
                        tableData = getTableData(pipelineSchedules);
                        schedulerTable.setContainerDataSource(tableData);
                        schedulerTable.setCurrentPage(pageNumber);
                        schedulerTable.setVisibleColumns((Object[]) visibleCols);
                        schedulerTable.setFilterFieldVisible("commands", false);
                        schedulerTable.setFilterFieldVisible("duration", false);
                        LOG.debug("Scheduler refreshed.");
                        refreshManager.triggerRefresh();
                        break;
                    default:
                        schedulerTable.setFilterFieldValue(entry.getKey(), entry.getValue());
                        break;
                }
            }
            if (showNewScheduleForm) {
                showSchedulePipeline(null, pipelineId);
                showNewScheduleForm = false;
            }
            pageNumber = scheduleId == null ? pageNumber : getExecPage(scheduleId);
            if (pageNumber != 0) {
                //Page number is set as last, because filtering automatically moves table to first page.
                schedulerTable.setCurrentPage(pageNumber);
            }
        }
    }

    public int getExecPage(Long scheduleId) {
        Iterator<?> it = schedulerTable.getItemIds().iterator();
        int index = 0;
        while (it.hasNext()) {
            Long id = ((Integer) it.next()).longValue();
            if (id.equals(scheduleId)) {
                return (index / schedulerTable.getPageLength()) + 1; // pages are from 1
            }
            index++;
        }
        return 0;
    }

    public void showDebugEventHandler(long scheduleId) {
        if (!schedulerTable.getItemIds().contains((new Long(scheduleId)).intValue())) {
            return;
        }
        Schedule schedule = scheduleFacade.getSchedule(scheduleId);
        if (schedule == null) {
            Notification.show(Messages.getString("Scheduler.0", scheduleId), Notification.Type.ERROR_MESSAGE);
            return;
        }
        showSchedulePipeline(scheduleId, null);
    }

    public void pageChangedHandler(Integer newPageNumber) {
        String uriFragment = Page.getCurrent().getUriFragment();
        ParametersHandler handler = new ParametersHandler(uriFragment);
        handler.addParameter("page", newPageNumber.toString());
        ((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
    }

}
