package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import java.util.Date;

import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Represent a scheduler plan. A single plan execute just one pipeline.
 *
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_schedule")
public class Schedule implements DataObject, OwnedEntity {

	/**
	 * Unique ID for each plan.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_schedule")
	@SequenceGenerator(name = "seq_exec_schedule", allocationSize = 1)
	private Long id;

	/**
	 * Plan's name.
	 */
	@Column
	private String name;

	/**
	 * Plan's description.
	 */
	@Column
	private String description;

	/**
	 * Pipeline to execute.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pipeline_id", nullable = false)
	private Pipeline pipeline;

	/**
	 * Plan is active for just one execution.
	 */
	@Column(name = "just_once")
	private boolean justOnce;

	/**
	 * True if the schedule is enabled. Disabled (not enabled) schedules are
	 * ignored.
	 */
	@Column(name = "enabled")
	private boolean enabled;

	/**
	 * Schedule rule type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type")
	private ScheduleType type;

	/**
	 * Time o first planned execution. May be null if this plan is only supposed
	 * to run after pipelines in {@link #afterPipelines}, but has no specific
	 * time to be run at. Used only if {@link #type} is
	 * {@link ScheduleType#PERIODICALLY}.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "first_exec", nullable = true)
	private Date firstExecution;

	/**
	 * Time of the last execution.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_exec", nullable = true)
	private Date lastExecution;

	/**
	 * Execution period in {@link #periodUnit}. Used only if {@link #type} =
	 * RunInTime.
	 */
	@Column(name = "time_period")
	private Integer period;

