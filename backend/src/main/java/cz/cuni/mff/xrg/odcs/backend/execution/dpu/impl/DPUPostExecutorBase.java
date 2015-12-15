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
package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.util.List;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.execution.dpu.DPUPostExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Extended base implementation {@link DPUPostExecutor}. Offers execution only for
 * given {@link DPUExecutionState}s.
 * Put the post-executor code into {@link #execute(Node, Map, Object, PipelineExecution, ProcessingUnitInfo)} method.
 * 
 * @author Petyr
 */
abstract class DPUPostExecutorBase implements DPUPostExecutor {

    /**
     * Contains states on which this execution will be executed, other states
     * are ignored.
     */
    private final List<DPUExecutionState> executionStates;

    /**
     * @param executionStates
     *            List of {@link DPUExecutionState} on which run {@link #execute(Node, Map, Object, PipelineExecution, ProcessingUnitInfo)}
     */
    public DPUPostExecutorBase(List<DPUExecutionState> executionStates) {
        this.executionStates = executionStates;
    }

    @Override
    public boolean postAction(Node node,
            Map<Node, Context> contexts,
            Object dpuInstance,
            PipelineExecution execution,
            ProcessingUnitInfo unitInfo) {
        // shall we execute ?
        if (executionStates.contains(unitInfo.getState())) {
            return execute(node, contexts, dpuInstance, execution, unitInfo);
        } else {
            return true;
        }
    }

    /**
     * Execute executor's code.
     * 
     * @param node
     * @param contexts
     * @param dpuInstance
     * @param execution
     * @param unitInfo
     * @return
     */
    protected abstract boolean execute(Node node,
            Map<Node, Context> contexts,
            Object dpuInstance,
            PipelineExecution execution,
            ProcessingUnitInfo unitInfo);

}
