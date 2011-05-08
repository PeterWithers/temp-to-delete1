package nl.mpi.kinnate.svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import nl.mpi.kinnate.kindata.EntityData;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;

/**
 *  Document   : EntitySvg
 *  Created on : Mar 9, 2011, 3:20:56 PM
 *  Author     : Peter Withers
 */
public class EntitySvg {

    protected HashMap<String, Float[]> entityPositions;
    private int symbolSize = 15;
    static protected int strokeWidth = 2;

    public EntitySvg() {
        entityPositions = new HashMap<String, Float[]>();
    }

    public void readEntityPositions(Node entityGroup) {
        // todo: read the entity positions from the existing dom
        // todo: only the x pos needs to be sotred because the y pos is determined by the generations
//        entityPositions
    }

    public void insertSymbols(SVGDocument doc, String svgNameSpace) {
        Element svgRoot = doc.getDocumentElement();
        Element defsNode = doc.createElementNS(svgNameSpace, "defs");
        defsNode.setAttribute("id", "KinSymbols");
        // add the circle symbol
        Element circleGroup = doc.createElementNS(svgNameSpace, "g");
        circleGroup.setAttribute("id", "circle");
        Element circleNode = doc.createElementNS(svgNameSpace, "circle");
        circleNode.setAttribute("cx", Integer.toString(symbolSize / 2));
        circleNode.setAttribute("cy", Integer.toString(symbolSize / 2));
        circleNode.setAttribute("r", Integer.toString(symbolSize / 2));
        circleNode.setAttribute("height", Integer.toString(symbolSize));
        circleNode.setAttribute("stroke", "black");
        circleNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        circleGroup.appendChild(circleNode);
        defsNode.appendChild(circleGroup);

        // add the square symbol
        Element squareGroup = doc.createElementNS(svgNameSpace, "g");
        squareGroup.setAttribute("id", "square");
        Element squareNode = doc.createElementNS(svgNameSpace, "rect");
        squareNode.setAttribute("x", "0");
        squareNode.setAttribute("y", "0");
        squareNode.setAttribute("width", Integer.toString(symbolSize));
        squareNode.setAttribute("height", Integer.toString(symbolSize));
        squareNode.setAttribute("stroke", "black");
        squareNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        squareGroup.appendChild(squareNode);
        defsNode.appendChild(squareGroup);
        svgRoot.appendChild(defsNode);

        // add the resource symbol
        Element resourceGroup = doc.createElementNS(svgNameSpace, "g");
        resourceGroup.setAttribute("id", "square45");
        Element resourceNode = doc.createElementNS(svgNameSpace, "rect");
        resourceNode.setAttribute("x", "0");
        resourceNode.setAttribute("y", "0");
        resourceNode.setAttribute("transform", "rotate(-45 " + Integer.toString(symbolSize / 2) + " " + Integer.toString(symbolSize / 2) + ")");
        resourceNode.setAttribute("width", Integer.toString(symbolSize));
        resourceNode.setAttribute("height", Integer.toString(symbolSize));
        resourceNode.setAttribute("stroke", "black");
        resourceNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        resourceGroup.appendChild(resourceNode);
        defsNode.appendChild(resourceGroup);
        svgRoot.appendChild(defsNode);

        // add the rhombus symbol
        Element rhombusGroup = doc.createElementNS(svgNameSpace, "g");
        rhombusGroup.setAttribute("id", "rhombus");
        Element rhombusNode = doc.createElementNS(svgNameSpace, "rect");
        rhombusNode.setAttribute("x", "0");
        rhombusNode.setAttribute("y", "0");
        rhombusNode.setAttribute("transform", "scale(1,2), rotate(-45 " + Integer.toString(symbolSize / 2) + " " + Integer.toString(symbolSize / 2) + ")");
        rhombusNode.setAttribute("width", Integer.toString(symbolSize));
        rhombusNode.setAttribute("height", Integer.toString(symbolSize));
        rhombusNode.setAttribute("stroke", "black");
        rhombusNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        rhombusGroup.appendChild(rhombusNode);
        defsNode.appendChild(rhombusGroup);
        svgRoot.appendChild(defsNode);

        // add the union symbol
        Element unionGroup = doc.createElementNS(svgNameSpace, "g");
        unionGroup.setAttribute("id", "union");
        Element upperNode = doc.createElementNS(svgNameSpace, "line");
        Element lowerNode = doc.createElementNS(svgNameSpace, "line");
        upperNode.setAttribute("x1", Integer.toString(0));
        upperNode.setAttribute("y1", Integer.toString((symbolSize / 6)));
        upperNode.setAttribute("x2", Integer.toString(symbolSize));
        upperNode.setAttribute("y2", Integer.toString((symbolSize / 6)));
        upperNode.setAttribute("stroke-width", Integer.toString(symbolSize / 3));
        upperNode.setAttribute("stroke", "black");
        lowerNode.setAttribute("x1", Integer.toString(0));
        lowerNode.setAttribute("y1", Integer.toString(symbolSize - (symbolSize / 6)));
        lowerNode.setAttribute("x2", Integer.toString(symbolSize));
        lowerNode.setAttribute("y2", Integer.toString(symbolSize - (symbolSize / 6)));
        lowerNode.setAttribute("stroke-width", Integer.toString(symbolSize / 3));
        lowerNode.setAttribute("stroke", "black");
        // add a background for selecting and draging
        Element backgroundNode = doc.createElementNS(svgNameSpace, "rect");
        backgroundNode.setAttribute("x", "0");
        backgroundNode.setAttribute("y", "0");
        backgroundNode.setAttribute("width", Integer.toString(symbolSize));
        backgroundNode.setAttribute("height", Integer.toString(symbolSize));
        backgroundNode.setAttribute("stroke", "none");
        backgroundNode.setAttribute("fill", "white");
        unionGroup.appendChild(backgroundNode);
        unionGroup.appendChild(upperNode);
        unionGroup.appendChild(lowerNode);
        defsNode.appendChild(unionGroup);
        svgRoot.appendChild(defsNode);

        // add the triangle symbol
        Element triangleGroup = doc.createElementNS(svgNameSpace, "g");
        triangleGroup.setAttribute("id", "triangle");
        Element triangleNode = doc.createElementNS(svgNameSpace, "polygon");
        int triangleHeight = (int) (Math.sqrt(3) * symbolSize / 2);
        triangleNode.setAttribute("points", (symbolSize / 2) + "," + 0 + " "
                + 0 + "," + triangleHeight
                + " " + symbolSize + "," + triangleHeight);
        triangleNode.setAttribute("stroke", "black");
        triangleNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        triangleGroup.appendChild(triangleNode);
        defsNode.appendChild(triangleGroup);
        svgRoot.appendChild(defsNode);

        // add the equals symbol
        Element equalsGroup = doc.createElementNS(svgNameSpace, "g");
        equalsGroup.setAttribute("id", "equals");
        Element equalsNode = doc.createElementNS(svgNameSpace, "polyline");
        int offsetAmounta = symbolSize / 2;
        int posXa = 0;
        int posYa = +symbolSize / 2;
        equalsNode.setAttribute("points", (posXa + offsetAmounta * 3) + "," + (posYa + offsetAmounta) + " " + (posXa - offsetAmounta) + "," + (posYa + offsetAmounta) + " " + (posXa - offsetAmounta) + "," + (posYa - offsetAmounta) + " " + (posXa + offsetAmounta * 3) + "," + (posYa - offsetAmounta));
        equalsNode.setAttribute("fill", "white");
        equalsNode.setAttribute("stroke", "black");
        equalsNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        equalsGroup.appendChild(equalsNode);
        defsNode.appendChild(equalsGroup);
        svgRoot.appendChild(defsNode);

        // add the cross symbol
        Element crossGroup = doc.createElementNS(svgNameSpace, "g");
        crossGroup.setAttribute("id", "cross");
        Element crossNode = doc.createElementNS(svgNameSpace, "polyline");
        int posX = symbolSize / 2;
        int posY = symbolSize / 2;
        int offsetAmount = symbolSize / 2;
        crossNode.setAttribute("points", (posX - offsetAmount) + "," + (posY - offsetAmount) + " " + (posX + offsetAmount) + "," + (posY + offsetAmount) + " " + (posX) + "," + (posY) + " " + (posX - offsetAmount) + "," + (posY + offsetAmount) + " " + (posX + offsetAmount) + "," + (posY - offsetAmount));
        crossNode.setAttribute("fill", "none");
        crossNode.setAttribute("stroke", "black");
        crossNode.setAttribute("stroke-width", Integer.toString(strokeWidth));
        crossGroup.appendChild(crossNode);
        defsNode.appendChild(crossGroup);
        svgRoot.appendChild(defsNode);
    }

