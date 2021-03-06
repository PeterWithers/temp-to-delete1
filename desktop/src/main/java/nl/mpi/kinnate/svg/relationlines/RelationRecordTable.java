/**
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.kinnate.svg.relationlines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.kindata.EntityRelation;
import nl.mpi.kinnate.kindata.RelationTypeDefinition;
import nl.mpi.kinnate.svg.GraphPanel;
import nl.mpi.kinnate.svg.OldFormatException;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;

/**
 * Created on : Jun 29, 2012, 7:11:48 PM
 *
 * @author Peter Withers
 */
public class RelationRecordTable {

    HashMap<String, RelationRecord> recordStore = new HashMap<String, RelationRecord>();
    LineLookUpTable lineLookUpTable;
    ArrayList<String> doneRelations = new ArrayList<String>();

    public void addRecord(GraphPanel graphPanel, EntityData entityData, EntityRelation entityRelation, int hSpacing, int vSpacing, int lineWidth) throws OldFormatException {
        // make directed and exclude any lines that are already done
        DataTypes.RelationType directedRelation = entityRelation.getRelationType();
        EntityData leftEntity;
        EntityData rightEntity;
        if (entityRelation.getRelationType() == DataTypes.RelationType.descendant) {
            // make sure the ancestral relations are unidirectional
            directedRelation = DataTypes.getOpposingRelationType(entityRelation.getRelationType());
            leftEntity = entityRelation.getAlterNode();
            rightEntity = entityData;
        } else if (entityRelation.getRelationType() == DataTypes.RelationType.ancestor) {
            // make sure the ancestral relations are unidirectional
            leftEntity = entityData;
            rightEntity = entityRelation.getAlterNode();
        } else if (entityRelation.getRelationType() == DataTypes.RelationType.directedin) {
            // make sure the directed relations are unidirectional
            directedRelation = DataTypes.getOpposingRelationType(entityRelation.getRelationType());
            leftEntity = entityRelation.getAlterNode();
            rightEntity = entityData;
        } else if (entityRelation.getRelationType() == DataTypes.RelationType.directedout) {
            // make sure the directed relations are unidirectional
            leftEntity = entityData;
            rightEntity = entityRelation.getAlterNode();
        } else if (entityData.getUniqueIdentifier().getQueryIdentifier().compareTo(entityRelation.getAlterNode().getUniqueIdentifier().getQueryIdentifier()) > 0) {
            // make sure all other relations are directed by the string sort order so that they can be made unique
            leftEntity = entityRelation.getAlterNode();
            rightEntity = entityData;
        } else {
            // make sure all other relations are directed by the string sort order so that they can be made unique
            leftEntity = entityData;
            rightEntity = entityRelation.getAlterNode();
        }
        String compoundIdentifier;
        if (directedRelation == DataTypes.RelationType.ancestor) {
            compoundIdentifier = "commonparent:" + leftEntity.getUniqueIdentifier().getQueryIdentifier() + directedRelation.name() + ":" + entityRelation.dcrType + ":" + entityRelation.customType;
        } else {
            compoundIdentifier = leftEntity.getUniqueIdentifier().getQueryIdentifier() + rightEntity.getUniqueIdentifier().getQueryIdentifier() + directedRelation.name() + ":" + entityRelation.dcrType + ":" + entityRelation.customType;
        }
        // make sure each equivalent relation is drawn only once
        if (!doneRelations.contains(compoundIdentifier)) {
            boolean skipSiblingRelation = false;
            String groupId = getGroupId(entityData, entityRelation);
            if (entityRelation.getRelationType() == DataTypes.RelationType.sibling) {
                String siblingGroupId = getGroupId(entityData, entityRelation);
                // do not draw lines for siblings if the common parent is visible because the ancestor lines will take the place of the sibling lines
                skipSiblingRelation = ((groupId == null) ? false : groupId.equals(siblingGroupId));
//                 skipSiblingRelation = groupId != null && groupId.equals(siblingGroupId); // this is correct because if the group is null then there are no parents 
            }
            if (!skipSiblingRelation) {
                doneRelations.add(compoundIdentifier);
                String lineColour = entityRelation.lineColour;
                String labelString = null;
                RelationTypeDefinition.CurveLineOrientation curveLineOrientation = RelationTypeDefinition.CurveLineOrientation.horizontal;
                int lineDash = 0;
                if (lineColour == null) {
                    for (RelationTypeDefinition relationTypeDefinition : graphPanel.dataStoreSvg.getRelationTypeDefinitions()) {
                        if (relationTypeDefinition.matchesType(entityRelation)) {
                            lineColour = relationTypeDefinition.getLineColour();
                            lineWidth = relationTypeDefinition.getLineWidth();
                            curveLineOrientation = relationTypeDefinition.getCurveLineOrientation();
                            lineDash = relationTypeDefinition.getLineDash();
                            labelString = relationTypeDefinition.getDisplayName();
                            break;
                        }
                    }
                }
                if (entityRelation.labelString != null) {
                    if (labelString == null) {
                        labelString = entityRelation.labelString;
                    } else {
                        labelString = labelString + " : " + entityRelation.labelString;
                    }
                }
                RelationRecord relationRecord = new RelationRecord(groupId, graphPanel, this.size(), leftEntity, rightEntity, directedRelation, entityRelation.dcrType, entityRelation.customType, lineWidth, lineDash, curveLineOrientation, lineColour, labelString, hSpacing, vSpacing);
                recordStore.put(relationRecord.lineIdString, relationRecord);
            }
        }
    }

