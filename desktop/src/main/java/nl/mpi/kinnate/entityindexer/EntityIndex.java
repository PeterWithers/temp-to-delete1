/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.kinnate.entityindexer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JProgressBar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import nl.mpi.arbil.data.ArbilComponentBuilder;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.kintypestrings.KinType;
import nl.mpi.kinnate.kintypestrings.KinTypeStringConverter;
import nl.mpi.kinnate.kintypestrings.ParserHighlight;
import nl.mpi.kinnate.svg.DataStoreSvg;
import nl.mpi.kinnate.ui.KinTypeStringProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *  Document   : EntityIndex
 *  Created on : Feb 2, 2011, 17:29:36 PM
 *  Author     : Peter Withers
 */
@Deprecated
public class EntityIndex implements EntityService {

//    IndexerParameters indexParameters;
    private HashMap<String, IndexerEntityData> knownEntities;
//    private EntityCollection entityCollection;

    public EntityIndex() {
//        indexParameters = indexParametersLocal;
//        entityCollection = new EntityCollection();
        knownEntities = new HashMap<String, IndexerEntityData>();
    }

    private IndexerEntityData getEntityData(String egoEntityUriString, IndexerParameters indexParameters) throws URISyntaxException {
        URI egoEntityUri = new URI(egoEntityUriString);
        return getEntityData(egoEntityUri, indexParameters);
    }

    private IndexerEntityData getEntityData(URI egoEntityUri, IndexerParameters indexParameters) {
        IndexerEntityData entityData = knownEntities.get(egoEntityUri.toASCIIString());
        if (entityData != null) {
            return entityData;
        } else {
            entityData = new IndexerEntityData(null); // todo: while this could pass the identifier it is unlikely that this class will use them as it relies on the url instead
            knownEntities.put(egoEntityUri.toASCIIString(), entityData);
            try {
                Document linksDom = ArbilComponentBuilder.getDocument(egoEntityUri);
                NodeList relationLinkNodeList = org.apache.xpath.XPathAPI.selectNodeList(linksDom, indexParameters.linkPath);
                for (int nodeCounter = 0; nodeCounter < relationLinkNodeList.getLength(); nodeCounter++) {
                    Node relationLinkNode = relationLinkNodeList.item(nodeCounter);
                    if (relationLinkNode != null) {
                        // resolve the alter URL against its ego URI
                        URI alterUri = egoEntityUri.resolve(relationLinkNode.getTextContent());
                        entityData.addRelation(alterUri.toASCIIString());
                        // get any requested link data
//                        for (ParameterElement relevantDataPath : indexParameters.relevantLinkData.getValues()) {
//                            for (Node linkDataNode = relationLinkNode.getParentNode().getFirstChild(); linkDataNode != null; linkDataNode = linkDataNode.getNextSibling()) {
//                                if (relevantDataPath.getXpathString().equals(linkDataNode.getNodeName())) {
//                                    entityData.addRelationData(alterUri.toASCIIString(), relevantDataPath.getXpathString(), linkDataNode.getTextContent());
//                                }
//                            }
//                        }
                    }
                }
                // get any requested entity data
                // todo: this has been removed due to changes in the data storage and would have to be replaced if this class comes back into use
//                for (String[] relevantDataPath : indexParameters.getRelevantEntityData()) {
//                    NodeList relevantaDataNodeList = org.apache.xpath.XPathAPI.selectNodeList(linksDom, relevantDataPath[0]);
//                    for (int dataCounter = 0; dataCounter < relevantaDataNodeList.getLength(); dataCounter++) {
//                        Node dataNode = relevantaDataNodeList.item(dataCounter);
//                        if (dataNode != null) {
//                            entityData.addEntityData(relevantDataPath[0], dataNode.getTextContent());
//                        }
//                    }
//                }
            } catch (TransformerException exception) {
//                GuiHelper.linorgBugCatcher.logError(exception);
            } catch (ParserConfigurationException exception) {
//                GuiHelper.linorgBugCatcher.logError(exception);
            } catch (DOMException exception) {
//                GuiHelper.linorgBugCatcher.logError(exception);
            } catch (IOException exception) {
//                GuiHelper.linorgBugCatcher.logError(exception);
            } catch (SAXException exception) {
//                GuiHelper.linorgBugCatcher.logError(exception);
            }
            return entityData;
        }
    }

