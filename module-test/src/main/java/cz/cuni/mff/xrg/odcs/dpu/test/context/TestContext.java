/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.dpu.test.context;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;

/**
 * Special implementation of {@link DPUContext} that enables testing.
 *
 * @author Petyr
 */
public class TestContext implements DPUContext {

    private static final Logger LOG = LoggerFactory.getLogger(TestContext.class);

    /**
     * Root directory for execution.
     */
    private File rootDirectory;

    /**
     * Date of last execution.
     */
    private Date lastExecution;

    /**
     * Jar path.
     */
    private String jarPath;

    /**
     * True if DPU that use this context publish warning event.
     */
    private boolean publishedWarning = false;

    /**
     * True if DPU that use this context publish error event.
     */
    private boolean publishedError = false;

    /**
     * Working directory, if null then the working subdirectory in {@link #rootDirectory} is used.
     */
    private File workingDirectory = null;

    /**
     * Result directory, if null then the result subdirectory in {@link #rootDirectory} is used.
     */
    private File resultDirectory = null;

    /**
     * Global DPU directory, if null then the global subdirectory in {@link #rootDirectory} is used.
     */
    private File globalDirectory = null;

    /**
     * User DPU directory, if null then the user subdirectory in {@link #rootDirectory} is used.
     */
    private File userDirectory = null;

    /**
     * Environment variables from Core, which are accesible by DPU.
     */
    private Map<String, String> environment;

    private File dpuInstanceDirectory = null;

    public TestContext(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.environment = new HashMap<>();
        globalDirectory = new File(rootDirectory, "global");
        if (!globalDirectory.exists()) {
            globalDirectory.mkdirs();
        }
        userDirectory = new File(rootDirectory, "user");
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        resultDirectory = new File(rootDirectory, "result");
        if (!resultDirectory.exists()) {
            resultDirectory.mkdirs();
        }
        workingDirectory = new File(rootDirectory, "working");
        if (!workingDirectory.exists()) {
            workingDirectory.mkdirs();
        }
        dpuInstanceDirectory = new File(rootDirectory, "dpuInstance");
        if (!dpuInstanceDirectory.exists()) {
            dpuInstanceDirectory.mkdirs();
        }
    }

    @Override
    public void sendMessage(MessageType type, String shortMessage) {
        sendMessage(type, shortMessage, "");
    }

    @Override
    public void sendMessage(MessageType type,
            String shortMessage,
            String fullMessage) {
        switch (type) {
            case DEBUG:
                LOG.debug("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage);
                break;
            case ERROR:
                LOG.error("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage);
                publishedError = true;
                break;
            case INFO:
                LOG.info("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage);
                break;
            case WARNING:
                LOG.warn("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage);
                publishedWarning = true;
                break;
        }

    }

    @Override
    public void sendMessage(MessageType type, String shortMessage,
            String fullMessage, Exception exception) {
        switch (type) {
            case DEBUG:
                LOG.debug("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage,
                        exception);
                break;
            case ERROR:
                LOG.error("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage,
                        exception);
                publishedError = true;
                break;
            case INFO:
                LOG.info("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage,
                        exception);
                break;
            case WARNING:
                LOG.warn("DPU publish message short: '{}' long: '{}'",
                        shortMessage,
                        fullMessage,
                        exception);
                publishedWarning = true;
                break;
        }
    }

    @Override
    public boolean isDebugging() {
        return false;
    }

    @Override
    public boolean canceled() {
        return false;
    }

    @Override
    public File getJarPath() {
        if (jarPath == null) {
            throw new RuntimeException(
                    "Jar-path has not been set! Use TestEnvironment.setJarPath");
        } else {
            return new File(jarPath);
        }
    }

    @Override
    public Date getLastExecutionTime() {
        return lastExecution;
    }

    /**
     * @return True if the warning message has been sent via this context.
     */
    public boolean isPublishedWarning() {
        return publishedWarning;
    }

    /**
     * @return True if the error message has been sent via this context.
     */
    public boolean isPublishedError() {
        return publishedError;
    }

    /**
     * @param lastExecution
     *            Date of last execution.
     */
    public void setLastExecution(Date lastExecution) {
        this.lastExecution = lastExecution;
    }

    /**
     * @param jarPath
     *            Path to the jar file.
     */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public File getWorkingDir() {
        return workingDirectory;
    }

    @Override
    public File getResultDir() {
        return resultDirectory;
    }

    @Override
    public File getGlobalDirectory() {
        return globalDirectory;
    }

    @Override
    public File getUserDirectory() {
        return userDirectory;
    }

    @Override
    public String getDpuInstanceDirectory() {
        return dpuInstanceDirectory.toURI().toASCIIString();
    }

    @Override
    public Locale getLocale() {
        return new Locale("en", "US");
    }

    @Override
    public Long getPipelineId() {
        return 7L;
    }

    @Override
    public Long getPipelineExecutionId() {
        return 15L;
    }

    @Override
    public Long getDpuInstanceId() {
        return 9L;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return environment;
    }

    @Override
    public String getPipelineOwner() {
        return "test_user";
    }

    @Override
    public String getOrganization() {
        return "test_organization";
    }

    @Override
    public String getPipelineExecutionActorExternalId() {
        return "test_user_actor_id";
    }

    @Override
    public String getPipelineExecutionOwner() {
        return "test_user_executor";
    }

    @Override
    public String getPipelineExecutionOwnerExternalId() {
        return "test_user_executor_id";
    }
}