    public String getGroupId(EntityData currentNode, EntityRelation graphLinkNode) {
//        System.out.println("ego: " + graphLinkNode.getRelationType() + " : " + currentNode.getLabel()[0].toString());
        if (!DataTypes.isSanguinLine(graphLinkNode.getRelationType())) {
            // group ids do not apply to non sangune relations
            return null;
        }
        ArrayList<String> parentIdList = new ArrayList<String>(); // we use a string here so that it can be sorted consistently, the array list is used because any number of parents could exist
        if (graphLinkNode.getRelationType() == DataTypes.RelationType.union) {
            // get the common parent id based on the union
            // todo: could this cause issues when there are three or more parents to one child?
            parentIdList.add(currentNode.getUniqueIdentifier().getAttributeIdentifier());
//            System.out.println("P1: " + currentNode.getLabel()[0]);
            if (!parentIdList.contains(graphLinkNode.alterUniqueIdentifier.getAttributeIdentifier())) {
                parentIdList.add(graphLinkNode.alterUniqueIdentifier.getAttributeIdentifier());
//                System.out.println("P2: " + graphLinkNode.getAlterNode().getLabel()[0]);
            }
        } else {
            // generate the id based on the ancestors of the entity
            EntityData childNode;
            if (graphLinkNode.getRelationType() != DataTypes.RelationType.descendant) {
                childNode = currentNode;
            } else {
                childNode = graphLinkNode.getAlterNode();
            }
            for (EntityRelation egosRelation : childNode.getAllRelations()) {
                if (egosRelation.getRelationType() == DataTypes.RelationType.ancestor) {
                    if (egosRelation.getAlterNode() != null && egosRelation.getAlterNode().isVisible) {
                        if (!parentIdList.contains(egosRelation.alterUniqueIdentifier.getAttributeIdentifier())) {
                            parentIdList.add(egosRelation.alterUniqueIdentifier.getAttributeIdentifier());
//                            System.out.println("P3: " + egosRelation.getAlterNode().getLabel()[0]);
                        }
                    }
                }
            }
        }
        if (parentIdList.isEmpty()) {
            return null;
        } else {
            Collections.sort(parentIdList);
//            System.out.println("getGroupId: " + parentIdList.toString());
            return parentIdList.toString() + graphLinkNode.customType;
        }
    }

    public RelationRecord getRecord(String idString) {
        return recordStore.get(idString);
    }

    public Collection<RelationRecord> getAllRecords() {
        return recordStore.values();
    }

    public ArrayList<RelationRecord> getRecordsForSelection(ArrayList<UniqueIdentifier> selectedIdentifiers) {
        ArrayList<RelationRecord> returnRecords = new ArrayList<RelationRecord>();
        HashSet<String> groupSet = new HashSet<String>();
        for (RelationRecord relationRecord : recordStore.values()) {
            if (relationRecord.pertainsToEntity(selectedIdentifiers)) {
                returnRecords.add(relationRecord);
                groupSet.add(relationRecord.getGroupName());
            }
        }
        // expand the selection to include the entire groups of any already partially included
        for (RelationRecord relationRecord : recordStore.values()) {
            if (relationRecord.belongsToGroup(groupSet)) {
                returnRecords.add(relationRecord);
            }
        }
        return returnRecords;
    }

    public int size() {
        return recordStore.size();
    }

    public void adjustLines(GraphPanel graphPanel) throws OldFormatException {
        lineLookUpTable = new LineLookUpTable();
        for (RelationRecord relationRecord : recordStore.values()) {
            relationRecord.updatePathPoints(lineLookUpTable);
        }
        lineLookUpTable.separateLinesOverlappingEntities(graphPanel.entitySvg.getAllEntityLocations());
        lineLookUpTable.separateOverlappingLines();
        lineLookUpTable.addLoops();
    }
}
