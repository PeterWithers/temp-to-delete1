package nl.mpi.kinnate;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputAdapter;
import nl.mpi.arbil.clarin.CmdiComponentBuilder;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

/**
 *  Document   : GraphPanel
 *  Created on : Aug 16, 2010, 5:31:33 PM
 *  Author     : Peter Withers
 */
public class GraphPanel extends JPanel {

    private JScrollPane jScrollPane;
    protected JSVGCanvas svgCanvas;
    private SVGDocument doc;
    private Element currentDraggedElement;
    private Cursor preDragCursor;
    HashSet<URI> egoSet = new HashSet<URI>();

    public GraphPanel() {
        this.setLayout(new BorderLayout());
        svgCanvas = new JSVGCanvas();
//        svgCanvas.setMySize(new Dimension(600, 400));
        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
//        drawNodes();
        svgCanvas.setEnableImageZoomInteractor(true);
        svgCanvas.setEnablePanInteractor(true);
        svgCanvas.setEnableRotateInteractor(true);
        svgCanvas.setEnableZoomInteractor(true);
        svgCanvas.setEnableResetTransformInteractor(true);

        MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {

            @Override
            public void mouseDragged(MouseEvent me) {
//                System.out.println("mouseDragged: " + me.toString());
                if (currentDraggedElement != null) {
                    currentDraggedElement.setAttribute("x", String.valueOf(me.getX()));
                    currentDraggedElement.setAttribute("y", String.valueOf(me.getY()));
                    svgCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    SVGRect bbox = ((SVGLocatable) currentDraggedElement).getBBox();
                    System.out.println("bbox X: " + bbox.getX());
                    System.out.println("bbox Y: " + bbox.getY());
                    System.out.println("bbox W: " + bbox.getWidth());
                    System.out.println("bbox H: " + bbox.getHeight());
//                    todo: look into transform issues when dragging ellements eg when the canvas is scaled or panned
//                            SVGLocatable.getTransformToElement()
//                            SVGPoint.matrixTransform()

                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
//                System.out.println("mouseReleased: " + me.toString());
                if (currentDraggedElement != null) {
                    currentDraggedElement.setAttribute("x", String.valueOf(me.getX()));
                    currentDraggedElement.setAttribute("y", String.valueOf(me.getY()));
                    currentDraggedElement.setAttribute("fill", "none");
                    currentDraggedElement = null;
                    svgCanvas.setCursor(preDragCursor);
                }
            }
        };
        svgCanvas.addMouseListener(mouseInputAdapter);
        svgCanvas.addMouseMotionListener(mouseInputAdapter);
        jScrollPane = new JScrollPane(svgCanvas);
        this.add(BorderLayout.CENTER, jScrollPane);
    }

    public void readSvg(File svgFilePath) {
        svgCanvas.setURI(svgFilePath.toURI().toString());
    }

    public void saveSvg(File svgFilePath) {
        new CmdiComponentBuilder().savePrettyFormatting(doc, svgFilePath);
    }

    public URI[] getEgoList() {
        // todo: read this from the SVG
        return egoSet.toArray(new URI[]{});
        //return new String[]{"file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/201101251709350.cmdi", "file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/20110125170936.cmdi"};
//"file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/20110125170935.cmdi"
        //ego tree: 
        //ego tree: file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/201101251709360.cmdi
        //ego tree: file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/201101251709361.cmdi
        //ego tree: file:/Users/petwit/Documents/SharedInVirtualBox/ArbilWorkingFiles/20110125170937.cmdi"};
    }

    public void setEgoList(URI[] egoListArray) {
        // todo: write this to the SVG
        egoSet = new HashSet<URI>();
        for (URI egoUri : egoListArray) {
            egoSet.add(egoUri);
        }
    }

    public void drawNodes(GraphData graphData) {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
//        Document doc = impl.createDocument(svgNS, "svg", null);
//        SVGDocument doc = svgCanvas.getSVGDocument();
        // Get the root element (the 'svg' elemen�t).
        Element svgRoot = doc.getDocumentElement();
        // svgRoot.removeAttribute("version");
        int maxTextLength = 0;
        for (GraphDataNode currentNode : graphData.getDataNodes()) {
            if (currentNode.getLabel()[0].length() > maxTextLength) {
                maxTextLength = currentNode.getLabel()[0].length();
            }
        }
        int vSpacing = 100;
        // todo: find the real text size from batik
        int hSpacing = maxTextLength * 10 + 100;
        int symbolSize = 10;
        int strokeWidth = 1;

        int preferedWidth = graphData.gridWidth * hSpacing + hSpacing * 2;
        int preferedHeight = graphData.gridHeight * vSpacing + vSpacing * 2;

        // Set the width and height attributes on the root 'svg' element.
        svgRoot.setAttributeNS(null, "width", Integer.toString(preferedWidth));
        svgRoot.setAttributeNS(null, "height", Integer.toString(preferedHeight));

        this.setPreferredSize(new Dimension(preferedWidth, preferedWidth));

        svgCanvas.setSVGDocument(doc);
//        svgCanvas.setDocument(doc);
        int counterTest = 0;
        for (GraphDataNode currentNode : graphData.getDataNodes()) {
            counterTest++;
            Element symbolNode;
            switch (currentNode.symbolType) {
                case circle:
                    symbolNode = doc.createElementNS(svgNS, "circle");
                    symbolNode.setAttributeNS(null, "cx", Integer.toString(currentNode.xPos * hSpacing + hSpacing));
                    symbolNode.setAttributeNS(null, "cy", Integer.toString(currentNode.yPos * vSpacing + vSpacing));
                    symbolNode.setAttributeNS(null, "r", Integer.toString(symbolSize / 2));
                    symbolNode.setAttributeNS(null, "height", Integer.toString(symbolSize));
//            <circle id="_16" cx="120.0" cy="155.0" r="50" fill="red" stroke="black" stroke-width="1"/>
//    <polygon id="_17" transform="matrix(0.7457627,0.0,0.0,circle0.6567164,467.339,103.462685)" points="20,10 80,40 40,80" fill="blue" stroke="black" stroke-width="1"/>
                    break;
                case square:
                    symbolNode = doc.createElementNS(svgNS, "rect");
                    symbolNode.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing - symbolSize / 2));
                    symbolNode.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
                    symbolNode.setAttributeNS(null, "width", Integer.toString(symbolSize));
                    symbolNode.setAttributeNS(null, "height", Integer.toString(symbolSize));
                    break;
                case resource:
                    symbolNode = doc.createElementNS(svgNS, "rect");
                    symbolNode.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing - symbolSize / 2));
                    symbolNode.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
                    symbolNode.setAttributeNS(null, "width", Integer.toString(symbolSize));
                    symbolNode.setAttributeNS(null, "height", Integer.toString(symbolSize));
                    symbolNode.setAttributeNS(null, "transform", "rotate(-45 " + Integer.toString(currentNode.xPos * hSpacing + hSpacing - symbolSize / 2) + " " + Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2) + ")");
                    symbolNode.setAttributeNS(null, "stroke-width", "4");
                    symbolNode.setAttributeNS(null, "fill", "black");
                    break;
                case union:
