package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.util.ApplicationVersionManager;
import nl.mpi.kinnate.KinOathVersion;
import nl.mpi.kinnate.KinTermSavePanel;
import nl.mpi.kinnate.SavePanel;
import nl.mpi.kinnate.export.ExportToR;
import nl.mpi.kinnate.transcoder.DiagramTranscoder;

/*
 *  Document   : MainFrame
 *  Author     : Peter Withers
 *  Created on : Aug 16, 2010, 5:20:20 PM
 */
public class MainFrame extends javax.swing.JFrame {

//    private GraphPanel graphPanel;
//    private JungGraph jungGraph;
    private RecentFileMenu recentFileMenu;
    private EntityUploadPanel entityUploadPanel;

    /** Creates new form MainFrame */
    public MainFrame() {
        recentFileMenu = new RecentFileMenu(this);
        initComponents();
        ((EditMenu) editMenu).enableMenuKeys();
        final ApplicationVersionManager versionManager = new ApplicationVersionManager(new KinOathVersion());
        nl.mpi.kinnate.KinnateArbilInjector.injectHandlers(versionManager);
//        entityCollection = new EntityCollection();
//        GraphPanel0 graphPanel0Deprecated;
//        graphPanel0Deprecated = new GraphPanel0();
//        graphPanel = new GraphPanel();
        // this data load should be elsewhere
//        GraphData graphData = new GraphData();
//        graphData.readData();
//        graphPanel.drawNodes(graphData);
//        jungGraph = new JungGraph();

//        JScrollPane tableScrollPane = new JScrollPane(previewTable);       
        this.add(jTabbedPane1, BorderLayout.CENTER);
        KinDiagramPanel egoSelectionTestPanel = new KinDiagramPanel(null);
//        egoSelectionTestPanel.createDefaultGraph(KinDiagramPanel.defaultGraphString);
        jTabbedPane1.add("Unsaved Diagram", egoSelectionTestPanel);
//        jTabbedPane1.add("Kin Type String", new KinTypeStringTestPanel());
//        jTabbedPane1.add("Kin Term Mapping for KinType Strings", new KinTypeStringTestPanel());
        // todo: move these into the menu and only add on menu actions                
//        jTabbedPane1.add("Graph", graphPanel);
//        jTabbedPane1.add("SVG2  (deprecated)", new GraphPanel1());
//        jTabbedPane1.add("Jung", jungGraph);
//        jTabbedPane1.add("Table", tableScrollPane);
//        jTabbedPane1.add("SVG (deprecated)", graphPanel0Deprecated);
//        PreviewSplitPanel.previewTable = previewTable;
//        PreviewSplitPanel.previewTableShown = true;

//        System.out.println();

        jMenuBar1.add(new DiagramPanelsMenu(this));
        jMenuBar1.add(new KinTermsMenu(this)); // the main frame is stored in the kin term menu for later use
        jMenuBar1.add(new ArchiveMenu(this));
        this.doLayout();
        this.pack();
        ArbilWindowManager.getSingleInstance().setMessagesCanBeShown(true);
        setTitle(versionManager.getApplicationVersion().applicationTitle + " " + versionManager.getApplicationVersion().compileDate);
        // todo: Ticket #1067 set the icon for the application (if this is still required for the various OSs). This is not required for Mac but might be needed for windows or linux.
//        setIconImage(ArbilIcons.getSingleInstance().linorgIcon.getImage());
//	if (arbilMenuBar.checkNewVersionAtStartCheckBoxMenuItem.isSelected()) {
        // todo: Ticket #1066 add the check for updates and check now menu items
        versionManager.checkForUpdate();
//	}
    }

    private SavePanel getSavePanel(int tabIndex) {
        Object selectedComponent = jTabbedPane1.getComponentAt(tabIndex);
        SavePanel savePanel = null;
        if (selectedComponent instanceof SavePanel) {
            savePanel = (SavePanel) selectedComponent;
        }
        return savePanel;
    }

    public KinTermSavePanel getKinTermPanel() {
        Object selectedComponent = jTabbedPane1.getComponentAt(jTabbedPane1.getSelectedIndex());
        KinTermSavePanel kinTermSavePanel = null;
        if (selectedComponent instanceof KinTermSavePanel) {
            kinTermSavePanel = (KinTermSavePanel) selectedComponent;
        }
        return kinTermSavePanel;
    }

    public void openDiagram(File selectedFile, boolean saveToRecentMenu) {
        if (saveToRecentMenu) {
            // prevent files from the samples menu being added to the recent files menu
            recentFileMenu.addRecentFile(selectedFile.getAbsolutePath());
        }
        KinDiagramPanel egoSelectionTestPanel = new KinDiagramPanel(selectedFile);
//        egoSelectionTestPanel.setTransferHandler(dragTransferHandler);
        jTabbedPane1.add(selectedFile.getName(), egoSelectionTestPanel);
        jTabbedPane1.setSelectedComponent(egoSelectionTestPanel);
        egoSelectionTestPanel.drawGraph();
    }

