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
package cz.cuni.mff.xrg.odcs.commons.app.module.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleChangeNotifier;

/**
 * Implement {@link ModuleChangeNotifier} by using shared file system.
 * To receive the notifications use {@link FileNotifierServer}.
 * 
 * @author Petyr
 */
class FileNotifierClient implements ModuleChangeNotifier {

    private static final Logger LOG = LoggerFactory.getLogger(FileNotifierClient.class);

    /**
     * Used to get information about modules.
     */
    @Autowired
    private ModuleFacade moduleFacade;

    @Autowired(required = false)
    private FileNotifierServer server;

    @Override
    public void updated(DPUTemplateRecord dpu) {
        // notification file name
        final String dpuSubDir = dpu.getJarDirectory();
        final String dpuDir = moduleFacade.getDPUDirectory() +
                File.separator + dpuSubDir;

        File notificationFile = new File(dpuDir,
                dpu.getJarName() + FileNotifierServer.UPDATE_EXT);

        if (notificationFile.exists()) {
            // server listen on create .. so we need to delete
            // old instance first, so we can create new one
            notificationFile.delete();
        }

        createFile(notificationFile, dpuSubDir);
    }

    @Override
    public void created(DPUTemplateRecord dpu) {
        final String dpuDir = dpu.getJarDirectory();

        File notificationFile = new File(moduleFacade.getDPUDirectory(),
                dpuDir + FileNotifierServer.NEW_EXT);
        if (notificationFile.exists()) {
            // server listen on create .. so we need to delete
            // old instance first, so we can create new one
            notificationFile.delete();
        }

        createFile(notificationFile, dpuDir);
    }

    @Override
    public void deleted(DPUTemplateRecord dpu) {
        final String dpuDir = dpu.getJarDirectory();

        File notificationFile = new File(moduleFacade.getDPUDirectory(),
                dpuDir + FileNotifierServer.DELETE_EXT);
        if (notificationFile.exists()) {
            // server listen on create .. so we need to delete
            // old instance first, so we can create new one
            notificationFile.delete();
        }

        createFile(notificationFile, dpuDir);
    }

    /**
     * Create notification file. If {@link FileNotifierServer} exist
     * then tell him to ignore fist notification of given name.
     * 
     * @param file
     * @param toIgnore
     *            Name of notification which should server ignore.
     */
    private void createFile(File file, String toIgnore) {
        if (server == null) {
            // no server is in our instance
        } else {
            // we say server to ignore the notification
            server.addToIgnore(toIgnore);
        }

        try {
            if (file.exists()) {
                // we react only on create, so the file must not exist
                file.delete();
            }

            file.createNewFile();
        } catch (IOException | SecurityException e) {
            LOG.warn("Failed to create notificaiton file.", e);
        }
    }

}
