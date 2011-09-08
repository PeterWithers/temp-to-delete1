package nl.mpi.kinnate.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.ImageIcon;
import nl.mpi.arbil.data.ArbilDataNode;
import nl.mpi.arbil.data.ArbilDataNodeLoader;
import nl.mpi.arbil.data.ArbilNode;
import nl.mpi.kinnate.kindata.DataTypes;
import nl.mpi.kinnate.kindata.EntityData;
import nl.mpi.kinnate.kindata.EntityRelation;
import nl.mpi.kinnate.svg.SymbolGraphic;

/**
 *  Document   : KinTreeNode
 *  Created on : Aug 22, 2011, 5:14:05 PM
 *  Author     : Peter Withers
 */
public class KinTreeNode extends ArbilNode implements Comparable {

    public EntityData entityData;
    DataTypes.RelationType subnodeFilter;
    ArbilNode[] childNodes = null;
    static SymbolGraphic symbolGraphic = new SymbolGraphic();

    public KinTreeNode(EntityData entityData) {
        super();
        this.entityData = entityData;
        this.subnodeFilter = null;
    }

    public KinTreeNode(EntityData entityData, DataTypes.RelationType subnodeFilter) {
        super();
        this.entityData = entityData;
        this.subnodeFilter = subnodeFilter; // subnode filter should be null unless the child nodes are to be filtered
    }

    @Override
    public String toString() {
        StringBuilder labelBuilder = new StringBuilder();
        if (entityData == null) {
            labelBuilder.append("(entity not loaded)");
        } else {
            final String[] labelArray = entityData.getLabel();
            if (labelArray != null && labelArray.length > 0) {
                for (String labelString : labelArray) {
                    labelBuilder.append(labelString);
                    labelBuilder.append(" ");
                }
            } else {
                labelBuilder.append("(unnamed entity)");
            }
        }
        return labelBuilder.toString();
    }

    @Override
    public ArbilDataNode[] getAllChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getAllChildren(Vector<ArbilDataNode> allChildren) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArbilNode[] getChildArray() {
        if (childNodes != null) {
            return childNodes;
        } else if (entityData != null) {
            ArrayList<ArbilNode> relationList = new ArrayList<ArbilNode>();
            // todo: add metanodes and ui option to hide show relation types
            for (EntityRelation entityRelation : entityData.getAllRelations()) {
                final boolean showFiltered = subnodeFilter == DataTypes.RelationType.ancestor || subnodeFilter == DataTypes.RelationType.descendant;
                if (subnodeFilter == null || (subnodeFilter == entityRelation.relationType && showFiltered)) {
                    relationList.add(new KinTreeNode(entityRelation.getAlterNode(), entityRelation.relationType));
                }
            }
            if (entityData.archiveLinkArray != null) {
                for (URI archiveLink : entityData.archiveLinkArray) {
                    ArbilDataNode linkedArbilDataNode = ArbilDataNodeLoader.getSingleInstance().getArbilDataNode(null, archiveLink);
                    relationList.add(linkedArbilDataNode);
                }
            }
            childNodes = relationList.toArray(new ArbilNode[]{});
            return childNodes;
        } else {
            return new ArbilNode[]{};
        }

        // todo: inthe case of metadata nodes load them via the arbil data loader
//                try {
//                    String entityPath = entityData.getEntityPath();
//                    if (entityPath != null) {
////                        ArbilDataNode arbilDataNode = ArbilDataNodeLoader.getSingleInstance().getArbilDataNode(null, new URI(entityPath));
//                        if (entityData.isEgo || egoIdentifiers.contains(entityData.getUniqueIdentifier())) {
//                            egoNodeArray.add(arbilDataNode);
//                        } else if (requiredEntityIdentifiers.contains(entityData.getUniqueIdentifier())) {
//                            requiredNodeArray.add(arbilDataNode);
//                        } else {
//                            remainingNodeArray.add(arbilDataNode);
//                        }
//                    }
//                } catch (URISyntaxException exception) {
//                    System.err.println(exception.getMessage());
//                }

    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImageIcon getIcon() {
        if (entityData != null) {
            return symbolGraphic.getSymbolGraphic(entityData.getSymbolType(), entityData.isEgo);
        }
        return null;
    }

    @Override
    public boolean hasCatalogue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasHistory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLocalResource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasResource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isArchivableFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCatalogue() {
        return false;
    }

    @Override
    public boolean isChildNode() {
        return false;
    }

    @Override
    public boolean isCmdiMetaDataNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCorpus() {
        return false;
    }

    @Override
    public boolean isDataLoaded() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isEditable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmptyMetaNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFavorite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLoading() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLocal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMetaDataNode() {
        return false;
    }

    @Override
    public boolean isResourceSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSession() {
        return false;
    }

    public int compareTo(Object o) {
        if (o instanceof KinTreeNode) {
            int compResult = this.toString().compareTo(o.toString());
            if (compResult == 0) {
                // todo: compare by age if the labels match
//                compResult = entityData
            }
            return compResult;
        } else {
            // put kin nodes in front of other nodes
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KinTreeNode other = (KinTreeNode) obj;
        if (entityData == null || other.entityData == null) {
            // todo: it would be good for this to never be null, or at least to aways have the UniqueIdentifier to compare
            return false;
        }
        if (this.entityData.getUniqueIdentifier() != other.entityData.getUniqueIdentifier() && (this.entityData.getUniqueIdentifier() == null || !this.entityData.getUniqueIdentifier().equals(other.entityData.getUniqueIdentifier()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.entityData.getUniqueIdentifier() != null ? this.entityData.getUniqueIdentifier().hashCode() : 0);
        return hash;
    }
}
