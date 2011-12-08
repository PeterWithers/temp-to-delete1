package nl.mpi.kinnate.ui.menu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import nl.mpi.kinnate.svg.GraphPanel;
import nl.mpi.kinnate.svg.GraphPanelSize;
import nl.mpi.kinnate.ui.window.AbstractDiagramManager;

/**
 *  Document   : DiagramOptionsMenu
 *  Created on : Dec 8, 2011, 10:35:50 AM
 *  Author     : Peter Withers
 */
public class DiagramOptionsMenu extends JMenu {

    private AbstractDiagramManager diagramWindowManager;
    // menu items
//    private JCheckBoxMenuItem snapToGridMenuItem;
//    private JCheckBoxMenuItem showBorderMenuItem;
//    private JCheckBoxMenuItem highlightRelationsMenuItem;
//    private JCheckBoxMenuItem showKinTermLinesMenuItem;
//    private JCheckBoxMenuItem showSanguineLinesMenuItem;
//    private JCheckBoxMenuItem showLabelssMenuItem;
//    private JCheckBoxMenuItem showKinTypeLabelssMenuItem;
////    private JCheckBoxMenuItem showKinTermLabelssMenuItem;
//    private JCheckBoxMenuItem showIdLabelsMenuItem;
//    private JCheckBoxMenuItem showDateLabelsMenuItem;
//    private JCheckBoxMenuItem showArchiveLinksMenuItem;
////    private JCheckBoxMenuItem showResourceLinksMenuItem;

    public DiagramOptionsMenu(AbstractDiagramManager diagramWindowManager) {
        this.diagramWindowManager = diagramWindowManager;
        this.setText("Diagram Options");

        this.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                setupMenuItems(null, null);
//                snapToGridMenuItem.setEnabled(graphPanel != null);
//                showBorderMenuItem.setEnabled(graphPanel != null);
//                highlightRelationsMenuItem.setEnabled(graphPanel != null);
//                showKinTermLinesMenuItem.setEnabled(graphPanel != null);
//                showSanguineLinesMenuItem.setEnabled(graphPanel != null);
//                showLabelssMenuItem.setEnabled(graphPanel != null);
//                showKinTypeLabelssMenuItem.setEnabled(graphPanel != null);
////   showKinTermLabelssMenuItem.setEnabled(graphPanel!=null);
//                showIdLabelsMenuItem.setEnabled(graphPanel != null);
//                showDateLabelsMenuItem.setEnabled(graphPanel != null);
//                showArchiveLinksMenuItem.setEnabled(graphPanel != null);
////   showResourceLinksMenuItem.setEnabled(graphPanel!=null);
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });
    }

    private void setupMenuItems(final GraphPanel graphPanel, final GraphPanelSize graphPanelSize) {
        JCheckBoxMenuItem snapToGridMenuItem = new JCheckBoxMenuItem("Snap To Grid");
        snapToGridMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphPanel.dataStoreSvg.snapToGrid = !graphPanel.dataStoreSvg.snapToGrid;
            }
        });
        this.add(snapToGridMenuItem);

        JCheckBoxMenuItem highlightRelationsMenuItem = new JCheckBoxMenuItem("Highlight Selected Relations");
        highlightRelationsMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphPanel.dataStoreSvg.highlightRelationLines = !graphPanel.dataStoreSvg.highlightRelationLines;
                graphPanel.drawNodes();
            }
        });
        this.add(highlightRelationsMenuItem);

        JCheckBoxMenuItem showBorderMenuItem = new JCheckBoxMenuItem("Show Diagram Border");
        showBorderMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphPanel.dataStoreSvg.showDiagramBorder = !graphPanel.dataStoreSvg.showDiagramBorder;
                graphPanel.drawNodes();
            }
        });
        this.add(showBorderMenuItem);

        JMenu diagramSizeMenuItem = new JMenu("Diagram Size");
        for (String currentString : graphPanelSize.getPreferredSizes()) {
            JMenuItem currentMenuItem = new JMenuItem(currentString);
            currentMenuItem.setActionCommand(currentString);
            currentMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String sizeString = evt.getActionCommand();
                    graphPanelSize.setSize(sizeString);
                    graphPanel.drawNodes();
                }
            });
            diagramSizeMenuItem.add(currentMenuItem);
        }
        this.add(diagramSizeMenuItem);
        JCheckBoxMenuItem showSanguineLinesMenuItem = new JCheckBoxMenuItem("Show Sanguin Lines");
        showSanguineLinesMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Hide/Show Sanguin Lines on the graph
                graphPanel.dataStoreSvg.showSanguineLines = !graphPanel.dataStoreSvg.showSanguineLines;
                graphPanel.drawNodes();
            }
        });
        this.add(showSanguineLinesMenuItem);
        JCheckBoxMenuItem showKinTermLinesMenuItem = new JCheckBoxMenuItem("Show Kin Term Lines");
        showKinTermLinesMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //  Hide/Show Kin Term lines on the graph
                graphPanel.dataStoreSvg.showKinTermLines = !graphPanel.dataStoreSvg.showKinTermLines;
                graphPanel.drawNodes();
            }
        });
        this.add(showKinTermLinesMenuItem);
        JCheckBoxMenuItem showLabelssMenuItem = new JCheckBoxMenuItem("Show Labels");
        showLabelssMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Hide/Show labels on the graph
                graphPanel.dataStoreSvg.showLabels = !graphPanel.dataStoreSvg.showLabels;
                graphPanel.drawNodes();
            }
        });
        this.add(showLabelssMenuItem);

        JCheckBoxMenuItem showKinTypeLabelssMenuItem = new JCheckBoxMenuItem("Show Kin Type Labels");
        showKinTypeLabelssMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Hide/Show Kin type labels on the graph
                graphPanel.dataStoreSvg.showKinTypeLabels = !graphPanel.dataStoreSvg.showKinTypeLabels;
                graphPanel.drawNodes();
            }
        });
        this.add(showKinTypeLabelssMenuItem);