    public String[] listSymbolNames(SVGDocument doc) {
        // get the symbol list from the dom
        ArrayList<String> symbolArray = new ArrayList<String>();
        Element kinSymbols = doc.getElementById("KinSymbols");
        if (kinSymbols != null) {
            for (Node kinSymbolNode = kinSymbols.getFirstChild(); kinSymbolNode != null; kinSymbolNode = kinSymbolNode.getNextSibling()) {
                symbolArray.add(kinSymbolNode.getAttributes().getNamedItem("id").getNodeValue());
            }
        }
        return symbolArray.toArray(new String[]{});
    }

    public Float[] getEntityLocation(String entityId) {
        Float[] returnLoc = entityPositions.get(entityId);
        Float xPos = returnLoc[0] + (symbolSize / 2);
        Float yPos = returnLoc[1] + (symbolSize / 2);
        return new Float[]{xPos, yPos};
    }
//    public float[] getEntityLocation(SVGDocument doc, String entityId) {
//        Element entitySymbol = doc.getElementById(entityId + "symbol");
////        Element entitySymbol = doc.getElementById(entityId); // the sybol group node
//        if (entitySymbol != null) {
//            SVGRect bbox = ((SVGLocatable) entitySymbol).getBBox();
//            SVGMatrix sVGMatrix = ((SVGLocatable) entitySymbol).getCTM();
////            System.out.println("getA: " + sVGMatrix.getA());
////            System.out.println("getB: " + sVGMatrix.getB());
////            System.out.println("getC: " + sVGMatrix.getC());
////            System.out.println("getD: " + sVGMatrix.getD());
////            System.out.println("getE: " + sVGMatrix.getE());
////            System.out.println("getF: " + sVGMatrix.getF());
//
////            System.out.println("bbox X: " + bbox.getX());
////            System.out.println("bbox Y: " + bbox.getY());
////            System.out.println("bbox W: " + bbox.getWidth());
////            System.out.println("bbox H: " + bbox.getHeight());
//            return new float[]{sVGMatrix.getE() + bbox.getWidth() / 2, sVGMatrix.getF() + bbox.getHeight() / 2};
////            bbox.setX(sVGMatrix.getE());
////            bbox.setY(sVGMatrix.getF());
////            return bbox;
//        } else {
//            return null;
//        }
//    }

