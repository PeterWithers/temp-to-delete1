/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on Aug 16, 2010, 5:20:20 PM
 */
package nl.mpi.kinnate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import nl.mpi.arbil.ImdiTable;
import nl.mpi.arbil.ImdiTableModel;
import nl.mpi.arbil.ImdiTree;
import nl.mpi.arbil.LinorgSessionStorage;
import nl.mpi.arbil.PreviewSplitPanel;
import nl.mpi.arbil.data.ImdiLoader;
import nl.mpi.arbil.data.ImdiTreeObject;

/**
 *
 * @author petwit
 */
public class MainFrame extends javax.swing.JFrame {

    private ImdiTree leftTree;
    private GraphPanel graphPanel;
    private JungGraph jungGraph;
    private ImdiTable previewTable;
    private ImdiTableModel imdiTableModel;

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        leftTree = new ImdiTree();
        GraphPanel0 graphPanel0Deprecated;
        graphPanel0Deprecated = new GraphPanel0();
        graphPanel = new GraphPanel();
        // this data load should be elsewhere
        GraphData graphData = new GraphData();
        graphData.readData();
        graphPanel.drawNodes(graphData);
        jungGraph = new JungGraph();
        imdiTableModel = new ImdiTableModel();
        previewTable = new ImdiTable(imdiTableModel, "Preview Table");

        JScrollPane tableScrollPane = new JScrollPane(previewTable);
        jScrollPane1.getViewport().add(leftTree);
        jTabbedPane1.add("KinTypes", new KinTypeStringTestPanel());
        jTabbedPane1.add("Graph", graphPanel);
        jTabbedPane1.add("SVG2  (deprecated)", new GraphPanel1());
        jTabbedPane1.add("Jung", jungGraph);
        jTabbedPane1.add("Table", tableScrollPane);
        jTabbedPane1.add("SVG (deprecated)", graphPanel0Deprecated);

        leftTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Test Tree"), true));
        String[] treeNodesArray = LinorgSessionStorage.getSingleInstance().loadStringArray("KinGraphTree");
        if (treeNodesArray != null) {
            ArrayList<ImdiTreeObject> tempArray = new ArrayList<ImdiTreeObject>();
            for (String currentNodeString : treeNodesArray) {
                try {
                    tempArray.add(ImdiLoader.getSingleInstance().getImdiObject(null, new URI(currentNodeString)));
                } catch (URISyntaxException exception) {
                    System.err.println(exception.getMessage());
                    exception.printStackTrace();
                }
            }
            leftTree.rootNodeChildren = tempArray.toArray(new ImdiTreeObject[]{});
            imdiTableModel.removeAllImdiRows();
            imdiTableModel.addImdiObjects(leftTree.rootNodeChildren);
        } //else {
        //   leftTree.rootNodeChildren = new ImdiTreeObject[]{graphPanel.imdiNode};
        // }
        leftTree.requestResort();

        PreviewSplitPanel.previewTable = previewTable;
        PreviewSplitPanel.previewTableShown = true;

        jSplitPane1.setDividerLocation(0.25);

        this.doLayout();
        this.pack();
    }

    private void startImport(final String importFileString) {
        new Thread() {

            @Override
            public void run() {
                JTextArea importTextArea = new JTextArea();
                JScrollPane importScrollPane = new JScrollPane(importTextArea);
                jTabbedPane1.add("Import", importScrollPane);
                jTabbedPane1.setSelectedComponent(importScrollPane);
                JProgressBar progressBar = new JProgressBar();
                progressBar.setVisible(true);
                new GedcomImporter().importTestFile(importTextArea, importFileString);
                progressBar.setVisible(false);
                String[] treeNodesArray = LinorgSessionStorage.getSingleInstance().loadStringArray("KinGraphTree");
                if (treeNodesArray != null) {
                    ArrayList<ImdiTreeObject> tempArray = new ArrayList<ImdiTreeObject>();
                    for (String currentNodeString : treeNodesArray) {
                        try {
                            tempArray.add(ImdiLoader.getSingleInstance().getImdiObject(null, new URI(currentNodeString)));
                        } catch (URISyntaxException exception) {
                            System.err.println(exception.getMessage());
                            exception.printStackTrace();
                        }
                    }
                    leftTree.rootNodeChildren = tempArray.toArray(new ImdiTreeObject[]{});
                    imdiTableModel.removeAllImdiRows();
                    imdiTableModel.addImdiObjects(leftTree.rootNodeChildren);
                }
                leftTree.requestResort();
                GraphData graphData = new GraphData();
                graphData.readData();
                graphPanel.drawNodes(graphData);
            }
        }.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setLeftComponent(jScrollPane1);
        jSplitPane1.setRightComponent(jTabbedPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem1.setText("Import Gedcom Torture File");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Import Gedcom Simple File");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        startImport("/TestGED/TGC55C.ged");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        startImport("/TestGED/wiki-test-ged.ged");
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
