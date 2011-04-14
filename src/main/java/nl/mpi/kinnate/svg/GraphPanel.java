package nl.mpi.kinnate.svg;

import nl.mpi.kinnate.kindata.GraphSorter;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.ui.GraphPanelContextMenu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilderFactory;
import nl.mpi.arbil.GuiHelper;
import nl.mpi.arbil.ImdiTableModel;
import nl.mpi.arbil.clarin.CmdiComponentBuilder;
import nl.mpi.kinnate.KinTermSavePanel;
import nl.mpi.kinnate.entityindexer.IndexerParameters;
import nl.mpi.kinnate.SavePanel;
import nl.mpi.kinnate.kindata.EntityRelation;
import nl.mpi.kinnate.kintypestrings.KinTerms;
import nl.mpi.kinnate.ui.KinTypeEgoSelectionTestPanel;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

/**
 *  Document   : GraphPanel
 *  Created on : Aug 16, 2010, 5:31:33 PM
 *  Author     : Peter Withers
 */
public class GraphPanel extends JPanel implements SavePanel {

    private JSVGScrollPane jSVGScrollPane;
    protected JSVGCanvas svgCanvas;
    protected SVGDocument doc;
    private KinTerms[] kinTermGroups;
    protected ImdiTableModel imdiTableModel;
    protected GraphSorter graphData;
    private boolean requiresSave = false;
    private File svgFile = null;
    protected GraphPanelSize graphPanelSize;
    protected ArrayList<String> selectedGroupId;
    protected String svgNameSpace = SVGDOMImplementation.SVG_NAMESPACE_URI;
    private DataStoreSvg dataStoreSvg;
    private URI[] egoPathsTemp = null;
    protected SvgUpdateHandler svgUpdateHandler;
    private int currentZoom = 0;
    private int currentWidth = 0;
    private int currentHeight = 0;
    public boolean snapToGrid = false;

    public GraphPanel(KinTermSavePanel egoSelectionPanel) {
        dataStoreSvg = new DataStoreSvg();
        svgUpdateHandler = new SvgUpdateHandler(this, egoSelectionPanel);
        selectedGroupId = new ArrayList<String>();
        graphPanelSize = new GraphPanelSize();
        kinTermGroups = new KinTerms[]{new KinTerms()};
        this.setLayout(new BorderLayout());
        svgCanvas = new JSVGCanvas();
//        svgCanvas.setMySize(new Dimension(600, 400));
        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
//        drawNodes();
        svgCanvas.setEnableImageZoomInteractor(false);
        svgCanvas.setEnablePanInteractor(false);
        svgCanvas.setEnableRotateInteractor(false);
        svgCanvas.setEnableZoomInteractor(false);
        svgCanvas.addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                currentZoom = currentZoom + e.getUnitsToScroll();
                if (currentZoom > 8) {
                    currentZoom = 8;
                }
                if (currentZoom < -6) {
                    currentZoom = -6;
                }
                double scale = 1 - e.getUnitsToScroll() / 10.0;
                double tx = -e.getX() * (scale - 1);
                double ty = -e.getY() * (scale - 1);
                AffineTransform at = new AffineTransform();
                at.translate(tx, ty);
                at.scale(scale, scale);
                at.concatenate(svgCanvas.getRenderingTransform());
                svgCanvas.setRenderingTransform(at);
//                zoomDrawing();
            }
        });
//        svgCanvas.setEnableResetTransformInteractor(true);
//        svgCanvas.setDoubleBufferedRendering(true); // todo: look into reducing the noticable aliasing on the canvas

        MouseListenerSvg mouseListenerSvg = new MouseListenerSvg(this);
        svgCanvas.addMouseListener(mouseListenerSvg);
        svgCanvas.addMouseMotionListener(mouseListenerSvg);
        jSVGScrollPane = new JSVGScrollPane(svgCanvas);