    public void importEntities(String importPath) {
        new GedcomImportPanel(jTabbedPane1).startImportJar(importPath);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        ImportGedcomUrl = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        newDiagramMenuItem = new javax.swing.JMenuItem();
        openDiagram = new javax.swing.JMenuItem();
        openRecentMenu = recentFileMenu;
        jMenu1 = new SamplesFileMenu(this);
        jMenu2 = new ImportSamplesFileMenu(this);
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        entityUploadMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        saveDiagram = new javax.swing.JMenuItem();
        saveDiagramAs = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        exportToR = new javax.swing.JMenuItem();
        closeTabMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        exitApplication = new javax.swing.JMenuItem();
        editMenu = new EditMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        fileMenu.setText("File");
        fileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fileMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        ImportGedcomUrl.setText("Import Gedcom Samples (from internet)");
        ImportGedcomUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportGedcomUrlActionPerformed(evt);
            }
        });
        fileMenu.add(ImportGedcomUrl);
        fileMenu.add(jSeparator1);

        newDiagramMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newDiagramMenuItem.setText("New");
        newDiagramMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDiagramMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newDiagramMenuItem);

        openDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openDiagram.setText("Open");
        openDiagram.setActionCommand("open");
        openDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDiagramActionPerformed(evt);
            }
        });
        fileMenu.add(openDiagram);

        openRecentMenu.setText("Open Recent Diagram");
        openRecentMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRecentMenuActionPerformed(evt);
            }
        });
        fileMenu.add(openRecentMenu);

        jMenu1.setText("Open Sample Diagram");
        fileMenu.add(jMenu1);

        jMenu2.setText("Import Sample Data");
        fileMenu.add(jMenu2);
        fileMenu.add(jSeparator2);

        entityUploadMenuItem.setText("Entity Upload");
        entityUploadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entityUploadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(entityUploadMenuItem);
        fileMenu.add(jSeparator4);

        saveDiagram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveDiagram.setText("Save");
        saveDiagram.setActionCommand("save");
        saveDiagram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDiagramActionPerformed(evt);
            }
        });
        fileMenu.add(saveDiagram);

        saveDiagramAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveDiagramAs.setText("Save As");
        saveDiagramAs.setActionCommand("saveas");
        saveDiagramAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDiagramAsActionPerformed(evt);
            }
        });
        fileMenu.add(saveDiagramAs);

        jMenuItem1.setText("Export as PDF");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        exportToR.setText("Export to R / SPSS");
        exportToR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToRActionPerformed(evt);
            }
        });
        fileMenu.add(exportToR);

        closeTabMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeTabMenuItem.setText("Close");
        closeTabMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTabMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeTabMenuItem);
        fileMenu.add(jSeparator3);

        exitApplication.setText("Exit");
        exitApplication.setActionCommand("exit");
        exitApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitApplicationActionPerformed(evt);
            }
        });
        fileMenu.add(exitApplication);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                editMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        editMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMenuActionPerformed(evt);
            }
        });
        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
    }//GEN-LAST:event_fileMenuActionPerformed

    private void openDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDiagramActionPerformed
        for (File selectedFile : ArbilWindowManager.getSingleInstance().showFileSelectBox("Open Kin Diagram", false, true, false)) {
            openDiagram(selectedFile, true);
        }
    }//GEN-LAST:event_openDiagramActionPerformed

    private void openRecentMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRecentMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_openRecentMenuActionPerformed

    private void saveDiagramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDiagramActionPerformed
        int tabIndex = Integer.valueOf(evt.getActionCommand());
        SavePanel savePanel = getSavePanel(tabIndex);
        savePanel.saveToFile();
    }//GEN-LAST:event_saveDiagramActionPerformed

    private void saveDiagramAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDiagramAsActionPerformed
        // todo: update the file select to limit to svg and test that a file has been selected
        // todo: move this into the arbil window manager and get the last used directory
        // todo: make sure the file has the svg suffix
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                return (file.getName().toLowerCase().endsWith(".svg"));
            }

            @Override
            public String getDescription() {
                return "Scalable Vector Graphics (SVG)";
            }
        });

        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            int tabIndex = Integer.valueOf(evt.getActionCommand());
            SavePanel savePanel = getSavePanel(tabIndex);
            savePanel.saveToFile(file);
            recentFileMenu.addRecentFile(file.getAbsolutePath());
            jTabbedPane1.setTitleAt(tabIndex, file.getName());
        } else {
            // todo: warn user that no file selected and so cannot save
        }
