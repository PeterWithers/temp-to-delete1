package nl.mpi.kinnate.svg;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nl.mpi.arbil.util.ArbilBugCatcher;
import nl.mpi.kinnate.entityindexer.IndexerParameters;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.GraphSorter;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;
import nl.mpi.kinnate.kintypestrings.KinTermGroup;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

/**
 *  Document   : DataStoreSvg
 *  Created on : Mar 10, 2011, 8:37:26 AM
 *  Author     : Peter Withers
 */
@XmlRootElement(name = "KinDiagramData", namespace = "http://mpi.nl/tla/kin")
public class DataStoreSvg {

    static protected String kinDataNameSpace = "kin";
    static protected String kinDataNameSpaceLocation = "http://mpi.nl/tla/kin";
//    @XmlElement(name = "EgoIdList", namespace = "http://mpi.nl/tla/kin")
//    @XmlElementWrapper(name = "kin:EgoIdList")
    @XmlElement(name = "EgoId", namespace = "http://mpi.nl/tla/kin")
    public HashSet<UniqueIdentifier> egoEntities = new HashSet<UniqueIdentifier>();
    public HashSet<UniqueIdentifier> requiredEntities = new HashSet<UniqueIdentifier>();
//        @XmlElementWrapper(name = "kin:KinTypeStrings")
    @XmlElement(name = "KinTypeString", namespace = "http://mpi.nl/tla/kin")
    protected String[] kinTypeStrings = new String[]{};
    @XmlElement(name = "IndexParameters", namespace = "http://mpi.nl/tla/kin")
    protected IndexerParameters indexParameters;
    @XmlElement(name = "KinTermGroup", namespace = "http://mpi.nl/tla/kin")
    protected KinTermGroup[] kinTermGroups;
    @XmlElement(name = "ShowLabels", namespace = "http://mpi.nl/tla/kin")
    public boolean showLabels = true;
    @XmlElement(name = "ShowKinTypeLabels", namespace = "http://mpi.nl/tla/kin")
    public boolean showKinTypeLabels = false;
    @XmlElement(name = "ShowKinTermLabels", namespace = "http://mpi.nl/tla/kin")
    public boolean showKinTermLabels = false;
    @XmlElement(name = "ShowKinTermLines", namespace = "http://mpi.nl/tla/kin")
    public boolean showKinTermLines = true;
    @XmlElement(name = "SnapToGrid", namespace = "http://mpi.nl/tla/kin")
    public boolean snapToGrid = true;
    @XmlElement(name = "ShowSanguineLines", namespace = "http://mpi.nl/tla/kin")
    public boolean showSanguineLines = true;
    @XmlElement(name = "ShowArchiveLinks", namespace = "http://mpi.nl/tla/kin")
    public boolean showArchiveLinks = true;
//    @XmlElement(name = "ShowResourceLinks", namespace = "http://mpi.nl/tla/kin")
//    public boolean showResourceLinks = true;
    @XmlElement(name = "HighlightRelationLines", namespace = "http://mpi.nl/tla/kin")
    public boolean highlightRelationLines = true;
    @XmlElement(name = "ShowDiagramBorder", namespace = "http://mpi.nl/tla/kin")
    public boolean showDiagramBorder = true;
    @XmlElement(name = "EntityData", namespace = "http://mpi.nl/tla/kin")
    protected GraphSorter graphData;

    public enum PanelType {

        KinTypeStrings,
        KinTerms,
        ArchiveLinker,
        MetaData,
        IndexerSettings,
        DiagramTree,
        EntitySearch
    }
    private EnumMap<PanelType, Integer> visiblePanels;

    public class GraphRelationData {

        public UniqueIdentifier egoNodeId;
        public UniqueIdentifier alterNodeId;
        public DataTypes.RelationType relationType;
        public DataTypes.RelationLineType relationLineType;
    }

    public DataStoreSvg() {
    }

    public void setDefaults() {
        // todo: it might be better not to add any kin group until the user explicitly adds one from the menu
        kinTermGroups = new KinTermGroup[]{new KinTermGroup(-1)}; //new KinTermGroup(0), new KinTermGroup(1)};
        indexParameters = new IndexerParameters();
    }

    public Set<Entry<PanelType, Integer>> getVisiblePanels() {
        return visiblePanels.entrySet();
    }