//        svgCanvas.setBackground(Color.LIGHT_GRAY);
        this.add(BorderLayout.CENTER, jSVGScrollPane);
        if (egoSelectionPanel instanceof KinTypeEgoSelectionTestPanel) {
            svgCanvas.setComponentPopupMenu(new GraphPanelContextMenu((KinTypeEgoSelectionTestPanel) egoSelectionPanel, this, graphPanelSize));
        } else {
            svgCanvas.setComponentPopupMenu(new GraphPanelContextMenu(null, this, graphPanelSize));
        }
    }

    private void zoomDrawing() {
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(1 - currentZoom / 10.0, 1 - currentZoom / 10.0);
        System.out.println("currentZoom: " + currentZoom);
//        svgCanvas.setRenderingTransform(scaleTransform);
        Rectangle canvasBounds = this.getBounds();
        SVGRect bbox = ((SVGLocatable) doc.getRootElement()).getBBox();
        if (bbox != null) {
            System.out.println("previousZoomedWith: " + bbox.getWidth());
        }
//        SVGElement rootElement = doc.getRootElement();
//        if (currentWidth < canvasBounds.width) {
        float drawingCenter = (currentWidth / 2);
//                float drawingCenter = (bbox.getX() + (bbox.getWidth() / 2));
        float canvasCenter = (canvasBounds.width / 2);
        AffineTransform at = new AffineTransform();
        at.translate((canvasCenter - drawingCenter), 1);
        at.concatenate(scaleTransform);
        svgCanvas.setRenderingTransform(at);
    }

    public void setImdiTableModel(ImdiTableModel imdiTableModelLocal) {
        imdiTableModel = imdiTableModelLocal;
    }

    public void readSvg(File svgFilePath) {
        svgFile = svgFilePath;
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory(parser);
        try {
            doc = (SVGDocument) documentFactory.createDocument(svgFilePath.toURI().toString());
            svgCanvas.setDocument(doc);
            dataStoreSvg = DataStoreSvg.loadDataFromSvg(doc);
            requiresSave = false;
        } catch (IOException ioe) {
            GuiHelper.linorgBugCatcher.logError(ioe);
        }
//        svgCanvas.setURI(svgFilePath.toURI().toString());
    }

    private void saveSvg(File svgFilePath) {
        svgFile = svgFilePath;
        new CmdiComponentBuilder().savePrettyFormatting(doc, svgFilePath);
        requiresSave = false;
    }

    private void printNodeNames(Node nodeElement) {
        System.out.println(nodeElement.getLocalName());
        System.out.println(nodeElement.getNamespaceURI());
        Node childNode = nodeElement.getFirstChild();
        while (childNode != null) {
            printNodeNames(childNode);
            childNode = childNode.getNextSibling();
        }
    }

    public String[] getKinTypeStrigs() {
        return dataStoreSvg.kinTypeStrings;
    }

    public void setKinTypeStrigs(String[] kinTypeStringArray) {
        // strip out any white space, blank lines and remove duplicates
        // this has set has been removed because it creates a discrepancy between what the user types and what is processed
//        HashSet<String> kinTypeStringSet = new HashSet<String>();
//        for (String kinTypeString : kinTypeStringArray) {
//            if (kinTypeString != null && kinTypeString.trim().length() > 0) {
//                kinTypeStringSet.add(kinTypeString.trim());
//            }
//        }
//        dataStoreSvg.kinTypeStrings = kinTypeStringSet.toArray(new String[]{});
        dataStoreSvg.kinTypeStrings = kinTypeStringArray;
    }

    public IndexerParameters getIndexParameters() {
        return dataStoreSvg.indexParameters;
    }

    public KinTerms[] getkinTermGroups() {
        return kinTermGroups;
    }

    public void addKinTermGroup() {
        ArrayList<KinTerms> kinTermsList = new ArrayList<KinTerms>(Arrays.asList(kinTermGroups));
        kinTermsList.add(new KinTerms());
        kinTermGroups = kinTermsList.toArray(new KinTerms[]{});
    }

    public String[] getEgoUniquiIdentifiersList() {
        return dataStoreSvg.egoIdentifierSet.toArray(new String[]{});
    }

    public String[] getEgoIdList() {
        return dataStoreSvg.egoIdentifierSet.toArray(new String[]{});
    }

    public URI[] getEgoPaths() {
        if (egoPathsTemp != null) {
            return egoPathsTemp;
        }
        ArrayList<URI> returnPaths = new ArrayList<URI>();
        for (String egoId : dataStoreSvg.egoIdentifierSet) {
            try {
                String entityPath = getPathForElementId(egoId);
//                if (entityPath != null) {
                returnPaths.add(new URI(entityPath));
//                }
            } catch (URISyntaxException ex) {
                GuiHelper.linorgBugCatcher.logError(ex);
                // todo: warn user with a dialog
            }
        }
        return returnPaths.toArray(new URI[]{});
    }

    public void setEgoList(URI[] egoPathArray, String[] egoIdentifierArray) {
        egoPathsTemp = egoPathArray; // egoPathsTemp is only required if the ego nodes are not already on the graph (otherwise the path can be obtained from the graph elements)
        dataStoreSvg.egoIdentifierSet = new HashSet<String>(Arrays.asList(egoIdentifierArray));
    }

    public void addEgo(URI[] egoPathArray, String[] egoIdentifierArray) {
        egoPathsTemp = egoPathArray; // egoPathsTemp is only required if the ego nodes are not already on the graph (otherwise the path can be obtained from the graph elements)
        dataStoreSvg.egoIdentifierSet.addAll(Arrays.asList(egoIdentifierArray));
    }

    public void removeEgo(String[] egoIdentifierArray) {
        dataStoreSvg.egoIdentifierSet.removeAll(Arrays.asList(egoIdentifierArray));
    }

    public String[] getSelectedIds() {
        return selectedGroupId.toArray(new String[]{});
    }

    public boolean selectionContainsEgo() {
        for (String selectedId : selectedGroupId) {
            if (dataStoreSvg.egoIdentifierSet.contains(selectedId)) {
                return true;
            }
        }
        return false;
    }

    public String getPathForElementId(String elementId) {
//        NamedNodeMap namedNodeMap = doc.getElementById(elementId).getAttributes();
//        for (int attributeCounter = 0; attributeCounter < namedNodeMap.getLength(); attributeCounter++) {
//            System.out.println(namedNodeMap.item(attributeCounter).getNodeName());
//            System.out.println(namedNodeMap.item(attributeCounter).getNamespaceURI());
//            System.out.println(namedNodeMap.item(attributeCounter).getNodeValue());
//        }
        Element entityElement = doc.getElementById(elementId);
//        if (entityElement == null) {
//            return null;
//        } else {
        return entityElement.getAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "path");
