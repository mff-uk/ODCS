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
 * Event that is used to publish informations about pipeline execution.
 * 
 * @author Petyr
 */
public class PipelineInfo extends PipelineEvent {

    private static final long serialVersionUID = 4944203292802753371L;

    private static final Logger LOG = LoggerFactory
            .getLogger(PipelineInfo.class);

    private final String shortMessage;

    private final String longMessage;

    protected PipelineInfo(PipelineExecution execution,
            Object source,
            String shortMessage,
            String longMessage) {
        super(null, execution, source);
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
    }

    @Override
    public MessageRecord getRecord() {
        return new MessageRecord(time, MessageRecordType.PIPELINE_INFO, null, execution, shortMessage, longMessage);
    }

    public static PipelineInfo createWait(PipelineExecution execution,
            Object source) {
        LOG.info("Execution is waiting for running conflict pipelines to end ...");
        return new PipelineInfo(execution, source, Messages.getString("PipelineInfo.waiting"), "");
    }

    public static PipelineInfo createWaitEnd(PipelineExecution execution,
            Object source) {
        LOG.info("Execution continue");
        return new PipelineInfo(execution, source, Messages.getString("PipelineInfo.continue"), "");
    }

}
