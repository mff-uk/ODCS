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
package eu.unifiedviews.master.api;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserActor;
import eu.unifiedviews.master.authentication.AuthenticationRequired;
import eu.unifiedviews.master.converter.PipelineExecutionDTOConverter;
import eu.unifiedviews.master.converter.PipelineExecutionEventDTOConverter;
import eu.unifiedviews.master.i18n.Messages;
import eu.unifiedviews.master.model.ApiException;
import eu.unifiedviews.master.model.PipelineExecutionDTO;
import eu.unifiedviews.master.model.PipelineExecutionEventDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
@Path("/pipelines")
@AuthenticationRequired
public class ExecutionResource {
    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private UserFacade userFacade;

    @GET
    @Path("/{pipelineid}/executions")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionDTO> getPipelineExecutions(@PathParam("pipelineid") String id) {
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", id), String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", id), String.format("Pipeline with id=%s doesn't exist!", id));
            }
            List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline);
            if (executions == null || executions.isEmpty()) {
                throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.execution.not.found"), String.format("null pipeline executions for pipeline with ID=%s!", id));
            }
            return PipelineExecutionDTOConverter.convert(executions);
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.get.executions.general.error"), e.getMessage());
        }
    }

    @GET
    @Path("/{pipelineid}/executions/last")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineExecutionDTO getLastPipelineExecution(@PathParam("pipelineid") String id) {
        Pipeline pipeline = null;
        PipelineExecution execution = null;
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", id), String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", id), String.format("Pipeline with id=%s doesn't exist!", id));
            }
            HashSet<PipelineExecutionStatus> set = new HashSet<>();
            set.add(PipelineExecutionStatus.CANCELLED);
            set.add(PipelineExecutionStatus.FINISHED_SUCCESS);
            set.add(PipelineExecutionStatus.FINISHED_WARNING);
            set.add(PipelineExecutionStatus.FAILED);
            execution = pipelineFacade.getLastExec(pipeline, set);
            if (execution == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.execution.not.found"), String.format("null pipeline executions for pipeline with ID=%s!", id));
            }
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.get.executions.general.error"), e.getMessage());
        }

        return PipelineExecutionDTOConverter.convert(execution);
    }

    @GET
    @Path("/{pipelineid}/executions/pending")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionDTO> getPendingPipelineExecution(@PathParam("pipelineid") String id) {
        Pipeline pipeline = null;
        List<PipelineExecution> executions = new ArrayList<>();
        if (StringUtils.isBlank(id) || !StringUtils.isNumeric(id)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", id), String.format("ID=%s is not valid pipeline ID", id));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(id));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", id), String.format("Pipeline with id=%s doesn't exist!", id));
            }
            executions.addAll(pipelineFacade.getExecutions(pipeline, PipelineExecutionStatus.CANCELLING));
            executions.addAll(pipelineFacade.getExecutions(pipeline, PipelineExecutionStatus.RUNNING));
            executions.addAll(pipelineFacade.getExecutions(pipeline, PipelineExecutionStatus.QUEUED));
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.get.executions.general.error"), e.getMessage());
        }

        return PipelineExecutionDTOConverter.convert(executions);
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}")
    @Produces({ MediaType.APPLICATION_JSON })
    public PipelineExecutionDTO getPipelineExecution(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        Pipeline pipeline = null;
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", pipelineId), String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.execution.id.invalid", executionId), String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", pipelineId), String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            execution = pipelineFacade.getExecution(Long.parseLong(executionId));
            if (execution == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.execution.id.not.found"), String.format("Pipeline execution with id=%s doesn't exist!", executionId));
            }
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.get.executions.general.error"), e.getMessage());
        }

        if (execution.getPipeline().getId().equals(pipeline.getId())) {
            return PipelineExecutionDTOConverter.convert(execution);
        } else {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.execution.mismatch", executionId, pipelineId),String.format("PipelineExecution with id=%s is not execution of pipeline with id=%s!", executionId, pipelineId));
        }
    }

    @POST
    @Path("/{pipelineid}/executions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PipelineExecutionDTO createPipelineExecution(@PathParam("pipelineid") String pipelineId, PipelineExecutionDTO newExecution) {
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", pipelineId), String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        try {
            // try to get pipeline
            Pipeline pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", pipelineId), String.format("Pipeline with ID=%s doesn't exist!", pipelineId));
            }
            // try to get user
            User user = userFacade.getUserByExtId(newExecution.getUserExternalId());
            if (user == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("execution.user.id.not.found"), String.format("User with ID=%s could not be found.", newExecution.getUserExternalId()));
            }

            UserActor actor = this.userFacade.getUserActorByExternalId(newExecution.getUserActorExternalId());

            execution = pipelineFacade.createExecution(pipeline);
            execution.setOwner(user);
            if (actor != null) {
                execution.setActor(actor);
            }
            execution.setDebugging(newExecution.isDebugging());
            execution.setOrderNumber(1L);
            pipelineFacade.save(execution);
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.create.execution.general.error"), e.getMessage());
        }
        return PipelineExecutionDTOConverter.convert(execution);
    }

    @GET
    @Path("/{pipelineid}/executions/{executionid}/events")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PipelineExecutionEventDTO> getPipelineExecutionEvents(@PathParam("pipelineid") String pipelineId, @PathParam("executionid") String executionId) {
        Pipeline pipeline = null;
        PipelineExecution execution = null;
        if (StringUtils.isBlank(pipelineId) || !StringUtils.isNumeric(pipelineId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.id.invalid", pipelineId), String.format("ID=%s is not valid pipeline ID", pipelineId));
        }
        if (StringUtils.isBlank(executionId) || !StringUtils.isNumeric(executionId)) {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.execution.id.invalid", executionId), String.format("ID=%s is not valid pipeline execution ID", executionId));
        }
        try {
            pipeline = pipelineFacade.getPipeline(Long.parseLong(pipelineId));
            if (pipeline == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.id.not.found", pipelineId), String.format("Pipeline with id=%s doesn't exist!", pipelineId));
            }
            execution = pipelineFacade.getExecution(Long.parseLong(executionId));
            if (execution == null) {
                throw new ApiException(Response.Status.NOT_FOUND, Messages.getString("pipeline.execution.id.not.found"), String.format("Pipeline execution with id=%s doesn't exist!", executionId));
            }
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, Messages.getString("pipeline.get.executions.events.general.error"), e.getMessage());
        }

        if (execution.getPipeline().getId().equals(pipeline.getId())) {
            List<MessageRecord> events = dpuFacade.getAllDPURecords(execution);
            return PipelineExecutionEventDTOConverter.convert(events);
        } else {
            throw new ApiException(Response.Status.BAD_REQUEST, Messages.getString("pipeline.execution.mismatch", executionId, pipelineId),String.format("PipelineExecution with id=%s is not execution of pipeline with id=%s!", executionId, pipelineId));
        }
    }
}