//        showKinTermLabelssMenuItem = new JCheckBoxMenuItem("Show Kin Term Labels");
//        showKinTermLabelssMenuItem.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                // Hide/Show Kin Terms on the graph
//                graphPanel.dataStoreSvg.showKinTermLabels = !graphPanel.dataStoreSvg.showKinTermLabels;
//                graphPanel.drawNodes();
//            }
//        });
//        this.add(showKinTermLabelssMenuItem);

        // todo: this should not show when no ids are specified by the user
        JCheckBoxMenuItem showIdLabelsMenuItem = new JCheckBoxMenuItem("Show Id Labels");
        showIdLabelsMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Hide/Show transient node ids on the graph
                graphPanel.dataStoreSvg.showIdLabels = !graphPanel.dataStoreSvg.showIdLabels;
                graphPanel.drawNodes();
            }
        });
        this.add(showIdLabelsMenuItem);

        JCheckBoxMenuItem showDateLabelsMenuItem = new JCheckBoxMenuItem("Show Date Labels");
        showDateLabelsMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Hide/Show dates on the graph
                graphPanel.dataStoreSvg.showDateLabels = !graphPanel.dataStoreSvg.showDateLabels;
                graphPanel.drawNodes();
            }
        });
        this.add(showDateLabelsMenuItem);


        JCheckBoxMenuItem showArchiveLinksMenuItem = new JCheckBoxMenuItem("Show Archive Links");
        showArchiveLinksMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphPanel.dataStoreSvg.showArchiveLinks = !graphPanel.dataStoreSvg.showArchiveLinks;
                graphPanel.drawNodes();
            }
        });
        this.add(showArchiveLinksMenuItem);
//        showResourceLinksMenuItem = new JCheckBoxMenuItem("Show Archive Resource Links");
//        showResourceLinksMenuItem.addActionListener(new java.awt.event.ActionListener() {
//
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                graphPanel.dataStoreSvg.showResourceLinks = !graphPanel.dataStoreSvg.showResourceLinks;
//                graphPanel.drawNodes();
//            }
//        });
//        this.add(showResourceLinksMenuItem);


        snapToGridMenuItem.setSelected(graphPanel.dataStoreSvg.snapToGrid);
        highlightRelationsMenuItem.setSelected(graphPanel.dataStoreSvg.highlightRelationLines);
        showBorderMenuItem.setSelected(graphPanel.dataStoreSvg.showDiagramBorder);
        showSanguineLinesMenuItem.setSelected(graphPanel.dataStoreSvg.showSanguineLines);
        showKinTermLinesMenuItem.setSelected(graphPanel.dataStoreSvg.showKinTermLines);
        showLabelssMenuItem.setSelected(graphPanel.dataStoreSvg.showLabels);
        showKinTypeLabelssMenuItem.setSelected(graphPanel.dataStoreSvg.showKinTypeLabels);
//        showKinTermLabelssMenuItem.setSelected(graphPanel.dataStoreSvg.showKinTermLabels);
        showIdLabelsMenuItem.setSelected(graphPanel.dataStoreSvg.showIdLabels);
        showDateLabelsMenuItem.setSelected(graphPanel.dataStoreSvg.showDateLabels);
        showArchiveLinksMenuItem.setSelected(graphPanel.dataStoreSvg.showArchiveLinks);
//        showResourceLinksMenuItem.setSelected(graphPanel.dataStoreSvg.showResourceLinks);
    }
}