	/**
	 * Time period unit type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "period_unit")
	private PeriodUnit periodUnit;

	/**
	 * Pipeline after which this job is supposed to run. Applicable only if
	 * {@link #type} is {@link ScheduleType#AFTER_PIPELINE}.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "exec_schedule_after",
			joinColumns
			= @JoinColumn(name = "schedule_id", referencedColumnName = "id"),
			inverseJoinColumns
			= @JoinColumn(name = "pipeline_id", referencedColumnName = "id"))
	private Set<Pipeline> afterPipelines = new HashSet<>();

	/**
	 * Notification settings for this schedule. May be null, if so
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.user.UserNotificationRecord} is
	 * to be used.
	 */
	@OneToOne(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ScheduleNotificationRecord notification;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User owner;

	/**
	 * If true then pipeline can be run only at most +10 minutes from scheduled
	 * time.
	 */
	@Column(name = "strict_timing")
	private boolean strictlyTimed;

	/**
	 * If {@link #strictlyTimed} is true then say how strict we are in minutes.
	 */
	@Column(name = "strict_tolerance")
	private Integer strictToleranceMinutes;

	/**
	 * Empty constructor. Used by JPA. Do not use otherwise.
	 */
	public Schedule() {
	}

	/**
	 *
	 * @return name of the schedule.
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param newName New schedule name.
	 */
	public void setName(String newName) {
		this.name = StringUtils.abbreviate(newName, LenghtLimits.SCHEDULE_NAME);
	}

	/**
	 *
	 * @return Description of the schedule.
	 */
	public String getDescription() {
		return StringUtils.defaultString(description);
	}

	/**
	 *
	 * @param newDescription New description.
	 */
	public void setDescription(String newDescription) {
		this.description = newDescription;
	}

	/**
	 *
	 * @return Pipeline to execution.
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 *
	 * @param pipeline New pipeline to execute.
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	/**
	 *
	 * @return True if the schedule can fire just once.
	 */
	public boolean isJustOnce() {
		return justOnce;
	}

	/**
	 *
	 * @param justOnce True if the schedule should fire just once, ei. should be
	 *                 disabled after first usage.
	 */
	public void setJustOnce(boolean justOnce) {
		this.justOnce = justOnce;
	}

	/**
	 *
	 * @return True if the execution is enabled ie. active.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 *
	 * @param enabled False to disable the schedule.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 *
	 * @return Schedule type.
	 */
	public ScheduleType getType() {
		return type;
	}

	/**
	 *
	 * @param type New type for the schedule rule.
	 */
	public void setType(ScheduleType type) {
		this.type = type;
	}

	/**
	 *
	 * @return Time of first activation, or null in case that the schedule has
	 *         not fire yet.
	 */
	public Date getFirstExecution() {
		return firstExecution;
	}

	/**
	 *
	 * @param firstExecution Set time of the first execution.
	 */
	public void setFirstExecution(Date firstExecution) {
		this.firstExecution = firstExecution;
	}

	/**
	 *
	 * @return Time of creation of the last created execution.
	 */
	public Date getLastExecution() {
		return lastExecution;
	}

	/**
	 *
	 * @param lastExecution New time for the last created execution.
	 */
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	/**
	 * This value is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @return Period in which create the execution.
	 */
	public Integer getPeriod() {
		return period;
	}

	/**
	 * * This value is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @param period Period in which create the execution.
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}

	/**
	 * * This value is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @return Period unit.
	 */
	public PeriodUnit getPeriodUnit() {
		return periodUnit;
	}

	/**
	 * * This value is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @param periodUnit Period unit.
	 */
	public void setPeriodUnit(PeriodUnit periodUnit) {
		this.periodUnit = periodUnit;
	}

	/**
	 * Schedules this job after every run of given pipelines. All the pipelines
	 * must be executed in order to fire this schedule. This value is used only
	 * if the schedule type is {@link ScheduleType#AFTER_PIPELINE}.
	 *
	 * @param pipeline That has to be executed in order to enable this schedule
	 *                 to fire.
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public boolean addAfterPipeline(Pipeline pipeline) {
		return afterPipelines.add(pipeline);
	}

	/**
	 * This value is used only if the schedule type is
	 * {@link ScheduleType#AFTER_PIPELINE}.
	 *
	 * @return List of pipelines that has to be executed in order to fire this
	 *         schedule.
	 */
	public Set<Pipeline> getAfterPipelines() {
		return new HashSet<>(afterPipelines);
	}

	/**
	 *
	 * @param afterPipelines List of pipelines that must be executed before this
	 *                       schedule fire.
	 */
	public void setAfterPipelines(Set<Pipeline> afterPipelines) {
		this.afterPipelines = afterPipelines;
	}

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Can be null, in such case the owner notification settings are used.
	 *
	 * @return Notification rule for the schedule or null.
	 */
	public ScheduleNotificationRecord getNotification() {
		return notification;
	}

	/**
	 * If set then overwrite the owner notification setting.
	 *
	 * @param notification Notification rule for the schedule.
	 */
	public void setNotification(ScheduleNotificationRecord notification) {
		this.notification = notification;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	/**
	 *
	 * @param owner owner of the schedule.
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * This value is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @return True if the pipeline is strictly timed.
	 */
	public boolean isStrictlyTimed() {
		return strictlyTimed;
	}

	/**
	 * This options is used only if the schedule type is
	 * {@link ScheduleType#PERIODICALLY}.
	 *
	 * @param strictTiming True to use strict timing.
	 */
	public void setStrictlyTimed(boolean strictTiming) {
		this.strictlyTimed = strictTiming;
	}

	/**
	 * Tolerance for schedule. If negative then enable pipeline to run sooner
	 * but any delay will be ignored. If positive then enable delay, but prevent
	 * from running earlier. * @return tolerance for execution jitter
	 *
	 * @return Tolerance for strict timing in minutes.
	 */
	public Integer getStrictToleranceMinutes() {
		return strictToleranceMinutes;
	}

	/**
	 * Set tolerance. If negative then enable pipeline to run sooner but any
	 * delay will be ignored. If positive then enable delay, but prevent from
	 * running earlier.
	 *
	 * @param strictToleranceMinutes Tolerance for strict timing in minutes.
	 */
	public void setStrictToleranceMinutes(Integer strictToleranceMinutes) {
		this.strictToleranceMinutes = strictToleranceMinutes;
	}

	/**
	 * Return time of the next execution. It the schedule is not time dependent
	 * return null.
	 *
	 * @return Estimate of time for next execution or null.
	 */
	public Date getNextExecutionTimeInfo() {
		return ScheduleNextRun.calculateNextRun(this);
	}

}
