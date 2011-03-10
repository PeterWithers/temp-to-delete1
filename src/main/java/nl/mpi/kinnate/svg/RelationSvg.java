package nl.mpi.kinnate.svg;

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

    protected void insertRelation(SVGDocument doc, String svgNameSpace, Element relationGroupNode, GraphDataNode currentNode, GraphDataNode.NodeRelation graphLinkNode, int hSpacing, int vSpacing, int strokeWidth) {
        int relationLineIndex = relationGroupNode.getChildNodes().getLength();
        Element groupNode = doc.createElementNS(svgNameSpace, "g");
        groupNode.setAttribute("id", "relation" + relationLineIndex);
        Element defsNode = doc.createElementNS(svgNameSpace, "defs");
        String lineIdString = "relationLine" + relationLineIndex;
        new DataStoreSvg().storeRelationParameters(doc, groupNode, currentNode.getEntityPath(), graphLinkNode.linkedNode.getEntityPath());
        // set the line end points
        int fromX = (currentNode.xPos * hSpacing + hSpacing);
        int fromY = (currentNode.yPos * vSpacing + vSpacing);
        int toX = (graphLinkNode.linkedNode.xPos * hSpacing + hSpacing);
        int toY = (graphLinkNode.linkedNode.yPos * vSpacing + vSpacing);
        // set the label position
        int labelX = (fromX + toX) / 2;
        int labelY = (fromY + toY) / 2;

        switch (graphLinkNode.relationLineType) {
            case horizontalCurve:
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
                System.out.println("link: " + graphLinkNode.linkedNode.xPos + ":" + graphLinkNode.linkedNode.yPos);

                //                <line id="_15" transform="translate(146.0,112.0)" x1="0" y1="0" x2="100" y2="100" ="black" stroke-width="1"/>
                Element linkLine = doc.createElementNS(svgNameSpace, "path");
                int fromBezX;
                int fromBezY;
                int toBezX;
                int toBezY;
                if (graphLinkNode.relationLineType == GraphDataNode.RelationLineType.verticalCurve) {
                    fromBezX = fromX;
                    fromBezY = toY;
                    toBezX = toX;
                    toBezY = fromY;
                    if (currentNode.yPos == graphLinkNode.linkedNode.yPos) {
                        fromBezX = fromX;
                        fromBezY = toY - vSpacing / 2;
                        toBezX = toX;
                        toBezY = fromY - vSpacing / 2;
                        // set the label postion and lower it a bit
                        labelY = toBezY + vSpacing / 3;
                    }
                } else {
                    fromBezX = toX;
                    fromBezY = fromY;
                    toBezX = fromX;
                    toBezY = toY;
                    if (currentNode.xPos == graphLinkNode.linkedNode.xPos) {
                        fromBezY = fromY;
                        fromBezX = toX - hSpacing / 2;
                        toBezY = toY;
                        toBezX = fromX - hSpacing / 2;
                        // set the label postion
                        labelX = toBezX;
                    }
                }
                linkLine.setAttribute("d", "M " + fromX + "," + fromY + " C " + fromBezX + "," + fromBezY + " " + toBezX + "," + toBezY + " " + toX + "," + toY);

                //                    linkLine.setAttribute("x1", );
                //                    linkLine.setAttribute("y1", );
                //
                //                    linkLine.setAttribute("x2", );
                linkLine.setAttribute("fill", "none");
                linkLine.setAttribute("stroke", "blue");
                linkLine.setAttribute("stroke-width", Integer.toString(strokeWidth));
                linkLine.setAttribute("id", lineIdString);
                defsNode.appendChild(linkLine);
                break;
            case square:
                //                            Element squareLinkLine = doc.createElement("line");
                //                            squareLinkLine.setAttribute("x1", Integer.toString(currentNode.xPos * hSpacing + hSpacing));
                //                            squareLinkLine.setAttribute("y1", Integer.toString(currentNode.yPos * vSpacing + vSpacing));
                //
                //                            squareLinkLine.setAttribute("x2", Integer.toString(graphLinkNode.linkedNode.xPos * hSpacing + hSpacing));
                //                            squareLinkLine.setAttribute("y2", Integer.toString(graphLinkNode.linkedNode.yPos * vSpacing + vSpacing));
                //                            squareLinkLine.setAttribute("stroke", "grey");
                //                            squareLinkLine.setAttribute("stroke-width", Integer.toString(strokeWidth));
                Element squareLinkLine = doc.createElementNS(svgNameSpace, "polyline");
                int midY = (fromY + toY) / 2;
                if (toY == fromY) {
                    // make sure that union lines go below the entities and sibling lines go above
                    if (graphLinkNode.relationType == GraphDataNode.RelationType.sibling) {
                        midY = toY - vSpacing / 2;
                    } else if (graphLinkNode.relationType == GraphDataNode.RelationType.union) {
                        midY = toY + vSpacing / 2;
                    }
                }

                squareLinkLine.setAttribute("points",
                        fromX + "," + fromY + " "
                        + fromX + "," + midY + " "
                        + toX + "," + midY + " "
                        + toX + "," + toY);

                squareLinkLine.setAttribute("fill", "none");
                squareLinkLine.setAttribute("stroke", "grey");
                squareLinkLine.setAttribute("stroke-width", Integer.toString(strokeWidth));
                squareLinkLine.setAttribute("id", lineIdString);
                defsNode.appendChild(squareLinkLine);
                break;
        }
        groupNode.appendChild(defsNode);
        Element useNode = doc.createElementNS(svgNameSpace, "use");
        useNode.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + lineIdString); // the xlink: of "xlink:href" is required for some svg viewers to render correctly

        //                    useNode.setAttribute("href", "#" + lineIdString);
        groupNode.appendChild(useNode);

        // add the relation label
        if (graphLinkNode.labelString != null) {
            Element labelText = doc.createElementNS(svgNameSpace, "text");
            labelText.setAttribute("text-anchor", "middle");
            //                        labelText.setAttribute("x", Integer.toString(labelX));
            //                        labelText.setAttribute("y", Integer.toString(labelY));
            labelText.setAttribute("fill", "blue");
            labelText.setAttribute("stroke-width", "0");
            labelText.setAttribute("font-size", "14");
            //                        labelText.setAttribute("transform", "rotate(45)");
            Element textPath = doc.createElementNS(svgNameSpace, "textPath");
            textPath.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", "#" + lineIdString); // the xlink: of "xlink:href" is required for some svg viewers to render correctly
            textPath.setAttribute("startOffset", "50%");
            //                        textPath.setAttribute("text-anchor", "middle");
            Text textNode = doc.createTextNode(graphLinkNode.labelString);
            textPath.appendChild(textNode);
            labelText.appendChild(textPath);
            groupNode.appendChild(labelText);
        }
        relationGroupNode.appendChild(groupNode);
    }

    public void updateRelationLines(SVGDocument doc) {
        Element relationGroup = doc.getElementById("RelationGroup");
        for (Node currentChild = relationGroup.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
            if ("g".equals(currentChild.getLocalName())) {
                Node idAttrubite = currentChild.getAttributes().getNamedItem("id");
                System.out.println("idAttrubite: " + idAttrubite.getNodeValue());
            }
        }
    }
}