//        }
    }

    public String getKinTypeForElementId(String elementId) {
        Element entityElement = doc.getElementById(elementId);
        return entityElement.getAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kintype");
    }

    public void resetZoom() {
        AffineTransform at = new AffineTransform();
        at.scale(1, 1);
        at.setToTranslation(1, 1);
        svgCanvas.setRenderingTransform(at);
    }

    private Element createEntitySymbol(EntityData currentNode, int hSpacing, int vSpacing, int symbolSize) {
        Element groupNode = doc.createElementNS(svgNameSpace, "g");
        groupNode.setAttribute("id", currentNode.getUniqueIdentifier());
        groupNode.setAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kin:path", currentNode.getEntityPath());
        groupNode.setAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kin:kintype", currentNode.getKinTypeString());
//        counterTest++;
        Element symbolNode;
        String symbolType = currentNode.getSymbolType();
        if (symbolType == null || symbolType.length() == 0) {
            symbolType = "square";
        }
        // todo: check that if an entity is already placed in which case do not recreate
        // todo: do not create a new dom each time but reuse it instead, or due to the need to keep things up to date maybe just store an array of entity locations instead
        symbolNode = doc.createElementNS(svgNameSpace, "use");
        symbolNode.setAttribute("id", currentNode.getUniqueIdentifier() + "symbol");
        symbolNode.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + symbolType); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
        groupNode.setAttribute("transform", "translate(" + Integer.toString(currentNode.getxPos() * hSpacing + hSpacing - symbolSize / 2) + ", " + Integer.toString(currentNode.getyPos() * vSpacing + vSpacing - symbolSize / 2) + ")");

        if (currentNode.isEgo) {
            symbolNode.setAttribute("fill", "black");
        } else {
            symbolNode.setAttribute("fill", "white");
        }

        symbolNode.setAttribute("stroke", "black");
        symbolNode.setAttribute("stroke-width", "2");
        groupNode.appendChild(symbolNode);

