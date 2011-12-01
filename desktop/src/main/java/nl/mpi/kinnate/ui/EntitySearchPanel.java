package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nl.mpi.arbil.data.ArbilNode;
import nl.mpi.kinnate.data.KinTreeNode;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.svg.GraphPanel;

/**
 *  Document   : EntitySearchPanel
 *  Created on : Mar 14, 2011, 4:01:11 PM
 *  Author     : Peter Withers
 */
public class EntitySearchPanel extends JPanel {

    private EntityCollection entityCollection;
    private KinTree resultsTree;
    private JTextArea resultsArea = new JTextArea();
    private JTextField searchField;
    private JProgressBar progressBar;
    private JButton searchButton;
    private JPanel searchPanel;
    private GraphPanel graphPanel;

    public EntitySearchPanel(EntityCollection entityCollection, KinDiagramPanel kinDiagramPanel, GraphPanel graphPanel) {
        this.entityCollection = entityCollection;
        this.graphPanel = graphPanel;
        this.setLayout(new BorderLayout());
        resultsTree = new KinTree(kinDiagramPanel, graphPanel);
        resultsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Test Tree"), true));
        resultsTree.setRootVisible(false);
        // resultsTree.requestResort();// this resort is unrequred
        JLabel searchLabel = new JLabel("Search Entity Names");
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    EntitySearchPanel.this.performSearch();
                }
                super.keyReleased(e);
            }
        });
        progressBar = new JProgressBar();
        searchButton = new JButton("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EntitySearchPanel.this.performSearch();
            }
        });
        searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchLabel, BorderLayout.PAGE_START);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.PAGE_END);
        this.add(searchPanel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(resultsTree), BorderLayout.CENTER);
        this.add(resultsArea, BorderLayout.PAGE_END);
    }

    public void setTransferHandler(KinDragTransferHandler dragTransferHandler) {
        resultsTree.setTransferHandler(dragTransferHandler);
        resultsTree.setDragEnabled(true);
    }

    protected void performSearch() {
        searchPanel.remove(searchButton);
        progressBar.setIndeterminate(true);
        searchPanel.add(progressBar, BorderLayout.PAGE_END);
        searchPanel.revalidate();
        new Thread() {

            @Override
            public void run() {
                ArrayList<ArbilNode> resultsArray = new ArrayList<ArbilNode>();
                EntityData[] searchResults = entityCollection.getEntityByKeyWord(searchField.getText(), graphPanel.getIndexParameters());
                resultsArea.setText("Found " + searchResults.length + " entities\n");
                for (EntityData entityData : searchResults) {
//            if (resultsArray.size() < 1000) {
                    resultsArray.add(new KinTreeNode(entityData, graphPanel.getIndexParameters()));
//            } else {
//                resultsArea.append("results limited to 1000\n");
//                break;
//            }
                }
                resultsTree.rootNodeChildren = resultsArray.toArray(new KinTreeNode[]{});
                resultsTree.requestResort();
                searchPanel.remove(progressBar);
                searchPanel.add(searchButton, BorderLayout.PAGE_END);
                searchPanel.revalidate();
            }
        }.start();
    }
}