    public void printKnownEntities() {
        for (String currentEgo : knownEntities.keySet()) {
            System.out.println("currentEgo: " + currentEgo);
            IndexerEntityData currentEntityData = knownEntities.get(currentEgo);
            for (String[] currentRecord : currentEntityData.getEntityFields()) {
                System.out.println("-> entityField: " + currentRecord[0] + " : " + currentRecord[1]);
            }
            for (String currentLink : currentEntityData.getRelationPaths()) {
                System.out.println("--> currentLink: " + currentLink);
                for (String[] currentRecord : currentEntityData.getRelationData(currentLink)) {
                    System.out.println("---> linkField: " + currentRecord[0] + " : " + currentRecord[1]);
                }
            }
        }
    }

    public void loadAllEntities(IndexerParameters indexParameters) {
//        String[] treeNodesArray = KinSessionStorage.getSingleInstance().loadStringArray("KinGraphTree");
//        if (treeNodesArray != null) {
//            for (String currentNodeString : treeNodesArray) {
//                try {
//                    URI egoEntityUri = new URI(currentNodeString);
//                    getEntityData(egoEntityUri, indexParameters);
//                } catch (URISyntaxException exception) {
//                    GuiHelper.linorgBugCatcher.logError(exception);
//                }
//            }
//        }
    }

    public void setKinTypeStringTerm(String symbolString, String fieldPath, String fieldValue) {
        // todo: set the terms that combine to form the kin type strings
        // eg: setKinTypeStringTerm("M", "Kinnate.Gedcom.Entity.SEX", "F");
    }

    private EntityData getGraphDataNode(boolean isEgo, URI entityUri, IndexerParameters indexParameters) {
        IndexerEntityData entityData = getEntityData(entityUri, indexParameters);
        ArrayList<String> labelTextList = new ArrayList<String>();
        for (ParameterElement currentLabelField : indexParameters.labelFields.getValues()) {
            String labelTextTemp = entityData.getEntityField(currentLabelField.getXpathString());
            if (labelTextTemp != null) {
                labelTextList.add(labelTextTemp);
            }
        }
        for (ParameterElement currentSymbolField : indexParameters.symbolFieldsFields.getValues()) {
            String linkSymbolString = entityData.getEntityField(currentSymbolField.getXpathString());
            if (linkSymbolString != null) {
                return null; // todo: this is now outdated and needs to be replaced //new EntityData(entityData.getUniqueIdentifier(), entityUri.toASCIIString(), null, currentSymbolField.getSelectedValue(), labelTextList.toArray(new String[]{}), isEgo);
            }
        }
        return null; // todo: this is now outdated and needs to be replaced //new EntityData(entityData.getUniqueIdentifier(), entityUri.toASCIIString(), null, EntityData.SymbolType.none, labelTextList.toArray(new String[]{}), isEgo);
    }

    private void setRelationData(EntityData egoNode, EntityData alterNode, IndexerEntityData egoData, String alterPath, IndexerParameters indexParameters) {
        DataTypes.RelationType egoType = null;
        DataTypes.RelationType alterType = null;
        String[][] alterRelationFields = egoData.getRelationData(alterPath);
        if (alterRelationFields != null) {
//            for (ParameterElement ancestorField : indexParameters.ancestorFields.getValues()) {
//                for (String[] egoRelationField : alterRelationFields) {
//                    if (ancestorField.getXpathString().equals(egoRelationField)) {
//                        egoType = DataTypes.RelationType.ancestor;
//                        alterType = DataTypes.RelationType.descendant;
//                    }
//                }
//            }
//            for (ParameterElement ancestorField : indexParameters.decendantFields.getValues()) {
//                for (String[] egoRelationField : alterRelationFields) {
//                    if (ancestorField.getXpathString().equals(egoRelationField[1])) {
//                        egoType = DataTypes.RelationType.descendant;
//                        alterType = DataTypes.RelationType.ancestor;
//                    }
//                }
//            }
            if (egoType != null && alterType != null) {
                egoNode.addRelatedNode(alterNode, egoType, null, null, null, null);
                alterNode.addRelatedNode(egoNode, alterType, null, null, null, null);
            }
        }
    }

    public EntityData[] getEgoGraphData(URI[] egoNodes, IndexerParameters indexParameters) {
        ArrayList<EntityData> graphDataNodeList = new ArrayList<EntityData>();
        for (URI currentEgoUri : egoNodes) {
            graphDataNodeList.add(getGraphDataNode(true, currentEgoUri, indexParameters));
        }
        return graphDataNodeList.toArray(new EntityData[]{});
    }

