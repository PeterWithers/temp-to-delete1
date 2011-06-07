package nl.mpi.kinnate.svg;

import nl.mpi.kinnate.kindata.EntityData;
import java.util.ArrayList;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.EntityRelation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGDocument;

/**
 *  Document   : RelationSvg
 *  Created on : Mar 9, 2011, 3:21:16 PM
 *  Author     : Peter Withers
 */
public class RelationSvg {

    private void addUseNode(SVGDocument doc, String svgNameSpace, Element targetGroup, String targetDefId) {
        String useNodeId = targetDefId + "use";
        Node useNodeOld = doc.getElementById(useNodeId);
        if (useNodeOld != null) {
            useNodeOld.getParentNode().removeChild(useNodeOld);
        }
        Element useNode = doc.createElementNS(svgNameSpace, "use");
        useNode.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + targetDefId); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
        //                    useNode.setAttribute("href", "#" + lineIdString);
        useNode.setAttribute("id", useNodeId);
        targetGroup.appendChild(useNode);
    }

    private void updateLabelNode(SVGDocument doc, String svgNameSpace, String lineIdString, String targetRelationId) {
        // remove and readd the text on path label so that it updates with the new path
        String labelNodeId = targetRelationId + "label";
        Node useNodeOld = doc.getElementById(labelNodeId);
        if (useNodeOld != null) {
            Node textParentNode = useNodeOld.getParentNode();
            String labelText = useNodeOld.getTextContent();
            useNodeOld.getParentNode().removeChild(useNodeOld);

            Element textPath = doc.createElementNS(svgNameSpace, "textPath");
            textPath.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + lineIdString); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
            textPath.setAttribute("startOffset", "50%");
            textPath.setAttribute("id", labelNodeId);
            Text textNode = doc.createTextNode(labelText);
            textPath.appendChild(textNode);
            textParentNode.appendChild(textPath);
        }
    }

    private void setPolylinePointsAttribute(Element targetNode, DataTypes.RelationType relationType, float vSpacing, float egoX, float egoY, float alterX, float alterY) {
        float midY = (egoY + alterY) / 2;
        if (alterY == egoY) {
            // make sure that union lines go below the entities and sibling lines go above
            if (relationType == DataTypes.RelationType.sibling) {
                midY = alterY - vSpacing / 2;
            } else if (relationType == DataTypes.RelationType.union) {
                midY = alterY + vSpacing / 2;
            }
        }
        targetNode.setAttribute("points",
                egoX + "," + egoY + " "
                + egoX + "," + midY + " "
                + alterX + "," + midY + " "
                + alterX + "," + alterY);
    }

    private void setPathPointsAttribute(Element targetNode, DataTypes.RelationType relationType, DataTypes.RelationLineType relationLineType, float hSpacing, float vSpacing, float egoX, float egoY, float alterX, float alterY) {
        float fromBezX;
        float fromBezY;
        float toBezX;
        float toBezY;
        if ((egoX > alterX && egoY < alterY) || (egoX > alterX && egoY > alterY)) {
            // prevent the label on the line from rendering upside down
            float tempX = alterX;
            float tempY = alterY;
            alterX = egoX;
            alterY = egoY;
            egoX = tempX;
            egoY = tempY;
        }
        if (relationLineType == DataTypes.RelationLineType.verticalCurve) {
            fromBezX = egoX;
            fromBezY = alterY;
            toBezX = alterX;
            toBezY = egoY;
            // todo: update the bezier positions similar to in the follwing else statement
            if (1 / (egoY - alterY) < vSpacing) {
                fromBezX = egoX;
                fromBezY = alterY - vSpacing / 2;
                toBezX = alterX;
                toBezY = egoY - vSpacing / 2;
            }
        } else {
            fromBezX = alterX;
            fromBezY = egoY;
            toBezX = egoX;
            toBezY = alterY;
            // todo: if the nodes are almost in align then this test fails and it should insted check for proximity not equality
//            System.out.println(1 / (egoX - alterX));
//            if (1 / (egoX - alterX) < vSpacing) {              
            if (egoX > alterX) {
                if (egoX - alterX < hSpacing / 4) {
                    fromBezX = egoX - hSpacing / 4;
                    toBezX = alterX - hSpacing / 4;
                } else {
                    fromBezX = (egoX - alterX) / 2 + alterX;
                    toBezX = (egoX - alterX) / 2 + alterX;
                }
            } else {
                if (alterX - egoX < hSpacing / 4) {
                    fromBezX = egoX + hSpacing / 4;
                    toBezX = alterX + hSpacing / 4;
                } else {
                    fromBezX = (alterX - egoX) / 2 + egoX;
                    toBezX = (alterX - egoX) / 2 + egoX;
                }
            }

        }
        targetNode.setAttribute("d", "M " + egoX + "," + egoY + " C " + fromBezX + "," + fromBezY + " " + toBezX + "," + toBezY + " " + alterX + "," + alterY);
    }

    protected void insertRelation(GraphPanel graphPanel, String svgNameSpace, Element relationGroupNode, EntityData currentNode, EntityRelation graphLinkNode, int hSpacing, int vSpacing) {
        int relationLineIndex = relationGroupNode.getChildNodes().getLength();
        Element groupNode = graphPanel.doc.createElementNS(svgNameSpace, "g");
        groupNode.setAttribute("id", "relation" + relationLineIndex);
        Element defsNode = graphPanel.doc.createElementNS(svgNameSpace, "defs");
        String lineIdString = "relation" + relationLineIndex + "Line";
        new DataStoreSvg().storeRelationParameters(graphPanel.doc, groupNode, graphLinkNode.relationType, graphLinkNode.relationLineType, currentNode.getUniqueIdentifier(), graphLinkNode.getAlterNode().getUniqueIdentifier());
        // set the line end points
//        int[] egoSymbolPoint = graphPanel.dataStoreSvg.graphData.getEntityLocation(currentNode.getUniqueIdentifier());
//        int[] alterSymbolPoint = graphPanel.dataStoreSvg.graphData.getEntityLocation(graphLinkNode.getAlterNode().getUniqueIdentifier());
        float[] egoSymbolPoint = graphPanel.entitySvg.getEntityLocation(currentNode.getUniqueIdentifier());
        float[] alterSymbolPoint = graphPanel.entitySvg.getEntityLocation(graphLinkNode.getAlterNode().getUniqueIdentifier());
//        float fromX = (currentNode.getxPos()); // * hSpacing + hSpacing
//        float fromY = (currentNode.getyPos()); // * vSpacing + vSpacing
//        float toX = (graphLinkNode.getAlterNode().getxPos()); // * hSpacing + hSpacing
//        float toY = (graphLinkNode.getAlterNode().getyPos()); // * vSpacing + vSpacing
        float fromX = (egoSymbolPoint[0]); // * hSpacing + hSpacing
        float fromY = (egoSymbolPoint[1]); // * vSpacing + vSpacing
        float toX = (alterSymbolPoint[0]); // * hSpacing + hSpacing
        float toY = (alterSymbolPoint[1]); // * vSpacing + vSpacing

        switch (graphLinkNode.relationLineType) {
            case kinTermLine:
            // this case uses the following case
            case verticalCurve:
                // todo: groupNode.setAttribute("id", );
                //                    System.out.println("link: " + graphLinkNode.linkedNode.xPos + ":" + graphLinkNode.linkedNode.yPos);
                //
                ////                <line id="_15" transform="translate(146.0,112.0)" x1="0" y1="0" x2="100" y2="100" ="black" stroke-width="1"/>
                //                    Element linkLine = doc.createElementNS(svgNS, "line");
                //                    linkLine.setAttribute("x1", Integer.toString(currentNode.xPos * hSpacing + hSpacing));
                //                    linkLine.setAttribute("y1", Integer.toString(currentNode.yPos * vSpacing + vSpacing));
                //
                //                    linkLine.setAttribute("x2", Integer.toString(graphLinkNode.linkedNode.xPos * hSpacing + hSpacing));
                //                    linkLine.setAttribute("y2", Integer.toString(graphLinkNode.linkedNode.yPos * vSpacing + vSpacing));
                //                    linkLine.setAttribute("stroke", "black");
                //                    linkLine.setAttribute("stroke-width", "1");
                //                    // Attach the rectangle to the root 'svg' element.
                //                    svgRoot.appendChild(linkLine);
                //System.out.println("link: " + graphLinkNode.getAlterNode().xPos + ":" + graphLinkNode.getAlterNode().yPos);

                //                <line id="_15" transform="translate(146.0,112.0)" x1="0" y1="0" x2="100" y2="100" ="black" stroke-width="1"/>
                Element linkLine = graphPanel.doc.createElementNS(svgNameSpace, "path");

                setPathPointsAttribute(linkLine, graphLinkNode.relationType, graphLinkNode.relationLineType, hSpacing, vSpacing, fromX, fromY, toX, toY);
                //                    linkLine.setAttribute("x1", );
                //                    linkLine.setAttribute("y1", );
                //
                //                    linkLine.setAttribute("x2", );
                linkLine.setAttribute("fill", "none");
                if (graphLinkNode.lineColour != null) {
                    linkLine.setAttribute("stroke", graphLinkNode.lineColour);
                } else {
                    linkLine.setAttribute("stroke", "blue");
                }
                linkLine.setAttribute("stroke-width", Integer.toString(EntitySvg.strokeWidth));
                linkLine.setAttribute("id", lineIdString);
                defsNode.appendChild(linkLine);
                break;
            case sanguineLine:
                //                            Element squareLinkLine = doc.createElement("line");
                //                            squareLinkLine.setAttribute("x1", Integer.toString(currentNode.xPos * hSpacing + hSpacing));
                //                            squareLinkLine.setAttribute("y1", Integer.toString(currentNode.yPos * vSpacing + vSpacing));
                //
                //                            squareLinkLine.setAttribute("x2", Integer.toString(graphLinkNode.linkedNode.xPos * hSpacing + hSpacing));
                //                            squareLinkLine.setAttribute("y2", Integer.toString(graphLinkNode.linkedNode.yPos * vSpacing + vSpacing));
                //                            squareLinkLine.setAttribute("stroke", "grey");
                //                            squareLinkLine.setAttribute("stroke-width", Integer.toString(strokeWidth));
                Element squareLinkLine = graphPanel.doc.createElementNS(svgNameSpace, "polyline");

                setPolylinePointsAttribute(squareLinkLine, graphLinkNode.relationType, vSpacing, fromX, fromY, toX, toY);

                squareLinkLine.setAttribute("fill", "none");
                squareLinkLine.setAttribute("stroke", "grey");
                squareLinkLine.setAttribute("stroke-width", Integer.toString(EntitySvg.strokeWidth));
                squareLinkLine.setAttribute("id", lineIdString);
                defsNode.appendChild(squareLinkLine);
                break;
        }
        groupNode.appendChild(defsNode);

        // insert the node that uses the above definition
        addUseNode(graphPanel.doc, svgNameSpace, groupNode, lineIdString);

        // add the relation label
        if (graphLinkNode.labelString != null) {
            Element labelText = graphPanel.doc.createElementNS(svgNameSpace, "text");
            labelText.setAttribute("text-anchor", "middle");
            //                        labelText.setAttribute("x", Integer.toString(labelX));
            //                        labelText.setAttribute("y", Integer.toString(labelY));
            if (graphLinkNode.lineColour != null) {
                labelText.setAttribute("fill", graphLinkNode.lineColour);
            } else {
                labelText.setAttribute("fill", "blue");
            }
            labelText.setAttribute("stroke-width", "0");
            labelText.setAttribute("font-size", "14");
            //                        labelText.setAttribute("transform", "rotate(45)");
            Element textPath = graphPanel.doc.createElementNS(svgNameSpace, "textPath");
            textPath.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + lineIdString); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
            textPath.setAttribute("startOffset", "50%");
            textPath.setAttribute("id", "relation" + relationLineIndex + "label");
            Text textNode = graphPanel.doc.createTextNode(graphLinkNode.labelString);
            textPath.appendChild(textNode);
            labelText.appendChild(textPath);
            groupNode.appendChild(labelText);
        }
        relationGroupNode.appendChild(groupNode);
    }

    public void updateRelationLines(GraphPanel graphPanel, ArrayList<String> draggedNodeIds, String svgNameSpace, int hSpacing, int vSpacing) {
        // todo: if an entity is above its ancestor then this must be corrected, if the ancestor data is stored in the relationLine attributes then this would be a good place to correct this
        Element relationGroup = graphPanel.doc.getElementById("RelationGroup");
        for (Node currentChild = relationGroup.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
            if ("g".equals(currentChild.getLocalName())) {
                Node idAttrubite = currentChild.getAttributes().getNamedItem("id");
                //System.out.println("idAttrubite: " + idAttrubite.getNodeValue());
                DataStoreSvg.GraphRelationData graphRelationData = new DataStoreSvg().getEntitiesForRelations(currentChild);
                if (graphRelationData != null) {
                    if (draggedNodeIds.contains(graphRelationData.egoNodeId) || draggedNodeIds.contains(graphRelationData.alterNodeId)) {
                        // todo: update the relation lines
                        //System.out.println("needs update on: " + idAttrubite.getNodeValue());
                        String lineElementId = idAttrubite.getNodeValue() + "Line";
                        Element relationLineElement = graphPanel.doc.getElementById(lineElementId);
                        //System.out.println("type: " + relationLineElement.getLocalName());
                        float[] egoSymbolPoint = graphPanel.entitySvg.getEntityLocation(graphRelationData.egoNodeId);
                        float[] alterSymbolPoint = graphPanel.entitySvg.getEntityLocation(graphRelationData.alterNodeId);
//                        int[] egoSymbolPoint = graphPanel.dataStoreSvg.graphData.getEntityLocation(graphRelationData.egoNodeId);
//                        int[] alterSymbolPoint = graphPanel.dataStoreSvg.graphData.getEntityLocation(graphRelationData.alterNodeId);

                        float egoX = egoSymbolPoint[0];
                        float egoY = egoSymbolPoint[1];
                        float alterX = alterSymbolPoint[0];
                        float alterY = alterSymbolPoint[1];

//                        SVGRect egoSymbolRect = new EntitySvg().getEntityLocation(doc, graphRelationData.egoNodeId);
//                        SVGRect alterSymbolRect = new EntitySvg().getEntityLocation(doc, graphRelationData.alterNodeId);
//
//                        float egoX = egoSymbolRect.getX() + egoSymbolRect.getWidth() / 2;
//                        float egoY = egoSymbolRect.getY() + egoSymbolRect.getHeight() / 2;
//                        float alterX = alterSymbolRect.getX() + alterSymbolRect.getWidth() / 2;
//                        float alterY = alterSymbolRect.getY() + alterSymbolRect.getHeight() / 2;

                        if ("polyline".equals(relationLineElement.getLocalName())) {
                            //System.out.println("polyline to update: " + lineElementId);
                            setPolylinePointsAttribute(relationLineElement, graphRelationData.relationType, vSpacing, egoX, egoY, alterX, alterY);
                        }
                        if ("path".equals(relationLineElement.getLocalName())) {
                            //System.out.println("path to update: " + relationLineElement.getLocalName());
                            setPathPointsAttribute(relationLineElement, graphRelationData.relationType, graphRelationData.relationLineType, hSpacing, vSpacing, egoX, egoY, alterX, alterY);
                        }
                        addUseNode(graphPanel.doc, svgNameSpace, (Element) currentChild, lineElementId);
                        updateLabelNode(graphPanel.doc, svgNameSpace, lineElementId, idAttrubite.getNodeValue());
                    }
                }
            }
        }
    }
//                            new RelationSvg().addTestNode(doc, (Element) relationLineElement.getParentNode().getParentNode(), svgNameSpace);
//    public void addTestNode(SVGDocument doc, Element addTarget, String svgNameSpace) {
//        Element squareNode = doc.createElementNS(svgNameSpace, "rect");
//        squareNode.setAttribute("x", "100");
//        squareNode.setAttribute("y", "100");
//        squareNode.setAttribute("width", "20");
//        squareNode.setAttribute("height", "20");
//        squareNode.setAttribute("fill", "green");
//        squareNode.setAttribute("stroke", "black");
//        squareNode.setAttribute("stroke-width", "2");
//        addTarget.appendChild(squareNode);
//    }
}