//        File selectedFile[] = LinorgWindowManager.getSingleInstance().showFileSelectBox("Save Kin Diagram", false, false, false);
//        if (selectedFile != null && selectedFile.length > 0) {
//            int tabIndex = Integer.valueOf(evt.getActionCommand());
//            SavePanel savePanel = getSavePanel(tabIndex);
//            savePanel.saveToFile(selectedFile[0]);
//            jTabbedPane1.setTitleAt(tabIndex, selectedFile[0].getName());
//        } else {
//            // todo: warn user that no file selected and so cannot save
//        }
    }//GEN-LAST:event_saveDiagramAsActionPerformed

    private void exitApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitApplicationActionPerformed
        // todo: check that things are saved and ask user if not
        System.exit(0);
    }//GEN-LAST:event_exitApplicationActionPerformed

    private void fileMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_fileMenuMenuSelected
        // set the save, save as and close text to include the tab to which the action will occur
        int selectedIndex = jTabbedPane1.getSelectedIndex();
        String currentTabText = jTabbedPane1.getTitleAt(selectedIndex);
        SavePanel savePanel = getSavePanel(selectedIndex);
        saveDiagramAs.setText("Save As (" + currentTabText + ")");
        saveDiagramAs.setActionCommand(Integer.toString(selectedIndex));
        saveDiagram.setText("Save (" + currentTabText + ")");
        saveDiagram.setActionCommand(Integer.toString(selectedIndex));
        closeTabMenuItem.setText("Close (" + currentTabText + ")");
        closeTabMenuItem.setActionCommand(Integer.toString(selectedIndex));
        if (savePanel != null) {
            saveDiagram.setEnabled(savePanel.hasSaveFileName() && savePanel.requiresSave());
            saveDiagramAs.setEnabled(true);
            exportToR.setEnabled(true);
            closeTabMenuItem.setEnabled(true);
        } else {
            saveDiagramAs.setEnabled(false);
            saveDiagram.setEnabled(false);
            exportToR.setEnabled(false);
            closeTabMenuItem.setEnabled(false);
        }
    }//GEN-LAST:event_fileMenuMenuSelected

    private void closeTabMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeTabMenuItemActionPerformed
        int tabIndex = Integer.valueOf(evt.getActionCommand());
        SavePanel savePanel = getSavePanel(tabIndex);
        if (savePanel.requiresSave()) {
            // todo: warn user to save
        }
        jTabbedPane1.remove(tabIndex);
    }//GEN-LAST:event_closeTabMenuItemActionPerformed

    private void newDiagramMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDiagramMenuItemActionPerformed
        KinDiagramPanel egoSelectionTestPanel = new KinDiagramPanel(null);
        jTabbedPane1.add("Unsaved Diagram", egoSelectionTestPanel);
        jTabbedPane1.setSelectedComponent(egoSelectionTestPanel);
    }//GEN-LAST:event_newDiagramMenuItemActionPerformed

    private void ImportGedcomUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportGedcomUrlActionPerformed
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
            new GedcomImportPanel(jTabbedPane1).startImport(importUrlString);
        }
    }//GEN-LAST:event_ImportGedcomUrlActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        // todo: implement pdf export
        new DiagramTranscoder().saveAsPdf((SavePanel) jTabbedPane1.getSelectedComponent());
        new DiagramTranscoder().saveAsJpg((SavePanel) jTabbedPane1.getSelectedComponent());
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void editMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMenuActionPerformed
    }//GEN-LAST:event_editMenuActionPerformed

    private void editMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_editMenuMenuSelected
//        // init the menu and pass it the current save panel
////        int tabIndex = Integer.valueOf(jTabbedPane1.getSelectedIndex());
////        SavePanel savePanel = getSavePanel(tabIndex);
// todo: pass the current tab selection to the menu         ((EditMenu) editMenu).initMenu();
    }//GEN-LAST:event_editMenuMenuSelected

    private void exportToRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToRActionPerformed
        new ExportToR().doExport(this, getKinTermPanel());
    }//GEN-LAST:event_exportToRActionPerformed

    private void entityUploadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entityUploadMenuItemActionPerformed
        if (entityUploadPanel == null) {
            entityUploadPanel = new EntityUploadPanel();
            jTabbedPane1.add("Entity Upload", entityUploadPanel);
        }
        jTabbedPane1.setSelectedComponent(entityUploadPanel);
//        JDialog uploadDialog = new JDialog(this, "Entity Upload", true);
//        uploadDialog.setContentPane(new EntityUploadPanel());
//        uploadDialog.setLocationRelativeTo(this);
//        uploadDialog.setPreferredSize(new Dimension(100, 150));
//        uploadDialog.setVisible(true);
    }//GEN-LAST:event_entityUploadMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ImportGedcomUrl;
    private javax.swing.JMenuItem closeTabMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem entityUploadMenuItem;
    private javax.swing.JMenuItem exitApplication;
    private javax.swing.JMenuItem exportToR;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem newDiagramMenuItem;
    private javax.swing.JMenuItem openDiagram;
    private javax.swing.JMenu openRecentMenu;
    private javax.swing.JMenuItem saveDiagram;
    private javax.swing.JMenuItem saveDiagramAs;
    // End of variables declaration//GEN-END:variables
}
