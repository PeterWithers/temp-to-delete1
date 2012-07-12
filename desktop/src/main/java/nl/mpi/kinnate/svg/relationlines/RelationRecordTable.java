package nl.mpi.kinnate.svg.relationlines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.kindata.EntityRelation;
import nl.mpi.kinnate.kindata.RelationTypeDefinition;
import nl.mpi.kinnate.svg.GraphPanel;
import nl.mpi.kinnate.svg.OldFormatException;
import nl.mpi.kinnate.uniqueidentifiers.UniqueIdentifier;

/**
 * Document : RelationRecordTable
 * Created on : Jun 29, 2012, 7:11:48 PM
 * Author : Peter Withers
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
            directedRelation = DataTypes.RelationType.ancestor;
            leftEntity = entityRelation.getAlterNode();
            rightEntity = entityData;
        } else if (entityRelation.getRelationType() == DataTypes.RelationType.ancestor) {
            // make sure the ancestral relations are unidirectional
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
        String compoundIdentifier = leftEntity.getUniqueIdentifier().getQueryIdentifier() + rightEntity.getUniqueIdentifier().getQueryIdentifier() + directedRelation.name() + ":" + entityRelation.dcrType + ":" + entityRelation.customType;
        // make sure each equivalent relation is drawn only once
        if (!doneRelations.contains(compoundIdentifier)) {
            boolean skipCurrentRelation = false;
            if (DataTypes.isSanguinLine(entityRelation.getRelationType())) {
                if (hasCommonParent(leftEntity, entityRelation)) {
                    // do not draw lines for siblings if the common parent is visible because the ancestor lines will take the place of the sibling lines
                    skipCurrentRelation = true;
                }
            }
            if (!skipCurrentRelation) {
                doneRelations.add(compoundIdentifier);
                String lineColour = entityRelation.lineColour;
                RelationTypeDefinition.CurveLineOrientation curveLineOrientation = RelationTypeDefinition.CurveLineOrientation.horizontal;
                int lineDash = 0;
                if (lineColour == null) {
                    for (RelationTypeDefinition relationTypeDefinition : graphPanel.dataStoreSvg.getRelationTypeDefinitions()) {
                        if (relationTypeDefinition.matchesType(entityRelation)) {
                            lineColour = relationTypeDefinition.getLineColour();
                            lineWidth = relationTypeDefinition.getLineWidth();
                            curveLineOrientation = relationTypeDefinition.getCurveLineOrientation();
                            lineDash = relationTypeDefinition.getLineDash();
                            break;
                        }
                    }
                }
                RelationRecord relationRecord = new RelationRecord(graphPanel, this.size(), leftEntity, rightEntity, directedRelation, lineWidth, lineDash, curveLineOrientation, lineColour, entityRelation.labelString, hSpacing, vSpacing);
                recordStore.put(relationRecord.lineIdString, relationRecord);
            }
        }
    }

//    public String getParentPointIdentifier(EntityData currentNode, EntityRelation graphLinkNode) {
    public boolean hasCommonParent(EntityData currentNode, EntityRelation graphLinkNode) {
        if (graphLinkNode.getRelationType() == DataTypes.RelationType.sibling) {
            for (EntityRelation altersRelation : graphLinkNode.getAlterNode().getAllRelations()) {
                if (altersRelation.getRelationType() == DataTypes.RelationType.ancestor) {
                    for (EntityRelation egosRelation : currentNode.getAllRelations()) {
                        if (egosRelation.getRelationType() == DataTypes.RelationType.ancestor) {
                            if (altersRelation.alterUniqueIdentifier.equals(egosRelation.alterUniqueIdentifier)) {
                                if (altersRelation.getAlterNode() != null && altersRelation.getAlterNode().isVisible) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public RelationRecord getRecord(String idString) {
        return recordStore.get(idString);
    }

    public Collection<RelationRecord> getAllRecords() {
        return recordStore.values();
    }

    public ArrayList<RelationRecord> getRecordsForSelection(ArrayList<UniqueIdentifier> selectedIdentifiers) {
        ArrayList<RelationRecord> returnRecords = new ArrayList<RelationRecord>();
        for (RelationRecord relationRecord : recordStore.values()) {
            if (relationRecord.pertainsToEntity(selectedIdentifiers)) {
                returnRecords.add(relationRecord);
            }
        }
        return returnRecords;
    }

    public int size() {
        return recordStore.size();
    }

    public void adjustLines() throws OldFormatException {
        lineLookUpTable = new LineLookUpTable();
        for (RelationRecord relationRecord : recordStore.values()) {
            relationRecord.updatePathPoints(lineLookUpTable);
        }
        lineLookUpTable.separateOverlappingLines();
        lineLookUpTable.addLoops();
    }
}
