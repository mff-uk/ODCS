package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link DbExecution}
 *
 * @author Petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
class DbExecutionImpl extends DbAccessBase<PipelineExecution> implements DbExecution {

    protected DbExecutionImpl() {
        super(PipelineExecution.class);
    }

	@Override
	public List<PipelineExecution> getAll() {
		final String stringQuery = "SELECT e FROM PipelineExecution e";
		return executeList(stringQuery);
	}
	
	@Override
	public List<PipelineExecution> getAll(Pipeline pipeline) {
		final String stringQuery = "SELECT e FROM PipelineExecution e"
								+ " WHERE e.pipeline = :pipe";
		TypedQuery<PipelineExecution> query = createTypedQuery(stringQuery);	
		query.setParameter("pipe", pipeline);
		return executeList(query);
	}
	
	@Override
	public List<PipelineExecution> getAll(PipelineExecutionStatus status) {
		final String stringQuery = "SELECT e FROM PipelineExecution e"
								+ " WHERE e.status = :status";
		TypedQuery<PipelineExecution> query = createTypedQuery(stringQuery);
		query.setParameter("status", status);
		return executeList(query);
	}	
	
	@Override
	public List<PipelineExecution> getAll(Pipeline pipeline, PipelineExecutionStatus status) {
		final String stringQuery = "SELECT e FROM PipelineExecution e"
								+ " WHERE e.pipeline = :pipe AND e.status = :status";
		TypedQuery<PipelineExecution> query = createTypedQuery(stringQuery);	
		query.setParameter("pipe", pipeline);
		query.setParameter("status", status);
		return executeList(query);
	}	
	
	@Override
	public PipelineExecution getLastExecution(Pipeline pipeline,
			Set<PipelineExecutionStatus> statuses) {
		final String stringQuery = "SELECT e FROM PipelineExecution e"
								+ " WHERE e.pipeline = :pipe"
								+ " AND e.status IN :status"
								+ " AND e.end IS NOT NULL"
								+ " ORDER BY e.end DESC";				
		TypedQuery<PipelineExecution> query = createTypedQuery(stringQuery);		
		query.setParameter("pipe", pipeline);
		query.setParameter("status", statuses);		
		return execute(query);
	}

	@Override
	public PipelineExecution getLastExecution(Schedule schedule,
			Set<PipelineExecutionStatus> statuses) {		
		final String stringQuery = "SELECT e FROM PipelineExecution e"
								+ " WHERE e.schedule = :schedule"
								+ " AND e.status IN :status"
								+ " AND e.end IS NOT NULL"
								+ " ORDER BY e.end DESC";		
		TypedQuery<PipelineExecution> query = createTypedQuery(stringQuery);		
		query.setParameter("schedule", schedule);
		query.setParameter("status", statuses);
        return execute(query);
	}

	@Override
	public boolean hasModified(Date since) {		
		final String stringQuery = "SELECT MAX(e.lastChange)"
				+ " FROM PipelineExecution e";
		
		TypedQuery<Date> query = em.createQuery(stringQuery, Date.class);
		Date lastModified = (Date) query.getSingleResult();
		
		if (lastModified == null) {
			// there are no executions in DB
			return false;
		}
		
		return lastModified.after(since);
	}

}
