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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserActor;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Information about executed pipeline and its states.
 * 
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Petyr
 */
@Entity
@Table(name = "exec_pipeline")
@Index(name = "ix_EXEC_PIPELINE", columnNames = "status, pipeline_id, context_id, schedule_id, owner_id, backend_id")
public class PipelineExecution implements OwnedEntity, DataObject {

    /**
     * Unique id of pipeline execution.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_pipeline")
    @SequenceGenerator(name = "seq_exec_pipeline", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * Actual status for executed pipeline.
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private PipelineExecutionStatus status;

    /**
     * Pipeline being executed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;

    /**
     * Node where execution should end. Only for debug mode.
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "debugnode_id", nullable = true)
    private Node debugNode;

    /**
     * Run in debug mode?
     */
    @Column(name = "debug_mode")
    private boolean isDebugging;

    @Column(name = "order_number", nullable = false)
    private Long orderNumber;

    /**
     * Timestamp when this execution started, or null.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_start")
    private Date start;

    /**
     * Timestamp when this execution started, or null.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_end")
    private Date end;

    /**
     * Execution context, can be null.
     */
    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", nullable = true)
    private ExecutionContextInfo context;

    /**
     * Schedule that planned this execution. Null for execution created by user.
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = true)
    private Schedule schedule;

    /**
     * It true pipeline run in silent mode and the end of the execution can't be
     * used to fire schedule.
     */
    @Column(name = "silent_mode")
    private boolean silentMode;

    /**
     * True if pipeline should or has been stopped on user request.
     */
    @Column(name = "stop")
    private boolean stop;

    /**
     * Timestamp when this execution was last changed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "t_last_change")
    private Date lastChange;

    /**
     * Owner ie. author of the execution.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "execution")
    private Set<MessageRecord> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_actor_id")
    private UserActor actor;

    @Column(name = "backend_id", length = 128)
    private String backendId;

    /**
     * No-arg constructor for JPA
     */
    public PipelineExecution() {
    }

    /**
     * Constructor. Create pipeline which will be run as soon as possible in
     * non-debug mode. The pipeline execution will not run other pipelines based
     * on scheduling rules.
     * 
     * @param pipeline
     *            the pipeline for pipeline execution.
     */
    public PipelineExecution(Pipeline pipeline) {
        this.status = PipelineExecutionStatus.QUEUED;
        this.isDebugging = false;
        this.schedule = null;
        this.silentMode = true;
        this.stop = false;
        setPipeline(pipeline);

        // Execution context is obligatory, so that we do not need to check for
        // nulls everywhere. A new execution has an empty context.
        this.context = new ExecutionContextInfo(this);
    }

    /**
     * Returns the set ID of this pipeline execution as {@link Long} value.
     * 
     * @return the set ID of this pipeline execution as {@link Long} value.
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Returns the actual status for executed pipeline.
     * 
     * @return The actual status for executed pipeline.
     */
    public PipelineExecutionStatus getStatus() {
        return status;
    }

    /**
     * Set new status for executed pipeline.
     * 
     * @param newStatus
     *            new value of {@link PipelineExecutionStatus}
     */
    public void setStatus(PipelineExecutionStatus newStatus) {
        status = newStatus;
    }

    /**
     * Returns the executed pipeline.
     * 
     * @return the executed pipeline
     */
    public Pipeline getPipeline() {
        return pipeline;
    }

    /**
     * Set new value of executed pipeline.
     * 
     * @param pipeline
     *            the new executed pipeline
     */
    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;

