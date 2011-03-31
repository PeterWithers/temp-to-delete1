package nl.mpi.kinnate.kindata;

import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Document   : GraphDataNode
 *  Created on : Sep 11, 2010, 4:30:41 PM
 *  Author     : Peter Withers
 */
@XmlRootElement(name = "Entity")
public class EntityData {

    public enum SymbolType {
        // symbol terms are used here to try to keep things agnostic

        square, triangle, circle, union, resource, ego, none
    }
    @XmlElement(name = "Identifier")
    private String uniqueIdentifier;
    @XmlElement(name = "Path")
    private String entityPath;
    private SymbolType symbolType;
    @XmlElement(name = "Symbol")
    private String symbolTypeString;
    public boolean isEgo = false;
    @XmlElementWrapper(name = "Labels")
    @XmlElement(name = "String")
    private String[] labelString;
    @XmlElementWrapper(name = "Relations")
    @XmlElement(name = "Relation")
    private EntityRelation[] relatedNodes;
    protected int xPos;
    protected int yPos;
    public boolean isVisible = false;
    private EntityRelation[] visiblyRelateNodes = null;
    private EntityRelation[] distinctRelateNodes = null;

//    // todo: move this into the graphdatanode
//    @XmlRootElement(name = "results")
//    static public class RelationResults {
//
//        @XmlElementWrapper(name = "relations")
//        @XmlElement(name = "entity")
//        RelationData[] relationArray;
//    }
//
//    static public class RelationData {
//
//        @XmlElement
//        GraphDataNode.RelationType type;
//        @XmlElement
//        String path;
//        String identifier;
//    }
    private EntityData() {
    }

    public EntityData(String uniqueIdentifierLocal, String entityPathLocal, String symbolTypeLocal, String[] labelStringLocal, boolean isEgoLocal) {
        uniqueIdentifier = uniqueIdentifierLocal;
        entityPath = entityPathLocal;
        symbolType = null;
        symbolTypeString = symbolTypeLocal;
        labelString = labelStringLocal;
        isEgo = isEgoLocal;
    }