////////////////////////////// tspan method appears to fail in batik rendering process unless saved and reloaded ////////////////////////////////////////////////
//            Element labelText = doc.createElementNS(svgNS, "text");
////            labelText.setAttribute("x", Integer.toString(currentNode.xPos * hSpacing + hSpacing + symbolSize / 2));
////            labelText.setAttribute("y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
//            labelText.setAttribute("fill", "black");
//            labelText.setAttribute("fill-opacity", "1");
//            labelText.setAttribute("stroke-width", "0");
//            labelText.setAttribute("font-size", "14px");
////            labelText.setAttribute("text-anchor", "end");
////            labelText.setAttribute("style", "font-size:14px;text-anchor:end;fill:black;fill-opacity:1");
//            //labelText.setNodeValue(currentChild.toString());
//
//            //String textWithUni = "\u0041";
//            int textSpanCounter = 0;
//            int lineSpacing = 10;
//            for (String currentTextLable : currentNode.getLabel()) {
//                Text textNode = doc.createTextNode(currentTextLable);
//                Element tspanElement = doc.createElement("tspan");
//                tspanElement.setAttribute("x", Integer.toString(currentNode.xPos * hSpacing + hSpacing + symbolSize / 2));
//                tspanElement.setAttribute("y", Integer.toString((currentNode.yPos * vSpacing + vSpacing - symbolSize / 2) + textSpanCounter));
////                tspanElement.setAttribute("y", Integer.toString(textSpanCounter * lineSpacing));
//                tspanElement.appendChild(textNode);
//                labelText.appendChild(tspanElement);
//                textSpanCounter += lineSpacing;
//            }
//            groupNode.appendChild(labelText);
////////////////////////////// end tspan method appears to fail in batik rendering process ////////////////////////////////////////////////

////////////////////////////// alternate method ////////////////////////////////////////////////
        // todo: this method has the draw back that the text is not selectable as a block
        int textSpanCounter = 0;
        int lineSpacing = 15;
        for (String currentTextLable : currentNode.getLabel()) {
            Element labelText = doc.createElementNS(svgNameSpace, "text");
            labelText.setAttribute("x", Double.toString(symbolSize * 1.5));
            labelText.setAttribute("y", Integer.toString(textSpanCounter));
            labelText.setAttribute("fill", "black");
            labelText.setAttribute("stroke-width", "0");
            labelText.setAttribute("font-size", "14");
            Text textNode = doc.createTextNode(currentTextLable);
            labelText.appendChild(textNode);
            textSpanCounter += lineSpacing;
            groupNode.appendChild(labelText);
        }