    public float[] moveEntity(SVGDocument doc, String entityId, float shiftX, float shiftY, boolean snapToGrid) {
        Element entitySymbol = doc.getElementById(entityId);
        float remainderAfterSnapX = 0;
        float remainderAfterSnapY = 0;
        if (entitySymbol != null) {
            boolean allowYshift = entitySymbol.getLocalName().equals("text");
            SVGMatrix sVGMatrix = ((SVGLocatable) entitySymbol).getCTM();
//            sVGMatrix.setE(sVGMatrix.getE() + shiftX);
//            sVGMatrix.setE(sVGMatrix.getF() + shiftY);
//            System.out.println("shiftX: " + shiftX);
            float updatedPositionX = sVGMatrix.getE() + shiftX;
            float updatedPositionY = sVGMatrix.getF();
            if (allowYshift) {
                updatedPositionY = updatedPositionY + shiftY;
            }
//            System.out.println("updatedPosition: " + updatedPosition);
            if (snapToGrid) {
                float updatedSnapPositionX = Math.round(updatedPositionX / 50) * 50; // limit movement to the grid
                remainderAfterSnapX = updatedPositionX - updatedSnapPositionX;
                updatedPositionX = updatedSnapPositionX;
                if (allowYshift) {
                    float updatedSnapPositionY = Math.round(updatedPositionY / 50) * 50; // limit movement to the grid
                    remainderAfterSnapY = updatedPositionY - updatedSnapPositionY;
                    updatedPositionY = updatedSnapPositionY;
                }
            } else {
                updatedPositionX = (int) updatedPositionX;  // prevent uncorrectable but visible variations in the position of entities to each other
                if (allowYshift) {
                    updatedPositionY = (int) updatedPositionY;
                }
            }
//            System.out.println("updatedPosition after snap: " + updatedPosition);
            entityPositions.put(entityId, new Float[]{updatedPositionX, updatedPositionY});
            ((Element) entitySymbol).setAttribute("transform", "translate(" + String.valueOf(updatedPositionX) + ", " + String.valueOf(updatedPositionY) + ")");
//            ((Element) entitySymbol).setAttribute("transform", "translate(" + String.valueOf(sVGMatrix.getE() + shiftX) + ", " + (sVGMatrix.getF() + shiftY) + ")");
        }
//        System.out.println("remainderAfterSnap: " + remainderAfterSnap);
        return new float[]{remainderAfterSnapX, remainderAfterSnapY};
    }

