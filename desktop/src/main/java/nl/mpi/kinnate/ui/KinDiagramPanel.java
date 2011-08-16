package nl.mpi.kinnate.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeContainer;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.ui.ArbilTable;
import nl.mpi.arbil.ui.ArbilTableModel;
import nl.mpi.arbil.ui.ArbilWindowManager;
import nl.mpi.arbil.ui.GuiHelper;
import nl.mpi.kinnate.KinTermSavePanel;
import nl.mpi.kinnate.kindata.GraphSorter;
import nl.mpi.kinnate.svg.GraphPanel;
import nl.mpi.kinnate.SavePanel;
import nl.mpi.kinnate.entityindexer.EntityCollection;
import nl.mpi.kinnate.entityindexer.EntityService;
import nl.mpi.kinnate.entityindexer.EntityServiceException;
import nl.mpi.kinnate.entityindexer.QueryParser;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;
import nl.mpi.kinnate.kintypestrings.KinTypeStringConverter;
import nl.mpi.kinnate.kintypestrings.ParserHighlight;

/**
 *  Document   : KinTypeStringTestPanel
 *  Created on : Sep 29, 2010, 12:52:01 PM
 *  Author     : Peter Withers
 */
public class KinDiagramPanel extends JPanel implements SavePanel, KinTermSavePanel, ArbilDataNodeContainer {

    private EntityCollection entityCollection;
    private JTextPane kinTypeStringInput;
    private GraphPanel graphPanel;
    private GraphSorter graphSorter;
    private EgoSelectionPanel egoSelectionPanel;
    private HidePane kinTermHidePane;
    private HidePane kinTypeHidePane;
    private KinTermTabPane kinTermPanel;
    private EntityService entityIndex;
    private HashMap<UniqueIdentifier, ArbilDataNode> registeredArbilDataNode;
    private String defaultString = "# The kin type strings entered here will determine how the entities show on the graph below\n";
    public static String defaultGraphString = "# The kin type strings entered here will determine how the entities show on the graph below\n"
            + "# Enter one string per line.\n"
            //+ "# By default all relations of the selected entity will be shown.\n"
            + "# for example:\n"
            //            + "EmWMMM\n"
            //            + "E:1:FFE\n"
            //            + "EmWMMM:1:\n"
            //            + "E:1:FFE\n"
            + "Em:Charles II of Spain:W:Marie Louise d'Orl�ans\n"
            + "Em:Charles II of Spain:F:Philip IV of Spain:F:Philip III of Spain:F:Philip II of Spain:F:Charles V, Holy Roman Emperor:F:Philip I of Castile\n"
            + "Em:Charles II of Spain:M:Mariana of Austria:M:Maria Anna of Spain:M:Margaret of Austria:M:Maria Anna of Bavaria\n"
            + "M:Mariana of Austria:F:Ferdinand III, Holy Roman Emperor:\n"
            + "F:Philip IV of Spain:M:Margaret of Austria\n"
            + "F:Ferdinand III, Holy Roman Emperor:\n"
            + "M:Maria Anna of Spain:\n"
            + "F:Philip III of Spain\n"
            + "M:Margaret of Austria\n"
            + "\n";
//            + "FS:1:BSSWMDHFF:1:\n"
//            + "M:2:SSDHMFM:2:\n"
//            + "F:3:SSDHMF:3:\n"
//            + "";
//            + "E=[Bob]MFM\n"
//            + "E=[Bob]MZ\n"
//            + "E=[Bob]F\n"
//            + "E=[Bob]M\n"
//            + "E=[Bob]S";
//    private String kinTypeStrings[] = new String[]{};
    Color commentColour = Color.GRAY;