    public void setPanelState(PanelType panelType, int panelWidth) {
        if (visiblePanels == null) {
            visiblePanels = new EnumMap<PanelType, Integer>(PanelType.class);
        }
        visiblePanels.put(panelType, panelWidth);
    }

    public GraphRelationData getEntitiesForRelations(Node relationGroup) {
        for (Node currentChild = relationGroup.getFirstChild(); currentChild != null; currentChild = currentChild.getNextSibling()) {
            if ("RelationEntities".equals(currentChild.getLocalName())) {
                GraphRelationData graphRelationData = new GraphRelationData();
                graphRelationData.egoNodeId = new UniqueIdentifier(currentChild.getAttributes().getNamedItemNS(kinDataNameSpace, "ego").getNodeValue());
                graphRelationData.alterNodeId = new UniqueIdentifier(currentChild.getAttributes().getNamedItemNS(kinDataNameSpace, "alter").getNodeValue());
                graphRelationData.relationType = DataTypes.RelationType.valueOf(currentChild.getAttributes().getNamedItemNS(kinDataNameSpace, "relationType").getNodeValue());
                graphRelationData.relationLineType = DataTypes.RelationLineType.valueOf(currentChild.getAttributes().getNamedItemNS(kinDataNameSpace, "lineType").getNodeValue());
                return graphRelationData;
            }
        }
        return null;
    }

    public void storeRelationParameters(SVGDocument doc, Element relationGroup, DataTypes.RelationType relationType, DataTypes.RelationLineType relationLineType, UniqueIdentifier egoEntity, UniqueIdentifier alterEntity) {
        Element dataRecordNode = doc.createElementNS(kinDataNameSpace, "kin:RelationEntities");
        dataRecordNode.setAttributeNS(kinDataNameSpace, "lineType", relationLineType.name());
        dataRecordNode.setAttributeNS(kinDataNameSpace, "relationType", relationType.name());
        dataRecordNode.setAttributeNS(kinDataNameSpace, "ego", egoEntity.getAttributeIdentifier());
        dataRecordNode.setAttributeNS(kinDataNameSpace, "alter", alterEntity.getAttributeIdentifier());
        relationGroup.appendChild(dataRecordNode);
    }

//    private void storeParameter(SVGDocument doc, Element dataStoreElement, String parameterName, String[] ParameterValues) {
//        for (String currentKinType : ParameterValues) {
//            Element dataRecordNode = doc.createElementNS(kinDataNameSpace, "kin:" + parameterName);
//            //            Element dataRecordNode = doc.createElement(kinDataNameSpace + ":" + parameterName);
//            dataRecordNode.setAttributeNS(kinDataNameSpace, "value", currentKinType);
//            dataStoreElement.appendChild(dataRecordNode);
//        }
//    }
//    private void storeParameter(SVGDocument doc, Element dataStoreElement, String parameterName, String[][] ParameterValues) {
//        for (String[] currentKinType : ParameterValues) {
//            Element dataRecordNode = doc.createElementNS(kinDataNameSpace, "kin:" + parameterName);
//            //            Element dataRecordNode = doc.createElement(kinDataNameSpace + ":" + parameterName);
//            if (currentKinType.length == 1) {
//                dataRecordNode.setAttributeNS(kinDataNameSpace, "value", currentKinType[0]);
//            } else if (currentKinType.length == 2) {
//                dataRecordNode.setAttributeNS(kinDataNameSpace, "path", currentKinType[0]);
//                dataRecordNode.setAttributeNS(kinDataNameSpace, "value", currentKinType[1]);
//            } else {
//                // todo: add any other datatypes if required
//                throw new UnsupportedOperationException();
//            }
//            dataStoreElement.appendChild(dataRecordNode);
//        }
//    }
    protected void storeAllData(SVGDocument doc) {
        // create string array to store the selected ego nodes in the dom
//        ArrayList<String> egoStringArray = new ArrayList<String>();
//        for (URI currentEgoUri : egoPathSet) {
//            egoStringArray.add(currentEgoUri.toASCIIString());
//        }
        // store the selected kin type strings and other data in the dom
        //        Namespace sNS = Namespace.getNamespace("someNS", "someNamespace");
        //        Element element = new Element("SomeElement", sNS);
//        Element kinTypesRecordNode = doc.createElementNS(kinDataNameSpaceLocation, "kin:KinDiagramData");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataStoreSvg.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(this, doc.getRootElement());
        } catch (JAXBException exception) {
            new ArbilBugCatcher().logError(exception);
//        } catch (BaseXException exception) {
//            new ArbilBugCatcher().logError(exception);
        }


//        Element kinTypesRecordNode = doc.createElement(kinDataNameSpace + ":KinDiagramData");
//        kinTypesRecordNode.setAttribute("xmlns:" + kinDataNameSpace, kinDataNameSpaceLocation); // todo: this surely is not the only nor the best way to st the namespace
//        storeParameter(doc, kinTypesRecordNode, "EgoPathList", egoStringArray.toArray(new String[]{}));
//        storeParameter(doc, kinTypesRecordNode, "EgoIdList", egoIdentifierSet.toArray(new String[]{}));
//        storeParameter(doc, kinTypesRecordNode, "KinTypeStrings", kinTypeStrings);
//        storeParameter(doc, kinTypesRecordNode, "AncestorFields", indexParameters.ancestorFields.getValues());
//        storeParameter(doc, kinTypesRecordNode, "DecendantFields", indexParameters.decendantFields.getValues());
//        storeParameter(doc, kinTypesRecordNode, "LabelFields", indexParameters.labelFields.getValues());
//        storeParameter(doc, kinTypesRecordNode, "SymbolFieldsFields", indexParameters.symbolFieldsFields.getValues());
//        doc.getRootElement().appendChild(kinTypesRecordNode);
        // end store the selected kin type strings and other data in the dom
    }

