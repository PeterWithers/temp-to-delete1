/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.kinnate.ui.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.JMenu;
import javax.swing.filechooser.FileFilter;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.arbil.util.BugCatcherManager;
import nl.mpi.arbil.util.MessageDialogHandler;
import nl.mpi.kinnate.SavePanel;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.entityindexer.EntityServiceException;
import nl.mpi.kinnate.export.ExportToR;
import nl.mpi.kinnate.gedcomimport.ImportException;
import nl.mpi.kinnate.projects.ProjectManager;
import nl.mpi.kinnate.svg.DataStoreSvg;
import nl.mpi.kinnate.svg.DiagramTranscoder;
import nl.mpi.kinnate.ui.DiagramTranscoderPanel;
import nl.mpi.kinnate.ui.ImportSamplesFileMenu;
import nl.mpi.kinnate.ui.KinDiagramPanel;
import nl.mpi.kinnate.ui.window.AbstractDiagramManager;
import org.apache.batik.transcoder.TranscoderException;

/**
 * Document : FileMenu Created on : Dec 1, 2011, 4:04:06 PM
 *
 * @author Peter Withers
 */
public class FileMenu extends javax.swing.JMenu {

    private static final ResourceBundle menus = ResourceBundle.getBundle("nl/mpi/kinoath/localisation/Menus");
//    private javax.swing.JMenuItem importGedcomUrl;
    private javax.swing.JMenuItem importGedcomFile;
//    private javax.swing.JMenuItem importCsvFile;
    private javax.swing.JMenuItem closeTabMenuItem;
//    private javax.swing.JMenuItem entityUploadMenuItem;
    private javax.swing.JMenuItem exitApplication;
    private javax.swing.JMenuItem exportToR;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuItem newDiagramMenuItem;
    private javax.swing.JMenuItem openDiagram;
    private RecentFileMenu recentFileMenu;
    private javax.swing.JMenuItem projectNewMenu;
    private javax.swing.JMenuItem projectOpenMenu;
    private ProjectFileMenu projectRecentMenu;
    private javax.swing.JMenuItem saveAsGlobalDefaultMenuItem;
    private javax.swing.JMenuItem saveAsProjectDefaultMenuItem;
    private javax.swing.JMenuItem saveDiagram;
    private javax.swing.JMenuItem saveDiagramAs;
    private javax.swing.JMenuItem savePdfMenuItem;
    private AbstractDiagramManager diagramWindowManager;
    private SessionStorage sessionStorage;
    private MessageDialogHandler dialogHandler; //ArbilWindowManager
    private Component parentComponent;
    private ProjectManager projectManager;

    public FileMenu(AbstractDiagramManager diagramWindowManager, SessionStorage sessionStorage, MessageDialogHandler dialogHandler, Component parentComponent, ProjectManager projectManager) {
        this.diagramWindowManager = diagramWindowManager;
        this.sessionStorage = sessionStorage;
        this.projectManager = projectManager;
        this.dialogHandler = dialogHandler;
        this.diagramWindowManager = diagramWindowManager;
        this.parentComponent = parentComponent;
//        importGedcomUrl = new javax.swing.JMenuItem();
        importGedcomFile = new javax.swing.JMenuItem();
//        importCsvFile = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        newDiagramMenuItem = new javax.swing.JMenuItem();
        openDiagram = new javax.swing.JMenuItem();
        recentFileMenu = new RecentFileMenu(diagramWindowManager, sessionStorage, parentComponent, dialogHandler);
        projectNewMenu = new javax.swing.JMenuItem();
        projectOpenMenu = new javax.swing.JMenuItem();
        projectRecentMenu = new ProjectFileMenu(diagramWindowManager, parentComponent, dialogHandler, projectManager);
        jMenu1 = new SamplesFileMenu(diagramWindowManager, dialogHandler, parentComponent);
        jMenu2 = new ImportSamplesFileMenu(diagramWindowManager, dialogHandler, parentComponent);
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
//        entityUploadMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        saveDiagram = new javax.swing.JMenuItem();
        saveDiagramAs = new javax.swing.JMenuItem();
        savePdfMenuItem = new javax.swing.JMenuItem();
        exportToR = new javax.swing.JMenuItem();
        closeTabMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        saveAsGlobalDefaultMenuItem = new javax.swing.JMenuItem();
        saveAsProjectDefaultMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        exitApplication = new javax.swing.JMenuItem();


        this.setText(menus.getString("FILE"));
        this.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fileMenuMenuSelected(evt);
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }

            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        newDiagramMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newDiagramMenuItem.setText(menus.getString("NEW (DEFAULT DIAGRAM)"));
        newDiagramMenuItem.setEnabled(KinDiagramPanel.getGlobalDefaultDiagramFile(sessionStorage).exists());
        newDiagramMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDiagramMenuItemActionPerformed(evt);
            }
        });
        this.add(newDiagramMenuItem);