    private void getNextRelations(HashMap<String, EntityData> createdGraphNodes, String currentEgoPath, EntityData egoNode, ArrayList<KinType> remainingKinTypes, IndexerParameters indexParameters) throws URISyntaxException {
        IndexerEntityData egoData = getEntityData(currentEgoPath, indexParameters);
//        String currentKinType = remaningKinTypeString.substring(0, 1);
//        remaningKinTypeString = remaningKinTypeString.substring(1);
        KinType currentKinType = remainingKinTypes.remove(0);
//        for (String alterPath : entityCollection.getRelatedNodes(egoNode.getUniqueIdentifier())) {
        for (String alterPath : egoData.getRelationPaths()) {
            try {
                boolean relationAdded = false;
                EntityData alterNode;
                if (createdGraphNodes.containsKey(alterPath)) {
                    alterNode = createdGraphNodes.get(alterPath);
                } else {
                    alterNode = getGraphDataNode(false, new URI(alterPath), indexParameters);
                    createdGraphNodes.put(alterPath, alterNode);
                    relationAdded = true;
                }
                IndexerEntityData alterData = getEntityData(currentEgoPath, indexParameters);
                setRelationData(egoNode, alterNode, egoData, alterPath, indexParameters);
                setRelationData(alterNode, egoNode, alterData, currentEgoPath, indexParameters);
                // todo: either prevent links being added if a node does not match the kin type or remove them when known
//                if (egoNode.relationMatchesType(alterPath, currentKinType)) {
                // only traverse if the type matches
                if (remainingKinTypes.size() > 0) {
                    getNextRelations(createdGraphNodes, alterPath, alterNode, remainingKinTypes, indexParameters);
                }
//                } else if (relationAdded) {
//                    createdGraphNodes.remove(alterPath);
//                }
            } catch (URISyntaxException urise) {
//                GuiHelper.linorgBugCatcher.logError(urise);
            }
        }
    }

    public void clearAbortRequest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void requestAbortProcess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EntityData[] processKinTypeStrings(ArrayList<KinTypeStringProvider> kinTypeStringProviders, IndexerParameters indexParameters, DataStoreSvg dataStoreSvg, JProgressBar progressBar) throws EntityServiceException, ProcessAbortException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EntityData[] processKinTypeStrings(URI[] egoNodes, String[] kinTypeStrings, ParserHighlight[] parserHighlight, IndexerParameters indexParameters, DataStoreSvg dataStoreSvg, JProgressBar progressBar) throws EntityServiceException, ProcessAbortException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EntityData[] getRelationsOfEgo(URI[] egoNodes, String[] uniqueIdentifiers, String[] kinTypeStrings, ParserHighlight[] parserHighlight, IndexerParameters indexParameters) throws EntityServiceException {
        KinTypeStringConverter kinTypeStringConverter = new KinTypeStringConverter(new DataStoreSvg());
        HashMap<String, EntityData> createdGraphNodes = new HashMap<String, EntityData>();
        for (URI currentEgoUri : egoNodes) {
            EntityData egoNode;
            if (createdGraphNodes.containsKey(currentEgoUri.toASCIIString())) {
                egoNode = createdGraphNodes.get(currentEgoUri.toASCIIString());
            } else {
                egoNode = getGraphDataNode(true, currentEgoUri, indexParameters);
                createdGraphNodes.put(currentEgoUri.toASCIIString(), egoNode);
            }
            if (kinTypeStrings != null) {
                for (String currentKinString : kinTypeStrings) {
                    ArrayList<KinType> kinTypes = kinTypeStringConverter.getKinTypes(currentKinString);
                    try {
                        getNextRelations(createdGraphNodes, currentEgoUri.toASCIIString(), egoNode, kinTypes, indexParameters);
                    } catch (URISyntaxException exception) {
                        throw new EntityServiceException(exception.getMessage());
                    }
                }
            }
        }
        return createdGraphNodes.values().toArray(new EntityData[]{});
    }

    public static void main(String[] args) {
//        String[] entityStringArray = KinSessionStorage.getSingleInstance().loadStringArray("KinGraphTree");
//        URI[] entityUriArray = new URI[entityStringArray.length];
        int uriCounter = 0;
//        for (String currentEntityString : entityStringArray) {
//            try {
//                entityUriArray[uriCounter] = new URI(currentEntityString);
//            } catch (URISyntaxException urise) {
//                GuiHelper.linorgBugCatcher.logError(urise);
//            }
//            uriCounter++;
//        }
        EntityIndex testEntityIndex = new EntityIndex();
        testEntityIndex.loadAllEntities(new IndexerParameters());
        testEntityIndex.printKnownEntities();
    }
}