//                    DOMUtilities.deepCloneDocument(doc, doc.getImplementation());

//                    symbolNode = doc.createElementNS(svgNS, "layer");
//                    Element upperNode = doc.createElementNS(svgNS, "rect");
//                    Element lowerNode = doc.createElementNS(svgNS, "rect");
//                    upperNode.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing - symbolSize / 2));
//                    upperNode.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
//                    upperNode.setAttributeNS(null, "width", Integer.toString(symbolSize));
//                    upperNode.setAttributeNS(null, "height", Integer.toString(symbolSize / 3));
//                    lowerNode.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing - symbolSize / 2 + (symbolSize / 3) * 2));
//                    lowerNode.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
//                    lowerNode.setAttributeNS(null, "width", Integer.toString(symbolSize));
//                    lowerNode.setAttributeNS(null, "height", Integer.toString(symbolSize / 3));
//                    lowerNode.appendChild(upperNode);
//                    symbolNode.appendChild(lowerNode);
                    symbolNode = doc.createElementNS(svgNS, "polyline");
                    int posXa = currentNode.xPos * hSpacing + hSpacing - symbolSize / 2;
                    int posYa = currentNode.yPos * vSpacing + vSpacing + symbolSize / 2;
                    int offsetAmounta = symbolSize / 2;
                    symbolNode.setAttributeNS(null, "fill", "none");
                    symbolNode.setAttributeNS(null, "points", (posXa + offsetAmounta * 3) + "," + (posYa + offsetAmounta) + " " + (posXa - offsetAmounta) + "," + (posYa + offsetAmounta) + " " + (posXa - offsetAmounta) + "," + (posYa - offsetAmounta) + " " + (posXa + offsetAmounta * 3) + "," + (posYa - offsetAmounta));

                    break;
                case triangle:
                    symbolNode = doc.createElementNS(svgNS, "polygon");
                    int posXt = currentNode.xPos * hSpacing + hSpacing;
                    int posYt = currentNode.yPos * vSpacing + vSpacing;
                    int triangleHeight = (int) (Math.sqrt(3) * symbolSize / 2);
                    symbolNode.setAttributeNS(null, "points",
                            (posXt - symbolSize / 2) + "," + (posYt + triangleHeight / 2) + " "
                            + (posXt) + "," + (posYt - +triangleHeight / 2) + " "
                            + (posXt + symbolSize / 2) + "," + (posYt + triangleHeight / 2));
                    break;
