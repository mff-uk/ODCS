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
package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.auth.EntityPermissions;
import cz.cuni.mff.xrg.odcs.commons.app.auth.PermissionUtils;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.DbMessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionViewAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.MessageRecordAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.ContainerSourceBase;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.db.DbCachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.PostLogoutCleaner;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ParametersHandler;
import eu.unifiedviews.commons.dao.view.ExecutionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.datefilter.DateInterval;
import org.tepi.filtertable.numberfilter.NumberInterval;

import java.util.Date;
import java.util.Map;

/**
 * Presenter for {@link ExecutionListPresenter}.
 * 
 * @author Petyr
 */
@Component
@Scope("session")
@Address(url = "ExecutionList")
public class ExecutionListPresenterImpl implements ExecutionListPresenter, PostLogoutCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionListPresenterImpl.class);

    @Autowired
    private DbMessageRecord dbMessageRecord;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private PipelineHelper pipelineHelper;

    @Autowired
    private ExecutionListView view;

    @Autowired
    private Utils utils;

    @Autowired
    private PermissionUtils permissionUtils;

    private ExecutionListData dataObject;
    private RefreshManager refreshManager;
    private Date lastLoad = new Date(0L);
    private ClassNavigator navigator;
    private boolean isInitialized = false;
    private ContainerSourceBase<ExecutionView> executionViewSource;

    @Override
    public Object enter() {
        if (isInitialized) {
            navigator = ((AppEntry) UI.getCurrent()).getNavigation();
            addRefreshManager();
            return view.enter(this);
        }

        navigator = ((AppEntry) UI.getCurrent()).getNavigation();
        executionViewSource = new ContainerSourceBase<>(
            pipelineHelper.getExecutionViews(),
            new ExecutionViewAccessor());

        ReadOnlyContainer c = new ReadOnlyContainer<>(executionViewSource);
        c.sort(new Object[] { "id" }, new boolean[] { false });
        dataObject = new ExecutionListData(c);

        // prepare view
        Object viewObject = view.enter(this);
        addRefreshManager();

        // set data object
        view.setDisplay(dataObject);

        isInitialized = true;

        // return main component
        return viewObject;
    }

    private void addRefreshManager() {
        refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
        refreshManager.addListener(RefreshManager.EXECUTION_MONITOR, new Refresher.RefreshListener() {
            private long lastRefreshFinished = 0;

            @Override
            public void refresh(Refresher source) {
                if (new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
                    refreshEventHandler();
                    LOG.debug("ExecutionMonitor refreshed.");
                    lastRefreshFinished = new Date().getTime();
                }
            }
        });
        refreshManager.triggerRefresh();
    }

    @Override
    public void setParameters(Object configuration) {
        if (configuration != null && Map.class.isAssignableFrom(configuration.getClass())) {
            view.resetFilters();
            int pageNumber = 0;
            Map<String, String> config = (Map<String, String>) configuration;
            Long execId = null;
            for (Map.Entry<String, String> entry : config.entrySet()) {
                switch (entry.getKey()) {
                    case "exec":
                        execId = Long.parseLong(entry.getValue());
                        view.setSelectedRow(execId);
                        showDebugEventHandler(execId);
                        break;
                    case "page":
                        pageNumber = Integer.parseInt(entry.getValue());
                        break;
                    case "id":
                        view.setFilter(entry.getKey(), ParametersHandler.getInterval(entry.getValue()));
                        break;
                    case "status":
                        view.setFilter(entry.getKey(), PipelineExecutionStatus.valueOf(entry.getValue()));
                        break;
                    case "isDebugging":
                    case "schedule":
                        view.setFilter(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
                        break;
                    case "start":
                        view.setFilter(entry.getKey(), ParametersHandler.getDateInterval(entry.getValue()));
                        break;
                    default:
                        view.setFilter(entry.getKey(), entry.getValue());
                        break;
                }
            }
            pageNumber = execId == null ? pageNumber : view.getExecPage(execId);
            if (pageNumber != 0) {
                //Page number is set as last, because filtering automatically moves table to first page.
                view.setPage(pageNumber);
            }
        }
    }

    @Override
    public void refreshEventHandler() {
        boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad)
                || (executionViewSource.size() > 0 &&
                pipelineFacade.hasDeletedExecutions(executionViewSource.getItemIds(0, executionViewSource.size())));
        view.refresh(hasModifiedExecutions);
        if (hasModifiedExecutions) {
            lastLoad = new Date();
            executionViewSource.setDataItems(pipelineHelper.getExecutionViews());
            dataObject.getContainer().refresh();
        }
    }

    @Override
    public boolean canStopExecution(long executionId) {
        PipelineExecution exec =
                getLightExecution(executionId);
        return permissionUtils.hasPermission(exec, EntityPermissions.PIPELINE_EXECUTION_STOP);
    }

    @Override
    public void stopEventHandler(long executionId) {
        pipelineFacade.stopExecution(getLightExecution(executionId));
        refreshEventHandler();
    }

    @Override
    public void showDebugEventHandler(long executionId) {
        if (!view.hasExecution(executionId)) {
            return;
        }
        PipelineExecution exec = getLightExecution(executionId);
        if (exec == null) {
            Notification.show(Messages.getString("ExecutionListPresenterImpl.0", executionId), Notification.Type.ERROR_MESSAGE);
            return;
        }
        view.showExecutionDetail(exec, new ExecutionDetailData(getMessageDataSource()));
    }

    @Override
    public void runEventHandler(long executionId) {
        pipelineHelper.runPipeline(getLightExecution(executionId).getPipeline(), false);
        refreshEventHandler();
    }

    @Override
    public void debugEventHandler(long executionId) {
        PipelineExecution exec = pipelineHelper.runPipeline(getLightExecution(executionId).getPipeline(), true);
        if (exec != null) {
            refreshEventHandler();
            view.setSelectedRow(exec.getId());
            view.showExecutionDetail(exec, new ExecutionDetailData(getMessageDataSource()));
        }
    }

    /**
     * Get light copy of execution.
     * 
     * @param executionId
     * @return light copy of execution
     */
    private PipelineExecution getLightExecution(long executionId) {
        return pipelineFacade.getExecution(executionId);
    }

    private ReadOnlyContainer<MessageRecord> getMessageDataSource() {
        return new ReadOnlyContainer<>(
                new DbCachedSource<>(dbMessageRecord,
                        new MessageRecordAccessor(), utils.getPageLength()));
    }

    @Override
    public void stopRefreshEventHandler() {
        refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
    }

    @Override
    public void startDebugRefreshEventHandler(DebuggingView debugView, PipelineExecution execution) {
        refreshManager.addListener(RefreshManager.DEBUGGINGVIEW,
                RefreshManager.getDebugRefresher(debugView, execution, pipelineFacade));
    }

    @Override
    public void pageChangedHandler(Integer newPageNumber) {
        String uriFragment = Page.getCurrent().getUriFragment();
        ParametersHandler handler = new ParametersHandler(uriFragment);
        handler.addParameter("page", newPageNumber.toString());
        ((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
    }

    @Override
    public void filterParameterEventHander(String propertyId, Object filterValue) {
        String uriFragment = Page.getCurrent().getUriFragment();
        ParametersHandler handler = new ParametersHandler(uriFragment);
        if (filterValue == null || (filterValue.getClass() == String.class && ((String) filterValue).isEmpty())) {
            //Remove from URI
            handler.removeParameter(propertyId);
        } else {
            String value;
            switch (propertyId) {
                case "id":
                    value = ParametersHandler.getStringForInterval((NumberInterval) filterValue);
                    break;
                case "start":
                    value = ParametersHandler.getStringForInterval((DateInterval) filterValue);
                    break;
                default:
                    value = filterValue.toString();
                    break;
            }
            handler.addParameter(propertyId, value);
        }
        ((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
    }

    @Override
    public void navigateToEventHandler(Class where, Object param) {
        if (param == null) {
            navigator.navigateTo(where);
        } else {
            navigator.navigateTo(where, param.toString());
        }
    }

    @Override
    public void doAfterLogout() {
        isInitialized = false;
    }

    @Override
    public boolean isLayoutInitialized() {
        return isInitialized;
    }

    @Override
    public boolean canReadLog(long executionId) {
        PipelineExecution exec =
                getLightExecution(executionId);
        return permissionUtils.hasPermission(exec, EntityPermissions.PIPELINE_EXECUTION_READ);
    }

    @Override
    public boolean canDebugData(long executionId) {
        PipelineExecution exec =
                getLightExecution(executionId);
        return permissionUtils.hasPermission(exec, EntityPermissions.PIPELINE_EXECUTION_READ);
    }

    @Override
    public boolean canRunPipeline(long executionId) {
        PipelineExecution exec =
                getLightExecution(executionId);
        return permissionUtils.hasPermission(exec, EntityPermissions.PIPELINE_RUN);
    }

    @Override
    public boolean canDebugPipeline(long executionId) {
        PipelineExecution exec =
                getLightExecution(executionId);
        return this.permissionUtils.hasUserAuthority(EntityPermissions.PIPELINE_RUN_DEBUG)
                && this.permissionUtils.hasPermission(exec, EntityPermissions.PIPELINE_RUN_DEBUG);
    }
}