    public EntityData(String uniqueIdentifierLocal, String entityPathLocal, SymbolType symbolIndex, String[] labelStringLocal, boolean isEgoLocal) {
        uniqueIdentifier = uniqueIdentifierLocal;
        entityPath = entityPathLocal;
        symbolType = symbolIndex;
        labelString = labelStringLocal;
        isEgo = isEgoLocal;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

//    public void setyPos(int yPos) {
//        // todo: y position cannot be set in the default layout of vertical generations
//        // this.yPos = yPos;
//    }
    public String getSymbolType() {
        if (symbolType != null) {
            switch (symbolType) {
                case circle:
                    return "circle";
                case ego:
                    return "square";
                case none:
                    return null;
                case resource:
                    return "resource";
                case square:
                    return "square";
                case triangle:
                    return "triangle";
                case union:
                    return "union";
            }
        }
        return symbolTypeString;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public String[] getLabel() {
        return labelString;
    }
    ArrayList<String> unhandledLinkTypesArray = new ArrayList<String>();

//    protected void calculateLinks(HashMap<String, GraphDataNode> graphDataNodeList) {
//        if (this.imdiTreeObject != null) {
//            this.imdiTreeObject.waitTillLoaded();
//
//
//            for (ImdiTreeObject childNode : this.imdiTreeObject.getAllChildren()) {
//                ImdiField[] currentField = childNode.getFields().get("Link");
//
//
//                if (currentField != null && currentField.length > 0) {
//                    GraphDataNode.RelationType relationType = GraphDataNode.RelationType.sibling;
//                    ImdiField[] relationTypeField = childNode.getFields().get("Type"); //todo: this RELA field might not be the best nor the only one to gather relation types from
//
//
//                    if (relationTypeField != null && relationTypeField.length > 0) {
//                        String typeString = relationTypeField[0].getFieldValue();
//                        System.out.println("link type field: " + relationTypeField[0].getFieldValue());
//                        List<String> ancestorTerms = Arrays.asList(new String[]{"SUBN", "_HME", "WIFE", "CHIL", "HUSB", "REPO", "OBJE", "NOTE", "FAMC", "FAMS", "SOUR", "ASSO", "SUBM", "ANCI", "DESI", "ALIA"});
//
//
//                        if (("Kinnate.Gedcom.Entity." + ancestorTerms).contains(typeString)) {
//                            relationType = GraphDataNode.RelationType.ancestor;
//
//
//                        } else {
//                            unhandledLinkTypesArray.add(typeString);
//
//
//                        }
//
////                        if ("Father".equals(typeString)) {
////                            relationType = GraphDataNode.RelationType.ancestor;
////                        } else if ("Mother".equals(typeString)) {
////                            relationType = GraphDataNode.RelationType.ancestor;
////                        }
//                    }
//                    System.out.println("link field: " + currentField[0].getFieldValue());
////                    linkArray.add(currentField[0].getFieldValue());
//                    GraphDataNode linkedNode = graphDataNodeList.get(currentField[0].getFieldValue());
//
//
//                    if (linkedNode != null) {
//                        this.addRelatedNode(linkedNode, 0, relationType);
//
//
//                    }
//                }
//            }
//        }
//        if (unhandledLinkTypesArray.size() > 0) {
//            System.err.println("unhandledLinkTypes: " + unhandledLinkTypesArray.toString());
//
//
//        }
//    }
//    public GraphDataNode[] getLinks() {
//        if (imdiTreeObject == null) {
//            return linkStringsArray;
//        } else {
//            ArrayList<String> linkArray = new ArrayList<String>();
//            imdiTreeObject.waitTillLoaded();
//            for (ImdiTreeObject childNode : imdiTreeObject.getAllChildren()) {
////            System.out.println("getAllChildren: " + childNode.getUrlString());
//                ImdiField[] currentField = childNode.getFields().get("Link");
//                if (currentField != null && currentField.length > 0) {
//                    System.out.println("link field: " + currentField[0].getFieldValue());
//                    linkArray.add(currentField[0].getFieldValue());
//                }
//            }
//            return linkArray.toArray(new String[]{});
//        }
//    }
    public void addRelatedNode(EntityData alterNodeLocal, int generationalDistance, DataTypes.RelationType relationType, DataTypes.RelationLineType relationLineType, String labelString) {
        // note that the test gedcom file has multiple links for a given pair so in might be necessary to filter incoming links on a preferential basis
        EntityRelation nodeRelation = new EntityRelation();
        nodeRelation.alterNode = alterNodeLocal;
        nodeRelation.generationalDistance = generationalDistance;
        nodeRelation.relationType = relationType;
        nodeRelation.relationLineType = relationLineType;
        nodeRelation.labelString = labelString;
        if (relatedNodes != null) {
            ArrayList<EntityRelation> relatedNodesList = new ArrayList<EntityRelation>();
            relatedNodesList.addAll(Arrays.asList(relatedNodes));
            relatedNodesList.add(nodeRelation);
            relatedNodes = relatedNodesList.toArray(new EntityRelation[]{});
        } else {
            relatedNodes = new EntityRelation[]{nodeRelation};
        }
    }

    public void clearVisibility() {
        isVisible = false;
        isEgo = false;
        visiblyRelateNodes = null;
        distinctRelateNodes = null;
    }

    public EntityRelation[] getVisiblyRelateNodes() {
        if (visiblyRelateNodes == null) {
            ArrayList<EntityRelation> visiblyRelatedNodes = new ArrayList<EntityRelation>();
            for (EntityRelation nodeRelation : getDistinctRelateNodes()) {
                if (nodeRelation.alterNode != null) {
                    if (nodeRelation.alterNode.isVisible) {
                        visiblyRelatedNodes.add(nodeRelation);
                    }
                }
            }
            visiblyRelateNodes = visiblyRelatedNodes.toArray(new EntityRelation[]{});
        }
        return visiblyRelateNodes;
    }

    public EntityRelation[] getDistinctRelateNodes() {
        if (distinctRelateNodes == null) {
            ArrayList<String> processedIds = new ArrayList<String>();
            ArrayList<EntityRelation> uniqueNodes = new ArrayList<EntityRelation>();
            for (EntityRelation nodeRelation : relatedNodes) {
                if (!processedIds.contains(nodeRelation.alterUniqueIdentifier)) {
                    uniqueNodes.add(nodeRelation);
                    processedIds.add(nodeRelation.alterUniqueIdentifier);
                }
            }
            distinctRelateNodes = uniqueNodes.toArray(new EntityRelation[]{});
        }
        return distinctRelateNodes;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }
}
