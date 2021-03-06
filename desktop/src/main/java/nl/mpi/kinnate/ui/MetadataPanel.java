/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
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
package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.data.ArbilNode;
import nl.mpi.arbil.data.ContainerNode;
import nl.mpi.arbil.ui.ArbilTable;
import nl.mpi.arbil.ui.ArbilTableController;
import nl.mpi.arbil.ui.ArbilTableModel;
import nl.mpi.arbil.ui.ArbilTree;
import nl.mpi.arbil.ui.ArbilTreeController;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.ui.ImageBoxRenderer;
import nl.mpi.arbil.ui.PreviewSplitPanel;
import nl.mpi.arbil.userstorage.SessionStorage;
import nl.mpi.arbil.util.MessageDialogHandler;
import nl.mpi.arbil.util.TreeHelper;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.kindata.ExternalLink;
import nl.mpi.kinnate.svg.GraphPanel;
import nl.mpi.kinnate.ui.menu.TableMenu;

/**
 * Created on : Oct 13, 2011, 5:06:28 PM
 *
 * @author Peter Withers
 */
public class MetadataPanel extends JPanel {

    private ArbilTree arbilTree;
//    JScrollPane tableScrollPane;
    private ArbilTableModel kinTableModel;
    private ArbilTableModel archiveTableModel;
    private JScrollPane kinTableScrollPane;
    private HidePane editorHidePane;
    private ArrayList<ArbilDataNode> metadataNodes = new ArrayList<ArbilDataNode>();
    private ArrayList<ArbilDataNode> archiveTreeNodes = new ArrayList<ArbilDataNode>();
    private ArrayList<ArbilDataNode> archiveRootNodes = new ArrayList<ArbilDataNode>();
    private ArrayList<SvgElementEditor> elementEditors = new ArrayList<SvgElementEditor>();
    //private DateEditorPanel dateEditorPanel;
    private ArbilDataNodeLoader dataNodeLoader;
    private final KinDiagramPanel kinDiagramPanel;
    private ContainerNode rootNode;
    private EntityCollection entityCollection;
    final private MessageDialogHandler dialogHandler;
    final SessionStorage sessionStorage;