    public Element createEntitySymbol(GraphPanel graphPanel, EntityData currentNode, int hSpacing, int vSpacing) {
        Element groupNode = graphPanel.doc.createElementNS(graphPanel.svgNameSpace, "g");
        groupNode.setAttribute("id", currentNode.getUniqueIdentifier());
        groupNode.setAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kin:path", currentNode.getEntityPath());
        // the kin type strings are stored here so that on selection in the graph the add kin term panel can be pre populatedwith the kin type strings of the selection
        groupNode.setAttributeNS(DataStoreSvg.kinDataNameSpaceLocation, "kin:kintype", currentNode.getKinTypeString());
//        counterTest++;
        Element symbolNode;
        String symbolType = currentNode.getSymbolType();
        if (symbolType == null || symbolType.length() == 0) {
            symbolType = "square";
        }
        // todo: check that if an entity is already placed in which case do not recreate
        // todo: do not create a new dom each time but reuse it instead, or due to the need to keep things up to date maybe just store an array of entity locations instead
        symbolNode = graphPanel.doc.createElementNS(graphPanel.svgNameSpace, "use");
        symbolNode.setAttribute("id", currentNode.getUniqueIdentifier() + "symbol");
        symbolNode.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + symbolType); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
        Float[] storedPosition = entityPositions.get(currentNode.getUniqueIdentifier());
        if (storedPosition == null) {
            storedPosition = new Float[]{currentNode.getxPos() * hSpacing + hSpacing - symbolSize / 2.0f,
                        currentNode.getyPos() * vSpacing + vSpacing - symbolSize / 2.0f};
            entityPositions.put(currentNode.getUniqueIdentifier(), storedPosition);
        }
        groupNode.setAttribute("transform", "translate(" + Float.toString(storedPosition[0]) + ", " + Float.toString(storedPosition[1]) + ")");
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
        ArrayList<String> labelList = new ArrayList<String>();
        if (graphPanel.dataStoreSvg.showLabels) {
            labelList.addAll(Arrays.asList(currentNode.getLabel()));
        }
        if (graphPanel.dataStoreSvg.showKinTypeLabels) {
            labelList.addAll(Arrays.asList(currentNode.getKinTypeStringArray()));
        }
        if (graphPanel.dataStoreSvg.showKinTermLabels) {
            labelList.addAll(Arrays.asList(currentNode.getKinTermStrings()));
        }
        // todo: this method has the draw back that the text is not selectable as a block
        int textSpanCounter = 0;
        int lineSpacing = 15;
        for (String currentTextLable : labelList) {
            Element labelText = graphPanel.doc.createElementNS(graphPanel.svgNameSpace, "text");
            labelText.setAttribute("x", Double.toString(symbolSize * 1.5));
            labelText.setAttribute("y", Integer.toString(textSpanCounter));
            labelText.setAttribute("fill", "black");
            labelText.setAttribute("stroke-width", "0");
            labelText.setAttribute("font-size", "14");
            Text textNode = graphPanel.doc.createTextNode(currentTextLable);
            labelText.appendChild(textNode);
            textSpanCounter += lineSpacing;
            groupNode.appendChild(labelText);
        }
////////////////////////////// end alternate method ////////////////////////////////////////////////
        ((EventTarget) groupNode).addEventListener("mousedown", new MouseListenerSvg(graphPanel), false);
        return groupNode;
    }
}