//    private String[] getSingleParametersFromDom(SVGDocument doc, String parameterName) {
//        ArrayList<String> parameterList = new ArrayList<String>();
//        if (doc != null) {
//            //            printNodeNames(doc);
//            try {
//                // todo: resolve names space issue
//                // todo: try setting the XPath namespaces
//                NodeList parameterNodeList = org.apache.xpath.XPathAPI.selectNodeList(doc, "/svg:svg/kin:KinDiagramData/kin:" + parameterName);
//                for (int nodeCounter = 0; nodeCounter < parameterNodeList.getLength(); nodeCounter++) {
//                    Node parameterNode = parameterNodeList.item(nodeCounter);
//                    if (parameterNode != null) {
//                        parameterList.add(parameterNode.getAttributes().getNamedItem("value").getNodeValue());
//                    }
//                }
//            } catch (TransformerException transformerException) {
//                new ArbilBugCatcher().logError(transformerException);
//            }
// done: populate the avaiable symbols indexParameters.symbolFieldsFields.setAvailableValues(new String[]{"circle", "triangle", "square", "union"});
//        }
//        return parameterList.toArray(new String[]{});
//    }
//    private String[][] getDoubleParametersFromDom(SVGDocument doc, String parameterName) {
//        ArrayList<String[]> parameterList = new ArrayList<String[]>();
//        if (doc != null) {
//            try {
//                // todo: resolve names space issue
//                NodeList parameterNodeList = org.apache.xpath.XPathAPI.selectNodeList(doc, "/svg/KinDiagramData/" + parameterName);
//                for (int nodeCounter = 0; nodeCounter < parameterNodeList.getLength(); nodeCounter++) {
//                    Node parameterNode = parameterNodeList.item(nodeCounter);
//                    if (parameterNode != null) {
//                        parameterList.add(new String[]{parameterNode.getAttributes().getNamedItem("path").getNodeValue(), parameterNode.getAttributes().getNamedItem("value").getNodeValue()});
//                    }
//                }
//            } catch (TransformerException transformerException) {
//                new ArbilBugCatcher().logError(transformerException);
//            }
//            //            // todo: populate the avaiable symbols indexParameters.symbolFieldsFields.setAvailableValues(new String[]{"circle", "triangle", "square", "union"});
//        }
//        return parameterList.toArray(new String[][]{});
//    }
    static protected DataStoreSvg loadDataFromSvg(SVGDocument doc) {
        DataStoreSvg dataStoreSvg = new DataStoreSvg();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataStoreSvg.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            NodeList dataStoreNodeList = doc.getElementsByTagNameNS("http://mpi.nl/tla/kin", "KinDiagramData");
            if (dataStoreNodeList.getLength() > 0) {
                dataStoreSvg = (DataStoreSvg) unmarshaller.unmarshal(dataStoreNodeList.item(0), DataStoreSvg.class).getValue();
            }
        } catch (JAXBException exception) {
            new ArbilBugCatcher().logError(exception);
        }
        return dataStoreSvg;
    }
}
