package nl.mpi.kinnate.gedcomimport;

import nl.mpi.kinnate.kindocument.EntityDocument;
import nl.mpi.kinnate.kindocument.ImportTranslator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.util.ArbilBugCatcher;
import nl.mpi.kinnate.kindata.DataTypes.RelationLineType;
import nl.mpi.kinnate.kindata.DataTypes.RelationType;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;

/**
 *  Document   : GedcomImporter
 *  Created on : Aug 24, 2010, 2:40:21 PM
 *  Author     : Peter Withers
 */
public class GedcomImporter extends EntityImporter implements GenericImporter {

    public GedcomImporter(JProgressBar progressBarLocal, JTextArea importTextAreaLocal, boolean overwriteExistingLocal) {
        super(progressBarLocal, importTextAreaLocal, overwriteExistingLocal);
    }

    @Override
    public boolean canImport(String inputFileString) {
        return (inputFileString.toLowerCase().endsWith(".ged"));
    }

    class SocialMemberElement {

        public SocialMemberElement(String typeString, EntityData memberEntity) {
            this.typeString = typeString;
            this.memberEntity = memberEntity;
        }
        String typeString;
        EntityData memberEntity;
    }

    @Override
    public URI[] importFile(InputStreamReader inputStreamReader) {
        ArrayList<URI> createdNodes = new ArrayList<URI>();
        HashMap<UniqueIdentifier, ArrayList<SocialMemberElement>> socialGroupRoleMap = new HashMap<UniqueIdentifier, ArrayList<SocialMemberElement>>(); // GroupID: @XX@, RoleType: WIFE HUSB CHIL, EntityData
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        ImportTranslator importTranslator = new ImportTranslator(true);
        // todo: add the translator values if required

        try {
            String strLine;
            int gedcomLevel = 0;
            ArrayList<String> gedcomLevelStrings = new ArrayList<String>();
            EntityDocument currentEntity = null;
            boolean skipFileEntity = false;
            while ((strLine = bufferedReader.readLine()) != null) {
                if (skipFileEntity) {
                    skipFileEntity = false;
                    while ((strLine = bufferedReader.readLine()) != null) {
                        if (strLine.startsWith("0")) {
                            break;
                        }
                    }
                }
                String[] lineParts = strLine.split(" ", 3);
                gedcomLevel = Integer.parseInt(lineParts[0]);
                while (gedcomLevelStrings.size() > gedcomLevel) {
                    gedcomLevelStrings.remove(gedcomLevelStrings.size() - 1);
                }
                gedcomLevelStrings.add(lineParts[1]);
                System.out.println(strLine);
//                System.out.println("gedcomLevelString: " + gedcomLevelStrings);
//                appendToTaskOutput(importTextArea, strLine);
                boolean lastFieldContinued = false;
                if (lineParts[1].equals("CONT")) {
                    // todo: if the previous field is null this should be caught and handled as an error in the source file
                    String lineContents = "";
                    if (lineParts.length > 2) {
                        lineContents = lineParts[2];
                    }
                    currentEntity.appendValueToLast("\n" + lineContents);
                    lastFieldContinued = true;
                } else if (lineParts[1].equals("CONC")) {
                    // todo: if the previous field is null this should be caught and handled as an error in the source file
                    String lineContents = "";
                    if (lineParts.length > 2) {
                        lineContents = lineParts[2];
                    }
                    currentEntity.appendValueToLast(lineContents);
                    lastFieldContinued = true;
                }
                if (lastFieldContinued == false) {
                    if (gedcomLevel == 0) {
                        if (lineParts[1].equals("TRLR")) {
                            appendToTaskOutput("End of file found");
                        } else {
//                        String gedcomXsdLocation = "/xsd/gedcom-import.xsd";
//                            String gedcomXsdLocation = "/xsd/gedcom-autogenerated.xsd";
                            String typeString;
                            if (lineParts.length > 2) {
                                typeString = lineParts[2];
                            } else {
                                typeString = lineParts[1];
                            }
                            // todo: the type string needs to determine if this is an entity or a metadata file
                            currentEntity = getEntityDocument(createdNodes, typeString, lineParts[1], importTranslator);
                            if (lineParts[1].equals("HEAD")) {
                                // because the schema specifies 1:1 of both head and entity we find rather than create the head and entity nodes
                                appendToTaskOutput("Reading Gedcom Header");
                                currentEntity.appendValue(lineParts[1], null, gedcomLevel);
                            } else {
                                // because the schema specifies 1:1 of both head and entity we find rather than create the head and entity nodes                                
                                if (lineParts.length > 2) {
                                    currentEntity.appendValue(lineParts[2], lineParts[1], gedcomLevel);
//                                    appendToTaskOutput(lineParts[2]);
//                                    currentEntity.insertValue("gedcom-type", lineParts[2]);
//                                    if (lineParts[2].equals("NOTE")) {
//                                        currentEntity.insertValue("NoteText", lineParts[2]);
//                                        Element addedNoteElement = metadataDom.createElement("NoteText");
//                                        currentDomNode.appendChild(addedNoteElement);
//                                        previousField = addedNoteElement;
//                                    }
//                                }
                                } else {
                                    currentEntity.appendValue("gedcom-id", lineParts[1], gedcomLevel);
                                }
//                                System.out.println("currentDomElement: " + currentDomNode + " value: " + currentDomNode.getTextContent());
                            }
                        } // end skip overwrite
                    } else {
//                        if (lineParts.length > 2) {
                        // todo: move this into an array to be processed after all the fields have been insterted


////                            gedcomImdiObject.saveChangesToCache(true);
//                                try {
//                                    URI linkUri = metadataBuilder.addChildNode(gedcomImdiObject, ".Gedcom.Relation", null, null, null);
//                                    ImdiTreeObject linkImdiObject = ImdiLoader.getSingleInstance().getImdiObject(null, linkUri);
//                                    appendToTaskOutput(importTextArea, "--> gedcomImdiObject.getChildCount: " + gedcomImdiObject.getChildCount());
//                                    gedcomImdiObject.loadImdiDom();
//                                    gedcomImdiObject.clearChildIcons();
//                                    gedcomImdiObject.clearIcon();
////                            gedcomImdiObject.waitTillLoaded();
//                                    appendToTaskOutput(importTextArea, "--> link url: " + linkImdiObject.getUrlString());
////                            appendToTaskOutput(importTextArea, "--> InternalNameT2" + lineParts[2] + " : " + linkImdiObject.getUrlString());
////                            createdNodesTable.put(lineParts[2], linkImdiObject.getUrlString());
////                            createdNodes.add(linkImdiObject.getUrlString());
////                            System.out.println("keys: " + linkImdiObject.getFields().keys().nextElement());
//                                    ImdiField[] currentField = linkImdiObject.getFields().get("Link");
//                                    if (currentField != null && currentField.length > 0) {
//                                        appendToTaskOutput(importTextArea, "--> Link" + lineParts[2]);
//                                        // the target of this link might not be read in at this point so lets store the fields for updateing later
//                                        //createdNodesTable.get(lineParts[2])
//                                        currentField[0].setFieldValue(lineParts[2], false, true);
//                                        linkNodes.add(linkImdiObject);
////                                appendToTaskOutput(importTextArea, "--> link count: " + linkFields.size());
//                                    }
//                                    ImdiField[] currentField1 = linkImdiObject.getFields().get("Type");
//                                    if (currentField1 != null && currentField1.length > 0) {
//                                        appendToTaskOutput(importTextArea, "--> Type" + lineParts[1]);
//                                        currentField1[0].setFieldValue(lineParts[1], false, true);
//                                    }
//                                    ImdiField[] currentField2 = linkImdiObject.getFields().get("TargetName");
//                                    if (currentField2 != null && currentField2.length > 0) {
//                                        appendToTaskOutput(importTextArea, "--> TargetName" + lineParts[2]);
//                                        currentField2[0].setFieldValue(lineParts[2], false, true);
//                                    }
//                                } catch (ArbilMetadataException arbilMetadataException) {
//                                    System.err.println(arbilMetadataException.getMessage());
//                                }
//                            }
//                        }
//                        }
                        // trim the nodes to the current gedcom level
//                        int parentNodeCount = 0;
//                        for (Node countingDomNode = currentDomNode; countingDomNode != null; countingDomNode = countingDomNode.getParentNode()) {
//                            parentNodeCount++;
//                        }
//                        for (int nodeCount = parentNodeCount; nodeCount > gedcomLevel + 3; nodeCount--) {
//                            System.out.println("gedcomLevel: " + gedcomLevel + " parentNodeCount: " + parentNodeCount + " nodeCount: " + nodeCount + " exiting from node: " + currentDomNode);
//                            currentDomNode = currentDomNode.getParentNode();
//                        }
//                        if (lineParts[1].equals("NAME") && currentDomNode.getNodeName().equals("Entity")) {
//                            // find the existing node if only one should exist
//                            System.out.println("Found Name Node easching: " + currentDomNode.getNodeName());
//                            for (Node childNode = currentDomNode.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
//                                System.out.println(childNode.getNodeName());
//                                if (childNode.getNodeName().equals("NAME")) {
//                                    System.out.println("Using found node");
//                                    currentDomNode = childNode;
//                                    break;
//                                }
//                            }
//                            appendToTaskOutput("Name: " + lineParts[2]);
//                        } else {
//                            System.out.println("Creating Node: " + lineParts[1]);
//                            // otherwise add the current gedcom node
//                            currentEntity.appendNode(lineParts[1]);
////                            Element addedElement = metadataDom.createElement(lineParts[1]);
////                            currentDomNode.appendChild(addedElement);
////                            currentDomNode = addedElement;
//                        }
                        // if the current line has a value then enter it into the node
                        if (lineParts.length == 2) {
                            currentEntity.appendValue(lineParts[1], null, gedcomLevel);
                        } else if (lineParts.length > 2) {
//                        if (lineParts[1].equals("NAME")) {
//                            ImdiField[] currentField = gedcomImdiObject.getFields().get("Gedcom.Name");
//                            if (currentField != null && currentField.length > 0) {
//                                currentField[0].setFieldValue(lineParts[2], false, true);
//                                previousField = currentField;
//                            } else {
//                                System.err.println("missing field for: " + lineParts[1]);
//                                previousField = null;
//                            }
//                        } else {
//                            String gedcomPath = "Kinnate.Gedcom";
//                            int loopLevelCount = 0;
//                            int nodeLevelCount = 0;
//                            Node nodeLevelCountNode = currentDomNode;
//                            while (nodeLevelCountNode != null) {
//                                nodeLevelCountNode = nodeLevelCountNode.getParentNode();
//                                nodeLevelCount++;
//                            }
//                            for (String levelString : gedcomLevelStrings) {
//                                if (levelString.startsWith("@")) {
                            // this could be handled better
                            // this occurs at level 0 where the element type is named eg "0 @I9@ INDI"
//                                    levelString = "Entity";
//                                }
//                                gedcomPath = gedcomPath + "." + levelString;
//                                loopLevelCount++;
//                                if (loopLevelCount > nodeLevelCount) {
//                                    Element addedElement = metadataDom.createElement(levelString);
//                                    currentDomNode.appendChild(addedElement);
//                                    currentDomNode = addedElement;
//                                }
//                            }
//                            List<String> swapList = Arrays.asList(new String[]{
//                                        "Kinnate.Gedcom.HEAD.SOUR",
//                                        "Kinnate.Gedcom.HEAD.CORP",
//                                        "Kinnate.Gedcom.HEAD.CORP.ADDR",
//                                        "Kinnate.Gedcom.HEAD.SOUR.DATA",
//                                        "Kinnate.Gedcom.HEAD.CHAN.DATE",
//                                        "Kinnate.Gedcom.HEAD.DATE",
//                                        "Kinnate.Gedcom.HEAD.CHAR",
//                                        "Kinnate.Gedcom.Entity.NAME",
//                                        "Kinnate.Gedcom.Entity.REFN",
//                                        "Kinnate.Gedcom.Entity.REPO",
//                                        "Kinnate.Gedcom.Entity.DATA",
//                                        "Kinnate.Gedcom.Entity.ENGA",
//                                        "Kinnate.Gedcom.Entity.ENGA.SOUR",
//                                        "Kinnate.Gedcom.Entity.MARB",
//                                        "Kinnate.Gedcom.Entity.MARB.SOUR",
//                                        "Kinnate.Gedcom.Entity.MARC",
//                                        "Kinnate.Gedcom.Entity.MARC.SOUR",
//                                        "Kinnate.Gedcom.Entity.MARL",
//                                        "Kinnate.Gedcom.Entity.MARL.SOUR",
//                                        "Kinnate.Gedcom.Entity.MARS",
//                                        "Kinnate.Gedcom.Entity.MARS.SOUR",
//                                        "Kinnate.Gedcom.Entity.DIV",
//                                        "Kinnate.Gedcom.Entity.DIV.SOUR",
//                                        "Kinnate.Gedcom.Entity.DIVF",
//                                        "Kinnate.Gedcom.Entity.DIVF.SOUR",
//                                        "Kinnate.Gedcom.Entity.DATA.EVEN",
//                                        "Kinnate.Gedcom.Entity.REPO.CALN",
//                                        "Kinnate.Gedcom.Entity.NAME.SOUR",
//                                        "Kinnate.Gedcom.Entity.ADDR",
//                                        "Kinnate.Gedcom.Entity.CHAN.DATE",
//                                        "Kinnate.Gedcom.Entity.DEAT",
//                                        "Kinnate.Gedcom.Entity.OBJE",
//                                        "Kinnate.Gedcom.HEAD.SOUR.CORP",
//                                        "Kinnate.Gedcom.HEAD.SOUR.CORP.ADDR",
//                                        "Kinnate.Gedcom.Entity.ANUL"});
//                            Element addedExtraElement = null;
//                            if (swapList.contains(gedcomPath)) {
//                                gedcomPath += "." + lineParts[1];
//                                currentEntity.appendNode(lineParts[1]);
////                                addedExtraElement = metadataDom.createElement(lineParts[1]);
////                                currentDomNode.appendChild(addedExtraElement);
////                                currentDomNode = addedExtraElement;
//                            }


                            // todo: filter the links found and handled below from being added here as metadata
                            currentEntity.appendValue(lineParts[1], lineParts[2], gedcomLevel);

                            if (gedcomLevelStrings.size() == 3) {
                                if (gedcomLevelStrings.get(2).equals("DATE")) {
                                    if (gedcomLevelStrings.get(1).equals("BIRT") || gedcomLevelStrings.get(1).equals("DEAT")) {
                                        String dateText = lineParts[2].trim();
                                        for (String prefixString : new String[]{"ABT", "BEF", "AFT"}) {
                                            if (dateText.startsWith(prefixString)) {
                                                appendToTaskOutput("Unsupported Date Type: " + dateText);
                                                dateText = dateText.substring(prefixString.length()).trim();
                                            }
                                        }
                                        SimpleDateFormat formatter;
                                        if (dateText.matches("[0-9]{1,4}")) {
                                            while (dateText.length() < 4) {
                                                // make sure that 812 has four digits like 0812
                                                dateText = "0" + dateText;
                                            }
                                            formatter = new SimpleDateFormat("yyyy");
                                        } else if (dateText.matches("[a-zA-Z]{3} [0-9]{4}")) {
                                            formatter = new SimpleDateFormat("MMM yyyy");
                                        } else {
                                            formatter = new SimpleDateFormat("dd MMM yyyy");
                                        }
                                        try {
                                            if (gedcomLevelStrings.get(1).equals("BIRT")) {
                                                currentEntity.entityData.setDateOfBirth(formatter.parse(dateText));
                                            } else {
                                                currentEntity.entityData.setDateOfDeath(formatter.parse(dateText));
                                            }
                                        } catch (ParseException exception) {
                                            System.out.println(exception.getMessage());
                                            appendToTaskOutput("Failed to parse date of birth: " + strLine);
                                        }
                                    }
                                }
                            }
                            if (gedcomLevelStrings.size() == 2) {
                                if (gedcomLevelStrings.get(1).equals("SEX")) {
                                    String genderString = lineParts[2];
                                    if ("F".equals(genderString)) {
                                        genderString = "female";
                                    } else if ("M".equals(genderString)) {
                                        genderString = "male";
                                    } else {
                                        appendToTaskOutput("Unknown gender type: " + genderString);
                                    }
                                    currentEntity.insertValue("Gender", genderString);
                                }
                            }
                            if (gedcomLevelStrings.get(gedcomLevelStrings.size() - 1).equals("FILE")) {
                                // todo: check if the FILE value can contain a path or just the file name and handle the path correctly if required
                                // todo: copy the file or not according to user options
                                if (lineParts[2].toLowerCase().startsWith("mailto:")) {                                    
                                    currentEntity.insertValue("mailto", lineParts[2]); // todo: check that this is not already inserted
                                } else {
                                    currentEntity.entityData.addArchiveLink(inputFileUri.resolve(lineParts[2]));
                                }
                            }

//                            currentDomNode.setTextContent(/*gedcomPath + " : " +*/lineParts[2]);
//                            if (addedExtraElement != null) {
//                                addedExtraElement = null;
//                                currentDomNode = currentDomNode.getParentNode();
//                            }
//                            currentDomNode = currentDomNode.getParentNode();

//                        System.out.println("is template: " + gedcomImdiObject.nodeTemplate.pathIsChildNode(gedcomPath));

//                            if (!xsdTagsDone.contains(gedcomPath)) {
//                                while (gedcomLevelStrings.size() > xsdLevelStrings.size() + 1) {
//                                    String xsdLevelString = gedcomLevelStrings.get(xsdLevelStrings.size());
//                                    if (xsdLevelString.startsWith("@")) {
//                                        // this occurs at level 0 where the element type is named eg "0 @I9@ INDI"
//                                        xsdLevelString = "NamedElement";
//                                    }
//                                    xsdLevelStrings.add(xsdLevelString);
//                                    xsdString += "   <xs:element name=\"" + xsdLevelString + "\">\n";
//                                    xsdString += "<xs:complexType>\n<xs:sequence>\n";
//                                }
////                            while (gedcomLevelStrings.size() < xsdLevelStrings.size()) {
////                                xsdLevelStrings.remove(xsdLevelStrings.size() - 1);
////                                xsdString += "</xs:sequence>\n</xs:complexType>\n";
////                            }
//                                String xsdElementString = lineParts[1];
//                                if (xsdElementString.startsWith("@")) {
//                                    // this occurs at level 0 where the element type is named eg "0 @I9@ INDI"
//                                    xsdElementString = "NamedElement";
//                                }
//                                xsdString += "   <xs:element name=\"" + xsdElementString + "\" />\n";// + gedcomPath + "\n" + strLine + "\n";
//                                xsdTagsDone.add(gedcomPath);
//                            }
                            // create the link node when required
                            if (lineParts[2].startsWith("@") && lineParts[2].endsWith("@")) {
//                                appendToTaskOutput("--> adding social relation");
                                RelationType targetRelation = RelationType.none;
                                // here the following five relation types are mapped to the correct relation types after this the association is cretaed and later the indigiduals are linked with sanguine relations
                                if (lineParts[1].equals("FAMS") || lineParts[1].equals("FAMC") || lineParts[1].equals("HUSB") || lineParts[1].equals("WIFE") || lineParts[1].equals("CHIL")) {
                                    UniqueIdentifier socialGroupIdentifier;
                                    EntityData socialGroupMember;
                                    if (lineParts[1].equals("FAMS") || lineParts[1].equals("FAMC")) {
                                        socialGroupIdentifier = getEntityDocument(createdNodes, null, lineParts[2], importTranslator).entityData.getUniqueIdentifier();
                                        socialGroupMember = currentEntity.entityData;
                                    } else {
                                        socialGroupIdentifier = currentEntity.entityData.getUniqueIdentifier();
                                        socialGroupMember = getEntityDocument(createdNodes, null, lineParts[2], importTranslator).entityData;
                                    }
                                    if (!socialGroupRoleMap.containsKey(socialGroupIdentifier)) {
                                        socialGroupRoleMap.put(socialGroupIdentifier, new ArrayList<SocialMemberElement>());
                                    }
                                    socialGroupRoleMap.get(socialGroupIdentifier).add(new SocialMemberElement(lineParts[1], socialGroupMember));
                                    targetRelation = RelationType.affiliation;
                                } else if (lineParts[1].equals("SUBM")) {
                                    targetRelation = RelationType.collector;
                                } else if (lineParts[1].equals("SUBN")) {
                                    targetRelation = RelationType.metadata;
                                } else if (lineParts[1].equals("NOTE")) {
                                    targetRelation = RelationType.metadata;
                                } else if (lineParts[1].equals("ALIA")) {
                                    targetRelation = RelationType.metadata;
                                } else if (lineParts[1].equals("ASSO")) {
                                    targetRelation = RelationType.affiliation;
                                } else if (lineParts[1].equals("ANCI")) {
                                    targetRelation = RelationType.collector;
                                } else if (lineParts[1].equals("DESI")) {
                                    targetRelation = RelationType.collector;
                                } else if (lineParts[1].equals("REPO")) {
                                    targetRelation = RelationType.metadata;
                                } else if (lineParts[1].equals("OBJE")) {
                                    targetRelation = RelationType.resource;
                                } else if (lineParts[1].equals("SOUR")) {
                                    targetRelation = RelationType.metadata;
                                } else if (lineParts[1].equals("_HME")) {
                                    targetRelation = RelationType.none;
                                    // todo: the gedcom test file uses the custom _HME tag: "In uses one custom tag ("_HME") to see what the software will say about custom tags."
                                    // for the case of custom tags we could ask the user what relation type is relevant
                                } else {
                                    appendToTaskOutput("Unknown relation type: " + lineParts[2]);
                                    targetRelation = RelationType.metadata;
                                }
                                // the fam relations to consist of associations with implied sanuine links to the related entities, these sangine relations are handled later when all members are known
                                currentEntity.entityData.addRelatedNode(getEntityDocument(createdNodes, null, lineParts[2], importTranslator).entityData, targetRelation, RelationLineType.none, null, null);
                            }
                        }
                    }
                }
                super.incrementLineProgress();
            }
            for (ArrayList<SocialMemberElement> currentSocialGroup : socialGroupRoleMap.values()) {
                for (SocialMemberElement outerMemberElement : currentSocialGroup) {
                    for (SocialMemberElement innerMemberElement : currentSocialGroup) {
                        if (!innerMemberElement.memberEntity.equals(outerMemberElement.memberEntity)) {
                            if (innerMemberElement.typeString.equals("FAMC") || innerMemberElement.typeString.equals("CHIL")) {
                                if (outerMemberElement.typeString.equals("FAMC") || outerMemberElement.typeString.equals("CHIL")) {
                                    innerMemberElement.memberEntity.addRelatedNode(outerMemberElement.memberEntity, RelationType.sibling, RelationLineType.sanguineLine, null, null);
                                } else {
                                    innerMemberElement.memberEntity.addRelatedNode(outerMemberElement.memberEntity, RelationType.ancestor, RelationLineType.sanguineLine, null, null);
                                }
                            } else {
                                if (outerMemberElement.typeString.equals("FAMC") || outerMemberElement.typeString.equals("CHIL")) {
                                    innerMemberElement.memberEntity.addRelatedNode(outerMemberElement.memberEntity, RelationType.descendant, RelationLineType.sanguineLine, null, null);
                                } else {
                                    innerMemberElement.memberEntity.addRelatedNode(outerMemberElement.memberEntity, RelationType.union, RelationLineType.sanguineLine, null, null);
                                }
                            }
//                            appendToTaskOutput("--> adding sanguine relation");
                        }
                    }
                }
            }

//            if (metadataDom != null) {
//                ArbilComponentBuilder.savePrettyFormatting(metadataDom, entityFile);
//                metadataDom = null;
//            }
//            ImdiLoader.getSingleInstance().saveNodesNeedingSave(true);
//            appendToTaskOutput(importTextArea, "--> link count: " + linkFields.size());
            // update all the links now we have the urls for each internal name

//            appendToTaskOutput(importTextArea, "xsdString:\n" + xsdString);

//            int linkNodesUpdated = 0;
//            for (URI currentUri : createdNodes) {
//                appendToTaskOutput(importTextArea, "linkParent: " + currentUri.toASCIIString());
//                try {
//                    String linkXpath = "/Kinnate/Relation/Link";
//                    Document linksDom = new CmdiComponentBuilder().getDocument(currentUri);
//                    NodeList relationLinkNodeList = org.apache.xpath.XPathAPI.selectNodeList(linksDom, linkXpath);
//                    for (int nodeCounter = 0; nodeCounter < relationLinkNodeList.getLength(); nodeCounter++) {
//                        Node relationLinkNode = relationLinkNodeList.item(nodeCounter);
//                        if (relationLinkNode != null) {
//                            // todo: update the links
//                            // todo: create links in ego and alter but but the type info such as famc only in the relevant entity
//                            String linkValue = createdNodesTable.get(relationLinkNode.getTextContent());
//                            if (linkValue != null) {
//                                relationLinkNode.setTextContent(linkValue);
//                                appendToTaskOutput(importTextArea, "linkValue: " + linkValue);
//                            }
//                        }
//                    }
//                    new CmdiComponentBuilder().savePrettyFormatting(linksDom, currentImdiObject.getFile());
//                } catch (TransformerException exception) {
//                    new ArbilBugCatcher().logError(exception);
//                }
//                linkNodesUpdated++;
//                if (progressBar != null) {
//                    progressBar.setValue((int) ((double) linkNodesUpdated / (double) createdNodes.size() * 100 / 2 + 50));
//                }
//            }
            saveAllDocuments();
//            appendToTaskOutput("Import finished with a node count of: " + createdNodes.size());

//            gedcomImdiObject.saveChangesToCache(true);
//            gedcomImdiObject.loadImdiDom();
//            gedcomImdiObject.clearChildIcons();
//            gedcomImdiObject.clearIcon();
            ArbilDataNodeLoader.getSingleInstance().saveNodesNeedingSave(true);
        } catch (IOException exception) {
            new ArbilBugCatcher().logError(exception);
            appendToTaskOutput("Error: " + exception.getMessage());
//        } catch (ParserConfigurationExceptionparserConfigurationException) {
//            new ArbilBugCatcher().logError(parserConfigurationException);
//            appendToTaskOutput("Error: " + parserConfigurationException.getMessage());
//        } catch (DOMExceptiondOMException) {
//            new ArbilBugCatcher().logError(dOMException);
//            appendToTaskOutput("Error: " + dOMException.getMessage());
//        } catch (SAXExceptionsAXException) {
//            new ArbilBugCatcher().logError(sAXException);
//            appendToTaskOutput("Error: " + sAXException.getMessage());
//        }
        } catch (ImportException exception) {
            new ArbilBugCatcher().logError(exception);
            appendToTaskOutput("Error: " + exception.getMessage());
        }
//        LinorgSessionStorage.getSingleInstance().loadStringArray("KinGraphTree");
//        String[] createdNodePaths = new String[createdNodes.size()];
//        int createdNodeCounter = 0;
//        for (URI currentUri : createdNodes) {
//            createdNodePaths[createdNodeCounter] = currentUri.toASCIIString();
////            createdNodeCounter++;
//        }
//        LinorgSessionStorage.getSingleInstance().saveStringArray("KinGraphTree", createdNodePaths);
        return createdNodes.toArray(new URI[]{});
    }
}
