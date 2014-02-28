package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.SharedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.Role;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of authorization logic for {@link CriteriaBuilder}.
 *
 * @author Petyr
 * @author Jan Vojt
 */
class DbAuthorizatorImpl implements DbAuthorizator {

    @Autowired(required = false)
    private AuthenticationContext authCtx;
    
    @Override
    public Predicate getAuthorizationPredicate(CriteriaBuilder cb, Path<?> root, Class<?> entityClass) {

        if (authCtx == null) {
            // no athorization
            return null;
        }
        
        if (authCtx.getAuthentication().getAuthorities().contains(Role.ROLE_ADMIN)) {
            // admin has no restrictions
            return null;
        }

        Predicate predicate = null;
        
        if (SharedEntity.class.isAssignableFrom(entityClass)) {
            predicate = or(cb, predicate, root.get("shareType").in(ShareType.PUBLIC));
        }

        if (OwnedEntity.class.isAssignableFrom(entityClass)) {
            predicate = or(cb, predicate, cb.equal(root.get("owner"), authCtx.getUser()));
        }
		
		// PipelineExecution is also viewable whenever its Pipeline is viewable
        if (PipelineExecution.class.isAssignableFrom(entityClass)) {
			predicate = or(cb, predicate, getAuthorizationPredicate(cb, root.get("pipeline"), Pipeline.class));
		}

        return predicate;
    }

    private Predicate or(CriteriaBuilder cb, Predicate left, Predicate right) {
        if (left == null) {
            return right;
        } else {
            return cb.or(left, right);
        }
    }

    /**
     * Gets property path.
     *
     * @param root the root where path starts form
     * @param propertyId the property ID
     * @return the path to property
     */
    private Path<Object> getPropertyPath(final Root<?> root, final Object propertyId) {
        final String[] propertyIdParts = ((String) propertyId).split("\\.");

        Path<Object> path = null;
        for (final String part : propertyIdParts) {
            if (path == null) {
                path = root.get(part);
            } else {
                path = path.get(part);
            }
        }
        return path;
    }
    
}