    public KinDiagramPanel(File existingFile) {
        entityCollection = new EntityCollection();
        EntityData[] svgStoredEntities = null;
        graphPanel = new GraphPanel(this);
        kinTypeStringInput = new JTextPane();
        kinTypeStringInput.setText(defaultString);
        kinTypeStringInput.setForeground(commentColour);
        if (existingFile != null && existingFile.exists()) {
            svgStoredEntities = graphPanel.readSvg(existingFile);
            String kinTermContents = null;
            for (String currentKinTypeString : graphPanel.getKinTypeStrigs()) {
                if (currentKinTypeString.trim().length() > 0) {
                    if (kinTermContents == null) {
                        kinTermContents = "";
                    } else {
                        kinTermContents = kinTermContents + "\n";
                    }
                    kinTermContents = kinTermContents + currentKinTypeString.trim();
                }
            }
            kinTypeStringInput.setText(kinTermContents);
        } else {
            graphPanel.generateDefaultSvg();
        }
        this.setLayout(new BorderLayout());
        registeredArbilDataNode = new HashMap<UniqueIdentifier, ArbilDataNode>();
        egoSelectionPanel = new EgoSelectionPanel();
        kinTermPanel = new KinTermTabPane(this, graphPanel.getkinTermGroups());
        // set the styles for the kin type string text
        Style styleComment = kinTypeStringInput.addStyle("Comment", null);
//        StyleConstants.setForeground(styleComment, new Color(247,158,9));
        StyleConstants.setForeground(styleComment, commentColour);
        Style styleKinType = kinTypeStringInput.addStyle("KinType", null);
        StyleConstants.setForeground(styleKinType, new Color(43, 32, 161));
        Style styleQuery = kinTypeStringInput.addStyle("Query", null);
        StyleConstants.setForeground(styleQuery, new Color(183, 7, 140));
        Style styleParamater = kinTypeStringInput.addStyle("Parameter", null);
        StyleConstants.setForeground(styleParamater, new Color(103, 7, 200));
        Style styleError = kinTypeStringInput.addStyle("Error", null);
//        StyleConstants.setForeground(styleError, new Color(172,3,57));
        StyleConstants.setForeground(styleError, Color.RED);
        Style styleUnknown = kinTypeStringInput.addStyle("Unknown", null);
        StyleConstants.setForeground(styleUnknown, Color.BLACK);

//        kinTypeStringInput.setText(defaultString);

        JPanel kinGraphPanel = new JPanel(new BorderLayout());

        TableCellDragHandler tableCellDragHandler = new TableCellDragHandler();

        kinTypeHidePane = new HidePane(HidePane.HidePanePosition.top, 0);
        kinTypeHidePane.add(new JScrollPane(kinTypeStringInput), "Kin Type Strings");

        IndexerParametersPanel indexerParametersPanel = new IndexerParametersPanel(this, graphPanel, tableCellDragHandler);
        JPanel advancedPanel = new JPanel(new BorderLayout());

        ArbilTableModel imdiTableModel = new ArbilTableModel();
        graphPanel.setArbilTableModel(imdiTableModel);
        ArbilTable imdiTable = new ArbilTable(imdiTableModel, "Selected Nodes");

        imdiTable.setTransferHandler(tableCellDragHandler);
        imdiTable.setDragEnabled(true);

        JScrollPane tableScrollPane = new JScrollPane(imdiTable);
        advancedPanel.add(tableScrollPane, BorderLayout.CENTER);
        HidePane indexParamHidePane = new HidePane(HidePane.HidePanePosition.right, 0);
        indexParamHidePane.add(indexerParametersPanel, "Indexer Parameters");
        advancedPanel.add(indexParamHidePane, BorderLayout.LINE_END);

        HidePane tableHidePane = new HidePane(HidePane.HidePanePosition.bottom, 0);
        tableHidePane.add(advancedPanel, "Metadata");

        DragTransferHandler dragTransferHandler = new DragTransferHandler();
        this.setTransferHandler(dragTransferHandler);

        EntitySearchPanel entitySearchPanel = new EntitySearchPanel(entityCollection);
        entitySearchPanel.setTransferHandler(dragTransferHandler);

        HidePane egoSelectionHidePane = new HidePane(HidePane.HidePanePosition.left, 0);
        egoSelectionHidePane.add(egoSelectionPanel, "Ego Selection");
        egoSelectionHidePane.add(entitySearchPanel, "Search Entities");

        kinTermHidePane = new HidePane(HidePane.HidePanePosition.right, 0);
        kinTermHidePane.add(kinTermPanel, "Kin Terms");
        kinTermHidePane.add(new ArchiveEntityLinkerPanel(), "Archive Linker");

        kinGraphPanel.add(kinTypeHidePane, BorderLayout.PAGE_START);
        kinGraphPanel.add(egoSelectionHidePane, BorderLayout.LINE_START);
        kinGraphPanel.add(graphPanel, BorderLayout.CENTER);
        kinGraphPanel.add(kinTermHidePane, BorderLayout.LINE_END);
        kinGraphPanel.add(tableHidePane, BorderLayout.PAGE_END);

        this.add(kinGraphPanel);

        entityIndex = new QueryParser(svgStoredEntities);
        graphSorter = new GraphSorter();

        kinTypeStringInput.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                if (kinTypeStringInput.getText().equals(defaultString)) {
                    kinTypeStringInput.setText("");
//                    kinTypeStringInput.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (kinTypeStringInput.getText().length() == 0) {
                    kinTypeStringInput.setText(defaultString);
                    kinTypeStringInput.setForeground(commentColour);
                }
            }
        });
        kinTypeStringInput.addKeyListener(new KeyListener() {

            String previousKinTypeStrings = null;

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                synchronized (e) {
                    if (previousKinTypeStrings == null || !previousKinTypeStrings.equals(kinTypeStringInput.getText())) {
                        graphPanel.setKinTypeStrigs(kinTypeStringInput.getText().split("\n"));
//                kinTypeStrings = graphPanel.getKinTypeStrigs();
                        drawGraph();
                        previousKinTypeStrings = kinTypeStringInput.getText();
                    }
                }
            }
        });
    }

    public void createDefaultGraph(String defaultGraphString) {
        kinTypeStringInput.setText(defaultGraphString);
        graphPanel.setKinTypeStrigs(kinTypeStringInput.getText().split("\n"));
        drawGraph();
    }

    public void drawGraph() {
        try {
            String[] kinTypeStrings = graphPanel.getKinTypeStrigs();
            ParserHighlight[] parserHighlight = new ParserHighlight[kinTypeStrings.length];
            EntityData[] graphNodes = entityIndex.processKinTypeStrings(null, graphPanel.dataStoreSvg.egoEntities, graphPanel.dataStoreSvg.requiredEntities, kinTypeStrings, parserHighlight, graphPanel.getIndexParameters());
            boolean visibleNodeFound = false;
            for (EntityData currentNode : graphNodes) {
                if (currentNode.isVisible) {
                    visibleNodeFound = true;
                    break;
                }
            }
            if (!visibleNodeFound /*graphNodes.length == 0*/) {
                KinTypeStringConverter graphData = new KinTypeStringConverter();
                graphData.readKinTypes(kinTypeStrings, graphPanel.getkinTermGroups(), graphPanel.dataStoreSvg, parserHighlight);
                graphPanel.drawNodes(graphData);
                egoSelectionPanel.setTransientNodes(graphData.getDataNodes());
//                KinDiagramPanel.this.doLayout();
            } else {
                graphSorter.setEntitys(graphNodes);
                // register interest Arbil updates and update the graph when data is edited in the table
                registerCurrentNodes(graphSorter.getDataNodes());
                graphPanel.drawNodes(graphSorter);
                egoSelectionPanel.setTreeNodes(graphPanel.dataStoreSvg.egoEntities, graphPanel.dataStoreSvg.requiredEntities, graphSorter.getDataNodes());
            }
            StyledDocument styledDocument = kinTypeStringInput.getStyledDocument();
            int lineStart = 0;
            for (int lineCounter = 0; lineCounter < parserHighlight.length; lineCounter++) {
                ParserHighlight currentHighlight = parserHighlight[lineCounter];
//                int lineStart = styledDocument.getParagraphElement(lineCounter).getStartOffset();
//                int lineEnd = styledDocument.getParagraphElement(lineCounter).getEndOffset();
                int lineEnd = lineStart + kinTypeStrings[lineCounter].length();
                styledDocument.setCharacterAttributes(lineStart, lineEnd, kinTypeStringInput.getStyle("Unknown"), true);
                while (currentHighlight.highlight != null) {
                    int startPos = lineStart + currentHighlight.startChar;
                    int charCount = lineEnd - lineStart;
                    if (currentHighlight.nextHighlight.highlight != null) {
                        charCount = currentHighlight.nextHighlight.startChar - currentHighlight.startChar;
                    }
                    if (currentHighlight.highlight != null) {
                        String styleName = currentHighlight.highlight.name();
                        styledDocument.setCharacterAttributes(startPos, charCount, kinTypeStringInput.getStyle(styleName), true);
                    }
                    currentHighlight = currentHighlight.nextHighlight;
                }
                lineStart += kinTypeStrings[lineCounter].length() + 1;
            }
//        kinTypeStrings = graphPanel.getKinTypeStrigs();
        } catch (EntityServiceException exception) {
            GuiHelper.linorgBugCatcher.logError(exception);
            ArbilWindowManager.getSingleInstance().addMessageDialogToQueue("Failed to load an entity", "Kinnate");
        }
    }

    @Deprecated
    public void setDisplayNodes(String typeString, String[] egoIdentifierArray) {
        // todo: should this be replaced by the required nodes?
        if (kinTypeStringInput.getText().equals(defaultString)) {
            kinTypeStringInput.setText("");
        }
        String kinTermContents = kinTypeStringInput.getText();
        for (String currentId : egoIdentifierArray) {
            kinTermContents = kinTermContents + typeString + "=[" + currentId + "]\n";
        }
        kinTypeStringInput.setText(kinTermContents);
        graphPanel.setKinTypeStrigs(kinTypeStringInput.getText().split("\n"));
//        kinTypeStrings = graphPanel.getKinTypeStrigs();
        drawGraph();
    }

    public void setEgoNodes(UniqueIdentifier[] egoIdentifierArray) {
        graphPanel.dataStoreSvg.egoEntities = new HashSet<UniqueIdentifier>(Arrays.asList(egoIdentifierArray));
        drawGraph();
    }

    public void addEgoNodes(UniqueIdentifier[] egoIdentifierArray) {
        graphPanel.dataStoreSvg.egoEntities.addAll(Arrays.asList(egoIdentifierArray));
        drawGraph();
    }

    public void removeEgoNodes(UniqueIdentifier[] egoIdentifierArray) {
        graphPanel.dataStoreSvg.egoEntities.removeAll(Arrays.asList(egoIdentifierArray));
        drawGraph();
    }

    public void addRequiredNodes(UniqueIdentifier[] egoIdentifierArray) {
        graphPanel.dataStoreSvg.requiredEntities.addAll(Arrays.asList(egoIdentifierArray));
        drawGraph();
    }

    public void removeRequiredNodes(UniqueIdentifier[] egoIdentifierArray) {
        graphPanel.dataStoreSvg.requiredEntities.removeAll(Arrays.asList(egoIdentifierArray));
        drawGraph();
    }

    public boolean hasSaveFileName() {
        return graphPanel.hasSaveFileName();
    }

    public File getFileName() {
        return graphPanel.getFileName();
    }

    public boolean requiresSave() {
        return graphPanel.requiresSave();
    }

    public void setRequiresSave() {
        graphPanel.setRequiresSave();
    }

    public void saveToFile() {
        graphPanel.saveToFile();
    }

    public void saveToFile(File saveFile) {
        graphPanel.saveToFile(saveFile);
    }

    public void updateGraph() {
        this.drawGraph();
    }

    public void exportKinTerms() {
        kinTermPanel.getSelectedKinTermPanel().exportKinTerms();
    }

    public void hideShow() {
        kinTermHidePane.toggleHiddenState();
    }

    public void importKinTerms() {
        kinTermPanel.getSelectedKinTermPanel().importKinTerms();
    }

    public void addKinTermGroup() {
        graphPanel.addKinTermGroup();
        kinTermPanel.updateKinTerms(graphPanel.getkinTermGroups());
    }

    public void setSelectedKinTypeSting(String kinTypeStrings) {
        kinTermPanel.setAddableKinTypeSting(kinTypeStrings);
    }

    public boolean isHidden() {
        return kinTermHidePane.isHidden();
    }

    private void registerCurrentNodes(EntityData[] currentEntities) {
        // todo: i think this is resolved but double check the issue where arbil nodes update frequency is too high and breaks basex
        for (EntityData entityData : currentEntities) {
            ArbilDataNode arbilDataNode = null;
            if (!registeredArbilDataNode.containsKey(entityData.getUniqueIdentifier())) {
                try {
                    String metadataPath = entityData.getEntityPath();
                    if (metadataPath != null) {
                        arbilDataNode = ArbilDataNodeLoader.getSingleInstance().getArbilDataNode(null, new URI(metadataPath));
                        registeredArbilDataNode.put(entityData.getUniqueIdentifier(), arbilDataNode);
                        arbilDataNode.registerContainer(this);
                        // todo: keep track of registered nodes and remove the unrequired ones here
                    } else {
                        GuiHelper.linorgBugCatcher.logError(new Exception("Error getting path for: " + entityData.getUniqueIdentifier().getAttributeIdentifier() + " : " + entityData.getLabel()[0]));
                    }
                } catch (URISyntaxException exception) {
                    GuiHelper.linorgBugCatcher.logError(exception);
                }
            } else {
                arbilDataNode = registeredArbilDataNode.get(entityData.getUniqueIdentifier());
            }
            if (arbilDataNode != null) {
                entityData.metadataRequiresSave = arbilDataNode.getNeedsSaveToDisk(false);
            }
        }
    }

    public void entityRelationsChanged(UniqueIdentifier[] selectedIdentifiers) {
        // this method does not need to update the database because the link changing process has already done that
        // remove the stored graph locations of the selected ids
        graphPanel.clearEntityLocations(selectedIdentifiers);
        graphPanel.getIndexParameters().valuesChanged = true;
        drawGraph();
    }

    public void dataNodeIconCleared(ArbilDataNode arbilDataNode) {
//         todo: this needs to be updated to be multi threaded so users can link or save multiple nodes at once
        boolean dataBaseRequiresUpdate = false;
        boolean redrawRequired = false;
        // find the entity data for this arbil data node
        for (EntityData entityData : graphSorter.getDataNodes()) {
            try {
                String entityPath = entityData.getEntityPath();
                if (entityPath != null && arbilDataNode.getURI().equals(new URI(entityPath))) {
                    // check if the metadata has been changed
                    // todo: something here fails to act on multiple nodes that have changed (it is the db update that was missed)
                    if (entityData.metadataRequiresSave && !arbilDataNode.getNeedsSaveToDisk(false)) {
                        dataBaseRequiresUpdate = true;
                        redrawRequired = true;
                    }
                    // clear or set the needs save flag
                    entityData.metadataRequiresSave = arbilDataNode.getNeedsSaveToDisk(false);
                    if (entityData.metadataRequiresSave) {
                        redrawRequired = true;
                    }
                }
            } catch (URISyntaxException exception) {
                GuiHelper.linorgBugCatcher.logError(exception);
            }
        }
        if (dataBaseRequiresUpdate) {
            entityCollection.updateDatabase(arbilDataNode.getURI());
            graphPanel.getIndexParameters().valuesChanged = true;
        }
        if (redrawRequired) {
            drawGraph();
        }
    }

    public void dataNodeRemoved(ArbilDataNode adn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}