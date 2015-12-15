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
package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Event published during the pipeline execution termination on user request.
 * 
 * @author Petyr
 */
public final class PipelineAbortedEvent extends PipelineEvent {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineAbortedEvent.class);

    public PipelineAbortedEvent(PipelineExecution pipelineExec, Object source) {
        super(null, pipelineExec, source);

        LOG.info("Pipeline aborted on user request.");
    }

    @Override
    public MessageRecord getRecord() {
        return new MessageRecord(time, MessageRecordType.PIPELINE_INFO,
                dpuInstance, execution,
                Messages.getString("PipelineAbortedEvent.execution.aborted"),
                Messages.getString("PipelineAbortedEvent.execution.aborted.detail"));
    }
}