//                case equals:
//                    symbolNode = doc.createElementNS(svgNS, "rect");
//                    symbolNode.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * stepNumber + stepNumber - symbolSize));
//                    symbolNode.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * stepNumber + stepNumber));
//                    symbolNode.setAttributeNS(null, "width", Integer.toString(symbolSize / 2));
//                    symbolNode.setAttributeNS(null, "height", Integer.toString(symbolSize / 2));
//                    break;
                default:
                    symbolNode = doc.createElementNS(svgNS, "polyline");
                    int posX = currentNode.xPos * hSpacing + hSpacing - symbolSize / 2;
                    int posY = currentNode.yPos * vSpacing + vSpacing + symbolSize / 2;
                    int offsetAmount = symbolSize / 2;
                    symbolNode.setAttributeNS(null, "fill", "none");
                    symbolNode.setAttributeNS(null, "points", (posX - offsetAmount) + "," + (posY - offsetAmount) + " " + (posX + offsetAmount) + "," + (posY + offsetAmount) + " " + (posX) + "," + (posY) + " " + (posX - offsetAmount) + "," + (posY + offsetAmount) + " " + (posX + offsetAmount) + "," + (posY - offsetAmount));
            }
            if (currentNode.isEgo) {
                symbolNode.setAttributeNS(null, "fill", "red");
            } else {
                symbolNode.setAttributeNS(null, "fill", "none");
            }

            symbolNode.setAttributeNS(null, "stroke", "black");
            symbolNode.setAttributeNS(null, "stroke-width", "2");
            // Attach the rectangle to the root 'svg' element.
            svgRoot.appendChild(symbolNode);

            ((EventTarget) symbolNode).addEventListener("mouseover", new EventListener() {

                public void handleEvent(Event evt) {
                    System.out.println("OnMouseOverCircleAction: " + evt.getCurrentTarget());
                    if (currentDraggedElement == null) {
                        ((Element) evt.getCurrentTarget()).setAttribute("fill", "green");
                    }
                }
            }, false);
            ((EventTarget) symbolNode).addEventListener("mouseout",
                    new EventListener() {

                        public void handleEvent(Event evt) {
                            System.out.println("mouseout: " + evt.getCurrentTarget());
                            if (currentDraggedElement == null) {
                                ((Element) evt.getCurrentTarget()).setAttribute("fill", "none");
                            }
                        }
                    }, false);
            ((EventTarget) symbolNode).addEventListener("mousedown",
                    new EventListener() {

                        public void handleEvent(Event evt) {
                            System.out.println("mousedrag: " + evt.getCurrentTarget());
                            currentDraggedElement = ((Element) evt.getCurrentTarget());
                            preDragCursor = svgCanvas.getCursor();
                            ((Element) evt.getCurrentTarget()).setAttribute("fill", "red");
                        }
                    }, false);
