package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import nl.mpi.arbil.LinorgSessionStorage;
import nl.mpi.arbil.XsdChecker;
import nl.mpi.arbil.data.ImdiLoader;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.gedcomimport.GedcomImporter;

/**
 *  Document   : GedcomImportPanel
 *  Created on : Mar 14, 2011, 8:38:36 AM
 *  Author     : Peter Withers
 */
public class GedcomImportPanel extends JPanel {

    private EntityCollection entityCollection;
    private JTabbedPane jTabbedPane1;
    private JTextArea importTextArea;
    private JProgressBar progressBar;
    private JCheckBox overwriteOnImport;
    private JCheckBox validateImportedXml;
    private JButton startButton;

    public GedcomImportPanel(EntityCollection entityCollectionLocal, JTabbedPane jTabbedPaneLocal) {
        jTabbedPane1 = jTabbedPaneLocal;
        entityCollection = entityCollectionLocal;

//        private ImdiTree leftTree;
////    private GraphPanel graphPanel;
////    private JungGraph jungGraph;
//    private ImdiTable previewTable;
//    private ImdiTableModel imdiTableModel;
//    private DragTransferHandler dragTransferHandler;
    }

    protected JPanel getCreatedNodesPane(final GedcomImporter gedcomImporter) {
        JPanel createdNodesPanel = new JPanel();
        createdNodesPanel.setLayout(new BoxLayout(createdNodesPanel, BoxLayout.PAGE_AXIS));
        if (gedcomImporter.createdNodeIds.isEmpty()) {
            createdNodesPanel.add(new JLabel("No new data was imported, nothing to show in the graph."));
        } else {
            final ArrayList<JCheckBox> checkBoxArray = new ArrayList<JCheckBox>();
            for (String typeString : gedcomImporter.createdNodeIds.keySet()) {
                JCheckBox currentCheckBox = new JCheckBox(typeString + " ( x " + gedcomImporter.createdNodeIds.get(typeString).size() + ")");
                currentCheckBox.setActionCommand(typeString);
                checkBoxArray.add(currentCheckBox);
                createdNodesPanel.add(currentCheckBox);
            }
            JButton showButton = new JButton("Show selected types in graph");
            showButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> selectedIds = new ArrayList<String>();
                    for (JCheckBox currentCheckBox : checkBoxArray) {
                        if (currentCheckBox.isSelected()) {
                            selectedIds.addAll((gedcomImporter.createdNodeIds.get(currentCheckBox.getActionCommand())));
                        }
                    }
                    KinTypeEgoSelectionTestPanel egoSelectionTestPanel = new KinTypeEgoSelectionTestPanel(null);
                    egoSelectionTestPanel.setDisplayNodes(selectedIds.toArray(new String[]{}));
                    jTabbedPane1.add("Imported Entities", egoSelectionTestPanel);
                }
            });
            createdNodesPanel.add(showButton);
        }
        return createdNodesPanel;
    }

    public void startImport(String importUriString) {
        File cachedFile = LinorgSessionStorage.getSingleInstance().updateCache(importUriString, 30);
        startImport(cachedFile, null, importUriString);
    }

    public void startImportJar(String importFileString) {
        startImport(null, importFileString, importFileString);
    }

    private void startImport(final File importFile, final String importFileString, String importLabel) {
        if (importFile != null && !importFile.exists()) {
            GedcomImportPanel.this.add(new JLabel("File not found"));
        } else {
            importTextArea = new JTextArea();
            JScrollPane importScrollPane = new JScrollPane(importTextArea);
            GedcomImportPanel.this.setLayout(new BorderLayout());
            GedcomImportPanel.this.add(importScrollPane, BorderLayout.CENTER);
            jTabbedPane1.add("Import", GedcomImportPanel.this);
            jTabbedPane1.setSelectedComponent(GedcomImportPanel.this);
            progressBar = new JProgressBar(0, 100);
            GedcomImportPanel.this.add(progressBar, BorderLayout.PAGE_END);
            progressBar.setVisible(true);
            JPanel topPanel = new JPanel();
            overwriteOnImport = new JCheckBox("Overwrite Existing");
            startButton = new JButton("Start");
            topPanel.add(overwriteOnImport);
            validateImportedXml = new JCheckBox("Validate Xml");
            topPanel.add(validateImportedXml);
            topPanel.add(startButton);
            JPanel topOuterPanel = new JPanel(new BorderLayout());
            topOuterPanel.add(new JLabel(importLabel, JLabel.CENTER), BorderLayout.PAGE_START);
            topOuterPanel.add(topPanel, BorderLayout.CENTER);
            GedcomImportPanel.this.add(topOuterPanel, BorderLayout.PAGE_START);
            startButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    startButton.setEnabled(false);
                    overwriteOnImport.setEnabled(false);
                    validateImportedXml.setEnabled(false);
                    new Thread() {

                        @Override
                        public void run() {
                            boolean overwriteExisting = overwriteOnImport.isSelected();
                            GedcomImporter gedcomImporter = new GedcomImporter(overwriteExisting);
                            gedcomImporter.setProgressBar(progressBar);
                            URI[] treeNodesArray;
                            if (importFileString != null) {
                                treeNodesArray = gedcomImporter.importTestFile(importTextArea, importFileString);
                            } else {
                                treeNodesArray = gedcomImporter.importTestFile(importTextArea, importFile);
                            }
                            progressBar.setVisible(false);
                            boolean checkFilesAfterImport = validateImportedXml.isSelected();
                            if (treeNodesArray != null && checkFilesAfterImport) {
//                    ArrayList<ImdiTreeObject> tempArray = new ArrayList<ImdiTreeObject>();                    
                                int maxXsdErrorToShow = 3;
                                progressBar.setValue(0);
                                for (URI currentNodeUri : treeNodesArray) {
                                    progressBar.setValue(progressBar.getValue() + 1);
                                    if (maxXsdErrorToShow > 0) {
//                        try {
//                            ImdiTreeObject currentImdiObject = ImdiLoader.getSingleInstance().getImdiObject(null, new URI(currentNodeString));
//                            tempArray.add(currentImdiObject);
//                            JTextPane fileText = new JTextPane();
                                        XsdChecker xsdChecker = new XsdChecker();
                                        if (xsdChecker.simpleCheck(new File(currentNodeUri), currentNodeUri) != null) {
                                            jTabbedPane1.add("XSD Error on Import", xsdChecker);
                                            xsdChecker.checkXML(ImdiLoader.getSingleInstance().getImdiObject(null, currentNodeUri));
                                            xsdChecker.setDividerLocation(0.5);
                                            maxXsdErrorToShow--;
                                            if (maxXsdErrorToShow == 0) {
                                                importTextArea.append("maximum xsd errors shown, no more files will be tested" + "\n");
                                            }
                                        }
//                            currentImdiObject.reloadNode();
//                            try {
//                                fileText.setPage(currentNodeString);
//                            } catch (IOException iOException) {
//                                fileText.setText(iOException.getMessage());
//                            }
//                            jTabbedPane1.add("ImportedFile", fileText);
//                        } catch (URISyntaxException exception) {
//                            GuiHelper.linorgBugCatcher.logError(exception);
//                        }
                                        // todo: possibly create a new diagram with a sample of the imported entities for the user
                                    }
                                }
//                    leftTree.rootNodeChildren = tempArray.toArray(new ImdiTreeObject[]{});
//                    imdiTableModel.removeAllImdiRows();
//                    imdiTableModel.addImdiObjects(leftTree.rootNodeChildren);
                            }
                            // todo: it might be more efficient to only update the new files
                            importTextArea.append("Starting update of entity database" + "\n");
                            entityCollection.createDatabase();
                            importTextArea.append("Updated entity database" + "\n");
                            importTextArea.setCaretPosition(importTextArea.getText().length());
                            System.out.println("created new database");
//                leftTree.requestResort();
//                GraphData graphData = new GraphData();
//                graphData.readData();
//                graphPanel.drawNodes(graphData);
                            GedcomImportPanel.this.add(GedcomImportPanel.this.getCreatedNodesPane(gedcomImporter), BorderLayout.PAGE_END);
                            GedcomImportPanel.this.revalidate();
                        }
                    }.start();
                }
            });
        }
    }
}