//        JMenuItem wizardMenuItem = new JMenuItem();
//        wizardMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
//        wizardMenuItem.setText("Start New Diagram Wizard");
//        wizardMenuItem.setEnabled(KinDiagramPanel.getGlobalDefaultDiagramFile(sessionStorage).exists());
//        wizardMenuItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
////                newDiagramMenuItemActionPerformed(evt);
//            }
//        });
//        this.add(wizardMenuItem);

//        JMenu freeformDiagramMenuItem = new DocumentNewMenu(diagramWindowManager, parentComponent, dialogHandler);
//        freeformDiagramMenuItem.setText("New Freform Diagram");
//        this.add(freeformDiagramMenuItem);

        JMenu projectDiagramMenuItem = new DocumentNewMenu(diagramWindowManager, parentComponent, dialogHandler);
        projectDiagramMenuItem.setText(menus.getString("NEW DIAGRAM OF TYPE"));
        this.add(projectDiagramMenuItem);

        openDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openDiagram.setText(menus.getString("OPEN DIAGRAM"));
        openDiagram.setActionCommand("open");
        openDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDiagramActionPerformed(evt);
            }
        });
        this.add(openDiagram);


        this.add(recentFileMenu);

        jMenu1.setText(menus.getString("OPEN SAMPLE DIAGRAM"));
        this.add(jMenu1);

        this.add(new javax.swing.JPopupMenu.Separator());
        projectNewMenu.setText(menus.getString("NEW PROJECT"));
        projectNewMenu.setActionCommand("new");
        projectNewMenu.addActionListener(projectRecentMenu);
        this.add(projectNewMenu);
        projectOpenMenu.setText(menus.getString("OPEN PROJECT"));
        projectOpenMenu.setActionCommand("browse");
        projectOpenMenu.addActionListener(projectRecentMenu);
        this.add(projectOpenMenu);

        this.add(projectRecentMenu);

//        this.add(jSeparator1);

        importGedcomFile.setText(menus.getString("IMPORT GEDCOM / CSV / TIP FILE (INTO CURRENT PROJECT)"));
        importGedcomFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importGedcomFileActionPerformed(evt);
            }
        });
        this.add(importGedcomFile);

//        importCsvFile.setText("Import CSV File");
//        importCsvFile.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                importCsvFileActionPerformed(evt);
//            }
//        });
//        this.add(importCsvFile);

        jMenu2.setText(menus.getString("IMPORT SAMPLE DATA (INTO CURRENT PROJECT)"));
        this.add(jMenu2);

//        importGedcomUrl.setText("Import Gedcom Samples (from internet)");
//        importGedcomUrl.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                importGedcomUrlActionPerformed(evt);
//            }
//        });
//        importGedcomUrl.setEnabled(false);
//        this.add(importGedcomUrl);
        SavePanel currentSavePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);
        if (currentSavePanel == null || currentSavePanel.getGraphPanel().dataStoreSvg.diagramMode != DataStoreSvg.DiagramMode.KinTypeQuery) {
            jMenu2.setEnabled(false);
            importGedcomFile.setEnabled(false);
        }
        this.add(jSeparator2);