////////////////////////////// tspan method appears to fail in batik rendering process unless saved and reloaded ////////////////////////////////////////////////
//            Element labelText = doc.createElementNS(svgNS, "text");
////            labelText.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing + symbolSize / 2));
////            labelText.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2));
//            labelText.setAttributeNS(null, "fill", "black");
//            labelText.setAttributeNS(null, "fill-opacity", "1");
//            labelText.setAttributeNS(null, "stroke-width", "0");
//            labelText.setAttributeNS(null, "font-size", "14px");
////            labelText.setAttributeNS(null, "text-anchor", "end");
////            labelText.setAttributeNS(null, "style", "font-size:14px;text-anchor:end;fill:black;fill-opacity:1");
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
//            svgRoot.appendChild(labelText);
////////////////////////////// end tspan method appears to fail in batik rendering process ////////////////////////////////////////////////

////////////////////////////// alternate method ////////////////////////////////////////////////
            // todo: this method has the draw back that the text is not selectable as a block
            int textSpanCounter = 0;
            int lineSpacing = 15;
            for (String currentTextLable : currentNode.getLabel()) {
                Element labelText = doc.createElementNS(svgNS, "text");
                labelText.setAttributeNS(null, "x", Integer.toString(currentNode.xPos * hSpacing + hSpacing + symbolSize / 2));
                labelText.setAttributeNS(null, "y", Integer.toString(currentNode.yPos * vSpacing + vSpacing - symbolSize / 2 + textSpanCounter));
                labelText.setAttributeNS(null, "fill", "black");
                labelText.setAttributeNS(null, "stroke-width", "0");
                labelText.setAttributeNS(null, "font-size", "14");
                Text textNode = doc.createTextNode(currentTextLable);
                labelText.appendChild(textNode);
                textSpanCounter += lineSpacing;
                svgRoot.appendChild(labelText);
            }
////////////////////////////// end alternate method ////////////////////////////////////////////////
            // draw links
            for (GraphDataNode.NodeRelation graphLinkNode : currentNode.getNodeRelations()) {
                if (graphLinkNode.sourceNode.equals(currentNode)) {
                    System.out.println("link: " + graphLinkNode.linkedNode.xPos + ":" + graphLinkNode.linkedNode.yPos);

//                <line id="_15" transform="translate(146.0,112.0)" x1="0" y1="0" x2="100" y2="100" ="black" stroke-width="1"/>
                    Element linkLine = doc.createElementNS(svgNS, "line");
                    linkLine.setAttributeNS(null, "x1", Integer.toString(currentNode.xPos * hSpacing + hSpacing));
                    linkLine.setAttributeNS(null, "y1", Integer.toString(currentNode.yPos * vSpacing + vSpacing));

                    linkLine.setAttributeNS(null, "x2", Integer.toString(graphLinkNode.linkedNode.xPos * hSpacing + hSpacing));
                    linkLine.setAttributeNS(null, "y2", Integer.toString(graphLinkNode.linkedNode.yPos * vSpacing + vSpacing));
                    linkLine.setAttributeNS(null, "stroke", "black");
                    linkLine.setAttributeNS(null, "stroke-width", "1");
                    // Attach the rectangle to the root 'svg' element.
                    svgRoot.appendChild(linkLine);
                }
            }
        }
        //new CmdiComponentBuilder().savePrettyFormatting(doc, new File("/Users/petwit/Documents/SharedInVirtualBox/mpi-co-svn-mpi-nl/LAT/Kinnate/trunk/src/main/resources/output.svg"));
        svgCanvas.revalidate();
    }
}
