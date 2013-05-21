/**
 * Copyright (C) 2012 The Language Archive
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.kinnate.ui.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.arbil.util.BugCatcherManager;
import nl.mpi.arbil.util.MessageDialogHandler;
import nl.mpi.kinnate.entityindexer.EntityServiceException;
import nl.mpi.kinnate.ui.window.AbstractDiagramManager;

/**
 * Created on : Oct 22, 2011, 09:14:39 AM
 *
 * @author Peter Withers
 */
public class ProjectFileMenu extends JMenu implements ActionListener {

    private final AbstractDiagramManager diagramWindowManager;
    private final SessionStorage sessionStorage;
    private final Component parentComponent;
    private final MessageDialogHandler dialogHandler;

    public ProjectFileMenu(AbstractDiagramManager diagramWindowManager, SessionStorage sessionStorage, Component parentComponent, MessageDialogHandler dialogHandler) {
        this.diagramWindowManager = diagramWindowManager;
        this.sessionStorage = sessionStorage;
        this.parentComponent = parentComponent;
        this.dialogHandler = dialogHandler;
        this.setText("Open Recent Project");
        this.addMenuListener(new MenuListener() {
            public void menuCanceled(MenuEvent evt) {
            }

            public void menuDeselected(MenuEvent evt) {
            }

            public void menuSelected(MenuEvent evt) {
                setupMenu();
            }
        });
    }

    static public void addRecentProject(SessionStorage sessionStorageS, File recentProjectFile) {
        // store the accessed and saved files and provide a menu of recent files
        ArrayList<String> tempList = new ArrayList<String>();
        String[] tempArray;
        try {
            tempArray = sessionStorageS.loadStringArray("RecentKinProjects");
            if (tempArray != null) {
                tempList.addAll(Arrays.asList(tempArray));
            }
            // restrict the recent file list to x number but make sure only the oldest gets removed
            while (tempList.size() > 10) {
                tempList.remove(0);
            }
            // todo: make sure the list is kept in order
            tempList.remove(recentProjectFile.toString());
            tempList.add(recentProjectFile.toString());
        } catch (IOException exception) {
//            BugCatcherManager.getBugCatcher().logError(exception);
            tempArray = new String[]{recentProjectFile.toString()};
        }
        try {
            sessionStorageS.saveStringArray("RecentKinProjects", tempList.toArray(new String[]{}));
        } catch (IOException exception) {
            BugCatcherManager.getBugCatcher().logError(exception);
        }
//        setupMenu();
    }

    private void setupMenu() {
        this.removeAll();
        try {
            String[] recentProjectFileArray = /* move this to the project manager */ sessionStorage.loadStringArray("RecentKinProjects");
            if (recentProjectFileArray != null) {
                for (int currentIndex = recentProjectFileArray.length - 1; currentIndex >= 0; currentIndex--) {
                    String currentFilePath = recentProjectFileArray[currentIndex];
                    JMenuItem currentMenuItem = new JMenuItem(currentFilePath);
                    currentMenuItem.setActionCommand(currentFilePath);
                    currentMenuItem.addActionListener(this);
                    this.add(currentMenuItem);
                }
            }
            this.add(new JSeparator());
            JMenuItem clearMenuItem = new JMenuItem("Clear List");
            clearMenuItem.setActionCommand("Clear List");
            clearMenuItem.addActionListener(this);
            this.add(clearMenuItem);
        } catch (IOException exception) {
            JMenuItem currentMenuItem = new JMenuItem("no recent files");
            currentMenuItem.setEnabled(false);
            this.add(currentMenuItem);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ("browse".equals(e.getActionCommand())) {
            System.out.println("browse for project");
        } else if ("Clear List".equals(e.getActionCommand())) {
            try {
                /* move this to the project manager */ sessionStorage.saveStringArray("RecentKinProjects", new String[]{});
            } catch (IOException exception) {
                BugCatcherManager.getBugCatcher().logError(exception);
            }
//            setupMenu();
        } else {
            try {
                final String actionString = e.getActionCommand();
                final File recentProjectFile = new File(actionString);
//                final int startIndex = actionString.lastIndexOf('/');
//                final String recentName = actionString.substring(startIndex + 1);
                final String recentName = recentProjectFile.getName();
                final Dimension parentSize = parentComponent.getSize();
                final Point parentLocation = parentComponent.getLocation();
                int offset = 10;
                final Rectangle windowRectangle = new Rectangle(parentLocation.x + offset, parentLocation.y + offset, parentSize.width - offset, parentSize.height - offset);
                diagramWindowManager.openDiagram(recentName, recentProjectFile.toURI(), true, windowRectangle);
//            } catch (URISyntaxException exception) {
//                bugCatcher.logError(exception);
//                ArbilWindowManager.getSingleInstance().addMessageDialogToQueue("Failed to load sample", "Sample Diagram");
//            }
                // todo: finish testing this action
                throw new EntityServiceException("Test throw of EntityServiceException");
            } catch (EntityServiceException entityServiceException) {
                dialogHandler.addMessageDialogToQueue("Failed to open diagram: " + entityServiceException.getMessage(), "Open Diagram Error");
            }
        }
    }
}