    public MetadataPanel(GraphPanel graphPanel, final EntityCollection entityCollection, final KinDiagramPanel kinDiagramPanel, HidePane editorHidePane, TableCellDragHandler tableCellDragHandler, ArbilDataNodeLoader dataNodeLoader, ImageBoxRenderer imageBoxRenderer, final SessionStorage sessionStorage, final MessageDialogHandler dialogHandler, ArbilTreeController treeController, TreeHelper treeHelper, ArbilTableController arbilTableController, ArbilWindowManager windowManager) {
        this.arbilTree = new ArbilTree(treeController, treeHelper, new PreviewSplitPanel(windowManager, arbilTableController));
        this.kinDiagramPanel = kinDiagramPanel;
        this.entityCollection = entityCollection;
        this.dialogHandler = dialogHandler;
        this.sessionStorage = sessionStorage;
        rootNode = new ContainerNode(null, "links", null, new ArbilNode[]{});
        arbilTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(rootNode)));
        this.kinTableModel = new ArbilTableModel(imageBoxRenderer);
        this.archiveTableModel = new ArbilTableModel(imageBoxRenderer);
        this.dataNodeLoader = dataNodeLoader;
        //dateEditorPanel = new DateEditorPanel();
        ArbilTableController tableController = new ArbilTableController(treeHelper, dialogHandler, windowManager) {

            // checkPopup is used to replace the Arbil context menu, as a result this override needs to copy other things that are done in the super class. So it would be better in the future to make the menu more formally addable to the table. 
            @Override
            public void checkPopup(java.awt.event.MouseEvent evt, boolean checkSelection) {
                final Object source = evt.getSource();
                if (source instanceof ArbilTable) {
                    ArbilTable targetTable = (ArbilTable) source;
                    if (evt.isPopupTrigger()) {
                        java.awt.Point p = evt.getPoint();
                        int clickedRow = targetTable.rowAtPoint(p);
                        int clickedColumn = targetTable.columnAtPoint(p);
                        // set the clicked cell selected
                        boolean clickedRowAlreadySelected = targetTable.isRowSelected(clickedRow);

                        if (checkSelection && !evt.isShiftDown() && !evt.isControlDown()) {
                            // if it is the right mouse button and there is already a selection then do not proceed in changing the selection
                            if (!(((evt.isPopupTrigger() /* evt.getButton() == MouseEvent.BUTTON3 || evt.isMetaDown() */) && clickedRowAlreadySelected))) {
                                if (clickedRow > -1 & clickedRow > -1) {
                                    // if the modifier keys are down then leave the selection alone for the sake of more normal behaviour
                                    targetTable.getSelectionModel().clearSelection();
                                    // make sure the clicked cell is selected
//                        System.out.println("clickedRow: " + clickedRow + " clickedRow: " + clickedRow);
//                        getSelectionModel().addSelectionInterval(clickedRow, clickedRow);
//                        getColumnModel().getSelectionModel().addSelectionInterval(clickedColumn, clickedColumn);
                                    targetTable.changeSelection(clickedRow, clickedColumn, false, evt.isShiftDown());
                                    // make sure the clicked cell is the lead selection
//                    getSelectionModel().setLeadSelectionIndex(rowIndex);
//                    getColumnModel().getSelectionModel().setLeadSelectionIndex(colIndex);
                                }
                            }
                        }
                    }

                    if (evt.isPopupTrigger() /* evt.getButton() == MouseEvent.BUTTON3 || evt.isMetaDown() */) {
//                    targetTable = (JTable) evt.getComponent();
//                    System.out.println("set the current table");

                        TableCellEditor tableCellEditor = targetTable.getCellEditor();
                        if (tableCellEditor != null) {
                            tableCellEditor.stopCellEditing();
                        }
                        new TableMenu(sessionStorage, dialogHandler, entityCollection, kinDiagramPanel, targetTable.getSelectedRowsFromTable(), targetTable.getSelectedFields()).show(targetTable, evt.getX(), evt.getY());
//                    new TableContextMenu(this).show(evt.getX(), evt.getY());
                        //new OldContextMenu().showTreePopup(evt.getSource(), evt.getX(), evt.getY());
                    }
                }
            }
        };
        ArbilTable kinTable = new ArbilTable(kinTableModel, tableController, "Selected Nodes");
        ArbilTable archiveTable = new ArbilTable(archiveTableModel, arbilTableController, "Selected Nodes");
        this.arbilTree.setCustomPreviewTable(archiveTable);
        kinTable.setTransferHandler(tableCellDragHandler);
        kinTable.setDragEnabled(true);

        this.editorHidePane = editorHidePane;
        this.setLayout(new BorderLayout());
        kinTableScrollPane = new JScrollPane(kinTable);
        JScrollPane archiveTableScrollPane = new JScrollPane(archiveTable);
        this.add(archiveTableScrollPane, BorderLayout.CENTER);
        this.add(arbilTree, BorderLayout.LINE_START);
    }

    public void removeAllEditors() {
        while (!elementEditors.isEmpty()) {
            editorHidePane.removeTab(elementEditors.remove(0));
        }
    }

    public void removeAllArbilDataNodeRows() {
        kinTableModel.removeAllArbilDataNodeRows();
        archiveTableModel.removeAllArbilDataNodeRows();
        for (ArbilDataNode arbilDataNode : metadataNodes) {
            if (arbilDataNode.getParentDomNode().getNeedsSaveToDisk(false)) {
                // reloading will first check if a save is required then save and reload
                dataNodeLoader.requestReload((ArbilDataNode) arbilDataNode.getParentDomNode());
            }
        }
        metadataNodes.clear();
    }

    public void addArbilDataNode(ArbilDataNode arbilDataNode) {
        archiveTableModel.addSingleArbilDataNode(arbilDataNode);
        archiveRootNodes.clear(); // do not show the tree for archive tree selections
        metadataNodes.add(arbilDataNode);
    }

    public void addEntityDataNode(EntityData entityData) {
        File entityFile = entityData.getUniqueIdentifier().getFileInProject(entityCollection.getProjectRecord());
        System.out.println("entity path: " + entityFile);
        if (entityFile != null && entityFile.exists()) {
            ArbilDataNode arbilDataNode = dataNodeLoader.getArbilDataNode(null, entityFile.toURI());
            // register this node with the graph panel
            kinDiagramPanel.registerArbilNode(entityData.getUniqueIdentifier(), arbilDataNode);
            kinTableModel.addSingleArbilDataNode(arbilDataNode);
            metadataNodes.add(arbilDataNode);
            // add the corpus links to the other table
            if (entityData.externalLinks != null) {
                for (ExternalLink archiveLink : entityData.externalLinks) {
                    ArbilDataNode archiveLinkNode = dataNodeLoader.getArbilDataNode(null, archiveLink.getLinkUri());
                    // todo: we do not register this node with the graph panel because it is not rendered on the graph, but if the name of the node changes then it should be updated in the tree which is not yet handled
                    archiveTableModel.addSingleArbilDataNode(archiveLinkNode);
                    archiveTreeNodes.add(archiveLinkNode);
                    archiveRootNodes.add(archiveLinkNode.getParentDomNode());
                    metadataNodes.add(archiveLinkNode);
                }
            }
        } else {
            dialogHandler.addMessageDialogToQueue("One or more items could not be displayed in the table.", "Table View");
        }
    }

//    public void setDateEditorEntities(ArrayList<EntityData> selectedEntities) {
//        if (selectedEntities.isEmpty()) {
//            editorHidePane.removeTab(dateEditorPanel);
//        } else {
//            dateEditorPanel.setEntities(selectedEntities);
//            editorHidePane.addTab("Date Editor", dateEditorPanel);
//        }
//    }
    public void addTab(String labelString, SvgElementEditor elementEditor) {
        editorHidePane.addTab(labelString, elementEditor);
        elementEditors.add(elementEditor);
    }

    public void removeTab(Component elementEditor) {
        editorHidePane.removeTab(elementEditor);
    }

    public void updateEditorPane() {
        // todo: add only imdi nodes to the tree and the root node of them
        // todo: maybe have a table for entities and one for achive metdata
        if (archiveTableModel.getArbilDataNodeCount() > 0) {
            editorHidePane.addTab("External Links", this);
        } else {
            removeTab(this);
        }
        if (kinTableModel.getArbilDataNodeCount() > 0) {
            editorHidePane.addTab("Kinship Data", kinTableScrollPane);
            editorHidePane.setSelectedComponent(kinTableScrollPane);
        } else {
            removeTab(kinTableScrollPane);
        }
        if (!archiveRootNodes.isEmpty()) {
            // todo: highlight or select the sub nodes that are actually linked
            rootNode.setChildNodes(archiveRootNodes.toArray(new ArbilNode[]{}));
            arbilTree.requestResort();
        }
        arbilTree.setVisible(!archiveRootNodes.isEmpty());
        editorHidePane.setHiddeState();
    }
}