////////////////////////////// end alternate method ////////////////////////////////////////////////
        ((EventTarget) groupNode).addEventListener("mousedown", new MouseListenerSvg(this), false);
        return groupNode;
    }

    public void drawNodes() {
        drawNodes(graphData);
    }

    public void drawNodes(GraphSorter graphDataLocal) {
        requiresSave = true;
        graphData = graphDataLocal;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            String templateXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:kin=\"http://mpi.nl/tla/kin\" "
                    + "xmlns=\"http://www.w3.org/2000/svg\" contentScriptType=\"text/ecmascript\" "
                    + " zoomAndPan=\"magnify\" contentStyleType=\"text/css\" "
                    + "preserveAspectRatio=\"xMidYMid meet\" version=\"1.0\"/>";
//        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
//        doc = (SVGDocument) impl.createDocument(svgNameSpace, "svg", null);

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory(parser);
            doc = (SVGDocument) documentFactory.createDocument(svgNameSpace, new StringReader(templateXml));

//            // todo: look into how to add the extra namespaces to the svg document - doc.getDomConfig()
//            doc.getDocumentElement().setAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kin:version", "");
//            doc.getDocumentElement().setAttribute("xmlns:" + DataStoreSvg.kinDataNameSpace, DataStoreSvg.kinDataNameSpaceLocation); // this method of declaring multiple namespaces looks to me to be wrong but it is the only method that does not get stripped out by the transformer on save
            new EntitySvg().insertSymbols(doc, svgNameSpace);
//        Document doc = impl.createDocument(svgNS, "svg", null);
//        SVGDocument doc = svgCanvas.getSVGDocument();
            // Get the root element (the 'svg' elemen�t).
            Element svgRoot = doc.getDocumentElement();
            // todo: set up a kinnate namespace so that the ego list and kin type strings can have more permanent storage places
//        int maxTextLength = 0;
//        for (GraphDataNode currentNode : graphData.getDataNodes()) {
//            if (currentNode.getLabel()[0].length() > maxTextLength) {
//                maxTextLength = currentNode.getLabel()[0].length();
//            }
//        }
            int vSpacing = graphPanelSize.getVerticalSpacing(graphData.gridHeight);
            // todo: find the real text size from batik
            // todo: get the user selected canvas size and adjust the hSpacing and vSpacing to fit
//        int hSpacing = maxTextLength * 10 + 100;
            int hSpacing = graphPanelSize.getHorizontalSpacing(graphData.gridWidth);
            int symbolSize = 15;
            int strokeWidth = 2;

//        int preferedWidth = graphData.gridWidth * hSpacing + hSpacing * 2;
//        int preferedHeight = graphData.gridHeight * vSpacing + vSpacing * 2;
            currentWidth = graphPanelSize.getWidth(graphData.gridWidth, hSpacing);
            currentHeight = graphPanelSize.getHeight(graphData.gridHeight, vSpacing);
            // Set the width and height attributes on the root 'svg' element.
            svgRoot.setAttribute("width", Integer.toString(currentWidth));
            svgRoot.setAttribute("height", Integer.toString(currentHeight));

            this.setPreferredSize(new Dimension(graphPanelSize.getHeight(graphData.gridHeight, vSpacing), graphPanelSize.getWidth(graphData.gridWidth, hSpacing)));

            // store the selected kin type strings and other data in the dom
            dataStoreSvg.storeAllData(doc);

            svgCanvas.setSVGDocument(doc);
//        svgCanvas.setDocument(doc);
//        int counterTest = 0;

            // add the relation symbols in a group below the relation lines
            Element relationGroupNode = doc.createElementNS(svgNameSpace, "g");
            relationGroupNode.setAttribute("id", "RelationGroup");
            for (EntityData currentNode : graphData.getDataNodes()) {
                // set up the mouse listners on the group node
//            ((EventTarget) groupNode).addEventListener("mouseover", new EventListener() {
//
//                public void handleEvent(Event evt) {
//                    System.out.println("OnMouseOverCircleAction: " + evt.getCurrentTarget());
//                    if (currentDraggedElement == null) {
//                        ((Element) evt.getCurrentTarget()).setAttribute("fill", "green");
//                    }
//                }
//            }, false);
//            ((EventTarget) groupNode).addEventListener("mouseout", new EventListener() {
//
//                public void handleEvent(Event evt) {
//                    System.out.println("mouseout: " + evt.getCurrentTarget());
//                    if (currentDraggedElement == null) {
//                        ((Element) evt.getCurrentTarget()).setAttribute("fill", "none");
//                    }
//                }
//            }, false);


                if (currentNode.isVisible) {
                    for (EntityRelation graphLinkNode : currentNode.getVisiblyRelateNodes()) {
                        new RelationSvg().insertRelation(doc, svgNameSpace, relationGroupNode, currentNode, graphLinkNode, hSpacing, vSpacing, strokeWidth);
                    }
                }
            }
            svgRoot.appendChild(relationGroupNode);
            // add the entity symbols in a group on top of the relation lines
            Element entityGroupNode = doc.createElementNS(svgNameSpace, "g");
            entityGroupNode.setAttribute("id", "EntityGroup");
            for (EntityData currentNode : graphData.getDataNodes()) {
                if (currentNode.isVisible) {
                    entityGroupNode.appendChild(createEntitySymbol(currentNode, hSpacing, vSpacing, symbolSize));
                }
            }
            svgRoot.appendChild(entityGroupNode);
            //new CmdiComponentBuilder().savePrettyFormatting(doc, new File("/Users/petwit/Documents/SharedInVirtualBox/mpi-co-svn-mpi-nl/LAT/Kinnate/trunk/src/main/resources/output.svg"));
//        svgCanvas.revalidate();
            dataStoreSvg.indexParameters.symbolFieldsFields.setAvailableValues(new EntitySvg().listSymbolNames(doc));
//        zoomDrawing();
        } catch (DOMException exception) {
            GuiHelper.linorgBugCatcher.logError(exception);
        } catch (IOException exception) {
            GuiHelper.linorgBugCatcher.logError(exception);
        }
    }

    public boolean hasSaveFileName() {
        return svgFile != null;
    }

    public boolean requiresSave() {
        return requiresSave;
    }

    public void saveToFile() {
        saveSvg(svgFile);
    }

    public void saveToFile(File saveAsFile) {
        saveSvg(saveAsFile);
    }

    public void updateGraph() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