//        entityUploadMenuItem.setText("Entity Upload");
//        entityUploadMenuItem.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                entityUploadMenuItemActionPerformed(evt);
//            }
//        });
//        this.add(entityUploadMenuItem);
//        this.add(jSeparator4);

        saveDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveDiagram.setText(menus.getString("SAVE"));
        saveDiagram.setActionCommand("save");
        saveDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDiagramActionPerformed(evt);
            }
        });
        this.add(saveDiagram);

        saveDiagramAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveDiagramAs.setText(menus.getString("SAVE AS"));
        saveDiagramAs.setActionCommand("saveas");
        saveDiagramAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDiagramAsActionPerformed(evt);
            }
        });
        this.add(saveDiagramAs);

        savePdfMenuItem.setText(menus.getString("EXPORT AS PDF/JPEG/PNG/TIFF"));
        savePdfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePdfMenuItemActionPerformed(evt);
            }
        });
        this.add(savePdfMenuItem);

        exportToR.setText(menus.getString("EXPORT FOR R / SPSS"));
        exportToR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToRActionPerformed(evt);
            }
        });
        this.add(exportToR);

        closeTabMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeTabMenuItem.setText(menus.getString("CLOSE"));
        closeTabMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTabMenuItemActionPerformed(evt);
            }
        });
        this.add(closeTabMenuItem);
        this.add(jSeparator3);

        saveAsGlobalDefaultMenuItem.setText(menus.getString("SAVE AS GLOBAL DEFAULT DIAGRAM"));
        saveAsGlobalDefaultMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsDefaultMenuItemActionPerformed(evt, true);
            }
        });
        this.add(saveAsGlobalDefaultMenuItem);

        saveAsProjectDefaultMenuItem.setText(menus.getString("SAVE AS PROJECT DEFAULT DIAGRAM"));
        saveAsProjectDefaultMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsDefaultMenuItemActionPerformed(evt, false);
            }
        });
        this.add(saveAsProjectDefaultMenuItem);
        this.add(jSeparator5);

        exitApplication.setText(menus.getString("EXIT"));
        exitApplication.setActionCommand("exit");
        exitApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitApplicationActionPerformed(evt);
            }
        });
        this.add(exitApplication);
    }

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private HashMap<String, FileFilter> getSvgFileFilter() {
        HashMap<String, FileFilter> fileFilterMap = new HashMap<String, FileFilter>(2);
        for (final String[] currentType : new String[][]{{menus.getString("KINSHIP DIAGRAM (SVG FORMAT)"), ".svg"}}) { // "Scalable Vector Graphics (SVG)";
            fileFilterMap.put(currentType[0], new FileFilter() {
                @Override
                public boolean accept(File selectedFile) {
                    final String extensionLowerCase = currentType[1].toLowerCase();
                    return (selectedFile.exists() && (selectedFile.isDirectory() || selectedFile.getName().toLowerCase().endsWith(extensionLowerCase)));
                }

                @Override
                public String getDescription() {
                    return currentType[0];
                }
            });
        }
        return fileFilterMap;
    }

    private void openDiagramActionPerformed(java.awt.event.ActionEvent evt) {
        final File[] selectedFilesArray = dialogHandler.showFileSelectBox(menus.getString("OPEN DIAGRAM"), false, true, getSvgFileFilter(), MessageDialogHandler.DialogueType.open, null);
        final Dimension parentSize = parentComponent.getSize();
        final Point parentLocation = parentComponent.getLocation();
        int offset = 10;
        final Rectangle windowRectangle = new Rectangle(parentLocation.x + offset, parentLocation.y + offset, parentSize.width - offset, parentSize.height - offset);
        if (selectedFilesArray != null) {
            for (File selectedFile : selectedFilesArray) {
                try {
                    diagramWindowManager.openDiagram(selectedFile.getName(), selectedFile.toURI(), true, windowRectangle);
                } catch (EntityServiceException entityServiceException) {
                    dialogHandler.addMessageDialogToQueue(java.text.MessageFormat.format(menus.getString("FAILED TO CREATE A NEW DIAGRAM: {0}"), new Object[]{entityServiceException.getMessage()}), "Open Diagram Error");
                }
            }
        }
    }

    private void saveDiagramActionPerformed(java.awt.event.ActionEvent evt) {
        int tabIndex = Integer.valueOf(evt.getActionCommand());
        SavePanel savePanel = diagramWindowManager.getSavePanel(tabIndex);
        savePanel.saveToFile();
    }

    private void saveDiagramAsActionPerformed(java.awt.event.ActionEvent evt) {
        final File[] selectedFilesArray = dialogHandler.showFileSelectBox(menus.getString("SAVE DIAGRAM AS"), false, false, getSvgFileFilter(), MessageDialogHandler.DialogueType.save, null);
        if (selectedFilesArray != null) {
            for (File selectedFile : selectedFilesArray) {
                if (!selectedFile.getName().toLowerCase().endsWith(".svg")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".svg");
                }
                int tabIndex = Integer.valueOf(evt.getActionCommand());
                SavePanel savePanel = diagramWindowManager.getSavePanel(tabIndex);
                savePanel.saveToFile(selectedFile);
                RecentFileMenu.addRecentFile(sessionStorage, selectedFile);
                diagramWindowManager.setDiagramTitle(tabIndex, selectedFile.getName());
            }
        }
    }

    private void exitApplicationActionPerformed(java.awt.event.ActionEvent evt) {
        // check that things are saved and ask user if not
        if (diagramWindowManager.offerUserToSaveAll()) {
            System.exit(0);
        }
    }

    private void fileMenuMenuSelected(javax.swing.event.MenuEvent evt) {
        // set the save, save as and close text to include the tab to which the action will occur
        SavePanel savePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);
        int selectedIndex = diagramWindowManager.getSavePanelIndex(parentComponent);
        String currentTabText = diagramWindowManager.getSavePanelTitle(selectedIndex);
        if (selectedIndex > -1) {
            saveDiagramAs.setText(java.text.MessageFormat.format(menus.getString("SAVE AS ({0})"), new Object[]{currentTabText}));
            saveDiagramAs.setActionCommand(Integer.toString(selectedIndex));
            saveDiagram.setText(java.text.MessageFormat.format(menus.getString("SAVE ({0})"), new Object[]{currentTabText}));
            saveDiagram.setActionCommand(Integer.toString(selectedIndex));
            closeTabMenuItem.setText(java.text.MessageFormat.format(menus.getString("CLOSE ({0})"), new Object[]{currentTabText}));
            closeTabMenuItem.setActionCommand(Integer.toString(selectedIndex));
            saveAsGlobalDefaultMenuItem.setText(java.text.MessageFormat.format(menus.getString("SET GLOBAL DEFAULT DIAGRAM AS ({0})"), new Object[]{currentTabText}));
            saveAsGlobalDefaultMenuItem.setActionCommand(Integer.toString(selectedIndex));
            saveAsProjectDefaultMenuItem.setText(java.text.MessageFormat.format(menus.getString("SET PROJECT DEFAULT DIAGRAM AS ({0})"), new Object[]{currentTabText}));
            saveAsProjectDefaultMenuItem.setActionCommand(Integer.toString(selectedIndex));
        }
        if (savePanel != null) {
            saveDiagram.setEnabled(savePanel.hasSaveFileName() && savePanel.requiresSave());
            saveDiagramAs.setEnabled(true);
            exportToR.setEnabled(true);
            closeTabMenuItem.setEnabled(true);
            saveAsGlobalDefaultMenuItem.setEnabled(true);
            saveAsProjectDefaultMenuItem.setEnabled(true);
            savePdfMenuItem.setEnabled(true);
        } else {
            saveDiagramAs.setEnabled(false);
            saveDiagram.setEnabled(false);
            exportToR.setEnabled(false);
            closeTabMenuItem.setEnabled(false);
            saveAsGlobalDefaultMenuItem.setEnabled(true);
            saveAsProjectDefaultMenuItem.setEnabled(true);
            savePdfMenuItem.setEnabled(false);
        }
    }

    private void closeTabMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        int tabIndex = Integer.valueOf(evt.getActionCommand());
        SavePanel savePanel = diagramWindowManager.getSavePanel(tabIndex);
        String diagramTitle = diagramWindowManager.getSavePanelTitle(tabIndex);
        boolean userCanceled = diagramWindowManager.offerUserToSave(savePanel, diagramTitle);
        if (!userCanceled) {
            diagramWindowManager.closeSavePanel(tabIndex);
        }
    }

    private void newDiagramMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        final Dimension parentSize = parentComponent.getSize();
        final Point parentLocation = parentComponent.getLocation();
        int offset = 10;
        try {
            diagramWindowManager.newDiagram(new Rectangle(parentLocation.x + offset, parentLocation.y + offset, parentSize.width - offset, parentSize.height - offset), null);
        } catch (EntityServiceException entityServiceException) {
            dialogHandler.addMessageDialogToQueue(java.text.MessageFormat.format(menus.getString("FAILED TO CREATE A NEW DIAGRAM: {0}"), new Object[]{entityServiceException.getMessage()}), "Open Diagram Error");
        }
    }

    private void importGedcomFileActionPerformed(java.awt.event.ActionEvent evt) {
        HashMap<String, FileFilter> fileFilterMap = new HashMap<String, FileFilter>(2);
        fileFilterMap.put("importfiles", new FileFilter() {
            @Override
            public boolean accept(File selectedFile) {
                if (selectedFile.isDirectory()) {
                    return true;
                }
                final String currentFileName = selectedFile.getName().toLowerCase();
                if (currentFileName.endsWith(".gedcom")) {
                    return true;
                }
                if (currentFileName.endsWith(".ged")) {
                    return true;
                }
                if (currentFileName.endsWith(".txt")) {
                    return true;
                }
                if (currentFileName.endsWith(".csv")) {
                    return true;
                }
                if (currentFileName.endsWith(".tip")) {
                    return true;
                }
                if (currentFileName.endsWith(".kinoath")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "GEDCOM, CSV, TIP Kinship Data";
            }
        });
        File[] importFiles = dialogHandler.showFileSelectBox(menus.getString("IMPORT KINSHIP DATA"), false, true, fileFilterMap, MessageDialogHandler.DialogueType.open, null);
        if (importFiles != null) {
            if (importFiles.length == 0) {
                dialogHandler.addMessageDialogToQueue(menus.getString("NO FILES SELECTED FOR IMPORT"), menus.getString("IMPORT KINSHIP DATA"));
            } else {
                SavePanel currentSavePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);
                if (currentSavePanel instanceof KinDiagramPanel) {
                    final KinDiagramPanel diagramPanel = (KinDiagramPanel) currentSavePanel;
                    for (File importFile : importFiles) {
                        try {
                            diagramWindowManager.openImportPanel(importFile, diagramPanel, getEntityCollection());
                        } catch (ImportException exception1) {
                            dialogHandler.addMessageDialogToQueue(exception1.getMessage() + "\n" + importFile.getAbsolutePath(), menus.getString("IMPORT FILE"));
                        }
                    }
                } // todo: while it would not happen, if we do land here then the user should be informed as to why
            }
        }
    }

    private void importCsvFileActionPerformed(java.awt.event.ActionEvent evt) {
        importGedcomFileActionPerformed(evt);
    }

    private void importGedcomUrlActionPerformed(java.awt.event.ActionEvent evt) {
        // todo: Ticket #1297 either remove this or change it so it does not open so many tabs / windows
        String[] importList = new String[]{"http://gedcomlibrary.com/gedcoms.html",
            "http://GedcomLibrary.com/gedcoms/gl120365.ged", //	Tammy Carter Inman
            "http://GedcomLibrary.com/gedcoms/gl120366.ged", //	Luis Lemonnier
            "http://GedcomLibrary.com/gedcoms/gl120367.ged", //	Cheryl Marion Follansbee
            // New England Genealogical Detective
            "http://GedcomLibrary.com/gedcoms/gl120368.ged", //	Phil Willaims
            "http://GedcomLibrary.com/gedcoms/gl120369.ged", //	Francisco Casta�eda
            "http://GedcomLibrary.com/gedcoms/gl120370.ged", //	Kim Carter
            "http://GedcomLibrary.com/gedcoms/gl120371.ged", //	Maria Perusia
            "http://GedcomLibrary.com/gedcoms/gl120372.ged", //	R. J. Bosman
            "http://GedcomLibrary.com/gedcoms/liverpool.ged", //	William Robinette
            "http://GedcomLibrary.com/gedcoms/misc2a.ged", //	William Robinette
            "http://GedcomLibrary.com/gedcoms/myline.ged", //	William Robinette

            // also look into http://gedcomlibrary.com/list.html for sample files
            "http://gedcomlibrary.com/gedcoms/gl120368.ged", //
            "http://GedcomLibrary.com/gedcoms/gl120367.ged", //
            "http://GedcomLibrary.com/gedcoms/liverpool.ged", //
            "http://GedcomLibrary.com/gedcoms/misc2a.ged", //
            "http://GedcomLibrary.com/gedcoms/gl120372.ged"};
        for (String importUrlString : importList) {
            SavePanel currentSavePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);
            if (currentSavePanel instanceof KinDiagramPanel) {
                final KinDiagramPanel diagramPanel = (KinDiagramPanel) currentSavePanel;
                try {
                    diagramWindowManager.openImportPanel(importUrlString, diagramPanel, getEntityCollection());
                } catch (ImportException exception1) {
                    dialogHandler.addMessageDialogToQueue(exception1.getMessage() + "\n" + importUrlString, menus.getString("IMPORT FILE"));
                }
            } // todo: while it would not happen, if we do land here then the user should be informed as to why
        }
    }

    private void savePdfMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

        final DiagramTranscoder diagramTranscoder = new DiagramTranscoder(diagramWindowManager.getCurrentSavePanel(parentComponent));
        DiagramTranscoderPanel diagramTranscoderPanel = new DiagramTranscoderPanel(diagramTranscoder);
        final File[] selectedFilesArray = dialogHandler.showFileSelectBox(menus.getString("EXPORT AS PDF/JPEG/PNG/TIFF"), false, false, null, MessageDialogHandler.DialogueType.save, diagramTranscoderPanel);
        if (selectedFilesArray != null) {
            try {
                for (File selectedFile : selectedFilesArray) {
                    diagramTranscoder.exportDiagram(selectedFile);
                }
            } catch (TranscoderException exception) {
                dialogHandler.addMessageDialogToQueue(exception.getMessage() + "\n" + menus.getString("THIS MAY OCCUR WHEN USING THE WEBSTART VERSION."), menus.getString("EXPORT IMAGE ERROR"));
                BugCatcherManager.getBugCatcher().logError(exception);
            } catch (IOException exception) {
                dialogHandler.addMessageDialogToQueue(exception.getMessage(), "Export Image Error");
                BugCatcherManager.getBugCatcher().logError(exception);
            }

        }
    }

    private void exportToRActionPerformed(java.awt.event.ActionEvent evt) {
        SavePanel currentSavePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);

        HashMap<String, FileFilter> fileFilterMap = new HashMap<String, FileFilter>(2);
        for (final String[] currentType : new String[][]{{menus.getString("DATA FRAME TAB-SEPARATED VALUES"), ".tab"}}) { // "Data Frame (CSV)"
            fileFilterMap.put(currentType[0], new FileFilter() {
                @Override
                public boolean accept(File selectedFile) {
                    final String extensionLowerCase = currentType[1].toLowerCase();
                    return (selectedFile.exists() && (selectedFile.isDirectory() || selectedFile.getName().toLowerCase().endsWith(extensionLowerCase)));
                }

                @Override
                public String getDescription() {
                    return currentType[0];
                }
            });
        }
        final File[] selectedFilesArray = dialogHandler.showFileSelectBox(menus.getString("EXPORT TAB-SEPARATED VALUES"), false, false, fileFilterMap, MessageDialogHandler.DialogueType.save, null);
        if (selectedFilesArray != null) {
            for (File selectedFile : selectedFilesArray) {
                if (!selectedFile.getName().toLowerCase().endsWith(".tab")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".tab");
                }
                new ExportToR(sessionStorage, dialogHandler).doExport(this, currentSavePanel, selectedFile);
            }
        }
    }

    private void entityUploadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        diagramWindowManager.openEntityUploadPanel(null, getEntityCollection());
    }

    private void saveAsDefaultMenuItemActionPerformed(java.awt.event.ActionEvent evt, boolean saveAsGlobal) {
        int tabIndex = Integer.valueOf(evt.getActionCommand());
        SavePanel savePanel = diagramWindowManager.getSavePanel(tabIndex);
        if (saveAsGlobal) {
            savePanel.saveToFile(KinDiagramPanel.getGlobalDefaultDiagramFile(sessionStorage));
        } else {
            savePanel.saveToFile(KinDiagramPanel.getDefaultDiagramFile(savePanel.getGraphPanel().dataStoreSvg.projectRecord));
        }
    }

    private EntityCollection getEntityCollection() {
        SavePanel currentSavePanel = diagramWindowManager.getCurrentSavePanel(parentComponent);
        if (currentSavePanel instanceof KinDiagramPanel) {
            final KinDiagramPanel diagramPanel = (KinDiagramPanel) currentSavePanel;
            return diagramPanel.getEntityCollection();
        } else {
            throw new UnsupportedOperationException("Cannot perform this menu action on this type of window");
        }
    }
}
