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
package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Level;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter.Compare;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogRead;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Facade for fetching persisted entities. Manipulating logs is not implemented,
 * as these should be created immutable by backend only.
 *
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
class LogFacadeImpl implements LogFacade {

    @Autowired
    private DbLogRead logDao;

    /**
     * Return true if there exist logs with given level for given dpu instance
     * of given pipeline execution.
     *
     * @param exec
     * @param level
     * @return
     */
    @Override
    public boolean existLogsGreaterOrEqual(PipelineExecution exec, Level level) {
        DbQueryBuilder<Log> builder = logDao.createQueryBuilder();
        // add filters
        builder.addFilter(Compare.equal("execution", exec.getId()));
        builder.addFilter(Compare.greaterEqual("logLevel", level.toInt()));
        // execute
        return logDao.executeSize(builder.getCountQuery()) > 0;
    }

    /**
     * Return list of all usable log's levels without aggregations. Ordered
     * descending by priority.
     *
     * @return
     */
    @Override
    public ArrayList<Level> getAllLevels() {
        ArrayList result = new ArrayList(5);

        result.add(Level.ERROR);
        result.add(Level.WARN);
        result.add(Level.INFO);
        result.add(Level.DEBUG);
        result.add(Level.TRACE);

        return result;
    }

    @Override
    public InputStream getLogsAsStream(List<Object> filters) {
        // apply filters as we have them
        DbQueryBuilder<Log> builder = logDao.createQueryBuilder();

        if (filters == null) {
            // no filters, take all the data
        } else {
            for (Object filter : filters) {
                builder.addFilter(filter);
            }
        }
        // get data and transform them into stream
        List<Log> data = logDao.executeList(builder.getQuery());

        StringBuilder sb = new StringBuilder();

        for (Log log : data) {
            sb.append(new Date(log.getTimestamp()));
            sb.append(' ');
            sb.append(Level.toLevel(log.getLogLevel()));
            sb.append(' ');
            sb.append(log.getSource());
            sb.append(' ');
            if (log.getStackTrace() == null || log.getStackTrace().isEmpty()) {
                sb.append(log.getMessage());
            } else {
                sb.append(log.getMessage());
                sb.append("\r\nStack trace:\r\n");
                // just do replace in stack trace
                sb.append(log.getStackTrace());
            }
            sb.append('\r');
            sb.append('\n');
        }

        if (sb.length() == 0) {
            return null;
        } else {
            return new ByteArrayInputStream(sb.toString().getBytes());
        }
    }

    @Override
    public void deleteLogs(PipelineExecution execution) {
        logDao.delete(execution);

    }
}