        if (pipeline != null)
            pipeline.getExecutions().add(this);
    }

    /**
     * Returns true, if pipeline run in debug mode, false otherwise.
     * 
     * @return true, if pipeline run in debug mode, false otherwise.
     */
    public boolean isDebugging() {
        return isDebugging;
    }

    /**
     * Set if pipeline will be run in debug mode or not.
     * 
     * @param isDebugging
     *            boolean value to set debug mode fot the pipeline
     *            execution.
     */
    public void setDebugging(boolean isDebugging) {
        this.isDebugging = isDebugging;
    }

    /**
     * Returns the timestamp when this execution started, or null.
     * 
     * @return The timestamp when this execution started, or null.
     */
    public Date getStart() {
        return start;
    }

    /**
     * Set the instance of {@link Date} as the value when the pipeline execution
     * started.
     * 
     * @param start
     *            the timestamp as value when the pipeline execution started.
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * Returns the timestamp when this execution ends, or null.
     * 
     * @return The timestamp when this execution ends, or null.
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Set the instance of {@link Date} as the value when the pipeline execution
     * ends.
     * 
     * @param end
     *            the timestamp as value when the pipeline execution ends.
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Use to gain read only access to the context.
     * 
     * @return Context or null.
     */
    public ExecutionContextInfo getContextReadOnly() {
        return context;
    }

    /**
     * Returns the execution context or null.
     * 
     * @return the execution context or null.
     */
    public ExecutionContextInfo getContext() {
        return context;
    }

    public void setContext(ExecutionContextInfo context) {
        this.context = context;
    }

    /**
     * Returns schedule that planned this execution. Null for execution created
     * by user.
     * 
     * @return Schedule that planned this execution. Null for execution created
     *         by user.
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Set the schedule that planned this execution. Null for execution created
     * by user.
     * 
     * @param schedule
     *            new instance of {@link Schedule} for planned this
     *            pipeline execution, null for execution created by user.
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;

        if (schedule != null) {
            schedule.getPipelineExecutions().add(this);
        }
    }

    /**
     * Returns true if pipeline run in silent mode and the end of the execution
     * can't be used to fire schedule, false otherwise.
     * 
     * @return true if pipeline run in silent mode and the end of the execution
     *         can't be used to fire schedule, false otherwise.
     */
    public boolean getSilentMode() {
        return silentMode;
    }

    /**
     * Set new boolean value for pipeline execution silent mode.
     * 
     * @param silentMode
     *            true if pipeline run in silent mode and the end of the
     *            execution can't be used to fire schedule, false
     *            otherwise.
     */
    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    /**
     * Returns the node where execution should end. Only for debug mode.
     * 
     * @return The node where execution should end. Only for debug mode.
     */
    public Node getDebugNode() {
        return debugNode;
    }

    /**
     * Set the node where execution should end. Only for debug mode.
     * 
     * @param debugNode
     *            instance of {@link Node} to set execution end node.
     */
    public void setDebugNode(Node debugNode) {
        this.debugNode = debugNode;

        if (debugNode != null)
            debugNode.getExecutions().add(this);
    }

    /**
     * Returns true if pipeline should or has been stopped on user request,
     * false otherwise.
     * 
     * @return true if pipeline should or has been stopped on user request,
     *         false otherwise.
     */
    public boolean getStop() {
        return stop;
    }

    /**
     * Set new value of this pipeline owner.
     * 
     * @param owner
     *            instance of {@link User} as new pipeline owner.
     */
    public void setOwner(User owner) {
        this.owner = owner;

        if (owner != null)
            owner.getExecutions().add(this);
    }

    /**
     * Return the instance of {@link User} as owner of this executablepipeline.
     * 
     * @return the owner of this executable pipeline.
     */
    @Override
    public User getOwner() {
        return owner;
    }

    /**
     * Stop pipeline execution.
     */
    public void stop() {
        status = PipelineExecutionStatus.CANCELLING;
        stop = true;
    }

    /**
     * Returns the {@link Date} instance where the pipeline was last changed.
     * 
     * @return {@link Date} where the pipeline was last changed.
     */
    public Date getLastChange() {
        return lastChange;
    }

    /**
     * Sets new value for last change date
     * 
     * @param lastChange
     */
    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    /**
     * Returns duration of execution. Returns -1 for unfinished/not started
     * executions.
     * 
     * @return duration of pipeline execution
     */
    public long getDuration() {
        if (start == null || end == null) {
            return -1;
        }
        return end.getTime() - start.getTime();
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long order) {
        this.orderNumber = order;
    }

    public UserActor getActor() {
        return this.actor;
    }

    public void setActor(UserActor actor) {
        this.actor = actor;
    }

    public String getBackendId() {
        return this.backendId;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    /**
     * Hashcode is compatible with {@link #equals(java.lang.Object)}.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 3;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Returns true if two objects represent the same pipeline execution. This
     * holds if and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
     * 
     * @param o
     *            value of object
     * @return true if both objects represent the same pipeline execution
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        final PipelineExecution other = (PipelineExecution) o;
        if (this.id == null) {
            return super.equals(other);
        }

        return Objects.equals(this.id, other.id);
    }

    public Set<MessageRecord> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageRecord> messages) {
        this.messages = messages;
    }

}
